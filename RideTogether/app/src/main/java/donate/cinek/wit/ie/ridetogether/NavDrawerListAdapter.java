package donate.cinek.wit.ie.ridetogether;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private List<User> navDrawerItems;

    public NavDrawerListAdapter(Context context, List<User> navDrawerItems){
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.drawer_icon);
        TextView txtUsername = (TextView) convertView.findViewById(R.id.drawerUsername);
        TextView txtName = (TextView) convertView.findViewById(R.id.drawerName);
        TextView txtMotorbike = (TextView) convertView.findViewById(R.id.drawerMotorbike);
        TextView txtUserSince = (TextView) convertView.findViewById(R.id.drawer_userSince);

        if (navDrawerItems.get(position).getIcon()==null)
        {
            imgIcon.setImageResource(R.drawable.userplain2);
        }
        else {
            imgIcon.setImageBitmap(navDrawerItems.get(position).getIcon());
        }

        txtUsername.setText("Username: "+navDrawerItems.get(position).getUsername());
        txtName.setText("Name: "+navDrawerItems.get(position).getName());
        txtMotorbike.setText("Motorbike: "+navDrawerItems.get(position).getMotorbike());
        txtUserSince.setText("User Since: "+navDrawerItems.get(position).getUserSince());



        return convertView;
    }
    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
