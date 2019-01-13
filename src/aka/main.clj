(ns aka.main
  "Conveniences for managing tools.deps aliases."
  (:refer-clojure :exclude [alias])
  (:require [aka.cmd :as cmd]
            [aka.taps :as t]))

(defn -main
  ""
  [& [cmd & args]]
  (let [taps (t/registry)
        cmd (cmd/->aka cmd)]
    (cmd/run cmd taps args)))
