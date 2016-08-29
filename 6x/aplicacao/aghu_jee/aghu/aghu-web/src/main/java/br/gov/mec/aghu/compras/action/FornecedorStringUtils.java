package br.gov.mec.aghu.compras.action;

import br.gov.mec.aghu.model.ScoFornecedor;

/**
 * Helper para formatação de número (CPF/CNPJ) do fornecedor.
 * 
 * @author mlcruz
 */
public final class FornecedorStringUtils {

	/**
	 * Formata fornecedor.
	 * 
	 * @param scoFornecedor Fornecedor
	 * @return Descrição do fornecedor no formato "<CNPJ> - <Razão Social>".
	 */
	public static String format(ScoFornecedor scoFornecedor) {
		String numero = scoFornecedor.getCgc() != null ? formatCnpj(scoFornecedor.getCgc()) :
			scoFornecedor.getCpf() != null ? formatCpf(scoFornecedor.getCpf()) : null;
		
		String format = numero != null ? String.format("%s - %s", 
				numero, scoFornecedor.getRazaoSocial()) : scoFornecedor.getRazaoSocial();
		
		return format;
	}

	/**
	 * Formata CPF.
	 * 
	 * @param cpf CPF
	 * @return CPF formatado.
	 */
	public static String formatCpf(Long cpf) {
		StringBuilder cpfSb = new StringBuilder(String.format("%011d", cpf));
		
		cpfSb.insert(3, '.');
		cpfSb.insert(7, '.');
		cpfSb.insert(11, '-');
		
		return cpfSb.toString();
	}

	/**
	 * Formata CNPJ.
	 * 
	 * @param cnpj CNPJ
	 * @return CNPJ formatado.
	 */
	public static String formatCnpj(Long cnpj) {
		StringBuilder cnpjSb = new StringBuilder(String.format("%014d", cnpj));

		cnpjSb.insert(2, '.');
		cnpjSb.insert(6, '.');
		cnpjSb.insert(10, '/');
		cnpjSb.insert(15, '-');
		
		return cnpjSb.toString();
	}
	
	/** Classe não instanciável. */
	private FornecedorStringUtils() {
	}
}
