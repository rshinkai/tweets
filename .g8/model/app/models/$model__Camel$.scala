package models

import scalikejdbc._
import skinny.orm._

/**
  * $model;format="Camel"$
  */
case class $model;format="Camel"$(id: Option[Long])

object $model;format="Camel"$ extends SkinnyCRUDMapper[$model;format="Camel"$] {

  override def defaultAlias: Alias[$model;format="Camel"$] = ???

  override def extract(rs: WrappedResultSet, n: ResultName[$model;format="Camel"$]): $model;format="Camel"$ =
    autoConstruct(rs, n)

  private def toNamedValues(record: $model;format="Camel"$): Seq[(Symbol, Any)] = ???

  def create($model;format="camel"$: $model;format="Camel"$)(implicit session: DBSession): Long =
    createWithAttributes(toNamedValues($model;format="camel"$): _*)

  def update($model;format="camel"$: $model;format="Camel"$)(implicit session: DBSession): Int =
    updateById($model;format="camel"$.id.get).withAttributes(toNamedValues($model;format="camel"$): _*)

}
