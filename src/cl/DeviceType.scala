package cl

import org.jocl.CL.*
import org.jocl.*

enum DeviceType(val enumeration: Long) {
    case DEFAULT extends DeviceType(CL_DEVICE_TYPE_DEFAULT)
    case CPU extends DeviceType(CL_DEVICE_TYPE_CPU)
    case GPU extends DeviceType(CL_DEVICE_TYPE_GPU)
    case ACCERLATOR extends DeviceType(CL_DEVICE_TYPE_ACCELERATOR)
    case ALL extends DeviceType(CL_DEVICE_TYPE_ALL)
}
