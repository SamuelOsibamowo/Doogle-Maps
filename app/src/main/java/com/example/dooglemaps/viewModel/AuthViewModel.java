package com.example.dooglemaps.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.dooglemaps.model.AuthenticationModel;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {

    private AuthenticationModel model;
    private MutableLiveData<FirebaseUser> userData;
    private MutableLiveData<Boolean> loggedStatus;



    public AuthViewModel(@NonNull Application application) {
        super(application);
        model = new AuthenticationModel(application);
        userData = model.getFirebaseUserMutableLiveData();
        loggedStatus = model.getUserLoggedMutableLiveData();
    }

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<Boolean> getLoggedStatus() {
        return loggedStatus;
    }

    public void register(String email, String password) {
        model.register(email, password);
    }

    public void signIn(String email, String password) {
        model.login(email, password);
    }

    public void signOut() {
        model.signOut();
    }
}
