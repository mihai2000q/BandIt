package com.bandit.extension

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun LocalDateTime.print() = this.toLocalDate().print() +
        " " +
        this.toLocalTime().print()

fun LocalDateTime.printName() = this.toLocalDate().printName() +
        " " +
        this.toLocalTime().print()

fun LocalDate.print() = this.dayOfMonth.toString().get2Characters() +
        "-" +
        this.month.value.toString().get2Characters() +
        "-" +
        this.year.toString()

fun LocalDate.printName() = this.dayOfMonth.toString() +
        " " +
        this.month.name.normalizeWord() +
        " " +
        this.year.toString()

fun LocalTime.print() = this.hour.toString().get2Characters() +
        ":" +
        this.minute.toString().get2Characters()

fun Duration.printMinutesAndSeconds() =
    this.toMinutes().toString().get2Characters() +
    ":" +
    (this.seconds % 60).toString().get2Characters()

fun Duration.printHoursAndMinutes() =
    this.toHours().toString().get2Characters() +
    ":" +
    this.toMinutes().toString().get2Characters()

fun Duration.printName() =
    if(this == Duration.ZERO)
        "00:00"
    else if(this.toMinutes() == 0L && this.toHours() == 0L)
        this.printSeconds()
    else if(this.toHours() == 0L && (this.seconds % 60) == 0L)
        this.printTotalMinutes()
    else if(this.toHours() == 0L)
        this.printTotalMinutes() +
        " and " +
        this.printSeconds()
    else
        this.printHours() +
        " and " +
        this.printMinutes()

private fun Duration.printSeconds() =
    (this.seconds % 60).toString() + " Second" + (if(this.seconds % 60 == 1L) "" else "s")

private fun Duration.printMinutes() =
    (this.toMinutes() % 60).toString() + " Minute" + (if(this.toMinutes() == 1L) "" else "s")

private fun Duration.printTotalMinutes() =
    this.toMinutes().toString() + " Minute" + (if(this.toMinutes() == 1L) "" else "s")

private fun Duration.printHours() =
    this.toHours().toString() + " Hour" + (if(this.toHours() == 1L) "" else "s")