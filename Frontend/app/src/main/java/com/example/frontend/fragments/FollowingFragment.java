package com.example.frontend.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.frontend.R;
import com.example.frontend.adapter.FollowingAdapter;
import com.example.frontend.adapter.SuggestedMeAdapter;
import com.example.frontend.response.ApiResponse.ApiResponse;
import com.example.frontend.response.User.GetAllUserByFollowsResponse;
import com.example.frontend.response.User.UserResponse;
import com.example.frontend.utils.SharedPreferenceLocal;
import com.example.frontend.viewModel.Follows.FollowsViewModel;
import com.example.frontend.viewModel.User.UserViewModel;
import com.google.gson.Gson;

import java.util.List;

public class FollowingFragment extends Fragment {

    private RecyclerView list_following;
    private List<UserResponse> userResponseList;
    private FollowingAdapter followingAdapter;
    private UserViewModel userViewModel;
    public FollowsViewModel followsViewModel;
    public LinearLayout linearLayout;
    ProgressBar progressBar;
    String userId;

    public FollowingFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        list_following = view.findViewById(R.id.list_following);
        linearLayout = view.findViewById(R.id.noDataFollowing);
        list_following.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // init call api suggest
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        followsViewModel = new ViewModelProvider(this).get(FollowsViewModel.class);

        // Hiển thị ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        list_following.setVisibility(View.GONE);
        // Nhận dữ liệu email từ Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            userId = bundle.getString("userId", "");
        }
        else userId = SharedPreferenceLocal.read(getContext().getApplicationContext(), "userId");
        followsViewModel.getUserFollowingById(userId).observe(getViewLifecycleOwner(), new Observer<ApiResponse<List<UserResponse>>>() {
            @Override
            public void onChanged(ApiResponse<List<UserResponse>> response) {
                Gson gson = new Gson();
                String json = gson.toJson(response);
                Log.d("following",json);
                if (response.getMessage().equals("Success!") && response.getStatus()) {
                    progressBar.setVisibility(View.GONE);
                    list_following.setVisibility(View.VISIBLE);
                    userResponseList = response.getData();
                    if (userResponseList.size() > 0){
                        followingAdapter = new FollowingAdapter(getContext(), userResponseList,followsViewModel,getViewLifecycleOwner(),userViewModel);
                        list_following.setAdapter(followingAdapter);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        list_following.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Xử lý khi không có dữ liệu hoặc có lỗi
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}