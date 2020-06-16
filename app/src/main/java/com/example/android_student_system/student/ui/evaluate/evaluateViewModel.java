package com.example.android_student_system.student.ui.evaluate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class evaluateViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public evaluateViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}