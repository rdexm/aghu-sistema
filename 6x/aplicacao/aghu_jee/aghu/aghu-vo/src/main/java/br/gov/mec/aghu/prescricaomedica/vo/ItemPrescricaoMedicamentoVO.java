package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioTipoUsoMdtoAntimicrobia;

public class ItemPrescricaoMedicamentoVO {

	private Integer pmdAtdSeq;
	private Long pmdSeq;
	private Short seqp;
	private Integer fdsSeq;
	private Integer matCodigo;
	private DominioTipoUsoMdtoAntimicrobia usoMdtoAntimicrobia;
	private Short duracaoTratAprovado;
	private Double qtdeMgKg;
	private Double qtdeMgSuperfCorporal;
	private String observacao;
	private BigDecimal dose;
	private Boolean origemJustificativa;
	
	public ItemPrescricaoMedicamentoVO() {
		
	}
	
	public ItemPrescricaoMedicamentoVO(Integer pmdAtdSeq, Long pmdSeq,
			Short seqp, Integer fdsSeq, Integer matCodigo,
			DominioTipoUsoMdtoAntimicrobia usoMdtoAntimicrobia,
			Short duracaoTratAprovado, Double qtdeMgKg,
			Double qtdeMgSuperfCorporal, String observacao, BigDecimal dose) {
		super();
		this.pmdAtdSeq = pmdAtdSeq;
		this.pmdSeq = pmdSeq;
		this.seqp = seqp;
		this.fdsSeq = fdsSeq;
		this.matCodigo = matCodigo;
		this.usoMdtoAntimicrobia = usoMdtoAntimicrobia;
		this.duracaoTratAprovado = duracaoTratAprovado;
		this.qtdeMgKg = qtdeMgKg;
		this.qtdeMgSuperfCorporal = qtdeMgSuperfCorporal;
		this.observacao = observacao;
		this.dose = dose;
	}

	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}
	
	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}
	
	public Long getPmdSeq() {
		return pmdSeq;
	}
	
	public void setPmdSeq(Long pmdSeq) {
		this.pmdSeq = pmdSeq;
	}
	
	public Short getSeqp() {
		return seqp;
	}
	
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	public Integer getFdsSeq() {
		return fdsSeq;
	}
	
	public void setFdsSeq(Integer fdsSeq) {
		this.fdsSeq = fdsSeq;
	}
	
	public Integer getMatCodigo() {
		return matCodigo;
	}
	
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	
	public DominioTipoUsoMdtoAntimicrobia getUsoMdtoAntimicrobia() {
		return usoMdtoAntimicrobia;
	}
	
	public void setUsoMdtoAntimicrobia(
			DominioTipoUsoMdtoAntimicrobia usoMdtoAntimicrobia) {
		this.usoMdtoAntimicrobia = usoMdtoAntimicrobia;
	}
	
	public Short getDuracaoTratAprovado() {
		return duracaoTratAprovado;
	}
	
	public void setDuracaoTratAprovado(Short duracaoTratAprovado) {
		this.duracaoTratAprovado = duracaoTratAprovado;
	}
	
	public Double getQtdeMgKg() {
		return qtdeMgKg;
	}
	
	public void setQtdeMgKg(Double qtdeMgKg) {
		this.qtdeMgKg = qtdeMgKg;
	}
	
	public Double getQtdeMgSuperfCorporal() {
		return qtdeMgSuperfCorporal;
	}
	
	public void setQtdeMgSuperfCorporal(Double qtdeMgSuperfCorporal) {
		this.qtdeMgSuperfCorporal = qtdeMgSuperfCorporal;
	}
	
	public String getObservacao() {
		return observacao;
	}
	
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public BigDecimal getDose() {
		return dose;
	}
	
	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}
	
	public Boolean getOrigemJustificativa() {
		return origemJustificativa;
	}

	public void setOrigemJustificativa(Boolean origemJustificativa) {
		this.origemJustificativa = origemJustificativa;
	}



	public enum Fields {
		PMD_ATD_SEQ("pmdAtdSeq"), PMD_SEQ("pmdSeq"), SEQP("seqp"), MAT_CODIGO("matCodigo"),
		FDS_SEQ("fdsSeq"),USO_MDTO_ANTIMICROBIA("usoMdtoAntimicrobia"), DURACAOTRATAPROVADO("duracaoTratAprovado"),
		QTDEMGKG("qtdeMgKg"),QTDEMGSUPERFCORPORAL("qtdeMgSuperfCorporal"),OBSERVACAO("observacao"),DOSE("dose"),
		ORIGEMJUSTIFICATIVA("origemJustificativa");

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
