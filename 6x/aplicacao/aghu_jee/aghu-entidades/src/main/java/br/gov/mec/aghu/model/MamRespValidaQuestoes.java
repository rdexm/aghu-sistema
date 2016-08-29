package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MAM_RESP_VALIDA_QUESTOES", schema = "AGH")
public class MamRespValidaQuestoes extends BaseEntityId<MamRespValidaQuestoesId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5827321905047576626L;
	
	private MamRespValidaQuestoesId id;
	private String descricao;
	private String descricaoImpressao;
	private Integer ordem;
	private DominioSituacao situacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	public MamRespValidaQuestoes() {
	}

	public MamRespValidaQuestoes(MamRespValidaQuestoesId id, String descricao,
			String descricaoImpressao, Integer ordem, DominioSituacao situacao,
			Date criadoEm, RapServidores servidor) {
		this.id = id;
		this.descricao = descricao;
		this.descricaoImpressao = descricaoImpressao;
		this.ordem = ordem;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "qusQutSeq", column = @Column(name = "QUS_QUT_SEQ", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "qusSeqp", column = @Column(name = "QUS_SEQP", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)) })
	public MamRespValidaQuestoesId getId() {
		return id;
	}
	
	public void setId(MamRespValidaQuestoesId id) {
		this.id = id;
	}
	
	@Column(name = "DESCRICAO", length = 250)
	@Length(max = 250)
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Column(name = "DESCRICAO_IMPRESSAO", length = 250)
	@Length(max = 250)
	public String getDescricaoImpressao() {
		return descricaoImpressao;
	}
	
	public void setDescricaoImpressao(String descricaoImpressao) {
		this.descricaoImpressao = descricaoImpressao;
	}
	
	@Column(name = "ORDEM", nullable = false, precision = 6, scale = 0)
	public Integer getOrdem() {
		return ordem;
	}
	
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}
	
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		QUS_QUT_SEQ("id.qusQutSeq"),
		QUS_SEQP("id.qusSeqp"),
		SEQP("id.seqp"),
		DESCRICAO("descricao"),
		DESCRICAO_IMPRESSAO("descricaoImpressao"),
		ORDEM("ordem"),
		IND_SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo");
		
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
        umHashCodeBuilder.append(this.getId());
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getDescricaoImpressao());
        umHashCodeBuilder.append(this.getOrdem());
        umHashCodeBuilder.append(this.getSituacao());
        umHashCodeBuilder.append(this.getCriadoEm());
        return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MamRespValidaQuestoes other = (MamRespValidaQuestoes) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getId(), other.getId());
        umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
        umEqualsBuilder.append(this.getDescricaoImpressao(), other.getDescricaoImpressao());
        umEqualsBuilder.append(this.getOrdem(), other.getOrdem());
        umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
        umEqualsBuilder.append(this.getCriadoEm(), other.getCriadoEm());
        return umEqualsBuilder.isEquals();
	}
}
