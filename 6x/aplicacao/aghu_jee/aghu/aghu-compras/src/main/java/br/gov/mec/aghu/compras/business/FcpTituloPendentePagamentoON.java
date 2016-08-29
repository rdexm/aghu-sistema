package br.gov.mec.aghu.compras.business;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.contaspagar.business.GeracaoArquivoCsvRN;
import br.gov.mec.aghu.compras.contaspagar.business.exception.ComprasExceptionCode;
import br.gov.mec.aghu.compras.contaspagar.impl.RegistroCsvTitulosPendentes;
import br.gov.mec.aghu.compras.contaspagar.vo.TituloPendenteVO;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FcpTituloPendentePagamentoON extends BaseBusiness{

	private static final long serialVersionUID = -4234693273198400403L;		 
	
		@EJB
		private FcpTituloPendentePagamentoRN fcpTituloPendentePagamentoRN;		
		
		@EJB
		private GeracaoArquivoCsvRN geracaoArquivoCsvRN;

		/** Prefixo do nome do arquivo csv dos boletos bloqueados */
		private static final String PREFIXO_ARQ_CSV = "titulos_pendentes_pagamento";

		
	/**
	 * Método responsável pela coleção de títulos pendentes de pagamento
	 * @param dtInicialVencimentoTitulo data inicial de vencimento do título
	 * @param dtFinalVencimentoTitulo data final de vencimento do título
	 * @param dtInicialEmissaoDocumentoFiscal data inicial emissão do documento fiscal
	 * @param dtFinalEmissaoDocumentoFiscal data final de emissão de documentos fiscal
	 * @param fornecedor 
	 * @return Colecao Títulos Pendentes
	 * @throws ApplicationBusinessException Exceção caso alguma regra de negócio não seja antendida
	 */
	public List<TituloPendenteVO> pesquisarTituloPendentePagamento(
			Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo,
			Date dtInicialEmissaoDocumentoFiscal,
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor) throws ApplicationBusinessException {		
		return pesquisaTitulosComRegras(dtInicialVencimentoTitulo, dtFinalVencimentoTitulo, dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal, fornecedor);
	}
	
	/**
	 * Método que retorna informações para geração do CSV
	 * @param dtInicialVencimentoTitulo data inicial de vencimento do título
	 * @param dtFinalVencimentoTitulo data final de vencimento do título
	 * @param dtInicialEmissaoDocumentoFiscal data inicial emissão do documento fiscal
	 * @param dtFinalEmissaoDocumentoFiscal data final de emissão de documentos fisca
	 * @param fornecedor
	 * @return Objeto com as informações para geração do arquivo
	 * @throws ApplicationBusinessException  Exceção caso alguma regra de negócio não seja antendida
	 */
	public ArquivoURINomeQtdVO gerarCSVTituloPendentePagamento(
			Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo,
			Date dtInicialEmissaoDocumentoFiscal,
			Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor)
			throws ApplicationBusinessException {
		List<TituloPendenteVO> colecao = pesquisaTitulosComRegras(dtInicialVencimentoTitulo, dtFinalVencimentoTitulo, dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal, fornecedor);
		return gerarArquivoTextoTitulosPendentes(colecao);
	}
	
	/**
	 * Método que valida as regras de acordo com os filtros inseridos
	 * @param dtInicialVencimentoTitulo data inicial de vencimento de título
	 * @param dtFinalVencimentoTitulo data final de vencimento de título
	 * @param dtInicialEmissaoDocumentoFiscal data inicial de emissão de documento fiscal
	 * @param dtFinalEmissaoDocumentoFiscal data final de emissão de documento fiscal
	 * @param fornecedor fornecedor
	 * @return Lista com títulos pendentes
	 * @throws ApplicationBusinessException Exceção caso alguma regra de negócio não seja atendida.
	 */
	private List<TituloPendenteVO> pesquisaTitulosComRegras(Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo, Date dtInicialEmissaoDocumentoFiscal, Date dtFinalEmissaoDocumentoFiscal, ScoFornecedor fornecedor) throws ApplicationBusinessException {
		List<TituloPendenteVO> colecao = null;
		if (isPeriodoValido(dtInicialVencimentoTitulo, dtFinalVencimentoTitulo) && isPeriodoValido(dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal)) {					
			colecao = this.getFcpTituloPendentePagamentoRN().pesquisarTituloPendentePagamento(dtInicialVencimentoTitulo, dtFinalVencimentoTitulo, dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal, fornecedor);
			if (colecao.isEmpty()) {
				if (isFiltroValido(dtInicialVencimentoTitulo, dtFinalVencimentoTitulo, dtInicialEmissaoDocumentoFiscal, dtFinalEmissaoDocumentoFiscal)) {
					throw new ApplicationBusinessException("NAO_EXISTEM_TITULOS_PENDENTES_PERIODO", Severity.ERROR, colecao);
				} else {					
					throw new ApplicationBusinessException("NAO_EXISTEM_TITULOS_PENDENTES", Severity.ERROR, colecao);
				}
			}
		} else {
			throw new ApplicationBusinessException("DATA_INICIAL_DEVE_SER_INFERIOR", Severity.ERROR, colecao);
		}
		return colecao;
	}	
	
	/**
	 * Método responsável por gerar o arquivo csv com os títulos pendentes
	 * 
	 * @return Retorna um objeto ArquivoURINomeQtdVO com a uri, nome, tamanho do arquivo CSV para download
	 */
	private ArquivoURINomeQtdVO gerarArquivoTextoTitulosPendentes(List<TituloPendenteVO> colecao) throws ApplicationBusinessException {
		ArquivoURINomeQtdVO result = null;
		URI uriCsv = null;
		GeracaoArquivoCsvRN geraRn = null;
		String prefixo = null;
		String nomeCsv = null;		
		try {
			prefixo = obterPrefixoNomeArquivo(PREFIXO_ARQ_CSV, new Date());			
			if (colecao != null && colecao.size() > 0) {			
				geraRn = this.geracaoArquivoCsvRN;
				uriCsv = geraRn.gerarDadosEmArquivo(RegistroCsvTitulosPendentes.class, colecao, prefixo, null);
				nomeCsv = GeracaoArquivoCsvRN.obterNomeArquivo(uriCsv, true);
				result = new ArquivoURINomeQtdVO(uriCsv, nomeCsv, colecao.size(), 1);
			} else {	
				colecao = null;
			}			
		} catch (IOException e) {
			throw new ApplicationBusinessException(ComprasExceptionCode.ERRO_GERAR_ARQUIVO, Severity.ERROR);			
		}		
		return result;
	}	
	
	/**
	 * Método que fornece o nome do arquivo
	 * @param prefix prefixo do nome
	 * @param data data de geração
	 * @return nome formatado
	 */
	private static String obterPrefixoNomeArquivo(String prefix, final Date data) {
		String result = null;
		SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy");
		String formattedDate = format.format(data);
		result = prefix + formattedDate + "-";
		return result;
	}
	
	/**
	 * Método que verifica se alguma data foi inserida no filtro
	 * @param dtInicialVencimentoTitulo data inicial de vencimento do título
	 * @param dtFinalVencimentoTitulo data final de vencimento do título
	 * @param dtInicialEmissaoDocumentoFiscal data inicial de emissão fiscal 
	 * @param dtFinalEmissaoDocumentoFiscal data final de emissão fiscal
	 * @return boolean 
	 */
	private boolean isFiltroValido(Date dtInicialVencimentoTitulo, Date dtFinalVencimentoTitulo, Date dtInicialEmissaoDocumentoFiscal, Date dtFinalEmissaoDocumentoFiscal) {
		return dtInicialVencimentoTitulo != null || dtFinalVencimentoTitulo != null || dtInicialEmissaoDocumentoFiscal != null || dtFinalEmissaoDocumentoFiscal != null;
	}
	
	/**
	 * Método que valida se a data inicial é anterior a data final
	 * @param dtInicial data inicial	 
	 * @param dtFinal data final
	 * @return boolean
	 */
	private boolean isPeriodoValido(Date dtInicial, Date dtFinal) {
		if (dtInicial != null && dtFinal != null) {
			if (!DateUtil.validaDataMenor(dtInicial, dtFinal)) {
				return false;
			}
		}
		return true;
	}
		
	public FcpTituloPendentePagamentoRN getFcpTituloPendentePagamentoRN() {
		return fcpTituloPendentePagamentoRN;
	}

	public void setFcpTituloPendentePagamentoRN(
			FcpTituloPendentePagamentoRN fcpTituloPendentePagamentoRN) {
		this.fcpTituloPendentePagamentoRN = fcpTituloPendentePagamentoRN;
	}

	@Override
	protected Log getLogger() {
		return null;
	}	

}
