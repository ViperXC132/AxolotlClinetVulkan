package io.github.axolotlclient.bridge.util;

import io.github.axolotlclient.bridge.internal.PlatformImplInternal;

public interface AxoI18n {
	static String translate(String nameKey, Object... args) {
		return PlatformImplInternal.getTranslatedString(nameKey, args);
	}
}
