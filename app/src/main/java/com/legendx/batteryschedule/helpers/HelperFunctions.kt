package com.legendx.batteryschedule.helpers

import android.util.Log

object HelperFunctions {

    fun saveSchedule(scheduleSet: ScheduleSet) {
        val currentKey = DataManage.getData("currentKey")
        val finalData =
            "${scheduleSet.id}^${scheduleSet.batteryLevel}^${scheduleSet.scheduleMessage}"
        currentKey?.let { value ->
            DataManage.saveData(value, finalData)
        }
        Log.d("Data", "Data Saved Successfully\n$finalData")
    }

    fun saveKey() {
        val currentKey = DataManage.getData("currentKey")
        val newKey = (currentKey?.toInt() ?: 1) + 1
        DataManage.saveData("currentKey", newKey.toString())
        Log.d("Data", "Key Saved Successfully\n$newKey")
    }

    fun allSchedule(): List<ScheduleSet> {
        val currentKey = DataManage.getData("currentKey")
        val allData = mutableListOf<ScheduleSet>()
        currentKey?.let {
            for (i in 1..it.toInt()) {
                val data = DataManage.getData(i.toString())
                data?.let { currentData ->
                    val dataSplit = currentData.split("^")
                    allData.add(
                        ScheduleSet(
                            dataSplit[0].toInt(),
                            dataSplit[1].toInt(),
                            dataSplit[2]
                        )
                    )
                }
            }
        }
        return allData
    }

    fun removeSchedule(scheduleSet: ScheduleSet) {
        val currentKey = DataManage.getData("currentKey")
        val finalMessage = "${scheduleSet.id}^${scheduleSet.batteryLevel}^${scheduleSet.scheduleMessage}"
        currentKey?.let {
            for (i in 1..it.toInt()) {
                val data = DataManage.getData(i.toString())
                data?.let { currentData ->
                    if (currentData == finalMessage) {
                        DataManage.removeData(i.toString())
                        Log.d("Battery1", "Schedule Removed Successfully\n$finalMessage")
                        return
                    }
                }
            }
        }
        Log.d("Battery1", "Schedule not found: $finalMessage")
    }


    fun removeAllSchedule() {
        val currentKey = DataManage.getData("currentKey")
        currentKey?.let {
            for (i in 1..it.toInt()) {
                DataManage.removeData(i.toString())
            }
        }
        DataManage.removeData("currentKey")
        Log.d("Data", "All Data Removed Successfully")
    }

}

object Authentication {
    private var password = ""
    fun leftPassword() {
        password += "9"
    }

    fun rightPassword() {
        password += "0"
    }

    fun authenticate(): Boolean {
        return if (password == "0900") {
            password = ""
            true
        } else {
            password = ""
            false
        }
    }

    fun resetDataBase(): Boolean {
        println(password)
        return if (password == "9099") {
            true
        } else {
            false
        }
    }

}


data class ScheduleSet(
    val id: Int,
    val batteryLevel: Int,
    val scheduleMessage: String
)