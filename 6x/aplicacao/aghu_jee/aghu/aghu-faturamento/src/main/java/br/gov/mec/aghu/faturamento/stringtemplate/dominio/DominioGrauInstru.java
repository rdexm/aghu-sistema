package br.gov.mec.aghu.faturamento.stringtemplate.dominio;

import br.gov.mec.aghu.dominio.DominioGrauInstrucao;

public enum DominioGrauInstru implements
		DominioCodigoInt {

	NAO_APLICAVEL(0),
	ANALFABETO(1),
	PRIMEIRO_GRAU(2),
	SEGUNDO_GRAU(3),
	TERCEIRO_GRAU(4), ;

	private final int codigo;

	private DominioGrauInstru(final int codigo) {

		this.codigo = codigo;
	}

	@Override
	public int getCodigo() {

		return this.codigo;
	}

	public static DominioGrauInstru valueOf(
			final DominioGrauInstrucao valor) {

		DominioGrauInstru result = null;

		if (valor != null) {
			switch (valor) {
			case PRIMEIRO_GRAU_COMPLETO:
				result = DominioGrauInstru.PRIMEIRO_GRAU;
				break;
			case SEGUNDO_GRAU_COMPLETO:
				result = DominioGrauInstru.SEGUNDO_GRAU;
				break;
			case SUPERIOR:
				result = DominioGrauInstru.TERCEIRO_GRAU;
				break;
			case NENHUM:
				result = DominioGrauInstru.ANALFABETO;
				break;
			case IGNORADO:
				result = DominioGrauInstru.NAO_APLICAVEL;
				break;
			case PRIMEIRO_GRAU_INCOMPLETO:
				result = DominioGrauInstru.PRIMEIRO_GRAU;
				break;
			case SEGUNDO_GRAU_INCOMPLETO:
				result = DominioGrauInstru.SEGUNDO_GRAU;
				break;
			case SUPERIOR_INCOMPLETO:
				result = DominioGrauInstru.TERCEIRO_GRAU;
				break;
			default:
				throw new IllegalArgumentException("Nao eh possivel converter: "
						+ valor);
			}
		}

		return result;
	}
}
