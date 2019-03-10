package com.example.moviewatchlist3;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<MovieCard> movies;

    public MovieAdapter(List<MovieCard> movies) {
        this.movies = movies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = inflater.inflate(R.layout.movie, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MovieCard movie = movies.get(i);

        // Set item views based on your views and data model
        TextView textView = viewHolder.movieTextView;
        textView.setText(movie.getName());
        Button button = viewHolder.messageButton;
        button.setText("+1");
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView movieTextView;
        public Button messageButton;

        public ViewHolder(View view) {
            super(view);
            movieTextView = (TextView) view.findViewById(R.id.movie_name);
            messageButton = (Button) view.findViewById(R.id.plus1_button);
        }
    }

    public void updateDataSet(List<MovieCard> l){
        movies = l;
        notifyDataSetChanged();
    }
}
