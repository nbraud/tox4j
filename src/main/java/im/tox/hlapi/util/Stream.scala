package im.tox.hlapi.util

import scala.collection.GenTraversable
import scala.concurrent._

class Stream[+A] {
  val next: Future[(A, Stream[A])] = ???
}

class Source[-A] {
  def put(a: A): Source[A] = ???
  def putAll(s: GenTraversable[A]): Source[A] =
    s.foldLeft(this)((src: Source[A], x: A) => src.put(x))
}
