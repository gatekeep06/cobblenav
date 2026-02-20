package com.metacontent.cobblenav.client

import net.minecraft.world.item.ItemDisplayContext

fun ItemDisplayContext.isGui() = this == ItemDisplayContext.GUI ||
//        this == ItemDisplayContext.GROUND ||
        this == ItemDisplayContext.FIXED