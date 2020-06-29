package com.ArabProgrammers.CollegeProject.Fragment_EditProfile;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ArabProgrammers.CollegeProject.OpenImageActivity;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadUserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity.appLangPreferences;
import static com.ArabProgrammers.CollegeProject.MainActivities.SignUpActivity.DEFAULT_ANOTHER_PHONE_NUMBER;
import static com.ArabProgrammers.CollegeProject.MainActivities.SignUpActivity.DEFAULT_IMAGE;
import static com.ArabProgrammers.CollegeProject.MainActivities.HomeActivity.PERMISSIONS;

public class EditProfileFragment extends Fragment {

    private TextView txt_user_name, txt_phone_number, txt_another_phone_number , txt_update_my_location;
    private ImageView img_edit_name, img_edit_phone_number, img_another_phone_number, img_change_photo, img_update_my_location;
    private CircleImageView profile_image;
    private ProgressBar m_progress_bar;

    private FirebaseUser current_user;
    private DatabaseReference m_users_database_ref;
    private StorageReference m_storage_database_ref , m_storage_database_thumbnails_ref;

    private List<uploadUserData> user_data_list;

    private Uri imageUri;

    private ProgressDialog mProgressDialog;
    private LocationManager locationManager;
    private UserLocationListener userListener ;

    String key ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = getLayoutInflater().inflate(R.layout.fragment_edit_profile, container, false);

        key = appLangPreferences.getString("Language_Key" , "ar");

        user_data_list = new ArrayList<>();

        //initialize firebase
        current_user = FirebaseAuth.getInstance().getCurrentUser();

        if (current_user != null) {

            m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users");

            m_storage_database_ref = FirebaseStorage.getInstance().getReference("ProfileImages");
            m_storage_database_thumbnails_ref = FirebaseStorage.getInstance()
                    .getReference("ProfileImagesThumbnails");

        }

        //initialize views
        txt_user_name = v.findViewById(R.id.txt_user_name);
        txt_phone_number = v.findViewById(R.id.txt_phone_number);
        txt_another_phone_number = v.findViewById(R.id.txt_another_phone_number);
        txt_update_my_location = v.findViewById(R.id.txt_update_my_location);

        img_edit_name = v.findViewById(R.id.img_edit_name);
        img_edit_phone_number = v.findViewById(R.id.img_another_phone_number);
        img_another_phone_number = v.findViewById(R.id.img_edit_add_phone);
        img_change_photo = v.findViewById(R.id.img_change_photo);
        img_update_my_location = v.findViewById(R.id.img_update_my_location);

        profile_image = v.findViewById(R.id.profile_image);

        m_progress_bar = v.findViewById(R.id.m_progress_bar);





        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //to get user information
        getUserInformation();


