package nlp.floschne.thumbnailAnnotator.core.captionTokenExtractor.reader;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LeipzigCorporaReader extends JCasCollectionReader_ImplBase {

    private static final String GERMAN_WIKI_SENTENCES_CORPORA_NAME = "deu_wikipedia_2016_10K";
    private static final String GERMAN_NEWS_SENTENCES_CORPORA_NAME = "deu_news_2015_10K";

    private static final String ENGLISH_WIKI_SENTENCES_CORPORA_NAME = "eng_wikipedia_2016_10K";
    private static final String ENGLISH_NEWS_SENTENCES_CORPORA_NAME = "eng_news_2016_10K";

    private static final String SENTENCES_CORPORA_SUFFIX = "-sentences.txt";

    private Logger logger = null;

    public static final String PARAM_LOAD_NEWS_CORPORA = "LoadNewsCorpora";
    @ConfigurationParameter(name = PARAM_LOAD_NEWS_CORPORA, description = "Boolean flag. If true News corpora should be loaded and false if not.")
    private Boolean loadNewsCorpora;

    public static final String PARAM_LOAD_WIKI_CORPORA = "LoadWikiCorpora";
    @ConfigurationParameter(name = PARAM_LOAD_WIKI_CORPORA, description = "Boolean flag. If true Wikipedia corpora should be loaded and false if not.")
    private Boolean loadWikiCorpora;

    public static final String PARAM_CORPORA_LANGUAGES = "ListOfLanguages";
    @ConfigurationParameter(name = PARAM_CORPORA_LANGUAGES, description = "A comma-separated list of language (codes {EN, DE}) of the corpora that will be read", mandatory = false, defaultValue = "DE")
    private String corporaLanguages;

    public static final String PARAM_NUM_SENTENCES = "NumberOfSentences";
    @ConfigurationParameter(name = PARAM_NUM_SENTENCES, description = "The total number of sentences from the corpora that will be read", mandatory = false, defaultValue = "10")
    private Integer numSentences;


    private List<String> sentencesCorporaFilesNames;
    private Integer currentSentencesCorporaIdx;


    /**
     * This method should be overwritten by subclasses.
     *
     * @param context the UIMA context the component is running in
     * @throws ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);

        if (!loadWikiCorpora && !loadNewsCorpora)
            throw new IllegalArgumentException("Either PARAM_LOAD_NEWS_CORPORA, PARAM_LOAD_WIKI_CORPORA or both must be true!");

        logger = context.getLogger();

        currentSentencesCorporaIdx = 0;

        sentencesCorporaFilesNames = generateCorporaFileNames(loadNewsCorpora, loadWikiCorpora);
        StringBuilder sb = new StringBuilder();
        sb.append("Ready to process the following corpora: \n");
        for (String c : sentencesCorporaFilesNames)
            sb.append("\t").append(c).append("\n");
        logger.log(Level.INFO, sb.toString());
    }

    /**
     * Generates the filenames of the corpora that should be read with the Reader
     *
     * @param loadNewsCorpora
     * @param loadWikiCorpora
     * @return
     */
    private List<String> generateCorporaFileNames(Boolean loadNewsCorpora, Boolean loadWikiCorpora) {
        List<String> corporaFileNames = new ArrayList<>();
        String corporaFileName = null;
        for (String langCode : corporaLanguages.split(",")) {
            switch (langCode.trim().toUpperCase()) {
                case "DE":
                    if (loadWikiCorpora) {
                        corporaFileName = GERMAN_WIKI_SENTENCES_CORPORA_NAME + "/" + GERMAN_WIKI_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    if (loadNewsCorpora) {
                        corporaFileName = GERMAN_NEWS_SENTENCES_CORPORA_NAME + "/" + GERMAN_NEWS_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    break;
                case "EN":
                    if (loadWikiCorpora) {
                        corporaFileName = ENGLISH_WIKI_SENTENCES_CORPORA_NAME + "/" + ENGLISH_WIKI_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    if (loadNewsCorpora) {
                        corporaFileName = ENGLISH_NEWS_SENTENCES_CORPORA_NAME + "/" + ENGLISH_NEWS_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Language Code '" + langCode.trim().toUpperCase() + "' is not supported! Supported: {EN, DE}");
            }
        }
        return corporaFileNames;
    }

    /**
     * Subclasses should implement this method rather than {@link #getNext(CAS)}
     *
     * @param jCas the {@link JCas} to store the read data to
     * @throws IOException         if there was a low-level I/O problem
     * @throws CollectionException if there was another problem
     */
    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        /*
            Reads the corpora with name corporaFileName, removes the line number, concatenates every sentence,
             shuffles the sentences and finally sets all the sentences as the jCas document
         */
        String corporaFileName = sentencesCorporaFilesNames.get(currentSentencesCorporaIdx++);

        // Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileUrl = classLoader.getResource(corporaFileName);
        if (fileUrl == null)
            throw new IOException("Cannot read " + corporaFileName + " from resources!");
        File file = new File(fileUrl.getFile());
        BufferedReader reader = new BufferedReader(new FileReader(file));

        List<String> sentences = new ArrayList<>();
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null && sentences.size() < this.numSentences) {
            String[] sentence;
            sentence = line.split("^\\d*\\s*"); //removes the line number and only keeps the sentence
            assert sentence.length == 2; // has to contain only two elements: an empty string (from split) and the sentence itself
            assert sentence[0].equals(""); //
            sentences.add(sentence[1].concat("\n")); // add the newline again
        }
        // TODO shuffle the lines not sentences!
        int seed = 42;
        Collections.shuffle(sentences, new Random(seed)); // shuffle the sentences
        for (String sentence : sentences)
            sb.append(sentence); // concatenate the shuffled sentences
        jCas.setDocumentText(sb.toString());
    }


    /**
     * Gets whether there are any elements remaining to be read from this
     * <code>CollectionReader</code>.
     *
     * @return true if and only if there are more elements available from this
     * <code>CollectionReader</code>.
     * @throws IOException         if an I/O failure occurs
     * @throws CollectionException if there is some other problem with reading from the Collection
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return currentSentencesCorporaIdx < sentencesCorporaFilesNames.size();
    }

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        return new Progress[]{new ProgressImpl(currentSentencesCorporaIdx, sentencesCorporaFilesNames.size(), Progress.ENTITIES)};
    }
}

