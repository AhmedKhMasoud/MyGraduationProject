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


public class FragmentProfessionList extends Fragment {

    int profession_images_list[];
    String profession_type_list[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = getLayoutInflater().inflate(R.layout.fragment_profession_list, container, false);

        profession_images_list = new int[]
                {R.drawable.profession_list_icon_contractor, R.drawable.profession_list_icon_armed_carpenter,
                        R.drawable.profession_list_icon_architecit, R.drawable.profession_list_icon_smith,
                        R.drawable.profession_list_icon_bleached_conch, R.drawable.profession_list_icon_plumber,
                        R.drawable.profession_list_icon_electrician, R.drawable.profession_list_icon_carpenter,
                        R.drawable.profession_list_icon_astorge, R.drawable.profession_list_icon_engraving,
                        R.drawable.profession_list_icon_ceramic_tiles, R.drawable.profession_list_icon_pharaonic_stone_tiles,
                        R.drawable.profession_list_icon_marble_tiles, R.drawable.profession_list_icon_lift_winch,
                        R.drawable.profession_list_icon_looder, R.drawable.profession_list_icon_tractor,
                        R.drawable.profession_list_icon_car_electrician, R.drawable.profession_list_icon_mechanical,
                        R.drawable.profession_list_icon_car_junk, R.drawable.profession_list_icon_rubber_changed,
                        R.drawable.profession_list_icon_oil_changed, R.drawable.profession_list_icon_winch};

        profession_type_list = getResources().getStringArray(R.array.profession_type_list);

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
        public Adapter.m_viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.fragment_profession_list_form, parent, false);

            m_viewHolder viewHolder = new m_viewHolder(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull m_viewHolder holder, final int position) {


            holder.profession_text.setText(profession_type_list[position]);
            holder.profession_image.setImageResource(profession_images_list[position]);

            holder.m_card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent openSearch = new Intent(getContext(), SearchActivity.class);
                    openSearch.putExtra("SearchFor", profession_type_list[position]);
                    startActivity(openSearch);
                }
            });

        }

        @Override
        public int getItemCount() {
            return profession_images_list.length;
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
