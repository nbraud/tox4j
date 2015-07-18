package im.tox.hlapi.storage

import scala.collection.immutable.{ HashMap, Map }
import scalaz._

import org.scalacheck._
import org.scalacheck.commands._
import org.scalacheck.Arbitrary._

import org.scalatest._
import org.scalatest.prop.PropertyChecks

abstract class KeyValueTest extends Commands {

  type Key
  type Value <: ValueType[Key]
  type Sut <: KeyValue[Key, Value]

  def genKey: Gen[Key]
  def genValue: Gen[Value]
  def cleanSut(): \/[IOError, Sut]

  override def newSut(state: State): Sut = {
    val keyValue = cleanSut().toOption.get
    state.kv.values.foreach(keyValue.add _)
    keyValue
  }

  final case class State(val kv: HashMap[Key, Value]) {
    def get(k: Key): Option[Value] = kv.get(k)
    def set(value: Value): State =
      copy(kv = kv + ((value.key, value)))
    def delete(key: Key): State = copy(kv = kv - key)
    def delete(obj: Value): State = delete(obj.key)
  }

  override def genInitialState: Gen[State] = {
    for {
      values <- Gen.nonEmptyContainerOf[List, Value](genValue)
      list = values.map { v => (v.key, v) }
    } yield State(HashMap(list: _*))
  }

  override final def initialPreCondition(s: State): Boolean = true
  override def canCreateNewSut(newState: State, initSuts: Traversable[State],
    runningSuts: Traversable[Sut]): Boolean = true

  override def destroySut(sut: Sut): Unit = ()

  final case class Get(key: Key) extends SuccessCommand {
    type Result = Option[Value]
    override def nextState(s: State): State = s

    override def preCondition(s: State): Boolean = true
    override def postCondition(s: State, r: Option[Value]): Prop = {
      Prop(r == s.get(key))
    }

    override def run(sut: Sut): Result = {
      sut.lookup(key)
    }
  }

  final case class Set(value: Value) extends SuccessCommand {
    type Result = Option[Value]
    override def nextState(s: State): State = {
      s.set(value)
    }

    override def preCondition(s: State): Boolean = true
    override def postCondition(s: State, r: Result): Prop = {
      Prop(r.map(_ == value).getOrElse(false))
    }

    override def run(sut: Sut): Result = {
      sut.add(value)
      sut.lookup(value.key)
    }
  }

  protected final def genGet(s: State): Gen[Get] = {
    genKey.map(Get(_))
  }

  protected final def genSet(s: State): Gen[Set] = {
    genValue.map(Set(_))
  }

  override def genCommand(s: State): Gen[Command] = {
    Gen.oneOf[Command](genGet(s), genSet(s))
  }

}
