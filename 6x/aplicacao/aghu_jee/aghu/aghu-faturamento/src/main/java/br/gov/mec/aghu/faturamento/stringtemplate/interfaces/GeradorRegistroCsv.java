package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface GeradorRegistroCsv {

	public abstract String obterRegistroFormatado(
			RegistroCsv registro)
			throws FileNotFoundException,
				ApplicationBusinessException,
				IOException;
}
