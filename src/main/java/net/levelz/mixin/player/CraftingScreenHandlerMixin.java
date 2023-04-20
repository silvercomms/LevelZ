package net.levelz.mixin.player;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private static void updateResultMixin(ScreenHandler handler, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory, CallbackInfo info,
            ServerPlayerEntity serverPlayerEntity, ItemStack itemStack) {
        if (!player.isCreative() && PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(itemStack.getItem()), 4)) {
            resultInventory.setStack(0, ItemStack.EMPTY);
            handler.setPreviousTrackedSlot(0, ItemStack.EMPTY);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, ItemStack.EMPTY));
            serverPlayerEntity.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
            info.cancel();
        }
    }

}
