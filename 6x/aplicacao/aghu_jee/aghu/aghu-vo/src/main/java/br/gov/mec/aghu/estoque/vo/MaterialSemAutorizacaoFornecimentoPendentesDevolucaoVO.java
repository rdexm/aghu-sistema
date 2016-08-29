package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author pedro.santiago
 *
 */
public class MaterialSemAutorizacaoFornecimentoPendentesDevolucaoVO {

	private String siglaMovimento;
	private Integer numeroEsl;
	private Date dtGeracao;
	private String fornecedorRazaoSocial;
	private Long cgc;
	private Long cpf;
	private String fornecedorCNPJ;
	private String contato;
	private Long numero;
	private String material;
	private String unidade;
	private Integer quantidade;
	private Integer quantidadeDevolvida;
	private String marca;
	private Integer sc;
	private Integer dfeSeq;
	private Boolean nfPrincipal;
	private Integer dfeSeqAssociarOrigem;
	
	public String getSiglaMovimento() {
		return siglaMovimento;
	}

	public void setSiglaMovimento(String siglaMovimento) {
		this.siglaMovimento = siglaMovimento;
	}

	public Integer getNumeroEsl() {
		return numeroEsl;
	}

	public void setNumeroEsl(Integer numeroEsl) {
		this.numeroEsl = numeroEsl;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public String getFornecedorRazaoSocial() {
		return fornecedorRazaoSocial;
	}

	public void setFornecedorRazaoSocial(String fornecedorRazaoSocial) {
		this.fornecedorRazaoSocial = fornecedorRazaoSocial;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getFornecedorCNPJ() {

		if (this.cpf == null) {
			if (this.cgc == null) {
				return this.fornecedorCNPJ;
			}
			return CoreUtil.formatarCNPJ(this.cgc);
		}
		return CoreUtil.formataCPF(this.cpf);
	}

	public void setFornecedorCNPJ(String fornecedorCNPJ) {
		this.fornecedorCNPJ = fornecedorCNPJ;
	}

	public String getContato() {
		return contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Integer getQuantidadeDevolvida() {
		return quantidadeDevolvida;
	}

	public void setQuantidadeDevolvida(Integer quantidadeDevolvida) {
		this.quantidadeDevolvida = quantidadeDevolvida;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Integer getSc() {
		return sc;
	}

	public void setSc(Integer sc) {
		this.sc = sc;
	}

	public Integer getDfeSeq() {
		return dfeSeq;
	}

	public void setDfeSeq(Integer dfeSeq) {
		this.dfeSeq = dfeSeq;
	}

	public Boolean getNfPrincipal() {
		return nfPrincipal;
	}

	public void setNfPrincipal(Boolean nfPrincipal) {
		this.nfPrincipal = nfPrincipal;
	}

	public enum Fields {
		SIGLA_MOVIMENTO("siglaMovimento"), NUMERO_ESL("numeroEsl"), DT_GERACAO(
				"dtGeracao"), FORNECEDOR_RAZAO_SOCIAL("fornecedorRazaoSocial"), FORNECEDOR_CNPJ(
				"cgc"), FORNECEDOR_CPF("cpf"), CONTATO("contato"), DOC_FISCAL(
				"numero"), MATERIAL("material"), UNIDADE("unidade"), QUANTIDADE(
				"quantidade"), QUANTIDADE_DEVOLVIDA("quantidadeDevolvida"), MARCA(
				"marca"), SC("sc"), DFE_SEQ("dfeSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getDfeSeqAssociarOrigem() {
		return dfeSeqAssociarOrigem;
	}

	public void setDfeSeqAssociarOrigem(Integer dfeSeqAssociarOrigem) {
		this.dfeSeqAssociarOrigem = dfeSeqAssociarOrigem;
	}
}
