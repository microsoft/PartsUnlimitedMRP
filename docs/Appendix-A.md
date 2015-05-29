#Appendix A#
##APPENDIX A
```
db.catalog.insert(
[{"skuNumber" : "REF-687", "description" : "R404A", "unit" : "kilograms", "unitPrice" : 49.95 },
{"skuNumber" : "MAC-234", "description" : "Control, Pressure", "unit" : "", "unitPrice" : 129.95 },
{"skuNumber" : "MAC-613", "description" : "Solenoid Valve", "unit" : "", "unitPrice" : 89.95 },
{"skuNumber" : "REF-020", "description" : "Hybrid A/C Compressor", "unit" : "", "unitPrice" : 679.95 }])

db.dealers.insert({ "name" : "Terry Adams", "address" : "17760 Northeast 67th Court, Redmond, WA 98052", "email" : "terry@adams.com", "phone" : "425-885-6217" })

db.quotes.insert([{
"quoteId" : "0",
"validUntil" : "2015-05-01T00:00:00+0000",
"customerName" : "Walter Harp",
"dealerName" : "Terry Adams",
"terms" : "All work is to occur between 3pm and 5pm in the afternoon",
"unitDescription" : "Small compressor unit.",
"city" : "Seattle",
"unitCost" : "759.95",
"totalCost" : "759.95",
"discount" : "0.0",
"unit": "",
"height" : "420",
"width" : "275",
"depth" : "275",
"buildOnSite" : true,
"state" : "WA",
"postalCode" : "98023",
"ambientPeak" : "0",
"ambientAverage" : "0",
"purpose" : "Refrigerator",
"additionalItems" : []
},
{
"quoteId" : "1",
"validUntil" : "2015-01-01T00:00:00+0000",
"customerName" : "Jerry Morrison",
"dealerName" : "Terry Adams",
"terms" : "To be completed prior to final payment",
"unitDescription" : "Walk in Refrigerator",
"city" : "Seattle",
"unitCost" : "5699.95",
"totalCost" : "5599.95",
"discount" : "100.0",
"unit": "",
"height" : "240",
"width" : "3000",
"depth" : "2500",
"buildOnSite" : true,
"state" : "WA",
"postalCode" : "98089",
"ambientPeak" : "0",
"ambientAverage" : "0",
"purpose" : "Refrigerator",
"additionalItems" : []
},
{
"quoteId" : "2",
"validUntil" : "2015-02-01T00:00:00+0000",
"customerName" : "Harrison Hall",
"dealerName" : "Terry Adams",
"terms" : "Unit must fit in the 400x400x400 space built into the chiller",
"unitDescription" : "Freezer Unit.",
"city" : "Seattle",
"unitCost" : "489.95",
"totalCost" : "489.95",
"discount" : "0.0",
"unit": "",
"height" : "400",
"width" : "400",
"depth" : "400",
"buildOnSite" : true,
"state" : "WA",
"postalCode" : "98027",
"ambientPeak" : "0",
"ambientAverage" : "0",
"purpose" : "Freezer",
"additionalItems" : []
}
])

db.orders.insert([{
"orderId" : "0",
"quoteId" : "0",
"orderDate" : "2015-03-02T20:43:37+0000",
"status" : "Created",
"events" : []
},
{"orderId" : "1",
"quoteId" : "2",
"orderDate" : "2015-03-02T20:43:37+0000",
"status" : "DeliveryConfirmed",
"events" : []
}])

db.shipments.insert([{
"orderId" : "0",
"contactName" : "Walter Harp",
"primaryContactPhone" : {
  "phoneNumber" : "435-783-2378",
  "kind" : "Mobile"
},
"deliveryAddress" : {
  "street" : "34 Sheridan Street",
  "city" : "Seattle",
  "state" : "WA",
  "postalCode" : "98023",
  "specialInstructions" : ""
},
"events" : []
},
{
"orderId" : "2",
"contactName" : "Harrison Hall",
"primaryContactPhone" : {
  "phoneNumber" : "435-712-7234",
  "kind" : "Mobile"
},
"deliveryAddress" : {
  "street" : "84 Queen Street",
  "city" : "Seattle",
  "state" : "WA",
  "postalCode" : "98027",
  "specialInstructions" : "To be installed on meat freezer 3."
},
"events" : []
}])
```