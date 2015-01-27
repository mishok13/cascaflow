(ns cascaflow.core)

(comment
  ;; Output needs to be handled by TextIO, I guess? Maybe other IO
  ;; handlers as well?
  (?- [output]
      (<- [?foo ?bar ?baz]
           ;; a source is a PCollection, with items either as atoms
          ;; (simple case) or always tuples (PeristentList would work
          ;; I suppose?). What's next?
          ;;
          ;; ...  Implement parsing of that into variables, I guess?
          ;; Variables don't make a lot of sense, but maybe
          ;; positionals? We would want to operate on each mapfn later
          ;; on, hmmm...
          (source :> ?foo ?bar)
          ;; This is PTransform, but how do we ensure the order of
          ;; execution? Need to think this through, maybe read more on
          ;; datalog.
          (op ?foo :> ?baz)
          ;; If xyz is ignored, how do we figure it out? Build a DAG
          ;; on our own? Sounds like overkill, but also don't see a
          ;; simpler solution so far.
          (op2 ?bar ?baz :> ?xyz))))
