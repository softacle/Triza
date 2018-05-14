package com.triza.android.Home;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.triza.android.Adapters.GigsAdapterHorizontal;
import com.triza.android.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ViewPager viewPager;
    TabLayout tabLayout;
    MyViewPagerAdapter myvpAdapter;
    int[] image;
    String[] imageDesc;
    TextView allCat;
    TextView prog;
    TextView video;
    TextView music;
    TextView buss;
    TextView imageDescription;
    TextView imageDescrptionExtra;
    Context context = getActivity();
    int timer = 5000; //in milliseconds
    int page = 0;
    Handler handler;
    RecyclerView recyclerView;
    ArrayList<Gigs> gigList;
    GigsAdapterHorizontal gigAdapter;
    LinearLayoutManager linearLayoutManager;
    DividerItemDecoration itemDecoration;

    // Runnable to help auto swtch my pageviewer

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (myvpAdapter.getCount() == page) {
                page = 0;
            } else {
                page++;
            }
            viewPager.setCurrentItem(page, true);
            handler.postDelayed(this, timer);
        }
    };
g


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        handler = new Handler(); //Initializing the handler

//        These are dummy set of images in place of oncloud images
        image = new int[]{R.drawable.desert, R.drawable.a, R.drawable.b,
                R.drawable.c, R.drawable.d, R.drawable.desert, R.drawable.e, R.drawable.f};

//        These are dummy set of image decsription in place of oncloud descriptions
        imageDesc = new String[]{"PROGRAMMING & TECH", "GRAPHICS & DESIGN", "DIGITAL MARKETING", "WRITING & TRANSLATION", "VIDEO & ANIMATION",
                "MUSIC & AUDIO", "BUSINESS & ACCOUNTING", "FUN & LIFESTYLE"};


        gigList = new ArrayList<>(); //Ths is where i instanciated my custom class and recycler adapter

        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false); //Layout manager in charge of horizontal recycler view

        dummyData(); //Method that holds my dummyDatas for development purpose
    }

    private void dummyData() {
        gigList.add(new Gigs("url", "I can develop android application from scratch", 3.5, 20, 7000, true));
        gigList.add(new Gigs("url", "Hoola me for SEO, amma dig it deep for you", 4, 70, 800, false));
        gigList.add(new Gigs("url", "I create mind blowing graphc logos", 2.5, 20000, 5000, true));
        gigList.add(new Gigs("url", "I can develop android application from scratch", 3.5, 20, 7000, false));
        gigList.add(new Gigs("url", "I develop create material contents ", 0, 40, 10000, true));
    } // Adding a dummy value to my custom class

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        //Referencing all objects from xml
        allCat = view.findViewById(R.id.all_cat);
        allCat.setText("All Categories");
        prog = view.findViewById(R.id.prog);
        prog.setText("Programming & Tech");
        video = view.findViewById(R.id.video);
        video.setText("Video & Animation");
        music = view.findViewById(R.id.music);
        music.setText("Music & Audio");
        buss = view.findViewById(R.id.buss);
        buss.setText("Business & Accounting");
        viewPager = view.findViewById(R.id.home_view_pager); //Referencing viewpager here
        tabLayout = view.findViewById(R.id.tab_dots); //Referencing the tablayout here
        tabLayout.setupWithViewPager(viewPager, true);
        myvpAdapter = new MyViewPagerAdapter(context, image);
        viewPager.setAdapter(myvpAdapter); //setting the viewpager adapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener); //implementing viewpagerchange listener method
        imageDescription = view.findViewById(R.id.image_desc); //referncing image decs
        imageDescription.setText("PROGRAMMING & TECH");// A DEFAULT TEXT BEFORE CHANGELISTENER IS CALLED
        imageDescrptionExtra = view.findViewById(R.id.image_desc_extra); //Referncing image decs extra here
        imageDescrptionExtra.setText("Make it work for you on triza");

        //Wiring up the recyclerview to populate
        recyclerView = view.findViewById(R.id.recycler_view_featured);
        recyclerView.setLayoutManager(linearLayoutManager);  //I set manager for recycler here
        recyclerView.setItemAnimator(new DefaultItemAnimator()); //Anmator for recycler view
        //itemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        gigAdapter = new GigsAdapterHorizontal(getActivity(), gigList); //My adapter in charge of recycler view
        //recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(gigAdapter); //i set the adapter on recycler here


        return view;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            imageDescription.setText(imageDesc[position]);
            page = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        Context context;
        int[] images;
        LayoutInflater layoutInflater;

        public MyViewPagerAdapter(Context context, int[] images) {
            this.context = context;
            this.images = images;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView;
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pager_item, container, false);
            imageView = view.findViewById(R.id.image_view); //getting reference to mage in the view
            imageView.setImageResource(images[position]); //setting images by position on imageview
            container.addView(view); //adding pagelayout to the current page of the viewpager
            return view;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }

        @Override
        public boolean isViewFromObject(View v, Object object) {
            return v == object;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, timer);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
