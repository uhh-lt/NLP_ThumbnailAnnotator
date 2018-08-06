
/* First created by JCasGen Mon Aug 06 17:40:38 CEST 2018 */
package captionTokenExtractor.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Token that wraps a POS Token including an additional flag to indicate if the POS should be ignored for further processing
 * Updated by JCasGen Mon Aug 06 17:40:38 CEST 2018
 * @generated */
public class PosExclusionFlagToken_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = PosExclusionFlagToken.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("captionTokenExtractor.type.PosExclusionFlagToken");
 
  /** @generated */
  final Feature casFeat_PosValue;
  /** @generated */
  final int     casFeatCode_PosValue;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPosValue(int addr) {
        if (featOkTst && casFeat_PosValue == null)
      jcas.throwFeatMissing("PosValue", "captionTokenExtractor.type.PosExclusionFlagToken");
    return ll_cas.ll_getStringValue(addr, casFeatCode_PosValue);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPosValue(int addr, String v) {
        if (featOkTst && casFeat_PosValue == null)
      jcas.throwFeatMissing("PosValue", "captionTokenExtractor.type.PosExclusionFlagToken");
    ll_cas.ll_setStringValue(addr, casFeatCode_PosValue, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Exclude;
  /** @generated */
  final int     casFeatCode_Exclude;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getExclude(int addr) {
        if (featOkTst && casFeat_Exclude == null)
      jcas.throwFeatMissing("Exclude", "captionTokenExtractor.type.PosExclusionFlagToken");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_Exclude);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setExclude(int addr, boolean v) {
        if (featOkTst && casFeat_Exclude == null)
      jcas.throwFeatMissing("Exclude", "captionTokenExtractor.type.PosExclusionFlagToken");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_Exclude, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public PosExclusionFlagToken_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_PosValue = jcas.getRequiredFeatureDE(casType, "PosValue", "uima.cas.String", featOkTst);
    casFeatCode_PosValue  = (null == casFeat_PosValue) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_PosValue).getCode();

 
    casFeat_Exclude = jcas.getRequiredFeatureDE(casType, "Exclude", "uima.cas.Boolean", featOkTst);
    casFeatCode_Exclude  = (null == casFeat_Exclude) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Exclude).getCode();

  }
}



    