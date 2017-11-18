(ns frontend.util.util)

(defn prevent-default
  "Returns a function to be used as an browser event handler. That
  function calls .preventDefault on the event. The return value of the
  fn is always nil. If f and args is supplied, calls f with args."
  [f & args]
  (fn [event]
    (.preventDefault event)
    (apply f args)
    nil))

(defn stop-propagation
  "Returns a function to be used as an browser event handler. That
  function calls .stopPropagation on the event. The return value of the
  fn is always nil. If f and args is supplied, calls f with args."
  [f & args]
  (fn [event]
    (.stopPropagation event)
    (apply f args)
    nil))

(defn confirm
  "Returns a function which first prompts the user with a confirm dialog
  before calling f. The return value of the fn is always nil. If f and
  args is supplied, calls f with args."
  [msg f & args]
  (fn [event]
    (.preventDefault event)
    (.stopPropagation event)
    (when (.confirm js/window msg)
      (apply f args))
    nil))

(defn event->keychord
  "Takes a keydown event, and returns a vector of all important keys
  like META, SHIFT, CTRL, ALT,.."
  [e]
  (let [code (.-keyCode e)
        ctrl (if (.-ctrlKey e) :ctrl)
        shift (if (.-shiftKey e) :shift)
        alt (if (.-altKey e) :alt)
        meta (if (.-metaKey e) :meta)]
    (filter identity [ctrl alt meta shift code])))

(defn- e->pos
  "Given an event will return the position tuple [x y] for the mouse coords."
  [e]
  [(.-clientX e) (.-clientY e)])

(defn combine-reducers
  "Returns a reducer fn that invokes every reducer inside the reducers
  map, and constructs a state object with the same shape."
  [reducers]
  (fn [state action]
    (reduce-kv #(assoc %1 %2 (%3 (%2 state) action)) {} reducers)))
