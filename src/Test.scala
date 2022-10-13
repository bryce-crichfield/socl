// import org.jocl.*
// import cl.*
// import org.jocl.CL.*
// import org.jocl.*
// import cl.implicits.given
// import scala.util.*



// object Test extends App {
//     val programSource =List({
//         "__kernel void "+
//         "sampleKernel("+
//         "             __global float *c)"+
//         "{"+
//         "    int gid = get_global_id(0);"+
//         "    c[gid] = 1.0f;"+
//         "}"})



//         val result = for {
//             context <- {}
//             (da, db, dc) <- Try {
//                 val a = new Array[Float](10)
//                 val b = new Array[Float](10)
//                 for (i <- 0 until 10) {
//                     a.update(i, i)
//                     b.update(i, i)
//                 }
//                 val c = new Array[Float](10)
//                 (a, b, c)
//             }
//             (pa, pb, pc) <- Try {
//                 val a = Pointer.to(da)
//                 val b = Pointer.to(db)
//                 val c = Pointer.to(dc)
//                 (a, b, c)
//             }
//             kernel <- Try {
//                 val program = clCreateProgramWithSource(context.id,
//                     1, programSource.toArray, null, null);
//                 clBuildProgram(program, 0, null, null, null, null);
//                 clCreateKernel(program, "sampleKernel", null);
//             }
//             (a, b, c) <- Try {
//                 val a = clCreateBuffer(context.id, 
//                     CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
//                     Sizeof.cl_float * 10, pa, null);
//                 val b =clCreateBuffer(context.id, 
//                     CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
//                     Sizeof.cl_float * 10, pb, null);
//                 val c = clCreateBuffer(context.id, 
//                     CL_MEM_READ_WRITE, 
//                     Sizeof.cl_float * 10, null, null);
//                 (a, b, c)
//             }
//             _ <- scala.util.Try {
//                 // clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(a));
//                 // clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(b));
//                 clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(c));
//             }
//             _ <- scala.util.Try {
//                 clEnqueueNDRangeKernel(context.queue.id, kernel, 1, null,
//                     new Array(10), null, 0, null, null);
        
//                 clEnqueueReadBuffer(context.queue.id, c, CL_TRUE, 0,
//                     10 * Sizeof.cl_float, pc, 0, null, null);
//             }
//             arr <- Try {

//             }
//         } yield pc
//         println(result.map(_.getNativePointer()))      
//     }
    
// }
