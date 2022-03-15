package myrehabcare.in.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import myrehabcare.in.JSB.Jsb;
import myrehabcare.in.R;
import myrehabcare.in.databinding.ActivityLocationBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity {

    private ActivityLocationBinding binding;
    private Activity activity;
    private Jsb jsb;

    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;

    private LatLng mLatLong;
    private String addresss;


    GoogleMap mGoogleMap;

    private PlacesClient placesClient;
    private List<AutocompletePrediction> autocompletePredictions;

    private ProgressDialog progressDialog;
    private boolean djldls = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        jsb = new Jsb(activity);
        progressDialog = jsb.getProgressDialog();
        progressDialog.show();

        Places.initialize(activity, getString(R.string.MapKey));
        placesClient = Places.createClient(activity);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        binding.mater.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        binding.mater.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= autocompletePredictions.size()){
                    return;
                }
                AutocompletePrediction autocompletePrediction = autocompletePredictions.get(position);
                binding.mater.clearSuggestions();
                binding.mater.setText("");
                hideKeyboard(activity);

                String placeId = autocompletePrediction.getPlaceId();

                List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG);
                FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId,placeField).build();

                placesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng latLng = place.getLatLng();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException){
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            Log.d("TAG", "onFailure: "+apiException.getStatusCode());
                            Log.d("TAG", "onFailure: "+apiException.getMessage());
                        }
                    }
                });
            }
            @Override
            public void OnItemDeleteListener(int position, View v) {}
        });

        binding.mater.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, final int before, int count) {
                if (!s.toString().isEmpty()) {
                    try {
                        final FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest
                                .builder()
                                .setCountry("in")
                                .setSessionToken(token)
                                .setQuery(s.toString())
                                .build();

                        placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                                if (task.isSuccessful()) {
                                    FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                                    if (predictionsResponse != null) {
                                        autocompletePredictions = predictionsResponse.getAutocompletePredictions();
                                        List<String> list = new ArrayList<>();
                                        for (AutocompletePrediction autocompletePrediction : autocompletePredictions) {
                                            list.add(autocompletePrediction.getFullText(null).toString());

                                        }

                                        binding.mater.updateLastSuggestions(list);
                                        if (!binding.mater.isSuggestionsVisible()) {
                                            binding.mater.showSuggestionsList();
                                        }
                                    }
                                } else {

                                    Log.e(activity.getPackageName(), "error " + task.getException().getMessage());
                                    task.getException().printStackTrace();
                                }
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


//        getSupportActionBar().setTitle("Search a Place");


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        client = LocationServices.getFusedLocationProviderClient(activity);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1111);
        }




        binding.submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLatLong != null){

                    progressDialog.show();

                    RequestBody requestBody = new FormBody.Builder()
                            .add("doctor_id", jsb.getUser().getUser_id())
                            .add("latitude", String.valueOf(mLatLong.latitude))
                            .add("longitude", String.valueOf(mLatLong.longitude))
                            .build();

                    jsb.getPreferences().edit().putString("address", addresss).apply();

                    jsb.post(getString(R.string.baseUrl) + "api/customer_location", requestBody, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()){
                                finish();
                            }else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                }else {
                    getCurrentLocation();
                    //Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        seeet();

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (djldls) {
            getCurrentLocation();
            djldls = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1111){
            if ( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else {
                finish();
            }
        }else {
            finish();
        }
    }

    private boolean isLocationEnabled() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager lm = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ex) {}

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ex) {}

            if(gps_enabled && network_enabled) {
                return true;
            }
            return false;

        } else {
            int mode = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    private void getCurrentLocation() {

        if (isLocationEnabled()) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            Task<Location> task = client.getLastLocation();

            task.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(activity, 51);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    }
                }
            });

            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    if (location != null) {
                        final LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));

                        jsb.getPreferences().edit().putString("latLng", latLng.latitude+":::"+ latLng.longitude).apply();
                        mLatLong = latLng;
                    }
                }
            });

        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

            // Setting DialogHelp Title
            alertDialog.setTitle("GPS is settings");


            // Setting DialogHelp Message
            alertDialog
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            djldls = true;
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivity(intent);
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        }

    }


    private void seeet() {
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                progressDialog.dismiss();
                mGoogleMap = googleMap;

                mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        return false;
                    }
                });

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    googleMap.setMyLocationEnabled(true);
                }


                mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {


                        LatLng latLng = mGoogleMap.getCameraPosition().target;

                        Geocoder geocoder;
                        List<Address> addresses;
                        geocoder = new Geocoder(activity, Locale.getDefault());
                        String address;
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        } catch (IOException e) {
                            e.printStackTrace();
                            address = "Error";
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            address = "Error";
                        }
                        binding.address.setText(address);
                        addresss = address;

                        mLatLong = latLng;

                    }
                });


                String latLng = jsb.getPreferences().getString("latLng", null);
                LatLng latLngg;
                if (latLng != null){
                    double latitude = Double.parseDouble(latLng.split(":::")[0]);
                    double longitude = Double.parseDouble(latLng.split(":::")[1]);

                    latLngg = new LatLng(latitude, longitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngg, 16f));
                }else {
                    latLngg = new LatLng(20.5937, 78.9629);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngg, 5f));
                }



                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mLatLong = latLng;
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                    }
                });

            }
        });
    }



}