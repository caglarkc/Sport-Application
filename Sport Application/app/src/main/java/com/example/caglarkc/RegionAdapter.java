package com.example.caglarkc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RegionAdapter extends BaseAdapter {
    private Context context;
    private List<Region> regionList;

    public RegionAdapter(Context context , List<Region> regionList){
        this.context = context;
        this.regionList = regionList;
    }

    @Override
    public int getCount() {
        return regionList != null ? regionList.size() : 0 ;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_region,viewGroup, false);

        TextView textName = rootView.findViewById(R.id.name);
        ImageView image = rootView.findViewById(R.id.image);

        textName.setText(regionList.get(i).getName());
        image.setImageResource(regionList.get(i).getImage());

        if (regionList.get(i).getName().equals("Choose Region")){
            image.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }
}
