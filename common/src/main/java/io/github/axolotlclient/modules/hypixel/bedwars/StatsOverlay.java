/*
 * Copyright © 2025 moehreag <moehreag@gmail.com> & Contributors
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

package io.github.axolotlclient.modules.hypixel.bedwars;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.IntegerOption;
import io.github.axolotlclient.api.API;
import io.github.axolotlclient.bridge.AxoMinecraftClient;
import io.github.axolotlclient.bridge.AxoPlayerListEntry;
import io.github.axolotlclient.bridge.key.AxoKeybinding;
import io.github.axolotlclient.bridge.key.AxoKeys;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoI18n;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.modules.hud.gui.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui.entry.BoxHudEntry;
import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DefaultOptions;
import io.github.axolotlclient.modules.hypixel.HypixelAbstractionLayer;
import io.github.axolotlclient.modules.hypixel.PlayerData.Bedwars.CombinedGameData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

public class StatsOverlay extends BoxHudEntry implements DynamicallyPositionable {
	@FunctionalInterface
	private interface EntryRenderer {

		AxoText render(BedwarsTeam team, String name, CombinedGameData data, int winstreak);
	}

	private record Entry(boolean acceptNull, String name, EntryRenderer compRenderer) {

	}

	private static final List<Entry> RENDER_ENTRIES = List.of(
		new Entry(true, "bedwars.stats_overlay.header.player", (t, n, bw, ws) -> AxoText.literal(t.getColorSection() + n)),
		new Entry(false, "bedwars.stats_overlay.header.fkdr", (t, n, bw, ws) -> AxoText.literal("%.2f (%s/%s)".formatted(bw.fkdr(), bw.finalKills(), bw.finalDeaths())).br$color(AxoText.Color.GOLD)),
		new Entry(false, "bedwars.stats_overlay.header.kdr", (t, n, bw, ws) -> AxoText.literal("%.2f (%s/%s)".formatted(bw.kdr(), bw.kills(), bw.deaths())).br$color(AxoText.Color.GOLD)),
		new Entry(false, "bedwars.stats_overlay.header.wlr", (t, n, bw, ws) -> AxoText.literal("%.2f (%s/%s)".formatted(bw.wlr(), bw.wins(), bw.losses())).br$color(AxoText.Color.GOLD)),
		new Entry(false, "bedwars.stats_overlay.header.ws", (t, n, bw, ws) -> AxoText.literal(ws).br$color(AxoText.Color.GOLD))
	);

	private class RenderHelper {

		private final Map<String, IntObjectPair<CombinedGameData>> stats;
		private final Map<BedwarsTeam, List<String>> playersByTeam;
		private int xCursor = getPos().x + padding.get();
		private int yFinal = 0;

		private RenderHelper(Map<String, IntObjectPair<CombinedGameData>> stats, Map<BedwarsTeam, List<String>> playersByTeam) {
			this.stats = stats;
			this.playersByTeam = playersByTeam;
		}

		private void renderColumn(AxoRenderContext ctx, Entry renderEntry) {
			final var shadow = true;
			final var dy = AxoMinecraftClient.getInstance().br$getFont().br$getFontHeight() + rowMargin.get();

			int currY = getPos().y + padding.get();
			int newXCursor = ctx.br$drawString(AxoI18n.translate(renderEntry.name), xCursor, currY, 0xffffffff, shadow);

			currY += dy;

			for (final var entry : playersByTeam.entrySet()) {
				final var team = entry.getKey();
				final var members = entry.getValue();

				for (String playerName : members) {
					final var data = stats.get(playerName);
					final var text = data == null ?
						(renderEntry.acceptNull ? renderEntry.compRenderer.render(team, playerName, null, 0) : AxoText.literal("?").br$color(AxoText.Color.RED)) :
						renderEntry.compRenderer.render(team, playerName, data.right(), data.left());

					newXCursor = Math.max(newXCursor, ctx.br$drawString(text, xCursor, currY, 0xffffffff, shadow));
					currY += dy;
				}
			}

			yFinal = currY;
			xCursor = newXCursor + columnMargin.get();
		}

		private void render(AxoRenderContext ctx) {
			for (final var renderEntry : RENDER_ENTRIES) {
				renderColumn(ctx, renderEntry);
			}

			// don't multiply the padding by two, since it's already accounted for by the cursors
			int newWidth = xCursor - getPos().x + padding.get() - columnMargin.get();
			int newHeight = yFinal - getPos().y + padding.get() - rowMargin.get();

			boolean dirty = newWidth != getWidth() || newHeight != getHeight();

			setWidth(newWidth);
			setHeight(newHeight);

			if (dirty) {
				onBoundsUpdate();
			}
		}

	}

	private static final Map<BedwarsTeam, List<String>> SAMPLE_PLAYERS = new EnumMap<>(BedwarsTeam.class);

	static {
		SAMPLE_PLAYERS.put(BedwarsTeam.AQUA, List.of("FloweyTF", "Adaklys"));
		SAMPLE_PLAYERS.put(BedwarsTeam.GREEN, List.of("herobrine", "steve"));
	}

	private static final Map<String, IntObjectPair<CombinedGameData>> SAMPLE_STATS = Map.of(
		"FloweyTF", IntObjectPair.of(3, new CombinedGameData(4234, 5634, 500, 300, 1469, 336, 230, 123)),
		"Adaklys", IntObjectPair.of(3, new CombinedGameData(1984, 2048, 300, 500, 834, 737, 123, 273)),
		"steve", IntObjectPair.of(3, new CombinedGameData(10, 1, 10, 1, 10, 1, 10, 1))
	);

	public final static AxoIdentifier ID = AxoIdentifier.of("axolotlclient", "bedwars_stats_overlay");

	protected final EnumOption<AnchorPoint> anchor = DefaultOptions.getAnchorPoint();

	protected final IntegerOption padding = new IntegerOption("hud.padding", 3, 1, 10);
	protected final IntegerOption columnMargin = new IntegerOption("hud.column_margin", 3, 0, 10);
	protected final IntegerOption rowMargin = new IntegerOption("hud.row_margin", 1, 0, 10);
	private final BedwarsMod mod;

	private Map<String, IntObjectPair<CombinedGameData>> stats = new HashMap<>();
	private final Map<BedwarsTeam, List<String>> playersByTeam = new EnumMap<>(BedwarsTeam.class);
	private final AxoKeybinding toggle = AxoKeybinding.create(AxoKeys.KEY_UNKNOWN, "bedwars.toggle_stats_overlay", "category.axolotlclient");
	private boolean shouldRender = false;
	@Nullable
	private String errorMessage = null;

	public StatsOverlay(BedwarsMod mod) {
		super(400, 600, true);
		this.mod = mod;
	}

	void onStart() {
		playersByTeam.clear();
		// can't call clear here, since we need a fresh map to avoid requests from writing
		stats = new HashMap<>();
		shouldRender = true;

		if (!API.getInstance().getApiOptions().enabled.get()) {
			errorMessage = "API Not Enabled!";
			return;
		}

		if (!API.getInstance().isAuthenticated()) {
			errorMessage = "API Not Authenticated!";
			return;
		}

		final var api = HypixelAbstractionLayer.getInstance().getPlayerDataApi();

		// need to use capturedStats since this map could've been "retired"
		final var capturedStats = this.stats;
		mod.getGame().ifPresent(g ->
			g.getPlayersByTeam().forEach((t, e) -> {
				playersByTeam.put(t, e.stream().map(AxoPlayerListEntry::br$getName).toList());
				e.forEach(entry ->
					api.getAsync(entry.br$getId().toString())
						.whenCompleteAsync((playerData, throwable) -> {
							if (playerData == null || playerData.isEmpty()) {
								return;
							}

							capturedStats.put(entry.br$getName(), IntObjectPair.of(
								playerData.get().bedwars().all().winstreak(),
								playerData.get().bedwars().core())
							);
						}, client));
			}));
	}

	public void onEnd() {
		shouldRender = false;
	}

	@Override
	public void render(AxoRenderContext ctx, float delta) {
		if (errorMessage != null) {
			ctx.br$drawString(AxoText.Color.RED + errorMessage, getPos().x, getPos().y, 0xffffffff, true);
		}

		if (mod.inGame() && shouldRender) {
			super.render(ctx, delta);
		}
	}

	@Override
	public void renderComponent(AxoRenderContext ctx, float delta) {
		new RenderHelper(stats, playersByTeam).render(ctx);
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext ctx, float delta) {
		new RenderHelper(SAMPLE_STATS, SAMPLE_PLAYERS).render(ctx);
	}

	@Override
	public void tick() {
		if (mod.inGame()) {
			if (this.toggle.br$consumeClick()) {
				shouldRender = !shouldRender;
			}
		}
	}

	@Override
	public boolean tickable() {
		return true;
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public AnchorPoint getAnchor() {
		return anchor.get();
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		final var opts = super.getConfigurationOptions();
		opts.add(anchor);
		opts.add(padding);
		opts.add(columnMargin);
		opts.add(rowMargin);
		return opts;
	}

	@Accessors(fluent = true)
	@Getter
	@Setter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private static class IntObjectPair<T> {
		private int left;
		private T right;

		static <A> IntObjectPair<A> of(int left, A right) {
			return new IntObjectPair<>(left, right);
		}
	}
}
