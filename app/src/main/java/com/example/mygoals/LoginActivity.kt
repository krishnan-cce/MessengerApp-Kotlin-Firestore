package com.example.mygoals

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager

import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        et_login.setOnClickListener{
            logInRegisteredUser()
        }

        et_register.setOnClickListener {
            RegisterActivity()
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password_register.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password_register.text.toString().trim { it <= ' ' }


            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()

                    if (task.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun RegisterActivity()
    {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

}