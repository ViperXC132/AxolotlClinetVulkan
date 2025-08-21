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

package io.github.axolotlclient.config.screen;

import java.util.List;

import io.github.axolotlclient.config.profiles.Profiles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProfilesScreen extends Screen {

	private final Screen parent;

	public ProfilesScreen(Screen parent) {
		super(new TranslatableText("profiles.configure.list"));
		this.parent = parent;
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics);
		super.render(graphics, mouseX, mouseY, delta);
		drawCenteredText(graphics, textRenderer, getTitle(), width / 2, 33 / 2 - textRenderer.fontHeight / 2, -1);
	}

	@Override
	protected void init() {
		addChild(new ProfilesList(client, width, height, 33, height - 33, 25));

		addButton(new ButtonWidget(width / 2 - 75, height - 33 / 2 - 10, 150, 20, ScreenTexts.BACK, btn -> onClose()));
	}

	@Override
	public void onClose() {
		Profiles.getInstance().saveProfiles();
		client.openScreen(parent);
	}

	public class ProfilesList extends ElementListWidget<ProfilesList.Entry> {
		private static final Entry SPACER = new SpacerEntry();
		private final Entry ADD = new NewEntry();

		public ProfilesList(MinecraftClient minecraft, int width, int height, int top, int bottom, int itemHeight) {
			super(minecraft, width, height, top, bottom, itemHeight);
			reload();
		}

		public void reload() {
			clearEntries();
			Profiles.getInstance().iterateAvailable(p -> addEntry(new ProfileEntry(p)));
			addEntry(SPACER);
			addEntry(ADD);
		}

		@Override
		public int getRowWidth() {
			return 340;
		}

		@Override
		protected int getScrollbarPositionX() {
			return getRowLeft() + getRowWidth() + 10;
		}

		@Environment(EnvType.CLIENT)
		public abstract static class Entry extends ElementListWidget.Entry<Entry> {

		}

		public static class SpacerEntry extends Entry {
			@Override
			public void render(MatrixStack guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {

			}

			@Override
			public List<? extends Element> children() {
				return List.of();
			}
		}

		@Environment(EnvType.CLIENT)
		public class ProfileEntry extends Entry {
			private static final Text CURRENT_TEXT = new TranslatableText("profiles.profile.current");
			private static final Text LOAD_BUTTON_TITLE = new TranslatableText("profiles.profile.load");
			private static final Text DUPLICATE_BUTTON_TITLE = new TranslatableText("profiles.profile.duplicate");
			private static final Text REMOVE_BUTTON_TITLE = new TranslatableText("profiles.profile.remove");
			private final TextFieldWidget profileName;
			private final ButtonWidget loadButton;
			private final ButtonWidget duplicateButton;
			private final ButtonWidget removeButton;
			private final Profiles.Profile profile;

			ProfileEntry(Profiles.Profile profile) {
				this.profile = profile;
				profileName = new TextFieldWidget(textRenderer, 0, 0, 150, 20, LiteralText.EMPTY);
				profileName.setText(profile.name());
				profileName.setChangedListener(profile::setName);
				loadButton = new ButtonWidget(0, 0, 50, 20, LOAD_BUTTON_TITLE, btn ->
					Profiles.getInstance().switchTo(profile));
				duplicateButton = new ButtonWidget(0, 0, 50, 20, DUPLICATE_BUTTON_TITLE, b -> {
					var dup = Profiles.getInstance().duplicate(profile);
					double d = (double) Math.max(0, getMaxPosition() - (bottom - top - 4)) - ProfilesList.this.getScrollAmount();
					ProfilesList.this.children().add(ProfilesList.this.children().indexOf(ProfileEntry.this) + 1, new ProfileEntry(dup));
					ProfilesList.this.setScrollAmount(Math.max(0, getMaxPosition() - (bottom - top - 4)) - d);
				});

				this.removeButton = new ButtonWidget(0, 0, 50, 20, REMOVE_BUTTON_TITLE, b -> {
					removeEntry(this);
					Profiles.getInstance().remove(profile);
					setScrollAmount(getScrollAmount());
				});
			}

			@Override
			public void render(MatrixStack guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
				int i = getScrollbarPositionX() - removeButton.getWidth() - 4;
				int j = top - 2;
				this.removeButton.x = i;
				removeButton.y = j;
				this.removeButton.render(guiGraphics, mouseX, mouseY, partialTick);

				i -= duplicateButton.getWidth();
				duplicateButton.x = i;
				duplicateButton.y = j;
				duplicateButton.render(guiGraphics, mouseX, mouseY, partialTick);

				boolean current = Profiles.getInstance().getCurrent() == profile;
				loadButton.setMessage(current ? CURRENT_TEXT : LOAD_BUTTON_TITLE);
				loadButton.active = !current;
				i -= loadButton.getWidth();
				this.loadButton.x = i;
				loadButton.y = j;
				this.loadButton.render(guiGraphics, mouseX, mouseY, partialTick);
				profileName.setWidth(i - left - 4);
				profileName.x = left;
				profileName.y = j;
				profileName.render(guiGraphics, mouseX, mouseY, partialTick);
			}

			@Override
			public List<? extends Element> children() {
				return List.of(profileName, this.loadButton, duplicateButton, removeButton);
			}
		}

		public class NewEntry extends Entry {

			private final ButtonWidget addButton;

			public NewEntry() {
				this.addButton = new ButtonWidget(0, 0, 150, 20, new TranslatableText("profiles.profile.add"), button -> {
					int i = ProfilesList.this.children().indexOf(this);
					ProfilesList.this.children().add(Math.max(i - 1, 0), new ProfileEntry(Profiles.getInstance().newProfile(I18n.translate("profiles.profile.default_new_name"))));
					Profiles.getInstance().saveProfiles();
					setScrollAmount(Math.max(0, getMaxPosition() - (bottom - top - 4)));
				});
			}

			@Override
			public void render(MatrixStack guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
				int i = getScrollbarPositionX() - width / 2 - 10 - addButton.getWidth() / 2;
				int j = top - 2;
				this.addButton.x = i;
				addButton.y = j;
				this.addButton.render(guiGraphics, mouseX, mouseY, partialTick);
			}

			@Override
			public List<? extends Element> children() {
				return List.of(addButton);
			}
		}
	}
}
