package cl

import cats.Monad
import cats.implicits.*
import cats.effect.Resource
import org.jocl.*
import org.jocl.CL.*

case class Program(id: cl_program)
object Program {
  private def apply[F[_]: Monad](source: List[String])
  (using context: Context): F[Program] = {
    val monad = summon[Monad[F]]
    for { 
      id <- Monad[F].pure(clCreateProgramWithSource(context.id, 
        1, source.toArray, null, null))
      _ <- Monad[F].pure(clBuildProgram(id, 0, null, null, null, null))
    } yield Program(id)
  }

  def create[F[_]: Monad](source: List[String])
    (using context: Context): Resource[F, Program] = {
      Resource.make[F, Program](Program.apply[F](source))(program =>
        println("Releasing Program")
        Monad[F].pure(clReleaseProgram(program.id)))
  }
}

case class Kernel(id: cl_kernel) {
  def setArg[A](index: Int, buffer: Buffer[A])
    (using cltype: CLType[A]): Unit = {
    clSetKernelArg(id, index, Sizeof.cl_mem, Pointer.to(buffer.id))
  }


  def enqueue[F[_]: Monad](work_dim: Int, global_work_size: Long)(using context: Context): F[Unit] = {
    Monad[F].pure {
      clEnqueueNDRangeKernel(context.queue.id,
        id, work_dim, null, Array(global_work_size), null, 0, null, null)  
    }
  }
}
object Kernel {
  private def apply[F[_]: Monad](program: Program, name: String)
  (using context: Context): F[Kernel] = {
    println("Making Kernel")
    val monad = summon[Monad[F]]
    Monad[F].pure {
      val id = clCreateKernel(program.id, name, null)
      Kernel(id)
    }
  }

  def create[F[_]: Monad](program: Program, name: String)
    (using context: Context): Resource[F, Kernel] = {
      Resource.make[F, Kernel](Kernel.apply[F](program, name))(kernel =>
        println("Releasing Kernel")
        Monad[F].pure(clReleaseKernel(kernel.id)))
  }

}


