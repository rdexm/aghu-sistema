package br.gov.mec.aghu.model;

// Generated 15/06/2011 21:07:17 by Hibernate Tools 3.4.0.CR1

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





import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * CsePerfilProcessos generated by hbm2java
 */
@Entity
@Table(name = "CSE_PERFIL_PROCESSOS", schema = "AGH")
public class CsePerfilProcessos extends BaseEntityId<CsePerfilProcessosId> implements java.io.Serializable {
	private static final long serialVersionUID = -1626415228910353792L;
	
	private CsePerfilProcessosId id;
	private CseProcessos processo;
	private Boolean indExecuta;
	private Boolean indConsulta;
	private Boolean indAssina;
	private DominioSituacao situacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Boolean indRegMedicoAnt;
	private Boolean indExigeEmAtend;
	private Perfil perfil;

	public CsePerfilProcessos() {
	}

	public CsePerfilProcessos(CsePerfilProcessosId id,
			CseProcessos processo, Boolean indExecuta, Boolean indConsulta,
			Boolean indAssina, DominioSituacao situacao, Date criadoEm,
			RapServidores servidor, Boolean indRegMedicoAnt,
			Boolean indExigeEmAtend) {
		this.id = id;
		this.processo = processo;
		this.indExecuta = indExecuta;
		this.indConsulta = indConsulta;
		this.indAssina = indAssina;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.indRegMedicoAnt = indRegMedicoAnt;
		this.indExigeEmAtend = indExigeEmAtend;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "rocSeq", column = @Column(name = "ROC_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "perNome", column = @Column(name = "PER_NOME", nullable = false, length = 30)) })
	public CsePerfilProcessosId getId() {
		return this.id;
	}

	public void setId(CsePerfilProcessosId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROC_SEQ", nullable = false, insertable = false, updatable = false)
	public CseProcessos getProcesso() {
		return this.processo;
	}

	public void setProcesso(CseProcessos processo) {
		this.processo = processo;
	}
	
	@Column(name = "IND_EXECUTA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExecuta() {
		return this.indExecuta;
	}

	public void setIndExecuta(Boolean indExecuta) {
		this.indExecuta = indExecuta;
	}
	
	@Column(name = "IND_CONSULTA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsulta() {
		return this.indConsulta;
	}

	public void setIndConsulta(Boolean indConsulta) {
		this.indConsulta = indConsulta;
	}
	
	@Column(name = "IND_ASSINA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAssina() {
		return this.indAssina;
	}

	public void setIndAssina(Boolean indAssina) {
		this.indAssina = indAssina;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "IND_REG_MEDICO_ANT", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRegMedicoAnt() {
		return this.indRegMedicoAnt;
	}

	public void setIndRegMedicoAnt(Boolean indRegMedicoAnt) {
		this.indRegMedicoAnt = indRegMedicoAnt;
	}
	
	@Column(name = "IND_EXIGE_EM_ATEND", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExigeEmAtend() {
		return this.indExigeEmAtend;
	}

	public void setIndExigeEmAtend(Boolean indExigeEmAtend) {
		this.indExigeEmAtend = indExigeEmAtend;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PER_NOME", referencedColumnName = "NOME", nullable = false, insertable = false, updatable = false)
	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}
	
	public enum Fields {
		ID("id"), SITUACAO("situacao"), SERVIDOR("servidor"), IND_ASSINA("indAssina"),
		ROC_SEQ("id.rocSeq"), PER_NOME("id.perNome"), IND_CONSULTA("indConsulta"),PERFIL("perfil"),
		IND_EXECUTA("indExecuta"), PROCESSO("processo")
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
		if (!(obj instanceof CsePerfilProcessos)) {
			return false;
		}
		CsePerfilProcessos other = (CsePerfilProcessos) obj;
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