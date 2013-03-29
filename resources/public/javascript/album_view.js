PreviewRadio.AlbumView = function() {
  var view        = null,
      artwork     = null,
      artist      = null,
      title       = null,
      link        = null,
      playlist    = null,
      songs       = {},
      active_song = null

  function init() {
    view = $("#album-view")
    artwork = view.find("#artwork")
    artist = view.find("#artist")
    title = view.find("#album-title")
    link = view.find("#view-in-itunes")
    playlist = view.find("#playlist")
  }

  function render() {
    playlist.empty()

    $.each(songs, function(id, song) {
      playlist.append(song.render())
    })
  }

  function set_active_song(song) {
    active_song = songs[song.id]
    active_song.set_active()
  }

  function set_progress(progress) {
    active_song.set_progress(progress)
    active_song.draw()
  }

  function set_playlist(_songs) {
    songs = {}

    for (var i = 0; i < _songs.length; i++)
      add_song(_songs[i])
  }

  function add_song(song) {
    songs[song.id] = new PreviewRadio.AlbumPlayerRow(song)
  }

  function set_artwork(url) {
    artwork.attr("src", resize(url))
  }

  function resize(url) {
    return url.replace("100x100", "170x170")
  }

  function set_artist(_artist) {
    artist.text(_artist)
  }

  function set_title(_title) {
    title.text(_title)
  }

  function set_link(_link) {
    link.attr("href", _link)
  }

  this.render = function() {
    render()
  }

  this.set_album = function(album) {
    set_artwork(album.artwork_url100)
    set_artist(album.artist)
    set_title(album.name)
    set_link(album.view_url)
    set_playlist(album.songs)
  }

  this.set_active_song = function(song) {
    set_active_song(song)
  }

  this.set_progress = function(progress) {
    set_progress(progress)
  }

  init()
}
