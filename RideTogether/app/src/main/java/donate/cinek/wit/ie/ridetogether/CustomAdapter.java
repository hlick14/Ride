package donate.cinek.wit.ie.ridetogether;

/**
 * Created by Kuba Pieczonka on 02/04/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;
    int votes = 0;
    TextView textViewVotes;
    Context context;
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName;


        ImageView imageViewIcon;
        ImageView im;
        TextView votes;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView);
            this.im = (ImageView) itemView.findViewById(R.id.imageView3);
            this.votes = (TextView) itemView.findViewById(R.id.textViewRating);


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
          textViewVotes = holder.votes;


        textViewName.setText(dataSet.get(listPosition).getName());
        textViewVotes.setText(String.valueOf(0));


        imageView.setImageDrawable(dataSet.get(listPosition).getImage());
        holder.im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                votes = votes +1;
                textViewVotes.setText(String.valueOf(votes));
                Toast.makeText(context, "VotesUp", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
