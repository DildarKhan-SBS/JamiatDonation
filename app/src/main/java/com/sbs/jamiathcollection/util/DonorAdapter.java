package com.sbs.jamiathcollection.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbs.jamiathcollection.R;

import java.util.ArrayList;
import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.MyViewHolder> {
    private Context context;
    private List<Donor> donorList;
    private DonorAdapterListener listener;
    //private String[] mDataset;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dName;
        public TextView dId;
        public TextView dPhone;
        public ImageButton ibDonorNext;

        public MyViewHolder(View view) {
            super(view);
            dName = view.findViewById(R.id.tv_searched_donorList_name);
            dId = view.findViewById(R.id.tv_searched_donorList_name);
            dPhone = view.findViewById(R.id.tv_searched_donorList_phone);
            ibDonorNext=view.findViewById(R.id.ib_search_donorList_next);
            ibDonorNext.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            /*
            if (v.getId() == cPhone.getId()) {
                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
            */
            System.out.println(dPhone.getText().toString());

            //listenerRef.get().onPositionClicked(getAdapterPosition());

        }
    }
    public DonorAdapter(Context context, List<Donor> donorList,DonorAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.donorList = donorList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.searched_donor_list_row, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Donor donor = donorList.get(position);
        holder.dName.setText(donor.getDonorName());
        // Displaying dot from HTML character code
        holder.dPhone.setText("");
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }



    public interface DonorAdapterListener {
        void onItemSelected(Donor donor);
    }
}