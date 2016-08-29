package br.gov.mec.aghu.view;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "V_SIG_CUSTOS_CALCULO_PROCED", schema="AGH")
@Immutable
public class VSigCustosCalculoProced extends BaseEntityId<VSigCustosCalculoProcedId> implements java.io.Serializable {
	
	private static final long serialVersionUID = 6269211682615670570L;
	
	private VSigCustosCalculoProcedId id;
	private Long iphCodTabela;
	private String iphDescricao;
	private Short cnvCodigo;
	private String compFat;
	private Integer conta;
	private BigDecimal custoTotalInsumos;
	private BigDecimal custoTotalPessoas;
	private BigDecimal custoTotalEquipamentos;
	private BigDecimal custoTotalServicos;
	private BigDecimal receitaTotal;
	private Integer prontuario;
	private AghAtendimentos atendimento;
	private SigCalculoAtdPaciente calculoAtdPaciente;
	private FatItensProcedHospitalar itemProcedimentoHospitalar;

	@EmbeddedId
	@AttributeOverrides( {
		@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ")),
		@AttributeOverride(name = "cacSeq", column = @Column(name = "CAC_SEQ")),
		@AttributeOverride(name = "pmuSeq", column = @Column(name = "PMU_SEQ")),
		@AttributeOverride(name = "principal", column = @Column(name = "CCP_IND_PRINCIPAL")),
		@AttributeOverride(name = "iphPhoSeq", column = @Column(name = "IPH_PHO_SEQ")),
		@AttributeOverride(name = "iphSeq", column = @Column(name = "IPH_SEQ"))
	})
	public VSigCustosCalculoProcedId getId() {
		return id;
	}
	
	public void setId(VSigCustosCalculoProcedId id) {
		this.id = id;
	}
	
	@Column(name="custo_total_insumos")
	public BigDecimal getCustoTotalInsumos() {
		return custoTotalInsumos;
	}
	
	public void setCustoTotalInsumos(BigDecimal custoTotalInsumos) {
		this.custoTotalInsumos = custoTotalInsumos;
	}
	
	@Column(name="custo_total_pessoas")
	public BigDecimal getCustoTotalPessoas() {
		return custoTotalPessoas;
	}
	
	public void setCustoTotalPessoas(BigDecimal custoTotalPessoas) {
		this.custoTotalPessoas = custoTotalPessoas;
	}
	
	@Column(name="custo_total_equipamentos")
	public BigDecimal getCustoTotalEquipamentos() {
		return custoTotalEquipamentos;
	}
	
	public void setCustoTotalEquipamentos(BigDecimal custoTotalEquipamentos) {
		this.custoTotalEquipamentos = custoTotalEquipamentos;
	}
	
	@Column(name="custo_total_servicos")
	public BigDecimal getCustoTotalServicos() {
		return custoTotalServicos;
	}
	
	public void setCustoTotalServicos(BigDecimal custoTotalServicos) {
		this.custoTotalServicos = custoTotalServicos;
	}
	
	@Column(name="receita_total")
	public BigDecimal getReceitaTotal() {
		return receitaTotal;
	}
	
	public void setReceitaTotal(BigDecimal receitaTotal) {
		this.receitaTotal = receitaTotal;
	}
	
	@Column(name="prontuario")
	public Integer getProntuario() {
		return prontuario;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false, updatable=false, insertable=false)
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CAC_SEQ", nullable = false, updatable=false, insertable=false)
	public SigCalculoAtdPaciente getCalculoAtdPaciente() {
		return calculoAtdPaciente;
	}
	
	public void setCalculoAtdPaciente(SigCalculoAtdPaciente calculoAtdPaciente) {
		this.calculoAtdPaciente = calculoAtdPaciente;
	}

	@Column(name="iph_cod_tabela")
	public Long getIphCodTabela() {
		return iphCodTabela;
	}
	
	public void setIphCodTabela(Long iphCodTabela) {
		this.iphCodTabela = iphCodTabela;
	}

	@Column(name="iph_descricao")
	public String getIphDescricao() {
		return iphDescricao;
	}
	
	public void setIphDescricao(String iphDescricao) {
		this.iphDescricao = iphDescricao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "IPH_PHO_SEQ", nullable=true, insertable = false, updatable = false),
			@JoinColumn(name = "IPH_SEQ", nullable=true, insertable = false, updatable = false) })
	public FatItensProcedHospitalar getItemProcedimentoHospitalar() {
		return this.itemProcedimentoHospitalar;
	}

	public void setItemProcedimentoHospitalar(FatItensProcedHospitalar itemProcedimentoHospitalar) {
		this.itemProcedimentoHospitalar = itemProcedimentoHospitalar;
	}

	@Column(name="cnv_codigo")
	public Short getCnvCodigo() {
		return cnvCodigo;
	}

	public void setCnvCodigo(Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}

	@Column(name="comp_fat")
	public String getCompFat() {
		return compFat;
	}

	public void setCompFat(String compFat) {
		this.compFat = compFat;
	}

	@Column(name="conta")
	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	public enum Fields {
		PMU_SEQ("id.pmuSeq"),
		PRINCIPAL("id.principal"),
		ATD_SEQ("id.atdSeq"),
		ATENDIMENTO("atendimento"),
		IPH_PHO_SEQ("id.iphPhoSeq"),
		IPH_SEQ("id.iphSeq"),
		ITEM_PROCEDIMENTO_HOSPITAL("itemProcedimentoHospitalar"),
		COD_TABELA("iphCodTabela"),
		DESCRICAO("iphDescricao"),
		CAC_SEQ("id.cac_seq"),
		CALCULO_ATENDIMENTO_PACIENTE("calculoAtdPaciente"),
		CUSTO_TOTAL_INSUMOS("custoTotalInsumos"),
		CUSTO_TOTAL_PESSOAS("custoTotalPessoas"),
		CUSTO_TOTAL_EQUIPAMENTOS("custoTotalEquipamentos"),
		CUSTO_TOTAL_SERVICOS("custoTotalServicos"),
		RECEITA_TOTAL("receitaTotal"),
		CNV_CODIGO("cnvCodigo"),
		COMP_FAT("compFat"),
		CONTA("conta")
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
}
