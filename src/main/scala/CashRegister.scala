import collection.mutable.ListBuffer

/** A Product is a good on the market. */
case class Product(
  name:     String,
  cents:    Int,
  category: Symbol,
  imported: Boolean
)

/** A BasketItem is just a Product with quantity. It represents
  * a certain quantity of this product picked up by a customer. */
case class BasketItem(
  product:  Product,
  quantity: Int
) {
  def cents = product.cents * quantity
  def name  = product.name
}

/** A ShoppingBasket is a list of BasketItems. */
class ShoppingBasket {
  val items = ListBuffer.empty[BasketItem]

  def add(
    product:  Product,
    quantity: Int = 1
  ) = items += BasketItem( product, quantity )
}

/** A ReceiptItem is a BasketItem with taxes on top. It represents
  * a line in the Receipt. */
case class ReceiptItem(
  item:     BasketItem,
  taxCents: Int
) {
  def cents      = item.cents
  def quantity   = item.quantity
  def name       = item.name
  def finalCents = cents + taxCents
  def finalPrice = finalCents / 100.0
  override def toString = f"$quantity%s $name%s: $finalPrice%.2f"
}

/** A receipt maintains a list of ReceiptItems, and also knows how
  * to calculates totals. */
class Receipt( items: List[ReceiptItem] = Nil ) {
  lazy val totalTaxCents = items.foldLeft(0)( _ + _.taxCents )

  lazy val subtotalCents = items.foldLeft(0)( _ + _.cents )

  def totalTax = totalTaxCents / 100.0

  def total    = ( totalTaxCents + subtotalCents ) / 100.0

  def +( it: ReceiptItem ) = new Receipt( it :: items )

  override def toString = {
    ( items.reverse.foldLeft("")( _ + _ + "\n" )
      + f"Sales Taxes: $totalTax%.2f\n"
      + f"Total: $total%.2f" )
  }
}

/** A CashRegister knows about tax rates and exemptions, and
  * can produce a receipt for a ShoppingBasket. */
class CashRegister(
  val basicTaxPercentage:  Int,
  val exemptedCategories:  List[Symbol],
  val importTaxPercentage: Int
) {
  def computeReceipt(
    basket: ShoppingBasket
  ) = {
    basket.items.foldLeft( new Receipt() ) { ( receipt, item ) =>
      receipt + ReceiptItem( item, taxCents( item ) )
    }
  }

  def taxCents( item: BasketItem ) = {
    item match {
      case BasketItem( product, quantity ) =>
        val taxPerc = taxPercentage( product )
        val taxRaw  = ( item.cents.toFloat * taxPerc / 100 )
        5 * ( taxRaw / 5 ).ceil.toInt
    }
  }

  def taxPercentage( product: Product ) = {
    val importPerc = if ( product.imported ) importTaxPercentage else 0
    val basicPerc  = if ( exemptedCategories.contains( product.category ) ) 0 else basicTaxPercentage
    importPerc + basicPerc
  }
}
