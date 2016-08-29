package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
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
@SequenceGenerator(name="fcuFagSq1", sequenceName="AGH.FCU_FAG_SQ1", allocationSize = 1)
@Table(name = "FCU_FORMA_ATEND_X_GRUPO_ATIV", schema = "AGH")
public class FcuFormaAtendXGrupoAtiv extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7529890261062330007L;
	private Short seq;
	private Integer version;
	private FcuFormaAtendimento fcuFormaAtendimento;
	private RapServidores rapServidores;
	private FcuGrupoAtividade fcuGrupoAtividade;
	private String indSituacao;
	private Date criadoEm;

	public FcuFormaAtendXGrupoAtiv() {
	}

	public FcuFormaAtendXGrupoAtiv(Short seq, FcuFormaAtendimento fcuFormaAtendimento, RapServidores rapServidores,
			FcuGrupoAtividade fcuGrupoAtividade, String indSituacao, Date criadoEm) {
		this.seq = seq;
		this.fcuFormaAtendimento = fcuFormaAtendimento;
		this.rapServidores = rapServidores;
		this.fcuGrupoAtividade = fcuGrupoAtividade;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "fcuFagSq1")
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
	@JoinColumn(name = "FTD_SEQ", nullable = false)
	public FcuFormaAtendimento getFcuFormaAtendimento() {
		return this.fcuFormaAtendimento;
	}

	public void setFcuFormaAtendimento(FcuFormaAtendimento fcuFormaAtendimento) {
		this.fcuFormaAtendimento = fcuFormaAtendimento;
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
	@JoinColumn(name = "GAT_SEQ", nullable = false)
	public FcuGrupoAtividade getFcuGrupoAtividade() {
		return this.fcuGrupoAtividade;
	}

	public void setFcuGrupoAtividade(FcuGrupoAtividade fcuGrupoAtividade) {
		this.fcuGrupoAtividade = fcuGrupoAtividade;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		FCU_FORMA_ATENDIMENTOS("fcuFormaAtendimento"),
		RAP_SERVIDORES("rapServidores"),
		FCU_GRUPO_ATIVIDADES("fcuGrupoAtividade"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm");

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
		if (!(obj instanceof FcuFormaAtendXGrupoAtiv)) {
			return false;
		}
		FcuFormaAtendXGrupoAtiv other = (FcuFormaAtendXGrupoAtiv) obj;
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
