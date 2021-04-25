package com.example.attendify;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class OTPFinalHaiYE extends AppCompatActivity {

    private static final String TAG = "OTPFinalHaiYe";
    String rollNo;
    String phoneNumber;
    PinView pinView;
    Button button;
    FirebaseAuth mAuth;
    String verifyId;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_final_hai_y_e);
        getSupportActionBar().hide();
        TextView textView=findViewById(R.id.textView3);
        textView.setText("OTP has been sent via SMS");
        mAuth = FirebaseAuth.getInstance();
        pinView = findViewById(R.id.pin_view_2);
        button =findViewById(R.id.afterotp);
        rollNo = getIntent().getStringExtra("rollNo");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        phoneNumber = "+91"+phoneNumber;
        getOtp(phoneNumber);
        // Check Current Date


        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(pinView.getText().toString().isEmpty())
                    Toast.makeText(OTPFinalHaiYE.this, "Empty input can't be processed", Toast.LENGTH_SHORT).show();
                else if(pinView.getText().toString().length()!=6)
                    Toast.makeText(OTPFinalHaiYE.this, "Enter valid 6 digit OTP", Toast.LENGTH_SHORT).show();
                else
                {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verifyId,pinView.getText().toString());
                    signInWithCredential(phoneAuthCredential);
                }
                /*if(a==1)
                {
                    startActivity(new Intent(OTPFinalHaiYE.this, Sucess.class));
                }
                else
                {
                    startActivity(new Intent(OTPFinalHaiYE.this, failerror.class));
                }*/
            }
        });
    }

    private void getOtp(String phoneNumber) {
        sendVerificationCode(phoneNumber);
    }
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Intent i = new Intent(OTPFinalHaiYE.this, Sucess.class);
                            i.putExtra("rollNo",rollNo);
                            startActivity(i);
                            finish();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(OTPFinalHaiYE.this, "Incorrect OTP entered", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Toast.makeText(getApplicationContext(),"Verification Successful",Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Toast.makeText(getApplicationContext(),"Verification failed, please try again",Toast.LENGTH_SHORT).show();
            Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Log.e(TAG,e.getMessage());
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.e(TAG,e.getMessage());
            }

            // Show a message and update the UI
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:" + verificationId);
            // Save verification ID and resending token so we can use them later
             verifyId = verificationId;
            forceResendingToken = token;
        }
    };
    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifyId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }
}