package com.triza.android.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.triza.android.Favorites.FavoritesFragment;
import com.triza.android.Favorites.Favourites;
import com.triza.android.Gigs.Gigs;
import com.triza.android.R;

import java.util.List;


public class FavouritesAdapterVertical extends RecyclerView.Adapter<FavouritesAdapterVertical.MyViewHolder> {

    private Context mContext;
    public static List<Favourites> favList;
    FavoritesFragment favoritesFragment;

    DataSnapshot favouritesDataSnapShot;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mFavouritesDatabaseReference;
    private String user_id = "muib";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView gigTitle, gigRating, gigNoReview, gigPrice;
        public ImageView gigImage, gigFavorite, gigOption;
        public RelativeLayout viewBackground;
        public LinearLayout viewForeground;
        public FrameLayout frameList;

        public MyViewHolder(View view) {
            super(view);
            gigTitle = view.findViewById(R.id.gig_title);
            gigRating = view.findViewById(R.id.gig_rating);
            gigNoReview = view.findViewById(R.id.gig_no_review);
            gigPrice = view.findViewById(R.id.gig_price);
            gigImage = view.findViewById(R.id.gig_image);
            gigFavorite = view.findViewById(R.id.gig_favorite);
            gigOption = view.findViewById(R.id.gig_option);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
            frameList = view.findViewById(R.id.frame_list);


        }


    }


    public FavouritesAdapterVertical(Context mContext, List<Favourites> favList) {
        this.mContext = mContext;
        this.favList = favList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gig_vertical, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, viewHolder.getAdapterPosition() + "", Snackbar.LENGTH_SHORT).show();

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final FavouritesAdapterVertical.MyViewHolder holder, final int position) {

        final Favourites favourite = favList.get(position);// Gets the item position
        final Gigs gig = favourite.getGig();
        holder.gigTitle.setText(gig.getGigTitle());
        holder.gigRating.setText(gig.getGigRating() + "");
        holder.gigNoReview.setText("(" + gig.getGigNoReview() + " reviews)");
        holder.gigPrice.setText("Min Price: " + gig.getMinPrice());
        holder.gigOption.setImageResource(R.drawable.ic_more_vert_black_25dp);
        holder.gigFavorite.setImageResource(R.drawable.ic_favorite_accent_25dp);

        //Instanciate firebase variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFavouritesDatabaseReference = mFirebaseDatabase.getReference().child("favourites");


        // loading image using Glide library
        Glide.with(mContext).load(gig.getGigImageUrl()).into(holder.gigImage);

        holder.gigOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.gigOption);
            }
        });

        setFav(holder, position, gig);
    }

    private void setFav(MyViewHolder holder, final int position, final Gigs gigs) {
        holder.gigFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mFavouritesDatabaseReference.orderByChild("filter_index").equalTo(user_id + "_" + gigs.getGigId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    for (DataSnapshot favGigSnapshot : dataSnapshot.getChildren()) {
                                        favGigSnapshot.getRef().removeValue();
                                        favoritesFragment = new FavoritesFragment();
                                        removeItem(position, favoritesFragment.emptyFavorites, favoritesFragment.deleteAll);
                                        Toast.makeText(mContext, gigs.getGigTitle() + " removed from favourites", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }
        });
    }


    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.gig_more, popup.getMenu());
        popup.setOnMenuItemClickListener(new FavouritesAdapterVertical.MyMenuItemClickListener());
        popup.show();
    }

    public void removeItem(int position, View view, View deleteButton) {
        favList.remove(position);
        if (favList.size() == 0) {
            view.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
        }
        notifyItemRemoved(position);

    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    public void restoreItem(Favourites fav, int position, View emptyView, View deleteAll) {
        favList.add(position, fav);
        emptyView.setVisibility(View.GONE);
        deleteAll.setVisibility(View.VISIBLE);

    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.seller_profile:
                    Toast.makeText(mContext, "Seller's profile", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.share_gig:
                    Toast.makeText(mContext, "Share gig", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.report_gig:
                    Toast.makeText(mContext, "Report gig", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }

    }

    public void removeAllItem(View emptyView, View deleteIcon) {
        favList.clear();
        emptyView.setVisibility(View.VISIBLE);
        deleteIcon.setVisibility(View.GONE);
        notifyDataSetChanged();
    }


}
