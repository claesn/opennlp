package app;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetector;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import utils.FileUtils;
import utils.Training;

public class TestOpenNLP {

	private Logger logger = LoggerFactory.getLogger(TestOpenNLP.class);
	private static String data;
	private static String ner_data;
	private static String parse_data;

	// model files
	private String sentModel = "src/main/resources/models/en-sent.bin";
	private String posModel = "src/main/resources/models/en-pos-maxent.bin";
	private String chunkModel = "src/main/resources/models/en-chunker.bin";
	private String langDectModel = "src/main/resources/models/langdetect-183.bin";
	private String tokModel = "src/main/resources/models/en-token.bin";
	private String nerLoc = "src/main/resources/models/en-ner-location.bin";
	private String nerPers = "src/main/resources/models/en-ner-person.bin";
	private String parseModel = "src/main/resources/models/en-parser-chunking.bin";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		data = FileUtils.readAsString("src/main/resources/input/input.txt");
		ner_data = FileUtils.readAsString("src/main/resources/input/ner_input.txt");
		parse_data = FileUtils.readAsString("src/main/resources/input/parse_input.txt");
	}

	@Test
	public void testSentenceDetector() throws IOException {

		SentenceModel model = new SentenceModel(new File(sentModel));
		SentenceDetectorME detector = new SentenceDetectorME(model);

		// Detecting the sentence
		String sentences[] = detector.sentDetect(data);
		for (String sent : sentences)
			System.out.println(sent);

		// Detecting the position of the sentences in the raw text
		Span spans[] = detector.sentPosDetect(data);
		// Getting the probabilities of the last decoded sequence
		double[] probs = detector.getSentenceProbabilities();

		for (int i = 0; i < spans.length; i++)
			System.out.println(
					data.substring(spans[i].getStart(), spans[i].getEnd()) + "\t" + spans[i] + " \t" + probs[i]);
	}

	@Test
	public void testTokenizer() throws IOException {

		TokenizerModel tokenModel = new TokenizerModel(new File(tokModel));
		TokenizerME tokenizer = new TokenizerME(tokenModel);
		// Tokenizer tokenizer = SimpleTokenizer.INSTANCE;
		// Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;

		String[] tokens = tokenizer.tokenize(data);
		for (String a : tokens)
			System.out.println(a);

		Span[] spans = tokenizer.tokenizePos(data);
		double[] probs = tokenizer.getTokenProbabilities();
		for (int i = 0; i < probs.length; i++)
			System.out.println(
					spans[i] + " " + data.substring(spans[i].getStart(), spans[i].getEnd()) + " \t" + probs[i]);

	}

	@Test
	public void testNER() throws IOException {

		TokenNameFinderModel model = new TokenNameFinderModel(new File(nerLoc));// nerPers, etc
		NameFinderME nameFinder = new NameFinderME(model);
		// weitere models für location, time, money, organization

		SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel(new File(sentModel)));
		String[] sentences = detector.sentDetect(ner_data);// ner_data
		for (String sentence : sentences) {
			String[] tokens = SimpleTokenizer.INSTANCE.tokenize(sentence);
			Span[] nameSpans = nameFinder.find(tokens);

			System.out.println(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
			for (Span s : nameSpans)
				System.out.println(s.toString() + "  " + tokens[s.getStart()]);
		}
	}

	@Test
	public void testPOS() throws IOException {

		// POS siehe z.B. http://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html

		// Loading Parts of speech-maxent model
		POSModel model = new POSModel(new File(posModel));
		POSTaggerME tagger = new POSTaggerME(model);

		String[] tokens = SimpleTokenizer.INSTANCE.tokenize(data);
		String[] tags = tagger.tag(tokens);
		double[] probs = tagger.probs();
		for (int i = 0; i < tags.length; i++) {
			System.out.println(tokens[i] + "\t" + tags[i] + "\t" + probs[i]);
		}
		System.out.println();

		// Ergebnis mit POSSample class
		POSSample sample = new POSSample(tokens, tags);
		System.out.println(sample.toString());

		// Monitoring the performance
		PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
		perfMon.start();
		perfMon.incrementCounter();
		perfMon.stopAndPrintFinalResult();
	}

	@Test
	public void testParser() throws IOException {

		ParserModel model = new ParserModel(new File(parseModel));
		Parser parser = ParserFactory.create(model);

		SentenceDetectorME detector = new SentenceDetectorME(new SentenceModel(new File(sentModel)));
		String[] sentences = detector.sentDetect(data);// parse_data
		for (String sentence : sentences) {
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
			for (Parse p : topParses)
				p.show();
		}
	}

	@Test
	public void testChunker() throws IOException {

		String[] tokens = SimpleTokenizer.INSTANCE.tokenize(data);
		POSModel model = new POSModel(new File(posModel));
		POSTaggerME tagger = new POSTaggerME(model);
		String[] tags = tagger.tag(tokens);

		// Loading the chunker model
		ChunkerModel chunkerModel = new ChunkerModel(new File(chunkModel));
		ChunkerME chunkerME = new ChunkerME(chunkerModel);
		String result[] = chunkerME.chunk(tokens, tags);
		for (int i = 0; i < result.length; i++) {
			System.out.println(result[i] + "\t" + tags[i] + "\t" + tokens[i]);
		}
	}

	@Test
	public void testCategorizer() throws IOException {

		String inputFile = "src/main/resources/trainingData/categorizer.train";
		String modelFile = "src/main/resources/models/cat_train.bin";

		Training.trainCatModel(inputFile, modelFile);

		DoccatModel docCatModel = new DoccatModel(new File(modelFile));
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(docCatModel);

		String[] str = SimpleTokenizer.INSTANCE.tokenize("read books");
		double[] outcomes = myCategorizer.categorize(str);
		String category = myCategorizer.getBestCategory(outcomes);
		System.out.println();
		System.out.println("assignment: " + category);
	}

	@Test
	public void testLangDetection() throws IOException {

		String inputText = "Como estas, senor";

		LanguageDetectorModel m = new LanguageDetectorModel(new File(langDectModel));
		LanguageDetector myCategorizer = new LanguageDetectorME(m);

		// Get the most probable language
		Language bestLanguage = myCategorizer.predictLanguage(inputText);
		System.out.println("Best language: " + bestLanguage.getLang());
		System.out.println("Best language confidence: " + bestLanguage.getConfidence());

		// Get an array with the most probable languages
		Language[] languages = myCategorizer.predictLanguages(inputText);
		for (Language language : languages)
			System.out.println(language);
	}

}
