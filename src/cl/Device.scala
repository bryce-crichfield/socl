package cl

import org.jocl.CL.*
import org.jocl.*

case class Device(id: cl_device_id) {

  def get[A](parameter: DeviceParameter[A], size: Int = 1): A =
    parameter.get(this, size).head

  def getList[A](parameter: DeviceParameter[A], size: Int): List[A] =
    parameter.get(this, size)
    
  def getInfo(): DeviceInfo = DeviceInfo(this)

  override def toString(): String =
    getInfo().toString()
}
