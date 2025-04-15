package com.example.moviesapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Adapters.DateAdapter;
import com.example.moviesapp.Adapters.SeatAdapter;
import com.example.moviesapp.Adapters.TimeAdapter;
import com.example.moviesapp.Api.SeatCinemaFilmRepo;
import com.example.moviesapp.Api.SeatResponse;
import com.example.moviesapp.Api.TimeCinemaFilmRepo;
import com.example.moviesapp.data.model.SeatItem;
import com.example.moviesapp.data.model.ShowtimeItem;
import com.example.moviesapp.R;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    private RecyclerView dateRecyclerView, timeRecyclerView, seatRecyclerView;
    private DateAdapter dateAdapter;
    private TimeAdapter timeAdapter;
    private SeatAdapter seatAdapter;
    private TextView nameTicketMovie;
    private LocalDate selectedDate;
    private String selectedTime;
    private Button payButton;
    private TextView selectedDateTextView;
    private TextView totalPriceTextView;
    private View screenIndicator;
    private LinearLayout seatLegend;
    private TextView screenLabel;
//    private DatabaseReference ticketsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        ticketsRef = database.getReference("tickets");

        initViews();
        setupDateRecyclerView();
        setupTimeRecyclerView();
        setupSeatRecyclerView();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("movieName")) {
            String movieTitle = intent.getStringExtra("movieName");
            nameTicketMovie.setText(movieTitle);
        } else {
            nameTicketMovie.setText("No movie selected");
        }
    }

    private void setupDateRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);

        List<LocalDate> dateList = generateDateList();
        dateAdapter = new DateAdapter(dateList, selectedDate -> {
            this.selectedDate = selectedDate;
            Log.d("BookingActivity", "Selected date: " + selectedDate.toString());
            Toast.makeText(this, "Ngày chọn: " + selectedDate.toString(), Toast.LENGTH_SHORT).show();
            updateTimeRecyclerView();
        });

        dateRecyclerView.setAdapter(dateAdapter);
    }

    private void setupTimeRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        timeRecyclerView.setLayoutManager(layoutManager);

        List<String> timeList = new ArrayList<>();
        timeAdapter = new TimeAdapter(timeList, selectedTime -> {
            this.selectedTime = selectedTime;
            Toast.makeText(this, "Khung giờ chọn: " + selectedTime, Toast.LENGTH_SHORT).show();
            updateSeatRecyclerView(selectedTime);
            screenIndicator.setVisibility(View.VISIBLE);
            screenLabel.setVisibility(View.VISIBLE);
            seatLegend.setVisibility(View.VISIBLE);
        });
        timeRecyclerView.setAdapter(timeAdapter);
    }

    private void setupSeatRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 8);
        seatRecyclerView.setLayoutManager(layoutManager);

        List<SeatItem> seatList = new ArrayList<>();
        seatAdapter = new SeatAdapter(seatList, selectedSeat -> {
            Log.d("BookingActivity", "Seat clicked: " + selectedSeat);
            Toast.makeText(this, "Ghế chọn: " + selectedSeat, Toast.LENGTH_SHORT).show();
            List<String> selectedSeats = seatAdapter.getSelectedSeats();
            Log.d("BookingActivity", "Selected seats: " + selectedSeats.toString());
            int totalPrice = selectedSeats.size() * 120000;
            totalPriceTextView.setText("Tổng tiền: " + String.format("%,d VNĐ", totalPrice));
            Log.d("BookingActivity", "Total price updated: " + totalPriceTextView.getText().toString());
            if (!selectedSeats.isEmpty()) {
                totalPriceTextView.setVisibility(View.VISIBLE);
                payButton.setVisibility(View.VISIBLE);
                Log.d("BookingActivity", "Showing payButton");
            } else {
                totalPriceTextView.setVisibility(View.GONE);
                payButton.setVisibility(View.GONE);
                Log.d("BookingActivity", "Hiding payButton");
            }
        });
        seatRecyclerView.setAdapter(seatAdapter);
        Log.d("BookingActivity", "seatRecyclerView initialized with empty list");
    }

    private void updateTimeRecyclerView() {
        String movieName = nameTicketMovie.getText().toString();
        String dateStr = selectedDate.format(DateTimeFormatter.ISO_DATE);

        Log.d("BookingActivity", "Calling Time API for movie: " + movieName + ", date: " + dateStr);

        TimeCinemaFilmRepo repo = new TimeCinemaFilmRepo();
        repo.getShowtimes(movieName, dateStr, new Callback<List<ShowtimeItem>>() {
            @Override
            public void onResponse(Call<List<ShowtimeItem>> call, Response<List<ShowtimeItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ShowtimeItem> showtimes = response.body();
                    List<String> timeList = new ArrayList<>();
                    for (ShowtimeItem item : showtimes) {
                        timeList.add(item.getTime());
                    }
                    timeAdapter = new TimeAdapter(timeList, selectedTime -> {
                        BookingActivity.this.selectedTime = selectedTime;
                        Log.d("BookingActivity", "Time selected: " + selectedTime);
                        Toast.makeText(BookingActivity.this, "Khung giờ chọn: " + selectedTime, Toast.LENGTH_SHORT).show();
                        updateSeatRecyclerView(selectedTime);
                    });
                    timeRecyclerView.setAdapter(timeAdapter);
                    if (timeList.isEmpty()) {
                        Toast.makeText(BookingActivity.this, "No showtimes available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("BookingActivity", "Time API Response failed: " + response.message());
                    Toast.makeText(BookingActivity.this, "Error loading showtimes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ShowtimeItem>> call, Throwable t) {
                Log.e("BookingActivity", "Time API Error: " + t.getMessage());
                Toast.makeText(BookingActivity.this, "Failed to load showtimes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSeatRecyclerView(String selectedTime) {
        String movieName = nameTicketMovie.getText().toString();
        String dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        Log.d("BookingActivity", "Calling Seat API for movie: " + movieName + ", date: " + dateStr + ", time: " + selectedTime);

        SeatCinemaFilmRepo repo = new SeatCinemaFilmRepo();
        repo.getSeats(movieName, dateStr, selectedTime, new Callback<SeatResponse>() {
            @Override
            public void onResponse(Call<SeatResponse> call, Response<SeatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SeatResponse seatResponse = response.body();
                    String seatStatus = seatResponse.getSeatStatus();
                    Log.d("BookingActivity", "Parsed seat_status: " + seatStatus);

                    if (seatStatus == null || seatStatus.length() != 40) {
                        Log.e("BookingActivity", "Invalid seat_status length: " + (seatStatus != null ? seatStatus.length() : "null"));
                        Toast.makeText(BookingActivity.this, "Invalid seat data", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<SeatItem> seatList = new ArrayList<>();
                    char[] rowsLeft = {'A', 'B', 'C', 'D', 'E'};
                    char[] rowsRight = {'F', 'G', 'H', 'I', 'J'};
                    int index = 0;

                    for (char row : rowsLeft) {
                        for (int i = 1; i <= 4; i++) {
                            int status = seatStatus.charAt(index) == '0' ? 0 : 1;
                            seatList.add(new SeatItem(row + String.valueOf(i), status));
                            index++;
                        }
                    }
                    for (char row : rowsRight) {
                        for (int i = 1; i <= 4; i++) {
                            int status = seatStatus.charAt(index) == '0' ? 0 : 1;
                            seatList.add(new SeatItem(row + String.valueOf(i), status));
                            index++;
                        }
                    }

                    Log.d("BookingActivity", "Seat list size: " + seatList.size());
                    seatAdapter.seatList.clear();
                    seatAdapter.seatList.addAll(seatList);
                    screenIndicator.setVisibility(View.VISIBLE);
                    screenLabel.setVisibility(View.VISIBLE);
                    seatLegend.setVisibility(View.VISIBLE);
                    seatAdapter.notifyDataSetChanged();
                    Log.d("BookingActivity", "seatRecyclerView updated with new data");
                } else {
                    Log.e("BookingActivity", "Seat API Response failed: " + response.message());
                    Toast.makeText(BookingActivity.this, "Error loading seats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SeatResponse> call, Throwable t) {
                Log.e("BookingActivity", "Seat API Error: " + t.getMessage());
                Toast.makeText(BookingActivity.this, "Failed to load seats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<LocalDate> generateDateList() {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 1; i < 14; i++) {
            dateList.add(currentDate.plusDays(i));
        }
        return dateList;
    }

    private void initViews() {
        dateRecyclerView = findViewById(R.id.dateRecyclerView);
        timeRecyclerView = findViewById(R.id.timeRecyclerView);
        seatRecyclerView = findViewById(R.id.seatRecyclerView);
        nameTicketMovie = findViewById(R.id.nameTicketMovie);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        screenIndicator = findViewById(R.id.screenIndicator);
        seatLegend = findViewById(R.id.seatLegend);
        screenLabel = findViewById(R.id.screenLabel);

        payButton = findViewById(R.id.payButton);
        payButton.setVisibility(View.GONE);
        payButton.setOnClickListener(v -> {
            List<String> selectedSeats = seatAdapter.getSelectedSeats();
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
            } else {
                seatAdapter.confirmBooking();
                payButton.setVisibility(View.GONE);
                totalPriceTextView.setVisibility(View.GONE);

                Toast.makeText(this, "Thanh toán thành công: " + selectedSeats.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}