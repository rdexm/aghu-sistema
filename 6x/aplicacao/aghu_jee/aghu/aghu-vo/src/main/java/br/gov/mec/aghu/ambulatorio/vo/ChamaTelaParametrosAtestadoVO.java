package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class ChamaTelaParametrosAtestadoVO implements Serializable{

	
	private static final long serialVersionUID = -5269880666822438651L;
	
//	pl_id 			PARAMLIST;
//	pl_name 		VARCHAR2(10);
//	v_dado			VARCHAR2(1);
//	v_retorno		NUMBER;
//	v_atd_seq		agh_atendimentos.seq%type;
//	v_dthr_mvto		DATE;
//	v_esp_seq		agh_especialidades.seq%type;
//	v_esp_pai		agh_especialidades.esp_seq%type;
//	v_unf_seq		agh_unidades_funcionais.seq%type;

	private Short   plId;
	
	private String  plName;
	
	private String  vDado;
	
	private Integer vRetorno;
	
	private Short   vAtdSeq;

//	private Date    vDthrMvto;
	
	private Short   vEspSeq;
	
	private Short   vEspPai;

	private Short   vUnfSeq;
	



	
	public enum Fields {
		SEQ_ATENDIMENTOS("vAtdSeq"),
		PL_ID("plId"),
		PL_NAME("plName"),
		V_DADO("vDado"),
		V_RETORNO("vRetorno"),
//		V("vDthrMvto"),
		SEQ_ESPECIALIDADES("vEspSeq"),
		SEQ_ESPECIALIDADES_PAI("vEspPai"),
		UNFSEQ_AAC_UNID_FUNCIONAL_SALA("vUnfSeq"),
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
	
	public Short getPlId() {
		return plId;
	}

	public void setPlId(Short plId) {
		this.plId = plId;
	}

	public String getPlName() {
		return plName;
	}

	public void setPlName(String plName) {
		this.plName = plName;
	}

	public String getvDado() {
		return vDado;
	}

	public void setvDado(String vDado) {
		this.vDado = vDado;
	}

	public Integer getvRetorno() {
		return vRetorno;
	}

	public void setvRetorno(Integer vRetorno) {
		this.vRetorno = vRetorno;
	}

	public Short getvAtdSeq() {
		return vAtdSeq;
	}

	public void setvAtdSeq(Short vAtdSeq) {
		this.vAtdSeq = vAtdSeq;
	}

//	public Date getvDthrMvto() {
//		return vDthrMvto;
//	}
//
//	public void setvDthrMvto(Date vDthrMvto) {
//		this.vDthrMvto = vDthrMvto;
//	}

	public Short getvEspSeq() {
		return vEspSeq;
	}

	public void setvEspSeq(Short vEspSeq) {
		this.vEspSeq = vEspSeq;
	}

	public Short getvEspPai() {
		return vEspPai;
	}

	public void setvEspPai(Short vEspPai) {
		this.vEspPai = vEspPai;
	}

	public Short getvUnfSeq() {
		return vUnfSeq;
	}

	public void setvUnfSeq(Short vUnfSeq) {
		this.vUnfSeq = vUnfSeq;
	}
}
