(ns myapp.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [myapp.layout :refer [error-page]]
            [myapp.routes.home :refer [home-routes]]
            [myapp.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [myapp.env :refer [defaults]]
            [mount.core :as mount]
            [myapp.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    #'service-routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
