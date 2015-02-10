package clearcuda;

import static jcuda.driver.JCudaDriver.cuGLMapBufferObject;
import static jcuda.driver.JCudaDriver.cuGLRegisterBufferObject;
import static jcuda.driver.JCudaDriver.cuGLUnmapBufferObject;
import static jcuda.driver.JCudaDriver.cuGLUnregisterBufferObject;
import jcuda.CudaException;

public class CudaOpenGLBufferObject extends CudaDevicePointer
{

	private Integer mPixelBufferObjectId;

	public CudaOpenGLBufferObject(int pPixelBufferObjectId)
	{
		super(true);
		mPixelBufferObjectId = pPixelBufferObjectId;
		cuGLRegisterBufferObject(mPixelBufferObjectId);
	}

	public void map()
	{
		final long[] lSizeInBytes = new long[1];
		cuGLMapBufferObject(getPeer(), lSizeInBytes, mPixelBufferObjectId);
		mSizeInBytes = lSizeInBytes[0];
	}

	public void unmap()
	{
		cuGLUnmapBufferObject(mPixelBufferObjectId);
	}

	@Override
	public void close() throws CudaException
	{
		if (mPixelBufferObjectId != null)
		{
			cuGLUnregisterBufferObject(mPixelBufferObjectId);
			mPixelBufferObjectId = null;
		}
		super.close();
	}

	@Override
	public String toString()
	{
		return "CudaGlBufferObject [mPixelBufferObjectId=" + mPixelBufferObjectId
						+ "]";
	}

}
