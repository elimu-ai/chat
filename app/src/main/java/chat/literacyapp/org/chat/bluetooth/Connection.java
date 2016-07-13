package chat.literacyapp.org.chat.bluetooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * Created by oscarmakala on 12/07/2016.
 * Class to simplify the process of establishing connection
 */
public class Connection {
    private static final String TAG = Connection.class.getSimpleName();
    public static final int SUCCESS = 0;
    public static final int FAILURE = 1;
    public static final int MAX_SUPPORTED = 7;

    private final Context mContext;
    private final String mPackageName;
    private final ServiceConnection mServiceConnection;
    private final Object mStartLock = new Object();
    private final OnConnectionServiceReadyDelegate mOnConnectionServiceReadyDelegate;
    private IConnection mIConnection;
    private boolean mStarted = false;


    private IConnectionCallback iConnectionCallback = new IConnectionCallback.Stub() {

        @Override
        public void incomingConnection(String device) throws RemoteException {

        }

        @Override
        public void maxConnectionsReached() throws RemoteException {

        }

        @Override
        public void messageReceived(String device, String message) throws RemoteException {

        }

        @Override
        public void connectionLost(String device) throws RemoteException {

        }
    };

    public Connection(Context context, OnConnectionServiceReadyDelegate onConnectionServiceReadyDelegate) {
        mContext = context;
        mPackageName = context.getPackageName();
        mOnConnectionServiceReadyDelegate = onConnectionServiceReadyDelegate;
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                synchronized (mStartLock) {
                    mIConnection = IConnection.Stub.asInterface(service);
                    mStarted = true;
                    if (mOnConnectionServiceReadyDelegate != null) {
                        mOnConnectionServiceReadyDelegate.OnConnectionServiceReady();
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                synchronized (mStartLock) {
                    try {
                        mStarted = false;
                        mIConnection.unregisterCallback(mPackageName);
                        mIConnection.shutdown(mPackageName);
                    } catch (RemoteException e) {
                        Log.e(TAG, "onServiceDisconnected", e);
                    }
                    mIConnection = null;
                }
            }
        };
        Intent intent = new Intent(mContext, ConnectionService.class);
        intent.setAction("chat.literacyapp.org.LiteracyAppChatGroupService");
        intent.addCategory("chat.literacyapp.org.LiteracyAppChatGroup");
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void shutdown() {
        try {
            mStarted = false;
            if (mIConnection != null) {
                mIConnection.shutdown(mPackageName);
            }
            mContext.unbindService(mServiceConnection);
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in shutdown", e);
        }
    }

    public int startServer(final int maxConnections, OnMaxConnectionsReachedDelegate onMaxConnectionsReachedDelegate) {
        if (!mStarted) {
            return Connection.FAILURE;
        }
        if (maxConnections > MAX_SUPPORTED) {
            Log.e(TAG, "The maximum number of connections is " + MAX_SUPPORTED);
            return Connection.FAILURE;
        }
        try {
            int result = mIConnection.startServer(mPackageName, maxConnections);
            mIConnection.registerCallback(mPackageName, iConnectionCallback);
            return result;
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException in startServer", e);
        }
        return Connection.FAILURE;
    }

    public interface OnConnectionServiceReadyDelegate {
        public void OnConnectionServiceReady();

    }


    public interface OnMaxConnectionsReachedDelegate {
        public void OnMaxConnectionsReached();
    }

}
