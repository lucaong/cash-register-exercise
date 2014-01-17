import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class CashRegisterSpec extends FunSpec with ShouldMatchers {
  describe("BasketItem") {
    describe("cents") {
      it("takes quantity into consideration") {
        val beer       = Product( "beer", 350, 'food, true )
        val basketItem = BasketItem( beer, 3 )
        basketItem.cents should be( 3 * 350 )
      }
    }
  }

  describe("ShoppingBasket") {
    describe("add") {
      it("appends BasketItems to the ShoppingBasket") {
        val beer       = Product( "beer", 350, 'food, true ) 
        val whiteAlbum = Product( "white album", 1340, 'music, false )
        val basket     = new ShoppingBasket()

        basket.add( beer, 3 )
        basket.add( whiteAlbum, 1 )

        basket.items.toList match {
          case first :: second :: Nil =>
            first  should be( BasketItem( beer, 3 ) )
            second should be( BasketItem( whiteAlbum, 1 ) )
          case _ => throw new Error("unexpected result!")
        }
      }
    }
  }

  describe("ReceiptItem") {
    describe("toString") {
      it("prints a line in the Receipt") {
        val beer = Product( "beer", 350, 'food, true ) 
        val receiptItem = ReceiptItem( BasketItem( beer, 3 ), 155 )
        receiptItem.toString should be("3 beer: 12.05")
      }
    }
  }

  describe("Receipt") {
    def receipt = {
      val beer         = Product( "beer", 350, 'food, true ) 
      val whiteAlbum   = Product( "white album", 1340, 'music, false )
      val beerReceipt  = ReceiptItem( BasketItem( beer, 3 ), 155 )
      val albumReceipt = ReceiptItem( BasketItem( whiteAlbum, 1 ), 215 )
      new Receipt( List( beerReceipt, albumReceipt ) )
    }

    describe("totalTaxCents") {
      it("sums up all the tax cents") {
        receipt.totalTaxCents should be(370)
      }
    }

    describe("subtotalCents") {
      it("sums up all the net cents") {
        receipt.subtotalCents should be(2390)
      }
    }

    describe("toString") {
      it("prints out the full Receipt") {
        receipt.toString should be(
          """1 white album: 15.55
            |3 beer: 12.05
            |Sales Taxes: 3.70
            |Total: 27.60""".stripMargin
        )
      }
    }
  }

  describe("CashRegister") {
    def register = new CashRegister( 10, List('food), 5 )

    describe("taxPercentage") {
      it("adds the import tax if Product is imported") {
        val beer = Product( "beer", 350, 'food, true ) 
        register.taxPercentage( beer ) should be( 5 )
      }

      it("adds basic tax if the Product category is not exempted") {
        val whiteAlbum = Product( "white album", 1340, 'music, false ) 
        register.taxPercentage( whiteAlbum ) should be( 10 )
      }

      it("adds the basic tax and the import tax if Product is imported and not exempted") {
        val perfume = Product( "perfume", 3499, 'beauty, true ) 
        register.taxPercentage( perfume ) should be( 15 )
      }

      it("adds no tax if Product is exempted and not imported") {
        val blackTrickle = Product( "black trickle", 395, 'food, false ) 
        register.taxPercentage( blackTrickle ) should be( 0 )
      }
    }

    describe("taxCents") {
      it("applies the tax rate to the shelf price") {
        val perfume = Product( "perfume", 3499, 'beauty, true ) 
        register.taxCents( BasketItem( perfume, 2 ) ) should be( 1050 )
      }

      it("rounds up at the nearest multiple of 5 cents") {
        val perfume = Product( "perfume", 3480, 'beauty, true ) 
        register.taxCents( BasketItem( perfume, 2 ) ) should be( 1045 )
        val oliveOil = Product( "olive oil", 1185, 'food, true ) 
        register.taxCents( BasketItem( oliveOil, 1 ) ) should be( 60 )
      }
    }
  }
}
