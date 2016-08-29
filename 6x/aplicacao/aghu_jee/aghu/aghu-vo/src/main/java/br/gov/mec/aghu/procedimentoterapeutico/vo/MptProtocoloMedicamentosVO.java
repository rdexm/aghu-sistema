package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioUnidadeCorrer;
import br.gov.mec.aghu.core.commons.BaseBean;

public class MptProtocoloMedicamentosVO implements BaseBean {
			
	private static final long serialVersionUID = -5390709362184204302L;
	
	private Long seq;
	private Integer vpsSeq;
	private Short tfqSeq;
	private String descricao;
	private Boolean indInfusorPortatil;
	private Boolean indBombaInfusao;
	private BigDecimal gotejo;
	private Short qtdHorasCorrer;
	private DominioUnidadeCorrer unidHorasCorrer;
	private Short frequencia;
	private String complemento;
	private Short ordem;
	
	private String descricaoAprazamento;
	private Boolean indDigitaFrequencia;
	private Integer medMatCodigo;
	private BigDecimal dose;
	private String descricaoMedicamento;
	private String descricaoVia;
	private String aprazamentoFormatado;
	private Date tempo;
	private Long seqItemProtocoloMdtos;
	
	public Long getSeq() {
		return seq;
	}
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	public Integer getVpsSeq() {
		return vpsSeq;
	}
	public void setVpsSeq(Integer vpsSeq) {
		this.vpsSeq = vpsSeq;
	}
	public Short getTfqSeq() {
		return tfqSeq;
	}
	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Boolean getIndInfusorPortatil() {
		return indInfusorPortatil;
	}
	public void setIndInfusorPortatil(Boolean indInfusorPortatil) {
		this.indInfusorPortatil = indInfusorPortatil;
	}
	public Boolean getIndBombaInfusao() {
		return indBombaInfusao;
	}
	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}
	public BigDecimal getGotejo() {
		return gotejo;
	}
	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}
	public Short getQtdHorasCorrer() {
		return qtdHorasCorrer;
	}
	public void setQtdHorasCorrer(Short qtdHorasCorrer) {
		this.qtdHorasCorrer = qtdHorasCorrer;
	}
	public DominioUnidadeCorrer getUnidHorasCorrer() {
		return unidHorasCorrer;
	}
	public void setUnidHorasCorrer(DominioUnidadeCorrer unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}
	public Short getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public String getDescricaoAprazamento() {
		return descricaoAprazamento;
	}
	public void setDescricaoAprazamento(String descricaoAprazamento) {
		this.descricaoAprazamento = descricaoAprazamento;
	}
	public Boolean getIndDigitaFrequencia() {
		return indDigitaFrequencia;
	}
	public void setIndDigitaFrequencia(Boolean indDigitaFrequencia) {
		this.indDigitaFrequencia = indDigitaFrequencia;
	}
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}
	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}
	public BigDecimal getDose() {
		return dose;
	}
	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}
	public String getDescricaoMedicamento() {
		return descricaoMedicamento;
	}
	public void setDescricaoMedicamento(String descricaoMedicamento) {
		this.descricaoMedicamento = descricaoMedicamento;
	}
	public String getDescricaoVia() {
		return descricaoVia;
	}
	public void setDescricaoVia(String descricaoVia) {
		this.descricaoVia = descricaoVia;
	}
	public Date getTempo() {
		return tempo;
	}
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	public Long getSeqItemProtocoloMdtos() {
		return seqItemProtocoloMdtos;
	}
	public void setSeqItemProtocoloMdtos(Long seqItemProtocoloMdtos) {
		this.seqItemProtocoloMdtos = seqItemProtocoloMdtos;
	}
	public String getAprazamentoFormatado() {
		if(this.frequencia != null || this.descricaoAprazamento != null){
			this.aprazamentoFormatado = this.frequencia+" ".concat(this.descricaoAprazamento);
		}
		return aprazamentoFormatado;
	}
	



	public enum Fields {

		SEQ("seq"), 
		VPS_SEQ("vpsSeq"),
		DESCRICAO("descricao"),
		IND_INFUSOR_PORTATIL("indInfusorPortatil"),
		IND_BOMBA_INFUSAO("indBombaInfusao"),
		GOTEJO("gotejo"),
		QTDE_HORAS_CORRER("qtdHorasCorrer"),
		UNID_HORAS_CORRER("unidHorasCorrer"),
		FREQUENCIA("frequencia"),
		COMPLEMENTO("complemento"),
		ORDEM("ordem"),
		DESCRICAO_APRAZAMENTO("descricaoAprazamento"),
		IND_DIGITA_FREQUENCIA("indDigitaFrequencia"),
		MED_MAT_CODIGO("medMatCodigo"),
		DOSE("dose"),
		DESCRICAO_MEDICAMENTO("descricaoMedicamento"),
		DESCRICAO_VIA("descricaoVia"),
		TEMPO("tempo"),
		TFQ_SEQ("tfqSeq"),
		SEQ_ITEM_PROTOCOLO_MDTOS("seqItemProtocoloMdtos");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}
