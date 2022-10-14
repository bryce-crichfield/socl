package cl

import cats.Monad
import cats.implicits.*
import cats.effect.{Resource, IO}
import org.jocl.*
import org.jocl.CL.*
trait BufferFlag {
  val enumeration: Long
}
// Designed to match the kernel type signature
// `__global const float* data` == KernelAccess.ReadOnly
// `__global float* data` == KernelAccess.WriteOnly
// `__global float* data` == KernelAccess.ReadWrite
enum KernelAccess(val enumeration: Long) extends BufferFlag {
  case ReadOnly extends KernelAccess(CL_MEM_READ_ONLY) 
  case WriteOnly extends KernelAccess(CL_MEM_WRITE_ONLY) 
  case ReadWrite extends KernelAccess(CL_MEM_READ_WRITE) 
}
// Currently Not Supported
// Considering Enumerating the Three Types as SubTypes of Buffer[T]
// Since this is essentially a limitation on the client's end
enum HostAccess(val enumeration: Long)  extends BufferFlag {
  case ReadOnly extends HostAccess(CL_MEM_HOST_READ_ONLY) 
  case WriteOnly extends HostAccess(CL_MEM_HOST_WRITE_ONLY) 
  case NoAccess extends HostAccess(CL_MEM_HOST_NO_ACCESS) 
  case NoneSpecifed extends HostAccess(0)
}

enum PointerAccess(val enumeration: Long)  extends BufferFlag {
  case UseHostPointer extends PointerAccess(CL_MEM_USE_HOST_PTR)
  case AllocateHostPointer extends PointerAccess(CL_MEM_ALLOC_HOST_PTR)
  case CopyHostPointer extends PointerAccess(CL_MEM_COPY_HOST_PTR)
  case NoneSpecifed extends PointerAccess(0)
}


class Buffer[T: CLType] (
        val id: cl_mem,
        val array: Array[T],
        val pointer: Pointer ,
        val size: Int,
        val bytesize: Int
) {
    //  clEnqueueReadBuffer(context.queue.id, buffer.id, CL_TRUE, 0,
    //     n*Sizeof.cl_float, buffer.pointer, 0, null, null)
      // clEnqueueReadBuffer(commandQueue, dstMem, CL_TRUE, 0,
      //       n * Sizeof.cl_float, dst, 0, null, null);
  def read[F[_]: Monad](offset: Long = 0)(using context: Context): F[Array[T]] = {
    Monad[F].pure(clEnqueueReadBuffer(
      context.queue.id,
      id, CL_TRUE, offset, bytesize, 
      pointer, 0, null, null
    )).map(_ => this.array)
  }
}




object Buffer {
  def apply[F[_]: Monad, A](array: Array[A],
    kernelAccess: KernelAccess = KernelAccess.ReadWrite,
    pointerAccess: PointerAccess) 
    (using context: Context) 
    (using cltype: CLType[A]): F[Buffer[A]] = 
  {
    val size = array.length
    val bytesize = size * cltype.size()
    val pointer = cltype.pointer(array)
    val creationPointer = {
      pointerAccess match
        case PointerAccess.UseHostPointer => cltype.pointer(array)
        case PointerAccess.AllocateHostPointer => null
        case PointerAccess.CopyHostPointer => cltype.pointer(array)
        case PointerAccess.NoneSpecifed => null
    }
    val flag = kernelAccess.enumeration | pointerAccess.enumeration
    for {
      id <- Monad[F].pure(clCreateBuffer(context.id, flag, bytesize, creationPointer, null))
    } yield new Buffer[A](id, array, pointer, size, bytesize)
  }

  def create[A](size: Int)(init: Int => A)
    (kernelAccess: KernelAccess = KernelAccess.ReadWrite,
    pointerAccess: PointerAccess) 
    (using context: Context) 
    (using cltype: CLType[A]): Resource[IO, Buffer[A]] = 
  {
      Resource.make[IO, Buffer[A]] {
        val data = cltype.array(size)
        for (i <- 0 until size) { data.update(i, init(i)) }
        for {
          _ <- IO.println("Constructing Buffer")
          b <- Buffer.apply[IO, A](data, kernelAccess, pointerAccess)
        } yield b
      } { buffer =>
        for {
          _ <- IO.println("Destroying Buffer")
          _ <- IO.pure(clReleaseMemObject(buffer.id))
        } yield ()
      } 
  }

}
