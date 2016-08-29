package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

/**
 * Domínio que indica a cor para ser mostrada na condição de atendimento.
 * 
 * @author gzapalaglio
 */
public enum DominioCorCondicaoAtendimento implements DominioString {
	
	LemonChiffon("LemonChiffon"),
	LightSteelBlue("LightSteelBlue"),
	Orange("Orange"),
	LightGray("LightGray"),
	yellow("yellow"),
	DarkGoldenrod("DarkGoldenrod"),
	PeachPuff("PeachPuff"),
	LightCyan("LightCyan"),
	Salmon("Salmon"),
	ForestGreen("ForestGreen"),
	Red("Red"),
	Pink("Pink");

	private String value;
	
	private DominioCorCondicaoAtendimento(String value) {
		this.value = value;
	}
	
	@Override
	public String getCodigo() {
		return this.value;
	}	

	@Override
	public String getDescricao() {
		switch (this) {
		case LemonChiffon:
			return "Amarelo Fraco";
		case LightSteelBlue:
			return "Azul Fraco";
		case Orange:
			return "Laranja";
		case LightGray:
			return "Cinza Fraco";
		case yellow:
			return "Amarelo";
		case DarkGoldenrod:
			return "Marrom";
		case PeachPuff:
			return "Pêssego";
		case LightCyan:
			return "Ciano Claro";
		case Salmon:
			return "Salmão";
		case ForestGreen:
			return "Verde";
		case Red:
			return "Vermelho";
		case Pink:
			return "Rosa";
		default:
			return "";
		}
	}
}