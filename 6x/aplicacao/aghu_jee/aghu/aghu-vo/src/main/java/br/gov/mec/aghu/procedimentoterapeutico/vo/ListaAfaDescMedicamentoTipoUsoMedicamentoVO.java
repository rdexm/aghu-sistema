package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ListaAfaDescMedicamentoTipoUsoMedicamentoVO implements BaseBean {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2330247151253205688L;
	
	
	private Integer matCodigo;
	private String descricaoEditada;
//	private String descricaoMaterial;
	private BigDecimal concentracao;
	private String descricao;
	private Integer unidadeMedidaMedicamento;
	private String trpSigla;
	private Boolean indPadronizacao;
	private Short frequenciaUsual;
	private String tumSigla;
	private DominioSituacaoMedicamento indSituacao;
	private Short tfqSeq;
	private Boolean indExigeJustificativa;
	private Boolean indExigeDuracaoSolicitada;
	private Boolean indAntimicrobiano;
	private Boolean indExigeObservacao;
	private Boolean indQuimioterapico;	
	
	public ListaAfaDescMedicamentoTipoUsoMedicamentoVO() {
	}
	
//	public ListaAfaDescMedicamentoTipoUsoMedicamentoVO(Integer matCodigo, 
//			String descricaoEditada, String descricaoMaterial, 
//			BigDecimal concentracao, String descricao, 
//			MpmUnidadeMedidaMedica unidadeMedidaMedicamento, 
//			String trpSigla, Boolean indPadronizacao, 
//			Short frequenciaUsual, String tumSigla, 
//			DominioSituacaoMedicamento indSituacao,
//			Short tfqSeq, Boolean indExigeJustificativa, 
//			Boolean indExigeDuracaoSolicitada, 
//			Boolean indAntimicrobiano, Boolean indExigeObservacao, 
//			Boolean indQuimioterapico) {
//		this.matCodigo = matCodigo;
//		this.descricaoEditada = descricaoEditada;
//		this.descricaoMaterial = descricaoMaterial;
//		this.concentracao = concentracao;
//		this.descricao = descricao;
//		this.unidadeMedidaMedicamento = unidadeMedidaMedicamento;
//		this.trpSigla = trpSigla;
//		this.indPadronizacao = indPadronizacao;
//		this.frequenciaUsual = frequenciaUsual;
//		this.tumSigla = tumSigla;
//		this.indSituacao = indSituacao;
//		this.tfqSeq = tfqSeq;
//		this.indExigeJustificativa = indExigeJustificativa;
//		this.indExigeDuracaoSolicitada = indExigeDuracaoSolicitada;
//		this.indAntimicrobiano = indAntimicrobiano;
//		this.indExigeObservacao = indExigeObservacao;
//		this.indQuimioterapico = indQuimioterapico;
//	}
	
	public enum Fields {

		MAT_CODIGO("matCodigo"), 
		DESCRICAO_EDITADA("descricaoEditada"), 
//		DESCRICAO_MATERIAL("descricaoMaterial"), 
		CONCENTRACAO("concentracao"), 
		DESCRICAO("descricao"), 
		UNIDADE_MEDIDA_MEDICAMENTO("unidadeMedidaMedicamento"), 
		TRP_SIGLA("trpSigla"), 
		IND_PADRONIZACAO("indPadronizacao"), 
		FREQUENCIA_USUAL("frequenciaUsual"), 
		TUM_SIGLA("tumSigla"), 
		IND_SITUACAO("indSituacao"), 
		TFQ_SEQ("tfqSeq"), 
		IND_EXIGE_JUSTIFICATIVA("indExigeJustificativa"), 
		IND_EXIGE_DURACAO_SOLICITADA("indExigeDuracaoSolicitada"), 
		IND_ANTIMICROBIANO("indAntimicrobiano"), 
		IND_EXIGE_OBSERVACAO("indExigeObservacao"), 
		IND_QUIMIOTERAPICO("indQuimioterapico");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getDescricaoEditada() {
		return descricaoEditada;
	}

	public void setDescricaoEditada(String descricaoEditada) {
		this.descricaoEditada = descricaoEditada;
	}

//	public String getDescricaoMaterial() {
//		return descricaoMaterial;
//	}
//
//	public void setDescricaoMaterial(String descricaoMaterial) {
//		this.descricaoMaterial = descricaoMaterial;
//	}

	public BigDecimal getConcentracao() {
		return concentracao;
	}

	public void setConcentracao(BigDecimal concentracao) {
		this.concentracao = concentracao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getUnidadeMedidaMedicamento() {
		return unidadeMedidaMedicamento;
	}

	public void setUnidadeMedidaMedicamento(Integer unidadeMedidaMedicamento) {
		this.unidadeMedidaMedicamento = unidadeMedidaMedicamento;
	}

	public String getTrpSigla() {
		return trpSigla;
	}

	public void setTrpSigla(String trpSigla) {
		this.trpSigla = trpSigla;
	}

	public Boolean getIndPadronizacao() {
		return indPadronizacao;
	}

	public void setIndPadronizacao(Boolean indPadronizacao) {
		this.indPadronizacao = indPadronizacao;
	}

	public Short getFrequenciaUsual() {
		return frequenciaUsual;
	}

	public void setFrequenciaUsual(Short frequenciaUsual) {
		this.frequenciaUsual = frequenciaUsual;
	}

	public String getTumSigla() {
		return tumSigla;
	}

	public void setTumSigla(String tumSigla) {
		this.tumSigla = tumSigla;
	}

	public DominioSituacaoMedicamento getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoMedicamento indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getTfqSeq() {
		return tfqSeq;
	}

	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}

	public Boolean getIndExigeJustificativa() {
		return indExigeJustificativa;
	}

	public void setIndExigeJustificativa(Boolean indExigeJustificativa) {
		this.indExigeJustificativa = indExigeJustificativa;
	}

	public Boolean getIndExigeDuracaoSolicitada() {
		return indExigeDuracaoSolicitada;
	}

	public void setIndExigeDuracaoSolicitada(Boolean indExigeDuracaoSolicitada) {
		this.indExigeDuracaoSolicitada = indExigeDuracaoSolicitada;
	}

	public Boolean getIndAntimicrobiano() {
		return indAntimicrobiano;
	}

	public void setIndAntimicrobiano(Boolean indAntimicrobiano) {
		this.indAntimicrobiano = indAntimicrobiano;
	}

	public Boolean getIndExigeObservacao() {
		return indExigeObservacao;
	}

	public void setIndExigeObservacao(Boolean indExigeObservacao) {
		this.indExigeObservacao = indExigeObservacao;
	}

	public Boolean getIndQuimioterapico() {
		return indQuimioterapico;
	}

	public void setIndQuimioterapico(Boolean indQuimioterapico) {
		this.indQuimioterapico = indQuimioterapico;
	}

}
