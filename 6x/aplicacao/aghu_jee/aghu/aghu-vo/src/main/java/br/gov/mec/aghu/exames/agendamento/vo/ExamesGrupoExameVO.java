package br.gov.mec.aghu.exames.agendamento.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ExamesGrupoExameVO implements BaseBean {

	private static final long serialVersionUID = -2961464705261921498L;
	
	private String ufeEmaExaSigla;
	private String descricaoUsualExame;
	private Integer ufeEmaManSeq;
	private String descricaoMaterial;
	private Short ufeUnfSeq;
	private String unfDescricao;
	private DominioSituacao situacao;
	private Integer version;
	
	public ExamesGrupoExameVO() {
		
	}
	
	public ExamesGrupoExameVO(String ufeEmaExaSigla, String descricaoUsualExame, Integer ufeEmaManSeq, String descricaoMaterial, Short ufeUnfSeq, String unfDescricao, DominioSituacao situacao) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
		this.descricaoUsualExame = descricaoUsualExame;
		this.ufeEmaManSeq = ufeEmaManSeq;
		this.descricaoMaterial = descricaoMaterial;
		this.ufeUnfSeq = ufeUnfSeq;
		this.unfDescricao = unfDescricao;
		this.situacao = situacao;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	
	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}
	
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}
	
	public enum Fields {
		UFE_EMA_EXA_SIGLA("ufeEmaExaSigla"),
		DESCRICAO_USUAL_EXAME("descricaoUsualExame"),
		UFE_EMA_MAN_SEQ("ufeEmaManSeq"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		UFE_UNF_SEQ("ufeUnfSeq"),
		UNF_DESCRICAO("unfDescricao"),
		VERSION("version"),
		SITUACAO("situacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
