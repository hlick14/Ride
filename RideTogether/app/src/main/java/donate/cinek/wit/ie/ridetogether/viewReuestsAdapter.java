package donate.cinek.wit.ie.ridetogether;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Kuba Pieczonka on 20/02/2016.
 */
public class viewReuestsAdapter extends ArrayAdapter<String> {
    Context context;
    int layoutResourceId;
    public List<String> costam;

    public viewReuestsAdapter(Context context, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.costam = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        viewRequestHolder holder = null;

        final String k = costam.get(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new viewRequestHolder();
//            holder.imgIcon = (ImageView)row.findViewById(R.id.mapImage);
            holder.txtUsername = (TextView)row.findViewById(R.id.txtTitle);
            holder.accept = (Button)row.findViewById(R.id.accept);



            row.setTag(holder);
        }
        else
        {
            holder = (viewRequestHolder)row.getTag();
        }

        //SoloTrip sTrip = costam[position];
        holder.txtUsername.setText(k);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentUser = ParseUser.getCurrentUser().getUsername();
                ParseObject requestedFriend = new ParseObject("Friends");
                requestedFriend.put("User", currentUser);


                requestedFriend.put("FriendsUsername", k);

//                                                requestedFriend.saveInBackground();
                try {
                    requestedFriend.save();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }


            }
        });

//        holder.imgIcon.setImageBitmap(k.getIcon());


        return row;
    }

    static class viewRequestHolder
    {
//        ImageView imgIcon;
        TextView txtUsername;
        Button accept,deny;

    }
}

