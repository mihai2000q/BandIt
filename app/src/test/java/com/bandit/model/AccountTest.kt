package com.bandit.model

import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import org.junit.Assert.*
import org.junit.Test

class AccountTest {
    @Test
    fun account_init() {
        val account = Account(
            name = "David Johnson",
            nickname = "Davy",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        assertEquals("David Johnson", account.name)
        assertEquals("Davy", account.nickname)
        assertEquals(BandItEnums.Account.Role.LeadGuitar, account.role)
        assertEquals("Davy@dave.com", account.email)
        assertNull(account.bandId)
        assertNotNull(account.id)
    }
    @Test
    fun account_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val account = Account.EMPTY
            assertNotNull(account.id)
            if (account.id < 0 || account.id > Constants.MAX_NR_ITEMS)
                fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun account_equals() {
        val account1 = Account(
            name = "David Johnson",
            nickname = "Davy",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        val account2 = Account(
            name = "David Johnson",
            nickname = "Davy",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        val account3 = Account(
            name = "David Juan",
            nickname = "Davy",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        assertEquals(account1, account2)
        assertNotEquals(account1.id, account2.id)
        assertNotEquals(account1, account3)
    }
    @Test
    fun account_sort_by_name() {
        val outcome = mutableListOf(
            Account(
                name = "",
                nickname = "Jamie",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            ),
            Account(
                name = "",
                nickname = "Ricky",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            ),
            Account(
                name = "",
                nickname = "Carlos",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            ),
            Account(
                name = "",
                nickname = "Davie",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            )
        )
        outcome.sort()
        val expected = listOf(
            Account(
                name = "",
                nickname = "Carlos",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            ),
            Account(
                name = "",
                nickname = "Davie",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            ),
            Account(
                name = "",
                nickname = "Jamie",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            ),
            Account(
                name = "",
                nickname = "Ricky",
                role = BandItEnums.Account.Role.LeadGuitar,
                email = "",
                bandId = null,
                bandName = null,
            )
        )
        assertEquals(outcome, expected)
    }
}