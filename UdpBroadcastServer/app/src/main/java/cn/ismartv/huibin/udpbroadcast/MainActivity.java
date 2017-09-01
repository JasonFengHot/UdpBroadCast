package cn.ismartv.huibin.udpbroadcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // Used to load the 'native-lib' library on application startup.
    private EditText searchInput;
    private EditText itemPkInput;
    private EditText subItemPkInput;
    private static String baseUrl;

    private static HttpService httpService;

    private RecyclerView mRecyclerView;
    private List<TvEntity> mDatas;

    private static Hashtable<String, String> tvList;
    private HomeAdapter mAdapter;
    OkHttpClient mClient;

    private static MessageHandler messageHandler;
    private TextView currentBind;

    private static boolean alive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchInput = (EditText) findViewById(R.id.search_input);
        itemPkInput = (EditText) findViewById(R.id.item_pk_input);
        subItemPkInput = (EditText) findViewById(R.id.subitem_pk);
        currentBind = (TextView) findViewById(R.id.current_bind);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        tvList = new Hashtable<>();

        mAdapter = new HomeAdapter();
        mDatas = new ArrayList<>();
        mRecyclerView.setAdapter(mAdapter);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        messageHandler = new MessageHandler();


    }

    @Override
    protected void onResume() {
        super.onResume();
        alive = true;
    }

    @Override
    protected void onPause() {
        alive = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        messageHandler.removeMessages(0);
        super.onDestroy();
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            initData();
        }
    }

    public void search(View view) {
        if (!TextUtils.isEmpty(baseUrl)) {
            httpService.apiSearch(searchInput.getText().toString())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        } else {
            Toast.makeText(this, "当前没有绑定设备", Toast.LENGTH_LONG).show();
        }
    }

    public void play(View view) {
        if (!TextUtils.isEmpty(baseUrl)) {
            httpService.apiPlayer(itemPkInput.getText().toString(), subItemPkInput.getText().toString())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
        } else {
            Toast.makeText(this, "当前没有绑定设备", Toast.LENGTH_LONG).show();
        }
    }

    public static void receiveBroadcastCallback(String message) {
        Log.d(TAG, "receiveBroadcastCallback: " + message);
        String url = message.replace("ismartv", "http");
        if (alive) {
            tvList.put(url, "");
            messageHandler.sendEmptyMessage(0);
        }
    }


    interface HttpService {
        @GET("search")
        Call<ResponseBody> apiSearch(
                @Query("keyword")
                        String keyword
        );

        @GET("player")
        Call<ResponseBody> apiPlayer(
                @Query("itemid")
                        String itemId,
                @Query("subitemid")
                        String subItemId);

    }

    protected void initData() {
        mDatas = new ArrayList<>();
        Iterator iter = tvList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) iter.next();
            String ip = entry.getKey();
            String modelName = entry.getValue();
            TvEntity tvEntity = new TvEntity();
            tvEntity.setTvIpAddress(ip);
            tvEntity.setModelName(modelName);
            mDatas.add(tvEntity);
        }

        mAdapter.notifyDataSetChanged();

    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> implements View.OnClickListener {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_layout, parent,
                    false));
            return holder;
        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.modelName.setText(mDatas.get(position).getModelName());
            holder.tvIpAddress.setText(mDatas.get(position).getTvIpAddress());
            holder.bindBtn.setTag(mDatas.get(position));
            holder.bindBtn.setOnClickListener(this);
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        @Override
        public void onClick(View view) {
            TvEntity tvEntity = (TvEntity) view.getTag();
            baseUrl = tvEntity.getTvIpAddress();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(mClient)
                    .build();

            httpService = retrofit.create(HttpService.class);

            currentBind.setText("当前绑定： " + baseUrl);

        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView modelName;
            TextView tvIpAddress;
            Button bindBtn;

            public MyViewHolder(View view) {
                super(view);
                modelName = view.findViewById(R.id.model_name);
                tvIpAddress = view.findViewById(R.id.tv_ip);
                bindBtn = view.findViewById(R.id.bind);
            }
        }
    }

    class TvEntity {
        private String modelName;
        private String tvIpAddress;

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getTvIpAddress() {
            return tvIpAddress;
        }

        public void setTvIpAddress(String tvIpAddress) {
            this.tvIpAddress = tvIpAddress;
        }
    }

}
