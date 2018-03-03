package tk.talcharnes.unborify.OtherFragmentActivities.ContactUs;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import tk.talcharnes.unborify.Models.ContactUsModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 8/31/17.
 * This fragment displays the contact us page and handles the user interactions.
 */

public class ContactUsFragment extends Fragment {

    public static final String CONTACT_TYPE_TIP = "Tip", CONTACT_TYPE_BUG = "Bug" ,
            CONTACT_TYPE_OTHER = "Other";

    private View rootView;
    private Activity activity;
    private EditText messageText;
    private Spinner spinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_contact_us, container, false);
        activity = getActivity();

        initialize();

        return rootView;
    }

    /**
     * Initializes the basic components.
     */
    public void initialize() {
        messageText = rootView.findViewById(R.id.contact_us_message_editText);
        Button submit_contact_us_button = rootView.findViewById(R.id.submit_contact_us_button);
        spinner = rootView.findViewById(R.id.contact_type_spinner);

        final String[] messageTypes = new String[]{getString(R.string.select), CONTACT_TYPE_TIP,
                CONTACT_TYPE_BUG, CONTACT_TYPE_OTHER};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                android.R.layout.simple_spinner_item, messageTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        submit_contact_us_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUserInput()) {
                    ContactUsModel contactUsModel = new ContactUsModel(messageText.getText()
                            .toString(), messageTypes[spinner.getSelectedItemPosition()],
                            DatabaseContants.getCurrentUser().getEmail());

                    DatabaseContants.getContactRef().push().setValue(contactUsModel);

                    spinner.setSelection(0);
                    messageText.setText("");

                    showSuccessDialog();
                }
            }
        });

    }

    /**
     * This method display a display telling the user that his/her message has been sent.
     */
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Success")
                .setMessage("Your message has been sent. Click ok to return to the main screen.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * This method check if the user entered a valid message and choose a category.
     */
    private boolean validateUserInput() {
        String message = messageText.getText().toString();

        if (message.isEmpty()) {
            messageText.setError(getString(R.string.message_empty_error));
            return false;
        } else if (message.length() < 10) {
            messageText.setError("Message too short, please add more details");
            return false;
        } else if (spinner.getSelectedItemPosition() < 1) {
            Toast.makeText(activity, "Please choose a subject", Toast.LENGTH_LONG).show();
            return false;
        }
       return true;
    }
}