package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.FcpTipoDocumentoPagamento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FcpTipoDocumentoPagamentoON extends BaseBusiness {


	/**
	 * 
	 */
	private static final long serialVersionUID = 6153096761716094442L;
	
	/**
	 * Injeção da RN para chamada de metodos
	 */
	@EJB
	private FcpTipoDocumentoPagamentoRN fcpTipoDocPagamentoRN;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	/**
	 * Metodo utilizado para retornar uma lista de fcpTipoDocumentoPagamento
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param fcpTipoDocumentoPagamento
	 * @return
	 */
	public List<FcpTipoDocumentoPagamento> pesquisarTiposDocumentoPagamento(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento)  {
		List<FcpTipoDocumentoPagamento> retorno = this.fcpTipoDocPagamentoRN.pesquisarTiposDocumentoPagamento(firstResult, maxResult, orderProperty, asc, fcpTipoDocumentoPagamento) ;
		return retorno;
	}
	
	/**
	 * Metodo utilizado para fazer o count
	 * 
	 * @param fcpTipoDocumentoPagamento
	 * @return
	 */
	public Long pesquisarCountTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento)   {
		return this.fcpTipoDocPagamentoRN.pesquisarCountTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
	}
	
	/**
	 * Metodo usado para inserir um novo tipoDocumentoPagamento
	 * 
	 * @param fcpTipoDocumentoPagamento
	 * @throws ApplicationBusinessException
	 */
	public void inserirTipoDocumentoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException {
		this.fcpTipoDocPagamentoRN.inserirTipoDocumentoPagamento(fcpTipoDocumentoPagamento) ;
	}
	
	/**
	 * Metodo usado para alterar tipoDocumentoPagamento
	 * 
	 * @param fcpTipoDocumentoPagamento
	 * @throws ApplicationBusinessException
	 */
	public void atualizarTipoDocumentoPagamento(FcpTipoDocumentoPagamento fcpTipoDocumentoPagamento) throws ApplicationBusinessException  {
		this.fcpTipoDocPagamentoRN.atualizarTipoDocumentoPagamento(fcpTipoDocumentoPagamento);
	}
	
	
	public FcpTipoDocumentoPagamentoRN getFcpTipoDocPagamentoRN() {
		return fcpTipoDocPagamentoRN;
	}

	public void setFcpTipoDocPagamentoRN(FcpTipoDocumentoPagamentoRN fcpTipoDocPagamentoRN) {
		this.fcpTipoDocPagamentoRN = fcpTipoDocPagamentoRN;
	}

	
}
