package tk.talcharnes.unborify.MainNavigationFragments.Deals;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tk.talcharnes.unborify.Dialogs.LocationMenuDialogFragment;
import tk.talcharnes.unborify.Models.DealsModel;
import tk.talcharnes.unborify.Models.DealsOptionsModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Services.SQLiteDatabaseHandlerDeals;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by Marzin.
 * This fragment displays the deals screen.
 */
@RuntimePermissions
public class DealsFragment extends Fragment{

    private static final String TAG = DealsFragment.class.getSimpleName();
    private static final int REQUEST_COARSE_LOCATIONS = 0;

    // Member Variables
    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter mFirebaseAdapter;
    private String country;
    private String zipcode;
    private Activity activity;
//    /*
//     * Define a request code to send to Google Play services This code is
//     * returned in Activity.onActivityResult
//     */
//    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // App ID to use Zipcode data
    //private final String APP_ID = "FylI6ZXJKGxkbgt8JxzQYj3l4NHKb9I3zeh8WYqczf3AOAYHvqqUIIlAFtApdW7f";
    private final String ZIPCODE_API="https://www.zipcodeapi.com/rest/FylI6ZXJKGxkbgt8JxzQYj3l4NHKb9I3zeh8WYqczf3AOAYHvqqUIIlAFtApdW7f/radius.json";
    private final String test="https://www.zipcodeapi.com/rest/FylI6ZXJKGxkbgt8JxzQYj3l4NHKb9I3zeh8WYqczf3AOAYHvqqUIIlAFtApdW7f/radius.json/30319/10/mile?minimal";

    // Time between location updates (45 Seconds and 2 Seconds)
    private long UPDATE_INTERVAL = 45 * 1000;
    private long FASTEST_INTERVAL = 2000;

    /**
     * OnCreateView will inflate the deals that comeback based on the location of the device
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_deals, container, false);

        mRecyclerView = rootView.findViewById(R.id.deals);
        activity = getActivity();

        setHasOptionsMenu(true);
        setUpFirebaseAdapter();
        //buildDummyDeals();



        return rootView;
    }

    public void buildDummyDeals() {
        DealsModel[] deals = {
                new DealsModel("Moschino", "https://www.moschino.com/us",
                        34.99, "Dec-20-2017", "It's an awesome deal"),
                new DealsModel("Valentino", "https://www.valentino.com/us",
                        36.99, "Dec-20-2018", "It's an awesome deal I think"),
                new DealsModel("Supreme", "http://www.supremenewyork.com/",
                        35.99, "Dec-20-2099", "You will buy this"),
        };
        for(DealsModel deal : deals) {
            DatabaseContants.getDealsRef().push().setValue(deal);
        }
    }

    /**
     * This method retrieves the comments for the photo and displays them.
     */
    private void setUpFirebaseAdapter() {
        FirebaseRecyclerOptions<DealsModel> options =
                new FirebaseRecyclerOptions.Builder<DealsModel>()
                        .setQuery(DatabaseContants.getDealsRef(), DealsModel.class).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<DealsModel, DealsAdapter>
                (options) {

            @Override
            protected void onBindViewHolder(DealsAdapter holder, int position, DealsModel deal) {
                Log.d(TAG, deal.getName());
                holder.onBindDeal(deal);
            }

            @Override
            public DealsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(activity)
                        .inflate(R.layout.deal_row_layout, parent, false);

                return new DealsAdapter(view);
            }
        };

        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setAdapter(mFirebaseAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Basic option menu setup for fragment
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_deals, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Menu options launches dialog
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_location:
                LocationMenuDialogFragment menu =  new LocationMenuDialogFragment();
                menu.show(activity.getFragmentManager(),"Menu Options");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * StartUserLocation gets the current location of the user
     */
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

    /**
     * onLocationChanged when Location has been updated
     * @param location
     * @param distance
     */
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
                    country=address.getCountryName();
                    zipcode=address.getPostalCode();
                    RequestParams params = new RequestParams();
//                    params.put("zip_code",address.getPostalCode());
//                    params.put("distance",distance);
//                    params.put("units","mile?minimal");
                    retrieveZipcodes(params);
                }else{
                    Log.e("no postal", "no postal code");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param params
     */
    private void retrieveZipcodes(RequestParams params) {
        String apiCall=buildUrl(ZIPCODE_API);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(apiCall, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Deals", "Success! JSON: " + response.toString());

                //call deals adapter with firebase
                setUpFirebaseAdapter();
                //DealsAdapter deals= new DealsAdapter(response);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Deals", "Fail " + e.toString());
                Log.d("Deals", "Status code " + statusCode);

            }

        });
    }

    /**
     * Builds url to for zipcodes API
     */
    public String buildUrl(String baseUrl){
        StringBuilder url= new StringBuilder();
        int radius;
        String units;
        SQLiteDatabaseHandlerDeals db= new SQLiteDatabaseHandlerDeals(this.getActivity());
        DealsOptionsModel deals=db.getDealsOptionsModel();
        if (deals==null){
            radius=10;
            deals.setRadius(radius);
            if (country.equalsIgnoreCase("United States")){
                units="mile";
                deals.setMetric(units);
            }else{
                units="km";
                deals.setMetric(units);
            }
        }else{
            radius=deals.getRadius();
            units=deals.getMetric();
        }

        url.append(baseUrl+"/"+zipcode+"/"+radius+"/"+units);
        String completeUrl=String.valueOf(url);

        return completeUrl;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        DealsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * Ask why we need to use location
     */
    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showRationaleForCamera(final PermissionRequest request) {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.permission_location_rationale)
                .setPositiveButton(android.R.string.ok, (dialog, button) -> request.proceed())
                .setNegativeButton(android.R.string.no, (dialog, button) -> request.cancel())
                .show();
    }

    /**
     * if users denies permission to location
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showDeniedForLocation() {
        new AlertDialog.Builder(activity)
                .setMessage(R.string.permission_Location_denied)
                .setPositiveButton(android.R.string.ok, (dialog, button) -> dialog.dismiss())
                .show();
    }

    /**
     * if user never wants to see the dialog box again
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showNeverAskForCamera() {

    }

    /**
     * Override onStart for FirebaseAdapter to start listening.
     */
    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    /**
     * Override onStop for FirebaseAdapter to stop listening.
     */
    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

}