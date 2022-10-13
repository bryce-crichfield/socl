import org.jocl.CL.*
import org.jocl.*
import cl.*
import cl.implicits.given
object Main extends App {
  val source = """__kernel void sample(__global float* out) {
        int gid = get_global_id(0);
        out[gid] = (float)gid;
    }"""

  val n = 10
  given Context = Context.default().get
  val result = for {
    program <- Program(List(source))
    kernel <- program.kernel("sample")
    buffer <- Buffer[Float](n, KernelAccess.ReadWrite)(_ => 0.0f)
    _ <- scala.util.Try {
      kernel.setArg(0, buffer)
    }
    _ <- kernel.enqueue(1, n.toLong)
    _ <- buffer.read(0)
  } yield buffer.array

  result.get.foreach(println)

}

