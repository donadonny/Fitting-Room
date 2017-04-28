package tk.talcharnes.unborify.apitools;

import retrofit2.Call;
import retrofit2.http.GET;
import tk.talcharnes.unborify.apitools.models.RandomJokeApiModel;

/**
 * Created by Tal on 4/26/2017.
 */

public interface RandomJokeApiNetworkService {

    @GET("joke/random")
    Call<RandomJokeApiModel> getRandomJoke();

}
