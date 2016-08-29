package br.gov.mec.aghu.core.commons.criptografia;

/**
 * Tipos de transformações criptográficas.
 * Fonte:
 * {@link docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html}
 * 
 * @author Vicente Grassi Filho
 *
 */
public enum TransformacaoCriptograficaEnum {

	DES_CBC_NoPadding, // Tamanho chave: 56
	DES_CBC_PKCS5Padding, // Tamanho chave: 56
	DES_ECB_NoPadding, // Tamanho chave: 56
	DES_ECB_PKCS5Padding, // Tamanho chave: 56
	Triple_DES_CBC_NoPadding, // Tamanho chave: 168
	Triple_DES_CBC_PKCS5Padding, // Tamanho chave: 168
	Triple_DES_ECB_NoPadding, // Tamanho chave: 168
	Triple_DES_ECB_PKCS5Padding; // Tamanho chave: 168	
    
	@Override
	public String toString() {
		switch (this) {

		case DES_CBC_NoPadding:
			return "DES/CBC/NoPadding";			
		case DES_CBC_PKCS5Padding:
			return "DES/CBC/PKCS5Padding";			
		case DES_ECB_NoPadding:
			return "DES/ECB/NoPadding";			
		case DES_ECB_PKCS5Padding:
			return "DES/ECB/PKCS5Padding";			
		case Triple_DES_CBC_NoPadding:
			return "DESede/CBC/NoPadding";			
		case Triple_DES_CBC_PKCS5Padding:
			return "DESede/CBC/PKCS5Padding";			
		case Triple_DES_ECB_NoPadding:
			return "DESede/ECB/NoPadding";			
		case Triple_DES_ECB_PKCS5Padding:
			return "DESede/ECB/PKCS5Padding";
		default:
			return null;
		}
	}	
}
