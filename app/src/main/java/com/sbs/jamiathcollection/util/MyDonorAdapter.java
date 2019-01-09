package com.sbs.jamiathcollection.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sbs.jamiathcollection.R;

import java.util.List;

public class MyDonorAdapter extends RecyclerView.Adapter<MyDonorAdapter.DonorListHolder> {

    Context context;
    List<Donor> donorList;
    private String[] mDataset;



    public MyDonorAdapter(Context c, List<Donor> lp,String[] mDataset) {
        this.context = c;
        this.donorList = lp;
        this.mDataset=mDataset;
    }

    public class DonorListHolder extends RecyclerView.ViewHolder {
        public MyCustomEditTextListener myCustomEditTextListener;
        TextView tvName;
        TextView tvCount;
        EditText etAmount;

        public DonorListHolder(View itemView,MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_donor_list_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_donor_list_count);
            etAmount = (EditText) itemView.findViewById(R.id.et_Donor_list_amount);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.etAmount.addTextChangedListener(myCustomEditTextListener);

        }
    }

    @Override
    public DonorListHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.donor_list_row, viewGroup, false);
        DonorListHolder ph = new DonorListHolder(v,new MyCustomEditTextListener());
        return ph;
    }

    @Override
    public void onBindViewHolder(final DonorListHolder donorListHolder, final int i) {
        donorListHolder.tvName.setText(donorList.get(i).getDonorName());
        donorListHolder.tvCount.setText((i+1)+"");
        //donorListHolder.etAmount.setText("0");
        donorListHolder.myCustomEditTextListener.updatePosition(donorListHolder.getAdapterPosition());
        donorListHolder.etAmount.setText(mDataset[donorListHolder.getAdapterPosition()]);
    }

    @Override
    public int getItemCount() {
        return donorList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            mDataset[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op

        }
    }

}