package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;


/**
 * The persistent class for the AEL_SISCAN_HISTO_COLO_CAD database table.
 * 
 */
@Entity
@Table(name="AEL_SISCAN_HISTO_COLO_CAD")
public class AelSiscanHistoColoCad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 437964773452487166L;
	private String codigo;
	private DominioSituacao indSituacao;
	private String nomeCampo;
	private String tagXml;
	private String tipo;
	private Integer version;
	private List<AelSiscanHistoColoRes> aelSiscanHistoColoRes;

    public AelSiscanHistoColoCad() {
    }


	@Id
	@Column(unique=true, nullable=false, length=60)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}


	@Column(name = "IND_SITUACAO",  length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}


	@Column(name="NOME_CAMPO", nullable=false, length=60)
	public String getNomeCampo() {
		return this.nomeCampo;
	}

	public void setNomeCampo(String nomeCampo) {
		this.nomeCampo = nomeCampo;
	}


	@Column(name="TAG_XML", nullable=false, length=60)
	public String getTagXml() {
		return this.tagXml;
	}

	public void setTagXml(String tagXml) {
		this.tagXml = tagXml;
	}


	@Column(nullable=false, length=5)
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to AelSiscanHistoColoRes
	@OneToMany(mappedBy="aelSiscanHistoColoCad")
	public List<AelSiscanHistoColoRes> getAelSiscanHistoColoRes() {
		return this.aelSiscanHistoColoRes;
	}

	public void setAelSiscanHistoColoRes(List<AelSiscanHistoColoRes> aelSiscanHistoColoRes) {
		this.aelSiscanHistoColoRes = aelSiscanHistoColoRes;
	}
	
	public enum Fields {

		CODIGO("codigo"),
		IND_SITUACAO("indSituacao"),
		NOME_CAMPO("nomeCampo"),
		TAG_XML("tagXml"),
		TIPO("tipo");
			
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
		result = prime * result + ((getCodigo() == null) ? 0 : getCodigo().hashCode());
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
		if (!(obj instanceof AelSiscanHistoColoCad)) {
			return false;
		}
		AelSiscanHistoColoCad other = (AelSiscanHistoColoCad) obj;
		if (getCodigo() == null) {
			if (other.getCodigo() != null) {
				return false;
			}
		} else if (!getCodigo().equals(other.getCodigo())) {
			return false;
		}
		return true;
	}
	
}