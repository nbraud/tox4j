package im.tox.hlapi.core.security

import java.security.{ Permission, Permissions }
import scala.collection.JavaConverters._
import scala.collection.GenTraversableOnce

object Policy {
  private def copy(p: Permissions): Permissions = {
    val _p = new Permissions()
    p.elements().asScala.foreach(_p.add)
    _p
  }

  def apply(p: Permissions) = {
    val _p = copy(p)
    _p.setReadOnly()
    new Policy(_p)
  }

  val default = Policy(new Permissions())
}

// XXXTODO: Private constructor
final class Policy(p: Permissions) {
  def add(perm: Permission): Policy = {
    val _p = Policy.copy(p)
    _p.add(perm)
    Policy(_p)
  }

  def addAll(perm: Permissions): Policy = {
    addAll(perm.elements().asScala)
  }

  def addAll(perm: GenTraversableOnce[Permission]): Policy = {
    val _p = Policy.copy(p)
    perm.foreach(_p.add)
    Policy(_p)
  }
}
