package com.metacontent.cobblenav.config

data class FeatureWeights(
    val shiny: Float,
    val perfectIvsRates: Map<Int, Float>,
    val hiddenAbility: Float,
    val eggMove: Float
) {
    companion object {
        val BASE = FeatureWeights(
            10f,
            mapOf(1 to 1f, 2 to 2f, 3 to 3f, 4 to 4f, 5 to 5f, 6 to 6f),
            1f,
            1f
        )
    }
}
