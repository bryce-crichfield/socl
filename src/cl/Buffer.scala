package cl

import scala.util.*
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


case class Buffer[T: CLType] (
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
  def read(offset: Long = 0)(using context: Context): Try[Array[T]] = {
    Try(clEnqueueReadBuffer(
      context.queue.id,
      id, CL_TRUE, offset, bytesize, 
      pointer, 0, null, null
    )).map(_ => this.array)
  }
}

object Buffer {
  def apply[A](size: Int,
    kernelAccess: KernelAccess = KernelAccess.ReadWrite,
    hostAccess: HostAccess = HostAccess.NoneSpecifed,
    pointerAccess: PointerAccess) 
    (arrayInit: Int => A)
    (using context: Context) 
    (using cltype: CLType[A]): Try[Buffer[A]] = 
  {
    val bytesize = size * cltype.size()
    val array = cltype.array(size)
    val pointer = cltype.pointer(array)
    for (i <- 0 until array.length) {
      array.update(i, arrayInit(i))
    }
    val creationPointer = {
      pointerAccess match
        case PointerAccess.UseHostPointer => cltype.pointer(array)
        case PointerAccess.AllocateHostPointer => null
        case PointerAccess.CopyHostPointer => cltype.pointer(array)
        case PointerAccess.NoneSpecifed => null
      
    }
    val flag = kernelAccess.enumeration | hostAccess.enumeration | pointerAccess.enumeration
    //flags.foldLeft(0L)((acc, flag) => acc | flag.enumeration)
    // val id = clCreateBuffer(context.id, flag, bytesize, null, null)
    // new Buffer[A](id, array, pointer, size, bytesize)
    for {
      id <- Try(clCreateBuffer(context.id, flag, bytesize, creationPointer, null))
    } yield new Buffer[A](id, array, pointer, size, bytesize)
  }
}
