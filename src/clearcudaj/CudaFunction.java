package clearcudaj;

import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import jcuda.NativePointerObject;
import jcuda.Pointer;
import jcuda.driver.CUfunction;

public class CudaFunction implements Runnable
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

	public CudaFunction(CudaModule pCudaModule,
											String pFunctionName)
	{
		mFunctionName = pFunctionName;
		mCUfunction = new CUfunction();
		int lCuModuleGetFunction = cuModuleGetFunction(	mCUfunction,
												pCudaModule.getPeer(),
												pFunctionName);
		System.out.println(lCuModuleGetFunction);
	}

	public int launch(Object... pParameters)
	{
		final int lNumberOfParameters = pParameters.length;
		NativePointerObject[] lNativePointerObjectArray = new NativePointerObject[lNumberOfParameters];
		for (int i = 0; i < lNumberOfParameters; i++)
		{
			Pointer lParameterPointer = convertParameter(pParameters[i]);
			lNativePointerObjectArray[i] = lParameterPointer;
		}
		Pointer lKernerlParametersPointer = Pointer.to(lNativePointerObjectArray);

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

	private Pointer convertParameter(Object pObject)
	{
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

	@Override
	public void run()
	{

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
