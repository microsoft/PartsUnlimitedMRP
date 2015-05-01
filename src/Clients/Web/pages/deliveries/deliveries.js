// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";
    var nav = WinJS.Navigation;

    WinJS.UI.Pages.define("pages/deliveries/deliveries.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        originalItem: null,
        deliveryDetailElement: null,
        itemTemplateHTML: null,
        bound: false,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;
            var fromOrder = WinJS.Promise.as();

            if (options && options.order) {
                showProgress("Creating Delivery...");
                fromOrder = Data.deliveryCreateFromOrder(options.order);
            }
            else {
                showProgress("Loading...");
            }

            return WinJS.UI.processAll(element).then(function () {
                element.querySelector("#manageExtras").addEventListener("click", that._extrasHandler.bind(that));
                element.querySelector("#manageOrderEvents").addEventListener("click", that._orderEventsHandler.bind(that));
                element.querySelector("#manageDeliveryEvents").addEventListener("click", that._deliveryEventsHandler.bind(that));

                that.listViewControl = element.querySelector(".deliveryListView").winControl;
                that.listViewControl.itemTemplate = that._itemRenderer.bind(that);
                that.itemTemplateHTML = element.querySelector(".deliveryItemTemplate").innerHTML;
                that.deliveryDetailElement = element.querySelector(".fullDeliveryDetail");

                subpage.style.display = "none";
                return fromOrder.then(function (newdelivery) {
                    return Data.deliveriesGet().then(function (deliveries) {
                        if (newdelivery && Data.deliveryFindById(newdelivery.orderId) < 0) {
                            deliveries.push(newdelivery);
                        }
                        if (!deliveries) {
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
                        that.editButtons['edit'].style.display = "none";

                        addAddressHandler(that.deliveryDetailElement.querySelector("#deliveryAddress"), function (place) {
                            that.currentItem.deliveryAddress.city = place.formatted_address;
                            that.currentItem.deliveryAddress.postalCode = getPostCodeFromPlace(place)
                        });

                        addAddressHandler(that.deliveryDetailElement.querySelector("#deliveryPostcode"), function (place) {
                            that.currentItem.deliveryAddress.city = place.formatted_address;
                            that.currentItem.deliveryAddress.postalCode = getPostCodeFromPlace(place)
                            that.quoteDetailElement.querySelector("#deliveryPostcode").value = that.currentItem.deliveryAddress.postalCode;
                        });

                        addAddressHandler(that.deliveryDetailElement.querySelector("#quoteAddress"), function (place) {
                            that.currentItem.__quote.city = place.formatted_address;
                            that.currentItem.__quote.postalCode = getPostCodeFromPlace(place)
                        });

                        addAddressHandler(that.deliveryDetailElement.querySelector("#quotePostcode"), function (place) {
                            that.currentItem.__quote.city = place.formatted_address;
                            that.currentItem.__quote.postalCode = getPostCodeFromPlace(place)
                            that.quoteDetailElement.querySelector("#quotePostcode").value = that.currentItem.__quote.postalCode;
                        });

                        that.listViewControl.onselectionchanged = function (arg) {
                            that.listViewControl.selection.getItems().then(function (items) {
                                if (items.length > 0) {
                                    that.currentItem = items[0].data;
                                    that.originalItem = clone(items[0].data.backingData);
                                    that.originalItem.__order = clone(items[0].data.__order.backingData);
                                    that.originalItem.__quote = clone(items[0].data.__quote.backingData);
                                    WinJS.Binding.processAll(that.deliveryDetailElement, items[0].data);
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
                                    if (newdelivery) {
                                        selectindex = Data.deliveryFindById(newdelivery.orderId);
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
                return Data.orderGetById(data.orderId).then(function (order) {
                    data.__order = order;
                    data.__quote = order.__quote;

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
            popup("deliveryExtrasPopup", "Manage Extras", "pages/extras/extras.html", this.currentItem.__quote);
        },
        _deliveryEventsHandler: function (args) {
            popup("deliveryExtrasPopup", "Manage Delivery Events", "pages/orderevents/orderevents.html", this.currentItem);
        },
        _orderEventsHandler: function (args) {
            popup("deliveryExtrasPopup", "Manage Order Events", "pages/orderevents/orderevents.html", this.currentItem.__order);
        },
        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    Data.deliverySave(this.currentItem, this.originalItem).then(function (saved) {
                        if (saved) {
                            var index = Data.deliveries.indexOf(saved);
                            that.listViewControl.selection.clear();
                            that.listViewControl.selection.add(index);
                            that.listViewControl.ensureVisible(index);
                        }
                    });
                    break;
                }
                case 'delete': {
                    confirm("Delete Order", "Are you sure that you'd like to delete this delivery?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = Data.deliveries.indexOf(that.currentItem) - 1;
                            if (idx < 0) {
                                idx = 0;
                            }
                            Data.deliveryDelete(that.currentItem).then(function (deleted) {
                                that.listViewControl.selection.clear();
                                if (Data.deliveries.length > 0) {
                                    that.listViewControl.selection.add(idx);
                                    that.listViewControl.ensureVisible(idx);
                                }
                            });
                        }
                    });
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
