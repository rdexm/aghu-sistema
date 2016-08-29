package br.gov.mec.aghu.model;

public interface IAelResultadoExame {

	AelParametroCamposLaudo getParametroCampoLaudo();

	String getDescricao();

	Long getValor();

	AelResultadoCodificado getResultadoCodificado();

	AelResultadoCaracteristica getResultadoCaracteristica();

}
