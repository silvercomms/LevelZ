package net.levelz.mixin.misc;

import java.util.List;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.levelz.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.util.registry.Registry;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

    @Nullable
    @Shadow
    private SmithingRecipe currentRecipe;

    public SmithingScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void updateResultMixin(CallbackInfo info, List<SmithingRecipe> list) {
        if (PlayerStatsManager.listContainsItemOrBlock(player, Registry.ITEM.getRawId(list.get(0).getOutput().getItem()), 3) && !player.isCreative()) {
            this.currentRecipe = null;
            this.output.setStack(0, ItemStack.EMPTY);
            player.sendMessage(Text.literal("You need a higher skill level to do this!").formatted(Formatting.RED), false);
            info.cancel();
        }
    }
}
