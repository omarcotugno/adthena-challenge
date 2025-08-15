object Main {
  def main(args: Array[String]): Unit = {
    val code = cli.PriceBasketApp.run(args)
    if (code != 0) sys.exit(code)
  }
}
