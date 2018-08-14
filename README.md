# On Fauna Workshop using Fauna Shell and Indexes

Checkout this repository

```
git clone git@github.com:retroryan/on-fauna.git
```

## Exercise 1 - Setup the Fauna Shell

1. Create a cloud account on the [fauna website](https://fauna.com/)

1. Setup the [fauna shell](https://github.com/fauna/fauna-shell)

1. Login to the cloud in the fauna shell:

`fauna cloud-login`

## Exercise 2 - Create a Basic Schema in the Fauna Shell

Create a database and enter the shell to interact with the database

```
    fauna create-database demo
    fauna shell demo
```

Create the initial classes
```
    CreateClass({name: "customer"})
```

Create the indexes
```
    CreateIndex(
    {
      name: "all_customers",
      source: Class("customer")
    })

    CreateIndex(
        {
          name: "customers_by_id",
          source: Class("customer"),
          terms: [{ field: ["data", "customerID"] }],
          values: [{ field: ["ref"]}]
        })

```

Query the schema

```
    Paginate(Classes(null))
    Get(Class("customer"))
    Paginate(Indexes(null))
```

## Exercise 3 - Enter some test data

```
Create(Class("customer"),{
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

Create(Class("customer"),{
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
Match(Index("all_customers"))

Paginate(Match(Index("all_customers")))

Map(
  Paginate(Match(Index("all_customers"))),
  Lambda("cst",
      Get(Var("cst"))
    )
)

Update(
    Ref(Class("customer"), "207455490912813568"),
        { data: { contactTitle: ["Marketing Director"] } }
)

Update(
    Ref(Class("customer"), "207455490912813568"),
        { data: { flag: ["VIP Customer!", "High Value Customer"] } }
)

Replace(
    Ref(Class("customer"), "207455490912813568"),{
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
Paginate(Indexes(null))
```

## Exercise 7 - Understanding Paginate

```
Paginate(Match(Index("all_orders")), {size:10})

Paginate(Match(Index("all_orders_by_customer_id")), {size:50, after:"LONEP"})

Paginate(Match(Index("all_orders_by_customer_id")), {size:50, before:"LONEP"})

Paginate(Match(Index("all_orders_by_date")), {size:10, after:Date("1998-01-01")})

Paginate(Match(Index("all_orders_by_date")), {size:10, before:Date("1998-01-01")})

```

## Exercise 8 - Understanding Select

```
Paginate(Match(Index("orders_by_customer_id"), "LONEP"))

Select(0,Paginate(Match(Index("orders_by_customer_id"), "LONEP")))

Select([0,2],Paginate(Match(Index("orders_by_customer_id"), "LONEP")))

Map(
  Paginate(Match(Index("orders_by_customer_id"), "LONEP")),
  Lambda("ordr",
      Var("ordr")
    )
)

Map(
   Paginate(Match(Index("orders_by_customer_id"), "LONEP")),
   Lambda("ordr",
       Select(2,Var("ordr"))
     )
 )


Map(
   Paginate(Match(Index("orders_by_customer_id"), "LONEP")),
   Lambda("ordr",
       Get(Select(2,Var("ordr")))
     )
 )

Map(
    Paginate(Match(Index("orders_by_customer_id"), "LONEP")),
    Lambda("ordr",
        Select(["data","details"],
            Get(Select(2,Var("ordr")))
        )
      )
)

Map(
    Paginate(Match(Index("orders_by_customer_id"), "LONEP")),
    Lambda("ordr",
        SelectAll(["data","details", "productID"],
            Get(Select(2,Var("ordr")))
        )
      )
)

Map(
    Paginate(Match(Index("orders_by_customer_id"), "LONEP")),
    Lambda("ordr",
        SelectAll(["data","details", "productID"],
            Get(Select(2,Var("ordr")))
        )
      )
)

Map(
  Paginate(Match(Index("order_by_id"), 10307)),
  Lambda("ordr",
       Let(
             {
                customer: Get(Var("ordr")),
                customerID: Casefold(Select((["data","customerID"]),Get(Var("ordr"))))
             },
             Map(
               Paginate(Match(Index("customer_by_id"), Var("customerID"))),
               Lambda("cst",
                   [Var("customer"), Get(Var("cst"))]
                 )
             )
         )
    )
)

Map(
  Paginate(Match(Index("all_orders")),{size:1000}),
  Lambda("ordr",
       Let(
             {
                customer: Get(Var("ordr")),
                customerID: Casefold(Select((["data","customerID"]),Get(Var("ordr"))))
             },
             Map(
               Paginate(Distinct(Match(Index("customer_by_id"), Var("customerID")))),
               Lambda("cst",
                   SelectAll(["data", "customerID"],Get(Var("cst")))
                 )

             )
        )
    )
)

```


## Exercise 9 - Uniqueness Constraints with Index - Enforcing a UID

```

Get(Index("all_orders"))

Let( {
  // Get the latest orders.
  head:Paginate(Match(Index("all_orders")), {size:1})
  },
  Var("head")
)

Let( {
  // Get the latest orders.
  head:Paginate(Match(Index("all_orders")), {size:1})
  },
  Let({
     // Select the last index out of the order details
     last_index:Select([0,0],Var("head"))
    },
    Var("last_index")
   )
)

Let( {
  // Get the latest orders.
  head:Paginate(Match(Index("all_orders")), {size:1})
  },
  Let({
     // Select the last index out of the order details
     last_index:Select([0,0],Var("head"))
    },
    // Create a new order using the last index from let
    Create(Class("order"),{
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

Let( {
  // Get the latest orders.
  head:Paginate(Match(Index("all_orders")), {size:1})
  },
  Let({
     // Select the last index out of the order details
     last_index:Select([0,0],Var("head"))
    },
    // Create a new order using the last index from let
    Create(Class("order"),{
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

Update(Index("all_orders"), {unique:true})

Create(Class("order"),{
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
Map(
    Paginate(
        Match(Index("orders_by_customer_id"),"ERNSH"), {size:2}
    ),
    Lambda("ordr",
        Delete(
            Select([2],Var("ordr"))
        )
     )
)
```

```
Paginate(
    Match(
    Index("orders_by_customer_id"),"ERNSH"),
        { events:true }
)


```

Get the latest orders since the last timestamp

```

Create(Class("order"),{
        data:{
    		"orderID" : 11089,
    		"customerID" : "LILAS",
    		"employeeID" : 6,
    		"productID" : 11,
    		"description" : "please wrap"
    	}
    })

Paginate(Match(Index("all_orders")), {after:1534198119501532})

```

Get the order from a specific time

```

Get(Ref(Class("order"), "207496352507101696") )

Get(Ref(Class("order"), "207496352507101696"),1534142792066902 )
```

Look at the history of a class instance
```
Create(Class("order"),{
        data:{
    		"orderID" : 11095,
    		"customerID" : "ERNSH",
    		"employeeID" : 6,
    		"productID" : 21,
    		"description" : "priority shipment"
    	}
    })

Update(
    Ref(Class("order"), "207553635813425668"),
        { data: { productID: 12, tag: "update order" } }
)

Get(Ref(Class("order"),"207553635813425668"),1534197421684875)

Paginate(Events(Ref(Class("order"), "207553635813425668")))

```
