

/* First created by JCasGen Mon Aug 06 17:40:38 CEST 2018 */
package captionTokenExtractor.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Token that wraps a POS Token including an additional flag to indicate if the POS should be ignored for further processing
 * Updated by JCasGen Mon Aug 06 17:40:38 CEST 2018
 * XML source: /home/p0w3r/gitrepos/NLP_ThumbnailAnnotator/thumbnailAnnotator.parent/thumbnailAnnotator.core/target/jcasgen/typesystem.xml
 * @generated */
public class PosExclusionFlagToken extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PosExclusionFlagToken.class);
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
  protected PosExclusionFlagToken() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public PosExclusionFlagToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public PosExclusionFlagToken(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public PosExclusionFlagToken(JCas jcas, int begin, int end) {
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
  //* Feature: PosValue

  /** getter for PosValue - gets The wrapped POS Tag value
   * @generated
   * @return value of the feature 
   */
  public String getPosValue() {
    if (PosExclusionFlagToken_Type.featOkTst && ((PosExclusionFlagToken_Type)jcasType).casFeat_PosValue == null)
      jcasType.jcas.throwFeatMissing("PosValue", "captionTokenExtractor.type.PosExclusionFlagToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((PosExclusionFlagToken_Type)jcasType).casFeatCode_PosValue);}
    
  /** setter for PosValue - sets The wrapped POS Tag value 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPosValue(String v) {
    if (PosExclusionFlagToken_Type.featOkTst && ((PosExclusionFlagToken_Type)jcasType).casFeat_PosValue == null)
      jcasType.jcas.throwFeatMissing("PosValue", "captionTokenExtractor.type.PosExclusionFlagToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((PosExclusionFlagToken_Type)jcasType).casFeatCode_PosValue, v);}    
   
    
  //*--------------*
  //* Feature: Exclude

  /** getter for Exclude - gets Flag to indicate if the wrapped POS Tag should be ignored or excluded for further processing
   * @generated
   * @return value of the feature 
   */
  public boolean getExclude() {
    if (PosExclusionFlagToken_Type.featOkTst && ((PosExclusionFlagToken_Type)jcasType).casFeat_Exclude == null)
      jcasType.jcas.throwFeatMissing("Exclude", "captionTokenExtractor.type.PosExclusionFlagToken");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((PosExclusionFlagToken_Type)jcasType).casFeatCode_Exclude);}
    
  /** setter for Exclude - sets Flag to indicate if the wrapped POS Tag should be ignored or excluded for further processing 
   * @generated
   * @param v value to set into the feature 
   */
  public void setExclude(boolean v) {
    if (PosExclusionFlagToken_Type.featOkTst && ((PosExclusionFlagToken_Type)jcasType).casFeat_Exclude == null)
      jcasType.jcas.throwFeatMissing("Exclude", "captionTokenExtractor.type.PosExclusionFlagToken");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((PosExclusionFlagToken_Type)jcasType).casFeatCode_Exclude, v);}    
  }

    