package com.metacontent.cobblenav

import com.cobblemon.mod.common.NetworkManager
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass

interface Implementation {
    val networkManager: NetworkManager

    fun registerItems()

    fun registerCommands()

    fun injectLootTables()

    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(id: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>)
}