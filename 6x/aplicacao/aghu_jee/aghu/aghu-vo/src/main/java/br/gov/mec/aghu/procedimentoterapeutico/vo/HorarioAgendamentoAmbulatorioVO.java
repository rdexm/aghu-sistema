package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HorarioAgendamentoAmbulatorioVO implements BaseBean {

	private static final long serialVersionUID = 3587122963001277155L;

	private Integer conNumero;
	private Integer pacCodigo;
	private Short cspCnvCodigo;
	private Short cspSeq;
	private DominioGrupoConvenio grupoConvenio;
	private String stcSituacao;
	private Integer retSeq;
	private Short caaSeq;
	private Short tagSeq;
	private Short pgdSeq;
	
	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	public Short getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Short cspSeq) {
		this.cspSeq = cspSeq;
	}

	public DominioGrupoConvenio getGrupoConvenio() {
		return grupoConvenio;
	}

	public void setGrupoConvenio(DominioGrupoConvenio grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}

	public String getStcSituacao() {
		return stcSituacao;
	}

	public void setStcSituacao(String stcSituacao) {
		this.stcSituacao = stcSituacao;
	}

	public Integer getRetSeq() {
		return retSeq;
	}

	public void setRetSeq(Integer retSeq) {
		this.retSeq = retSeq;
	}

	public Short getCaaSeq() {
		return caaSeq;
	}

	public void setCaaSeq(Short caaSeq) {
		this.caaSeq = caaSeq;
	}

	public Short getTagSeq() {
		return tagSeq;
	}

	public void setTagSeq(Short tagSeq) {
		this.tagSeq = tagSeq;
	}

	public Short getPgdSeq() {
		return pgdSeq;
	}

	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}

	public enum Fields {

		CON_NUMERO("conNumero"), 
		PAC_CODIGO("pacCodigo"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		GRUPO_CONVENIO("grupoConvenio"),
		STC_SITUACAO("stcSituacao"),
		IND_SIT_CONSULTA("indSitConsulta"),
		RET_SEQ("retSeq"),
		CAA_SEQ("caaSeq"),
		TAG_SEQ("tagSeq"),
		PGD_SEQ("pgdSeq");
		
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
