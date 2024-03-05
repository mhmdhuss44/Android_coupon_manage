package com.example.coupon_managment_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CouponInfoActivity extends AppCompatActivity {
    private TextView couponId,comp,category,etstartDate,etendDate,amount,price,description,title;
    private ImageView selectedImageIM;
    private Button btnReturn;
    private Date startDate=null;
    private Date endDate=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_info);
        couponId = findViewById(R.id.couponInfo_tvCoupId);
        comp= findViewById(R.id.couponInfo_tvComp);
        category= findViewById(R.id.couponInfo_tvCategory);
        etstartDate= findViewById(R.id.couponInfo_tvStartDate);
        etendDate= findViewById(R.id.couponInfo_tvEndDate);
        amount= findViewById(R.id.couponInfo_tvAmount);
        price= findViewById(R.id.couponInfo_tvPrice);
        description= findViewById(R.id.couponInfo_tvDescription);
        selectedImageIM = findViewById(R.id.couponInfo_IVimage);
        btnReturn= findViewById(R.id.couponInfo_btnReturn);
        title= findViewById(R.id.couponInfo_tvTitle);

        Intent intent = getIntent();
        Coupon selectedcoupon = (Coupon) intent.getSerializableExtra("coupon");
        String CompName = intent.getStringExtra("CompName");
        // set coupon info
        startDate = selectedcoupon.getStartDate();
        endDate = selectedcoupon.getEndDate();
        //date fix
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedStartDate = dateFormat.format(startDate);
        String formattedEndDate = dateFormat.format(endDate);
        couponId.setText(selectedcoupon.getId()+"");
        comp.setText(CompName);
        category.setText(selectedcoupon.getCategory().toString());
        etstartDate.setText(formattedStartDate);
        etendDate.setText(formattedEndDate);
        title.setText(selectedcoupon.getTitle());
        amount.setText(selectedcoupon.getAmount()+"");
        price.setText(selectedcoupon.getPrice()+"");
        description.setText(selectedcoupon.getDescription());
        // image load
        selectedImageIM.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // This code will be executed after the ImageView has been laid out and has valid dimensions.
                int width = selectedImageIM.getWidth();
                int height = selectedImageIM.getHeight();

                // Remove the listener to avoid multiple calls
                selectedImageIM.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Now you can use the width and height
                if (width != 0 && height != 0) {
                    Bitmap imageBitmap = loadImageFromInternalStorage(selectedcoupon.getImage());
                    if (imageBitmap != null) {
                        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
                        selectedImageIM.setImageBitmap(imageBitmap);
                    }
                }
            }
        });


        // return button click
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private Bitmap loadImageFromInternalStorage(String path) {
        try {
            File file = new File(path);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}