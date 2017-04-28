package tk.talcharnes.unborify.apitools;

import retrofit2.Call;
import retrofit2.http.GET;
import tk.talcharnes.unborify.apitools.models.ChuckNorrisAPIModel;
/**
 * Created by Tal on 4/16/2017.
 */


    /**
     * Created by Tal on 1/26/2017.
     */

    public interface ChuckNetworkService {

        @GET("jokes/random")
        Call<ChuckNorrisAPIModel> getChucked();

    }

