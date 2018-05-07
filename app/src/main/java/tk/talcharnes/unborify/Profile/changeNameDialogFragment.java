package tk.talcharnes.unborify.Profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.DatabaseContants;

/**
 * Created by Khuram Chaudhry on 9/21/17.
 * This fragment displays a dialog in which the user can change their name.
 */

public class changeNameDialogFragment extends DialogFragment {

    static final String TAG = changeNameDialogFragment.class.getSimpleName();

    private onNameChangeListener mListener;
    private AlertDialog dialog;

    /**
     * This interface is used to send data between the Activity and this DialogFragment.
     */
    public interface onNameChangeListener {
        void onChange(String name);
    }

    /**
     * onAttach is overrided to make sure the Activity implemented the listener.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (onNameChangeListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onNameChangeListener");
        }
    }

    /**
     * onAttach is overrided to make sure the Activity implemented the listener.
     * This method deals with older devices.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                this.mListener = (onNameChangeListener) activity;
            } catch (final ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement onNameChangeListener");
            }
        }
    }

    /**
     * Initializes basic initialization of components of the dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        if(getActivity() != null) {
            LayoutInflater inflater = getActivity().getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_change_name, null))
                    // Add action buttons
                    .setPositiveButton("CHANGE NAME", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
        }
        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        setDialogListener();
        return dialog;
    }

    /**
     * This method sets the custom listener for the positive button.
     */
    public void setDialogListener() {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = true;
                final EditText newName = (EditText) dialog.findViewById(R.id.input_new_name);
                final String name = newName.getText().toString();

                if (name.length() < 5 || name.length() > 25) {
                    newName.setError("enter between 5 and 25 characters");
                    wantToCloseDialog = false;
                }
                if (wantToCloseDialog) {
                    FirebaseUser user = DatabaseContants.getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User name updated.");
                                    }
                                }
                            });
                    DatabaseContants.getCurrentUserRef().child("name").setValue(name);
                    mListener.onChange(name);
                    dismiss();
                }
            }
        });
    }

    /**
     * onStart is overrided to change the colors of the dialog buttons.
     */
    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }
}
