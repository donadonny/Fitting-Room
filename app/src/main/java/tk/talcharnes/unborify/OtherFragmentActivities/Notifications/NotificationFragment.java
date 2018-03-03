package tk.talcharnes.unborify.OtherFragmentActivities.Notifications;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;
import tk.talcharnes.unborify.Utilities.SimpleDividerItemDecoration;
import tk.talcharnes.unborify.Models.NotificationModel;

/**
 * Created by Khuram Chaudhry on 8/31/17.
 * This fragment handles user interaction with the notification screen.
 */

public class NotificationFragment extends Fragment {

    private static String TAG = NotificationFragment.class.getSimpleName();

    private View rootView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.VISIBLE);

        loadAd();

        loadNotifications();

        return rootView;
    }

    /**
     * This method sets up the banner ad.
     */
    private void loadAd() {
        AdView mAdView = (AdView) rootView.findViewById(R.id.notification_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    /**
     * This method loads the notifications for the user.
     */
    private void loadNotifications() {
        final RecyclerView notificationRecycleView = (RecyclerView) rootView.findViewById(R.id
                .notification_recycle_view);
        notificationRecycleView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        notificationRecycleView.setHasFixedSize(false);
        notificationRecycleView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));

        DatabaseContants.getCurrentUserNotificationRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<NotificationModel> notifications = new ArrayList<NotificationModel>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        notifications.add(snapshot.getValue(NotificationModel.class));
                    }
                    NotificationAdapter adapter = new NotificationAdapter(getActivity(), notifications);
                    notificationRecycleView.setAdapter(adapter);
                } else {
                    RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.main_view);
                    TextView textView = new TextView(getActivity());
                    textView.setText("You have no notifications. :(");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setLayoutParams(new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));
                    relativeLayout.addView(textView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
        progressBar.setVisibility(View.GONE);
    }
}