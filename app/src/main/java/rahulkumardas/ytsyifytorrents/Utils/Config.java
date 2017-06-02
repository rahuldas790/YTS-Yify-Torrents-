package rahulkumardas.ytsyifytorrents.Utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rahulkumardas.ytsyifytorrents.network.RestAdapterAPI;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rahul Kumar Das on 07-05-2017.
 */

public final class Config {
    private Config() {
    }

    public static final String YOUTUBE_API_KEY = "AIzaSyCB-7vlizSsSTYUby5Mv5L7XTYlkGCkV50";

    public static RestAdapterAPI getRestAdapter() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors â€¦

        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestAdapterAPI.BASE_END_POINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();


        return retrofit.create(RestAdapterAPI.class);
    }
}
