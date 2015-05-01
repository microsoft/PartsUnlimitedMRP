// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";

    WinJS.UI.Pages.define("pages/orderevents/orderevents.html", {
        listViewControl: null,
        editTools: null,
        editButtons: null,
        currentItem: null,
        originalItem: null,
        ordereventsDetailElement: null,
        ordereventsData: null,
        events: null,
        addingItem: false,
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {
            var subpage = element.querySelector(".page-section");
            var that = this;
            that.events = options.state.events;

            return WinJS.UI.processAll(element).then(function () {

                that.listViewControl = element.querySelector(".ordereventsListView").winControl;
                that.ordereventsDetailElement = element.querySelector(".ordereventsDetail");
                subpage.style.display = "none";
                showProgress("Loading...");
                return Data.catalogGet().then(function (catalog) {
                    that.ordereventsData = that._getOrderEventsDataSource(that.events);

                    WinJS.UI.setOptions(that.listViewControl, {
                        itemDataSource: that.ordereventsData.dataSource
                    });

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
                                that.originalItem = clone(items[0].data);
                                WinJS.Binding.processAll(that.ordereventsDetailElement, items[0].data);
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
                    that.listViewControl.selection.add(0);
                    hideProgress();
                    subpage.style.display = "";

                    WinJS.UI.Animation.enterContent(subpage);
                });
            });
        },
        _getOrderEventsDataSource: function (events) {
            var orderevents = new WinJS.Binding.List().createSorted(function (l, r) {
                try {
                    var ldate = new Date(l.date);
                    var rdate = new Date(r.date);
                    if (ldate.getFullYear() < 1970) {
                        ldate.setFullYear(ldate.getFullYear() + 100);
                    }

                    if (rdate.getFullYear() < 1970) {
                        rdate.setFullYear(rdate.getFullYear() + 100);
                    }

                    return ldate > rdate ? -1 : ldate === rdate ? 0 : 1;
                }
                catch (e) {
                    return l.date > r.date ? -1 : l.date === r.date ? 0 : 1;
                }

            });

            for (var n = 0; n < events.length; n++) {
                orderevents.push(WinJS.Binding.as(events[n]));
            }

            return orderevents;
        },
        _buttonHandler: function (args) {
            if (!args.label || (this.currentItem == null && args.label != 'add')) {
                return;
            }
            var that = this;

            switch (args.label) {
                case 'save': {
                    this.addingItem = false;
                    if (that.currentItem.__new) {
                        that.ordereventsData.push(that.currentItem);
                    }
                    var index = that.ordereventsData.indexOf(that.currentItem);
                    that.listViewControl.selection.clear();
                    that.listViewControl.selection.add(index);
                    that.listViewControl.ensureVisible(index);
                    break;
                }
                case 'delete': {
                    this.addingItem = false;
                    return confirm("Delete OrderEvent", "Are you sure that you'd like to delete this event?", "Yes", "No").then(function (result) {
                        if (result.reason == "primary") {
                            var idx = that.ordereventsData.indexOf(that.currentItem);
                            if (idx >= 0) {
                                that.ordereventsData.splice(idx, 1);
                                idx -= 1;
                            }

                            if (idx < 0) {
                                idx = 0;
                            }

                            that.listViewControl.selection.clear();
                            if (that.ordereventsData.length > 0) {
                                that.listViewControl.selection.add(idx);
                                that.listViewControl.ensureVisible(idx);
                            }
                        }
                    });
                    break;
                }
                case 'add': {
                    this.addingItem = true;
                    this.listViewControl.selection.clear();
                    this.currentItem = WinJS.Binding.as({ "date": Date.now().toString("M/d/yyyy hh:mm:ss tt"), "comments": "" });
                    this.currentItem.__new = true;
                    WinJS.Binding.processAll(this.ordereventsDetailElement, this.currentItem);
                    that.ordereventsDetailElement.querySelector("#orderevents-comments").focus();

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
                that.events.splice(0, that.events.length);
                that.ordereventsData.forEach(function (orderevent) {
                    that.events.push(orderevent.backingData);
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
