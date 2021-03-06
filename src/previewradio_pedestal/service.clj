(ns previewradio-pedestal.service
    (:require [io.pedestal.service.http :as bootstrap]
              [io.pedestal.service.http.route :as route]
              [io.pedestal.service.http.body-params :as body-params]
              [io.pedestal.service.http.route.definition :refer [defroutes]]
              [io.pedestal.service.interceptor :as interceptor]
              [comb.template :as comb]
              [clojure.java.io :as io]
              [previewradio-pedestal.itunes :as itunes]
              [clojure.data.json :as json]
              [ring.util.response :as ring-resp]))

(interceptor/defon-response html-content-type
  [response]
  (ring-resp/content-type response "text/html"))

(defn- render
  "Renders an erb file with optional bindings."
  ([template] (render template {}))
  ([template template-bindings]
     (comb/eval (slurp (io/resource template)) template-bindings)))

(defn- erb
  "Given an erb file's basename, renders it wrapped in a default layout"
  [& args]
  (let [basename (format "public/%s.erb" (name (first args)))]
    (ring-resp/response (render "public/layout.erb" {:yield (apply render (cons basename (rest args)))}))))

(defn home-page
  [request]
  (erb :index))

(defn search-page
  [request]
  (let [albums (itunes/album-search (get (:params request) "query"))]
    (erb :search {:albums albums})))

(defn preview-page
  [request]
  (let [album (itunes/related-album (-> request :path-params :id))
        album-json (json/write-str album)]
    (erb :preview {:album-json album-json})))

(defn preview-next-page
  [request]
  (let [album (itunes/related-album (-> request :path-params :id))]
    (ring-resp/response (json/write-str album))))

(defroutes routes
  [[["/" {:get home-page}
     ^:interceptors [body-params/body-params html-content-type]
     ["/previews/search" {:post search-page}]
     ["/previews/:id" {:get preview-page}]
     ["/previews/:id/next" {:get preview-next-page}]]]])

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
              ::bootstrap/port (Integer. (or (System/getenv "PORT") 8080))})