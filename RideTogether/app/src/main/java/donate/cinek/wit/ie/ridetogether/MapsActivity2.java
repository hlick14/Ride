package donate.cinek.wit.ie.ridetogether;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity2 extends FragmentActivity implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Location location;
    protected String addressToSend ="";
    protected String addressToSend2="";
    MarkerOptions options, options2;
    Bitmap bmOverlay;
    RelativeLayout  mapLayout;

    LatLng origin, dest;
    String distance = "";
    String duration = "";

    Context context;


    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    static Bitmap snypImageScaled;
    String temp;
    Button ok,cancel;
    LatLng loc,orginLoc,destLoc ;
    boolean orginOrDest;
    double olat,olong,dlat,dlong;
    ArrayList<Marker> markers = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        setUpMapIfNeeded();
        try {
            Bundle extras = getIntent().getExtras();
            if(!extras.isEmpty()) {

                olat = (double) extras.get("finalOrginLat");
                olong = (double) extras.get("finalOrginLong");
                orginLoc= new LatLng(olat,olong);

                dlat = (double) extras.get("finalDestLat");
                dlong = (double) extras.get("finalDestLong");
                destLoc = new LatLng(dlat,dlong);
            }
        }
        catch (Exception e)
        {
            Log.d("RideTogether", "MapsActivity2 Extras are empty");

        }
        mapLayout = (RelativeLayout) this.findViewById(R.id.mainLayout);
        context = this.getApplicationContext();
        setRoute(orginLoc,destLoc);



        LocationManager lm = null;
        double latitude = 0.0;
        double longitude = 0.0;
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location  not enabled");
            dialog.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

        }

//        this.canGetLocation = true;
        if (network_enabled) {
            lm.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            Log.d("Network", "Network Enabled");
            if (lm != null) {
                location = lm
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        }
        // if GPS Enabled get lat/long using GPS Services
        if (gps_enabled) {
            if (location == null) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);
                Log.d("GPS", "GPS Enabled");
                if (lm != null) {
                    location = lm
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }

        try {

            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.setMyLocationEnabled(false);
//            mMap.getUiSettings().setZoomControlsEnabled(true);
            //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(olat, olong)).zoom(12).build();



//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),padding));
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
                }
            });

            initilizeMap();

            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                public void onCameraChange(CameraPosition arg0) {
//                    mMap.clear();
                    loc = arg0.target;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }



    }




    private void initilizeMap() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            View mapView = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getView();
            View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80,80); // size of button in dp
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            params.setMargins(0, 0, 20, 0);
            btnMyLocation.setLayoutParams(params);

            // check if map is created successfully or not
            if (mMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.

        }
    }

 public void setRoute(LatLng orgin,LatLng destin)
 {
     Log.d("Orgin", orgin.latitude + "-" + orgin.longitude);
     Log.d("Destination", destin.latitude + "-" + destin.longitude);


     // adding marker
//     if (markerPoints.size() > 1) {
//         markerPoints.clear();
//         mMap.clear();
//
//     }



     options = new MarkerOptions();

     // Setting the position of the marker
     options.position(orginLoc);
     options.title("Starting Here");
     options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
     mMap.setPadding(0, 160, 0, 0);

     markers.add(mMap.addMarker(options));
     mMap.addMarker(options);


     /**
      * For the start location, the color of marker is GREEN and
      * for the end location, the color of marker is RED.
      */
////////////////////////////////////////////////////////////////////////////////////
     new ReverseGeocodingTask(getBaseContext()).execute(orginLoc);
     new ReverseGeocodingTask2(getBaseContext()).execute(destLoc);

     options2 = new MarkerOptions();
     // Add new marker to the Google Map Android API V2
     options2.position(destLoc);
     options.title("Your Trip Ends Here");
     options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
     mMap.setPadding(0, 160, 0, 60);
     markers.add(mMap.addMarker(options2));
     mMap.addMarker(options2);
     // Getting URL to the Google Directions API
     String url = getDirectionsUrl(orginLoc, destLoc);
     DownloadTask downloadTask = new DownloadTask();
         // Start downloading json data from Google Directions API
     downloadTask.execute(url);


 }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }
    //////ADD ASYNC METHOD TO THIS

    private String downloadUrl(String strUrl)  throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
