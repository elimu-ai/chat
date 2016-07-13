package chat.literacyapp.org.chat.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by oscarmakala on 13/07/2016.
 * Helper class for bluetooth
 */
public class BluetoothHelper {
    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothAdapter mBluetoothAdapter;
        if (Build.VERSION.SDK_INT >= 18) {
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }


}
