package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator;

import captionTokenExtractor.type.PosExclusionFlagToken;
import captionTokenExtractor.type.ViewMappingToken;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.SofaCapability;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import static org.apache.uima.cas.CAS.NAME_DEFAULT_SOFA;


@SofaCapability(
        inputSofas = NAME_DEFAULT_SOFA,
        outputSofas = PosViewCreator.POS_VIEW
)
@TypeCapability(
        inputs = {"captionTokenExtractor.type.PosExclusionToken"},
        outputs = {"captionTokenExtractor.type.ViewMappingToken"}
)
public class PosViewCreator extends JCasAnnotator_ImplBase {
    // public by intention!
    public static final String POS_VIEW = "POS_VIEW";

    /**
     * Creates  a JCas View that only holds the POS tags as text and is annotated with Sentences and ViewMappingTokens
     * for each PosExclusionTags
     *
     * @param aJCas input JCas (should be default)
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        JCas defaultView = JCasUtil.getView(aJCas, NAME_DEFAULT_SOFA, false);
        JCas posView = JCasUtil.getView(aJCas, POS_VIEW, true);

        StringBuilder posViewText = new StringBuilder();
        Integer offset = 0;
        Integer sentenceBegin = offset;
        for (Sentence s : JCasUtil.select(defaultView, Sentence.class)) {
            sentenceBegin = offset;
            for (PosExclusionFlagToken pet : JCasUtil.selectCovered(defaultView, PosExclusionFlagToken.class, s)) {
                // create the ViewMappingTokens and set document text to be equal to POS Tags separated by spaces
                posViewText.append(pet.getPosValue());
                ViewMappingToken vmt = new ViewMappingToken(posView, offset, posViewText.length());
                vmt.setBeginSourceView(pet.getBegin());
                vmt.setEndSourceView(pet.getEnd());
                vmt.setValueSourceView(defaultView.getDocumentText().substring(vmt.getBeginSourceView(), vmt.getEndSourceView()));
                vmt.addToIndexes();
                posViewText.append(" ");
                offset += pet.getPosValue().length() + 1;
            }
            // create sentence in pos view
            Sentence sPos = new Sentence(posView, sentenceBegin, posViewText.toString().trim().length());
            sPos.addToIndexes();
        }
        posView.setDocumentText(posViewText.toString().trim());

    }
}
