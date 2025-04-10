package com.example.apphoctienganh;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apphoctienganh.VocabularyDbHelper;

import java.util.List;

public class NewVocabularyAdapter extends RecyclerView.Adapter<NewVocabularyAdapter.VocabularyViewHolder> {

    private List<VocabularyItem> vocabularyItems;
    private VocabularyDbHelper dbHelper;

    public NewVocabularyAdapter(List<VocabularyItem> vocabularyItems, VocabularyDbHelper dbHelper) {
        this.vocabularyItems = vocabularyItems;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vocabulary, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        VocabularyItem item = vocabularyItems.get(position);
        holder.tvEnglishWord.setText(item.getEnglishWord());
        holder.tvVietnameseMeaning.setText(item.getVietnameseMeaning());

        // Đảm bảo ImageView có kích thước phù hợp
        holder.ivVocabularyImage.getLayoutParams().width = 200;
        holder.ivVocabularyImage.getLayoutParams().height = 200;

        // Tải hình ảnh với placeholders rõ ràng
        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .centerCrop()
                .placeholder(android.R.drawable.ic_menu_gallery) // Hiển thị khi đang tải
                .error(android.R.drawable.ic_menu_report_image) // Hiển thị khi lỗi
                .into(holder.ivVocabularyImage);

        // Thêm sự kiện xóa khi nhấn giữ
        holder.itemView.setOnLongClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Context context = holder.itemView.getContext();

                // Hiển thị hộp thoại xác nhận
                new AlertDialog.Builder(context)
                        .setTitle("Xóa từ vựng")
                        .setMessage("Bạn có chắc chắn muốn xóa từ vựng này?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            VocabularyItem itemToDelete = vocabularyItems.get(adapterPosition);
                            dbHelper.deleteVocabulary(itemToDelete.getEnglishWord());

                            // Xóa khỏi danh sách và cập nhật adapter
                            vocabularyItems.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);

                            Toast.makeText(context, "Đã xóa từ vựng", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Không", null)
                        .show();
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return vocabularyItems.size();
    }

    static class VocabularyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVocabularyImage;
        TextView tvEnglishWord;
        TextView tvVietnameseMeaning;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivVocabularyImage = itemView.findViewById(R.id.ivVocabularyImage);
            tvEnglishWord = itemView.findViewById(R.id.tvEnglishWord);
            tvVietnameseMeaning = itemView.findViewById(R.id.tvVietnameseMeaning);
        }
    }
}