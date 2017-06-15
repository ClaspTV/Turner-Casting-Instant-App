package com.turner.castinginstantapp.feature;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazon.whisperplay.install.InstallDiscoveryController;
import com.amazon.whisperplay.install.RemoteInstallService;
import com.turner.instantappexample.feature.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Debugging TAG
    private static final String TAG = MainActivity.class.getName();

    // Set selected player from device picker
    private RemoteInstallService mCurrentDevice;

    // Discovery controller that triggers start/stop discovery
    private InstallDiscoveryController mController;

    // Lock object for mDeviceList synchronization
    private final Object mDeviceListAvailableLock = new Object();

    // Set the discovered devices from Discovery controller
    private List<RemoteInstallService> mDeviceList = new LinkedList<>();

    // Comparator to sort device list with alphabet device name order
    private RemoteInstallServiceComp mComparator = new RemoteInstallServiceComp();

    // Device list
    private ArrayAdapter<String> mDeviceListAdapter;
    private ArrayList<String> mDeviceListViewData = new ArrayList<>();
    private ListView mDeviceListView;

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create DiscoveryInstallService
        mController = new InstallDiscoveryController(this);

        // Initialize UI resources
        mDeviceListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, mDeviceListViewData);

        TextView deviceListHeaderView = (TextView) LayoutInflater.from(this)
                .inflate(R.layout.view_list_header, null);

        mDeviceListView = (ListView) findViewById(R.id.list_view);
        mDeviceListView.setEmptyView(findViewById(R.id.empty_view));
        mDeviceListView.addHeaderView(deviceListHeaderView);
        mDeviceListView.setAdapter(mDeviceListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start Discovery InstallService
        Log.i(TAG, "onResume - start Discovery");
        mController.start(mDiscoveryListener);
    }

    @Override
    protected void onPause() {
        mCurrentDevice = null;
        mController.stop();
        super.onPause();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Amazon Fire TV Discovery
    ///////////////////////////////////////////////////////////////////////////

    private InstallDiscoveryController.IInstallDiscoveryListener mDiscoveryListener =
            new InstallDiscoveryController.IInstallDiscoveryListener() {

                @Override
                public void installServiceDiscovered(RemoteInstallService remoteInstallService) {
                    synchronized (mDeviceListAvailableLock) {
                        int threadId = android.os.Process.myTid();

                        if (mDeviceList.contains(remoteInstallService)) {
                            mDeviceList.remove(remoteInstallService);
                            Log.i(TAG, "[" + threadId + "]" + "serviceDiscovered(updating): " + remoteInstallService.getName());
                        } else {
                            Log.i(TAG, "[" + threadId + "]" + "serviceDiscovered(adding): " + remoteInstallService.getName());
                        }

                        mDeviceList.add(remoteInstallService);
                        triggerUpdate();
                    }
                }

                @Override
                public void installServiceLost(RemoteInstallService remoteInstallService) {
                    synchronized (mDeviceListAvailableLock) {

                        if (mDeviceList.contains(remoteInstallService)) {
                            int threadId = android.os.Process.myTid();
                            Log.i(TAG, "[" + threadId + "]" + "serviceDiscovered(removing): " + remoteInstallService.getName());

                            if (remoteInstallService.equals(mCurrentDevice)) {
                                mCurrentDevice = null;
                            }
                            mDeviceList.remove(remoteInstallService);
                            triggerUpdate();
                        }
                    }
                }

                @Override
                public void discoveryFailure() {
                    Log.e(TAG, "Discovery Failure");
                }

                private void triggerUpdate() {
                    // It should be run in main thread since it is updating Adapter.
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Collections.sort(mDeviceList, mComparator);
                            mDeviceListViewData.clear();

                            for (RemoteInstallService device : mDeviceList) {
                                mDeviceListViewData.add(device.getName());
                            }
                            mDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };

    private static class RemoteInstallServiceComp implements Comparator<RemoteInstallService> {
        @Override
        public int compare(RemoteInstallService service1, RemoteInstallService service2) {
            return service1.getName().compareTo(service2.getName());
        }
    }
}
