package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@SequenceGenerator(name="rarRslSq1", sequenceName="AGH.RAR_RSL_SQ1", allocationSize = 1)
@Table(name = "RAR_RESOLUCOES", schema = "AGH")
public class RarResolucao extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7611932393374669222L;
	private Integer seq;
	private Integer version;
	private String descricao;
	private String resumo;
	private Date dtInicio;
	private Date dtFim;
	private Integer chAno;
	private Set<RarItemResolucao> rarItemResolucaos = new HashSet<RarItemResolucao>(0);
	private Set<RarPrograma> rarProgramaes = new HashSet<RarPrograma>(0);

	public RarResolucao() {
	}

	public RarResolucao(Integer seq, String descricao, Date dtInicio) {
		this.seq = seq;
		this.descricao = descricao;
		this.dtInicio = dtInicio;
	}

	public RarResolucao(Integer seq, String descricao, String resumo, Date dtInicio, Date dtFim, Integer chAno,
			Set<RarItemResolucao> rarItemResolucaos, Set<RarPrograma> rarProgramaes) {
		this.seq = seq;
		this.descricao = descricao;
		this.resumo = resumo;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
		this.chAno = chAno;
		this.rarItemResolucaos = rarItemResolucaos;
		this.rarProgramaes = rarProgramaes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rarRslSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
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

	@Column(name = "DESCRICAO", nullable = false, length = 80)
	@Length(max = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "RESUMO", length = 40)
	@Length(max = 40)
	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO", nullable = false, length = 29)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "CH_ANO")
	public Integer getChAno() {
		return this.chAno;
	}

	public void setChAno(Integer chAno) {
		this.chAno = chAno;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarResolucao")
	public Set<RarItemResolucao> getRarItemResolucaos() {
		return this.rarItemResolucaos;
	}

	public void setRarItemResolucaos(Set<RarItemResolucao> rarItemResolucaos) {
		this.rarItemResolucaos = rarItemResolucaos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarResolucao")
	public Set<RarPrograma> getRarProgramaes() {
		return this.rarProgramaes;
	}

	public void setRarProgramaes(Set<RarPrograma> rarProgramaes) {
		this.rarProgramaes = rarProgramaes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		DESCRICAO("descricao"),
		RESUMO("resumo"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		CH_ANO("chAno"),
		RAR_ITEM_RESOLUCAOS("rarItemResolucaos"),
		RAR_PROGRAMAES("rarProgramaes");

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
		if (!(obj instanceof RarResolucao)) {
			return false;
		}
		RarResolucao other = (RarResolucao) obj;
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
