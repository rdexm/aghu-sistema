package br.gov.mec.aghu.paciente.business.validacaoprontuario;


/**
 * 
 * Versão extendida do módulo 10 utilizado no hospital do maranhão
 *
 */
public class ValidaProntuarioModulo10Extendido extends AbstractValidaProntuario {

	@Override
	public int executaModulo(Integer numeroProntuario) {
		return aghcModulo10MA(numeroProntuario);
	}

	public static int aghcModulo10MA(Integer numeroProntuario) {
		int soma;
		int multiplicador;
		int i;

		soma = 0;
		multiplicador = 1;
		StringBuilder sb = new StringBuilder("");
		sb.append(numeroProntuario);
		int tam = sb.length();

		while (tam > 0) {
			multiplicador = multiplicador + 1;
			soma += Integer.parseInt(String.valueOf(sb.charAt(tam - 1)))
					* multiplicador;
			tam -= 1;
		}

		i = 11 - (soma % 11);
		if (i >= 10){
			i = 0;
		}
		return i;

	}
}
