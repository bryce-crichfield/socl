package cl
import org.jocl.CL.*

case class DeviceInfo (
    device: Device,
    name: String,
    vendor: String,
    driver_version: String,
    opencl_c_version: String,
    memory: Long,
    memory_used: Long = 0,
    global_cache: Long = 0,
    local_cache: Long = 0,
    max_global_buffer: Long = 0,
    max_constant_buffer: Long = 0,
    max_work_item_dim: Long = 0,
    max_work_group_size: Long = 0,
    max_work_item_sizes: List[Long] = List(),
    compute_units: Long = 0,
    clock_frequency: Long = 0,
    is_cpu: Boolean = false,
    is_gpu: Boolean = true,
    is_fp64_capable: Boolean = false,
    is_fp32_capable: Boolean = false,
    is_fp16_capable: Boolean = false,
    is_int64_capable: Boolean = false,
    is_int32_capable: Boolean = false,
    is_int16_capable: Boolean = false,
    is_int8_capable: Boolean = false,
    cores: Int = 0,
    tflops: Double = 0.0
) {
  override def toString(): String = {
    f"""
    DeviceInfo 
    {
      name = $name
      vendor = $vendor
      driver_version = $driver_version
      opencl_c_version = $opencl_c_version
      memory = $memory
      memory_used = $memory_used
      global_cache = $global_cache
      local_cache = $local_cache
      max_global_buffer = $max_global_buffer
      max_constant_buffer = $max_constant_buffer
      max_work_item_dim = $max_work_item_dim
      max_work_item_sizes = $max_work_item_sizes
      max_work_group_size = $max_work_group_size
      compute_unit = $compute_units
      clock_frequency = $clock_frequency
      is_cpu = $is_cpu
      is_gpu = $is_gpu
      is_fp16_capable = $is_fp16_capable
      is_fp32_capable = $is_fp32_capable
      is_fp64_capable = $is_fp64_capable
      is_int8_capable = $is_int8_capable
      is_int16_capable = $is_int16_capable
      is_int32_capable = $is_int32_capable
      is_int64_capable = $is_int64_capable
      cores = $cores
      tflops = $tflops
    }
    """
  }
}
object DeviceInfo {

  def apply(device: Device): DeviceInfo = DeviceInfo.apply (
    device = device,
    name = device.get(DeviceParameter.NAME),
    vendor = device.get(DeviceParameter.VENDOR),
    driver_version = device.get(DeviceParameter.DRIVER_VERSION),
    opencl_c_version = device.get(DeviceParameter.OPENCL_C_VERSION),
    memory = device.get(DeviceParameter.GLOBAL_MEM_SIZE),
    global_cache = device.get(DeviceParameter.GLOBAL_MEM_CACHE_SIZE),
    local_cache = device.get(DeviceParameter.LOCAL_MEM_SIZE),
    max_global_buffer = device.get(DeviceParameter.MAX_MEM_ALLOC_SIZE),
    max_constant_buffer = device.get(DeviceParameter.MAX_CONSTANT_BUFFER_SIZE),
    max_work_item_dim = device.get(DeviceParameter.MAX_WORK_ITEM_DIMENSIONS),
    max_work_group_size = device.get(DeviceParameter.MAX_WORK_GROUP_SIZE),
    max_work_item_sizes = device.getList(DeviceParameter.MAX_WORK_ITEM_SIZES, 3),
    compute_units = device.get(DeviceParameter.MAX_COMPUTE_UNITS),
    clock_frequency = device.get(DeviceParameter.MAX_CLOCK_FREQUENCY),
    is_cpu = device.get(DeviceParameter.DEVICE_TYPE) == DeviceType.CPU.enumeration,
    is_gpu = device.get(DeviceParameter.DEVICE_TYPE) == DeviceType.GPU.enumeration,
    is_fp64_capable =  device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_DOUBLE) >= 1,
    is_fp32_capable =  device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_FLOAT) >= 1,
    is_fp16_capable =  device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_HALF) >= 1,
    is_int64_capable = device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_LONG) >= 1,
    is_int32_capable = device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_INT) >= 1,
    is_int16_capable = device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_SHORT) >= 1,
    is_int8_capable =  device.get(DeviceParameter.NATIVE_VECTOR_WIDTH_CHAR) >= 1,
    // cores = 0, // TODO implement per vendor
    // tflops = 0 // TOOD implement per is_gpu
  )

}
