package com.example.android.tmdbmovies;

/**
 * Created by Ashok on 5/24/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TNAdapter extends RecyclerView.Adapter<TNAdapter.ViewHolder> {
    private ArrayList<TNUnit> tnUnits;
    private Context context;

    public TNAdapter(Context context,ArrayList<TNUnit> tnUnits) {
        this.tnUnits = tnUnits;
        this.context = context;
    }

    @Override
    public TNAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TNAdapter.ViewHolder viewHolder, int i) {

        viewHolder.tn_text.setText(tnUnits.get(i).getTN_Text());
        Picasso.with(context).load(tnUnits.get(i).getTN_image_url()).resize(120, 120).into(viewHolder.tn_image);
    }

    @Override
    public int getItemCount() {
        return tnUnits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tn_text;
        private ImageView tn_image;
        public ViewHolder(View view) {
            super(view);

            tn_text = (TextView)view.findViewById(R.id.tn_text);
            tn_image = (ImageView) view.findViewById(R.id.tn_img);
        }
    }

}
