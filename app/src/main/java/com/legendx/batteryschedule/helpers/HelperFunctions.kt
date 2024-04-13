package com.legendx.batteryschedule.helpers

object HelperFunctions {

    fun saveSchedule(scheduleSet: ScheduleSet) {
        val currentKey = DataManage.getData("currentKey")
        val finalData =
            "${scheduleSet.id}^${scheduleSet.batteryLevel}^${scheduleSet.scheduleMessage}"
        currentKey?.let { value ->
            DataManage.saveData(value, finalData)
        }

    }

    fun saveKey() {
        val currentKey = DataManage.getData("currentKey")
        val newKey = (currentKey?.toInt() ?: 1) + 1
        DataManage.saveData("currentKey", newKey.toString())
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
                        MainFunctions.checkSchedule()
                        return
                    }
                }
            }
        }
    }

    fun removeAllSchedule() {
        val currentKey = DataManage.getData("currentKey")
        currentKey?.let {
            for (i in 1..it.toInt()) {
                DataManage.removeData(i.toString())
            }
        }
        DataManage.removeData("currentKey")
    }

}

object MainFunctions{
    fun checkSchedule(){
        val allSchedule = HelperFunctions.allSchedule()
        DataManage.getData("isSchedule").toBoolean()
        if (allSchedule.isNotEmpty()){
            DataManage.saveData("isSchedule", "true")
        }
        else{
            DataManage.saveData("isSchedule", "false")
        }
    }
}


data class ScheduleSet(
    val id: Int,
    val batteryLevel: Int,
    val scheduleMessage: String
)