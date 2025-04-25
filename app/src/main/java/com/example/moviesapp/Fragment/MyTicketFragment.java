package com.example.moviesapp.Fragment;

import static android.app.Activity.RESULT_OK;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesapp.Activity.BookingActivity;
import com.example.moviesapp.Adapters.TicketAdapter;
import com.example.moviesapp.Domain.Ticket;
import com.example.moviesapp.R;


public class MyTicketFragment extends Fragment {
    private RecyclerView ticketRecyclerView;
    private TicketAdapter ticketAdapter;

    ImageButton btnBack;
    public MyTicketFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myticket, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationHelper.navigateTo(MyTicketFragment.this,
                        new ProfileFragment(), "profile_fragment");
            }
        });
        ticketRecyclerView = view.findViewById(R.id.ticketRecyclerView);
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ticketAdapter = new TicketAdapter();
        ticketRecyclerView.setAdapter(ticketAdapter);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Ticket ticket = (Ticket) data.getSerializableExtra("ticket");
            if (ticket != null) {
                ticketAdapter.addTicket(ticket);
            }
        }
    }

    public void startBookingActivityForResult() {
        Intent intent = new Intent(getActivity(), BookingActivity.class);
        startActivityForResult(intent, 1);
    }
}
