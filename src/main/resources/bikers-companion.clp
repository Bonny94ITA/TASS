;;;=================
;;;   PROGETTO CLIPS
;;;=================

(defmodule MAIN (export ?ALL))

;;****************
;;* DEFFUNCTIONS *
;;****************

(deffunction MAIN::get-variables-name-with-value (?variables ?value)
  (bind ?v (create$))
  (bind ?i 1)
  (while (< ?i (length$ ?variables)) do
    (if (eq (nth$ (+ ?i 1) ?variables) ?value) then (bind ?v (insert$ ?v 1 (nth$ ?i ?variables))))
    (bind ?i (+ ?i 2)))
  return ?v)

;funzione che calcola media
(deffunction MAIN::average (?values)
  (bind ?sum 0)
  (loop-for-count (?i 1 (length$ ?values)) do
    (bind ?sum (+ ?sum (nth$ ?i ?values))))
  (bind ?avg (/ ?sum (length$ ?values)))
  return ?avg)

;;*****************
;;* INITIAL STATE *
;;*****************

(deftemplate MAIN::attribute
   (slot name)
   (multislot value)
   (slot certainty (default 100.0)))

(deftemplate MAIN::hotel-attribute
 (slot name)
 (slot certainty (type FLOAT) (default ?NONE))
 (multislot unknown-variables (type SYMBOL) (default ?NONE)))

(deftemplate MAIN::hotel
  (slot name (default ?NONE))
  (slot tr (type SYMBOL))
  (slot stars (type INTEGER) (range 1 5)))

;tipo di turismo
(deftemplate MAIN::tourism-resort
  (slot name (type SYMBOL))
  (slot region (type SYMBOL))
  (multislot type (type SYMBOL)))

;meta regole if then
(deftemplate MAIN::rule
  (slot certainty (default 100.0))
  (multislot if)
  (multislot then))

(deftemplate MAIN::tourism-type
   (slot tt (type SYMBOL)))

(deffunction MAIN::get-hotel-attribute-list ()
  (bind ?facts (find-all-facts ((?f hotel-attribute)) (> ?f:certainty 0.0))))

(defrule MAIN::start
  (declare (salience 10000))
  =>
  (set-fact-duplication TRUE)
  (focus REASONING-RULES RULES HOTELS))

(defrule MAIN::filter-hotels-for-unknown-values
  (declare (salience 100)
           (auto-focus TRUE))
  ?rem1 <- (hotel-attribute (name ?rel) (certainty ?per1) (unknown-variables $?uv1))
  ?rem2 <- (hotel-attribute (name ?rel) (certainty ?per2) (unknown-variables $?uv2))
  (test (neq ?rem1 ?rem2))
  (test (< (length$ ?uv1) (length$ ?uv2)))
  =>
  (retract ?rem2))

(defrule MAIN::filter-hotels-for-same-min-unknown-values
  (declare (salience 50)
           (auto-focus TRUE))
  ?rem1 <- (hotel-attribute (name ?rel) (certainty ?per1) (unknown-variables $?uv1))
  ?rem2 <- (hotel-attribute (name ?rel) (certainty ?per2) (unknown-variables $?uv2))
  (test (neq ?rem1 ?rem2))
  (test (and (neq (length$ ?uv1) 0) (eq ?uv1 ?uv2)))
  =>
  (retract ?rem2))

;; RULES COMBINE CERTAINTIES ;;
(defrule MAIN::combine-hotels-certainties-positive-signs
  (declare (salience 50)
           (auto-focus TRUE))
  ?rem1 <- (hotel-attribute (name ?rel) (certainty ?per1&:(>= ?per1 0)) (unknown-variables $?uv1))
  ?rem2 <- (hotel-attribute (name ?rel) (certainty ?per2&:(>= ?per2 0)) (unknown-variables $?uv2))
  (test (neq ?rem1 ?rem2))
  (test (or (and (eq (length$ ?uv1) 0) (eq (length$ ?uv2) 0)) (neq ?uv1 ?uv2)))
  =>
  (retract ?rem1)
  (modify ?rem2 (certainty (/ (- (* 100.0 (+ ?per1 ?per2)) (* ?per1 ?per2)) 100.0))))

