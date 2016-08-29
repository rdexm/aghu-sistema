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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioTipoRescicaoContrato;
import br.gov.mec.aghu.core.persistence.BaseEntity;


/**
 * The persistent class for the sco_res_contratos database table.
 * 
 */
@Entity
@Table(name="SCO_RES_CONTRATOS", schema="AGH")
public class ScoResContrato implements BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 206140563368092129L;

	@Id
	@SequenceGenerator(name="SCO_RES_CONTRATOS_SEQ_GENERATOR", sequenceName="AGH.SCO_RCON_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCO_RES_CONTRATOS_SEQ_GENERATOR")
	private Integer seq;

	@Column(name = "ALTERADO_EM")	 
	@Temporal(TemporalType.DATE)
	private Date alteradoEm;

    @OneToOne
    @JoinColumn(name = "CONT_SEQ", referencedColumnName = "SEQ")
	private ScoContrato contrato;

	@Column(name = "CRIADO_EM", nullable = false)	 
	@Temporal(TemporalType.DATE)
	private Date criadoEm;

    @Temporal( TemporalType.DATE)
	@Column(name="DT_ASSINATURA")
	private Date dtAssinatura;

    @Temporal( TemporalType.DATE)
	@Column(name="DT_PUBLICACAO")
	private Date dtPublicacao;

	@Column(name = "IND_SITUACAO", length= 2, nullable = false)	 
	@Enumerated(EnumType.STRING)
	private DominioSituacaoEnvioContrato indSituacao;

	@Column(name = "IND_TIPO_RESCISAO", length= 1, nullable = false)
	@Enumerated(EnumType.STRING)
	private DominioTipoRescicaoContrato indTipoRescisao;

	@Column(name="JUSTIFICATIVA", length=80)
	private String justificativa;

	@Column(length=255)
	private String observacoes;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	private RapServidores servidor;


	@Column(name = "VERSION", length= 7)
	@Version
	private Integer version;
	
		
    public ScoResContrato() {
    }

	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}


	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getDtAssinatura() {
		return this.dtAssinatura;
	}

	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}

	public Date getDtPublicacao() {
		return this.dtPublicacao;
	}

	public void setDtPublicacao(Date dtPublicacao) {
		this.dtPublicacao = dtPublicacao;
	}


	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getObservacoes() {
		return this.observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public DominioSituacaoEnvioContrato getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoEnvioContrato indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioTipoRescicaoContrato getIndTipoRescisao() {
		return indTipoRescisao;
	}

	public void setIndTipoRescisao(DominioTipoRescicaoContrato indTipoRescisao) {
		this.indTipoRescisao = indTipoRescisao;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("seq",this.seq)
		.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoResContrato)){
			return false;
		}
		ScoResContrato castOther = (ScoResContrato) other;
		return new EqualsBuilder()
			.append(this.seq, castOther.getSeq())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.seq)
		.toHashCode();
	}
	
	public enum Fields {
		SEQ("seq"), 
		CONT_SEQ("contrato"), 
		JUSTIFICATIVA("justificativa"), 
		IND_TIPO_RESCICAO("indTipoRescisao"), 
		VERSION("version"),
		DT_ASSINATURA("dtAssinatura"), 
		DT_PUBLICACAO("dtPublicacao"), 		
		IND_SITUACAO("indSituacao"), 
		OBSERVACOES("observacoes"), 		
		CRIADO_EM("criadoEm"), 
		ALTERADO_EM("alteradoEm"), 
		SERVIDOR("servidor"), 			
	;
	
	private String field;

	private Fields(String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return this.field;
	}
}	

}