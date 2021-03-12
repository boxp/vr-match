(ns vr-match.lib.components.material-ui
  (:refer-clojure :exclude [list])
  (:require
   [reagent.core :as r]
   ;; [reagent.impl.template :as rtpl]
   ;; ["material-ui/styles" :refer [withStyles]]
   ;; material-ui のバージョンアップによって変わったのか、 js/material_ui ではなく js/MaterialUI にモジュールが展開されるようになっていた
   ["@material-ui/core" :as material-ui]
   ["@material-ui/core/styles" :as material-ui-styles]
   ["@material-ui/core/colors" :as material-ui-colors]))

;; material-ui
(def app-bar (r/adapt-react-class material-ui/AppBar))
(def tool-bar (r/adapt-react-class material-ui/Toolbar))
(def typo-graphy (r/adapt-react-class material-ui/Typography))
(def icon-button (r/adapt-react-class material-ui/IconButton))
(def icon (r/adapt-react-class material-ui/Icon))
(def no-ssr (r/adapt-react-class material-ui/NoSsr))
(def card (r/adapt-react-class material-ui/Card))
(def card-action-area (r/adapt-react-class material-ui/CardActionArea))
(def card-actions (r/adapt-react-class material-ui/CardActions))
(def card-media (r/adapt-react-class material-ui/CardMedia))
(def card-content (r/adapt-react-class material-ui/CardContent))
(def checkbox (r/adapt-react-class material-ui/Checkbox))
(def grid (r/adapt-react-class material-ui/Grid))
(def button (r/adapt-react-class material-ui/Button))
(def button-base (r/adapt-react-class material-ui/ButtonBase))
(def switch (r/adapt-react-class material-ui/Switch))
(def slide (r/adapt-react-class material-ui/Slide))
(def fade (r/adapt-react-class material-ui/Fade))
(def chip (r/adapt-react-class material-ui/Chip))
(def divider (r/adapt-react-class material-ui/Divider))
(def avatar (r/adapt-react-class material-ui/Avatar))
(def dialog (r/adapt-react-class material-ui/Dialog))
(def dialog-title (r/adapt-react-class material-ui/DialogTitle))
(def dialog-content (r/adapt-react-class material-ui/DialogContent))
(def dialog-content-text (r/adapt-react-class material-ui/DialogContentText))
(def dialog-actions (r/adapt-react-class material-ui/DialogActions))
(def drawer (r/adapt-react-class material-ui/Drawer))
(def form-control (r/adapt-react-class material-ui/FormControl))
(def form-control-label (r/adapt-react-class material-ui/FormControlLabel))
(def text-field (r/adapt-react-class material-ui/TextField))
(def list (r/adapt-react-class material-ui/List))
(def list-item (r/adapt-react-class material-ui/ListItem))
(def list-item-avatar (r/adapt-react-class material-ui/ListItemAvatar))
(def list-item-text (r/adapt-react-class material-ui/ListItemText))
(def list-item-icon (r/adapt-react-class material-ui/ListItemIcon))
(def list-item-secondary-action (r/adapt-react-class material-ui/ListItemSecondaryAction))
(def list-subheader (r/adapt-react-class material-ui/ListSubheader))

;; material-ui/styles
(def with-styles (.-withStyles material-ui-styles))
(def create-mui-theme (.-createMuiTheme material-ui-styles))
(def create-generate-class-name (.-createGenerateClassName material-ui-styles))
(def MuiThemeProvider (-> (.-MuiThemeProvider material-ui-styles) r/adapt-react-class))

;; material-ui/colors
(def mui-color-red (.-red material-ui-colors))

(def primary-color "#ef5350")
(def secondary-color mui-color-red)

(def favorite-color primary-color)
(def skip-color "#e0e0e0")

(defn theme []
  (create-mui-theme
   #js {"palette"
        #js {"primary" #js {"main" primary-color}
             "secondary" secondary-color}}))