(defrule MAIN::combine-hotels-certainties-negative-signs
  (declare (salience 50)
           (auto-focus TRUE))
  ?rem1 <- (hotel-attribute (name ?rel) (certainty ?per1&:(< ?per1 0)) (unknown-variables $?uv1))
  ?rem2 <- (hotel-attribute (name ?rel) (certainty ?per2&:(< ?per2 0)) (unknown-variables $?uv2))
  (test (neq ?rem1 ?rem2))
  (test (or (and (eq (length$ ?uv1) 0) (eq (length$ ?uv2) 0)) (neq ?uv1 ?uv2)))
  =>
  (retract ?rem1)
  (modify ?rem2 (certainty (+ (+ ?per1 ?per2) (/ (* ?per1 ?per2) 100.0)))))

(defrule MAIN::combine-hotels-certainties-opposite-signs
  (declare (salience 50)
           (auto-focus TRUE))
  ?rem1 <- (hotel-attribute (name ?rel) (certainty ?per1) (unknown-variables $?uv1))
  ?rem2 <- (hotel-attribute (name ?rel) (certainty ?per2) (unknown-variables $?uv2))
  (test (neq ?rem1 ?rem2))
  (test (or (and (eq (length$ ?uv1) 0) (eq (length$ ?uv2) 0)) (neq ?uv1 ?uv2)))
  (test (or (and (>= ?per1 0) (< ?per2 0)) (and (< ?per1 0) (>= ?per2 0))))
  =>
  (retract ?rem1)
  (bind ?mincf (min (abs ?per1) (abs ?per2)))
  (modify ?rem2 (certainty (/ (+ ?per1 ?per2) (- 1 ?mincf)))))

(defrule MAIN::combine-certainties-positive-signs
  (declare (salience 100)
           (auto-focus TRUE))
  ?rem1 <- (attribute (name ?rel&:(eq (sub-string 1 5 ?rel) "best-")) (value ?val) (certainty ?per1&:(>= ?per1 0)))
  ?rem2 <- (attribute (name ?rel&:(eq (sub-string 1 5 ?rel) "best-")) (value ?val) (certainty ?per2&:(>= ?per2 0)))
  (test (neq ?rem1 ?rem2))
  =>
  (retract ?rem1)
  (modify ?rem2 (certainty (/ (- (* 100.0 (+ ?per1 ?per2)) (* ?per1 ?per2)) 100.0))))

(defrule MAIN::combine-certainties-negative-signs
  (declare (salience 100)
           (auto-focus TRUE))
  ?rem1 <- (attribute (name ?rel&:(eq (sub-string 1 5 ?rel) "best-")) (value ?val) (certainty ?per1&:(< ?per1 0)))
  ?rem2 <- (attribute (name ?rel&:(eq (sub-string 1 5 ?rel) "best-")) (value ?val) (certainty ?per2&:(< ?per2 0)))
  (test (neq ?rem1 ?rem2))
  =>
  (retract ?rem1)
  (modify ?rem2 (certainty (+ (+ ?per1 ?per2) (/ (* ?per1 ?per2) 100.0)))))

(defrule MAIN::combine-certainties-opposite-signs
  (declare (salience 100)
           (auto-focus TRUE))
  ?rem1 <- (attribute (name ?rel&:(eq (sub-string 1 5 ?rel) "best-")) (value ?val) (certainty ?per1))
  ?rem2 <- (attribute (name ?rel&:(eq (sub-string 1 5 ?rel) "best-")) (value ?val) (certainty ?per2))
  (test (neq ?rem1 ?rem2))
  (test (or (and (>= ?per1 0) (< ?per2 0)) (and (< ?per1 0) (>= ?per2 0))))
  =>
  (retract ?rem1)
  (bind ?mincf (min (abs ?per1) (abs ?per2)))
  (modify ?rem2 (certainty (/ (+ ?per1 ?per2) (- 1 ?mincf)))))
