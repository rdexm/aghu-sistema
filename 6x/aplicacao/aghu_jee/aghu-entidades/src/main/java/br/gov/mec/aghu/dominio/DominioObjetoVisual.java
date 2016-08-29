package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioObjetoVisual implements Dominio {

	
	TEXTO_ALFANUMERICO(1,"Texto alfanumérico", "A"), 

	
	TEXTO_CODIFICADO(2,"Texto codificado", "C"),

	
	TEXTO_FIXO(3,"Texto fixo", "T"),
	
	
	TEXTO_NUMERICO_EXPRESSAO(4,"Texto Numérico/Expressão", "NE"),
	
	
	TEXTO_LONGO(5,"Texto longo", "A"),
	
	
	EQUIPAMENTO(6,"Equipamento", "Q"),
	
	
	METODO(7,"Metodo", "M"),
	
	
	RECEBIMENTO(8,"Recebimento", "R"),
	
	
	HISTORICO(9, "Histórico", "H"),
	
	
	VALORES_REFERENCIA(10,"Valores de Refência", "V");
	
	
	
	private String descricao;
	private int numero;
	private String siglaLaudo;
	
	private DominioObjetoVisual(int numero, String descricao, String siglaLaudo) {
		this.descricao = descricao;
		this.numero = numero;
		this.siglaLaudo = siglaLaudo;
	}
	
	
	@Override
	public int getCodigo() {
		return this.numero;
	}

	@Override
	public String getDescricao() {
		return this.descricao;
	}
	
	public String getSiglaLaudo(){
		return this.siglaLaudo;
	}

}
