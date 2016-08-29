package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
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

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioAdministracao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAdministracao;
import br.gov.mec.aghu.dominio.DominioSituacaoManipulacao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricaoSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptSesSq1", sequenceName="AGH.MPT_SES_SQ1", allocationSize = 1)
@Table(name = "MPT_SESSAO", schema = "AGH")

public class MptSessao extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 3934601518964037741L;
	
	private Integer seq;
	private MptPrescricaoCiclo mptPrescricaoCiclo;
	private MptPrescricaoPaciente mptPrescricaoPaciente;	
	private Date dthrChegada;	
	private Date dthrInicio;	
	private Date dthrFim;	
	private Boolean indIntercorrencia;
	private DominioSituacaoSessao indSituacaoSessao;
	private DominioSituacaoPrescricaoSessao indSituacaoPrescricaoSessao;
	private DominioSituacaoManipulacao indSituacaoManipulacao;
	private DominioSituacaoAdministracao indSituacaoAdministracao;
	private Date criadoEm;	
	private RapServidores servidor;	
	private Integer version;

	private DominioSituacao tipoAtendimento;
	private Boolean indMedicamentoDomiciliar;
	private Boolean indImpressaoPulseira;
	private RapServidores responsavel;
	private DominioAdministracao tipoAdministracao;
	private Set<MptHorarioSessao> listMptHorarioSessao = new HashSet<MptHorarioSessao>(0);
	
	private Integer cloSeq;

	public MptSessao() {
		
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptSesSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CLO_SEQ", referencedColumnName = "seq")
	public MptPrescricaoCiclo getMptPrescricaoCiclo() {
		return mptPrescricaoCiclo;
	}


	public void setMptPrescricaoCiclo(MptPrescricaoCiclo mptPrescricaoCiclo) {
		this.mptPrescricaoCiclo = mptPrescricaoCiclo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "PTE_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
		@JoinColumn(name = "PTE_SEQ", referencedColumnName = "SEQ") })
	public MptPrescricaoPaciente getMptPrescricaoPaciente() {
		return mptPrescricaoPaciente;
	}


	public void setMptPrescricaoPaciente(MptPrescricaoPaciente mptPrescricaoPaciente) {
		this.mptPrescricaoPaciente = mptPrescricaoPaciente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_CHEGADA", length = 7)
	public Date getDthrChegada() {
		return dthrChegada;
	}


	public void setDthrChegada(Date dthrChegada) {
		this.dthrChegada = dthrChegada;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", length = 7)
	public Date getDthrInicio() {
		return dthrInicio;
	}


	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 7)
	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Column(name = "IND_INTERCORRENCIA", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndIntercorrencia() {
		return indIntercorrencia;
	}


	public void setIndIntercorrencia(Boolean indIntercorrencia) {
		this.indIntercorrencia = indIntercorrencia;
	}

	@Column(name = "IND_SITUACAO_SESSAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoSessao getIndSituacaoSessao() {
		return indSituacaoSessao;
	}


	public void setIndSituacaoSessao(DominioSituacaoSessao indSituacaoSessao) {
		this.indSituacaoSessao = indSituacaoSessao;
	}

	@Column(name = "IND_SITUACAO_PRESCRICAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoPrescricaoSessao getIndSituacaoPrescricaoSessao() {
		return indSituacaoPrescricaoSessao;
	}


	public void setIndSituacaoPrescricaoSessao(
			DominioSituacaoPrescricaoSessao indSituacaoPrescricaoSessao) {
		this.indSituacaoPrescricaoSessao = indSituacaoPrescricaoSessao;
	}

	@Column(name = "IND_SITUACAO_MANIPULACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoManipulacao getIndSituacaoManipulacao() {
		return indSituacaoManipulacao;
	}


	public void setIndSituacaoManipulacao(
			DominioSituacaoManipulacao indSituacaoManipulacao) {
		this.indSituacaoManipulacao = indSituacaoManipulacao;
	}

	@Column(name = "IND_SITUACAO_ADMINISTRACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAdministracao getIndSituacaoAdministracao() {
		return indSituacaoAdministracao;
	}


	public void setIndSituacaoAdministracao(
			DominioSituacaoAdministracao indSituacaoAdministracao) {
		this.indSituacaoAdministracao = indSituacaoAdministracao;
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

	@Column(name = "TIPO_ATENDIMENTO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getTipoAtendimento() {
		return tipoAtendimento;
	}


	public void setTipoAtendimento(DominioSituacao tipoAtendimento) {
		this.tipoAtendimento = tipoAtendimento;
	}
	
	@Column(name = "IND_MEDICAMENTO_DOMICILIAR", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndMedicamentoDomiciliar() {
		return indMedicamentoDomiciliar;
	}


	public void setIndMedicamentoDomiciliar(Boolean indMedicamentoDomiciliar) {
		this.indMedicamentoDomiciliar = indMedicamentoDomiciliar;
	}
	

	@Column(name = "IND_IMPRESSAO_PULSEIRA", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImpressaoPulseira() {
		return indImpressaoPulseira;
	}


	public void setIndImpressaoPulseira(Boolean indImpressaoPulseira) {
		this.indImpressaoPulseira = indImpressaoPulseira;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mptSessao")
	public Set<MptHorarioSessao> getListMptHorarioSessao() {
		return listMptHorarioSessao;
	}


	public void setListMptHorarioSessao(Set<MptHorarioSessao> listMptHorarioSessao) {
		this.listMptHorarioSessao = listMptHorarioSessao;
	}
	
	@Column(name="CLO_SEQ", insertable=false, updatable=false)
	public Integer getCloSeq() {
		return cloSeq;
	}
	
	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_RESP", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_RESP", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getResponsavel() {
		return responsavel;
	}


	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}
	
	@Column(name = "TIPO_ADM", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioAdministracao getTipoAdministracao() {
		return tipoAdministracao;
	}


	public void setTipoAdministracao(DominioAdministracao tipoAdministracao) {
		this.tipoAdministracao = tipoAdministracao;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		
		SEQ("seq"),
		MPT_PRESCRICAO_CICLO("mptPrescricaoCiclo"),
		CICLO("mptPrescricaoCiclo.seq"),
		CICLO_NUMERO("mptPrescricaoCiclo.ciclo"),
		MPT_PRESCRICAO_PACIENTE("mptPrescricaoPaciente"),
		MPT_PRESCRICAO_PACIENTE_ATD_SEQ("mptPrescricaoPaciente.id.atdSeq"),
		MPT_PRESCRICAO_PACIENTE_SEQ("mptPrescricaoPaciente.id.seq"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		IND_INTERCORRENCIA("indIntercorrencia"),
		IND_SITUACAOSESSAO("indSituacaoSessao"),
		IND_SITUACAOPRESCRICAOSESSAO("indSituacaoPrescricaoSessao"),
		IND_SITUACAOMANIPULACAO("indSituacaoManipulacao"),
		IND_SITUACAOADMINISTRACAO("indSituacaoAdministracao"),		
		TIPO_ATENDIMENTO("tipoAtendimento"),
		IND_MEDICAMENTO_DOMICILIAR("indMedicamentoDomiciliar"),
		IND_IMPRESSAO_PULSEIRA("indImpressaoPulseira"),		
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		RESPONSAVEL("responsavel"),		
		HORARIO_SESSAO("listMptHorarioSessao"),
		DTHR_CHEGADA("dthrChegada"),
		CLO_SEQ("cloSeq"),
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
	
	
//	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof MptSessao)) {
			return false;
		}
		MptSessao other = (MptSessao) obj;
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
