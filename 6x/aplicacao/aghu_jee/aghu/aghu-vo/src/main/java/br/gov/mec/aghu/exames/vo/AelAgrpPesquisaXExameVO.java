package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacao;


public class AelAgrpPesquisaXExameVO implements Serializable{
	
	private static final long serialVersionUID = 3801878678389456556L;
	
	private Integer seq;
	private String emaExaSigla;
	private Integer emaManSeq;
	private Short unfSeq;
	private DominioSituacao indSituacao;

	private String descricaoUsualExame;
	private String descricaoMaterial;
	private String descricaoUnidade;
	private Integer version;

	public enum Fields {
		SEQ("seq"),
		EMA_EXA_SIGLA("emaExaSigla"),
		EMA_MAN_SEQ("emaManSeq"),
		UNF_SEQ("unfSeq"),
		IND_SITUACAO("indSituacao"),
		DESCRICAO_USUAL_EXAME("descricaoUsualExame"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		DESCRICAO_UNIDADE("descricaoUnidade"),
		VERSION("version");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getDescricaoUsualExame() {
		return descricaoUsualExame;
	}

	public void setDescricaoUsualExame(String descricaoUsualExame) {
		this.descricaoUsualExame = descricaoUsualExame;
	}

	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}

	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}

	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}

	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AelAgrpPesquisaXExameVO)) {
			return false;
		}
		AelAgrpPesquisaXExameVO other = (AelAgrpPesquisaXExameVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (version == null) {
			if (other.version != null) {
				return false;
			}
		} else if (!version.equals(other.version)) {
			return false;
		}
		return true;
	}

}
