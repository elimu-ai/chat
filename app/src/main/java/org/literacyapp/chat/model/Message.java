package org.literacyapp.chat.model;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.literacyapp.chat.dao.converter.CalendarConverter;

import java.util.Calendar;

@Entity
public class Message {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private String deviceId;

    private String studentId;

    private String studentAvatar;

    @NotNull
    @Convert(converter = CalendarConverter.class, columnType = Long.class)
    private Calendar timeSent;

    @NotNull
    private String text;

    @Generated(hash = 449884345)
    public Message(Long id, @NotNull String deviceId, String studentId,
            String studentAvatar, @NotNull Calendar timeSent,
            @NotNull String text) {
        this.id = id;
        this.deviceId = deviceId;
        this.studentId = studentId;
        this.studentAvatar = studentAvatar;
        this.timeSent = timeSent;
        this.text = text;
    }

    @Generated(hash = 637306882)
    public Message() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentAvatar() {
        return this.studentAvatar;
    }

    public void setStudentAvatar(String studentAvatar) {
        this.studentAvatar = studentAvatar;
    }

    public Calendar getTimeSent() {
        return this.timeSent;
    }

    public void setTimeSent(Calendar timeSent) {
        this.timeSent = timeSent;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
