package com.example.apphoctienganh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private ArrayList<ReminderItem> reminderList;
    private Context context;

    public ReminderAdapter(ArrayList<ReminderItem> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderItem item = reminderList.get(position);
        holder.timeTextView.setText(item.getTime());
        holder.activeSwitch.setChecked(item.isActive());

        holder.activeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setActive(isChecked);
            ReminderDatabaseHelper dbHelper = new ReminderDatabaseHelper(context);
            dbHelper.updateReminderStatus(item.getId(), isChecked);
        });

        holder.deleteButton.setOnClickListener(v -> {
            int itemPosition = holder.getAdapterPosition();
            if (itemPosition != RecyclerView.NO_POSITION) {
                int reminderId = reminderList.get(itemPosition).getId();
                ((StudyReminderActivity) context).deleteReminder(reminderId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        Switch activeSwitch;
        ImageButton deleteButton;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.textViewTime);
            activeSwitch = itemView.findViewById(R.id.switchActive);
            deleteButton = itemView.findViewById(R.id.buttonDelete);
        }
    }
}