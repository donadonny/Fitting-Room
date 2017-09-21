package tk.talcharnes.unborify.NavigationFragments.Notifications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.SimpleDividerItemDecoration;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;
import tk.talcharnes.unborify.myNotifications;

/**
 * Created by khuramchaudhry on 8/31/17.
 *
 */

public class NotificationFragment extends Fragment {

    private static String TAG = NotificationFragment.class.getSimpleName();

    private View rootView;
    private ProgressBar progressBar;
    private AdView mAdView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.indeterminateBar);
        progressBar.setVisibility(View.VISIBLE);

        loadAd();

        loadNotifications();

        return rootView;
    }

    private void loadAd() {
        mAdView = (AdView) rootView.findViewById(R.id.notification_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadNotifications() {
        final RecyclerView notification_recycle_view = (RecyclerView) rootView.findViewById(R.id
                .notification_recycle_view);
        notification_recycle_view.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        notification_recycle_view.setHasFixedSize(false);
        notification_recycle_view.addItemDecoration(new SimpleDividerItemDecoration(getResources()));

        final Query query = FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                .child(FirebaseConstants.getUser().getUid()).child(FirebaseConstants.NOTIFICATION);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    List<myNotifications> notifications = new ArrayList<myNotifications>();
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        notifications.add(snapshot.getValue(myNotifications.class));
                    }
                    NotificationAdapter adapter = new NotificationAdapter(getActivity(), notifications);
                    notification_recycle_view.setAdapter(adapter);
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
                Log.d(TAG, "Failed to retrieve notifications.");
                Log.d(TAG, databaseError.getMessage());
                Log.d(TAG, databaseError.getDetails());
            }
        });

        progressBar.setVisibility(View.GONE);

    }
}