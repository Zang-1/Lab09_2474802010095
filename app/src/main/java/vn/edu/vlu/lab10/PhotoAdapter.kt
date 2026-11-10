package vn.edu.vlu.lab10

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import vn.edu.vlu.lab10.databinding.ItemPhotoBinding

/**
 * PhotoAdapter: bridge between the PhotoItem list and the RecyclerView.
 * Each row is built from item_photo.xml through ItemPhotoBinding (View Binding).
 */
class PhotoAdapter(
    private val photos: List<PhotoItem>
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    /** Holds the binding of one row so it is reused instead of looking views up on every scroll. */
    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    /** Creates a new row when the RecyclerView needs one. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false // RecyclerView attaches the row itself
        )
        return PhotoViewHolder(binding)
    }

    /** Binds the photo at [position] to the row. */
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        val context = holder.itemView.context

        holder.binding.tvTitle.text = photo.title
        holder.binding.tvAuthor.text = context.getString(R.string.photo_author, photo.author)

        // Glide downloads and decodes on a background thread, so the UI never blocks
        Glide.with(context)                                       // request follows the row's lifecycle
            .load(photo.imageUrl)                                 // image URL
            .placeholder(R.drawable.ic_placeholder)               // shown while loading
            .error(R.drawable.ic_broken_image)                    // shown when loading fails (bad URL, offline)
            .transition(DrawableTransitionOptions.withCrossFade()) // smooth fade-in
            .diskCacheStrategy(DiskCacheStrategy.ALL)             // cache the original and the resized image on disk
            .into(holder.binding.ivPhoto)
    }

    override fun getItemCount(): Int = photos.size

    /**
     * A recycled row may still have a request running for its old photo:
     * cancel it so the wrong image never appears in the reused row and memory is freed.
     */
    override fun onViewRecycled(holder: PhotoViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.binding.ivPhoto)
    }
}
