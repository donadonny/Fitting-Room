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
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 9/21/17.
 */

public class changeNameDialogFragment extends DialogFragment {

    static final String TAG = changeNameDialogFragment.class.getSimpleName();

    public static interface onNameChangeListener {
        public abstract void onChange(String name);
    }

    private onNameChangeListener mListener;

    // make sure the Activity implemented it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (onNameChangeListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onNameChangeListener");
        }
    }

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
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
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = true;
                final EditText newName = (EditText) dialog.findViewById(R.id.input_new_name);
                final String name = newName.getText().toString();

                if (name.isEmpty() || name.length() < 5 || name.length() > 25) {
                    newName.setError("enter between 5 and 25 characters");
                    wantToCloseDialog = false;
                }
                if (wantToCloseDialog) {
                    FirebaseUser user = FirebaseConstants.getUser();
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
                    FirebaseConstants.getRef().child(FirebaseConstants.USERS)
                            .child(FirebaseConstants.getUser().getUid())
                            .child(FirebaseConstants.USERNAME).setValue(name);
                    mListener.onChange(name);
                    dismiss();
                }
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }
}
