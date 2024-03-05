package com.example.coupon_managment_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CouponsLVAdapter extends ArrayAdapter<Coupon> {
    private Context context;
    private ArrayList<Coupon> couponsArrayList;
    private int line;
    private static final int width=130;
    private static final int height=100;


    public CouponsLVAdapter(@NonNull Context context, int line, ArrayList<Coupon> coupons) {
        super(context, line, coupons);
        this.context = context;
        this.couponsArrayList = coupons;
        this.line = line;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        /**Creating Custom View*/
        LayoutInflater inflater = LayoutInflater.from(context);
        View myView = inflater.inflate(line, parent, false);

        Coupon coupon = couponsArrayList.get(position);
        TextView tvTitle, tvAmount, tvPrice,tvEndDate;
        ImageView ivCouponImage;
        tvTitle = myView.findViewById(R.id.couponForCompnay_titleTV);
        tvAmount = myView.findViewById(R.id.couponForCompnay_tvAmount);
        tvPrice = myView.findViewById(R.id.couponForCompnay_priceTV);
        tvEndDate = myView.findViewById(R.id.couponForCompnay_endDateTV);
        ivCouponImage = myView.findViewById(R.id.couponForCompnay_ivImage);
        // loading image
        Bitmap imageBitmap = loadImageFromInternalStorage(coupon.getImage());
        if (imageBitmap != null) {
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
            ivCouponImage.setImageBitmap(imageBitmap);
        }
        // date fixed
        Date startDate = coupon.getStartDate();
        Date endDate = coupon.getEndDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedEndDate = dateFormat.format(endDate);

        tvTitle.setText(coupon.getTitle());
        tvAmount.setText("Amount:  " + coupon.getAmount());
        tvPrice.setText("Price:   "+ coupon.getPrice()); // i.e. Purchase Price
        tvEndDate.setText("End Date: " + formattedEndDate);
        return myView;
    }


    public void refreshAllCoupons(ArrayList<Coupon> coupons) {
        if (coupons==null){
            clear();
            return;}
        clear();
        addAll(coupons);
        notifyDataSetChanged();
    }

    public void refreshCouponAdded(Coupon coupon) {
        add(coupon);
        notifyDataSetChanged();
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
