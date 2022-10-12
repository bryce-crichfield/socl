package cl

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
trait CommandQueue

class Context (
    val id: cl_context,
    val devices: List[Device],
    val queue: CommandQueue
) {
    // def 
}
// object Context {
//     def apply(devices: List[Device], properties: ContextProperties): Context = {
//         val dids = devices.map(_.id).toArray
//         val id = clCreateContext(
//             properties.properties, dids.length, dids, null, null, null)
//         Context(id, devices)
//     }
// }
