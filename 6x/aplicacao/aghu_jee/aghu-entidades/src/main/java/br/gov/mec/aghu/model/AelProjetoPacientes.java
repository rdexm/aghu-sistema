package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;
import br.gov.mec.aghu.core.utils.StringUtil;

@Entity
@Table(name = "AEL_PROJETO_PACIENTES", schema = "AGH")
public class AelProjetoPacientes extends BaseEntityId<AelProjetoPacientesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9140650106384147275L;
	private AelProjetoPacientesId id;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date dtInicio;
	private Date dtFim;
	private DominioSituacao indSituacao;
	private String numero;
	private String complementoJustificativa;
	//private Integer jexSeq;
	private Set<AelDataRespostaProtocolos> aelDataRespostaProtocoloses = new HashSet<AelDataRespostaProtocolos>(
			0);

	private AelProjetoPesquisas projetoPesquisa;
	
	private AelJustificativaExclusoes justificativaExclusao;
	
	//Transient
	private String nomeProjetoPesquisador;
	
	public AelProjetoPacientes() {
	}

	public AelProjetoPacientes(AelProjetoPacientesId id, Date criadoEm,
			Integer serMatricula, Short serVinCodigo, Date dtInicio,
			DominioSituacao indSituacao) {
		this.id = id;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.dtInicio = dtInicio;
		this.indSituacao = indSituacao;
	}

	public AelProjetoPacientes(AelProjetoPacientesId id, Date criadoEm,
			Integer serMatricula, Short serVinCodigo, Date dtInicio, Date dtFim,
			DominioSituacao indSituacao, String numero, String complementoJustificativa,
			 AelJustificativaExclusoes justificativaExclusao,
			Set<AelDataRespostaProtocolos> aelDataRespostaProtocoloses) {
		this.id = id;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
		this.indSituacao = indSituacao;
		this.numero = numero;
		this.complementoJustificativa = complementoJustificativa;
		this.justificativaExclusao = justificativaExclusao;
		this.aelDataRespostaProtocoloses = aelDataRespostaProtocoloses;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pjqSeq", column = @Column(name = "PJQ_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)) })
	public AelProjetoPacientesId getId() {
		return this.id;
	}

	public void setId(AelProjetoPacientesId id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_INICIO", nullable = false, length = 7)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_FIM", length = 7)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "NUMERO", length = 20)
	@Length(max = 20)
	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name = "COMPLEMENTO_JUSTIFICATIVA", length = 500)
	@Length(max = 500)
	public String getComplementoJustificativa() {
		return this.complementoJustificativa;
	}

	public void setComplementoJustificativa(String complementoJustificativa) {
		this.complementoJustificativa = complementoJustificativa;
	}

//	@Column(name = "JEX_SEQ", precision = 5, scale = 0)
//	public Integer getJexSeq() {
//		return this.jexSeq;
//	}
//
//	public void setJexSeq(Integer jexSeq) {
//		this.jexSeq = jexSeq;
//	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aelProjetoPacientes")
	public Set<AelDataRespostaProtocolos> getAelDataRespostaProtocoloses() {
		return this.aelDataRespostaProtocoloses;
	}
	
	public void setAelDataRespostaProtocoloses(
			Set<AelDataRespostaProtocolos> aelDataRespostaProtocoloses) {
		this.aelDataRespostaProtocoloses = aelDataRespostaProtocoloses;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PJQ_SEQ", insertable = false, updatable = false)
	public AelProjetoPesquisas getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JEX_SEQ", insertable = false, updatable = false)
	public AelJustificativaExclusoes getJustificativaExclusao() {
		return justificativaExclusao;
	}

	public void setJustificativaExclusao(
			AelJustificativaExclusoes justificativaExclusao) {
		this.justificativaExclusao = justificativaExclusao;
	}



	public enum Fields {
		PAC_CODIGO("id.pacCodigo"),
		PJQ_SEQ("id.pjqSeq"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		JUSTIFICATIVA_EXCLUSAO("justificativaExclusao"),
		JUSTIFICATIVA_EXCLUSAO_SEQ("justificativaExclusao.seq"),
		PROJETO_PESQUISA("projetoPesquisa"),
		SEQ("id"),
		IND_SITUACAO("indSituacao")
		; 

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AelProjetoPacientes)) {
			return false;
		}
		AelProjetoPacientes other = (AelProjetoPacientes) obj;
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
	
	@Transient
	public String getNomeProjetoPesquisador() {
		return nomeProjetoPesquisador;
	}

	public void setNomeProjetoPesquisador(String nomeProjetoPesquisador) {
		this.nomeProjetoPesquisador = nomeProjetoPesquisador;
	}
	
	public String getNomeProjetoPesquisadorTrunc(Long size) {
		return StringUtil.trunc(nomeProjetoPesquisador, true, size);
	}

}
