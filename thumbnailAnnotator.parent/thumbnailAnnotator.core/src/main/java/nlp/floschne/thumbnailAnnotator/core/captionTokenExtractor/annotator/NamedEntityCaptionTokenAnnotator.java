package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import captionTokenExtractor.type.PosExclusionFlagToken;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

@TypeCapability(
        inputs = {"de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity"},
        outputs = {"captionTokenExtractor.type.CaptionTokenAnnotation"}
)
public class NamedEntityCaptionTokenAnnotator extends JCasAnnotator_ImplBase {
    /**
     * Creates CaptionTokenAnnotations from NamedEntities.
     * Needs to run after NER and PosExclusionFlagTokenAnnotator and before NounCaptionTokenAnnotator since it sets the
     * exclusion flag of the PosExclusionFlagTokens that are covered by the NEs to false.
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        // create CaptionTokenAnnotations from NamedEntities
        for (NamedEntity ne : JCasUtil.select(aJCas, NamedEntity.class)) {
            CaptionTokenAnnotation captionToken = new CaptionTokenAnnotation(aJCas, ne.getBegin(), ne.getEnd());
            captionToken.setTypeOf(ne.getValue());
            captionToken.setValue(captionToken.getCoveredText());

            // get underlying POS Tags and transform to semicolon separated string aka POSList
            StringBuilder sb = new StringBuilder();
            for (POS p : JCasUtil.selectCovered(aJCas, POS.class, captionToken))
                sb.append(p.getPosValue()).append(";");
            captionToken.setPOSList(sb.toString().substring(0, sb.length() - 1));

            // get underlying Tokens and transform to semicolon separated string aka TokenList
            sb.setLength(0);
            for (Token t : JCasUtil.selectCovered(aJCas, Token.class, captionToken))
                sb.append(t.getCoveredText()).append(";");
            captionToken.setTokenList(sb.toString().substring(0, sb.length() - 1));


            captionToken.addToIndexes();

            // set exclusion flag of PosExclusionFlagTokens to true
            for (PosExclusionFlagToken peft : JCasUtil.selectCovered(aJCas, PosExclusionFlagToken.class, captionToken))
                peft.setExclude(true);
        }
    }

}
