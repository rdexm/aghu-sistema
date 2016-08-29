package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HomologarProtocoloVO implements BaseBean {
	
	/**
	 * #44279
	 */
	private static final long serialVersionUID = -8880178817154808668L;
	
	private Long ptmSeq;
	private Integer vpsSeq;
	private MpmTipoFrequenciaAprazamento tipoFreqApraz;
	private Short ordem;
	private Short ordemMdtoConcomitante; //TODO Preencher após criação da tabela MPT_MDTOS_CONCOMITANTES_PROT 
	private String medicamento;
	private BigDecimal dose;
	private String via;
	private Boolean indFrequencia;
	private String descricaoAprazamento;
	private String sintaxeFrequencia;
	private Date tempo;
	private Boolean indPermiteAlteracao;
	private Short frequencia;
	
	public enum Fields {

		PTM_SEQ("ptmSeq"), 
		VPS_SEQ("vpsSeq"), 
		TFQ_SEQ("tipoFreqApraz"), 
		ORDEM("ordem"),
		ORDEM_MDTO_CONCOMITANTE("ordemMdtoConcomitante"),
		MEDICAMENTO("medicamento"), 
		DOSE("dose"), 
		VIA("via"),
		INDICADOR_FREQUENCIA("indFrequencia"), 
		DESCRICAO_APRAZAMENTO("descricaoAprazamento"),
		SINTAXE_FREQUENCIA("sintaxeFrequencia"),
		TEMPO("tempo"),
		FREQUENCIA("frequencia"),
		IND_PERMITE_ALTERACAO("indPermiteAlteracao"); 
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public String obterAprazamento(){
		String retorno =  StringUtils.EMPTY;
		if(descricaoAprazamento !=  null && !descricaoAprazamento.isEmpty()){
			if(this.frequencia != null){
				retorno = this.frequencia.toString() + StringUtils.SPACE + this.descricaoAprazamento;
			}else{
				retorno = this.descricaoAprazamento;
			}
		}
		return retorno;
	}
	
	//Getter's e Setter's
	public Long getPtmSeq() {
		return ptmSeq;
	}
	public void setPtmSeq(Long ptmSeq) {
		this.ptmSeq = ptmSeq;
	}
	public Integer getVpsSeq() {
		return vpsSeq;
	}
	public void setVpsSeq(Integer vpsSeq) {
		this.vpsSeq = vpsSeq;
	}
	public MpmTipoFrequenciaAprazamento getTipoFreqApraz() {
		return tipoFreqApraz;
	}
	public void setTipoFreqApraz(MpmTipoFrequenciaAprazamento tipoFreqApraz) {
		this.tipoFreqApraz = tipoFreqApraz;
	}
	public Short getOrdem() {
		return ordem;
	}
	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
	public Short getOrdemMdtoConcomitante() {
		return ordemMdtoConcomitante;
	}
	public void setOrdemMdtoConcomitante(Short ordemMdtoConcomitante) {
		this.ordemMdtoConcomitante = ordemMdtoConcomitante;
	}
	public String getMedicamento() {
		return medicamento;
	}
	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}
	public BigDecimal getDose() {
		return dose;
	}
	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}
	public String getVia() {
		return via;
	}
	public void setVia(String via) {
		this.via = via;
	}
	public Boolean getIndFrequencia() {
		return indFrequencia;
	}
	public void setIndFrequencia(Boolean indFrequencia) {
		this.indFrequencia = indFrequencia;
	}
	public String getDescricaoAprazamento() {
		return descricaoAprazamento;
	}
	public void setDescricaoAprazamento(String descricaoAprazamento) {
		this.descricaoAprazamento = descricaoAprazamento;
	}
	public String getSintaxeFrequencia() {
		return sintaxeFrequencia;
	}
	public void setSintaxeFrequencia(String sintaxeFrequencia) {
		this.sintaxeFrequencia = sintaxeFrequencia;
	}
	public Date getTempo() {
		return tempo;
	}
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	public Boolean getIndPermiteAlteracao() {
		return indPermiteAlteracao;
	}
	public void setIndPermiteAlteracao(Boolean indPermiteAlteracao) {
		this.indPermiteAlteracao = indPermiteAlteracao;
	}
	public Short getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

}
