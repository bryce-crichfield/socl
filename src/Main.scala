import org.jocl.CL.*
import org.jocl.*
import cl.*
import cl.implicits.given
import cats.Monad
import cats.implicits.*
import cats.effect.IO
import cats.effect.*
// import cats.effect.implicits.*

object Main extends IOApp {
  val source = List("""__kernel void sample(
        __global float* a, 
        __global float* b,
        __global float* c) {
        int gid = get_global_id(0);
        c[gid] = a[gid] * b[gid];
    }""")

  val n = 10
  given Context = Context.default().get
  def MyProgram() = {
    Program.create[IO](source).use { program =>
      Kernel.create[IO](program, "sample").use { kernel =>
        IO.pure(kernel.toString())
      //   Buffer
      //     .create(10)(i => i * 1.0f)(pointerAccess =
      //       PointerAccess.CopyHostPointer
      //     )
      //     .use { a =>
      //       Buffer
      //         .create(10)(i => i * 1.0f)(pointerAccess =
      //           PointerAccess.CopyHostPointer
      //         )
      //         .use { b =>
      //           Buffer
      //             .create(10)(i => i * 0.0f)(pointerAccess =
      //               PointerAccess.NoneSpecifed
      //             )
      //             .use { c =>
      //               for {
      //                 _ <- IO {
      //                   kernel.setArg(0, a)
      //                   kernel.setArg(1, b)
      //                   kernel.setArg(2, c)
      //                 }
      //                 _ <- kernel.enqueue[IO](1, n.toLong)
      //                 _ <- c.read[IO]()
      //               } yield c.array
      //             }
      //         }
      //     }
      }
    }
  }
  override def run(args: List[String]): IO[ExitCode] =
    for {
      id <- MyProgram()
      _ <- IO.println(id)
    } yield ExitCode.Success

  // val result: IO[Array[Float]] = for {
  //   program <- Program[IO](source)   
  //   kernel <- Kernel[IO](program, "sample")
  //   a <- Buffer[Float](n, pointerAccess = PointerAccess.CopyHostPointer)(_ => 1.0f)
  //   b <- Buffer[Float](n, pointerAccess = PointerAccess.CopyHostPointer)(_ => 2.0f)
  //   c <- Buffer[Float](n, pointerAccess = PointerAccess.NoneSpecifed)(_ => 0.0f)
  //   _ <- IO {
  //     kernel.setArg(0, a)
  //     kernel.setArg(1, b)
  //     kernel.setArg(2, c)
  //   }
  //   _ <- kernel.enqueue(1, n.toLong)
  //   _ <- c.read()
  // } yield c.array

//   result.get.foreach(println)

}
