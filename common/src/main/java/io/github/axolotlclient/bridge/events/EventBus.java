package io.github.axolotlclient.bridge.events;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

/**
 * Probably an overengineered event bus.
 *
 * @param <T>
 */
public final class EventBus<T> {
	public static final class Phase<T> {
		private final AxoIdentifier name;
		private final EventBus<T> bus;
		private final Set<T> handlers;
		private final List<Phase<T>> after;

		private int tempInDegree;

		private Phase(AxoIdentifier name, EventBus<T> bus, Set<T> handlers, List<Phase<T>> after) {
			this.name = name;
			this.bus = bus;
			this.handlers = handlers;
			this.after = after;
		}

		public ListenerHandle register(T listener) {
			handlers.add(listener);
			return () -> handlers.remove(listener);
		}
	}

	public final class PhaseBuilder {
		private final List<Phase<T>> before = new ArrayList<>();
		private final List<Phase<T>> after = new ArrayList<>();

		private PhaseBuilder() {
		}

		public PhaseBuilder before(Phase<T> phase) {
			Preconditions.checkArgument(phase.bus == EventBus.this, "phase %s belongs to a different bus", phase.name);
			before.add(phase);
			return this;
		}

		public PhaseBuilder beforeDefault() {
			return before(defaultPhase);
		}

		public PhaseBuilder after(Phase<T> phase) {
			Preconditions.checkArgument(phase.bus == EventBus.this, "phase %s belongs to a different bus", phase.name);
			after.add(phase);
			return this;
		}

		public PhaseBuilder afterDefault() {
			return after(defaultPhase);
		}

		public Phase<T> define(AxoIdentifier name) {
			Preconditions.checkArgument(!phases.containsKey(name), "phase already exists");
			Phase<T> phase = new Phase<>(
				name,
				EventBus.this,
				Sets.newSetFromMap(new IdentityHashMap<>()),
				after
			);

			before.forEach(x -> x.after.add(phase));
			phases.put(name, phase);
			rebuildSeqCache();
			return phase;
		}

		public Phase<T> define(String ns, String path) {
			return define(AxoIdentifier.of(ns, path));
		}
	}

	public interface ListenerHandle {
		void cancel();
	}

	private final List<Phase<T>> seq = new ArrayList<>();
	private final Map<AxoIdentifier, Phase<T>> phases = new HashMap<>();
	private final Function<Iterable<T>, T> combiner;
	private final Phase<T> defaultPhase = phase().define(AxoIdentifier.of("default"));

	@Nullable
	private T cachedInvoker;

	private static <T> void topologicalSort(Collection<Phase<T>> allPhases, List<Phase<T>> result) {
		ArrayDeque<Phase<T>> queue = new ArrayDeque<>();

		// setup indegree counts
		for (Phase<T> phase : allPhases) {
			phase.tempInDegree = 0;
		}

		for (Phase<T> phase : allPhases) {
			for (Phase<T> dep : phase.after) {
				dep.tempInDegree++;
			}
		}

		// enqueue phases
		for (Phase<T> phase : allPhases) {
			if (phase.tempInDegree == 0) {
				queue.add(phase);
			}
		}

		// process phases
		while (!queue.isEmpty()) {
			Phase<T> phase = queue.poll();
			result.add(phase);

			for (Phase<T> dep : phase.after) {
				dep.tempInDegree--;
				if (dep.tempInDegree == 0) {
					queue.push(dep);
				}
			}
		}

		if (result.size() != allPhases.size()) {
			throw new IllegalArgumentException("Phase dependency detected");
		}
	}

	private void rebuildSeqCache() {
		seq.clear();
		cachedInvoker = null;
		topologicalSort(phases.values(), seq);
	}

	public static <T> EventBus<Predicate<T>> firstTrue() {
		return new EventBus<>(input -> arg -> {
			for (Predicate<T> pred : input) {
				if (pred.test(arg)) {
					return true;
				}
			}

			return false;
		});
	}

	public static <T> EventBus<Predicate<T>> firstFalse() {
		return new EventBus<>(input -> arg -> {
			for (Predicate<T> pred : input) {
				if (!pred.test(arg)) {
					return false;
				}
			}

			return true;
		});
	}

	public static <T> EventBus<UnaryOperator<T>> pipeline() {
		return new EventBus<>(input -> val -> {
			for (UnaryOperator<T> op : input) {
				val = op.apply(val);
			}

			return val;
		});
	}

	public static <T> EventBus<Runnable> broadcast0() {
		return new EventBus<>(input -> () -> {
			for (Runnable op : input) {
				op.run();
			}
		});
	}

	public static <T> EventBus<Consumer<T>> broadcast1() {
		return new EventBus<>(input -> val -> {
			for (Consumer<T> op : input) {
				op.accept(val);
			}
		});
	}

	public static <T, U> EventBus<BiConsumer<T, U>> broadcast2() {
		return new EventBus<>(input -> (a, b) -> {
			for (BiConsumer<T, U> op : input) {
				op.accept(a, b);
			}
		});
	}

	public EventBus(Function<Iterable<T>, T> combiner) {
		this.combiner = combiner;
	}

	public Optional<Phase<T>> getPhase(AxoIdentifier name) {
		return Optional.ofNullable(phases.get(name));
	}

	public PhaseBuilder phase() {
		return new PhaseBuilder();
	}

	public T invoker() {
		if (cachedInvoker == null) {
			cachedInvoker = combiner.apply(() -> seq.stream().flatMap(x -> x.handlers.stream()).iterator());
		}

		return cachedInvoker;
	}

	public Phase<T> defaultPhase() {
		return defaultPhase;
	}
}
