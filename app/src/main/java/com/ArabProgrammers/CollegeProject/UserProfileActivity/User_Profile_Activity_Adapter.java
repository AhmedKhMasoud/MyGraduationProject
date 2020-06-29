package com.ArabProgrammers.CollegeProject.UserProfileActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ArabProgrammers.CollegeProject.OpenImageActivity;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadJobPic;
import com.squareup.picasso.Picasso;

import java.util.List;

public class User_Profile_Activity_Adapter extends RecyclerView.Adapter<User_Profile_Activity_Adapter.m_viewHolder>{

    List<uploadJobPic> image_list;
    Context context;

    public User_Profile_Activity_Adapter(List<uploadJobPic> image_list, Context context) {
        this.image_list = image_list;
        this.context = context;
    }

    @NonNull
    @Override
    public m_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.activity_user_profile_form, parent , false);

        m_viewHolder viewHolder = new m_viewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull m_viewHolder holder, final int position) {


        if(image_list.size() != 0 ) {

            Picasso.with(context).load(image_list.get(position).getJob_imageURL())
                    .placeholder(R.drawable.default_profile_image)
                    .into(holder.user_job_pic);

        }else {

            holder.user_job_pic.setImageResource(R.drawable.default_profile_image);

        }


        holder.user_job_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenImageActivity image = new OpenImageActivity(context , image_list.get(position).getJob_imageURL());
                image.displayImage();

            }
        });

    }

    @Override
    public int getItemCount() {
        return image_list.size();
    }

    public class m_viewHolder extends RecyclerView.ViewHolder{

        private ImageView user_job_pic;

        public m_viewHolder(@NonNull View itemView) {
            super(itemView);

            user_job_pic = itemView.findViewById(R.id.user_job_pic);
        }
    }

}
