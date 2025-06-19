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

package io.github.axolotlclient.modules.hud.gui0.hud.item;

import io.github.axolotlclient.AxolotlClientConfig.api.options.Option;
import io.github.axolotlclient.AxolotlClientConfig.api.util.Colors;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.ColorOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.EnumOption;
import io.github.axolotlclient.bridge.item.AxoEnchants;
import io.github.axolotlclient.bridge.item.AxoItemStack;
import io.github.axolotlclient.bridge.item.AxoItems;
import io.github.axolotlclient.bridge.render.AxoRenderContext;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.modules.hud.gui0.component.DynamicallyPositionable;
import io.github.axolotlclient.modules.hud.gui0.entry.TextHudEntry;
import io.github.axolotlclient.modules.hud.gui0.layout.AnchorPoint;
import io.github.axolotlclient.modules.hud.util.DrawPosition;
import io.github.axolotlclient.util.ItemUtil;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 *
 * @license GPL-3.0
 */
public class ArmorHud extends TextHudEntry implements DynamicallyPositionable {

	public static final AxoIdentifier ID = AxoIdentifier.of("kronhud", "armorhud");
	private static final AxoItemStack PLACEHOLDER_MAIN_HAND = AxoItemStack.of(AxoItems.IRON_SWORD);
	private static final List<AxoItemStack> PLACEHOLDER_GEAR = List.of(
		AxoItemStack.of(AxoItems.IRON_BOOTS),
		AxoItemStack.of(AxoItems.IRON_LEGGINGS),
		AxoItemStack.of(AxoItems.IRON_CHESTPLATE),
		AxoItemStack.of(AxoItems.IRON_HELMET)
	);

	protected final BooleanOption showProtLvl = new BooleanOption("showProtectionLevel", false);
	private final BooleanOption showDurabilityNumber = new BooleanOption("show_durability_num", false);
	private final BooleanOption showMaxDurabilityNumber = new BooleanOption("show_max_durability_num", false);
	private final BooleanOption customDurabilityNumColor = new BooleanOption("armorhud.custom_durability_num_color", false);
	private final ColorOption durabilityNumColor = new ColorOption("armorhud.durability_num_color", Colors.WHITE);
	private final EnumOption<MainHandItemPosition> mainHandItemPosition = new EnumOption<>("armorhud.main_hand_item_position", MainHandItemPosition.class, MainHandItemPosition.BOTTOM);

	private final EnumOption<AnchorPoint> anchor = new EnumOption<>("anchorpoint", AnchorPoint.class,
		AnchorPoint.TOP_RIGHT);

	public ArmorHud() {
		super(20, 100, true);
	}

	@Override
	public void renderComponent(AxoRenderContext graphics, float delta) {
		final var player = client.br$getPlayer();
		if(player == null) {
			return;
		}
		renderInternal(graphics, player.br$getInventory().br$getMainHand(), player.br$getInventory().br$getArmor());
	}

	@Override
	public void renderPlaceholderComponent(AxoRenderContext graphics, float delta) {
		renderInternal(graphics, PLACEHOLDER_MAIN_HAND, PLACEHOLDER_GEAR);
	}

