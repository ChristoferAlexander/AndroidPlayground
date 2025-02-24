package com.alex.androidplayground.codeExamples

// Data classes
data class Cylinder(var number: Int = 0, var volume: Int = 0)
data class Engine(var horsepower: Int = 0, var type: String = "", var cylinders: List<Cylinder> = listOf())
data class Wheels(var count: Int = 4, var size: Int = 16)
data class Car(var model: String = "", var engine: Engine = Engine(), var wheels: Wheels = Wheels())

// DSL Marker for Car
@DslMarker
annotation class CarDsl

// DSL Marker for Engine (to restrict cylinders inside engine)
@DslMarker
annotation class EngineDsl


// Cylinder DSL
@EngineDsl // This ensures cylinders can only be used inside engine
class CylinderBuilder {
    var number: Int = 0
    var volume: Int = 0
    fun build() = Cylinder(number, volume)
}

// Cylinders DSL
@EngineDsl // CylindersBuilder can only be used inside Engine
class CylindersBuilder {
    private val cylinders = mutableListOf<Cylinder>()
    fun cylinder(block: CylinderBuilder.() -> Unit) {
        cylinders += CylinderBuilder().apply(block).build()
    }

    fun build() = cylinders
}

// Engine DSL
@CarDsl // EngineBuilder can only be used inside Car
@EngineDsl // Ensure cylinders can be used here
class EngineBuilder {
    var horsepower: Int = 0
    var type: String = ""
    private var cylinders: List<Cylinder> = listOf()

    fun cylinders(block: CylindersBuilder.() -> Unit) {
        cylinders = CylindersBuilder().apply(block).build()
    }

    fun build() = Engine(horsepower, type, cylinders)
}

// Wheels DSL
@CarDsl // WheelsBuilder can only be used inside Car
class WheelsBuilder {
    var count: Int = 4
    var size: Int = 16
    fun build() = Wheels(count, size)
}

// Car DSL
@CarDsl // This ensures that the car block is top-level and only allows engine and wheels
class CarBuilder {
    var model: String = ""
    private lateinit var engine: Engine
    private var wheels: Wheels = Wheels()

    fun engine(block: EngineBuilder.() -> Unit) {
        engine = EngineBuilder().apply(block).build()
    }

    fun wheels(block: WheelsBuilder.() -> Unit) {
        wheels = WheelsBuilder().apply(block).build()
    }

    fun build() = Car(model, engine, wheels)
}

// Top-level DSL function
fun car(block: CarBuilder.() -> Unit): Car = CarBuilder().apply(block).build()

fun createExampleCar(): Car {
    return car {
        model = "Tesla Model S"
        engine {
            horsepower = 670
            type = "Electric"
            cylinders {
                cylinder { number = 1; volume = 500 }
                cylinder { number = 2; volume = 500 }
                cylinder { number = 3; volume = 500 }
                cylinder { number = 4; volume = 500 }
            }
        }
        wheels {
            count = 4
            size = 19
        }
    }
}