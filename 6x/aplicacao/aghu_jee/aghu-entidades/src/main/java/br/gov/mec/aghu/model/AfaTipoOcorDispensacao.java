package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUsoDispensacao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="afaTodSq1", sequenceName="AGH.AFA_TOD_SQ1", allocationSize = 1) 
@Table(name="AFA_TIPO_OCOR_DISPENSACOES", schema="AGH")

public class AfaTipoOcorDispensacao extends BaseEntitySeq<Short> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6825263599621241967L;

	private Short seq;
	
	private String descricao;
	
	private Date criadoEm;
	
	private DominioSituacao situacao;

	private RapServidores servidor;
	
	private DominioTipoUsoDispensacao tipoUso;
	
	private Integer version;
	
	private Boolean indEnvioDispensario;
	
	private enum TipoOcorDispensacaoExceptionCode implements BusinessExceptionCode{
		AFA_TOD_CK1
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaTodSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false)})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "IND_TIPO_USO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoUsoDispensacao getTipoUso() {
		return tipoUso;
	}

	public void setTipoUso(DominioTipoUsoDispensacao tipoUso) {
		this.tipoUso = tipoUso;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "IND_ENVIO_DISPENSARIO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEnvioDispensario() {
		return indEnvioDispensario;
	}

	public void setIndEnvioDispensario(Boolean indEnvioDispensario) {
		this.indEnvioDispensario = indEnvioDispensario;
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validarOcorrencia(){
		/*
		 * VALORE PADRÃO
		 */
		if (this.situacao == null){
			this.situacao = DominioSituacao.A;
		}
		
		if (this.tipoUso == null){
			this.tipoUso = DominioTipoUsoDispensacao.D;
		}
		
		if (this.indEnvioDispensario == null){
			this.indEnvioDispensario = Boolean.FALSE;
		}
		
		
		/*
		 * RESTRIÇÕES (CONSTRAINTS)
		 */
		  if (!this.situacao.equals(DominioSituacao.A) && !this.situacao.equals(DominioSituacao.I)){
			  throw new BaseRuntimeException(TipoOcorDispensacaoExceptionCode.AFA_TOD_CK1);
		  }
		 	
	}

	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		SITUACAO("situacao"),
		TIPO_USO("tipoUso"),		
		IND_ENVIO_DISPENSARIO("indEnvioDispensario"),
		SERVIDOR("servidor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	@Transient
	public String getSeqEDescricao(){
		return getSeq() + "   " + getDescricao();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AfaTipoOcorDispensacao)) {
			return false;
		}
		AfaTipoOcorDispensacao castOther = (AfaTipoOcorDispensacao) other;
		return new EqualsBuilder().append(this.getSeq(), castOther.getSeq())
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}
	
	@Transient
	public String getDescricaoTrunc(Long size){
		if(size != null && getDescricao() != null && getDescricao().length() > size) {
			return getDescricao().substring(0,size.intValue()-2) + "...";
		} else {
			return getDescricao();
		}
	}
	
}