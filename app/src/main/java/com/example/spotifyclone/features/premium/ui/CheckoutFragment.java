package com.example.spotifyclone.features.premium.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.spotifyclone.R;
import com.example.spotifyclone.features.premium.model.Plan;
import com.example.spotifyclone.features.premium.viewmodel.PremiumVMFactory;
import com.example.spotifyclone.features.premium.viewmodel.PremiumViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

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
    private TextView planName, planDetails, todayCharge, duration, afterTrial;
    private EditText etAmount;
    private RadioButton rbCreditCard, rbPaypal, rbMomo;
    private Button btnPay;
    private AlertDialog progressDialog;

    private Plan plan;

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
        duration = view.findViewById(R.id.tv_duration);
        etAmount = view.findViewById(R.id.et_amount);
        rbCreditCard = view.findViewById(R.id.rb_credit_card);
        rbPaypal = view.findViewById(R.id.rb_paypal);
        rbMomo = view.findViewById(R.id.rb_momo);
        btnPay = view.findViewById(R.id.btn_pay);

        Bundle bundle = getArguments();
        plan = new Gson().fromJson(bundle.getString("planJson"), Plan.class);
        handlePlan(plan);

        btnPay.setOnClickListener(v -> {
            showConfirmationDialog(plan);
        });

        observeViewModel();

        return view;
    }

    private void showConfirmationDialog(Plan plan) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_payment);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        // Initialize dialog views
        TextView tvDialogPlanName = dialog.findViewById(R.id.tv_dialog_plan_name);
        TextView tvDialogPlanDetails = dialog.findViewById(R.id.tv_dialog_plan_details);
        TextView tvDialogPaymentMethod = dialog.findViewById(R.id.tv_dialog_payment_method);
        TextView tvDialogAmount = dialog.findViewById(R.id.tv_dialog_amount);
        MaterialButton btnDialogCancel = dialog.findViewById(R.id.btn_dialog_cancel);
        MaterialButton btnDialogConfirm = dialog.findViewById(R.id.btn_dialog_confirm);

        // Set dialog content
        tvDialogPlanName.setText(planName.getText());
        tvDialogPlanDetails.setText(planDetails.getText());
        tvDialogAmount.setText(etAmount.getText());

        // Get selected payment method
        String paymentMethod;
        if (rbPaypal.isChecked()) {
            paymentMethod = "PayPal";
        } else if (rbMomo.isChecked()) {
            paymentMethod = "Momo";
        } else {
            paymentMethod = "Credit Card";
        }
        tvDialogPaymentMethod.setText(paymentMethod);

        // Set button click listeners
        btnDialogCancel.setOnClickListener(v -> dialog.dismiss());

        btnDialogConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            // Call API to create subscription
            try {
                premiumViewModel.createSubscription(
                        plan.getType(),
                        plan.getDuration(),
                        plan.getCurrentPrice(),
                        plan.getNewCharge()
                );
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void handlePlan(Plan plan) {
        planName.setText(plan.getTitle());
        planDetails.setText(plan.getTerm());
        duration.setText("After " + plan.getDuration().toString() + " months");
        afterTrial.setText("₫" + formatPrice(plan.getNewCharge()) + "/month");
        todayCharge.setText("₫" + formatPrice(plan.getCurrentPrice()));
        etAmount.setText(todayCharge.getText().toString());
        btnPay.setText("Pay " + etAmount.getText().toString());
    }

    private void observeViewModel() {
        // Observe ViewModel
        premiumViewModel.getIsSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Payment success
                Toast.makeText(requireContext(), "Payment success", Toast.LENGTH_SHORT).show();

                // Redirect to home
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_home);
            }
        });

        premiumViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                // Show loading dialog
                showLoadingDialog();
            }
            else {
                // Dismiss loading dialog
                dismissLoadingDialog();
            }
        });

        premiumViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (!errorMessage.isEmpty()) {
                // Payment failed
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(R.layout.dialog_loading); // Custom layout with ProgressBar
        builder.setCancelable(false);

        progressDialog = builder.create();
        progressDialog.show();
    }

    private void dismissLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String formatPrice(Integer price) {
        // convert 10000 to 10,000
        return String.format("%,d", price);
    }
}
