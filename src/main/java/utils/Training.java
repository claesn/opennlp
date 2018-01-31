package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.langdetect.LanguageDetectorFactory;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;
import opennlp.tools.langdetect.LanguageDetectorSampleStream;
import opennlp.tools.langdetect.LanguageSample;
import opennlp.tools.lemmatizer.LemmaSample;
import opennlp.tools.lemmatizer.LemmaSampleStream;
import opennlp.tools.lemmatizer.LemmatizerFactory;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.ml.perceptron.PerceptronTrainer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;

public class Training {

	public static void trainNERModel(String inputFile, String modelFile) throws IOException {
	
		ObjectStream<String> lineStream = lineStream(inputFile);
		ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
	
		TokenNameFinderModel model = NameFinderME.train("en", "person", sampleStream,
				TrainingParameters.defaultParams(), new TokenNameFinderFactory());
		model.serialize(new File(modelFile));
	}

	public static void trainLangDectModel(String inputFile, String modelFile) throws IOException {

		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("corpus.txt"));
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
		ObjectStream<LanguageSample> sampleStream = new LanguageDetectorSampleStream(lineStream);

		// Custom params:
		TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
		params.put(TrainingParameters.ALGORITHM_PARAM, PerceptronTrainer.PERCEPTRON_VALUE);
		params.put(TrainingParameters.CUTOFF_PARAM, 0);

		LanguageDetectorModel model = LanguageDetectorME.train(sampleStream, params, new LanguageDetectorFactory());
		model.serialize(new File("langdetect.bin"));
	}

	public static void trainLemmatizerModel(String inputFile, String modelFile) throws IOException {

		ObjectStream<LemmaSample> sampleStream = new LemmaSampleStream(lineStream(inputFile));
		LemmatizerModel model = LemmatizerME.train("en", sampleStream, TrainingParameters.defaultParams(),
				new LemmatizerFactory());
		model.serialize(new File("lemmatizer.bin"));
	}

	public static void trainCatModel(String inputFile, String modelFile) throws IOException {
	
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream(inputFile));
		DoccatModel model = DocumentCategorizerME.train("en", sampleStream, TrainingParameters.defaultParams(),
				new DoccatFactory());
		model.serialize(new File(modelFile));
	}

	private static ObjectStream<String> lineStream(String inputFile) throws FileNotFoundException, IOException {
		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(inputFile));
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
		return lineStream;
	}

}