package br.gov.mec.aghu.papelaria.business;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.ws.BindingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.tempuri.Service1;
import org.tempuri.Service1Soap;

import br.gov.mec.aghu.business.integracao.AGHUIntegracao;
import br.gov.mec.aghu.business.integracao.ServiceEnum;
import br.gov.mec.aghu.business.integracao.exception.AGHUIntegracaoException;
import br.gov.mec.aghu.business.integracao.exception.ESBInacessivelException;
import br.gov.mec.aghu.business.integracao.exception.ExecucaoServicoException;
import br.gov.mec.aghu.business.integracao.exception.ExecucaoServicoNegocioException;
import br.gov.mec.aghu.business.integracao.exception.ServicoIndisponivelException;
import br.gov.mec.aghu.business.integracao.exception.ServicoSemRespostaException;
import br.gov.mec.aghu.sig.custos.vo.PedidoPapelariaVO;
import br.gov.mec.aghu.sig.custos.vo.PedidosPapelariaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta" })
@Stateless
public class PapelariaIntegracaoON extends AGHUIntegracao {

	private static final long serialVersionUID = 8965264594576662465L;
	
	private static final Log LOG = LogFactory.getLog(PapelariaIntegracaoON.class);

	
	private enum PapelariaIntegracaoONExceptionCode implements
			BusinessExceptionCode {
		JAXB_PARSER_EXCEPTION, ESB_INACESSIVEL_EXCEPTION, EXECUCAO_SERVICO_EXCEPTION, EXECUCAO_SERVICO_NEGOCIO_EXCEPTION, SERVICO_INDISPONIVEL_EXCEPTION, SERVICO_SEM_RESPOSTA_EXCEPTION, ERRO_CHAMADA_SERVICO
	}

	public PedidosPapelariaVO buscarPedidosPapelaria(String autorizacao, String papelariaServiceAddress)
			throws ApplicationBusinessException {
		PedidosPapelariaVO result = null;

		result = buscarPedidosPapelariaWs(autorizacao, papelariaServiceAddress);

		return result;
	}

	private PedidosPapelariaVO buscarPedidosPapelariaWs(String autorizacao, String papelariaServiceAddress)
			throws ApplicationBusinessException {
		Service1 service = new Service1();
		Service1Soap port = service.getPort(Service1Soap.class);


		// aponta para o endpoint apropriado
		BindingProvider bindingProvider = (BindingProvider) port;
		bindingProvider.getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY, papelariaServiceAddress);

		PedidosPapelariaVO pedidosPapelariaVO;
		try {
			String pedidos = port.buscaPedidos(autorizacao);
			pedidosPapelariaVO = parseResultXML(pedidos);
			LOG.info("Servico de papelaria executado com sucesso");
		} catch (JAXBException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.JAXB_PARSER_EXCEPTION);
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.EXECUCAO_SERVICO_EXCEPTION);
		}

		return pedidosPapelariaVO;
	}

	/**
	 * @deprecated substituida por chamada através através de web service
	 * @param autorizacao
	 * @return
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	@SuppressWarnings("unused")
	private PedidosPapelariaVO buscarPedidosPapelariaJms(String autorizacao)
			throws ApplicationBusinessException {
		LOG.info("Executando servico de pedidos de papelaria...");
		final Integer tempoEspera = 120000; // 2 mim de serviço
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("BuscaPedidos.Autorizacao", autorizacao);
		try {
			Object retorno = this.invocarServicoSincrono(
					ServiceEnum.BUSCA_PEDIDOS_PAPELARIA, parametros,
					tempoEspera);
			if (retorno instanceof Map<?, ?>) {
				Map<String, Object> mapRetorno = (Map<String, Object>) retorno;
				PedidosPapelariaVO pedidosPapelaria = parseResultXML(mapRetorno);
				return pedidosPapelaria;
			}

			this.logInfo("Servico de papelaria executado com sucesso");
		} catch (JAXBException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.JAXB_PARSER_EXCEPTION);
		} catch (ESBInacessivelException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.ESB_INACESSIVEL_EXCEPTION);
		} catch (ExecucaoServicoException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.EXECUCAO_SERVICO_EXCEPTION);
		} catch (ExecucaoServicoNegocioException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.EXECUCAO_SERVICO_NEGOCIO_EXCEPTION);
		} catch (ServicoIndisponivelException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.SERVICO_INDISPONIVEL_EXCEPTION);
		} catch (ServicoSemRespostaException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.SERVICO_SEM_RESPOSTA_EXCEPTION);
		} catch (AGHUIntegracaoException e) {
			throw new ApplicationBusinessException(
					PapelariaIntegracaoONExceptionCode.ERRO_CHAMADA_SERVICO);
		}

		return null;
	}

	private PedidosPapelariaVO parseResultXML(String xml) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(PedidosPapelariaVO.class);
		Unmarshaller um = context.createUnmarshaller();
		StringReader reader = new StringReader(xml);
		PedidosPapelariaVO pedidosPapelariaVO = (PedidosPapelariaVO) um
				.unmarshal(reader);

		return pedidosPapelariaVO;
	}

	/**
	 * @deprecated usar @see parseResultXML(String xml)
	 * @param mapRetorno
	 * @return
	 * @throws JAXBException
	 */
	private PedidosPapelariaVO parseResultXML(Map<String, Object> mapRetorno)
			throws JAXBException {
		String xml = (String) mapRetorno.get(PedidoPapelariaVO.Fields.RETORNO
				.toString());

		return parseResultXML(xml);
	}

	
	
}