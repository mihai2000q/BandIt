package com.bandit.extension

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun Duration.print() = this.toMinutes().toString().get2Characters() +
        ":" +
        (this.seconds % 60).toString().get2Characters()

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
