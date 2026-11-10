package vn.edu.vlu.lab10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import vn.edu.vlu.lab10.databinding.ActivityAuthBinding

/**
 * AuthActivity: Email + Password registration / login with Firebase Authentication.
 * - View Binding only, no findViewById().
 * - Skips straight to MainActivity when the previous session is still valid (auto-login).
 */
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    // FirebaseAuth singleton: keeps the signed-in session on the device
    private lateinit var auth: FirebaseAuth

    // Current mode: true = Register, false = Login
    private var isRegisterMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // (1) Auto-login: Firebase restores currentUser from its stored refresh token
        if (auth.currentUser != null) {
            goToMain()
            return // no need to set up the form
        }

        setupModeToggle()
        setupSubmitButton()
        setupForgotPassword()
    }

    /** Login <-> Register tab switch: shows or hides the fields of each mode. */
    private fun setupModeToggle() {
        binding.toggleAuthMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener // ignore the "unchecked" event of the other tab

            isRegisterMode = checkedId == binding.btnTabRegister.id

            // Confirm password only in Register mode, "Forgot password?" only in Login mode
            binding.tilConfirmPassword.visibility = if (isRegisterMode) View.VISIBLE else View.GONE
            binding.tvForgotPassword.visibility = if (isRegisterMode) View.GONE else View.VISIBLE

            binding.btnSubmit.text = getString(if (isRegisterMode) R.string.btn_register else R.string.btn_login)

            clearErrors()
        }
    }

    /** Submit: validate on the device first, then call Firebase. */
    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirm = binding.etConfirmPassword.text.toString()

            // Client-side checks save a network call and answer instantly
            if (!validateInput(email, password, confirm)) return@setOnClickListener

            if (isRegisterMode) registerUser(email, password) else loginUser(email, password)
        }
    }

    /**
     * Returns true when the input is valid. Catches: empty or badly formatted email,
     * password shorter than 6 characters, confirm password that does not match.
     */
    private fun validateInput(email: String, password: String, confirm: String): Boolean {
        clearErrors()
        var isValid = true

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.error_email_empty)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.error_email_format)
            isValid = false
        }

        // Firebase rejects passwords shorter than 6 characters
        if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.error_password_short)
            isValid = false
        }

        if (isRegisterMode && password != confirm) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch)
            isValid = false
        }
        return isValid
    }

    /** Creates a new account; Firebase signs the new user in at once. */
    private fun registerUser(email: String, password: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.msg_register_ok, Toast.LENGTH_SHORT).show()
                    goToMain()
                } else {
                    handleAuthError(task.exception)
                }
            }
    }

    /** Signs in with an existing account. */
    private fun loginUser(email: String, password: String) {
        showLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, R.string.msg_login_ok, Toast.LENGTH_SHORT).show()
                    goToMain()
                } else {
                    handleAuthError(task.exception)
                }
            }
    }

    /** Challenge 1: sends a password reset link to the email typed in the form. */
    private fun setupForgotPassword() {
        binding.tvForgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            clearErrors()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.error_reset_email)
                return@setOnClickListener
            }

            showLoading(true)
            auth.sendPasswordResetEmail(email).addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.msg_reset_sent, email), Toast.LENGTH_LONG).show()
                } else {
                    handleAuthError(task.exception)
                }
            }
        }
    }

    /**
     * Turns each Firebase exception into a message the user understands.
     * Order matters: FirebaseAuthWeakPasswordException is a subclass of FirebaseAuthInvalidCredentialsException.
     */
    private fun handleAuthError(exception: Exception?) {
        Log.w(TAG, "Firebase Auth failed", exception)
        when (exception) {
            // WEAK_PASSWORD: under 6 characters or too simple
            is FirebaseAuthWeakPasswordException ->
                binding.tilPassword.error = getString(R.string.error_weak_password)

            // Badly formatted email, or wrong email / password. With email enumeration protection
            // (on by default) Firebase reports both wrong email and wrong password this way,
            // so one generic message is shown, which is also safer.
            is FirebaseAuthInvalidCredentialsException ->
                if (exception.errorCode == "ERROR_INVALID_EMAIL") {
                    binding.tilEmail.error = getString(R.string.error_invalid_email)
                } else {
                    Toast.makeText(this, R.string.error_wrong_credentials, Toast.LENGTH_LONG).show()
                }

            // The email already has an account
            is FirebaseAuthUserCollisionException ->
                binding.tilEmail.error = getString(R.string.error_email_taken)

            // Account deleted or disabled
            is FirebaseAuthInvalidUserException ->
                Toast.makeText(this, R.string.error_user_disabled, Toast.LENGTH_LONG).show()

            // No connection (Airplane Mode, Wi-Fi off)
            is FirebaseNetworkException ->
                Toast.makeText(this, R.string.error_network, Toast.LENGTH_LONG).show()

            // Anything else: show the original message to make debugging easier
            else ->
                Toast.makeText(this, getString(R.string.error_generic, exception?.localizedMessage), Toast.LENGTH_LONG)
                    .show()
        }
    }

    /** Shows the progress circle and locks the buttons so one tap sends one request. */
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !isLoading
        binding.tvForgotPassword.isEnabled = !isLoading
    }

    private fun clearErrors() {
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
    }

    /** Opens MainActivity and clears the back stack, so Back does not return to the login screen. */
    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private companion object {
        const val TAG = "AuthActivity"
    }
}
