package br.gov.mec.aghu.mensageiro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.configuracao.vo.NotificacaoDestinoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.vo.PacientesInternadosUltrapassadoVO;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghNotificacoes;

@Stateless
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
public class InternacoesExcedentesON extends NotificacoesBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 632657839736135206L;
	
	private static final Log LOG = LogFactory.getLog(InternacoesExcedentesON.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Override
	protected Boolean enviarMensagemWhatsapp(AghNotificacoes notificacao, AghJobDetail job) throws ApplicationBusinessException {
		
		List<NotificacaoDestinoVO> destinos = getAdministracaoFacade().obtemDestinosDaNotificacao(notificacao.getSeq());
		if(destinos.isEmpty()) {
			getSchedulerFacade().adicionarLog(job, "Nenhum celular cadastrado na notificação para envio da mensagem");
			return false;
		}
		
		String cabecalho = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CABECALHO_WHATSAPP_INTERNACOES_EXCEDENTES).getVlrTexto();
		String mensagem = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_MENSAGEM_WHATSAPP_INTERNACOES_EXCEDENTES).getVlrTexto();
		String whatsUrl = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_URL_WHATSAPP).getVlrTexto();
		
		cabecalho = cabecalho.replace(" ", "%20");
		String mensagemFormatada = obtemMensagemFormatada(mensagem);
		for(NotificacaoDestinoVO destino : destinos) {
			try {
				URL url = obtemUrl(whatsUrl, cabecalho.concat(mensagemFormatada), obtemCelularFormatado(destino));
				URLConnection connection = url.openConnection();
				
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if(inputLine.contains("Mensagem enviada com sucesso!")) {
						getSchedulerFacade().adicionarLog(job, "Whatsapp para " + obtemCelularFormatado(destino) + "enviado com sucesso!");
						break;
					}
				}
		        in.close();
		        
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				getSchedulerFacade().adicionarLog(job, e.getMessage());
			}
		}
		return true;
	}
	
	
	private String obtemCelularFormatado(NotificacaoDestinoVO destino) {
		return destino.getDddCelular().toString().concat(destino.getCelular().toString());
	}


	private URL obtemUrl(String whatsUrl, String mensagem, String celular) throws MalformedURLException {
		whatsUrl = whatsUrl.replace("{0}", celular);
		whatsUrl = whatsUrl.replace("{1}", mensagem);
		return new URL(whatsUrl);
	}
	
	private String obtemMensagemFormatada(String mensagem) {
		List<PacientesInternadosUltrapassadoVO> listaUnidadesQtdes = getInternacaoFacade().obterUnidadesQtdPacientesInternadosExcedentes();
		String mensagemFormatada = "";
		for(PacientesInternadosUltrapassadoVO item : listaUnidadesQtdes) {
			mensagemFormatada = mensagemFormatada.concat(mensagem);
			mensagemFormatada = mensagemFormatada.replace("{0}", item.getDescricaoUnidade());
			mensagemFormatada = mensagemFormatada.replace("{1}", item.getNroPacientes().toString());
			mensagemFormatada = mensagemFormatada.replace(" ", "%20");
		}
		
		return mensagemFormatada; 
	}


	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

}
