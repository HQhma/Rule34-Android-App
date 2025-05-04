package com.HQHMA.rule34;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.HQHMA.rule34.Adapters.RecyclerViewAdapterTagsForItems;
import com.HQHMA.rule34.Adapters.RecyclerViewHeightLimitedEdition;
import com.HQHMA.rule34.Models.Tag;
import com.HQHMA.rule34.Utilities.Utilities;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public FeedFragment feedFragment;
    FavoritesFragment favoritesFragment;
    public CollectionFragment collectionFragment;
    public BottomNavigationView buttonNavView;

    FragmentManager fragmentManager;
    Fragment lastFragment;

    private BottomSheetDialog mBottomSheetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //AndroidBug5497Workaround.assistActivity(this);

        buttonNavView = findViewById(R.id.buttonNavView);

        fragmentManager = getSupportFragmentManager();
        feedFragment = new FeedFragment();
        favoritesFragment = new FavoritesFragment();
        collectionFragment = new CollectionFragment();

        buttonNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.menu_Feed){
                    lastFragment = Utilities.openFragment(fragmentManager,R.id.frame_Layout,feedFragment,lastFragment);
                } else if (id == R.id.menu_Favorites) {
                    lastFragment = Utilities.openFragment(fragmentManager,R.id.frame_Layout,favoritesFragment,lastFragment);
                } else if (id == R.id.menu_Collection) {
                    collectionFragment = new CollectionFragment(); // update every time
                    lastFragment = Utilities.openFragment(fragmentManager,R.id.frame_Layout,collectionFragment,lastFragment);
                }
                return true;
            }
        });

        buttonNavView.setSelectedItemId(R.id.menu_Feed);

    }

    public void showBottomDialog(ArrayList<Tag> tags){
        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);

        FlexboxLayoutManager layoutManagerFlex = new FlexboxLayoutManager(this);
        layoutManagerFlex.setFlexDirection(FlexDirection.ROW);
        layoutManagerFlex.setJustifyContent(JustifyContent.CENTER);

        RecyclerViewHeightLimitedEdition recyclerView = (RecyclerViewHeightLimitedEdition) bottomSheetLayout.findViewById(R.id.tagsRecyclerView);
        recyclerView.setLayoutManager(layoutManagerFlex);

        RecyclerViewAdapterTagsForItems mAdapterTags = new RecyclerViewAdapterTagsForItems(tags,this,feedFragment,recyclerView);

        recyclerView.setAdapter(mAdapterTags);
        recyclerView.setVisibility(View.VISIBLE);

        (bottomSheetLayout.findViewById(R.id.closeBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        mBottomSheetDialog.setCancelable(false);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);
        mBottomSheetDialog.show();
    }
}