;; RULES COMBINE CERTAINTIES END ;;

;;*****************
;; The RULES module
;;*****************

(defmodule RULES (import MAIN deftemplate attribute) (export deftemplate rule)
                 (import MAIN deftemplate rule))

(defrule RULES::throw-away-ands-in-antecedent
  ?f <- (rule (if and $?rest))
  =>
  (modify ?f (if ?rest)))

(defrule RULES::throw-away-ands-in-consequent
  ?f <- (rule (then and $?rest))
  =>
  (modify ?f (then ?rest)))

(defrule RULES::remove-is-condition-when-satisfied
  ?f <- (rule (certainty ?c1)
              (if ?attribute is ?value $?rest))
  (attribute (name ?attribute)
             (value ?value)
             (certainty ?c2))
  =>
  (modify ?f (certainty (min ?c1 ?c2)) (if ?rest)))

(defrule RULES::remove-is-not-condition-when-satisfied
  ?f <- (rule (certainty ?c1)
              (if ?attribute is-not ?value $?rest))
  (attribute (name ?a&:(eq ?a (sym-cat "not-" ?attribute))) (value ?value) (certainty ?c2))
  =>
  (modify ?f (certainty (min ?c1 ?c2)) (if ?rest)))

(defrule RULES::perform-rule-consequent-is-with-certainty
  ?f <- (rule (certainty ?c1)
              (if)
              (then ?attribute is ?value with certainty ?c2 $?rest))
  =>
  (modify ?f (then ?rest))
  (assert (attribute (name ?attribute)
                     (value ?value)
                     (certainty (/ (* ?c1 ?c2) 100.0)))))

(defrule RULES::perform-rule-consequent-is-without-certainty
  ?f <- (rule (certainty ?c1)
              (if)
              (then ?attribute is ?value $?rest))
  (test (or (eq (length$ ?rest) 0)
            (neq (nth$ 1 ?rest) with)))
  =>
  (modify ?f (then ?rest))
  (assert (attribute (name ?attribute) (value ?value) (certainty ?c1))))

;;*******************
;;* REASONING RULES *
;;*******************

(defmodule REASONING-RULES (import RULES deftemplate rule)
                           (import MAIN deftemplate attribute))

;;genera regole per le città scelte dall'utente
(defrule REASONING-RULES::compile-citta'
  (attribute (name city)
             (value ?v))
  =>
  (assert (rule (if city is ?v)
      (then best-city is ?v with certainty 90))))

;;genera le regole per il tipo di turismo
(defrule REASONING-RULES::compile-rules-tourism-type
  (attribute (name tourism-type)
             (value ?v))
  =>
  (assert (rule (if tourism-type is ?v)
      (then best-tourism-type is ?v with certainty 80))))

;;genera le regole per le regioni scelte dall'utente
(defrule REASONING-RULES::compile-rules-region
  (attribute (name region)
             (value ?v))
  =>
  (assert (rule (if region is ?v)
      (then best-region is ?v with certainty 95))))

;;genera le regole per le regioni che l'utente non vuole
(defrule REASONING-RULES::compile-rules-not-region
  (attribute (name not-region)
             (value ?v))
  =>
  (assert (rule (if not-region is ?v)
      (then best-region is ?v with certainty -95))))

(defrule REASONING-RULES::compile-rules-only-region1
  (attribute (name only-region)
             (value ?v))
  (attribute (name city)
             (value ?city&:(and (neq ?city unknown) (eq (str-index ?v ?city) FALSE))))
  =>
  (bind ?j (str-index "-" ?city))
  (bind ?region (sym-cat (sub-string 1 (- ?j 1) ?city)))
  (assert (rule (if region is ?v)
      (then best-city is ?city with certainty -90 and
            best-region is ?region with certainty -20))))

(defrule REASONING-RULES::compile-rules-only-region2
  (attribute (name only-region)
             (value ?v))
  (attribute (name city)
             (value ?city&:(neq (str-index ?v ?city) FALSE)))
  =>
  (bind ?j (str-index "-" ?city))
  (bind ?region (sym-cat (sub-string 1 (- ?j 1) ?city)))
  (assert (rule (if region is ?v)
      (then best-city is ?city with certainty 90 and
            best-region is ?region with certainty 20))))

