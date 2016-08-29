package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="aelGmaSq1", sequenceName="AGH.AEL_GMA_SQ1", allocationSize = 1)
//Sequence: ael_man_sq1 utilizado na DAO
@Table(name = "AEL_GRUPO_MATERIAL_ANALISES", schema = "AGH")
public class AelGrupoMaterialAnalise extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 451994969448063300L;

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		SERVIDOR("servidor"),
		ORD_PRONT_ONLINE("ordProntOnline"),
		GRUPO_MATERIAL("grupoXMaterial");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	private Integer seq;
	private String descricao;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
//	ser_matricula integer NOT NULL,
//	  ser_vin_codigo smallint NOT NULL,
	private Integer ordProntOnline;
	private Integer version;
	private Set<AelGrupoXMaterialAnalise> grupoXMaterial;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelGmaSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "DESCRICAO", nullable = false)
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criado_em) {
		this.criadoEm = criado_em;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	public void setIndSituacao(DominioSituacao ind_situacao) {
		this.indSituacao = ind_situacao;
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
	
	@Column(name = "ORD_PRONT_ONLINE", nullable = false)
	public Integer getOrdProntOnline() {
		return ordProntOnline;
	}
	public void setOrdProntOnline(Integer ord_pront_online) {
		this.ordProntOnline = ord_pront_online;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	

	@OneToMany(fetch = FetchType.LAZY,mappedBy="grpMatAnal")
	public Set<AelGrupoXMaterialAnalise> getGrupoXMaterial() {
		return grupoXMaterial;
	}
	public void setGrupoXMaterial(Set<AelGrupoXMaterialAnalise> materiaisAnalise) {
		this.grupoXMaterial = materiaisAnalise;
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
		if (!(obj instanceof AelGrupoMaterialAnalise)) {
			return false;
		}
		AelGrupoMaterialAnalise other = (AelGrupoMaterialAnalise) obj;
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
