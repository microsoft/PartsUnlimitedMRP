// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";
    var nav = WinJS.Navigation;

    WinJS.UI.Pages.define("pages/quotes/quotes.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        quoteDetailElement: null,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;

            return WinJS.UI.processAll(element).then(function () {
                element.querySelector("#manageExtras").addEventListener("click", that._extrasHandler.bind(that));

                that.listViewControl = element.querySelector(".quoteListView").winControl;
                that.quoteDetailElement = element.querySelector(".quoteDetail");
                subpage.style.display = "none";
                showProgress("Loading...");
                return Data.quotesGet("").then(function (quotes) {
                    if (!quotes) {
                        hideProgress();
                        nav.back(nav.history.backStack.length);
                        return;
                    }
                    var toolsElement = subpage.querySelector(".edittools");
                    if (!toolsElement) {
                        return;
                    }
                    that.editTools = toolsElement.winControl;
                    that.editButtons = that.editTools.getButtons();
                    that.editTools.addEventListener("click", that._buttonHandler.bind(that));
                    that.listViewControl.forceLayout();

                    that.editButtons['add'].disabled = false;
                    that.editButtons['edit'].style.display = "";
                    that.editButtons['edit'].firstElementChild.textContent = WinJS.UI.AppBarIcon["newwindow"];

                    addAddressHandler(that.quoteDetailElement.querySelector("#address"), function (place) {
                        that.currentItem.city = place.formatted_address;
                        that.currentItem.postalCode = getPostCodeFromPlace(place)
                    });
                    addAddressHandler(that.quoteDetailElement.querySelector("#postcode"), function (place) {
                        that.currentItem.city = place.formatted_address;
                        that.currentItem.postalCode = getPostCodeFromPlace(place);
                        that.quoteDetailElement.querySelector("#postcode").value = that.currentItem.postalCode;
                    });
                    that.listViewControl.onselectionchanged = function (arg) {
                        that.listViewControl.selection.getItems().then(function (items) {
                            if (items.length > 0) {
                                that.currentItem = items[0].data;
                                WinJS.Binding.processAll(that.quoteDetailElement, items[0].data);
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
        _extrasHandler: function (args) {
            popup("quoteExtrasPopup", "Manage Extras", "pages/extras/extras.html", this.currentItem);
        },
        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    Data.quoteSave(this.currentItem).then(function (saved) {
                        if (saved) {
                            var index = Data.quotes.indexOf(saved);
                            that.listViewControl.selection.clear();
                            that.listViewControl.selection.add(index);
                            that.listViewControl.ensureVisible(index);
                        }
                    });
                    break;
                }
                case 'edit': {
                    nav.navigate("pages/orders/orders.html", { quote: that.currentItem });
                    break;
                }
                case 'delete': {
                    confirm("Delete Dealer", "Are you sure that you'd like to delete this quote?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = Data.quotes.indexOf(that.currentItem) - 1;
                            if (idx < 0) {
                                idx = 0;
                            }
                            Data.quoteDelete(that.currentItem).then(function (deleted) {
                                that.listViewControl.selection.clear();
                                if (Data.quotes.length > 0) {
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
                    this.currentItem = Data.quoteCreate();
                    WinJS.Binding.processAll(this.quoteDetailElement, this.currentItem);
                    that.quoteDetailElement.querySelector("#dealerName").focus();

                    break;
                }
                default: {

                }
            }
        },

        unload: function () {

        },

        updateLayout: function (element) {
            /// <param name="element" domElement="true" />

            // TODO: Respond to changes in layout.
        }
    });
})();
