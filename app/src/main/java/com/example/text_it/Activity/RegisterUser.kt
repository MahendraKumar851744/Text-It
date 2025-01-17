package com.example.text_it.Activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.text_it.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.hbb20.CountryCodePicker
import java.util.concurrent.TimeUnit


class RegisterUser : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)

        auth = FirebaseAuth.getInstance()

        val backButton: ImageButton = findViewById(R.id.backButton)
        val regBut: Button = findViewById(R.id.buttonRegister)

        val name: EditText = findViewById(R.id.editTextName)
        val ccp: CountryCodePicker = findViewById(R.id.ccp)
        val phone: EditText = findViewById(R.id.editTextPhone)
        val email: EditText = findViewById(R.id.editTextEmail)
        val password: EditText = findViewById(R.id.editTextPassword)
        val confirmPassword: EditText = findViewById(R.id.editTextConfirmPassword)

        ccp.registerCarrierNumberEditText(phone)

        backButton.setOnClickListener {
            startActivity(
                Intent(
                    this, page1::class.java
                )
            )
        }
        regBut.setOnClickListener {
            var doLogin = true
            if (password.text.toString() != confirmPassword.text.toString()) {
                doLogin = false
                Toast.makeText(
                    baseContext, "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (name.text.toString() == "" || phone.toString() == "" || email.text.toString() == "" || password.text.toString() == "" || confirmPassword.text.toString() == "") {
                Toast.makeText(
                    baseContext, "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
                doLogin = false
            }
            if (!ccp.isValidFullNumber) {
                Toast.makeText(
                    baseContext, "Please enter a valid phone number",
                    Toast.LENGTH_SHORT
                ).show()
                doLogin = false
            }
            if (doLogin) {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(ccp.fullNumberWithPlus)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            Log.d(TAG, "onVerificationCompleted:$credential")
                            Toast.makeText(
                                baseContext, "Verification Completed",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this@RegisterUser, baseHomeActivity::class.java
                                )
                            )
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Log.w(TAG, "onVerificationFailed", e)
                            Toast.makeText(
                                baseContext, "Verification Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            Log.d(TAG, "onCodeSent:$verificationId")
                            Toast.makeText(
                                baseContext, "Code Sent",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@RegisterUser, OTPVerification::class.java)
                            intent.putExtra("verificationId", verificationId)
                            intent.putExtra("phoneNumber", ccp.fullNumberWithPlus)
                            intent.putExtra("name", name.text.toString())
                            intent.putExtra("email", email.text.toString())
                            intent.putExtra("password", password.text.toString())
                            startActivity(intent)
                        }
                    })
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            }
        }


    }
}
