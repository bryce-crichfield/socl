package cl

import scala.util.*
import org.jocl.CL.*
import org.jocl.*

class ContextProperties private (
    val properties: cl_context_properties
) {    
    def addPlatform(platform: Platform): Unit = 
        properties.addProperty(CL_CONTEXT_PLATFORM, platform.id)

}
object ContextProperties {
    def apply(): ContextProperties =
        new ContextProperties(new cl_context_properties())   
}

case class Context(
    val id: cl_context,
    val device: Device,
    val queue: CommandQueue
)
object Context {
    def apply(device_provider: => Option[Device], properties: ContextProperties): Try[Context] = {
        for {
            device <- Try(device_provider.get)
            cid <- Try(clCreateContext(properties.properties,
                1, Array(device.id), null, null, null))
            queue <- CommandQueue(cid, device, QueueProperties())
        } yield Context(cid, device, queue)
    }

    def default(device_provider: DeviceProvider = DefaultDeviceProvider): Try[Context] = {
        CL.setExceptionsEnabled(true);
        val platform = Platform.get().head
        val devices = platform.getDevices(DeviceType.ALL)
        val properties = ContextProperties()
        properties.addPlatform(platform)
        Context(devices.headOption, properties)
    }



    def test(): Context = {
        val platformIndex = 0
        val deviceType = CL_DEVICE_TYPE_ALL
        val deviceIndex = 0
        CL.setExceptionsEnabled(true)
        val numPlatformsArray = new Array[Int](1)
        clGetPlatformIDs(0, null, numPlatformsArray)
        val numPlatforms = numPlatformsArray.head
        val platforms = new Array[cl_platform_id](numPlatforms)
        clGetPlatformIDs(platforms.length, platforms, null)
        val platform = platforms(platformIndex)
        val contextProperties = new cl_context_properties()
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform)
        val numDevicesArray = new Array[Int](1)
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray)
        val numDevices = numDevicesArray.head
        val devices = new Array[cl_device_id](numDevices)
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null)
        val device = devices(deviceIndex)
        val context = clCreateContext(contextProperties, 1, Array(device), null, null, null)
        val properties = new cl_queue_properties()
        val commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null)
        new Context(context, Device(device), CommandQueue(commandQueue))
    }

}

type DeviceProvider = List[Device] => Option[Device]
val DefaultDeviceProvider: DeviceProvider = {
    (devices: List[Device]) => 
        devices.map(_.getInfo()).sortWith {
            case (a, b) => a.clock_frequency < a.clock_frequency
        }.lastOption.map(_.device)
}