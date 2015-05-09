package clearcuda;

import static jcuda.driver.JCudaDriver.CU_PARAM_TR_DEFAULT;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuParamSetTexRef;
import jcuda.Pointer;
import jcuda.driver.CUfunction;
import coremem.interfaces.HasPeer;

public class CudaFunction implements HasPeer<CUfunction>
{

	private final String mFunctionName;
	private final CUfunction mCUfunction;

	private int mGridDimX, mGridDimY, mGridDimZ, mBlockDimX,
			mBlockDimY, mBlockDimZ, mSharedMemoryBytes;
	private boolean mSynchronize = true;

	protected CudaFunction(CUfunction pCUfunction, String pFunctionName)
	{
		mCUfunction = pCUfunction;
		mFunctionName = pFunctionName;
	}

	public CudaFunction(CudaModule pCudaModule, String pFunctionName)
	{
		mFunctionName = pFunctionName;
		mCUfunction = new CUfunction();
		final int lCuModuleGetFunction = cuModuleGetFunction(	mCUfunction,
																										pCudaModule.getPeer(),
																										pFunctionName);
		System.out.println(lCuModuleGetFunction);
	}

	public void setGridDim(int... pGridDim)
	{
		mGridDimX = 1;
		mGridDimY = 1;
		mGridDimZ = 1;
		mGridDimX = pGridDim[0];
		if (pGridDim.length == 1)
			return;
		mGridDimY = pGridDim[1];
		if (pGridDim.length == 2)
			return;
		mGridDimZ = pGridDim[2];
	}

	public void setBlockDim(int... pBlockDim)
	{
		mBlockDimX = 1;
		mBlockDimY = 1;
		mBlockDimZ = 1;
		mBlockDimX = pBlockDim[0];
		if (pBlockDim.length == 1)
			return;
		mBlockDimY = pBlockDim[1];
		if (pBlockDim.length == 2)
			return;
		mBlockDimZ = pBlockDim[2];
	}

	public void setTexture(CudaTextureReference pCudaTextureReference)
	{
		cuParamSetTexRef(	getPeer(),
											CU_PARAM_TR_DEFAULT,
											pCudaTextureReference.getPeer());
	}

	public Pointer[] launch(Object... pParameters)
	{
		final int lNumberOfParameters = pParameters.length;
		final Pointer[] lNativePointerObjectArray = new Pointer[lNumberOfParameters];
		for (int i = 0; i < lNumberOfParameters; i++)
		{
			final Pointer lParameterPointer = convertParameter(pParameters[i]);
			lNativePointerObjectArray[i] = lParameterPointer;
		}
		final Pointer lKernerlParametersPointer = Pointer.to(lNativePointerObjectArray);

		cuLaunchKernel(	getPeer(),
										mGridDimX,
										mGridDimY,
										mGridDimZ,
										mBlockDimX,
										mBlockDimY,
										mBlockDimZ,
										mSharedMemoryBytes,
										null,
										lKernerlParametersPointer,
										null);
		if (mSynchronize)
			cuCtxSynchronize();

		return lNativePointerObjectArray;
	}

	private Pointer convertParameter(Object pObject)
	{
		if (pObject instanceof CudaDevicePointer)
			return Pointer.to(((CudaDevicePointer) pObject).getPeer());
		if (pObject instanceof Byte)
			return Pointer.to(new byte[]
			{ (Byte) pObject });
		if (pObject instanceof Character)
			return Pointer.to(new char[]
			{ (Character) pObject });
		if (pObject instanceof Short)
			return Pointer.to(new short[]
			{ (Short) pObject });
		if (pObject instanceof Integer)
			return Pointer.to(new int[]
			{ (Integer) pObject });
		if (pObject instanceof Long)
			return Pointer.to(new long[]
			{ (Long) pObject });
		if (pObject instanceof Float)
			return Pointer.to(new float[]
			{ (Float) pObject });
		if (pObject instanceof Double)
			return Pointer.to(new double[]
			{ (Double) pObject });
		if (pObject instanceof byte[])
			return Pointer.to((byte[]) pObject);
		if (pObject instanceof char[])
			return Pointer.to((char[]) pObject);
		if (pObject instanceof short[])
			return Pointer.to((short[]) pObject);
		if (pObject instanceof int[])
			return Pointer.to((int[]) pObject);
		if (pObject instanceof long[])
			return Pointer.to((long[]) pObject);
		if (pObject instanceof float[])
			return Pointer.to((float[]) pObject);
		if (pObject instanceof double[])
			return Pointer.to((double[]) pObject);
		return null;
	}

	@Override
	public CUfunction getPeer()
	{
		return mCUfunction;
	}

	public String getFunctionName()
	{
		return mFunctionName;
	}

	@Override
	public String toString()
	{
		return "CudaFunction [mFunctionName=" + mFunctionName + "]";
	}

	public boolean isSynchronize()
	{
		return mSynchronize;
	}

	public void setSynchronize(boolean pSynchronize)
	{
		mSynchronize = pSynchronize;
	}

}
