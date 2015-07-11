var conn;
while (conn === undefined)
{
    try
    {
        conn = new Mongo("localhost:27017");
    }
    catch (e)
    {
        print(e);
    }

    sleep(100);
}

db = conn.getDB("ordering");
db.catalog.insert(
[
  {"skuNumber" : "LIG-0001", "description" : "Helogen Headlights (2 Pack)", "price" : 38.99, "inventory" : 10, "leadTime" : 3},
  {"skuNumber" : "LIG-0002", "description" : "Bugeye Headlights (2 Pack)", "price" : 48.99, "inventory" : 7, "leadTime" : 3},
  {"skuNumber" : "LIG-0003", "description" : "Turn Signal Light Bulb", "price" : 6.49, "inventory" : 18, "leadTime" : 3},
  {"skuNumber" : "WHE-0001", "description" : "Matte Finish Rim", "price" : 75.99, "inventory" : 4, "leadTime" : 5},
  {"skuNumber" : "WHE-0002", "description" : "Blue Performance Alloy Rim", "price" : 88.99, "inventory" : 8, "leadTime" : 5},
  {"skuNumber" : "WHE-0003", "description" : "High Performance Rim", "price" : 99.99, "inventory" : 3, "leadTime" : 5},
  {"skuNumber" : "WHE-0004", "description" : "Wheel Tire Combo", "price" : 72.49, "inventory" : 0, "leadTime" : 4},
  {"skuNumber" : "WHE-0005", "description" : "Chrome Rim Tire Combo", "price" : 129.99, "inventory" : 1, "leadTime" : 4},
  {"skuNumber" : "WHE-0006", "description" : "Wheel Tire Combo (4 Pack)", "price" : 219.99, "inventory" : 3, "leadTime" : 6},
  {"skuNumber" : "BRA-0001", "description" : "Disk and Pad Combo", "price" : 25.99, "inventory" : 0, "leadTime" : 6},
  {"skuNumber" : "BRA-0002", "description" : "Brake Rotor", "price" : 18.99, "inventory" : 4, "leadTime" : 4},
  {"skuNumber" : "BRA-0003", "description" : "Brake Disk and Calipers", "price" : 43.99, "inventory" : 2, "leadTime" : 8},
  {"skuNumber" : "BAT-0001", "description" : "12-Volt Calcium Battery", "price" : 129.99, "inventory" : 9, "leadTime" : 8},
  {"skuNumber" : "BAT-0002", "description" : "Spiral Coil Battery", "price" : 154.99, "inventory" : 3, "leadTime" : 10},
  {"skuNumber" : "BAT-0003", "description" : "Jumper Leads", "price" : 16.99, "inventory" : 6, "leadTime" : 3},
  {"skuNumber" : "OIL-0001", "description" : "Filter Set", "price" : 28.99, "inventory" : 3, "leadTime" : 4},
  {"skuNumber" : "OIL-0002", "description" : "Oil and Filter Combo", "price" : 34.49, "inventory" : 5, "leadTime" : 4},
  {"skuNumber" : "OIL-0003", "description" : "Synthetic Engine Oil", "price" : 39.99, "inventory" : 11, "leadTime" : 4}
])

db.dealers.insert({ "name" : "Terry Adams", "address" : "17760 Northeast 67th Court, Redmond, WA 98052", "email" : "terry@adams.com", "phone" : "425-885-6217" })

db.quotes.insert([{
"quoteId" : "0",
"validUntil" : "2015-05-01T00:00:00+0000",
"customerName" : "Walter Harp",
"dealerName" : "Terry Adams",
"city" : "Seattle",
"totalCost" : "51.97",
"discount" : "0.0",
"state" : "WA",
"postalCode" : "98023",
"quoteItems" : [
    {"skuNumber":"LIG-0001", "amount":1 },
    {"skuNumber":"LIG-0003", "amount":2 }]
},
{
"quoteId" : "1",
"validUntil" : "2015-01-01T00:00:00+0000",
"customerName" : "Jerry Morrison",
"dealerName" : "Terry Adams",
"city" : "Seattle",
"totalCost" : "25.99",
"state" : "WA",
"postalCode" : "98089",
"quoteItems" : [
  { "skuNumber" : "BRA-0001", "amount" : 1}
]
},
{
"quoteId" : "2",
"validUntil" : "2015-02-01T00:00:00+0000",
"customerName" : "Harrison Hall",
"dealerName" : "Terry Adams",
"city" : "Seattle",
"totalCost" : "600.90",
"discount" : "0.0",
"state" : "WA",
"postalCode" : "98027",
"quoteItems" : [
  { "skuNumber" : "WHE-0002", "amount" : 4},
  { "skuNumber" : "BRA-0003", "amount" : 4},
  { "skuNumber" : "OIL-0001", "amount" : 1},
  { "skuNumber" : "OIL-0001", "amount" : 1}
]
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
  "specialInstructions" : "Leave around by the back door."
},
"events" : []
}])
