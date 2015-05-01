// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";

    WinJS.UI.Pages.define("pages/extras/extras.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        extrasDetailElement: null,
        extrasData: null,
        additionalItems: null,
        addingItem: false,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;
            that.additionalItems = options.state.additionalItems;

            return WinJS.UI.processAll(element).then(function () {

                that.listViewControl = element.querySelector(".extrasListView").winControl;
                that.extrasDetailElement = element.querySelector(".extrasDetail");
                subpage.style.display = "none";
                showProgress("Loading...");
                return Data.catalogGet().then(function (catalog) {
                    var skuField = that.extrasDetailElement.querySelector("#extras-skuNumber");
                    that.extrasData = that._getCatalogEntriesForExtras(that.additionalItems);

                    WinJS.UI.setOptions(that.listViewControl, {
                        itemDataSource: that.extrasData.dataSource
                    });

                    that.editTools = subpage.querySelector(".edittools").winControl;
                    that.editButtons = that.editTools.getButtons();
                    that.editTools.addEventListener("click", that._buttonHandler.bind(that));
                    that.listViewControl.forceLayout();

                    that.editButtons['add'].disabled = false;
                    that.editButtons['edit'].style.display = "none";

                    addTextChangeEventHandler(skuField, function (args) {
                        that._populateExtraDetails(skuField.value);
                    }.bind(that));

                    that.listViewControl.onselectionchanged = function (arg) {
                        that.listViewControl.selection.getItems().then(function (items) {
                            if (items.length > 0) {
                                that.currentItem = items[0].data;
                                WinJS.Binding.processAll(that.extrasDetailElement, items[0].data);
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
        _populateExtraDetails: function (skuNumber) {
            var that = this;
            var extra = Data.catalogFindSku(skuNumber);
            if (extra) {
                if (extra.backingData) {
                    extra = extra.backingData;
                }
                Object.keys(extra).forEach(function (extraKey) {
                    if (extraKey != "skuNumber") {
                        that.currentItem[extraKey] = extra[extraKey];
                    }
                });
            }
        },
        _getCatalogEntriesForExtras: function (additionalItems) {
            var extras = new WinJS.Binding.List().createSorted(function (l, r) {
                return l.skuNumber < r.skuNumber ? -1 : l.skuNumber === r.skuNumber ? 0 : 1;
            });

            for (var n = 0; n < additionalItems.length; n++) {
                var catalogEntry = Data.catalogFindSku(additionalItems[n].skuNumber);
                if (catalogEntry != null) {
                    extras.push(catalogEntry);
                }
            }

            return extras;
        },
        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    this.addingItem = false;
                    return Data.catalogSave(this.currentItem).then(function (saved) {
                        if (saved) {
                            if (that.currentItem.__new) {
                                that.extrasData.push(saved);
                            }
                            var index = that.extrasData.indexOf(saved);
                            that.listViewControl.selection.clear();
                            that.listViewControl.selection.add(index);
                            that.listViewControl.ensureVisible(index);
                        }
                    });
                    break;
                }
                case 'delete': {
                    this.addingItem = false;
                    return confirm("Delete Extra", "Are you sure that you'd like to delete " + this.currentItem.skuNumber + "?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = that.extrasData.indexOf(that.currentItem);
                            if (idx >= 0) {
                                that.extrasData.splice(idx, 1);
                                idx -= 1;
                            }

                            if (idx < 0) {
                                idx = 0;
                            }

                            that.listViewControl.selection.clear();
                            if (that.extrasData.length > 0) {
                                that.listViewControl.selection.add(idx);
                                that.listViewControl.ensureVisible(idx);
                            }

                            //Data.catalogDelete(that.currentItem).then(function (deleted) {
                            //    that.listViewControl.selection.clear();
                            //    if (that.dataSource.length > 0) {
                            //        that.listViewControl.selection.add(idx);
                            //        that.listViewControl.ensureVisible(idx);
                            //    }
                            //});
                        }
                    });
                    break;
                }
                case 'add': {
                    this.addingItem = true;
                    this.listViewControl.selection.clear();
                    this.currentItem = Data.catalogCreate();
                    WinJS.Binding.processAll(this.extrasDetailElement, this.currentItem);
                    that.extrasDetailElement.querySelector("#extras-skuNumber").focus();

                    break;
                }
                default: {

                }
            }
            return WinJS.Promise.as(true);
        },

        unload: function () {
            var that = this;

            function cleanupUnload() {
                // TODO: Respond to navigations away from this page.
                that.additionalItems.splice(0, that.additionalItems.length);
                that.extrasData.forEach(function (extra) {
                    var theExtraEntry = {};
                    theExtraEntry.skuNumber = extra.skuNumber;
                    theExtraEntry.shouldPreInstall = true;
                    theExtraEntry.amount = 1;
                    that.additionalItems.push(theExtraEntry);
                });
            }

            if (this.addingItem == true) {
                this._buttonHandler({ label: 'save' }).then(cleanupUnload);
            }
            else {
                cleanupUnload();
            }
        },

        updateLayout: function (element) {
            /// <param name="element" domElement="true" />

            // TODO: Respond to changes in layout.
        }
    });
})();
