package tk.talcharnes.unborify.MainNavigationFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tk.talcharnes.unborify.Dialogs.LocationMenuDialogFragment;
import tk.talcharnes.unborify.Models.DealsAdapter;
import tk.talcharnes.unborify.R;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by khuramchaudhry on 9/29/17.
 * This fragment displays the deals screen.
 */
@RuntimePermissions
public class DealsFragment extends Fragment{
    private static final int REQUEST_COARSE_LOCATIONS =0 ;

    // Member Variables
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = DealsFragment.class.getSimpleName();

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // App ID to use Zipcode data
    private final String APP_ID = "FylI6ZXJKGxkbgt8JxzQYj3l4NHKb9I3zeh8WYqczf3AOAYHvqqUIIlAFtApdW7f";
    private final String ZIPCODE_API="https://www.zipcodeapi.com/rest/FylI6ZXJKGxkbgt8JxzQYj3l4NHKb9I3zeh8WYqczf3AOAYHvqqUIIlAFtApdW7f/radius.json";
    private final String test="https://www.zipcodeapi.com/rest/FylI6ZXJKGxkbgt8JxzQYj3l4NHKb9I3zeh8WYqczf3AOAYHvqqUIIlAFtApdW7f/radius.json/30319/10/mile?minimal";
    // Time between location updates (45 Seconds and 2 Seconds)
    private long UPDATE_INTERVAL = 45 * 1000;
    private long FASTEST_INTERVAL = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_deals, container, false);
        mRecyclerView = rootView.findViewById(R.id.deals);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        DealsFragmentPermissionsDispatcher.startUserLocationWithPermissionCheck(DealsFragment.this);
        List<String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            input.add("Test" + i);
        }// define an adapter
        mAdapter = new DealsAdapter(input);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return rootView;
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    public void startUserLocation(){
        // Create the location request to start receiving updates
        LocationRequest mLocationRequest= new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation(),1);
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location, int distance) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

        final Geocoder gcd = new Geocoder(getContext());
        try {
            List<Address> addresses= gcd.getFromLocation(location.getLatitude(),location.getLongitude(),distance);
            for (Address address: addresses){
                if (address.getLocality()!=null && address.getPostalCode() != null){
                    Log.d("location", address.getPostalCode());
                    RequestParams params = new RequestParams();
//                    params.put("zip_code",address.getPostalCode());
//                    params.put("distance",distance);
//                    params.put("units","mile?minimal");
                    networking(params);
                }else{
                    Log.e("no postal", "no postal code");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(getContext());

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location,10);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void networking(RequestParams params) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(test, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Deals", "Success! JSON: " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Deals", "Fail " + e.toString());
                Log.d("Deals", "Status code " + statusCode);

            }

        });
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),resultCode,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(), "Location Updates");
            }

            return false;
        }
    }
    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_COARSE_LOCATIONS);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        DealsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }


    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showRationaleForLocation(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.permission_location_rationale)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.proceed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        request.cancel();
                    }
                }
                ).show();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showDeniedForLocation() {
        Toast.makeText(getContext(), R.string.permission_Location_denied, Toast.LENGTH_SHORT).show();
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showNeverAskForCamera() {
        Toast.makeText(getContext(), R.string.permission_camera_neverask, Toast.LENGTH_SHORT).show();
    }


    private void generateZipcodes(RequestParams params){


    }

    private void updateDeals(ArrayList<String> deals){

    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_deals,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_location:
                LocationMenuDialogFragment menu =  new LocationMenuDialogFragment();
                //show  pop up dialog
                menu.show(getActivity().getFragmentManager(),"Menu Options");
                    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}