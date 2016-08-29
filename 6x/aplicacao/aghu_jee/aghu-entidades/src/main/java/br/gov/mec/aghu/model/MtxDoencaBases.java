package br.gov.mec.aghu.model;

import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxDobSq1", sequenceName="AGH.MTX_DOB_SQ1", allocationSize = 1)
@Table(name = "MTX_DOENCA_BASES", schema = "AGH")
public class MtxDoencaBases extends BaseEntitySeq<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241760946687237217L;

	private Integer seq;
	private String descricao;
	private DominioTipoOrgao tipoOrgao;
	private Integer version;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
	private Date criadoEm;
	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxDobSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", length = 150, nullable = false)
	@Length(max = 150)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "TIPO_ORGAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}
	
	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
		@ManyToOne(fetch = FetchType.LAZY)
		
		public RapServidores getServidor() {
			return servidor;
		}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	
	public void setIndSituacao(DominioSituacao tipoSituacao) {
		this.indSituacao = tipoSituacao;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		DESCRICAO("descricao"),
		TIPO_ORGAO("tipoOrgao"),
		IND_SITUACAO("indSituacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getTipoOrgao());
        umHashCodeBuilder.append(this.getSeq());
        return umHashCodeBuilder.toHashCode();
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
		MtxDoencaBases other = (MtxDoencaBases) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());
        umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
        umEqualsBuilder.append(this.getTipoOrgao(), other.getTipoOrgao());
        return umEqualsBuilder.isEquals();
	}

	
}
