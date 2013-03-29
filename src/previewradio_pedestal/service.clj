(ns previewradio-pedestal.service
    (:require [io.pedestal.service.http :as bootstrap]
              [io.pedestal.service.http.route :as route]
              [io.pedestal.service.http.body-params :as body-params]
              [io.pedestal.service.http.route.definition :refer [defroutes]]
              [io.pedestal.service.interceptor :as interceptor]
              [comb.template :as comb]
              [clojure.java.io :as io]
              [ring.util.response :as ring-resp]))

(interceptor/defon-response html-content-type
  [response]
  (ring-resp/content-type response "text/html"))
5
(defn- render
  ([template] (render template {}))
  ([template template-bindings]
     (comb/eval (slurp (io/resource template)) template-bindings)))

(defn- response-with-layout [& args]
  (ring-resp/response (render "public/layout.erb" {:yield (apply render args)})))

(defn test-page
  [request]
  (ring-resp/response "Hello World!"))

(defn home-page
  [request]
  (response-with-layout "public/index.erb"))

(defn search-page
  [request]
  (let [albums
        [{:artist "Timbalada", :name "Pure Brazil: The Best of Timbalada", :id 22704866} {:artist "Timbalada", :name "Axé Bahia", :id 263721902} {:artist "Timbalada", :name "Mãe de Samba", :id 263715941} {:artist "Timbalada", :name "Alegria Original", :id 388180677} {:artist "Timbaland", :name "Shock Value", :id 250012892} {:artist "Various Artists", :name "Pure Brazil 2: Rio Bahia Carnival", :id 128666852} {:artist "Timbaland", :name "Break Ya Back (feat. Dev) - Single", :id 517307604} {:artist "Various Artists", :name "Pure Brazil 2: Dance of the Orixás", :id 134754575} {:artist "Justin Timberlake", :name "FutureSex/LoveSounds", :id 310757916} {:artist "Kiriaka", :name "Live At Musikens Hus 2009 - EP", :id 488870402}]]
    (response-with-layout "public/search.erb" {:albums albums})))

(defn preview-page
  [request]
  (let [album-json "{\"wrapper_type\":\"collection\",\"collection_type\":\"Album\",\"artist_id\":101845783,\"amg_artist_id\":736692,\"collection_censored_name\":\"Fantastica Batucada\",\"artist_view_url\":\"https://itunes.apple.com/us/artist/escola-samba-nocidade-independante/id101845783?uo=4\",\"artwork_url60\":\"http://a5.mzstatic.com/us/r1000/000/Music/d2/9e/db/mzi.hzsqwxgc.60x60-50.jpg\",\"artwork_url100\":\"http://a3.mzstatic.com/us/r1000/000/Music/d2/9e/db/mzi.hzsqwxgc.100x100-75.jpg\",\"collection_price\":6.99,\"collection_explicitness\":\"notExplicit\",\"track_count\":10,\"copyright\":\"℗ 2005 IRIS MUSIC\",\"country\":\"USA\",\"currency\":\"USD\",\"release_date\":\"2005-06-09T07:00:00Z\",\"primary_genre_name\":\"World\",\"id\":101846315,\"name\":\"Fantastica Batucada\",\"artist\":\"Escola De Samba Nocidade Independante De Padre Miguel\",\"view_url\":\"https://itunes.apple.com/us/album/fantastica-batucada/id101846315?uo=4\"}"]
    (response-with-layout "public/preview.erb" {:album-json album-json})))

(defn preview-next-page
  [request]
  (ring-resp/response "[]"))

(defroutes routes
  [[["/" {:get home-page}
     ^:interceptors [html-content-type]
     ["/previews/search" {:post search-page}]
     ["/previews/:id" {:get preview-page}]
     ["/previews/:id/next" {:get preview-next-page}]
     ["/test" {:get test-page}]]]])

;; You can use this fn or a per-request fn via io.pedestal.service.http.route/url-for
(def url-for (route/url-for-routes routes))

;; Consumed by previewradio-pedestal.server/create-server
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes
              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"
              ;; Either :jetty or :tomcat (see comments in project.clj
              ;; to enable Tomcat)
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
