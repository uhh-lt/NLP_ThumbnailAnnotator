package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import lombok.Data;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.NamedEntityCaptionTokenAnnotator;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.NounCaptionTokenAnnotator;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.PosExclusionFlagTokenAnnotator;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.PosViewCreator;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractionResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The main component to manage the extraction of {@link CaptionToken}s from {@link UserInput}s.
 * Internally the extraction is done in parallel by {@link ExtractorAgent}s that are instantiated for each {@link UserInput} to support higher loads.
 */
public class CaptionTokenExtractor {

    /**
     * Agent that extracts the {@link CaptionToken}s  from a {@link UserInput} and wraps the result in a {@link ExtractionResult}.
     */
    @Data
    private class ExtractorAgent implements Callable<ExtractionResult> {

        /**
         * The {@link UserInput}  that is processed by this {@link ExtractorAgent}
         */
        private UserInput userInput;

        public ExtractorAgent(UserInput userInput) {
            this.userInput = userInput;
        }

        /**
         * Creates a JCas that holds the {@link UserInput} as document text.
         *
         * @param userInput the input from a user wrapped in a {@link UserInput}
         * @return the JCas that holds the {@link UserInput} as document text.
         * @throws ResourceInitializationException
         * @throws AnalysisEngineProcessException
         */
        private JCas createJCasFromUserInput(UserInput userInput) throws ResourceInitializationException, AnalysisEngineProcessException {
            // create JCas containing userInput from the coreExtractorEngine
            JCas userInputJCas = CaptionTokenExtractor.getInstance().coreExtractorEngine.newJCas();
            userInputJCas.setDocumentText(userInput.getValue());
            return userInputJCas;
        }

        /**
         * The main extraction happens here!
         *
         * @return the result wrapped in a {@link ExtractionResult}
         * @throws AnalysisEngineProcessException
         * @throws ResourceInitializationException
         */
        @Override
        public ExtractionResult call() throws AnalysisEngineProcessException, ResourceInitializationException {
            // create userInputJCas
            JCas userInputJCas = createJCasFromUserInput(this.userInput);
            // process userInputJCas
            CaptionTokenExtractor.getInstance().coreExtractorEngine.process(userInputJCas);

            ExtractionResult extractionResult = new ExtractionResult();
            extractionResult.setUserInput(userInput);
            // get the CaptionTokenAnnotations
            for (CaptionTokenAnnotation cta : JCasUtil.select(userInputJCas, CaptionTokenAnnotation.class))
                // transform them into CaptionTokens and add them to the extractionResult
                extractionResult.addCaptionToken(createCaptionTokenFromAnnotation(cta));

            return extractionResult;
        }
    }

    private static final Integer MAX_PARALLEL_THREADS = 16;
    private static String LANGUAGE = "en";

    /**
     * the app AnalysisEngine to extract CaptionTokens
     */
    private AnalysisEngine coreExtractorEngine;
    /**
     * the managed thread threadPool
     */
    private ExecutorService threadPool;

    /**
     * singleton instance
     */
    private static CaptionTokenExtractor _singleton;

    private CaptionTokenExtractor() throws ResourceInitializationException {
        //build CoreExtractorEngine
        this.coreExtractorEngine = buildCoreExtractorEngine();
        //create a managed thread threadPool with fixed size
        this.threadPool = Executors.newFixedThreadPool(MAX_PARALLEL_THREADS);
    }

    /**
     * Get the singleton instance of the {@link CaptionTokenExtractor}
     *
     * @return
     * @throws ResourceInitializationException
     */
    public static CaptionTokenExtractor getInstance() throws ResourceInitializationException {
        if (_singleton == null)
            _singleton = new CaptionTokenExtractor();
        return _singleton;
    }

