package com.blog.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blog.app.R;
import com.blog.app.activity.FullImageSliderActivity;
import com.blog.app.listner.SliderClickListener;
import com.blog.app.model.PostData;
import com.blog.app.model.User;
import com.blog.app.utils.AppConstant;
import com.blog.app.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHodler> {

    private Context context;
    private ArrayList<PostData> arrayList;

    public PostAdapter(Context context, ArrayList<PostData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false);
        return new ViewHodler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHodler holder, int position) {
        setUserData(holder, position);
        setViewPager(holder, position);
        holder.tvDesc.setText(arrayList.get(position).getPostDescription());
        holder.tvTime.setText(AppUtils.getTimeAgo(Long.parseLong(arrayList.get(position).getPostCreationDate())));
    }

    private SliderClickListener listener = new SliderClickListener() {
        @Override
        public void onClick(int pos, ArrayList<String> images) {
            Intent intent = new Intent(context, FullImageSliderActivity.class);
            intent.putExtra("data", images);
            intent.putExtra("position", pos);
            context.startActivity(intent);
        }
    };

    private void setViewPager(final ViewHodler holder, final int position) {
        final int count = arrayList.get(position).getImageUrls().size();
        if (count == 1) {
            holder.tvCount.setVisibility(View.GONE);
        } else {
            holder.tvCount.setText("1/" + count);
        }
        ImageSliderAdapter adapter = new ImageSliderAdapter(context,
                arrayList.get(position).getImageUrls(), listener);
        holder.viewPager.setAdapter(adapter);

        holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                holder.tvCount.setText((position + 1) + "/" + count);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUserData(final ViewHodler holder, int position) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(AppConstant.USER_TABLE)
                .child(arrayList.get(position).getUserId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                holder.tvName.setText(user.getName());
                Glide.with(context).load(user.getUserImage()).into(holder.imgUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHodler extends RecyclerView.ViewHolder {

        private CircleImageView imgUser;
        private TextView tvName, tvCount, tvTime, tvDesc;
        private ViewPager viewPager;

        public ViewHodler(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.circleImageView1);
            tvName = itemView.findViewById(R.id.textView4);
            tvCount = itemView.findViewById(R.id.textView14);
            tvTime = itemView.findViewById(R.id.textView11);
            tvDesc = itemView.findViewById(R.id.textView12);
            viewPager = itemView.findViewById(R.id.viewPager);
        }
    }
}
