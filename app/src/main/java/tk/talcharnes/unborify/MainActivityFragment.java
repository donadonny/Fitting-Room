package tk.talcharnes.unborify;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.talcharnes.unborify.apitools.NetworkService;
import tk.talcharnes.unborify.apitools.UrlChooser;
import tk.talcharnes.unborify.apitools.models.ChuckNorrisAPIModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        getJoke(rootView);
        ImageButton forwardButton = (ImageButton) rootView.findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getJoke(rootView);

            }
        });

        return rootView;
    }
    private void getJoke(View rootView){
        final View rootView1 = rootView;
        final UrlChooser urlChooser = new UrlChooser();

        Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlChooser.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NetworkService networkService;
        networkService =  retrofit.create(NetworkService.class);

        Call<ChuckNorrisAPIModel> chuckNorrisAPIModelCall;
        chuckNorrisAPIModelCall = networkService.getChucked();
        chuckNorrisAPIModelCall.enqueue(new retrofit2.Callback<ChuckNorrisAPIModel>() {
            @Override
            public void onResponse(Call<ChuckNorrisAPIModel> call, Response<ChuckNorrisAPIModel> response) {
                if(response != null){
                    Log.i("retrofit failed", "failure");
                }
                ChuckNorrisAPIModel chuckNorrisAPIModel = response.body();
                Joke joke = new Joke();
                joke.setJoke(chuckNorrisAPIModel.getValue());
                TextView jokeTextView = (TextView) rootView1.findViewById(R.id.jokeTextView);
                jokeTextView.setText(joke.getJoke());
            }

            @Override
            public void onFailure(Call<ChuckNorrisAPIModel> call, Throwable t) {
                Log.e("failure", t.getMessage());
                TextView emptyView = (TextView) rootView1.findViewById(R.id.empty_view);
                emptyView.setVisibility(View.VISIBLE);
            }
        });

    }
}
