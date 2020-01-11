package app.com.gitkill.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import app.com.gitkill.R;
import app.com.gitkill.models.repositories.TrendingRepositories;

public class TrendingRepositoriesAdapter extends RecyclerView.Adapter<TrendingRepositoriesAdapter.ViewHolder>{
    private ArrayList<TrendingRepositories> repositoriesArrayList;
    private String TAG = "TrendingRepositoriesAdapter";
    private Context context;
    private onItemClickListener onItemClickListener;

    public TrendingRepositoriesAdapter(ArrayList<TrendingRepositories> repositoriesArrayList, Context context, onItemClickListener onItemClickListener) {
        this.repositoriesArrayList = repositoriesArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_layout_trending_repositories,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final TrendingRepositories trendingRepo=repositoriesArrayList.get(position);
        Log.v(TAG,trendingRepo.getAuthor());
        viewHolder.RepoOwner.setText(trendingRepo.getAuthor());
        viewHolder.RepoName.setText(trendingRepo.getName());
        viewHolder.NumberOfStars.setText(""+trendingRepo.getStars());
        viewHolder.NumberOfForks.setText(""+trendingRepo.getForks());
        viewHolder.Language.setText(""+trendingRepo.getLanguage());
        viewHolder.RepoLink.setText(""+trendingRepo.getUrl());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.respond(trendingRepo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return repositoriesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView RepoOwner,RepoName,NumberOfStars,Language,NumberOfForks,RepoLink;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RepoOwner = itemView.findViewById(R.id.RepoOwner);
            RepoName = itemView.findViewById(R.id.RepoName);
            NumberOfStars = itemView.findViewById(R.id.NumberOfStars);
            NumberOfForks = itemView.findViewById(R.id.NumberOfForks);
            Language = itemView.findViewById(R.id.Language);
            RepoLink = itemView.findViewById(R.id.RepoLink);
            cardView = itemView.findViewById(R.id.item_card_view);
        }
    }

    public interface onItemClickListener {
        void respond(TrendingRepositories trendingRepositories);
    }
}