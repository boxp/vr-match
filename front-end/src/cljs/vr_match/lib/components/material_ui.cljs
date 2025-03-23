(ns vr-match.lib.components.material-ui
  (:refer-clojure :exclude [list])
  (:require
   [reagent.core :as r]
   ["@material-ui/core" :as mui]
   ["@material-ui/core/styles" :refer [withStyles createMuiTheme createGenerateClassName MuiThemeProvider]]
   ["@material-ui/core/colors" :as colors]))

;; material-ui/core
(def app-bar (r/adapt-react-class (.-AppBar mui)))
(def tool-bar (r/adapt-react-class (.-Toolbar mui)))
(def typo-graphy (r/adapt-react-class (.-Typography mui)))
(def icon-button (r/adapt-react-class (.-IconButton mui)))
(def icon (r/adapt-react-class (.-Icon mui)))
(def no-ssr (r/adapt-react-class (.-NoSsr mui)))
(def card (r/adapt-react-class (.-Card mui)))
(def card-action-area (r/adapt-react-class (.-CardActionArea mui)))
(def card-actions (r/adapt-react-class (.-CardActions mui)))
(def card-media (r/adapt-react-class (.-CardMedia mui)))
(def card-content (r/adapt-react-class (.-CardContent mui)))
(def checkbox (r/adapt-react-class (.-Checkbox mui)))
(def grid (r/adapt-react-class (.-Grid mui)))
(def button (r/adapt-react-class (.-Button mui)))
(def button-base (r/adapt-react-class (.-ButtonBase mui)))
(def switch (r/adapt-react-class (.-Switch mui)))
(def slide (r/adapt-react-class (.-Slide mui)))
(def fade (r/adapt-react-class (.-Fade mui)))
(def chip (r/adapt-react-class (.-Chip mui)))
(def divider (r/adapt-react-class (.-Divider mui)))
(def avatar (r/adapt-react-class (.-Avatar mui)))
(def dialog (r/adapt-react-class (.-Dialog mui)))
(def dialog-title (r/adapt-react-class (.-DialogTitle mui)))
(def dialog-content (r/adapt-react-class (.-DialogContent mui)))
(def dialog-content-text (r/adapt-react-class (.-DialogContentText mui)))
(def dialog-actions (r/adapt-react-class (.-DialogActions mui)))
(def drawer (r/adapt-react-class (.-Drawer mui)))
(def form-control (r/adapt-react-class (.-FormControl mui)))
(def form-control-label (r/adapt-react-class (.-FormControlLabel mui)))
(def text-field (r/adapt-react-class (.-TextField mui)))
(def list (r/adapt-react-class (.-List mui)))
(def list-item (r/adapt-react-class (.-ListItem mui)))
(def list-item-avatar (r/adapt-react-class (.-ListItemAvatar mui)))
(def list-item-text (r/adapt-react-class (.-ListItemText mui)))
(def list-item-icon (r/adapt-react-class (.-ListItemIcon mui)))
(def list-item-secondary-action (r/adapt-react-class (.-ListItemSecondaryAction mui)))
(def list-subheader (r/adapt-react-class (.-ListSubheader mui)))

;; material-ui/styles
(def with-styles withStyles)
(def create-mui-theme createMuiTheme)
(def create-generate-class-name createGenerateClassName)
(def MuiThemeProvider (r/adapt-react-class MuiThemeProvider))

;; material-ui/colors
(def mui-color-red (.-red colors))

(def primary-color "#ef5350")
(def secondary-color mui-color-red)

(def favorite-color primary-color)
(def skip-color "#e0e0e0")

(defn theme []
  (create-mui-theme
   #js {:palette
        #js {:primary
             #js {:main primary-color}
             :secondary secondary-color}}))
