// For an introduction to the Grid template, see the following documentation:
// http://go.microsoft.com/fwlink/?LinkID=232446
(function () {
    "use strict";

    var activation = null;
    var app = WinJS.Application;
    var nav = WinJS.Navigation;
    var sched = WinJS.Utilities.Scheduler;
    var ui = WinJS.UI;

    app.addEventListener("activated", function (args) {
        try {
            activation = Windows.ApplicationModel.Activation;
            if (args.detail.kind === activation.ActivationKind.launch) {
                if (args.detail.previousExecutionState !== activation.ApplicationExecutionState.terminated) {
                    // TODO: This application has been newly launched. Initialize
                    // your application here.
                } else {
                    // TODO: This application has been reactivated from suspension.
                    // Restore application state here.
                }
            }
        }
        catch (e) { }
        showProgress("Starting...");
        nav.history = app.sessionState.history || {};
        nav.history.current.initialPlaceholder = true;

        // Optimize the load of the application and while the splash screen is shown, execute high priority scheduled work.
        ui.disableAnimations();
        var p = ui.processAll().then(function () {
            Controls.EditTools.cacheControlTemplate();
            var navigationCommands = document.querySelectorAll('.navigationButton');
            for (var n = 0; n < navigationCommands.length; n++) {
                var navigationCommand = navigationCommands[n];
                navigationCommand.addEventListener('click', app.navbarInvoked);
            }
            hideProgress();
            return nav.navigate(nav.location || Application.navigator.home, nav.state);
        }).then(function () {
            return sched.requestDrain(sched.Priority.aboveNormal + 1);
        }).then(function () {
            ui.enableAnimations();
        });

        args.setPromise(p);
    });

    app.navbarInvoked = function (ev) {
        var navbarCommand = ev.currentTarget.winControl;
        if (navbarCommand.page == "main") {
            nav.back(nav.history.backStack.length);
        }
        else {
            var location = "pages/" + navbarCommand.page + "/" + navbarCommand.page + ".html";
            nav.navigate(location);
        }
        var trigger = document.querySelector(".nav-trigger");
        trigger.checked = false;
    }

    app.oncheckpoint = function (args) {
        // TODO: This application is about to be suspended. Save any state
        // that needs to persist across suspensions here. If you need to 
        // complete an asynchronous operation before your application is 
        // suspended, call args.setPromise().
        app.sessionState.history = nav.history;
    };

    app.start();
})();

logmessage = function (msg) {
    console.log(msg);
}

var _progressNeeded = false;

function showProgress(message) {
    _progressNeeded = true;
    WinJS.Promise.timeout(500).then(function () {
        if (!_progressNeeded) {
            return;
        }
        var progress = document.querySelector("#progressContainer");
        if (progress) {
            progress.style.display = "flex";
            var underlay = document.querySelector(".progressUnderlay");
            if (underlay) {
                underlay.style.display = "";
                WinJS.Promise.timeout(200).then(function () {
                    underlay.style.opacity = "0.45";
                });
            }

            var messageDiv = document.querySelector("#progressMessage");
            if (messageDiv) {
                messageDiv.textContent = message;
            }
        }
    });
}

function hideProgress() {
    _progressNeeded = false;
    var progress = document.querySelector("#progressContainer");
    if (progress) {
        progress.style.display = "none";
        var underlay = document.querySelector(".progressUnderlay");
        if (underlay) {
            underlay.style.display = "none";
            WinJS.Promise.timeout(200).then(function () {
                underlay.style.opacity = "0.0";
            });
        }
    }
}

function confirm(title, message, primary, secondary) {
    var contentDialogElement = document.querySelector("#confirmdialog");
    var contentDialog = contentDialogElement.firstElementChild.winControl;
    var messageElement = contentDialogElement.querySelector(".win-contentdialog-content");
    messageElement.textContent = message;
    messageElement.style.marginTop = "10px";
    contentDialog.title = title;
    contentDialog.primaryCommandText = primary;
    contentDialog.secondaryCommandText = secondary;

    WinJS.Promise.timeout(500).then(function () {
        contentDialogElement.querySelector(".win-contentdialog-secondarycommand").focus();
    });

    return contentDialog.show();
}

