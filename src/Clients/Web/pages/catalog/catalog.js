// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";
    var nav = WinJS.Navigation;

    WinJS.UI.Pages.define("pages/catalog/catalog.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        catalogDetailElement: null,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;

            return WinJS.UI.processAll(element).then(function () {

                that.listViewControl = element.querySelector(".catalogListView").winControl;
                that.catalogDetailElement = element.querySelector(".catalogDetail");
                subpage.style.display = "none";
                showProgress("Loading...");
                return Data.catalogGet().then(function (catalog) {
                    if (!catalog) {
                        hideProgress();
                        nav.back(nav.history.backStack.length);
                        return;
                    }
                    that.editTools = subpage.querySelector(".edittools").winControl;
                    that.editButtons = that.editTools.getButtons();
                    that.editTools.addEventListener("click", that._buttonHandler.bind(that));
                    that.listViewControl.forceLayout();

                    that.editButtons['add'].disabled = false;
                    that.editButtons['edit'].style.display = "none";

                    that.listViewControl.onselectionchanged = function (arg) {
                        that.listViewControl.selection.getItems().then(function (items) {
                            if (items.length > 0) {
                                that.currentItem = items[0].data;
                                WinJS.Binding.processAll(that.catalogDetailElement, items[0].data);
                                //that.editButtons['delete'].disabled = false;
                                //that.editButtons['edit'].disabled = false;
                                //that.editButtons['save'].disabled = false;
                            }
                            else {
                                that.currentItem = null;
                                //that.editButtons['delete'].disabled = true;
                                //that.editButtons['edit'].disabled = true;
                                //that.editButtons['save'].disabled = true;
                            }
                        });
                    }
                    that.listViewControl.selection.add(0);
                    hideProgress();
                    subpage.style.display = "";

                    WinJS.UI.Animation.enterContent(subpage);
                });
            });
        },

        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    Data.catalogSave(this.currentItem).then(function (saved) {
                        if (saved) {
                            var index = Data.catalog.indexOf(saved);
                            that.listViewControl.selection.clear();
                            that.listViewControl.selection.add(index);
                            that.listViewControl.ensureVisible(index);
                        }
                    });
                    break;
                }
                case 'delete': {
                    confirm("Delete Product", "Are you sure that you'd like to delete " + this.currentItem.skuNumber + "?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = Data.catalog.indexOf(that.currentItem) - 1;
                            if (idx < 0) {
                                idx = 0;
                            }
                            Data.catalogDelete(that.currentItem).then(function (deleted) {
                                that.listViewControl.selection.clear();
                                if (Data.catalog.length > 0) {
                                    that.listViewControl.selection.add(idx);
                                    that.listViewControl.ensureVisible(idx);
                                }
                            });
                        }
                    });
                    break;
                }
                case 'add': {
                    this.listViewControl.selection.clear();
                    this.currentItem = Data.catalogCreate();
                    WinJS.Binding.processAll(this.catalogDetailElement, this.currentItem);
                    that.catalogDetailElement.querySelector("#skuNumber").focus();

                    break;
                }
                default: {

                }
            }
        },

        unload: function () {
            // TODO: Respond to navigations away from this page.
        },

        updateLayout: function (element) {
            /// <param name="element" domElement="true" />

            // TODO: Respond to changes in layout.
        }
    });
})();
