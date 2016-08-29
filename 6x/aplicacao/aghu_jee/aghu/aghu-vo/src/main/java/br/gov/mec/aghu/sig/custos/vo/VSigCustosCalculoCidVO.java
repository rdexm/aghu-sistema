package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import br.gov.mec.aghu.model.AghCid;

public class VSigCustosCalculoCidVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2738094121614734176L;

	private Integer cidSeq;
	private BigDecimal custoTotal;
	private BigDecimal custoTotalInsumos;
	private BigDecimal custoTotalPessoas;
	private BigDecimal custoTotalEquipamentos;
	private BigDecimal custoTotalServicos;
	private BigDecimal receitaTotal;
	private Integer prontuario;
	private Integer atdSeq;
	private Integer cacSeq;
	private Boolean principal;
	private String cidCodigo;
	private String cidDescricao;
	private Integer pmuSeq;
	private Long quantidade;
	private Integer cidSeqSecundario;
	private String cidCodigoSecundario;
	private String cidDescricaoSecundario;
	private BigDecimal custoTotalSecundario;
	private BigDecimal receitaTotalSecundario;
	private Long quantidadeSecundario;
	private String pacNome;
	private Boolean segundoNivel;
	private Boolean terceiroNivel;
	private AghCid cid;
	private Boolean cidPrincipal;
	private List<Integer> listaAtdSeq;
	
	public Boolean getTerceiroNivel() {
		return terceiroNivel;
	}
	public void setTerceiroNivel(Boolean terceiroNivel) {
		this.terceiroNivel = terceiroNivel;
	}
	
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public Integer getCidSeq() {
		return cidSeq;
	}
	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}
	public BigDecimal getCustoTotalInsumos() {
		return custoTotalInsumos;
	}
	public void setCustoTotalInsumos(BigDecimal custoTotalInsumos) {
		this.custoTotalInsumos = custoTotalInsumos;
	}
	public BigDecimal getCustoTotalPessoas() {
		return custoTotalPessoas;
	}
	public void setCustoTotalPessoas(BigDecimal custoTotalPessoas) {
		this.custoTotalPessoas = custoTotalPessoas;
	}
	public BigDecimal getCustoTotalEquipamentos() {
		return custoTotalEquipamentos;
	}
	public void setCustoTotalEquipamentos(BigDecimal custoTotalEquipamentos) {
		this.custoTotalEquipamentos = custoTotalEquipamentos;
	}
	public BigDecimal getCustoTotalServicos() {
		return custoTotalServicos;
	}
	public void setCustoTotalServicos(BigDecimal custoTotalServicos) {
		this.custoTotalServicos = custoTotalServicos;
	}
	public BigDecimal getReceitaTotal() {
		return receitaTotal;
	}
	public void setReceitaTotal(BigDecimal receitaTotal) {
		this.receitaTotal = receitaTotal;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getCacSeq() {
		return cacSeq;
	}
	public void setCacSeq(Integer cacSeq) {
		this.cacSeq = cacSeq;
	}
	public Boolean getPrincipal() {
		return principal;
	}
	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}
	public String getCidCodigo() {
		return cidCodigo;
	}
	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}
	public String getCidDescricao() {
		return cidDescricao;
	}
	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}
	public Integer getPmuSeq() {
		return pmuSeq;
	}
	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}
	public BigDecimal getCustoTotal() {
		return custoTotal;
	}
	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}
	
	public Integer getCidSeqSecundario() {
		return cidSeqSecundario;
	}
	public void setCidSeqSecundario(Integer cidSeqSecundario) {
		this.cidSeqSecundario = cidSeqSecundario;
	}
	public String getCidCodigoSecundario() {
		return cidCodigoSecundario;
	}
	public void setCidCodigoSecundario(String cidCodigoSecundario) {
		this.cidCodigoSecundario = cidCodigoSecundario;
	}
	public String getCidDescricaoSecundario() {
		return cidDescricaoSecundario;
	}
	public void setCidDescricaoSecundario(String cidDescricaoSecundario) {
		this.cidDescricaoSecundario = cidDescricaoSecundario;
	}
	public BigDecimal getCustoTotalSecundario() {
		return custoTotalSecundario;
	}
	public void setCustoTotalSecundario(BigDecimal custoTotalSecundario) {
		this.custoTotalSecundario = custoTotalSecundario;
	}
	public BigDecimal getReceitaTotalSecundario() {
		return receitaTotalSecundario;
	}
	public void setReceitaTotalSecundario(BigDecimal receitaTotalSecundario) {
		this.receitaTotalSecundario = receitaTotalSecundario;
	}
	public Long getQuantidadeSecundario() {
		return quantidadeSecundario;
	}
	public void setQuantidadeSecundario(Long quantidadeSecundario) {
		this.quantidadeSecundario = quantidadeSecundario;
	}
	
	public String getPacNome() {
		return pacNome;
	}
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Boolean getSegundoNivel() {
		return segundoNivel;
	}
	public void setSegundoNivel(Boolean segundoNivel) {
		this.segundoNivel = segundoNivel;
	}

	public List<Integer> getListaAtdSeq() {
		return listaAtdSeq;
	}
	public void setListaAtdSeq(List<Integer> listaAtdSeq) {
		this.listaAtdSeq = listaAtdSeq;
	}

	public Boolean getCidPrincipal() {
		return cidPrincipal;
	}
	public void setCidPrincipal(Boolean cidPrincipal) {
		this.cidPrincipal = cidPrincipal;
	}

	public AghCid getCid() {
		return cid;
	}
	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public enum Fields {

		QUANTIDADE("quantidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
}
