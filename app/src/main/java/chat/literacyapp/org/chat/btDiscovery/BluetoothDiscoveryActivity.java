package chat.literacyapp.org.chat.btDiscovery;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import chat.literacyapp.org.chat.R;

public class BluetoothDiscoveryActivity extends AppCompatActivity {

    private static final int REQUEST_DISCOVERABLE_CODE = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DISCOVERABLE_CODE) {
            // Bluetooth Discoverable Mode does not return the standard
            // Activity result codes.
            // Instead, the result code is the duration (seconds) of
            // discoverability or a negative number if the user answered "NO".
            Toast.makeText(this, "Discoverable mode enabled.", Toast.LENGTH_LONG).show();
            finish();

        }
    }
}
