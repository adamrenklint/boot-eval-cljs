(ns adamrenklint.boot-eval-cljs
  {:boot/export-tasks true}
  (:require [clojure.java.io :as io]
            [boot.core :as core]
            [boot.util :as util]
            [boot.task.built-in :refer [target]]
            [clojure.string :as string]
            [adamrenklint.boot-exec :refer [exec]]
            [adzerk.boot-cljs :refer [cljs]]
            [degree9.boot-nodejs :refer [cljs-edn]]))

(defn missing-source-map-support-lib? []
  (not (.exists (io/file "./target/node_modules/source-map-support/package.json"))))

(core/deftask eval-cljs
  "Compile ClojureScript function and evaluate in Node.js"
  [f fn FUNCTION sym "Symbol for function to evaluate"]
  (assert fn "Must provide symbol for function to evaluate")
  (let [ns (namespace fn)
        name (string/replace ns "-" "_")]
    (comp (cljs-edn :edn name
                    :require [(symbol ns)]
                    :init-fns [fn]
                    :target :nodejs
                    :compiler-options {:infer-externs  true
                                       :compiler-stats true
                                       :parallel-build true
                                       :target :nodejs})
          (cljs :ids #{name})
          (target :no-clean true)
          (exec :cmd "npm install source-map-support --no-package-lock --silent"
                :dir "target"
                :pred-fn 'adamrenklint.boot-eval-cljs/missing-source-map-support-lib?)
          (core/with-pass-thru _ (util/info (str "\nEvaluating " fn "\n")))
          (exec :cmd (str "node --stack-trace-limit=100 " name ".js")
                :dir "target"))))
