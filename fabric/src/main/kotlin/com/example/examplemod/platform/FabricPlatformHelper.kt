package com.example.examplemod.platform

import com.example.examplemod.platform.services.IPlatformHelper
import net.fabricmc.loader.api.FabricLoader

class FabricPlatformHelper : IPlatformHelper {
    override val platformName: String = "Fabric"

    override fun isModLoaded(modId: String?): Boolean = FabricLoader.getInstance().isModLoaded(modId)


    override val isDevelopmentEnvironment: Boolean
        get() = FabricLoader.getInstance().isDevelopmentEnvironment
}