package chat.literacyapp.org.main;

import android.bluetooth.BluetoothDevice;

/**
 * This specifies the contract between the view and the presenter
 */
public interface MainContract {
    interface View {
        void setProgressIndicator(boolean active);

        void showChatUi(BluetoothDevice device);

    }

    interface UserActionListener {

    }
}
