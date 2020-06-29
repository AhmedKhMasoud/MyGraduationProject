package com.ArabProgrammers.CollegeProject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ArabProgrammers.CollegeProject.SearchActivity.SearchActivity;

public class FragmentShopList extends Fragment {

    int shop_images_list[];
    String shop_type_list[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = getLayoutInflater().inflate(R.layout.fragment_shop_list, container, false);


        shop_images_list = new int[]
                {R.drawable.shop_list_icon_electricity, R.drawable.shop_list_icon_irons_and_paints,
                        R.drawable.shop_list_icon_cement_and_sand, R.drawable.shop_list_icon_armature,
                        R.drawable.shop_list_icon_contracting_company, R.drawable.shop_list_icon_electrical_devieces_mainternance,
                        R.drawable.shop_list_icon_screens_mainternance, R.drawable.shop_list_icon_computer_sale_and_mainternance,
                        R.drawable.shop_list_icon_cars_oils, R.drawable.shop_list_icon_cars_rubber,
                        R.drawable.shop_list_icon_cars_accessories, R.drawable.shop_list_icon_plumbing_tools,
                        R.drawable.shop_list_icon_shop_electrical_tools, R.drawable.shop_list_icon_car_wash};

        shop_type_list = getResources().getStringArray(R.array.shop_type_list);

        RecyclerView m_recycle_view = v.findViewById(R.id.m_recycle_view);

        m_recycle_view.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3
                , RecyclerView.VERTICAL, false);


        m_recycle_view.setLayoutManager(gridLayoutManager);

        Adapter adapter = new Adapter();

        m_recycle_view.setAdapter(adapter);


        return v;
    }


    public class Adapter extends RecyclerView.Adapter<Adapter.m_viewHolder> {


        @NonNull
        @Override
        public m_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.fragment_shop_list_form, parent, false);

            m_viewHolder viewHolder = new m_viewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull m_viewHolder holder, final int position) {


            holder.profession_text.setText(shop_type_list[position]);
            holder.profession_image.setImageResource(shop_images_list[position]);

            holder.m_card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent openSearch = new Intent(getContext(), SearchActivity.class);
                    openSearch.putExtra("SearchFor", shop_type_list[position]);
                    startActivity(openSearch);

                }
            });


        }

        @Override
        public int getItemCount() {
            return shop_type_list.length;
        }

        public class m_viewHolder extends RecyclerView.ViewHolder {

            ImageView profession_image;
            TextView profession_text;
            CardView m_card_view;

            public m_viewHolder(@NonNull View itemView) {
                super(itemView);

                profession_image = itemView.findViewById(R.id.profession_image);
                profession_text = itemView.findViewById(R.id.profession_text);
                m_card_view = itemView.findViewById(R.id.m_card_view);
            }
        }


    }


}
