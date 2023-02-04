package com.bandit.repository

import com.bandit.data.model.Concert
import com.bandit.data.repository.ConcertRepository
import com.bandit.constant.BandItEnums
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ConcertRepositoryTest : BaseRepositoryTest<Concert>() {
    private lateinit var concertRepository: ConcertRepository
    @Before
    fun setup() {
        concertRepository = ConcertRepository()
    }
    override fun import_data() {
        runBlocking {
            concertRepository.add(
                Concert(
                    name = "Legacy of the beast",
                    dateTime = LocalDateTime.of(2023, 10, 21, 20, 0),
                    duration = Duration.ofMinutes(100),
                    bandId = -1,
                    city = "Los Angeles",
                    country = "United States",
                    place = "Big Arena",
                    concertType = BandItEnums.Concert.Type.Tournament
                )
            )
            concertRepository.add(
                Concert(
                    name = "Legacy of the beast 2",
                    dateTime = LocalDateTime.of(2023, 11, 21, 20, 0),
                    duration = Duration.ofMinutes(100),
                    bandId = -1,
                    city = "Los Angeles",
                    country = "United States of America",
                    place = "Big Arena 2",
                    concertType = BandItEnums.Concert.Type.Tournament
                )
            )
            concertRepository.add(
                Concert(
                    name = "Legacy of the concert",
                    dateTime = LocalDateTime.of(2024, 7, 21, 20, 0),
                    duration = Duration.ofMinutes(120),
                    bandId = -1,
                    city = "L A ",
                    country = "U.S.A",
                    place = "Small Arena",
                    concertType = BandItEnums.Concert.Type.Simple
                )
            )
            concertRepository.add(
                Concert(
                    name = "Rock fest",
                    dateTime = LocalDateTime.of(2024, 9, 21, 23, 0),
                    duration = Duration.ofHours(1),
                    bandId = -1,
                    city = "Berlin",
                    country = "Germany",
                    place = "rock fest Arena",
                    concertType = BandItEnums.Concert.Type.Festival
                )
            )
            concertRepository.add(
                Concert(
                    name = "Amon Amar cool",
                    dateTime = LocalDateTime.parse("2023-02-09T12:00"),
                    duration = Duration.ofMinutes(120),
                    bandId =  -1,
                    city = "Leipzig",
                    country = "Germany",
                    place = "rock fest Arena",
                    concertType = BandItEnums.Concert.Type.Simple
                )
            )
        }
    }
    @Test
    fun concert_repository_add() {
        add_concert(
            repository = concertRepository,
            size = 0,
            name = "newConcert",
            dateTime = LocalDateTime.parse("2020-12-10T10:00"),
            duration = Duration.ofMinutes(100),
            city = "newCity",
            country = "newCountry",
            place = "newPlace",
            type = BandItEnums.Concert.Type.Festival
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
            repository = concertRepository,
            size = 4,
            name = "newConcert",
            dateTime = LocalDateTime.parse("2020-12-10T10:00"),
            duration = Duration.ofMinutes(120),
            city = "newCity",
            country = "newCountry",
            place = "newPlace",
            type = BandItEnums.Concert.Type.Festival
        )
        add_concert(
            repository = concertRepository,
            size = 5,
            name = "newConcert2",
            dateTime = LocalDateTime.parse("1999-12-10T10:00"),
            duration = Duration.ofMinutes(121),
            city = "newCity2",
            country = "newCountry2",
            place = "newPlace2",
            type = BandItEnums.Concert.Type.Tournament
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
        different_ids(concertRepository, Concert.EMPTY)
    }
    @Test
    fun concert_repository_edit() {
        import_data()
        var concertToEdit = concertRepository.list[0]
        val newConcert = Concert(
            name = "newConcert",
            dateTime = LocalDateTime.parse("2020-12-10T10:00"),
            duration = Duration.ofMinutes(120),
            bandId = -1,
            city = "newCity",
            country = "newCountry",
            place = "newPlace",
            concertType = BandItEnums.Concert.Type.Festival
        )
        //before
        assert_concert(
            repository = concertRepository,
            size = 5,
            index = 0,
            name = "Legacy of the beast",
            dateTime = LocalDateTime.parse("2023-10-21T20:00"),
            duration = Duration.ofMinutes(100),
            city = "Los Angeles",
            country = "United States",
            place = "Big Arena",
            type = BandItEnums.Concert.Type.Tournament
        )
        concertToEdit = Concert(
            name = newConcert.name,
            dateTime = newConcert.dateTime,
            duration = newConcert.duration,
            bandId = newConcert.bandId,
            city = newConcert.city,
            country = newConcert.country,
            place = newConcert.place,
            concertType = newConcert.concertType,
            id = concertToEdit.id
        )
        runBlocking {
            concertRepository.edit(concertToEdit)
            assert_concert(
                repository = concertRepository,
                size = 5,
                index = 0,
                name = "newConcert",
                dateTime = LocalDateTime.parse("2020-12-10T10:00"),
                duration = Duration.ofMinutes(120),
                city = "newCity",
                country = "newCountry",
                place = "newPlace",
                type = BandItEnums.Concert.Type.Festival
            )
        }
    }
    @Test
    fun concert_repository_filter_string() {
        import_data()
        //one result
        val outcome = concertRepository.filterConcerts(name = "am")
        val expected = Concert(
            name = "Amon Amar cool",
            dateTime = LocalDateTime.parse("2023-02-09T12:00"),
            duration = Duration.ofMinutes(120),
            bandId = -1,
            city = "Leipzig",
            country = "Germany",
            place = "rock fest Arena",
            concertType = BandItEnums.Concert.Type.Simple
        )
        assertEquals(outcome.size, 1)
        assertEquals(expected, outcome.first())

        //multiple result - multiple words for filtering
        val outcome2 = concertRepository.filterConcerts(name = "lEgAcy oF thE BeaSt", duration = Duration.ofMinutes(100))
        val expected2 = listOf(
            Concert(
                name = "Legacy of the beast",
                dateTime = LocalDateTime.of(2023, 10, 21, 20,0),
                duration = Duration.ofMinutes(100),
                bandId = -1,
                city = "Los Angeles",
                country = "United States",
                place = "Big Arena",
                concertType = BandItEnums.Concert.Type.Tournament
            ),
            Concert(
                name = "Legacy of the beast 2",
                dateTime = LocalDateTime.of(2023, 11, 21, 20,0),
                duration = Duration.ofMinutes(100),
                bandId =  -1,
                city = "Los Angeles",
                country = "United States of America",
                place = "Big Arena 2",
                concertType = BandItEnums.Concert.Type.Tournament
            )
        )
        assertEquals(outcome2.size, 2)
        assertEquals(expected2, outcome2)

        //multiple filters
        val outcome3 = concertRepository.filterConcerts(name = "am", city = "Leipzig", country = "Germany")
        assertEquals(outcome3.size, 1)
        assertEquals(expected, outcome3.first())

        //assert name in between
        val outcome4 = concertRepository.filterConcerts(name = "amar")
        assertEquals(outcome4.size, 1)
        assertEquals(expected, outcome4.first())

        //assert place
        val outcome5 = concertRepository.filterConcerts(name = "amar", place = "rock fest")
        assertEquals(outcome5.size, 1)
        assertEquals(expected, outcome5.first())
    }
    @Test
    fun concert_repository_filter_date_time() {
        import_data()
        //assert date
        val outcome = concertRepository.filterConcerts(date = LocalDate.of(2023,2,9))
        val expected = Concert(
            name = "Amon Amar cool",
            dateTime = LocalDateTime.parse("2023-02-09T12:00"),
            duration = Duration.ofMinutes(120),
            bandId =  -1,
            city = "Leipzig",
            country = "Germany",
            place = "rock fest Arena",
            concertType = BandItEnums.Concert.Type.Simple
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
        val outcome = concertRepository.filterConcerts( type = BandItEnums.Concert.Type.Festival)
        val expected = Concert(
            name = "Rock fest",
            dateTime = LocalDateTime.of(2024, 9, 21, 23, 0),
            duration = Duration.ofMinutes(60),
            bandId = -1,
            city = "Berlin",
            country = "Germany",
            place = "rock fest Arena",
            concertType = BandItEnums.Concert.Type.Festival
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
        duration: Duration,
        city: String,
        country: String,
        place: String,
        type: BandItEnums.Concert.Type
    ) {
        runBlocking { repository.add(Concert(name, dateTime, duration,-1, city, country, place, type)) }
        assert_concert(repository, size + 1, size ,name, dateTime, duration, city, country, place, type)
    }
    private fun assert_concert(
        repository: ConcertRepository,
        size: Int,
        index: Int,
        name: String,
        dateTime: LocalDateTime,
        duration: Duration,
        city: String,
        country: String,
        place: String,
        type: BandItEnums.Concert.Type
    ) {
        assertEquals(size, repository.list.size)
        assertNotNull(repository.list[index].id)
        assertEquals(name, repository.list[index].name)
        assertEquals(dateTime, repository.list[index].dateTime)
        assertEquals(duration, repository.list[index].duration)
        assertEquals(city, repository.list[index].city)
        assertEquals(country, repository.list[index].country)
        assertEquals(place, repository.list[index].place)
        assertEquals(type, repository.list[index].concertType)
    }

}