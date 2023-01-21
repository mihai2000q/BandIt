package com.bandit.concerts

import com.bandit.data.model.Concert
import com.bandit.data.repository.ConcertRepository
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ConcertRepositoryTest {
    private lateinit var concertRepository: ConcertRepository

    @Before
    fun setup() {
        concertRepository = ConcertRepository()
    }
    private fun import_data() {
        runBlocking {
            concertRepository.add(
                Concert(
                    "Legacy of the beast",
                    LocalDateTime.of(2023, 10, 21, 20, 0),
                    "Los Angeles",
                    "United States",
                    "Big Arena",
                    BandItEnums.Concert.Type.Tournament
                )
            )
            concertRepository.add(
                Concert(
                    "Legacy of the beast 2",
                    LocalDateTime.of(2023, 11, 21, 20, 0),
                    "Los Angeles",
                    "United States of America",
                    "Big Arena 2",
                    BandItEnums.Concert.Type.Tournament
                )
            )
            concertRepository.add(
                Concert(
                    "Legacy of the concert",
                    LocalDateTime.of(2024, 7, 21, 20, 0),
                    "L A ",
                    "U.S.A",
                    "Small Arena",
                    BandItEnums.Concert.Type.Simple
                )
            )
            concertRepository.add(
                Concert(
                    "Rock fest",
                    LocalDateTime.of(2024, 9, 21, 23, 0),
                    "Berlin",
                    "Germany",
                    "rock fest Arena",
                    BandItEnums.Concert.Type.Festival
                )
            )
            concertRepository.add(
                Concert(
                    "Amon Amar cool",
                    LocalDateTime.parse("2023-02-09T12:00"),
                    "Leipzig",
                    "Germany",
                    "rock fest Arena",
                    BandItEnums.Concert.Type.Simple
                )
            )
        }
    }
    @Test
    fun concert_repository_add() {
        add_concert(
            concertRepository,
            0,
            "newConcert",
            LocalDateTime.parse("2020-12-10T10:00"),
            "newCity",
            "newCountry",
            "newPlace",
            BandItEnums.Concert.Type.Festival
        )
    }
    @Test
    fun concert_repository_remove() {
        import_data()
        remove_concert(concertRepository, 0, 5)
    }
    @Test
    fun concert_repository_remove_and_add_multiple() {
        import_data()
        remove_concert(concertRepository, 3, 5)
        add_concert(
            concertRepository,
            4,
            "newConcert",
            LocalDateTime.parse("2020-12-10T10:00"),
            "newCity",
            "newCountry",
            "newPlace",
            BandItEnums.Concert.Type.Festival
        )
        add_concert(
            concertRepository,
            5,
            "newConcert2",
            LocalDateTime.parse("1999-12-10T10:00"),
            "newCity2",
            "newCountry2",
            "newPlace2",
            BandItEnums.Concert.Type.Tournament
        )
        remove_concert(concertRepository, 3, 6)
        remove_concert(concertRepository, 3, 5)
        remove_concert(concertRepository, 3, 4)
        remove_concert(concertRepository, 2, 3)
        remove_concert(concertRepository, 1, 2)
        remove_concert(concertRepository, 0, 1)

        assertEquals(concertRepository.list.size, 0)
    }
    @Test
    fun concert_repository_different_ids() {
        runBlocking {
            with(concertRepository) {
                for (i in 0 until Constants.MAX_NR_ITEMS / 2)
                    add(Concert.getEmpty())
                for (concert in list)
                    if (list.filter { it.id == concert.id }.size > 1)
                        fail("Id's should be different")
            }
        }
    }
    @Test
    fun concert_repository_edit() {
        import_data()
        var concertToEdit = concertRepository.list[0]
        val newConcert = Concert(
            "newConcert",
            LocalDateTime.parse("2020-12-10T10:00"),
            "newCity",
            "newCountry",
            "newPlace",
            BandItEnums.Concert.Type.Festival
        )
        //before
        assert_concert(
            concertRepository,
            5,
            0,
            "Legacy of the beast",
            LocalDateTime.parse("2023-10-21T20:00"),
            "Los Angeles",
            "United States",
            "Big Arena",
            BandItEnums.Concert.Type.Tournament
        )
        concertToEdit = Concert(
            newConcert.name,
            newConcert.dateTime,
            newConcert.city,
            newConcert.country,
            newConcert.place,
            newConcert.type,
            concertToEdit.id
        )
        runBlocking {
            concertRepository.edit(concertToEdit)
            assert_concert(
                concertRepository,
                5,
                0,
                "newConcert",
                LocalDateTime.parse("2020-12-10T10:00"),
                "newCity",
                "newCountry",
                "newPlace",
                BandItEnums.Concert.Type.Festival
            )
        }
    }
    @Test
    fun concert_repository_filter_string() {
        import_data()
        //one result
        val outcome = concertRepository.filterConcerts(
            "am", null, null, null, null, null,null)
        val expected = Concert(
            "Amon Amar cool",
            LocalDateTime.parse("2023-02-09T12:00"),
            "Leipzig",
            "Germany",
            "rock fest Arena",
            BandItEnums.Concert.Type.Simple
        )
        assertEquals(outcome.size, 1)
        assertEquals(expected, outcome.first())

        //multiple result - multiple words for filtering
        val outcome2 = concertRepository.filterConcerts("lEgAcy oF thE BeaSt",
            null, null,null, null, null, null)
        val expected2 = listOf(
            Concert(
                "Legacy of the beast",
                LocalDateTime.of(2023, 10, 21, 20,0),
                "Los Angeles",
                "United States",
                "Big Arena",
                BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                "Legacy of the beast 2",
                LocalDateTime.of(2023, 11, 21, 20,0),
                "Los Angeles",
                "United States of America",
                "Big Arena 2",
                BandItEnums.Concert.Type.Tournament
            )
        )
        assertEquals(outcome2.size, 2)
        assertEquals(expected2, outcome2)

        //multiple filters
        val outcome3 = concertRepository.filterConcerts("am",
            null, null,"Leipzig", "Germany", null, null)
        assertEquals(outcome3.size, 1)
        assertEquals(expected, outcome3.first())

        //assert name in between
        val outcome4 = concertRepository.filterConcerts(
            "amar", null, null, null, null, null,null)
        assertEquals(outcome4.size, 1)
        assertEquals(expected, outcome4.first())

        //assert place
        val outcome5 = concertRepository.filterConcerts(
            "amar", null, null, null, null, "rock fest",null)
        assertEquals(outcome5.size, 1)
        assertEquals(expected, outcome5.first())
    }
    @Test
    fun concert_repository_filter_date_time() {
        import_data()
        //assert date
        val outcome = concertRepository.filterConcerts(
            null,
            LocalDate.of(2023,2,9),
            null,
            null, null, null, null)
        val expected = Concert(
            "Amon Amar cool",
            LocalDateTime.parse("2023-02-09T12:00"),
            "Leipzig",
            "Germany",
            "rock fest Arena",
            BandItEnums.Concert.Type.Simple
        )
        assertEquals(outcome.size, 1)
        assertEquals(expected, outcome.first())
        //assert time
        val outcome2 = concertRepository.filterConcerts(
            null, null,
            LocalTime.parse("12:00"),
            null, null, null, null)
        assertEquals(outcome2.size, 1)
        assertEquals(expected, outcome.first())
    }
    @Test
    fun concert_repository_filter_type() {
        import_data()
        val outcome = concertRepository.filterConcerts(
            null,
            null,
            null,
            null, null, null, BandItEnums.Concert.Type.Festival)
        val expected = Concert(
            "Rock fest",
            LocalDateTime.of(2024, 9, 21, 23, 0),
            "Berlin",
            "Germany",
            "rock fest Arena",
            BandItEnums.Concert.Type.Festival
        )
        assertEquals(outcome.size, 1)
        assertEquals(expected, outcome.first())
    }
    private fun remove_concert(repository: ConcertRepository, index: Int, size: Int) {
        val concertToRemove = repository.list[index]
        val before = repository.list.filter { it == concertToRemove }
        assertEquals(1, before.size)
        assertEquals(size, repository.list.size)
        runBlocking { repository.remove(concertToRemove) }
        val after = repository.list.filter { it == concertToRemove }
        assertEquals(0, after.size)
        assertEquals(size - 1, repository.list.size)
        assertThrows(IndexOutOfBoundsException::class.java) {
            repository.list[size - 1]
        }
    }
    private fun add_concert(
        repository: ConcertRepository,
        size: Int,
        name: String,
        dateTime: LocalDateTime,
        city: String,
        country: String,
        place: String,
        type: BandItEnums.Concert.Type
    ) {
        runBlocking { repository.add(Concert(name, dateTime, city, country, place, type)) }
        assert_concert(repository, size + 1, size ,name, dateTime, city, country, place, type)
    }
    private fun assert_concert(
        repository: ConcertRepository,
        size: Int,
        index:Int,
        name: String,
        dateTime: LocalDateTime,
        city: String,
        country: String,
        place: String,
        type: BandItEnums.Concert.Type
    ) {
        assertEquals(size, repository.list.size)
        assertNotNull(repository.list[index].id)
        assertEquals(name, repository.list[index].name)
        assertEquals(dateTime, repository.list[index].dateTime)
        assertEquals(city, repository.list[index].city)
        assertEquals(country, repository.list[index].country)
        assertEquals(place, repository.list[index].place)
        assertEquals(type, repository.list[index].type)
    }

}