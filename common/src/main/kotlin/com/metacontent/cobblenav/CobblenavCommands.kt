package com.metacontent.cobblenav

import com.metacontent.cobblenav.command.ContactCommand
import com.metacontent.cobblenav.command.TitleCommand
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

object CobblenavCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, registry: CommandBuildContext, selection: Commands.CommandSelection) {
        TitleCommand.register(dispatcher)
        ContactCommand.register(dispatcher)
    }
}