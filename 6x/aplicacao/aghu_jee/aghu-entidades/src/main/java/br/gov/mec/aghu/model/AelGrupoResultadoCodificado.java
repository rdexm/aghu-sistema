package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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


/**
 * The persistent class for the ael_grupo_resul_codificados database table.
 * 
 */
@Entity
@Table(name="AEL_GRUPO_RESUL_CODIFICADOS", schema = "AGH")
@SequenceGenerator(name = "aelGtcSq1", sequenceName = "AGH.AEL_GTC_SQ1", allocationSize = 1)
public class AelGrupoResultadoCodificado extends BaseEntitySeq<Integer> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3366696828041150935L;
	
	private Integer seq;
	private Date criadoEm;
	private String descricao;
	private DominioSituacao situacao;
	private RapServidores servidor;
	private Integer version;
	private List<AelResultadoCodificado> resultadosCodificados;
	private List<AelCampoLaudo> camposLaudo;

    public AelGrupoResultadoCodificado() {
    }


    @Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelGtcSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Version
	@Column(name = "VERSION", nullable = true)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 120)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name="IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
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

	@OneToMany(mappedBy="grupoResulCodificado")
	public List<AelResultadoCodificado> getResultadosCodificados() {
		return resultadosCodificados;
	}
	
	public void setResultadosCodificados(List<AelResultadoCodificado> resultadosCodificados) {
		this.resultadosCodificados = resultadosCodificados;
	}

	@OneToMany(mappedBy="grupoResultadoCodificado")
	public List<AelCampoLaudo> getCamposLaudo() {
		return camposLaudo;
	}
	
	public void setCamposLaudo(List<AelCampoLaudo> camposLaudo) {
		this.camposLaudo = camposLaudo;
	}

	
	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		SERVIDOR("servidor"),
		CAMPOS_LAUDO("camposLaudo");

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
		if (!(obj instanceof AelGrupoResultadoCodificado)) {
			return false;
		}
		AelGrupoResultadoCodificado other = (AelGrupoResultadoCodificado) obj;
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