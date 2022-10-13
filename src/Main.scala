import org.jocl.CL.*
import org.jocl.*
import cl.*
import cl.implicits.given
object Main extends App {
  val source = """__kernel void sample(
        __global float* a, 
        __global float* b,
        __global float* c) {
        int gid = get_global_id(0);
        c[gid] = a[gid] * b[gid];
    }"""

  val n = 10
  given Context = Context.default().get
  val result = for {
    program <- Program(List(source))
    kernel <- program.kernel("sample")
    a <- Buffer[Float](n, pointerAccess = PointerAccess.CopyHostPointer)(_ => 1.0f)
    b <- Buffer[Float](n, pointerAccess = PointerAccess.CopyHostPointer)(_ => 2.0f)
    c <- Buffer[Float](n, pointerAccess = PointerAccess.NoneSpecifed)(_ => 0.0f)
    _ <- scala.util.Try {
      kernel.setArg(0, a)
      kernel.setArg(1, b)
      kernel.setArg(2, c)
    }
    _ <- kernel.enqueue(1, n.toLong)
    _ <- c.read()
  } yield c.array

  result.get.foreach(println)

}

