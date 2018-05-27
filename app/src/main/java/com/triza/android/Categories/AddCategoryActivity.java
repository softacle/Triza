package com.triza.android.Categories;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.triza.android.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddCategoryActivity extends AppCompatActivity implements AddCategoryFragment.OnAddCatFragmentContinueListener, SubCategoryFragment.OnAddSubCatFragmentInteractionListener{



    private  Categories mCategory;
    private  SubCategories mSubCategory;

    private ProgressBar cat_saving_prgBr;

    private  Uri mSelectedImageUrl;

    StorageReference photoRef;

    private static final int RC_PHOTO_PICKER = 101;
    //firebase variable
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCategoriesDatabaseReference;
    private DatabaseReference mSubCategoriesDatabaseReference;
	private FirebaseStorage mFirebaseStorage;
	private StorageReference mStorageReference;

	AddCategoryFragment addCategoryFragment;
	SubCategoryFragment subCategoryFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        cat_saving_prgBr = findViewById(R.id.cat_saving_progress_bar);


        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.newCatFragmentHolder) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of AddCategoryFragment
            addCategoryFragment = new AddCategoryFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            addCategoryFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'newCatFragmentHolder' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.newCatFragmentHolder, addCategoryFragment).commit();
        }


    }

    public  void imagePicker(View view){
        Intent intent  = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"),AddCategoryActivity.RC_PHOTO_PICKER);

    }

    //addNewCaegory button on fragment_add_category clicked
    public  void addNewCategory(View view){
        addCategoryFragment.onContinueButtonPressed();
    }
    //saveToFirebase button on fragment_sub_category clicked
    public  void saveToFirebase(View view){
        cat_saving_prgBr.setVisibility(View.VISIBLE);
        subCategoryFragment.onSaveButtonPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_PHOTO_PICKER){
            if(resultCode == RESULT_OK){
                mSelectedImageUrl = data.getData();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Categories category) {
        mCategory =  category;

        //TODO:start sub category fragment

//         subCategoryFragment = getSupportFragmentManager().findFragmentById(R.id.sub_category_fragment);

//        if (subCategoryFragment != null) {
            // If article frag is available, we're in two-pane layout...

            // Call a method in the ArticleFragment to update its content
//            subCategoryFragment.updateArticleView(position);

//        } else {
            // If the frag is not available, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            subCategoryFragment = new SubCategoryFragment();
            Bundle args = new Bundle();
            args.putString(SubCategoryFragment.ARG_CAT_TITLE, category.getCatTitle());
            args.putString(SubCategoryFragment.ARG_CAT_IMAGE_URL, mSelectedImageUrl.toString());//get the local image
			subCategoryFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.newCatFragmentHolder, subCategoryFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
//        }


    }


    public void saveCategoryToFirebase(){

        //instantiate the firebase variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCategoriesDatabaseReference = mFirebaseDatabase.getReference().child("categories");

        mSubCategoriesDatabaseReference = mFirebaseDatabase.getReference().child("sub_categories");
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("categories_images");
        //custom name
        String dateStamp= new SimpleDateFormat("dd-mm-yyyy HH:mm:ss:SSS").format(new Date()).toString();
        //photoRef = mStorageReference.child(selectedimageUrl.getLastPathSegment());
        photoRef = mStorageReference.child("category_"+dateStamp);
        //upload file to firebase storage
        photoRef.putFile(mSelectedImageUrl).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    cat_saving_prgBr.setVisibility(View.GONE);
                    Toast.makeText(AddCategoryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();

                    throw task.getException();
                }

                //continue with the task to get the download url
                return  photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()){
                    Uri imageUrl = task.getResult();
                    mCategory.setCatImageUrl(imageUrl.toString());

                    SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
                    f.setLenient(false);
                    try {
                        mCategory.catDateAdded = f.parse(f.format(new Date())).getTime();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                   // mCategoriesDatabaseReference.push().setValue(mCategory);
                    //add empty data and get the pushId/key
                    String catId = mCategoriesDatabaseReference.push().getKey();
                    mSubCategory.setCatId(catId);
                    mCategory.setCatId(catId);
                    //creat a child using the created/gotten (pushId) key of the empty data created
                    mCategoriesDatabaseReference.child(catId).setValue(mCategory);


                    //save sub_category
                    mSubCategoriesDatabaseReference.push().setValue(mSubCategory);
                    Toast.makeText(AddCategoryActivity.this, mCategory.getCatTitle()+" added to category", Toast.LENGTH_LONG).show();
                    cat_saving_prgBr.setVisibility(View.GONE);
                            finish();
                }
                else{
                    //handle failure
                    cat_saving_prgBr.setVisibility(View.GONE);
                    Toast.makeText(AddCategoryActivity.this, "Something went wrong. Please try again!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    @Override
    public void onAddSubCatFragmentInteraction(SubCategories subCategory) {
        mSubCategory = subCategory;

        //save to database
        saveCategoryToFirebase();

    }
}
