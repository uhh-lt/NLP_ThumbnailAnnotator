package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import captionTokenExtractor.type.PosExclusionFlagToken;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
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
    public void process(JCas aJCas) {
        // create CaptionTokenAnnotations from NamedEntities
        for (NamedEntity ne : JCasUtil.select(aJCas, NamedEntity.class)) {
            CaptionTokenAnnotation captionToken = new CaptionTokenAnnotation(aJCas, ne.getBegin(), ne.getEnd());
            captionToken.setTypeOf(ne.getValue());
            captionToken.setValue(captionToken.getCoveredText());

            CaptionTokenAnnotatorUtil.setTokenList(aJCas, captionToken);
            CaptionTokenAnnotatorUtil.setPosList(aJCas, captionToken);
            CaptionTokenAnnotatorUtil.setLemmaList(aJCas, captionToken);

            captionToken.addToIndexes();

            // set exclusion flag of PosExclusionFlagTokens to true
            for (PosExclusionFlagToken peft : JCasUtil.selectCovered(aJCas, PosExclusionFlagToken.class, captionToken))
                peft.setExclude(true);
        }
    }

}
