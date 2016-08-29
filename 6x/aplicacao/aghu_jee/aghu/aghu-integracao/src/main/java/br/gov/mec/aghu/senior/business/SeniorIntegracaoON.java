package br.gov.mec.aghu.senior.business;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.senior.services.cliente.ClientesGravarClientesIn;
import br.com.senior.services.cliente.ClientesGravarClientesInDadosGeraisCliente;
import br.com.senior.services.cliente.ClientesGravarClientesInDadosGeraisClienteDefinicoesCliente;
import br.com.senior.services.cliente.ClientesGravarClientesOut;
import br.com.senior.services.cliente.G5SeniorServices;
import br.com.senior.services.cliente.ObjectFactory;
import br.com.senior.services.cliente.SapiensSynccomSeniorG5CoGerCadClientes;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.integracao.AGHUIntegracao;
import br.gov.mec.aghu.exames.vo.ClienteNfeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;


@SuppressWarnings({"PMD.HierarquiaONRNIncorreta" })
@Stateless
public class SeniorIntegracaoON extends AGHUIntegracao {

	private static final long serialVersionUID = 8965264594576662465L;
	
	private static final Log LOG = LogFactory.getLog(SeniorIntegracaoON.class);

	
	private IParametroFacade parametroFacade = ServiceLocator.getBean(IParametroFacade.class, "aghu-configuracao");
	
	private enum SeniorONExceptionCode implements
			BusinessExceptionCode {
		 SERVICO_INDISPONIVEL_EXCEPTION, ENVIO_CLIENTE_NOTA_SENIOR_ERRO
	}

