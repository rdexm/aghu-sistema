package br.gov.mec.aghu.core.commons.criptografia;

public enum AlgoritmoCriptografiaEnum {
	DES,
	Triple_DES;
	
	@Override
	public String toString() {
		switch (this) {

		case DES:
			return "DES";
		case Triple_DES:
			return "DESede";			
		default:
			return null;
		}
	}
}
