// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";
    var nav = WinJS.Navigation;

    WinJS.UI.Pages.define("pages/dealers/dealers.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        dealerDetailElement: null,
        autocomplete: null,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;


            that.listViewControl = element.querySelector(".dealerListView").winControl;
            that.dealerDetailElement = element.querySelector(".dealerDetail");
            subpage.style.display = "none";
            showProgress("Loading...");
            return Data.dealersGet().then(function (dealers) {
                if (!dealers) {
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

                addAddressHandler(that.dealerDetailElement.querySelector("#address"), function (place) {
                    that.currentItem.address = place.formatted_address;
                });

                that.listViewControl.onselectionchanged = function (arg) {
                    that.listViewControl.selection.getItems().then(function (items) {
                        if (items.length > 0) {
                            that.currentItem = items[0].data;
                            WinJS.Binding.processAll(that.dealerDetailElement, items[0].data);
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
        },

        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    Data.dealerSave(this.currentItem).then(function (saved) {
                        if (saved) {
                            var index = Data.dealers.indexOf(saved);
                            that.listViewControl.selection.clear();
                            that.listViewControl.selection.add(index);
                            that.listViewControl.ensureVisible(index);
                        }
                    });
                    break;
                }
                case 'delete': {
                    confirm("Delete Dealer", "Are you sure that you'd like to delete " + this.currentItem.name + "?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = Data.dealers.indexOf(that.currentItem) - 1;
                            if (idx < 0) {
                                idx = 0;
                            }
                            Data.dealerDelete(that.currentItem).then(function (deleted) {
                                that.listViewControl.selection.clear();
                                if (Data.dealers.length > 0) {
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
                    this.currentItem = Data.dealerCreate();
                    WinJS.Binding.processAll(this.dealerDetailElement, this.currentItem);
                    that.dealerDetailElement.querySelector("#name").focus();

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
