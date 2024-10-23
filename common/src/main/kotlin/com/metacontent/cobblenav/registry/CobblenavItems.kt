package com.metacontent.cobblenav.registry

import com.metacontent.cobblenav.item.Pokefinder
import com.metacontent.cobblenav.item.Pokenav
import com.metacontent.cobblenav.item.PokenavModelType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters
import net.minecraft.world.item.CreativeModeTab.Output
import net.minecraft.world.item.Item

object CobblenavItems : RegistryProvider<Registry<Item>, ResourceKey<Registry<Item>>, Item>() {
    override val registry: Registry<Item> = BuiltInRegistries.ITEM
    override val resourceKey: ResourceKey<Registry<Item>> = Registries.ITEM

    // Pokenavs
    val POKENAV = pokenavItem(PokenavModelType.BASE)
    val WHITE_POKENAV = pokenavItem(PokenavModelType.WHITE)
    val LIGHT_GRAY_POKENAV = pokenavItem(PokenavModelType.LIGHT_GRAY)
    val GRAY_POKENAV = pokenavItem(PokenavModelType.GRAY)
    val BLACK_POKENAV = pokenavItem(PokenavModelType.BLACK)
    val BROWN_POKENAV = pokenavItem(PokenavModelType.BROWN)
    val RED_POKENAV = pokenavItem(PokenavModelType.RED)
    val ORANGE_POKENAV = pokenavItem(PokenavModelType.ORANGE)
    val YELLOW_POKENAV = pokenavItem(PokenavModelType.YELLOW)
    val LIME_POKENAV = pokenavItem(PokenavModelType.LIME)
    val GREEN_POKENAV = pokenavItem(PokenavModelType.GREEN)
    val CYAN_POKENAV = pokenavItem(PokenavModelType.CYAN)
    val LIGHT_BLUE_POKENAV = pokenavItem(PokenavModelType.LIGHT_BLUE)
    val BLUE_POKENAV = pokenavItem(PokenavModelType.BLUE)
    val PURPLE_POKENAV = pokenavItem(PokenavModelType.PURPLE)
    val MAGENTA_POKENAV = pokenavItem(PokenavModelType.MAGENTA)
    val PINK_POKENAV = pokenavItem(PokenavModelType.PINK)
    val GHOLDENGO_POKENAV = pokenavItem(PokenavModelType.GHOLDENGO)

    // Pokefinders
    val BLACK_POKEFINDER = pokefinderItem("black")
    val BLUE_POKEFINDER = pokefinderItem("blue")
    val GREEN_POKEFINDER = pokefinderItem("green")
    val PINK_POKEFINDER = pokefinderItem("pink")
    val RED_POKEFINDER = pokefinderItem("red")
    val WHITE_POKEFINDER = pokefinderItem("white")
    val YELLOW_POKEFINDER = pokefinderItem("yellow")

    private fun pokenavItem(model: PokenavModelType): Item {
        return add(Pokenav.BASE_REGISTRY_KEY + model.modelName, Pokenav(model))
    }

    private fun pokefinderItem(color: String): Item {
        return add(Pokefinder.BASE_REGISTRY_KEY + color, Pokefinder())
    }

    fun addToGroup(displayContext: ItemDisplayParameters, entries: Output) {
        entries.accept(POKENAV)
        entries.accept(WHITE_POKENAV)
        entries.accept(LIGHT_GRAY_POKENAV)
        entries.accept(GRAY_POKENAV)
        entries.accept(BLACK_POKENAV)
        entries.accept(BROWN_POKENAV)
        entries.accept(RED_POKENAV)
        entries.accept(ORANGE_POKENAV)
        entries.accept(YELLOW_POKENAV)
        entries.accept(LIME_POKENAV)
        entries.accept(GREEN_POKENAV)
        entries.accept(CYAN_POKENAV)
        entries.accept(LIGHT_BLUE_POKENAV)
        entries.accept(BLUE_POKENAV)
        entries.accept(PURPLE_POKENAV)
        entries.accept(MAGENTA_POKENAV)
        entries.accept(PINK_POKENAV)
        entries.accept(GHOLDENGO_POKENAV)

        entries.accept(BLACK_POKEFINDER)
        entries.accept(BLUE_POKEFINDER)
        entries.accept(GREEN_POKEFINDER)
        entries.accept(PINK_POKEFINDER)
        entries.accept(RED_POKEFINDER)
        entries.accept(WHITE_POKEFINDER)
        entries.accept(YELLOW_POKEFINDER)
    }
}