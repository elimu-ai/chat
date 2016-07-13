package chat.literacyapp.org.chat.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import chat.literacyapp.org.chat.btDiscovery.BluetoothDiscoveryActivity;
import chat.literacyapp.org.chat.utils.BluetoothHelper;

/**
 * Created by oscarmakala on 12/07/2016.
 */
public class ConnectionService extends Service {

    private static final String TAG = ConnectionService.class.getSimpleName();
    private BluetoothAdapter mBluetoothAdapter;

    private static final List<UUID> UUID_LIST;

    static {
        ArrayList<UUID> uuid = new ArrayList();
        uuid.add(UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666"));
        uuid.add(UUID.fromString("503c7430-bc23-11de-8a39-0800200c9a66"));
        uuid.add(UUID.fromString("503c7431-bc23-11de-8a39-0800200c9a66"));
        uuid.add(UUID.fromString("503c7432-bc23-11de-8a39-0800200c9a66"));
        uuid.add(UUID.fromString("503c7433-bc23-11de-8a39-0800200c9a66"));
        uuid.add(UUID.fromString("503c7434-bc23-11de-8a39-0800200c9a66"));
        uuid.add(UUID.fromString("503c7435-bc23-11de-8a39-0800200c9a66"));
        UUID_LIST = Collections.unmodifiableList(uuid);
    }

    private HashMap<String, BluetoothSocket> mBluetoothSockets;
    private ArrayList<String> mBluetoothDeviceAddresses;
    private IConnectionCallback mIconIConnectionCallback;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
        mBluetoothSockets = new HashMap<>();
        mBluetoothDeviceAddresses = new ArrayList<>();
        mBluetoothAdapter = BluetoothHelper.getBluetoothAdapter(ConnectionService.this);

    }

    private String mPackageName = "";
    private final IConnection.Stub mBinder = new IConnection.Stub() {
        @Override
        public String getAddress() throws RemoteException {
            return mBluetoothAdapter.getAddress();
        }

        @Override
        public String getName() throws RemoteException {
            return mBluetoothAdapter.getName();
        }

        @Override
        public int startServer(String packageName, int maxConnections) throws RemoteException {
            if (mPackageName.length() > 0) {
                return Connection.FAILURE;
            }
            mPackageName = packageName;
            new Thread(new AcceptThread(mPackageName, maxConnections)).start();

            //Ensure bluetooth discovery is enabled.
            Intent intent = new Intent(ConnectionService.this, BluetoothDiscoveryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return Connection.SUCCESS;
        }

        @Override
        public int unregisterCallback(String packageName) throws RemoteException {
            return Connection.SUCCESS;
        }

        @Override
        public int registerCallback(String packageName, IConnectionCallback cb) throws RemoteException {
            return 0;
        }

        @Override
        public void shutdown(String packageName) throws RemoteException {

        }

        @Override
        public int connect(String packageName, String device) throws RemoteException {
            if (mPackageName.length() > 0) {
                return Connection.FAILURE;
            }
            mPackageName = packageName;
            BluetoothDevice bluetoothServer = mBluetoothAdapter.getRemoteDevice(device);
            BluetoothSocket bluetoothSocket = null;
            for (int i = 0; i < Connection.MAX_SUPPORTED && bluetoothSocket == null; i++) {
                for (int j = 0; j < 3 && bluetoothSocket == null; j++) {
                    bluetoothSocket = getConnectedSocket(bluetoothServer, UUID_LIST.get(i));
                    if (bluetoothSocket == null) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "InterruptedException in connect", e);
                        }
                    }
                }
            }
            if (bluetoothSocket == null) {
                return Connection.FAILURE;
            }
            mBluetoothSockets.put(device, bluetoothSocket);
            mBluetoothDeviceAddresses.add(device);
            return Connection.SUCCESS;
        }
    };

    private BluetoothSocket getConnectedSocket(BluetoothDevice bluetoothServer, UUID uuid) {
        BluetoothSocket bluetoothSocket;
        try {
            bluetoothSocket = bluetoothServer.createInsecureRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            return bluetoothSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private class AcceptThread implements Runnable {
        private final String cPackageName;
        private int maxConnections;

        public AcceptThread(String packageName, int connections) {
            cPackageName = packageName;
            maxConnections = connections;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < Connection.MAX_SUPPORTED && maxConnections > 0; i++) {
                    //get bluetooth server socket.loop through to find available
                    BluetoothServerSocket myServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(cPackageName, UUID_LIST.get(i));
                    BluetoothSocket bluetoothSocket = myServerSocket.accept();
                    //close the socket now that connecion made
                    myServerSocket.close();

                    String address = bluetoothSocket.getRemoteDevice().getAddress();
                    mBluetoothSockets.put(address, bluetoothSocket);
                    mBluetoothDeviceAddresses.add(address);

                    maxConnections = maxConnections - 1;
                    if (mIconIConnectionCallback != null) {
                        mIconIConnectionCallback.incomingConnection(address);
                    }

                }
                if (mIconIConnectionCallback != null) {
                    mIconIConnectionCallback.maxConnectionsReached();
                }
            } catch (IOException e) {
                Log.i(TAG, " IOException AcceptThread", e);
            } catch (RemoteException e) {
                Log.i(TAG, "RemoteException AcceptThread", e);
            }
        }
    }
}
