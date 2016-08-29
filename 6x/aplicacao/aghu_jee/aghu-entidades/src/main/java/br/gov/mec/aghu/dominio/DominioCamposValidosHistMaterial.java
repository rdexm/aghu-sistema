package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;



public enum DominioCamposValidosHistMaterial  implements Dominio {

	UMD_CODIGO("umdCodigo", "Unidade de Medida"),
	DESCRICAO("descricao", "Descrição"),
    IND_SITUACAO("indSituacao", "Situação"),
	IND_ESTOCAVEL("indEstocavel", "Estocável"),
	IND_GENERICO("indGenerico", "Genérico"),
	IND_MENOR_PRECO("indMenorPreco", "Menor Preço"),
	IND_PRODUCAO_INTERNA("indProducaoInterna", "Produção Interna"),
	IND_ATU_QTDE_DISPONIVEL("indAtuQtdeDisponivel", "Quantidade Disponível"),
	OPT_CODIGO("optCodigo", "Origem Parecer Técnico"),
	CLASSIF_XYZ("classifXyz", "Classificação XYZ"),
	SAZONALIDADE("sazonalidade", "Sazonalidade"),
	NOME("nome", "Nome"),
	OBSERVACAO("observacao", "Observação"),
	GMT_CODIGO("gmtCodigo", "Grupo"),
	IND_FATURAVEL("indFaturavel", "Faturável"),
	ALM_SEQ_LOCAL_ESTQ("almSeqLocalEstq", "Local Estoque"),
	IND_PADRONIZADO("indPadronizado", "Padronizado"),
	IND_FOTOSENSIVEL("indFotosensivel", "Fotosensível"),
	IND_TIPO_USO("indTipoUso", "Tipo Uso"),
	IND_TERMOLABIL("indTermolabil", "Termolábil"),
	TEMPERATURA("temperatura", "Temperatura"),		
	IND_VINCULADO("indVinculado", "Vinculado"),
	IND_SUSTENTAVEL("indSustentavel", "Sustentável"),	
	LEGISLACAO("legislacao", "Legislação"),
	IND_CONFAZ("indConfaz", "Confaz"),	
	IND_CAP_CMED("indCapCmed", "CAP/CMED"),
	IND_TIPO_RESIDUO("indTipoResiduo", "Tipo Resíduo"),
	IND_CONTROLE_VALIDADE("indControleValidade", "Controle Validade"),
	COD_CATMAT("codCatmat", "Código CATMAT"),
	COD_MAT_ANTIGO("codMatAntigo", "Código Material Antigo");
	
	
	private String valor;
	private String descricao;
	
	private DominioCamposValidosHistMaterial(String valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	
	@Override
	public String toString(){
		return this.valor;
	}
	

	@Override
	public int getCodigo() {
		// TODO Auto-generated method stub
		return ordinal();
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}
}
