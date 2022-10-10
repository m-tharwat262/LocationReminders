package com.udacity.project4.locationreminders.data.local

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeAndroidTestRepository() :
    ReminderDataSource {

    var reminders: MutableList<ReminderDTO>? = mutableListOf()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        reminders?.let {
            return Result.Success(ArrayList(it))
        }
        return Result.Error(
            "Reminders Not Found"
        )
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        reminders?.let {
            for (reminder in it) {
                if (id == reminder.id) {
                    return Result.Success(reminder)
                }
            }
        }
        return Result.Error("Reminder Not Found")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}