	private void renderInternal(AxoRenderContext graphics, AxoItemStack mainHand, List<? extends AxoItemStack> armorStacks) {
		int width = 20;
		int height = 100;
		boolean boundsChanged = false;
		boolean showDurability = showDurabilityNumber.get();
		boolean showMaxDurability = showMaxDurabilityNumber.get();

		int labelWidth = (showDurability || showMaxDurability) ?
			Stream.concat(Stream.of(mainHand), armorStacks.stream())
				.map(stack -> {
					String text = showDurability && showMaxDurability
						? (stack.br$getMaxDamage() - stack.br$getDamage()) + "/" + stack.br$getMaxDamage()
						: String.valueOf(showDurability ? stack.br$getMaxDamage() - stack.br$getDamage()
						: stack.br$getMaxDamage());
					return graphics.br$getFont().br$getWidth(text) + 2;
				}).mapToInt(Integer::intValue).max().orElse(0) : 0;

		width += labelWidth;
		if (width != getWidth()) {
			setWidth(width);
			boundsChanged = true;
		}

		DrawPosition pos = getPos();
		MainHandItemPosition mhPos = mainHandItemPosition.get();

		if (mhPos == MainHandItemPosition.DISABLED) {
			height -= 20;
		}

		if (height != getHeight()) {
			setHeight(height);
			boundsChanged = true;
		}
		if (boundsChanged) {
			onBoundsUpdate();
		}

		int lastY = 2 + (height - 20);

		if (mhPos == MainHandItemPosition.BOTTOM) {
			renderMainItem(graphics, mainHand, pos.x() + 2, pos.y() + lastY, labelWidth);
			lastY -= 20;
		}

		for (AxoItemStack stack : armorStacks) {
			String label = null;

			if (showProtLvl.get() && stack.br$hasEnchantment(AxoEnchants.PROTECTION)) {
				label = String.valueOf(stack.br$getEnchantment(AxoEnchants.PROTECTION));
			}

			renderItem(graphics, stack, pos.x() + 2, pos.y() + lastY, labelWidth, label);
			lastY -= 20;
		}

		if (mhPos == MainHandItemPosition.TOP) {
			renderMainItem(graphics, mainHand, pos.x() + 2, pos.y() + lastY, labelWidth);
		}
	}

	public void renderMainItem(AxoRenderContext graphics, AxoItemStack stack, int x, int y, int offset) {
		renderDurabilityNumber(graphics, stack, x, y);
		x += offset;
		String total = String.valueOf(ItemUtil.getTotal(client, stack.br$getItem()));
		if (total.equals("1")) {
			total = null;
		}
		graphics.br$renderGuiItemModel(stack, x, y);
		graphics.br$renderGuiItemOverlay(stack, x, y, total);
	}

	public void renderItem(AxoRenderContext graphics, AxoItemStack stack, int x, int y, int offset, String labelOverride) {
		renderDurabilityNumber(graphics, stack, x, y);
		x += offset;
		graphics.br$renderGuiItemModel(stack, x, y);
		graphics.br$renderGuiItemOverlay(stack, x, y, labelOverride);
	}

	private void renderDurabilityNumber(AxoRenderContext graphics, AxoItemStack stack, int x, int y) {
		boolean showDurability = showDurabilityNumber.get();
		boolean showMaxDurability = showMaxDurabilityNumber.get();
		if (stack.br$isEmpty() || !(showMaxDurability || showDurability) || stack.br$getMaxDamage() == 0) {
			return;
		}
		String text = showDurability && showMaxDurability ? (stack.br$getMaxDamage() - stack.br$getDamage()) + "/" + stack.br$getMaxDamage() : String.valueOf((showDurability ? stack.br$getMaxDamage() - stack.br$getDamage() : stack.br$getMaxDamage()));
		int textY = y + 10 - graphics.br$getFont().br$getFontHeight() / 2;
		graphics.br$drawString(text, x, textY, customDurabilityNumColor.get() ? durabilityNumColor.get().toInt() : stack.br$getBarColor(), true);
	}

	@Override
	public AxoIdentifier getId() {
		return ID;
	}

	@Override
	public List<Option<?>> getConfigurationOptions() {
		List<Option<?>> options = super.getConfigurationOptions();
		options.add(showProtLvl);
		options.add(showDurabilityNumber);
		options.add(showMaxDurabilityNumber);
		options.add(customDurabilityNumColor);
		options.add(durabilityNumColor);
		options.add(anchor);
		options.add(mainHandItemPosition);
		return options;
	}

	public AnchorPoint getAnchor() {
		return anchor.get();
	}

	private enum MainHandItemPosition {
		BOTTOM,
		TOP,
		DISABLED,
		;

		@Override
		public String toString() {
			return "armorhud.main_hand_item_position."+super.toString().toLowerCase(Locale.ROOT);
		}
	}
}
