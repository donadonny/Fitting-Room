package tk.talcharnes.unborify.NavigationFragments.Notifications;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.myNotifications;

/**
 * Created by khuramchaudhry on 9/17/17.
 *
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ItemRowHolder> {

    private Context mContext;
    private List<myNotifications> dataList;
    private int num = 5;

    NotificationAdapter(Context context, List<myNotifications> dataList) {
        this.mContext = context;
        this.dataList = dataList;
    }

    @Override
    public NotificationAdapter.ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_notification,
                parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ItemRowHolder holder, int position) {
        myNotifications notification = dataList.get(position);
        holder.message.setText(notification.getMessage());
        holder.senderID.setText(notification.getSenderID());
    }

    @Override
    public int getItemCount() {
        return (dataList.isEmpty()) ? 0 : dataList.size();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {

        private TextView message, senderID;

        ItemRowHolder(View view) {
            super(view);

            if(!dataList.isEmpty()) {
                this.message = (TextView) view.findViewById(R.id.message);
                this.senderID = (TextView) view.findViewById(R.id.senderID);
            }
        }

    }

}
