package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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
@SequenceGenerator(name = "sigCcaSq1", sequenceName = "SIG_CCA_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_atd_consumos", schema = "agh")
public class SigCalculoAtdConsumo extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4423351486063029177L;

	private Integer seq;

	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	private SigCalculoAtdPermanencia calculoAtividadePermanencia;
	private FatProcedHospInternos procedHospInterno;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private FccCentroCustos centroCustos;
	private MbcCirurgias cirurgia;

	private BigDecimal qtde;

	private BigDecimal valorMedioDiretoInsumos = BigDecimal.ZERO;
	private BigDecimal valorMedioDiretoPessoas = BigDecimal.ZERO;
	private BigDecimal valorMedioDiretoEquipamentos = BigDecimal.ZERO;
	private BigDecimal valorMedioDiretoServicos = BigDecimal.ZERO;

	private BigDecimal valorMedioIndiretoInsumos = BigDecimal.ZERO;
	private BigDecimal valorMedioIndiretoPessoas = BigDecimal.ZERO;
	private BigDecimal valorMedioIndiretoEquipamentos = BigDecimal.ZERO;
	private BigDecimal valorMedioIndiretoServicos = BigDecimal.ZERO;

	private BigDecimal valorParcialPrevistoInsumos = BigDecimal.ZERO;
	private BigDecimal valorParcialPrevistoPessoas = BigDecimal.ZERO;
	private BigDecimal valorParcialPrevistoEquipamentos = BigDecimal.ZERO;
	private BigDecimal valorParcialPrevistoServicos = BigDecimal.ZERO;

	private BigDecimal valorParcialConsumidoInsumos = BigDecimal.ZERO;
	private BigDecimal valorParcialConsumidoPessoas = BigDecimal.ZERO;
	private BigDecimal valorParcialConsumidoEquipemantos = BigDecimal.ZERO;
	private BigDecimal valorParcialConsumidoServicos = BigDecimal.ZERO;

	private BigDecimal valorTotalInsumos = BigDecimal.ZERO;
	private BigDecimal valorTotalPessoas = BigDecimal.ZERO;
	private BigDecimal valorTotalEquipamentos = BigDecimal.ZERO;
	private BigDecimal valorTotalServicos = BigDecimal.ZERO;
	
	private AfaItemPreparoMdto itemPreparoMdto;
	private SigCategoriaConsumos categoriaConsumo;
	
	private Set<SigCalculoDetalheConsumo> sigCalculoDetalheConsumos;

	public SigCalculoAtdConsumo() {
	}

	public SigCalculoAtdConsumo(Integer seq) {
		this.setSeq(seq);
	}
	
	public SigCalculoAtdConsumo(Integer seq, BigDecimal valorParcialPrevistoInsumos, BigDecimal valorParcialConsumidoInsumos) {
		this.setSeq(seq);
		this.valorParcialPrevistoInsumos = valorParcialPrevistoInsumos;
		this.valorParcialConsumidoInsumos = valorParcialConsumidoInsumos;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCcaSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PHI_SEQ", nullable = true, referencedColumnName = "SEQ")
	public FatProcedHospInternos getProcedHospInterno() {
		return this.procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ocv_seq", nullable = true, referencedColumnName = "seq")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return this.sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CPP_SEQ", nullable = false)
	public SigCalculoAtdPermanencia getCalculoAtividadePermanencia() {
		return calculoAtividadePermanencia;
	}

	public void setCalculoAtividadePermanencia(SigCalculoAtdPermanencia calculoAtividadePermanencia) {
		this.calculoAtividadePermanencia = calculoAtividadePermanencia;
	}

	@Column(name = "qtde", nullable = false, precision = 10, scale = 4)
	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ")
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(final MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ITO_PTO_SEQ", referencedColumnName = "PTO_SEQ"),
			@JoinColumn(name = "ITO_SEQP", referencedColumnName = "SEQP") })
	public AfaItemPreparoMdto getItemPreparoMdto() {
		return itemPreparoMdto;
	}

	public void setItemPreparoMdto(AfaItemPreparoMdto itemPreparoMdto) {
		this.itemPreparoMdto = itemPreparoMdto;
	}

	public void setCategoriaConsumo(SigCategoriaConsumos categoriaConsumo) {
		this.categoriaConsumo = categoriaConsumo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTC_SEQ")
	public SigCategoriaConsumos getCategoriaConsumo() {
		return categoriaConsumo;
	}

	@Column(name = "VLR_MED_DIR_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioDiretoInsumos() {
		return valorMedioDiretoInsumos;
	}

	public void setValorMedioDiretoInsumos(BigDecimal valorMedioDiretoInsumos) {
		this.valorMedioDiretoInsumos = valorMedioDiretoInsumos;
	}

	@Column(name = "VLR_MED_DIR_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioDiretoPessoas() {
		return valorMedioDiretoPessoas;
	}

	public void setValorMedioDiretoPessoas(BigDecimal valorMedioDiretoPessoas) {
		this.valorMedioDiretoPessoas = valorMedioDiretoPessoas;
	}

	@Column(name = "VLR_MED_DIR_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioDiretoEquipamentos() {
		return valorMedioDiretoEquipamentos;
	}

	public void setValorMedioDiretoEquipamentos(BigDecimal valorMedioDiretoEquipamentos) {
		this.valorMedioDiretoEquipamentos = valorMedioDiretoEquipamentos;
	}

	@Column(name = "VLR_MED_DIR_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioDiretoServicos() {
		return valorMedioDiretoServicos;
	}

	public void setValorMedioDiretoServicos(BigDecimal valorMedioDiretoServicos) {
		this.valorMedioDiretoServicos = valorMedioDiretoServicos;
	}

	@Column(name = "VLR_MED_IND_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioIndiretoInsumos() {
		return valorMedioIndiretoInsumos;
	}

	public void setValorMedioIndiretoInsumos(BigDecimal valorMedioIndiretoInsumos) {
		this.valorMedioIndiretoInsumos = valorMedioIndiretoInsumos;
	}

	@Column(name = "VLR_MED_IND_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioIndiretoPessoas() {
		return valorMedioIndiretoPessoas;
	}

	public void setValorMedioIndiretoPessoas(BigDecimal valorMedioIndiretoPessoas) {
		this.valorMedioIndiretoPessoas = valorMedioIndiretoPessoas;
	}

	@Column(name = "VLR_MED_IND_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioIndiretoEquipamentos() {
		return valorMedioIndiretoEquipamentos;
	}

	public void setValorMedioIndiretoEquipamentos(BigDecimal valorMedioIndiretoEquipamentos) {
		this.valorMedioIndiretoEquipamentos = valorMedioIndiretoEquipamentos;
	}

	@Column(name = "VLR_MED_IND_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorMedioIndiretoServicos() {
		return valorMedioIndiretoServicos;
	}

	public void setValorMedioIndiretoServicos(BigDecimal valorMedioIndiretoServicos) {
		this.valorMedioIndiretoServicos = valorMedioIndiretoServicos;
	}

	@Column(name = "VLR_PAC_PREV_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialPrevistoInsumos() {
		return valorParcialPrevistoInsumos;
	}

	public void setValorParcialPrevistoInsumos(BigDecimal valorParcialPrevistoInsumos) {
		this.valorParcialPrevistoInsumos = valorParcialPrevistoInsumos;
	}

	@Column(name = "VLR_PAC_PREV_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialPrevistoPessoas() {
		return valorParcialPrevistoPessoas;
	}

	public void setValorParcialPrevistoPessoas(BigDecimal valorParcialPrevistoPessoas) {
		this.valorParcialPrevistoPessoas = valorParcialPrevistoPessoas;
	}

	@Column(name = "VLR_PAC_PREV_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialPrevistoServicos() {
		return valorParcialPrevistoServicos;
	}

	public void setValorParcialPrevistoServicos(BigDecimal valorParcialPrevistoServicos) {
		this.valorParcialPrevistoServicos = valorParcialPrevistoServicos;
	}

	@Column(name = "VLR_PAC_PREV_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialPrevistoEquipamentos() {
		return valorParcialPrevistoEquipamentos;
	}

	public void setValorParcialPrevistoEquipamentos(BigDecimal valorParcialPrevistoEquipamentos) {
		this.valorParcialPrevistoEquipamentos = valorParcialPrevistoEquipamentos;
	}

	@Column(name = "VLR_PAC_CONS_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialConsumidoInsumos() {
		return valorParcialConsumidoInsumos;
	}

	public void setValorParcialConsumidoInsumos(BigDecimal valorParcialConsumidoInsumos) {
		this.valorParcialConsumidoInsumos = valorParcialConsumidoInsumos;
	}

	@Column(name = "VLR_PAC_CONS_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialConsumidoEquipemantos() {
		return valorParcialConsumidoEquipemantos;
	}

	public void setValorParcialConsumidoEquipemantos(BigDecimal valorParcialConsumidoEquipemantos) {
		this.valorParcialConsumidoEquipemantos = valorParcialConsumidoEquipemantos;
	}

	@Column(name = "VLR_PAC_CONS_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialConsumidoPessoas() {
		return valorParcialConsumidoPessoas;
	}

	public void setValorParcialConsumidoPessoas(BigDecimal valorParcialConsumidoPessoas) {
		this.valorParcialConsumidoPessoas = valorParcialConsumidoPessoas;
	}

	@Column(name = "VLR_PAC_CONS_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorParcialConsumidoServicos() {
		return valorParcialConsumidoServicos;
	}

	public void setValorParcialConsumidoServicos(BigDecimal valorParcialConsumidoServicos) {
		this.valorParcialConsumidoServicos = valorParcialConsumidoServicos;
	}

	@Column(name = "VLR_TOT_INSUMOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorTotalInsumos() {
		return valorTotalInsumos;
	}

	public void setValorTotalInsumos(BigDecimal valorTotalInsumos) {
		this.valorTotalInsumos = valorTotalInsumos;
	}

	@Column(name = "VLR_TOT_EQUIPAMENTOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorTotalEquipamentos() {
		return valorTotalEquipamentos;
	}

	public void setValorTotalEquipamentos(BigDecimal valorTotalEquipamentos) {
		this.valorTotalEquipamentos = valorTotalEquipamentos;
	}

	@Column(name = "VLR_TOT_SERVICOS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorTotalServicos() {
		return valorTotalServicos;
	}

	public void setValorTotalServicos(BigDecimal valorTotalServicos) {
		this.valorTotalServicos = valorTotalServicos;
	}

	@Column(name = "VLR_TOT_PESSOAS", nullable = false, precision = 20, scale = 4)
	public BigDecimal getValorTotalPessoas() {
		return valorTotalPessoas;
	}

	public void setValorTotalPessoas(BigDecimal valorTotalPessoas) {
		this.valorTotalPessoas = valorTotalPessoas;
	}
	
	public enum Fields {

		SEQ("seq"),
		CTC_SEQ("categoriaConsumo.seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("rapServidores"),
		CALCULO_ATIVIDADE_PERMANENCIA("calculoAtividadePermanencia"),
		CALCULO_ATIVIDADE_PERMANENCIA_SEQ("calculoAtividadePermanencia.seq"),
		OBJETO_CUSTO_VERSAO("sigObjetoCustoVersoes"),
		OBJETO_CUSTO_VERSAO_SEQ("sigObjetoCustoVersoes.seq"),
		PROCEDIMENTO_HOSPITALAR_INTERNO("procedHospInterno"),
		PHI_SEQ("procedHospInterno.seq"),
		QUANTIDADE("qtde"),
		CENTRO_CUSTO("centroCustos"),
		CENTRO_CUSTO_CODIGO("centroCustos.codigo"),
		CIRURGIA("cirurgia"),
		VALOR_MEDIO_DIRETO_INSUMOS("valorMedioDiretoInsumos"),
		VALOR_MEDIO_DIRETO_PESSOAS("valorMedioDiretoPessoas"),
		VALOR_MEDIO_DIRETO_EQUIPAMENTOS("valorMedioDiretoEquipamentos"),
		VALOR_MEDIO_DIRETO_SERVICOS("valorMedioDiretoServicos"),
		VALOR_MEDIO_INDIRETO_INSUMOS("valorMedioIndiretoInsumos"),
		VALOR_MEDIO_INDIRETO_PESSOAS("valorMedioIndiretoPessoas"),
		VALOR_MEDIO_INDIRETO_EQUIPAMENTOS("valorMedioIndiretoEquipamentos"),
		VALOR_MEDIO_INDIRETO_SERVICOS("valorMedioIndiretoServicos"),
		VALOR_PARCIAL_PREVISTO_INSUMOS("valorParcialPrevistoInsumos"),
		VALOR_PARCIAL_PREVISTO_PESSOAS("valorParcialPrevistoPessoas"),
		VALOR_PARCIAL_PREVISTO_EQUIPAMENTOS("valorParcialPrevistoEquipamentos"),
		VALOR_PARCIAL_PREVISTO_SERVICOS("valorParcialPrevistoServicos"),
		VALOR_PARCIAL_CONSUMIDO_INSUMOS("valorParcialConsumidoInsumos"),
		VALOR_PARCIAL_CONSUMIDO_PESSOAS("valorParcialConsumidoPessoas"),
		VALOR_PARCIAL_CONSUMIDO_EQUIPAMENTOS("valorParcialConsumidoEquipemantos"),
		VALOR_PARCIAL_CONSUMIDO_SERVICOS("valorParcialConsumidoServicos"),
		VALOR_TOTAL_INSUMOS("valorTotalInsumos"),
		VALOR_TOTAL_PESSOAS("valorTotalPessoas"),
		VALOR_TOTAL_EQUIPAMENTOS("valorTotalEquipamentos"),
		VALOR_TOTAL_SERVICOS("valorTotalServicos"),
		CALCULO_DETALHE_CONSUMO("sigCalculoDetalheConsumos"),
		SIG_CATEGORIA_CONSUMO("categoriaConsumo"),
		SIG_CATEGORIA_CONSUMO_SEQ("categoriaConsumo.seq");
		
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
		if (!(obj instanceof SigCalculoAtdConsumo)) {
			return false;
		}
		SigCalculoAtdConsumo other = (SigCalculoAtdConsumo) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();

	}


	public void setSigCalculoDetalheConsumos(Set<SigCalculoDetalheConsumo> sigCalculoDetalheConsumos) {
		this.sigCalculoDetalheConsumos = sigCalculoDetalheConsumos;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "calculoAtividadeConsumo")
	public Set<SigCalculoDetalheConsumo> getSigCalculoDetalheConsumos() {
		return sigCalculoDetalheConsumos;
	}
}
