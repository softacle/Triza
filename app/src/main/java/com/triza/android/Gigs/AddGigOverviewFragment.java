package com.triza.android.Gigs;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ryanpope.tagedittext.TagEditText;
import com.triza.android.Dialogs.TagEntryInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.triza.android.Categories.Categories;
import com.triza.android.Categories.SubCategories;
import com.triza.android.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddGigOverviewFragment.OnAddGigOverviewListener} interface
 * to handle interaction events.
 * Use the {@link AddGigOverviewFragment} factory method to
 * create an instance of this fragment.
 */
public class AddGigOverviewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private String gig_title, gig_desc = "", category_id = "", sub_cat_id = "", search_tag = "";

    private EditText gigTitle_editText;
    private OnAddGigOverviewListener mListener;
    TextView titleTextCount;
    TagEditText searchTagEditText;
    ImageView tagInfo;

    ArrayAdapter<Categories> categoryAdapter;
    ArrayAdapter<SubCategories> subCategoryAdapter;
    private Spinner catSpinner;
    private Spinner subCatSpinner;
    //firebase variable
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCategoriesDatabaseReference;
    private DatabaseReference mSubCategoriesDatabaseReference;


    public AddGigOverviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_gig_overview_fragment, container, false);
        titleTextCount = view.findViewById(R.id.title_text_count);
        gigTitle_editText = view.findViewById(R.id.gig_title_editText);
        searchTagEditText = view.findViewById(R.id.search_tag_editText);
        searchTagEditText.getText().toString();
        tagInfo = view.findViewById(R.id.tag_info);
        tagInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TagEntryInfo dialog = new TagEntryInfo();

                /*this help to target this particular fragment in  the main activity*/
                dialog.setTargetFragment(AddGigOverviewFragment.this, 0);


                dialog.show(getFragmentManager(), "123");
            }
        });


        //programatically i set the text counter by using text watcher and attached to editetxt
        titleTextCount.setText(0 + "/70 max");
        titleTextCount.setTextColor(Color.rgb(0, 150, 136));



        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
               String titleListener = gigTitle_editText.getText().toString();
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.blinking);
                if (titleListener.length() <= 60) {
                    titleTextCount.setText(titleListener.length() + "/70 max");
                    titleTextCount.setTextColor(Color.rgb(0, 150, 136));

                }

                if (titleListener.length() > 60) {
                    titleTextCount.setText(titleListener.length() + "/70 max");
                    titleTextCount.setTextColor(Color.rgb(29, 36, 228));
                }
                if (titleListener.length() > 70) {
                    titleTextCount.setText(70 - titleListener.length() + "/70 max");
                    titleTextCount.setTextColor(Color.parseColor("RED"));
                    titleTextCount.startAnimation(anim);
                }

            }
        };
        gigTitle_editText.addTextChangedListener(textWatcher);

        final View viewHome = inflater.inflate(R.layout.fragment_add_gig_overview_fragment, container, false);

//        cat_spinner = view.findViewById(R.id.sub_cat_spinener);
        //instantiate the firebase variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCategoriesDatabaseReference = mFirebaseDatabase.getReference().child("categories");
        mSubCategoriesDatabaseReference = mFirebaseDatabase.getReference().child("sub_categories");

//        final ArrayList categories = new ArrayList();
        mCategoriesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<Categories> categories = new ArrayList<Categories>();

                for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                    Categories areaName = areaSnapshot.getValue(Categories.class);
                    categories.add(areaName);
                }

                catSpinner = (Spinner) viewHome.findViewById(R.id.category_spinner);
                categoryAdapter = new ArrayAdapter<Categories>(getActivity(), android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                catSpinner.setAdapter(categoryAdapter);

                catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        // Here you get the current item (a Categories object) that is selected by its position
                        Categories category = categoryAdapter.getItem(position);
                        // Here you can do the action you want to...
                        category_id = category.getCatId();

                        getSubCategories(category_id, viewHome);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gigTitle_editText = viewHome.findViewById(R.id.gig_title_editText);


        return viewHome;
    }

    public void getSubCategories(String category_id, final View viewHome) {
        mSubCategoriesDatabaseReference.orderByChild("catId").equalTo(category_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<SubCategories> subCategories = new ArrayList<SubCategories>();

                for (DataSnapshot subCatSnapshot : dataSnapshot.getChildren()) {
                    SubCategories subCategory = subCatSnapshot.getValue(SubCategories.class);
                    subCategories.add(subCategory);
                }

                Spinner subCatSpinner = (Spinner) viewHome.findViewById(R.id.sub_cat_spinner);
                final ArrayAdapter<SubCategories> subCategoryAdapter = new ArrayAdapter<SubCategories>(getActivity(), android.R.layout.simple_spinner_item, subCategories);
                subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subCatSpinner.setAdapter(subCategoryAdapter);

                subCatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view,
                                               int position, long id) {
                        SubCategories subCategory = subCategoryAdapter.getItem(position);
                        sub_cat_id = subCategory.getCatId();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void onSaveOverviewButtonPressed() {
        gig_title = gigTitle_editText.getText().toString();
        if (mListener != null) {
            mListener.onAddGigOverview(gig_title, category_id, sub_cat_id, search_tag);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddGigOverviewListener) {
            mListener = (OnAddGigOverviewListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnAddGigOverviewListener {
        void onAddGigOverview(String gig_title, String category_id, String sub_cat_id, String search_tag);
    }





}
