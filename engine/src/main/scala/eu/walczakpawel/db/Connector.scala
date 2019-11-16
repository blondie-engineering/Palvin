package eu.walczakpawel.db

import java.util

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport
import com.amazonaws.services.dynamodbv2.document.spec._
import com.amazonaws.services.dynamodbv2.document.utils.{NameMap, ValueMap}
import com.amazonaws.services.dynamodbv2.document._
import com.amazonaws.services.dynamodbv2.model._
import eu.walczakpawel.model.Campaign

class Connector {


  val client: DynamoDB = new DynamoDB(
    AmazonDynamoDBClientBuilder.standard()
      .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
      .build()
  )

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
    val transaction_date = "Peaky Blinders"

    try {
      table.putItem(new Item().withPrimaryKey("company", company, "transaction_date", transaction_date).withString("description", "Super campaign"))
      System.out.println("PutItem succeeded: " + company + " " + transaction_date)
    } catch {
      case e: Exception =>
        System.err.println("Unable to add movie: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }
  }

  def loadCampaigns(c: Campaign): Unit = {
    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns")


    try {
        table.putItem(new Item().withPrimaryKey("company", c.company, "transaction_date", c.date).withNumber("amount", c.amount))
        System.out.println("PutItem succeeded: " + c.company + " " + c.date)
    } catch {
      case e: Exception =>
        System.err.println(e.getMessage)
    }
  }

  def readData(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Movies")
    val company = "Coca-Cola"
    val date = "Peaky Blinders"

    val spec = new GetItemSpec().withPrimaryKey("company", company, "date", date)

    try {
      System.out.println("Attempting to read the item...")
      val outcome = table.getItem(spec)
      System.out.println("GetItem succeeded: " + outcome)
    } catch {
      case e: Exception =>
        System.err.println("Unable to read item: " + company + " " + date)
        System.err.println(e.getMessage)
    }
  }

  def updateData(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Movies")
    val company = "Coca-Cola"
    val transaction_date = "Peaky Blinders"

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
    val transaction_date = "Peaky Blinders"

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

  def queryData(): Unit = {
    val dynamoDB = createConnection()

    val table = dynamoDB.getTable("Campaigns");
    val company = "Coca-Cola"
    val transaction_date = "Peaky Blinders"

    val nameMap = new util.HashMap[String, String]();
    nameMap.put("#yr", "year");

    val valueMap = new util.HashMap[String, Object]();
    valueMap.put(":yyyy", new Integer(1985));

    val querySpec = new QuerySpec().withKeyConditionExpression("#yr = :yyyy").withNameMap(nameMap)
      .withValueMap(valueMap);


    try {
      System.out.println("Campaigns from 1985");
      val items: ItemCollection[QueryOutcome] = table.query(querySpec);
      var iterator: IteratorSupport[Item, QueryOutcome] = null;
      var item: Item = null;

      iterator = items.iterator();
      while (iterator.hasNext()) {
        item = iterator.next();
        System.out.println(item.getNumber("year") + ": " + item.getString("title"));
      }

    } catch {
      case e: Exception =>
        System.err.println("Unable to query item: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }

    valueMap.put(":yyyy", new Integer(1992));
    valueMap.put(":letter1", "A");
    valueMap.put(":letter2", "L");

    querySpec.withProjectionExpression("#yr, title, info.genres, info.actors[0]")
      .withKeyConditionExpression("#yr = :yyyy and title between :letter1 and :letter2").withNameMap(nameMap)
      .withValueMap(valueMap);

    try {
      System.out.println("Movies from 1992 - titles A-L, with genres and lead actor");
      var items: ItemCollection[QueryOutcome] = table.query(querySpec);
      var iterator: IteratorSupport[Item, QueryOutcome] = null;
      var item: Item = null;
      items = table.query(querySpec);

      iterator = items.iterator();
      while (iterator.hasNext()) {
        item = iterator.next();
        System.out.println(item.getNumber("year") + ": " + item.getString("title") + " " + item.getMap("info"));
      }

    } catch {
      case e: Exception =>
        System.err.println("Unable to query item: " + company + " " + transaction_date)
        System.err.println(e.getMessage)
    }
  }

  def scanTable(): Unit = {

    val dynamoDB = createConnection()
    val table = dynamoDB.getTable("Campaigns");
    val scanSpec = new ScanSpec().withProjectionExpression("amount, company, transaction_date")
                      .withFilterExpression("#am between :start_am and :end_am")
                      .withNameMap(new NameMap().`with`("#am", "amount"))
                      .withValueMap(new ValueMap().withNumber(":start_am", 1)
                      .withNumber(":end_am", 100))

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



