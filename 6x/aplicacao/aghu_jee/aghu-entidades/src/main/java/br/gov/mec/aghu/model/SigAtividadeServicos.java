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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigArvSq1", sequenceName = "SIG_ARV_SQ1", allocationSize = 1)
@Table(name = "SIG_ATIVIDADE_SERVICOS", schema = "AGH")
public class SigAtividadeServicos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 6983397879695133720L;

	private Integer seq;
	private SigAtividades sigAtividades;
	private ScoAfContrato scoAfContrato;
	private ScoItensContrato scoItensContrato;
	private SigDirecionadores sigDirecionadores;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private Integer version;
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoServico servico;
	private Double valorTotalItem;
	private Double estimadoMes;

	private Boolean emEdicao = Boolean.FALSE;
	
	public SigAtividadeServicos(){
		
	}
	
	public SigAtividadeServicos(Integer seq){
		this.seq = seq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigArvSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ", referencedColumnName = "SEQ")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AFCO_SEQ", referencedColumnName = "SEQ")
	public ScoAfContrato getScoAfContrato() {
		return scoAfContrato;
	}

	public void setScoAfContrato(ScoAfContrato scoAfContrato) {
		this.scoAfContrato = scoAfContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ICON_SEQ", referencedColumnName = "SEQ")
	public ScoItensContrato getScoItensContrato() {
		return scoItensContrato;
	}

	public void setScoItensContrato(ScoItensContrato scoItensContrato) {
		this.scoItensContrato = scoItensContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO", referencedColumnName = "NUMERO")
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SRV_CODIGO", referencedColumnName = "CODIGO")
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	@Transient
	public Double getValorTotalItem() {
		return valorTotalItem;
	}

	public void setValorTotalItem(Double valorTotalItem) {
		this.valorTotalItem = valorTotalItem;
	}

	@Transient
	public Double getEstimadoMes() {
		return estimadoMes;
	}

	public void setEstimadoMes(Double estimadoMes) {
		this.estimadoMes = estimadoMes;
	}

	public enum Fields {

		SEQ("seq"),
		ATIVIDADE("sigAtividades"),
		CONTRATO("scoAfContrato"),
		ITEM_CONTRATO("scoItensContrato"),
		DIRECIONADOR("sigDirecionadores"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		IND_SITUACAO("indSituacao"),
		ATIVIDADE_SEQ("sigAtividades.seq"),
		AUTORIZACAO_FORNEC("autorizacaoForn"),
		SERVICO("servico");

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
		if (!(obj instanceof SigAtividadeServicos)) {
			return false;
		}
		SigAtividadeServicos other = (SigAtividadeServicos) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
