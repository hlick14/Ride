package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class ListUsersActivity extends AppCompatActivity {
    protected ImageButton account;
    private MenuView.ItemView it;
    String name;
    String username;
    String Motorbike;
    String UserSince;
    Date UserSince2;
    Bitmap bitmap;
    String uSince;
    String Model, Engine;

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
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getUserData();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        showSpinner();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        //broadcast receiver to listen for the broadcast
        //from MessageService
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);

                progressDialog.dismiss();
                //show a toast message if the Sinch
                //service failed to start
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("donate.cinek.wit.ie.ridetogether.ListUsersActivity"));
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                if (menuItem.getTitle() == "Log Out") {
                    //Turn of the service from sinch


                    Intent backToMain = new Intent(ListUsersActivity.this, MainActivity.class);
                    startActivity(backToMain);
                }
                return true;
            }
        });

//        logoutButton = (Button) findViewById(R.id.logoutButton);
//        logoutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                stopService(new Intent(getApplicationContext(), MessageService.class));
//                ParseUser.logOut();
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//            }
//        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //display clickable a list of all users
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
                            openConversation(names, i);
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

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //show a loading spinner while the sinch client starts
    private void showSpinner() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean success = intent.getBooleanExtra("success", false);
                Toast.makeText(getBaseContext(), " " + success.toString(), Toast.LENGTH_LONG).show();
                if (success) {
                    Toast.makeText(getBaseContext(), " in success " + success.toString(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }

                if (!success) {
                    Toast.makeText(getApplicationContext(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("donate.cinek.wit.ie.ridetogether.ListUsersActivity"));
    }

    @Override
    public void onResume() {
        setConversationsList();
        super.onResume();
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
    public void onBackPressed() {
        Intent backToOptions = new Intent(ListUsersActivity.this, Options.class);

        startActivity(backToOptions);

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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ListUsers Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://donate.cinek.wit.ie.ridetogether/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "ListUsers Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://donate.cinek.wit.ie.ridetogether/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}