package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio contendo os códigos dos campos relacionados a Exame Histopatológico
 * de Colo- SISCAN.
 * 
 * @author ghernandez
 * 
 */
public enum DominioSiscanProcedimentoCirurgico implements Dominio {

	BIOPSIA(1), CONIZACAO(4), OUTROS(5), EXERESE_DA_ZONA_DE_TRANSFORMACAO(6), HISTERECTOMIA_SIMPLES(7), HISTERECTOMIA_COM_ANEXECTOMIA(8);

	private int codigo;

	private DominioSiscanProcedimentoCirurgico(int codigo) {
		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {
		return codigo;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case BIOPSIA:
			return "Biópsia";
		case CONIZACAO:
			return "Conização";
		case OUTROS:
			return "Outros";
		case EXERESE_DA_ZONA_DE_TRANSFORMACAO:
			return "Exerese da Zona de Transformação";
		case HISTERECTOMIA_SIMPLES:
			return "Histerectomia Simples";
		case HISTERECTOMIA_COM_ANEXECTOMIA:
			return "Histerectomia com Anexectomia Uni ou Bilateral";
		default:
			return "";
		}

	}
	
	public static DominioSiscanProcedimentoCirurgico getInstance(int codigo){
		switch (codigo) {
		case 1:
			return BIOPSIA;
		case 4:
			return CONIZACAO;
		case 5:
			return OUTROS;
		case 6:
			return EXERESE_DA_ZONA_DE_TRANSFORMACAO;
		case 7:
			return HISTERECTOMIA_SIMPLES;
		case 8:
			return HISTERECTOMIA_COM_ANEXECTOMIA;
		default:
			return null;
		}
	}

}
