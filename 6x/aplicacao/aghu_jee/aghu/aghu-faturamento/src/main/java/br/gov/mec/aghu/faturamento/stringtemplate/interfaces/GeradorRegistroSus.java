package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface GeradorRegistroSus {

	public abstract String obterRegistroAihNormalFormatado(
			RegistroAihNormalSus registro)
			throws FileNotFoundException,
				ApplicationBusinessException,
				IOException;

	public abstract String obterRegistroAihRegCivilFormatado(
			RegistroAihRegCivilSus registro)
			throws FileNotFoundException,
			ApplicationBusinessException,
				IOException;

	public abstract String obterRegistroAihOpmFormatado(
			RegistroAihOpmSus registro)
			throws FileNotFoundException,
			ApplicationBusinessException,
				IOException;

	public abstract String obterRegistroAihFormatado(
			AgrupamentoRegistroAih registro)
			throws FileNotFoundException,
			ApplicationBusinessException,
				IOException;
}
