package cl

import org.jocl.CL.*
import org.jocl.*


class QueueProperties private (
    val properties: cl_queue_properties
) {    
}
object QueueProperties {
    def apply(): QueueProperties =
        new QueueProperties(new cl_queue_properties())   
}

case class CommandQueue(id: cl_command_queue)
object CommandQueue {
    def apply(context_id: cl_context, device: Device, properties: QueueProperties): scala.util.Try[CommandQueue] = 
    {
        for { 
            id <- scala.util.Try(clCreateCommandQueueWithProperties(
                context_id, device.id, properties.properties, null))
        } yield new CommandQueue(id)
    }
}
