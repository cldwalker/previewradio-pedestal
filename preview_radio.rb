require "sinatra/base"
require "ostruct"
require "net/http"
require "json"
require "mechanize"

module PreviewRadio
  class Application < Sinatra::Base
    get "/" do
      erb :index
    end

    post "/previews/search" do
      @albums = Album.search(params[:query])
      erb :search
    end

    get "/previews/:id/next" do |id|
      album = AlbumRelator.new(Album.find(id)).album
      album.to_json
    end

    get "/previews/:id" do |id|
      @album = AlbumRelator.new(Album.find(id)).album
      erb :preview
    end
  end

  module Underscore
    def underscore(key)
      key.to_s.gsub(/([A-Z])/, '_\1').downcase.to_sym
    end
  end

  module OpenStructAsJson
    def to_json(*args)
      JSON.dump(marshal_dump)
    end
  end

  class Album < OpenStruct
    include Underscore
    include OpenStructAsJson

    def self.find(id)
      results = Itunes.lookup(id, :entity => :song)
      album   = Album.new(results.shift)
      album.songs = results.map { |r| Song.new(r) }
      album
    end

    def self.search(query)
      Itunes.search(query).map { |album| new(album) }
    end

    def initialize(params={})
      params = Hash[params.map { |k,v| [underscore(k), v] }]
      params[:id] = params.delete(:collection_id)
      params[:name] = params.delete(:collection_name)
      params[:artist] = params.delete(:artist_name)
      params[:view_url] = params.delete(:collection_view_url)
      super(params)
    end
  end

  class Song < OpenStruct
    include Underscore
    include OpenStructAsJson

    def initialize(params={})
      params = Hash[params.map { |k,v| [underscore(k), v] }]
      params[:id] = params.delete(:track_id)
      params[:name] = params.delete(:track_name)
      params[:album] = params.delete(:collection_name)
      params[:artist] = params.delete(:artist_name)
      params[:view_url] = params.delete(:track_view_url)
      super(params)
    end
  end

  class AlbumRelator
    attr_reader :agent, :_album

    def initialize(album)
      @agent  = Mechanize.new
      @_album = album
    end

    def album
      Album.find(album_id)
    end

    def album_id
      link[:href].match(/id(\d+)$/)[1]
    end

    def link
      groups[rand(groups.size)].at(".artwork-link")
    end

    def groups
      container.css("div[role='group']")
    end

    def container
      page = agent.get(_album.view_url)
      page.at("div[metrics-loc='Titledbox_Listeners Also Bought']")
    end
  end

  module Itunes
    extend self

    def find(id)
      lookup(id).first
    end

    def lookup(id, params={})
      url = lookup_url(id, params)
      uri = URI.parse(url)
      request(uri)
    end

    def search(term)
      request(URI.parse(search_url(term)))
    end

    private

    def request(uri)
      http = Net::HTTP.new(uri.host, uri.port)
      http.use_ssl = true
      http.verify_mode = OpenSSL::SSL::VERIFY_NONE

      request  = Net::HTTP::Get.new(uri.request_uri)
      response = http.request(request)
      JSON.parse(response.body)["results"]
    end

    def search_url(term)
      "https://itunes.apple.com/search?term=#{term.split(" ").join("+")}&media=music&entity=album&limit=10"
    end

    def lookup_url(id, params={})
      params[:id] = id
      params = params.map { |k,v| "#{k}=#{v}" }.join("&")
      "https://itunes.apple.com/lookup?#{params}"
    end
  end
end
