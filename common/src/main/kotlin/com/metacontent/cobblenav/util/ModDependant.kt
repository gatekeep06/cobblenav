package com.metacontent.cobblenav.util

import com.metacontent.cobblenav.Cobblenav

interface ModDependant {
    var neededInstalledMods: List<ModDependency>
    var neededUninstalledMods: List<ModDependency>

    fun isModDependencySatisfied(): Boolean {
        return if (neededInstalledMods.isNotEmpty() && neededInstalledMods.any { !Cobblenav.implementation.isModInstalled(it) }) {
            false
        } else if (neededUninstalledMods.isNotEmpty() && neededUninstalledMods.any { Cobblenav.implementation.isModInstalled(it) }) {
            false
        } else {
            true
        }
    }
}

data class ModDependency(
    val id: String,
    val version: String
)