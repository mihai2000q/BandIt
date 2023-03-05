package com.bandit.repository

import com.bandit.constant.BandItEnums
import com.bandit.data.model.Account
import com.bandit.data.model.Band
import com.bandit.data.repository.BandRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class BandRepositoryTest {
    private lateinit var bandRepository: BandRepository
    private lateinit var member1: Account
    private lateinit var member2: Account
    @Before
    fun setup() {
        bandRepository = BandRepository()
        member1 = Account(
            name = "David Johnson",
            nickname = "",
            role = BandItEnums.Account.Role.LeadGuitar,
            email = "",
            bandId = null,
            bandName = null,
        )
        member2 = Account(
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
        runBlocking { bandRepository.createBand(band) }
    }
    @Test
    fun band_repository_create_band() {
        assertEquals("Iron Maiden", bandRepository.band.name)
        assertEquals(member1, bandRepository.band.members.keys.first())
        assertEquals(member2, bandRepository.band.members.keys.first { member2 == it })
    }
    @Test
    fun band_repository_kick_band_member() {
        runBlocking { bandRepository.kickBandMember(member1) }
        assertEquals(1, bandRepository.band.members.size)
        assertNotEquals(member1, bandRepository.band.members.keys.first())
    }
    @Test
    fun band_repository_disband_band() {
        runBlocking { bandRepository.disbandBand() }
        assertEquals(Band.EMPTY, bandRepository.band)
    }
    @Test
    fun band_repository_abandon_band() {
        runBlocking { bandRepository.abandonBand() }
        assertEquals(Band.EMPTY, bandRepository.band)
    }
}