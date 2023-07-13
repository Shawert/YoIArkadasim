package com.example.travelbook.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.travelbook.R;
import com.example.travelbook.adapter.PlaceAdapter;
import com.example.travelbook.databinding.ActivityMainBinding;
import com.example.travelbook.model.Place;
import com.example.travelbook.roomdb.PlaceDao;
import com.example.travelbook.roomdb.PlaceDataBase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    PlaceDataBase db;
    PlaceDao placeDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setIcon(R.drawable.logo);

        setContentView(R.layout.activity_main);*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        db = Room.databaseBuilder(getApplicationContext(), PlaceDataBase.class,"Places").build();

        placeDao = db.placeDao();

        compositeDisposable.add(placeDao.getAllList()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handleResponsive)
        );
        Button addButton = findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hedef sayfaya geçmek için bir Intent oluşturuyoruz
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("info","new");
                startActivity(intent);
            }
        });
    }
    private  void  handleResponsive(List<Place> placeList){
        binding.re.setLayoutManager(new LinearLayoutManager(this));
        PlaceAdapter placeAdapter = new PlaceAdapter(placeList);
        binding.re.setAdapter(placeAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.travel_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_place){
            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}