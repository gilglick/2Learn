package com.example.a2learn.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2learn.R;
import com.example.a2learn.model.Student;
import com.example.a2learn.utility.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private Context context;
    private List<Student> mUsers;
    private OnFragmentLoader fragmentLoader;
    private Student caller;

    public StudentAdapter(Context context, List<Student> mUsers, Student caller) {
        this.context = context;
        this.mUsers = mUsers;
        this.caller = caller;
        setHasStableIds(true);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userNameChat;
        private ImageView userImageChat;
        private ImageView chatImageView;
        private ImageView ratingImageView;
        private ImageView infoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameChat = itemView.findViewById(R.id.userNameChat);
            userImageChat = itemView.findViewById(R.id.userImageChat);
            chatImageView = itemView.findViewById(R.id.userChat);
            ratingImageView = itemView.findViewById(R.id.userRating);
            infoImageView = itemView.findViewById(R.id.userInfo);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_match_item, parent, false);
        return new StudentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Student callee = mUsers.get(position);
        holder.userNameChat.setText(callee.getFullName());
        if (!callee.getUri().matches("")) {
            Picasso.get().load(callee.getUri()).transform(new CircleTransform()).into(holder.userImageChat);
        } else {
            Picasso.get().load(R.drawable.no_picture_circle).transform(new CircleTransform()).into(holder.userImageChat);
        }
        holder.chatImageView.setOnClickListener(v -> {
            if (fragmentLoader != null) {
                fragmentLoader.triggerFragmentChat(callee);
            }
        });
        holder.infoImageView.setOnClickListener(v -> {
            if (fragmentLoader != null) {
                fragmentLoader.triggerFragmentProfile(callee);
            }
        });
        holder.ratingImageView.setOnClickListener(v -> {
            if (fragmentLoader != null) {
                fragmentLoader.triggerRatingBar(caller, callee);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public void setFragmentLoader(OnFragmentLoader fragmentLoader) {
        this.fragmentLoader = fragmentLoader;
    }

    public interface OnFragmentLoader {
        void triggerFragmentChat(Student student);

        void triggerFragmentProfile(Student student);

        void triggerRatingBar(Student caller, Student callee);
    }
}