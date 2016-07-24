package org.literacyapp.chat.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.literacyapp.chat.dao.converter.CalendarConverter;

import java.util.Calendar;

@Deprecated
public class Message {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String deviceId;

    @NotNull
    @Convert(converter = CalendarConverter.class, columnType = Long.class)
    private Calendar timeSent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Calendar getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Calendar timeSent) {
        this.timeSent = timeSent;
    }
}
