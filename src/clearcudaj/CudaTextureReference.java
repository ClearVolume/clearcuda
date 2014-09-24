package clearcudaj;

import static jcuda.driver.JCudaDriver.CU_TRSA_OVERRIDE_FORMAT;
import static jcuda.driver.JCudaDriver.cuModuleGetTexRef;
import static jcuda.driver.JCudaDriver.cuTexRefSetAddressMode;
import static jcuda.driver.JCudaDriver.cuTexRefSetArray;
import static jcuda.driver.JCudaDriver.cuTexRefSetFilterMode;
import static jcuda.driver.JCudaDriver.cuTexRefSetFlags;
import static jcuda.driver.JCudaDriver.cuTexRefSetFormat;
import jcuda.driver.CUtexref;

public class CudaTextureReference
{

	private final CUtexref mCUtexref = new CUtexref();;
	private final String mTextureName;

	public CudaTextureReference(CudaModule pCudaModule,
															String pTextureName)
	{
		mTextureName = pTextureName;
		cuModuleGetTexRef(mCUtexref, pCudaModule.getPeer(), pTextureName);
	}

	public void setTo(CudaArray pCudaArray)
	{
		cuTexRefSetFormat(mCUtexref,
											pCudaArray.getFormat(),
											pCudaArray.getNumberOfChannels());
		cuTexRefSetArray(	mCUtexref,
											pCudaArray.getPeer(),
											CU_TRSA_OVERRIDE_FORMAT);
	}

	public void setFilterMode(int pFilterMode)
	{
		cuTexRefSetFilterMode(mCUtexref, pFilterMode);
	}

	public void setAddressMode(int pDimensionIndex, int pAddressMode)
	{
		cuTexRefSetAddressMode(mCUtexref, pDimensionIndex, pAddressMode);
	}

	public void setFlags(int pFlags)
	{
		cuTexRefSetFlags(mCUtexref, pFlags);
	}

	public CUtexref getPeer()
	{
		return mCUtexref;
	}

	public String getTextureName()
	{
		return mTextureName;
	}

	@Override
	public String toString()
	{
		return "CudaTextureReference [mTextureName=" + mTextureName + "]";
	}


}
