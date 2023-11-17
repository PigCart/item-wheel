package pigcart.itemwheel.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pigcart.itemwheel.ItemWheel;

@Mod(ItemWheel.MOD_ID)
public class ItemWheelForge {
    public ItemWheelForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ItemWheel.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ItemWheel.init();
    }
}
