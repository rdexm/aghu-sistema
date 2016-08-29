package br.gov.mec.aghu.model;

// Generated 24/06/2011 18:11:15 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

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
@Table(name = "MAM_PROCED_ESPECIALIDADES", schema = "AGH")
public class MamProcedEspecialidade extends BaseEntityId<MamProcedEspecialidadeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6481373780962270555L;
	private MamProcedEspecialidadeId id;
	private DominioSituacao situacao;
	private Date criadoEm;
	private RapServidores servidor;
	private MamProcedimento procedimento;
	private AghEspecialidades especialidade;

	public MamProcedEspecialidade() {
	}

	public MamProcedEspecialidade(MamProcedEspecialidadeId id,
			RapServidores servidor) {
		this.id = id;
		this.servidor = servidor;
	}

	public MamProcedEspecialidade(MamProcedEspecialidadeId id,
			DominioSituacao situacao, Date criadoEm, RapServidores servidor) {
		this.id = id;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "espSeq", column = @Column(name = "ESP_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "prdSeq", column = @Column(name = "PRD_SEQ", nullable = false, precision = 6, scale = 0)) })
	public MamProcedEspecialidadeId getId() {
		return this.id;
	}

	public void setId(MamProcedEspecialidadeId id) {
		this.id = id;
	}

	@Column(name = "IND_SITUACAO", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRD_SEQ", nullable = false, insertable=false, updatable=false)
	public MamProcedimento getProcedimento() {
		return this.procedimento;
	}

	public void setProcedimento(MamProcedimento procedimento) {
		this.procedimento = procedimento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ", nullable = false, insertable=false, updatable=false)
	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}	
	
	public enum Fields {
		PROCEDIMENTO("procedimento"),
		SITUACAO("situacao"),
		ESPECIALIDADE_SEQ("especialidade.seq");

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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof MamProcedEspecialidade)) {
			return false;
		}
		MamProcedEspecialidade other = (MamProcedEspecialidade) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
