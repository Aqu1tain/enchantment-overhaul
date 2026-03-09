package com.akitain.enchantmentoverhaul.mixin;

import com.akitain.enchantmentoverhaul.enchant.BookshelfScanner;
import com.akitain.enchantmentoverhaul.enchant.CatalogueData;
import com.akitain.enchantmentoverhaul.enchant.CatalogueScreenHandler;
import com.akitain.enchantmentoverhaul.enchant.ModAdvancements;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void openCatalogue(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient()) {
            cir.setReturnValue(ActionResult.SUCCESS);
            return;
        }

        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        BookshelfScanner.ScanResult scan = BookshelfScanner.scan(world, pos);
        List<Identifier> unlocked = scan.unlocked().stream()
                .map(RegistryKey::getValue)
                .toList();
        int bookshelves = scan.normalBookshelves();
        ModAdvancements.checkEndgameBooks(serverPlayer, scan.unlocked());
        Text title = world.getBlockEntity(pos) instanceof EnchantingTableBlockEntity entity
                ? entity.getDisplayName()
                : Text.translatable("container.enchant");

        serverPlayer.openHandledScreen(new ExtendedScreenHandlerFactory<CatalogueData>() {
            @Override
            public CatalogueData getScreenOpeningData(ServerPlayerEntity p) {
                return new CatalogueData(unlocked, bookshelves);
            }

            @Override
            public Text getDisplayName() {
                return title;
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity p) {
                return new CatalogueScreenHandler(syncId, inv, ScreenHandlerContext.create(world, pos), unlocked, bookshelves);
            }
        });

        cir.setReturnValue(ActionResult.SUCCESS);
    }
}
