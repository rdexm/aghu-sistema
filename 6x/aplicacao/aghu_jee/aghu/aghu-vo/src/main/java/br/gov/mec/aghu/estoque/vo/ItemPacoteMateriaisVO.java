package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class ItemPacoteMateriaisVO implements Serializable{
	
	private static final long serialVersionUID = 5266613625263893575L;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer numeroFornecedor;
	private String nomeFantasiaFornecedor;
	private String codigoUnidadeMedida;
	private String descricaoUnidadeMedida;
	private Integer codigoGrupoMaterial;
	private Integer seqEstoque;
	private Integer codigoCentroCustoAplicacaoPacote;
	private Integer codigoCentroCustoProprietarioPacote;
	private Integer numeroPacote;
	private Integer quantidade;
	private Double consumo30Dias;
	private Double mediaSemestre;
	private Short seqAlmoxarifado;
	private Boolean estocavel;
	private String nomeGrupoMaterial;	
	
	public Integer getCodigoMaterial(){
		return this.codigoMaterial;
	}
	
	public void setCodigoMaterial(Integer codigoMaterial){
		this.codigoMaterial = codigoMaterial;
	}
	
	public String getNomeMaterial(){
		return this.nomeMaterial;
	}
	
	public void setNomeMaterial(String nomeMaterial){
		this.nomeMaterial = nomeMaterial;
	}
	
	public Integer getNumeroFornecedor(){
		return this.numeroFornecedor;
	}
	
	public void setNumeroFornecedor(Integer numeroFornecedor){
		this.numeroFornecedor = numeroFornecedor;
	}
	
	public String getNomeFantasiaFornecedor(){
		return this.nomeFantasiaFornecedor;
	}
	
	public void setNomeFantasiaFornecedor(String nomeFantasiaFornecedor){
		this.nomeFantasiaFornecedor = nomeFantasiaFornecedor;
	}
	
	public String getCodigoUnidadeMedida(){
		return this.codigoUnidadeMedida;
	}
	
	public void setCodigoUnidadeMedida(String codigoUnidadeMedida){
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}
	
	public String getDescricaoUnidadeMedida(){
		return this.descricaoUnidadeMedida;
	}
	
	public void setDescricaoUnidadeMedida(String descricaoUnidadeMedida){
		this.descricaoUnidadeMedida = descricaoUnidadeMedida;
	}
	
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getConsumo30Dias() {
		
		return consumo30Dias;
	}
	
	public void setConsumo30Dias(Double consumo30Dias) {
		this.consumo30Dias = consumo30Dias;
	}
	
	public Double getMediaSemestre() {
		return mediaSemestre;
	}
	
	public void setMediaSemestre(Double mediaSemestre) {
		this.mediaSemestre = mediaSemestre;
	}
	
	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoCentroCustoAplicacaoPacote(Integer codigoCentroCustoAplicacaoPacote) {
		this.codigoCentroCustoAplicacaoPacote = codigoCentroCustoAplicacaoPacote;
	}

	public Integer getCodigoCentroCustoAplicacaoPacote() {
		return codigoCentroCustoAplicacaoPacote;
	}

	public Integer getSeqEstoque() {
		return seqEstoque;
	}

	public void setSeqEstoque(Integer seqEstoque) {
		this.seqEstoque = seqEstoque;
	}

	public Integer getCodigoCentroCustoProprietarioPacote() {
		return codigoCentroCustoProprietarioPacote;
	}

	public void setCodigoCentroCustoProprietarioPacote(
			Integer codigoCentroCustoProprietarioPacote) {
		this.codigoCentroCustoProprietarioPacote = codigoCentroCustoProprietarioPacote;
	}

	public Integer getNumeroPacote() {
		return numeroPacote;
	}

	public void setNumeroPacote(Integer numeroPacote) {
		this.numeroPacote = numeroPacote;
	}
	

	public Short getSeqAlmoxarifado() {
		return seqAlmoxarifado;
	}

	public void setSeqAlmoxarifado(Short seqAlmoxarifado) {
		this.seqAlmoxarifado = seqAlmoxarifado;
	}

	public Boolean getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(Boolean estocavel) {
		this.estocavel = estocavel;
	}





	public String getNomeGrupoMaterial() {
		return nomeGrupoMaterial;
	}

	public void setNomeGrupoMaterial(String nomeGrupoMaterial) {
		this.nomeGrupoMaterial = nomeGrupoMaterial;
	}





	public enum Fields{
		
		QUANTIDADE("quantidade"),
		CODIGO_UNIDADE_MEDIDA("codigoUnidadeMedida"),
		DESCRICAO_UNIDADE_MEDIDA("descricaoUnidadeMedida"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		NOME_FANTASIA_FORNECEDOR("nomeFantasiaFornecedor"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		NOME_GRUPO_MTAERIAL("nomeGrupoMaterial"),
		CODIGO_GRUPO_MATERIAL("codigoGrupoMaterial"),
		CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE("codigoCentroCustoAplicacaoPacote"),
		CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE("codigoCentroCustoProprietarioPacote"),
		SEQ_ESTOQUE("seqEstoque"),
		SEQ_ALMOXARIFADO("seqAlmoxarifado"),
		NUMERO_PACOTE("numeroPacote"),
		ESTOCAVEL("estocavel");
		
		
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
