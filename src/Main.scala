import org.jocl.CL.*
import org.jocl.*
import cl.*
import cl.implicits.given
import cats.Monad
import cats.implicits.*
import cats.effect.IO
import cats.effect.*

object Main extends IOApp {
  
  given Context = Context.default().get

  val source = List("""__kernel void sample(
        __global float* a, 
        __global float* b,
        __global float* c) {
        int gid = get_global_id(0);
        c[gid] = a[gid] * b[gid];
    }""")

  val n = 10

  def Data = for {
    program <- Program.create(source)
    kernel <- Kernel.create(program, "sample")
    a <- Buffer.create(n)(idx => idx * 1.0f)
      (pointerAccess = PointerAccess.CopyHostPointer)
    b <- Buffer.create(n)(idx => idx * 1.0f)
      (pointerAccess = PointerAccess.CopyHostPointer)
    c <- Buffer.create(n)(idx => idx * 1.0f)
      (pointerAccess = PointerAccess.NoneSpecifed)
  } yield (kernel, a, b, c)

  def Exec = Data.use { 
    case (kernel, a, b, c) => 
      for {
        _ <- IO {
          kernel.setArg(0, a)
          kernel.setArg(1, b)
          kernel.setArg(2, c)
        }
        _ <- kernel.enqueue[IO](1, n.toLong)
        _ <- c.read[IO]()
    } yield c.array
  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      array <- Exec
      _ <- IO { array.foreach(println) }
    } yield ExitCode.Success
  }
}
