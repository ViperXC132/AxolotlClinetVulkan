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
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientCommon;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.api.SimpleTextInputScreen;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.auth.MSApi;
import io.github.axolotlclient.modules.hud.util.DrawUtil;
import io.github.axolotlclient.util.ButtonWidgetTextures;
import io.github.axolotlclient.util.ClientColors;
import io.github.axolotlclient.util.Watcher;
import io.github.axolotlclient.util.notifications.Notifications;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkinManagementScreen extends Screen {
	private static final Path SKINS_DIR = FabricLoader.getInstance().getGameDir().resolve("skins");
	private static final int LIST_SKIN_WIDTH = 75;
	private static final int LIST_SKIN_HEIGHT = 110;
	private static final Text TEXT_EQUIPPING = new TranslatableText("skins.manage.equipping");
	private final Screen parent;
	private final Account account;
	private MSApi.MCProfile cachedProfile;
	private SkinListWidget skinList;
	private SkinListWidget capesList;
	private boolean capesTab;
	private SkinWidget current;
	private final Watcher skinDirWatcher;
	private final List<Drawable> drawables = new ArrayList<>();
	private final CompletableFuture<?> refreshFuture;
	private Text tooltip;

	public SkinManagementScreen(Screen parent, Account account) {
		super(new TranslatableText("skins.manage"));
		this.parent = parent;
		this.account = account;
		skinDirWatcher = Watcher.createSelfTicking(SKINS_DIR, () -> {
			AxolotlClientCommon.getInstance().getLogger().info("Reloading screen as local files changed!");
			loadSkinsList();
		});
		if (account.needsRefresh()) {
			refreshFuture = account.refresh(Auth.getInstance().getMsApi());
		} else {
			refreshFuture = CompletableFuture.completedFuture(null);
		}
	}

	@Override
	public void init() {
		int headerHeight = 33;
		int contentHeight = height - headerHeight * 2;

		var back = addDrawableChild(new ButtonWidget(width / 2 - 75, height - headerHeight / 2 - 10, 150, 20, ScreenTexts.BACK, btn -> onClose()));

		var loadingPlaceholder = new AbstractButtonWidget(0, headerHeight, width, contentHeight, new TranslatableText("skins.loading")) {
			@Override
			public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
				int centerX = this.x + this.getWidth() / 2;
				int centerY = this.y + this.getHeight() / 2;
				Text text = this.getMessage();
				textRenderer.draw(graphics, text, centerX - textRenderer.getWidth(text) / 2f, centerY - 9, -1);
				String string = switch ((int) (Util.getMeasuringTimeMs() / 300L % 4L)) {
					case 1, 3 -> "o O o";
					case 2 -> "o o O";
					default -> "O o o";
				};
				textRenderer.draw(graphics, string, centerX - textRenderer.getWidth(string) / 2f, centerY + 9, 0xFF808080);
			}

			@Override
			protected MutableText getNarrationMessage() {
				return LiteralText.EMPTY.copy();
			}
		};
		loadingPlaceholder.active = false;
		addDrawableChild(loadingPlaceholder);
		addDrawableChild(back);

		skinList = new SkinListWidget(client, width / 2, contentHeight - 24, headerHeight + 24, LIST_SKIN_HEIGHT + 34);
		capesList = new SkinListWidget(client, width / 2, contentHeight - 24, headerHeight + 24, skinList.getEntryContentsHeight() + 24);
		skinList.setLeftPos(width / 2);
		capesList.setLeftPos(width / 2);
		var currentHeight = Math.min((width / 2f) * 120 / 85, contentHeight);
		var currentWidth = currentHeight * 85 / 120;
		current = new SkinWidget((int) currentWidth, (int) currentHeight, null, account);
		current.setPosition((int) (width / 4f - currentWidth / 2), (int) (height / 2f - currentHeight / 2));

		if (!capesTab) {
			capesList.visible = capesList.active = false;
		} else {
			skinList.visible = skinList.active = false;
		}
		List<AbstractButtonWidget> navBar = new ArrayList<>();
		var skinsTab = new ButtonWidget(Math.max(width * 3 / 4 - 102, width / 2 + 2), headerHeight, Math.min(100, width / 4 - 2), 20, new TranslatableText("skins.nav.skins"), btn -> {
			navBar.forEach(w -> {
				if (w != btn) w.active = true;
			});
			btn.active = false;
			skinList.visible = skinList.active = true;
			capesList.visible = capesList.active = false;
			capesTab = false;
		});
		navBar.add(skinsTab);
		var capesTab = new ButtonWidget(width * 3 / 4 + 2, headerHeight, Math.min(100, width / 4 - 2), 20, new TranslatableText("skins.nav.capes"), btn -> {
			navBar.forEach(w -> {
				if (w != btn) w.active = true;
			});
			btn.active = false;
			skinList.visible = skinList.active = false;
			capesList.visible = capesList.active = true;
			this.capesTab = true;
		});
		navBar.add(capesTab);
		var importButton = new SpriteButton(new TranslatableText("skins.manage.import.local"), btn -> {
			btn.active = false;
			SkinImportUtil.openImportSkinDialog().thenAccept(this::filesDragged).thenRun(() -> btn.active = true);
		}, new Identifier("axolotlclient", "textures/gui/sprites/folder.png"));
		var downloadButton = new SpriteButton(new TranslatableText("skins.manage.import.online"), btn -> {
			btn.active = false;
			promptForSkinDownload();
		}, new Identifier("axolotlclient", "textures/gui/sprites/download.png"));
		downloadButton.x = importButton.x - 2 - 11;
		downloadButton.y = capesTab.y - 13;
		if (width - (capesTab.x + capesTab.getWidth()) > 28) {
			importButton.x = width - importButton.getWidth() - 2;
			downloadButton.x = importButton.x - downloadButton.getWidth() - 2;
			importButton.y = capesTab.y + capesTab.getHeight() - 11;
			downloadButton.y = importButton.y;
		} else {
			importButton.x = capesTab.x + capesTab.getWidth() - 11;
			importButton.y = capesTab.y - 13;
			downloadButton.x = importButton.x - 2 - 11;
			downloadButton.y = importButton.y;
		}
		skinsTab.active = this.capesTab;
		capesTab.active = !this.capesTab;
		Runnable addWidgets = () -> {
			clear();
			addDrawableChild(current);
			addDrawableChild(skinsTab);
			addDrawableChild(capesTab);
			addDrawableChild(downloadButton);
			addDrawableChild(importButton);
			addDrawableChild(skinList);
			addDrawableChild(capesList);
			addDrawableChild(back);
		};
		if (cachedProfile != null) {
			initDisplay();
			addWidgets.run();
			return;
		}
		refreshFuture.thenComposeAsync(unused -> Auth.getInstance().getMsApi().getProfile(account))
			.thenAcceptAsync(profile -> {
				cachedProfile = profile;
				initDisplay();
				addWidgets.run();
			}).exceptionally(t -> {
				if (t.getCause() instanceof CancellationException) {
					client.openScreen(parent);
					return null;
				}
				AxolotlClientCommon.getInstance().getLogger().error("Failed to load skins!", t);
				var error = new TranslatableText("skins.error.failed_to_load");
				var errorDesc = new TranslatableText("skins.error.failed_to_load_desc");
				clear();
				addDrawableChild(back);
				class TextWidget extends AbstractButtonWidget {

					public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
						super(x, y, width, height, message);
						active = false;
					}

					@Override
					public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
						drawCenteredText(matrices, textRenderer, getMessage(), x + getWidth() / 2, y + getHeight() / 2 - textRenderer.fontHeight / 2, -1);
					}
				}
				addDrawableChild(new TextWidget(width / 2 - textRenderer.getWidth(error) / 2, height / 2 - textRenderer.fontHeight - 2, textRenderer.getWidth(error), textRenderer.fontHeight, error, textRenderer));
				addDrawableChild(new TextWidget(width / 2 - textRenderer.getWidth(errorDesc) / 2, height / 2 + 1, textRenderer.getWidth(errorDesc), textRenderer.fontHeight, errorDesc, textRenderer));
				return null;
			});
	}

	private void promptForSkinDownload() {
		client.openScreen(new SimpleTextInputScreen(this, new TranslatableText("skins.manage.import.online"), new TranslatableText("skins.manage.import.online.input"), s ->
			UUIDHelper.ensureUuidOpt(s).thenAccept(o -> {
				if (o.isPresent()) {
					AxolotlClientCommon.getInstance().getLogger().info("Downloading skin of {} ({})", s, o.get());
					Auth.getInstance().getMsApi().getTextures(o.get())
						.exceptionally(th -> {
							AxolotlClientCommon.getInstance().getLogger().info("Failed to download skin of {} ({})", s, o.get(), th);
							return null;
						}).thenAccept(t -> {
							if (t == null) {
								Notifications.getInstance().addStatus("skins.notification.title", "skins.notification.import.online.failed_to_download", s);
								return;
							}
							try {
								var bytes = t.skin().join();
								var out = ensureNonexistent(SKINS_DIR.resolve(t.skinKey()));
								Skin.Local.writeMetadata(out, Map.of(Skin.Local.CLASSIC_METADATA_KEY, t.classicModel(), "name", t.name(), "uuid", t.id(), "download_time", Instant.now()));
								Files.write(out, bytes);
								client.execute(this::loadSkinsList);
								Notifications.getInstance().addStatus("skins.notification.title", "skins.notification.import.online.downloaded", t.name());
								AxolotlClientCommon.getInstance().getLogger().info("Downloaded skin of {} ({})", t.name(), o.get());
							} catch (IOException e) {
								AxolotlClientCommon.getInstance().getLogger().warn("Failed to write skin file", e);
								Notifications.getInstance().addStatus("skins.notification.title", "skins.notification.import.online.failed_to_save", t.name());
							}
						});
				} else {
					Notifications.getInstance().addStatus("skins.notification.title", "skins.notification.import.online.not_found", s);
				}
			})));
	}

	private <T extends Drawable & Element> T addDrawableChild(T child) {
		drawables.add(child);
		return addChild(child);
	}

	private void clear() {
		children.clear();
		buttons.clear();
		drawables.clear();
	}

	@Override
	public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
		tooltip = null;
		renderBackground(graphics);
		drawables.forEach(d -> d.render(graphics, mouseX, mouseY, delta));
		drawCenteredText(graphics, textRenderer, getTitle(), width / 2, 33 / 2 - textRenderer.fontHeight / 2, -1);
		if (tooltip != null) {
			renderTooltip(graphics, tooltip, mouseX, mouseY + 20);
		}
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
		capesList.clearEntries0();
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
				widget = createEntry(capesList.getEntryContentsHeight(), deselectCape, new TranslatableText("skins.capes.no_cape"));
			} else {
				var cape = capes.get(i - 1);
				widget = createEntryForCape(current.getSkin(), cape, capesList.getEntryContentsHeight());
			}
			List<AbstractButtonWidget> widgets = new ArrayList<>();
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
		skinList.clearEntries0();
		var profile = cachedProfile;
		int columns = Math.max(2, (width / 2 - 25) / LIST_SKIN_WIDTH);
		List<Skin> skins = new ArrayList<>(profile.skins());
		var hashes = skins.stream().map(Asset::textureKey).collect(Collectors.toSet());
		var defaultSkinHash = Auth.getInstance().getSkinManager().getDefaultSkinHash(account);
		var local = new ArrayList<>(loadLocalSkins());
		var localHashes = local.stream().collect(Collectors.toMap(Asset::textureKey, Function.identity(), (skin, skin2) -> skin));
		local.removeIf(s -> !localHashes.containsValue(s));
		skins.replaceAll(s -> {
			if (s instanceof MSApi.MCProfile.OnlineSkin online) {
				if (localHashes.containsKey(s.textureKey()) && localHashes.get(s.textureKey()) instanceof Skin.Local file) {
					local.remove(localHashes.remove(s.textureKey()));
					return new Skin.Shared(file, online);
				}
			}
			return s;
		});
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
			List<AbstractButtonWidget> widgets = new ArrayList<>();
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

	private Path ensureNonexistent(Path p) {
		if (Files.exists(p)) {
			int counter = 0;
			do {
				counter++;
				p = p.resolveSibling(p.getFileName().toString() + "_" + counter);
			} while (Files.exists(p));
		}
		return p;
	}

	@Override
	public void filesDragged(List<Path> packs) {
		if (packs.isEmpty()) return;

		CompletableFuture<?>[] futs = new CompletableFuture[packs.size()];
		for (int i = 0; i < packs.size(); i++) {
			Path p = packs.get(i);
			futs[i] = CompletableFuture.runAsync(() -> {
				try {
					var target = ensureNonexistent(SKINS_DIR.resolve(p.getFileName()));
					var skin = Auth.getInstance().getSkinManager().read(p, false);
					if (skin != null) {
						Files.write(target, skin.image().join());
					} else {
						AxolotlClientCommon.getInstance().getLogger().info("Skipping dragged file {} because it does not seem to be a valid skin!", p);
						Notifications.getInstance().addStatus("skins.notification.title", "skins.notification.not_copied", p.getFileName());
					}
				} catch (IOException e) {
					AxolotlClientCommon.getInstance().getLogger().warn("Failed to copy skin file: ", e);
				}
			}, client);
		}
		CompletableFuture.allOf(futs).thenRun(this::loadSkinsList);
	}

	private @NotNull Entry createEntryForSkin(Skin skin, int entryHeight) {
		return createEntry(entryHeight, new SkinWidget(LIST_SKIN_WIDTH, LIST_SKIN_HEIGHT, skin, account));
	}

	private @NotNull Entry createEntryForCape(Skin currentSkin, Cape cape, int entryHeight) {
		return createEntry(entryHeight, createWidgetForCape(currentSkin, cape), new LiteralText(cape.alias()));
	}

	private SkinWidget createWidgetForCape(Skin currentSkin, Cape cape) {
		SkinWidget widget2 = new SkinWidget(LIST_SKIN_WIDTH, LIST_SKIN_HEIGHT, currentSkin, cape, account);
		widget2.setRotationY(210);
		return widget2;
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		Auth.getInstance().getSkinManager().releaseAll();
		super.resize(client, width, height);
	}

	@Override
	public void removed() {
		Auth.getInstance().getSkinManager().releaseAll();
		Watcher.close(skinDirWatcher);
	}

	@Override
	public void onClose() {
		client.openScreen(parent);
	}

	private SkinListWidget getCurrentList() {
		return capesTab ? capesList : skinList;
	}

	private class SkinListWidget extends ElementListWidget<Row> {
		public boolean active = true, visible = true;

		public SkinListWidget(MinecraftClient minecraft, int width, int height, int y, int entryHeight) {
			super(minecraft, width, SkinManagementScreen.this.height, y, y + height, entryHeight);
			setRenderHeader(false, 0);
			//setRenderBackground(false);
		}

		@Override
		public int addEntry(Row entry) {
			return super.addEntry(entry);
		}

		@Override
		protected int getScrollbarPositionX() {
			return right - 8;
		}

		@Override
		public int getRowLeft() {
			return left + 3;
		}

		@Override
		public int getRowWidth() {
			if (!(getMaxScroll() > 0)) {
				return width - 4;
			}
			return width - 14;
		}

		private int getMaxScroll() {
			return Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4));
		}

		public int getEntryContentsHeight() {
			return itemHeight - 4;
		}

		public void clearEntries0() {
			super.clearEntries();
		}

		@Override
		public void centerScrollOn(Row entry) {
			super.centerScrollOn(entry);
		}

		@Override
		public boolean mouseScrolled(double mouseX, double mouseY, double amountY) {
			if (!visible) return false;
			return super.mouseScrolled(mouseX, mouseY, amountY);
		}

		@Override
		public boolean isMouseOver(double mouseX, double mouseY) {
			return active && visible && super.isMouseOver(mouseX, mouseY);
		}

		@Override
		public void render(MatrixStack graphics, int mouseX, int mouseY, float delta) {
			if (!visible) return;
			super.render(graphics, mouseX, mouseY, delta);
		}
	}

	private class Row extends ElementListWidget.Entry<Row> {
		private final List<AbstractButtonWidget> widgets;

		public Row(List<AbstractButtonWidget> entries) {
			this.widgets = entries;
		}

		@Override
		public void render(MatrixStack guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {
			int x = left;
			if (widgets.isEmpty()) return;
			int count = widgets.size();
			int padding = ((width - 5 * (count - 1)) / count);
			for (var w : widgets) {
				w.x = x;
				w.y = top;
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
		public void setFocused(@Nullable Element focused) {
			super.setFocused(focused);
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

	private class Entry extends AbstractButtonWidget implements ParentElement {
		private final SkinWidget skinWidget;
		private final @Nullable AbstractButtonWidget label;
		private final List<AbstractButtonWidget> actionButtons = new ArrayList<>();
		private final AbstractButtonWidget equipButton;
		private boolean equipping;
		private long equippingStart;
		@Nullable
		private Element focused;
		private boolean dragging;

		public Entry(int height, SkinWidget widget, @Nullable Text label) {
			super(0, 0, widget.getWidth(), height, LiteralText.EMPTY);
			widget.setWidth(getWidth() - 4);
			var asset = widget.getFocusedAsset();
			if (asset instanceof Skin skin) {
				var wideSprite = new Identifier("axolotlclient", "textures/gui/sprites/wide.png");
				var slimSprite = new Identifier("axolotlclient", "textures/gui/sprites/slim.png");
				var slimText = new TranslatableText("skins.manage.variant.classic");
				var wideText = new TranslatableText("skins.manage.variant.slim");
				actionButtons.add(new SpriteButton(skin.classicVariant() ? wideText : slimText, btn -> {
					var self = (SpriteButton) btn;
					skin.classicVariant(!skin.classicVariant());
					self.sprite = skin.classicVariant() ? slimSprite : wideSprite;
					self.setMessage(skin.classicVariant() ? wideText : slimText);
				}, skin.classicVariant() ? slimSprite : wideSprite));
			}
			if (asset != null) {
				if (asset.isLocal()) {
					this.actionButtons.add(new SpriteButton(new TranslatableText("skins.manage.delete"), btn -> {
						btn.active = false;
						client.openScreen(new ConfirmScreen(confirmed -> {
							client.openScreen(SkinManagementScreen.this);
							if (confirmed) {
								try {
									Files.delete(asset.file());
									Skin.Local.deleteMetadata(asset.file());
									refreshCurrentList();
								} catch (IOException e) {
									AxolotlClientCommon.getInstance().getLogger().warn("Failed to delete: ", e);
								}
							}
							btn.active = true;
						}, new TranslatableText("skins.manage.delete.confirm"), (Text) (asset.active() ?
							new TranslatableText("skins.manage.delete.confirm.desc_active") :
							new TranslatableText("skins.manage.delete.confirm.desc")
						).br$color(Colors.RED.toInt())));
					}, new Identifier("axolotlclient", "textures/gui/sprites/delete.png")));
				}
				if (asset.supportsDownload() && !asset.isLocal()) {
					this.actionButtons.add(new SpriteButton(new TranslatableText("skins.manage.download"), btn -> {
						btn.active = false;
						download(asset).thenRun(() -> btn.active = true);
					}, new Identifier("axolotlclient", "textures/gui/sprites/download.png")));
				}
			}
			if (label != null) {
				this.label = new AbstractButtonWidget(0, 0, widget.getWidth(), 16, label) {
					@Override
					public void renderButton(MatrixStack guiGraphics, int mouseX, int mouseY, float partialTick) {
						DrawUtil.drawScrollableText(guiGraphics, textRenderer, getMessage(), x + 2, y, x + width - 2, y + height, -1);
					}
				};
				this.label.active = false;
			} else {
				this.label = null;
			}
			this.equipButton = new ButtonWidget(0, 0, widget.getWidth(), 20, new TranslatableText(
				widget.isEquipped() ? "skins.manage.equipped" : "skins.manage.equip"),
				btn -> {
					equippingStart = Util.getMeasuringTimeMs();
					equipping = true;
					btn.setMessage(TEXT_EQUIPPING);
					btn.active = false;
					Consumer<CompletableFuture<MSApi.MCProfile>> consumer = f -> f.thenAcceptAsync(p -> {
						cachedProfile = p;
						refreshCurrentList();
					}).exceptionally(t -> {
						AxolotlClientCommon.getInstance().getLogger().warn("Failed to equip asset!", t);
						equipping = false;
						return null;
					});
					if (asset instanceof Skin && !current.getSkin().isLocal()) {
						client.openScreen(new ConfirmScreen(confirmed -> {
							if (confirmed) {
								consumer.accept(download(current.getSkin()).thenCompose(a -> widget.equip()));
							} else {
								consumer.accept(widget.equip());
							}
						}, new TranslatableText("skins.manage.equip.confirm"), new TranslatableText("skins.manage.equip.download_current")));
					} else {
						consumer.accept(widget.equip());
					}
				});
			this.equipButton.active = !widget.isEquipped();
			this.skinWidget = widget;
		}

		private @NotNull CompletableFuture<?> download(Asset asset) {
			return asset.image().thenAcceptAsync(b -> {
				try {
					var out = SKINS_DIR.resolve(asset.textureKey());
					Files.createDirectories(out.getParent());
					Files.write(out, b);
					if (asset instanceof Skin skin) {
						Skin.Local.writeMetadata(out, Map.of(Skin.Local.CLASSIC_METADATA_KEY, skin.classicVariant()));
					}
				} catch (IOException e) {
					AxolotlClientCommon.getInstance().getLogger().warn("Failed to download: ", e);
				}
				refreshCurrentList();
			});
		}

		@Override
		public final boolean isDragging() {
			return this.dragging;
		}

		@Override
		public final void setDragging(boolean dragging) {
			this.dragging = dragging;
		}

		@Nullable
		@Override
		public Element getFocused() {
			return this.focused;
		}

		@Override
		public void setFocused(@Nullable Element child) {
			this.focused = child;
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			return ParentElement.super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return ParentElement.super.mouseReleased(mouseX, mouseY, button);
		}

		@Override
		public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
			return ParentElement.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
		}

		@Override
		public boolean isFocused() {
			return getFocused() != null;
		}

		@Override
		public void setFocused(boolean focused) {

		}

		@Override
		public @NotNull List<? extends Element> children() {
			return Stream.concat(actionButtons.stream(), Stream.of(skinWidget, label, equipButton)).filter(Objects::nonNull).toList();
		}

		private float applyEasing(float x) {
			return x * x * x;
		}

		@Override
		public void renderButton(MatrixStack guiGraphics, int mouseX, int mouseY, float partialTick) {
			int y = this.y + 4;
			int x = this.x + 2;
			skinWidget.setPosition(x, y);
			skinWidget.setWidth(getWidth() - 4);
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
				GradientHoleRectangleRenderState.render(guiGraphics, this.x + 2, this.y + 2, this.x + getWidth() - 2,
					skinWidget.getY() + skinWidget.getHeight() + 2,
					gradientWidth,
					equipping ? 0xFFFF0088 : ClientColors.SELECTOR_GREEN.toInt(), 0);
			}
			skinWidget.render(guiGraphics, mouseX, mouseY, partialTick);
			int actionButtonY = this.y + 2;
			for (var button : actionButtons) {
				button.x = skinWidget.getX() + skinWidget.getWidth() - button.getWidth();
				button.y = actionButtonY;
				if (isHovered() || button.isHovered()) {
					button.render(guiGraphics, mouseX, mouseY, partialTick);
				}
				actionButtonY += button.getHeight() + 2;
			}
			if (label != null) {
				label.x = x;
				label.y = skinWidget.getY() + skinWidget.getHeight() + 6;
				label.render(guiGraphics, mouseX, mouseY, partialTick);
				label.setWidth(getWidth() - 4);
				equipButton.x = x;
				equipButton.y = label.y + label.getHeight() + 2;
			} else {
				equipButton.x = x;
				equipButton.y = skinWidget.getY() + skinWidget.getHeight() + 4;
			}
			equipButton.setWidth(getWidth() - 4);
			equipButton.render(guiGraphics, mouseX, mouseY, partialTick);

			if (isHovered()) {
				guiGraphics.br$outlineRect(this.x, this.y, getWidth(), getHeight(), -1);
			}
		}

		private static class GradientHoleRectangleRenderState {

			public static void render(MatrixStack graphics, int x0, int y0, int x1, int y1, float gradientWidth, int col1, int col2) {
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.disableAlphaTest();
				RenderSystem.defaultBlendFunc();
				RenderSystem.shadeModel(7425);
				var tessellator = Tessellator.getInstance();
				var vertexConsumer = tessellator.getBuffer();
				float z = 0;
				//top
				var pose = graphics.peek().getModel();
				vertexConsumer.begin(7, VertexFormats.POSITION_COLOR);
				vertexConsumer.vertex(pose, x0, y0, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x0 + gradientWidth, y0 + gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x1 - gradientWidth, y0 + gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x1, y0, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				//left
				vertexConsumer.vertex(pose, x0, y1, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x0 + gradientWidth, y1 - gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x0 + gradientWidth, y0 + gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x0, y0, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				//bottom
				vertexConsumer.vertex(pose, x1, y1, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x1 - gradientWidth, y1 - gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x0 + gradientWidth, y1 - gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x0, y1, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				//right
				vertexConsumer.vertex(pose, x1, y0, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x1 - gradientWidth, y0 + gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x1 - gradientWidth, y1 - gradientWidth, z).color(col2 >> 16 & 255, col2 >> 8 & 255, col2 & 255, col2 >> 24 & 255).next();
				vertexConsumer.vertex(pose, x1, y1, z).color(col1 >> 16 & 255, col1 >> 8 & 255, col1 & 255, col1 >> 24 & 255).next();
				tessellator.draw();
				RenderSystem.shadeModel(7424);
				RenderSystem.disableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
			}
		}
	}

	private class SpriteButton extends ButtonWidget {
		private Identifier sprite;

		public SpriteButton(Text message, PressAction onPress, Identifier sprite) {
			super(0, 0, 11, 11, message, onPress);
			this.sprite = sprite;
		}

		@Override
		public void renderButton(MatrixStack graphics, int mouseX, int mouseY, float delta) {
			Identifier tex = ButtonWidgetTextures.get(getYImage(hovered));
			DrawUtil.blitSprite(tex, x, y, width, height, new DrawUtil.NineSlice(200, 20, 3));
			client.getTextureManager().bindTexture(sprite);
			drawTexture(graphics, x + 2, y + 2, 0, 0, 7, 7, 7, 7);
			if (this.isHovered()) {
				tooltip = getMessage();
			}
		}
	}
}
