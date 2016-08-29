package br.gov.mec.aghu.casca.business;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.dao.PerfilDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosJnDAO;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.PerfisUsuariosJn;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.casca.vo.FiltroPerfisUsuariosJnVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PerfilUsuarioON extends BaseBusiness {

	private static final long serialVersionUID = -992775421865071269L;
	
	
	private static final Log LOG = LogFactory.getLog(PerfilUsuarioON.class);

	@Inject
	private PerfisUsuariosJnDAO perfisUsuariosJnDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private PerfilDAO perfilDAO;
	
	@Inject
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradoFacade;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * Quantidade de dias que o perfil de acesso ao sistema com pendências
	 * bloqueantes estará vinculado ao usuário. Se passar da quantidade de dias,
	 * o perfil será desvinculado do usuário.
	 */
	private static final Integer FAIXA_DIAS_NAO_EXCLUIR_PERFIL = 3;
	
	protected enum PerfilUsuarioONExceptionCode implements BusinessExceptionCode {
		USUARIO_NAO_PODE_DELEGAR_PERFIS,
		PERFIL_NAO_DELEGAVEL,
		USUARIO_JA_POSSUI_PERFIL,
		VOCE_NAO_POSSUI_EMAIL_CADASTRADO,
		CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO,
		DATA_EXPIRACAO_PASSADO,
		DATA_EXPIRACAO_ULTRAPASSOU_LIMITE,
		MOTIVO_DEVE_SER_MAIS_DETALHADO,
		;
	}
	
	public boolean usuarioLogadoLiberadoDePendenciasBloqueantes() throws ApplicationBusinessException {
		boolean liberaAcessoComPendenciasBloqueantes = false;

		liberaAcessoComPendenciasBloqueantes = this.usuarioLiberadoDePendenciasBloqueantes(getServidorLogadoFacade().obterServidorLogado().getUsuario());

		return liberaAcessoComPendenciasBloqueantes;
	}

	public boolean usuarioLiberadoDePendenciasBloqueantes(String usuario) {
		boolean usuarioLiberadoDePendenciasBloqueantes = false;

		try {
			AghParametros pAghuPerfilNaoBloqueante = this.getParametroFacade().obterAghParametro(
					AghuParametrosEnum.P_AGHU_PERFIL_NAO_BLOQUEANTE);

			String vlrTexto = pAghuPerfilNaoBloqueante.getVlrTexto();

			if (StringUtils.isNotBlank(vlrTexto)) {
				usuarioLiberadoDePendenciasBloqueantes = this.getPerfisUsuariosDAO().existePerfilUsuario(usuario, vlrTexto);
			}
		} catch (ApplicationBusinessException e) {
			logWarn("Ocorreu uma exceção, o usuário não terá acesso liberado para acesso em caso de pendências bloqueantes.", e);
		}
		
		return usuarioLiberadoDePendenciasBloqueantes;
	}

	public void liberarAcessoParaAcessarSistemaComPendenciasBloqueantes(Usuario usuario) throws BaseException {
		PerfisUsuariosDAO perfisUsuariosDAO = this.getPerfisUsuariosDAO();
		
		try {
			AghParametros pAghuPerfilNaoBloqueante = this.getParametroFacade().obterAghParametro(
					AghuParametrosEnum.P_AGHU_PERFIL_NAO_BLOQUEANTE);

			String vlrTexto = pAghuPerfilNaoBloqueante.getVlrTexto();
			
			Perfil perfil = this.getPerfilDAO().pesquisarPerfil(vlrTexto);
			
			PerfisUsuarios perfisUsuarios = new PerfisUsuarios();
			perfisUsuarios.setUsuario(usuario);
			perfisUsuarios.setPerfil(perfil);
			perfisUsuarios.setDataCriacao(new Date());
			
			perfisUsuariosDAO.persistir(perfisUsuarios);
			perfisUsuariosDAO.flush();
			
			// cria a journal
			PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfisUsuarios, DominioOperacoesJournal.INS);
			// persiste a journal
			PerfisUsuariosJnDAO perfisUsuariosJnDAO = getPerfisUsuariosJnDAO();
			perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
			perfisUsuariosJnDAO.flush();
			
			//TODO rever limpeza de cache nesta arquitetura
			//PerfilDAOCache.limpaCache();
		} catch (ApplicationBusinessException e) {
			logWarn("Ocorreu um erro ao liberar o acesso ao sistema com pendências bloqueantes", e);
			throw e;
		}
	}
	
	public PerfisUsuarios obterPerfilUsuario(String usuario, String perfil) {
		return this.getPerfisUsuariosDAO().obterPerfilUsuario(usuario, perfil);
	}
	
	public PerfisUsuarios obterPerfilUsuarioLogin(String usuario, String perfil) {
		return this.getPerfisUsuariosDAO().obterPerfilUsuarioLogin(usuario, perfil);
	}
	

	public void removerLiberacaoAcessoSistemaComPendenciasBloqueantes(Usuario usuario) throws BaseException {
		PerfisUsuariosDAO perfisUsuariosDAO = this.getPerfisUsuariosDAO();

		try {
			AghParametros pAghuPerfilNaoBloqueante = this.getParametroFacade().obterAghParametro(
					AghuParametrosEnum.P_AGHU_PERFIL_NAO_BLOQUEANTE);

			String vlrTexto = pAghuPerfilNaoBloqueante.getVlrTexto();

			PerfisUsuarios perfisUsuarios = perfisUsuariosDAO.obterPerfilUsuario(usuario.getNome(), vlrTexto);
			
			// cria a journal
			PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfisUsuarios, DominioOperacoesJournal.DEL);
			
			perfisUsuariosDAO.remover(perfisUsuarios);
			perfisUsuariosDAO.flush();
			
			// persiste a journal
			PerfisUsuariosJnDAO perfisUsuariosJnDAO = getPerfisUsuariosJnDAO();
			perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
			perfisUsuariosJnDAO.flush();
			
			//TODO rever limpeza de cache nesta arquitetura
			//PerfilDAOCache.limpaCache();
		} catch (ApplicationBusinessException e) {
			logWarn("Ocorreu um erro ao remover a liberação do acesso ao sistema com pendências bloqueantes", e);
			throw e;
		}
	}

	public void removerPerfisNaoBloqueantes() {
		try {
			AghParametros pAghuPerfilNaoBloqueante = this.getParametroFacade().obterAghParametro(
					AghuParametrosEnum.P_AGHU_PERFIL_NAO_BLOQUEANTE);

			String vlrTexto = pAghuPerfilNaoBloqueante.getVlrTexto();

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, -FAIXA_DIAS_NAO_EXCLUIR_PERFIL);

			PerfisUsuariosDAO perfisUsuariosDAO = this.getPerfisUsuariosDAO();

			List<PerfisUsuarios> lista = perfisUsuariosDAO.listarPerfisUsuarios(vlrTexto,
					DateUtil.obterDataComHoraFinal(cal.getTime()));

			if (lista != null && !lista.isEmpty()) {
				for (PerfisUsuarios perfilUsuario : lista) {
					// cria a journal
					PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfilUsuario, DominioOperacoesJournal.DEL);
					
					perfisUsuariosDAO.remover(perfilUsuario);
					
					// persiste a journal
					PerfisUsuariosJnDAO perfisUsuariosJnDAO = getPerfisUsuariosJnDAO();
					perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
				}
				perfisUsuariosDAO.flush();
			}
			
			//TODO rever limpeza de cache nesta arquitetura
			//PerfilDAOCache.limpaCache();
		} catch (ApplicationBusinessException e) {
			logWarn("Ocorreu um erro ao remover as liberações do acesso ao sistema com pendências bloqueantes", e);
		}
	}
	
	/**
	 * Método que faz algumas validações de delegação de perfil
	 * @param usuario
	 * @param perfil
	 * @param usuarioPerfil
	 * @param dataExpiracao
	 * @param motivoDelegacao
	 * @param limiteDias
	 * @param lengthMinMotivo
	 * @throws ApplicationBusinessException
	 */
	private void validaDelegacaoPerfil(Usuario usuario, Perfil perfil, Usuario usuarioPerfil, Date dataExpiracao, String motivoDelegacao, Integer limiteDias, Integer lengthMinMotivo) throws ApplicationBusinessException {
		
		if(limiteDias == null || lengthMinMotivo == null){
			throw new InvalidParameterException("parâmetros não podem ser nulos");
		} else if(!usuario.isDelegarPerfil()){	
			// VALIDAÇÂO DE USUÁRIO		
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.USUARIO_NAO_PODE_DELEGAR_PERFIS);
		} else if(!perfil.isDelegavel()){		
			// VALIDAÇÂO DE PERFIL		
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.PERFIL_NAO_DELEGAVEL);
		}		
		// VALIDAÇÃO DO PERFIL_USUARIO
		for (PerfisUsuarios perfilUsuario : usuarioPerfil.getPerfisUsuario()) {
			if( perfilUsuario.getPerfil().getId().equals(perfil.getId()) &&
					(perfilUsuario.getDataExpiracao() == null || perfilUsuario.getDataExpiracao().after(new Date()))){
				throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.USUARIO_JA_POSSUI_PERFIL);
			}
		}
		
		Calendar limite = Calendar.getInstance();
		limite.add(Calendar.DAY_OF_YEAR, limiteDias);		
		Date dataLimite = limite.getTime();
		
		if(StringUtils.isEmpty(usuario.getEmail())){
			//VALIDAÇÃO DE EMAIL DOS USUÁRIOS ENVOLVIDOS
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.VOCE_NAO_POSSUI_EMAIL_CADASTRADO);
		} else if(StringUtils.isEmpty(usuarioPerfil.getEmail())){
			//VALIDAÇÃO DE EMAIL DOS USUÁRIOS ENVOLVIDOS
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.CASCA_MENSAGEM_EMAIL_NAO_CADASTRADO);
		} else if(dataExpiracao.before(new Date())){
			// VALIDAÇÃO DE DATAS		
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.DATA_EXPIRACAO_PASSADO);
		} else  if(dataExpiracao.after(dataLimite)){
			// VALIDAÇÃO DE DATAS
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.DATA_EXPIRACAO_ULTRAPASSOU_LIMITE);
		} else if(motivoDelegacao.length() < lengthMinMotivo){
			// VALIDAÇÃO DO MOTIVO		
			throw new ApplicationBusinessException(PerfilUsuarioONExceptionCode.MOTIVO_DEVE_SER_MAIS_DETALHADO);
		}		
	}
	
	/**
	 * Método que realiza a delegação de um perfil.
	 * @param usuario
	 * @param perfil
	 * @param usuarioPerfil
	 * @param dataExpiracao
	 * @param motivoDelegacao
	 * @throws ApplicationBusinessException
	 */
	public void delegarPerfilUsuario(Usuario usuario, Perfil perfil, Usuario usuarioPerfil, Date dataExpiracao, String motivoDelegacao) throws ApplicationBusinessException {
		
		// validações dos parâmetros
		if(usuario == null || perfil == null || usuarioPerfil == null || dataExpiracao == null || StringUtils.isEmpty(motivoDelegacao)){
			throw new InvalidParameterException("parâmetros não podem ser nulos");
		}
		
		//Cria os daos
		PerfisUsuariosDAO perfisUsuariosDAO = this.getPerfisUsuariosDAO();
		PerfisUsuariosJnDAO perfisUsuariosJnDAO = this.getPerfisUsuariosJnDAO();
		
		//Validações
		this.validaDelegacaoPerfil(usuario, perfil, usuarioPerfil, dataExpiracao, motivoDelegacao, 30, 20);
		
		//Apaga PerfilUsuario expirado
		PerfisUsuarios perfilExcluir = null;
		for (PerfisUsuarios perfilUsuario : usuarioPerfil.getPerfisUsuario()) {
			if( perfilUsuario.getPerfil().getId().equals(perfil.getId()) && 
					perfilUsuario.getDataExpiracao() != null && 
					perfilUsuario.getDataExpiracao().before(new Date())){
				
				// cria a journal da exclusão
				PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfilUsuario, DominioOperacoesJournal.DEL);
				perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
				perfilExcluir = perfilUsuario;
				break;
			}
		}
		if(perfilExcluir != null){			
			usuarioPerfil.getPerfisUsuario().remove(perfilExcluir);
			perfisUsuariosDAO.remover(perfilExcluir);
			perfisUsuariosDAO.flush();
		}
		
		//Cria a nova associação
		PerfisUsuarios perfisUsuarios = new PerfisUsuarios();
		perfisUsuarios.setUsuario(usuarioPerfil);
		perfisUsuarios.setPerfil(perfil);
		perfisUsuarios.setDataCriacao(new Date());
		perfisUsuarios.setDataExpiracao(dataExpiracao);
		perfisUsuarios.setMotivoDelegacao(motivoDelegacao);
		perfisUsuariosDAO.persistir(perfisUsuarios);
		perfisUsuariosDAO.flush();
		
		//TODO rever limpeza de cache nesta arquitetura
		//PerfilDAOCache.limpaCache();
		
		// cria a journal
		PerfisUsuariosJn perfisUsuariosJn = this.criarJournal(perfisUsuarios, DominioOperacoesJournal.INS);
		perfisUsuariosJnDAO.persistir(perfisUsuariosJn);
		perfisUsuariosJnDAO.flush();
		
		this.envialEmail(usuario, perfil, usuarioPerfil, dataExpiracao, motivoDelegacao);
		
	}
	
	/**
	 * Método que cria um objeto journal de Perfisusuarios.
	 * @param perfisUsuarios
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	private PerfisUsuariosJn criarJournal(PerfisUsuarios perfisUsuarios, DominioOperacoesJournal operacao) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		PerfisUsuariosJn perfisUsuariosJn = BaseJournalFactory.getBaseJournal(operacao, PerfisUsuariosJn.class, servidorLogado.getUsuario());
		perfisUsuariosJn.setId(perfisUsuarios.getId());
		perfisUsuariosJn.setIdUsuario(perfisUsuarios.getUsuario().getId());
		perfisUsuariosJn.setIdPerfil(perfisUsuarios.getPerfil().getId());		
		perfisUsuariosJn.setLogin(perfisUsuarios.getUsuario().getLogin());
		perfisUsuariosJn.setNomePerfil(perfisUsuarios.getPerfil().getNome());
		perfisUsuariosJn.setDataExpiracao(perfisUsuarios.getDataExpiracao());
		perfisUsuariosJn.setMotivoDelegacao(perfisUsuarios.getMotivoDelegacao());
		return perfisUsuariosJn;
	}
	
	/**
	 * Envia email para os usuários envolvidos com a delegação do perfil
	 * 
	 * @param usuario
	 * @param perfil
	 * @param usuarioPerfil
	 * @param dataExpiracao
	 * @param motivoDelegacao
	 */
	private void envialEmail(Usuario usuario, Perfil perfil, Usuario usuarioPerfil, Date dataExpiracao, String motivoDelegacao){
		
		try {
			String emailDe = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
				
			final List<String> emailPara = new ArrayList<String>();
			emailPara.add(usuario.getEmail());
			emailPara.add(usuarioPerfil.getEmail());
			
			final String assunto = getResourceBundleValue("CASCA_ASSUNTO_EMAIL_DELEGACAO_PERFIL");
			String conteudoEmail = getResourceBundleValue("CASCA_MENSAGEM_EMAIL_DELEGACAO_PERFIL");
			
			conteudoEmail = java.text.MessageFormat.format(conteudoEmail, perfil.getNome(), perfil.getDescricaoResumida(), usuarioPerfil.getNome(), usuarioPerfil.getLogin(), usuario.getNome(), usuario.getLogin(), DateUtil.obterDataFormatadaHoraMinutoSegundo(dataExpiracao), motivoDelegacao);
	
			// Envia email.
			this.getEmailUtil().enviaEmail(emailDe, emailPara, null, assunto, conteudoEmail);
		} catch (ApplicationBusinessException e) {
			logError("Email não enviado", e);
		}
	}
	
	/**
	 * Busca o histórico do cadastro do perfisUsuarios
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtroPerfisUsuariosJnVO
	 * @return
	 */
	public List<PerfisUsuariosJn> pesquisarHistoricoPorPerfisUsuarios(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {
		if(orderProperty == null){
			orderProperty = PerfisUsuariosJn.Fields.SEQ_JN.toString();
			asc = false;
		}
		return getPerfisUsuariosJnDAO().pesquisarPorPerfisUsuarios(firstResult, maxResult,
				orderProperty, asc, filtroPerfisUsuariosJnVO);
	}
	
	/**
	 * Quantidade de registros no histórico do cadastro do perfisUsuarios
	 * 
	 * @param filtroPerfisUsuariosJnVO
	 * @return
	 */
	public Long pesquisarHistoricoPorPerfisUsuariosCount(FiltroPerfisUsuariosJnVO filtroPerfisUsuariosJnVO) {
		return getPerfisUsuariosJnDAO().pesquisarPorPerfisUsuariosCount(filtroPerfisUsuariosJnVO);
	}
	/**
	 * #39000 - Serviço que retorna existe servidor categoria prof medicos
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		Integer categoriaProfMedico = null;
		RapServidores rapServidores = getRegistroColaboradoFacade().obterServidor(vinculo, matricula);
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
		if (parametro != null) {
			categoriaProfMedico = parametro.getVlrNumerico().intValue();
		}
		if (rapServidores != null && categoriaProfMedico != null) {
			return getPerfisUsuariosDAO().existeServidorCategoriaProfMedico(rapServidores.getUsuario(), categoriaProfMedico);
		}
		return false;	
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected PerfilDAO getPerfilDAO() {
		return perfilDAO;
	}
	
	protected PerfisUsuariosDAO getPerfisUsuariosDAO() {
		return perfisUsuariosDAO;
	}
	
	protected PerfisUsuariosJnDAO getPerfisUsuariosJnDAO() {
		return perfisUsuariosJnDAO;
	}
	
	protected EmailUtil getEmailUtil() {
		return this.emailUtil;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradoFacade(){
		return this.registroColaboradoFacade;
	}
		
}
