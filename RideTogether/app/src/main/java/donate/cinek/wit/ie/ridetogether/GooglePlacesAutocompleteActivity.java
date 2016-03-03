package donate.cinek.wit.ie.ridetogether;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class GooglePlacesAutocompleteActivity extends AppCompatActivity implements OnItemClickListener, LocationListener {

    String name;
    String username;
    String Motorbike;
    String UserSince;
    Date UserSince2;
    Bitmap bitmap;
    String uSince;

    String Model, Engine;

    private TextView textView, textView2, textView3, textView4;
    Context context;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    Location location;
    double orginLatitude, orginLongitude, destLatitude, destLongtitude,orginLat,orginLong,destLat,destLong;
    AutoCompleteTextView autocompleteView, autocompleteView2;
    LatLng orginLoc, destLoc, returnOrginLock, returnDestLock;
    Button btn;
    public static final String PREFS_NAME = "RideTogether_Settings";

    SharedPreferences settings,settings2;
    String olat,olong,dlat,dlong;
    double finalOriginLat,finalOriginLong,finalDestLat,finalDestLong;

    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayAdapter<Bitmap> imagesArrayAdapter;
    private ArrayList<String> names, usernameForImages;
    private ArrayList<Bitmap> images;
    private ListView usersListView;
    private Button logoutButton;
    private ProgressDialog progressDialog;
    private BroadcastReceiver receiver = null;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_google_places_autocomplete);
        autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView2 = (AutoCompleteTextView) findViewById(R.id.autocomplete2);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        autocompleteView2.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
//        autocompleteView.setFocusable(false);
//        autocompleteView2.setFocusable(false);

        btn = (Button) findViewById(R.id.Finish);
