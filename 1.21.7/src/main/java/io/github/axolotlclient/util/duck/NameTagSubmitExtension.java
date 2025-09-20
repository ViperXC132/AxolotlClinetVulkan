package io.github.axolotlclient.util.duck;

public interface NameTagSubmitExtension {
	default void axolotlclient$hasBadge(boolean b) {

	}

	default boolean axolotlclient$hasBadge() {
		return false;
	}

	default void axolotlclient$isForLevelHead(boolean b) {
	}

	default boolean axolotlclient$isForLevelHead() {
		return false;
	}
}
