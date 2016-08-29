package br.gov.mec.aghu.prescricaomedica.vo;


import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class AfaCompoGrupoComponenteVO  implements java.io.Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -1523553168068768072L;
	private Short ticSeq;
	private Short gcnSeq;
	private String descricao;
	private Date criadoEm;
	private Date alteradoEm;
	private DominioSituacao indSituacao;
	private String observacao;
	private String usuario;
	private String usuario2;
	
	private String tooltip;
	private String tooltip2;


	public AfaCompoGrupoComponenteVO() {
	}


	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Short getTicSeq() {
		return ticSeq;
	}


	public void setTicSeq(Short ticSeq) {
		this.ticSeq = ticSeq;
	}


	public Short getGcnSeq() {
		return gcnSeq;
	}


	public void setGcnSeq(Short gcnSeq) {
		this.gcnSeq = gcnSeq;
	}


	public String getUsuario2() {
		return usuario2;
	}

	public void setUsuario2(String usuario2) {
		this.usuario2 = usuario2;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getTooltip2() {
		return tooltip2;
	}


	public void setTooltip2(String tooltip2) {
		this.tooltip2 = tooltip2;
	}


	public enum Fields {

		TIC_SEQ("ticSeq"),
		GCN_SEQ("gcnSeq"),
		VERSION("version"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		IND_SITUACAO("indSituacao"),
		USUARIO("usuario"),
		USUARIO2("usuario2"),
		OBSERVACAO("observacao");

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
