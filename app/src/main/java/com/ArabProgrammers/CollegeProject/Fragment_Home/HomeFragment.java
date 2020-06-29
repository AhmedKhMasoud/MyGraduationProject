package com.ArabProgrammers.CollegeProject.Fragment_Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ArabProgrammers.CollegeProject.R;
import com.ArabProgrammers.CollegeProject.FragmentShopList;
import com.ArabProgrammers.CollegeProject.FragmentProfessionList;
import com.ArabProgrammers.CollegeProject.SearchActivity.SearchActivity;

public class HomeFragment extends Fragment {

    private TextView txt_sni3y, txt_shops;
    private LinearLayout lin_electrician, lin_mechanical, lin_plumbing,
            lin_plumbing_tools, lin_electrical_tools, lin_car_wash;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = getLayoutInflater().inflate(R.layout.fragment_home, container , false);

        //initialize views
        txt_sni3y = v.findViewById(R.id.txt_sni3y);
        txt_shops = v.findViewById(R.id.txt_shops);

        lin_electrician = v.findViewById(R.id.lin_electrician);
        lin_mechanical = v.findViewById(R.id.lin_mechanical);
        lin_plumbing = v.findViewById(R.id.lin_plumbing);
        lin_plumbing_tools = v.findViewById(R.id.lin_plumbing_tools);
        lin_electrical_tools = v.findViewById(R.id.lin_electrical_tools);
        lin_car_wash = v.findViewById(R.id.lin_car_wash);

        ////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////
        lin_electrician.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSearching(getString(R.string.Electrician));

            }
        });

        lin_mechanical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSearching(getString(R.string.mechanical));

            }
        });

        lin_plumbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSearching(getString(R.string.Plumber));

            }
        });

        lin_plumbing_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSearching(getString(R.string.plumbing_tools));

            }
        });

        lin_electrical_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSearching(getString(R.string.electrical_tools));

            }
        });

        lin_car_wash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startSearching(getString(R.string.car_wash));


            }
        });

        ////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////

        txt_sni3y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout_container , new FragmentProfessionList() , "PROFESSION")
                        .commit();

            }
        });

        txt_shops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout_container , new FragmentShopList() , "SHOP")
                        .commit();

            }
        });

        return v;
    }


    private void startSearching(String searchFor){

        Intent openSearch = new Intent(getContext() , SearchActivity.class);
        openSearch.putExtra("SearchFor" , searchFor);
        startActivity(openSearch);

    }
}
