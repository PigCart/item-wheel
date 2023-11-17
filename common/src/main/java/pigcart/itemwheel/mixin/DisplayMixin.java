package pigcart.itemwheel.mixin;

import com.mojang.math.Transformation;
import net.minecraft.util.Brightness;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import pigcart.itemwheel.DisplayAccess;
import pigcart.itemwheel.ItemWheel;
import pigcart.itemwheel.Wheel;

@Mixin(Display.class)
public abstract class DisplayMixin implements DisplayAccess {

    @Shadow protected abstract void setBrightnessOverride(Brightness arg);

    @Shadow protected abstract void setTransformation(Transformation arg);

    @Shadow protected abstract void setTransformationInterpolationDelay(int i);

    @Shadow protected abstract void setTransformationInterpolationDuration(int i);

    private Wheel itemWheel_wheel;
    private float itemWheel_startAngle;
    private float itemWheel_endAngle;
    private float itemWheel_scale;
    private float itemWheel_offset;
    private float itemWheel_prevRadians = 0;
    public boolean itemWheel_isChosenItem;

    public void itemWheel_update(float spinPercent) {
        float angle = Mth.lerp(spinPercent, itemWheel_startAngle, itemWheel_endAngle);
        this.setTransformationInterpolationDelay(0);
        this.setTransformationInterpolationDuration(ItemWheel.updateFrequency);
        Vector3f translation = new Vector3f((float)Math.sin(-angle), (float)Math.cos(-angle), itemWheel_offset);
        Quaternionf leftRotation = new Quaternionf(new AxisAngle4d(angle, 0, 0, 1));
        Vector3f scale = new Vector3f(itemWheel_scale);
        Quaternionf rightRotation = new Quaternionf();
        this.setTransformation(new Transformation(translation,leftRotation,scale,rightRotation));
        if (angle % Mth.TWO_PI < itemWheel_prevRadians) {
            itemWheel_wheel.playSound();
        }
        itemWheel_prevRadians = angle % Mth.TWO_PI;
    }

    public void itemWheel_finish() {
        if (itemWheel_isChosenItem) {
            this.itemWheel_isChosenItem = false;
            ((Display)(Object)this).setGlowingTag(true);
        }
    }

    public void itemWheel_setInitialData(float angle, float finalAngle, float scale, float offset, boolean isChosenItem, ItemStack itemStack, Wheel wheel) {
        itemWheel_startAngle = angle;
        itemWheel_endAngle = finalAngle;
        itemWheel_scale = scale;
        itemWheel_offset = offset;
        itemWheel_isChosenItem = isChosenItem;
        itemWheel_wheel = wheel;
        Vector3f translation = new Vector3f((float)Math.sin(-angle), (float)Math.cos(-angle), offset);
        Quaternionf leftRotation = new Quaternionf(new AxisAngle4d(angle, 0, 0, 1));
        Vector3f scale2 = new Vector3f(0);
        Quaternionf rightRotation = new Quaternionf();
        this.setTransformation(new Transformation(translation,leftRotation,scale2,rightRotation));
        this.setTransformationInterpolationDelay(0);
        this.setTransformationInterpolationDuration(ItemWheel.updateFrequency);
        this.setBrightnessOverride(Brightness.FULL_BRIGHT);
        if (((Display)(Object)this) instanceof Display.ItemDisplay) {
            ((Display.ItemDisplay)(Object)this).getSlot(0).set(itemStack);
        }
    }
}
