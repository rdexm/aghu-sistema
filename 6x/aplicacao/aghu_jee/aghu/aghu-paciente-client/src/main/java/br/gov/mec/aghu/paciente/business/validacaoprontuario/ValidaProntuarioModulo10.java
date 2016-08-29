package br.gov.mec.aghu.paciente.business.validacaoprontuario;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * Versão extendida do módulo 10 padrão, implantada na maioria dos HUs.
 * 
 */
public class ValidaProntuarioModulo10 extends AbstractValidaProntuario {

	private static final Log LOG = LogFactory.getLog(ValidaProntuarioModulo10.class);
	
	@Override
	public int executaModulo(Integer numeroProntuario) {
		return aghcModulo10(numeroProntuario);
	}

	/**
	 * Implementação da function AGHC_MODULO10 responsável por fazer a validação
	 * do código de prontuário.
	 * 
	 * Implementação baseada no modulo 10 do HCPA
	 * 
	 * @param pValor
	 *            valor do prontuário informado pelo usuário.
	 * @return
	 */
	private int aghcModulo10(Integer numeroProntuario) {
		StringBuilder sb = new StringBuilder("");
		sb.append(numeroProntuario);
		// LPAD
		int size = sb.length();
		for (int i = 1; i <= 12 - size; i++) {
			sb.insert(0, "0");
		}
		String vValorZero = sb.toString();
		int soma = 0;
		int somat = 0;
		int cont = 10;

		while (cont > 0) {
			somat += Integer.parseInt(vValorZero.substring(cont, cont + 1));
			cont -= 2;
		}

		cont = 11;

		while (cont > 0) {
			soma = Integer.parseInt(vValorZero.substring(cont, cont + 1)) * 2;

			try {
				if(String.valueOf(soma).length() > 0) {
					int tmp = Integer
							.parseInt(String.valueOf(soma).substring(0, 1));
					somat += tmp;
				}
			} catch (StringIndexOutOfBoundsException e) {
				LOG.error("Não foi possível converter o parametro para inteiro.", e);
			}

			try {
				if(String.valueOf(soma).length() > 1) {	
					int tmp = Integer
							.parseInt(String.valueOf(soma).substring(1, 2));
					somat += tmp;
				}
			} catch (StringIndexOutOfBoundsException e) {
				LOG.error("Não foi possível converter o parametro para inteiro.", e);
			}

			cont -= 2;
		}

		return modulo10(somat);
	}
	
	
	/**
	 * Implementação da function modulo10 responsável por efetuar o módulo do
	 * parametro.
	 * 
	 * @param somat
	 * @return
	 */
	private int modulo10(int somat) {
		int num = somat % 10;
		return num = (num > 0) ? 10 - num : 0;
	}
	
}
