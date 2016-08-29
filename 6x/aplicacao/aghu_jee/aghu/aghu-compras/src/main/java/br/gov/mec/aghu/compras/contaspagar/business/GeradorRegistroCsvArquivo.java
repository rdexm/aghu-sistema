package br.gov.mec.aghu.compras.contaspagar.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;

import br.gov.mec.aghu.compras.contaspagar.interfaces.IRegistroCsv;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class GeradorRegistroCsvArquivo implements GeradorRegistroCsv {

	private static final String STRING_TEMPLATE_ATTRIBUTE_NAME_EQ_ARGS = "args";


	protected static final String STRING_TEMPLATE_GROUP_NAME = "arquivo_csv_txt";
	protected static final String STRING_TEMPLATE_PARENT_DIR = "stringtemplates";
	protected static final String STRING_TEMPLATE_GROUP_NAME_FILE = STRING_TEMPLATE_GROUP_NAME + ".stg";
	protected static final String STRING_TEMPLATE_PATH = STRING_TEMPLATE_PARENT_DIR + "/" + STRING_TEMPLATE_GROUP_NAME_FILE;

	private static GeradorRegistroCsvArquivo instance = new GeradorRegistroCsvArquivo();
	private static StringTemplateGroup stGroup = null;
	
	
	protected static Reader getReaderFromContext() {

		InputStream in = null;
		InputStreamReader result = null;

		in = Thread.currentThread().getContextClassLoader().getResourceAsStream(STRING_TEMPLATE_PATH);
		result = new InputStreamReader(in);

		return result;
	}

	protected static Reader getReaderFromFile()
			throws FileNotFoundException {

		FileReader reader = null;
		String path = null;

		path = System.getProperty("user.dir") + File.separatorChar
				+ "jar" + File.separatorChar
				+ "src" + File.separatorChar
				+ "main" + File.separatorChar				
				+ "resources" + File.separatorChar
				+ STRING_TEMPLATE_PARENT_DIR + File.separatorChar
				+ STRING_TEMPLATE_GROUP_NAME_FILE;
		reader = new FileReader(path);

		return reader;
	}

	protected static Reader getReader()
			throws FileNotFoundException {

		Reader result = null;

		try {
			result = getReaderFromContext();
		} catch (Exception e) {
			result = getReaderFromFile();
		}

		return result;
	}
	
	protected static StringTemplateGroup getStGroup()
			throws FileNotFoundException {

		Reader reader = null;

		if (stGroup == null) {
			reader = getReader();
			stGroup = new StringTemplateGroup(reader, AngleBracketTemplateLexer.class);
		}

		return stGroup;
	}



	protected static String obterString(final IRegistroCsv reg)
			throws FileNotFoundException {

		String result = null;
		StringTemplateGroup stg = null;
		StringTemplate st = null;
		
		stg = getStGroup();
		
		if (reg != null) {
			st = stg.getInstanceOf(reg.obterNomeTemplate());
			st.setAttribute(STRING_TEMPLATE_ATTRIBUTE_NAME_EQ_ARGS, reg.obterRegistrosComoLista());
			result = st.toString();
		}

		return result;
	}
		
	protected static String internObterRegistroFormatado(final IRegistroCsv registro)
			throws FileNotFoundException {
	
		String result = null;
	
		if (registro == null) {
			throw new IllegalArgumentException("Parametro registro nao informado!!!");
		}
		result = obterString(registro);
	
		return result;
	}

	private GeradorRegistroCsvArquivo() {

		super();
	}

	@Override
	public String obterRegistroFormatado(final IRegistroCsv registro)
			throws FileNotFoundException,
				ApplicationBusinessException,
				IOException {

		return internObterRegistroFormatado(registro);
	}

	public static GeradorRegistroCsvArquivo getInstance() {	
		return instance;
	}
}