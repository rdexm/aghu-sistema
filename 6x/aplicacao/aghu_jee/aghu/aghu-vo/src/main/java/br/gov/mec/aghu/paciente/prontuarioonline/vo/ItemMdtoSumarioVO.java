package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioUnidadeCorrer;


public class ItemMdtoSumarioVO { 
	
	private Short imsTfqSeq;
	private Short imsTvaSeq;
	private Integer imsMatCodigo;
	private Integer imsFdsSeq;
	private BigDecimal imsDose;
	private Short imsFrequecia;
	private Byte imsQtdeCorrer;
	private BigDecimal imsGotejo;
	private Boolean imsIndSeNecessario;
	private String imsObservacao;
	private String imsObservacaoItem;
	private String imsVadSigla;
	private DominioUnidadeCorrer imsUnidadeCorrer;
	private Byte imsDiasUsoDomiciliar;
	private Date imsHoraInicioAdministracao;


	public Short getImsTfqSeq() {
		return imsTfqSeq;
	}
	public void setImsTfqSeq(Short imsTfqSeq) {
		this.imsTfqSeq = imsTfqSeq;
	}
	public Short getImsTvaSeq() {
		return imsTvaSeq;
	}
	public void setImsTvaSeq(Short imsTvaSeq) {
		this.imsTvaSeq = imsTvaSeq;
	}
	public Integer getImsMatCodigo() {
		return imsMatCodigo;
	}
	public void setImsMatCodigo(Integer imsMatCodigo) {
		this.imsMatCodigo = imsMatCodigo;
	}
	public Integer getImsFdsSeq() {
		return imsFdsSeq;
	}
	public void setImsFdsSeq(Integer imsFdsSeq) {
		this.imsFdsSeq = imsFdsSeq;
	}
	public BigDecimal getImsDose() {
		return imsDose;
	}
	public void setImsDose(BigDecimal imsDose) {
		this.imsDose = imsDose;
	}
	public Short getImsFrequecia() {
		return imsFrequecia;
	}
	public void setImsFrequecia(Short imsFrequecia) {
		this.imsFrequecia = imsFrequecia;
	}
	public Byte getImsQtdeCorrer() {
		return imsQtdeCorrer;
	}
	public void setImsQtdeCorrer(Byte imsQtdeCorrer) {
		this.imsQtdeCorrer = imsQtdeCorrer;
	}
	public BigDecimal getImsGotejo() {
		return imsGotejo;
	}
	public void setImsGotejo(BigDecimal imsGotejo) {
		this.imsGotejo = imsGotejo;
	}
	public Boolean getImsIndSeNecessario() {
		return imsIndSeNecessario;
	}
	public void setImsIndSeNecessario(Boolean imsIndSeNecessario) {
		this.imsIndSeNecessario = imsIndSeNecessario;
	}
	public String getImsObservacao() {
		return imsObservacao;
	}
	public void setImsObservacao(String imsObservacao) {
		this.imsObservacao = imsObservacao;
	}
	public String getImsVadSigla() {
		return imsVadSigla;
	}
	public void setImsVadSigla(String imsVadSigla) {
		this.imsVadSigla = imsVadSigla;
	}
	public DominioUnidadeCorrer getImsUnidadeCorrer() {
		return imsUnidadeCorrer;
	}
	public void setImsUnidadeCorrer(DominioUnidadeCorrer imsUnidadeCorrer) {
		this.imsUnidadeCorrer = imsUnidadeCorrer;
	}
	public Byte getImsDiasUsoDomiciliar() {
		return imsDiasUsoDomiciliar;
	}
	public void setImsDiasUsoDomiciliar(Byte imsDiasUsoDomiciliar) {
		this.imsDiasUsoDomiciliar = imsDiasUsoDomiciliar;
	}
	public String getImsObservacaoItem() {
		return imsObservacaoItem;
	}
	public void setImsObservacaoItem(String imsObservacaoItem) {
		this.imsObservacaoItem = imsObservacaoItem;
	}
	public Date getImsHoraInicioAdministracao() {
		return imsHoraInicioAdministracao;
	}
	public void setImsHoraInicioAdministracao(Date imsHoraInicioAdministracao) {
		this.imsHoraInicioAdministracao = imsHoraInicioAdministracao;
	}

}