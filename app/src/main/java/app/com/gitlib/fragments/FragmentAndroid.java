package app.com.gitlib.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.ArrayList;
import app.com.gitlib.R;
import app.com.gitlib.activities.s.details.DetailsActivity;
import app.com.gitlib.adapters.AllTopicAdapter;
import app.com.gitlib.apiutils.AllApiService;
import app.com.gitlib.apiutils.AllUrlClass;
import app.com.gitlib.models.alltopic.TopicBase;
import app.com.gitlib.models.alltopic.Item;
import app.com.gitlib.utils.UX;
import app.com.gitlib.utils.UtilsManager;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentAndroid extends Fragment {
    private static final FragmentAndroid FRAGMENT_ANDROID = null;
    private RecyclerView androidTopicRecyclerView;
    private UX ux;
    private UtilsManager utilsManager;
    private ArrayList<Item> androidTopicList;
    private AllUrlClass allUrlClass;
    private AllApiService apiService;
    private CircleImageView refreshListButton;
    private Spinner androidFilterSpinner;
    private TextView NoData;
    private ImageView NoDataIV;
    private String[] androidFilterList = new String[]{"Select Query","Layouts","Drawing",
            "Navigation","Scanning","RecyclerView","ListView","Image Processing","Binding","Debugging"};
    private AdView adView;

    public FragmentAndroid() {
        // Required empty public constructor
    }

    public static FragmentAndroid getInstance() {
        if (FRAGMENT_ANDROID == null) return new FragmentAndroid();
        else return FRAGMENT_ANDROID;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_android, container, false);
        init(view);
        bindUIWithComponents(view);
        return view;
    }

    private void init(View view) {
        androidTopicRecyclerView = view.findViewById(R.id.mRecyclerView);
        refreshListButton = view.findViewById(R.id.RefreshList);
        androidFilterSpinner = view.findViewById(R.id.FilterSpinner);
        NoData = view.findViewById(R.id.NoDataMessage);
        NoDataIV = view.findViewById(R.id.NoDataIV);
        adView = view.findViewById(R.id.adView);
        androidTopicList = new ArrayList<>();
        allUrlClass = new AllUrlClass();
        ux = new UX(getContext());
        utilsManager = new UtilsManager(getContext());
    }

    private void bindUIWithComponents(View view) {

        ux.setSpinnerAdapter(androidFilterSpinner,androidFilterList);

        new BackgroundDataLoad(allUrlClass.ALL_TOPICS_BASE_URL , "android").execute();

        refreshListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroundDataLoad(allUrlClass.ALL_TOPICS_BASE_URL , "android").execute();
            }
        });

        androidFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String queryString = adapterView.getItemAtPosition(position).toString();
                new BackgroundDataLoad(allUrlClass.ALL_TOPICS_BASE_URL, "android"+queryString ).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //region adMob
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.v("onInitComplete","InitializationComplete");
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.v("onAdListener","AdlLoaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                Log.v("onAdListener","AdFailedToLoad");
                Log.v("onAdListener","AdFailedToLoad Error "+adError.getMessage());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Log.v("onAdListener","AdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.v("onAdListener","AdClicked");
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.v("onAdListener","AdLeftApplication");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                Log.v("onAdListener","AdClosed");
            }
        });
        //endregion
    }

    private void loadListView(){
        ux.loadListView(androidTopicList, androidTopicRecyclerView, R.layout.adapter_layout_android_topics).setOnItemClickListener(new AllTopicAdapter.onItemClickListener() {
            @Override
            public void respond(Item androidItem) {
                Intent intent = new Intent(getContext() , DetailsActivity.class);
                intent.putExtra("item", androidItem);
                getContext().startActivity(intent);
            }
        });
    }

    private class BackgroundDataLoad extends AsyncTask<String, Void, String> {

        String url , querySting;

        public BackgroundDataLoad( String url, String querySting) {
            this.url = url;
            this.querySting = querySting;
        }

        @Override
        protected void onPreExecute() {
            ux.getLoadingView();
        }

        @Override
        protected String doInBackground(String... strings) {
            loadRecord(url , querySting);
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("done")){
                Log.v("result async task :: ",result);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (androidTopicList.size()>0){
                            loadListView();
                            NoData.setVisibility(View.GONE);
                            NoDataIV.setVisibility(View.GONE);
                        }
                        else {
                            NoData.setVisibility(View.VISIBLE);
                            NoDataIV.setVisibility(View.VISIBLE);
                            Toasty.error(getContext(),R.string.no_data_message).show();
                        }
                        ux.removeLoadingView();
                    }
                }, 6000);
            }
        }

    }

    private void loadRecord(String url , String queryString) {
        androidTopicList.clear();
        //Creating the instance for api service from AllApiService interface
        apiService=utilsManager.getClient(url).create(AllApiService.class);
        final Call<TopicBase> androidTopicCall=apiService.getAllTopics(url+"repositories",queryString);
        //handling user requests and their interactions with the application.
        androidTopicCall.enqueue(new Callback<TopicBase>() {
            @Override
            public void onResponse(Call<TopicBase> call, Response<TopicBase> response) {
                try{
                    for (int start=0;start<response.body().getItems().size();start++) {
                        Item item=response.body().getItems().get(start);
                        androidTopicList.add(new Item(item.getFullName(),item.getAvatar_url(),item.getHtmlUrl(),item.getLanguage(),item.getStargazersCount(),item.getWatchersCount(),
                                item.getForksCount(),item.getForks(),item.getWatchers()));
                    }
                }
                catch (Exception e){
                    Log.v("EXCEPTION : ",""+e.getMessage());
                }
            }
            @Override
            public void onFailure(Call<TopicBase> call, Throwable t) {
                Log.v("Android fragment",""+t.getMessage());
            }
        });

    }

}