//            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);

                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();



            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    String temp2 = path.get(1).get("duration");

                    if (j == 0) {    // Get distance from the list
                        distance =  point.get("distance");

                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration =  point.get("duration");
//
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.width(2);
                lineOptions.color(Color.RED);
                lineOptions.visible(true);
                lineOptions.addAll(points);

            }
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            builder.include(orginLoc);
//            builder.include(destLoc);
//            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 12));
            mMap.addPolyline(lineOptions);
            if(distance.length()>0) {
                Snackbar.make(findViewById(R.id.mainLayout), "Distance: " + distance + " Duration: " + duration, Snackbar.LENGTH_INDEFINITE).setAction("Confirm", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent UserTrips = new Intent(MapsActivity2.this, TripInfo.class);
                        UserTrips.putExtra("mapDetails", addressToSend);
                        UserTrips.putExtra("mapDetails2", addressToSend2);
                        UserTrips.putExtra("originLat", orginLoc.latitude);
                        UserTrips.putExtra("originLong", orginLoc.longitude);
                        UserTrips.putExtra("destLat", destLoc.latitude);
                        UserTrips.putExtra("destLong", destLoc.longitude);
                        UserTrips.putExtra("distance", distance);
                        UserTrips.putExtra("duration", duration);
                        startActivity(UserTrips);
                    }

                }).show();
            }

            captureScreen();
        }
    }


    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context) {
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            List<Address> addresses = null;
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);


                addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());


                addressToSend = address.getAddressLine(1);
                Log.d("ADDRESS", addressToSend.toString());

            }
            return addressText;
        }

        @Override
        protected void onPostExecute(final String addressText) {
            // Setting the title for the marker.
            // This will be displayed on taping the marker


//            if (markerPoints.size() == 1) {
//                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//
//                mMap.setPadding(0, 160, 0, 0);
//
//            } else if (markerPoints.size() == 2) {
//                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
////                tvDistanceDuration1.setText("End: " + addressText);
//                mMap.setPadding(0, 160, 0, 60);


        }
    }

        // Placing a marker on the touched position
        private class ReverseGeocodingTask2 extends AsyncTask<LatLng, Void, String> {
            Context mContext;

            public ReverseGeocodingTask2(Context context) {
                super();
                mContext = context;
            }

            // Finding address using reverse geocoding
            @Override
            protected String doInBackground(LatLng... params) {
                Geocoder geocoder = new Geocoder(mContext);
                double latitude = params[0].latitude;
                double longitude = params[0].longitude;

                List<Address> addresses = null;
                String addressText = "";

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);


                    addressText = String.format("%s, %s, %s",
                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                            address.getLocality(),
                            address.getCountryName());


                    addressToSend2 = address.getAddressLine(1);
                    Log.d("ADDRESS", addressToSend2.toString());


                }
                return addressText;
            }

            @Override
            protected void onPostExecute(final String addressText) {


            }


        }


            public void captureScreen() {

                SnapshotReadyCallback callback = new SnapshotReadyCallback() {

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        // TODO Auto-generated method stub
                        bmOverlay = snapshot;
                        String fileName = "myImage";
                        /////////////Bundle can only store data up to 1 mb therefore map screenshot has to be scaled down and comrpessed
                        ///////////////////////////////Current 275,200
                        try {
                            snypImageScaled = Bitmap.createScaledBitmap(bmOverlay, 640, 480
                                    * bmOverlay.getHeight() / bmOverlay.getWidth(), false);
                            // Convert it to byte
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            // Compress image to lower quality scale 1 - 100
                            snypImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
                            fo.write(bos.toByteArray());
                            // remember close file output
                            fo.close();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                };

                mMap.snapshot(callback);
            }


        }

