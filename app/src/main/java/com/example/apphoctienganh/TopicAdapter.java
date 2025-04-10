package com.example.apphoctienganh;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TopicAdapter extends BaseAdapter {
    private List<TopicModel> list = new ArrayList<>();
    private Context context;

    public TopicAdapter(Context context, List<TopicModel> list) {
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
        View itemView = inflater.inflate(R.layout.layoutopic, parent, false);
        TextView textVocabulary = itemView.findViewById(R.id.text_Topic);
        ImageView image = itemView.findViewById(R.id.image_vocabulary);
        TopicModel topicModel = list.get(position);
        textVocabulary.setText("Chủ đề: " + topicModel.getTopic());
        image.setImageResource(topicModel.getImageView());
        return itemView;
    }
}
