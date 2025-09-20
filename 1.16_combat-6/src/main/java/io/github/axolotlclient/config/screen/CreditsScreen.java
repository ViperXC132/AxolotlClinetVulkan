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

package io.github.axolotlclient.config.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.github.axolotlclient.AxolotlClient;
import io.github.axolotlclient.AxolotlClientConfig.impl.ui.vanilla.widgets.PlainTextButtonWidget;
import io.github.axolotlclient.bridge.util.AxoText;
import io.github.axolotlclient.credits.Credits;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.modules.hud.util.RenderUtil;
import io.github.axolotlclient.util.ClientColors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class CreditsScreen extends Screen {

	public static final HashMap<String, String[]> externalModuleCredits = new HashMap<>();
	private final Screen parent;
	private final SoundInstance bgm = PositionedSoundInstance.master(SoundEvents.MUSIC_DISC_CHIRP, 1, 1);
	private CreditsList creditsList;

	public CreditsScreen(Screen parent) {
		super(new TranslatableText("credits"));
		this.parent = parent;
	}

	@Override
	public void renderBackground(MatrixStack stack) {
		if (AxolotlClient.config().someNiceBackground.get()) { // Credit to pridelib for the colors
			fill(stack, 0, 0, width, height / 6, 0xFFff0018);
			fill(stack, 0, height / 6, width, height * 2 / 6, 0xFFffa52c);
			fill(stack, 0, height * 2 / 6, width, height / 2, 0xFFffff41);
			fill(stack, 0, height * 2 / 3, width, height * 5 / 6, 0xFF0000f9);
			fill(stack, 0, height / 2, width, height * 2 / 3, 0xFF008018);
			fill(stack, 0, height * 5 / 6, width, height, 0xFF86007d);
		} else {
			super.renderBackground(stack);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderBackground(matrices);
		creditsList.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, textRenderer, getTitle(), width / 2, 33 / 2 - textRenderer.fontHeight / 2, -1);
	}

	@Override
	public void init() {
		creditsList = addChild(new CreditsList(client, width, height, height - 33 - 33, 33, 25));

		var back = addButton(new ButtonWidget(width / 2 - 75, height - 33 / 2 - 20 / 2, 150, 20, ScreenTexts.BACK, buttonWidget -> onClose()));

		addButton(new ButtonWidget(6, back.y, 100, 20, new TranslatableText("creditsBGM").append(": ")
			.append(new TranslatableText(AxolotlClient.config().creditsBGM.get() ? "options.on" : "options.off")),
			buttonWidget -> {
				AxolotlClient.config().creditsBGM.toggle();
				AxolotlClient.getInstance().getConfigManager().save();
				stopBGM();
				buttonWidget.setMessage(new TranslatableText("creditsBGM").append(": ").append(
					new TranslatableText(AxolotlClient.config().creditsBGM.get() ? "options.on" : "options.off")));
			}));
	}

	@Override
	public void onClose() {
		client.openScreen(parent);
		stopBGM();
	}

	@Override
	public void tick() {
		tickBGM();
	}

	public void tickBGM() {
		if (AxolotlClient.config().creditsBGM.get() && !client.getSoundManager().isPlaying(bgm)) {
			client.getSoundManager().play(bgm);
		}
	}

	private void stopBGM() {
		client.getSoundManager().stop(bgm);
	}

	private class CreditsList extends ElementListWidget<Entry> {

		public CreditsList(MinecraftClient minecraftClient, int width, int screenHeight, int height, int top,
						   int entryHeight) {
			super(minecraftClient, width, height, top, top + height, entryHeight);

			addEntry(new SpacerTitle("- - - - - - " + I18n.translate("contributors") + " - - - - - -"));
			Credits.getContributors().forEach(credit -> addEntry(new Credit(credit.getName(), credit.getThings())));

			addEntry(new SpacerTitle("- - - - - - " + I18n.translate("other_people") + " - - - - - -"));
			Credits.getOtherPeople().forEach(credit -> addEntry(new Credit(credit.getName(), credit.getThings())));

			if (!externalModuleCredits.isEmpty()) {
				addEntry(new SpacerTitle("- - - - - - " + I18n.translate("external_modules") + " - - - - - -"));
				externalModuleCredits.forEach((s, s2) -> addEntry(new Credit(s, s2)));
			}
		}
	}

	private abstract static class Entry extends ElementListWidget.Entry<Entry> {

	}

	private class Credit extends Entry {

		private final String name;
		private final String[] things;
		private final ButtonWidget c;

		public Credit(String name, String... things) {
			this.name = name;
			this.things = things;
			c = new ButtonWidget(0, 0, 200, 20, Text.of(name), buttonWidget -> client.openScreen(new CreditOverlay(this))) {
				@Override
				public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
					if (isHovered()) {
						RenderUtil.drawOutline(graphics, x, y, getWidth(), getHeight(), ClientColors.ERROR.toInt());
					}
					int i = this.active ? (isHovered() ? ClientColors.SELECTOR_RED.toInt() : -1) : 10526880;
					DrawUtil.drawCenteredText(graphics, textRenderer, getMessage(), x + width / 2, y + height / 2 - textRenderer.fontHeight / 2, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
				}
			};
		}

		@Override
		public void render(MatrixStack graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
						   int mouseY, boolean hovered, float tickDelta) {
			c.x = x;
			c.y = y;
			c.render(graphics, mouseX, mouseY, tickDelta);
		}

		@Override
		public List<? extends Element> children() {
			return List.of(c);
		}
	}

	private class CreditOverlay extends Screen {
		private final Credit credit;
		private final List<Consumer<MatrixStack>> lines = new ArrayList<>();

		public CreditOverlay(Credit credit) {
			super(Text.of(credit.name));
			this.credit = credit;
		}

		@Override
		public void onClose() {
			client.openScreen(CreditsScreen.this);
		}

		@Override
		public void init() {
			int startY = 100;
			for (String t : credit.things) {
				int textWidth = textRenderer.br$getWidth(t);
				if (t.startsWith("http")) {
					addButton(new PlainTextButtonWidget(width / 2 - textWidth / 2, startY, textWidth, 12,
						(Text) AxoText.literal(t).br$color(ClientColors.SELECTOR_GREEN), btn -> {
						handleTextClick(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, t)));
					}, textRenderer));
				} else {
					int y = startY;
					lines.add(m -> textRenderer.draw(m, t, width / 2f - textWidth / 2f, y,
						ClientColors.SELECTOR_GREEN.toInt()));
				}
				startY += 12;
			}
			addButton(new ButtonWidget(width / 2 - 75, height - 33 / 2 - 10, 150, 20, ScreenTexts.BACK, buttonWidget -> onClose()));
		}

		@Override
		public void renderBackground(MatrixStack graphics) {
			super.renderBackground(graphics);
			RenderUtil.drawRectangle(graphics, 100, 50, width - 200, height - 100,
				ClientColors.DARK_GRAY.withAlpha(127));
			DrawUtil.outlineRect(graphics, 100, 50, width - 200, height - 100,
				ClientColors.BLACK.toInt());
		}

		@Override
		public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
			super.render(graphics, mouseX, mouseY, delta);
			DrawUtil.drawCenteredString(graphics, textRenderer, credit.name,
				width / 2, 57, -16784327, true);
			lines.forEach(s -> s.accept(graphics));
		}

		@Override
		public void tick() {
			CreditsScreen.this.tickBGM();
		}
	}

	private class SpacerTitle extends Entry {

		private final String name;

		public SpacerTitle(String name) {
			this.name = name;
		}

		@Override
		public void render(MatrixStack graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
						   int mouseY, boolean hovered, float tickDelta) {
			DrawUtil.drawCenteredString(graphics, textRenderer, name, x + entryWidth / 2, y, -128374,
				true);
		}

		@Override
		public List<? extends Element> children() {
			return List.of();
		}
	}
}
