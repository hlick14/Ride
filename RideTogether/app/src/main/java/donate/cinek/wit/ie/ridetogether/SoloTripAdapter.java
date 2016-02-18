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
//            holder.txtDate =(TextView)row.findViewById(R.id.txtDate);
//            holder.txtTime =(TextView)row.findViewById(R.id.txtTime);


            row.setTag(holder);
        }
        else
        {
            holder = (SoloTripHolder)row.getTag();
        }

        //SoloTrip sTrip = costam[position];
        holder.txtTitle.setText(k.getTitle());
        holder.imgIcon.setImageBitmap(k.getIcon());
        holder.txtStartCity.setText("Start "+"\n"+k.getStartCity()+ "  ");
        holder.txtEndCity.setText("End "+"\n"+k.getEndCity()+ "  ");
//        holder.txtDate.setText("Date "+"\n"+k.getTripDate());
//        holder.txtTime.setText("Time "+"\n"+k.getTripTime());

        return row;
    }

    static class SoloTripHolder
    {
        ImageView imgIcon;
        TextView txtTitle,txtStartCity,txtEndCity,txtDate,txtTime;

    }
}
