package br.gov.mec.aghu.sig.custos.business;

public class SigNoEnumOperationException extends UnsupportedOperationException {

	private static final long serialVersionUID = -7270886022220527975L;

	public SigNoEnumOperationException(){
		super("No suport for Enum type.");
	}
}
