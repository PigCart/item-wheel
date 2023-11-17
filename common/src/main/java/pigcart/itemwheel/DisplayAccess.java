package pigcart.itemwheel;

import net.minecraft.world.item.ItemStack;

public interface DisplayAccess {
    void itemWheel_update(float spinPercent);
    void itemWheel_finish();
    void itemWheel_setInitialData(float angle, float finalAngle, float scale, float offset, boolean isChosenItem, ItemStack itemStack, Wheel wheel);
}
