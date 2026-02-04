package org.example.coroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

suspend fun bathTime() {
    println("Going to the bathroom...")
    delay(500L)
    println("Bath done, exiting")
}

suspend fun boilingWater() {
    println("Boiling water...")
    delay(1000L)
    println("Water boiled.")
}

suspend fun sequentialMorningRoutine() {
    coroutineScope {
        bathTime()
    }
    coroutineScope {
        boilingWater()
    }
}

suspend fun concurrentMorningRoutines() {
    coroutineScope {
        launch { bathTime() }
    }
    coroutineScope {
        launch { boilingWater() }
    }
}

fun noStructConcurrencyMorningRoutine() {
    GlobalScope.launch {
        bathTime()
    }
    GlobalScope.launch {
        boilingWater()
    }
}

suspend fun makeCoffee() {
    println("Starting coffee maker...")
    delay(500L)
    println("Coffee is ready!")
}

suspend fun morningRoutineWithCoffee() {
    coroutineScope {
        val bathTimeJob = launch { bathTime() }
        val boilingWaterJob = launch { boilingWater() }
        bathTimeJob.join()
        boilingWaterJob.join()
        launch { makeCoffee() }
    }
}

suspend fun morningCoroutineWithCoffeeStructured() {
    coroutineScope {
        coroutineScope {
            launch { bathTime() }
            launch { boilingWater() }
        }
        launch { makeCoffee() }
    }
}

suspend fun preparingJavaCoffee(): String {
    println("Preparing Java Coffee...")
    delay(500L)
    println("Java Coffee is ready!")
    return "Java Coffee"
}

suspend fun toastingBread(): String {
    println("Toasting Bread...")
    delay(1000L)
    println("Bread is toasted!")
    return "Toasted Bread"
}

suspend fun prepareBreakfast() {
    coroutineScope {
        val coffee = async { preparingJavaCoffee() }
        val bread = async { toastingBread() }
        val finalCoffee = coffee.await()
        val finalToast = bread.await()
        println("Breakfast is ready with ${finalToast} and ${finalCoffee}")
    }
}

suspend fun workingHard() {
    println("Working hard...")
    while (true) {}
    delay(1500L)
    println("Work completed!")
}

suspend fun workHardRoutine() {
    val dispatcher= Dispatchers.Default.limitedParallelism(2)
    coroutineScope {
        launch(dispatcher) { workingHard() }
        launch(dispatcher) { takeABreak() }
    }
}

suspend fun takeABreak() {
    println("Taking a break...")
    delay(500L)
    println("Break over!")
}

suspend fun main() {
//    println("=== Sequential Morning Routine ===")
//    sequentialMorningRoutine()
//
//    println("\n=== No Structured Concurrency Morning Routine ===")
//    noStructConcurrencyMorningRoutine()
//    delay(2000L) // Wait for coroutines to finish
//
//    println("\n=== Concurrent Morning Routines ===")
//    concurrentMorningRoutines()
//    delay(2000L) // Wait for coroutines to finish
//
//    println("\n=== Morning Routine with Coffee ===")
//    morningRoutineWithCoffee()
//
//    println("\n=== Morning Coroutine with Coffee Structured ===")
//    morningCoroutineWithCoffeeStructured()

//    println("\n=== Preparing Breakfast ===")
//    prepareBreakfast()

    println("\n=== Work Hard routine ===")
    workHardRoutine()
}