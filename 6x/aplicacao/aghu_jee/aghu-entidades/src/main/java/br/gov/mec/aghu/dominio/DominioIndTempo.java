package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioIndTempo implements Dominio{
	
	HR,
	MI,
	DI,
	SE,
	QZ,
	MS,
	BI,
	TM,
	QM,
	SM,
	AN;
	
	@Override
	public int getCodigo() {
		return this.ordinal();
	}
	
	@Override
	public String getDescricao() {
		switch (this) {
		case HR:
			return "Horas";
		case MI:
			return "Minutos";
		case DI:
			return "Dias";
		case SE:
			return "Semanas";
		case QZ:
			return "Quinzena";
		case MS:
			return "Mês";
		case BI:
			return "Bimestre";
		case TM:
			return "Trimestre";
		case QM:
			return "Quadrimestre";
		case SM:
			return "Semestre";
		case AN:
			return "Ano";
		default:
			return "";
		}
	}
}
