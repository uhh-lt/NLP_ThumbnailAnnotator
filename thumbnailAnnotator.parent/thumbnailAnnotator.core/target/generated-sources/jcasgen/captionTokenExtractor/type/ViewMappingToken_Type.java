
/* First created by JCasGen Mon Aug 06 17:40:38 CEST 2018 */
package captionTokenExtractor.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Token that holds the begin and end in another CAS View
 * Updated by JCasGen Mon Aug 06 17:40:38 CEST 2018
 * @generated */
public class ViewMappingToken_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ViewMappingToken.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("captionTokenExtractor.type.ViewMappingToken");
 
  /** @generated */
  final Feature casFeat_ValueSourceView;
  /** @generated */
  final int     casFeatCode_ValueSourceView;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValueSourceView(int addr) {
        if (featOkTst && casFeat_ValueSourceView == null)
      jcas.throwFeatMissing("ValueSourceView", "captionTokenExtractor.type.ViewMappingToken");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ValueSourceView);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValueSourceView(int addr, String v) {
        if (featOkTst && casFeat_ValueSourceView == null)
      jcas.throwFeatMissing("ValueSourceView", "captionTokenExtractor.type.ViewMappingToken");
    ll_cas.ll_setStringValue(addr, casFeatCode_ValueSourceView, v);}
    
  
 
  /** @generated */
  final Feature casFeat_BeginSourceView;
  /** @generated */
  final int     casFeatCode_BeginSourceView;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getBeginSourceView(int addr) {
        if (featOkTst && casFeat_BeginSourceView == null)
      jcas.throwFeatMissing("BeginSourceView", "captionTokenExtractor.type.ViewMappingToken");
    return ll_cas.ll_getIntValue(addr, casFeatCode_BeginSourceView);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBeginSourceView(int addr, int v) {
        if (featOkTst && casFeat_BeginSourceView == null)
      jcas.throwFeatMissing("BeginSourceView", "captionTokenExtractor.type.ViewMappingToken");
    ll_cas.ll_setIntValue(addr, casFeatCode_BeginSourceView, v);}
    
  
 
  /** @generated */
  final Feature casFeat_EndSourceView;
  /** @generated */
  final int     casFeatCode_EndSourceView;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getEndSourceView(int addr) {
        if (featOkTst && casFeat_EndSourceView == null)
      jcas.throwFeatMissing("EndSourceView", "captionTokenExtractor.type.ViewMappingToken");
    return ll_cas.ll_getIntValue(addr, casFeatCode_EndSourceView);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEndSourceView(int addr, int v) {
        if (featOkTst && casFeat_EndSourceView == null)
      jcas.throwFeatMissing("EndSourceView", "captionTokenExtractor.type.ViewMappingToken");
    ll_cas.ll_setIntValue(addr, casFeatCode_EndSourceView, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ViewMappingToken_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_ValueSourceView = jcas.getRequiredFeatureDE(casType, "ValueSourceView", "uima.cas.String", featOkTst);
    casFeatCode_ValueSourceView  = (null == casFeat_ValueSourceView) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ValueSourceView).getCode();

 
    casFeat_BeginSourceView = jcas.getRequiredFeatureDE(casType, "BeginSourceView", "uima.cas.Integer", featOkTst);
    casFeatCode_BeginSourceView  = (null == casFeat_BeginSourceView) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_BeginSourceView).getCode();

 
    casFeat_EndSourceView = jcas.getRequiredFeatureDE(casType, "EndSourceView", "uima.cas.Integer", featOkTst);
    casFeatCode_EndSourceView  = (null == casFeat_EndSourceView) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_EndSourceView).getCode();

  }
}



    