package cl

import org.jocl.*
import org.jocl.CL.*

case class Program(id: cl_program) extends AnyVal {}
class ProgramFactory(context: Context) {
  def build(source: List[String]): Program = {
    val id =
      clCreateProgramWithSource(context.id, 1, source.toArray, null, null)
    clBuildProgram(id, 0, null, null, null, null)
    Program(id)
  }
}

trait CLType[T] {
  def size(): Int
  def array(size: Int): Array[T]
  def pointer(t: Array[T]): Pointer
}

object implicits {
  given CLType[Double] with
    def size(): Int = Sizeof.cl_double
    def array(size: Int): Array[Double] = new Array(size)
    def pointer(t: Array[Double]) = Pointer.to(t)

  given CLType[Float] with
    def size(): Int = Sizeof.cl_float
    def array(size: Int): Array[Float] = new Array(size)
    def pointer(t: Array[Float]) = Pointer.to(t)

  given CLType[Long] with
    def size(): Int = Sizeof.cl_long
    def array(size: Int): Array[Long] = new Array(size)
    def pointer(t: Array[Long]) = Pointer.to(t)

  given CLType[Int] with
    def size(): Int = Sizeof.cl_int
    def array(size: Int): Array[Int] = new Array[Int](size)
    def pointer(t: Array[Int]) = Pointer.to(t)

  given CLType[Short] with
    def size(): Int = Sizeof.cl_short
    def array(size: Int): Array[Short] = new Array(size)
    def pointer(t: Array[Short]) = Pointer.to(t)

  given CLType[Char] with
    def size(): Int = Sizeof.cl_char
    def array(size: Int): Array[Char] = new Array(size)
    def pointer(t: Array[Char]) = Pointer.to(t)
}

type BufferFlag = Int
trait BufferFlags {
  def enumeration: Int = ???
}

case class Buffer[T: CLType](
    id: cl_mem,
    size: Int,
    array: Array[T],
    pointer: Pointer
)
class BufferFactory(context: Context) {
  def build[A](flags: BufferFlags, size: Int)
    (using cltype: CLType[A]): Buffer[A] = {
    val array = cltype.array(size)
    val pointer = cltype.pointer(array)
    val id = clCreateBuffer(
      context.id,
      flags.enumeration,
      cltype.size() * size,
      pointer,
      null
    )
    Buffer[A](id, size, array, pointer)
  }
}
