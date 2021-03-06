package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * AbsMotivoRejeicao generated by hbm2java
 */
@Entity
@SequenceGenerator(name="absMreSq1", sequenceName="AGH.ABS_MRE_SQ1", allocationSize = 1)
@Table(name = "ABS_MOTIVOS_REJEICOES", schema = "AGH")
public class AbsMotivoRejeicao extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1529174723213707586L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private String descricao;
	private String indVencimento;
	private String indSituacao;
	private Set<AbsQuestao> absQuestaoes = new HashSet<AbsQuestao>(0);
	private Set<AbsTipoConclusao> absTipoConclusaoes = new HashSet<AbsTipoConclusao>(0);
	
	// FIXME Implementar este relacionamento
//	private Set<AbsMovimentosComponentes> absMovimentosComponenteses = new HashSet<AbsMovimentosComponentes>(0);

	public AbsMotivoRejeicao() {
	}

	public AbsMotivoRejeicao(Short seq, RapServidores rapServidores, String descricao) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
	}

	public AbsMotivoRejeicao(Short seq, RapServidores rapServidores, String descricao, String indVencimento, String indSituacao,
//			Set<AbsMovimentosComponentes> absMovimentosComponenteses, 
			Set<AbsQuestao> absQuestaoes, Set<AbsTipoConclusao> absTipoConclusaoes) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.descricao = descricao;
		this.indVencimento = indVencimento;
		this.indSituacao = indSituacao;
//		this.absMovimentosComponenteses = absMovimentosComponenteses;
		this.absQuestaoes = absQuestaoes;
		this.absTipoConclusaoes = absTipoConclusaoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "absMreSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_VENCIMENTO", length = 1)
	@Length(max = 1)
	public String getIndVencimento() {
		return this.indVencimento;
	}

	public void setIndVencimento(String indVencimento) {
		this.indVencimento = indVencimento;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "absMotivoRejeicao")
//	public Set<AbsMovimentosComponentes> getAbsMovimentosComponenteses() {
//		return this.absMovimentosComponenteses;
//	}
//
//	public void setAbsMovimentosComponenteses(Set<AbsMovimentosComponentes> absMovimentosComponenteses) {
//		this.absMovimentosComponenteses = absMovimentosComponenteses;
//	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "absMotivoRejeicao")
	public Set<AbsQuestao> getAbsQuestaoes() {
		return this.absQuestaoes;
	}

	public void setAbsQuestaoes(Set<AbsQuestao> absQuestaoes) {
		this.absQuestaoes = absQuestaoes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "absMotivoRejeicao")
	public Set<AbsTipoConclusao> getAbsTipoConclusaoes() {
		return this.absTipoConclusaoes;
	}

	public void setAbsTipoConclusaoes(Set<AbsTipoConclusao> absTipoConclusaoes) {
		this.absTipoConclusaoes = absTipoConclusaoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		DESCRICAO("descricao"),
		IND_VENCIMENTO("indVencimento"),
		IND_SITUACAO("indSituacao"),
//		ABS_MOVIMENTOS_COMPONENTESES("absMovimentosComponenteses"),
		ABS_QUESTAOES("absQuestaoes"),
		ABS_TIPO_CONCLUSAOES("absTipoConclusaoes");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof AbsMotivoRejeicao)) {
			return false;
		}
		AbsMotivoRejeicao other = (AbsMotivoRejeicao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
