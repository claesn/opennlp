package tools;

import java.io.File;
import java.io.IOException;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class TokenizerWrapper {
	TokenizerModel model;
	Tokenizer tokenizer;

	public TokenizerWrapper(String modelFile) throws IOException {
		model = new TokenizerModel(new File(modelFile));
		tokenizer = new TokenizerME(model);
	}

	public Tokenizer getTokenizer() {
		return tokenizer;
	}

	public Tokenizer getSimpleTokenizer() {
		return SimpleTokenizer.INSTANCE;
	}
	
	public Tokenizer getWhitespaceTokenizer() {
		return WhitespaceTokenizer.INSTANCE;
	}

	public String[] tokenize(String data) {
		return tokenizer.tokenize(data);
	}

	public String[] tokenizeWithWhiteSpaceTokenizer(String data) {
		return getWhitespaceTokenizer().tokenize(data);
	}
	
	public String[] tokenizeWithSimpleTokenizer(String data) {
		return getSimpleTokenizer().tokenize(data);
	}

}