package clearcudaj;

import static jcuda.driver.JCudaDriver.cuGLMapBufferObject;
import static jcuda.driver.JCudaDriver.cuGLRegisterBufferObject;
import static jcuda.driver.JCudaDriver.cuGLUnmapBufferObject;
import static jcuda.driver.JCudaDriver.cuGLUnregisterBufferObject;
import jcuda.CudaException;

public class CudaGlBufferObject extends CudaDevicePointer
{

	private final int mPixelBufferObjectId;

	public CudaGlBufferObject(int pPixelBufferObjectId)
	{
		super();
		mPixelBufferObjectId = pPixelBufferObjectId;
		cuGLRegisterBufferObject(mPixelBufferObjectId);
	}


	public void map()
	{
		cuGLMapBufferObject(mCUdeviceptr,
												new long[1],
												mPixelBufferObjectId);
	}

	public void unmap()
	{
		cuGLUnmapBufferObject(mPixelBufferObjectId);
	}


	@Override
	public void close() throws CudaException
	{
		cuGLUnregisterBufferObject(mPixelBufferObjectId);
		super.close();
	}

	@Override
	public String toString()
	{
		return "CudaGlBufferObject [mPixelBufferObjectId=" + mPixelBufferObjectId
						+ "]";
	}

}
