package com.quaditsolutions.mmreport;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class GetLocation {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;

    public static String longtitude = "";
    public static String latitude = "";
    public static String address = "";




    public void getCurrLoc(final Activity activity) {
        locationRequest = new LocationRequest();


        locationRequest.setFastestInterval(1);
        locationRequest.setInterval(1);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity.getApplicationContext());


        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                try {

                    if (location != null)
                    {
                        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        longtitude = String.valueOf(addresses.get(0).getLatitude());
                        latitude = String.valueOf(addresses.get(0).getLatitude());
                        address = String.valueOf(addresses.get(0).getAddressLine(0));
                    }
                    else
                    {
                        askNewLocation(activity);
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(activity, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        };

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }

    }
    public void askNewLocation(Activity activity)
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }
}
