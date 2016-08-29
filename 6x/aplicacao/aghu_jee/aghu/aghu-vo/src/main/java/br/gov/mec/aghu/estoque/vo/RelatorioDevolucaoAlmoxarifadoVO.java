/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author daniel.augusto
 *
 */
public class RelatorioDevolucaoAlmoxarifadoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3390684198343413730L;

	private Integer seq;
	private Short almSeq;
	private Date dtGeracao;
	private Integer cctCodigo;
	private String cctDescricao;
	private String observacao;
	private Integer matCodigo;
	private String matNome;
	private String umdCodigo;
	private String fornecedor;
	private String endereco;
	private Integer quantidade;
	private Double valor;
	private Double valorTotal;
	private String nomePessoaServidor;
	private Integer rccRamal;
	private Short tmvSeq;
	private Byte tmvComplemento;
	private Integer numeroFornecedor;	

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getAlmSeq() {
		return almSeq;
	}

	public void setAlmSeq(Short almSeq) {
		this.almSeq = almSeq;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public String getCctDescricao() {
		return cctDescricao;
	}

	public void setCctDescricao(String cctDescricao) {
		this.cctDescricao = cctDescricao;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getMatNome() {
		return matNome;
	}

	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getNomePessoaServidor() {
		return nomePessoaServidor;
	}

	public void setNomePessoaServidor(String nomePessoaServidor) {
		this.nomePessoaServidor = nomePessoaServidor;
	}

	public Integer getRccRamal() {
		return rccRamal;
	}

	public void setRccRamal(Integer rccRamal) {
		this.rccRamal = rccRamal;
	}

	public Short getTmvSeq() {
		return tmvSeq;
	}

	public void setTmvSeq(Short tmvSeq) {
		this.tmvSeq = tmvSeq;
	}

	public Byte getTmvComplemento() {
		return tmvComplemento;
	}

	public void setTmvComplemento(Byte tmvComplemento) {
		this.tmvComplemento = tmvComplemento;
	}
	
	public Double getValor() {
		return valor;
	}
	
	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	
	public String getCentroCustoSeqDescricao() {
		return new StringBuilder().append(getCctCodigo()).append(' ').append(getCctDescricao()).toString();
	}
	
	public enum Fields{
		SEQ("seq"), ALM_SEQ("almSeq"), DT_GERACAO("dtGeracao"),
		CCT_CODIGO("cctCodigo"), CCT_DESCRICAO("cctDescricao"),
		OBSERVACAO("observacao"), MAT_CODIGO("matCodigo"), 
		MAT_NOME("matNome"), UMD_CODIGO("umdCodigo"),
		FORNECEDOR("fornecedor"), ENDERECO("endereco"),
		QUANTIDADE("quantidade"), VALOR("valor"),
		NOME_PESSOA_SERVIDOR("nomePessoaServidor"), RCC_RAMAL("rccRamal"),
		TMV_SEQ("tmvSeq"), TMV_COMPLEMENTO("tmvComplemento"),
		NUMERO_FORNECEDOR("numeroFornecedor");
		
		private String fields;
		
		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}
}