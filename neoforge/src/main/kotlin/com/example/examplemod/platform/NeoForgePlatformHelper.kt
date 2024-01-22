package com.example.examplemod.platform

import com.example.examplemod.platform.services.IPlatformHelper
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader

class NeoForgePlatformHelper : IPlatformHelper {
    override val platformName: String = "NeoForge"

    override fun isModLoaded(modId: String?): Boolean = ModList.get().isLoaded(modId)


    override val isDevelopmentEnvironment: Boolean
        get() = !FMLLoader.isProduction()
}