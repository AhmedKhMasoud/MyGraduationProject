package com.ArabProgrammers.CollegeProject.Fragment_Profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.ArabProgrammers.CollegeProject.OpenImageActivity;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadJobPic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.List;
import static com.ArabProgrammers.CollegeProject.Fragment_Profile.ProfileFragment.my_profile_adapter;



public class ProfileFragmentAdapter extends RecyclerView.Adapter<ProfileFragmentAdapter.mViewHolder> {

    private List<uploadJobPic> users_job_pic_list;
    private List<String> keys_list;
    private Context context;


    public ProfileFragmentAdapter(List<uploadJobPic> users_job_pic_list, List<String> keys_list
            , Context context) {

        this.users_job_pic_list = users_job_pic_list;
        this.keys_list = keys_list;
        this.context = context;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context)
                .inflate(R.layout.fragment_profile_adapter, parent, false);

        mViewHolder viewHolder = new mViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, final int position) {


        if (users_job_pic_list.size() != 0) {

            Picasso.with(context).load(users_job_pic_list.get(position).getJob_imageURL())
                    .placeholder(R.drawable.default_profile_image)
                    .into(holder.img_job_pic);

        }

        holder.img_more_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteImage(users_job_pic_list.get(position).getJob_imageURL(), keys_list.get(position));

            }
        });

        holder.img_job_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenImageActivity image = new OpenImageActivity(context , users_job_pic_list.get(position).getJob_imageURL());
                image.displayImage();

            }
        });


    }

    @Override
    public int getItemCount() {
        return users_job_pic_list.size();
    }


    public class mViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_more_option, img_job_pic;

        public mViewHolder(@NonNull View itemView) {
            super(itemView);

            img_more_option = itemView.findViewById(R.id.img_more_option);
            img_job_pic = itemView.findViewById(R.id.img_job_pic);

        }


    }


    //for delete image
    private void deleteImage(String imageUrl, final String key) {

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference m_users_job_pic_database_ref = FirebaseDatabase.getInstance().getReference("UsersJobPic")
                .child(current_user.getUid());

        final FirebaseStorage m_storage_job_pic_database_ref = FirebaseStorage.getInstance();

        final StorageReference ref = m_storage_job_pic_database_ref.getReferenceFromUrl(imageUrl);

        //dialog
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getResources().getString(R.string.delete));
        progressDialog.setCancelable(false);


        //for show dialog
        CharSequence[] item = {context.getResources().getString(R.string.delete)};

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        mBuilder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if (which == 0) {

                    progressDialog.show();

                    ref.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    m_users_job_pic_database_ref.child(key).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()) {

                                                        my_profile_adapter.notifyDataSetChanged();

                                                        progressDialog.dismiss();

                                                        Toast.makeText(context, "image deleted successfully"
                                                                , Toast.LENGTH_SHORT).show();


                                                    } else {

                                                        progressDialog.dismiss();
                                                        Toast.makeText(context, "failed to delete image"
                                                                , Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });

                }
            }
        });


        mBuilder.show();

    }


}
