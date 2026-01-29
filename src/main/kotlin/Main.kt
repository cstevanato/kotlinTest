package org.example

import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.createCoroutineUnintercepted
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
//    val name = "Kotlin"
//    //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//    // to see how IntelliJ IDEA suggests fixing it.
//    println("Hello, " + name + "!")
//
//    for (i in 1..5) {
//        //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//        // for y  but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//        println("i = $i")
//    }


//    println("Feeding Boots the dog.")
//    println("Feeding Fluffy the cat.")
//    println("Feeding Danny the dog.")
//    println("Feeding Stocks the cat.")
//    println("Feeding Rover the dog.")
//    println("Feeding Ginger the cat.")


//    repeat(3) {
//        feedDogs.resume()
//        feedCats.resume()
//    }
//    println("-----------------------------")
//    repeat(3) {
//        feedDogs1.resume()
//        feedCats1.resume()
//    }

    val start = LocalDate.parse("1947-12-01")
    val end = LocalDate.parse("1947-12-31")

    for (date in weekdays( start, end)) {
        println(date)
    }
}

val weekendDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

fun weekdays(from: LocalDate, to: LocalDate) = sequence {
    require(from <= to)

    var date: LocalDate = from
    while (date <= to) {
        if (date.dayOfWeek !in weekendDays) yield(date)
        date = date.plusDays(1)
    }
}




/// corountine pause
val feedDogs1 = createCoroutine {
    val dogs = listOf("Boots", "Danny", "Rover")
    for (dog in dogs) {
        println("Feeding $dog the dog"); pause()
    }
}

val feedCats1 = createCoroutine {
    val cats = listOf("Boots", "Danny", "Rover")
    for (cat in cats) {
        println("Feeding $cat the cat"); pause()
    }
}


val feedDogs = createCoroutine {
    println("Feeding Boots the dog."); pause()
    println("Feeding Danny the dog."); pause()
    println("Feeding Rover the dog."); pause()
}


val feedCats = createCoroutine {
    println("Feeding Fluffy the cat."); pause()
    println("Feeding Stocks the cat."); pause()
    println("Feeding Ginger the cat."); pause()
}

fun <T> createCoroutine(block: suspend () -> T)  =
    block.createCoroutineUnintercepted(Continuation(EmptyCoroutineContext) {})


fun Continuation<Unit>.resume(): Unit = resume(Unit)
suspend inline fun pause() = suspendCoroutine<Unit> { cont -> COROUTINE_SUSPENDED}
