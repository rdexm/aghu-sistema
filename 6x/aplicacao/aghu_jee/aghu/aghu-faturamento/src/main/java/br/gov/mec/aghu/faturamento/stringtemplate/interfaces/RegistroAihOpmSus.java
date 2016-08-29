package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.util.List;

/**
 * AIH 07
 * @author gandriotti
 *
 */
public interface RegistroAihOpmSus
		extends
			AgrupamentoRegistroAih {

	public abstract RegistroAihComum getComum();

	public abstract List<RegistroAihDadosOpm> getDadosOpm();
}