//

        getUserData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Trip Planner");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("donate.cinek.wit.ie.ridetogether.GooglePlacesAutocompleteActivity"));
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                if (menuItem.getTitle() == "Log Out") {
                    //Turn of the service from sinch


                    Intent backToMain = new Intent(GooglePlacesAutocompleteActivity.this, MainActivity.class);
                    startActivity(backToMain);
                }
                return true;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GooglePlacesAutocompleteActivity.this, "Orgin lat is : "+finalOriginLat  + "/n/"+
                        "Orgin long is : "+finalOriginLong  + "/n/"+
                        "Destination lat is : "+finalDestLat  + "/n/"+
                        "Destination long is : "+finalDestLong  + "/n/", Toast.LENGTH_LONG).show();
                Intent startMaps2 = new Intent(GooglePlacesAutocompleteActivity.this,MapsActivity2.class);
                startMaps2.putExtra("finalOrginLat",finalOriginLat);
                startMaps2.putExtra("finalOrginLong",finalOriginLong);
                startMaps2.putExtra("finalDestLat",finalDestLat);
                startMaps2.putExtra("finalDestLong",finalDestLong);
                startActivity(startMaps2);
            }
        });


    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String description = (String) parent.getItemAtPosition(position);
        Toast.makeText(this, description, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

            if(prefs!=null) {
                 olat = prefs.getString("orginLat", null);
                 olong = prefs.getString("orginLong", null);
                if(olat!=null)
                {
                    finalOriginLat = Double.parseDouble(olat);
                    finalOriginLong = Double.parseDouble(olong);
                }
                Log.d("sharedPref:", "1. When firstly getting orging " + "/n"+"Getting string Value of orgin lat " + olat + "value of orging long"+ olong);
                 dlat = prefs.getString("destLat", null);
                 dlong = prefs.getString("destLong", null);
                if(dlat!=null)
                {
                    finalDestLat = Double.parseDouble(dlat);
                    finalDestLong = Double.parseDouble(dlong);
                }


                Log.d("sharedPref:", "2. When firstly getting destination " + "/n"+"Getting string Value of destin lat " + dlat + "value of destin long"+ dlong);
            }
            try {
                Bundle extras = getIntent().getExtras();
                if(!extras.isEmpty()) {

                    orginLoc = (LatLng) extras.get("orgLatLong");
                    if(orginLoc!=null) {
                        String originLocLat = String.valueOf(orginLoc.latitude);
                        finalOriginLat = Double.parseDouble(originLocLat);
                        String orginLocLong = String.valueOf(orginLoc.longitude);
                        finalOriginLong = Double.parseDouble(orginLocLong);
                    }
                    destLoc = (LatLng) extras.get("destLatLong");
                    if(destLoc!=null) {
                        String destLocLat = String.valueOf(destLoc.latitude);
                        finalDestLat = Double.parseDouble(destLocLat);
                        String destLocLong = String.valueOf(destLoc.longitude);
                        finalDestLong = Double.parseDouble(destLocLong);
                    }

                    if(orginLoc!=null ) {
                        autocompleteView.setText("Point on Map");
                    }
                    if(olat.length()>0)
                    {
                        autocompleteView.setText("Point on Map");
                    }
                    else if (destLoc!=null )
                    {

                        autocompleteView2.setText("Point on Map");

                    }
                    if(dlat.length()>0)
                    {
                        autocompleteView2.setText("Point on Map");
                    }
//                    else if ((orginLoc!=null && destLoc!=null)||dlat.length()>0||olat.length()>0) {
//                        autocompleteView.setText("Point on Map");
//                        autocompleteView2.setText("Point on Map");
//
//                    }
                }
            }
            catch (Exception e)
            {
                Log.d("RideTogether", "Extras are empty");

            }

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            textView = (TextView) findViewById(R.id.YourLoc);
            textView2 = (TextView) findViewById(R.id.ChoosePoint);
            textView3 = (TextView) findViewById(R.id.YourLoc2);
            textView4 = (TextView) findViewById(R.id.ChoosePoint2);

            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append("").append(" ");
            builder.setSpan(new ImageSpan(this, R.drawable.ic_my_location_white_24dp),
                    builder.length() - 1, builder.length(), 0);
            builder.append(" Your Location");

            textView.setText(builder);
            textView3.setText(builder);
            LocationListener ls = new LocationListener() {
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
            };
            final LocationListener finalLs = ls;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        context = getApplicationContext();
                        LocationManager lm = null;
                        orginLatitude = 0.0;
                        orginLongitude = 0.0;
                        boolean gps_enabled = false, network_enabled = false;
                        if (lm == null)
                            lm = (LocationManager) GooglePlacesAutocompleteActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        try {

                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch (Exception ex) {
                        }
                        try {
                            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        } catch (Exception ex) {
                        }

                        if (!gps_enabled) {
                            showAlert();

                        }


                        if (network_enabled) {
                            lm.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, finalLs);
                            Log.d("RideTogether", "Network based location Enabled");
                            if (lm != null) {
                                location = lm
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    orginLatitude = location.getLatitude();
                                    orginLongitude = location.getLongitude();
                                    finalOriginLat=orginLatitude;
                                    finalOriginLat=orginLongitude;
                                }
                            }
                        }
                        // if GPS Enabled get lat/long using GPS Services
                        if (gps_enabled) {
                            if (location == null) {
                                lm.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, finalLs);
                                Log.d("GPS", "GPS Enabled");
                                if (lm != null) {
                                    location = lm
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (location != null) {
                                        orginLatitude = location.getLatitude();
                                        orginLongitude = location.getLongitude();
                                        finalOriginLat=orginLatitude;
                                        finalOriginLat=orginLongitude;
                                    }
                                }
                            }
                        }
                    }

                    autocompleteView.setText("From: Your Location");

                }
            });
            textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    {
                        context = getApplicationContext();
                        LocationManager lm = null;
                        destLatitude = 0.0;
                        destLongtitude = 0.0;
                        boolean gps_enabled = false, network_enabled = false;
                        if (lm == null)
                            lm = (LocationManager) GooglePlacesAutocompleteActivity.this.getSystemService(Context.LOCATION_SERVICE);
                        try {

                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch (Exception ex) {
                        }
                        try {
                            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        } catch (Exception ex) {
                        }

                        if (!gps_enabled) {
                            showAlert();

                        }


                        if (network_enabled) {
                            lm.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, finalLs);
                            Log.d("RideTogether", "Network based location Enabled");
                            if (lm != null) {
                                location = lm
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                if (location != null) {
                                    destLatitude = location.getLatitude();
                                    destLongtitude = location.getLongitude();
                                    finalDestLat = destLatitude;
                                    finalDestLong = destLatitude;
                                }
                            }
                        }
                        // if GPS Enabled get lat/long using GPS Services
                        if (gps_enabled) {
                            if (location == null) {
                                lm.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, finalLs);
                                Log.d("GPS", "GPS Enabled");
                                if (lm != null) {
                                    location = lm
                                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                    if (location != null) {
                                        destLatitude = location.getLatitude();
                                        destLongtitude = location.getLongitude();
                                        finalDestLat = destLatitude;
                                        finalDestLong = destLatitude;
                                    }
                                }
                            }
                        }
                    }

                    autocompleteView2.setText("To: Your Location");

                }
            });

            SpannableStringBuilder builder2 = new SpannableStringBuilder();
            builder2.append("").append(" ");
            builder2.setSpan(new ImageSpan(this, R.drawable.ic_add_location_white_24dp),
                    0, builder2.length(), 0);
            builder2.append(" Choose your point on map");

            textView2.setText(builder2);
            textView4.setText(builder2);
            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent maps = new Intent(GooglePlacesAutocompleteActivity.this, MapsActivity.class);
                    maps.putExtra("orgin", true);
                    String dlat="";
                    String dlong="";
                    if (destLoc != null) {

                        dlat= String.valueOf(destLoc.latitude);
                        dlong = String.valueOf(destLoc.longitude);

                    settings2 = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings2.edit();
                    Log.v("SharedPreferences", "Saving Destination String");
                    editor.putString("destLat", dlat);
                    editor.putString("destLong", dlong);
                    editor.commit();
                    }
                    startActivity(maps);


                    startActivity(maps);

                }
            });
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent maps = new Intent(GooglePlacesAutocompleteActivity.this, MapsActivity.class);
                    maps.putExtra("orgin", false);
                    String olat="";
                    String olong="";
                if (orginLoc != null) {

                     olat= String.valueOf(orginLoc.latitude);
                     olong = String.valueOf(orginLoc.longitude);

                    settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    Log.v("SharedPreferences", "Saving Orgin String");
                    editor.putString("orginLat", olat);
                    editor.putString("orginLong", olong);
                    editor.commit();
                }
                    startActivity(maps);

                }
            });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.chat:
                final Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
                final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
                startActivity(intent);
                startService(serviceIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
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


    class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

        ArrayList<String> resultList;

        Context mContext;
        int mResource;

        PlaceAPI mPlaceAPI = new PlaceAPI();

        public PlacesAutoCompleteAdapter(Context context, int resource) {
            super(context, resource);

            mContext = context;
            mResource = resource;
        }

        @Override
        public int getCount() {
            // Last item will be the footer
            return resultList.size();
        }

        @Override
        public String getItem(int position) {

            return resultList.get(position);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        resultList = mPlaceAPI.autocomplete(constraint.toString());

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };

            return filter;
        }
    }
    private void setConversationsList() {
        currentUserId = ParseUser.getCurrentUser().getUsername();
        names = new ArrayList<String>();
        images = new ArrayList<Bitmap>();
        usernameForImages = new ArrayList<String>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < userList.size(); i++) {

                        usernameForImages.add(userList.get(i).getObjectId());

                        names.add(userList.get(i).getUsername());

                        ///Fetching image

//                        final ParseQuery<ParseObject> query3 = ParseQuery.getQuery("ImageUpload");
//                        query3.whereEqualTo("CreatedbyUser", usernameForImages.get(i));
//                        query3.getFirstInBackground(new GetCallback<ParseObject>() {
//                            public void done(ParseObject object, ParseException e) {
//                                if (object != null) {
//
//                                    ParseFile file = (ParseFile) object.get("ImageFile");
//                                    file.getDataInBackground(new GetDataCallback() {
//                                        public void done(byte[] data, ParseException e) {
//                                            if (e == null) {
//                                                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                                                images.add(bitmap);
//                                                // Toast.makeText(BaseActivity.this, "" + bitmap.getHeight() , Toast.LENGTH_SHORT).show();
//                                                //use this bitmap as you want
//                                            } else {
//                                                Toast.makeText(ListUsersActivity.this, "Zdjecie jest puste" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
//
//                                            }
//                                        }
//
//
//                                    });
//
//                                } else {
//                                    Toast.makeText(ListUsersActivity.this, "objekt jest pusty" + e.getMessage().toString(), Toast.LENGTH_LONG).show();
//
//                                }
//                            }
//                        });
//
//
                    }

                    usersListView = (ListView) findViewById(R.id.usersListView);
                    namesArrayAdapter =
                            new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.user_list_item, names);
                    usersListView.setAdapter(namesArrayAdapter);
