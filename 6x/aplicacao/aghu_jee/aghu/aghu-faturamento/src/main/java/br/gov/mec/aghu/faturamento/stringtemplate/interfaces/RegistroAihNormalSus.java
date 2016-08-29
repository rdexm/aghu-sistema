package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import java.util.List;

/**
 * Cobre os registros AIH do tipo: 01, 03 e 05
 * @author gandriotti
 *
 */
public interface RegistroAihNormalSus
		extends
			AgrupamentoRegistroAih {

	public abstract RegistroAihComum getComum();

	public abstract RegistroAihEspecificacao getEspecificacao();

	public abstract RegistroAihIdentificacaoPaciente getIdentificacaoPaciente();

	public abstract RegistroAihEnderecoPaciente getEnderecoPaciente();

	public abstract RegistroAihInternacao getInternacao();

	public abstract List<RegistroAihProcedimentosSecundariosEspeciais> getProcedimentosSecundariosEspeciais();

	public abstract RegistroAihUtiNeonatal getUtiNeonatal();

	public abstract RegistroAihAcidenteTrabalho getAcidenteTrabalho();

	public abstract RegistroAihParto getParto();

	public abstract RegistroAihDocumentoPaciente getDocumentoPaciente();
}
