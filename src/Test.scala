import org.jocl.*
import cl.*
object Test extends App {
    CL.setExceptionsEnabled(true);
    val platform = Platform.get().head
    val devices = platform.getDevices()
    println(platform)
    println(devices)
}
