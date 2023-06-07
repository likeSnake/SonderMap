package net.maps.navigation.gps.location.sondermap.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mapbox.geojson.Point;
import com.mapbox.search.CompletionCallback;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchEngineSettings;
import com.mapbox.search.SearchOptions;
import com.mapbox.search.SearchSelectionCallback;
import com.mapbox.search.ServiceProvider;
import com.mapbox.search.common.AsyncOperationTask;
import com.mapbox.search.record.HistoryDataProvider;
import com.mapbox.search.record.HistoryRecord;
import com.mapbox.search.result.SearchAddress;
import com.mapbox.search.result.SearchResult;
import com.mapbox.search.result.SearchSuggestion;

import net.maps.navigation.gps.location.sondermap.R;
import net.maps.navigation.gps.location.sondermap.adapter.ResAdapter;
import net.maps.navigation.gps.location.sondermap.base.BaseActivity;
import net.maps.navigation.gps.location.sondermap.base.TestBeam;
import net.maps.navigation.gps.location.sondermap.bean.ResultsBean;
import net.maps.navigation.gps.location.sondermap.util.DistanceUtil;
import net.maps.navigation.gps.location.sondermap.util.MyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActSearch extends BaseActivity {

    private SearchOptions options;
    private ConstraintLayout la_search;
    private EditText search;
    private  List<ResultsBean> list = new ArrayList<>();

    private AsyncOperationTask searchRequestTask;
    private SearchEngine searchEngine;
    private AsyncOperationTask task = null;
    private ResAdapter destinationAdapter;
    private RecyclerView rv_search_result;
    private final HistoryDataProvider historyDataProvider = ServiceProvider.getInstance().historyDataProvider();
    @Override
    protected int getRootView() {
        return R.layout.act_search;
    }

    // 检索搜索历史
    private final CompletionCallback<List<HistoryRecord>> callback = new CompletionCallback<List<HistoryRecord>>() {
        @Override
        public void onComplete(List<HistoryRecord> result) {
            Log.i("SearchApiExample", "所有搜索历史: " + result);

            System.out.println(result.size());
            for (int i = 0; i < result.size(); i++) {
                System.out.println(result.get(i).getName());
            }

        }

        @Override
        public void onError(@NonNull Exception e) {
            Log.i("SearchApiExample", "Unable to retrieve history records", e);
        }
    };


    @Override
    protected void initView() {
        la_search = findViewById(R.id.la_search);
        search = findViewById(R.id.search);
        rv_search_result = findViewById(R.id.rv_search_result);

    }

    @Override
    protected void initData() {
        options = new SearchOptions.Builder()
                .limit(5)
                .build();

        task = historyDataProvider.getAll(callback);

        searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
                new SearchEngineSettings(this.getString(R.string.mapbox_access_token))
        );
    }

    @Override
    protected void initListener() {

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                System.out.println(actionId);
                if ((actionId == EditorInfo.IME_ACTION_SEARCH||actionId ==EditorInfo.IME_ACTION_UNSPECIFIED) && event != null) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ActSearch.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    System.out.println("开始搜索");
                    searchRequestTask = (AsyncOperationTask) searchEngine.search(v.getText().toString(), options, searchCallback);
                }

                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){


        }

    }
    private final SearchSelectionCallback searchCallback = new SearchSelectionCallback() {

        @Override
        public void onSuggestions(@NonNull List<SearchSuggestion> suggestions, @NonNull ResponseInfo responseInfo) {
            list.clear();
            if (suggestions.isEmpty()) {
                Log.i("SearchApiExample", "没搜索到");
            } else {

           //     tv_search_title.setText("SEARCH RESULT");

                for (int i = 0; i < 5; i++) {
                    searchRequestTask = (AsyncOperationTask) searchEngine.select(suggestions.get(i), this);
                }
            }
        }

        @Override
        public void onResult(@NonNull SearchSuggestion suggestion, @NonNull SearchResult result, @NonNull ResponseInfo info) {
            Log.i("SearchApiExample", "精确地址: " + result);
            String addressInfo = "";
            Point point = result.getCoordinate();
            SearchAddress address = result.getAddress();
            assert address != null;
            String country = address.getCountry();
            String region = address.getRegion();
            String place = address.getPlace();
            String locality = address.getLocality();
            String street = address.getStreet();
            String name = result.getName();
            Point MyPoint = result.getRequestOptions().getOptions().getProximity();
            if (country!=null){
                addressInfo = country+" ";
            }
            if (region!=null){
                addressInfo += region+" ";
            }
            if (place!=null){
                addressInfo += place+" ";
            }
            if (locality!=null){
                addressInfo += locality+" ";
            }
            if (street!=null){
                addressInfo += street;
            }
            //  System.out.println(point.longitude()+" "+ point.latitude()+ " "+MyPoint.longitude()+" "+ MyPoint.latitude());
            double distances = DistanceUtil.getDistances(point.longitude(), point.latitude(), MyPoint.longitude(), MyPoint.latitude());
            System.out.println("-地址名:"+name+"-地址:"+addressInfo+"-距离:"+distances);
            list.add(new ResultsBean(name,addressInfo,String.valueOf(distances)+" km",point));
            start(false,list);
      //      rv_search_record.setVisibility(View.GONE);
            rv_search_result.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCategoryResult(@NonNull SearchSuggestion suggestion, @NonNull List<SearchResult> results, @NonNull ResponseInfo responseInfo) {
            Log.i("SearchApiExample", "Category search results: " + results);
        }

        @Override
        public void onError(@NonNull Exception e) {
            Log.i("SearchApiExample", "Search error: ", e);
        }
    };
    public void start(Boolean b,List<ResultsBean> list){
        LinearLayoutManager manager = new LinearLayoutManager(ActSearch.this, LinearLayoutManager.VERTICAL, false);
        destinationAdapter = new ResAdapter(list,this);
        if(b){
            rv_search_result.scrollToPosition(destinationAdapter.getItemCount()-1);
        }
        rv_search_result.setLayoutManager(manager);
        rv_search_result.setAdapter(destinationAdapter);
    }
    @Override
    protected void onDestroy() {
        if (searchRequestTask!=null){
            searchRequestTask.cancel();
        }
        if (task!=null){
            task.cancel();
        }

        super.onDestroy();
    }
}