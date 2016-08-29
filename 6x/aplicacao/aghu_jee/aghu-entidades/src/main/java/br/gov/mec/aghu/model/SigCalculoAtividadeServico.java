package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCasSq1", sequenceName = "sig_cas_sq1", allocationSize = 1)
@Table(name = "sig_calculo_atividade_servicos", schema = "agh")
public class SigCalculoAtividadeServico extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigCalculoComponente sigCalculoComponentes;
	private ScoItensContrato scoItensContrato;
	private ScoAfContrato scoAfContrato;
	private BigDecimal vlrItemContrato;
	private BigDecimal peso;
	private SigAtividades sigAtividades;
	private SigAtividadeServicos sigAtividadeServicos;
	private SigDirecionadores sigDirecionadores;
	private Integer version;
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoServico servico;


	public SigCalculoAtividadeServico() {
	}

	public SigCalculoAtividadeServico(Integer seq, Date criadoEm, RapServidores rapServidores, SigCalculoComponente sigCalculoComponentes,
			ScoItensContrato scoItensContrato, ScoAfContrato scoAfContrato, BigDecimal vlrItemContrato, BigDecimal peso, SigAtividades sigAtividades,
			SigAtividadeServicos sigAtividadeServicos) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.sigCalculoComponentes = sigCalculoComponentes;
		this.scoItensContrato = scoItensContrato;
		this.scoAfContrato = scoAfContrato;
		this.vlrItemContrato = vlrItemContrato;
		this.peso = peso;
		this.sigAtividades = sigAtividades;
		this.sigAtividadeServicos = sigAtividadeServicos;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCasSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmt_seq", nullable = false, referencedColumnName = "seq")
	public SigCalculoComponente getSigCalculoComponentes() {
		return this.sigCalculoComponentes;
	}

	public void setSigCalculoComponentes(SigCalculoComponente sigCalculoComponentes) {
		this.sigCalculoComponentes = sigCalculoComponentes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "icon_seq", referencedColumnName = "seq")
	public ScoItensContrato getScoItensContrato() {
		return scoItensContrato;
	}

	public void setScoItensContrato(ScoItensContrato scoItensContrato) {
		this.scoItensContrato = scoItensContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dir_seq", referencedColumnName = "seq")
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "afco_seq", referencedColumnName = "seq")
	public ScoAfContrato getScoAfContrato() {
		return scoAfContrato;
	}

	public void setScoAfContrato(ScoAfContrato scoAfContrato) {
		this.scoAfContrato = scoAfContrato;
	}

	@Column(name = "vlr_item_contrato", nullable = false, precision = 18, scale = 4)
	public BigDecimal getVlrItemContrato() {
		return vlrItemContrato;
	}

	public void setVlrItemContrato(BigDecimal vlrItemContrato) {
		this.vlrItemContrato = vlrItemContrato;
	}

	@Column(name = "peso", nullable = false, precision = 14, scale = 5)
	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tvd_seq", referencedColumnName = "seq")
	public SigAtividades getSigAtividades() {
		return this.sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "arv_seq", referencedColumnName = "seq")
	public SigAtividadeServicos getSigAtividadeServicos() {
		return sigAtividadeServicos;
	}

	public void setSigAtividadeServicos(SigAtividadeServicos sigAtividadeServicos) {
		this.sigAtividadeServicos = sigAtividadeServicos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO", referencedColumnName = "NUMERO")
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	@Column(name = "version", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SRV_CODIGO")
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}


	public enum Fields {
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("rapServidores"),
		CALCULO_COMPONENTE("sigCalculoComponentes"),
		ITENS_CONTRATO("scoItensContrato"),
		AF_CONTRATO("scoAfContrato"),
		VALOR_ITEM_CONTRATO("vlrItemContrato"),
		PESO("peso"),
		ATIVIDADES("sigAtividades"),
		DIRECIONADOR("sigDirecionadores"),
		AUTORIZACAO_FORNECEDOR("autorizacaoForn"),
		ATIVIDADE_SERVICOS("sigAtividadeServicos"),
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
		if (!(obj instanceof SigCalculoAtividadeServico)) {
			return false;
		}
		SigCalculoAtividadeServico other = (SigCalculoAtividadeServico) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
