package com.example.coupon_managment_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class BuyACouponFragment extends Fragment {

    private EditText etMaxPrice;
    private ListView listViewBuyCoupons;
    private Context context;
    private MyCouponsLVAdapter couponAdapter;
    private ArrayList<Coupon> couponsArrayList = new ArrayList<>();
    private CouponsDBDAO couponsDBDAO;
    private Button lastbuy,info;
    private int selectedRow = -1; // Initialize with -1, indicating no selection
    private Coupon selectedCoupon = null;
    private View selectedView = null;
    private CustomerFacade customerFacadetwo;
    private Spinner categorySpinner;
    private ArrayList<Coupon> filtered;
    /***************************~ ListView Selected line Management  ~*****************************/
    //private int selectedRow = -1; // Initialize with -1, indicating no selection
    private ConstraintLayout bgLayout;
    private int bgLineColor;
    /**********************************************************************************************/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buy_a_coupon, container, false);

        // Initialize UI elements
        etMaxPrice = view.findViewById(R.id.buyCouponFrag_etMaxPrice);
        couponsDBDAO=CouponsDBDAO.getInstance(context);
        listViewBuyCoupons = view.findViewById(R.id.buyCouponFrag_lvBuyCoupon);
        lastbuy=view.findViewById(R.id.buyCouponFrag_btnBuy);
        couponsDBDAO=CouponsDBDAO.getInstance(context);
        categorySpinner=view.findViewById(R.id.buyCouponFrag_sbCouponCategory);
        info=view.findViewById(R.id.buyCouponFrag_btnInfo);




        // Initialize the adapter with the coupon data
        try {
            couponAdapter = new MyCouponsLVAdapter(requireContext(),R.layout.fragment_my_coupons_listview_line, couponsDBDAO.getValidCoupons());
        } catch (CouponException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        try {
            couponsArrayList=couponsDBDAO.getValidCoupons();
        } catch (CouponException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        listViewBuyCoupons.setAdapter(couponAdapter);

        /** fill spinner with categories**/
        Category[] categories = Category.values();
        String[] categoryNames = new String[categories.length + 1];
        categoryNames[0] = "All";
        for (int i = 0; i < categories.length; i++) {
            categoryNames[i + 1] = categories[i].toString();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);


        /** spinner lisnter **/
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = categoryNames[position];
                // Handle the selected option here
                try {
                    // Handle the selected option here
                    switch (selectedOption) {
                        case "All":
                            // show all coupons
                            if (etMaxPrice.getText().length() > 0) {
                                filtered = filterByMaxPrice(couponsDBDAO.getValidCoupons(), Double.parseDouble(etMaxPrice.getText().toString()));
                            } else {
                                filtered = couponsDBDAO.getValidCoupons();
                            }
                            couponAdapter.refreshAllMyCoupons(filtered);
                            break;
                        case "Food":
                            // filter by food
                            if (etMaxPrice.getText().length() > 0) {
                                filtered = filterByMaxPrice(couponsDBDAO.getValidCoupons(), Double.parseDouble(etMaxPrice.getText().toString()));
                            } else {
                                filtered = couponsDBDAO.getValidCoupons();
                            }
                            filtered = filterByCategory(filtered, Category.Food);
                            couponAdapter.refreshAllMyCoupons(filtered);
                            break;
                        case "Electricity":
                            // filter by electricity
                            if (etMaxPrice.getText().length() > 0) {
                                filtered = filterByMaxPrice(couponsDBDAO.getValidCoupons(), Double.parseDouble(etMaxPrice.getText().toString()));
                            }
                            else
                            {
                                filtered = couponsDBDAO.getValidCoupons();
                            }
                            filtered = filterByCategory(couponsDBDAO.getValidCoupons(), Category.Electricity);
                            couponAdapter.refreshAllMyCoupons(filtered);
                            break;
                        case "Restaurant":
                            // filter by restaurant
                            if (etMaxPrice.getText().length() > 0) {
                                filtered = filterByMaxPrice(couponsDBDAO.getValidCoupons(), Double.parseDouble(etMaxPrice.getText().toString()));
                            }
                            filtered = filterByCategory(filtered, Category.Restaurant);
                            couponAdapter.refreshAllMyCoupons(filtered);
                            break;
                        case "Vacation":
                            // filter by vacation
                            if (etMaxPrice.getText().length() > 0) {
                                filtered = filterByMaxPrice(filtered, Double.parseDouble(etMaxPrice.getText().toString()));
                            }
                            filtered = filterByCategory(filtered, Category.Vacation);
                            couponAdapter.refreshAllMyCoupons(filtered);
                            break;
                        default:
                            // Handle unexpected category or add more cases as needed
                            throw new IllegalStateException("Unexpected value: " + selectedOption);
                    }
                }
                catch (CouponException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                try {
                    couponAdapter.refreshAllMyCoupons(couponsDBDAO.getValidCoupons());
                    if (etMaxPrice.getText().length() > 0) {
                        couponsArrayList = filterByMaxPrice(couponsDBDAO.getValidCoupons(), Double.parseDouble(etMaxPrice.getText().toString()));
                    }
                } catch (CouponException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Set an item click listener for the ListView
        listViewBuyCoupons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Reset the color of the previously selected item
                if (selectedView != null) {
                    selectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                }

                // Change the background color of the selected item
                if(selectedRow != -1)
                {
                    bgLayout.setBackgroundColor(bgLineColor);
                }
                selectedRow = position;
                listViewBuyCoupons.setSelection(selectedRow);
                bgLayout = (ConstraintLayout) view.findViewById(R.id.myCoupons_lvLine_constraintLayout);
                bgLineColor = view.getSolidColor();
                bgLayout.setBackgroundColor(getResources().getColor(R.color.bblue));


                // Update the selected view and retrieve the selected coupon object
                selectedView = view;
                try {
                    selectedCoupon = couponsDBDAO.getValidCoupons().get(position);
                } catch (CouponException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        lastbuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCoupon != null) {
                    // Create an AlertDialog to confirm the purchase
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure you want to buy the selected item?");

                    // Add the "Yes" button
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                // Purchase the selected coupon
                                customerFacadetwo.purchaseCoupon(selectedCoupon);

                                // Clear the adapter and update the coupon list
                                couponAdapter.clear();
                                couponAdapter.addAll(couponsDBDAO.getValidCoupons());
                                couponAdapter.notifyDataSetChanged();
                            } catch (CustomerException | CouponException e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Add the "No" button
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Dismiss the dialog and do nothing
                            dialog.dismiss();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(context, "Please Select A Coupon To Purchase!", Toast.LENGTH_LONG).show();
                }
            }
        });

        info.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (selectedCoupon != null) {
                    Intent intent = new Intent(context, CouponInfoActivity.class);
                    Company comp = null;
                    String compName = "";
                    try {
                         comp = customerFacadetwo.companiesDAO.getOneCompany(selectedCoupon.getCompanyId());
                    } catch (CompanyException e) {
                        throw new RuntimeException(e);
                    }
                    if(comp!=null)
                    {
                        compName = comp.getName();
                    }
                    intent.putExtra("coupon", selectedCoupon);
                    intent.putExtra("CompName",compName);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(context, "Please Select A Coupon To View!", Toast.LENGTH_LONG).show();
                }
            }
        });

        /** filter to max price edit text listener **/
        // Attach a TextWatcher to etMaxPrice
        etMaxPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Get the user's input from etMaxPrice
                String userInput = editable.toString();

                // Check if the user input is not empty
                if (!userInput.isEmpty()) {
                    try {
                        // Parse the user input as a double
                        double maxPrice = Double.parseDouble(userInput);

                        // Filter coupons based on the maxPrice and the selected category
                        if(categorySpinner.getSelectedItem().toString().equals("All"))
                        {
                            filtered= filterByMaxPrice( couponsDBDAO.getValidCoupons(),maxPrice);
                        }
                        else
                        {
                            filtered= filterByCategory(couponsDBDAO.getValidCoupons(),Category.valueOf(categorySpinner.getSelectedItem().toString()));
                            filtered=filterByMaxPrice(filtered,maxPrice);
                        }
                        // Update the ListView with the filtered coupons
                    } catch (NumberFormatException e) {
                        // Handle invalid input (non-numeric input) if needed
                    } catch (CouponException e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    couponAdapter.clear();
                    couponAdapter.addAll(filtered);
                    couponAdapter.notifyDataSetChanged();
                } else {
                    // If the user input is empty, show all coupons based on the selected category
                    String selectedCategory = categorySpinner.getSelectedItem().toString();
                    if(selectedCategory.equals("All")) {
                        try {
                            filtered = couponsDBDAO.getValidCoupons();
                        } catch (CouponException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            filtered = filterByCategory(couponsDBDAO.getValidCoupons(),Category.valueOf(selectedCategory));
                        } catch (CouponException e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    // Update the ListView with the filtered coupons
                    couponAdapter.clear();
                    couponAdapter.addAll(filtered);
                    couponAdapter.notifyDataSetChanged();
                }
            }
        });
        return view;
    }




    private void filterCouponsByPrice(int maxPrice) throws CouponException {
        couponsDBDAO=CouponsDBDAO.getInstance(context);
        List<Coupon> filteredCoupons = new ArrayList<>();
        List<Coupon> allCoupons = couponsDBDAO.getValidCoupons();

        for (Coupon coupon : allCoupons) {
            // Assuming Coupon has a getPrice() method to get the price
            Double price = coupon.getPrice();

            // Check if the coupon price is lower than or equal to the specified maxPrice
            if (price <= maxPrice) {
                filteredCoupons.add(coupon);
            }
        }
        // Clear the adapter and add the filtered coupons
        couponAdapter.clear();
        couponAdapter.addAll(filteredCoupons);
        couponAdapter.notifyDataSetChanged();
    }

    public void setFacade(CustomerFacade customerFacade){
        customerFacadetwo=customerFacade;

    }

    public void setContext(Context context) {this.context = context;}

    public ArrayList<Coupon> filterByCategory(ArrayList<Coupon> all,Category category)
    {
        ArrayList<Coupon> filteredCoupons = new ArrayList<>();
        for (Coupon coupon : all) {
            if (coupon.getCategory() == category) {
                filteredCoupons.add(coupon);
            }
        }
        return filteredCoupons;
    }

    public ArrayList<Coupon> filterByMaxPrice(ArrayList<Coupon> all, double maxPrice) {
        ArrayList<Coupon> filteredCoupons = new ArrayList<>();
        for (Coupon coupon : all) {
            if (coupon.getPrice() <= maxPrice) {
                filteredCoupons.add(coupon);
            }
        }
        return filteredCoupons;
    }

    public void setLvBuyACouponSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }
}