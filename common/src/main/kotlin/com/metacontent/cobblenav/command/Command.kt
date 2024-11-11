package com.metacontent.cobblenav.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack

interface Command {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>)
}