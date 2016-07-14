package chat.literacyapp.org.chat.main;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import chat.literacyapp.org.chat.R;
import org.literacyapp.chat.bluetooth.ChatServerListFragment;
import chat.literacyapp.org.chat.session.ChatSessionActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends AppCompatActivity implements ChatServerListFragment.ChatServerListDelegate {

    //will use this later to make a two pane layout of list of devices, and chat for tablet
    private boolean mTwoPane;
    private int connectionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        // Set up the toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent() != null) {
            Intent intent = getIntent();
            //test as client
            connectionType = intent.getIntExtra("connectionType", 1);
        }


        if (null == savedInstanceState) {
            initFragment(MainFragment.newInstance(connectionType));
        }

    }

    private void initFragment(Fragment fragment) {
        // Add the ChatesFragment to the layout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.contentFrame, fragment);
        transaction.commit();
    }


    @Override
    public void onItemSelected(BluetoothDevice device, BTDevicesAdapter.BTDevicesAdapterViewHolder vh, Dialog dialog) {

        dialog.dismiss();
    }
}
