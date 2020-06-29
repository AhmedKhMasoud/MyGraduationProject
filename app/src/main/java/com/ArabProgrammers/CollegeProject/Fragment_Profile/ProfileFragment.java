package com.ArabProgrammers.CollegeProject.Fragment_Profile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ArabProgrammers.CollegeProject.OpenImageActivity;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadJobPic;
import com.ArabProgrammers.CollegeProject.uploadUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity.appLangPreferences;
import static com.ArabProgrammers.CollegeProject.MainActivities.SignUpActivity.DEFAULT_IMAGE;


public class ProfileFragment extends Fragment {

    private TextView txt_user_name , text_share_first_job_image , text_hope_our_app_help_you ,txt_user_rate_num;
    private ImageView img_add_pic , image_hope_our_app_help_you  ;
    private ImageView image_share_first_image;
    private CircleImageView profile_image;
    private ProgressBar m_progress_bar;
    private RecyclerView m_recycle_view;
    private RelativeLayout relative;
    private CardView card_rating;

    private FirebaseUser current_user;
    private DatabaseReference m_users_job_pic_database_ref, m_users_database_ref;
    private StorageReference m_storage_job_pic_database_ref;

    private StorageTask current_task;

    private Uri imageUri;

    public static ProfileFragmentAdapter my_profile_adapter;

    private List<uploadJobPic> users_job_image_list;
    private List<String> keys_list;
    private List<uploadUserData> user_data_list;

    private String[] account_type_string_list ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = getLayoutInflater().inflate(R.layout.fragment_profile, container, false);

        account_type_string_list = getResources().getStringArray(R.array.account_type_list);

        String key = appLangPreferences.getString("Language_Key" , "ar");

        users_job_image_list = new ArrayList<>();
        keys_list = new ArrayList<>();
        user_data_list = new ArrayList<>();

        //initialize firebase
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        if (current_user != null) {

            m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users")
                    .child(key).child(current_user.getUid());

            //to keep information when user offline
            m_users_database_ref.keepSynced(true);

            m_users_job_pic_database_ref = FirebaseDatabase.getInstance().getReference("UsersJobPic")
                    .child(current_user.getUid());


            m_storage_job_pic_database_ref = FirebaseStorage.getInstance().getReference("Jobs_Pic")
                    .child(current_user.getUid());


        }

        //initialize views
        txt_user_name = v.findViewById(R.id.txt_user_name);
        img_add_pic = v.findViewById(R.id.img_add_pic);

        profile_image = v.findViewById(R.id.profile_image);

        m_progress_bar = v.findViewById(R.id.m_progress_bar);

        m_recycle_view = v.findViewById(R.id.m_recycle_view);

        relative = v.findViewById(R.id.relative2);

        txt_user_rate_num = v.findViewById(R.id.txt_user_rate_num);
        card_rating = v.findViewById(R.id.card_rating);

        // if user still not upload any pic
        image_share_first_image = v.findViewById(R.id.image_share_first_image);
        text_share_first_job_image = v.findViewById(R.id.text_share_first_job_image);

        // if current user is client
        image_hope_our_app_help_you = v.findViewById(R.id.image_hope_our_app_help_you);
        text_hope_our_app_help_you = v.findViewById(R.id.text_hope_our_app_help_you);

