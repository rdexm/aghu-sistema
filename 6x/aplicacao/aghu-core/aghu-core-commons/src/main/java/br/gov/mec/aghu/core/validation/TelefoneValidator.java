package br.gov.mec.aghu.core.validation;

import java.util.regex.Pattern;

public class TelefoneValidator extends AbstractStringValidator<Telefone> {

	private Pattern pattern;

	@Override
	public void initialize(Telefone value) {
		pattern = Pattern.compile(value.regexPattern());
	}

	@Override
	public boolean validate(String value) {
		return pattern.matcher(value).matches();
	}
	
}