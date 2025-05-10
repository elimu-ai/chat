package ai.elimu.chat.dao.converter

import org.greenrobot.greendao.converter.PropertyConverter
import java.util.Calendar

class CalendarConverter : PropertyConverter<Calendar?, Long?> {

    override fun convertToEntityProperty(databaseValue: Long?): Calendar? {
        val calendar = Calendar.getInstance()
        databaseValue?.let {
            calendar.setTimeInMillis(databaseValue)
        }
        return calendar
    }

    override fun convertToDatabaseValue(entityProperty: Calendar?): Long? {
        val databaseValue = entityProperty?.getTimeInMillis()
        return databaseValue
    }
}
