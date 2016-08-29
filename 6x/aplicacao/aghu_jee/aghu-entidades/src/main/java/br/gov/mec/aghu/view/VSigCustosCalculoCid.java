package br.gov.mec.aghu.view;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "V_SIG_CUSTOS_CALCULO_CID", schema="AGH")
@Immutable
public class VSigCustosCalculoCid extends BaseEntityId<VSigCustosCalculoCidId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2318076315661991041L;
	
	private VSigCustosCalculoCidId id;
	private Integer cidSeq;
	
	private BigDecimal custoTotalInsumos;
	private BigDecimal custoTotalPessoas;
	private BigDecimal custoTotalEquipamentos;
	private BigDecimal custoTotalServicos;
	private BigDecimal receitaTotal;
	private Integer prontuario;
	private AghAtendimentos atendimento;
	private SigCalculoAtdPaciente calculoAtdPaciente;

	@EmbeddedId
	@AttributeOverrides( {
		@AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ")),
		@AttributeOverride(name = "cacSeq", column = @Column(name = "CAC_SEQ")),
		@AttributeOverride(name = "principal", column = @Column(name = "CID_IND_PRINCIPAL")),
		@AttributeOverride(name = "cidCodigo", column = @Column(name = "CID_CODIGO")),
		@AttributeOverride(name = "cidDescricao", column = @Column(name = "CID_DESCRICAO")),
		@AttributeOverride(name = "pmuSeq", column = @Column(name = "PMU_SEQ")) })

	public VSigCustosCalculoCidId getId() {
		return id;
	}
	public void setId(VSigCustosCalculoCidId id) {
		this.id = id;
	}

	@Column(name="cid_seq")
	public Integer getCidSeq() {
		return cidSeq;
	}
	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
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




	public enum Fields {

		CID_SEQ("cidSeq"),
		CID_CODIGO("id.cidCodigo"),
		CID_DESCRICAO("id.cidDescricao"),
		PMU_SEQ("id.pmuSeq"),
		CID_IND_PRINCIPAL("id.principal"),
		ATD_SEQ("id.atdSeq"),
		ATENDIMENTO("atendimento"),
		CALCULO_ATENDIMENTO_PACIENTE("calculoAtdPaciente");
		
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
