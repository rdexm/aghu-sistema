package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio para campos de tela com opção de sim ou não.
 * 
 * @author gmneto
 * 
 * OBS: Conforme definição do Checklist do Desenvolvedor (desde Abril/2010):
 * https://apus.hcpa.ufrgs.br/projects/aghu/wiki/CheckList_do_Desenvolvedor
 * NÃO mais utilizar esta enum para mapear colunas que usem somente estes dois
 * valores, 'Sim' e 'Não', e usar BooleanUserType no lugar.
 */
public enum DominioIdentificacaoComponenteNPT implements Dominio {
	ACETATO_ZINCO,
	AMINOACIDOS_AD,
	AMINOACIDOS_HEP,
	AMINOACIDOS_NEF,
	AMINOACIDOS_PED,
	CLORETO_POTASSIO,
	CLORETO_SODIO,
	FOSFATO_POTASSIO,
	GLICOSE_5,
	GLICOSE_10,
	GLICOSE_50,
	GLUCO_CALCIO,
	HEPARINA,
	LIPIDIOS_10,
	LIPIDIOS_20,
	OLIGOELEMENTOS_AD,
	OLIGOELEMENTOS_PED,
	SULFATO_MAGNESIO;
	
	
	
	


	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case LIPIDIOS_10:
			return "Lipídios 10%";
		case LIPIDIOS_20:
			return "Lipídios 20%";
		case AMINOACIDOS_AD:
			return "Aminoácidos Adulto 10%";
		case AMINOACIDOS_PED:
			return "Aminoácidos Pediátrico 10%";
		case AMINOACIDOS_HEP:
			return "Aminoácidos para Hepatopatas";
		case AMINOACIDOS_NEF:
			return "Aminoácidos para Nefropatas";
		case ACETATO_ZINCO:
			return "Acetato de Zinco";
		case CLORETO_SODIO:
			return "Cloreto de Sódio 20%";
		case GLUCO_CALCIO:
			return "Gluconato de Cálcio 10%";
		case CLORETO_POTASSIO:
			return "Cloreto de Potássio 10%";
		case FOSFATO_POTASSIO:
			return "Fosfato de Potássio";
		case SULFATO_MAGNESIO:
			return "Sulfato de Magnésio 50%";
		case GLICOSE_50:
			return "Glicose 50%";
		case GLICOSE_10:
			return "Glicose 10%";
		case GLICOSE_5:
			return "Glicose 5%";
		case HEPARINA:
			return "Heparina";
		case OLIGOELEMENTOS_AD:
			return "Oligoelementos Adulto";
		case OLIGOELEMENTOS_PED:
			return "Oligoelementos Pediátrico";
		default:
			return "";
		}
	}

	
}
