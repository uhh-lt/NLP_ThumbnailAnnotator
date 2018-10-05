package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor;

import captionTokenExtractor.type.CaptionTokenAnnotation;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.maltparser.MaltParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpNamedEntityRecognizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedExtendedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NoNormalization;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.SetOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.StringSplit;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSynsetSenseInventory;
import lombok.Data;
import net.sf.extjwnl.JWNLException;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.NamedEntityCaptionTokenAnnotator;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.NounCaptionTokenAnnotator;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.PosExclusionFlagTokenAnnotator;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.annotator.PosViewCreator;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractorResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UDependency;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * The main component to manage the extraction of {@link CaptionToken}s from {@link UserInput}s.
 * Internally the extraction is done in parallel by {@link ExtractorAgent}s that are instantiated for each {@link UserInput} to support higher loads.
 */
public class CaptionTokenExtractor {

    /**
     * Agent that extracts the {@link CaptionToken}s  from a {@link UserInput} and wraps the result in a {@link ExtractorResult}.
     */
    private class ExtractorAgent implements Callable<ExtractorResult> {

        /**
         * The {@link UserInput}  that is processed by this {@link ExtractorAgent}
         */
        private UserInput userInput;

        public ExtractorAgent(UserInput userInput) throws IOException, JWNLException {
            this.userInput = userInput;
        }

        /**
         * Creates a JCas that holds the {@link UserInput} as document text.
         *
         * @param userInput the input from a user wrapped in a {@link UserInput}
         * @return the JCas that holds the {@link UserInput} as document text.
         * @throws ResourceInitializationException
         */
        private JCas createJCasFromUserInput(@NotNull UserInput userInput) throws ResourceInitializationException, IOException, JWNLException {
            // create JCas containing userInput from the coreExtractorEngine
            JCas userInputJCas = CaptionTokenExtractor.getInstance().coreExtractorEngine.newJCas();
            userInputJCas.setDocumentText(userInput.getValue());
            return userInputJCas;
        }

        /**
         * The main extraction happens here!
         *
         * @return the result wrapped in a {@link ExtractorResult}
         * @throws AnalysisEngineProcessException
         * @throws ResourceInitializationException
         */
        @Override
        public ExtractorResult call() throws AnalysisEngineProcessException, ResourceInitializationException, SenseInventoryException, IOException, JWNLException {
            // create userInputJCas
            JCas userInputJCas = createJCasFromUserInput(this.userInput);
            // process userInputJCas
            CaptionTokenExtractor.getInstance().coreExtractorEngine.process(userInputJCas);

            ExtractorResult extractorResult = new ExtractorResult();
            extractorResult.setUserInput(userInput);
            // get the CaptionTokenAnnotations
            for (CaptionTokenAnnotation cta : JCasUtil.select(userInputJCas, CaptionTokenAnnotation.class)) {
                // transform them into CaptionTokens
                CaptionToken ct = createCaptionToken(userInputJCas, cta);
                // and add them to the extractorResult
                extractorResult.addCaptionToken(ct);
            }

            return extractorResult;
        }

        /**
         * Creates a {@link CaptionToken} from a {@link CaptionTokenAnnotation} (which is a UIMA Type)
         *
         * @param cta a {@link CaptionTokenAnnotation} instance
         * @return a {@link CaptionToken} instance
         */
        @NotNull
        @Contract("_, _ -> new")
        private CaptionToken createCaptionToken(JCas userInputJCas, @NotNull CaptionTokenAnnotation cta) throws SenseInventoryException {
            List<String> posTags = Arrays.asList(cta.getPOSList().split(";"));
            List<String> tokens = Arrays.asList(cta.getTokenList().split(";"));

            // get UDContext and WordNetSense per sentence
            List<String> wNetSenses = new ArrayList<>();
            List<UDependency> udContext = new ArrayList<>();
            for (Sentence context : JCasUtil.select(userInputJCas, Sentence.class)) {
                // only take the parent sentence into account
                if (cta.getBegin() >= context.getBegin() && cta.getEnd() <= context.getEnd()) {
                    udContext = getUDContext(tokens, userInputJCas, context);
                    wNetSenses = getWNetSenses(cta, userInputJCas, context);
                }
            }

            return new CaptionToken(cta.getValue(), CaptionToken.Type.valueOf(cta.getTypeOf().toUpperCase()), posTags, tokens, udContext, wNetSenses);
        }

