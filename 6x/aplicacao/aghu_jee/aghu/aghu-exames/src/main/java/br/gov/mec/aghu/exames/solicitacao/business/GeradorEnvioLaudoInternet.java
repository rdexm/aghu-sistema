package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.File;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.exames.solicitacao.integracao.business.ExameIntegracaoInternet;
import br.gov.mec.aghu.exames.solicitacao.integracao.business.IExameIntegracaoInternet;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosRetornoExameInternetVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemLaudoExameVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.criptografia.CriptografiaUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/laudoExamesQueue") })
public class GeradorEnvioLaudoInternet extends BaseBusiness implements MessageListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3487412895754393L;
	
	private static final String ERRO_GENERICO_ZIP = "Erro genérico ao gerar o arquivo ZIP";
	private static final String ERRO_URL_NAO_DEFINIDA = "Erro URL do Webservice não definida";
	private static final String ERRO_SENHA_PORTAL = "Erro Senha Portal não definida";
	
	private static final Log LOG = LogFactory
			.getLog(GeradorEnvioLaudoInternet.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private IParametroFacade parametroFacade;
	
	@Override
	public void onMessage(Message message) {
		
		MensagemLaudoExameVO mensagem = null;
		
		try {
			mensagem = (MensagemLaudoExameVO) ((ObjectMessage) message)
					.getObject();
			LOG.info("Mensagem capturada com sucesso da fila laudoExamesQueue");

			File arquivoZip = this.getExameIntegracaoInternet()
					.gerarArquivoCompactado(mensagem.getArquivoLaudo(),
							mensagem.getXmlEnvio());
			
			if(arquivoZip == null){
				LOG.error("Erro ao gerar o arquivo zip");
				this.solicitacaoExameFacade.atualizarStatusInternet(
						mensagem.getSeqSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(),
						DominioStatusExameInternet.FE,
						DominioSituacaoExameInternet.E, null,
						ERRO_GENERICO_ZIP);
				return;
			}
						
			AghParametros urlParametro = this.parametroFacade
					.buscarAghParametro(
							AghuParametrosEnum.P_URL_INTEGRACAO_PORTAL_LAUDO);
			if(urlParametro == null || StringUtils.isEmpty(urlParametro.getVlrTexto())){
				LOG.error("Erro ao comunicar com o webservice do Portal");
				this.solicitacaoExameFacade.atualizarStatusInternet(
						mensagem.getSeqSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(),
						DominioStatusExameInternet.FE,
						DominioSituacaoExameInternet.E, null,
						ERRO_URL_NAO_DEFINIDA);
				return;
			}
			
			AghParametros senhaParametro = this.parametroFacade
					.buscarAghParametro(
							AghuParametrosEnum.P_AGHU_SENHA_PORTAL_EXAMES_INTERNET);
			if(senhaParametro == null || StringUtils.isEmpty(senhaParametro.getVlrTexto())){
				LOG.error("Erro ao obter parametro da senha do portal de laudos da internet");
				this.solicitacaoExameFacade.atualizarStatusInternet(
						mensagem.getSeqSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(),
						DominioStatusExameInternet.FE,
						DominioSituacaoExameInternet.E, null,
						ERRO_SENHA_PORTAL);
				return;
			}
			
			DadosRetornoExameInternetVO retornoVO = this.getExameIntegracaoInternet().enviarLaudoPortal(
								mensagem.getSeqSolicitacaoExame(),
								mensagem.getSeqExameInternetGrupo(),
								mensagem.getLocalizador(), arquivoZip, urlParametro.getVlrTexto(),
								CriptografiaUtil.descriptografar(senhaParametro.getVlrTexto()));
				
			if (retornoVO.getDescricaoErro() != null
					&& !retornoVO.getDescricaoErro().isEmpty()) {
				this.solicitacaoExameFacade.atualizarStatusInternet(
						mensagem.getSeqSolicitacaoExame(),
						mensagem.getSeqExameInternetGrupo(),
						DominioStatusExameInternet.FE,
						DominioSituacaoExameInternet.E, null,
						this.montarDescricaoErro(retornoVO.getDescricaoErro()));
				return;
			}				
			
			this.solicitacaoExameFacade.atualizarStatusInternet(
					mensagem.getSeqSolicitacaoExame(),
					mensagem.getSeqExameInternetGrupo(),
					DominioStatusExameInternet.FE,
					DominioSituacaoExameInternet.R, DominioStatusExameInternet.EC, null);
			
			this.flush();			
		} catch (JMSException e) {
			LOG.error("Erro ao obter mensagem: ", e);
			throw new IllegalArgumentException(e);
		} catch (ApplicationBusinessException e) {			
			LOG.error("Erro ao obter mensagem: ", e);
			this.solicitacaoExameFacade.atualizarStatusInternet(
					mensagem.getSeqSolicitacaoExame(),
					mensagem.getSeqExameInternetGrupo(),
					DominioStatusExameInternet.FE,
					DominioSituacaoExameInternet.E, null,
					e.getMessage() + " causa " + e.getCause());
		}

	}

	/**
	 * Retornar a descrição do erro loocalizado no XML
	 * @param listaErro
	 * @return
	 */
	private String montarDescricaoErro(List<String> listaErro){
		
		StringBuilder descricaoErro = new StringBuilder();
		
		for(String erro : listaErro){
			descricaoErro.append(erro);
			if(descricaoErro.length() > 4000){
				break;
			}
		}
		
		return descricaoErro.length() <= 4000 ? descricaoErro.toString() : descricaoErro.substring(0, 4000);		
	}
	
	protected IExameIntegracaoInternet getExameIntegracaoInternet() {
		return new ExameIntegracaoInternet();
	}

}
