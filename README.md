# On Fauna Workshop

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
    CreateClass({name: "order"})
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

    CreateIndex(
    {
      name: "orders_by_customer_id",
      source: Class("order"),
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

Create(Class("customer"),{
    data:{
		"customerID" : "QUEEN",
		"companyName" : "Queen Cozinha",
		"contactName" : "Lúcia Carvalho",
		"contactTitle" : "Marketing Assistant",
		"address" : {
			"street" : "Alameda dos Canàrios 891",
			"city" : "Sao Paulo",
			"region" : "SP",
			"postalCode" : "05487-020",
			"country" : "Brazil",
			"phone" : "(11) 555-1189"
		}
	}
})

Create(Class("order"),{
    data:{
		"orderID" : 10914,
		"customerID" : "QUEEN",
		"employeeID" : 6,
		"orderDate" : "1998-02-27 00:00:00.000",
		"requiredDate" : "1998-03-27 00:00:00.000",
		"shippedDate" : "1998-03-02 00:00:00.000",
		"shipVia" : 1,
		"freight" : 21.19,
		"shipName" : "Queen Cozinha",
		"shipAddress" : {
			"street" : "Alameda dos Canàrios 891",
			"city" : "Sao Paulo",
			"region" : "SP",
			"postalCode" : "05487-020",
			"country" : "Brazil"
		},
		"details" : [
			{
				"productID" : 71,
				"unitPrice" : 21.5,
				"quantity" : 25,
				"discount" : 0
			}
		]
	}
})

Create(Class("order"),{
    data:{
		"orderID" : 10914,
		"customerID" : "QUEEN",
		"employeeID" : 6,
		"orderDate" : "1998-02-27 00:00:00.000",
		"requiredDate" : "1998-03-27 00:00:00.000",
		"shippedDate" : "1998-03-02 00:00:00.000",
		"shipVia" : 1,
		"freight" : 21.19,
		"shipName" : "Queen Cozinha",
		"shipAddress" : {
			"street" : "Alameda dos Canàrios 891",
			"city" : "Sao Paulo",
			"region" : "SP",
			"postalCode" : "05487-020",
			"country" : "Brazil"
		},
		"details" : [
			{
				"productID" : 71,
				"unitPrice" : 21.5,
				"quantity" : 25,
				"discount" : 0
			}
		]
	}
})

Create(Class("order"),{
    data:{
		"orderID" : 10372,
		"customerID" : "QUEEN",
		"employeeID" : 5,
		"orderDate" : "1996-12-04 00:00:00.000",
		"requiredDate" : "1997-01-01 00:00:00.000",
		"shippedDate" : "1996-12-09 00:00:00.000",
		"shipVia" : 2,
		"freight" : 890.78,
		"shipName" : "Queen Cozinha",
		"shipAddress" : {
			"street" : "Alameda dos Canàrios 891",
			"city" : "Sao Paulo",
			"region" : "SP",
			"postalCode" : "05487-020",
			"country" : "Brazil"
		},
		"details" : [
			{
				"productID" : 20,
				"unitPrice" : 64.8,
				"quantity" : 12,
				"discount" : 0.25
			},
			{
				"productID" : 38,
				"unitPrice" : 210.8,
				"quantity" : 40,
				"discount" : 0.25
			},
			{
				"productID" : 60,
				"unitPrice" : 27.2,
				"quantity" : 70,
				"discount" : 0.25
			},
			{
				"productID" : 72,
				"unitPrice" : 27.8,
				"quantity" : 42,
				"discount" : 0.25
			}
		]
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

Map(
  Paginate(Match(Index("orders_by_customer_id"), "QUEEN")),
  Lambda("ord",
      Get(Var("ord"))
    )
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

In the cloud dashboard create a new secret key under [Cloud Keys](https://app.fauna.com/keys)

```
    export FAUNA_SECRET= ...
    unzip on-fauna-0.1.zip
    cd on-fauna-0.1
    bin/on-fauna
```

## Exercise 6 - Understanding Paginate and Select

```

Paginate(Match(Index("orders_by_customer_id"), "LONEP"))

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

## Exercise 7 - Indexes in Depth - Sorting, Transformation and Pagination

## Exercise 8 - Uniqueness Constraints with Index - Enforcing a UID

## Exercise 9 - Temporality

## Exercise 10 - Security

