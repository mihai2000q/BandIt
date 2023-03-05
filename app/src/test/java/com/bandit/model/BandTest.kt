package com.bandit.model

import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BandTest {
    @Test
    fun band_init() {
        val member1 = Account(
            name = "David Johnson",
            nickname = "",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "",
            bandId = null,
            bandName = null,
        )
        val member2 = Account(
            name = "Carlos Johnson",
            nickname = "",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "",
            bandId = null,
            bandName = null,
        )
        val band = Band(
            name = "Iron Maiden",
            creator = -1,
            members = mutableMapOf(
                member1 to false,
                member2 to true
            )
        )
        assertEquals("Iron Maiden", band.name)
        assertEquals(member1, band.members.keys.first())
        assertEquals(member2, band.members.keys.first { member2 == it })
    }
    @Test
    fun band_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val band = Band.EMPTY
            Assert.assertNotNull(band.id)
            if (band.id < 0 || band.id > Constants.MAX_NR_ITEMS)
                Assert.fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun band_equals() {
        val member1 = Account(
            name = "David Johnson",
            nickname = "",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "",
            bandId = null,
            bandName = null,
        )
        val member2 = Account(
            name = "Carlos Johnson",
            nickname = "",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "",
            bandId = null,
            bandName = null,
        )
        val member22 = Account(
            name = "Carloss Johnson",
            nickname = "",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "",
            bandId = null,
            bandName = null,
        )
        val band1 = Band(
            name = "Iron Maiden",
            creator = -1,
            members = mutableMapOf(
                member1 to false,
                member2 to true
            )
        )
        val band2 = Band(
            name = "Iron Maiden",
            creator = -1,
            members = mutableMapOf(
                member1 to false,
                member2 to true
            )
        )
        val band3 = Band(
            name = "Iron Maiden",
            creator = -1,
            members = mutableMapOf(
                member1 to false,
                member22 to true
            )
        )
        assertEquals(band1, band2)
        assertNotEquals(band2, band3)
    }
}