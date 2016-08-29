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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@org.hibernate.annotations.Cache(usage=org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
@Table(name = "SCO_PONTOS_PARADA_SOLICITACOES", schema = "AGH")
@SequenceGenerator(name="scoPpsSq1", sequenceName="AGH.SCO_PPS_SQ1", allocationSize = 1)
public class ScoPontoParadaSolicitacao extends BaseEntityCodigo<Short> implements java.io.Serializable, Cloneable {
	
	private static final long serialVersionUID = 2797894804061838505L;

	private Short codigo;
	private String descricao;
	private DominioSituacao situacao;
	private Integer version;
	private Date criadoEm;
	private RapServidores servidor;
	private DominioTipoPontoParada tipoPontoParada;
	private Boolean exigeResponsavel;
	
	public ScoPontoParadaSolicitacao() {
	}

	public ScoPontoParadaSolicitacao(Short codigo, String descricao,
			Integer version) {
		super();
		this.codigo = codigo;
		this.descricao = descricao;
		this.version = version;
	}

	@Id
	@Column(name = "CODIGO", nullable = false)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="scoPpsSq1")
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	@Column(name="DESCRICAO", nullable = false)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "TIPO", length = 2, nullable = true)
	@Enumerated(EnumType.STRING)
	public DominioTipoPontoParada getTipoPontoParada() {
		return this.tipoPontoParada;
	}

	public void setTipoPontoParada(DominioTipoPontoParada tipoPontoParada) {
		this.tipoPontoParada = tipoPontoParada;
	}

    @Column(name = "EXIGE_RESPONSAVEL", nullable = false)
    @org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExigeResponsavel() {
		return this.exigeResponsavel;
	}

	public void setExigeResponsavel(Boolean exigeResponsavel) {
		this.exigeResponsavel = exigeResponsavel;
	}
	
	public enum Fields {
	    CODIGO("codigo"), 
	    DESCRICAO("descricao"), 
	    SITUACAO("situacao"), 
	    VERSION("version"),
  		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		TIPO("tipoPontoParada"),
		EXIGE_RESPONSAVEL("exigeResponsavel");
	    
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		return result;
	}

	/*@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}if (obj == null){
			return false;
		}if (getClass() != obj.getClass()){
			return false;
		}
		ScoPontoParadaSolicitacao other = (ScoPontoParadaSolicitacao) obj;
		if (codigo == null) {
			if (other.codigo != null){
				return false;
			}
		} else if (!codigo.equals(other.codigo)){
			return false;
		}
		return true;
	}*/
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ScoPontoParadaSolicitacao){
			ScoPontoParadaSolicitacao castOther = (ScoPontoParadaSolicitacao) other;
			return new EqualsBuilder()
				.append(this.codigo, castOther.getCodigo())
			.isEquals();
		}
		else {
			return false;
		}	
	}
	
	@Transient
	public String getCodigoDescricao() {
		return new StringBuilder().append(getCodigo()).append(" - ")
				.append(getDescricao()).toString();
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
}