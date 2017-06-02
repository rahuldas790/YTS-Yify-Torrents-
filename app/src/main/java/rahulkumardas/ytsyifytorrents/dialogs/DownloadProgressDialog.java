package rahulkumardas.ytsyifytorrents.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import rahulkumardas.ytsyifytorrents.R;
import rahulkumardas.ytsyifytorrents.Utils.CircleProgressBar;
import rahulkumardas.ytsyifytorrents.bitlet.Downloader;
import rahulkumardas.ytsyifytorrents.databases.DownloadDatabase;
import rahulkumardas.ytsyifytorrents.models.Torrent;
import rahulkumardas.ytsyifytorrents.network.RestAdapterAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rahul Kumar Das on 23-05-2017.
 */

public class DownloadProgressDialog extends DialogFragment {

    public TextView dialogMsg;
    public CircleProgressBar progressBar;
    private static String TAG = "DownloadAlert";

    public static Torrent torrent;
    private boolean isTorr, isMov, isImg;
    private String movieName;
    private DownloadDatabase db;

    public static DownloadProgressDialog newInstance(Torrent torrent, String name, String type) {
        DownloadProgressDialog f = new DownloadProgressDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("torr", torrent);
        args.putString("name", name);
        args.putString("type", type);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        torrent = getArguments().getParcelable("torr");
        movieName = getArguments().getString("name");
        String type = getArguments().getString("type");
        isTorr = false;
        isMov = false;
        isImg = false;
        if(type.equals("torrent")){
            isTorr = true;
        }else if(type.equals("image")) {
            isImg = true;
        }else{
            isMov = true;
        }

    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawableResource(R.color.semi_background);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        db = new DownloadDatabase(getActivity());
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_download, container);
        dialogMsg = (TextView) view.findViewById(R.id.message);
        progressBar = (CircleProgressBar) view.findViewById(R.id.progress);
        getDialog().setTitle("Download");


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


        RestAdapterAPI adapterAPI = retrofit.create(RestAdapterAPI.class);
        Call<ResponseBody> call;

        call = adapterAPI.downloadFileWithDynamicUrlSync(torrent.torUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    dialogMsg.setText("Connected!");
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                    if (writtenToDisk) {
                        Toast.makeText(getActivity(), "File downloaded", Toast.LENGTH_SHORT).show();
                    }
                    dismiss();
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });

        return view;
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            dismiss();
            File directory = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Torrents"
                    + File.separator, "");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String totalFile = Environment.getExternalStorageDirectory()
                    + File.separator + "Torrents"
                    + File.separator + movieName + " " + torrent.quality + ".torrent";

            File futureStudioIconFile = null;

            if (isImg) {
                totalFile = Environment.getExternalStorageDirectory()
                        + File.separator + "Torrents"
                        + File.separator + movieName + ".jpg";
                futureStudioIconFile = new File(directory, movieName + ".jpg");
            } else {
                totalFile = Environment.getExternalStorageDirectory()
                        + File.separator + "Torrents"
                        + File.separator + movieName + " " + torrent.quality + ".torrent";
                futureStudioIconFile = new File(directory, movieName + " " + torrent.quality + ".torrent");
            }

            if (!futureStudioIconFile.exists()) {
                try {
                    futureStudioIconFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                dialogMsg.setText("Downloading...");
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setMax(100);
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    progressBar.setProgress((int) (fileSizeDownloaded / fileSize * 100));
                }

                outputStream.flush();
                if(isTorr){
                    db.insertNew(totalFile, "torrent");
                }else if(isImg){
                    db.insertNew(totalFile, "image");
                }
                else {
                    db.insertNew(totalFile, "torrent");
                    startDownload(totalFile, directory);
                }
                return true;
            } catch (IOException e) {
                Log.i(TAG, "Error is " + e.getMessage());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.i(TAG, "Error is " + e.getMessage());
            return false;
        }
    }

    private void startDownload(String loc, File directory) {
            Downloader torr = new Downloader();
            torr.startDownload(loc, directory);
    }

}
