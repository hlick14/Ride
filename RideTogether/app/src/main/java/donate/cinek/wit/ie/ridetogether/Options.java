package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.MenuView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ImageButton;
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


public class Options extends BaseActivity {
    protected ImageButton plan;
    protected ImageButton showUserTrips;
    protected ImageButton showAllTrips;
    protected ImageButton account;
    //message text view
    private TextView messageView;
    //message array
    private String[] messages;
    //total messages
    private int numMessages = 4;
    //current message -start at zero
    private int currMessage = 0;
    private GestureDetectorCompat gDetect;

    private MenuView.ItemView it;

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


    String name;
    String username;
    String Motorbike;
    String UserSince;
    Date UserSince2;
    Bitmap bitmap;
    String uSince;
    String Model, Engine;

    ProgressDialog dialog;
    private List<Bitmap> imgList = new ArrayList<Bitmap>();
    public List<SoloTrip> hujumuniu = new ArrayList<>();
    public List<ParseObject> tempObjectHolder = new ArrayList<>();
    String tName, tDate, tTime;
    int t = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options2);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getUserData();


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(1);

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
                    Intent backToMain = new Intent(Options.this, MainActivity.class);
                    startActivity(backToMain);
                }
                return true;
            }
        });
        //Used for updating the city by text , city update gonna be gathered straight from users location

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new TwoFragment())
//                    .commit();
//        }
//        getListOfUsers();

    }





private void setupTabIcons() {
        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Profile");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.home, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Solo Rides");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.solo, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);


        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Group Rides");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.group, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);

        return super.onCreateOptionsMenu(menu);

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
            case R.id.change_city:

                final Intent addFriendIntent = new Intent(getApplicationContext(), addFriend.class);

                startActivity(addFriendIntent);

        }

        return super.onOptionsItemSelected(item);
    }



    public void changeCity(String city) {

        TwoFragment wf = (TwoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_two);

        wf.changeCity(city);
        new CityPreference(this).setCity(city);
    }

    //set();
    @Override
    public void onBackPressed() {
        Intent backToMain = new Intent(Options.this, MainActivity.class);
        ParseUser.logOut();

        startActivity(backToMain);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
//        viewPager.setCurrentItem(2);
        viewPager.setAdapter(adapter);
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
                                Toast.makeText(Options.this, "Error Loading Data From Our Servers - Image Problem", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                } else {
                    Toast.makeText(Options.this, "Error Loading Data From Our Servers - User Problem", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(Options.this, "Error Finding User Details", Toast.LENGTH_SHORT);
                        }
                    }
                });


            }
        });


    }
}



