package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator;

import captionTokenExtractor.type.PosExclusionFlagToken;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;


@TypeCapability(
        inputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"},
        outputs = {"captionTokenExtractor.type.PosExclusionFlagToken"}
)
public class PosExclusionFlagTokenAnnotator extends JCasAnnotator_ImplBase {
    /**
     * Creates a PosExclusionFlagToken for each POS Tag with the exclusion flag set to false
     * @param aJCas
     * @throws AnalysisEngineProcessException
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        for (POS pos : JCasUtil.select(aJCas, POS.class)) {
            PosExclusionFlagToken peft = new PosExclusionFlagToken(aJCas, pos.getBegin(), pos.getEnd());
            peft.setExclude(false);
            peft.setPosValue(pos.getPosValue());
            peft.addToIndexes();
        }
    }
}
