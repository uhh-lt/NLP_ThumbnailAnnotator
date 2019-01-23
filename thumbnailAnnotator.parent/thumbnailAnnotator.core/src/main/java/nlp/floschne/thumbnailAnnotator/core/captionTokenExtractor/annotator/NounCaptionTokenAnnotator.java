package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import captionTokenExtractor.type.PosExclusionFlagToken;
import captionTokenExtractor.type.ViewMappingToken;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.PosViewCreator.POS_VIEW;
import static org.apache.uima.cas.CAS.NAME_DEFAULT_SOFA;

@TypeCapability(
        inputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"},
        outputs = {"captionTokenExtractor.type.CaptionToken"}
)
public class NounCaptionTokenAnnotator extends JCasAnnotator_ImplBase {

    // Captures Nouns or CompoundNouns described by a list of adjectives e.g. "red, broken and big car control system"
    private static final Pattern NOUN_REGEX_PATTERN = Pattern.compile("((JJ )((, )|(CC ))?)*((NN )|(NNP )|(NNS ))+");

    /**
     * Creates CaptionToken from (consecutive) Nouns (i.e. NN, NNP & NNS POS Tags) via Regex.
     * Needs to run after NamedEntityCaptionTokenAnnotator!
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        JCas defaultView = JCasUtil.getView(aJCas, NAME_DEFAULT_SOFA, false);
        JCas posView = JCasUtil.getView(aJCas, POS_VIEW, false);
        assert posView != null : "PosViewCreator has to create the posView befor running this Annotator!";

        for (Sentence s : JCasUtil.select(posView, Sentence.class)) {

            // find matches
            String posTagText = s.getCoveredText();
            Matcher m = NOUN_REGEX_PATTERN.matcher(posTagText);
            List<MatchResult> matches = new ArrayList<>();
            while (m.find())
                matches.add(m.toMatchResult());

            for (MatchResult mr : matches) {
                Boolean exclusionFlag = Boolean.FALSE;
                // get the ViewMappingTokens that are covered by the match
                List<ViewMappingToken> vmts = JCasUtil.selectCovered(posView, ViewMappingToken.class, s.getBegin() + mr.start(), s.getBegin() + mr.end());
                for (ViewMappingToken vmt : vmts) {
                    // get PosExclusionFlagToken covered by the ViewMappingToken in the defaultView
                    List<PosExclusionFlagToken> pefts = JCasUtil.selectCovered(defaultView, PosExclusionFlagToken.class, vmt.getBeginSourceView(), vmt.getEndSourceView());
                    assert pefts.size() == 1 : "Cannot find covered PosExclusionFlagToken";
                    PosExclusionFlagToken peft = pefts.get(0);
                    // if the exclusion flag of the PosExclusionFlagToken is set to false (i.e. there is no CaptionToken that already covers the Noun of the new CaptionToken)
                    if (peft.getExclude())
                        exclusionFlag = Boolean.TRUE;
                }

                if (!exclusionFlag) {
                    // create a CaptionToken in the defaultView with the offsets and value of the original (defaultView) Text
                    CaptionTokenAnnotation captionToken = null;
                    if (vmts.size() == 1) {
                        captionToken = new CaptionTokenAnnotation(defaultView, vmts.get(0).getBeginSourceView(), vmts.get(0).getEndSourceView());
                        captionToken.setTypeOf(CaptionToken.Type.NOUN.toString());
                    } else {
                        captionToken = new CaptionTokenAnnotation(defaultView, vmts.get(0).getBeginSourceView(), vmts.get(vmts.size() - 1).getEndSourceView());
                        captionToken.setTypeOf(CaptionToken.Type.COMPOUND.toString());
                    }

                    CaptionTokenAnnotatorUtil.setTokenList(aJCas, captionToken);
                    CaptionTokenAnnotatorUtil.setPosList(aJCas, captionToken);
                    CaptionTokenAnnotatorUtil.setLemmaList(aJCas, captionToken);

                    captionToken.setValue(captionToken.getCoveredText());
                    captionToken.addToIndexes();
                }
            }
        }
    }
}
