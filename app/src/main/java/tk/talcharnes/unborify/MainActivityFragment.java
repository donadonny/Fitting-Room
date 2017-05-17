package tk.talcharnes.unborify;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tk.talcharnes.unborify.apitools.ChuckNetworkService;
import tk.talcharnes.unborify.apitools.RandomJokeApiNetworkService;
import tk.talcharnes.unborify.apitools.UrlChooser;
import tk.talcharnes.unborify.apitools.models.ChuckNorrisAPIModel;
import tk.talcharnes.unborify.apitools.models.RandomJokeApiModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    TextView jokeTextView;
    FloatingActionButton fab;
    SwipeFlingAdapterView swipeFlingAdapterView;
    ArrayList<String> al;
    ArrayAdapter<String> arrayAdapter;
    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        jokeTextView = (TextView) rootView.findViewById(R.id.jokeTextView);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        // The following code is a test

        swipeFlingAdapterView = (SwipeFlingAdapterView) rootView.findViewById(R.id.frame);
        TextView textView = (TextView) rootView.findViewById(R.id.jokeTextView);
        textView.setVisibility(View.VISIBLE);
        // add entertaining things to arraylist using al.add()
        al = new ArrayList<String>();
        //choose your favorite adapter
        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.swipe_layout, R.id.helloText, al);
        al.add(getJoke(rootView));
        al.add(getJoke(rootView));
        //set the listener and the adapter
        swipeFlingAdapterView.setAdapter(arrayAdapter);
        swipeFlingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Log.d(LOG_TAG, "Left card Exit");
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Log.d(LOG_TAG, "Right card Exit");
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add(getJoke(rootView));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
            }

            @Override
            public void onScroll(float v) {
                View view = swipeFlingAdapterView.getSelectedView();
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(v < 0 ? -v : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(v > 0 ? v : 0);
            }
        });

        // Optionally add an OnItemClickListener
        swipeFlingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Log.d(LOG_TAG, "Item clicked");
            }
        });
//        Test over



        return rootView;
    }

    private String getJoke(View rootView) {
        final View rootView1 = rootView;
        final UrlChooser urlChooser = new UrlChooser();

        Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urlChooser.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        if (urlChooser.getRandomInt() == 0) {
            ChuckNetworkService chuckNetworkService;
            chuckNetworkService = retrofit.create(ChuckNetworkService.class);

            Call<ChuckNorrisAPIModel> chuckNorrisAPIModelCall;
            chuckNorrisAPIModelCall = chuckNetworkService.getChucked();
            chuckNorrisAPIModelCall.enqueue(new retrofit2.Callback<ChuckNorrisAPIModel>() {
                @Override
                public void onResponse(Call<ChuckNorrisAPIModel> call, Response<ChuckNorrisAPIModel> response) {
                    if (response == null) {
                        Log.i("retrofit failed", "failure");
                    } else if (response.toString().isEmpty()) {
                        Log.i(LOG_TAG, "Response is empty. Response = " + response);
                    }
                    ChuckNorrisAPIModel chuckNorrisAPIModel = response.body();
                    final Joke joke = new Joke();
                    joke.setJoke(chuckNorrisAPIModel.getValue());
                    Log.d(LOG_TAG, "joke = " + joke.getJoke());
                    jokeTextView.setText(joke.getJoke());

                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, joke.getJoke());
                            shareIntent.setType("text/plain");
                            startActivity(shareIntent);
                        }
                    });

                }

                @Override
                public void onFailure(Call<ChuckNorrisAPIModel> call, Throwable t) {
                    Log.e("failure", t.getMessage());
                    TextView emptyView = (TextView) rootView1.findViewById(R.id.empty_view);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        }
        if(urlChooser.getRandomInt() == 1){
            RandomJokeApiNetworkService randomJokeApiNetworkService;
            randomJokeApiNetworkService = retrofit.create(RandomJokeApiNetworkService.class);

            Call<RandomJokeApiModel> randomJokeApiNetworkServiceCall;
            randomJokeApiNetworkServiceCall = randomJokeApiNetworkService.getRandomJoke();
            randomJokeApiNetworkServiceCall.enqueue(new retrofit2.Callback<RandomJokeApiModel>() {
                @Override
                public void onResponse(Call<RandomJokeApiModel> call, Response<RandomJokeApiModel> response) {
                    if (response == null) {
                        Log.i("retrofit failed", "failure");
                    } else if (response.toString().isEmpty()) {
                        Log.i(LOG_TAG, "Response is empty. Response = " + response);
                    }
                    RandomJokeApiModel randomJokeApiModel = response.body();
                    final Joke joke = new Joke();
                    joke.setJoke(randomJokeApiModel.getJoke());
                    Log.d(LOG_TAG, "joke = " + joke.getJoke());
                    jokeTextView.setText(joke.getJoke());

                    fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.putExtra(Intent.EXTRA_TEXT, joke.getJoke());
                            shareIntent.setType("text/plain");
                            startActivity(shareIntent);
                        }
                    });

                }

                @Override
                public void onFailure(Call<RandomJokeApiModel> call, Throwable t) {
                    Log.e("failure", t.getMessage());
                    TextView emptyView = (TextView) rootView1.findViewById(R.id.empty_view);
                    emptyView.setVisibility(View.VISIBLE);
                }
            });
        }
        arrayAdapter.notifyDataSetChanged();
        return jokeTextView.getText().toString();
    }


}
