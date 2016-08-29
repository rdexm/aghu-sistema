package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.util.List;

/**
 * Cobre o registros AIH do tipo: 04
 * @author gandriotti
 *
 */
public interface RegistroAihRegCivilSus
		extends
			AgrupamentoRegistroAih {

	public abstract RegistroAihComum getComum();

	public abstract List<RegistroAihRegistroCivil> getRegistroCivil();
}
