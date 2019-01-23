package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

public class CaptionTokenAnnotatorUtil {

    public static void setPosList(JCas aJCas, CaptionTokenAnnotation captionToken) {
        // get underlying POS Tags and transform to semicolon separated string aka POSList
        StringBuilder sb = new StringBuilder();
        for (POS p : JCasUtil.selectCovered(aJCas, POS.class, captionToken))
            sb.append(p.getPosValue()).append(";");
        captionToken.setPOSList(sb.toString().substring(0, sb.length() - 1));
    }

    public static void setTokenList(JCas aJCas, CaptionTokenAnnotation captionToken) {
        // get underlying Tokens and transform to semicolon separated string aka TokenList
        StringBuilder sb = new StringBuilder();
        for (Token t : JCasUtil.selectCovered(aJCas, Token.class, captionToken))
            sb.append(t.getCoveredText()).append(";");
        captionToken.setTokenList(sb.toString().substring(0, sb.length() - 1));
    }

    public static void setLemmaList(JCas aJCas, CaptionTokenAnnotation captionToken) {
        // get underlying Lemmata and transform to semicolon separated string aka LemmaList
        StringBuilder sb = new StringBuilder();
        for (Lemma l : JCasUtil.selectCovered(aJCas, Lemma.class, captionToken))
            sb.append(l.getValue()).append(";");
        captionToken.setLemmaList(sb.toString().substring(0, sb.length() - 1));
    }
}
