package com.sagarnileshshah.nytsearchapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sagarnileshshah.nytsearchapp.R;
import com.sagarnileshshah.nytsearchapp.adapters.ArticleArrayAdapter;
import com.sagarnileshshah.nytsearchapp.fragments.FilterDialogFragment;
import com.sagarnileshshah.nytsearchapp.listeners.EndlessScrollListener;
import com.sagarnileshshah.nytsearchapp.models.Article;
import com.sagarnileshshah.nytsearchapp.models.Filter;
import com.sagarnileshshah.nytsearchapp.models.gson.JSONTopLevel;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.OnFilterDialogFragmentInteractionListener {

    static final String ARTICLE_SEARCH_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    static final String API_KEY = "e9d5bb253d22d14fcc8a2c53f0e3956f:0:74368102";

    @Bind(R.id.gvArticles)
    GridView gvArticles;

    MenuItem actionSettings;
    MenuItem actionFilter;

    ArrayList<Article> mArticleList;
    ArticleArrayAdapter mArticleArrayAdapter;
    Filter mFilter;
    String mQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        mArticleList = new ArrayList<>();
        mArticleArrayAdapter = new ArticleArrayAdapter(this, mArticleList);
        gvArticles.setAdapter(mArticleArrayAdapter);

        gvArticles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Article article = mArticleList.get(position);
                Intent i = new Intent(SearchActivity.this, ArticleActivity.class);
                i.putExtra("article", Parcels.wrap(article));
                startActivity(i);
            }
        });

        gvArticles.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                doArticleSearch(page);

                return true;
            }
        });

        mFilter = new Filter();
        mQuery = "";

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        actionFilter = menu.findItem(R.id.action_filter);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenuItems(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                showMenuItems(true);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mQuery = query;
                doArticleSearch(0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            showFilterDialogFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    public void doArticleSearch(int page) {
        if (page == 0) {
            mArticleList.clear();
        }

        if (!isNetworkAvailable() || !isOnline()) {
            if (!isNetworkAvailable())
                Toast.makeText(this, "No network connection. Please check network settings and activate either Wifi or Data.", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this, "Current network not connected to the internet. Please try again after some time or contact network operator.", Toast.LENGTH_LONG).show();
            return;
        }

        RequestParams requestParams = new RequestParams();
        requestParams.put("api-key", API_KEY);
        requestParams.put("page", page);
        requestParams.put("q", mQuery);

        if (!mFilter.getStartDate().equals("")) {
            requestParams.put("begin_date", mFilter.getStartDate());
        }

        if (mFilter.getNewsDesk().size() > 0) {
            String param = "news_desk:(";
            for (String choice : mFilter.getNewsDesk()) {
                param += "\"" + choice + "\"" + " ";
            }
            param = param.trim();
            param += ")";
            requestParams.put("fq", param);
        }

        if (!mFilter.getSortBy().equals("")) {
            requestParams.put("sort", mFilter.getSortBy().toLowerCase());
        }

        final String contentErrorMsg = "Sorry. Unable to get content from NYT. Please try again after some time.";
        final String noContentMsg = "No content available for given search query and chosen filters";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

        asyncHttpClient.get(ARTICLE_SEARCH_URL, requestParams, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String json) {
                if (statusCode == 200) {
                    JSONTopLevel jsonTopLevel = JSONTopLevel.parseJson(json);
                    if (jsonTopLevel.getResponse().getDocs().size() > 0) {
                        mArticleList.addAll(Article.fromGson(jsonTopLevel));
                        mArticleArrayAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SearchActivity.this, noContentMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, contentErrorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(this.toString(), responseString);
                Toast.makeText(SearchActivity.this, contentErrorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showMenuItems(boolean toShow) {
        actionFilter.setVisible(toShow);
    }

    private void showFilterDialogFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance(mFilter);
        filterDialogFragment.show(fm, "filterDialogFragment");
    }

    @Override
    public void applyFilter(Filter filter) {
        mFilter = filter;
        if (!mQuery.equals(""))
            doArticleSearch(0);
    }
}
