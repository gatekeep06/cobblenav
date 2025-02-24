package com.metacontent.cobblenav.mixin;

import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GrowingPlantBlock.class)
public interface GrowingPlantBlockMixin {
    @Invoker("getHeadBlock")
    GrowingPlantHeadBlock invokeGetHeadBlock();
}
