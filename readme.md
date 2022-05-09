# Auction House
#### Authors: Jonathan Salazar, Cyrus McCormick

### How to Use:

This project is comprised of three components:
 1) The Bank system
 2) Auction House systems
 3) Users

In order to function properly, the bank must be the first system to be run; however, the order that any various auction
houses or users are initialized in do not matter.

## Use default command for each unless a custom IP address & port must be used for the bank server,
## in which case the SAME address & port must be provided to each executable where asked for.

To run the bank system use the following:

<b>Default port:</b> ```java -jar bank.jar```

<b>User provided port:</b> ```java -jar bank.jar portNumber```

To run the Auction House system use the following:

<b>Default address & port:</b> ```java -jar AH.jar```

<b>User provided address & port:</b> ```java -jar AH.jar IPAddress port```


To run the user use the following:

<b>Default address & port:</b> ```java -jar user.jar clientUsername initialBalance```

<b>User provided address & port:</b> ```java -jar AH.jar clientUsername initialBalance IPAddress port```


### Agent commands:
> Refresh connection to auction houses - ah

> List items for sale by auction houses - items

> Bid on an item - bid auctionHouseId itemId bidAmount


### Contribution breakdown:
A comprehensive view of contributions can be found at our 
[lobogit repository](https://lobogit.unm.edu/forcs351/cs351-project5-auctionhouse).

##### Otherwise, here is a brief breakdown:

Cyrus McCormick:
1) Head of bank system.

Jonathan Salazar:
1) Head of Auction House system.

Both: 
1) Agent communications and flow.
2) Various contributions to each others code both through debugging and through pair programming.

### Known bugs and issues:

No known bugs or issues.