package chat.literacyapp.org.main;

import android.support.annotation.NonNull;

/**
 * Created by oscarmakala on 05/07/2016.
 */
public class MainPresenter implements MainContract.UserActionListener {
    private final MainContract.View mChatView;

    public MainPresenter(@NonNull MainContract.View chatView) {
        mChatView = chatView;
    }
}
