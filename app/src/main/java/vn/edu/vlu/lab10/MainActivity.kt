package vn.edu.vlu.lab10

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import vn.edu.vlu.lab10.databinding.ActivityMainBinding

/** MainActivity: photo gallery loaded with Glide, circular avatar, Logout in the toolbar. */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Guard: signed out or session revoked -> back to the login screen
        val user = auth.currentUser
        if (user == null) {
            navigateToAuth()
            return
        }

        binding.tvWelcome.text = getString(R.string.welcome, user.email)

        setupRecyclerView()
        setupLogoutAction()
        loadAvatar(user.email)
    }

    /** Vertical list with the 10 sample photos. */
    private fun setupRecyclerView() {
        binding.rvPhotos.layoutManager = LinearLayoutManager(this)
        binding.rvPhotos.adapter = PhotoAdapter(PhotoDataSource.getPhotos())
        // The list size never changes the RecyclerView size: skips extra layout passes
        binding.rvPhotos.setHasFixedSize(true)
    }

    /** Logout action in the toolbar: clears the Firebase session, then returns to AuthActivity. */
    private fun setupLogoutAction() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId != R.id.action_logout) return@setOnMenuItemClickListener false
            auth.signOut() // removes the stored session and tokens
            Toast.makeText(this, R.string.msg_logged_out, Toast.LENGTH_SHORT).show()
            navigateToAuth()
            true
        }
    }

    /** Challenge 2: avatar from a URL, cropped into a circle with circleCrop(). */
    private fun loadAvatar(email: String?) {
        // The email is the "seed", so every account always gets the same unique avatar
        val seed = email ?: "guest"
        val avatarUrl = "https://i.pravatar.cc/200?u=$seed"

        Glide.with(this)
            .load(avatarUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_broken_image)
            .circleCrop() // transforms the image into a circle
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.ivAvatar)
    }

    /** Opens AuthActivity and clears the whole back stack, so Back cannot return to the gallery. */
    private fun navigateToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
