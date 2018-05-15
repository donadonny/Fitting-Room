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
import android.widget.TextView;

import tk.talcharnes.unborify.Models.DealsOptionsModel;
import tk.talcharnes.unborify.R;
import tk.talcharnes.unborify.Services.SQLiteDatabaseHandlerDeals;

/**
 * Created by coolm_000 on 2/23/2018.
 */

public class LocationMenuDialogFragment extends DialogFragment{
    // Variables
    private final int FARTHEST_DISTANCE=50;
    private final int INCREAMENT=1;
    private final String[] items = {"Km","Mi"};
    private String mUnitChoice;
    private int distanceChoice;



    /**
     * onCreateDialog that will store the deals options
     * @param savedInstanceState
     * @return
     */
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_location, null);
        final SQLiteDatabaseHandlerDeals DB = new SQLiteDatabaseHandlerDeals(this.getActivity());
        final DealsOptionsModel DEALS=DB.getDealsOptionsModel();
        final TextView RADIUS_TEXT= v.findViewById(R.id.radius_number);
        SeekBar mSeekBar= v.findViewById(R.id.radius_bar);
        mSeekBar.setMax(FARTHEST_DISTANCE);
        mSeekBar.setKeyProgressIncrement(INCREAMENT);
        final CheckBox CHECKBOX_MI= v.findViewById(R.id.checkbox_Mi);
        final CheckBox CHECKBOX_KM= v.findViewById(R.id.checkbox_Km);

        if (DEALS!=null){
            distanceChoice=DEALS.getRadius();
            mUnitChoice=DEALS.getMetric();
            mSeekBar.setProgress(distanceChoice);
            RADIUS_TEXT.setText(String.valueOf(distanceChoice));
            if (mUnitChoice.equalsIgnoreCase("Km")){
                CHECKBOX_KM.setChecked(true);
            }else{
                CHECKBOX_MI.setChecked(true);
            }
        }

        // check box for Km
        CHECKBOX_KM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    CHECKBOX_MI.setChecked(false);
                    mUnitChoice="km";
                }
            }
        });

        // check box for Mi
        CHECKBOX_MI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    CHECKBOX_KM.setChecked(false);
                    mUnitChoice="mile";
                }
            }
        });

        // seekBar for distance
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int radius, boolean isChanged) {
                if (isChanged){
                    distanceChoice=radius;
                    RADIUS_TEXT.setText(String.valueOf(distanceChoice));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * Alert dialog that add the checkboxes and seekbar
         */
        AlertDialog.Builder alert= new AlertDialog.Builder(getActivity());
        alert.setTitle(R.string.deals_option)
                .setView(v)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // start the save to sqllite method then close dialog
                        if (DEALS==null){
                            DealsOptionsModel newOption= new DealsOptionsModel(distanceChoice,mUnitChoice);
                            DB.addDealOptions(newOption);
                        }else{
                            DEALS.setRadius(distanceChoice);
                            DEALS.setMetric(mUnitChoice);
                            DB.updateRecord(DEALS);
                        }
                    }
                })
                // cancel button
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        return alert.create();
    }
}