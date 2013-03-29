PreviewRadio.AlbumPlayer = function(_album) {
  var player     = null,
      control    = null,
      album_view = null,
      next_album = null,
      songs      = []

  function init(album) {
    player     = $("audio")
    control    = $("#menu #control")
    album_view = new PreviewRadio.AlbumView()

    start(album)
    bind()
    update()
    player[0].load()
    player[0].play()
  }

  function start(album) {
    fetch_next(album)
    songs = order_by_track_number(album.songs)
    album_view.set_album(album)
    album_view.render()
    player[0].volume = 0
  }

  function order_by_track_number(songs) {
    return songs.sort(function(a,b) { return a.track_number > b.track_number })
  }

  function bind() {
    player.on("ended", ended)
    player.on("timeupdate", timeupdate)
    control.on("click", toggle)
  }

  function ended() {
    update()
    player[0].pause()
    player[0].volume = 0
    player[0].load()
    player[0].play()
  }

  function timeupdate() {
    if (player[0].currentTime < 2.0)
      fadein()
    else if ((player[0].duration - player[0].currentTime) < 2.0)
      fadeout()
    else
      player[0].volume = 1.0

    album_view.set_progress((this.currentTime / this.duration) * 100)
  }

  function fadein() {
    player[0].volume = (player[0].currentTime / 2.0)
  }

  function fadeout() {
    player[0].volume = (player[0].duration - player[0].currentTime) / 2.0
  }

  function toggle() {
    if (control.hasClass("stop")) {
      player[0].pause()
      control.removeClass("stop")
    } else {
      player[0].play()
      control.addClass("stop")
    }
  }

  function update() {
    if (songs.length == 0)
      start(next_album)

    set_active_song(songs.shift())
  }

  function fetch_next(_album) {
    $.getJSON("/previews/" + _album.id + "/next", function(album) {
      set_next_album(album)
    })
  }

  function set_next_album(album) {
    next_album = album
  }

  function set_active_song(song) {
    player.find("source").attr("src", song.preview_url)
    album_view.set_active_song(song)
  }

  init(_album)
}
