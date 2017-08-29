package tk.talcharnes.unborify.Old_Files;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import tk.talcharnes.unborify.MyTransformation;
import tk.talcharnes.unborify.Photo;
import tk.talcharnes.unborify.R;

/**
 * Created by Tal on 7/2/2017.
 */

public class SwipeViewAdapter extends ArrayAdapter<Photo> {
    private static final String LOG_TAG = SwipeViewAdapter.class.getSimpleName();

    public SwipeViewAdapter(Context context, List<Photo> photoFile) {
        super(context, 0, photoFile);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Photo photo = getItem(position);
        long amount_likes = photo.getLikes();
        long amount_dislikes = photo.getDislikes();
        String occastion_subtitle_string = photo.getOccasion_subtitle();
        String urlString = photo.getUrl();

        Log.d(LOG_TAG, "Photo info: Subtitle String = " + occastion_subtitle_string
        + "Likes = " + amount_likes + "Dislikes = " + amount_dislikes + "url = " + urlString);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_layout, parent, false);
        }
        int rotation = 0;
        if(getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            if( photo.getOrientation() != 0){
                rotation = 90;
//                imageView.setRotation(90);
            }
        }
        else if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            if(photo.getOrientation() == 0){
//                imageView.setRotation(90);
                rotation = 90;
            }
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.userFashionStylePhoto);
        if(urlString != null && !urlString.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(urlString);
            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(storageRef).transform(new MyTransformation(getContext(), rotation))
                    .into(imageView);
        }


        // Lookup view for data population
        TextView occastion_subtitle = (TextView) convertView.findViewById(R.id.occasion_subtitle);
        // Populate the data into the template view using the data object
        occastion_subtitle.setText(occastion_subtitle_string);
        occastion_subtitle.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

//        final Button reportButton = (Button) convertView.findViewById(R.id.reportButton);
//        reportButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                photo.setReports(photo.getReports() + 1);
//                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//                final DatabaseReference photoReference = firebaseDatabase.getReference().child("Photos");
//                photoReference.child(photo.getUrl()).setValue(photo);
//                reportButton.setVisibility(View.GONE);
//            }
//        });

        // Return the completed view to render on screen

        return convertView;
    }

}

