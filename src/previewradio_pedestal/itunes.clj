(ns previewradio-pedestal.itunes
  (:require clojure.string
            [clojure.data.json :as json]))

;;; TODO: encode params correctly
(defn- lookup-url
  [id params]
  (format "https://itunes.apple.com/lookup?%s"
          (->> (assoc params :id id)
               (map (fn [[k v]] (format "%s=%s" (name k) v)))
               (clojure.string/join "&"))))

(defn- url->json-results [url]
  (-> url
      slurp
      (json/read-str :key-fn keyword)
      (get :results)))

(defn ->album [m]
  {:id (:collectionId m)
   :name (:collectionName m)
   :artist (:artistName m)
   :artwork_url100 (:artworkUrl100 m)
   :view_url (:collectionViewUrl m)})

(defn ->song [m]
  {:id (:trackId m)
   :name (:trackName m)
   :album (:collectionName m)
   :artist (:artistName m)
   :track_number (:trackNumber m)
   :preview_url (:previewUrl m)
   :view_url (:trackViewUrl m)})

(defn find-album
  "Given an album id, returns an album map."
  ([id] (find-album id {}))
  ([id params]
     (let [results (url->json-results (lookup-url id params))
           album (->album (first results))
           songs (map ->song (rest results))]
       (assoc album :songs songs))))

(defn album-search
  "Given a search term, returns search results as a list of album maps."
  [term]
  (->> (format "https://itunes.apple.com/search?term=%s&media=music&entity=album&limit=10"
               (clojure.string/replace term " " "+"))
       url->json-results
       (map ->album)))

(defn- related-album-urls [album]
  (let [body (slurp (:view_url album))
        related-albums-section
          (or
           (re-find #"(?s)Titledbox_Listeners Also Bought.*id=\"left-stack\"" body)
           (throw (ex-info "No related albums section found for album" {:album album})))
        related-albums (->>
                        related-albums-section
                        (re-seq #"(https://itunes.apple.com\S+)\"\s+class=\"artwork-link\"")
                        (map second))]
    (when (empty? related-albums)
      (throw (ex-info "No related albums found for album" {:album album})))
    related-albums))

(defn related-album
  "Given an album id, returns a random related album using the 'Listeners Also Bought'
section of an album's page."
  [id]
  (let [url (-> id
                find-album
                related-album-urls
                shuffle
                first)
        id (or (re-find #"(?<=id)\d+" url)
               (throw (ex-info "No id found in album url" {:url url})))]
    (find-album id {:entity "song"})))