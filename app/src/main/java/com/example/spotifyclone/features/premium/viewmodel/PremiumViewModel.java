package com.example.spotifyclone.features.premium.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.spotifyclone.features.authentication.repository.AuthRepository;
import com.example.spotifyclone.features.premium.model.Subscription;
import com.example.spotifyclone.features.premium.network.PremiumService;
import com.example.spotifyclone.shared.model.APIResponse;
import com.example.spotifyclone.shared.model.User;
import com.example.spotifyclone.shared.network.RetrofitClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PremiumViewModel extends ViewModel {
    private final PremiumService premiumService;
    private final AuthRepository authRepository;

    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<String> errorMessage = new MutableLiveData<>();
    MutableLiveData<Boolean> isSuccess = new MutableLiveData<>();
    MutableLiveData<Boolean> isPremiumUser = new MutableLiveData<>();
    MutableLiveData<Subscription> latestSubscription = new MutableLiveData<>();

    public PremiumViewModel(Context context) {
        premiumService = RetrofitClient.getClient(context).create(PremiumService.class);
        authRepository = new AuthRepository(context);
    }

    public void createSubscription(String plan, Integer duration, Integer total, Integer newCharge) throws ParseException {
        String startDate = new Date().toString();

        // Calculate end date based on duration (month)
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, duration);
        String endDate = calendar.getTime().toString();

        User user = authRepository.getUser();
        if (user == null) {
            return;
        }

        isLoading.setValue(true);

        premiumService.createSubscription(
                user.getId(),
                plan,
                convertDate(startDate), convertDate(endDate),
                total,
                newCharge
        ).enqueue(new Callback<APIResponse<Subscription>>() {
            @Override
            public void onResponse(Call<APIResponse<Subscription>> call, Response<APIResponse<Subscription>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    Subscription subscription = response.body().getData();
                    if (subscription != null) {
                        isSuccess.setValue(true);
                    }

                } else {
                    Log.d("DEBUG", "Failed to create subscription: " + response.message());
                    errorMessage.setValue("Failed to create subscription");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Subscription>> call, Throwable t) {
                isLoading.setValue(false);
                Log.d("DEBUG", "Failed to create subscription: " + t.getMessage());
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void checkSubscription() {
        isLoading.setValue(true);
        User user = authRepository.getUser();
        premiumService.checkSubscription(user.getId()).enqueue(new Callback<APIResponse<Subscription>>() {
            @Override
            public void onResponse(Call<APIResponse<Subscription>> call, Response<APIResponse<Subscription>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    Subscription subscription = response.body() != null ? response.body().getData() : null;
                    if (subscription != null) {
                        isPremiumUser.setValue(true);
                        isSuccess.setValue(true);
                    } else {
                        isPremiumUser.setValue(false);
                        isSuccess.setValue(false);
                    }
                } else {
                    errorMessage.setValue("Failed to check subscription");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Subscription>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void cancelSubscription() {
        isLoading.setValue(true);
        User user = authRepository.getUser();
        premiumService.cancelSubscription(user.getId()).enqueue(new Callback<APIResponse<Void>>() {
            @Override
            public void onResponse(Call<APIResponse<Void>> call, Response<APIResponse<Void>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    isSuccess.setValue(true);
                } else {
                    errorMessage.setValue("Failed to cancel subscription");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public void getSubscription() {
        isLoading.setValue(true);
        User user = authRepository.getUser();

        premiumService.getLatestSubscription(user.getId()).enqueue(new Callback<APIResponse<Subscription>>() {
            @Override
            public void onResponse(Call<APIResponse<Subscription>> call, Response<APIResponse<Subscription>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful()) {
                    Subscription subscription = response.body() != null ? response.body().getData() : null;
                    if (subscription != null) {
                        isSuccess.setValue(true);
                        latestSubscription.setValue(subscription);
                    } else {
                        isSuccess.setValue(false);
                        errorMessage.setValue("No subscription found");
                    }
                } else {
                    errorMessage.setValue("Failed to get subscription");
                }
            }

            @Override
            public void onFailure(Call<APIResponse<Subscription>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    public static String convertDate(String _date) throws ParseException {
        // Step 1: Parse the input date string
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        Date date = inputFormat.parse(_date);

        // Step 2: Convert to UTC manually using Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Step 3: Format the date in the required format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return outputFormat.format(calendar.getTime());
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    public MutableLiveData<Boolean> getIsPremiumUser() {
        return isPremiumUser;
    }

    public MutableLiveData<Subscription> getLatestSubscription() {
        return latestSubscription;
    }
}
