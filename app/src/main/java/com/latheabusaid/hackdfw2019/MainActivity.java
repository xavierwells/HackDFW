package com.latheabusaid.hackdfw2019;

import android.graphics.Color;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    public static final String TAG = "MainActivity";
    private BeaconManager beaconManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //RangingActivity testActivity = new RangingActivity();
        //testActivity.onBeaconServiceConnect();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        /*
         To detect proprietary beacons, you must add a line like below corresponding to your beacon
         type.  Do a web search for "setBeaconLayout" to get the proper expression.
         */
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);

        FirebaseApp.initializeApp(this);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        //addVehicle test
//        addVehicle(db,"licence","make","model");

        final TextInputLayout makein;
        makein = (TextInputLayout) findViewById(R.id.makein);

        final TextInputLayout name;
        name = (TextInputLayout) findViewById(R.id.name);

        final TextInputLayout model;
        model = (TextInputLayout) findViewById(R.id.model);

        final TextInputLayout license;
        license = (TextInputLayout) findViewById(R.id.license);

        Button mapButton;
        mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String licensePlateStr = license.getEditText().getText().toString();
                final String makeStr = makein.getEditText().getText().toString();
                final String nameStr = name.getEditText().getText().toString();
                final String modelStr = model.getEditText().getText().toString();
                Log.d(TAG, nameStr + " " + licensePlateStr + " " + makeStr + " " + modelStr);
                addVehicle(db,nameStr,licensePlateStr,makeStr,modelStr);
                setContentView(R.layout.activity_map);

                //Spot3
                Button spot3;
                spot3 = (Button) findViewById(R.id.button3);
                spot3.setBackgroundColor(Color.GREEN);

                isParkingSpaceFull(db,1, spot3);

            }
        });

        setParkingSpace(db,1,true);
    }

    @Override
    protected void onDestroy() {
        Log.d(MainActivity.TAG, "onDestroy was called");
        super.onDestroy();
        beaconManager.unbind(this);
    }

//    @Override
    public void onBeaconServiceConnect() {
        Log.d(MainActivity.TAG, "onBeaconServiceConnect was called");
        Region region = new Region("myBeacons", Identifier.parse("669ce5ab-dda9-4cc8-9af8-317768fcdb05"), null, null);
        // Region region1 = new Region("myBeacons", Identifier.parse("669ce5ab-dda9-4cc8-9af8-317768fcdb05"), null, null);
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
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

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
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

        public void addVehicle(final FirebaseFirestore db, String name, String licencePlateNum, String make, String model) {
        //store passed info to car object
        Map<String, Object> car = new HashMap<>();
        car.put("Name", name);
        car.put("Licence Plate", licencePlateNum);
        car.put("Make", make);
        car.put("Model", model);

        //Add car to database
        db.collection("Cars")
                .add(car)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void setParkingSpace (FirebaseFirestore db, int spotNum, boolean occupied) {
        //store passed info to parkingSpace object
        Map<String, Object> parkingSpace = new HashMap<>();
        parkingSpace.put("Occupied", occupied);

        //Write parkingSpace info to database
        db.collection("Parking Spaces").document("Spot" + spotNum)
                .set(parkingSpace)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    //TODO: make this function read the data from the document and return the occupied value
    public void isParkingSpaceFull (FirebaseFirestore db, int spotNum, final Button spot) {
        DocumentReference docRef = db.collection("Parking Spaces").document("Spot" + spotNum);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getBoolean("Occupied")) {
                            spot.setBackgroundColor(Color.RED);
                        }
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Log.d(TAG, "Parking spot occupied: " + document.getBoolean("Occupied"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}
