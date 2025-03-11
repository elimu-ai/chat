package ai.elimu.chat.dao.converter;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Calendar;

public class CalendarConverter implements PropertyConverter<Calendar, Long> {

    @Override
    public Calendar convertToEntityProperty(Long databaseValue) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(databaseValue);
        return calendar;
    }

    @Override
    public Long convertToDatabaseValue(Calendar entityProperty) {
        Long databaseValue = entityProperty.getTimeInMillis();
        return databaseValue;
    }
}
