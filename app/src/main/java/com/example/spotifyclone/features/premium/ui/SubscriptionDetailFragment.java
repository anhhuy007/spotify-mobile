package com.example.spotifyclone.features.premium.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.spotifyclone.R;

public class SubscriptionDetailFragment extends Fragment {

    TextView tvPlanName, tvPlanDetail, tvSubscriptionId, tvStartDate, tvEndDate, tvPaymentMethod;
    Button btnManageSubscription, btnCancelSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize your fragment here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription_detail, container, false);

        initializeViews(view);

        return view;
    }

    private void initializeViews(View view) {
        tvPlanName = view.findViewById(R.id.tv_plan_name);
        tvPlanDetail = view.findViewById(R.id.tv_plan_details);
        tvSubscriptionId = view.findViewById(R.id.tv_subscription_id);
        tvStartDate = view.findViewById(R.id.tv_start_date);
        tvEndDate = view.findViewById(R.id.tv_next_billing_date);
        tvPaymentMethod = view.findViewById(R.id.tv_payment_method);

        btnManageSubscription = view.findViewById(R.id.btn_manage_subscription);
        btnCancelSubscription = view.findViewById(R.id.btn_cancel_subscription);

        // Set up click listeners for buttons
        btnManageSubscription.setOnClickListener(v -> manageSubscription());
        btnCancelSubscription.setOnClickListener(v -> cancelSubscription());
    }

    private void cancelSubscription() {
    }

    private void manageSubscription() {

    }
}
