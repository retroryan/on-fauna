# On Fauna Workshop using the Fauna Dashboard


## Exercise 2 - Create a Basic Schema in the Fauna Shell

Create a database and enter the shell to interact with the database

```
    fauna create-database demo
    fauna shell demo
```

Create the initial classes
```
    q.CreateClass({name: "customer"})
```

Create the indexes
```
    q.CreateIndex(
    {
      name: "all_customers",
      source: q.Class("customer")
    })

    q.CreateIndex(
        {
          name: "customers_by_id",
          source: q.Class("customer"),
          terms: [{ field: ["data", "customerID"] }],
          values: [{ field: ["ref"]}]
        })

```

Query the schema

```
    q.Paginate(q.Classes(null))
    q.Get(q.Class("customer"))
    q.Paginate(q.Indexes(null))
```

## Exercise 3 - Enter some test data

```
q.Create(q.Class("customer"),{
        data:{
            "customerID" : "PRINI",
            "companyName" : "Princesa Isabel Vinhos",
            "contactName" : "Isabel de Castro",
            "contactTitle" : "Sales Representative",
            "address" : {
                "street" : "Estrada da saúde n. 58",
                "city" : "Lisboa",
                "region" : "NULL",
                "postalCode" : "1756",
                "country" : "Portugal",
                "phone" : "(1) 356-5634"
            }
	    }
})

q.Create(q.Class("customer"),{
    data:{
		"customerID" : "LONEP",
		"companyName" : "Lonesome Pine Restaurant",
		"contactName" : "Fran Wilson",
		"contactTitle" : "Sales Manager",
		"address" : {
			"street" : "89 Chiaroscuro Rd.",
			"city" : "Portland",
			"region" : "OR",
			"postalCode" : "97219",
			"country" : "USA",
			"phone" : "(503) 555-9573"
		}
	}
})


```

## Exercise 4 - Basic Queries

```
q.Match(q.Index("all_customers"))

q.Paginate(q.Match(q.Index("all_customers")))

q.Map(
  q.Paginate(q.Match(q.Index("all_customers"))),
  q.Lambda("cst",
      q.Get(Var("cst"))
    )
)

q.Update(
    q.Ref(q.Class("customer"), "207604518630720013"),
        { data: { contactTitle: ["Marketing Director"] } }
)

q.Update(
    q.Ref(q.Class("customer"), "207455490912813568"),
        { data: { flag: ["VIP Customer!", "High Value Customer"] } }
)

q.Replace(
    q.Ref(q.Class("customer"), "207455490912813568"),{
        data:{
            "customerID" : "PRINI",
            "contactName" : "Isabel de Pineada"
	    }
    }
)
```

## Exercise 5 - Load the test Data

If you are running against the cloud first get your key.

