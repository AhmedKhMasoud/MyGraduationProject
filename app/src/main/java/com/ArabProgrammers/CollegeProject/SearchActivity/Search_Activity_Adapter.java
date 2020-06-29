package com.ArabProgrammers.CollegeProject.SearchActivity;

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
import com.ArabProgrammers.CollegeProject.UserProfileActivity.UserProfileActivity;
import com.ArabProgrammers.CollegeProject.uploadDistanceByKm;
import com.ArabProgrammers.CollegeProject.uploadUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class Search_Activity_Adapter  extends RecyclerView.Adapter<Search_Activity_Adapter.m_viewHolder>{

    private List<uploadUserData> users_list;
    private List<uploadDistanceByKm> km_list;
    private Context context;

    private FirebaseUser current_user;
    private DatabaseReference m_users_bookmark_database_ref;


    public Search_Activity_Adapter(List<uploadUserData> users_list, List<uploadDistanceByKm> km_list , Context context) {
        this.users_list = users_list;
        this.km_list=km_list;
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

        View v = LayoutInflater.from(context).inflate(R.layout.activity_search_form, parent , false);

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

            Picasso.with(context).load(users_list.get(position).getUserImageThumbnailsURL())
                    .placeholder(R.drawable.default_profile_image).into(holder.profile_image);


        }

        //put the kilometer
        if(km_list.size() != 0){

            holder.txt_km.setText(km_list.get(position).getKm()+" "+context.getString(R.string.km));

        }else{

            holder.txt_km.setVisibility(View.GONE);
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

        //check if this user already bookmarked or not
        m_users_bookmark_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null ) {

                    if(dataSnapshot.hasChild(users_list.get(position).getUserId())){

                        holder.img_bookmark.setVisibility(View.GONE);


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ///////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////

        holder.img_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                bookMarkUser(users_list.get(position).getUserId());

            }
        });


        holder.m_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openUserProfile = new Intent(context , UserProfileActivity.class);
                openUserProfile.putExtra("userId" , users_list.get(position).getUserId());
                openUserProfile.putExtra("userName" , users_list.get(position).getUserName());
                openUserProfile.putExtra("userPhone1" , users_list.get(position).getUserPhoneNumber());
                openUserProfile.putExtra("userPhone2" , users_list.get(position).getUserAnotherPhoneNumber());
                openUserProfile.putExtra("userImage" , users_list.get(position).getUserImageURL());
                openUserProfile.putExtra("userRate", users_list.get(position).getUserTotalRate());
                context.startActivity(openUserProfile);
            }
        });

    }

    public class m_viewHolder extends RecyclerView.ViewHolder{

        private CircleImageView profile_image;
        private TextView txt_user_name , txt_km , txt_total_rating;
        private ImageView img_call , img_bookmark;
        private CardView m_card_view;

        public m_viewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            txt_user_name = itemView.findViewById(R.id.txt_user_name);
            txt_km = itemView.findViewById(R.id.txt_km);
            txt_total_rating = itemView.findViewById(R.id.txt_total_rating);
            img_call = itemView.findViewById(R.id.img_call);
            img_bookmark = itemView.findViewById(R.id.img_bookmark);
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
    private void bookMarkUser(String user) {

        m_users_bookmark_database_ref.child(user).child("BookMark").setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(context, "تم التعليم بنجاح"
                                    , Toast.LENGTH_SHORT).show();


                        } else {

                            Toast.makeText(context, "failed to bookmark\n" +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });


    }




}
