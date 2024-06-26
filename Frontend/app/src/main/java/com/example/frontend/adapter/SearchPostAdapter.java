package com.example.frontend.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.frontend.R;
import com.example.frontend.request.Post.RequestPostByUserId;
import com.example.frontend.response.Post.PostResponse;
import com.example.frontend.response.User.UserResponse;

import java.util.List;

public class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.MyViewHolder> {

    Context context;
    List<RequestPostByUserId> post_searchList;
    LayoutInflater layoutInflater;
    private SearchPostAdapter.OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(SearchPostAdapter.OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public SearchPostAdapter(Context context, List<RequestPostByUserId> post_searchList) {
        this.context = context;
        this.post_searchList = post_searchList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.search_post_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(post_searchList.get(position).getImagePost() != null)
            Glide.with(context)
                    .load(Uri.parse(post_searchList.get(position).getImagePost().get(0)))
                    .placeholder(R.drawable.logo) // Ảnh thay thế khi đang load
                    .error(R.drawable.logo) // Ảnh thay thế khi có lỗi
                    .into(holder.imgSearchPost);
    }

    @Override
    public int getItemCount() {
        return post_searchList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSearchPost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgSearchPost = itemView.findViewById(R.id.imgSearchPost);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }
}
