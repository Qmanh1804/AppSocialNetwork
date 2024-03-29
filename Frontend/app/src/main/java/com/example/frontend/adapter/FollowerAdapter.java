package com.example.frontend.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.response.User.UserResponse;
import com.example.frontend.utils.SharedPreferenceLocal;
import com.example.frontend.viewModel.Follows.FollowsViewModel;
import com.example.frontend.viewModel.User.UserViewModel;

import java.util.List;

public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.ViewHolder> {

    public static Context mContext;
    public List<UserResponse> userResponseList;
    public static FollowsViewModel followsViewModel;
    private LifecycleOwner lifecycleOwner;
    public static UserViewModel userViewModel;

    public FollowerAdapter(Context mContext, List<UserResponse> userResponseList,FollowsViewModel followsViewModel,LifecycleOwner lifecycleOwner,
                            UserViewModel userViewModel) {
        this.mContext = mContext;
        this.userResponseList = userResponseList;
        this.followsViewModel = followsViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.userViewModel = userViewModel;
    }
    @NonNull
    @Override
    public FollowerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_follower, parent, false);
        return new FollowerAdapter.ViewHolder(view,lifecycleOwner);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerAdapter.ViewHolder holder, int position) {
        UserResponse userResponse = userResponseList.get(position);
        // Set thông tin bài đăng vào các view
        holder.idNameUser.setText(userResponse.getUsername());
        holder.nameUser.setText(userResponse.getName());
        holder.idFollows = userResponse.getId();
        // Load hình ảnh từ URL bằng Glide
        Glide.with(mContext)
                .load(userResponse.getAvatarImg())
                .placeholder(R.drawable.logo) // Ảnh thay thế khi đang load
                .error(R.drawable.logo) // Ảnh thay thế khi có lỗi
                .into(holder.img_avtUser);
    }

    @Override
    public int getItemCount() {
        return userResponseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_avtUser;
        private TextView idNameUser, nameUser;
        private Button btnFollow;
        private String idFollows;
        private LifecycleOwner lifecycleOwner;
        public ViewHolder(@NonNull View itemView, LifecycleOwner lifecycleOwner) {
            super(itemView);
            img_avtUser = itemView.findViewById(R.id.img_avtUser);
            idNameUser = itemView.findViewById(R.id.idNameUser);
            nameUser = itemView.findViewById(R.id.nameUser);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            this.lifecycleOwner = lifecycleOwner;

            String userId = SharedPreferenceLocal.read(mContext.getApplicationContext(), "userId");

        }

    }
}
