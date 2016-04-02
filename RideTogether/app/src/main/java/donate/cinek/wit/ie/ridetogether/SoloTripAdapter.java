package donate.cinek.wit.ie.ridetogether;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Cinek on 26/02/2015.
 */
public class SoloTripAdapter extends ArrayAdapter<SoloTrip> {

    Context context;
    int layoutResourceId;
    public List<SoloTrip> costam;

    public SoloTripAdapter(Context context, int layoutResourceId, List<SoloTrip> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.costam = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        SoloTripHolder holder = null;

        SoloTrip k = costam.get(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new SoloTripHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.mapImage);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            holder.txtStartCity =(TextView)row.findViewById(R.id.txtStartCity);
            holder.txtEndCity =(TextView)row.findViewById(R.id.txtEndCity);
            holder.txtDate =(TextView)row.findViewById(R.id.txtTripDate);
            holder.txtTime =(TextView)row.findViewById(R.id.txtTripTime);


            row.setTag(holder);
        }
        else
        {
            holder = (SoloTripHolder)row.getTag();
        }

        //SoloTrip sTrip = costam[position];

        String time = k.getTripTime();
        int hour,minute;
        if (time.length() == 3) {
             hour = Integer.valueOf(time.substring(0, 1));
             minute = Integer.valueOf(time.substring(1, time.length()));
        }
        else
        {
             hour = Integer.valueOf(time.substring(0, 2));
             minute = Integer.valueOf(time.substring(2, time.length()));
        }


        holder.txtTitle.setText(k.getTitle());
        holder.imgIcon.setImageBitmap(k.getIcon());
        holder.txtStartCity.setText("Start In: " + k.getStartCity() + "  ");
        holder.txtEndCity.setText("End In:  " + k.getEndCity() + "  ");
        holder.txtDate.setText("Date: "+k.getTripDate());
        holder.txtTime.setText("Time: "+hour + ":" + minute);

        return row;
    }

    static class SoloTripHolder
    {
        ImageView imgIcon;
        TextView txtTitle,txtStartCity,txtEndCity,txtDate,txtTime;

    }
}
