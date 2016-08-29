package br.gov.mec.aghu.faturamento.business.exception;

import java.util.LinkedList;
import java.util.List;

import br.gov.mec.aghu.faturamento.stringtemplate.impl.AbstractAgrupamentoRegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAih;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihAcidenteTrabalho;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihComum;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihDocumentoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEnderecoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihEspecificacao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihIdentificacaoPaciente;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihInternacao;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihNormalSus;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihParto;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihProcedimentosSecundariosEspeciais;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroAihUtiNeonatal;

/**
 * AIHs 01, 03 e 05
 * @author gandriotti
 *
 */
public class RegistroAihNormalSusPadrao
		extends
			AbstractAgrupamentoRegistroAih
		implements
			RegistroAihNormalSus {

	private final RegistroAihComum comum;
	private final RegistroAihEspecificacao especificacao;
	private final RegistroAihIdentificacaoPaciente identificacaoPaciente;
	private final RegistroAihEnderecoPaciente enderecoPaciente;
	private final RegistroAihInternacao internacao;
	private final RegistroAihDocumentoPaciente documentoPaciente;
	private final List<RegistroAihProcedimentosSecundariosEspeciais> procedimentosSecundariosEspeciais;
	private final RegistroAihUtiNeonatal utiNeonatal;
	private final RegistroAihAcidenteTrabalho acidenteTrabalho;
	private final RegistroAihParto parto;

	public RegistroAihNormalSusPadrao(
			final RegistroAihComum comum,
			final RegistroAihEspecificacao especificacao,
			final RegistroAihIdentificacaoPaciente identificacaoPaciente,
			final RegistroAihEnderecoPaciente enderecoPaciente,
			final RegistroAihInternacao internacao,
			final RegistroAihDocumentoPaciente documentoPaciente,
			final List<RegistroAihProcedimentosSecundariosEspeciais> procedimentosSecundariosEspeciais,
			final RegistroAihUtiNeonatal utiNeonatal,
			final RegistroAihAcidenteTrabalho acidenteTrabalho,
			final RegistroAihParto parto) {

		super();

		//check args
		if (comum == null) {
			throw new IllegalArgumentException("Parametro comum nao informado!!!");
		}
		if (especificacao == null) {
			throw new IllegalArgumentException("Parametro especificacao nao informado!!!");
		}
		if (identificacaoPaciente == null) {
			throw new IllegalArgumentException("Parametro identificacaoPaciente nao informado!!!");
		}
		if (enderecoPaciente == null) {
			throw new IllegalArgumentException("Parametro enderecoPaciente nao informado!!!");
		}
		if (internacao == null) {
			throw new IllegalArgumentException("Parametro internacao nao informado!!!");
		}
		if (documentoPaciente == null) {
			throw new IllegalArgumentException("Parametro documentoPaciente nao informado!!!");
		}
		//algo
		this.comum = comum;
		this.especificacao = especificacao;
		this.identificacaoPaciente = identificacaoPaciente;
		this.enderecoPaciente = enderecoPaciente;
		this.internacao = internacao;
		this.documentoPaciente = documentoPaciente;
		this.procedimentosSecundariosEspeciais = procedimentosSecundariosEspeciais;
		this.utiNeonatal = utiNeonatal;
		this.acidenteTrabalho = acidenteTrabalho;
		this.parto = parto;
	}

	@Override
	public List<RegistroAih> obterListaRegistros() {

		List<RegistroAih> result = null;

		result = new LinkedList<RegistroAih>();
		result.add(this.getComum());
		result.add(this.getEspecificacao());
		result.add(this.getIdentificacaoPaciente());
		result.add(this.getEnderecoPaciente());
		result.add(this.getInternacao());
		result.addAll(this.getProcedimentosSecundariosEspeciais());
		result.add(this.getUtiNeonatal());
		result.add(this.getAcidenteTrabalho());
		result.add(this.getParto());
		result.add(this.getDocumentoPaciente());

		return result;
	}

	@Override
	public RegistroAihComum getComum() {

		return this.comum;
	}

	@Override
	public RegistroAihEspecificacao getEspecificacao() {

		return this.especificacao;
	}

	@Override
	public RegistroAihIdentificacaoPaciente getIdentificacaoPaciente() {

		return this.identificacaoPaciente;
	}

	@Override
	public RegistroAihEnderecoPaciente getEnderecoPaciente() {

		return this.enderecoPaciente;
	}

	@Override
	public RegistroAihInternacao getInternacao() {

		return this.internacao;
	}

	@Override
	public List<RegistroAihProcedimentosSecundariosEspeciais> getProcedimentosSecundariosEspeciais() {

		return this.procedimentosSecundariosEspeciais;
	}

	@Override
	public RegistroAihUtiNeonatal getUtiNeonatal() {

		return this.utiNeonatal;
	}

	@Override
	public RegistroAihAcidenteTrabalho getAcidenteTrabalho() {

		return this.acidenteTrabalho;
	}

	@Override
	public RegistroAihParto getParto() {

		return this.parto;
	}

	@Override
	public RegistroAihDocumentoPaciente getDocumentoPaciente() {

		return this.documentoPaciente;
	}
}
