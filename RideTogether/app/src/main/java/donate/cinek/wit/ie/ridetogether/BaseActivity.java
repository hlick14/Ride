package donate.cinek.wit.ie.ridetogether;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    protected RelativeLayout _completeLayout, _activityLayout;
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;
    Bitmap icon;
    String name;
    String username ;
    String Motorbike ;
    String UserSince ;
    Date UserSince2;
    Bitmap bitmap ;
    static List<User> navDrawerItems= new ArrayList<>();
    private NavDrawerListAdapter adapter;

    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
//        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);

//        String currentUser = ParseUser.getCurrentUser().getUsername();
//        ParseUser currentUser2 = ParseUser.getCurrentUser();
//
//
//
//        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("ImageUpload");
//        query2.whereEqualTo("CreatedbyUser", currentUser2);
//        //Toast.makeText(BaseActivity.this, currentUser2.toString() , Toast.LENGTH_LONG).show();
//        query2.getFirstInBackground(new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (object != null) {
//
//                    ParseFile file = (ParseFile)object.get("ImageFile");
//                    file.getDataInBackground(new GetDataCallback() {
//                        public void done(byte[] data, ParseException e) {
//                            if (e == null) {
//                                bitmap=BitmapFactory.decodeByteArray(data, 0, data.length);
//
//                                // Toast.makeText(BaseActivity.this, "" + bitmap.getHeight() , Toast.LENGTH_SHORT).show();
//                                //use this bitmap as you want
//                            } else {
//                                // something went wrong
//                            }
//                        }
//                    });
//
//                } else {
//                    // Toast.makeText(BaseActivity.this, "objekt jest pusty" , Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//
//        final ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.whereEqualTo("username", currentUser);
//        /////////////////////////NEEEEEED TO READ IN THE VALUES AND SEND THEM TO ADPATER LOOK
//
//        query.findInBackground(new FindCallback<ParseUser>() {
//            @Override
//            public void done(List<ParseUser> parseObjects, ParseException e) {
//                query.findInBackground(new FindCallback<ParseUser>() {
//                    public void done(List<ParseUser> UserDetails, ParseException e) {
//                        if (e == null) {
//                            if (UserDetails == null || UserDetails.size() == 0) {
//
//
//                                return; //no objects
//                            }
//
//
//                            for (int i = 0; i < UserDetails.size(); i++) {
//
//                                ParseObject object = UserDetails.get(i);
//
//
//
//
//
//
//
////                                int icon =(int) object.get("TripName");
//                                name = (String) object.get("Name");
//                                username = (String) object.get("username");
//                                Motorbike = (String) object.get("Motorbike");
//
//                                UserSince2 = object.getCreatedAt();
//                                UserSince=UserSince2.toString();
//                                UserSince = UserSince.substring(3, 11);
//                                UserSince = UserSince + UserSince2.toString().substring(23, 28);
//
//
//                                //navDrawerItems.add(new NavDrawerItem(
//
////                                if (navDrawerItems.size() > 0)        {
////                                    navDrawerItems.clear();
////                                }
////                                navDrawerItems.add(new User(bitmap, name, username, Motorbike, UserSince));
//                                //set();
//
//                            }
//
//
//
//                        }
//                    }
//                });
//
//
//
//
//
//            }
//        });
//

    }



//    public void set() {
//
//
//        //mTitle = mDrawerTitle = getTitle();
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        mDrawerList = (ListView) findViewById(R.id.left_drawer);
//        adapter = new NavDrawerListAdapter(getApplicationContext(),
//                navDrawerItems);
//
//        mDrawerList.setAdapter(adapter);
//
//// enabling action bar app icon and behaving it as toggle button
//            //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            //getSupportActionBar().setHomeButtonEnabled(true);
//
//// getSupportActionBar().setIcon(R.drawable.ic_drawer);
//
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                // nav menu toggle icon
//                R.string.app_name, // nav drawer open - description for
//// accessibility
//                R.string.app_name // nav drawer close - description for
//// accessibility
//        )  {
//            public void onDrawerClosed(View view) {
//                //getSupportActionBar().setTitle(mTitle);
//// calling onPrepareOptionsMenu() to show action bar icons
//                supportInvalidateOptionsMenu();
//            }
//
//            public void onDrawerOpened(View drawerView) {
//               // getSupportActionBar().setTitle(mDrawerTitle);
//// calling onPrepareOptionsMenu() to hide action bar icons
//                supportInvalidateOptionsMenu();
//            }
//        };
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//    }
//    private class SlideMenuClickListener implements
//            ListView.OnItemClickListener {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//// display view for selected nav drawer item
//            displayView(position);
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//// getSupportMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == android.R.id.home) {
//            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
//                mDrawerLayout.closeDrawer(mDrawerList);
//            } else {
//                mDrawerLayout.openDrawer(mDrawerList);
//            }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /***
//     * Called when invalidateOptionsMenu() is triggered
//     */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//// if nav drawer is opened, hide the action items
//// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//// menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    /**
//     * Diplaying fragment view for selected nav drawer list item
//     * */
//    private void displayView(int position) {
//// update the main content by replacing fragments
//        switch (position) {
//            case 0:
//
//                break;
//            case 1:
//
//                finish();
//                break;
//            case 2:
//
//                finish();
//                break;
//            case 3:
//
//                finish();
//                break;
//            case 4:
//
//                finish();
//                break;
//            case 5:
//
//                finish();
//                break;
//            default:
//                break;
//        }
//
//// update selected item and title, then close the drawer
//        mDrawerList.setItemChecked(position, true);
//        mDrawerList.setSelection(position);
//        mDrawerLayout.closeDrawer(mDrawerList);
//    }
//
//    @Override
//    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getSupportActionBar().setTitle(mTitle);
//    }
//
//    /**
//     * When using the ActionBarDrawerToggle, you must call it during
//     * onPostCreate() and onConfigurationChanged()...
//     */
//
//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//// Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//// Pass any configuration change to the drawer toggls
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
}


