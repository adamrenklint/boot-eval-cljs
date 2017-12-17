(def project 'adamrenklint/boot-eval-cljs)
(def version "1.2.1")

(set-env!
 :source-paths #{"src"}
 :dependencies '[[adamrenklint/boot-exec "1.0.1"]
                 [adzerk/boot-cljs       "2.1.4"]
                 [degree9/boot-nodejs    "1.2.1"]
                 [org.clojure/clojurescript "1.9.946" :scope "test"]
                 [adzerk/bootlaces       "0.1.13" :scope "test"]
                 [tolitius/boot-check    "0.1.6"  :scope "test"]
                 [adamrenklint/boot-fmt  "1.3.0"  :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[adamrenklint.boot-eval-cljs :refer [eval-cljs]]
         '[adamrenklint.boot-fmt :refer [fmt]]
         '[adamrenklint.boot-exec :refer [exec]]
         '[tolitius.boot-check :as check])

(bootlaces! version)

(ns-unmap 'boot.user 'test)
(ns-unmap 'boot.user 'format)

(deftask deps [])

(deftask release []
  (comp (build-jar)
        (push-release)
        (exec :cmd "git push --tags")))

(deftask check []
  (comp (check/with-yagni)
        (check/with-eastwood)
        (check/with-kibit)
        (check/with-bikeshed)))

(deftask format []
  (fmt))

(deftask test []
  (merge-env! :source-paths #{"test"})
  (eval-cljs :fn 'adamrenklint.boot-eval-cljs-test/main
             :compiler-options {:verbose true}))

(task-options!
  pom {:project     project
       :version     version
       :description "Boot task to compile a ClojureScript function and evaluate in Node.js"
       :url         "https://github.com/adamrenklint/boot-eval-cljs"
       :scm         {:url "https://github.com/adamrenklint/boot-eval-cljs"}
       :license     {"MIT" "https://github.com/adamrenklint/boot-eval-cljs/blob/master/LICENSE"}})
