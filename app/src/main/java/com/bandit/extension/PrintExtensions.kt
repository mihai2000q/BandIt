package com.bandit.extension

import java.time.Duration

fun Duration.print() = this.toMinutes().toString().get2Characters() +
        ":" +
        (this.seconds % 60).toString().get2Characters()
