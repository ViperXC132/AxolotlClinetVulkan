package io.github.axolotlclient;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

public abstract class AxolotlClientConfigCommon {
	// options
	public final OptionCategory config = OptionCategory.create("config");
	public final OptionCategory hidden = OptionCategory.create("storedOptions");
	public final StringOption datetimeFormat = new StringOption("datetime_format", "yyyy/MM/dd HH:mm:ss", s -> dateTimeFormatter = DateTimeFormatter.ofPattern(s));

	@Getter
	private DateTimeFormatter dateTimeFormatter;

	public final void add(Option<?> option) {
		config.add(option);
	}

	public final void addCategory(OptionCategory cat) {
		config.add(cat);
	}

	public final OptionCategory getConfig() {
		return config;
	}
}
