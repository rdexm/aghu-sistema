package br.gov.mec.aghu.exames.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;

public class ResultadoMonitorPendenciasExamesVO {

	private Integer soeSeq;
	private Integer nroUnico; 
	private Short iseSeqp;
	private Short amoSeqp;
	private String exame;
	private String material; 
	private DominioUnidadeMedidaTempo unidTempoIntervaloColeta;
	private BigDecimal tempoIntervaloColeta;
	private Short iseUfeUnfSeq; 
	private Short amoUnfSeq;
	private Date dtNumeroUnico;
	private Boolean indEnviado; 
	private DominoOrigemMapaAmostraItemExame origemMapa; 
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Date dtHrEvento;


	public enum Fields {

		SOE_SEQ("soeSeq"), 
		NRO_UNICO("nroUnico"),
		ISE_SEQP("iseSeqp"),
		AMO_SEQP("amoSeqp"),
		EXAME("exame"),
		MATERIAL("material"),
		UNID_TEMPO_INTERVALO_COLETA("unidTempoIntervaloColeta"),
		TEMPO_INTERVALO_COLETA("tempoIntervaloColeta"), 
		ISE_UFE_UNFSEQ("iseUfeUnfSeq"),
		AMO_UNFSEQ("amoUnfSeq"),
		DT_NUMERO_UNICO("dtNumeroUnico"),
		IND_ENVIADO("indEnviado"), 
		ORIGEM_MAPA("origemMapa"),
		UFE_EMA_EXA_SIGLA("ufeEmaExaSigla"),
		UFE_EMA_MAN_SEQ("ufeEmaManSeq"),
		DTHR_EVENTO("dtHrEvento");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Integer getNroUnico() {
		return nroUnico;
	}

	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}

	public Short getIseSeqp() {
		return iseSeqp;
	}

	public void setIseSeqp(Short iseSeqp) {
		this.iseSeqp = iseSeqp;
	}

	public Short getAmoSeqp() {
		return amoSeqp;
	}

	public void setAmoSeqp(Short amoSeqp) {
		this.amoSeqp = amoSeqp;
	}

	public String getExame() {
		return exame;
	}

	public void setExame(String exame) {
		this.exame = exame;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public DominioUnidadeMedidaTempo getUnidTempoIntervaloColeta() {
		return unidTempoIntervaloColeta;
	}

	public void setUnidTempoIntervaloColeta(DominioUnidadeMedidaTempo unidTempoIntervaloColeta) {
		this.unidTempoIntervaloColeta = unidTempoIntervaloColeta;
	}

	public BigDecimal getTempoIntervaloColeta() {
		return tempoIntervaloColeta;
	}

	public void setTempoIntervaloColeta(BigDecimal tempoIntervaloColeta) {
		this.tempoIntervaloColeta = tempoIntervaloColeta;
	}

	public Short getIseUfeUnfSeq() {
		return iseUfeUnfSeq;
	}

	public void setIseUfeUnfSeq(Short iseUfeUnfSeq) {
		this.iseUfeUnfSeq = iseUfeUnfSeq;
	}

	public Short getAmoUnfSeq() {
		return amoUnfSeq;
	}

	public void setAmoUnfSeq(Short amoUnfSeq) {
		this.amoUnfSeq = amoUnfSeq;
	}

	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}

	public void setDtNumeroUnico(Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}

	public Boolean getIndEnviado() {
		return indEnviado;
	}

	public void setIndEnviado(Boolean indEnviado) {
		this.indEnviado = indEnviado;
	}

	public DominoOrigemMapaAmostraItemExame getOrigemMapa() {
		return origemMapa;
	}

	public void setOrigemMapa(DominoOrigemMapaAmostraItemExame origemMapa) {
		this.origemMapa = origemMapa;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public Date getDtHrEvento() {
		return dtHrEvento;
	}

	public void setDtHrEvento(Date dtHrEvento) {
		this.dtHrEvento = dtHrEvento;
	}

}
