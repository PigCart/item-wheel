package pigcart.itemwheel.fabric;

import pigcart.itemwheel.ItemWheel;
import net.fabricmc.api.ModInitializer;

public class ItemWheelFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ItemWheel.init();
    }
}
