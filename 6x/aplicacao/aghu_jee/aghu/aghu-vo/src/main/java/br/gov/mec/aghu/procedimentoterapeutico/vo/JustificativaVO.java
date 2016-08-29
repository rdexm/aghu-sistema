package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class JustificativaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7989120291453835432L;

	private Short seqJus;
	private Short seqTps;
	private Short seqTpj;
	private String descricaoJus;
	private String descTipoJus;
	private String descTipoSes;
	private DominioSituacao indSituacao;

	public enum Fields {
		SEQ("seqJus"), SEQ_TPS("seqTps"), SEQ_TPJ("seqTpj"), DESC_TIPO_JUS(
				"descTipoJus"), DESC_TIPO_SES("descTipoSes"), DESCRICAO(
				"descricaoJus"), IND_SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeqJus() {
		return seqJus;
	}

	public void setSeqJus(Short seqJus) {
		this.seqJus = seqJus;
	}

	public String getDescricaoJus() {
		return descricaoJus;
	}

	public void setDescricaoJus(String descricaoJus) {
		this.descricaoJus = descricaoJus;
	}

	public String getDescTipoJus() {
		return descTipoJus;
	}

	public void setDescTipoJus(String descTipoJus) {
		this.descTipoJus = descTipoJus;
	}

	public String getDescTipoSes() {
		return descTipoSes;
	}

	public void setDescTipoSes(String descTipoSes) {
		this.descTipoSes = descTipoSes;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getSeqTps() {
		return seqTps;
	}

	public void setSeqTps(Short seqTps) {
		this.seqTps = seqTps;
	}

	public Short getSeqTpj() {
		return seqTpj;
	}

	public void setSeqTpj(Short seqTpj) {
		this.seqTpj = seqTpj;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.seqJus);
		umHashCodeBuilder.append(this.seqTpj);
		umHashCodeBuilder.append(this.seqTps);
		umHashCodeBuilder.append(this.descricaoJus);
		umHashCodeBuilder.append(this.descTipoJus);
		umHashCodeBuilder.append(this.descTipoSes);
		umHashCodeBuilder.append(this.indSituacao);
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JustificativaVO other = (JustificativaVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqJus(), other.getSeqJus());
		umEqualsBuilder.append(this.getSeqTpj(), other.getSeqTpj());
		umEqualsBuilder.append(this.getSeqTps(), other.getSeqTps());
		umEqualsBuilder.append(this.getIndSituacao(), other.getIndSituacao());
		umEqualsBuilder.append(this.getDescricaoJus(), other.getDescricaoJus());
		umEqualsBuilder.append(this.getDescTipoJus(), other.getDescTipoJus());
		umEqualsBuilder.append(this.getDescTipoSes(), other.getDescTipoSes());
		return umEqualsBuilder.isEquals();
	}

}
