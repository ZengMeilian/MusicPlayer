package com.example.music_zengmeilian.home;

import android.os.Bundle;
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
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeAdapter adapter;
    private HomeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewModel();
        setupObservers();
        loadData(true);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        adapter = new HomeAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(() -> loadData(true));
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setupObservers() {
        viewModel.getHomeData().observe(this, data -> {
            swipeRefreshLayout.setRefreshing(false);
            try {
                if (data != null && !data.isEmpty()) {
                    adapter.setData(data);
                } else {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "UI渲染错误: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            swipeRefreshLayout.setRefreshing(false);
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "错误: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData(boolean isRefresh) {
        swipeRefreshLayout.setRefreshing(true);
        viewModel.loadHomeData(isRefresh);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter = null;
        }
    }
}