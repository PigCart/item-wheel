package pigcart.itemwheel;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSourceImpl;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import java.util.ArrayList;
import java.util.Random;

public class ItemWheel {
    public static final String MOD_ID = "itemwheel";
    public static ArrayList<Wheel> activeWheels = new ArrayList<>();
    public static final ItemStack[] banners = {Items.RED_BANNER.getDefaultInstance(),Items.YELLOW_BANNER.getDefaultInstance(),Items.LIME_BANNER.getDefaultInstance(),Items.BLUE_BANNER.getDefaultInstance()};
    public static final int wheelRotations = 8;

    public static void init() {
        TickEvent.SERVER_POST.register(ItemWheel::tick);
        LifecycleEvent.SERVER_STOPPING.register(ItemWheel::RemoveWheelsOnServerStop);
    }

    private static void RemoveWheelsOnServerStop(MinecraftServer ignored) {
        if (!activeWheels.isEmpty()) {
            for (int i = 0; i < activeWheels.size(); i++) {
                activeWheels.get(i).remove();
            }
        }
    }

    public static final int updateFrequency = 2;
    private static int ticks = 0;
    private static void tick(MinecraftServer ignored) {
        ticks++;
        if (ticks == updateFrequency) {
            ticks = 0;
            if (!activeWheels.isEmpty()) {
                for (int i = 0; i < activeWheels.size(); i++) {
                    activeWheels.get(i).tick();
                }
            }
        }
    }

    public static Wheel getWheel(BlockPos blockPos) {
        Wheel wheel = null;
        if (!activeWheels.isEmpty()) {
            for (int i = 0; i < activeWheels.size(); i++) {
                if (activeWheels.get(i).blockPos.equals(blockPos)) {
                    wheel = activeWheels.get(i);
                }
            }
        }
        return wheel;
    }

    public static boolean attemptSpawnWheel(ServerLevel serverLevel, BlockPos blockPos, DirectionProperty FACING, BaseEntityBlock block) {
        Wheel preExistingWheel = getWheel(blockPos);
        if (preExistingWheel != null) {
            return preExistingWheel.spinPercent >= 1;
        }
        BlockSourceImpl blockSourceImpl = new BlockSourceImpl(serverLevel, blockPos);
        RandomizableContainerBlockEntity container = blockSourceImpl.getEntity();
        int middleSlotIndex = container.getContainerSize() / 2;
        ItemStack middleSlot = container.getItem(middleSlotIndex);
        if (middleSlot.getItem() == Items.COMPASS) {
            int size = container.getContainerSize();
            ArrayList<Integer> wheelItemIndeces = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                if (i != middleSlotIndex) {
                    ItemStack itemStack = container.getItem(i);
                    if (!itemStack.isEmpty()) {
                        wheelItemIndeces.add(i);
                    }
                }
            }
            if (wheelItemIndeces.size() != 0) {
                Random random = new Random();
                int chosenWheelItemIndex = random.nextInt(wheelItemIndeces.size());
                Wheel wheel = new Wheel(serverLevel, blockPos, block, wheelItemIndeces.get(chosenWheelItemIndex));
                ItemWheel.activeWheels.add(wheel);
                for (int i = 0; i < wheelItemIndeces.size(); i++) {
                    boolean isChosen = i == chosenWheelItemIndex;

                    float segmentAngle = Mth.TWO_PI / wheelItemIndeces.size();
                    float angle = segmentAngle * i;
                    float chosenItemAngle = segmentAngle * chosenWheelItemIndex;
                    float finalAngle = angle + ((Mth.TWO_PI * wheelRotations) - chosenItemAngle);
                    Vec3i normal = container.getBlockState().getValue(FACING).getNormal();
                    createDisplayEntity(banners[i % banners.length], angle, finalAngle, 1, 0, isChosen, serverLevel, blockPos, normal, wheel);

                    ItemStack itemStack = container.getItem(wheelItemIndeces.get(i));
                    createDisplayEntity(itemStack, angle, finalAngle, 0.5f, -0.1f, isChosen, serverLevel, blockPos, normal, wheel);
                }
            }
            return false;
        }
        return true;
    }
    private static void createDisplayEntity(ItemStack itemStack, float angle, float finalAngle, float scale, float offset, boolean isChosenItem, ServerLevel serverLevel, BlockPos blockPos, Vec3i normal, Wheel wheel) {
        Display.ItemDisplay entity = new Display.ItemDisplay(EntityType.ITEM_DISPLAY, serverLevel);
        ((DisplayAccess)entity).itemWheel_setInitialData(angle, finalAngle, scale, offset, isChosenItem, itemStack, wheel);
        entity.setPos(blockPos.getCenter());
        entity.lookAt(EntityAnchorArgument.Anchor.FEET, blockPos.getCenter().subtract(normal.getX(), normal.getY(), normal.getZ()));
        serverLevel.addFreshEntity(entity);
        wheel.displayEntities.add(entity);
    }
}
