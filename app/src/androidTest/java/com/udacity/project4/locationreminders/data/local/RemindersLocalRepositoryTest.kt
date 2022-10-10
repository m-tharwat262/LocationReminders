package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        // GIVEN - A new reminder saved in the database.
        val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        localRepository.saveReminder(reminderDTO1)

        // WHEN  - Reminder retrieved by ID.
        val result = localRepository.getReminder(reminderDTO1.id)

        // THEN - Same reminder is returned.
        result as Result.Success
        assertThat(result.data.title, `is`("title1"))
        assertThat(result.data.description, `is`("description1"))
        assertThat(result.data.location, `is`("location1"))
        assertThat(result.data.latitude, `is`(10.0))
        assertThat(result.data.longitude, `is`(20.0))
    }

    @Test
    fun getReminders() = runBlocking {
        // GIVEN -Save reminders to the database
        val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        val reminderDTO2 = ReminderDTO("title2", "description2", "location2", 11.0, 21.0)
        val reminderDTO3 = ReminderDTO("title3", "description3", "location3", 12.0, 22.0)
        localRepository.saveReminder(reminderDTO1)
        localRepository.saveReminder(reminderDTO2)
        localRepository.saveReminder(reminderDTO3)

        // Fetch reminders
        val result = localRepository.getReminders()
        result as Result.Success
        assertThat(result.data.size, `is`(3))
    }

    @Test
    fun deletetReminders() = runBlocking {
        // GIVEN -Save reminders to the database
        val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        val reminderDTO2 = ReminderDTO("title2", "description2", "location2", 11.0, 21.0)
        val reminderDTO3 = ReminderDTO("title3", "description3", "location3", 12.0, 22.0)
        localRepository.saveReminder(reminderDTO1)
        localRepository.saveReminder(reminderDTO2)
        localRepository.saveReminder(reminderDTO3)

        // Delete reminders
        localRepository.deleteAllReminders()

        val result2 = localRepository.getReminders()
        result2 as Result.Success
        assertThat(result2.data.size, `is`(0))
    }
}