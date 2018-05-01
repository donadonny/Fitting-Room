package tk.talcharnes.unborify.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import tk.talcharnes.unborify.R;

/**
 * Created by coolm_000 on 2/23/2018.
 */

public class LocationMenuDialogFragment extends DialogFragment{
   private final int FARTHEST_DISTANCE=50;
   private final int INCREAMENT=1;
   private final String[] items = {"Km","Mi"};
   private String mUnitChoice;
   private int distanceChoice;

   public Dialog onCreateDialog (Bundle savedInstanceState) {
       View v = getActivity().getLayoutInflater().inflate(R.layout.locationmenudialog, null);
    SeekBar mSeekBar= v.findViewById(R.id.radius_bar);
    mSeekBar.setMax(FARTHEST_DISTANCE);
    mSeekBar.setKeyProgressIncrement(INCREAMENT);
    final CheckBox mCheckboxMi= v.findViewById(R.id.checkbox_Mi);
    final CheckBox mCheckboxKm= v.findViewById(R.id.checkbox_Km);

    mCheckboxKm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked){
                mCheckboxMi.setChecked(false);
                // add KM to sqllite db
            }
        }
    });

    mCheckboxMi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if (isChecked){
                mCheckboxKm.setChecked(false);
                // add MI to sqllite db
            }
        }
    });

    mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int radius, boolean isChanged) {
                if (isChanged){

                    Toast.makeText(getActivity(),"radius is "+radius,Toast.LENGTH_SHORT).show();
                    // save radius to sqllite db
                }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    });

     AlertDialog.Builder alert= new AlertDialog.Builder(getActivity());
               alert.setTitle(R.string.deals_option)
               .setMessage(R.string.distance)
               .setView(v)
               .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       // start the save to sqllite method then close dialog
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.cancel();
                   }
               });
 return alert.create();
   }
   // method to save to SqlLite DB
    public void saveToSqlLite(){

    }
}
