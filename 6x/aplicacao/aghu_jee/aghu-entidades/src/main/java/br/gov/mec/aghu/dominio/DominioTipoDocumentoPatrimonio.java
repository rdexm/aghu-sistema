package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica o tipo de documento Patrimonio.
 * 
 * @author rodrigo.saraujo
 * 
 */
public enum DominioTipoDocumentoPatrimonio implements Dominio {

	/**
	 * 	Parecer Técnico
	 */
	PARECER_TECNICO(1),
	/**
	 * 	Manual
	 */
	MANUAL(2),
	/**
	 * 	Parecer do Fornecedor
	 */
	PARECER_FORNECEDOR(3),
	/**
	 * 	Comprovante de Residência
	 */
	COMPROVANTE_RESIDENCIA(4),
	/**
	 * 	Foto
	 */
	FOTO(5),
	/**
	 * 	Descrição Técnica
	 */
	DESCRICAO_TECNICA(6),
	/**
	 * 	Edital
	 */
	EDITAL(7),
	/**
	 * 	Carta Registrada
	 */
	CARTA_REGISTRADA(8), 
	/**
	 * 	Nota Fiscal
	 */
	NOTA_FISCAL(9),
	/**
	 * 	Documento de Identificação
	 */
    DOCUMENTO_IDENTIFICACAO(10),
    /**
	 * 	Aprovação Processo de Baixa
	 */
	APROVACAO_PROCESSO_BAIXA(11),
	/**
	 * 	Aditivo
	 */
	ADITIVO(12),
	/**
	 * 	Certidão de Filantropia
	 */
	CERTIDAO_FILANTROPIA(13),
	/**
	 * 	CNPJ
	 */
	CNPJ(14),
	/**
	 * 	Ata de Nomeação
	 */
	ATA_NOMEACAO(15),
	/**
	 * 	Boletim de Ocorrência
	 */
	BOLETIM_OCORRENCIA(16),
	/**
	 * 	Evidência
	 */
	EVIDENCIA(17),
	/**
	 * 	Laudos de Baixa
	 */
	LAUDOS_BAIXA(18),
	/**
	 * 	Parecer da Auditoria
	 */
	PARECER_AUDITORIA(19),
	/**
	 * 	Ata de Leilão
	 */
	ATA_DE_LEILAO(20),
	/**
	 * 	Nota Fiscal Saída
	 */
	NOTA_FISCAL_SAIDA(21),
	/**
	 * 	Outros
	 */
	OUTROS(22)
	;

	private Integer valor;
	
	private DominioTipoDocumentoPatrimonio(Integer valor) {
		this.valor = valor;
	}	
	
	@Override
	public int getCodigo() {
		return this.valor;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PARECER_TECNICO:
			return "Parecer Técnico";
		case MANUAL:
			return "Manual";
		case PARECER_FORNECEDOR:
			return "Parecer do Fornecedor";
		case COMPROVANTE_RESIDENCIA:
			return "Comprovante de Residência";
		case FOTO:
			return "Foto";
		case DESCRICAO_TECNICA:
			return "Descrição Técnica";
		case EDITAL:
			return "Edital";
		case CARTA_REGISTRADA:
			return "Carta Registrada";
		case NOTA_FISCAL:
			return "Nota Fiscal";
		case DOCUMENTO_IDENTIFICACAO:
			return "Documento de Identificação";
		case APROVACAO_PROCESSO_BAIXA:
			return "Aprovação Processo de Baixa";
		case ADITIVO:
			return "Aditivo";
		case CERTIDAO_FILANTROPIA:
			return "Certidão de Filantropia";
		case CNPJ:
			return "CNPJ";
		case ATA_NOMEACAO:
			return "Ata de Nomeação";
		case BOLETIM_OCORRENCIA:
			return "Boletim de Ocorrência";
		case EVIDENCIA:
			return "Evidência";
		case LAUDOS_BAIXA:
			return "Laudos de Baixa";
		case PARECER_AUDITORIA:
			return "Parecer da Auditoria";
		case ATA_DE_LEILAO:
			return "Ata de Leilão";
		case NOTA_FISCAL_SAIDA:
			return "Nota Fiscal Saída";
		case OUTROS:
			return "Outros";
		default:
			return "";
		}
	}	


	public static String getDescricao(Integer i) {
		switch (i) {
		case 1:
			return "Parecer Técnico";
		case 2:
			return "Manual";
		case 3:
			return "Parecer do Fornecedor";
		case 4:
			return "Comprovante de Residência";
		case 5:
			return "Foto";
		case 6:
			return "Descrição Técnica";
		case 7:
			return "Edital";
		case 8:
			return "Carta Registrada";
		case 9:
			return "Nota Fiscal";
		case 10:
			return "Documento de Identificação";
		case 11:
			return "Aprovação Processo de Baixa";
		case 12:
			return "Aditivo";
		case 13:
			return "Certidão de Filantropia";
		case 14:
			return "CNPJ";
		case 15:
			return "Ata de Nomeação";
		case 16:
			return "Boletim de Ocorrência";
		case 17:
			return "Evidência";
		case 18:
			return "Laudos de Baixa";
		case 19:
			return "Parecer da Auditoria";
		case 20:
			return "Ata de Leilão";
		case 21:
			return "Nota Fiscal Saída";
		case 22:
			return "Outros";
		default:
			return "";
		}
	}	
	
}