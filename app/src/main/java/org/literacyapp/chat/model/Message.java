package org.literacyapp.chat.model;

import java.util.Calendar;

public class Message {

    private String deviceId;

    private Calendar timeSent;

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
