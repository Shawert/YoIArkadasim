package com.example.travelbook.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Placeholder;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbook.databinding.RecylerRowBinding;
import com.example.travelbook.model.Place;
import com.example.travelbook.view.MapsActivity;
import com.google.android.material.transition.Hold;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.Placeholder> {
   
    List<Place> placeList;
   
    public  PlaceAdapter(List<Place> placeList){
        this.placeList = placeList;
    }

    @Override

    public Placeholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecylerRowBinding recylerRowBinding = RecylerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new Placeholder(recylerRowBinding);
    }

    @Override
    public void onBindViewHolder( PlaceAdapter.Placeholder holder, int position) {
        holder.recylerRowBinding.recyclerViewTextView.setText(placeList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("place",placeList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public  class  Placeholder extends RecyclerView.ViewHolder{
        RecylerRowBinding recylerRowBinding;
        public Placeholder( RecylerRowBinding recylerRowBinding) {

            super(recylerRowBinding.getRoot());
            this.recylerRowBinding = recylerRowBinding;
        }
    }

}
