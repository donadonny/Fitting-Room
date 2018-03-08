package tk.talcharnes.unborify.MainNavigationFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import tk.talcharnes.unborify.OtherFragmentActivities.About.AboutActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.ContactUs.ContactUsActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.Favorites.FavoritesActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.LIkes.LikesActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.Help.HelpActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos.MyPhotosActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.Notifications.NotificationsActivity;
import tk.talcharnes.unborify.Profile.ProfileActivity;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/29/17.
 * This fragment display other options for the user to interact with.
 */

public class OtherFragment extends Fragment {

    private static final String TAG = OtherFragment.class.getSimpleName();

    HashMap<Integer, Class> options = new HashMap<>();
    {
        options.put(R.id.nav_photos, MyPhotosActivity.class);
        options.put(R.id.my_profile_button, ProfileActivity.class);
        options.put(R.id.nav_notifications, NotificationsActivity.class);
        options.put(R.id.nav_help, HelpActivity.class);
        options.put(R.id.nav_contact_us, ContactUsActivity.class);
        options.put(R.id.nav_about_us, AboutActivity.class);
        options.put(R.id.nav_likes, LikesActivity.class);
        options.put(R.id.nav_favorites, FavoritesActivity.class);
        options.put(R.id.nav_sign_out, null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);

        for(final Integer viewId : options.keySet()) {
            rootView.findViewById(viewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(viewId == R.id.nav_sign_out) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.sign_out_title)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        FirebaseAuth.getInstance().signOut();
                                        getActivity().finish();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Log.d(TAG, "Canceling out sign out dialog.");
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    } else {
                        Intent intent = new Intent(getActivity(), options.get(viewId));
                        if(viewId == R.id.my_profile_button) {
                            String uid = DatabaseContants.getCurrentUser().getUid();
                            intent.putExtra("uid", uid);
                        }
                        intent.putExtra("fragmentNumber", 4);
                        startActivityForResult(intent, 0);
                    }
                }
            });
        }

        return rootView;
    }
}