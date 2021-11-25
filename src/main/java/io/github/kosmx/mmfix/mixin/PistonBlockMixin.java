package io.github.kosmx.mmfix.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;

@Mixin(PistonExtensionBlock.class)
public class PistonBlockMixin extends BlockWithEntity {

    protected PistonBlockMixin(Settings settings) {
        super(settings);
    }


    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        var block = world.getBlockEntity(hit.getBlockPos());
        if(block instanceof PistonBlockEntity pistonEntity){
            pistonEntity.getPushedBlock().onProjectileHit(world, state, hit, projectile);
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        var block = world.getBlockEntity(pos);
        if(block instanceof PistonBlockEntity pistonEntity){
            pistonEntity.getPushedBlock().getBlock().onDestroyedByExplosion(world, pos, explosion);
        }
    }

    @Inject(method = "getDroppedStacks", at = @At("HEAD"), cancellable = true)
    private void getDroppedStackModified(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir){
        if(builder.getNullable(LootContextParameters.EXPLOSION_RADIUS) != null || builder.getNullable(LootContextParameters.THIS_ENTITY) instanceof TntEntity tnt){
            BlockEntity thisEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
            if(thisEntity instanceof PistonBlockEntity pistonEntity){
                if(!pistonEntity.getPushedBlock().getBlock().shouldDropItemsOnExplosion(null)){
                    cir.setReturnValue(Collections.emptyList());
                    cir.cancel();
                }
            }
        }
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