//                    imagesArrayAdapter = new ArrayAdapter<Bitmap>(getApplicationContext(),R.layout.user_list_item,images);
//                    usersListView.setAdapter(imagesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
//                            openConversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Toast.makeText(getApplicationContext(),
                "" + usernameForImages,
                Toast.LENGTH_LONG).show();

    }

    public void getUserData() {
        String currentUser = ParseUser.getCurrentUser().getUsername();
        ParseUser currentUser2 = ParseUser.getCurrentUser();
        ///Fetching image
        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("ImageUpload");
        query2.whereEqualTo("CreatedbyUser", currentUser2);
        query2.getFirstInBackground(new GetCallback<ParseObject>() {

            public void done(ParseObject object, ParseException e) {
                if (object != null) {

                    ParseFile file = (ParseFile) object.get("ImageFile");
                    file.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                // Toast.makeText(BaseActivity.this, "" + bitmap.getHeight() , Toast.LENGTH_SHORT).show();
                                //use this bitmap as you want
                            } else {
                                // something went wrong
                            }
                        }
                    });

                } else {
                    // Toast.makeText(BaseActivity.this, "objekt jest pusty" , Toast.LENGTH_SHORT).show();

                }
            }
        });
        /////////////////////////////////Fetching data
        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", currentUser);
        /////////////////////////NEEEEEED TO READ IN THE VALUES AND SEND THEM TO ADPATER LOOK

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseObjects, ParseException e) {
                query.findInBackground(new FindCallback<ParseUser>() {
                    public void done(List<ParseUser> UserDetails, ParseException e) {
                        if (e == null) {
                            if (UserDetails == null || UserDetails.size() == 0) {


                                return; //no objects
                            }
                            for (int i = 0; i < UserDetails.size(); i++) {

                                ParseObject object = UserDetails.get(i);
//                                int icon =(int) object.get("TripName");
                                name = (String) object.get("Name");
                                username = (String) object.get("username");
                                Motorbike = (String) object.get("Motorbike");
                                Model = (String) object.get("Model");
                                Engine = (String) object.get("Engine");

                                UserSince2 = object.getCreatedAt();
                                UserSince = UserSince2.toString();
                                UserSince = UserSince.substring(3, 11);
                                uSince = UserSince + UserSince2.toString().substring(23, 28);


                                NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

                                CircleImageView cv = (CircleImageView) findViewById(R.id.circleUserProfile);
                                TextView tv = (TextView) findViewById(R.id.usernameDrawer);
                                tv.setText(username);

                                cv.setImageBitmap(bitmap);
                                Menu m = navigationView.getMenu();


                                SubMenu topChannelMenu = m.addSubMenu("Rider");
                                topChannelMenu.add("User Since: " + uSince);
                                topChannelMenu.getItem(0).setEnabled(false);
                                topChannelMenu.getItem(0).setIcon(R.drawable.solo);
                                topChannelMenu.add("Motorbike:  " + Motorbike + " " + Model + " " + Engine);
                                topChannelMenu.getItem(1).setIcon(R.drawable.bike);
                                topChannelMenu.getItem(1).setEnabled(false);
                                topChannelMenu.add("");
                                topChannelMenu.getItem(2).setEnabled(false);
                                topChannelMenu.add("");
                                topChannelMenu.getItem(3).setEnabled(false);

                                SubMenu topChannelMenu2 = m.addSubMenu("Options");
                                topChannelMenu2.add("Settings");
                                topChannelMenu2.getItem(0).setIcon(R.drawable.settings);
                                topChannelMenu2.add("Log Out");
                                topChannelMenu2.getItem(1).setIcon(R.drawable.sing_out);


                                MenuItem mi = m.getItem(m.size() - 1);
                                mi.setTitle(mi.getTitle());
                                m.getItem(0).setIcon(R.drawable.solo);
                            }


                        }
                    }
                });


            }
        });


    }
}
