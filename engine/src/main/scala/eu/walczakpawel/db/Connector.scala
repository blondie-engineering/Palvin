package eu.walczakpawel.db

import java.util
import java.util.Calendar

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport
import com.amazonaws.services.dynamodbv2.document.spec._
import com.amazonaws.services.dynamodbv2.document.utils.{NameMap, ValueMap}
import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.services.dynamodbv2.model._
import eu.walczakpawel.model.Campaign

object Connector {


  val client: DynamoDB = sys.env("IS_LOCAL") match {
    case "true" => new DynamoDB(
      AmazonDynamoDBClientBuilder.standard()
        .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
        .build())
    case _ => new DynamoDB(AmazonDynamoDBClientBuilder.standard()
      .withRegion(Regions.EU_WEST_1)
      .build())
  }


  private def createConnection(): DynamoDB = {
    return this.client
  }

  @throws[Exception]
  def createAdDataTable(): Unit = {

    val dynamoDB: DynamoDB = createConnection()
    val tableName = "Campaigns"

    try {
      System.out.println("Attempting to create table; please wait...")
      val table: Table = dynamoDB.createTable(tableName, util.Arrays.asList(new KeySchemaElement("company", KeyType.HASH), // Partition
        // key
        new KeySchemaElement("transaction_date", KeyType.RANGE)), // Sort key
        util.Arrays.asList(new AttributeDefinition("company", ScalarAttributeType.S), new AttributeDefinition("transaction_date", ScalarAttributeType.S)), new ProvisionedThroughput(10L, 10L))
      table.waitForActive
      System.out.println("Success.  Table status: " + table.getDescription.getTableStatus)
    } catch {
      case e: Exception =>
        System.err.println("Unable to create table: ")
        System.err.println(e.getMessage)
    }
  }

  def loadData(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")
    val company = "Coca-Cola"
    val transaction_date = "Thu Nov 21 19:57:34 CET 2019"

    try {
      table.putItem(new Item().withPrimaryKey("company", company, "transaction_date", transaction_date).withString("description", "Super campaign"))
      System.out.println("PutItem succeeded: " + company + " " + transaction_date)
    } catch {
      case e: Exception =>
        System.err.println("Unable to add campaign: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }
  }

  def loadCampaigns(campaigns: List[Campaign]): Unit = {
    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")
    val transaction_date = Calendar.getInstance().getTime().toString

    try {
        campaigns.foreach(c => {
          table.putItem(new Item().withPrimaryKey("company", c.company, "transaction_date", transaction_date).withNumber("amount", c.amount))
          System.out.println("PutItem succeeded: " + c.company + " " + transaction_date)
        })
    } catch {
      case e: Exception =>
        System.err.println(e.getMessage)
    }
  }

  def readData(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")
    val company = "Coca-Cola"
    val transaction_date = "Thu Nov 21 19:57:34 CET 2019"

    val spec = new GetItemSpec().withPrimaryKey("company", company, "transaction_date", transaction_date)

    try {
      System.out.println("Attempting to read the item...")
      val outcome = table.getItem(spec)
      System.out.println("GetItem succeeded: " + outcome)
    } catch {
      case e: Exception =>
        System.err.println("Unable to read item: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }
  }

  def updateData(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")
    val company = "Coca-Cola"
    val transaction_date = "Thu Nov 21 19:57:34 CET 2019"

    val updateItemSpec = new UpdateItemSpec().withPrimaryKey("company", company, "transaction_date", transaction_date)
      .withUpdateExpression("set amount = :r")
      .withValueMap(new ValueMap().withNumber(":r", 10))
      .withReturnValues(ReturnValue.UPDATED_NEW)

    try {
      System.out.println("Updating the item...")
      val outcome = table.updateItem(updateItemSpec)
      System.out.println("UpdateItem succeeded:\n" + outcome.getItem.toJSONPretty)
    } catch {
      case e: Exception =>
        System.err.println("Unable to update item: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }
  }

  def deleteData(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")
    val company = "Coca-Cola"
    val transaction_date = "Thu Nov 21 19:57:34 CET 2019"

    val deleteItemSpec = new DeleteItemSpec().withPrimaryKey(new PrimaryKey("company", company, "transaction_date", transaction_date)).withConditionExpression("company = :val").withValueMap(new ValueMap().withString(":val", "Coca-Cola"))

    try {
      System.out.println("Attempting a conditional delete...")
      table.deleteItem(deleteItemSpec)
      System.out.println("DeleteItem succeeded")
    } catch {
      case e: Exception =>
        System.err.println("Unable to delete item: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }
  }

  def scanTable(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns");
    val scanSpec = new ScanSpec().withProjectionExpression("amount, company, transaction_date")
                      .withFilterExpression("#am between :start_am and :end_am")
                      .withNameMap(new NameMap().`with`("#am", "amount"))
                      .withValueMap(new ValueMap().withNumber(":start_am", 10)
                      .withNumber(":end_am", 1000000))

    try {
      val items = table.scan(scanSpec)
      val iter = items.iterator
      while ( {
        iter.hasNext
      }) {
        val item = iter.next
        System.out.println(item.toString)
      }
    } catch {
      case e: Exception =>
        System.err.println("Unable to scan the table:")
        System.err.println(e.getMessage)
    }
  }

  def deleteTable(): Unit = {
    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")

    try {
      System.out.println("Attempting to delete table; please wait...")
      table.delete
      table.waitForDelete
      System.out.print("Success.")
    } catch {
      case e: Exception =>
        System.err.println("Unable to delete table: ")
        System.err.println(e.getMessage)
    }
  }

}



