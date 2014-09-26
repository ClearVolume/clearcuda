package clearcudaj;

import static jcuda.driver.JCudaDriver.CU_PARAM_TR_DEFAULT;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuParamSetTexRef;
import jcuda.NativePointerObject;
import jcuda.Pointer;
import jcuda.driver.CUfunction;

public class CudaFunction
{

	private final String mFunctionName;
	private CUfunction mCUfunction;

	private int mGridDimX, mGridDimY, mGridDimZ, mBlockDimX,
			mBlockDimY, mBlockDimZ, mSharedMemoryBytes;

	protected CudaFunction(CUfunction pCUfunction, String pFunctionName)
	{
		mCUfunction = pCUfunction;
		mFunctionName = pFunctionName;
	}

	public CudaFunction(CudaModule pCudaModule, String pFunctionName)
	{
		mFunctionName = pFunctionName;
		mCUfunction = new CUfunction();
		int lCuModuleGetFunction = cuModuleGetFunction(	mCUfunction,
																										pCudaModule.getPeer(),
																										pFunctionName);
		System.out.println(lCuModuleGetFunction);
	}

	public void setGridDim(int... pGridDim)
	{
		mGridDimX = pGridDim[0];
		mGridDimY = pGridDim[1];
		mGridDimZ = pGridDim[2];
	}

	public void setBlockDim(int... pBlockDim)
	{
		mBlockDimX = pBlockDim[0];
		mBlockDimY = pBlockDim[1];
		mBlockDimZ = pBlockDim[2];
	}

	public void setTexture(CudaTextureReference pCudaTextureReference)
	{
		cuParamSetTexRef(	getPeer(),
											CU_PARAM_TR_DEFAULT,
											pCudaTextureReference.getPeer());
	}

	public int launch(Object... pParameters)
	{
		final int lNumberOfParameters = pParameters.length;
		NativePointerObject[] lNativePointerObjectArray = new NativePointerObject[lNumberOfParameters];
		for (int i = 0; i < lNumberOfParameters; i++)
		{
			NativePointerObject lParameterPointer = convertParameter(pParameters[i]);
			lNativePointerObjectArray[i] = lParameterPointer;
		}
		Pointer lKernerlParametersPointer = Pointer.to(lNativePointerObjectArray);

		/*
		Pointer.to(Pointer.to(a,b,c)
		
		mCUdeviceptr),
								Pointer.to(new int[]
								{ getTextureWidth() }),
								Pointer.to(new int[]
								{ getTextureHeight() }),
								Pointer.to(new float[]
								{ (float) getScaleX() }),
								Pointer.to(new float[]
								{ (float) getScaleY() }),
								Pointer.to(new float[]
								{ (float) getScaleZ() }),
								Pointer.to(new float[]
								{ (float) getBrightness() }),
								Pointer.to(new float[]
								{ (float) getTransferRangeMin() }),
								Pointer.to(new float[]
								{ (float) getTransferRangeMax() }),
								Pointer.to(new float[]
								{ (float) getGamma() }));
		/**/

		return cuLaunchKernel(getPeer(),
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
	}

	private NativePointerObject convertParameter(Object pObject)
	{
		if (pObject instanceof CudaDevicePointer)
			return Pointer.to(((CudaDevicePointer) pObject).getPeer());
		if (pObject instanceof Byte)
			return Pointer.to(new byte[]
			{ (byte) pObject });
		if (pObject instanceof Character)
			return Pointer.to(new char[]
			{ (char) pObject });
		if (pObject instanceof Short)
			return Pointer.to(new short[]
			{ (short) pObject });
		if (pObject instanceof Integer)
			return Pointer.to(new int[]
			{ (int) pObject });
		if (pObject instanceof Long)
			return Pointer.to(new long[]
			{ (long) pObject });
		if (pObject instanceof Float)
			return Pointer.to(new float[]
			{ (float) pObject });
		if (pObject instanceof Double)
			return Pointer.to(new double[]
			{ (double) pObject });
		return null;
	}


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

}
