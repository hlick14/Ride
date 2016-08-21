package donate.cinek.wit.ie.ridetogether;

/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    public int val;


    Context context;
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;


        ImageView imageViewIcon;
        ImageView im,im2;
        TextView votes;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.im = (ImageView) itemView.findViewById(R.id.imageView3);
            this.votes = (TextView) itemView.findViewById(R.id.textViewRating);
            this.im2 = (ImageView) itemView.findViewById(R.id.imageView4);


        }
    }

    public CustomAdapter(ArrayList<DataModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards_layout, parent, false);
        context = parent.getContext();


        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewName = holder.textViewName;

        ImageView imageView = holder.imageViewIcon;
        ImageView im = holder.im;
        ImageView im2 = holder.im2;
        final TextView votes = holder.votes;



        textViewName.setText(dataSet.get(listPosition).getName());
        votes.setText(String.valueOf(dataSet.get(listPosition).getVotes()));



        imageView.setImageBitmap(dataSet.get(listPosition).getImage());
        holder.im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                votes = votes +1;
//                textViewVotes.setText(String.valueOf(votes));
                val = dataSet.get(listPosition).getVotes();
                val = val+1;
                val = dataSet.get(listPosition).setVotes(val);
                votes.setText(String.valueOf(val));

                Log.v("RideCustomAdapter", String.valueOf(val));
                val=0;


            }
        });
        holder.im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                val = dataSet.get(listPosition).getVotes();
                if (val > 0)
                {
                    val = val-1;
                    val = dataSet.get(listPosition).setVotes(val);
                    votes.setText(String.valueOf(val));


                }
                else if (val ==0){
                    Toast.makeText(context, "Can't go below zero !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

}
