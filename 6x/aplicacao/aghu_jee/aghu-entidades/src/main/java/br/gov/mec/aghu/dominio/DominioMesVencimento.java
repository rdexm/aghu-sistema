package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * @author israel.haas
 * 
 */
public enum DominioMesVencimento implements Dominio {
	
	MES_APURACAO(0, "Mês de Apuração"),
	MES_SEGUINTE(1, "Mês Seguinte"),
	DOIS(2, "+2"),
	TRES(3, "+3"),
	QUATRO(4, "+4"),
	CINCO(5, "+5"),
	SEIS(6, "+6"),
	SETE(7, "+7"),
	OITO(8, "+8"),
	NOVE(9, "+9"),
	DEZ(10, "+10"),
	ONZE(11, "+11"),
	DOZE(12, "+12");
	
	private int value;
	private String descricao;
	
	private DominioMesVencimento(int value, String descricao) {
		this.value = value;
		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}
	
	@Override
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
