package org.literacyapp.chat.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.literacyapp.chat.dao.converter.CalendarConverter;

import java.util.Calendar;

@Entity
public class TextMessage /*extends Message*/ {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String deviceId;

    @NotNull
    @Convert(converter = CalendarConverter.class, columnType = Long.class)
    private Calendar timeSent;


    @NotNull
    private String text;

    @Generated(hash = 394821772)
    public TextMessage(Long id, @NotNull String deviceId,
            @NotNull Calendar timeSent, @NotNull String text) {
        this.id = id;
        this.deviceId = deviceId;
        this.timeSent = timeSent;
        this.text = text;
    }

    @Generated(hash = 1667976043)
    public TextMessage() {
    }

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
