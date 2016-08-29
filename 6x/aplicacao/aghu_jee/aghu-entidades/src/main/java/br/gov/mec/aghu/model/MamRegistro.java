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

import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.dominio.DominioTipoFormularioAlta;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the mam_registros database table.
 * 
 */
@Entity
@SequenceGenerator(name="mamRgtSq1", sequenceName="AGH.MAM_RGT_SQ1", allocationSize = 1)
@Table(name = "MAM_REGISTROS")
public class MamRegistro extends BaseEntitySeq<Long> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1809431797419633048L;

	private Long seq;

	private AghAtendimentos atendimento;

	private Date criadoEm;	

	private AghEspecialidades especialidade;

	private Boolean indNoConsultorio;	
	
	private Boolean indPedeMotivo;	
	
	private DominioSituacaoRegistro indSituacao;
	
	/**
	 * chave estrangeira para AGH_MICROCOMPUTADORES
	 */
	private String micNome;
	
	/**
	 * chave estrangeira para MAM_MVTO_TRIAGENS
	 */
	private Integer mvrSeqp;
	
	/**
	 * chave estrangeira para MAM_MVTO_TRIAGENS
	 */
	private Long mvrTrgSeq;	
	
	private RapServidores servidor;	
	
	private DominioTipoFormularioAlta tipoFormularioAlta;	
	
	private MamTriagens triagem;	
	
	private AghUnidadesFuncionais unidadeFuncional;
	
	private Set<MamMotivoAtendimento> motivoAtendimentos;

	public MamRegistro() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamRgtSq1")
	@Column(unique = true, nullable = false)
	@NotNull
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESP_SEQ")
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}


	@Column(name = "IND_NO_CONSULTORIO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getIndNoConsultorio() {
		return this.indNoConsultorio;
	}

	public void setIndNoConsultorio(Boolean indNoConsultorio) {
		this.indNoConsultorio = indNoConsultorio;
	}

	@Column(name = "IND_PEDE_MOTIVO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	@NotNull
	public Boolean getIndPedeMotivo() {
		return this.indPedeMotivo;
	}

	public void setIndPedeMotivo(Boolean indPedeMotivo) {
		this.indPedeMotivo = indPedeMotivo;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioSituacaoRegistro getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoRegistro indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "MIC_NOME", length = 50)
	public String getMicNome() {
		return this.micNome;
	}

	public void setMicNome(String micNome) {
		this.micNome = micNome;
	}

	@Column(name = "MVR_SEQP")
	public Integer getMvrSeqp() {
		return this.mvrSeqp;
	}

	public void setMvrSeqp(Integer mvrSeqp) {
		this.mvrSeqp = mvrSeqp;
	}

	@Column(name = "MVR_TRG_SEQ")
	public Long getMvrTrgSeq() {
		return this.mvrTrgSeq;
	}

	public void setMvrTrgSeq(Long mvrTrgSeq) {
		this.mvrTrgSeq = mvrTrgSeq;
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
	
	

	@Column(name = "TIPO_FORMULARIO_ALTA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	@NotNull
	public DominioTipoFormularioAlta getTipoFormularioAlta() {
		return this.tipoFormularioAlta;
	}

	public void setTipoFormularioAlta(DominioTipoFormularioAlta tipoFormularioAlta) {
		this.tipoFormularioAlta = tipoFormularioAlta;
	}


	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRG_SEQ")
	public MamTriagens getTriagem() {
		return triagem;
	}

	public void setTriagem(MamTriagens triagem) {
		this.triagem = triagem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ")
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	@OneToMany(fetch = FetchType.LAZY, mappedBy = "registro")
	public Set<MamMotivoAtendimento> getMotivoAtendimentos() {
		return this.motivoAtendimentos;
	}

	public void setMotivoAtendimentos(Set<MamMotivoAtendimento> motivoAtendimentos) {
		this.motivoAtendimentos = motivoAtendimentos;
	}

	public enum Fields {
		
		IND_SITUACAO("indSituacao"),
		ATENDIMENTO("atendimento"),
		ATD_SEQ("atendimento.seq"),
		SEQ("seq"),
		TRIAGEM_SEQ("triagem"),
		IND_NO_CONSULTORIO("indNoConsultorio"),
		TIPO_FORMULARIO_ALTA("tipoFormularioAlta"),
		MVM_TRG("mvrTrgSeq"),
		TRG_SEQ("trg_seq"),
		CRIADO_EM("criadoEm");
		

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
		if (!(obj instanceof MamRegistro)) {
			return false;
		}
		MamRegistro other = (MamRegistro) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

}