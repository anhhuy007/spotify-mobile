package com.example.spotifyclone.features.premium.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.premium.viewmodel.PremiumViewModel;
import com.example.spotifyclone.shared.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SubscriptionDetailFragment extends Fragment {

    TextView tvPlanName, tvPlanDetail, tvSubscriptionId, tvStartDate, tvEndDate;
    Button btnManageSubscription, btnCancelSubscription;
    PremiumViewModel premiumViewModel;
    AlertDialog confirmationDialog;

    public SubscriptionDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription_detail, container, false);

        initializeViews(view);

        premiumViewModel = new PremiumViewModel(getContext());
        premiumViewModel.getSubscription();

        // Observe the latest subscription data
        premiumViewModel.getLatestSubscription().observe(getViewLifecycleOwner(), subscription -> {
            if (subscription != null) {
                // Set subscription title and details based on subscription type
                handleSubscriptionType(subscription.getSubscriptionType());

                // Set other subscription details
                tvSubscriptionId.setText(subscription.getId());
                tvStartDate.setText(convertDate(subscription.getStartDate()));
                tvEndDate.setText(convertDate(subscription.getEndDate()));
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        tvPlanName = view.findViewById(R.id.tv_plan_name);
        tvPlanDetail = view.findViewById(R.id.tv_plan_details);
        tvSubscriptionId = view.findViewById(R.id.tv_subscription_id);
        tvStartDate = view.findViewById(R.id.tv_start_date);
        tvEndDate = view.findViewById(R.id.tv_next_billing_date);

        btnManageSubscription = view.findViewById(R.id.btn_manage_subscription);
        btnCancelSubscription = view.findViewById(R.id.btn_cancel_subscription);

        // Set up click listeners for buttons
        btnManageSubscription.setOnClickListener(v -> manageSubscription());
        btnCancelSubscription.setOnClickListener(v -> showConfirmationDialog());
    }

    private void handleSubscriptionType(String subscriptionType) {
        String title = getString(R.string.unknown_plan);
        String description = getString(R.string.no_details_available);

        switch(subscriptionType) {
            case "individual":
                title = getString(R.string.premium_individual);
                description = getString(R.string.individual_plan_term);
                break;
            case "student":
                title = getString(R.string.premium_student);
                description = getString(R.string.student_plan_term);
                break;
            case "mini":
                title = getString(R.string.premium_mini);
                description = getString(R.string.mini_plan_description);
                break;
        }

        tvPlanName.setText(title);
        tvPlanDetail.setText(description);
    }

    private void cancelSubscription() {
        premiumViewModel.cancelSubscription();

        // Observe the cancellation result
        premiumViewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Handle successful cancellation
                Toast.makeText(getContext(), "Subscription cancelled successfully", Toast.LENGTH_SHORT).show();

                // Update user premium status
                AuthRepository authRepository = new AuthRepository(requireContext());
                User updatedUser = authRepository.getUser();
                updatedUser.setPremium(false);

                // Save updated user to SharedPreferences
                authRepository.saveUser(updatedUser);

                // navigate back to home screen
                NavController navController = NavHostFragment.findNavController(this);
                navController.navigate(R.id.nav_home);
            } else {
                // Handle cancellation failure
                Log.e("SubscriptionDetailFragment", "Failed to cancel subscription");
            }
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cancel Subscription")
                .setMessage("Are you sure you want to cancel your subscription?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cancelSubscription();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        confirmationDialog = builder.create();
        confirmationDialog.show();
    }

    private void manageSubscription() {
    }

    private String convertDate(Date date) {
        // Convert date to desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        return dateFormat.format(date);
    }
}
