package tools;

import java.io.File;
import java.io.IOException;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.SimpleTokenizer;

public class Categorizer {

	private DocumentCategorizerME myCategorizer;

	public Categorizer(String modelFile) throws IOException {
		DoccatModel docCatModel = new DoccatModel(new File(modelFile));
		myCategorizer = new DocumentCategorizerME(docCatModel);
	}

	public String getBestCategory(String string) {
		String[] str = SimpleTokenizer.INSTANCE.tokenize(string);
		double[] outcomes = myCategorizer.categorize(str);
		String category = myCategorizer.getBestCategory(outcomes);
		return category;
	}

}