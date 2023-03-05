package com.bandit.model

import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.model.BandInvitation
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class BandInvitationTest {
    private lateinit var band: Band
    @Before
    fun setup() {
        band = Band(
            name = "Iron Maiden",
            creator = -1, // irrelevant for this test
            members =  mutableMapOf()
        )
    }
    @Test
    fun band_invitation_init() {
        val theInvited = Account(
            name = "David Johnson",
            nickname = "Davy",
            role = BandItEnums.Account.Role.RhythmGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        val bandInvitation = BandInvitation(
            band,
            theInvited,
            false
        )
        assertEquals(band, bandInvitation.band)
        assertEquals(theInvited, bandInvitation.account)
        assertEquals(false, bandInvitation.hasAccepted)
    }
    @Test
    fun band_invitation_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val bandInvitation = BandInvitation.EMPTY
            Assert.assertNotNull(bandInvitation.id)
            if (bandInvitation.id < 0 || bandInvitation.id > Constants.MAX_NR_ITEMS)
                Assert.fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun band_invitation_equals() {
        val theInvited = Account(
            name = "David Johnson",
            nickname = "Davy",
            role = BandItEnums.Account.Role.RhythmGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        val theInvited2 = Account(
            name = "David Johnson",
            nickname = "Davyy",
            role = BandItEnums.Account.Role.RhythmGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        val bandInvitation1 = BandInvitation(
            band,
            theInvited,
            false
        )
        val bandInvitation2 = BandInvitation(
            band,
            theInvited2,
            false
        )
        val bandInvitation3 = BandInvitation(
            band,
            theInvited,
            false
        )
        assertEquals(bandInvitation1, bandInvitation3)
        assertNotEquals(bandInvitation2, bandInvitation1)
    }
    @Test
    fun band_invitation_sort_by_band() {
        // assuming the same person gets invited by multiple bands
        val theInvited = Account(
            name = "David Johnson",
            nickname = "Davy",
            role = BandItEnums.Account.Role.RhythmGuitar,
            email = "Davy@dave.com",
            bandId = null,
            bandName = null,
        )
        val bands = listOf(
            Band(
                name = "Iron Maidens",
                creator = -1,
                members =  mutableMapOf()
            ),
            Band(
                name = "Troopers",
                creator = -1,
                members =  mutableMapOf()
            )
        )
        val outcome = mutableListOf(
            BandInvitation(
                band = bands[0],
                account = theInvited,
                hasAccepted = false
            ),
            BandInvitation(
                band = band,
                account = theInvited,
                hasAccepted = false
            ),
            BandInvitation(
                band = bands[1],
                account = theInvited,
                hasAccepted = false
            )
        )
        outcome.sort()
        val expected = listOf(
            BandInvitation(
                band = band,
                account = theInvited,
                hasAccepted = false
            ),
            BandInvitation(
                band = bands[0],
                account = theInvited,
                hasAccepted = false
            ),
            BandInvitation(
                band = bands[1],
                account = theInvited,
                hasAccepted = false
            )
        )
        assertEquals(expected, outcome)
    }
}