        private List<UDependency> getUDContext(@NotNull List<String> tokens, JCas userInputJCas, Sentence s) {
            // the target token is always the last noun of the caption token since all tokens before are modifiers
            String targetToken = tokens.get(tokens.size() - 1);
            List<UDependency> context = new ArrayList<>();
            for (Dependency d : JCasUtil.selectCovered(userInputJCas, Dependency.class, s))
                if (d.getGovernor().getCoveredText().equals(targetToken)
                        || d.getDependent().getCoveredText().equals(targetToken)
                        || d.getCoveredText().equals(targetToken) && !d.getDependencyType().equals("punct"))
                    context.add(new UDependency(d.getDependencyType(), d.getGovernor().getCoveredText(), d.getDependent().getCoveredText()));

            return context;
        }

        private List<String> getWNetSenses(CaptionTokenAnnotation cta, JCas userInputJCas, @NotNull Sentence context) throws SenseInventoryException {
            List<String> wNetSenses = new ArrayList<>();
            // get the lemma of the target token to get the WNetSense
            List<Lemma> lemmas = JCasUtil.selectCovered(userInputJCas, Lemma.class, cta);
            String targetLemma = lemmas.get(lemmas.size() - 1).getValue();
            Map<String, Double> disambiguation = simplifiedExtendedLesk.getDisambiguation(targetLemma, POS.NOUN, context.getCoveredText());

            if (disambiguation != null && !disambiguation.isEmpty()) {
                // sort the map by the Lesk Score
                Map<String, Double> sortedDisambiguation = disambiguation.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

                // get highest score senses and resolve description with inventory
                Iterator<Map.Entry<String, Double>> senseIt = sortedDisambiguation.entrySet().iterator();
                Double highestScore = Double.MIN_VALUE;
                while (senseIt.hasNext()) {
                    Map.Entry<String, Double> currentSense = senseIt.next();
                    if (currentSense.getValue().compareTo(highestScore) >= 0) {
                        highestScore = currentSense.getValue();
                        wNetSenses.add(senseInventory.getSenseDescription(currentSense.getKey()));
                    } else
                        break;
                }
            }
            return wNetSenses;
        }
    }

    private static final int MAX_PARALLEL_THREADS = 16;
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
     * The SimplifiedExtendedLesk Algorithm to perform WSD
     */
    SimplifiedExtendedLesk simplifiedExtendedLesk;
    /**
     * The WordNetSynsetSenseInventory for the SimplifiedExtendedLesk algorithm
     */
    WordNetSynsetSenseInventory senseInventory;
    /**
     * singleton instance
     */
    private static CaptionTokenExtractor _singleton;

    private CaptionTokenExtractor() throws ResourceInitializationException, IOException, JWNLException {
        //build CoreExtractorEngine
        this.coreExtractorEngine = buildCoreExtractorEngine();
        //create a managed thread threadPool with fixed size
        this.threadPool = Executors.newFixedThreadPool(MAX_PARALLEL_THREADS);

        // Initialize the SenseInventory and file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        URL wnProperiesUrl = classLoader.getResource("WordNet-3.0/extjwnl_properties.xml");
        if (wnProperiesUrl == null)
            throw new IOException("Cannot read WordNet Files from resources!");
        this.senseInventory = new WordNetSynsetSenseInventory(wnProperiesUrl);

        // Initialize the Lesk algorithm
        this.simplifiedExtendedLesk = new SimplifiedExtendedLesk(senseInventory,
                new SetOverlap(),
                new NoNormalization(),
                new StringSplit(),
                new StringSplit()
        );
    }

    /**
     * Get the singleton instance of the {@link CaptionTokenExtractor}
     *
     * @return
     * @throws ResourceInitializationException
     */
    public static CaptionTokenExtractor getInstance() throws ResourceInitializationException, IOException, JWNLException {
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

        AnalysisEngineDescription seg = AnalysisEngineFactory.createEngineDescription(ClearNlpSegmenter.class,
                OpenNlpSegmenter.PARAM_LANGUAGE, LANGUAGE);
        aggregateBuilder.add(seg);

        AnalysisEngineDescription pos = AnalysisEngineFactory.createEngineDescription(ClearNlpPosTagger.class,
                ClearNlpPosTagger.PARAM_LANGUAGE, LANGUAGE);
        aggregateBuilder.add(pos);

        AnalysisEngineDescription lemma = AnalysisEngineFactory.createEngineDescription(ClearNlpLemmatizer.class,
                ClearNlpLemmatizer.PARAM_LANGUAGE, LANGUAGE);
        aggregateBuilder.add(lemma);

        AnalysisEngineDescription parse = AnalysisEngineFactory.createEngineDescription(MaltParser.class,
                MaltParser.PARAM_LANGUAGE, LANGUAGE,
                MaltParser.PARAM_VARIANT, "poly");
        aggregateBuilder.add(parse);

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
     * @return a {@link Future} that holds the {@link ExtractorResult}
     */
    public Future<ExtractorResult> startExtractionOfCaptionTokens(UserInput userInput) throws IOException, JWNLException {
        return this.threadPool.submit(new ExtractorAgent(userInput));
    }

}
