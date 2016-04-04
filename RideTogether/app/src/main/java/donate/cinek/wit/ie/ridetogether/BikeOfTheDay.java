package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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


/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */
public class BikeOfTheDay extends AppCompatActivity  {

    Button friendSearch;
    private ArrayAdapter<String> namesArrayAdapter;
    private ListView usersListView;
    ProgressDialog progressDialog;
    TextView time, date,FoundRequestedUsers;
    TextInputLayout update;
    private ArrayList<String> listOfUsernames;
    String name;

    protected ImageButton account;
    private DrawerLayout mDrawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tv;
    private android.support.v7.widget.Toolbar toolbar;
    private int[] tabIcons = {
            R.drawable.group,
            R.drawable.solo,
            R.drawable.home

    };
    String Dname;
    String username;
    String Motorbike;
    String UserSince;
    Date UserSince2;
    Bitmap bitmap;
    String uSince;
    String Model, Engine;
    private int hot_number = 0;
    private TextView ui_hot = null;
    ProgressDialog dialog;
    private List<Bitmap> imgList = new ArrayList<Bitmap>();
    public List<SoloTrip> hujumuniu = new ArrayList<>();
    public List<ParseObject> tempObjectHolder = new ArrayList<>();
    String tName, tDate, tTime;
    int t = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_of_the_day);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        getUserData();

        viewPager = (ViewPager) findViewById(R.id.viewpagerBikeOfTheDay);

        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.BikeOfTheDayTabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        setupTabIcons();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                if (menuItem.getTitle() == "Log Out") {
                    //Turn of the service from sinch

                    ParseUser.logOut();
                    Intent backToMain = new Intent(BikeOfTheDay.this, MainActivity.class);
                    startActivity(backToMain);
                }
                else    if (menuItem.getTitle() == "Settings") {



                    Intent backToMain = new Intent(BikeOfTheDay.this, AccountOptions.class);
                    startActivity(backToMain);
                }
                return true;
            }
        });


        getListOfUsers();
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_options, menu);
//
//        return super.onCreateOptionsMenu(menu);
//
//    }
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
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new UploadImage(), "UploadImage");
//        adapter.addFragment(new viewRequests(), "ViewRequests");

//        viewPager.setCurrentItem(2);
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.friend_custom_tab, null);
        tabTwo.setText("Bike Of The Day");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_motorcycle_white_24dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabTwo);

//        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.friend_custom_tab, null);
//        tabOne.setText("View Friend Requests");
//        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_supervisor_account_white_24dp, 0, 0);
//        tabLayout.getTabAt(1).setCustomView(tabOne);





    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    private void getListOfUsers() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        listOfUsernames = new ArrayList<String>();


        String currentUser = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUser);

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {

                        listOfUsernames.add(objects.get(i).get("username").toString());
                    }
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(BikeOfTheDay.this, "Could not load the list of users", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
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
                                Toast.makeText(BikeOfTheDay.this, "Error Loading Data From Our Servers - Image Problem", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {
                    Toast.makeText(BikeOfTheDay.this, "Error Loading Data From Our Servers - User Problem", Toast.LENGTH_SHORT).show();

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
                                Dname = (String) object.get("Name");
                                username = (String) object.get("username");
                                Motorbike = (String) object.get("Motorbike");
                                Model = (String) object.get("Model");
                                Engine = (String) object.get("Engine");

                                UserSince2 = object.getCreatedAt();
                                UserSince = UserSince2.toString();
                                UserSince = UserSince.substring(3, 11);
                                uSince = UserSince + UserSince2.toString().substring(23, 28);


                                NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

                                de.hdodenhof.circleimageview.CircleImageView cv = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.circleUserProfile);
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


                        } else {
                            Toast.makeText(BikeOfTheDay.this, "Error Finding User Details", Toast.LENGTH_SHORT);
                        }
                    }
                });


            }
        });


    }

}

