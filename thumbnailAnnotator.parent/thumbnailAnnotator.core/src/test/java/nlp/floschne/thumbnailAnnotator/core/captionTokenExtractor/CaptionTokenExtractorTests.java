package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor;

import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.consumer.CaptionTokenExtractorDebugConsolePrinter;
import nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.reader.LeipzigCorporaReader;
import nlp.floschne.thumbnailAnnotator.core.domain.ExtractionResult;
import nlp.floschne.thumbnailAnnotator.core.domain.UserInput;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class CaptionTokenExtractorTests {


    /**
     * In this test all implemented components are used. If it runs without an Exception or Error it's considered to run correctly.
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
    public void startExtractionOfCaptionTokens() throws ResourceInitializationException, ExecutionException, InterruptedException {
        UserInput input = new UserInput("Benazech is said to have been born in London about the year 1744.");
        Future<ExtractionResult> extractionResultFuture = CaptionTokenExtractor.getInstance().startExtractionOfCaptionTokens(input);
        assertNotNull(extractionResultFuture);
        ExtractionResult extractionResult = extractionResultFuture.get();
        assertNotNull(extractionResult);
        assertEquals(input.getValue(), extractionResult.getUserInput().getValue());
        assertEquals(4, extractionResult.getCaptionTokens().size());
    }
}