package com.sagarnileshshah.nytsearchapp.activities;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sagarnileshshah.nytsearchapp.R;
import com.sagarnileshshah.nytsearchapp.adapters.ArticleRecyclerViewAdapter;
import com.sagarnileshshah.nytsearchapp.decorations.ArticleItemDecoration;
import com.sagarnileshshah.nytsearchapp.fragments.FilterDialogFragment;
import com.sagarnileshshah.nytsearchapp.listeners.EndlessRecyclerViewScrollListener;
import com.sagarnileshshah.nytsearchapp.models.Article;
import com.sagarnileshshah.nytsearchapp.models.Filter;
import com.sagarnileshshah.nytsearchapp.models.gson.JSONTopLevel;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.OnFilterDialogFragmentInteractionListener {

    static final String ARTICLE_SEARCH_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    static final String API_KEY = "e9d5bb253d22d14fcc8a2c53f0e3956f:0:74368102";

    @Bind(R.id.tvQuery)
    TextView tvQuery;

    @Bind(R.id.tvStartDate)
    TextView tvStartDate;

    @Bind(R.id.tvNewsDesk)
    TextView tvNewsDesk;

    @Bind(R.id.tvSortBy)
    TextView tvSortBy;

    @Bind(R.id.rvArticles)
    RecyclerView rvArticles;

    @Bind(R.id.pbProgressBar)
    ProgressBar pbProgressBar;

    @Bind(R.id.ivPlaceholderArticle)
    ImageView ivPlaceholderArticle;

    MenuItem actionFilter;

    ArrayList<Article> mArticleList;
    ArticleRecyclerViewAdapter mArticleRecyclerViewAdapter;
    Filter mFilter;
    String mQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        tvQuery.setVisibility(View.GONE);
        tvNewsDesk.setVisibility(View.GONE);
        tvStartDate.setVisibility(View.GONE);
        tvSortBy.setVisibility(View.GONE);

        mArticleList = new ArrayList<>();

        mArticleRecyclerViewAdapter = new ArticleRecyclerViewAdapter(SearchActivity.this, mArticleList);

        rvArticles.setAdapter(mArticleRecyclerViewAdapter);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvArticles.setLayoutManager(staggeredGridLayoutManager);

        ArticleItemDecoration articleItemDecoration = new ArticleItemDecoration(16);
        rvArticles.addItemDecoration(articleItemDecoration);

        //rvArticles.setItemAnimator(new SlideInUpAnimator());

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                doArticleSearch(page);

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

        searchView.setMaxWidth(Integer.MAX_VALUE);

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

    public void doArticleSearch(final int page) {
        ivPlaceholderArticle.setVisibility(View.GONE);
        if (page == 0) {
            mArticleRecyclerViewAdapter.notifyItemRangeRemoved(0, mArticleList.size());
            mArticleList.clear();
            pbProgressBar.setVisibility(View.VISIBLE);
            renderFilterDesc(tvQuery, mQuery, true);
        }

        if (!isNetworkAvailable() || !isOnline()) {
            if (!isNetworkAvailable()) {
                renderSnackBar("No network connection. Please check network settings and activate either Wifi or Data.");
                pbProgressBar.setVisibility(View.GONE);

            } else {
                renderSnackBar("Current network not connected to the internet. Please try again after some time or contact network operator.");
                pbProgressBar.setVisibility(View.GONE);
            }
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
                        ArrayList<Article> articleList = Article.fromGson(jsonTopLevel);
                        mArticleList.addAll(articleList);
                        int curSize = mArticleRecyclerViewAdapter.getItemCount();
                        mArticleRecyclerViewAdapter.notifyItemRangeInserted(curSize, mArticleList.size() - 1);
                        if (page == 0) {
                            rvArticles.scrollToPosition(0);
                        }
                    } else {
                        renderSnackBar(noContentMsg);
                    }
                } else {
                    renderSnackBar(contentErrorMsg);
                }
                pbProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(this.toString(), responseString);
                renderSnackBar(contentErrorMsg);
                pbProgressBar.setVisibility(View.GONE);
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

        try {
            if (!mFilter.getFormattedStartDate().equals(""))
                renderFilterDesc(tvStartDate, mFilter.getFormattedStartDate(), true);
            else
                renderFilterDesc(tvStartDate, "", false);
        } catch (ParseException e) {
            renderFilterDesc(tvStartDate, "", false);
            e.printStackTrace();
        }

        String choices = "";
        if (mFilter.getNewsDesk().size() > 0) {
            for (int i = 0; i < mFilter.getNewsDesk().size(); i++) {
                choices += mFilter.getNewsDesk().get(i);
                if (i != mFilter.getNewsDesk().size() - 1)
                    choices += ", ";
            }
            renderFilterDesc(tvNewsDesk, choices, true);
        } else
            renderFilterDesc(tvNewsDesk, choices, false);


        if (!mFilter.getSortBy().equals(""))
            renderFilterDesc(tvSortBy, mFilter.getSortBy(), true);
        else
            renderFilterDesc(tvSortBy, mFilter.getSortBy(), false);
    }

    private void renderFilterDesc(TextView view, String value, Boolean toShow) {
        if (!toShow) {
            view.setVisibility(View.GONE);
        } else {

            String key = "";
            String text = "";
            if (view.getId() == tvQuery.getId()) {
                key = getString(R.string.search_query);
            } else if (view.getId() == tvStartDate.getId()) {
                key = getString(R.string.start_date);
            } else if (view.getId() == tvNewsDesk.getId()) {
                key = getString(R.string.news_desk);
            } else if (view.getId() == tvSortBy.getId()) {
                key = getString(R.string.sort_by);
            }

            text += key + ": ";
            text += value;
            view.setText(text);

            view.setVisibility(View.VISIBLE);
        }
    }

    private void renderSnackBar(String msg) {

        final Snackbar snackBar = Snackbar.make(tvQuery, msg, Snackbar.LENGTH_INDEFINITE);

        snackBar.setAction("Dismiss", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                snackBar.dismiss();

            }
        });
        snackBar.setActionTextColor(Color.WHITE).show();
    }
}
