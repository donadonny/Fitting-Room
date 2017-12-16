package tk.talcharnes.unborify.Models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import tk.talcharnes.unborify.R;

/**
 * Created by coolm_000 on 12/16/2017.
 */

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {
    private List<String> values;


    public DealsAdapter(List<String> myDataset){
        values=myDataset;
    }

    //Provide a reference to the views for each data item
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mDealsDescritpion;
        public TextView mDealsDateRange;
        public View mLayout;

        public ViewHolder(View v){
            super(v);
            mLayout=v;
            mDealsDescritpion=(TextView)v.findViewById(R.id.dealsDescription);
            mDealsDateRange=(TextView)v.findViewById(R.id.dateRangeDeals);
        }

    }
    //Add to recycler view
    public void add(int position, String item){
        values.add(position,item);
        notifyItemInserted(position);
    }
    //Remove from recycler view
    public void remove(int position){
        values.remove(position);
        notifyItemRemoved(position);
    }


    // Create new views (invoked by the layout manager)
    @Override
    public DealsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View v= inflater.inflate(R.layout.deal_row_layout,parent,false);
        ViewHolder vh= new ViewHolder(v);
        return vh;
    }

    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String NAME= values.get(position);
        holder.mDealsDescritpion.setText(NAME);
        holder.mDealsDescritpion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             // open up new view in internal website with deal
            }
        });
        int year= Calendar.YEAR;
        holder.mDealsDateRange.setText("Expire Date:"+ year);
    }

    @Override
    public int getItemCount() {
        return values.size();
    }
}
