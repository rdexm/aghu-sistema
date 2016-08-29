package br.gov.mec.aghu.internacao.vo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.commons.BaseBean;



public class MedicamentoVO implements BaseBean{
	
	private static final long serialVersionUID = 7856268639293467535L;	
	
	private Integer matCodigo;
	private String descricaoEditada;
	private String descricaoMat;
	private BigDecimal concentracao;
	private String descricao;
	private Integer ummSeq;
	private String tprSigla;
	private Boolean indPadronizacao;
	private Short frequenciaUsual;
	private String tumSigla;
	private DominioSituacaoMedicamento indSituacao;
	private Short tfqSeq;
	private Boolean indExigeJustificativa;
	private Boolean indExigeDuracaoSolicitada;
	private Boolean indAntimicrobiano;
	private Boolean indExigeObservacao;
	private String descricaoUnidadeMedica;
	private String sinonimo;
	private AfaMedicamento medicamento;
	private String concentracaoEditada;

	public MedicamentoVO(){
		
	}

	public MedicamentoVO(Integer matCodigo, String descricaoEditada,
			String descricaoMat, BigDecimal concentracao, String descricao,
			Integer ummSeq, String tprSigla, Boolean indPadronizacao,
			Short frequenciaUsual, String tumSigla,
			DominioSituacaoMedicamento indSituacao, Short tfqSeq,
			Boolean indExigeJustificativa, Boolean indExigeDuracaoSolicitada,
			Boolean indAntimicrobiano, Boolean indExigeObservacao, String concentracaoEditada) {
		super();
		this.matCodigo = matCodigo;
		this.descricaoEditada = descricaoEditada;
		this.descricaoMat = descricaoMat;
		this.concentracao = concentracao;
		this.descricao = descricao;
		this.ummSeq = ummSeq;
		this.tprSigla = tprSigla;
		this.indPadronizacao = indPadronizacao;
		this.frequenciaUsual = frequenciaUsual;
		this.tumSigla = tumSigla;
		this.indSituacao = indSituacao;
		this.tfqSeq = tfqSeq;
		this.indExigeJustificativa = indExigeJustificativa;
		this.indExigeDuracaoSolicitada = indExigeDuracaoSolicitada;
		this.indAntimicrobiano = indAntimicrobiano;
		this.indExigeObservacao = indExigeObservacao;
		this.concentracaoEditada = concentracaoEditada;
		
	}

	public String getConcentracaoFormatada() {
		Locale locBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format;
        if(concentracao != null)
        {
        	format = new DecimalFormat("#,###,###,###,##0.####", dfSymbols);
        	return format.format(concentracao);
        }
		
        return null;
	}


	//GETTERs e SETTERs
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

	public String getDescricaoMat() {
		return descricaoMat;
	}

	public void setDescricaoMat(String descricaoMat) {
		this.descricaoMat = descricaoMat;
	}	

	public BigDecimal getConcentracao() {
		return concentracao;
	}

	public void setConcentracao(BigDecimal concentracao) {
		this.concentracao = concentracao;
		this.concentracaoEditada = getConcentracaoFormatada();
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getUmmSeq() {
		return ummSeq;
	}

	public void setUmmSeq(Integer ummSeq) {
		this.ummSeq = ummSeq;
	}

	public String getTprSigla() {
		return tprSigla;
	}

	public void setTprSigla(String tprSigla) {
		this.tprSigla = tprSigla;
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

	public String getDescricaoUnidadeMedica() {
		return descricaoUnidadeMedica;
	}

	public void setDescricaoUnidadeMedica(String descricaoUnidadeMedica) {
		this.descricaoUnidadeMedica = descricaoUnidadeMedica;
	}
	
	public Boolean getIndExigeObservacao() {
		return indExigeObservacao;
	}

	public void setIndExigeObservacao(Boolean indExigeObservacao) {
		this.indExigeObservacao = indExigeObservacao;
	}
	
	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public String getDescricaoMedicamento() {
		return (this.getMedicamento() != null)?this.getMedicamento().getDescricao():null;
	}
	
	public String getSinonimo() {
		return sinonimo;
	}

	public void setSinonimo(String sinonimo) {
		this.sinonimo = sinonimo;
	}
	
	public String getConcentracaoEditada() {
		return concentracaoEditada;
	}

	public void setConcentracaoEditada(String concentracaoEditada) {
		this.concentracaoEditada = concentracaoEditada;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.matCodigo);
		return builder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MedicamentoVO)) {
			return false;
		}
		
		MedicamentoVO other = (MedicamentoVO) obj;

		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.matCodigo, other.getMatCodigo());
		return builder.isEquals();
	}

	/**
	 * FIELDS
	 */
	public enum Fields {
		MAT_CODIGO("matCodigo"),
		MEDICAMENTO("medicamento"),
		DESCRICAO_EDITADA("descricaoEditada"),
		DESCRICAO_MAT("descricaoMat"),
		CONCENTRACAO("concentracao"),
		DESCRICAO("descricao"),
		UMM_SEQ("ummSeq"),
		TPR_SIGLA("tprSigla"),
		IND_PADRONIZACAO("indPadronizacao"),
		FREQUENCIA_USUAL("frequenciaUsual"),
		TUM_SIGLA("tumSigla"),
		IND_SITUACAO("indSituacao"),
		TFQ_SEQ("tfqSeq"),
		IND_EXIGE_JUSTIFICATIVA("indExigeJustificativa"),
		IND_EXIGE_DURACAO_SOLICITADA("indExigeDuracaoSolicitada"),
		IND_ANTIMICROBIANO("indAntimicrobiano"),
		IND_EXIGE_OBSERVACAO("indExigeObservacao"),
		DESCRICAO_UNIDADE_MEDICA("descricaoUnidadeMedica"),
		SINONIMO("sinonimo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}	
	
}
