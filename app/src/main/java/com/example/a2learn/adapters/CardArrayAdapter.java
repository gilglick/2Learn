package com.example.a2learn.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.a2learn.R;
import com.example.a2learn.model.Card;
import com.example.a2learn.model.Rating;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CardArrayAdapter extends android.widget.ArrayAdapter<Card> {

    public CardArrayAdapter(Context context, int resourceId, List<Card> items) {
        super(context, resourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Card card_item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        TextView askForHelp = convertView.findViewById(R.id.AskHelpTextView);
        TextView offerForHelp = convertView.findViewById(R.id.OfferHelpTextView);
        TextView academeInstitution = convertView.findViewById(R.id.academeInstitution);
        ImageView image = convertView.findViewById(R.id.image);
        RatingBar ratingBar = convertView.findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        if (card_item != null) {
            name.setText(card_item.getStudent().getFullName());
            askForHelp.setText(card_item.getStudent().getUserNeedHelpListStringFormat());
            offerForHelp.setText(card_item.getStudent().getUserOfferListStringFormat());
            academeInstitution.setText(card_item.getStudent().getAcademicInstitution());
            if (!card_item.getStudent().getUri().matches("")) {
                Picasso.get().load(card_item.getStudent().getUri()).into(image);
            } else {
                Picasso.get().load(R.drawable.no_picture).into(image);
            }
            ratingBar.setRating(card_item.getRating().getCurrentRating());
        }

        return convertView;
    }


}
