package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import br.com.redeimagem.ObterUrlDocumentosComFiltroExclusivoResponse.ObterUrlDocumentosComFiltroExclusivoResult;
import br.com.redeimagem.WsLiquidWeb;
import br.com.redeimagem.WsLiquidWebSoap;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml.ObterUrlDocumentosComFiltroExclusivo;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml.ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento;
import br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml.ObterUrlDocumentosComFiltroExclusivo.Documentos.Documento.Campos.Campo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class GEDServiceON extends BaseBusiness implements IGEDService{

	private static final long serialVersionUID = 18569461345785685L;

	private static final Log LOG = LogFactory.getLog(GEDServiceON.class);

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public	List<DocumentoGEDVO> consultarDocumentosGED(String camposFicha, String usuarioGed, String senhaGed, Integer ficha) {
		
		try {
			List<DocumentoGEDVO> retorno = new ArrayList<>();
			
			WsLiquidWeb service = getGEDService();
			WsLiquidWebSoap port = service.getWsLiquidWebSoap();
			ObterUrlDocumentosComFiltroExclusivoResult result = port.obterUrlDocumentosComFiltroExclusivo(usuarioGed, senhaGed, ficha, camposFicha);

			for (Object node : result.getContent()) {

				JAXBContext context = JAXBContext.newInstance("br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.xml");

				Unmarshaller unmarshaller = context.createUnmarshaller();
				@SuppressWarnings("unchecked")
				JAXBElement<ObterUrlDocumentosComFiltroExclusivo> element = (JAXBElement<ObterUrlDocumentosComFiltroExclusivo>) unmarshaller.unmarshal((Node) node);
				ObterUrlDocumentosComFiltroExclusivo obterUrlsDocumentos = element.getValue();
				for (Documento doc : obterUrlsDocumentos.getDocumentos().getDocumento()) {
					Map<String, Object> campos = new HashMap<String,Object>();
					for (Campo campoFicha : doc.getCampos().getCampo()) {
						campos.put(campoFicha.getNome(), campoFicha.getValor());
					}
					DocumentoGEDVO docVo = new DocumentoGEDVO(obterUrlsDocumentos.getFicha().intValue(), campos, doc.getUrlVisualizar());
					retorno.add(docVo);
				}
			}			
			return retorno;
		} catch (Exception e) {
			LOG.error(e.getClass().getName(), e);
			return new ArrayList<DocumentoGEDVO>();
		}
	}

	private WsLiquidWeb getGEDService() throws ApplicationBusinessException, MalformedURLException {
		String urlWs = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_URL_WS_SISTEMA_GED);
		URL baseUrl = br.com.redeimagem.WsLiquidWeb.class.getResource(".");
		URL wsdlLocation = new URL(baseUrl, urlWs);
		return new WsLiquidWeb(wsdlLocation,new QName("http://redeimagem.com.br/", "WsLiquidWeb"));
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
