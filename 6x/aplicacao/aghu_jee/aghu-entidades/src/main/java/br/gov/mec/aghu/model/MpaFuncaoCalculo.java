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
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@SequenceGenerator(name="mpaFcaSq1", sequenceName="AGH.MPA_FCA_SQ1", allocationSize = 1)
@Table(name = "MPA_FUNCAO_CALCULOS", schema = "AGH")
public class MpaFuncaoCalculo extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3735976972375861991L;
	private Short seq;
	private Integer version;
	private RapServidores rapServidores;
	private MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica;
	private String descricao;
	private String nomeFuncao;
	private Date criadoEm;
	private String indSituacao;
	private Set<MpaParamCalculoDose> mpaParamCalculoDosees = new HashSet<MpaParamCalculoDose>(0);
	
	// FIXME Implementar este relacionamento
//	private Set<MptItemPrescricaoMedicamento> mptItemPrescricaoMedicamentoes = new HashSet<MptItemPrescricaoMedicamento>(0);

	public MpaFuncaoCalculo() {
	}

	public MpaFuncaoCalculo(Short seq, RapServidores rapServidores, MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica,
			String descricao, String nomeFuncao, Date criadoEm, String indSituacao) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
		this.descricao = descricao;
		this.nomeFuncao = nomeFuncao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
	}

	public MpaFuncaoCalculo(Short seq, RapServidores rapServidores, MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica,
			String descricao, String nomeFuncao, Date criadoEm, String indSituacao, Set<MpaParamCalculoDose> mpaParamCalculoDosees
//			, Set<MptItemPrescricaoMedicamento> mptItemPrescricaoMedicamentoes
			) {
		this.seq = seq;
		this.rapServidores = rapServidores;
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
		this.descricao = descricao;
		this.nomeFuncao = nomeFuncao;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.mpaParamCalculoDosees = mpaParamCalculoDosees;
//		this.mptItemPrescricaoMedicamentoes = mptItemPrescricaoMedicamentoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpaFcaSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMM_SEQ", nullable = false)
	public MpmUnidadeMedidaMedica getMpmUnidadeMedidaMedica() {
		return this.mpmUnidadeMedidaMedica;
	}

	public void setMpmUnidadeMedidaMedica(MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica) {
		this.mpmUnidadeMedidaMedica = mpmUnidadeMedidaMedica;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 500)
	@Length(max = 500)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "NOME_FUNCAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getNomeFuncao() {
		return this.nomeFuncao;
	}

	public void setNomeFuncao(String nomeFuncao) {
		this.nomeFuncao = nomeFuncao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaFuncaoCalculo")
	public Set<MpaParamCalculoDose> getMpaParamCalculoDosees() {
		return this.mpaParamCalculoDosees;
	}

	public void setMpaParamCalculoDosees(Set<MpaParamCalculoDose> mpaParamCalculoDosees) {
		this.mpaParamCalculoDosees = mpaParamCalculoDosees;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaFuncaoCalculo")
//	public Set<MptItemPrescricaoMedicamento> getMptItemPrescricaoMedicamentoes() {
//		return this.mptItemPrescricaoMedicamentoes;
//	}
//
//	public void setMptItemPrescricaoMedicamentoes(Set<MptItemPrescricaoMedicamento> mptItemPrescricaoMedicamentoes) {
//		this.mptItemPrescricaoMedicamentoes = mptItemPrescricaoMedicamentoes;
//	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MPM_UNIDADE_MEDIDA_MEDICA("mpmUnidadeMedidaMedica"),
		DESCRICAO("descricao"),
		NOME_FUNCAO("nomeFuncao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		MPA_PARAM_CALCULO_DOSEES("mpaParamCalculoDosees"),
//		MPT_ITEM_PRESCRICAO_MEDICAMENTOES("mptItemPrescricaoMedicamentoes")
		;

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
		if (!(obj instanceof MpaFuncaoCalculo)) {
			return false;
		}
		MpaFuncaoCalculo other = (MpaFuncaoCalculo) obj;
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
