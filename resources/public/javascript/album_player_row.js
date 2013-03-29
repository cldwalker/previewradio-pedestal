PreviewRadio.AlbumPlayerRow = function(song) {
  var id       = null,
      title    = null,
      progress = null,
      view     = $('<li class="song"><div class="progress"></div><p class="title"></p></li>')

  function init(song) {
    view.attr("id", song.id)
    set_title(song.name)
  }

  function set_title(_title) {
    title = _title
  }

  function set_progress(_progress) {
    progress = _progress
  }

  function set_active() {
    $(".progress").hide()
    view.find(".progress").show()
  }

  function draw() {
    view.find(".title").text(title)
    view.find(".progress").animate({ width: Math.ceil(progress) + "%" })
  }

  function render() {
    draw()
    return view
  }

  this.render = function() {
    return render()
  }

  this.draw = function() {
    return draw()
  }

  this.set_title = function(_title) {
    return set_title(_title)
  }

  this.set_progress = function(progress) {
    set_progress(progress)
  }

  this.set_active = function() {
    set_active()
  }

  init(song)
}
