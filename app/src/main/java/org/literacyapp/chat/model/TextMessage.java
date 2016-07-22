package org.literacyapp.chat.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TextMessage extends Message {

    @NotNull
    private String text;

    @Generated(hash = 1737413216)
    public TextMessage(@NotNull String text) {
        this.text = text;
    }

    @Generated(hash = 1667976043)
    public TextMessage() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
