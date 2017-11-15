package tk.talcharnes.unborify.MainNavigationFragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import tk.talcharnes.unborify.OtherFragmentActivities.About.AboutActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.ContactUs.ContactUsActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.Favorites.FavoritesActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.Help.HelpActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.FavoritesAndLikes.LIkes.LikesActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.MyPhotos.MyPhotosActivity;
import tk.talcharnes.unborify.OtherFragmentActivities.Notifications.NotificationsActivity;
import tk.talcharnes.unborify.R;

/**
 * Created by khuramchaudhry on 9/29/17.
 *
 */

public class OtherFragment extends Fragment {

    private static final String TAG = OtherFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_other, container, false);

        rootView.findViewById(R.id.nav_photos).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MyPhotosActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.nav_notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotificationsActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.nav_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.nav_contact_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactUsActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.nav_about_us).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.nav_likes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LikesActivity.class);
                startActivity(intent);
            }
        });

        rootView.findViewById(R.id.nav_favorites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FavoritesActivity.class);
                startActivity(intent);
            }
        });


        rootView.findViewById(R.id.nav_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        return rootView;
    }
}