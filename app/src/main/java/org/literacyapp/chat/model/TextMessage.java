package org.literacyapp.chat.model;

import org.greenrobot.greendao.annotation.NotNull;

public class TextMessage extends Message {

    @NotNull
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
