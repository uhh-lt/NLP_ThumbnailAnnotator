

/* First created by JCasGen Mon Aug 06 17:40:38 CEST 2018 */
package captionTokenExtractor.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Aug 06 17:40:38 CEST 2018
 * XML source: /home/p0w3r/gitrepos/NLP_ThumbnailAnnotator/thumbnailAnnotator.parent/thumbnailAnnotator.core/target/jcasgen/typesystem.xml
 * @generated */
public class CaptionTokenAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CaptionTokenAnnotation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected CaptionTokenAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CaptionTokenAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CaptionTokenAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CaptionTokenAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: Value

  /** getter for Value - gets The surface value of the Token
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_Value);}
    
  /** setter for Value - sets The surface value of the Token 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_Value == null)
      jcasType.jcas.throwFeatMissing("Value", "captionTokenExtractor.type.CaptionTokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_Value, v);}    
   
    
  //*--------------*
  //* Feature: TypeOf

  /** getter for TypeOf - gets (Compound)Noun or NamedEntity
   * @generated
   * @return value of the feature 
   */
  public String getTypeOf() {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_TypeOf == null)
      jcasType.jcas.throwFeatMissing("TypeOf", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_TypeOf);}
    
  /** setter for TypeOf - sets (Compound)Noun or NamedEntity 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTypeOf(String v) {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_TypeOf == null)
      jcasType.jcas.throwFeatMissing("TypeOf", "captionTokenExtractor.type.CaptionTokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_TypeOf, v);}    
   
    
  //*--------------*
  //* Feature: TokenList

  /** getter for TokenList - gets Semicolon-separated list of the Tokens the CaptionToken consists of
   * @generated
   * @return value of the feature 
   */
  public String getTokenList() {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_TokenList == null)
      jcasType.jcas.throwFeatMissing("TokenList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_TokenList);}
    
  /** setter for TokenList - sets Semicolon-separated list of the Tokens the CaptionToken consists of 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTokenList(String v) {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_TokenList == null)
      jcasType.jcas.throwFeatMissing("TokenList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_TokenList, v);}    
   
    
  //*--------------*
  //* Feature: POSList

  /** getter for POSList - gets Semicolon-separated list of the underlying POS Tags
   * @generated
   * @return value of the feature 
   */
  public String getPOSList() {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_POSList == null)
      jcasType.jcas.throwFeatMissing("POSList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_POSList);}
    
  /** setter for POSList - sets Semicolon-separated list of the underlying POS Tags 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPOSList(String v) {
    if (CaptionTokenAnnotation_Type.featOkTst && ((CaptionTokenAnnotation_Type)jcasType).casFeat_POSList == null)
      jcasType.jcas.throwFeatMissing("POSList", "captionTokenExtractor.type.CaptionTokenAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((CaptionTokenAnnotation_Type)jcasType).casFeatCode_POSList, v);}    
  }

    