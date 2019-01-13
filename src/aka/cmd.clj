(ns aka.cmd
  "Implementations of dispatched subcommands."
  (:require [aka.log :as log]
            [aka.taps :as t]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]))

(defn ->aka
  "Parses a command line alias option."
  [aka]
  (when aka
    (keyword (-> (str/replace aka #"^-A:?|:" "")
                 (str/replace #"/$" "")))))

(defmulti run
  "Dispatches on cmd."
  (fn [cmd _ _] cmd))

(defn parse-tap-args
  ""
  [[arg1 arg2 arg3 arg4]]
  (if (= "-o" arg1)
    [arg3 arg4 arg2]
    [arg1 arg2]))

(defmethod run :aka/tap
  [_ taps args]
  (let [[tap url deps-edn] (parse-tap-args args)]
    (cond
      (not tap)
      (do
        (log/infof "Error: Missing alias prefix\n")
        (run :aka/describe taps [":aka/tap"]))

      (not url)
      (do
        (log/infof "Error: Missing tap-file\n")
        (run :aka/describe taps [":aka/tap"]))

      :else
      (let [tap (->aka tap)]
        (t/tappity! taps tap url deps-edn)))))

(defmethod run :aka/read
  [_ taps [aka]]
  (let [aka (->aka aka)
        definition (t/tapped taps aka)]
    (if definition
      (pprint
       {:aliases
        {aka (dissoc
               (t/tapped taps aka)
               :description :usage)}})
      (do
        (when-not (#{:-h :--help} aka)
          (log/infof "No alias found for '%s' (is it tapped?)\n" aka)
          (log/infof "Available aliases:")
          (run :aka/describe taps [":aka/"]))
        (System/exit 1)))))

(defn ns-matches?
  ""
  [tap]
  (if tap
    (fn [[aka]]
      (str/starts-with? (namespace aka) (name tap)))
    (constantly true)))

(defn exact-match?
  ""
  [tap]
  (fn [[aka]]
    (= tap aka)))

(defmethod run :aka/describe
  [_ _ [tap]]
  (let [tap (->aka tap)
        [[aka {:keys [description usage] :as matching-tap}]] (filter (exact-match? tap) (t/taps))]
    (if matching-tap
      (if usage
        (do
          (when description
            (log/infof "%s\n" description))
          (log/infof "%s" (str/join "\n" usage)))
        (if description
            (log/infof "%s - %s" aka description)
            (log/infof "%s - (no description) %s" aka matching-tap)))
      (let [matching-taps (filter (ns-matches? tap) (t/taps))]
        (doseq [[aka {:keys [description] :as matching-tap}] matching-taps]
          (if description
            (log/infof "%s - %s" aka description)
            (log/infof "%s - (no description) %s" aka matching-tap)))))))

(defmethod run :inject
  [_ _ [tap deps-edn]]
  (let [tap (->aka tap)
          [[aka {:keys [description usage] :as matching-tap}]] (filter (exact-match? tap) (t/taps))]
      (if matching-tap
       (do
        ()))))

(defmethod run :default
  [_ taps _]
  (run :aka/describe taps [":aka/"]))
