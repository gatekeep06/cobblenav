package com.metacontent.cobblenav.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack

interface CobblenavCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>)
}