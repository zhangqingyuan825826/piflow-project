package cn.piflow.bundle.json

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, JsonGroup, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.MapUtil
import org.apache.spark.sql.SparkSession

import scala.beans.BeanProperty

class JsonPathParser extends ConfigurableStop{

  @BeanProperty  val inportCount: Int = 1
  @BeanProperty  val outportCount: Int = 1

  var jsonPath: String = _
  var tag : String = _

  def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {

    val spark = pec.get[SparkSession]()

    val jsonDF = spark.read.option("multiline","true").json(jsonPath)
    val jsonDFNew = jsonDF.select(tag)
    jsonDFNew.printSchema()
    jsonDFNew.show(10)
    out.write(jsonDF)
  }

  def initialize(ctx: ProcessContext): Unit = {

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    jsonPath = MapUtil.get(map,"jsonPath").asInstanceOf[String]
    tag = MapUtil.get(map,"tag").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = ???

  override def getIcon(): Array[Byte] = ???

  override def getGroup(): StopGroup = {
    JsonGroup
  }

}

class JsonStringParser extends ConfigurableStop{

  val inportCount: Int = 1
  val outportCount: Int = 1

  var jsonString: String = _

  def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {

    val spark = pec.get[SparkSession]()
    val jsonRDD = spark.sparkContext.makeRDD(jsonString :: Nil)
    val jsonDF = spark.read.json(jsonRDD)

    jsonDF.show(10)
    out.write(jsonDF)
  }

  def initialize(ctx: ProcessContext): Unit = {

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    jsonString = MapUtil.get(map,"jsonString").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = ???

  override def getIcon(): Array[Byte] = ???

  override def getGroup(): StopGroup = {
    JsonGroup
  }

}
