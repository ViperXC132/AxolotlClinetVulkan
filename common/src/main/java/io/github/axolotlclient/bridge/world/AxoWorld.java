package io.github.axolotlclient.bridge.world;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import java.util.List;

public interface AxoWorld {
	long getTimeOfDay();

	List<? extends AxoEntity> getPlayers();
}
