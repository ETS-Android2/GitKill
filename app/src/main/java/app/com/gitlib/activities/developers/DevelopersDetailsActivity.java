package app.com.gitlib.activities.developers;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import app.com.gitlib.R;
import app.com.gitlib.models.users.Developer;
import app.com.gitlib.utils.UX;
import app.com.gitlib.viewmodels.SingleDeveloperViewModel;
import static app.com.gitlib.utils.UtilsManager.hasConnection;
import static app.com.gitlib.utils.UtilsManager.internetErrorDialog;

public class DevelopersDetailsActivity extends AppCompatActivity {
    private AdView adView;
    private String userName = "";
    private SingleDeveloperViewModel viewModel;
    private UX ux;
    private TextView userNameTxt, name, followers, following, publicRepos, gists, bio, blog, company, location, hireable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developers_details);

        //region get intent data
        getIntentData();
        //endregion

        //region init and bind UI components
        init();
        bindUIWithComponents();
        //endregion
    }

    //region get intent data
    private void getIntentData(){
        if (getIntent().getStringExtra("user") != null){
            userName = getIntent().getStringExtra("user");
        }
    }
    //endregion

    //region init UI components
    private void init() {
        adView = findViewById(R.id.adView);
        userNameTxt = findViewById(R.id.userNameTxt);
        name = findViewById(R.id.name);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        publicRepos = findViewById(R.id.publicRepos);
        gists = findViewById(R.id.gists);
        bio = findViewById(R.id.bio);
        blog = findViewById(R.id.blog);
        company = findViewById(R.id.company);
        location = findViewById(R.id.location);
        hireable = findViewById(R.id.hireable);
        viewModel = ViewModelProviders.of(this).get(SingleDeveloperViewModel.class);
        ux = new UX(this);
    }
    //endregion

    //region bind UI components
    private void bindUIWithComponents() {
        //region toolbar on back click listener and set toolbar title
        findViewById(R.id.BackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DevelopersDetailsActivity.this, TrendingDevelopersListActivity.class));
            }
        });
        //endregion

        performServerOperation(userName);

        //region adMob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
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
    //endregion

    //region perform mvvm server fetch
    private void performServerOperation(String url){
        if (hasConnection(DevelopersDetailsActivity.this)) {
            ux.getLoadingView();
            viewModel.getData(this,url);
            viewModel.getDeveloper().observe(this, new Observer<Developer>() {
                @Override
                public void onChanged(Developer developer) {
                    if (developer != null) {
                        setData(developer);
                    } else {
                        Toast.makeText(DevelopersDetailsActivity.this,R.string.no_data_message,Toast.LENGTH_SHORT).show();
                    }
                    ux.removeLoadingView();
                }
            });
        }
        else{
            internetErrorDialog(DevelopersDetailsActivity.this);
        }
    }
    //endregion

    //region set UI data
    private void setData(Developer developer){
        userNameTxt.setText("@"+developer.getLogin());
        name.setText(developer.getName());

        if (developer.getFollowers() != 0) {
            followers.setText(String.valueOf(developer.getFollowers()));
        } else {
            followers.setText("0");
        }

        if (developer.getFollowing() != 0) {
            following.setText(String.valueOf(developer.getFollowing()));
        } else {
            following.setText("0");
        }

        if (developer.getPublicRepos() != 0) {
            publicRepos.setText(String.valueOf(developer.getPublicRepos()));
        } else {
            publicRepos.setText("0");
        }

        if (developer.getPublicGists() != 0) {
            gists.setText(String.valueOf(developer.getPublicGists()));
        } else {
            gists.setText("0");
        }

        if (developer.getBio() != null) {
            bio.setText(developer.getBio().toString());
        } else {
            bio.setText("No bio found");
        }

        if (!TextUtils.isEmpty(developer.getBlog())) {
            blog.setText(developer.getBlog());
        } else {
            blog.setText("No blog address found");
        }

        if (developer.getCompany() != null) {
            company.setText(developer.getCompany().toString());
        } else {
            company.setText("No company information found");
        }

        if (!TextUtils.isEmpty(developer.getLocation())) {
            location.setText(developer.getLocation());
        } else {
            location .setText("No location information found");
        }

        if (developer.getHireable() != null){
            if (developer.getHireable().toString().equals("true")){
                hireable.setText("Hireable");
            } else{
                hireable.setText("Not Hireable");
            }
        }else {
            hireable.setText("No hireable information found");
        }
    }
    //endregion

    //region activity components
    @Override
    public void onBackPressed() {
        startActivity(new Intent(DevelopersDetailsActivity.this, TrendingDevelopersListActivity.class));
    }
    //endregion
}