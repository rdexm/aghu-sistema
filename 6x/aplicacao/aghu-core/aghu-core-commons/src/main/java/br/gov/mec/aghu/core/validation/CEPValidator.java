package br.gov.mec.aghu.core.validation;

import java.util.regex.Pattern;

public class CEPValidator extends AbstractStringValidator<CEP> {
    
    
    private Pattern pattern;

    @Override
    public void initialize(CEP Annotation) {
        pattern = Pattern.compile("[0-9]{8}");
    }

    @Override
    protected boolean validate(String value) {
    	
    	value = value.trim().replace("-", "");
    	
        return pattern.matcher(value).matches();
    }

}
