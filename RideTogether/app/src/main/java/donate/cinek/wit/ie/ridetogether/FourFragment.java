package donate.cinek.wit.ie.ridetogether;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

/**
 * Created by Kuba Pieczonka on 05/11/2015.
 */
public class FourFragment extends android.support.v4.app.Fragment {


    public FourFragment() {
        // Required empty public constructor
    }
    public View fragmentView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle extras = getActivity().getIntent().getExtras();
        String sStart = extras.getString("mapDetails");
        String sEnd = extras.getString("mapDetails2");

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getApplicationContext()
                    .openFileInput("myImage"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        final Bitmap bitmap =intent.getParcelableExtra("screenshot");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);



        fragmentView = inflater.inflate(R.layout.fragment_four, container, false);
        FloatingActionButton fab = (FloatingActionButton)fragmentView.findViewById(R.id.fab);
        TextView tv = (TextView)fragmentView.findViewById(R.id.txtStartCity2);
        tv.setText("Starting Point: " + sStart);
        TextView tv2 =(TextView)fragmentView.findViewById(R.id.txtEndCity);
        tv2.setText("End Point : " + sEnd);
        ImageView iv = (ImageView)fragmentView.findViewById(R.id.imageView2);

        iv.setImageBitmap(bitmap);
        Intent UserTrips = new Intent(getActivity(), FragmentSix.class);
        UserTrips.putExtra("Start", sStart);
        UserTrips.putExtra("End", sEnd);
        return fragmentView;
    }

}