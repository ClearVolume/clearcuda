package clearcudaj;

import static jcuda.driver.JCudaDriver.cuGLMapBufferObject;
import static jcuda.driver.JCudaDriver.cuGLRegisterBufferObject;
import static jcuda.driver.JCudaDriver.cuGLUnmapBufferObject;
import static jcuda.driver.JCudaDriver.cuGLUnregisterBufferObject;
import jcuda.CudaException;

public class CudaOpenGLBufferObject extends CudaDevicePointer
{

	private final int mPixelBufferObjectId;

	public CudaOpenGLBufferObject(int pPixelBufferObjectId)
	{
		super(true);
		mPixelBufferObjectId = pPixelBufferObjectId;
		cuGLRegisterBufferObject(mPixelBufferObjectId);
	}

	public void map()
	{
		long[] lSizeInBytes = new long[1];
		cuGLMapBufferObject(getPeer(),
												lSizeInBytes,
												mPixelBufferObjectId);
		mSizeInBytes = lSizeInBytes[0];
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
