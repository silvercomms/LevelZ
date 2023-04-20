package net.levelz.mixin.misc;

import java.util.ArrayList;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Shadow
    private ItemStack cursorStack;

    @Nullable
    private ScreenHandlerSyncHandler syncHandler;

    @Nullable
    @Shadow
    @Final
    private ScreenHandlerType<?> type;

    @Shadow
    @Final
    @Mutable
    public DefaultedList<Slot> slots = DefaultedList.of();

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setCursorStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 2, shift = Shift.BEFORE), cancellable = true)
    private void internalOnSlotClickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !cursorStack.isEmpty()) {
            // Slot 3: top; slot 0-2: bottom slots
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(cursorStack.getItem()), 2) && !player.isCreative()) {
                player.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/Slot;setStack(Lnet/minecraft/item/ItemStack;)V", ordinal = 2, shift = Shift.BEFORE), cancellable = true)
    private void internalOnSlotClickSwitchMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !cursorStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(cursorStack.getItem()), 2) && !player.isCreative()) {
                player.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", ordinal = 2), cancellable = true)
    private void internalOnSlotSwitchSetMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getStack(button);
        if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !itemStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(itemStack.getItem()), 2) && !player.isCreative()) {
                player.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/slot/Slot;getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", ordinal = 3), cancellable = true)
    private void internalOnSlotSwitchSwitchMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemStack = playerInventory.getStack(button);
        if (type == ScreenHandlerType.BREWING_STAND && slotIndex == 3 && !itemStack.isEmpty()) {
            if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(itemStack.getItem()), 2) && !player.isCreative()) {
                player.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
                info.cancel();
            }
        }
    }

    @Inject(method = "internalOnSlotClick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;", ordinal = 1), cancellable = true)
    private void internalOnSlotClickQuickMixin(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo info) {
        ItemStack itemStack = this.slots.get(slotIndex).getStack();
        if (!LevelLists.customItemList.isEmpty() && LevelLists.customItemList.contains(Registry.ITEM.getId(itemStack.getItem()).toString())
                && !PlayerStatsManager.playerLevelisHighEnough(player, LevelLists.customItemList, Registry.ITEM.getId(itemStack.getItem()).toString(), true)) {
            player.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
            info.cancel();
        }
    }
}
