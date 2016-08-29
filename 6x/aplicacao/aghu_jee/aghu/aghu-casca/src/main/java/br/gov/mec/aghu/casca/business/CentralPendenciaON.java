package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.CentralPendenciasInterface.TipoAcaoEnum;
import br.gov.mec.aghu.casca.business.CentralPendenciasInterface.TipoPendenciaEnum;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.vo.PendenciaVO;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioFormaIdentificacaoCaixaPostal;
import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.dominio.DominioTipoMensagemExame;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostalServidorId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.mail.ContatoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;

@Stateless
public class CentralPendenciaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CentralPendenciaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8468341944052245571L;
	
	protected static final String URL_PENDENCIAS_CERTIFICACAO_DIGITAL = 
		"/certificacaodigital/listarPendenciasAssinatura.xhtml?centralPendencia=true";	
	
	protected static final String URL_RESULT_EXAMES = "/exames/consultarResultadoNotaAdicional.seam";
	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.casca.business.ICentralPendenciaFacade#getListaPendencias()
	 */
	public List<PendenciaVO> getListaPendencias() throws ApplicationBusinessException {

		List<PendenciaVO> result = null;
		PendenciaVO vo = null;

		result = new LinkedList<PendenciaVO>();
		vo = this.getPendenciaCertificacaoDigital();
		if (vo != null) {
			result.add(vo);			
		}

		result.addAll(this.getListaPendenciasCaixaPostal());

		return result;
	}


	protected TipoPendenciaEnum getTipoPendenciaCertificacaoDigital(boolean excedeLim, int qtdPend) {		
		TipoPendenciaEnum result = null;

		result = TipoPendenciaEnum.IRRELEVANTE;
		if (excedeLim) {
			result = TipoPendenciaEnum.INTRUSIVA;
		} else {
			if (qtdPend > 0) {
				result = TipoPendenciaEnum.LEMBRETE;
			}
		}		
		return result;
	}


	


	protected PendenciaVO getPendenciaCertificacaoDigital() {
		PendenciaVO result = null;
		TipoPendenciaEnum tipo = null;
		String url = null;
		String mensagem = null;
		boolean excedeLim = false;
		Long qtdPend = 0l;
		int qtdDias = 0;

		if (this.getCertificacaoDigitalFacade().verificarCertificacaoDigitalHabilitado()) {
			// coletando dados
			excedeLim = this.getCertificacaoDigitalFacade().verificarNecessidadeResolverPendencias();
			qtdPend = this.getCertificacaoDigitalFacade().obterQuantidadeCertificadosPendentes();

			if (qtdPend > 0) {
				qtdDias = this.getCertificacaoDigitalFacade().obterDiasPendenciaMaisAntiga();
				// tipo
				tipo = this.getTipoPendenciaCertificacaoDigital(excedeLim, qtdPend.intValue());

				url = URL_PENDENCIAS_CERTIFICACAO_DIGITAL;
				
				// mensagem
				mensagem = "Atenção existem " + qtdPend + " documentos pendentes de assinatura digital, " +
				"sendo o mais antigo esperando a " + qtdDias + " dias."; 
				// vo
				result = new PendenciaVO(TipoAcaoEnum.LINK, tipo, url, mensagem, "Assinatura Digital", "Resolver");
				result.setDataInicio(new Date());
			}	
		}

		return result;
	}


	/**
	 * Lista as Pendências de AghCaixaPostal de Nota Adicional de Exames. #5847.
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	private List<PendenciaVO> getListaPendenciasCaixaPostal() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		List<AghCaixaPostal> listCaixaPostal = null;
		List<PendenciaVO> result = new LinkedList<PendenciaVO>();

		String acaoBotaoExcluir = "#{caixaPostarServidorController.excluirMensagem}";//Ação utilizada para excluir as pendências
		
		if(servidorLogado!=null){
			listCaixaPostal = this.getAghuFacade().pesquisarMensagemPendenciasCaixaPostal(servidorLogado);

			if(listCaixaPostal!=null && !listCaixaPostal.isEmpty()){
				
				AghCaixaPostalServidorId  caixaPostalServidorId = null;
				
				for (AghCaixaPostal caixaPostal : listCaixaPostal) {
					
					PendenciaVO pendencia = null;
					caixaPostalServidorId = new AghCaixaPostalServidorId(caixaPostal.getSeq(), servidorLogado);
					
					if(caixaPostal.getTipoMensagem().equals(DominioTipoMensagemExame.A)){
						
						if(caixaPostal.getUrlAcao() == null || !caixaPostal.getUrlAcao().contains("/")  ){
							//TODO: Teste temporário para continuar funcionando os links do módulo de exames (laudo e recoleta)
							if(caixaPostal.getMensagem().contains("laudo") || caixaPostal.getMensagem().contains("recoleta")){
								pendencia = new PendenciaVO(TipoAcaoEnum.LINK_E_EXCLUSAO, TipoPendenciaEnum.LEMBRETE, URL_RESULT_EXAMES +"?cxtSeq="+caixaPostal.getSeq(), 
										caixaPostal.getMensagem(),"Resultado de Exames", null);
							}
							else{
								pendencia = new PendenciaVO(TipoAcaoEnum.SEM_ACAO, TipoPendenciaEnum.LEMBRETE, null ,caixaPostal.getMensagem(), null, null);
							}
						}else if (caixaPostal.getMensagem().contains("Consultoria internação")) {
							pendencia = new PendenciaVO(TipoAcaoEnum.LINK, TipoPendenciaEnum.LEMBRETE, caixaPostal.getMensagem(), 
									caixaPostal.getTituloAbaAcao(), null, adicionaSeqCaixaPosta(caixaPostal.getUrlAcao(), caixaPostal), null, caixaPostalServidorId);
						}else{
							pendencia = new PendenciaVO(TipoAcaoEnum.LINK_E_EXCLUSAO, TipoPendenciaEnum.LEMBRETE, caixaPostal.getMensagem(), 
									caixaPostal.getTituloAbaAcao(), acaoBotaoExcluir, adicionaSeqCaixaPosta(caixaPostal.getUrlAcao(), caixaPostal), null, caixaPostalServidorId);
						}
					}
					else if (caixaPostal.getTipoMensagem().equals(DominioTipoMensagemExame.I)){
						pendencia = new PendenciaVO(TipoAcaoEnum.INFORMACAO, TipoPendenciaEnum.LEMBRETE, caixaPostal.getMensagem(), null, acaoBotaoExcluir, null, null, caixaPostalServidorId);
					}				
					
					if(pendencia != null){
						pendencia.setSeqCaixaPostal(caixaPostal.getSeq());
						pendencia.setDataInicio(caixaPostal.getDthrInicio());
						result.add(pendencia);
					}
				}
			}
		}

		return result;
	}
	
	private String adicionaSeqCaixaPosta(String url, AghCaixaPostal caixaPostal){
		if(url.contains("?")){
			url += "&cxtSeq=" + caixaPostal.getSeq();
		}else{
			url += "?cxtSeq=" + caixaPostal.getSeq();
		}
		return url;
	}
	
	
	public void excluirPendencia(Long seqCaixaPostal) throws ApplicationBusinessException{ 
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghCaixaPostalServidorId id = new AghCaixaPostalServidorId();
		id.setCxtSeq(seqCaixaPostal);
		id.setServidor(servidorLogado);
		this.getAghuFacade().excluirMensagemCxPostServidor(id);
	}
	
	public void excluirPendenciaComUsuarioSelecionado(Long seqCaixaPostal, RapServidores usuarioSelecionado) { 
		AghCaixaPostalServidorId id = new AghCaixaPostalServidorId();
		id.setCxtSeq(seqCaixaPostal);
		id.setServidor(usuarioSelecionado);
		this.getAghuFacade().excluirMensagemCxPostServidor(id);
	}
	
	public void adicionarPendenciaAcao(String mensagem, String url,
			String descricaoAba, List<RapServidores> listaServidores,
			Boolean enviarEmail) throws ApplicationBusinessException {
		
		AghCaixaPostal caixaPostal = new AghCaixaPostal();
		caixaPostal.setMensagem(mensagem);
		caixaPostal.setUrlAcao(url);
		caixaPostal.setTituloAbaAcao(descricaoAba);
		caixaPostal.setTipoMensagem(DominioTipoMensagemExame.A);
		
		this.adicionarPendenciaParaServidores(caixaPostal, listaServidores, enviarEmail);
	}

	public void adicionarPendenciaAcao(String mensagem, String url, String descricaoAba, RapServidores servidor, Boolean enviarEmail)
			throws ApplicationBusinessException {

		List<RapServidores> listaServidores = new ArrayList<RapServidores>();
		listaServidores.add(servidor);
		this.adicionarPendenciaAcao(mensagem, url, descricaoAba, listaServidores, enviarEmail);
	}
	
	public void adicionarPendenciaInformacao(String mensagem, List<RapServidores> listaServidores,
			Boolean enviarEmail) throws ApplicationBusinessException {
		
		AghCaixaPostal caixaPostal = new AghCaixaPostal();
		caixaPostal.setMensagem(mensagem);
		caixaPostal.setUrlAcao(null);
		caixaPostal.setTituloAbaAcao(null);
		caixaPostal.setTipoMensagem(DominioTipoMensagemExame.I);
		
		this.adicionarPendenciaParaServidores(caixaPostal, listaServidores, enviarEmail);
	}

	
	public void adicionarPendenciaParaServidores(AghCaixaPostal caixaPostal,
			List<RapServidores> listaServidores, Boolean enviarEmail) throws ApplicationBusinessException  {
				
		//TODO: Campos sem utilização, mas que são obrigatórios por enquanto
		if(caixaPostal.getAcaoObrigatoria() == null){
			caixaPostal.setAcaoObrigatoria(true);
		}
		if(caixaPostal.getFormaIdentificacao() == null){
			caixaPostal.setFormaIdentificacao(DominioFormaIdentificacaoCaixaPostal.E);
		}
		
		
		if(caixaPostal.getSeq() == null){
			caixaPostal.setCriadoEm(new Date());
			if(caixaPostal.getDthrInicio() == null){
				caixaPostal.setDthrInicio(new Date());
			}
			this.getAghuFacade().persistirAghCaixaPostal(caixaPostal);
		}
		else{
			this.getAghuFacade().atualizarAghCaixaPostal(caixaPostal);
		}
		
		//Lista dos servidores que devem receber o email com a pendência
		List<ContatoEmail> listaDestinatarios = new ArrayList<ContatoEmail>(listaServidores.size());
		String nome = null;
		String email = null;
		
		for(RapServidores servidor : listaServidores){
			
			if(servidor == null){
				continue;
			}
			
			nome = servidor.getUsuario();
			email =  this.buscarEmailServidor(servidor);
			
			if(email != null){
				//Armazena os dados para enviar o email quando existir um email
				listaDestinatarios.add(new ContatoEmail(nome, email));
				logDebug("Incluindo email para envio: "+nome+" = "+email);
			}
			else{
				logInfo("Email não cadastrado para o usuário "+nome);
			}
			
			//Armazena a pendência para o servidor
			AghCaixaPostalServidor caixaPostalServidor = new AghCaixaPostalServidor();
			caixaPostalServidor.setId(new AghCaixaPostalServidorId(caixaPostal.getSeq(), servidor));
			caixaPostalServidor.setSituacao(DominioSituacaoCxtPostalServidor.N);
			this.getAghuFacade().persistirAghCaixaPostalServidor(caixaPostalServidor);
		}
		
		//Envia o email para os destinatários
		if(enviarEmail && !listaDestinatarios.isEmpty()){
			AghParametros emailDe = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
			ContatoEmail remetente = new ContatoEmail(emailDe.getVlrTexto().toLowerCase());
			String assunto = this.buscarMensagem("ASSUNTO_EMAIL_CAIXA_POSTAL");
			getEmailUtil().enviaEmail(remetente, listaDestinatarios, null, assunto, caixaPostal.getMensagem());
			logDebug("Enviando "+listaDestinatarios.size()+" email(s) para informar a pendência!");
		}
		else{
			logInfo("A lista está vazia, não foi possível enviar o email para informar a pendência");
		}
	}
	
	public String buscarEmailServidor(RapServidores servidor) throws ApplicationBusinessException{
		
		Usuario usuario = null;
		usuario = this.getCascaFacade().obterUsuarioAtivo(servidor.getUsuario());
		
		
		if(usuario != null){
			return usuario.getEmail();
		}
		else{
			return servidor.getEmail();
		}
	}

	public Integer buscaTempoRefreshPendencias(RapServidores servidor) {
		Integer retorno = null;
		try {
			Usuario usuario = null;
			usuario = this.getCascaFacade().obterUsuarioAtivo(servidor.getUsuario());
			
			
			if (usuario != null) {
				retorno = usuario.getAtualizacaoPendenciaMinutos();
				
				if (retorno == null) {
					retorno = this.getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_TEMPO_ATUALIZACAO_PENDENCIAS_MINUTOS);
				}
			}
		} catch (Exception e) {
			logError("Ocorreu um erro ao buscar o tempo de refresh padrão. Valor retornado: 30 minutos.");
		}
		
		if (retorno == null) {
			// tempo padrão: 30 minutos
			retorno = 30;
		}
		
		// transforma o retorno de minutos para milisegundos
		return retorno * 60 * 1000;
	}

	
	private String buscarMensagem(String chave, Object... parametros) {
		String mensagem = getResourceBundleValue(chave);
		mensagem = java.text.MessageFormat.format(mensagem, parametros);
		return mensagem;
	}
	
	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade(){
		return this.certificacaoDigitalFacade;
	}
	
	protected EmailUtil getEmailUtil(){
		return this.emailUtil;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade(){
		return this.aghuFacade;
	}
	
	protected ICascaFacade getCascaFacade(){
		return this.cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
