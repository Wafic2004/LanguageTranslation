package com.ahmed.languagetranslation;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LanguageRecyclerAdapter extends RecyclerView.Adapter<LanguageRecyclerAdapter.LanguageRecyclerViewHolder>{
    //list of class objects
    List<SharedPreferenceDataValues> listOfConversationObject;

    LanguageRecyclerAdapter (List<SharedPreferenceDataValues> listOfConversationObject) {
        this.listOfConversationObject = new ArrayList<>();
        this.listOfConversationObject = listOfConversationObject;
    }

    @NonNull
    @Override
    public LanguageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout to a view and sending it to the viewHolder class
        Context parentContext = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(parentContext);
        View languageView = layoutInflater.inflate(R.layout.layout_for_items, parent, false);
        Log.d("TestLog", "after inflating");
        return new LanguageRecyclerViewHolder(languageView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageRecyclerViewHolder holder, int position) {
        final int[] firstConversationSwitch = {0};
        final int[] secondConversationSwitch = {0};
        String firstConversationFromText = listOfConversationObject.get(position).getFirstConversationFrom();
        String firstConversationToText = listOfConversationObject.get(position).getFirstConversationTo();
        String secondConversationFromText = listOfConversationObject.get(position).getSecondConversationFrom();
        String secondConversationToText = listOfConversationObject.get(position).getSecondConversationTo();

        holder.firstMultiTextBox.setText(firstConversationFromText);
        holder.secondMultiTextBox.setText(secondConversationToText);

        holder.firstTranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstConversationSwitch[0] == 0) {
                    holder.firstMultiTextBox.setText(firstConversationToText);
                    firstConversationSwitch[0] = 1;
                } else {
                    holder.firstMultiTextBox.setText(firstConversationFromText);
                    firstConversationSwitch[0] = 0;
                }
            }
        });

        holder.secondTranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(secondConversationSwitch[0] == 0) {
                    holder.secondMultiTextBox.setText(secondConversationFromText);
                    secondConversationSwitch[0] = 1;
                } else {
                    holder.secondMultiTextBox.setText(secondConversationToText);
                    secondConversationSwitch[0] = 0;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfConversationObject.size();
    }

    public static class LanguageRecyclerViewHolder extends RecyclerView.ViewHolder {
        EditText firstMultiTextBox;
        EditText secondMultiTextBox;
        ImageButton firstTranslateButton;
        ImageButton secondTranslateButton;
        public LanguageRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            firstMultiTextBox = itemView.findViewById(R.id.firstConversation);
            secondMultiTextBox = itemView.findViewById(R.id.secondConversation);
            firstTranslateButton = itemView.findViewById(R.id.firstConversationButton);
            secondTranslateButton = itemView.findViewById(R.id.secondConversationButton);
        }
    }
}