	public Long gravarClienteNota(ClienteNfeVO clienteNfeVo) throws ApplicationBusinessException {
		try {
			G5SeniorServices servicoCliente = new G5SeniorServices();
			SapiensSynccomSeniorG5CoGerCadClientes port = servicoCliente.getSapiensSynccomSeniorG5CoGerCadClientesPort();
			
			// aponta para o endpoint apropriado
					BindingProvider bindingProvider = (BindingProvider) port;
					bindingProvider.getRequestContext().put(
							BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getUrlEndpoint());
					
			ClientesGravarClientesIn cliente = new ClientesGravarClientesIn();
			
		
			ObjectFactory objectFactory = new ObjectFactory();
			
					
			JAXBElement<String> jaxbElementApeCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteApeCli(clienteNfeVo.getApeCli());
	
			JAXBElement<String> jaxbElementCgcCpf = objectFactory.createClientesGravarClientesInDadosGeraisClienteCgcCpf(clienteNfeVo.getDocumento());
			JAXBElement<String> jaxbElementCliCon = objectFactory.createClientesGravarClientesInDadosGeraisClienteCliCon(clienteNfeVo.getCliCon());
			
			JAXBElement<String> jaxbElementCodCpg = objectFactory.createClientesGravarClientesInDadosGeraisClienteDefinicoesClienteCodCpg(clienteNfeVo.getCodCpg());
		    JAXBElement<Integer> jaxbElementCodEmp = objectFactory.createClientesGravarClientesInDadosGeraisClienteDefinicoesClienteCodEmp(clienteNfeVo.getCodEmp());
		    JAXBElement<Integer> jaxbElementCodFil = objectFactory.createClientesGravarClientesInDadosGeraisClienteDefinicoesClienteCodFil(clienteNfeVo.getCodFil());
		    JAXBElement<Integer> jaxbElementCodRep = objectFactory.createClientesGravarClientesInDadosGeraisClienteDefinicoesClienteCodRep(clienteNfeVo.getCodRep());
		    JAXBElement<Integer> jaxbElementCtaRed = objectFactory.createClientesGravarClientesInDadosGeraisClienteDefinicoesClienteCtaRed(clienteNfeVo.getCtaRed());
		   
		    JAXBElement<String> jaxbElementNomeCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteNomCli(clienteNfeVo.getNomCli());
		   
			JAXBElement<String> jaxbElementIntNet = objectFactory.createClientesGravarClientesInDadosGeraisClienteIntNet(clienteNfeVo.getIntNet());
			JAXBElement<String> jaxbElementEmail = objectFactory.createClientesGravarClientesInDadosGeraisClienteEmaNfe(clienteNfeVo.getEmaNfe());				
			JAXBElement<String> jaxbElementCepCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteCepCli(clienteNfeVo.getCepCli());
			JAXBElement<String> jaxbElementNenCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteNenCli(clienteNfeVo.getNenCli());
			JAXBElement<String> jaxbElementEndCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteEndCli(clienteNfeVo.getEndCli());
			JAXBElement<String> jaxbElementCplEnd = objectFactory.createClientesGravarClientesInDadosGeraisClienteCplEnd(clienteNfeVo.getCplEnd());
			JAXBElement<String> jaxbElementSigUfs = objectFactory.createClientesGravarClientesInDadosGeraisClienteSigUfs(clienteNfeVo.getSigUfs());
			JAXBElement<String> jaxbElementSitCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteSitCli(clienteNfeVo.getSitCli());		
			JAXBElement<String> jaxbElementTipCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteTipCli(clienteNfeVo.getTipCli());
			JAXBElement<String> jaxbElementTipMer = objectFactory.createClientesGravarClientesInDadosGeraisClienteTipMer(clienteNfeVo.getTipMer());
			JAXBElement<String> jaxbElementCodPai = objectFactory.createClientesGravarClientesInDadosGeraisClienteCodPai(clienteNfeVo.getCodPai());
			JAXBElement<String> jaxbElementCidCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteCidCli(clienteNfeVo.getCidCli());
			JAXBElement<String> jaxbElementBaiCli = objectFactory.createClientesGravarClientesInDadosGeraisClienteBaiCli(clienteNfeVo.getBaiCli());
			
			
			ClientesGravarClientesInDadosGeraisCliente dadosGeraisCliente = new ClientesGravarClientesInDadosGeraisCliente();
			
			dadosGeraisCliente.setApeCli(jaxbElementApeCli);
			dadosGeraisCliente.setCepCli(jaxbElementCepCli);
			dadosGeraisCliente.setCgcCpf(jaxbElementCgcCpf);
			dadosGeraisCliente.setCliCon(jaxbElementCliCon);
			
			
			ClientesGravarClientesInDadosGeraisClienteDefinicoesCliente  definicoesCliente = new ClientesGravarClientesInDadosGeraisClienteDefinicoesCliente();
			
			definicoesCliente.setCodCpg(jaxbElementCodCpg);
			definicoesCliente.setCodEmp(jaxbElementCodEmp);
			definicoesCliente.setCodFil(jaxbElementCodFil);
			definicoesCliente.setCodRep(jaxbElementCodRep);
			definicoesCliente.setCtaRed(jaxbElementCtaRed);
			
			dadosGeraisCliente.getDefinicoesCliente().add(definicoesCliente);		
			
			
			dadosGeraisCliente.setIntNet(jaxbElementIntNet);
			dadosGeraisCliente.setEmaNfe(jaxbElementEmail);
			dadosGeraisCliente.setNomCli(jaxbElementNomeCli);
			dadosGeraisCliente.setNenCli(jaxbElementNenCli);
			dadosGeraisCliente.setEndCli(jaxbElementEndCli);
			dadosGeraisCliente.setCplEnd(jaxbElementCplEnd);
			dadosGeraisCliente.setSigUfs(jaxbElementSigUfs);
			dadosGeraisCliente.setSitCli(jaxbElementSitCli);
			dadosGeraisCliente.setTipCli(jaxbElementTipCli);
			dadosGeraisCliente.setTipMer(jaxbElementTipMer);
			dadosGeraisCliente.setCodPai(jaxbElementCodPai);
			dadosGeraisCliente.setCidCli(jaxbElementCidCli);
			dadosGeraisCliente.setBaiCli(jaxbElementBaiCli);
			
			
			
			cliente.getDadosGeraisCliente().add(dadosGeraisCliente);
	        ClientesGravarClientesOut  clientesGravarClientesOut = port.gravarClientes(getUser(), getPwd(), 0, cliente);
			
	        if (clientesGravarClientesOut.getRetornosClientes() != null &&
	        	clientesGravarClientesOut.getRetornosClientes().size() > 0	){
	        	if (!clientesGravarClientesOut.getRetornosClientes().get(0).getRetorno().getValue().equalsIgnoreCase("OK")){
	        		throw new ApplicationBusinessException(SeniorONExceptionCode.ENVIO_CLIENTE_NOTA_SENIOR_ERRO, clientesGravarClientesOut.getRetornosClientes().get(0).getRetorno().getValue());
	        	}
	        	
	        	
	        	return Long.valueOf(clientesGravarClientesOut.getRetornosClientes().get(0).getCodCli().getValue());
	        }
	        
	        return null;
			
		}
		catch(WebServiceException e){
			throw new ApplicationBusinessException(
					SeniorONExceptionCode.SERVICO_INDISPONIVEL_EXCEPTION);
		}
				
	}	
	
	private String getUrlEndpoint() throws ApplicationBusinessException {
		String result = null;
		try {
			result = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_URL_WS_COD_CLIENTE_NFE);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao buscar parâmetro P_URL_WS_COD_CLIENTE_NFE",e);
			throw new ApplicationBusinessException(e);
		}
		return result;
	}
	
	private String getUser() throws ApplicationBusinessException {
		String result = null;
		try {
			result = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NFSE_USUARIO_WS);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao buscar parâmetro P_NFSE_USUARIO_WS",e);
			throw new ApplicationBusinessException(e);
		}
		return result;
	}
	
	private String getPwd() throws ApplicationBusinessException {
		String result = null;
		try {
			result = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_NFSE_PASSWORD_WS);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao buscar parâmetro P_NFSE_PASSWORD_WS",e);
			throw new ApplicationBusinessException(e);
		}
		return result;
	}
	
	
}