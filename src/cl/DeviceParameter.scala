package cl

import org.jocl.CL.*
import org.jocl.*

abstract class DeviceParameter[A](val enumeration: Int) {
  def get(device: Device, size: Int): List[A]
}

object DeviceParameter {
  case object NAME extends StringParameter(CL_DEVICE_NAME)
  case object VENDOR extends StringParameter(CL_DEVICE_VENDOR)
  case object DRIVER_VERSION extends StringParameter(CL_DRIVER_VERSION)
  case object OPENCL_C_VERSION
      extends StringParameter(CL_DEVICE_OPENCL_C_VERSION)
  case object GLOBAL_MEM_SIZE extends LongParameter(CL_DEVICE_GLOBAL_MEM_SIZE)
  case object GLOBAL_MEM_CACHE_SIZE
      extends LongParameter(CL_DEVICE_GLOBAL_MEM_CACHE_SIZE)
  case object LOCAL_MEM_SIZE extends LongParameter(CL_DEVICE_LOCAL_MEM_SIZE)
  case object MAX_MEM_ALLOC_SIZE
      extends LongParameter(CL_DEVICE_MAX_MEM_ALLOC_SIZE)
  case object MAX_CONSTANT_BUFFER_SIZE
      extends LongParameter(CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE)
  case object MAX_WORK_ITEM_DIMENSIONS
      extends LongParameter(CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS)
  case object MAX_WORK_ITEM_SIZES
      extends SizeParameter(CL_DEVICE_MAX_WORK_ITEM_SIZES)
  case object MAX_WORK_GROUP_SIZE
      extends SizeParameter(CL_DEVICE_MAX_WORK_GROUP_SIZE)
  case object MAX_COMPUTE_UNITS
      extends LongParameter(CL_DEVICE_MAX_COMPUTE_UNITS)
  case object MAX_CLOCK_FREQUENCY
      extends LongParameter(CL_DEVICE_MAX_CLOCK_FREQUENCY)
  case object DEVICE_TYPE extends LongParameter(CL_DEVICE_TYPE)
  case object NATIVE_VECTOR_WIDTH_HALF
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF)
  case object NATIVE_VECTOR_WIDTH_FLOAT
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT)
  case object NATIVE_VECTOR_WIDTH_DOUBLE
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE)
  case object NATIVE_VECTOR_WIDTH_CHAR
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR)
  case object NATIVE_VECTOR_WIDTH_SHORT
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT)
  case object NATIVE_VECTOR_WIDTH_INT
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_INT)
  case object NATIVE_VECTOR_WIDTH_LONG
      extends LongParameter(CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG)

  abstract class StringParameter(override val enumeration: Int)
      extends DeviceParameter[String](enumeration) {
    def get(device: Device, size: Int): List[String] = {
      // Obtain String Length
      val size = new Array[Long](1)
      clGetDeviceInfo(device.id, enumeration, 0, null, size)
      // Create a buffer of the appropriate size and fill it with the info
      val buffer = new Array[Byte](size.head.toInt)
      clGetDeviceInfo(
        device.id,
        enumeration,
        buffer.length,
        Pointer.to(buffer),
        null
      )
      List(buffer.map(_.toChar).mkString)
    }
  }

  abstract class LongParameter(override val enumeration: Int)
      extends DeviceParameter[Long](enumeration) {
    def get(device: Device, size: Int): List[Long] = {
      val values = new Array[Long](size)
      clGetDeviceInfo(
        device.id,
        enumeration,
        Sizeof.cl_long * size,
        Pointer.to(values),
        null
      )
      values.toList
    }
  }

  abstract class SizeParameter(override val enumeration: Int)
      extends DeviceParameter[Long](enumeration) {
    def get(device: Device, size: Int): List[Long] = {
      val buffer = java.nio.ByteBuffer
        .allocate(size * Sizeof.size_t)
        .order(java.nio.ByteOrder.nativeOrder())
      clGetDeviceInfo(
        device.id,
        enumeration,
        Sizeof.size_t * size,
        Pointer.to(buffer),
        null
      )
      val sizes = new Array[Long](size)
      if (Sizeof.size_t == 4) then {
        for (i <- 0 until size) {
          sizes.update(i, buffer.getInt(i * Sizeof.size_t))
        }
      } else {
        for (i <- 0 until size) {
          sizes.update(i, buffer.getLong(i * Sizeof.size_t))
        }

      }
      sizes.toList
    }
  }

  abstract class IntParameter(override val enumeration: Int)
      extends DeviceParameter[Int](enumeration) {
    def get(device: Device, size: Int): List[Int] = {
      val values = new Array[Int](size)
      clGetDeviceInfo(
        device.id,
        enumeration,
        Sizeof.cl_int * size,
        Pointer.to(values),
        null
      )
      values.toList
    }
  }

}