(defrule REASONING-RULES::compile-rules-only-region3
  (attribute (name only-region)
             (value ?v))
  (attribute (name region)
             (value ?v1&:(and (neq ?v1 ?v) (neq ?v1 unknown))))
  =>
  (assert (rule (if region is ?v)
      (then best-region is ?v1 with certainty -95))))

(defrule REASONING-RULES::compile-rules-only-not-region1
  (attribute (name only-not-region)
             (value ?v))
  (attribute (name city)
             (value ?city&:(and (neq ?city unknown) (eq (str-index ?v ?city) FALSE))))
  =>
  (bind ?j (str-index "-" ?city))
  (bind ?region (sym-cat (sub-string 1 (- ?j 1) ?city)))
  (assert (rule (if region is ?v)
      (then best-city is ?city with certainty 90 and
            best-region is ?region with certainty 20))))

(defrule REASONING-RULES::compile-rules-only-not-region2
  (attribute (name only-not-region)
             (value ?v))
  (attribute (name city)
             (value ?city&:(neq (str-index ?v ?city) FALSE)))
  =>
  (bind ?j (str-index "-" ?city))
  (bind ?region (sym-cat (sub-string 1 (- ?j 1) ?city)))
  (assert (rule (if region is ?v)
      (then best-city is ?city with certainty -90 and
            best-region is ?region with certainty -20))))

(defrule REASONING-RULES::compile-rules-only-not-region3
  (attribute (name only-not-region)
             (value ?v))
  (attribute (name region)
             (value ?v1&:(and (neq ?v1 ?v) (neq ?v1 unknown))))
  =>
  (assert (rule (if not-region is ?v)
      (then best-region is ?v1 with certainty 95))))

;;**************************
;;* HOTELS SELECTION RULES *
;;**************************

(defmodule HOTELS (import MAIN ?ALL) (export deftemplate hotel))

(deffacts HOTELS::unknown-attributes
  (attribute (name city) (value unknown) (certainty 0.0))
  (attribute (name max-budget) (value 500) (certainty 0.0))
  (attribute (name time) (value 1) (certainty 0.0))
  (attribute (name people) (value 1) (certainty 0.0))
  (attribute (name tourism-type) (value unknown) (certainty 0.0))
  (attribute (name region) (value unknown) (certainty 0.0))
  (attribute (name min-stars) (value 1) (certainty 0.0))
  (attribute (name max-stars) (value 5) (certainty 0.0)))

;;genera gli hotel attribute
(defrule HOTELS::generate-hotels
  (hotel (name ?name) (tr ?tr) (stars ?s))
  (tourism-resort (name ?tr) (region ?r) (type $? ?t $?))
  (tourism-type (tt ?t))
  (attribute (name best-region) (value ?region) (certainty ?certainty-1))
  (attribute (name best-city) (value ?city) (certainty ?certainty-2))
  (attribute (name best-tourism-type) (value ?tt) (certainty ?certainty-3))
  (attribute (name min-stars) (value ?min-stars) (certainty ?certainty-4))
  (attribute (name max-stars) (value ?max-stars) (certainty ?certainty-5))
  (test (or (eq ?region ?r) (eq ?region unknown)))
  (test (or (eq ?city (sym-cat (str-cat ?r "-") ?tr)) (eq ?city unknown)))
  (test (or (eq ?tt ?t) (eq ?tt unknown)))
  =>
  (if (> ?min-stars ?s) then (bind ?certainty-4 -100.0))
  (if (< ?max-stars ?s) then (bind ?certainty-5 -100.0))
  (bind ?uv (get-variables-name-with-value (create$ region ?region city ?city tourism-type ?tt) unknown))
  (assert (hotel-attribute
             (name ?name)
             (certainty (average (create$ ?certainty-1 ?certainty-2 ?certainty-3 ?certainty-4 ?certainty-5)))
             (unknown-variables ?uv))))
