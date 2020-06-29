package com.ArabProgrammers.CollegeProject.Fragment_Bookmark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.uploadUserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.ArabProgrammers.CollegeProject.MainActivities.LogInActivity.appLangPreferences;

public class BookMarkFragment extends Fragment {


    private DatabaseReference m_users_database_ref , m_bookmarks_database_ref;
    private FirebaseUser current_user;

    private ImageView oops_image;
    private TextView text;
    private SwipeRefreshLayout m_swipe_refresh;
    private ProgressBar m_progress_bar;
    private RecyclerView m_recycle_view;
    private List<String> bookmark_users_list;
    private List<uploadUserData> users_list;

    public static BookMark_Fragment_Adapter bookMark_fragment_adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = getLayoutInflater().inflate(R.layout.fragment_bookmark, container , false);

        String key = appLangPreferences.getString("Language_Key" , "ar");

        m_progress_bar = v.findViewById(R.id.m_progress_bar);
        m_swipe_refresh = v.findViewById(R.id.m_swipe_refresh);
        text = v.findViewById(R.id.text);
        oops_image = v.findViewById(R.id.oops_image);

        bookmark_users_list = new ArrayList<>();
        users_list = new ArrayList<>();

        current_user = FirebaseAuth.getInstance().getCurrentUser();
        m_users_database_ref = FirebaseDatabase.getInstance().getReference("Users").child(key);

        m_bookmarks_database_ref = FirebaseDatabase.getInstance()
                .getReference("BookMarks").child(current_user.getUid());

        //recycle view
        m_recycle_view = v.findViewById(R.id.m_recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext() , RecyclerView.VERTICAL , false);

        m_recycle_view.setHasFixedSize(true);
        m_recycle_view.setLayoutManager(layoutManager);


        //initialize adapter
        bookMark_fragment_adapter = new BookMark_Fragment_Adapter(users_list , getContext());
        m_recycle_view.setAdapter(bookMark_fragment_adapter);

        //to get the result
        getBookmarkUsers();


        m_swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getBookmarkUsers();

            }
        });


        return v;
    }







    //to get search result users information
    private void getBookmarkUsers(){

        m_progress_bar.setVisibility(View.VISIBLE);
        m_recycle_view.setVisibility(View.GONE);

        m_bookmarks_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue() != null) {

                    bookmark_users_list.clear();

                    for(DataSnapshot data : dataSnapshot.getChildren()){

                        String user_id = data.getKey();

                        bookmark_users_list.add(user_id);

                    }

                    if(bookmark_users_list.size() != 0){

                        m_recycle_view.setVisibility(View.VISIBLE);

                        text.setVisibility(View.GONE);
                        oops_image.setVisibility(View.GONE);

                        getBookmarkUsersInformation(bookmark_users_list);
                    }
                    else{

                        m_recycle_view.setVisibility(View.GONE);

                        m_progress_bar.setVisibility(View.GONE);

                        m_swipe_refresh.setRefreshing(false);

                        text.setVisibility(View.VISIBLE);
                        oops_image.setVisibility(View.VISIBLE);


                        YoYo.with(Techniques.Flash)
                                .playOn(text);

                        YoYo.with(Techniques.Landing)
                                .playOn(oops_image);

                    }


                }

                else {


                    m_recycle_view.setVisibility(View.GONE);

                    m_progress_bar.setVisibility(View.GONE);

                    m_swipe_refresh.setRefreshing(false);

                    text.setVisibility(View.VISIBLE);
                    oops_image.setVisibility(View.VISIBLE);


                    YoYo.with(Techniques.Flash)
                            .playOn(text);

                    YoYo.with(Techniques.Landing)
                            .playOn(oops_image);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                m_progress_bar.setVisibility(View.GONE);

            }
        });


    }


    //to get all bookmark users information
    private void getBookmarkUsersInformation(final List<String> users_key){

        m_users_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.getValue() != null) {

                    users_list.clear();

                    for(DataSnapshot data : dataSnapshot.getChildren()){


                        uploadUserData userData = data.getValue(uploadUserData.class);

                        for(int i=0 ; i<users_key.size() ; i++){

                            if(userData.getUserId().equals(users_key.get(i))){

                                users_list.add(userData);

                            }


                        }


                    }

                    bookMark_fragment_adapter.notifyDataSetChanged();

                    m_progress_bar.setVisibility(View.GONE);

                    m_swipe_refresh.setRefreshing(false);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                m_progress_bar.setVisibility(View.GONE);

                m_swipe_refresh.setRefreshing(false);

            }
        });



    }



}
