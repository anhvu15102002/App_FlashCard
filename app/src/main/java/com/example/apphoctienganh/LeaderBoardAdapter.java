package com.example.apphoctienganh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderBoardAdapter extends BaseAdapter {
    private List<UserPoint> list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public LeaderBoardAdapter(Context context, List<UserPoint> list) {
        this.context = context;
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Sắp xếp danh sách: Điểm cao nhất trước, nếu điểm bằng nhau thì thời gian ngắn hơn trước
        Collections.sort(list, new Comparator<UserPoint>() {
            @Override
            public int compare(UserPoint user1, UserPoint user2) {
                // So sánh điểm số (cao xuống thấp)
                int compareResult = Integer.compare(user2.getPoints(), user1.getPoints());
                if (compareResult == 0) {
                    // Nếu điểm bằng nhau, so sánh thời gian (ngắn hơn xếp trước)
                    int time1 = convertTimeStringToSeconds(user1.getTime());
                    int time2 = convertTimeStringToSeconds(user2.getTime());
                    return Integer.compare(time1, time2); // Thời gian ngắn hơn xếp trước
                }
                return compareResult;
            }
        });
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

    static class ViewHolder {
        TextView txtUsername;
        TextView txtScore;
        TextView txtTime;
        ImageView rank;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.leaderboardlayout, parent, false);
            holder = new ViewHolder();
            holder.txtUsername = convertView.findViewById(R.id.text_username);
            holder.txtScore = convertView.findViewById(R.id.text_score);
            holder.txtTime = convertView.findViewById(R.id.text_time);
            holder.rank = convertView.findViewById(R.id.image_leaderboard);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Đặt ảnh xếp hạng dựa trên vị trí
        setRankImage(holder.rank, position);

        // Đặt thông tin người dùng
        UserPoint userPoint = list.get(position);
        holder.txtUsername.setText(userPoint.getUser());
        holder.txtScore.setText("Điểm số: " + userPoint.getPoints());

        // Định dạng thời gian
        String formattedTime = formatTime(userPoint.getTime());
        holder.txtTime.setText("Thời gian: " + formattedTime);

        return convertView;
    }

    private void setRankImage(ImageView imageView, int position) {
        switch (position) {
            case 0:
                imageView.setImageResource(R.drawable.top1);
                break;
            case 1:
                imageView.setImageResource(R.drawable.top2);
                break;
            case 2:
                imageView.setImageResource(R.drawable.top3);
                break;
            case 3:
                imageView.setImageResource(R.drawable.top4);
                break;
            case 4:
                imageView.setImageResource(R.drawable.top5);
                break;
            default:
                imageView.setImageResource(R.drawable.default1);
                break;
        }
    }

    private String formatTime(String timeString) {
        try {
            String[] parts = timeString.split(":");
            if (parts.length == 2) {
                return String.format("%02d:%02d", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
            return timeString; // Trả về chuỗi gốc nếu không đúng định dạng
        } catch (NumberFormatException e) {
            return timeString; // Trả về chuỗi gốc nếu không thể chuyển đổi
        }
    }

    private int convertTimeStringToSeconds(String timeString) {
        try {
            String[] parts = timeString.split(":");
            if (parts.length == 2) {
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                return minutes * 60 + seconds; // Chuyển đổi thành số giây
            }
            return Integer.MAX_VALUE; // Trả về giá trị lớn nếu không đúng định dạng
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // Trả về giá trị lớn nếu không thể chuyển đổi
        }
    }
}