package something

import scala.annotation.StaticAnnotation

class enhance extends StaticAnnotation

object Test {
  @enhance class SomeClass { }
  val x = SomeClass()
}

@enhance class SomeClas1s { }

object Test1 {
  val y = SomeClass()
}

object Test2 {
  /*start*/(Test.x, Test1.y)/*end*/
}
//(Int, Int)