(function () {
    "use strict";

    var controlTemplate = null;
    var templateInitPromise = null;

    // This will load and cache the control's template.
    // This happens the first time an instance of the control is created, or when you call Controls.EditTools.cacheControlTemplate
    function loadControlTemplate() {
        if (!templateInitPromise) {
            var controlFragment = document.createElement("div");
            templateInitPromise = WinJS.UI.Fragments.render("controls/edittools/edittools.html", controlFragment).then(function () {
                controlTemplate = new WinJS.Binding.Template(controlFragment.querySelector("#edittools-template"));
                controlTemplate._renderImpl = controlTemplate._compileTemplate({ target: "render" });
            });
        }
        return templateInitPromise;
    }

    var EditTools = WinJS.Class.define(function (element, options) {
        var options = options || {};
        this.element = element || document.createElement("div");
        this.element.winControl = this;
        WinJS.UI.setOptions(this, options);

        this.controlInitialized = false;
        var that = this;
        this.initPromise = loadControlTemplate().then(function () {
            that.element.className = controlTemplate.element.className;
            return controlTemplate.render(options.dataSource, that.element).then(
                function (element) {
                    that._initialize();
                    that.controlInitialized = true;
                });
        });
    }
    , {
        _initialize: function () {
            var that = this;
            var labels = this.element.querySelectorAll(".edit-tools-button-label");
            for (var n = 0; n < labels.length; n++) {
                var label = labels[n];
                if (WinJS.UI.AppBarIcon[label.dataset.label]) {
                    label.textContent = WinJS.UI.AppBarIcon[label.dataset.label];
                }
                else {
                    label.textContent = label.dataset.label;
                }
                label.parentElement.dataset.label = label.dataset.label;
            }

            var buttons = this.getButtons();
            Object.keys(buttons).forEach(function (buttonKey) {
                var button = buttons[buttonKey];
                button.addEventListener("click", function () {
                    that.dispatchEvent("click", {
                        sender: button,
                        label: this.dataset.label
                    });
                });
            });
        },

        ensureInitialized: function () {
            return this.initPromise;
        },

        dispose: function () {
            // TODO: Control clean-up
            this._disposed = true;
        },

        getButtons: function () {
            var buttons = {};
            var buttonElements = this.element.querySelectorAll(".edit-tools-button");
            for (var n = 0; n < buttonElements.length; n++) {
                var buttonElement = buttonElements[n];
                buttons[buttonElement.dataset.label] = buttonElement;
            }
            return buttons;
        }
    },
    {
        cacheControlTemplate: function () {
            return loadControlTemplate();
        },
    });

    WinJS.Namespace.define("Controls", {
        EditTools: EditTools,
    });
})();

WinJS.Class.mix(Controls.EditTools,
    WinJS.Utilities.createEventProperties("click"),
    WinJS.UI.DOMEventMixin);