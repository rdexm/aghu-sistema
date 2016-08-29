package br.gov.mec.aghu.model;

// Generated 07/05/2010 10:12:13 by Hibernate Tools 3.2.5.Beta

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


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MamAreaAtuNros generated by hbm2java
 */
@Entity
@Table(name = "MAM_AREA_ATU_NROS", schema = "AGH")

public class MamAreaAtuacaoNumero extends BaseEntityId<MamAreaAtuacaoNumeroId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4162451488026956164L;
	private MamAreaAtuacaoNumeroId id;
	private RapServidores servidor;
	private MamAreaAtuacao areaAtuacao;
	private Integer numeroInicialImpar;
	private Integer numeroFinalImpar;
	private Integer numeroInicialPar;
	private Integer numeroFinalPar;
	private Date criadoEm;
	private DominioSituacao situacao;

	public MamAreaAtuacaoNumero() {
	}

	public MamAreaAtuacaoNumero(MamAreaAtuacaoNumeroId id, RapServidores servidor,
			MamAreaAtuacao areaAtuacao, Date criadoEm, DominioSituacao situacao) {
		this.id = id;
		this.servidor = servidor;
		this.areaAtuacao = areaAtuacao;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
	}

	public MamAreaAtuacaoNumero(MamAreaAtuacaoNumeroId id, RapServidores servidor,
			MamAreaAtuacao areaAtuacao, Integer numeroInicialImpar,
			Integer numeroFinalImpar, Integer numeroInicialPar, Integer numeroFinalPar,
			Date criadoEm, DominioSituacao situacao) {
		this.id = id;
		this.servidor = servidor;
		this.areaAtuacao = areaAtuacao;
		this.numeroInicialImpar = numeroInicialImpar;
		this.numeroFinalImpar = numeroFinalImpar;
		this.numeroInicialPar = numeroInicialPar;
		this.numeroFinalPar = numeroFinalPar;
		this.criadoEm = criadoEm;
		this.situacao = situacao;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "araSeq", column = @Column(name = "ARA_SEQ", nullable = false, precision = 5, scale = 0)) })
	public MamAreaAtuacaoNumeroId getId() {
		return this.id;
	}

	public void setId(MamAreaAtuacaoNumeroId id) {
		this.id = id;
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
	@JoinColumn(name = "ARA_SEQ", nullable = false, insertable = false, updatable = false)
	public MamAreaAtuacao getAreaAtuacao() {
		return this.areaAtuacao;
	}

	public void setAreaAtuacao(MamAreaAtuacao areaAtuacao) {
		this.areaAtuacao = areaAtuacao;
	}

	@Column(name = "NRO_INICIAL_IMPAR", precision = 5, scale = 0)
	public Integer getNumeroInicialImpar() {
		return this.numeroInicialImpar;
	}

	public void setNumeroInicialImpar(Integer numeroInicialImpar) {
		this.numeroInicialImpar = numeroInicialImpar;
	}

	@Column(name = "NRO_FINAL_IMPAR", precision = 5, scale = 0)
	public Integer getNumeroFinalImpar() {
		return this.numeroFinalImpar;
	}

	public void setNumeroFinalImpar(Integer numeroFinalImpar) {
		this.numeroFinalImpar = numeroFinalImpar;
	}

	@Column(name = "NRO_INICIAL_PAR", precision = 5, scale = 0)
	public Integer getNumeroInicialPar() {
		return this.numeroInicialPar;
	}

	public void setNumeroInicialPar(Integer numeroInicialPar) {
		this.numeroInicialPar = numeroInicialPar;
	}

	@Column(name = "NRO_FINAL_PAR", precision = 5, scale = 0)
	public Integer getNumeroFinalPar() {
		return this.numeroFinalPar;
	}

	public void setNumeroFinalPar(Integer numeroFinalPar) {
		this.numeroFinalPar = numeroFinalPar;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		MamAreaAtuacaoNumero other = (MamAreaAtuacaoNumero) obj;
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
		ARA_SEQ("id.araSeq"),
		NRO_INICIAL_PAR("numeroInicialPar"),
		NRO_FINAL_PAR("numeroFinalPar"),
		NRO_INICIAL_IMPAR("numeroInicialImpar"),
		NRO_FINAL_IMPAR("numeroFinalImpar");

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