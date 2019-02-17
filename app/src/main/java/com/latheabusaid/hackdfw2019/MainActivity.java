package com.latheabusaid.hackdfw2019;

import android.Manifest;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Collection;
import java.util.Iterator;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements BeaconConsumer{
    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1234 );
        Log.d(MainActivity.TAG, "onCreate was called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        /*
         To detect proprietary beacons, you must add a line like below corresponding to your beacon
         type.  Do a web search for "setBeaconLayout" to get the proper expression.
         */
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);

    }

    @Override
    protected void onDestroy() {
        Log.d(MainActivity.TAG, "onDestroy was called");
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect()
    {
        Log.d(MainActivity.TAG, "onBeaconServiceConnect was called");
        Region region = new Region("myBeacons", Identifier.parse("669ce5ab-dda9-4cc8-9af8-317768fcdb05"), null, null);
       // Region region1 = new Region("myBeacons", Identifier.parse("669ce5ab-dda9-4cc8-9af8-317768fcdb05"), null, null);
        beaconManager.setMonitorNotifier(new MonitorNotifier()
        {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    Log.d(MainActivity.TAG, "try block");
                    Log.d(MainActivity.TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    Log.d(MainActivity.TAG, "catch block");

                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(MainActivity.TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.d(MainActivity.TAG, "didnd");

            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier()
        {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region)
            {
                Log.d(MainActivity.TAG, "didRangeBeaconsInRegion was called");
                //{
                    Log.d(MainActivity.TAG, "ID: " + region.getId1() + "|");
                //}
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}