package models

import org.scalatest.{ fixture, BeforeAndAfterAll }
import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.scalatest._

trait ModelSpec extends fixture.FunSpec with AutoRollback with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = DBs.setupAll()

  override protected def afterAll(): Unit = DBs.closeAll()

}