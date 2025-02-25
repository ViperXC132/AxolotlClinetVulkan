/*
 * Copyright © 2024 moehreag <moehreag@gmail.com> & Contributors
 *
 * This file is part of AxolotlClient.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, see the LICENSE file.
 */

package io.github.axolotlclient.modules.hypixel;

import io.github.axolotlclient.api.API;
import io.github.axolotlclient.api.Request;
import io.github.axolotlclient.api.Response;
import io.github.axolotlclient.util.CachedAPI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

public class HypixelAbstractionLayer {
	@AllArgsConstructor
	@Getter
	private enum RequestDataType {
		NETWORK_LEVEL("network_level"),
		BEDWARS_LEVEL("bedwars_level"),
		SKYWARS_EXPERIENCE("skywars_experience"),
		BEDWARS_DATA("bedwars_data");
		private final String id;
	}

	@Getter
	private static final HypixelAbstractionLayer instance = new HypixelAbstractionLayer();

	private record Entry<T>(String desc, CompletableFuture<T> res, Function<Response, T> func, Request req) {
		public void resolve(Response response) {
			res.complete(func.apply(response));
		}
	}

	private final LinkedBlockingQueue<Entry<?>> tasks = new LinkedBlockingQueue<>();
	private final AtomicBoolean isRunning = new AtomicBoolean(true);

	// TODO: Someone who's better at async algorithms should port this to a scheduled executor service. This implementation is certainly janky and VERY VERY BAD!
	// TODO: after a request fails {n} times, we should give up...
	private final Thread worker = new Thread("HypixelAbstractionLayerWorker") {
		@Override
		public void run() {
			AtomicLong timeout = new AtomicLong(System.currentTimeMillis());

			while (isRunning.get()) {
				Entry<?> task;

				try {
					task = tasks.take();
				} catch (InterruptedException e) {
					continue;
				}

				try {
					if (timeout.get() > System.currentTimeMillis()) {
						Thread.sleep(timeout.get() - System.currentTimeMillis());
					}

					API.getInstance().getLogger().debug("Performing request for {}", task.desc);
					API.getInstance().get(task.req).whenComplete((res, err) -> {
						long delay;
						// handle rate limit
						if (res.getStatus() == 429) {
							delay = Duration.of(
								res.firstHeader("RateLimit-Reset")
									.map(Long::parseLong)
									.orElse(2L),
								ChronoUnit.SECONDS
							).toMillis();
						} else {
							delay = 100;
						}

						long newTimeout = System.currentTimeMillis() + delay;
						timeout.set(newTimeout);
						API.getInstance().getLogger().debug("Rate limit: backing off until {} (+{}ms)", newTimeout, delay);

						if (err != null || res.getStatus() != 200) {
							if (err != null) {
								API.getInstance().getLogger().warn("While performing request: ", err);
							} else {
								API.getInstance().getLogger().warn("Bad response ({}): {}", res.getStatus(), res.getBody());
							}
							tasks.add(task);
						} else {
							try {
								API.getInstance().getLogger().debug("Resolved request for {}", task.desc);
								task.resolve(res);
							} catch (Throwable ex) {
								API.getInstance().getLogger().warn("Failed to parse response: ", ex);
								tasks.add(task);
							}
						}

						this.interrupt();
					});

					timeout.getAndSet(System.currentTimeMillis() + Duration.of(2L, ChronoUnit.SECONDS).toMillis());
				} catch (InterruptedException ignored) {
					// we need to try again
					tasks.add(task);
				}
			}
		}
	};

	private HypixelAbstractionLayer() {
		worker.start();
	}

	private <V> CachedAPI<String, V> create(RequestDataType type, Function<Response, V> app) {
		return new CachedAPI<>(uuid -> {
			Request request = Request.Route.HYPIXEL
				.builder()
				.field("request_type", type.getId())
				.field("target_player", uuid)
				.build();

			CompletableFuture<V> future = new CompletableFuture<>();
			tasks.add(new Entry<>("[%s, %s]".formatted(type, uuid), future, app, request));
			return future;
		});
	}

	private CachedAPI<String, Integer> createLevel(RequestDataType type) {
		return create(type, res -> res.<Number>getBody(type.getId()).intValue());
	}

	private void freePlayerData(String uuid) {
		bedwarsDataApi.invalidate(uuid);
		networkLevelApi.invalidate(uuid);
		bedwarsLevelApi.invalidate(uuid);
		skywardsExpApi.invalidate(uuid);
	}

	@Getter
	private final CachedAPI<String, BedwarsData> bedwarsDataApi = create(RequestDataType.BEDWARS_DATA,
		res -> new BedwarsData(
			res.<Number>getBody("final_kills_bedwars").intValue(),
			res.<Number>getBody("final_deaths_bedwars").intValue(),
			res.<Number>getBody("beds_broken_bedwars").intValue(),
			res.<Number>getBody("deaths_bedwars").intValue(),
			res.<Number>getBody("kills_bedwars").intValue(),
			res.<Number>getBody("losses_bedwars").intValue(),
			res.<Number>getBody("wins_bedwars").intValue(),
			res.<Number>getBody("winstreak").intValue()
		)
	);

	@Getter
	private final CachedAPI<String, Integer> networkLevelApi = createLevel(RequestDataType.NETWORK_LEVEL);

	@Getter
	private final CachedAPI<String, Integer> bedwarsLevelApi = createLevel(RequestDataType.BEDWARS_LEVEL);

	@Getter
	private final CachedAPI<String, Integer> skywardsExpApi = createLevel(RequestDataType.SKYWARS_EXPERIENCE);

	@SneakyThrows // propagate interrupted exception
	public void shutdown() {
		isRunning.set(false);
		worker.interrupt();
		worker.join();
	}

	public void clearPlayerData() {
		bedwarsDataApi.invalidate();
		networkLevelApi.invalidate();
		bedwarsLevelApi.invalidate();
		skywardsExpApi.invalidate();
	}

	public void handleDisconnectEvents(UUID uuid) {
		freePlayerData(uuid.toString());
	}
}
