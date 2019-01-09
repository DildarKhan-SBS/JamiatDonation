package com.sbs.jamiathcollection.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sbs.jamiathcollection.R;

import java.util.ArrayList;
import java.util.List;

public class CollectorAdapter extends RecyclerView.Adapter<CollectorAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<Collector> collectorList;
    private List<Collector> collectorListFiltered;
    private CollectorAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView cName;
        public TextView cId;
        public TextView cPhone;
        public ImageButton ibCollectorNext;

        public MyViewHolder(View view) {
            super(view);
            cName = view.findViewById(R.id.tv_collectorList_name);
            cId = view.findViewById(R.id.tv_collectorList_id);
            cPhone = view.findViewById(R.id.tv_collectorList_phone);
            ibCollectorNext=view.findViewById(R.id.ib_collectorList_next);
            ibCollectorNext.setOnClickListener(this);
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
            System.out.println(cPhone.getText().toString());

            //listenerRef.get().onPositionClicked(getAdapterPosition());

        }
    }

    public CollectorAdapter(Context context, List<Collector> cList, CollectorAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.collectorList = cList;
        this.collectorListFiltered = cList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.collector_list_row, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Collector collector = collectorListFiltered.get(position);
        holder.cName.setText(collector.getCollectorName());
        // Displaying dot from HTML character code
        holder.cId.setText(collector.getCollectorId());
        // Formatting and displaying timestamp
        holder.cPhone.setText(collector.getCollectorPhone());

    }

    @Override
    public int getItemCount() {
        return collectorListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    collectorListFiltered = collectorList;
                } else {
                    List<Collector> filteredList = new ArrayList<>();
                    for (Collector row : collectorList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCollectorPhone().toLowerCase().contains(charString.toLowerCase()) || row.getCollectorName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    collectorListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = collectorListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                collectorListFiltered = (ArrayList<Collector>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CollectorAdapterListener {
        void onItemSelected(Collector item);
    }
}