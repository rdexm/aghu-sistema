package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class PesquisaManterMaterialVO implements BaseBean {
	
	private static final long serialVersionUID = -8398342527156538581L;
	private Integer codigo;
	private String nome;
	private String unidadeSigla;
	private String unidadeDescricao;
	private Integer grupoCodigo;
	private String grupoDescricao;
	private Short localEstoqueSeq;
	private String localEstoqueDescricao;
	private String estocavel;
	private String padronizado;
	private String generico;
	private String controlaValidade;
	private String menorPreco;
	private String quantidadeDisponivel;
	private String faturavel;
	private String situacao;
	private Integer codCatmat;
	private Long codMatAntigo;
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUnidadeSigla() {
		return unidadeSigla;
	}

	public void setUnidadeSigla(String unidadeSigla) {
		this.unidadeSigla = unidadeSigla;
	}

	public String getUnidadeDescricao() {
		return unidadeDescricao;
	}

	public void setUnidadeDescricao(String unidadeDescricao) {
		this.unidadeDescricao = unidadeDescricao;
	}

	public Integer getGrupoCodigo() {
		return grupoCodigo;
	}

	public void setGrupoCodigo(Integer grupoCodigo) {
		this.grupoCodigo = grupoCodigo;
	}

	public String getGrupoDescricao() {
		return grupoDescricao;
	}

	public void setGrupoDescricao(String grupoDescricao) {
		this.grupoDescricao = grupoDescricao;
	}

	public Short getLocalEstoqueSeq() {
		return localEstoqueSeq;
	}

	public void setLocalEstoqueSeq(Short localEstoqueSeq) {
		this.localEstoqueSeq = localEstoqueSeq;
	}

	public String getLocalEstoqueDescricao() {
		return localEstoqueDescricao;
	}

	public void setLocalEstoqueDescricao(String localEstoqueDescricao) {
		this.localEstoqueDescricao = localEstoqueDescricao;
	}

	public String getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(String estocavel) {
		this.estocavel = estocavel;
	}

	public String getPadronizado() {
		return padronizado;
	}

	public void setPadronizado(String padronizado) {
		this.padronizado = padronizado;
	}

	public String getGenerico() {
		return generico;
	}

	public void setGenerico(String generico) {
		this.generico = generico;
	}

	public String getControlaValidade() {
		return controlaValidade;
	}

	public void setControlaValidade(String controlaValidade) {
		this.controlaValidade = controlaValidade;
	}

	public String getMenorPreco() {
		return menorPreco;
	}

	public void setMenorPreco(String menorPreco) {
		this.menorPreco = menorPreco;
	}

	public String getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(String quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}

	public String getFaturavel() {
		return faturavel;
	}

	public void setFaturavel(String faturavel) {
		this.faturavel = faturavel;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	
	/**
	 * Formata a descricao de unidade, grupo e local de estoque
	 * @param seq
	 * @param descricao
	 * @return
	 */
	private String formataSeqDescricao(Object seq, Object descricao) {
		seq = seq == null ? "" : seq + " - ";
		descricao = descricao == null ? "" : descricao;
		return seq.toString() + descricao.toString();
	}
	
	/**
	 * Descrição completa da unidade
	 * @return
	 */
	public String getDescricaoCompletaUnidade(){
		return this.formataSeqDescricao(unidadeSigla, unidadeDescricao);
	}
	
	/**
	 * Descrição completa do grupo
	 * @return
	 */
	public String getDescricaoCompletaGrupo(){
		return this.formataSeqDescricao(grupoCodigo, grupoDescricao);
	}
	
	/**
	 * Descrição completa do local de estoque
	 * @return
	 */
	public String getDescricaoCompletaLocalEstoque(){
		return this.formataSeqDescricao(localEstoqueSeq, localEstoqueDescricao);
	}

	public Integer getCodCatmat() {
		return codCatmat;
	}

	public void setCodCatmat(Integer codCatmat) {
		this.codCatmat = codCatmat;
	}

	public Long getCodMatAntigo() {
		return codMatAntigo;
	}

	public void setCodMatAntigo(Long codMatAntigo) {
		this.codMatAntigo = codMatAntigo;
	}
	
	

}
