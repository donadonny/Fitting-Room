package tk.talcharnes.unborify.NavigationFragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import tk.talcharnes.unborify.R;

/**
 * Created by khuramchaudhry on 8/31/17.
 *
 */

public class ContactUsFragment extends Fragment {

    private ImageButton emailButton, phoneButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_contact_us, container, false);

        emailButton = (ImageButton) rootview.findViewById(R.id.companyEmail);
        phoneButton = (ImageButton) rootview.findViewById(R.id.companyPhone);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = "Feedback";
                String mailto = "mailto:something@gmail.com" + "?subject=" + Uri.encode(subject);

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no email app is available
                    Toast.makeText(getActivity(), "No email app is available",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "9999999999";
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", phoneNumber, null));

                try {
                    startActivity(phoneIntent);
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no phone app is available
                    Toast.makeText(getActivity(), "Unable to dial. Check if your device has" +
                            " calling capabilities.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootview;
    }
}