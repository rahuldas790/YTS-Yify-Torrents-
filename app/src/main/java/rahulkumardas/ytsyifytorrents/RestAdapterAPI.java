package rahulkumardas.ytsyifytorrents;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Rahul Kumar Das on 01-05-2017.
 */

public interface RestAdapterAPI {
    public static final String BASE_END_POINT = "https://yts.ag/api/v2/";

    //get the movie list
    @GET("list_movies.json")
    Call<JsonObject> getList();

    //api to fetch search list based on string query
    @GET("list_movies.json")
    Call<JsonObject> getListWithPage(@Query("page") int no);
}
