package com.ArabProgrammers.CollegeProject.Fragment_Bookmark;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ArabProgrammers.CollegeProject.MainActivities.SignUpActivity;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.SearchActivity.SearchActivity;
import com.ArabProgrammers.CollegeProject.UserProfileActivity.UserProfileActivity;
import com.ArabProgrammers.CollegeProject.uploadUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookMark_Fragment_Adapter  extends RecyclerView.Adapter<BookMark_Fragment_Adapter.m_viewHolder>{

    private List<uploadUserData> users_list;
    private Context context;

    private FirebaseUser current_user;
    private DatabaseReference m_users_bookmark_database_ref;

    public BookMark_Fragment_Adapter(List<uploadUserData> users_list, Context context) {
        this.users_list = users_list;
        this.context = context;

        current_user = FirebaseAuth.getInstance().getCurrentUser();

        if (current_user != null) {

            m_users_bookmark_database_ref = FirebaseDatabase.getInstance()
                    .getReference("BookMarks").child(current_user.getUid());
        }

    }

    @NonNull
    @Override
    public m_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.fragment_bookmark_form, parent , false);

        m_viewHolder viewHolder = new m_viewHolder(v);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return users_list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final m_viewHolder holder, final int position) {

        if(users_list.size() != 0 ) {

            holder.txt_user_name.setText(users_list.get(position).getUserName());

            holder.txt_total_rating.setText(users_list.get(position).getUserTotalRate()+"");

            ////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////

            if(users_list.get(position).getUserProfessionTypeOne().equals(SignUpActivity.DEFAULT_PROFESSION_TYPE_ONE)){


                //to check if user has more then one shop or not
                if(users_list.get(position).getUserShopTypeTwo().equals(SignUpActivity.DEFAULT_SHOP_TYPE_TWO)) {

                    holder.txt_user_job.setText(users_list.get(position).getUserShopTypeOne());

                }else {

                    holder.txt_user_job.setText(users_list.get(position).getUserShopTypeOne() + " , "
                            + users_list.get(position).getUserShopTypeTwo());

                }


            }

            if ( users_list.get(position).getUserShopTypeOne().equals(SignUpActivity.DEFAULT_SHOP_TYPE_ONE) ) {


                if(users_list.get(position).getUserProfessionTypeTwo().equals(SignUpActivity.DEFAULT_PROFESSION_TYPE_TWO)) {

                    holder.txt_user_job.setText(users_list.get(position).getUserProfessionTypeOne());

                }else {

                    holder.txt_user_job.setText(users_list.get(position).getUserProfessionTypeOne() + " , "
                            + users_list.get(position).getUserProfessionTypeTwo());

                }


            }


            ////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////

            Picasso.with(context).load(users_list.get(position).getUserImageThumbnailsURL())
                    .placeholder(R.drawable.default_profile_image).into(holder.profile_image);


        }

        holder.img_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(context , Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){


                    openPhoneNumber(users_list.get(position).getUserPhoneNumber()
                            , users_list.get(position).getUserAnotherPhoneNumber());


                }else {

                    ActivityCompat.requestPermissions(((SearchActivity)context)
                            ,new String[]{Manifest.permission.CALL_PHONE} , 0);
                }



            }
        });


        ///////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////

        holder.img_unBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                unBookMarkUser(users_list.get(position).getUserId());

            }
        });


        holder.m_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openUserProfile = new Intent(context, UserProfileActivity.class);
                openUserProfile.putExtra("userId", users_list.get(position).getUserId());
                openUserProfile.putExtra("userName", users_list.get(position).getUserName());
                openUserProfile.putExtra("userPhone1", users_list.get(position).getUserPhoneNumber());
                openUserProfile.putExtra("userPhone2", users_list.get(position).getUserAnotherPhoneNumber());
                openUserProfile.putExtra("userImage", users_list.get(position).getUserImageURL());
                openUserProfile.putExtra("userRate", users_list.get(position).getUserTotalRate());
                context.startActivity(openUserProfile);

            }
        });

    }

    public class m_viewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profile_image;
        private TextView txt_user_name , txt_user_job , txt_total_rating;
        private ImageView img_call , img_unBookmark;
        private CardView m_card_view;

        public m_viewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_user_job = itemView.findViewById(R.id.txt_user_job);
            txt_total_rating = itemView.findViewById(R.id.txt_total_rating);
            img_call = itemView.findViewById(R.id.img_call);
            img_unBookmark = itemView.findViewById(R.id.img_unBookmark);
            m_card_view = itemView.findViewById(R.id.m_card_view);
        }
    }


    private String selected_phone_number ;
    private String second_phone;
    private void openPhoneNumber(final String phone_one , final String phone_two){

        second_phone = phone_two;

        if( second_phone.equals(SignUpActivity.DEFAULT_ANOTHER_PHONE_NUMBER)){

            second_phone = context.getString(R.string.no_another_phone_number_yet);

        }

        CharSequence[] phones = {phone_one , second_phone};

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.calling))
                .setCancelable(false)
                .setItems(phones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0) {

                            selected_phone_number = phone_one;

                        } else {

                            selected_phone_number = second_phone;


                        }

                        Intent call = new Intent(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:"+selected_phone_number));
                        context.startActivity(call);

                    }
                })
                .setNeutralButton(context.getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create().show();

    }

    //to bookmark user
    private void unBookMarkUser(final String user) {

        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.unBookmark))
                .setMessage(context.getString(R.string.are_you_sure))
                .setNegativeButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        m_users_bookmark_database_ref.child(user).removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            Toast.makeText(context, "تم ازاله العلامه بنجاح", Toast.LENGTH_SHORT).show();

                                            BookMarkFragment.bookMark_fragment_adapter.notifyDataSetChanged();


                                        }else {

                                            Toast.makeText(context, "failed to remove\n"+
                                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                });



                    }
                })
                .setPositiveButton(context.getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .create().show();


    }




}
