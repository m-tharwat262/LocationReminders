package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        // GIVEN - save a reminder
        val reminderDTO = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        database.reminderDao().saveReminder(reminderDTO)

        // WHEN - Get the reminder by id from the database.
        val savedReminder = database.reminderDao().getReminderById(reminderDTO.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(savedReminder as ReminderDTO, notNullValue())
        assertThat(savedReminder.id, `is`(reminderDTO.id))
        assertThat(savedReminder.title, `is`(reminderDTO.title))
        assertThat(savedReminder.description, `is`(reminderDTO.description))
        assertThat(savedReminder.location, `is`(reminderDTO.location))
        assertThat(savedReminder.latitude, `is`(reminderDTO.latitude))
        assertThat(savedReminder.longitude, `is`(reminderDTO.longitude))
    }

    @Test
    fun getReminders() = runBlockingTest {
        val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        val reminderDTO2 = ReminderDTO("title2", "description2", "location2", 11.0, 21.0)
        val reminderDTO3 = ReminderDTO("title3", "description3", "location3", 12.0, 22.0)
        database.reminderDao().saveReminder(reminderDTO1)
        database.reminderDao().saveReminder(reminderDTO2)
        database.reminderDao().saveReminder(reminderDTO3)

        // Fetch reminders
        val reminders = database.reminderDao().getReminders()

        assertThat(reminders.size, `is`(3))
    }

    @Test
    fun deleteReminders() = runBlockingTest {
        val reminderDTO1 = ReminderDTO("title1", "description1", "location1", 10.0, 20.0)
        val reminderDTO2 = ReminderDTO("title2", "description2", "location2", 11.0, 21.0)
        val reminderDTO3 = ReminderDTO("title3", "description3", "location3", 12.0, 22.0)
        database.reminderDao().saveReminder(reminderDTO1)
        database.reminderDao().saveReminder(reminderDTO2)
        database.reminderDao().saveReminder(reminderDTO3)

        // Delete reminders
        database.reminderDao().deleteAllReminders()

        // Fetch reminders
        val reminders = database.reminderDao().getReminders()

        assertThat(reminders.size, `is`(0))
    }

    @After
    fun closeDb() = database.close()

}