package com.example.examplemod

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import com.example.examplemod.platform.CommonClass

@Mod(Constants.MOD_ID)
class ExampleMod(eventBus: IEventBus?) {
    init {
        // This method is invoked by the NeoForge mod loader when it is ready
        // to load your mod. You can access NeoForge and Common code in this
        // project.

        // Use NeoForge to bootstrap the Common mod.

        Constants.LOG.info("Hello NeoForge world!")
        CommonClass.init()
    }
}