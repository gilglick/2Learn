package com.example.a2learn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a2learn.model.Student;
import com.example.a2learn.utility.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {
    private Context context;
    private List<Student> mUsers;
    private OnFragmentLoader fragmentLoader;

    public StudentAdapter(Context context, List<Student> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
        setHasStableIds(true);

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userNameChat;
        public ImageView userImageChat;
        public Button userChatButton;
        public Button userProfileButton;
        private View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameChat = itemView.findViewById(R.id.userNameChat);
            userImageChat = itemView.findViewById(R.id.userImageChat);
            userChatButton = itemView.findViewById(R.id.userChat);
            userProfileButton = itemView.findViewById(R.id.userProfile);
            this.itemView = itemView;
        }

        public View getItemView() {
            return itemView;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new StudentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Student student = mUsers.get(position);
        holder.userNameChat.setText(student.getFullName());
        if (!student.getUri().matches("")) {
            Picasso.get().load(student.getUri()).transform(new CircleTransform()).into(holder.userImageChat);
        } else {
            Picasso.get().load(R.drawable.no_picture_circle).transform(new CircleTransform()).into(holder.userImageChat);
        }
        holder.userChatButton.setOnClickListener(v -> {
            if (fragmentLoader != null) {
                fragmentLoader.triggerFragmentChat(student);
            }
        });
        holder.userProfileButton.setOnClickListener(v -> {
            if(fragmentLoader != null){
                fragmentLoader.triggerFragmentProfile(student);
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

    public void clear() {
        int size = getItemCount();
        if (size > 0) {
            mUsers.subList(0, size).clear();

            notifyItemRangeRemoved(0, size);
        }
    }


    public void setFragmentLoader(OnFragmentLoader fragmentLoader) {
        this.fragmentLoader = fragmentLoader;
    }

    interface OnFragmentLoader {
        void triggerFragmentChat(Student student);
        void triggerFragmentProfile(Student student);
    }
}
