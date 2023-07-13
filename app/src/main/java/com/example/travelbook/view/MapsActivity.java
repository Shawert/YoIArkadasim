package com.example.travelbook.view;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.travelbook.R;
import com.example.travelbook.adapter.PlaceAdapter;
import com.example.travelbook.model.Place;
import com.example.travelbook.roomdb.PlaceDao;
import com.example.travelbook.roomdb.PlaceDataBase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.travelbook.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;
    ActivityResultLauncher<String> permissionLauncher;
    SharedPreferences sharedPreferences;
    boolean info;
    PlaceDataBase placeDataBase;
    PlaceDao placeDao;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    Place selectedPlace;
    Double SlctLat,SlctLong;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        registerLauncher();
        sharedPreferences = MapsActivity.this.getSharedPreferences("com.example.travelbook",MODE_PRIVATE);
        info = false;

        placeDataBase = Room.databaseBuilder(getApplicationContext(),PlaceDataBase.class,"Places").build();
        placeDao = placeDataBase.placeDao();

        binding.saveButton.setEnabled(false);
        SlctLat =0.0;
        SlctLong =0.0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String infoFromIntent = intent.getStringExtra("info");
        if(infoFromIntent.equals("new")){
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);


            locationManager =(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    System.out.println("konumm"+location.toString());
                    info = sharedPreferences.getBoolean("info",false);
                    if(!info){
                        LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
                        sharedPreferences.edit().putBoolean("info",true).apply();
                    }
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    LocationListener.super.onStatusChanged(provider, status, extras);
                }
            };
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(binding.getRoot(), "Harita icin izin gerekiyor.", Snackbar.LENGTH_INDEFINITE).setAction("Izin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                } else {
                    // izin iste
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,locationListener);

                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }
                mMap.setMyLocationEnabled(true);

            }
        }else{
            mMap.clear();
            selectedPlace = (Place) intent.getSerializableExtra("place");
            LatLng latLng = new LatLng(selectedPlace.lat,selectedPlace.lot);

            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            binding.placeNameText.setText(selectedPlace.name);
            binding.placeComment.setText(selectedPlace.comment);
            binding.saveButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);

        }


        // Add a marker in Sydney and move the camera
        //  LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }

    private void registerLauncher() {
        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            //permission granted
                            if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (lastLocation != null) {
                                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                                }
                                mMap.setMyLocationEnabled(true);

                            }
                        } else {
                            //permission denied
                            Toast.makeText(MapsActivity.this,"Permisson needed!",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        SlctLat = latLng.latitude;
        SlctLong= latLng.longitude;
        binding.saveButton.setEnabled(true);
    }

    public  void save(View view){
        Place place = new Place(binding.placeNameText.getText().toString(),binding.placeComment.getText().toString(),SlctLat,SlctLong);
        compositeDisposable.add(placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MapsActivity.this::getBack)
        );

    }


    private  void getBack(){
        Intent intent = new Intent(MapsActivity.this,MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public  void delete(View view){

            compositeDisposable.add(placeDao.delete(selectedPlace)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MapsActivity.this::getBack));

    }

    protected  void  onDestroy(){
        super.onDestroy();
        compositeDisposable.clear();
    }

}