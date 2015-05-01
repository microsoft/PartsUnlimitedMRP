// For an introduction to the Page Control template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkId=232511
(function () {
    "use strict";

    var app = WinJS.Application;
    var nav = WinJS.Navigation;
    var sched = WinJS.Utilities.Scheduler;
    var ui = WinJS.UI;

    WinJS.UI.Pages.define("pages/main/main.html", {
        // This function is called whenever a user navigates to this page. It
        // populates the page elements with the app's data.
        ready: function (element, options) {

            var launchtiles = document.querySelectorAll('.launchtile');
            for (var n = 0; n < launchtiles.length; n++) {
                var launchtile = launchtiles[n];
                launchtile.addEventListener('click', this.launchtileInvoked.bind(this));
            }
        },

        toggleNavBarVisibility: function (ev) {
            document.getElementById('createNavBar').winControl.show();
        },

        launchtileInvoked: function (ev) {
            var tile = ev.currentTarget;
            var location = "pages/" + tile.dataset.page + "/" + tile.dataset.page + ".html";
            nav.navigate(location);
            var trigger = document.querySelector(".nav-trigger");
            trigger.checked = false;
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
