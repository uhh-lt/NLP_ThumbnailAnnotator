
/* First created by JCasGen Mon Aug 06 17:40:38 CEST 2018 */
package captionTokenExtractor.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Aug 06 17:40:38 CEST 2018
 * @generated */
public class CaptionTokenAnnotation_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CaptionTokenAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("captionTokenExtractor.type.CaptionTokenAnnotation");
 
  /** @generated */
  final Feature casFeat_Value;
  /** @generated */
  final int     casFeatCode_Value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_Value == null)
      jcas.throwFeatMissing("Value", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_Value == null)
      jcas.throwFeatMissing("Value", "captionTokenExtractor.type.CaptionTokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TypeOf;
  /** @generated */
  final int     casFeatCode_TypeOf;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTypeOf(int addr) {
        if (featOkTst && casFeat_TypeOf == null)
      jcas.throwFeatMissing("TypeOf", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TypeOf);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTypeOf(int addr, String v) {
        if (featOkTst && casFeat_TypeOf == null)
      jcas.throwFeatMissing("TypeOf", "captionTokenExtractor.type.CaptionTokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_TypeOf, v);}
    
  
 
  /** @generated */
  final Feature casFeat_TokenList;
  /** @generated */
  final int     casFeatCode_TokenList;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTokenList(int addr) {
        if (featOkTst && casFeat_TokenList == null)
      jcas.throwFeatMissing("TokenList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_TokenList);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTokenList(int addr, String v) {
        if (featOkTst && casFeat_TokenList == null)
      jcas.throwFeatMissing("TokenList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_TokenList, v);}
    
  
 
  /** @generated */
  final Feature casFeat_POSList;
  /** @generated */
  final int     casFeatCode_POSList;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPOSList(int addr) {
        if (featOkTst && casFeat_POSList == null)
      jcas.throwFeatMissing("POSList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_POSList);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPOSList(int addr, String v) {
        if (featOkTst && casFeat_POSList == null)
      jcas.throwFeatMissing("POSList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_POSList, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CaptionTokenAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Value = jcas.getRequiredFeatureDE(casType, "Value", "uima.cas.String", featOkTst);
    casFeatCode_Value  = (null == casFeat_Value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Value).getCode();

 
    casFeat_TypeOf = jcas.getRequiredFeatureDE(casType, "TypeOf", "uima.cas.String", featOkTst);
    casFeatCode_TypeOf  = (null == casFeat_TypeOf) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TypeOf).getCode();

 
    casFeat_TokenList = jcas.getRequiredFeatureDE(casType, "TokenList", "uima.cas.String", featOkTst);
    casFeatCode_TokenList  = (null == casFeat_TokenList) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_TokenList).getCode();

 
    casFeat_POSList = jcas.getRequiredFeatureDE(casType, "POSList", "uima.cas.String", featOkTst);
    casFeatCode_POSList  = (null == casFeat_POSList) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_POSList).getCode();

  }
}



    