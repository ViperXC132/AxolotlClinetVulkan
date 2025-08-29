package io.github.axolotlclient.modules.auth.skin;

import java.util.concurrent.CompletableFuture;

import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.mixin.GuiGraphicsAccessor;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.auth.MSApi;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class SkinWidget extends AbstractWidget {
	private static final float MODEL_HEIGHT = 2.125F;
	private static final float FIT_SCALE = 0.97F;
	private static final float ROTATION_SENSITIVITY = 2.5F;
	private static final float DEFAULT_ROTATION_X = -5.0F;
	private static final float DEFAULT_ROTATION_Y = 30.0F;
	private static final float ROTATION_X_LIMIT = 50.0F;
	private float rotationX = DEFAULT_ROTATION_X;
	@Setter
	private float rotationY = DEFAULT_ROTATION_Y;
	@Getter
	@Setter
	private Skin skin;
	@Getter
	@Setter
	private Cape cape;
	private final Account owner;
	private boolean noCape, noCapeActive;
	private boolean darkenIfEquipped;

	public SkinWidget(int width, int height, Skin skin, @Nullable Cape cape, Account owner) {
		super(0, 0, width, height, CommonComponents.EMPTY);
		this.skin = skin;
		this.cape = cape;
		this.owner = owner;
	}

	public SkinWidget(int width, int height, Skin skin, Account owner) {
		this(width, height, skin, null, owner);
	}

	public void noCape(boolean noCapeActive) {
		noCape = true;
		this.noCapeActive = noCapeActive;
	}

	public SkinWidget darkenIfEquipped() {
		darkenIfEquipped = true;
		return this;
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		var minecraft = Minecraft.getInstance();

		float scale = FIT_SCALE * this.getHeight() / MODEL_HEIGHT;
		float pivotY = -1.0625F;

		AxoIdentifier skinRl;
		int col;
		boolean classic;
		SkinManager skinManager = Auth.getInstance().getSkinManager();
		CompletableFuture<AxoIdentifier> loader = skin == null ? null : skinManager.loadSkin(skin, owner);
		if (loader != null && loader.isDone()) {
			skinRl = loader.join();
			col = darkenIfEquipped && isEquipped() ? ARGB.setBrightness(-1, 0.4f) : -1;
			classic = skin.isClassicVariant();
		} else {
			col = ARGB.setBrightness(-1, 0.6f);
			var skin = DefaultPlayerSkin.get(UUIDHelper.fromUndashed(owner.getUuid()));
			classic = skin.model() == PlayerSkin.Model.WIDE;
			skinRl = skin.texture();
		}
		var capeRl = cape == null ? null : skinManager.loadCape(cape);

		var renderer = SkinRenderer.getOrCreate(minecraft.renderBuffers().bufferSource(), minecraft, "" + hashCode());
		((GuiGraphicsAccessor) guiGraphics).getGuiRenderState()
			.submitPicturesInPictureState(
				new SkinRenderState(classic, (ResourceLocation) skinRl, (ResourceLocation) capeRl, this.rotationX, this.rotationY, pivotY, this.getX(), this.getY(), this.getRight(), this.getBottom(), scale, guiGraphics.scissorStack.peek(), renderer, col));
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		this.rotationX = Mth.clamp(this.rotationX - (float) dragY * ROTATION_SENSITIVITY, -ROTATION_X_LIMIT, ROTATION_X_LIMIT);
		this.rotationY += (float) dragX * ROTATION_SENSITIVITY;
	}

	@Override
	public void playDownSound(SoundManager handler) {
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Nullable
	@Override
	public ComponentPath nextFocusPath(FocusNavigationEvent event) {
		return null;
	}

	public boolean isEquipped() {
		return noCape ? noCapeActive : (cape != null ? cape.isActive() : skin == null || skin.isActive());
	}

	public CompletableFuture<MSApi.MCProfile> equip() {
		var msApi = Auth.getInstance().getMsApi();
		if (noCape) {
			return msApi.hideCape(owner);
		}
		if (cape != null) {
			return cape.equip(msApi, owner);
		}
		if (skin != null) {
			return skin.equip(msApi, owner);
		}
		return msApi.resetSkin(owner);
	}
}
