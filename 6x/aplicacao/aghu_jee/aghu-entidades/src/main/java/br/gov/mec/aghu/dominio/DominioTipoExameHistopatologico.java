package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioTipoExameHistopatologico implements Dominio {

	
	REVISAO_LAMINA(1),
	
	IMUNOHISTOQUIMICA(2),
	
	BIOPSIA_PECA(3);
	
	private Integer value;
	
	private DominioTipoExameHistopatologico(Integer value) {
		this.value = value;
	}

	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch(this) {
			case REVISAO_LAMINA: 
				return "Revisão Lamina";
			case IMUNOHISTOQUIMICA:
				return "Imunohistoquimica";
			case BIOPSIA_PECA:
				return "Biópsia/Peça";
			default:
				return "";
		}
	}
	
	public static DominioTipoExameHistopatologico getInstance(Integer value) {
		switch (value) {
		case 1:
			return REVISAO_LAMINA;
		case 2:
			return IMUNOHISTOQUIMICA;
		case 3:
			return BIOPSIA_PECA;
		default:
			return null;
		}
	}
}
