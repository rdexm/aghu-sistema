package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ProtocoloMedicamentoSolucaoCuidadoVO implements BaseBean {
	
	/**
	 * @author marcelo.deus
	 */
	private static final long serialVersionUID = -7059125065305714533L;
	
	private Long ptmSeq;
	private MptVersaoProtocoloSessao versaoProtocoloSessao;
	private Short tfqSeq;
	private String ptmDescricao;
	private Boolean indInfusorPortatil;
	private Boolean indBombaInfusao;
	private BigDecimal gotejo;
	private String descricaoAprazamento;
	private Boolean indDigitaFrequencia;
	private Integer medMatCodigo;
	private BigDecimal dose;
	private String descricaoMedicamento;
	private Short qtdHorasCorrer;
	private DominioUnidadeHorasMinutos unidadeHorasCorrer;
	private Integer frequenciaProtocoloCuidado;
	private String complemento;
	private Date tempo;
	private String descricaoVia;
	private String siglaVia;
	private Short ordem;
	private Integer pcuSeq;
	private MpmCuidadoUsual cuidadoUsual;
	private String descricaoCuidadoUsual;
	private Boolean indDigitaComplemento;
	private Short frequenciaCuidado;
	private Short tfqCuidado;
	private Integer vpsSeq;
	private Integer cduSeq;
	private Long seqItemProtocoloMdtos;
	private Short ptmFrequencia;
	private String aprazamentoFormatado;
	private Short tvaSeq;
	private String tfsSintaxe;
	private Boolean indUsoDomiciliar;
	private Short diasUsoDomiciliar;
	private Boolean indSeNecessario;
	private String descricaoVeloc;
	private String observacao;
	private Boolean indSolucao;
	private String styleCelulaDia;
	private Boolean renderizaAzul;
	private Integer medMatCodigoDiluente;
		
	public ProtocoloMedicamentoSolucaoCuidadoVO() {
		
	}
		
	public enum Fields {

		PTM_SEQ("ptmSeq"), 
		VPS_SEQ("vpsSeq"), 
		TFQ_SEQ("tfqSeq"), 
		PTM_DESCRICAO("ptmDescricao"), 
		IND_INFUSOR_PORTATIL("indInfusorPortatil"), 
		IND_BOMBA_INFUSAO("indBombaInfusao"), 
		DESCRICAO_APRAZAMENTO("descricaoAprazamento"), 
		IND_DIGITA_FREQUENCIA("indDigitaFrequencia"), 
		MED_MAT_CODIGO("medMatCodigo"), 
		DOSE("dose"), 
		DESCRICAO_MEDICAMENTO("descricaoMedicamento"), 
		QTD_HORAS_CORRER("qtdHorasCorrer"), 
		UNIDADE_HORAS_CORRER("unidadeHorasCorrer"), 
		FREQUENCIA_PROTOCOLO_CUIDADO("frequenciaProtocoloCuidado"), 
		COMPLEMENTO("complemento"), 
		TEMPO("tempo"), 
		DESCRICAO_VIA("descricaoVia"), 
		SIGLA_VIA("siglaVia"), 
		ORDEM("ordem"), 
		PCU_SEQ("pcuSeq"), 
		CDU_SEQ("cduSeq"), 
		DESCRICAO_CUIDADO_USUAL("descricaoCuidadoUsual"), 
		IND_DIGITA_COMPLEMENTO("indDigitaComplemento"), 
		FREQUENCIA_CUIDADO("frequenciaCuidado"), 
		TFQ_CUIDADO("tfqCuidado"),
		SEQ_ITEM_PROTOCOLO_MDTOS("seqItemProtocoloMdtos"),
		GOTEJO("gotejo"),
		PTM_FREQUENCIA("ptmFrequencia"),
		TVA_SEQ("tvaSeq"),
		TFS_SINTAXE("tfsSintaxe"),
		IND_USO_DOMICILIAR("indUsoDomiciliar"),
		DIAS_USO_DOMICILIAR("diasUsoDomiciliar"),
		IND_SE_NECESSARIO("indSeNecessario"),
		DESCRICAO_VELOC("descricaoVeloc"),
		OBSERVACAO("observacao"),
		IND_SOLUCAO("indSolucao"),
		MED_MAT_CODIGO_DILUENTE("medMatCodigoDiluente"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	public Long getPtmSeq() {
		return ptmSeq;
	}
	public void setPtmSeq(Long ptmSeq) {
		this.ptmSeq = ptmSeq;
	}	
	public MptVersaoProtocoloSessao getVersaoProtocoloSessao() {
		return versaoProtocoloSessao;
	}
	public void setVersaoProtocoloSessao(
			MptVersaoProtocoloSessao versaoProtocoloSessao) {
		this.versaoProtocoloSessao = versaoProtocoloSessao;
	}
	public Short getTfqSeq() {
		return tfqSeq;
	}
	public void setTfqSeq(Short tfqSeq) {
		this.tfqSeq = tfqSeq;
	}
	public String getPtmDescricao() {
		return ptmDescricao;
	}
	public void setPtmDescricao(String ptmDescricao) {
		this.ptmDescricao = ptmDescricao;
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
	public Short getQtdHorasCorrer() {
		return qtdHorasCorrer;
	}
	public void setQtdHorasCorrer(Short qtdHorasCorrer) {
		this.qtdHorasCorrer = qtdHorasCorrer;
	}
	public DominioUnidadeHorasMinutos getUnidadeHorasCorrer() {
		return unidadeHorasCorrer;
	}
	public void setUnidadeHorasCorrer(DominioUnidadeHorasMinutos unidadeHorasCorrer) {
		this.unidadeHorasCorrer = unidadeHorasCorrer;
	}
	public Integer getFrequenciaProtocoloCuidado() {
		return frequenciaProtocoloCuidado;
	}
	public void setFrequenciaProtocoloCuidado(Integer frequenciaProtocoloCuidado) {
		this.frequenciaProtocoloCuidado = frequenciaProtocoloCuidado;
	}

	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public Date getTempo() {
		return tempo;
	}
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	public String getDescricaoVia() {
		return descricaoVia;
	}
	public void setDescricaoVia(String descricaoVia) {
		this.descricaoVia = descricaoVia;
	}	
	public String getSiglaVia() {
		return siglaVia;
	}
	public void setSiglaVia(String siglaVia) {
		this.siglaVia = siglaVia;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public Integer getPcuSeq() {
		return pcuSeq;
	}
	public void setPcuSeq(Integer pcuSeq) {
		this.pcuSeq = pcuSeq;
	}	
	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}
	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}
	public String getDescricaoCuidadoUsual() {
		return descricaoCuidadoUsual;
	}
	public void setDescricaoCuidadoUsual(String descricaoCuidadoUsual) {
		this.descricaoCuidadoUsual = descricaoCuidadoUsual;
	}
	public Boolean getIndDigitaComplemento() {
		return indDigitaComplemento;
	}
	public void setIndDigitaComplemento(Boolean indDigitaComplemento) {
		this.indDigitaComplemento = indDigitaComplemento;
	}		
	public Short getFrequenciaCuidado() {
		return frequenciaCuidado;
	}
	public void setFrequenciaCuidado(Short frequenciaCuidado) {
		this.frequenciaCuidado = frequenciaCuidado;
	}
	public Short getTfqCuidado() {
		return tfqCuidado;
	}
	public void setTfqCuidado(Short tfqCuidado) {
		this.tfqCuidado = tfqCuidado;
	}
	public Integer getVpsSeq() {
		return vpsSeq;
	}
	public void setVpsSeq(Integer vpsSeq) {
		this.vpsSeq = vpsSeq;
	}
	public Integer getCduSeq() {
		return cduSeq;
	}
	public void setCduSeq(Integer cduSeq) {
		this.cduSeq = cduSeq;
	}
	public Long getSeqItemProtocoloMdtos() {
		return seqItemProtocoloMdtos;
	}
	public void setSeqItemProtocoloMdtos(Long seqItemProtocoloMdtos) {
		this.seqItemProtocoloMdtos = seqItemProtocoloMdtos;
	}
	public Short getPtmFrequencia() {
		return ptmFrequencia;
	}
	public void setPtmFrequencia(Short ptmFrequencia) {
		this.ptmFrequencia = ptmFrequencia;
	}
	public Short getTvaSeq() {
		return tvaSeq;
	}
	public void setTvaSeq(Short tvaSeq) {
		this.tvaSeq = tvaSeq;
	}
	public String getTfsSintaxe() {
		return tfsSintaxe;
	}
	public void setTfsSintaxe(String tfsSintaxe) {
		this.tfsSintaxe = tfsSintaxe;
	}
	public Boolean getIndUsoDomiciliar() {
		return indUsoDomiciliar;
	}
	public void setIndUsoDomiciliar(Boolean indUsoDomiciliar) {
		this.indUsoDomiciliar = indUsoDomiciliar;
	}
	public Short getDiasUsoDomiciliar() {
		return diasUsoDomiciliar;
	}
	public void setDiasUsoDomiciliar(Short diasUsoDomiciliar) {
		this.diasUsoDomiciliar = diasUsoDomiciliar;
	}
	public Boolean getIndSeNecessario() {
		return indSeNecessario;
	}
	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}	
	public String getDescricaoVeloc() {
		return descricaoVeloc;
	}
	public void setDescricaoVeloc(String descricaoVeloc) {
		this.descricaoVeloc = descricaoVeloc;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getAprazamentoFormatado() {
		if(this.ptmFrequencia != null && this.descricaoAprazamento != null){
			this.aprazamentoFormatado = this.ptmFrequencia+" ".concat(this.descricaoAprazamento);
		}else{
			this.aprazamentoFormatado = this.descricaoAprazamento;
		}
		return aprazamentoFormatado;
	}
	public Boolean getIndSolucao() {
		return indSolucao;
	}
	public void setIndSolucao(Boolean indSolucao) {
		this.indSolucao = indSolucao;
	}
	public String getStyleCelulaDia() {
		return styleCelulaDia;
	}
	public void setStyleCelulaDia(String styleCelulaDia) {
		this.styleCelulaDia = styleCelulaDia;
	}
	public Boolean getRenderizaAzul() {
		return renderizaAzul;
	}
	public void setRenderizaAzul(Boolean renderizaAzul) {
		this.renderizaAzul = renderizaAzul;
	}
	public Integer getMedMatCodigoDiluente() {
		return medMatCodigoDiluente;
	}
	public void setMedMatCodigoDiluente(Integer medMatCodigoDiluente) {
		this.medMatCodigoDiluente = medMatCodigoDiluente;
	}	
	
}
