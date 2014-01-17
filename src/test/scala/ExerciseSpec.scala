import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

class ExerciseSpec extends FunSpec with ShouldMatchers {
  describe("Exercise") {
    def register = new CashRegister( 10, List('books, 'food, 'medical), 5 )

    describe("sample input 1") {
      it("should produce the sample output 1") {
        // Input 1:
        // 1 book at 12.49
        // 1 music CD at 14.99
        // 1 chocolate bar at 0.85
        val book   = Product( "book", 1249, 'books, false ) 
        val cd     = Product( "music CD", 1499, 'music, false ) 
        val choco  = Product( "chocolate bar", 85, 'food, false ) 
        val basket = new ShoppingBasket()

        basket.add( book )
        basket.add( cd )
        basket.add( choco )

        register.computeReceipt( basket ).toString should be(
          """1 book: 12.49
            |1 music CD: 16.49
            |1 chocolate bar: 0.85
            |Sales Taxes: 1.50
            |Total: 29.83""".stripMargin
        )
      }
    }

    describe("sample input 2") {
      it("should produce the sample output 2") {
        // Input 2:
        // 1 imported box of chocolates at 10.00
        // 1 imported bottle of perfume at 47.50
        val chocolate = Product( "imported box of chocolate", 1000, 'food, true ) 
        val perfume   = Product( "imported bottle of perfume", 4750, 'beauty, true ) 
        val basket    = new ShoppingBasket()

        basket.add( chocolate )
        basket.add( perfume )

        register.computeReceipt( basket ).toString should be(
          """1 imported box of chocolate: 10.50
            |1 imported bottle of perfume: 54.65
            |Sales Taxes: 7.65
            |Total: 65.15""".stripMargin
        )
      }
    }

    describe("sample input 3") {
      it("should produce the sample output 3") {
        // Input 3:
        // 1 imported bottle of perfume at 27.99
        // 1 bottle of perfume at 18.99
        // 1 packet of headache pills at 9.75
        // 1 box of imported chocolates at 11.25
        val perfume   = Product( "imported bottle of perfume", 2799, 'beauty, true ) 
        val perfume2  = Product( "bottle of perfume", 1899, 'beauty, false ) 
        val pills     = Product( "packet of headache pills", 975, 'medical, false ) 
        val chocolate = Product( "box of imported chocolates", 1125, 'food, true ) 
        val basket    = new ShoppingBasket()

        basket.add( perfume )
        basket.add( perfume2 )
        basket.add( pills )
        basket.add( chocolate )

        register.computeReceipt( basket ).toString should be(
          """1 imported bottle of perfume: 32.19
            |1 bottle of perfume: 20.89
            |1 packet of headache pills: 9.75
            |1 box of imported chocolates: 11.85
            |Sales Taxes: 6.70
            |Total: 74.68""".stripMargin
        )
      }
    }
  }
}
