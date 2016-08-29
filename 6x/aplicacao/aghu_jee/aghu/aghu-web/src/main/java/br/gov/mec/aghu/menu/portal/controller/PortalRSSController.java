package br.gov.mec.aghu.menu.portal.controller;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.menu.portal.rss.Channel;
import br.gov.mec.aghu.casca.menu.portal.rss.Item;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghParametros;


public class PortalRSSController extends ActionController {

	
	private static final int QUANTIDADE_NOTICIAS_PADRAO = 3;

	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 1451619513907861671L;
	private static final Logger log = LoggerFactory.getLogger(PortalRSSController.class);

	private static final int TIMEOUT = 3 * 1000;

	private static final String CHAVE_CACHE = "portalRssNews";
	
	
	private Channel canalPortal;
	
	@Inject
	private PortalRSSCacheManager portalRSSCache;

	
	public void inicio() throws  IOException {
		
		try {
			Channel cacheRecuperado = recuperaCache();
			if (cacheRecuperado == null) {
				recuperaNoticiasPortal();
				gravaCache();
				log.info("Recuperando notícias do portal no cache " + CHAVE_CACHE);
				return;
			} else {
				recuperaNoticiasPortalCache(cacheRecuperado);
			}
		} catch (Exception e) {
			log.error("Ocorreu um erro ao tentar renderizar as notícias do portal", e);
		}
	}

	private Channel recuperaNoticiasPortalCache(Channel cacheRecuperado) {
		return this.canalPortal = cacheRecuperado;
	}

	private Channel recuperaCache() throws ApplicationBusinessException {
		return (Channel) this.portalRSSCache.get(CHAVE_CACHE);
	}

	private void gravaCache() {
		this.portalRSSCache.put(CHAVE_CACHE, canalPortal);
	}

	private void recuperaNoticiasPortal() throws JAXBException, ApplicationBusinessException {
		String conteudoRSSPortal = recuperaXmlRSSPortal();
		canalPortal = converteConteudoPortalObjetos(conteudoRSSPortal);
		limitaNoticiasPeloParametro(canalPortal);
		log.info("Renderizando notícias do portal");
	}

	private void limitaNoticiasPeloParametro(Channel canalPortal) throws ApplicationBusinessException {
		Integer quantidadeNoticiasParametro = recuperaValorParametroQuantidadeNoticias();
		selecionaNoticias(canalPortal, quantidadeNoticiasParametro);
	}

	private void selecionaNoticias(Channel canalPortal, Integer quantidadeNoticiasParametro) {
		List<Item> itens = new ArrayList<Item>();
		Integer contador = 0;
		for (Item item : canalPortal.getItem()) {
			if (contador < quantidadeNoticiasParametro) {
				itens.add(item);
				contador++;
				continue;
			}
			break;
		}
		canalPortal.setItem(itens);
	}

	private Integer recuperaValorParametroQuantidadeNoticias() {
		AghParametros qtdeNoticiasParam = null;
		Integer quantidadeNoticias = QUANTIDADE_NOTICIAS_PADRAO;
		try {
			qtdeNoticiasParam = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PORTAL_NUMERO_DE_NOTICIAS_TELA_LOGIN);
			quantidadeNoticias = recuperaValorParametroNoticias(qtdeNoticiasParam);
		} catch (ApplicationBusinessException e) {
			log.error("Ocorreu um erro ao tentar recuperar o parâmetro " + AghuParametrosEnum.P_PORTAL_NUMERO_DE_NOTICIAS_TELA_LOGIN);
			log.info("Quantidade de notícias carregadas padrão: " + QUANTIDADE_NOTICIAS_PADRAO);
		}
		return quantidadeNoticias;
	}

	private Integer recuperaValorParametroNoticias(AghParametros qtdeNoticiasParam) {
		Integer quantidadeNoticias = qtdeNoticiasParam.getVlrNumerico() == null ? 0 : qtdeNoticiasParam.getVlrNumerico().intValue();
		if (quantidadeNoticias == 0) {
			quantidadeNoticias = Integer.valueOf(qtdeNoticiasParam.getVlrNumericoPadrao().intValue());
		}
		return quantidadeNoticias;
	}

	private Channel converteConteudoPortalObjetos(String conteudoRSSPortal) throws JAXBException {
		log.info("Convertendo RSS em Objetos");
		JAXBContext jaxbContext = JAXBContext.newInstance(Channel.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Channel noticiasPortal = (Channel) jaxbUnmarshaller.unmarshal(new StringReader(conteudoRSSPortal));
		log.info("RSS Convertido");
		return noticiasPortal;
	}

	private String recuperaXmlRSSPortal() {
		HttpGet request = new HttpGet(criaURL());
		HttpResponse xml = null;
		HttpEntity httpEntity = null;
		try {
			CloseableHttpClient client = recuperaClienteHttp(configuraTimeOutRequisicao());
			xml = client.execute(request);
			httpEntity = xml.getEntity();
			String conteudoRSS = EntityUtils.toString(httpEntity);
			return removeCabecalhoXML(conteudoRSS);
		} catch (Exception e) {
			log.error("Erro ao tentar recuperar o xml do RSS do portal", e);
		}
		return StringUtils.EMPTY;
	}

	private CloseableHttpClient recuperaClienteHttp(RequestConfig config) {
		CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		return client;
	}

	private RequestConfig configuraTimeOutRequisicao() {
		return RequestConfig.custom().setConnectTimeout(TIMEOUT).setConnectionRequestTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build();
	}
	
	private String removeCabecalhoXML(String conteudoRSS) {
		return conteudoRSS.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", StringUtils.EMPTY)
				.replace("<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\"> ", StringUtils.EMPTY)
				.replace("</rss>", StringUtils.EMPTY).replace("dc:", StringUtils.EMPTY);
	}

	private URI criaURL() {
		log.info("Criando url para acesso ao RSS do portal");
    	URIBuilder urlBuilder = new URIBuilder();
    	urlBuilder.setScheme("http")
	    	.setHost("www.ebserh.gov.br")
	    	.setPath("/web/aghu/informes/-/blogs/rss")
	    	.setParameter("_33_displayStyle", "abstract")
	    	.setParameter("_33_type", "rss")
	    	.setParameter("_33_version", "2.0");
    	log.info("URL Criada: " + urlBuilder.toString());
    	
    	try {
			return urlBuilder.build();
		} catch (URISyntaxException e) {
			log.error("Erro ao tentar criar a URL do RSS do Portal", e);
			return null;
		}
		
	}

	public Channel getCanalPortal() {
		return canalPortal;
	}

	public void setCanalPortal(Channel canalPortal) {
		this.canalPortal = canalPortal;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	
}
