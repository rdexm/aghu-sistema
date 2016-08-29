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
@SequenceGenerator(name="rapAtvSq1", sequenceName="AGH.RAP_ATV_SQ1", allocationSize = 1)
@Table(name = "RAP_PPP_ATIVIDADES", schema = "AGH")
public class RapPppAtividade extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3280330279100077115L;
	private Integer seq;
	private Integer version;
	private RapOcupacaoCargo rapOcupacaoCargo;
	private FccCentroCustos fccCentroCustos;
	private String resumo;
	private String descricao;
	private Set<RapPppProfissiografia> rapPppProfissiografiaes = new HashSet<RapPppProfissiografia>(0);

	public RapPppAtividade() {
	}

	public RapPppAtividade(Integer seq, String resumo, String descricao) {
		this.seq = seq;
		this.resumo = resumo;
		this.descricao = descricao;
	}

	public RapPppAtividade(Integer seq, RapOcupacaoCargo rapOcupacaoCargo, FccCentroCustos fccCentroCustos, String resumo,
			String descricao, Set<RapPppProfissiografia> rapPppProfissiografiaes) {
		this.seq = seq;
		this.rapOcupacaoCargo = rapOcupacaoCargo;
		this.fccCentroCustos = fccCentroCustos;
		this.resumo = resumo;
		this.descricao = descricao;
		this.rapPppProfissiografiaes = rapPppProfissiografiaes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapAtvSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "OCA_CAR_CODIGO", referencedColumnName = "CAR_CODIGO"),
			@JoinColumn(name = "OCA_CODIGO", referencedColumnName = "CODIGO") })
	public RapOcupacaoCargo getRapOcupacaoCargo() {
		return this.rapOcupacaoCargo;
	}

	public void setRapOcupacaoCargo(RapOcupacaoCargo rapOcupacaoCargo) {
		this.rapOcupacaoCargo = rapOcupacaoCargo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@Column(name = "RESUMO", nullable = false, length = 60)
	@Length(max = 60)
	public String getResumo() {
		return this.resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	@Length(max = 2000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rapPppAtividade")
	public Set<RapPppProfissiografia> getRapPppProfissiografiaes() {
		return this.rapPppProfissiografiaes;
	}

	public void setRapPppProfissiografiaes(Set<RapPppProfissiografia> rapPppProfissiografiaes) {
		this.rapPppProfissiografiaes = rapPppProfissiografiaes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_OCUPACAO_CARGO("rapOcupacaoCargo"),
		FCC_CENTRO_CUSTOS("fccCentroCustos"),
		RESUMO("resumo"),
		DESCRICAO("descricao"),
		RAP_PPP_PROFISSIOGRAFIAES("rapPppProfissiografiaes");

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
		if (!(obj instanceof RapPppAtividade)) {
			return false;
		}
		RapPppAtividade other = (RapPppAtividade) obj;
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
