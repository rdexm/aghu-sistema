package br.gov.mec.aghu.sicon.business;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioClassificacaoErroXML;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioStatusEnvio;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoLogEnvioSiconDAO;
import br.gov.mec.aghu.sicon.dao.ScoResContratoDAO;
import br.gov.mec.aghu.sicon.util.Cnet;
import br.gov.mec.aghu.sicon.util.integracaosicon.CnetRet;
import br.gov.mec.aghu.sicon.vo.DadosEnvioVO;
import br.gov.mec.aghu.sicon.vo.EnvioItemVO;
import br.gov.mec.aghu.sicon.vo.SiconResponseVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe para utilização das regras negócio necessárias para o cadastro de
 * contratos manuais.
 * 
 * @author agerling
 * 
 */

@SuppressWarnings("PMD.AtributoEmSeamContextManager")
@Stateless
public class EnvioContratoSiasgSiconON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EnvioContratoSiasgSiconON.class);
	private static final long serialVersionUID = 7176845511302796913L;

	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private ScoResContratoDAO scoResContratoDAO;
	
	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	
	@Inject
	private ScoAditContratoDAO scoAditContratoDAO;
	
	@Inject
	private ScoContratoDAO scoContratoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoLogEnvioSiconDAO scoLogEnvioSiconDAO;


	public enum EnvioContratoSiasgSiconONExceptionCode implements BusinessExceptionCode {
		ERRO_CONTEXTO_XML, ERRO_CRIACAO_XML, ERRO_ENVIO_XML, CONT_INEXISTENTE, CONTRATO_NAO_ENCONTRADO, ERRO_RETORNO_ENVIO_XML, OPERACAO_ENVIO_NAO_SUPORTADA, CPF_NAO_ENCONTRADO;
	}
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");


	/**
	 * OBS.: Método comentando para não configurar o certificado no AGHU e deixar
	 * isso como configuração do servidor
	 * 
	 * Configura a trust store para requisição HTTPS de envio de dados.<br />
	 * Busca o caminho completo do arquivo de certificados na agh parametros e
	 * verifica a existencia do arquivo no servidor.
	 * 
	 * @throws ApplicationBusinessException
	 *             se exception ao buscar parâmetro
	 * @throws ApplicationBusinessException
	 *             se o arquivo imformado no parâmetro não existir no servidor
	 */
	/*
	private void configuraTrustStore() throws ApplicationBusinessException {
		// configura key store
		AghParametros chave = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_PATH_CHAVE_SICON);
		String trustStorePath = chave.getVlrTexto();

		logDebug("------// Path keystore : " + trustStorePath);
				
		// verifica existencia do arquivo
		File trustStore = new File(trustStorePath);
		if (!trustStore.exists() || !trustStore.isFile()) {
			logError("ERRO - Operação de envio não suportada, "
							+ "falta o arquivo de certificados para a requisição HTTPS. ");
			logError(
					"O arquivo " + trustStore.getAbsolutePath()
							+ " não existe neste servidor.");
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.OPERACAO_ENVIO_NAO_SUPORTADA);
		}

		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", "12345678");
		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
	}*/

	/**
	 * Método de envio do xml de Inclusão e Alteração de Contrato para o SICON.
	 * 
	 */
	public SiconResponseVO integrarSIASG(DadosEnvioVO dadosEnvioVO,
			int contSeq, boolean usaVlrAfs) throws ApplicationBusinessException {

		SiconResponseVO siconResponseVO = new SiconResponseVO();

		ScoContrato contrato = getScoContratoDAO().obterPorChavePrimaria(
				contSeq);

		if (contrato == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.CONT_INEXISTENTE);
		}

		List<Cnet.Itens.Item> itensAux = armazenarItensContrato(dadosEnvioVO
				.getCnet());

		// Enviando XML
		if(LOG.isDebugEnabled()){
			logDebug("---// Processo XML(1) : envio do XML");
			logDebug("XML Envio: " + dadosEnvioVO.getXmlEnvio());
		}
		String xmlRetorno = enviarXMLSIASG(dadosEnvioVO.getXmlEnvio());

		// Tratando Retorno do Envio
		if(LOG.isDebugEnabled()){
			logDebug("---------// Processo XML(2) : tratamento de retorno do envio do XML");		
			logDebug("XML Retorno: " + xmlRetorno);
		}
		siconResponseVO = tratarXmlRetorno(xmlRetorno, itensAux);

		atualizarLogEnvioContrato(siconResponseVO, contSeq, usaVlrAfs);

		atualizarContratoEnviado(contSeq, siconResponseVO, dadosEnvioVO,
				usaVlrAfs);

		return siconResponseVO;
	}

	public SiconResponseVO atualizarLogEnvioContrato(
			SiconResponseVO siconResponseVO, int contSeq, Boolean usaVlrAfs)
			throws ApplicationBusinessException {

		ScoContrato contrato = getScoContratoDAO().obterPorChavePrimaria(
				contSeq);

		if (contrato == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.CONT_INEXISTENTE);
		}

		// Atualizando Log de Envio
		if(LOG.isDebugEnabled()){
			logDebug("------------------// Processo XML(3) : atualizando tabela de log do envio");
			logDebug(" Status do Envio : " + siconResponseVO.getStatusEnvio());
		}

		ScoLogEnvioSicon logEnvioSicon = atualizarLogEnvioContrato(contrato,
				usaVlrAfs, siconResponseVO);
		siconResponseVO.setLogEnvioSicon(logEnvioSicon);

		if(LOG.isDebugEnabled()){
			logDebug("------------------// Processo XML(3.1) : tabela de log do envio atualizada");
		}

		return siconResponseVO;
	}

	/**
	 * Método de envio do xml de Inclusão e Alteração de Aditivo para o SICON.
	 * 
	 */
	public SiconResponseVO integrarAditivoSIASG(DadosEnvioVO dadosEnvioVO,
			ScoAditContrato aditContrato) throws ApplicationBusinessException {

		SiconResponseVO siconResponseVO = new SiconResponseVO();

		// Enviando XML
		if(LOG.isDebugEnabled()){
			logDebug("---// Processo XML(1) : envio XML Aditivo");
			logDebug("XML Envio Aditivo: " + dadosEnvioVO.getXmlEnvio());
		}
		String xmlRetorno = enviarXMLSIASG(dadosEnvioVO.getXmlEnvio());

		// Tratando Retorno do Envio
		if(LOG.isDebugEnabled()){
			logDebug("------// Processo XML(2) : tratamento de retorno do envio XML Aditivo");
			logDebug("XML Retorno Aditivo: " + xmlRetorno);
		}
		siconResponseVO = tratarXmlRetorno(xmlRetorno, null);

		// Atualizando Log de Envio
		if(LOG.isDebugEnabled()){
			logDebug("------------// Processo XML(3) : atualizando tabela de log do envio Aditivo");
			logDebug(" Status do Envio Aditivo : "
						+ siconResponseVO.getStatusEnvio());
		}
		ScoLogEnvioSicon logEnvioSicon = atualizarLogEnvioAditivo(aditContrato,
				siconResponseVO);
		siconResponseVO.setLogEnvioSicon(logEnvioSicon);

		// Atualizando Dados do Aditivo
		if(LOG.isDebugEnabled()){
			logDebug("------------// Processo XML(3.1) : atualizando tabela de log do envio Aditivo - Finalizado");
			logDebug("------------------------------// Processo XML(4) : atualizando dados do Aditivo");
			logDebug(" Número Aditivo  : " + aditContrato.getId().getSeq());
			logDebug(
					" Número contrato : " + aditContrato.getCont().getNrContrato());
		}
		atualizarAditivo(aditContrato, siconResponseVO);

		if(LOG.isDebugEnabled()){
			logDebug("Processo XML(5) --- Processo XML Aditivo - Finalizado --------- ");
		}

		return siconResponseVO;
	}

	public SiconResponseVO integrarRescisaoSIASG(DadosEnvioVO dadosEnvioVO,
			ScoResContrato rescisaoContrato) throws ApplicationBusinessException {

		SiconResponseVO siconResponseVO = new SiconResponseVO();

		// Enviando XML
		if(LOG.isDebugEnabled()){
			logDebug("---// Processo XML(1) : envio do XML Rescisão");
			logDebug("XML Envio: " + dadosEnvioVO.getXmlEnvio());
		}
		String xmlRetorno = enviarXMLSIASG(dadosEnvioVO.getXmlEnvio());

		// Tratando Retorno do Envio
		if(LOG.isDebugEnabled()){
			logDebug("---------// Processo XML(2) : tratamento de retorno do envio do XML de Rescisão");
			logDebug("XML Retorno: " + xmlRetorno);
		}
		siconResponseVO = tratarXmlRetorno(xmlRetorno, null);

		// Atualizando Log de Envio
		if(LOG.isDebugEnabled()){
			logDebug("------------------// Processo XML(3) : atualizando tabela de log do envio da Rescisão");
			logDebug(" Status do Envio : " + siconResponseVO.getStatusEnvio());
		}
		ScoLogEnvioSicon logEnvioSicon = atualizarLogEnvioRescisao(
				rescisaoContrato, siconResponseVO);
		siconResponseVO.setLogEnvioSicon(logEnvioSicon);

		if(LOG.isDebugEnabled()){
			logDebug("------------------// Processo XML(3.1) : atualizando tabela de log do envio da Rescisão - Finalizado");
	
			// Atualizando Dados da Rescisao Contrato
			logDebug("------------------------------// Processo XML(4) : atualizando dados da Rescisão");
			logDebug(" Número contrato : "
							+ rescisaoContrato.getContrato().getNrContrato());
		}
		atualizarRescisaoContrato(rescisaoContrato, siconResponseVO);

		if(LOG.isDebugEnabled()){
			logDebug("Processo XML(5) --- Processo XML Rescisão - Finalizado --------- ");
		}

		return siconResponseVO;
	}

	private String enviarXMLSIASG(String xmlEnvio) throws ApplicationBusinessException {

		//configuraTrustStore();

		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;

		try {
			// URL para Post
			AghParametros urlParametro = getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_URL_INTEGRACAO_SICON);
			String url = urlParametro.getVlrTexto();

			if(LOG.isDebugEnabled()){
				logDebug(" Acesso URL Externa - HttpClient ");
				logDebug(" URL de envio xml sicon: " + url);
			}

			HttpClient client = new HttpClient();
			PostMethod post = new PostMethod(url);

			client.getParams().setParameter("http.useragent", "Test Client");

			NameValuePair[] params = { new NameValuePair("xml", xmlEnvio) };

			post.setRequestBody(params);

			int returncode = client.executeMethod(post);

			if (returncode == HttpStatus.SC_OK) {

				br = new BufferedReader(new InputStreamReader(
						post.getResponseBodyAsStream(), Charset.forName("ISO-8859-1")));
				String readLine;

				while (((readLine = br.readLine()) != null)) {
					sb.append(readLine);
				}

				sb.append(LINE_SEPARATOR);
			}

			return sb.toString();

		} catch (IOException e) {

			logError("------// Erro no HttpClient : ", e);

			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.ERRO_ENVIO_XML);
		}
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private SiconResponseVO tratarXmlRetorno(String xmlRetorno,
			List<Cnet.Itens.Item> itensAux) throws ApplicationBusinessException {

		CnetRet cnetRet = unmarshallXmlRet(xmlRetorno.getBytes());

		SiconResponseVO responseVO = new SiconResponseVO();

		boolean itemPendente = false;
		
		// Contrato, aditivo ou rescisão
		// Erro Criação XML
		if (cnetRet.getErroxml() != null && !cnetRet.getErroxml().equals("0")) {

			responseVO.setStatusEnvio(DominioStatusEnvio.ERRO);
			responseVO
					.setClassificaoErroXML(DominioClassificacaoErroXML.ESTRUTURA);

			responseVO.setCodigoErro(cnetRet.getErroxml());
			responseVO.setDescricaoErro(converteStringIsoUtf8(cnetRet.getDescxml()));

			return responseVO;
		}

		// Contrato, aditivo ou rescisão
		// Erro Validação XML
		if (cnetRet.getErronat() != null && !cnetRet.getErronat().equals("0")) {

			responseVO.setStatusEnvio(DominioStatusEnvio.ERRO);
			responseVO
					.setClassificaoErroXML(DominioClassificacaoErroXML.VALIDACAO);
			
			responseVO.setCodigoErro(cnetRet.getErroxml());
			responseVO.setDescricaoErro(converteStringIsoUtf8(cnetRet.getDescnat()));
			
			return responseVO;
		}

		// Somente inclusão, alteração de Contrato
		// Trata Retorno dos Itens Enviados
		// Verifica se foi enviado algum item
		if (itensAux != null
				&& itensAux.size() > 0
				&& (cnetRet.getItens() != null && cnetRet.getItens().getItem() != null)) {

			List<EnvioItemVO> listEnvioItemVO = new ArrayList<EnvioItemVO>();

			int indiceItem = 0;

			// Percorre retorno dos Itens
			for (CnetRet.Itens.Item itemRet : cnetRet.getItens().getItem()) {

				EnvioItemVO itemVO = new EnvioItemVO();

				// Dados do Item (Serviço/Material)
				itemVO.setNumItem(itensAux.get(indiceItem).getNumeroItem());

				if (itensAux.get(indiceItem).getCodigoMatServ() != null) {
					itemVO.setCodigoMatServ(itensAux.get(indiceItem)
							.getCodigoMatServ().getValue());
				}

				if (itensAux.get(indiceItem).getIndicadorMatServ() != null) {

					itemVO.setIndicadorMatServ(itensAux.get(indiceItem)
							.getIndicadorMatServ().getValue());

					if (itensAux.get(indiceItem).getIndicadorMatServ()
							.getValue().equals("S")) {

						Integer codSicon = Integer.valueOf(itensAux.get(indiceItem)
								.getCodigoMatServ().getValue());
						ScoServicoSicon scoServicoSicon = this
								.getCadastrosBasicosSiconFacade()
								.obterServicoCodigoSicon(codSicon);

						if (scoServicoSicon != null
								&& scoServicoSicon.getServico() != null) {
							itemVO.setCodigoInterno(scoServicoSicon
									.getServico().getCodigo().toString());
						}

					} else {
						if (itensAux.get(indiceItem).getIndicadorMatServ()
								.getValue().equals("M")) {

							Integer _codigoSicon = Integer.valueOf(itensAux
									.get(indiceItem).getCodigoMatServ()
									.getValue());
							ScoMaterialSicon scoMaterialSicon = this
									.getCadastrosBasicosSiconFacade()
									.obterPorCodigoSicon(_codigoSicon);

							if (scoMaterialSicon != null
									&& scoMaterialSicon.getMaterial() != null) {
								itemVO.setCodigoInterno(scoMaterialSicon
										.getMaterial().getCodigo().toString());
							}

						}
					}
				}

				if (itensAux.get(indiceItem).getDescricao() != null) {
					itemVO.setDescricao(itensAux.get(indiceItem).getDescricao()
							.getValue());
				}

				// Dados Retorno Envio
				if (StringUtils.isNotBlank(itemRet.getErronat())) {

					if (!itemRet.getErronat().equals("0")) {

						itemPendente = true;

						itemVO.setStatusEnvio(DominioStatusEnvio.ERRO);
						itemVO.setCodigoErro(itemRet.getErronat());
						itemVO.setDescricaoErro(itemRet.getDescnat());

					} else {

						itemVO.setStatusEnvio(DominioStatusEnvio.SUCESSO);
					}
				}

				listEnvioItemVO.add(itemVO);

				indiceItem += 1;
			}

			if (listEnvioItemVO.size() > 0) {
				responseVO.setListEnvioItemVO(listEnvioItemVO);
			}
		}

		// XML inclusão de Contrato
		// Contrato incluido parcialmente
		// Incluido no Sicon, porém com erro na validação de itens
		if (itemPendente) {
			responseVO.setStatusEnvio(DominioStatusEnvio.PENDENTE);
			responseVO
					.setClassificaoErroXML(DominioClassificacaoErroXML.ERRO_ITEM);
			
			responseVO.setDescricaoErro(converteStringIsoUtf8(cnetRet.getDescnat()));
			
		} else { // Contrato, aditivo ou rescisão - incluidos integralmente
			responseVO.setStatusEnvio(DominioStatusEnvio.SUCESSO);
		}

		return responseVO;
	}

	private CnetRet unmarshallXmlRet(byte[] bytes) throws ApplicationBusinessException {

		InputStream is = new ByteArrayInputStream(bytes);
		CnetRet cnetRet = null;

		try {
			JAXBContext context = JAXBContext
					.newInstance("br.gov.mec.aghu.sicon.util.integracaosicon");

			Unmarshaller unmarshaller = context.createUnmarshaller();

			cnetRet = unmarshaller.unmarshal(new StreamSource(is),
					CnetRet.class).getValue();

		} catch (JAXBException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.ERRO_RETORNO_ENVIO_XML);
		}

		return cnetRet;
	}

	/**
	 * Armazena os itens do contrato na sequencia que serão enviados para o
	 * SICON.
	 * 
	 * @param xmlEnvio
	 * @throws ApplicationBusinessException
	 */
	private List<Cnet.Itens.Item> armazenarItensContrato(Cnet cnet)
			throws ApplicationBusinessException {

		List<Cnet.Itens.Item> itensAux = new ArrayList<Cnet.Itens.Item>();

		if (cnet.getItens() != null && cnet.getItens().getItem() != null) {

			itensAux = cnet.getItens().getItem();
		}

		return itensAux;
	}

	private ScoLogEnvioSicon atualizarLogEnvioContrato(ScoContrato contrato,
			Boolean usaVlrAfs, SiconResponseVO siconResponseVO) {

		ScoLogEnvioSicon logEnvioSicon = new ScoLogEnvioSicon();

		logEnvioSicon.setContrato(contrato);
		logEnvioSicon.setDtEnvio(new Date());

		if (siconResponseVO.getStatusEnvio().equals(DominioStatusEnvio.ERRO)) {
			logEnvioSicon.setIndSucesso(DominioSimNao.N);
			logEnvioSicon.setDsErro(siconResponseVO.getDescricaoErro());
		} else {
			logEnvioSicon.setIndSucesso(DominioSimNao.S);
		}

		if (usaVlrAfs) {
			logEnvioSicon.setIndVlrAf(DominioSimNao.S);
		} else {
			logEnvioSicon.setIndVlrAf(DominioSimNao.N);
		}

		ScoLogEnvioSiconDAO scoLogEnvioSiconDAO = this.getLogEnvioSiconDAO();
		scoLogEnvioSiconDAO.persistir(logEnvioSicon);
		scoLogEnvioSiconDAO.flush();
		
		return logEnvioSicon;
	}

	private ScoLogEnvioSicon atualizarLogEnvioRescisao(
			ScoResContrato rescisaoContrato, SiconResponseVO siconResponseVO) {

		ScoLogEnvioSicon logEnvioSicon = new ScoLogEnvioSicon();

		logEnvioSicon.setContrato(rescisaoContrato.getContrato());
		logEnvioSicon.setRescisao(rescisaoContrato);
		logEnvioSicon.setDtEnvio(new Date());
		logEnvioSicon.setIndVlrAf(DominioSimNao.N);

		if (siconResponseVO.getStatusEnvio().equals(DominioStatusEnvio.ERRO)) {
			logEnvioSicon.setIndSucesso(DominioSimNao.N);
			logEnvioSicon.setDsErro(siconResponseVO.getDescricaoErro());
		} else {
			logEnvioSicon.setIndSucesso(DominioSimNao.S);
		}

		ScoLogEnvioSiconDAO scoLogEnvioSiconDAO = this.getLogEnvioSiconDAO();
		scoLogEnvioSiconDAO.persistir(logEnvioSicon);
		scoLogEnvioSiconDAO.flush();

		return logEnvioSicon;
	}

	private ScoLogEnvioSicon atualizarLogEnvioAditivo(
			ScoAditContrato aditContrato, SiconResponseVO siconResponseVO) {

		ScoLogEnvioSicon logEnvioSicon = new ScoLogEnvioSicon();

		logEnvioSicon.setAditivo(aditContrato);
		logEnvioSicon.setDtEnvio(new Date());
		logEnvioSicon.setIndVlrAf(DominioSimNao.N);

		if (siconResponseVO.getStatusEnvio().equals(DominioStatusEnvio.ERRO)) {
			logEnvioSicon.setIndSucesso(DominioSimNao.N);
			logEnvioSicon.setDsErro(siconResponseVO.getDescricaoErro());
		} else {
			logEnvioSicon.setIndSucesso(DominioSimNao.S);
		}

		ScoLogEnvioSiconDAO scoLogEnvioSiconDAO = this.getLogEnvioSiconDAO();
		scoLogEnvioSiconDAO.persistir(logEnvioSicon);
		scoLogEnvioSiconDAO.flush();

		return logEnvioSicon;
	}

	private void atualizarRescisaoContrato(ScoResContrato rescisaoContrato,
			SiconResponseVO siconResponseVO) {
		if (siconResponseVO.getStatusEnvio().equals(DominioStatusEnvio.SUCESSO)) {
			rescisaoContrato.setIndSituacao(DominioSituacaoEnvioContrato.E);
		} else {
			rescisaoContrato.setIndSituacao(DominioSituacaoEnvioContrato.EE);
		}

		ScoResContratoDAO scoResContratoDAO = this.getScoResContratoDAO();
		scoResContratoDAO.atualizar(rescisaoContrato);
		scoResContratoDAO.flush();
	}

	private void atualizarAditivo(ScoAditContrato aditContrato,
			SiconResponseVO siconResponseVO) {
		if (siconResponseVO.getStatusEnvio().equals(DominioStatusEnvio.SUCESSO)) {
			aditContrato.setSituacao(DominioSituacaoEnvioContrato.E);
		} else {
			aditContrato.setSituacao(DominioSituacaoEnvioContrato.EE);
		}

		ScoAditContratoDAO acoAditContratoDAO = this.getScoAditContratoDAO();
		acoAditContratoDAO.atualizar(aditContrato);
		acoAditContratoDAO.flush();
	}

	public void atualizarContratoEnviado(int contSeq,
			SiconResponseVO siconResponseVO, DadosEnvioVO dadosEnvioVO,
			Boolean usaVlrAfs) throws ApplicationBusinessException {

		ScoContrato cto = getScoContratoDAO().obterPorChavePrimaria(contSeq);

		if (cto == null) {
			throw new ApplicationBusinessException(
					EnvioContratoSiasgSiconONExceptionCode.CONT_INEXISTENTE);
		}

		// Atualizando Dados do Contrato
		if(LOG.isDebugEnabled()){
			logDebug("------------------------------// Processo XML(4) : atualizando dados do contrato");
			logDebug(" Número contrato : " + cto.getNrContrato());
		}

		if (siconResponseVO.getStatusEnvio().equals(DominioStatusEnvio.SUCESSO)) {
			cto.setSituacao(DominioSituacaoEnvioContrato.E);
			cto.setCodInternoUasg(dadosEnvioVO.getCnet().getCodInternoUasg()
					.getValue());

		} else {
			if (siconResponseVO.getStatusEnvio().equals(
					DominioStatusEnvio.PENDENTE)) {
				cto.setSituacao(DominioSituacaoEnvioContrato.AR);
				cto.setCodInternoUasg(dadosEnvioVO.getCnet()
						.getCodInternoUasg().getValue());

			} else {
				if (siconResponseVO.getStatusEnvio().equals(
						DominioStatusEnvio.ERRO)) {
					cto.setSituacao(DominioSituacaoEnvioContrato.EE);
				}
			}
		}

		if (usaVlrAfs) {
			cto.setValorTotal(cto.getValEfetAfs());
		}

		ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
		scoContratoDAO.atualizar(cto);
		scoContratoDAO.flush();

		if(LOG.isDebugEnabled()){
			logDebug("Processo XML(5) --- Processo XML Contrato - Finalizado --------- ");
		}
	}

	/**
	 * Converte uma String ISO-8859-1 para UTF-8
	 * 
	 * @param _arg
	 * 			String na formatação ISO-8859-1
	 * @return
	 * 			String na formatação UTF-8
	 */
	public String converteStringIsoUtf8 (String _arg) {
		
		String result = null;
		try {
			result = new String (_arg.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logError(e.getMessage());
		}
		
		return result;
	}
	
	// Getters and Setters
	protected IParametroFacade getParametroFacade() {
		//return IparametroFacade;
		return this.parametroFacade;
	}


	protected ICadastrosBasicosSiconFacade getCadastrosBasicosSiconFacade() {
		return cadastrosBasicosSiconFacade;
	}

	protected ScoLogEnvioSiconDAO getLogEnvioSiconDAO() {
		return scoLogEnvioSiconDAO;
	}

	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}

	protected ScoResContratoDAO getScoResContratoDAO() {
		return scoResContratoDAO;
	}

	protected ScoAditContratoDAO getScoAditContratoDAO() {
		return scoAditContratoDAO;
	}

}
