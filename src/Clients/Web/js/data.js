(function () {
    "use strict";

    var rest_headers = { "Content-type": "application/json", "Pragma": "no-cache", "Cache-Control": "no-cache", "If-Modified-Since": new Date(0).toUTCString() };

    var dealers = new WinJS.Binding.List().createSorted(function (l, r) {
        return l.name < r.name ? -1 : l.name === r.name ? 0 : 1;
    });

    var catalog = new WinJS.Binding.List().createSorted(function (l, r) {
        return l.skuNumber < r.skuNumber ? -1 : l.skuNumber === r.skuNumber ? 0 : 1;
    });

    var quotes = new WinJS.Binding.List().createSorted(function (l, r) {
        return l.validUntil < r.validUntil ? -1 : l.validUntil === r.validUntil ? 0 : 1;
    });

    var orders = new WinJS.Binding.List().createSorted(function (l, r) {
        return l.orderDate < r.orderDate ? -1 : l.orderDate === r.orderDate ? 0 : 1;
    });

    var deliveries = new WinJS.Binding.List().createSorted(function (l, r) {
        return l.deliveryDate > r.deliveryDate ? -1 : l.deliveryDate === r.deliveryDate ? 0 : 1;
    });

    WinJS.Namespace.define("Data", {
        dealers: dealers,
        dealersGet: dealersGet,
        dealerSave: dealerSave,
        dealerDelete: dealerDelete,
        dealerCreate: dealerCreate,
        catalog: catalog,
        catalogGet: catalogGet,
        catalogSave: catalogSave,
        catalogDelete: catalogDelete,
        catalogCreate: catalogCreate,
        catalogFindSku: catalogFindSku,
        quotes: quotes,
        quotesGet: quotesGet,
        quoteGetById: quoteGetById,
        quoteSave: quoteSave,
        quoteDelete: quoteDelete,
        quoteCreate: quoteCreate,
        orders: orders,
        ordersGet: ordersGet,
        orderGetById: orderGetById,
        orderSave: orderSave,
        orderDelete: orderDelete,
        orderCreateFromQuote: orderCreateFromQuote,
        orderFindById: orderFindById,
        deliveries: deliveries,
        deliveriesGet: deliveriesGet,
        deliverySave: deliverySave,
        deliveryDelete: deliveryDelete,
        deliveryCreateFromOrder: deliveryCreateFromOrder,
        deliveryFindById: deliveryFindById
    });

    function deliveriesGet() {

        return WinJS.xhr({ url: baseAddress + "/shipments", headers: rest_headers }).then(function (response) {
            if (response.status == 0) {
                reporterror("Could not retrieve deliveries", "Unfortunately, the server could not be contacted in order to retrieve delivery details.", "");
                return false;
            }
            Data.deliveries.splice(0, Data.deliveries.length);
            var theDeliveries = JSON.parse(response.responseText);
            theDeliveries.forEach(function processDealerData(deliveryData) {
                if (!deliveryData.deliveryDate) {
                    deliveryData.deliveryDate = Date.today().addMonths(2).toString("M/d/yyyy");
                }
                Data.deliveries.push(WinJS.Binding.as(deliveryData));
            });
            return WinJS.Promise.wrap(Data.deliveries);
        }, function (err) {
            reporterror("Could not retrieve deliveries", "Unfortunately, the server could not be contacted in order to retrieve delivery details.", err);
            return WinJS.Promise.wrap(null);
        });
    }

    function deliverySaveEdits(delivery) {
        var rawdelivery = delivery;
        if (delivery.backingData) {
            rawdelivery = delivery.backingData;
        }
        return WinJS.xhr({
            type: 'put',
            url: baseAddress + "/shipments/" + encodeURIComponent(delivery.orderId),
            headers: rest_headers,
            data: JSON.stringify(rawdelivery),
        }).then(function (response) {
            return WinJS.Promise.wrap(delivery);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function deliverySaveNew(delivery) {
        var rawdelivery = delivery;
        if (delivery.backingData) {
            rawdelivery = delivery.backingData;
        }
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/shipments",
            headers: rest_headers,
            data: JSON.stringify(rawdelivery),
        }).then(function (response) {
            return WinJS.Promise.wrap(delivery);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function deliverySave(delivery, original) {
        if (delivery.__new) {
            return deliverySaveNew(delivery).then(function (saveddelivery) {
                return orderSave(delivery.__order, original.__order).then(function () {
                    return saveddelivery;
                });
            });
        }
        else {
            return deliverySaveEdits(delivery, original).then(function (saveddelivery) {
                return orderSave(delivery.__order, original.__order).then(function () {
                    return saveddelivery;
                });
            });
        }
    }

    function deliveryDelete(delivery) {
        return WinJS.xhr({
            type: 'delete',
            url: baseAddress + "/shipments/" + encodeURIComponent(delivery.orderId)
        }).then(function (response) {
            if (response.status == 204) {
                var index = deliveries.indexOf(delivery);
                if (index > -1) {
                    deliveries.splice(index, 1);
                }
            }
            else if (response.status == 0) {
                reporterror("Could not delete delivery", "Unfortunately, the server could not be contacted in order to retrieve delivery details.", "");
                return false;
            }
            return WinJS.Promise.wrap(true);
        }, function (response) {
            return WinJS.Promise.wrap(false);
        });
    }


    function deliveryCreateFromOrder(order) {
        return WinJS.Promise.as(WinJS.Binding.as({
            "orderId": order.orderId,
            "deliveryDate": Date.today().addMonths(1).toString("M/d/yyyy"),
            "events": [],
            "deliveryAddress": {
                "street": "",
                "city": order.__quote.city,
                "state": "",
                "postalCode": order.__quote.postalCode,
                "specialInstructions": ""
            },
            "contactName": order.__quote.customerName,
            "primaryContactPhone": {
                "phoneNumber": "",
                "kind": "Mobile"
            },
            "alternateContactPhone": {
                "phoneNumber": "",
                "kind": "Work"
            }
        })).then(function (delivery) {
            delivery.__new = true;
            Data.deliveries.push(delivery);
            return delivery;
        });
    }

    function deliveryFindById(orderId) {
        for (var n = 0; n < deliveries.length; n++) {
            if (deliveries.getAt(n).orderId == orderId) {
                return n;
            }
        }
        return -1;
    }


    function orderFindById(orderId) {
        for (var n = 0; n < orders.length; n++) {
            if (orders.getAt(n).orderId == orderId) {
                return n;
            }
        }
        return -1;
    }

    function ordersGet(dealer, status) {

        if (!dealer) {
            dealer = "";
        }

        if (!status) {
            status = "None";
        }

        return WinJS.xhr({ url: baseAddress + "/orders?dealer=" + encodeURIComponent(dealer) + "&status=" + encodeURIComponent(status), headers: rest_headers }).then(function (response) {
            Data.orders.splice(0, Data.orders.length);
            var theDealers = JSON.parse(response.responseText);
            theDealers.forEach(function processDealerData(orderData) {
                orders.push(WinJS.Binding.as(orderData));
            });
            return WinJS.Promise.wrap(orders);
        }, function (err) {
            reporterror("Could not retrieve orders", "Unfortunately, the server could not be contacted in order to retrieve order details.", err);
            return WinJS.Promise.wrap(null);
        });
    }

    function orderDelete(order) {
        return WinJS.xhr({
            type: 'delete',
            url: baseAddress + "/orders/" + encodeURIComponent(order.orderId)
        }).then(function (response) {
            if (response.status == 204) {
                var index = orders.indexOf(order);
                if (index > -1) {
                    orders.splice(index, 1);
                }
            }
            else if (response.status == 0) {
                reporterror("Could not delete order", "Unfortunately, the server could not be contacted in order to retrieve order details.", "");
                return false;
            }
            return WinJS.Promise.wrap(true);
        }, function (response) {
            return WinJS.Promise.wrap(false);
        });
    }

    function orderGetById(orderId) {

        return WinJS.xhr({ url: baseAddress + "/orders/" + encodeURIComponent(orderId) + "?unique=" + Date.now(), headers: rest_headers }).then(function (response) {
            Data.orders.splice(0, Data.orders.length);
            var orderData = JSON.parse(response.responseText);

            var order = WinJS.Binding.as(orderData);

            return Data.quoteGetById(order.quoteId).then(function (quote) {
                order.__quote = quote;
                return order;
            });
        });
    }

    function orderAddEvent(orderId, event) {
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/orders/" + encodeURIComponent(orderId) + "/events",
            headers: rest_headers,
            data: JSON.stringify(event),
        }).then(function (response) {
            return WinJS.Promise.wrap(event);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function orderSave(order, original) {
        if (order.__new) {
            return orderSaveNew(order).then(function (savedorder) {
                return quoteSave(order.__quote).then(function () {
                    return savedorder;
                });
            });
        }
        else {
            return orderSaveEdits(order, original).then(function (savedorder) {
                return quoteSave(order.__quote).then(function () {
                    return savedorder;
                });
            });
        }
    }

    function orderSaveEdits(order, original) {

        return orderSaveEditsToStatus(order, original).then(function () {
            var raworder = order;
            if (order.backingData) {
                raworder = order.backingData;
            }
            return WinJS.xhr({
                type: 'put',
                url: baseAddress + "/orders/" + encodeURIComponent(raworder.orderId),
                headers: rest_headers,
                data: JSON.stringify(raworder),
            }).then(function (response) {
                return WinJS.Promise.wrap(order);
            }, function (response) {
                return WinJS.Promise.wrap(null);
            });
        });
    }

    function orderSaveEditsToStatus(order, original) {
        var statusSave = WinJS.Promise.as(order);
        var statusChangeEvent = { date: Date.now().toString("M/d/yyyy hh:mm:ss tt"), comments: "Status change: " + order.status };

        if (order.status != original.status) {
            statusSave = WinJS.xhr({
                type: 'put',
                url: baseAddress + "/orders/" + encodeURIComponent(order.orderId) + "/status",
                headers: rest_headers,
                data: JSON.stringify({ status: order.status, eventInfo: statusChangeEvent }),
            }).then(function (response) {
                order.events.push(statusChangeEvent);
                return WinJS.Promise.wrap(order);
            }, function (response) {
                return WinJS.Promise.wrap(null);
            });
        }

        return statusSave;
    }

    function orderSaveEditsIndividually(order, original) {
        var raworder = order;
        if (order.backingData) {
            raworder = order.backingData;
        }

        var statusSave = orderSaveEditsToStatus(order, original);
        var eventSave = WinJS.Promise.as(order.events);

        if (order.events.length != original.events.length || JSON.stringify(order.events) != JSON.stringify(original.events)) {
            var toAdd = [];

            var eventMap = {};
            var originalEventMap = {};

            for (var n = 0; n < order.events.length; n++) {
                var theDate = new Date(order.events[n].date).toString("M/d/yyyy hh:mm:ss tt");
                eventMap[theDate + order.events[n].comments] = order.events[n];
            }

            for (var n = 0; n < original.events.length; n++) {
                var theDate = new Date(original.events[n].date).toString("M/d/yyyy hh:mm:ss tt");
                originalEventMap[theDate + original.events[n].comments] = original.events[n];
            }

            Object.keys(eventMap).forEach(function (extraKey) {
                if (!originalEventMap[extraKey]) {
                    toAdd.push(eventMap[extraKey]);
                }
            });

            var a = 1;
        }

        var promises = [];

        promises.push(statusSave);

        for (var n = 0; n < toAdd.length; n++) {
            promises.push(orderAddEvent(order.orderId, toAdd[n]));
        }

        return WinJS.Promise.join(promises).then(function (results) {
            return results;
        });
    }

    function orderSaveNew(order) {
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/orders",
            headers: rest_headers,
            data: JSON.stringify({ fromQuote: order.__quote.quoteId }),
        }).then(function (response) {
            if (response.status == 201) {
                //orders.push(order);
                return orderGetById("order-" + order.__quote.quoteId);
            }
            return WinJS.Promise.wrap(order);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function orderCreateFromQuote(quote) {
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/orders?fromQuote=" + encodeURIComponent(quote.quoteId),
            headers: rest_headers,
            data: JSON.stringify({}),
        }).then(function (response) {
            if (response.status == 201) {
                var orderData = JSON.parse(response.responseText);
                var order = WinJS.Binding.as(orderData);
                return order;
            }
            else {
                return null;
            }
        }, function (response) {
            if (response.status == 409) {
                return orderGetById("order-" + quote.quoteId);
            }
            return WinJS.Promise.wrap(null);
        });
    }

    function quotesGet(search) {

        return WinJS.xhr({ url: baseAddress + "/quotes?name=" + encodeURIComponent(search) + "&unique=" + Date.now(), headers: rest_headers }).then(function (response) {
            if (response.status == 0) {
                reporterror("Could not retrieve quotes", "Unfortunately, the server could not be contacted in order to retrieve quote details.", "");
                return false;
            }
            Data.quotes.splice(0, Data.quotes.length);
            var theDealers = JSON.parse(response.responseText);
            theDealers.forEach(function processDealerData(quoteData) {
                quotes.push(WinJS.Binding.as(quoteData));
            });
            return WinJS.Promise.wrap(quotes);
        }, function (err) {
            reporterror("Could not retrieve quotes", "Unfortunately, the server could not be contacted in order to retrieve quote details.", err);
            return WinJS.Promise.wrap(null);
        });
    }
    function quoteDelete(quote) {
        return WinJS.xhr({
            type: 'delete',
            url: baseAddress + "/quotes/" + encodeURIComponent(quote.quoteId)
        }).then(function (response) {
            if (response.status == 204) {
                var index = quotes.indexOf(quote);
                if (index > -1) {
                    quotes.splice(index, 1);
                }
            }
            return WinJS.Promise.wrap(true);
        }, function (response) {
            return WinJS.Promise.wrap(false);
        });
    }

    function quoteGetById(quoteId) {

        return WinJS.xhr({ url: baseAddress + "/quotes/" + encodeURIComponent(quoteId) + "?unique=" + Date.now(), headers: rest_headers }).then(function (response) {
            var quoteData = JSON.parse(response.responseText);
            return WinJS.Binding.as(quoteData);
        });
    }

    function quoteSave(quote) {
        if (quote.__new) {
            return quoteSaveNew(quote);
        }
        else {
            return quoteSaveEdits(quote);
        }
    }

    function quoteSaveEdits(quote) {
        var rawquote = quote;
        if (quote.backingData) {
            rawquote = quote.backingData;
        }
        return WinJS.xhr({
            type: 'put',
            url: baseAddress + "/quotes/" + encodeURIComponent(quote.quoteId),
            headers: rest_headers,
            data: JSON.stringify(rawquote),
        }).then(function (response) {
            return WinJS.Promise.wrap(quote);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function quoteSaveNew(quote) {
        var rawquote = quote;
        if (quote.backingData) {
            rawquote = quote.backingData;
        }
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/quotes",
            headers: rest_headers,
            data: JSON.stringify(rawquote),
        }).then(function (response) {
            if (response.status == 201) {
                quotes.push(quote);
            }
            return WinJS.Promise.wrap(quote);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function quoteCreate() {
        var newquote = WinJS.Binding.as({
            "quoteId": "",
            "validUntil": Date.today().addMonths(1).toString("M/d/yyyy"),
            "customerName": "",
            "dealerName": "",
            "comments": "",
            "terms": "60 days",
            "unitDescription": "",
            "unitCost": 0,
            "additionalItems": [],
            "totalCost": 0,
            "discount": 0,
            "height": 2.5,
            "width": 0,
            "depth": 0,
            "unit": "",
            "purpose": "Refrigerator",
            "ambientPeak": 30,
            "ambientAverage": 20,
            "buildOnSite": true,
            "city": "",
            "postalCode": "",
            "state": ""
        });
        newquote.__new = true;
        return newquote;
    }

    function catalogFindSku(sku) {
        var result = null;

        for (var n = 0; n < catalog.length; n++) {
            if (catalog.getAt(n).skuNumber == sku) {
                result = catalog.getAt(n);
                break;
            }
        }

        return result;
    }

    function catalogGet() {

        return WinJS.xhr({ url: baseAddress + "/catalog?unique=" + Date.now(), headers: rest_headers }).then(function (response) {
            if (response.status == 0) {
                reporterror("Could not retrieve datalog", "Unfortunately, the server could not be contacted in order to retrieve datalog details.", "");
                return null;
            }
            Data.catalog.splice(0, Data.catalog.length);
            var theCatalog = JSON.parse(response.responseText);
            theCatalog.forEach(function processCatalogData(catalogData) {
                catalog.push(WinJS.Binding.as(catalogData));
            });
            return WinJS.Promise.wrap(catalog);
        }, function (err) {
            reporterror("Could not retrieve catalog", "Unfortunately, the server could not be contacted in order to retrieve catalog details.", err);
            return WinJS.Promise.wrap(null);
        });
    }

    function catalogDelete(product) {
        return WinJS.xhr({
            type: 'delete',
            url: baseAddress + "/catalog/" + encodeURIComponent(product.skuNumber)
        }).then(function (response) {
            if (response.status == 204) {
                var index = catalog.indexOf(product);
                if (index > -1) {
                    catalog.splice(index, 1);
                }
            }
            return WinJS.Promise.wrap(true);
        }, function (response) {
            return WinJS.Promise.wrap(false);
        });
    }

    function catalogSave(catalog) {
        if (catalog.__new) {
            return catalogSaveNew(catalog);
        }
        else {
            return catalogSaveEdits(catalog);
        }
    }

    function catalogSaveEdits(product) {
        var rawProduct = product;
        if (product.backingData) {
            rawProduct = product.backingData;
        }
        return WinJS.xhr({
            type: 'put',
            url: baseAddress + "/catalog/" + encodeURIComponent(product.skuNumber),
            headers: rest_headers,
            data: JSON.stringify(rawProduct),
        }).then(function (response) {
            return WinJS.Promise.wrap(product);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function catalogSaveNew(product) {
        var rawProduct = product;
        if (product.backingData) {
            rawProduct = product.backingData;
        }
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/catalog",
            headers: rest_headers,
            data: JSON.stringify(rawProduct),
        }).then(function (response) {
            if (response.status == 201) {
                catalog.push(product);
            }
            return WinJS.Promise.wrap(product);
        }, function (response) {
            if (response.status == 409) { // already exists (conflict)
                return catalogSaveEdits(product);
            }
            else {
                return WinJS.Promise.wrap(null);
            }
        });
    }


    function catalogCreate() {
        var newCatalog = WinJS.Binding.as({
            skuNumber: '',
            description: '',
            unit: '',
            unitPrice: ''
        });
        newCatalog.__new = true;
        return newCatalog;
    }

    function dealersGet() {

        return WinJS.xhr({ url: baseAddress + "/dealers?unique=" + Date.now(), headers: rest_headers }).then(function (response) {
            if (response.status == 0) {
                reporterror("Could not retrieve dealers", "Unfortunately, the server could not be contacted in order to retrieve dealer details.", "");
                return null;
            }
            Data.dealers.splice(0, Data.dealers.length);
            var theDealers = JSON.parse(response.responseText);
            theDealers.forEach(function processDealerData(dealerData) {
                dealers.push(WinJS.Binding.as(dealerData));
            });
            return WinJS.Promise.wrap(dealers);
        }, function (err) {
            reporterror("Could not retrieve dealers", "Unfortunately, the server could not be contacted in order to retrieve dealer details.", err);
            return WinJS.Promise.wrap(null);
        });
    }

    function dealerDelete(dealer) {
        return WinJS.xhr({
            type: 'delete',
            url: baseAddress + "/dealers/" + encodeURIComponent(dealer.name)
        }).then(function (response) {
            if (response.status == 204) {
                var index = dealers.indexOf(dealer);
                if (index > -1) {
                    dealers.splice(index, 1);
                }
            }
            return WinJS.Promise.wrap(true);
        }, function (response) {
            return WinJS.Promise.wrap(false);
        });
    }

    function dealerSave(dealer) {
        if (dealer.__new) {
            return dealerSaveNew(dealer);
        }
        else {
            return dealerSaveEdits(dealer);
        }
    }

    function dealerSaveEdits(dealer) {
        var rawDealer = dealer;
        if (dealer.backingData) {
            rawDealer = dealer.backingData;
        }
        return WinJS.xhr({
            type: 'put',
            url: baseAddress + "/dealers/" + encodeURIComponent(dealer.name),
            headers: rest_headers,
            data: JSON.stringify(rawDealer),
        }).then(function (response) {
            return WinJS.Promise.wrap(dealer);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function dealerSaveNew(dealer) {
        var rawDealer = dealer;
        if (dealer.backingData) {
            rawDealer = dealer.backingData;
        }
        return WinJS.xhr({
            type: 'post',
            url: baseAddress + "/dealers",
            headers: rest_headers,
            data: JSON.stringify(rawDealer),
        }).then(function (response) {
            if (response.status == 201) {
                dealers.push(dealer);
            }
            return WinJS.Promise.wrap(dealer);
        }, function (response) {
            return WinJS.Promise.wrap(null);
        });
    }

    function dealerCreate() {
        var newDealer = WinJS.Binding.as({
            name: '',
            contact: '',
            address: '',
            email: '',
            phone: ''
        });
        newDealer.__new = true;
        return newDealer;
    }

})();

