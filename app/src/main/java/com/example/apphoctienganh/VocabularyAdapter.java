package com.example.apphoctienganh;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class VocabularyAdapter extends BaseAdapter {
    private List<Vocabulary> list = new ArrayList<>();
    private Context context;

    public VocabularyAdapter(Context context, List<Vocabulary> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.layout_custom_vocabulary, parent, false);
        TextView textVocabulary = itemView.findViewById(R.id.text_Vocabulary);
        TextView textVocabularyAnswer = itemView.findViewById(R.id.text_Answer);
        ImageView iamge = itemView.findViewById(R.id.image_vocabulary);
        Vocabulary topicModel = list.get(position); // Check if topicModel is null
        textVocabulary.setText("Từ vựng: " + topicModel.getWords());
        textVocabularyAnswer.setText("Đáp án: " + topicModel.getAnswer());
        Picasso.get()
                .load(topicModel.getImage())
                .into(iamge);
        return itemView;
    }

}
