import org.apache.spark.sql.{SparkSession, functions => F}

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Adthena Shopping Basket")
      .master(sys.env.getOrElse("SPARK_MASTER", "local[*]"))
      .getOrCreate()

    // sanity check Spark works in the container
    val df = spark.range(5).toDF("n")
    println("Spark sanity check:")
    df.withColumn("n2", F.col("n") * 2).show(false)

    // TODO: wire your CLI and pricing logic here
    println(s"Args: ${args.mkString("[", ", ", "]")}")

    spark.stop()
  }
}
