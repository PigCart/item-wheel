package pigcart.itemwheel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BaseEntityBlock;

import java.util.ArrayList;

public class Wheel {
    public ArrayList<Display> displayEntities = new ArrayList<>();
    public float spinPercent = 0f;
    public final ServerLevel serverLevel;
    public final BlockPos blockPos;
    public final BaseEntityBlock block;
    public final int chosenItemIndex;

    public Wheel(ServerLevel serverLevel, BlockPos blockPos, BaseEntityBlock block, int chosenItemIndex) {
        this.serverLevel = serverLevel;
        this.blockPos = blockPos;
        this.block = block;
        this.chosenItemIndex = chosenItemIndex;
    }

    public void remove() {
        ItemWheel.activeWheels.remove(this);
        displayEntities.forEach((entity) -> {
            entity.remove(Entity.RemovalReason.DISCARDED);
        });
    }

    public void playSound() {
        serverLevel.playSound(null, blockPos, SoundEvents.DISPENSER_FAIL, SoundSource.BLOCKS, 1, 1.3f);
    }

    public void tick() {
        spinPercent = spinPercent + 0.01f;
        if (spinPercent < 1) {
            displayEntities.forEach((entity) -> {
                ((DisplayAccess) entity).itemWheel_update(EaseOut(spinPercent));
            });
        } else {
            serverLevel.scheduleTick(blockPos, block, 20);
            displayEntities.forEach((entity) -> {
                ((DisplayAccess) entity).itemWheel_finish();
            });
        }
    }
    public float flip(float x) {
        return 1 - x;
    }
    public float EaseOut(float t) {
        return flip(Mth.square(flip(t)));
    }
}
