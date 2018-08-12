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
    CreateClass({ name: "customer" })
    CreateClass({ name: "order" })
```