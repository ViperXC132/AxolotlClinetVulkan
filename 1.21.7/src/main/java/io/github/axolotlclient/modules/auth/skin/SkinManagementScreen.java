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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.mixin.GameRendererAccessor;
import io.github.axolotlclient.mixin.GuiGraphicsAccessor;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.auth.MSApi;
import io.github.axolotlclient.util.ClientColors;
import io.github.axolotlclient.util.Watcher;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public class SkinManagementScreen extends Screen {
	private static final Path SKINS_DIR = FabricLoader.getInstance().getGameDir().resolve("skins");
	private static final int LIST_SKIN_WIDTH = 75;
	private static final int LIST_SKIN_HEIGHT = 110;
	private static final MutableComponent TEXT_EQUIPPING = Component.translatable("skins.manage.equipping");
	private final Screen parent;
	private final HeaderAndFooterLayout haF = new HeaderAndFooterLayout(this);
	private boolean initialized;
	private final Account account;
	private MSApi.MCProfile cachedProfile;
	private SkinListWidget skinList;
	private SkinListWidget capesList;
	private boolean capesTab;
	private SkinWidget current;
	private final Watcher skinDirWatcher;

	public SkinManagementScreen(Screen parent, Account account) {
		super(Component.translatable("skins.manage"));
		this.parent = parent;
		this.account = account;
		skinDirWatcher = Watcher.createSelfTicking(SKINS_DIR, this::loadSkinsList);
	}

	@Override
	protected void init() {
		if (!initialized) {
			initialized = true;

			haF.addTitleHeader(getTitle(), getFont());
			haF.addToFooter(Button.builder(CommonComponents.GUI_BACK, btn -> onClose()).build());
		}
		haF.arrangeElements();
		var loadingPlaceholder = new LoadingDotsWidget(getFont(), Component.translatable("skins.loading"));
		loadingPlaceholder.setRectangle(width, haF.getContentHeight(), 0,
			haF.getHeaderHeight());
		addRenderableWidget(loadingPlaceholder);
		skinList = new SkinListWidget(minecraft, width / 2, haF.getContentHeight() - 24, haF.getHeaderHeight() + 24, LIST_SKIN_HEIGHT + 34);
		capesList = new SkinListWidget(minecraft, width / 2, haF.getContentHeight() - 24, haF.getHeaderHeight() + 24, skinList.getEntryContentsHeight() + 24);
		skinList.setX(width / 2);
		capesList.setX(width / 2);
		var currentHeight = Math.min((width / 2f) * 120 / 85, haF.getContentHeight());
		var currentWidth = currentHeight * 85 / 120;
		current = new SkinWidget((int) currentWidth, (int) currentHeight, null, account);
		current.setPosition((int) (width / 4f - currentWidth / 2), (int) (height / 2f - currentHeight / 2));

		if (!capesTab) {
			capesList.visible = capesList.active = false;
		} else {
			skinList.visible = skinList.active = false;
		}
		List<AbstractWidget> navBar = new ArrayList<>();
		var skinsTab = Button.builder(Component.translatable("skins.nav.skins"), btn -> {
			navBar.forEach(w -> {
				if (w != btn) w.active = true;
			});
			btn.active = false;
			skinList.visible = skinList.active = true;
			capesList.visible = capesList.active = false;
			capesTab = false;
		}).pos(width * 3 / 4 - 102, haF.getHeaderHeight()).width(100).build();
		navBar.add(skinsTab);
		var capesTab = Button.builder(Component.translatable("skins.nav.capes"), btn -> {
			navBar.forEach(w -> {
				if (w != btn) w.active = true;
			});
			btn.active = false;
			skinList.visible = skinList.active = false;
			capesList.visible = capesList.active = true;
			this.capesTab = true;
		}).pos(width * 3 / 4 + 2, haF.getHeaderHeight()).width(100).build();
		navBar.add(capesTab);
		skinsTab.active = this.capesTab;
		capesTab.active = !this.capesTab;
		Runnable addWidgets = () -> {
			removeWidget(loadingPlaceholder);
			addRenderableWidget(current);
			addRenderableWidget(skinsTab);
			addRenderableWidget(capesTab);
			addRenderableWidget(skinList);
			addRenderableWidget(capesList);
			haF.visitWidgets(this::addRenderableWidget);
		};
		if (cachedProfile != null) {
			initDisplay();
			addWidgets.run();
			return;
		}
		CompletableFuture<?> fut;
		if (account.needsRefresh()) {
			fut = account.refresh(Auth.getInstance().getMsApi());
		} else {
			fut = CompletableFuture.completedFuture(null);
		}
		fut.thenComposeAsync(unused -> Auth.getInstance().getMsApi().getProfile(account))
			.thenAcceptAsync(profile -> {
				cachedProfile = profile;
				initDisplay();
				addWidgets.run();
			}).exceptionally(t -> {
				AxolotlClientCommon.getInstance().getLogger().error("Failed to load skins!", t);
				var error = Component.translatable("skins.error.failed_to_load");
				var errorDesc = Component.translatable("skins.error.failed_to_load_desc");
				removeWidget(loadingPlaceholder);
				haF.visitWidgets(this::addRenderableWidget);
				addRenderableWidget(new StringWidget(width / 2 - getFont().width(error) / 2, height / 2 - getFont().lineHeight - 2, getFont().width(error), getFont().lineHeight, error, getFont()));
				addRenderableWidget(new StringWidget(width / 2 - getFont().width(errorDesc) / 2, height / 2 + 1, getFont().width(errorDesc), getFont().lineHeight, errorDesc, getFont()));
				return null;
			});
	}

	private void initDisplay() {
		loadSkinsList();
		loadCapesList();
	}

	private void refreshCurrentList() {
		if (capesTab) {
			var scroll = capesList.scrollAmount();
			loadCapesList();
			capesList.setScrollAmount(scroll);
		} else {
			var scroll = skinList.scrollAmount();
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
		var activeCape = capes.stream().filter(Cape::isActive).findFirst();
		current.setCape(activeCape.orElse(null));
		deselectCape.noCape(activeCape.isEmpty());
		for (int i = 0; i < capes.size() + 1; i += columns) {
			Entry widget;
			if (i == 0) {
				widget = createEntry(capesList.getEntryContentsHeight(), deselectCape, Component.translatable("skins.capes.no_cape"));
			} else {
				var cape = capes.get(i - 1);
				widget = createEntryForCape(current.getSkin(), cape, capesList.getEntryContentsHeight());
			}
			List<AbstractWidget> widgets = new ArrayList<>();
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
		local.removeIf(s -> hashes.contains(s.textureKey()));
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
			if (s != null && s.isActive()) {
				current.setSkin(s);
			}
			var widget = createEntryForSkin(s, entryHeight);
			List<AbstractWidget> widgets = new ArrayList<>();
			widgets.add(widget);
			for (int c = 1; c < columns; c++) {
				if (!(i < skins.size() - c)) continue;
				var s2 = skins.get(i + c);
				if (s2 != null && s2.isActive()) {
					current.setSkin(s2);
				}
				var widget2 = createEntryForSkin(s2, entryHeight);
				widgets.add(widget2);
			}
			skinList.addEntry(new Row(widgets));
		}
	}

	@Override
	public void onFilesDrop(List<Path> packs) {
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
		return createEntry(entryHeight, createWidgetForCape(currentSkin, cape), Component.literal(cape.alias()));
	}

	private SkinWidget createWidgetForCape(Skin currentSkin, Cape cape) {
		SkinWidget widget2 = new SkinWidget(LIST_SKIN_WIDTH, LIST_SKIN_HEIGHT, currentSkin, cape, account);
		widget2.setRotationY(210);
		return widget2;
	}

	@Override
	protected void clearWidgets() {
		super.clearWidgets();
		SkinRenderer.closeRenderers();
		Auth.getInstance().getSkinManager().releaseAll();
	}

	@Override
	public void removed() {
		Auth.getInstance().getSkinManager().releaseAll();
		Watcher.close(skinDirWatcher);
		SkinRenderer.closeRenderers();
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
	}

	private SkinListWidget getCurrentList() {
		return capesTab ? capesList : skinList;
	}

	private static class SkinListWidget extends ContainerObjectSelectionList<Row> {
		public SkinListWidget(Minecraft minecraft, int width, int height, int y, int entryHeight) {
			super(minecraft, width, height, y, entryHeight);
		}

		@Override
		public int addEntry(Row entry) {
			return super.addEntry(entry);
		}

		@Override
		protected int scrollBarX() {
			return getRight() - 8;
		}

		@Override
		public int getRowLeft() {
			return getX() + 3;
		}

		@Override
		public int getRowWidth() {
			if (!scrollbarVisible()) {
				return getWidth() - 4;
			}
			return getWidth() - 14;
		}

		public int getEntryContentsHeight() {
			return itemHeight - 4;
		}

		@Override
		public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
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
	}

	private class Row extends ContainerObjectSelectionList.Entry<Row> {
		private final List<AbstractWidget> widgets;

		public Row(List<AbstractWidget> entries) {
			this.widgets = entries;
		}

		@Override
		public @NotNull List<? extends NarratableEntry> narratables() {
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
		public @NotNull List<? extends GuiEventListener> children() {
			return widgets;
		}

		@Override
		public void setFocused(@Nullable GuiEventListener focused) {
			super.setFocused(focused);
			if (focused != null) {
				getCurrentList().centerScrollOn(this);
			}
		}
	}

	Entry createEntry(int height, SkinWidget widget) {
		return createEntry(height, widget, null);
	}

	Entry createEntry(int height, SkinWidget widget, Component label) {
		return new Entry(height, widget, label);
	}

	private class Entry extends AbstractContainerWidget {
		private final SkinWidget skinWidget;
		private final @Nullable AbstractWidget label;
		private final @Nullable AbstractWidget trashButton;
		private final AbstractWidget equipButton;
		private boolean equipping;
		private long equippingStart;

		public Entry(int height, SkinWidget widget, @Nullable Component label) {
			super(0, 0, widget.getWidth(), height, Component.empty());
			widget.setWidth(getWidth() - 4);
			if (widget.getSkin() instanceof Skin.Local local) {
				this.trashButton = SpriteIconButton.builder(Component.translatable("skins.manage.delete"), btn -> {
					btn.active = false;
					minecraft.setScreen(new ConfirmScreen(confirmed -> {
						minecraft.setScreen(SkinManagementScreen.this);
						if (confirmed) {
							try {
								Files.delete(local.file());
								refreshCurrentList();
							} catch (IOException e) {
								AxolotlClientCommon.getInstance().getLogger().warn("Failed to delete skin: ", e);
							}
						}
						btn.active = true;
					}, Component.translatable("skins.manage.delete.confirm"), Component.translatable("skins.manage.delete.confirm.desc")
						.withColor(Colors.RED.toInt())));
				}, true).sprite(ResourceLocation.fromNamespaceAndPath("axolotlclient", "delete"), 7, 7).size(11, 11).build();
			} else {
				trashButton = null;
			}
			if (label != null) {
				this.label = new AbstractStringWidget(0, 0, widget.getWidth(), 16, label, Minecraft.getInstance().font) {
					@Override
					protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
						renderScrollingString(guiGraphics, getFont(), 2, -1);
					}
				};
				this.label.active = false;
			} else {
				this.label = null;
			}
			this.equipButton = Button.builder(Component.translatable(
					widget.isEquipped() ? "skins.manage.equipped" : "skins.manage.equip"),
				btn -> {
					equippingStart = Util.getMillis();
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
		public @NotNull List<? extends GuiEventListener> children() {
			return Stream.of(trashButton, skinWidget, label, equipButton).filter(Objects::nonNull).toList();
		}

		@Override
		protected int contentHeight() {
			return getHeight();
		}

		@Override
		protected double scrollRate() {
			return 0;
		}

		@Override
		protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
			int y = getY() + 4;
			int x = getX() + 2;
			if (skinWidget.isEquipped() || equipping) {
				long prog;
				if (equipping) {
					prog = (Util.getMillis() - equippingStart) / 20 % 100;
				} else {
					prog = Math.abs((Util.getMillis() / 50 % 100) - 50);
				}
				var percent = prog / 100f;
				float gradientWidth;
				if (equipping) {
					gradientWidth = percent * Math.min(getWidth() / 3f, getHeight() / 3f);
				} else {
					gradientWidth = Math.min(getWidth() / 15f, getHeight() / 6f) + percent * Math.min(getWidth() * 2 / 15f, getHeight() / 6f);
				}
				GradientHoleRectangleRenderState.create(guiGraphics, getX() + 2, getY() + 2, getRight() - 2,
					skinWidget.getBottom() + 2,
					gradientWidth,
					equipping ? 0xFFFF0088 : ClientColors.SELECTOR_GREEN.toInt(), 0).submit();
			}
			skinWidget.setPosition(x, y);
			skinWidget.setWidth(getWidth() - 4);
			skinWidget.render(guiGraphics, mouseX, mouseY, partialTick);
			if (trashButton != null) {
				trashButton.setPosition(skinWidget.getRight() - trashButton.getWidth(), getY() + 2);
				if (isHovered() || trashButton.isHoveredOrFocused()) {
					trashButton.render(guiGraphics, mouseX, mouseY, partialTick);
				}
			}
			if (label != null) {
				label.setPosition(x, skinWidget.getBottom() + 6);
				label.render(guiGraphics, mouseX, mouseY, partialTick);
				label.setWidth(getWidth() - 4);
				equipButton.setPosition(x, label.getBottom() + 2);
			} else {
				equipButton.setPosition(x, skinWidget.getBottom() + 4);
			}
			equipButton.setWidth(getWidth() - 4);
			equipButton.render(guiGraphics, mouseX, mouseY, partialTick);

			if (isHovered()) {
				guiGraphics.renderOutline(getX(), getY(), getWidth(), getHeight(), -1);
			}
		}

		@Override
		protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
			skinWidget.updateNarration(narrationElementOutput);
			if (trashButton != null) {
				trashButton.updateNarration(narrationElementOutput);
			}
			if (label != null) {
				label.updateNarration(narrationElementOutput);
			}
			equipButton.updateNarration(narrationElementOutput);
		}

		private record GradientHoleRectangleRenderState(RenderPipeline pipeline, TextureSetup textureSetup,
														Matrix3x2f pose,
														int x0, int y0, int x1, int y1, float gradientWidth, int col1,
														int col2, @Nullable ScreenRectangle scissorArea,
														@Nullable ScreenRectangle bounds) implements GuiElementRenderState {

			public static GradientHoleRectangleRenderState create(GuiGraphics graphics, int x0, int y0, int x1, int y1, float gradientWidth, int col1, int col2) {
				var matrix = new Matrix3x2f(graphics.pose());
				var area = ((GuiGraphicsAccessor) graphics).getScissorStack().peek();
				return new GradientHoleRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), matrix, x0, y0, x1, y1, gradientWidth, col1, col2, area, getBounds(x0, y0, x1, y1, matrix, area));
			}

			public void submit() {
				((GameRendererAccessor) Minecraft.getInstance().gameRenderer).getGuiRenderState().submitGuiElement(this);
			}

			@Override
			public void buildVertices(VertexConsumer vertexConsumer, float f) {
				//top
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), f).setColor(this.col1());
				//left
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), f).setColor(this.col1());
				//bottom
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), f).setColor(this.col1());
				//right
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), f).setColor(this.col1());
			}

			@Nullable
			private static ScreenRectangle getBounds(int i, int j, int k, int l, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {
				ScreenRectangle screenRectangle2 = new ScreenRectangle(i, j, k - i, l - j).transformMaxBounds(matrix3x2f);
				return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
			}
		}
	}
}
