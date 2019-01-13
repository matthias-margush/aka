(ns aka.taps
  "Conveniences for managing tools.deps aliases."
  (:refer-clojure :exclude [alias])
  (:require [aka.log :as log]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.pprint :refer [pprint]]))

(defn tapped
  ""
  [taps aka]
  (get-in taps [:aka :taps aka]))

(defn namespaced
  ""
  [tap]
  (fn [[k v]] [(keyword (name tap) (name k)) v]))

(defn aliases
  ""
  [tap aka]
  (into {}
        (->> aka
             (map (namespaced tap))
             (map (fn [[k v]] [k (dissoc v :description :usage)])))))

(defn tap
  "Taps an aka inventory."
  [taps tap aka]
  (let [new-taps (into {} (map (namespaced tap) aka))]
    (doseq [[alias] new-taps]
      (log/infof "Tapped %s" alias))
    (update-in taps [:aka :taps] merge
              new-taps)))

(def ^:private
  tap-file
  ""
  (str (System/getProperty "user.home")
       "/.clojure/aka.edn"))

(defn slurp-edn
  ""
  [f]
  (when (.exists (io/file f))
    (edn/read-string
      (slurp f))))

(defn tappity!
  ""
  ([taps tapX aka]
   (tappity! taps tapX aka nil))
  ([taps tapX aka deps-edn]
   (let [aka (edn/read-string (slurp aka))]
     (let [taps (tap taps tapX aka)]
       (with-open [w (io/writer tap-file)]
         (pprint taps w))
       (when deps-edn
         (let [deps (slurp-edn deps-edn)]
           (with-open [w (io/writer deps-edn)]
             (pprint
               (update deps :aliases merge (aliases tapX aka))
               w))))))))

(defn registry
  "Loads the local registry of taps, and ensures that the core aliases are
  registered."
  []
  (let [taps (slurp-edn tap-file)]
    (if (get-in taps [:aka :taps :aka/tap])
      taps
      (do
        (tappity! taps :aka (io/resource "aka.edn"))
        (registry)))))

(defn taps
  ""
  []
  (get-in (registry) [:aka :taps]))
