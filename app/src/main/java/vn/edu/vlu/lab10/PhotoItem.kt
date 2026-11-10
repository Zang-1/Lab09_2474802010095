package vn.edu.vlu.lab10

/**
 * PhotoItem: data model of one photo in the list.
 * A data class generates equals(), hashCode(), toString() and copy().
 */
data class PhotoItem(
    val id: Int,          // Photo id
    val title: String,    // Display title
    val author: String,   // Author name
    val imageUrl: String  // Image URL (must be https://)
)

/**
 * PhotoDataSource: mock data source with 10 high-quality photos from Picsum Photos.
 * URL format: https://picsum.photos/id/{id}/{width}/{height}
 * An object (singleton), so it is called directly as PhotoDataSource.getPhotos().
 */
object PhotoDataSource {

    fun getPhotos(): List<PhotoItem> = listOf(
        PhotoItem(1, "Sunrise on the mountain", "Picsum Studio", "https://picsum.photos/id/10/800/600"),
        PhotoItem(2, "Foggy pine forest", "Picsum Studio", "https://picsum.photos/id/11/800/600"),
        PhotoItem(3, "Sunset beach", "Picsum Studio", "https://picsum.photos/id/12/800/600"),
        PhotoItem(4, "Autumn road", "Picsum Studio", "https://picsum.photos/id/14/800/600"),
        PhotoItem(5, "Peaceful lake", "Picsum Studio", "https://picsum.photos/id/15/800/600"),
        PhotoItem(6, "Deep blue ocean", "Picsum Studio", "https://picsum.photos/id/16/800/600"),
        PhotoItem(7, "Spring valley", "Picsum Studio", "https://picsum.photos/id/17/800/600"),
        PhotoItem(8, "Coastal cliff", "Picsum Studio", "https://picsum.photos/id/18/800/600"),
        PhotoItem(9, "Ripe rice field", "Picsum Studio", "https://picsum.photos/id/19/800/600"),
        PhotoItem(10, "Waterfall in the forest", "Picsum Studio", "https://picsum.photos/id/20/800/600")
    )
}
