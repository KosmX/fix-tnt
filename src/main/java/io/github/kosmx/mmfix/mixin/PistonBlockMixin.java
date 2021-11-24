package io.github.kosmx.mmfix.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.PistonExtensionBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
