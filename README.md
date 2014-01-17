# Cash Register Exercise

This is a possible solution of a programming exercise, also intended to show
some basic Scala language features, and a hybrid OOP/functional approach.

## Problem description

Basic sales tax is applicable at a rate of 10% on all goods, except books, 
food, and medical products that are exempt. Import duty is an additional sales 
tax applicable on all imported goods at a rate of 5%, with no exemptions.

When I purchase items I receive a receipt which lists the name of all the 
items and their price (including tax), finishing with the total cost of the 
items, and the total amounts of sales taxes paid. The rounding rules for sales 
tax are that for a tax rate of n%, a shelf price of p contains (np/100 rounded 
up to the nearest 0.05) amount of sales tax.

Write an application that prints out the receipt details for these shopping 
baskets.

## Running the project and its tests

You need to install [Scala](http://scala-lang.org/) and [sbt](http://www.scala-sbt.org/). It is as easy as asking your package manager. Then, go to the project folder.

To run the test suite: `sbt test`

To experiment with an interactive console: `sbt console`

The first time it might be slow as it needs to download and compile the Scala
environment. If you get out-of-memory errors, try with `export
SBT_OPTS=-XX:MaxPermSize=256m` (yes, the Scala compiler is hungry for memory).

## The solution

There would be many possible valid solutions with a wide range of approaches.
Here I outline one based on the observation that the problem of applying sales
tax can be model almost entirely in a purely functional way. This gives
benefits in testability and robustness, as mutable state and side effects are
minimized.

Scala is a natural choice for this, as its functional and object-oriented
nature gives us the possibility to remain in the domain of pure functions as
much as it make sense, but also does not prevent us to maintain a mutable state
and perform side effect when necessary.

The components of the solution are:

  - The `Product`, which is a good available on the market. It is represented
    as an immutable case class, which in this case just acts as a sort of
    'struct', collecting the product attributes: name, price, category and a
    boolean flag marking imported products. Prices are always stored in cents
    (as they should always be, if you don't want to get punched in the face by
    floating point arithmetic. Try doing 0.9 - 0.8 ;) )

  - When a `Product` is picked up in a certain quantity by a customer, a
    `BasketItem` is created. The `BasketItem` is immutable too.

  - `BasketItem`s are collected in a `ShoppingBasket`. This is the only
    non-purely-functional component, and it should be like that. The shopping
    basket has a state and can be modified by appending items. A functional
    approach returning a new immutable basket anytime an item is added would be
    artificial, less practical for a library user and too stretched out.

  - As the customer proceeds to the purchase, the `CashRegister` produces a
    `Receipt`, calculating taxes and totals. Each line of the receipt is a
    `ReceiptItem`, which adds taxes to the `BasketItem`. The `CashRegister`
    instance is responsible for tax calculations: it is the single responsible
    for tax rates and exempted categories. This is good: should these rates or
    category change, only one place has to be modified. Also, different
    `CashRegister` instances might handle different tax rates (maybe in
    different countries?).

  - The `Receipt` can be printed, which is, transformed to a string
    representation via its method `toString`. It does that by reducing all its
    `ReceiptItems` to their string representation (calling their method
    `toString`), and appending the totals.

## Caveats

Attention was mostly put on maintainability, testability and robustness, but on
the side of raw performance it can be improved. For example, item list
traversal can be reduced, and some method like `Receipt.toString` could use
memoization. That said, the solution should be reasonably efficient and not
waste resources in any nasty way.

Some classes could be made private inside other classes. In theory, only
`CacheRegister`, `Product`, `ShoppingBasket` and `Receipt` need to be exposed.
But listing them sequentially in the same file makes it easy to read the
solution code for didactic purposes.

# License (for the solution code)

The MIT License (MIT)

Copyright (c) 2014 Luca Ongaro

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
