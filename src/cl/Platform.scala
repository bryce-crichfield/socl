package cl

import org.jocl.CL.*
import org.jocl.*

abstract class PlatformParameter[A](val enumeration: Int) {
  def get(platform: Platform, size: Int): List[A]
}
object PlatformParameter {
  abstract class StringParameter(override val enumeration: Int)
      extends PlatformParameter[String](enumeration) {
    def get(platform: Platform, size: Int): List[String] = {
      // Obtain String Length
      val size = new Array[Long](1)
      clGetPlatformInfo(platform.id, enumeration, 0, null, size)
      // Create a buffer of the appropriate size and fill it with the info
      val buffer = new Array[Byte](size.head.toInt)
      clGetPlatformInfo(
        platform.id,
        enumeration,
        buffer.length,
        Pointer.to(buffer),
        null
      )
      List(buffer.map(_.toChar).mkString)
    }

  }

  case object NAME extends StringParameter(CL_PLATFORM_NAME)
  case object VENDOR extends StringParameter(CL_PLATFORM_VENDOR)
  case object VERSION extends StringParameter(CL_PLATFORM_VERSION)
}

case class Platform(id: cl_platform_id) extends AnyVal {

  def getDevices(deviceType: DeviceType = DeviceType.ALL): List[cl.Device] = {
    val numDevicesArray = new Array[Int](1)
    clGetDeviceIDs(id, deviceType.ordinal, 0, null, numDevicesArray)
    val count = numDevicesArray.head
    val device_ids = new Array[cl_device_id](count)
    clGetDeviceIDs(id, deviceType.ordinal, count, device_ids, null)
    device_ids.toList.map(id => Device(id))
  }

  def get[A](parameter: PlatformParameter[A]): A =
    parameter.get(this, 1).head

  override def toString(): String = {
    f"""
    Platform    
    {
        Name = ${get(PlatformParameter.NAME)}
        Vendor = ${get(PlatformParameter.VENDOR)}
        Version = ${get(PlatformParameter.VERSION)}
        NumDevices = ${getDevices(DeviceType.ALL).length}
    }
    """
  }

}
object Platform {
  def get(): List[Platform] = {
    val numPlatformsArray = new Array[Int](1)
    clGetPlatformIDs(0, null, numPlatformsArray)
    val count = numPlatformsArray.head
    val platforms = new Array[cl_platform_id](count)
    clGetPlatformIDs(platforms.length, platforms, null)
    platforms.toList.map(id => Platform(id))
  }
}
