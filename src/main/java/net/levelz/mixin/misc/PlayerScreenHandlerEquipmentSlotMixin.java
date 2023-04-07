package net.levelz.mixin.misc;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Mixin(targets = "net/minecraft/screen/PlayerScreenHandler$1")
public class PlayerScreenHandlerEquipmentSlotMixin {
    @Shadow @Final PlayerEntity field_39410;

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void levelz$preventEquipIfLevelTooLow(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final boolean result;
        if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registry.ITEM.getId(stack.getItem()).toString())) {
            result = PlayerStatsManager.playerLevelisHighEnough(this.field_39410, LevelLists.customItemList, Registry.ITEM.getId(stack.getItem()).toString(), true);
        } else if (stack.getItem() instanceof ArmorItem armor) {
            result = PlayerStatsManager.playerLevelisHighEnough(this.field_39410, LevelLists.armorList, armor.getMaterial().getName().toLowerCase(Locale.ROOT), true);
        } else if (stack.getItem() instanceof ElytraItem) {
            result = PlayerStatsManager.playerLevelisHighEnough(this.field_39410, LevelLists.elytraList, null, true);
        } else {
            result = true;
        }

        if (!result) {
            cir.setReturnValue(false);
        }
    }
}