        //initialize recycle view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()
                , LinearLayoutManager.VERTICAL, false);

        m_recycle_view.setHasFixedSize(true);
        m_recycle_view.setLayoutManager(layoutManager);


        //for get the user information
        getCurrentUserInformation();
        //getMyJobImages();


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //for upload job image
        img_add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current_task != null && current_task.isInProgress()) {

                    Toast.makeText(getContext(), "there is another progress in upload", Toast.LENGTH_SHORT).show();

                } else {

                    getPicFromGallery();

                }

            }
        });


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenImageActivity image = new OpenImageActivity(getContext(), user_data_list.get(0).getUserImageURL());
                image.displayImage();

            }
        });


    }



    //for get current user information
    private void getCurrentUserInformation() {

        m_progress_bar.setVisibility(View.VISIBLE);

        //getting user information
        if (current_user != null) {

            m_users_database_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.getChildren() != null) {


                        uploadUserData user_data = dataSnapshot.getValue(uploadUserData.class);

                        user_data_list.add(user_data);

                        //for fill user data
                        fillCurrentUserInformation(user_data_list);

                        getMyJobImages(user_data_list);


                    }

                    m_progress_bar.setVisibility(View.GONE);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    m_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "failed to load data >> " +
                            "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        } else {

            m_progress_bar.setVisibility(View.GONE);
        }

    }

    //to fill current user information
    private void fillCurrentUserInformation(List<uploadUserData> data) {

        String[] account_type_string_list = getResources().getStringArray(R.array.account_type_list);

        //fill the user data
        if (data.size() != 0) {


            // check current user profession type
            if (data.get(0).getUserAccountType().equals(account_type_string_list[0])) {

                relative.setVisibility(View.GONE);

                card_rating.setVisibility(View.GONE);

                text_hope_our_app_help_you.setVisibility(View.VISIBLE);
                image_hope_our_app_help_you.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.Shake)
                        .playOn(image_hope_our_app_help_you);

                YoYo.with(Techniques.FadeIn)
                        .playOn(text_hope_our_app_help_you);


            }


            //upload user name
            txt_user_name.setText(data.get(0).getUserName());

            txt_user_rate_num.setText(""+data.get(0).getUserTotalRate());

            //upload user image
            if (data.get(0).getUserImageURL().equals(DEFAULT_IMAGE)) {

                profile_image.setImageResource(R.drawable.default_profile_image);

            } else {

                Picasso.with(getContext()).load(data.get(0).getUserImageURL())
                        .placeholder(R.drawable.default_profile_image).into(profile_image);

            }


            m_progress_bar.setVisibility(View.GONE);

        }
        //if user did not have data
        else {

            txt_user_name.setText(getContext().getResources().getString(R.string.account_type_or_name));


            profile_image.setImageResource(R.drawable.default_profile_image);


            m_progress_bar.setVisibility(View.GONE);

        }

    }

    //to get current user all information data
    //to get my job images
    private void getMyJobImages(final List<uploadUserData> data) {

        m_progress_bar.setVisibility(View.VISIBLE);

        m_users_job_pic_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {

                    users_job_image_list.clear();
                    keys_list.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        uploadJobPic images_data = data.getValue(uploadJobPic.class);

                        users_job_image_list.add(images_data);
                        keys_list.add(data.getKey());

                    }


                    if(users_job_image_list.size() !=0){

                        text_share_first_job_image.setVisibility(View.GONE);
                        image_share_first_image.setVisibility(View.GONE);

                        m_recycle_view.setVisibility(View.VISIBLE);
                        m_progress_bar.setVisibility(View.GONE);

                        my_profile_adapter = new ProfileFragmentAdapter(users_job_image_list, keys_list, getContext());
                        m_recycle_view.setAdapter(my_profile_adapter);
                        my_profile_adapter.notifyDataSetChanged();

                    }
                    else{


                        // check current user profession type
                        if (!data.get(0).getUserAccountType().equals(account_type_string_list[0])) {

                            m_recycle_view.setVisibility(View.GONE);
                            m_progress_bar.setVisibility(View.GONE);

                            text_hope_our_app_help_you.setVisibility(View.GONE);
                            image_hope_our_app_help_you.setVisibility(View.GONE);

                            text_share_first_job_image.setVisibility(View.VISIBLE);
                            image_share_first_image.setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.FadeInDown)
                                    .playOn(text_share_first_job_image);

                            YoYo.with(Techniques.Swing)
                                    .playOn(image_share_first_image);


                        }

                    }






                } else {

                    // check current user profession type
                    if (!data.get(0).getUserAccountType().equals(account_type_string_list[0])) {

                        m_recycle_view.setVisibility(View.GONE);
                        m_progress_bar.setVisibility(View.GONE);

                        text_hope_our_app_help_you.setVisibility(View.GONE);
                        image_hope_our_app_help_you.setVisibility(View.GONE);

                        text_share_first_job_image.setVisibility(View.VISIBLE);
                        image_share_first_image.setVisibility(View.VISIBLE);

                        YoYo.with(Techniques.FadeInDown)
                                .playOn(text_share_first_job_image);

                        YoYo.with(Techniques.Swing)
                                .playOn(image_share_first_image);


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "failed to load images"
                        + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                m_progress_bar.setVisibility(View.GONE);

            }
        });


    }


    // for get the Extension of the photo
    private String getFileExtensions(Uri uri) {

        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    //to get the image from gallery
    private void getPicFromGallery() {


        if (Build.VERSION.SDK_INT > 23) {

            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickImage, 0);

            } else {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }


        }

    }


    //to get image uri
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            //for uploading job image
            uploadImageToStorage(imageUri);


        }


    }


    //for uploading image into job images
    private void uploadImageToStorage(Uri uri) {

        if (uri == null) {

            Toast.makeText(getContext(), "no photo selected", Toast.LENGTH_SHORT).show();

        } else {

            m_progress_bar.setVisibility(View.VISIBLE);

            Random random = new Random();

            final StorageReference filePath = m_storage_job_pic_database_ref
                    .child(random.nextInt() + "." + getFileExtensions(imageUri));


            current_task = filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                m_users_job_pic_database_ref.push().child("job_imageURL")
                                        .setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        m_progress_bar.setVisibility(View.GONE);

                                        Toast.makeText(getContext(), "upload job pic successfully"
                                                , Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }
                        });


                    } else {

                        m_progress_bar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "failed to load job pic "
                                + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }


    }


}
