package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioTipoCalculo;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCbjSq1", sequenceName = "SIG_CBJ_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_objeto_custos", schema = "agh")
public class SigCalculoObjetoCusto extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 7495061869213668204L;

	private Integer seq;
	private SigProcessamentoCusto sigProcessamentoCustos;
	private RapServidores rapServidores;
	private Date criadoEm;
	private FccCentroCustos fccCentroCustos;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private BigDecimal qtdeProduzida;
	private BigDecimal vlrRateioInsumos = BigDecimal.ZERO;
	private BigDecimal vlrRateioPessoas = BigDecimal.ZERO;
	private BigDecimal vlrRateioEquipamentos = BigDecimal.ZERO;
	private BigDecimal vlrRateioServico = BigDecimal.ZERO;;
	private BigDecimal vlrRateioIndiretos = BigDecimal.ZERO;
	private BigDecimal vlrAtvInsumos = BigDecimal.ZERO;
	private BigDecimal vlrAtvPessoal = BigDecimal.ZERO;
	private BigDecimal vlrAtvEquipamento = BigDecimal.ZERO;
	private BigDecimal vlrAtvServicos = BigDecimal.ZERO;
	private BigDecimal vlrIndInsumos = BigDecimal.ZERO;
	private BigDecimal vlrIndPessoas = BigDecimal.ZERO;
	private BigDecimal vlrIndEquipamentos = BigDecimal.ZERO;
	private BigDecimal vlrIndServicos = BigDecimal.ZERO;
	private BigDecimal vlrObjetoCusto = BigDecimal.ZERO;
	private DominioTipoCalculo tipoCalculo;
	private Boolean indComposicao;
	private AacPagador pagador;
	private Integer version;

	private Set<SigProducaoObjetoCusto> sigProducaoObjetoCustoses = new HashSet<SigProducaoObjetoCusto>(0);
	private Set<SigCalculoRateioInsumo> sigCalculoRateioInsumoses = new HashSet<SigCalculoRateioInsumo>(0);
	private Set<SigCalculoRateioPessoa> sigCalculoRateioPessoas = new HashSet<SigCalculoRateioPessoa>(0);
	private Set<SigCalculoComponente> sigCalculoComponentesesForCbjSeq = new HashSet<SigCalculoComponente>(0);
	private Set<SigCalculoComponente> sigCalculoComponentesesForCbjSeqSubProduto = new HashSet<SigCalculoComponente>(0);

	private BigDecimal vlrTotalDiretoInsumo;
	private BigDecimal vlrTotalDiretoPessoa;
	private BigDecimal vlrTotalDiretoEquipamento;
	private BigDecimal vlrTotalDiretoServico;
	private BigDecimal vlrTotalIndireto;

	public SigCalculoObjetoCusto() {
	}

	public SigCalculoObjetoCusto(Integer seq) {
		this.seq = seq;
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCbjSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmu_seq", nullable = false, referencedColumnName = "seq")
	public SigProcessamentoCusto getSigProcessamentoCustos() {
		return this.sigProcessamentoCustos;
	}

	public void setSigProcessamentoCustos(SigProcessamentoCusto sigProcessamentoCustos) {
		this.sigProcessamentoCustos = sigProcessamentoCustos;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cct_codigo", nullable = false, referencedColumnName = "codigo")
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ocv_seq", nullable = false, referencedColumnName = "seq")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return this.sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
	}

	@Column(name = "qtde_produzida", nullable = false, precision = 10, scale = 4)
	public BigDecimal getQtdeProduzida() {
		return this.qtdeProduzida;
	}

	public void setQtdeProduzida(BigDecimal qtdeProduzida) {
		this.qtdeProduzida = qtdeProduzida;
	}

	@Column(name = "vlr_rateio_insumos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrRateioInsumos() {
		return this.vlrRateioInsumos;
	}

	public void setVlrRateioInsumos(BigDecimal vlrRateioInsumos) {
		this.vlrRateioInsumos = vlrRateioInsumos;
	}

	@Column(name = "vlr_rateio_indiretos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrRateioIndiretos() {
		return this.vlrRateioIndiretos;
	}

	public void setVlrRateioIndiretos(BigDecimal vlrRateioIndiretos) {
		this.vlrRateioIndiretos = vlrRateioIndiretos;
	}

	@Column(name = "vlr_atv_insumos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrAtvInsumos() {
		return this.vlrAtvInsumos;
	}

	public void setVlrAtvInsumos(BigDecimal vlrAtvInsumos) {
		this.vlrAtvInsumos = vlrAtvInsumos;
	}

	@Column(name = "vlr_atv_pessoal", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrAtvPessoal() {
		return this.vlrAtvPessoal;
	}

	public void setVlrAtvPessoal(BigDecimal vlrAtvPessoal) {
		this.vlrAtvPessoal = vlrAtvPessoal;
	}

	@Column(name = "vlr_atv_equipamento", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrAtvEquipamento() {
		return this.vlrAtvEquipamento;
	}

	public void setVlrAtvEquipamento(BigDecimal vlrAtvEquipamento) {
		this.vlrAtvEquipamento = vlrAtvEquipamento;
	}

	@Column(name = "vlr_atv_servicos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrAtvServicos() {
		return this.vlrAtvServicos;
	}

	public void setVlrAtvServicos(BigDecimal vlrAtvServicos) {
		this.vlrAtvServicos = vlrAtvServicos;
	}

	@Column(name = "vlr_ind_insumos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndInsumos() {
		return vlrIndInsumos;
	}

	public void setVlrIndInsumos(BigDecimal vlrIndInsumos) {
		this.vlrIndInsumos = vlrIndInsumos;
	}

	@Column(name = "vlr_ind_pessoas", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndPessoas() {
		return vlrIndPessoas;
	}

	public void setVlrIndPessoas(BigDecimal vlrIndPessoas) {
		this.vlrIndPessoas = vlrIndPessoas;
	}

	@Column(name = "vlr_ind_equipamentos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndEquipamentos() {
		return vlrIndEquipamentos;
	}

	public void setVlrIndEquipamentos(BigDecimal vlrIndEquipamentos) {
		this.vlrIndEquipamentos = vlrIndEquipamentos;
	}

	@Column(name = "vlr_ind_servicos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrIndServicos() {
		return vlrIndServicos;
	}

	public void setVlrIndServicos(BigDecimal vlrIndServicos) {
		this.vlrIndServicos = vlrIndServicos;
	}

	@Column(name = "vlr_objeto_custo", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrObjetoCusto() {
		return this.vlrObjetoCusto;
	}

	public void setVlrObjetoCusto(BigDecimal vlrObjetoCusto) {
		this.vlrObjetoCusto = vlrObjetoCusto;
	}

	@Column(name = "vlr_rateio_pessoas", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrRateioPessoas() {
		return vlrRateioPessoas;
	}

	public void setVlrRateioPessoas(BigDecimal vlrRateioPessoas) {
		this.vlrRateioPessoas = vlrRateioPessoas;
	}

	@Column(name = "vlr_rateio_equipamentos", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrRateioEquipamentos() {
		return vlrRateioEquipamentos;
	}

	public void setVlrRateioEquipamentos(BigDecimal vlrRateioEquipamentos) {
		this.vlrRateioEquipamentos = vlrRateioEquipamentos;
	}

	@Column(name = "vlr_rateio_servico", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrRateioServico() {
		return vlrRateioServico;
	}

	public void setVlrRateioServico(BigDecimal vlrRateioServico) {
		this.vlrRateioServico = vlrRateioServico;
	}

	@Column(name = "tipo_calculo", nullable = false, length = 2)
	@Enumerated(EnumType.STRING)
	public DominioTipoCalculo getTipoCalculo() {
		return this.tipoCalculo;
	}

	public void setTipoCalculo(DominioTipoCalculo tipoCalculo) {
		this.tipoCalculo = tipoCalculo;
	}

	@Column(name = "ind_composicao", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndComposicao() {
		return this.indComposicao;
	}

	public void setIndComposicao(Boolean indComposicao) {
		this.indComposicao = indComposicao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoObjetoCustos")
	public Set<SigProducaoObjetoCusto> getSigProducaoObjetoCustoses() {
		return this.sigProducaoObjetoCustoses;
	}

	public void setSigProducaoObjetoCustoses(Set<SigProducaoObjetoCusto> sigProducaoObjetoCustoses) {
		this.sigProducaoObjetoCustoses = sigProducaoObjetoCustoses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoObjetoCustos")
	public Set<SigCalculoRateioInsumo> getSigCalculoRateioInsumoses() {
		return this.sigCalculoRateioInsumoses;
	}

	public void setSigCalculoRateioInsumoses(Set<SigCalculoRateioInsumo> sigCalculoRateioInsumoses) {
		this.sigCalculoRateioInsumoses = sigCalculoRateioInsumoses;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoObjetoCustos")
	public Set<SigCalculoRateioPessoa> getSigCalculoRateioPessoas() {
		return this.sigCalculoRateioPessoas;
	}

	public void setSigCalculoRateioPessoas(Set<SigCalculoRateioPessoa> sigCalculoRateioPessoas) {
		this.sigCalculoRateioPessoas = sigCalculoRateioPessoas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoObjetoCustosByCbjSeq")
	public Set<SigCalculoComponente> getSigCalculoComponentesesForCbjSeq() {
		return this.sigCalculoComponentesesForCbjSeq;
	}

	public void setSigCalculoComponentesesForCbjSeq(Set<SigCalculoComponente> sigCalculoComponentesesForCbjSeq) {
		this.sigCalculoComponentesesForCbjSeq = sigCalculoComponentesesForCbjSeq;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sigCalculoObjetoCustosByCbjSeqSubProduto")
	public Set<SigCalculoComponente> getSigCalculoComponentesesForCbjSeqSubProduto() {
		return this.sigCalculoComponentesesForCbjSeqSubProduto;
	}

	public void setSigCalculoComponentesesForCbjSeqSubProduto(Set<SigCalculoComponente> sigCalculoComponentesesForCbjSeqSubProduto) {
		this.sigCalculoComponentesesForCbjSeqSubProduto = sigCalculoComponentesesForCbjSeqSubProduto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PGD_SEQ", nullable = true)
	public AacPagador getPagador() {
		return this.pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public enum Fields {

		SEQ("seq"),
		PROCESSAMENTO_CUSTOS("sigProcessamentoCustos"),
		PROCESSAMENTO_CUSTOS_SEQ("sigProcessamentoCustos.seq"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		CRIADO_EM("criadoEm"),
		CENTRO_CUSTO("fccCentroCustos"),
		CENTRO_CUSTO_CODIGO("fccCentroCustos.codigo"),
		OBJETO_CUSTO_VERSAO("sigObjetoCustoVersoes"),
		QUANTIDADE_PRODUZIDA("qtdeProduzida"),
		VALOR_RATEIO_INSUMOS("vlrRateioInsumos"),
		VALOR_RATEIO_INDIRETOS("vlrRateioIndiretos"),
		VALOR_ATV_INSUMOS("vlrAtvInsumos"),
		VALOR_ATV_PESSOAL("vlrAtvPessoal"),
		VALOR_ATV_EQUIPAMENTO("vlrAtvEquipamento"),
		VALOR_ATV_SERVICO("vlrAtvServicos"),
		VALOR_IND_INSUMOS("vlrIndInsumos"),
		VALOR_IND_PESSOAS("vlrIndPessoas"),
		VALOR_IND_EQUIPAMENTOS("vlrIndEquipamentos"),
		VALOR_IND_SERVICOS("vlrIndServicos"),
		VALOR_OBJETO_CUSTO("vlrObjetoCusto"),
		VALOR_RATEIO_PESSOAS("vlrRateioPessoas"),
		VALOR_RATEIO_EQUIPAMENTOS("vlrRateioEquipamentos"),
		VALOR_RATEIO_SERVICO("vlrRateioServico"),
		TIPO_CALCULO("tipoCalculo"),
		COMPOSICAO("indComposicao"),
		LISTA_PRODUCAO_OBJETO_CUSTO("sigProducaoObjetoCustoses"),
		LISTA_CALCULO_RATEIO_INSUMOS("sigCalculoRateioInsumoses"),
		LISTA_CALCULO_RATEIO_PESSOAS("sigCalculoRateioPessoas"),
		LISTA_CALCULO_COMPONENTES_CBJSEQ("sigCalculoComponentesesForCbjSeq"),
		LISTA_CALCULO_COMPONENTES_CBJSEQSUBPRODUTO("sigCalculoComponentesesForCbjSeqSubProduto"),
		PAGADOR("pagador");

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
		if (!(obj instanceof SigCalculoObjetoCusto)) {
			return false;
		}
		SigCalculoObjetoCusto other = (SigCalculoObjetoCusto) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();
	}

	@Transient
	public BigDecimal getVlrTotalDiretoInsumo() {

		this.vlrTotalDiretoInsumo = BigDecimal.ZERO;

		if (this.getVlrAtvInsumos() != null) {
			this.vlrTotalDiretoInsumo = this.vlrTotalDiretoInsumo.add(this.getVlrAtvInsumos());
		}

		if (this.getVlrRateioInsumos() != null) {
			this.vlrTotalDiretoInsumo = this.vlrTotalDiretoInsumo.add(this.getVlrRateioInsumos());
		}

		return this.vlrTotalDiretoInsumo;
	}

	public void setVlrTotalDiretoInsumo(BigDecimal vlrTotalDiretoInsumo) {
		this.vlrTotalDiretoInsumo = vlrTotalDiretoInsumo;
	}

	@Transient
	public BigDecimal getVlrTotalDiretoPessoa() {

		this.vlrTotalDiretoPessoa = BigDecimal.ZERO;

		if (this.getVlrAtvPessoal() != null) {
			this.vlrTotalDiretoPessoa = this.vlrTotalDiretoPessoa.add(this.getVlrAtvPessoal());
		}

		if (this.getVlrRateioPessoas() != null) {
			this.vlrTotalDiretoPessoa = this.vlrTotalDiretoPessoa.add(this.getVlrRateioPessoas());
		}

		return vlrTotalDiretoPessoa;
	}

	public void setVlrTotalDiretoPessoa(BigDecimal vlrTotalDiretoPessoa) {
		this.vlrTotalDiretoPessoa = vlrTotalDiretoPessoa;
	}

	@Transient
	public BigDecimal getVlrTotalDiretoEquipamento() {

		this.vlrTotalDiretoEquipamento = BigDecimal.ZERO;

		if (this.getVlrAtvEquipamento() != null) {
			this.vlrTotalDiretoEquipamento = this.vlrTotalDiretoEquipamento.add(this.getVlrAtvEquipamento());
		}

		if (this.getVlrRateioEquipamentos() != null) {
			this.vlrTotalDiretoEquipamento = this.vlrTotalDiretoEquipamento.add(this.getVlrRateioEquipamentos());
		}

		return vlrTotalDiretoEquipamento;
	}

	public void setVlrTotalDiretoEquipamento(BigDecimal vlrTotalDiretoEquipamento) {
		this.vlrTotalDiretoEquipamento = vlrTotalDiretoEquipamento;
	}

	@Transient
	public BigDecimal getVlrTotalDiretoServico() {

		this.vlrTotalDiretoServico = BigDecimal.ZERO;

		if (this.getVlrAtvServicos() != null) {
			this.vlrTotalDiretoServico = this.vlrTotalDiretoServico.add(this.getVlrAtvServicos());
		}

		if (this.getVlrRateioServico() != null) {
			this.vlrTotalDiretoServico = this.vlrTotalDiretoServico.add(this.getVlrRateioServico());
		}

		return vlrTotalDiretoServico;
	}

	public void setVlrTotalDiretoServico(BigDecimal vlrTotalDiretoServico) {
		this.vlrTotalDiretoServico = vlrTotalDiretoServico;
	}

	@Transient
	public BigDecimal getVlrTotalIndireto() {

		this.vlrTotalIndireto = BigDecimal.ZERO;

		if (this.getVlrRateioIndiretos() != null) {
			this.vlrTotalIndireto = this.vlrTotalIndireto.add(this.getVlrRateioIndiretos());
		}

		return vlrTotalIndireto;
	}

	public void setVlrTotalIndireto(BigDecimal vlrTotalIndireto) {
		this.vlrTotalIndireto = vlrTotalIndireto;
	}

}
