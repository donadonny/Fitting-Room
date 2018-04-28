package tk.talcharnes.unborify.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
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

import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Utilities.FirebaseConstants;

/**
 * Created by khuramchaudhry on 8/8/16.
 */
public class changePasswordDialogFragment extends DialogFragment {

    static final String TAG = changePasswordDialogFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_change_password, null))
                // Add action buttons
                .setPositiveButton("CHANGE PASSWORD", new DialogInterface.OnClickListener() {
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
                final EditText newPassword = (EditText) dialog.findViewById(R.id.input_new_password);
                final EditText confirmnPassword = (EditText) dialog.findViewById(R.id.confirm_new_password);
                final String new_password = newPassword.getText().toString();
                final String confirm_password = confirmnPassword.getText().toString();

                if (new_password.isEmpty() || new_password.length() < 8 || new_password.length() > 25) {
                    newPassword.setError("between 8 and 25 alphanumeric characters");
                    wantToCloseDialog = false;
                }
                if (confirm_password.isEmpty() || confirm_password.length() < 8 || confirm_password.length() > 25) {
                    confirmnPassword.setError("between 8 and 25 alphanumeric characters");
                    wantToCloseDialog = false;
                }
                if (!confirm_password.equals(new_password)) {
                    confirmnPassword.setError("does not match");
                    wantToCloseDialog = false;
                }
                if (wantToCloseDialog) {
                    FirebaseUser user = FirebaseConstants.getUser();
                    user.updatePassword(new_password)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "UserModel password updated.");
                                    }
                                }
                            });
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