function reporterror(title, message, err) {
    var contentDialogElement = document.querySelector("#confirmdialog");
    var contentDialog = contentDialogElement.firstElementChild.winControl;
    var messageElement = contentDialogElement.querySelector(".win-contentdialog-content");
    messageElement.textContent = message;
    messageElement.style.marginTop = "10px";
    contentDialog.title = title;
    contentDialog.primaryCommandText = "OK";
    contentDialog.secondaryCommandText = undefined;

    WinJS.Promise.timeout(500).then(function () {
        contentDialogElement.querySelector(".win-contentdialog-secondarycommand").focus();
    });

    return contentDialog.show();
}

WinJS.Namespace.define("Binding.Mode", {
    twoway: WinJS.Binding.initializer(function (source, sourceProps, dest, destProps) {
        var destPath, event, sourcePath;
        WinJS.Binding.defaultBind(source, sourceProps, dest, destProps);
        switch (dest.nodeName) {
            case "INPUT":
                event = dest.type === "checkbox" ? "onclick" : "oninput";
                break;
            case "SELECT":
                event = "onchange";
        }
        if (event) {
            destPath = destProps.join(".");
            sourcePath = sourceProps.join(".");
            return dest[event] = function (event) {
                var d, property, s, sourceParent;
                d = WinJS.Utilities.getMember(destPath, dest);
                s = WinJS.Utilities.getMember(sourcePath, source);
                if (s !== d) {
                    if (sourceProps.length === 1) {
                        return source[sourcePath] = d;
                    } else {
                        property = sourceProps[sourceProps.length - 1];
                        sourceParent = sourceProps.slice(0, -1).join('.');
                        return WinJS.Utilities.getMember(sourceParent, source)[property] = d;
                    }
                }
            };
        }
    })
});


function popup(id, title, page, state) {
    var contentDialogElement = document.querySelector("#" + id);
    var contentDialog = contentDialogElement.firstElementChild.winControl;
    var messageElement = contentDialogElement.querySelector(".win-contentdialog-content");
    var popupContentPageControl = null;

    contentDialog.title = title;

    function cleanup(result) {
        if (popupContentPageControl && popupContentPageControl.unload) {
            popupContentPageControl.unload();
        }

        if (messageElement.childElementCount > 0) {
            var oldElement = messageElement.firstElementChild;
            // Cleanup and remove previous element 
            if (oldElement.winControl) {
                if (oldElement.winControl.unload) {
                    oldElement.winControl.unload();
                }
                oldElement.winControl.dispose();
            }
            oldElement.parentNode.removeChild(oldElement);
            oldElement.innerText = "";
        }

        return result;
    }

    return this._lastNavigationPromise = WinJS.Promise.as().then(function () {
        return WinJS.UI.Pages.render(page, messageElement, { popup: contentDialog, state: state });
    }).then(function (rendered) {
        popupContentPageControl = rendered;
        return contentDialog.show();
    }).then(cleanup, cleanup);

}

function addTextChangeEventHandler(element, handler) {
    /*
    onchange occurs only when you blur the textbox
    onkeyup & onkeypress doesn't always occur on text change
    onkeydown occurs on text change (but cannot track cut & paste with mouse click)
    onpaste & oncut occurs with keypress and even with the mouse right click.
    */

    element.onchange = handler;
    element.onkeyup = handler;
    element.onkeydown = handler;
    element.onpast = handler;
    element.oncut = handler;
}

function addAddressHandler(input, setDataItem) {
    try {
        var options = {
        };

        var autocomplete = new google.maps.places.Autocomplete(input, options);

        google.maps.event.addListener(autocomplete, 'place_changed', function () {
            var place = autocomplete.getPlace();
            if (place.geometry) {
                setDataItem(place);
            }
        });
    }
    catch (e) { }
}

function getPostCodeFromPlace(place) {
    var postcode = "";
    for (var i = 0; i < place.address_components.length; i++) {
        for (var j = 0; j < place.address_components[i].types.length; j++) {
            if (place.address_components[i].types[j] == "postal_code") {
                postcode = place.address_components[i].long_name;
                break;
            }
        }
    }
    return postcode;
}

function clone(obj) {
    return JSON.parse(JSON.stringify(obj));
};