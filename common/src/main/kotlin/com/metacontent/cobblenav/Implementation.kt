package com.metacontent.cobblenav

import com.cobblemon.mod.common.NetworkManager
import com.metacontent.cobblenav.util.ModDependency
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.commands.synchronization.ArgumentTypeInfo
import net.minecraft.resources.ResourceLocation
import kotlin.reflect.KClass

interface Implementation {
    val networkManager: NetworkManager

    fun registerItems()

    fun registerCommands()

    fun injectLootTables()

    fun <A : ArgumentType<*>, T : ArgumentTypeInfo.Template<A>> registerCommandArgument(identifier: ResourceLocation, argumentClass: KClass<A>, serializer: ArgumentTypeInfo<A, T>)

    fun isModInstalled(mod: ModDependency): Boolean
}