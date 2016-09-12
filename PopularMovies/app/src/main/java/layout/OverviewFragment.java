package layout;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.yuriy.popularmovies.FetchPopularMoviesTask;
import com.udacity.yuriy.popularmovies.R;
import com.udacity.yuriy.popularmovies.SessionConfigurations;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import data.MovieImageContract;
import data.MovieImageDbHelper;
import entities.Movie;

public class OverviewFragment extends Fragment {

    private ImageAdapter images;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View retView = inflater.inflate(R.layout.fragment_overview, container, false);

        images = new ImageAdapter(getActivity());

        GridView gridview = (GridView) retView.findViewById(R.id.gridView);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridview.setColumnWidth(metrics.widthPixels / 3);
        } else {
            gridview.setColumnWidth(metrics.widthPixels / 2);
        }
        gridview.setAdapter(images);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "Yoyo " + position, Toast.LENGTH_SHORT).show();
            }
        });



        return retView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public static class ImageAdapter extends BaseAdapter {
        private Context mContext;
        //List<Movie> movies = new ArrayList<>();
        int page = 0;
        public int count = 0;
        public boolean wentToFetch = false;

        SQLiteOpenHelper sqlHelper;

        // public static final String baseUrl = "http://api.themoviedb.org/3/";

        public ImageAdapter(Context c) {
            mContext = c;
            sqlHelper = new MovieImageDbHelper(mContext);
        }

        public void addToMovies(List<Movie> list) {
            //movies.addAll(list);
            SQLiteDatabase db = sqlHelper.getWritableDatabase();
            for (Movie movie : list) {
                ContentValues value = new ContentValues();
                value.put(MovieImageContract.POSITION, count++);
                //value.put(MovieImageContract._ID, Long.parseLong(movie.getId()));
                value.put(MovieImageContract.POSTER_PATH, movie.getPosterPath());
                db.insert(MovieImageContract.TABLE_NAME, null, value);
            }
            db.close();
            notifyDataSetChanged();
        }

        public int getCount() {
            return count + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

        public void refresh() {
            notifyDataSetChanged();
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {

            SquareImageView view = (SquareImageView) convertView;
            if (view == null) {
                view = new SquareImageView(mContext);
            }

                if (position >= count && !wentToFetch) {
                    wentToFetch = true;
                    FetchPopularMoviesTask fetchMovies = new FetchPopularMoviesTask(this, mContext);
                    page++;
                    fetchMovies.execute(Integer.toString(page));
                }

                if (count == 0 || position == count) {
                    view.setImageResource(R.drawable.downloading);
                } else {

                    SQLiteDatabase db = sqlHelper.getReadableDatabase();
                    Cursor c = db.rawQuery("select * from " + MovieImageContract.TABLE_NAME
                            + " where " + MovieImageContract.POSITION + " == " + position, null);

                    if (!c.moveToFirst()) {
                        view.setImageResource(R.drawable.downloading);
                    } else {
                        String posterStr = c.getString(c.getColumnIndexOrThrow(MovieImageContract.POSTER_PATH));
                        String path = SessionConfigurations.baseImageUrl + SessionConfigurations.logoSize + posterStr;
                        Picasso.with(mContext).load(path).placeholder(R.drawable.downloading).resize(100, 100).into(view);
                    }
                    c.close();
                    db.close();
                }

            view.setScaleType(SquareImageView.ScaleType.FIT_XY);

            return view;
        }

    }
}
