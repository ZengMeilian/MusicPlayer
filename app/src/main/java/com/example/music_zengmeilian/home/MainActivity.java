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
    private static final String TAG = "MainActivity";

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View emptyView;
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
        emptyView = findViewById(R.id.emptyView); // 确保布局中有这个id

        adapter = new HomeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(() -> loadData(true));
        findViewById(R.id.retryButton).setOnClickListener(v -> loadData(true)); // 确保布局中有这个id
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getHomeData().observe(this, data -> {
            swipeRefreshLayout.setRefreshing(false);
            if (data == null || data.isEmpty()) {
                showEmptyView(true);
                Toast.makeText(this, R.string.no_data_available, Toast.LENGTH_SHORT).show();
            } else {
                showEmptyView(false);
                adapter.setData(data);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            swipeRefreshLayout.setRefreshing(false);
            showEmptyView(true);
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadData(boolean isRefresh) {
        swipeRefreshLayout.setRefreshing(true);
        viewModel.loadHomeData(isRefresh);
    }

    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter.cleanup();
        }
        super.onDestroy();
    }
}