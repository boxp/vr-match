(ns vr-match.lib.components.material-ui
  (:refer-clojure :exclude [list])
  (:require
   [reagent.core :as r]
   ;; [reagent.impl.template :as rtpl]
   ;; ["material-ui/styles" :refer [withStyles]]
   ;; material-ui のバージョンアップによって変わったのか、 js/material_ui ではなく js/MaterialUI にモジュールが展開されるようになっていた
   ["material-ui"]
   ["material-ui/styles"]
   ["material-ui/colors"]))

;; material-ui
(def app-bar (r/adapt-react-class js/MaterialUI.AppBar))
(def tool-bar (r/adapt-react-class js/MaterialUI.Toolbar))
(def typo-graphy (r/adapt-react-class js/MaterialUI.Typography))
(def icon-button (r/adapt-react-class js/MaterialUI.IconButton))
(def icon (r/adapt-react-class js/MaterialUI.Icon))
(def no-ssr (r/adapt-react-class js/MaterialUI.NoSsr))
(def card (r/adapt-react-class js/MaterialUI.Card))
(def card-action-area (r/adapt-react-class js/MaterialUI.CardActionArea))
(def card-actions (r/adapt-react-class js/MaterialUI.CardActions))
(def card-media (r/adapt-react-class js/MaterialUI.CardMedia))
(def card-content (r/adapt-react-class js/MaterialUI.CardContent))
(def checkbox (r/adapt-react-class js/MaterialUI.Checkbox))
(def grid (r/adapt-react-class js/MaterialUI.Grid))
(def button (r/adapt-react-class js/MaterialUI.Button))
(def button-base (r/adapt-react-class js/MaterialUI.ButtonBase))
(def slide (r/adapt-react-class js/MaterialUI.Slide))
(def fade (r/adapt-react-class js/MaterialUI.Fade))
(def chip (r/adapt-react-class js/MaterialUI.Chip))
(def divider (r/adapt-react-class js/MaterialUI.Divider))
(def avatar (r/adapt-react-class js/MaterialUI.Avatar))
(def dialog (r/adapt-react-class js/MaterialUI.Dialog))
(def dialog-title (r/adapt-react-class js/MaterialUI.DialogTitle))
(def dialog-content (r/adapt-react-class js/MaterialUI.DialogContent))
(def dialog-content-text (r/adapt-react-class js/MaterialUI.DialogContentText))
(def dialog-actions (r/adapt-react-class js/MaterialUI.DialogActions))
(def drawer (r/adapt-react-class js/MaterialUI.Drawer))
(def form-control (r/adapt-react-class js/MaterialUI.FormControl))
(def form-control-label (r/adapt-react-class js/MaterialUI.FormControlLabel))
(def text-field (r/adapt-react-class js/MaterialUI.TextField))
(def list (r/adapt-react-class js/MaterialUI.List))
(def list-item (r/adapt-react-class js/MaterialUI.ListItem))
(def list-item-avatar (r/adapt-react-class js/MaterialUI.ListItemAvatar))
(def list-item-text (r/adapt-react-class js/MaterialUI.ListItemText))
(def list-item-icon (r/adapt-react-class js/MaterialUI.ListItemIcon))
(def list-item-secondary-action (r/adapt-react-class js/MaterialUI.ListItemSecondaryAction))
(def list-subheader (r/adapt-react-class js/MaterialUI.ListSubheader))

;; material-ui/styles
(def with-styles (.-withStyles js/MaterialUIStyles))
(def create-mui-theme (.-createMuiTheme js/MaterialUIStyles))
(def create-generate-class-name (.-createGenerateClassName js/MaterialUIStyles))
(def MuiThemeProvider (-> (.-MuiThemeProvider js/MaterialUIStyles) r/adapt-react-class))

;; material-ui/colors
(def mui-color-red (.-red js/MaterialUIColors))

(def primary-color "#ef5350")
(def secondary-color mui-color-red)

(def favorite-color primary-color)
(def skip-color "#e0e0e0")

(defn theme []
  (create-mui-theme
   #js {"palette"
        #js {"primary" #js {"main" primary-color}
             "secondary" secondary-color}}))
