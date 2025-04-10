package com.alex.androidplayground.breweryScreen.domain.model

sealed class BreweryType(val type: String, val description: String) {
    data object Micro : BreweryType("micro", "Most craft breweries. For example, Samuel Adams is still considered a microbrewery.")
    data object Nano : BreweryType("nano", "An extremely small brewery which typically only distributes locally.")
    data object Regional : BreweryType("regional", "A regional location of an expanded brewery, like Sierra Nevada’s Asheville, NC location.")
    data object Brewpub : BreweryType("brewpub", "A beer-focused restaurant or restaurant/bar with a brewery on-premise.")
    data object Large : BreweryType("large", "A very large brewery. Likely not for visitors, e.g., Miller-Coors. (deprecated)")
    data object Planning : BreweryType("planning", "A brewery in planning or not yet opened to the public.")
    data object Bar : BreweryType("bar", "A bar. No brewery equipment on-premise. (deprecated)")
    data object Contract : BreweryType("contract", "A brewery that uses another brewery’s equipment.")
    data object Proprietor : BreweryType("proprietor", "Similar to contract brewing, refers to a brewery incubator.")
    data object Closed : BreweryType("closed", "A location which has been closed.")
    data object Taproom : BreweryType("Taproom", "..")
}

fun getAllBreweryTypes(): List<BreweryType> {
    return BreweryType::class.sealedSubclasses.map { it.objectInstance!! }
}