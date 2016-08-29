package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "AEL_GRUPO_X_MAT_ANALISES", schema = "AGH")
public class AelGrupoXMaterialAnalise extends BaseEntityId<AelGrupoXMaterialAnaliseId> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6130162224504904762L;

	private AelGrupoXMaterialAnaliseId id = new AelGrupoXMaterialAnaliseId();

	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	private AelGrupoMaterialAnalise grpMatAnal;
	private AelMateriaisAnalises matAnal;
	
	@EmbeddedId
	public AelGrupoXMaterialAnaliseId getId() {
		return id;
	}
	public void setId(AelGrupoXMaterialAnaliseId id) {
		this.id = id;
	}
	
	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name="VERSION")
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "GMA_SEQ", referencedColumnName = "SEQ",insertable=false,updatable=false) })
	public AelGrupoMaterialAnalise getGrpMatAnal() {
		return grpMatAnal;
	}
	public void setGrpMatAnal(AelGrupoMaterialAnalise grpMatAnal) {
		this.grpMatAnal = grpMatAnal;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "MAN_SEQ", referencedColumnName = "SEQ",insertable=false,updatable=false) })
	public AelMateriaisAnalises getMatAnal() {
		return matAnal;
	}
	public void setMatAnal(AelMateriaisAnalises matAnal) {
		this.matAnal = matAnal;
	}
	
	
	
	public enum Fields {
		GMA_SEQ("id.gmaSeq"),
		MAN_SEQ("id.manSeq"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		SERVIDOR("servidor"),
		GRUPO_MATERIAL("grpMatAnal"),
		MATERIAL_ANALISE("matAnal");

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
		if (!(obj instanceof AelGrupoXMaterialAnalise)) {
			return false;
		}
		AelGrupoXMaterialAnalise other = (AelGrupoXMaterialAnalise) obj;
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
