(ns previewradio-pedestal.itunes
  (:require clojure.string
            [clojure.data.json :as json]))

;;; TODO: encode params correctly
(defn- lookup-url
  ([id] (lookup-url id {}))
  ([id params]
     (format "https://itunes.apple.com/lookup?%s"
             (->> (assoc params :id id)
                  (map (fn [[k v]] (format "%s=%s" (name k) v)))
                  (clojure.string/join "&")))))
(defn json-slurp [url]
  (-> url
      slurp
      (json/read-str :key-fn keyword)
      (get :results)))

(defn lookup [id]
  (-> (lookup-url id)
      json-slurp
      first))

(defn ->album [m]
  {:id (:collectionId m)
   :name (:collectionName m)
   :artist (:artistName m)
   :view_url (:collectionViewUrl m)})

(defn album-search [term]
  (->> (format "https://itunes.apple.com/search?term=%s&media=music&entity=album&limit=10"
               (clojure.string/replace term " " "+"))
       json-slurp
       (map ->album)))