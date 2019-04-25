package com.example.himanshu.directionapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener



 {

    private PlaceAutocompleteFragment placeAutoComplete;
    private GoogleMap mMap;
    private PlaceAutocompleteFragment placeAutoComplete2;
    Button Click;
    Button draw;
    double s_lat, S_lng, D_lat, D_lng;
    TextView Distance, Duration;
    PolylineOptions polylineOptions;
    Polyline line;
    List<LatLng> directionList = new ArrayList<LatLng>();
     public static final int REQUEST_Location_code=99;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checklocationpermission();
        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Distance = findViewById(R.id.distance);
        Duration = findViewById(R.id.time);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName());
                double lat = place.getLatLng().latitude;
                double lng = place.getLatLng().longitude;
                s_lat = lat;
                S_lng = lng;

                LatLng source = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(source).title("Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 18f));
            }

            @Override
            public void onError(Status status) {
                Log.d("map", "An error occurred" + status);
            }
        });

        placeAutoComplete2 = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete2);
        placeAutoComplete2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName());
                double lat = place.getLatLng().latitude;
                double lng = place.getLatLng().longitude;
                D_lat = lat;
                D_lng = lng;
                LatLng source = new LatLng(lat, lng);
                mMap.addMarker(new MarkerOptions().position(source).title("Here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(source, 18f));
            }

            @Override
            public void onError(Status status) {
                Log.d("map", "An error occurred" + status);
            }
        });
        draw=(Button)findViewById(R.id.btn_draw);

        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> data = new HashMap<>();
                data.put("origin", s_lat + "," + S_lng);
                data.put("destination", D_lat + "," + D_lng);
                data.put("key", "AIzaSyDELpqMi27VwVMB44JliiQG3wSDAYEuG_c");

                Api api = ApiClient.apiclient().create(Api.class);
                Call<Map> call = api.placedata(data);


                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {


//                        Log.d("PolyPoints",((LinkedTreeMap)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)response.body()).get("routes")).get(0)).get("legs")).get(0)).get("steps")).get(0)).get("polyline")).get("points").toString());
                                                Gson gson = new Gson();
                        String json = gson.toJson(response.body());  //Gson hame pue json formate ka data  ek String(json) me de diya
                        // String code[];
                        JSONArray legs;
                        try {
                            JSONObject jsonObject = new JSONObject(json); //because pura json formate ek object me he to json pass ker diya object bna ke

                            JSONArray route = jsonObject.getJSONArray("routes");//routes array me jana he Json array bna diya


                            int routeSize = route.length();// route array ka size nikal liya or loop me chala diya isme hi legs array ko get kerke legs ki lenght ko print kra diya


                            for (int i = 0; i < routeSize; i++) {  // i ek JSONObject ki trah kam kr rha he

                                legs = route.getJSONObject(i).getJSONArray("legs");  // getJSONObject and getJSONArray capital wale lene he JSONArray legs uper bna diya

                                System.out.println("Legs Size: " + legs.length());

                                int legsize = legs.length();

                                for (int j = 0; j < legsize; j++) {

                                    JSONArray steps = legs.getJSONObject(j).getJSONArray("steps");
                                    System.out.println("Step Size: " + steps.length());
                                    for (int k = 0; k < steps.length(); k++) {   //steps ek Array he isse object ko get krenge

                                        // polylineOptions=new PolylineOptions();


                                        JSONObject polyline = steps.getJSONObject(k).getJSONObject("polyline");
                                        String lines = polyline.getString("points");         //Strig ki value he point usko get krenge
                                        System.out.println("PolyLine : " + lines);

                                        List<LatLng> singlePolyline = decodePoly(lines);

                                        for (int z = 0; z < singlePolyline.size() - 1; z++) {  //-1 lat and +1 lng ko bta rha he
                                            LatLng src = singlePolyline.get(z);
                                            LatLng dest = singlePolyline.get(z + 1);


                                            line = mMap.addPolyline(new PolylineOptions()
                                                    .add(new LatLng(src.latitude, src.longitude),
                                                            new LatLng(dest.latitude, dest.longitude)
                                                    ).width(5).color(Color.BLACK).geodesic(true));


                                        }


                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {

                    }
                });

            }
        });



        Click = findViewById(R.id.Find);
        Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> data = new HashMap<>();
                data.put("origin", s_lat + "," + S_lng);
                data.put("destination", D_lat + "," + D_lng);
                data.put("key", "AIzaSyDELpqMi27VwVMB44JliiQG3wSDAYEuG_c");

                Api api = ApiClient.apiclient().create(Api.class);
                Call<Map> call = api.placedata(data);


                call.enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        Distance.setText(((LinkedTreeMap) ((LinkedTreeMap) ((ArrayList) ((LinkedTreeMap) ((ArrayList) response.body().get("routes")).get(0)).get("legs")).get(0)).get("distance")).get("text").toString());
                        Duration.setText(((LinkedTreeMap) ((LinkedTreeMap) ((ArrayList) ((LinkedTreeMap) ((ArrayList) response.body().get("routes")).get(0)).get("legs")).get(0)).get("duration")).get("text").toString());
//    Log.d("onResponse","Hello"+((LinkedTreeMap)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)response.body()).get("routes")).get(0)).get("overview_polyline")).get("points").toString());
                        //  Log.d("points","There are points"+((LinkedTreeMap)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)((ArrayList)((LinkedTreeMap)response.body()).get("routes")).get(0)).get("legs")).get(0)).get("steps")).get(1)).get("polyline")).get("points").toString());



                    }


                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                    }
                });
            }
        });
    }

     private boolean checklocationpermission() {
         if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
         {
             if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
             {
                 ActivityCompat.requestPermissions(this,new  String []{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_Location_code);
             }
             else
             {
                 ActivityCompat.requestPermissions(this,new  String []{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_Location_code);

             }
             return false;
         }
         else

             return true;
     }


     private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);




    }



    @Override
    public void onLocationChanged(Location location) {


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
