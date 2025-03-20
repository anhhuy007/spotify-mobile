package com.example.spotifyclone.features.premium.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.premium.viewmodel.PremiumVMFactory;
import com.example.spotifyclone.features.premium.viewmodel.PremiumViewModel;

import java.text.ParseException;

public class CheckoutFragment extends Fragment {
    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private PremiumViewModel premiumViewModel;
    private TextView planName, planDetails, todayCharge, afterTrial;
    private EditText etAmount;
    private RadioButton rbCreditCard, rbPaypal, rbMomo;
    private Button btnPay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        premiumViewModel = new ViewModelProvider(this, new PremiumVMFactory(requireContext())).get(PremiumViewModel.class);

        planName = view.findViewById(R.id.tv_plan_name);
        planDetails = view.findViewById(R.id.tv_plan_details);
        todayCharge = view.findViewById(R.id.tv_today_charge);
        afterTrial = view.findViewById(R.id.tv_after_trial);
        etAmount = view.findViewById(R.id.et_amount);
        rbCreditCard = view.findViewById(R.id.rb_credit_card);
        rbPaypal = view.findViewById(R.id.rb_paypal);
        rbMomo = view.findViewById(R.id.rb_momo);
        btnPay = view.findViewById(R.id.btn_pay);

        Bundle bundle = getArguments();
        final String plan = bundle.getString("plan");
        final Integer duration = bundle.getInt("duration");
        handlePlan(plan, duration);

        btnPay.setOnClickListener(v -> {
            // Handle payment
            try {
                premiumViewModel.createSubscription(plan, duration);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        observeViewModel();

        return view;
    }

    private void handlePlan(String plan, Integer duration) {
        switch (plan) {
            case "mini":
                planName.setText("Premium Mini");
                planDetails.setText("Get access to all features for 1 month");
                etAmount.setText("₫10,500");
                break;
            case "individual":
                planName.setText("Premium Individual");
                planDetails.setText("Get access to all features for 3 months");
                etAmount.setText("₫0");
                break;
            case "student":
                planName.setText("Premium Student");
                planDetails.setText("Get access to all features for 6 months");
                etAmount.setText("₫29,500");
                break;
        }

        todayCharge.setText(etAmount.getText().toString());
        btnPay.setText("Pay " + etAmount.getText().toString());
    }

    private void observeViewModel() {
        // Observe ViewModel
        premiumViewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Payment success
                Toast.makeText(requireContext(), "Payment success", Toast.LENGTH_SHORT).show();

                // Redirect to home
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        premiumViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            Log.d("DEBUG", "isLoading: " + isLoading);
            if (isLoading) {
                // Show loading
                Toast.makeText(requireContext(), "Processing payment...", Toast.LENGTH_SHORT).show();
            }
        });

        premiumViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (!errorMessage.isEmpty()) {
                // Payment failed
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
