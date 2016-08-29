package br.gov.mec.aghu.compras.contaspagar.business;

import java.io.FileNotFoundException;
import java.io.IOException;

import br.gov.mec.aghu.compras.contaspagar.interfaces.IRegistroCsv;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface GeradorRegistroCsv {

	public abstract String obterRegistroFormatado(IRegistroCsv registro)
			throws FileNotFoundException, ApplicationBusinessException,
			IOException;
}
