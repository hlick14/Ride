package donate.cinek.wit.ie.ridetogether;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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


public class GooglePlacesAutocompleteActivity extends AppCompatActivity implements OnItemClickListener,LocationListener {

    String name;
    String username;
    String Motorbike;
    String UserSince;
    Date UserSince2;
    Bitmap bitmap;
    String uSince;
    String Model, Engine;
    private BroadcastReceiver receiver = null;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private TextView textView,textView2,textView3,textView4;
    Context context;
    public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    Location location;
    double orginLatitude,orginLongitude,destLatitude,destLongtitude;
    AutoCompleteTextView autocompleteView,autocompleteView2;
    LatLng orginLoc, destLoc,returnOrginLock ;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_google_places_autocomplete);
         autocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete);
        autocompleteView2 = (AutoCompleteTextView) findViewById(R.id.autocomplete2);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        autocompleteView2.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_item));
        btn = (Button) findViewById(R.id.Finish);
        try {
            Bundle extras = getIntent().getExtras();
            if(!extras.isEmpty()) {

                orginLoc = (LatLng) extras.get("orgLatLong");
                destLoc = (LatLng) extras.get("destLatLong");

                returnOrginLock = (LatLng) extras.get("orginLoc");
                if(orginLoc!=null) {
                    if (returnOrginLock != null) {
                        autocompleteView.setText("Point on Map");
                    }
                }
                else if (destLoc!=null)
                {

                    autocompleteView2.setText("Point on Map");
                }
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

                    if (!gps_enabled ) {
                        showAlert();

                    }


                    if (network_enabled) {
                        lm.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,  finalLs);
                        Log.d("RideTogether", "Network based location Enabled");
                        if (lm != null) {
                            location = lm
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                orginLatitude = location.getLatitude();
                                orginLongitude = location.getLongitude();
                            }
                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (gps_enabled) {
                        if (location == null) {
                            lm.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES,  finalLs);
                            Log.d("GPS", "GPS Enabled");
                            if (lm != null) {
                                location = lm
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    orginLatitude = location.getLatitude();
                                    orginLongitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }

                autocompleteView2.setText("To: Your Location");

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
                                }
                            }
                        }
                    }
                }

                autocompleteView.setText("From: Your Location");

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
                maps.putExtra("orgin",true);
                if(orginLoc!=null)
                {
                    maps.putExtra("orginLoc",orginLoc);
                }
//                else if (destLoc!=null)
//                {
//                    maps.putExtra("destLoc",destLoc);
//                }

                startActivity(maps);

            }
        });
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent maps = new Intent(GooglePlacesAutocompleteActivity.this, MapsActivity.class);
                maps.putExtra("orgin",false);
                if(orginLoc!=null)
                {
                    maps.putExtra("orginLoc",orginLoc);
                }
//                else if (destLoc!=null)
//                {
//                    maps.putExtra("destLoc",destLoc);
//                }
                startActivity(maps);

            }
        });

        getUserData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
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
                Toast.makeText(GooglePlacesAutocompleteActivity.this,"Orgin Destination is " + orginLoc+ "Destination desti" + destLoc,Toast.LENGTH_LONG).show();
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
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }
            };

            return filter;
        }
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
