// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";
    var nav = WinJS.Navigation;

    WinJS.UI.Pages.define("pages/orders/orders.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        originalItem: null,
        orderDetailElement: null,
        itemTemplateHTML: null,
        bound: false,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;
            var fromQuote = WinJS.Promise.as();

            if (options && options.quote) {
                showProgress("Creating Order...");
                fromQuote = Data.orderCreateFromQuote(options.quote);
            }
            else {
                showProgress("Loading...");
            }

            return WinJS.UI.processAll(element).then(function () {
                element.querySelector("#manageExtras").addEventListener("click", that._extrasHandler.bind(that));
                element.querySelector("#manageEvents").addEventListener("click", that._eventsHandler.bind(that));

                that.listViewControl = element.querySelector(".orderListView").winControl;
                that.listViewControl.itemTemplate = that._itemRenderer.bind(that);
                that.itemTemplateHTML = element.querySelector(".orderItemTemplate").innerHTML;
                that.orderDetailElement = element.querySelector(".fullOrderDetail");

                subpage.style.display = "none";
                return fromQuote.then(function (neworder) {
                    return Data.ordersGet("").then(function (orders) {
                        if (!orders) {
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
                        that.editButtons['add'].style.display = "none";
                        //that.editButtons['edit'].firstElementChild.textContent = "⛟";
                        that.editButtons['edit'].firstElementChild.textContent = WinJS.UI.AppBarIcon["newwindow"];
                        that.editButtons['edit'].title = "Deliver";
                        addAddressHandler(that.orderDetailElement.querySelector("#address"), function (place) {
                            that.currentItem.city = place.formatted_address;
                            that.currentItem.postalCode = getPostCodeFromPlace(place)
                        });
                        addAddressHandler(that.orderDetailElement.querySelector("#postcode"), function (place) {
                            that.currentItem.city = place.formatted_address;
                            that.currentItem.postalCode = getPostCodeFromPlace(place);
                            that.quoteDetailElement.querySelector("#postcode").value = that.currentItem.postalCode;
                        });

                        that.listViewControl.onselectionchanged = function (arg) {
                            that.listViewControl.selection.getItems().then(function (items) {
                                if (items.length > 0) {
                                    that.currentItem = items[0].data;
                                    that.originalItem = clone(items[0].data.backingData);
                                    WinJS.Binding.processAll(that.orderDetailElement, items[0].data);
                                    //that.editButtons['delete'].disabled = false;
                                    //that.editButtons['edit'].disabled = false;
                                    //that.editButtons['save'].disabled = false;
                                }
                                else {
                                    that.currentItem = null;
                                    that.originalItem = null;
                                    //that.editButtons['delete'].disabled = true;
                                    //that.editButtons['edit'].disabled = true;
                                    //that.editButtons['save'].disabled = true;
                                }
                            });
                        }

                        that.listViewControl.onloadingstatechanged = function (args) {
                            if (that.listViewControl.loadingState === "viewPortLoaded") {
                                WinJS.Promise.timeout().then(function () {
                                    var selectindex = 0;
                                    if (neworder) {
                                        selectindex = Data.orderFindById(neworder.orderId);
                                    }
                                    
                                    that.listViewControl.selection.add(selectindex);
                                    that.listViewControl.onloadingstatechanged = undefined;
                                });
                            }
                        }
                        hideProgress();
                        subpage.style.display = "";
                        WinJS.UI.Animation.enterContent(subpage);
                    });
                });
            });
        },

        _itemRenderer: function (itemPromise, recycled) {
            var that = this;
            return itemPromise.then(function (currentItem) {
                if (!that) {
                    return document.createElement("div");
                }
                var data = currentItem.data;
                return Data.quoteGetById(data.quoteId).then(function (quote) {
                    data.__quote = quote;

                    var boundTemplate = document.createElement("div");
                    boundTemplate.className = "win-template";
                    boundTemplate.innerHTML = that.itemTemplateHTML;

                    return WinJS.Binding.processAll(boundTemplate, data).then(function () {
                        return boundTemplate;
                    });
                });
            });
        },

        _extrasHandler: function (args) {
            popup("orderExtrasPopup", "Manage Extras", "pages/extras/extras.html", this.currentItem.__quote);
        },
        _eventsHandler: function (args) {
            popup("orderExtrasPopup", "Manage Events", "pages/orderevents/orderevents.html", this.currentItem);
        },
        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    Data.orderSave(this.currentItem, this.originalItem).then(function (saved) {
                        if (saved) {
                            var index = Data.orders.indexOf(saved);
                            that.listViewControl.selection.clear();
                            that.listViewControl.selection.add(index);
                            that.listViewControl.ensureVisible(index);
                        }
                    });
                    break;
                }
                case 'edit': {
                    nav.navigate("pages/deliveries/deliveries.html", { order: that.currentItem });
                    break;
                }
                case 'delete': {
                    confirm("Delete Order", "Are you sure that you'd like to delete this order?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = Data.orders.indexOf(that.currentItem) - 1;
                            if (idx < 0) {
                                idx = 0;
                            }
                            Data.orderDelete(that.currentItem).then(function (deleted) {
                                that.listViewControl.selection.clear();
                                if (Data.orders.length > 0) {
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
                    this.currentItem = Data.orderCreate();
                    this.originalItem = null;
                    WinJS.Binding.processAll(this.orderDetailElement, this.currentItem);
                    that.orderDetailElement.querySelector("#dealerName").focus();

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