In the cloud dashboard create a new secret key under [Cloud Keys](https://app.fauna.com/keys) and then set it in the terminal window

And then run:


```
    export FAUNA_URL=http://localhost:8443
    export FAUNA_SECRET=sdfef2f23432
    export DB_NAME="northwinds"
```

To run against localhost or your own instance
```
    export FAUNA_URL=http://localhost:8443
    export FAUNA_SECRET=secret
    export DB_NAME="northwinds"
```

```
    unzip on-fauna-0.1.zip
    cd on-fauna-0.1
    bin/on-fauna
```

Now login to the fauna shell to the new database

```
    fauna shell northwinds
```

## Exercise 6 - Indexes in Depth - Sorting and Transformation

Look at all the indexes that have been created

```
q.Paginate(q.Indexes(null))
```

## Exercise 7 - Understanding Paginate

```
q.Paginate(q.Match(q.Index("all_orders")), {size:10})

q.Paginate(q.Match(q.Index("all_orders_by_customer_id")), {size:50, after:"LONEP"})

q.Paginate(q.Match(q.Index("all_orders_by_customer_id")), {size:50, before:"LONEP"})

q.Paginate(q.Match(q.Index("all_orders_by_date")), {size:10, after:Date("1998-01-01")})

q.Paginate(q.Match(q.Index("all_orders_by_date")), {size:10, before:Date("1998-01-01")})

```

## Exercise 8 - Understanding Select

```
q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP"))

q.Select(0,q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")))

q.Select([0,2],q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")))

q.Map(
  q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")),
  q.Lambda("ordr",
      Var("ordr")
    )
)

q.Map(
   q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")),
   q.Lambda("ordr",
       q.Select(2,Var("ordr"))
     )
 )


q.Map(
   q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")),
   q.Lambda("ordr",
       q.Get(q.Select(2,Var("ordr")))
     )
 )

q.Map(
    q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")),
    q.Lambda("ordr",
        q.Select(["data","details"],
            q.Get(q.Select(2,Var("ordr")))
        )
      )
)

q.Map(
    q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")),
    q.Lambda("ordr",
        q.SelectAll(["data","details", "productID"],
            q.Get(q.Select(2,Var("ordr")))
        )
      )
)

q.Map(
    q.Paginate(q.Match(q.Index("orders_by_customer_id"), "LONEP")),
    q.Lambda("ordr",
        q.SelectAll(["data","details", "productID"],
            q.Get(q.Select(2,Var("ordr")))
        )
      )
)

q.Map(
  q.Paginate(q.Match(q.Index("order_by_id"), 10307)),
  q.Lambda("ordr",
       Let(
             {
                customer: q.Get(Var("ordr")),
                customerID: Casefold(q.Select((["data","customerID"]),q.Get(Var("ordr"))))
             },
             q.Map(
               q.Paginate(q.Match(q.Index("customer_by_id"), Var("customerID"))),
               q.Lambda("cst",
                   [Var("customer"), q.Get(Var("cst"))]
                 )
             )
         )
    )
)

q.Map(
  q.Paginate(q.Match(q.Index("all_orders")),{size:1000}),
  q.Lambda("ordr",
       Let(
             {
                customer: q.Get(Var("ordr")),
                customerID: Casefold(q.Select((["data","customerID"]),q.Get(Var("ordr"))))
             },
             q.Map(
               q.Paginate(Distinct(q.Match(q.Index("customer_by_id"), Var("customerID")))),
               q.Lambda("cst",
                   q.SelectAll(["data", "customerID"],q.Get(Var("cst")))
                 )

             )
        )
    )
)

```


## Exercise 9 - Uniqueness Constraints with Index - Enforcing a UID

```

q.Get(q.Index("all_orders"))

q.Let( {
  // Get the latest orders.
  head:q.Paginate(q.Match(q.Index("all_orders")), {size:1})
  },
  Var("head")
)

q.Let( {
  // Get the latest orders.
  head:q.Paginate(q.Match(q.Index("all_orders")), {size:1})
  },
  q.Let({
     // Select the last q.Index out of the order details
     last_index:q.Select([0,0],Var("head"))
    },
    Var("last_index")
   )
)

q.Let( {
  // Get the latest orders.
  head:q.Paginate(q.Match(q.Index("all_orders")), {size:1})
  },
  q.Let({
     // Select the last index out of the order details
     last_index:q.Select([0,0],Var("head"))
    },
    // Create a new order using the last index from let
    q.Create(q.Class("order"),{
        data:{
    		"orderID" : Add(Var("last_index"),1),
    		"customerID" : "RICAR",
    		"employeeID" : 6,
    		"productID" : 7,
    		"description" : "please rush shipment"
    	}
    })
   )
)

q.Let( {
  // Get the latest orders.
  head:q.Paginate(q.Match(Index("all_orders")), {size:1})
  },
  q.Let({
     // Select the last index out of the order details
     last_index:q.Select([0,0],Var("head"))
    },
    // Create a new order using the last index from let
    q.Create(q.Class("order"),{
        data:{
    		"orderID" : Add(Var("last_index"),1),
    		"customerID" : "ERNSH",
    		"employeeID" : 6,
    		"productID" : 15,
    		"description" : "priority shipment"
    	}
    })
   )
)

Update(q.Index("all_orders"), {unique:true})

q.Create(q.Class("order"),{
        data:{
    		"orderID" : 11091,
    		"customerID" : "ERNSH",
    		"employeeID" : 6,
    		"productID" : 21,
    		"description" : "priority shipment"
    	}
    })

```

## Exercise 10 - Temporality

Event types are different for sets vs. instances:

sets have add/remove

class instances have create/update/delete

First delete the last order we entered by getting the last orders for Ernsh

```
q.Map(
    q.Paginate(
        q.Match(q.Index("orders_by_customer_id"),"ERNSH"), {size:2}
    ),
    q.Lambda("ordr",
        q.Delete(
            q.Select([2],Var("ordr"))
        )
     )
)
```

```
q.Paginate(
    q.Match(
    q.Index("orders_by_customer_id"),"ERNSH"),
        { events:true }
)


```

Get the latest orders since the last timestamp

```

q.Create(q.Class("order"),{
        data:{
    		"orderID" : 11089,
    		"customerID" : "LILAS",
    		"employeeID" : 6,
    		"productID" : 11,
    		"description" : "please wrap"
    	}
    })

q.Paginate(q.Match(q.Index("all_orders")), {after:1534198119501532})

```

Get the order from a specific time

```

q.Get(q.Ref(q.Class("order"), "207496352507101696") )

q.Get(q.Ref(q.Class("order"), "207496352507101696"),1534142792066902 )
```

Look at the history of a class instance
```
q.Create(q.Class("order"),{
        data:{
    		"orderID" : 11095,
    		"customerID" : "ERNSH",
    		"employeeID" : 6,
    		"productID" : 21,
    		"description" : "priority shipment"
    	}
    })

Update(
    q.Ref(q.Class("order"), "207553635813425668"),
        { data: { productID: 12, tag: "update order" } }
)

q.Get(q.Ref(q.Class("order"),"207553635813425668"),1534197421684875)

q.Paginate(q.Events(q.Ref(q.Class("order"), "207553635813425668")))

```
