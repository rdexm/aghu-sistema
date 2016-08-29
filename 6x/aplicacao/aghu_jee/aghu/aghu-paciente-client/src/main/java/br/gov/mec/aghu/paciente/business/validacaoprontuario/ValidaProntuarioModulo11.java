package br.gov.mec.aghu.paciente.business.validacaoprontuario;


/**
 *
 * Modulo 11, utilizado no HU de Mato Grosso do Sul.
 *  
 */
public class ValidaProntuarioModulo11 extends AbstractValidaProntuario {

	@Override
	public int executaModulo(Integer numeroProntuario) {
		return modulo11(numeroProntuario);
	}
	
	/**
	 * 
	 * @param numeroProntuario
	 * @return
	 */
	public static int modulo11(Integer numeroProntuario) {
		StringBuilder sb = new StringBuilder("");
		sb.append(numeroProntuario);
		// LPAD 
		int size = sb.length();
		for (int i = 1; i <= 7 - size; i++) {
			sb.insert(0, "0");
		}		
		/**
		 * Efetua carga de array com o código atual recebido 
		 */
		char[] chars = sb.toString().toCharArray();
		
		int[] array = new int[chars.length];
		for( int i = 0; i < chars.length; i++ ) {
			char c = chars[i];			
			array[ i ] = Integer.parseInt(Character.toString(c));
		}

		/**
		 * Efetua a geração do digito verificar para o código atual recebido
		 * 
		 */
		int produto = 0;
		int produtoSoma = 0;
		int digitoVerificador = 0;
		int j = 2;

		for ( int i = (array.length) - 1; i>=0; i-- ) {
			produto = (array[i] * j++) ;
			produtoSoma = produtoSoma + produto ;
		}

		int resto  =  (produtoSoma % 11); 

		if (resto == 0 || resto == 1 ){
			digitoVerificador = 0;
		}
		else {
			digitoVerificador = 11 - resto; 
		}
		return digitoVerificador;

	}


}
