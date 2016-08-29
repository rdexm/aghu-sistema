package br.gov.mec.aghu.controleinfeccao;

/**
 * VO do Retorno da PROCEDURE MCIK_RN.RN_MCIP_ATU_LOCAL
 * 
 * @author aghu
 *
 */
public class LocalNotificacaoOrigemRetornoVO {

	private String ltoLtoId;
	private Short qrtNumero;
	private Short unfSeq;
	private String ltoLtoIdNotificado;
	private Short qrtNumeroNotificado;
	private Short unfSeqNotificado;

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getLtoLtoIdNotificado() {
		return ltoLtoIdNotificado;
	}

	public void setLtoLtoIdNotificado(String ltoLtoIdNotificado) {
		this.ltoLtoIdNotificado = ltoLtoIdNotificado;
	}

	public Short getQrtNumeroNotificado() {
		return qrtNumeroNotificado;
	}

	public void setQrtNumeroNotificado(Short qrtNumeroNotificado) {
		this.qrtNumeroNotificado = qrtNumeroNotificado;
	}

	public Short getUnfSeqNotificado() {
		return unfSeqNotificado;
	}

	public void setUnfSeqNotificado(Short unfSeqNotificado) {
		this.unfSeqNotificado = unfSeqNotificado;
	}

}
