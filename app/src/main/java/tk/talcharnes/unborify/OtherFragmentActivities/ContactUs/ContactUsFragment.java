package tk.talcharnes.unborify.OtherFragmentActivities.ContactUs;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import tk.talcharnes.unborify.Models.ContactUsModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 8/31/17.
 */

public class ContactUsFragment extends Fragment {

    private DatabaseReference mDatabaseReference;
    private EditText contact_us_message_editText;
    private Button submit_contact_us_button;
    private Spinner spinner;
    private String[] messageTypes;
    private int spinnerPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_contact_us, container, false);
        contact_us_message_editText = rootview.findViewById(R.id.contact_us_message_editText);
        submit_contact_us_button = rootview.findViewById(R.id.submit_contact_us_button);
        spinner = rootview.findViewById(R.id.contact_type_spinner);
        spinnerPosition = 0;

        messageTypes = new String[]{getString(R.string.select), FirebaseConstants.CONTACT_TYPE_TIP,
                FirebaseConstants.CONTACT_TYPE_BUG, FirebaseConstants.CONTACT_TYPE_OTHER};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, messageTypes);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerPosition = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                spinnerPosition = 0;
            }
        });


        mDatabaseReference = FirebaseConstants.getRef().child(FirebaseConstants.CONTACT_US);


        submit_contact_us_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = contact_us_message_editText.getText().toString();
                if (messageGood(message) && spinnerNotEmpty()) {
                    ContactUsModel contactUsModel = new ContactUsModel(message,
                            messageTypes[spinnerPosition], FirebaseConstants.getUser().getEmail());

                    /*contactUsModel.setMessage(message);
                    String email = FirebaseConstants.getUserUid().getEmail();
                    contactUsModel.setEmail(email);
                    contactUsModel.setContactType(messageTypes[spinnerPosition]);*/

                    mDatabaseReference.push().setValue(contactUsModel);
                    // TODO: Go to main activity fragment. If that destroys current fragment, remove next 2 lines
                    spinner.setSelection(0);
                    contact_us_message_editText.setText("");
                    showSuccessDialog();
                }
            }
        });


        return rootview;
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Holo_Light_Dialog);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle("Success")
                .setMessage("Your message has been sent. Click ok to return to the main screen.")
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .show();

    }

    private boolean messageGood(String message) {
        if (message.isEmpty()) {
            contact_us_message_editText.setError(getString(R.string.message_empty_error));
            return false;
        } else if (message.length() < 10) {
            contact_us_message_editText.setError("Message too short, please add more details");
            return false;
        } else return true;
    }

    private boolean spinnerNotEmpty() {
        if (spinnerPosition != 0) {
            return true;
        } else {
            Toast.makeText(getContext(), "Please choose a subject", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}