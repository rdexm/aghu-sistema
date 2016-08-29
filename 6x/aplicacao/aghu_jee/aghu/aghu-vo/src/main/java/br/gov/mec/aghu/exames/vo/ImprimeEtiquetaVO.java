package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

/**
 * @author marcosilva
 *
 */
public class ImprimeEtiquetaVO  implements Serializable{

	private static final long serialVersionUID = 4182754562216041848L;
	
	private Long nroAp;
	private Integer soeSeq;
	private Short amoSeqP;
	private Boolean receberAmostra;
	private Short unfSeq;
	private Boolean imprimir;
	private Boolean imprimirFichaTrabPat;
	private Boolean imprimirFichaTrabLab;
	private Boolean imprimirFichaTrabAmo;
	private Integer qtdAmostras;
	private Integer qtdEtiquetas;
	private String msgNroUnico;

	private String sigla;
	private Long numeroAp;
	
	public Long getNroAp() {
		return nroAp;
	}
	
	public void setNroAp(Long nroAp) {
		this.nroAp = nroAp;
	}
	
	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Boolean getImprimir() {
		return imprimir;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimir = imprimir;
	}

	public Short getAmoSeqP() {
		return amoSeqP;
	}

	public void setAmoSeqP(Short amoSeqP) {
		this.amoSeqP = amoSeqP;
	}

	public Boolean getReceberAmostra() {
		return receberAmostra;
	}

	public void setReceberAmostra(Boolean receberAmostra) {
		this.receberAmostra = receberAmostra;
	}

	public Boolean getImprimirFichaTrabPat() {
		return imprimirFichaTrabPat;
	}

	public void setImprimirFichaTrabPat(Boolean imprimirFichaTrabPat) {
		this.imprimirFichaTrabPat = imprimirFichaTrabPat;
	}

	public Boolean getImprimirFichaTrabLab() {
		return imprimirFichaTrabLab;
	}

	public void setImprimirFichaTrabLab(Boolean imprimirFichaTrabLab) {
		this.imprimirFichaTrabLab = imprimirFichaTrabLab;
	}

	public Boolean getImprimirFichaTrabAmo() {
		return imprimirFichaTrabAmo;
	}

	public void setImprimirFichaTrabAmo(Boolean imprimirFichaTrabAmo) {
		this.imprimirFichaTrabAmo = imprimirFichaTrabAmo;
	}

	public Integer getQtdAmostras() {
		return qtdAmostras;
	}

	public void setQtdAmostras(Integer qtdAmostras) {
		this.qtdAmostras = qtdAmostras;
	}

	public Integer getQtdEtiquetas() {
		return qtdEtiquetas;
	}

	public void setQtdEtiquetas(Integer qtdEtiquetas) {
		this.qtdEtiquetas = qtdEtiquetas;
	}

	public String getMsgNroUnico() {
		return msgNroUnico;
	}

	public void setMsgNroUnico(String msgNroUnico) {
		this.msgNroUnico = msgNroUnico;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}
		
}
