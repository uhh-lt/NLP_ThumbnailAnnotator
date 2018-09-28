package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor;

import de.tudarmstadt.ukp.dkpro.wsd.lesk.algorithm.SimplifiedExtendedLesk;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.normalization.NoNormalization;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.overlap.SetOverlap;
import de.tudarmstadt.ukp.dkpro.wsd.lesk.util.tokenization.StringSplit;
import de.tudarmstadt.ukp.dkpro.wsd.si.POS;
import de.tudarmstadt.ukp.dkpro.wsd.si.SenseInventoryException;
import de.tudarmstadt.ukp.dkpro.wsd.si.wordnet.WordNetSynsetSenseInventory;
import net.sf.extjwnl.JWNLException;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.consumer.CaptionTokenExtractorDebugConsolePrinter;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.reader.LeipzigCorporaReader;
import nlp.floschne.thumbnailAnnotator.core.domain.CaptionToken;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractionResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CaptionTokenExtractorTests {


    /**
     * In this test all implemented components are used. If it runs without an Exception or Error it's considered to run correctly.
     *
     * @throws UIMAException
     * @throws IOException
     */
    @Test
    public void highLevelFunctionalityTest() throws UIMAException, IOException {
        CaptionTokenExtractor cte = CaptionTokenExtractor.getInstance();

        AnalysisEngine cteAnalysisEngine = cte.buildCoreExtractorEngine();

        CollectionReader reader = CollectionReaderFactory.createReader(LeipzigCorporaReader.class,
                LeipzigCorporaReader.PARAM_CORPORA_LANGUAGES, "EN",
                LeipzigCorporaReader.PARAM_LOAD_NEWS_CORPORA, Boolean.FALSE,
                LeipzigCorporaReader.PARAM_LOAD_WIKI_CORPORA, Boolean.TRUE,
                LeipzigCorporaReader.PARAM_NUM_SENTENCES, 50);


        AnalysisEngine debugPrinter = AnalysisEngineFactory.createEngine(CaptionTokenExtractorDebugConsolePrinter.class,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_SENTENCE, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_CAPTION_TOKEN, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_POS, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_PEFT, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_NE, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_POS_VIEW, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_VIEW_MAPPING_TOKEN, Boolean.TRUE);


        SimplePipeline.runPipeline(reader, cteAnalysisEngine, debugPrinter);
    }

    @Test
    public void dummyLeskFunctionallityTest() throws IOException, JWNLException, SenseInventoryException {

        WordNetSynsetSenseInventory si = new WordNetSynsetSenseInventory(new URL("file:///home/p0w3r/Downloads/WordNet-3.0/extjwnl_properties.xml"));

        SimplifiedExtendedLesk l = new SimplifiedExtendedLesk(si,
                new SetOverlap(),
                new NoNormalization(),
                new StringSplit(),
                new StringSplit()
        );


        l.getDisambiguation("dog", POS.NOUN, "I like my mouse and my dog.");
    }

    @Test
    public void complexCaptionTokenTest() throws UIMAException {
        String complexCaptionTokenText = "The red, broken and big car control system is great.";
        JCas cas = JCasFactory.createText(complexCaptionTokenText);

        CaptionTokenExtractor cte = CaptionTokenExtractor.getInstance();

        AnalysisEngine cteAnalysisEngine = cte.buildCoreExtractorEngine();


        AnalysisEngine debugPrinter = AnalysisEngineFactory.createEngine(CaptionTokenExtractorDebugConsolePrinter.class,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_SENTENCE, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_CAPTION_TOKEN, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_POS, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_PEFT, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_NE, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_POS_VIEW, Boolean.TRUE,
                CaptionTokenExtractorDebugConsolePrinter.PARAM_PRINT_VIEW_MAPPING_TOKEN, Boolean.TRUE);


        SimplePipeline.runPipeline(cas, cteAnalysisEngine, debugPrinter);

        JCas out = JCasUtil.getView(cas, "OUTPUT_CAS", false);


        Pattern complexCaptionTokenMatcher = Pattern.compile("CaptionTokens: red, broken and big car control system\\[Compound \\| JJ;,;JJ;CC;JJ;NN;NN;NN\\]");
        Matcher m = complexCaptionTokenMatcher.matcher(out.getDocumentText());

        assertTrue(m.find());
    }

    @Test
    public void getInstance() throws ResourceInitializationException {
        CaptionTokenExtractor cte1 = CaptionTokenExtractor.getInstance();
        assertNotNull(cte1);
        CaptionTokenExtractor cte2 = CaptionTokenExtractor.getInstance();
        assertTrue(cte1 == cte2);
    }

    @Test
    public void buildCoreExtractorEngine() throws ResourceInitializationException {
        AnalysisEngine engine = CaptionTokenExtractor.getInstance().buildCoreExtractorEngine();
        assertNotNull(engine);
    }

    @Test
    public void startExtractionOfCaptionTokens() throws ResourceInitializationException, ExecutionException, InterruptedException, IOException, JWNLException {
        UserInput input = new UserInput("Benazech is said to have been born in London about the year 1744.");
        Future<ExtractionResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        assertNotNull(extractionResultFuture);
        ExtractionResult extractionResult = extractionResultFuture.get();
        assertNotNull(extractionResult);
        assertEquals(input.getValue(), extractionResult.getUserInput().getValue());
        assertEquals(3, extractionResult.getCaptionTokens().size());
    }


    @Test
    public void dependencyContextTest() throws ResourceInitializationException, AnalysisEngineProcessException, ExecutionException, InterruptedException, IOException, JWNLException {

        UserInput input = new UserInput("I have a mouse and a keyboard.");
        Future<ExtractionResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        assertNotNull(extractionResultFuture);
        ExtractionResult extractionResult = extractionResultFuture.get();
        assertNotNull(extractionResult);
        assertEquals(input.getValue(), extractionResult.getUserInput().getValue());
        assertEquals(2, extractionResult.getCaptionTokens().size());

        for (CaptionToken t : extractionResult.getCaptionTokens()) {
            if (t.getValue().equals("mouse")) {
                assertEquals(4, t.getUdContext().size());
            } else if (t.getValue().equals("keyboard")) {
                assertEquals(2, t.getUdContext().size());
            }
        }
    }
}