package com.veljkotosic.animalwatch.data.marker.entity.tag

object WatchMarkerAnimalStateTags {
    const val Hungry = "Hungry"
    const val Thirsty = "Thirsty"
    const val Wounded = "Wounded"
    const val Bleeding = "Bleeding"
    const val Dead = "Dead"

    val All = listOf(
        Hungry,
        Thirsty,
        Wounded,
        Bleeding,
        Dead
    )
}