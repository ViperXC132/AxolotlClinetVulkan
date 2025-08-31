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

package io.github.axolotlclient.modules.auth.skin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.auth.MSApi;
import io.github.axolotlclient.util.ClientColors;
import io.github.axolotlclient.util.Watcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ElementPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.navigation.GuiNavigationEvent;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DraggingWidget;
import net.minecraft.client.gui.widget.LoadingTextWidget;
import net.minecraft.client.gui.widget.button.ButtonWidget;
import net.minecraft.client.gui.widget.button.SpriteButtonWidget;
import net.minecraft.client.gui.widget.list.ElementListWidget;
import net.minecraft.client.gui.widget.text.AbstractTextWidget;
import net.minecraft.client.gui.widget.text.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.CommonTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkinManagementScreen extends Screen {
	private static final Path SKINS_DIR = FabricLoader.getInstance().getGameDir().resolve("skins");
	private static final int LIST_SKIN_WIDTH = 75;
	private static final int LIST_SKIN_HEIGHT = 110;
	private static final Text TEXT_EQUIPPING = Text.translatable("skins.manage.equipping");
	private final Screen parent;
	private final Account account;
	private MSApi.MCProfile cachedProfile;
	private SkinListWidget skinList;
	private SkinListWidget capesList;
	private boolean capesTab;
	private SkinWidget current;
	private final Watcher skinDirWatcher;
	private boolean triedAccountRefresh;

	public SkinManagementScreen(Screen parent, Account account) {
		super(Text.translatable("skins.manage"));
		this.parent = parent;
		this.account = account;
		skinDirWatcher = Watcher.createSelfTicking(SKINS_DIR, this::loadSkinsList);
	}

	@Override
	protected void init() {
		int headerHeight = 33;
		int contentHeight = height - headerHeight * 2;

		var titleWidget = new TextWidget(0, headerHeight / 2 - textRenderer.fontHeight/2, width, textRenderer.fontHeight, getTitle(), textRenderer);
		if (!triedAccountRefresh) {
			addDrawableSelectableElement(titleWidget);
		}
		var back = ButtonWidget.builder(CommonTexts.BACK, btn -> closeScreen())
			.positionAndSize(width / 2 - 75, height - headerHeight / 2 - 10, 150, 20).build();

		var loadingPlaceholder = new LoadingTextWidget(textRenderer, Text.translatable("skins.loading"));
		loadingPlaceholder.setDimensionsAndPosition(width, contentHeight, 0, headerHeight);
		if (!triedAccountRefresh) {
			addDrawableSelectableElement(loadingPlaceholder);
			addDrawableSelectableElement(back);
		}
		skinList = new SkinListWidget(client, width / 2, contentHeight - 24, headerHeight + 24, LIST_SKIN_HEIGHT + 34);
		capesList = new SkinListWidget(client, width / 2, contentHeight - 24, headerHeight + 24, skinList.getEntryContentsHeight() + 24);
		skinList.setX(width / 2);
		capesList.setX(width / 2);
		var currentHeight = Math.min((width / 2f) * 120 / 85, contentHeight);
		var currentWidth = currentHeight * 85 / 120;
		current = new SkinWidget((int) currentWidth, (int) currentHeight, null, account);
		current.setPosition((int) (width / 4f - currentWidth / 2), (int) (height / 2f - currentHeight / 2));

		if (!capesTab) {
			capesList.visible = capesList.active = false;
		} else {
			skinList.visible = skinList.active = false;
		}
		List<ClickableWidget> navBar = new ArrayList<>();
		var skinsTab = ButtonWidget.builder(Text.translatable("skins.nav.skins"), btn -> {
			navBar.forEach(w -> {
				if (w != btn) w.active = true;
			});
			btn.active = false;
			skinList.visible = skinList.active = true;
			capesList.visible = capesList.active = false;
			capesTab = false;
		}).position(width * 3 / 4 - 102, headerHeight).width(100).build();
		navBar.add(skinsTab);
		var capesTab = ButtonWidget.builder(Text.translatable("skins.nav.capes"), btn -> {
			navBar.forEach(w -> {
				if (w != btn) w.active = true;
			});
			btn.active = false;
			skinList.visible = skinList.active = false;
			capesList.visible = capesList.active = true;
			this.capesTab = true;
		}).position(width * 3 / 4 + 2, headerHeight).width(100).build();
		navBar.add(capesTab);
		skinsTab.active = this.capesTab;
		capesTab.active = !this.capesTab;
		Runnable addWidgets = () -> {
			clearChildren();
			addDrawableSelectableElement(titleWidget);
			addDrawableSelectableElement(current);
			addDrawableSelectableElement(skinsTab);
			addDrawableSelectableElement(capesTab);
			addDrawableSelectableElement(skinList);
			addDrawableSelectableElement(capesList);
			addDrawableSelectableElement(back);
		};
		if (cachedProfile != null) {
			initDisplay();
			addWidgets.run();
			return;
		}
		CompletableFuture<?> fut;
		if (account.needsRefresh()) {
			if (triedAccountRefresh) {
				fut = CompletableFuture.failedFuture(new Throwable(null, null, false, false) {
				});
			} else {
				triedAccountRefresh = true;
				account.refresh(Auth.getInstance().getMsApi());
				return;
			}
		} else {
			fut = CompletableFuture.completedFuture(null);
		}
		fut.thenComposeAsync(unused -> Auth.getInstance().getMsApi().getProfile(account))
			.thenAcceptAsync(profile -> {
				cachedProfile = profile;
				initDisplay();
				addWidgets.run();
			}).exceptionally(t -> {
				if (!triedAccountRefresh) {
					AxolotlClientCommon.getInstance().getLogger().error("Failed to load skins!", t);
				}
				var error = Text.translatable("skins.error.failed_to_load");
				var errorDesc = Text.translatable(triedAccountRefresh ? "skins.error.failed_to_load_not_refreshed" : "skins.error.failed_to_load_desc");
				clearChildren();
				addDrawableSelectableElement(titleWidget);
				addDrawableSelectableElement(new TextWidget(width / 2 - textRenderer.getWidth(error) / 2, height / 2 - textRenderer.fontHeight - 2, textRenderer.getWidth(error), textRenderer.fontHeight, error, textRenderer));
				addDrawableSelectableElement(new TextWidget(width / 2 - textRenderer.getWidth(errorDesc) / 2, height / 2 + 1, textRenderer.getWidth(errorDesc), textRenderer.fontHeight, errorDesc, textRenderer));
				addDrawableSelectableElement(back);
				return null;
			});
	}

	private void initDisplay() {
		loadSkinsList();
		loadCapesList();
	}

	private void refreshCurrentList() {
		if (capesTab) {
			var scroll = capesList.getScrollAmount();
			loadCapesList();
			capesList.setScrollAmount(scroll);
		} else {
			var scroll = skinList.getScrollAmount();
			loadSkinsList();
			skinList.setScrollAmount(scroll);
		}
	}

	private void loadCapesList() {
		capesList.clearEntries();
		var profile = cachedProfile;
		int columns = Math.max(2, (width / 2 - 25) / LIST_SKIN_WIDTH);
		var capes = profile.capes();
		var deselectCape = createWidgetForCape(current.getSkin(), null);
		var activeCape = capes.stream().filter(Cape::active).findFirst();
		current.setCape(activeCape.orElse(null));
		deselectCape.noCape(activeCape.isEmpty());
		for (int i = 0; i < capes.size() + 1; i += columns) {
			Entry widget;
			if (i == 0) {
				widget = createEntry(capesList.getEntryContentsHeight(), deselectCape, Text.translatable("skins.capes.no_cape"));
			} else {
				var cape = capes.get(i - 1);
				widget = createEntryForCape(current.getSkin(), cape, capesList.getEntryContentsHeight());
			}
			List<ClickableWidget> widgets = new ArrayList<>();
			widgets.add(widget);
			for (int c = 1; c < columns; c++) {
				if (!(i < capes.size() + 1 - c)) continue;
				var cape2 = capes.get(i + c - 1);
				Entry widget2 = createEntryForCape(current.getSkin(), cape2, capesList.getEntryContentsHeight());

				widgets.add(widget2);
			}
			capesList.addEntry(new Row(widgets));
		}
	}

	private void loadSkinsList() {
		skinList.clearEntries();
		var profile = cachedProfile;
		int columns = Math.max(2, (width / 2 - 25) / LIST_SKIN_WIDTH);
		List<Skin> skins = new ArrayList<>(profile.skins());
		var hashes = skins.stream().map(Asset::textureKey).collect(Collectors.toSet());
		var defaultSkinHash = Auth.getInstance().getSkinManager().getDefaultSkinHash(account);
		var local = new ArrayList<>(loadLocalSkins());
		var localHashes = local.stream().collect(Collectors.toMap(Asset::textureKey, Function.identity()));
		skins.replaceAll(s -> {
			if (s instanceof MSApi.MCProfile.OnlineSkin online) {
				if (localHashes.containsKey(s.textureKey()) && localHashes.get(s.textureKey()) instanceof Skin.Local file) {
					local.remove(localHashes.remove(s.textureKey()));
					return new Skin.Shared(file, online);
				}
			}
			return s;
		});
		//local.removeIf(s -> hashes.contains(s.textureKey()));
		skins.addAll(local);
		if (!hashes.contains(defaultSkinHash)) {
			skins.add(null);
		}
		populateSkinList(skins, columns);
	}

	private List<Skin> loadLocalSkins() {
		try {
			Files.createDirectories(SKINS_DIR);
			try (Stream<Path> skins = Files.list(SKINS_DIR)) {
				return skins.filter(Files::isRegularFile).sorted(Comparator.<Path>comparingLong(p -> {
					try {
						return Files.getLastModifiedTime(p).toMillis();
					} catch (IOException e) {
						return 0L;
					}
				}).reversed()).map(Auth.getInstance().getSkinManager()::read).filter(Objects::nonNull).toList();
			}
		} catch (IOException e) {
			AxolotlClientCommon.getInstance().getLogger().warn("Failed to read skins dir!", e);
		}
		return Collections.emptyList();
	}

	private void populateSkinList(List<? extends Skin> skins, int columns) {
		int entryHeight = skinList.getEntryContentsHeight();
		for (int i = 0; i < skins.size(); i += columns) {
			var s = skins.get(i);
			if (s != null && s.active()) {
				current.setSkin(s);
			}
			var widget = createEntryForSkin(s, entryHeight);
			List<ClickableWidget> widgets = new ArrayList<>();
			widgets.add(widget);
			for (int c = 1; c < columns; c++) {
				if (!(i < skins.size() - c)) continue;
				var s2 = skins.get(i + c);
				if (s2 != null && s2.active()) {
					current.setSkin(s2);
				}
				var widget2 = createEntryForSkin(s2, entryHeight);
				widgets.add(widget2);
			}
			skinList.addEntry(new Row(widgets));
		}
	}

	@Override
	public void filesDragged(List<Path> packs) {
		packs.forEach(p -> {
			try {
				Files.copy(p, SKINS_DIR.resolve(p.getFileName()));
			} catch (IOException e) {
				AxolotlClientCommon.getInstance().getLogger().warn("Failed to copy skin file: ", e);
			}
		});
		loadSkinsList();
	}

	private @NotNull Entry createEntryForSkin(Skin skin, int entryHeight) {
		return createEntry(entryHeight, new SkinWidget(LIST_SKIN_WIDTH, LIST_SKIN_HEIGHT, skin, account));
	}

	private @NotNull Entry createEntryForCape(Skin currentSkin, Cape cape, int entryHeight) {
		return createEntry(entryHeight, createWidgetForCape(currentSkin, cape), Text.literal(cape.alias()));
	}

	private SkinWidget createWidgetForCape(Skin currentSkin, Cape cape) {
		SkinWidget widget2 = new SkinWidget(LIST_SKIN_WIDTH, LIST_SKIN_HEIGHT, currentSkin, cape, account);
		widget2.setRotationY(210);
		return widget2;
	}

	@Override
	protected void clearChildren() {
		super.clearChildren();
		Auth.getInstance().getSkinManager().releaseAll();
	}

	@Override
	public void removed() {
		Auth.getInstance().getSkinManager().releaseAll();
		Watcher.close(skinDirWatcher);
	}

	@Override
	public void closeScreen() {
		client.setScreen(parent);
	}

	private SkinListWidget getCurrentList() {
		return capesTab ? capesList : skinList;
	}

	private class SkinListWidget extends ElementListWidget<Row> {
		public SkinListWidget(MinecraftClient minecraft, int width, int height, int y, int entryHeight) {
			super(minecraft, width, height, y, entryHeight);
		}

		@Override
		public int addEntry(Row entry) {
			return super.addEntry(entry);
		}

		@Override
		protected int getScrollbarPositionX() {
			return getXEnd() - 8;
		}

		@Override
		public int getRowLeft() {
			return getX() + 3;
		}

		@Override
		public int getRowWidth() {
			if (!canScroll()) {
				return getWidth() - 4;
			}
			return getWidth() - 14;
		}

		public int getEntryContentsHeight() {
			return itemHeight - 4;
		}

		@Override
		public @Nullable ElementPath nextFocusPath(GuiNavigationEvent event) {
			if (!active || !visible) return null;
			return super.nextFocusPath(event);
		}

		@Override
		public void clearEntries() {
			super.clearEntries();
		}

		@Override
		public void centerScrollOn(Row entry) {
			super.centerScrollOn(entry);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double amountX, double amountY) {
			if (!visible) return false;
			return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return active && visible && super.isMouseOver(mouseX, mouseY);
		}
	}

	private class Row extends ElementListWidget.Entry<Row> {
		private final List<ClickableWidget> widgets;

		public Row(List<ClickableWidget> entries) {
			this.widgets = entries;
		}

		@Override
		public @NotNull List<? extends Selectable> selectableChildren() {
			return widgets;
		}

		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			int x = left;
			if (widgets.isEmpty()) return;
			int count = widgets.size();
			int padding = ((width - 5 * (count - 1)) / count);
			for (var w : widgets) {
				w.setPosition(x, top);
				w.setWidth(padding);
				w.render(guiGraphics, mouseX, mouseY, partialTick);
				x += w.getWidth() + 5;
			}
		}

		@Override
		public @NotNull List<? extends Element> children() {
			return widgets;
		}

		@Override
		public void setFocusedChild(@Nullable Element focused) {
			super.setFocusedChild(focused);
			if (focused != null) {
				getCurrentList().centerScrollOn(this);
			}
		}
	}

	Entry createEntry(int height, SkinWidget widget) {
		return createEntry(height, widget, null);
	}

	Entry createEntry(int height, SkinWidget widget, Text label) {
		return new Entry(height, widget, label);
	}

	private class Entry extends DraggingWidget {
		private final SkinWidget skinWidget;
		private final @Nullable ClickableWidget label;
		private final List<ClickableWidget> actionButtons = new ArrayList<>();
		private final ClickableWidget equipButton;
		private boolean equipping;
		private long equippingStart;

		public Entry(int height, SkinWidget widget, @Nullable Text label) {
			super(0, 0, widget.getWidth(), height, Text.empty());
			widget.setWidth(getWidth() - 4);
			var asset = widget.getFocusedAsset();
			if (asset != null) {
				if (asset.isLocal()) {
					var delete = SpriteButtonWidget.builder(Text.translatable("skins.manage.delete"), btn -> {
							btn.active = false;
							client.setScreen(new ConfirmScreen(confirmed -> {
								client.setScreen(SkinManagementScreen.this);
								if (confirmed) {
									try {
										Files.delete(asset.file());
										refreshCurrentList();
									} catch (IOException e) {
										AxolotlClientCommon.getInstance().getLogger().warn("Failed to delete: ", e);
									}
								}
								btn.active = true;
							}, Text.translatable("skins.manage.delete.confirm"), (asset.active() ?
								Text.translatable("skins.manage.delete.confirm.desc_active") :
								Text.translatable("skins.manage.delete.confirm.desc"))
								.setColor(Colors.RED.toInt())));
						}, true).sprite(Identifier.of("axolotlclient", "delete"), 7, 7).dimensions(11, 11)
						.build();
					delete.setTooltip(Tooltip.create(delete.getMessage()));
					this.actionButtons.add(delete);
				}
				if (asset.supportsDownload() && !asset.isLocal()) {
					var download = SpriteButtonWidget.builder(Text.translatable("skins.manage.download"), btn -> {
						btn.active = false;
						asset.image().thenAcceptAsync(b -> {
							try {
								var out = SKINS_DIR.resolve(asset.textureKey());
								Files.createDirectories(out.getParent());
								Files.write(out, b);
							} catch (IOException e) {
								AxolotlClientCommon.getInstance().getLogger().warn("Failed to download: ", e);
							}
							refreshCurrentList();
							btn.active = true;
						});
					}, true).sprite(Identifier.of("axolotlclient", "download"), 7, 7).dimensions(11, 11).build();
					download.setTooltip(Tooltip.create(download.getMessage()));
					this.actionButtons.add(download);
				}
			}
			if (label != null) {
				this.label = new AbstractTextWidget(0, 0, widget.getWidth(), 16, label, textRenderer) {
					@Override
					protected void drawWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
						drawScrollingText(guiGraphics, textRenderer, 2, -1);
					}
				};
				this.label.active = false;
			} else {
				this.label = null;
			}
			this.equipButton = ButtonWidget.builder(Text.translatable(
					widget.isEquipped() ? "skins.manage.equipped" : "skins.manage.equip"),
				btn -> {
					equippingStart = Util.getMeasuringTimeMs();
					equipping = true;
					btn.setMessage(TEXT_EQUIPPING);
					btn.active = false;
					widget.equip().thenAcceptAsync(p -> {
						cachedProfile = p;
						refreshCurrentList();
					}).exceptionally(t -> {
						AxolotlClientCommon.getInstance().getLogger().warn("Failed to equip asset!", t);
						return null;
					});
				}).width(widget.getWidth()).build();
			this.equipButton.active = !widget.isEquipped();
			this.skinWidget = widget;
		}

		@Override
		public @NotNull List<? extends Element> children() {
			return Stream.concat(actionButtons.stream(), Stream.of(skinWidget, label, equipButton)).filter(Objects::nonNull).toList();
		}

		private float applyEasing(float x) {
			return x * x * x;
		}

		@Override
		protected void drawWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int y = getY() + 4;
			int x = getX() + 2;
			if (skinWidget.isEquipped() || equipping) {
				long prog;
				if (Auth.getInstance().skinManagerAnimations.get()) {
					if (equipping) prog = (Util.getMeasuringTimeMs() - equippingStart) / 20 % 100;
					else prog = Math.abs((Util.getMeasuringTimeMs() / 30 % 200) - 100);
				} else prog = 100;
				var percent = (prog / 100f);
				float gradientWidth;
				if (equipping) {
					gradientWidth = percent * Math.min(getWidth() / 3f, getHeight() / 3f);
				} else {
					gradientWidth = Math.min(getWidth() / 15f, getHeight() / 6f) + applyEasing(percent) * Math.min(getWidth() * 2 / 15f, getHeight() / 6f);
				}
				GradientHoleRectangleRenderState.render(guiGraphics, getX() + 2, getY() + 2, getXEnd() - 2,
					skinWidget.getYEnd() + 2,
					gradientWidth,
					equipping ? 0xFFFF0088 : ClientColors.SELECTOR_GREEN.toInt(), 0);
			}
			skinWidget.setPosition(x, y);
			skinWidget.setWidth(getWidth() - 4);
			skinWidget.render(guiGraphics, mouseX, mouseY, partialTick);
			int actionButtonY = getY() + 2;
			for (var button : actionButtons) {
				button.setPosition(skinWidget.getXEnd() - button.getWidth(), actionButtonY);
				if (isHovered() || button.isHoveredOrFocused()) {
					button.render(guiGraphics, mouseX, mouseY, partialTick);
				}
				actionButtonY += button.getHeight() + 2;
			}
			if (label != null) {
				label.setPosition(x, skinWidget.getYEnd() + 6);
				label.render(guiGraphics, mouseX, mouseY, partialTick);
				label.setWidth(getWidth() - 4);
				equipButton.setPosition(x, label.getYEnd() + 2);
			} else {
				equipButton.setPosition(x, skinWidget.getYEnd() + 4);
			}
			equipButton.setWidth(getWidth() - 4);
			equipButton.render(guiGraphics, mouseX, mouseY, partialTick);

			if (isHovered()) {
				guiGraphics.br$outlineRect(getX(), getY(), getWidth(), getHeight(), -1);
			}
		}

		@Override
		protected void updateNarration(NarrationMessageBuilder narrationElementOutput) {
			skinWidget.appendNarrations(narrationElementOutput);
			actionButtons.forEach(w -> w.appendNarrations(narrationElementOutput));
			if (label != null) {
				label.appendNarrations(narrationElementOutput);
			}
			equipButton.appendNarrations(narrationElementOutput);
		}

		private static class GradientHoleRectangleRenderState {

			public static void render(GuiGraphics graphics, int x0, int y0, int x1, int y1, float gradientWidth, int col1, int col2) {
				var vertexConsumer = graphics.getVertexConsumers().getBuffer(RenderLayer.getGui());
				float z = 0;
				//top
				var pose = graphics.getMatrices().peek().getModel();
				vertexConsumer.xyz(pose, x0, y0, z).color(col1);
				vertexConsumer.xyz(pose, x0 + gradientWidth, y0 + gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x1 - gradientWidth, y0 + gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x1, y0, z).color(col1);
				//left
				vertexConsumer.xyz(pose, x0, y1, z).color(col1);
				vertexConsumer.xyz(pose, x0 + gradientWidth, y1 - gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x0 + gradientWidth, y0 + gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x0, y0, z).color(col1);
				//bottom
				vertexConsumer.xyz(pose, x1, y1, z).color(col1);
				vertexConsumer.xyz(pose, x1 - gradientWidth, y1 - gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x0 + gradientWidth, y1 - gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x0, y1, z).color(col1);
				//right
				vertexConsumer.xyz(pose, x1, y0, z).color(col1);
				vertexConsumer.xyz(pose, x1 - gradientWidth, y0 + gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x1 - gradientWidth, y1 - gradientWidth, z).color(col2);
				vertexConsumer.xyz(pose, x1, y1, z).color(col1);
			}
		}
	}
}