        //to edit the name
        img_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeUserName();

            }
        });


        //to edit the phone number
        img_edit_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changePhoneNumber();

            }
        });

        //to add another phone number
        img_another_phone_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeAnotherPhoneNumber();

            }
        });

        //to change user photo
        img_change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                changeUserPhoto();

            }
        });

        img_update_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                updateMyLocation();

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenImageActivity image = new OpenImageActivity(getContext() , user_data_list.get(0).getUserImageURL());
                image.displayImage();

            }
        });


    }


    //for get user information
    private void getUserInformation(){

        m_progress_bar.setVisibility(View.VISIBLE);

        //getting user information
        if (current_user != null) {

            m_users_database_ref.child(key).child(current_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getChildren() != null) {


                        uploadUserData user_data = dataSnapshot.getValue(uploadUserData.class);

                        user_data_list.add(user_data);

                        //for fill user data
                        fillUserData(user_data_list);


                    }

                    m_progress_bar.setVisibility(View.GONE);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    //m_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "failed to load data >> " +
                            "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        } else {

            m_progress_bar.setVisibility(View.GONE);
        }

    }

    //to fill user data
    private void fillUserData(List<uploadUserData> data) {

        //fill the user data
        if (data.size() != 0) {

            //upload user name
            txt_user_name.setText(data.get(0).getUserName());

            //upload user phone
            txt_phone_number.setText(data.get(0).getUserPhoneNumber());

            if (data.get(0).getUserAnotherPhoneNumber().equals(DEFAULT_ANOTHER_PHONE_NUMBER)) {

                txt_another_phone_number.setText(getContext().getResources()
                        .getString(R.string.add_another_phone_number));


            } else {

                txt_another_phone_number.setText(data.get(0).getUserAnotherPhoneNumber());

            }


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

            txt_phone_number.setText(getContext().getResources().getString(R.string.phone_number));

            txt_another_phone_number.setText(getContext().getResources()
                    .getString(R.string.add_another_phone_number));

            profile_image.setImageResource(R.drawable.default_profile_image);


            m_progress_bar.setVisibility(View.GONE);

        }

    }


    //to change user name
    private void changeUserName() {

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.custom_dialog_new_name_form);

        final EditText et_new_name = dialog.findViewById(R.id.et_new_phone_number);
        Button b_save = dialog.findViewById(R.id.b_save);
        Button b_dismiss = dialog.findViewById(R.id.b_dismiss);


        dialog.setCancelable(false);

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_new_name.getText().toString().isEmpty()) {

                    et_new_name.setError("no new name found");
                    et_new_name.requestFocus();
                    return;

                } else {


                    m_users_database_ref.child(key).child(current_user.getUid()).child("userName")
                            .setValue(et_new_name.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        if(key.equals("ar")){

                                            key = "en";
                                        }else {
                                            key = "ar";
                                        }


                                        m_users_database_ref.child(key).child(current_user.getUid()).child("userName")
                                                .setValue(et_new_name.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){


                                                            txt_user_name.setText(et_new_name.getText().toString());

                                                            Toast.makeText(getContext()
                                                                    , "new name updated successfully"
                                                                    , Toast.LENGTH_SHORT).show();

                                                            dialog.dismiss();

                                                        }

                                                    }
                                                });



                                    } else {

                                        Toast.makeText(getContext()
                                                , "failed to update new name>>" +
                                                        "" + task.getException().getMessage()
                                                , Toast.LENGTH_SHORT).show();

                                        dialog.dismiss();
                                    }

                                }
                            });

                }

            }
        });

        b_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }


    //to change user phone number
    private void changePhoneNumber() {

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.custom_dialog_new_phone_form);

        final EditText et_new_phone_number = dialog.findViewById(R.id.et_new_phone_number);
        Button b_save = dialog.findViewById(R.id.b_save);
        Button b_dismiss = dialog.findViewById(R.id.b_dismiss);

        dialog.setCancelable(false);

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_new_phone_number.getText().toString().isEmpty()) {

                    et_new_phone_number.setError("no phone number found");
                    et_new_phone_number.requestFocus();
                    return;

                }

                if (et_new_phone_number.length() != 11) {

                    et_new_phone_number.setError("phone number should be 11 character");
                    et_new_phone_number.requestFocus();
                    return;

                }


                m_users_database_ref.child(key).child(current_user.getUid())
                        .child("userPhoneNumber")
                        .setValue(et_new_phone_number.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    // to save the same data in the other language
                                    if(key.equals("ar")){

                                        key = "en";
                                    }else {
                                        key = "ar";
                                    }

                                    m_users_database_ref.child(key).child(current_user.getUid())
                                            .child("userPhoneNumber")
                                            .setValue(et_new_phone_number.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){


                                                        txt_phone_number.setText(""+et_new_phone_number.getText().toString());

                                                        Toast.makeText(getContext()
                                                                , "new phone number updated successfully"
                                                                , Toast.LENGTH_SHORT).show();

                                                        dialog.dismiss();
                                                    }

                                                }
                                            });



                                } else {

                                    Toast.makeText(getContext()
                                            , "failed to update new phone number>>" +
                                                    "" + task.getException().getMessage()
                                            , Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                }

                            }
                        });

            }


        });

        b_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }


    //to change the user another phone number
    private void changeAnotherPhoneNumber() {

        final Dialog dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.custom_dialog_another_phone_form);

        final EditText et_new_phone_number = dialog.findViewById(R.id.et_new_phone_number);
        Button b_save = dialog.findViewById(R.id.b_save);
        Button b_dismiss = dialog.findViewById(R.id.b_dismiss);


        dialog.setCancelable(false);

        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et_new_phone_number.getText().toString().isEmpty()) {

                    et_new_phone_number.setError("no phone number found");
                    et_new_phone_number.requestFocus();
                    return;

                }

                if (et_new_phone_number.length() != 11) {

                    et_new_phone_number.setError("phone number should be 11 character");
                    et_new_phone_number.requestFocus();
                    return;

                }


                m_users_database_ref.child(key).child(current_user.getUid())
                        .child("userAnotherPhoneNumber")
                        .setValue(et_new_phone_number.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {

                                    // to save the same data in the other language
                                    if(key.equals("ar")){

                                        key = "en";
                                    }else {
                                        key = "ar";
                                    }

                                    m_users_database_ref.child(key).child(current_user.getUid())
                                            .child("userAnotherPhoneNumber")
                                            .setValue(et_new_phone_number.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        txt_another_phone_number.setText(""+et_new_phone_number.getText().toString());

                                                        Toast.makeText(getContext()
                                                                , "another phone number updated successfully"
                                                                , Toast.LENGTH_SHORT).show();

                                                        dialog.dismiss();

                                                    }
                                                }
                                            });



                                } else {

                                    Toast.makeText(getContext()
                                            , "failed to update another phone number>>" +
                                                    "" + task.getException().getMessage()
                                            , Toast.LENGTH_SHORT).show();

                                    dialog.dismiss();
                                }

                            }
                        });

            }


        });

        b_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog.show();

    }


    // for get the Extension of the photo
    private String getFileExtensions(Uri uri) {

        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    //to change user photo
    private void changeUserPhoto() {


        if(Build.VERSION.SDK_INT > 23){

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


    //for get image uri
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {

            imageUri = data.getData();

            //to crop image
            CropImage.activity(imageUri)
                    .setMinCropResultSize(300, 300)
                    .setMaxCropResultSize(1500, 1500)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(getContext() , this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            //the new uri from cropped image
            Uri cropped_image_uri = result.getUri();


            //for uploading image with default size
            uploadProfileImageToStorage(cropped_image_uri);
            //for compress and uploading thumbnails images
            uploadThumbnailsImage(cropped_image_uri);



        }


    }


    //for uploading profile image in storage
    private void uploadProfileImageToStorage(final Uri uri) {

        m_progress_bar.setVisibility(View.VISIBLE);

        final StorageReference filePath = m_storage_database_ref.child(current_user.getUid() + "."
                + getFileExtensions(imageUri));

        filePath.putFile(uri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri urii) {


                                            m_users_database_ref.child(key).child(current_user.getUid())
                                                    .child("userImageURL")
                                                    .setValue(urii.toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            // to save the same data in the other language
                                                            if(key.equals("ar")){

                                                                key = "en";
                                                            }else {
                                                                key = "ar";
                                                            }

                                                            m_users_database_ref.child(key).child(current_user.getUid())
                                                                    .child("userImageURL")
                                                                    .setValue(urii.toString())
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){

                                                                                m_progress_bar.setVisibility(View.GONE);

                                                                                Toast.makeText(getContext()
                                                                                        , "uploaded successfully",
                                                                                        Toast.LENGTH_SHORT).show();

                                                                                //for refresh the data
                                                                                profile_image.setImageURI(uri);

                                                                            }
                                                                        }
                                                                    });


                                                        }
                                                    });

                                        }
                                    });



                        }else{

                            m_progress_bar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "failed to load image"
                                    , Toast.LENGTH_SHORT).show();

                        }


                    }
                });


    }

    //for uploading the thumbnails images
    private void uploadThumbnailsImage(Uri uri){

        try {

            final File file = new File(uri.getPath());

            Bitmap imageCompressed = new Compressor(getContext())
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(file);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageCompressed.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] bytes = baos.toByteArray();


            final StorageReference filePath = m_storage_database_thumbnails_ref
                    .child(current_user.getUid()+"."+getFileExtensions(imageUri));

            filePath.putBytes(bytes)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){

                                filePath.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(final Uri uri) {

                                                m_users_database_ref.child(key).child(current_user.getUid())
                                                        .child("userImageThumbnailsURL")
                                                        .setValue(uri.toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {


                                                                if(key.equals("ar")){

                                                                    key = "en";
                                                                }else {
                                                                    key = "ar";
                                                                }


                                                                m_users_database_ref.child(key).child(current_user.getUid())
                                                                        .child("userImageThumbnailsURL")
                                                                        .setValue(uri.toString());


                                                            }
                                                        });

                                            }
                                        });


                            }else{

                                Toast.makeText(getContext(), "failed to load thumbnail" +
                                        ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });



        }catch (Exception e){
            Toast.makeText(getContext(), "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    // for update user location
    private void updateMyLocation() {

        if(ContextCompat.checkSelfPermission(getContext() , PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext() , PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED) {

            //to get user location
            locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

            //check if gps is enabled or not
            boolean gps_provider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            String searchWay = "";

            if(gps_provider){

                searchWay = LocationManager.GPS_PROVIDER;

            }else {

                searchWay = LocationManager.NETWORK_PROVIDER;
            }


            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setTitle(getString(R.string.update_location));
            mProgressDialog.setMessage(getString(R.string.please_wait_until_taking_your_location));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            userListener = new UserLocationListener();

            locationManager.requestSingleUpdate(searchWay , userListener , null);

            //locationManager.requestLocationUpdates(searchWay
              //      , 0 , 0 ,userLocation );

        }

    }


    public class UserLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            uploadCurrentUserLocation(location.getLatitude() , location.getLongitude());


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            mProgressDialog.dismiss();
        }

        @Override
        public void onProviderEnabled(String provider) {

            // Toast.makeText(HomeActivity.this, "provider is enabled", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {

            //Toast.makeText(HomeActivity.this, "provider is disabled", Toast.LENGTH_SHORT).show();

        }
    }

    //to save the user location on firebase
    private void uploadCurrentUserLocation(final double latitude , final double longitude){

        final DatabaseReference m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users");

        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        m_users_database_ref.child(key).child(current_user.getUid())
                .child("userLocationLatitude")
                .setValue(latitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){



                            m_users_database_ref.child(key).child(current_user.getUid())
                                    .child("userLocationLongitude")
                                    .setValue(longitude)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            uploadCurrentUserLocationInOtherLanguage(latitude , longitude);


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            mProgressDialog.dismiss();

                                            Toast.makeText(getContext(), "failed to update location\n"
                                                    +e.getMessage(), Toast.LENGTH_SHORT).show();



                                        }
                                    });

                        }else {
                            mProgressDialog.dismiss();

                        }

                    }
                });

    }


    // to save data in other language
    private void uploadCurrentUserLocationInOtherLanguage(final double latitude, final double longitude) {

        if (key.equals("ar")) {

            key = "en";

        } else {

            key = "ar";
        }


        // save data in other language
        m_users_database_ref.child(key).child(current_user.getUid())
                .child("userLocationLatitude")
                .setValue(latitude)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            m_users_database_ref.child(key).child(current_user.getUid())
                                    .child("userLocationLongitude").setValue(longitude)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            if (task.isSuccessful()) {


                                                mProgressDialog.dismiss();

                                                Toast.makeText(getContext(), "تم تحديث الموقع بنجاح"
                                                        , Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });


                        }

                    }
                });


    }


}


