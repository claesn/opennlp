package tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import utils.FileUtils;

public class NameFinderUtil {

	public static String[] getNames(String tokenizerModel, String nameModel, String inputFile) throws IOException {

		TokenNameFinderModel model = new TokenNameFinderModel(new File(nameModel));
		NameFinderME nameFinder = new NameFinderME(model);

		String[] tokens = SimpleTokenizer.INSTANCE.tokenize(FileUtils.readAsString(inputFile));
		Span nameSpans[] = nameFinder.find(tokens);
		List<String> names = new ArrayList<String>();

		for (Span span : nameSpans) {
			int start = span.getStart();
			int end = span.getEnd();
			String temp = "";
			for (int i = start; i < end; i++) {
				temp = temp + tokens[i];
			}
			names.add(temp);
		}
		return names.toArray(new String[names.size()]);
	}
}