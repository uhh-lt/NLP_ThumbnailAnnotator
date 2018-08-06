

/* First created by JCasGen Mon Aug 06 17:40:38 CEST 2018 */
package captionTokenExtractor.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Token that holds the begin and end in another CAS View
 * Updated by JCasGen Mon Aug 06 17:40:38 CEST 2018
 * XML source: /home/p0w3r/gitrepos/NLP_ThumbnailAnnotator/thumbnailAnnotator.parent/thumbnailAnnotator.core/target/jcasgen/typesystem.xml
 * @generated */
public class ViewMappingToken extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ViewMappingToken.class);
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
  protected ViewMappingToken() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ViewMappingToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ViewMappingToken(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ViewMappingToken(JCas jcas, int begin, int end) {
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
  //* Feature: ValueSourceView

  /** getter for ValueSourceView - gets The surface value of the Token in the Source View
   * @generated
   * @return value of the feature 
   */
  public String getValueSourceView() {
    if (ViewMappingToken_Type.featOkTst && ((ViewMappingToken_Type)jcasType).casFeat_ValueSourceView == null)
      jcasType.jcas.throwFeatMissing("ValueSourceView", "captionTokenExtractor.type.ViewMappingToken");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ViewMappingToken_Type)jcasType).casFeatCode_ValueSourceView);}
    
  /** setter for ValueSourceView - sets The surface value of the Token in the Source View 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValueSourceView(String v) {
    if (ViewMappingToken_Type.featOkTst && ((ViewMappingToken_Type)jcasType).casFeat_ValueSourceView == null)
      jcasType.jcas.throwFeatMissing("ValueSourceView", "captionTokenExtractor.type.ViewMappingToken");
    jcasType.ll_cas.ll_setStringValue(addr, ((ViewMappingToken_Type)jcasType).casFeatCode_ValueSourceView, v);}    
   
    
  //*--------------*
  //* Feature: BeginSourceView

  /** getter for BeginSourceView - gets Begin in the source view
   * @generated
   * @return value of the feature 
   */
  public int getBeginSourceView() {
    if (ViewMappingToken_Type.featOkTst && ((ViewMappingToken_Type)jcasType).casFeat_BeginSourceView == null)
      jcasType.jcas.throwFeatMissing("BeginSourceView", "captionTokenExtractor.type.ViewMappingToken");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ViewMappingToken_Type)jcasType).casFeatCode_BeginSourceView);}
    
  /** setter for BeginSourceView - sets Begin in the source view 
   * @generated
   * @param v value to set into the feature 
   */
  public void setBeginSourceView(int v) {
    if (ViewMappingToken_Type.featOkTst && ((ViewMappingToken_Type)jcasType).casFeat_BeginSourceView == null)
      jcasType.jcas.throwFeatMissing("BeginSourceView", "captionTokenExtractor.type.ViewMappingToken");
    jcasType.ll_cas.ll_setIntValue(addr, ((ViewMappingToken_Type)jcasType).casFeatCode_BeginSourceView, v);}    
   
    
  //*--------------*
  //* Feature: EndSourceView

  /** getter for EndSourceView - gets End in the source view
   * @generated
   * @return value of the feature 
   */
  public int getEndSourceView() {
    if (ViewMappingToken_Type.featOkTst && ((ViewMappingToken_Type)jcasType).casFeat_EndSourceView == null)
      jcasType.jcas.throwFeatMissing("EndSourceView", "captionTokenExtractor.type.ViewMappingToken");
    return jcasType.ll_cas.ll_getIntValue(addr, ((ViewMappingToken_Type)jcasType).casFeatCode_EndSourceView);}
    
  /** setter for EndSourceView - sets End in the source view 
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndSourceView(int v) {
    if (ViewMappingToken_Type.featOkTst && ((ViewMappingToken_Type)jcasType).casFeat_EndSourceView == null)
      jcasType.jcas.throwFeatMissing("EndSourceView", "captionTokenExtractor.type.ViewMappingToken");
    jcasType.ll_cas.ll_setIntValue(addr, ((ViewMappingToken_Type)jcasType).casFeatCode_EndSourceView, v);}    
  }

    