package cl

import scala.util.Try
import org.jocl.*
import org.jocl.CL.*

case class Program(id: cl_program) {
  def kernel(name: String)(using context: Context): Try[Kernel] = {
    Kernel(this, name)
  }
}
object Program {
  def apply(source: List[String])(using context: Context): Try[Program] = {
    for { 
      id <- Try(clCreateProgramWithSource(context.id, 
        1, source.toArray, null, null))
      _ <- Try(clBuildProgram(id, 0, null, null, null, null))
    } yield Program(id)
  }
}

case class Kernel(id: cl_kernel) {
  def setArg[A](index: Int, buffer: Buffer[A])
    (using cltype: CLType[A]): Unit = {
    clSetKernelArg(id, index, Sizeof.cl_mem, Pointer.to(buffer.id))
  }


  def enqueue(work_dim: Int, global_work_size: Long)(using context: Context): Try[Unit] = {
    Try {
      clEnqueueNDRangeKernel(context.queue.id,
        id, work_dim, null, Array(global_work_size), null, 0, null, null)  
    }
  }
}
object Kernel {
  def apply(program: Program, name: String)(using context: Context): Try[Kernel] = {

    Try {
      val id = clCreateKernel(program.id, name, null)
      Kernel(id)
    }
  }

}


