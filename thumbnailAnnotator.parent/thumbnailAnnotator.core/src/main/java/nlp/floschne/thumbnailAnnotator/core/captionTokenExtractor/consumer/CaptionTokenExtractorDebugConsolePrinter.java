package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.consumer;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import captionTokenExtractor.type.PosExclusionFlagToken;
import captionTokenExtractor.type.ViewMappingToken;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import static nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.PosViewCreator.POS_VIEW;


public class CaptionTokenExtractorDebugConsolePrinter extends JCasConsumer_ImplBase {

    public static final String PARAM_PRINT_SENTENCE = "PrintSentence";
    @ConfigurationParameter(name = PARAM_PRINT_SENTENCE, description = "Boolean flag. If true print Sentences.")
    private Boolean printSentence;

    public static final String PARAM_PRINT_POS = "PrintPos";
    @ConfigurationParameter(name = PARAM_PRINT_POS, description = "Boolean flag. If true print POS Tags.")
    private Boolean printPos;

    public static final String PARAM_PRINT_PEFT = "PrintPEFT";
    @ConfigurationParameter(name = PARAM_PRINT_PEFT, description = "Boolean flag. If true print PosExclusionFlagTokens.")
    private Boolean printPEFT;

    public static final String PARAM_PRINT_NE = "PrintNE";
    @ConfigurationParameter(name = PARAM_PRINT_NE, description = "Boolean flag. If true print NamedEntities.")
    private Boolean printNE;

    public static final String PARAM_PRINT_CAPTION_TOKEN = "PrintCaptionTokens";
    @ConfigurationParameter(name = PARAM_PRINT_CAPTION_TOKEN, description = "Boolean flag. If true print CaptionTokens.")
    private Boolean printCaptionTokens;

    public static final String PARAM_PRINT_POS_VIEW = "PrintPosView";
    @ConfigurationParameter(name = PARAM_PRINT_POS_VIEW, description = "Boolean flag. If true print Sentences in PosView.")
    private Boolean printPosView;

    public static final String PARAM_PRINT_VIEW_MAPPING_TOKEN = "PrintViewMappingTokens";
    @ConfigurationParameter(name = PARAM_PRINT_VIEW_MAPPING_TOKEN, description = "Boolean flag. If true print ViewMappingTokens.")
    private Boolean printViewMappingTokens;

    private String outputString;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        outputString = null;
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        StringBuilder outputStringBuilder = new StringBuilder();

        for (Sentence s : JCasUtil.select(aJCas, Sentence.class)) {

            if (printSentence) {
                outputStringBuilder.append("\n\n");
                // Sentence
                if (!s.getCoveredText().isEmpty()) {
                    outputStringBuilder.append("[").append(s.getType().getShortName()).append("]: ").append("\n");
                    outputStringBuilder.append("\t").append(s.getCoveredText());
                }
            }

            if (printPos) {
                outputStringBuilder.append("\n");
                // POS tags
                outputStringBuilder.append("POS: ");
                for (POS p : JCasUtil.selectCovered(aJCas, POS.class, s)) {
                    outputStringBuilder.append(p.getCoveredText()).append("[" + p.getPosValue() + "] ");
                }
            }

            if (printPEFT) {
                outputStringBuilder.append("\n");
                // PosExclusionFlagTokens
                outputStringBuilder.append("PEFTs: ");
                for (PosExclusionFlagToken peft : JCasUtil.selectCovered(aJCas, PosExclusionFlagToken.class, s)) {
                    outputStringBuilder.append("[" + peft.getPosValue() + " | " + peft.getExclude() + "] ");
                }
            }

            if (printNE) {
                outputStringBuilder.append("\n");
                //  NamedEntity
                outputStringBuilder.append("NamedEntity: ");
                for (NamedEntity ne : JCasUtil.selectCovered(aJCas, NamedEntity.class, s)) {
                    outputStringBuilder.append(ne.getCoveredText()).append("[" + ne.getValue() + "] ");
                }
            }

            if (printCaptionTokens) {
                outputStringBuilder.append("\n");
                // CaptionTokens
                outputStringBuilder.append("CaptionTokens: ");
                for (CaptionTokenAnnotation ct : JCasUtil.selectCovered(aJCas, CaptionTokenAnnotation.class, s)) {
                    outputStringBuilder.append(ct.getValue()).append("[" + ct.getTypeOf() + " | " + ct.getPOSList() + "] ");
                }
            }
        }

        try {
            JCas posView = aJCas.getView(POS_VIEW);
            if (printViewMappingTokens) {
                outputStringBuilder.append("\n\nViewMappingTokens:\n");
                // ViewMappingTokens
                for (Sentence s : JCasUtil.select(posView, Sentence.class)) {
                    for (ViewMappingToken vmt : JCasUtil.selectCovered(posView, ViewMappingToken.class, s))
                        outputStringBuilder.append(vmt.getCoveredText()).append("[" + vmt.getValueSourceView() + "] ");
                    outputStringBuilder.append("\n");
                }
            }

            if (printPosView) {
                outputStringBuilder.append("\n\n");
                // Sentences in PosView
                for (Sentence s : JCasUtil.select(posView, Sentence.class)) {
                    outputStringBuilder.append("[PosView ").append(s.getType().getShortName()).append("]: ").append("\n");
                    outputStringBuilder.append(s.getCoveredText()).append("\n");
                }
            }
        } catch (CASException e) {
            e.printStackTrace();
        }

        // create a JCas with the output as document text (to access it from outside)
        this.outputString = outputStringBuilder.toString();
        JCas out = JCasUtil.getView(aJCas, "OUTPUT_CAS", true);
        out.setDocumentText(outputString);

        //print in console
        getContext().getLogger().log(Level.INFO, this.outputString);
    }

    public String getOutputString() {
        return outputString;
    }
}

