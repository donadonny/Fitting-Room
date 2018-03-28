package tk.talcharnes.unborify.MainNavigationFragments.Deals;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import tk.talcharnes.unborify.Models.DealsModel;
import tk.talcharnes.unborify.R;

/**
 * Created by Marzin on 12/16/2017.
 */

public class DealsAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mDealsDescription;
    private TextView mDealsDateRange;
    private View mView;

    DealsAdapter(View view) {
        super(view);
        mView = view;
        mDealsDescription = (TextView) view.findViewById(R.id.dealsDescription);
        mDealsDateRange = (TextView) view.findViewById(R.id.dateRangeDeals);
    }

    void onBindDeal(DealsModel deal) {
        mDealsDescription.setText(deal.getName());
        mDealsDateRange.setText(deal.getExpirationDate());
    }

    @Override
    public void onClick(View v) {
        // open up new view in internal website with deal
    }
}