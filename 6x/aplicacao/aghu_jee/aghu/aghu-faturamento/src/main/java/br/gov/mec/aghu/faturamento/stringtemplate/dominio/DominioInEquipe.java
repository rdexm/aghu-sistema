package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

public enum DominioInEquipe implements
		DominioCodigoInt {

	NAO_APLICAVEL(0),
	PRIMEIRO_CIRURGIAO(1),
	PRIMEIRO_AUXILIAR(2),
	SEGUNDO_AUXILIAR(3),
	TERCEIRO_AUXILIAR(4),
	QUARTO_AUXILIAR(5),
	ANESTESISTA(6), ;

	private final int codigo;

	private DominioInEquipe(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}

	public static DominioInEquipe valueOf(final int codigo) {

		DominioInEquipe result = null;

		for (DominioInEquipe d : DominioInEquipe.values()) {
			if (d.getCodigo() == codigo) {
				result = d;
				break;
			}
		}

		return result;
	}
}
