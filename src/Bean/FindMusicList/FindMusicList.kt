package Bean.FindMusicList

data class FindMusicList(
    val code: Int,
    val more: Boolean,
    val playlist: List<Playlist>
)

data class MusicList(val name:String,
                     val id:Long)