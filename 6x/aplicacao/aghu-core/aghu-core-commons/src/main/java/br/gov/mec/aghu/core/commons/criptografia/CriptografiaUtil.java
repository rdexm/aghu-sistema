package br.gov.mec.aghu.core.commons.criptografia;

import java.security.GeneralSecurityException;
import java.security.InvalidParameterException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Classe utilitária para fins de uso em casos de criptografia e descriptografia.
 * Originalmente foi implementada para suportar apenas DES e Triple DES, porém
 * pode ter seu escopo aumentado para atender outros algoritmos no futuro.
 * 
 * @author Vicente Grassi Filho
 */
public class CriptografiaUtil {
	
	private static final String chavePadrao = "@gH70k3n";
	private static String chave;
	private static final IvParameterSpec vetorInicializacaoPadrao = new IvParameterSpec(new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
		
	private static final Log LOG = LogFactory.getLog(CriptografiaUtil.class);
	
	public static String descriptografar(String entrada) {		
		return CriptografiaUtil.descriptografar(entrada, AlgoritmoCriptografiaEnum.DES,
				TransformacaoCriptograficaEnum.DES_CBC_PKCS5Padding,
				CriptografiaUtil.chavePadrao,
				CriptografiaUtil.vetorInicializacaoPadrao);
	}
	
	public static String descriptografar(final String entrada, final AlgoritmoCriptografiaEnum algoritmo, final TransformacaoCriptograficaEnum transformacao, final String chave, final IvParameterSpec vetorInicializacao) {	
		return CriptografiaUtil.executarOperacao(Cipher.DECRYPT_MODE, entrada, algoritmo, transformacao, vetorInicializacao);		
	}
	
	public static String criptografar(String entrada) {		
		return CriptografiaUtil.criptografar(entrada, AlgoritmoCriptografiaEnum.DES,
				TransformacaoCriptograficaEnum.DES_CBC_PKCS5Padding,
				CriptografiaUtil.chavePadrao,
				CriptografiaUtil.vetorInicializacaoPadrao);
	}
	
	public static String criptografar(final String entrada, final AlgoritmoCriptografiaEnum algoritmo, final TransformacaoCriptograficaEnum transformacao, final String chave, final IvParameterSpec vetorInicializacao) {
		return CriptografiaUtil.executarOperacao(Cipher.ENCRYPT_MODE, entrada, algoritmo, transformacao, vetorInicializacao);		
	}	
	
	private static String executarOperacao(final int operacao, final String entrada, final AlgoritmoCriptografiaEnum algoritmo, final TransformacaoCriptograficaEnum transformacao, final IvParameterSpec vetorInicializacao) {
		if (operacao != Cipher.ENCRYPT_MODE && operacao != Cipher.DECRYPT_MODE) {
			throw new InvalidParameterException("Somente permitida operação de criptografia ou descriptografia.");
		}
		
		String parametroEntradaAjustado = CriptografiaUtil.ajustarParametroEntradaOperacao(entrada, transformacao);
		
		String chaveCriptografica = CriptografiaUtil.chave != null ? CriptografiaUtil.chave : CriptografiaUtil.chavePadrao;
		
		byte[] resultadoOperacao = null;
		String resultadoOperacaoString = null;
		
		try {
			SecretKey key = new SecretKeySpec(chaveCriptografica.getBytes("UTF-8"), algoritmo.toString()); 

			Cipher c = Cipher.getInstance(transformacao.toString());
			
			if (TransformacaoCriptograficaEnum.Triple_DES_ECB_NoPadding.equals(transformacao)
					|| TransformacaoCriptograficaEnum.Triple_DES_ECB_PKCS5Padding.equals(transformacao)) {
				c.init(operacao, key);
			} else {
				c.init(operacao, key, vetorInicializacao);
			}

			if (operacao == Cipher.ENCRYPT_MODE) {
				resultadoOperacao = c.doFinal(parametroEntradaAjustado.getBytes("UTF-8"));
				resultadoOperacaoString = Base64Util.encodeBytes(resultadoOperacao);
			} else {
				resultadoOperacao = c.doFinal(Base64Util.decode(parametroEntradaAjustado));
				resultadoOperacaoString = new String(resultadoOperacao);
			}
		} catch (GeneralSecurityException gse) {
			LOG.error(gse.getMessage(), gse);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		
		if (LOG.isDebugEnabled()) {
			// OBSERVACAO: quando isto fica exposto, aparece no log do wildfly
			// a senha do usuario no momento do login. Quando extremamente necessario, descomente
			// o trecho abaixo, mas nao coloque em producao.
			//StringBuilder msgDesCript = new StringBuilder(operacao == Cipher.DECRYPT_MODE ? "Descriptografando: " : "Criptografando: ");
			//LOG.debug(msgDesCript.append(entrada).toString());			
			
			// OBSERVAÇÃO: jogar no log a chave de criptografia?? 
			// Avaliar questões de segurança da instituição antes de 
			// descomentar o trecho abaixo:
			//log.trace("Chave criptográfica utilizada: "+chaveCriptografica);

			LOG.debug("Algoritmo ["+algoritmo+"] com transformação ["+transformacao+"]");			
			LOG.debug("Resultado da operação: "+resultadoOperacaoString);
		}

		return resultadoOperacaoString;		
	}
	
	private static String ajustarParametroEntradaOperacao(String entrada, final TransformacaoCriptograficaEnum transformacao) {
		
		if (TransformacaoCriptograficaEnum.Triple_DES_CBC_PKCS5Padding.equals(transformacao)
				|| TransformacaoCriptograficaEnum.Triple_DES_ECB_PKCS5Padding.equals(transformacao)
				|| TransformacaoCriptograficaEnum.Triple_DES_ECB_NoPadding.equals(transformacao)) {
			return StringUtils.rightPad(" ", 8 - entrada.trim().length() % 8, " ");
		}
		
		return entrada; 
	}
	
	public static void setChave(String chave) {
		if (chave == null || chave.isEmpty() || chave.length() < 8) {
			throw new InvalidParameterException("Chave vazia ou menor que 8 bytes.");
		}
		CriptografiaUtil.chave = chave;
	}
	
//	/**
//	 * Descomentar o método abaixo apenas para testes locais
//	 * @param args Nulo
//	 */
//	public static void main(String[] args) {		
//		System.out.println(">> Padding: right, 8 bytes length");
//		System.out.println(">> Chave: "+CriptografiaUtil.chavePadrao);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
//		
//		// Bloco teste completo
//		String senha = "senhaTeste123";
//		System.out.println(">> Senha:               "+senha);
//		//CriptografiaUtil.setChave("c");
//		String senhaCriptografada = CriptografiaUtil.criptografar(senha);
//		System.out.println(">> Senha Criptografada: "+senhaCriptografada);
//	//	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//	//	System.out.println(">> Senha Descriptografada: "+CriptografiaUtil.descriptografar(senhaCriptografada));
//	//	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");	
//		String token = "N|2013-07-29 14:10:48|AGHU|"+senhaCriptografada;
//		System.out.println(">> Token:               "+token);
//		String tokenCriptografado = CriptografiaUtil.criptografar(token); 
//		// Fim Bloco teste completo
//		
//		// alblopes
//		//String tokenCriptografado = "wBlgYFH9kr1Qc2m4d8gRMLOH7f%2F0XNpgAjUBCvJfKSF7W%2FmzPnQTon6tIxnK5w%2FF%0D%0AYrzMIJLtVGBJ%2F0%2Fqo87TU05Gy2W0Nc8iQM1LqwSXFf0%3D";
//		// ALBLOPES
//	//	String tokenCriptografado = "wBlgYFH9kr1WMj%2BkWo6zSUoX95vuSKHc0Slx4iTeOKsaQ6jAiCNKLqOP%2FTGXiG9j%0D%0AgZb5lvK%2FBd7Ql4xCeBcM%2B1m1TwVWYkNnd%2FCmj3vyMjY%3D";
//		System.out.println(">> Token Criptografado:        "+tokenCriptografado);
//		String tokenCriptEncoded = null;
//		try {
//			tokenCriptEncoded = CoreUtil.encodeURL(tokenCriptografado);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		System.out.println(">> Token Criptografado encode: "+tokenCriptEncoded);
//		
//		
//		String tokenCriptDecoded = null;
//		try {
//			tokenCriptDecoded = CoreUtil.decodeURL(tokenCriptEncoded);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		
//		System.out.println(">> Token Criptografado decode: "+tokenCriptDecoded);
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		System.out.println(">> Token Descriptografado: "+CriptografiaUtil.descriptografar(tokenCriptDecoded));		
//	} 
}