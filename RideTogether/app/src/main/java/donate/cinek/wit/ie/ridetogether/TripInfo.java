package donate.cinek.wit.ie.ridetogether;

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
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


public class TripInfo extends BaseActivity {

            ImageView icon;
    RelativeLayout editLayout;
    Button editButton,eConfirm,eTime,eDate,eDelete;
    Animation slide_down, slide_up;
    String sName,sTime,sDate,sStart,sEnd,sId;
    Bitmap sIcon;
    private FragmentFive five;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    public int year, month, day, hour, minute;
    int check;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private double startLat, finishLat, startLong, finishLong;

    private GestureDetectorCompat gDetect;

    private MenuView.ItemView it;

    private DrawerLayout mDrawerLayout;
    TextView tv1,tv2,tv3,tv4;
    String start,end,date;
    String name;
    String username ;
    String Motorbike ;
    String UserSince ;
    Date UserSince2;
    Bitmap bitmap ;
    String uSince;
    String Model,Engine;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tv;
    private android.support.v7.widget.Toolbar toolbar;
    private int[] tabIcons = {
            R.drawable.ic_offline_pin_white_18dp,
            R.drawable.ic_navigation_white_18dp,
            R.drawable.ic_access_time_white_18dp

    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_info);
        ////////////////////////////////Tabs

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

//        tv = (TextView)findViewById(R.id.name);
//        tv.setText(super.name);

//        it = (MenuView.ItemView)findViewById(R.id.navigation_item_attachment);
        getUserData();
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Your Trip");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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
                    Intent backToMain = new Intent(TripInfo.this, MainActivity.class);
                    startActivity(backToMain);
                }
                return true;
            }
        });


        /////////////////////////date and time



        ////////////////////////////////


        ////////////////////////////////////
        Bundle extras = getIntent().getExtras();


        if (extras != null) {


            sStart = extras.getString("mapDetails");
            sEnd = extras.getString("mapDetails2");

        }
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
//            case R.id.action_settings:
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void setupTabIcons() {
        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Date & Time");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_access_time_white_18dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("A-B");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_navigation_white_18dp, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);






    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FourFragment(), "Four");
        adapter.addFragment(new FragmentFive(), "Five");
//        adapter.addFragment(new FragmentSix(), "Four");
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
    public void getUserData()
    {


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


                                //navDrawerItems.add(new NavDrawerItem(

//                                if (navDrawerItems.size() > 0)        {
//                                    navDrawerItems.clear();
//                                }
//                                navDrawerItems.add(new User(bitmap, name, username, Motorbike, UserSince));
                                //set();

                            }


                        }
                    }
                });


            }
        });


    }

}


