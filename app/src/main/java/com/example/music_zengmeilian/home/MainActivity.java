package com.example.music_zengmeilian.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.home.adapter.HomeAdapter;
import com.example.music_zengmeilian.home.viewmodel.HomeViewModel;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewModel();
        loadData(true);
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new HomeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(() -> loadData(true));
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getHomeData().observe(this, data -> {
            swipeRefreshLayout.setRefreshing(false);
            adapter.setData(data);
        });

    }

    private void loadData(boolean isRefresh) {
        swipeRefreshLayout.setRefreshing(true);
        viewModel.loadHomeData(isRefresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}