    /**
     * Method to create the (aggregate) {@link AnalysisEngine} to extract the {@link CaptionToken}s.
     * This package private by intention to support unit testing!
     *
     * @return the {@link AnalysisEngine} to extract the {@link CaptionToken}s.
     * @throws ResourceInitializationException
     */
    AnalysisEngine buildCoreExtractorEngine() throws ResourceInitializationException {

        AggregateBuilder aggregateBuilder = new AggregateBuilder();

        AnalysisEngineDescription seg = AnalysisEngineFactory.createEngineDescription(OpenNlpSegmenter.class,
                OpenNlpSegmenter.PARAM_LANGUAGE, LANGUAGE);
        aggregateBuilder.add(seg);

        AnalysisEngineDescription pos = AnalysisEngineFactory.createEngineDescription(ClearNlpPosTagger.class,
                ClearNlpPosTagger.PARAM_LANGUAGE, LANGUAGE);
        aggregateBuilder.add(pos);

        AnalysisEngineDescription nerLoc = AnalysisEngineFactory.createEngineDescription(OpenNlpNamedEntityRecognizer.class,
                OpenNlpNamedEntityRecognizer.PARAM_LANGUAGE, LANGUAGE,
                OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "location");
        aggregateBuilder.add(nerLoc);

        AnalysisEngineDescription nerPers = AnalysisEngineFactory.createEngineDescription(OpenNlpNamedEntityRecognizer.class,
                OpenNlpNamedEntityRecognizer.PARAM_LANGUAGE, LANGUAGE,
                OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "person");
        aggregateBuilder.add(nerPers);

        AnalysisEngineDescription nerOrg = AnalysisEngineFactory.createEngineDescription(OpenNlpNamedEntityRecognizer.class,
                OpenNlpNamedEntityRecognizer.PARAM_LANGUAGE, LANGUAGE,
                OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "organization");
        aggregateBuilder.add(nerOrg);

        AnalysisEngineDescription nerDate = AnalysisEngineFactory.createEngineDescription(OpenNlpNamedEntityRecognizer.class,
                OpenNlpNamedEntityRecognizer.PARAM_LANGUAGE, LANGUAGE,
                OpenNlpNamedEntityRecognizer.PARAM_VARIANT, "date");
        aggregateBuilder.add(nerDate);

        AnalysisEngineDescription pefta = AnalysisEngineFactory.createEngineDescription(PosExclusionFlagTokenAnnotator.class);
        aggregateBuilder.add(pefta);

        AnalysisEngineDescription necta = AnalysisEngineFactory.createEngineDescription(NamedEntityCaptionTokenAnnotator.class);
        aggregateBuilder.add(necta);

        AnalysisEngineDescription pvc = AnalysisEngineFactory.createEngineDescription(PosViewCreator.class);
        aggregateBuilder.add(pvc);

        AnalysisEngineDescription nncta = AnalysisEngineFactory.createEngineDescription(NounCaptionTokenAnnotator.class);
        aggregateBuilder.add(nncta);

        return aggregateBuilder.createAggregate();
    }


    /**
     * Starts the extraction of {@link CaptionToken}s from a {@link UserInput} by instantiating a {@link ExtractorAgent}
     * and submitting it to a managed {@link sun.nio.ch.ThreadPool}.
     *
     * @param userInput the input from a user wrapped in a {@link UserInput}
     * @return a {@link Future} that holds the {@link ExtractionResult}
     */
    public Future<ExtractionResult> startExtractionOfCaptionTokens(UserInput userInput) {
        return this.threadPool.submit(new ExtractorAgent(userInput));
    }

    /**
     * Creates a {@link CaptionToken} from a {@link CaptionTokenAnnotation} (which is a UIMA Type)
     *
     * @param cta a {@link CaptionTokenAnnotation} instance
     * @return a {@link CaptionToken} instance
     */
    private static CaptionToken createCaptionTokenFromAnnotation(CaptionTokenAnnotation cta) {
        List<String> posTags = Arrays.asList(cta.getPOSList().split(";"));
        List<String> tokens = Arrays.asList(cta.getTokenList().split(";"));
        return new CaptionToken(cta.getValue(), CaptionToken.Type.valueOf(cta.getTypeOf().toUpperCase()), cta.getBegin(), cta.getEnd(), posTags, tokens);
    }

}
