package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;


@Entity
@Table(name = "SCO_CATALOGO_SICON", schema = "AGH")
public class ScoCatalogoSicon extends BaseEntityCodigo<Integer> implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 573228583071436982L;
	private Integer                 codigoSicon;
	private DominioTipoItemContrato tipoItemContrato;
	private String 					descricao;
	private RapServidores 			servidor;
	private Date 					criadoEm;
	private Date 					alteradoEm;
	private DominioSituacao 		situacao;
	private Integer 				version;

	// Construtor
	public ScoCatalogoSicon() {

	}

	@Id
	@Column(name = "CODIGO_SICON", length = 7, nullable = false)	
	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}
	

	@Column(name = "IND_TIPO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoItemContrato getTipoItemContrato() {
		return tipoItemContrato;
	}

	public void setTipoItemContrato(DominioTipoItemContrato tipoItemContrato) {
		this.tipoItemContrato = tipoItemContrato;
	}

	
	@Column(name = "DESCRICAO", length = 180)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	

	@Column(name = "CRIADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	

	@Column(name = "ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	
	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("codigoSicon", this.codigoSicon).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoCatalogoSicon)){
			return false;
		}
		ScoCatalogoSicon castOther = (ScoCatalogoSicon) other;
		return new EqualsBuilder().append(this.codigoSicon, castOther.getCodigoSicon())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.codigoSicon).toHashCode();
	}
	
	
	public enum Fields {
		CODIGO_SICON("codigoSicon"), 
		TIPOITEMCONTRATO("tipoItemContrato"),
		DESCRICAO("descricao"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"), 
		ALTERADO_EM("alteradoEm"), 
		SITUACAO("situacao"), 
		VERSION("version");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
 
 @Transient public Integer getCodigo(){ return this.getCodigoSicon();} 
 public void setCodigo(Integer codigo){ this.setCodigoSicon(codigo);}
}
