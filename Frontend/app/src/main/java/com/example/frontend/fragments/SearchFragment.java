package com.example.frontend.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.R;
import com.example.frontend.response.Search.SearchHistoryResponse;
import com.example.frontend.response.User.UserResponse;
import com.example.frontend.utils.SharedPreference_SearchHistory;
import com.example.frontend.viewModel.Search.SearchQuery_ViewModel;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    Fragment_searchUser fragment_searchUser;
    Fragment_performSearch fragment_performSearch;
    Fragment_searchHistory fragment_searchHistory;
    Button btnSearch;
    private SearchQuery_ViewModel searchQueryViewModel;
    private SearchView searchView;

    private SharedPreference_SearchHistory sharedPreferences;
    private ArrayList<SearchHistoryResponse> searchHistoryResponseArrayList;
    private Gson gson;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Khởi tạo search Query ViewModel
        searchQueryViewModel = new ViewModelProvider(requireActivity()).get(SearchQuery_ViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gson = new Gson();
        sharedPreferences = new SharedPreference_SearchHistory(getActivity());
        getSearchHistoryListFromSharedPreference();

        fragment_searchHistory = new Fragment_searchHistory();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_Search_Container, fragment_searchHistory)
                .commit();


        searchView = view.findViewById(R.id.searchView);
        btnSearch = view.findViewById(R.id.btnSearch);
        btnSearch.setVisibility(View.GONE);
        // Nhập nội dung tìm kiếm và bắt đầu tìm kiếm theo tên user
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // Set newText vào ViewModel để Fragment_searchUser có thể lấy được newText
                searchQueryViewModel.setSearchQuery(newText);

                Fragment currentFragment = getChildFragmentManager().findFragmentById(R.id.fragment_Search_Container);

                if (currentFragment instanceof Fragment_performSearch || currentFragment instanceof Fragment_searchHistory) {
                    fragment_searchUser = new Fragment_searchUser();
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.fragment_Search_Container, fragment_searchUser)
                            .addToBackStack(null) // Optional: Add to back stack for navigation
                            .commit();
                }
                else fragment_searchUser.resultList();
                if(!newText.equals(""))
                    btnSearch.setVisibility(View.VISIBLE);
                else btnSearch.setVisibility(View.GONE);

                return false;
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch(searchQueryViewModel.getSearchQuery());
            }
        });

    }

    private void getSearchHistoryListFromSharedPreference() {
        String jsonHistory = sharedPreferences.read_SearchHistoryList();
        Type type = new TypeToken<List<SearchHistoryResponse>>() {}.getType();
        searchHistoryResponseArrayList = gson.fromJson(jsonHistory, type);

        if (searchHistoryResponseArrayList == null) {
            searchHistoryResponseArrayList = new ArrayList<>();
        }
    }

    private void saveSearchHistoryListToSharedPreference(ArrayList<SearchHistoryResponse> searchHistoryList) {
        // convert object to String by Gson
        String jsonHistory = gson.toJson(searchHistoryList);

        // save to shared preference
        sharedPreferences.save_SearchHistoryList(jsonHistory);
    }

    private void saveToSearchHistory(SearchHistoryResponse searchHistoryResponse, String query) {
        if (searchHistoryResponseArrayList.isEmpty())
            searchHistoryResponseArrayList.add(searchHistoryResponse);
        else {
            // Xoa object trong shared preference co text trung voi query
            int i = 0;
            while (i < searchHistoryResponseArrayList.size()) {
                if (searchHistoryResponseArrayList.get(i).getText().equals(query) && !searchHistoryResponseArrayList.get(i).getAccount()) {
                    searchHistoryResponseArrayList.remove(searchHistoryResponseArrayList.get(i));
                    break;
                }
                i++;
            }
            searchHistoryResponseArrayList.add(0, searchHistoryResponse);
        }

        // Luu vao shared preference
        saveSearchHistoryListToSharedPreference(searchHistoryResponseArrayList);
    }

    public void startSearch(String query) {
        getSearchHistoryListFromSharedPreference();

        // Khi nhan tim kiem -> put data vao Shared preferences
        SearchHistoryResponse searchHistoryResponse = new SearchHistoryResponse(query, null, false, null, null, new java.util.Date());
        // Luu vao shared preference
        saveToSearchHistory(searchHistoryResponse, query);

        // Không hiện con trỏ nhấp nháy trong searchview
        searchView.clearFocus();

        // Set query vào ViewModel để Fragment_searchUser có thể lấy được query
        searchQueryViewModel.setSearchQuery(query);

        // Chuyển sang fragment_performSearch
        fragment_performSearch = new Fragment_performSearch();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_Search_Container, fragment_performSearch, "perform_search")
                .addToBackStack("perform_search") // Optional: Add to back stack for navigation
                .commit();
    }

}
