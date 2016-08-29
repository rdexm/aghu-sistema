package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class AfaGrupoComponenteNptVO implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -551534765940226482L;
	private Short seq;
	private Integer version;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private String observacao;
	private String usuario;
	
	private String tooltipCodigo;
	private String tooltipCodigo2;

	public AfaGrupoComponenteNptVO() {
	}

	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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
	
	public String getTooltipCodigo() {
		return tooltipCodigo;
	}

	public void setTooltipCodigo(String tooltipCodigo) {
		this.tooltipCodigo = tooltipCodigo;
	}
	
	public String getTooltipCodigo2() {
		return tooltipCodigo2;
	}

	public void setTooltipCodigo2(String tooltipCodigo2) {
		this.tooltipCodigo2 = tooltipCodigo2;
	}


	public enum Fields {
		SEQ("seq"), 
		VERSION("version"), 
		DESCRICAO("descricao"), 
		CRIADO_EM("criadoEm"), 
		IND_SITUACAO("indSituacao"), 
		USUARIO("usuario"),
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
