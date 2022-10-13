import org.jocl.*
import cl.*
object Test extends App {
    CL.setExceptionsEnabled(true);
    val platform = Platform.get()
    // val devices = platform.getDevices()
    println(platform)
    // println(devices)
}
