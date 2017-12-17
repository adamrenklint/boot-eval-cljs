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

(def default-compiler-options
  {:infer-externs  true
   :compiler-stats true
   :parallel-build true
   :target :nodejs})

(core/deftask eval-cljs
  "Compile a ClojureScript function and evaluate in Node.js"
  [f fn FN sym "Symbol for function to evaluate"
   c compiler-options OPTS edn "Options to pass to the ClojureScript compiler"]
  (assert fn "Must provide symbol for function to evaluate")
  (let [ns (namespace fn)
        name (string/replace ns "-" "_")
        dir "target"]
    (comp (cljs-edn :edn name
                    :require [(symbol ns)]
                    :init-fns [fn]
                    :target :nodejs
                    :compiler-options (merge default-compiler-options
                                             compiler-options))

          (cljs :ids #{name})
          (target :dir #{dir} :no-clean true)
          (exec :cmd "npm install source-map-support --no-package-lock --silent"
                :dir dir
                :pred-fn 'adamrenklint.boot-eval-cljs/missing-source-map-support-lib?)
          (core/with-pass-thru _ (util/info (str "\nEvaluating " fn "\n")))
          (exec :cmd (str "node --stack-trace-limit=100 " name ".js")
                :dir dir))))
