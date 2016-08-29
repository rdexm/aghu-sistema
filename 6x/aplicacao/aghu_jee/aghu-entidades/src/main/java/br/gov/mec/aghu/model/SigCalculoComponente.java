package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCmtSq1", sequenceName = "SIG_CMT_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_componentes", schema = "agh")
public class SigCalculoComponente extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 2857907117613168105L;

	private Integer seq;
	private SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeqSubProduto;
	private SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeq;
	private SigProcessamentoCusto sigProcessamentoCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigObjetoCustoComposicoes sigObjetoCustoComposicoes;
	private SigDirecionadores sigDirecionadores;
	private BigDecimal vlrInsumos = BigDecimal.ZERO;
	private BigDecimal vlrPessoal = BigDecimal.ZERO;
	private BigDecimal vlrEquipamento = BigDecimal.ZERO;
	private BigDecimal vlrServicos = BigDecimal.ZERO;
	private BigDecimal vlrAtividade = BigDecimal.ZERO;
	private Integer version;

	private Set<SigCalculoAtividadeInsumo> sigCalculoAtividadeInsumoses = new HashSet<SigCalculoAtividadeInsumo>(0);
	private Set<SigCalculoAtividadePessoa> sigCalculoAtividadePessoas = new HashSet<SigCalculoAtividadePessoa>(0);

	public SigCalculoComponente() {
	}
	
	public SigCalculoComponente(Integer seq) {
		this.seq = seq;
	}

	public SigCalculoComponente(Integer seq, SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm,
			RapServidores rapServidores, SigObjetoCustoComposicoes sigObjetoCustoComposicoes, SigDirecionadores sigDirecionadores, BigDecimal vlrInsumos,
			BigDecimal vlrPessoal, BigDecimal vlrEquipamento, BigDecimal vlrServicos, BigDecimal vlrAtividade) {
		this.seq = seq;
		this.sigCalculoObjetoCustosByCbjSeq = sigCalculoObjetoCustosByCbjSeq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.sigObjetoCustoComposicoes = sigObjetoCustoComposicoes;
		this.sigDirecionadores = sigDirecionadores;
		this.vlrInsumos = vlrInsumos;
		this.vlrPessoal = vlrPessoal;
		this.vlrEquipamento = vlrEquipamento;
		this.vlrServicos = vlrServicos;
		this.vlrAtividade = vlrAtividade;
	}

	public SigCalculoComponente(Integer seq, SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeqSubProduto,
			SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm, RapServidores rapServidores,
			SigObjetoCustoComposicoes sigObjetoCustoComposicoes, SigDirecionadores sigDirecionadores, BigDecimal vlrInsumos, BigDecimal vlrPessoal,
			BigDecimal vlrEquipamento, BigDecimal vlrServicos, BigDecimal vlrAtividade, Set<SigCalculoAtividadeInsumo> sigCalculoAtividadeInsumoses) {
		this.seq = seq;
		this.sigCalculoObjetoCustosByCbjSeqSubProduto = sigCalculoObjetoCustosByCbjSeqSubProduto;
		this.sigCalculoObjetoCustosByCbjSeq = sigCalculoObjetoCustosByCbjSeq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.sigObjetoCustoComposicoes = sigObjetoCustoComposicoes;
		this.sigDirecionadores = sigDirecionadores;
		this.vlrInsumos = vlrInsumos;
		this.vlrPessoal = vlrPessoal;
		this.vlrEquipamento = vlrEquipamento;
		this.vlrServicos = vlrServicos;
		this.vlrAtividade = vlrAtividade;
		this.sigCalculoAtividadeInsumoses = sigCalculoAtividadeInsumoses;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCmtSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq_sub_produto", referencedColumnName = "seq")
	public SigCalculoObjetoCusto getSigCalculoObjetoCustosByCbjSeqSubProduto() {
		return this.sigCalculoObjetoCustosByCbjSeqSubProduto;
	}

	public void setSigCalculoObjetoCustosByCbjSeqSubProduto(SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeqSubProduto) {
		this.sigCalculoObjetoCustosByCbjSeqSubProduto = sigCalculoObjetoCustosByCbjSeqSubProduto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq", nullable = false, referencedColumnName = "seq")
	public SigCalculoObjetoCusto getSigCalculoObjetoCustosByCbjSeq() {
		return this.sigCalculoObjetoCustosByCbjSeq;
	}

	public void setSigCalculoObjetoCustosByCbjSeq(SigCalculoObjetoCusto sigCalculoObjetoCustosByCbjSeq) {
		this.sigCalculoObjetoCustosByCbjSeq = sigCalculoObjetoCustosByCbjSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmu_seq", nullable = false, referencedColumnName = "seq")
	public SigProcessamentoCusto getSigProcessamentoCustos() {
		return this.sigProcessamentoCustos;
	}

	public void setSigProcessamentoCustos(SigProcessamentoCusto sigProcessamentoCustos) {
		this.sigProcessamentoCustos = sigProcessamentoCustos;
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
	@JoinColumn(name = "cbt_seq", nullable = false, referencedColumnName = "seq")
	public SigObjetoCustoComposicoes getSigObjetoCustoComposicoes() {
		return this.sigObjetoCustoComposicoes;
	}

	public void setSigObjetoCustoComposicoes(SigObjetoCustoComposicoes sigObjetoCustoComposicoes) {
		this.sigObjetoCustoComposicoes = sigObjetoCustoComposicoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dir_seq", referencedColumnName = "seq")
	public SigDirecionadores getSigDirecionadores() {
		return this.sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@Column(name = "vlr_insumos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrInsumos() {
		return this.vlrInsumos;
	}

	public void setVlrInsumos(BigDecimal vlrInsumos) {
		this.vlrInsumos = vlrInsumos;
	}

	@Column(name = "vlr_pessoal", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrPessoal() {
		return this.vlrPessoal;
	}

	public void setVlrPessoal(BigDecimal vlrPessoal) {
		this.vlrPessoal = vlrPessoal;
	}

	@Column(name = "vlr_equipamento", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrEquipamento() {
		return this.vlrEquipamento;
	}

	public void setVlrEquipamento(BigDecimal vlrEquipamento) {
		this.vlrEquipamento = vlrEquipamento;
	}

	@Column(name = "vlr_servicos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrServicos() {
		return this.vlrServicos;
	}

	public void setVlrServicos(BigDecimal vlrServicos) {
		this.vlrServicos = vlrServicos;
	}

	@Column(name = "vlr_atividade", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrAtividade() {
		return this.vlrAtividade;
	}

	public void setVlrAtividade(BigDecimal vlrAtividade) {
		this.vlrAtividade = vlrAtividade;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoComponentes")
	public Set<SigCalculoAtividadeInsumo> getSigCalculoAtividadeInsumoses() {
		return this.sigCalculoAtividadeInsumoses;
	}

	public void setSigCalculoAtividadeInsumoses(Set<SigCalculoAtividadeInsumo> sigCalculoAtividadeInsumoses) {
		this.sigCalculoAtividadeInsumoses = sigCalculoAtividadeInsumoses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoComponentes")
	public Set<SigCalculoAtividadePessoa> getSigCalculoAtividadePessoas() {
		return sigCalculoAtividadePessoas;
	}

	public void setSigCalculoAtividadePessoas(Set<SigCalculoAtividadePessoa> sigCalculoAtividadePessoas) {
		this.sigCalculoAtividadePessoas = sigCalculoAtividadePessoas;
	}

	public enum Fields {

		SEQ("seq"),
		CALCULO_OBJETO_CUSTO_CBJSEQSUBPRODUTO("sigCalculoObjetoCustosByCbjSeqSubProduto"),
		CALCULO_OBJETO_CUSTO_CBJSEQ("sigCalculoObjetoCustosByCbjSeq"),
		PROCESSAMENTO_CUSTO("sigProcessamentoCustos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		OBJETO_CUSTO_COMPOSICAO("sigObjetoCustoComposicoes"),
		DIRECIONADOR("sigDirecionadores"),
		VALOR_INSUMO("vlrInsumos"),
		VALOR_PESSOAL("vlrPessoal"),
		VALOR_EQUIPAMENTO("vlrEquipamento"),
		VALOR_SERVICO("vlrServicos"),
		VALOR_ATIVIDADE("vlrAtividade"),
		LISTA_CALCULO_ATIVIDADE_INSUMO("sigCalculoAtividadeInsumoses"),
		LISTA_CALCULO_ATIVIDADE_PESSOAS("sigCalculoAtividadePessoas");

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
		if (!(obj instanceof SigCalculoComponente)) {
			return false;
		}
		SigCalculoComponente other = (SigCalculoComponente) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}

}
