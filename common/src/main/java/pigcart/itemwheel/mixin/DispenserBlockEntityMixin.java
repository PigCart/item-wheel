package pigcart.itemwheel.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pigcart.itemwheel.ItemWheel;
import pigcart.itemwheel.Wheel;

@Mixin(DispenserBlockEntity.class)
public class DispenserBlockEntityMixin {

    @Inject(method = "getRandomSlot", at = @At(value = "TAIL"), cancellable = true)
    public void getRandomSlot(RandomSource randomSource, CallbackInfoReturnable<Integer> cir) {
        Wheel wheel = ItemWheel.getWheel(((DispenserBlockEntity)(Object)this).getBlockPos());
        if (wheel != null) {
            wheel.remove();
            cir.setReturnValue(wheel.chosenItemIndex);
            cir.cancel();
        }
    }
}
