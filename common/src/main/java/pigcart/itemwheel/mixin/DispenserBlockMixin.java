package pigcart.itemwheel.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pigcart.itemwheel.ItemWheel;
import pigcart.itemwheel.Wheel;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Shadow @Final public static DirectionProperty FACING;

    @Inject(method = "tick", at = @At(value = "HEAD"), cancellable = true)
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        boolean shouldDispense = ItemWheel.attemptSpawnWheel(serverLevel, blockPos, FACING, (DispenserBlock)(Object)this);
        if (!shouldDispense) {
            ci.cancel();
        }
    }

    @Inject(method = "onRemove", at = @At(value = "HEAD"))
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl, CallbackInfo ci) {
        if (!blockState.is(blockState2.getBlock())) {
            Wheel wheel = ItemWheel.getWheel(blockPos);
            if (wheel != null) {
                wheel.remove();
            }
        }
    }
}
