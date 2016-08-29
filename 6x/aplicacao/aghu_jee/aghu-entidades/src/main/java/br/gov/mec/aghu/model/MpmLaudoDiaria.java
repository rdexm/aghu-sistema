package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

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
@Table(name = "MPM_LAUDO_DIARIAS", schema = "AGH")
public class MpmLaudoDiaria extends BaseEntityId<MpmLaudoDiariaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 805385923191549855L;
	private MpmLaudoDiariaId id;
	private Integer phiSeq;
	private RapServidores servidor;
	private Date criadoEm;
	private String justificativa;
	private Boolean impressao;
	private AghAtendimentos atendimento;

	public MpmLaudoDiaria() {
	}

	public MpmLaudoDiaria(MpmLaudoDiariaId id, Integer phiSeq, 
			RapServidores servidor, Date criadoEm, String justificativa,
			Boolean impressao) {
		this.id = id;
		this.phiSeq = phiSeq;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.justificativa = justificativa;
		this.impressao = impressao;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	public MpmLaudoDiariaId getId() {
		return this.id;
	}

	public void setId(MpmLaudoDiariaId id) {
		this.id = id;
	}

	@Column(name = "PHI_SEQ", nullable = false, precision = 6, scale = 0)
	public Integer getPhiSeq() {
		return this.phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "JUSTIFICATIVA", nullable = false, length = 500)
	@Length(max = 500)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
	@Column(name = "IND_IMPRESSAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getImpressao() {
		return this.impressao;
	}

	public void setImpressao(Boolean impressao) {
		this.impressao = impressao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false, insertable = false, updatable = false)
	public AghAtendimentos getAtendimento() {
		return this.atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		MpmLaudoDiaria other = (MpmLaudoDiaria) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
	
	
	public enum Fields {

		ID("id"),
		PHI_SEQ("phiSeq"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		JUSTIFICATIVA("justificativa"),
		IMPRESSAO("impressao"),
		ATENDIMENTO("atendimento");

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
