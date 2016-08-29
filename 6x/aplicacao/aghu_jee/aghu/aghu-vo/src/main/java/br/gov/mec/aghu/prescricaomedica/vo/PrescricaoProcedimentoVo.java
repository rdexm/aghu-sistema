package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;

public class PrescricaoProcedimentoVo {

	private MbcProcedimentoCirurgicos pciSeq;
	private MpmProcedEspecialDiversos pedSeq;
	private ScoMaterial matCodigo;

	private DominioIndPendenteItemPrescricao indPendente;
	private Date dthrFim;
	
	private String justificativa;
	
	private Short quantidade;
	private String informacaoComplementar;
	private Short duracaoTratamentoSolicitado;
	
	public void setPciSeq(MbcProcedimentoCirurgicos pciSeq) {
		this.pciSeq = pciSeq;
	}

	public MbcProcedimentoCirurgicos getPciSeq() {
		return pciSeq;
	}

	public MpmProcedEspecialDiversos getPedSeq() {
		return pedSeq;
	}

	public void setPedSeq(MpmProcedEspecialDiversos pedSeq) {
		this.pedSeq = pedSeq;
	}

	public ScoMaterial getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(ScoMaterial matCodigo) {
		this.matCodigo = matCodigo;
	}

	public DominioIndPendenteItemPrescricao getIndPendente() {
		return indPendente;
	}

	public void setIndPendente(DominioIndPendenteItemPrescricao indPendente) {
		this.indPendente = indPendente;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	/**
	 * @param justificativa the justificativa to set
	 */
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	/**
	 * @return the justificativa
	 */
	public String getJustificativa() {
		return justificativa;
	}

	/**
	 * @param quantidade the quantidade to set
	 */
	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	/**
	 * @return the quantidade
	 */
	public Short getQuantidade() {
		return quantidade;
	}

	/**
	 * @param informacaoComplementar the informacaoComplementar to set
	 */
	public void setInformacaoComplementar(String informacaoComplementar) {
		this.informacaoComplementar = informacaoComplementar;
	}

	/**
	 * @return the informacaoComplementar
	 */
	public String getInformacaoComplementar() {
		return informacaoComplementar;
	}

	/**
	 * @param duracaoTratamentoSolicitado the duracaoTratamentoSolicitado to set
	 */
	public void setDuracaoTratamentoSolicitado(
			Short duracaoTratamentoSolicitado) {
		this.duracaoTratamentoSolicitado = duracaoTratamentoSolicitado;
	}

	/**
	 * @return the duracaoTratamentoSolicitado
	 */
	public Short getDuracaoTratamentoSolicitado() {
		return duracaoTratamentoSolicitado;
	}

}
