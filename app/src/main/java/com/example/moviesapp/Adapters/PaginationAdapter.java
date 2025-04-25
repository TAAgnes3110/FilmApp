package com.example.moviesapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moviesapp.R;
import java.util.ArrayList;
import java.util.List;

public class PaginationAdapter extends RecyclerView.Adapter<PaginationAdapter.ViewHolder> {
    private List<String> displayPages;
    private int currentPage;
    private int totalPages;
    private OnPageClickListener listener;
    private static final String PREVIOUS = "PREV";
    private static final String NEXT = "NEXT";

    public interface OnPageClickListener {
        void onPageClick(int page);
    }

    public PaginationAdapter(OnPageClickListener listener) {
        this.displayPages = new ArrayList<>();
        this.listener = listener;
        this.currentPage = 1;
        this.totalPages = 1;
    }

    public void setPages(int totalPages, int currentPage) {
        this.totalPages = Math.max(totalPages, 1);
        this.currentPage = Math.min(Math.max(currentPage, 1), this.totalPages);
        updateDisplayPages();
        notifyDataSetChanged();
    }

    private void updateDisplayPages() {
        displayPages.clear();

        // Thêm nút Previous
        displayPages.add(PREVIOUS);

        // Xác định số bắt đầu dựa trên trang hiện tại
        int startPage;
        if (currentPage <= 3) {
            startPage = 1; // Khi ở trang 1, 2, 3, hiển thị 1, 2, 3, 4, 5
        } else {
            startPage = currentPage - 2; // Hiển thị 5 số, với currentPage ở giữa
        }

        // Điều chỉnh startPage nếu totalPages nhỏ hơn 5
        if (totalPages < 5) {
            startPage = 1; // Nếu totalPages < 5, bắt đầu từ 1
        } else if (startPage + 4 > totalPages) {
            startPage = totalPages - 4; // Đảm bảo không vượt quá totalPages
        }

        // Hiển thị 5 số liên tiếp từ startPage
        int endPage = Math.min(startPage + 4, totalPages);
        for (int i = startPage; i <= endPage; i++) {
            displayPages.add(String.valueOf(i));
        }

        // Thêm nút Next
        displayPages.add(NEXT);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page_number, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String pageText = displayPages.get(position);
        holder.pageNumber.setText(pageText.equals(PREVIOUS) || pageText.equals(NEXT) ? "" : pageText);

        if (pageText.equals(PREVIOUS)) {
            holder.pageNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_previous, 0, 0, 0);
            holder.pageNumber.setEnabled(currentPage > 1);
            holder.itemView.setOnClickListener(v -> {
                if (currentPage > 1) {
                    listener.onPageClick(currentPage - 1);
                }
            });
        } else if (pageText.equals(NEXT)) {
            holder.pageNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_next, 0);
            holder.pageNumber.setEnabled(currentPage < totalPages);
            holder.itemView.setOnClickListener(v -> {
                if (currentPage < totalPages) {
                    listener.onPageClick(currentPage + 1);
                }
            });
        } else {
            int page = Integer.parseInt(pageText);
            holder.pageNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.pageNumber.setSelected(page == currentPage);
            holder.pageNumber.setEnabled(true);
            holder.itemView.setOnClickListener(v -> listener.onPageClick(page));
        }
    }

    @Override
    public int getItemCount() {
        return displayPages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pageNumber;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            pageNumber = itemView.findViewById(R.id.page_number);
        }
    }
}