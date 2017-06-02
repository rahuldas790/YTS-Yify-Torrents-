package rahulkumardas.ytsyifytorrents.network;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Rahul Kumar Das on 01-05-2017.
 */

public interface RestAdapterAPI {
    public static final String BASE_END_POINT = "https://yts.ag/api/v2/";
    public static final String BASE_END_POINT2 = "https://yts.ag/ajax/";

    //get the movie list
    @GET("list_movies.json")
    Call<JsonObject> getList();

    //api to fetch get movie list based on page no
    @GET("list_movies.json")
    Call<JsonObject> getListWithPage(@Query("page") int no);

    //api tp get movie list based on serach query
    @GET("list_movies.json")
    Call<JsonObject> getListWithQuery(@Query("query_term") String string);

    //filter api
    @GET("list_movies.json")
    Call<JsonObject> getListWithFilter(@Query("page") int no, @Query("query_term") String string, @Query("quality") String quality, @Query("minimum_rating") String rating,
                                       @Query("genre") String genre, @Query("sort_by") String sort, @Query("order_by") String order);

    //api to fetch get movie list based on page no
    @GET("movie_suggestions.json")
    Call<JsonObject> getMovieSuggestions(@Query("movie_id") long no);

    // api to download torrent
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    //autoSerach
    @GET("search")
    Call<JsonObject> autoSeach(@Query("query") String query);

}
