package br.gov.mec.aghu.model;

import java.io.Serializable;
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;

import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.view.VSigCustosCalculoCid;
import br.gov.mec.aghu.view.VSigCustosCalculoProced;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCacSq1", sequenceName = "SIG_CAC_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_atd_pacientes", schema = "agh")
public class SigCalculoAtdPaciente extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;
	private DominioSituacaoCalculoPaciente situacaoCalculoPaciente;
	private SigProcessamentoCusto processamentoCusto;
	private AghAtendimentos atendimento;
	private AinInternacao internacao;
	private Boolean indPacientePediatrico;
	private Boolean indFatPendente = false;
	private AacPagador pagador;
	private MptTratamentoTerapeutico tratamentoTerapeutico;
	
	//Mapeamentos Bi-direcionais
	private Set<SigCalculoAtdCIDS> listSigCalculoAtdCIDS = new HashSet<SigCalculoAtdCIDS>(0);
	
	private Set<SigCalculoAtdProcedimentos> listSigCalculoAtdProcedimentos = new HashSet<SigCalculoAtdProcedimentos>(0);
	
	private Set<SigCalculoAtdPermanencia> calculoAtdPermanencias = new HashSet<SigCalculoAtdPermanencia>(0);
	
	private Set<VSigCustosCalculoCid> sigCustosCalculoCids = new HashSet<VSigCustosCalculoCid>(0);
	
	private Set<VSigCustosCalculoProced> sigCustosCalculoProcedimentos = new HashSet<VSigCustosCalculoProced>(0);

	public SigCalculoAtdPaciente() {
	}
	
	public SigCalculoAtdPaciente(Integer seq) {
		this.setSeq(seq);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCacSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "version", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_SITUACAO", nullable = true, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoCalculoPaciente getSituacaoCalculoPaciente() {
		return situacaoCalculoPaciente;
	}

	public void setSituacaoCalculoPaciente(DominioSituacaoCalculoPaciente situacaoCalculoPaciente) {
		this.situacaoCalculoPaciente = situacaoCalculoPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PMU_SEQ", nullable = false, referencedColumnName = "seq")
	public SigProcessamentoCusto getProcessamentoCusto() {
		return processamentoCusto;
	}

	public void setProcessamentoCusto(SigProcessamentoCusto processamentoCusto) {
		this.processamentoCusto = processamentoCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false)
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "INT_SEQ", nullable = true)
	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	@Column(name = "IND_PAC_PEDIATRICO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPacientePediatrico() {
		return indPacientePediatrico;
	}

	public void setIndPacientePediatrico(Boolean indPacientePediatrico) {
		this.indPacientePediatrico = indPacientePediatrico;
	}

	@Column(name = "IND_FAT_PENDENTE", nullable = true, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndFatPendente() {
		return indFatPendente;
	}

	public void setIndFatPendente(Boolean indFatPendente) {
		this.indFatPendente = indFatPendente;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PGD_SEQ", nullable = true)
	public AacPagador getPagador() {
		return this.pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtdPaciente")
	@Cascade({ org.hibernate.annotations.CascadeType.DELETE })
	public Set<SigCalculoAtdCIDS> getListSigCalculoAtdCIDS() {
		return listSigCalculoAtdCIDS;
	}

	public void setListSigCalculoAtdCIDS(Set<SigCalculoAtdCIDS> listSigCalculoAtdCIDS) {
		this.listSigCalculoAtdCIDS = listSigCalculoAtdCIDS;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRP_SEQ", nullable = true)
	public MptTratamentoTerapeutico getTratamentoTerapeutico() {
		return tratamentoTerapeutico;
	}

	public void setTratamentoTerapeutico(
			MptTratamentoTerapeutico tratamentoTerapeutico) {
		this.tratamentoTerapeutico = tratamentoTerapeutico;
	}

	public void setCalculoAtdPermanencias(Set<SigCalculoAtdPermanencia> calculoAtdPermanencias) {
		this.calculoAtdPermanencias = calculoAtdPermanencias;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtdPaciente")
	public Set<SigCalculoAtdPermanencia> getCalculoAtdPermanencias() {
		return calculoAtdPermanencias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtdPaciente")
	public Set<VSigCustosCalculoCid> getSigCustosCalculoCids() {
		return sigCustosCalculoCids;
	}

	public void setSigCustosCalculoCids(
			Set<VSigCustosCalculoCid> sigCustosCalculoCids) {
		this.sigCustosCalculoCids = sigCustosCalculoCids;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtdPaciente")
	public Set<VSigCustosCalculoProced> getSigCustosCalculoProcedimentos() {
		return sigCustosCalculoProcedimentos;
	}

	public void setSigCustosCalculoProcedimentos(Set<VSigCustosCalculoProced> sigCustosCalculoProcedimentos) {
		this.sigCustosCalculoProcedimentos = sigCustosCalculoProcedimentos;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtdPaciente")
	public Set<SigCalculoAtdProcedimentos> getListSigCalculoAtdProcedimentos() {
		return listSigCalculoAtdProcedimentos;
	}

	public void setListSigCalculoAtdProcedimentos(
			Set<SigCalculoAtdProcedimentos> listSigCalculoAtdProcedimentos) {
		this.listSigCalculoAtdProcedimentos = listSigCalculoAtdProcedimentos;
	}
	
	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("rapServidores"),
		SITUACAO_CALCULO_PACIENTE("situacaoCalculoPaciente"),
		PROCESSAMENTO_CUSTO("processamentoCusto"),
		PROCESSAMENTO_CUSTO_SEQ("processamentoCusto.seq"),
		PMU_SEQ("processamentoCusto.seq"),
		INTERNACAO("internacao"),
		INTERNACAO_SEQ("internacao.seq"),
		ATENDIMENTO("atendimento"),
		ATD_SEQ("atendimento.seq"),
		LISTA_CALCULOS_CIDS("listSigCalculoAtdCIDS"),
		LISTA_CALCULO_ATD_PROCEDIMENTOS("listSigCalculoAtdProcedimentos"),
		ATENDIMENTO_SEQ("atendimento.seq"),
		CALCULOS_ATD_PERMANENCIAS("calculoAtdPermanencias"),
		SIG_CUSTOS_CALCULO_PROCEDIMENTOS("sigCustosCalculoProcedimentos"),
		PAGADOR("pagador"),
		IND_FAT_PENDENTE ("indFatPendente");

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
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigCalculoAtdPaciente)) {
			return false;
		}
		SigCalculoAtdPaciente other = (SigCalculoAtdPaciente) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}
}
