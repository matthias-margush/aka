(ns aka.log)

(defn infof
  [& args]
  (binding [*out* *err*]
    (println (apply format args))))
