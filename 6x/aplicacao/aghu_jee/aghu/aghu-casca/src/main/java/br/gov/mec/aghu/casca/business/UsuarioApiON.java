package br.gov.mec.aghu.casca.business;

import java.util.ArrayList;
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
import br.gov.mec.aghu.casca.dao.PerfisUsuariosApiDAO;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosApiJnDAO;
import br.gov.mec.aghu.casca.dao.UsuarioApiDAO;
import br.gov.mec.aghu.casca.dao.UsuarioApiJnDAO;
import br.gov.mec.aghu.casca.model.PerfisUsuariosApi;
import br.gov.mec.aghu.casca.model.PerfisUsuariosApiJn;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.casca.model.UsuarioApiJn;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;

@Stateless
public class UsuarioApiON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(UsuarioApiON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private UsuarioApiJnDAO usuarioApiJnDAO;
	
	@Inject
	private UsuarioApiDAO usuarioApiDAO;
	
	@Inject
	private PerfisUsuariosApiDAO perfisUsuariosApiDAO;
	
	@Inject
	private PerfisUsuariosApiJnDAO perfisUsuariosApiJnDAO;
	
	@EJB
	TokenApiON tokenApiOn;
	
	private static final long serialVersionUID = -269643020564432693L;
	
	protected enum UsuarioApiONExceptionCode implements BusinessExceptionCode {
		CASCA_MENSAGEM_USUARIO_NAO_INFORMADO, 
		CASCA_MENSAGEM_LOGIN_EXISTENTE, 
		CASCA_MENSAGEM_USUARIO_NAO_ENCONTRADO, 
		CASCA_MENSAGEM_PARAMETROS_NAO_INFORMADO, 
		CASCA_MENSAGEM_USUARIO_INATIVO,
		CASCA_MENSAGEM_USUARIO_SEM_PERFIL, CASCA_MENSAGEM_HORA_DIFERENTE,
		ERRO_AUTENTICACAO, CASCA_MENSAGEM_PERFIL_EXISTENTE,
		CASCA_USUARIO_NOME_EXISTENTE;
	}

	public void enviarEmailChaveIdentificadora(UsuarioApi usuarioApi) {
		String remetente;
		try {
			remetente = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		} catch (ApplicationBusinessException e) {
			remetente = "aghu@hcpa.edu.br";
		}
		List<String> destinatarios = new ArrayList<String>();
		destinatarios.add(usuarioApi.getEmail());

		String titulo = "Chave de Acesso para API AGHU";
		StringBuilder msg = new StringBuilder(1024);
		msg.append("Insira as credenciais abaixo na propriedade 'Authorization' no cabeçalho da requisição para solicitação do token de acesso, em formato base64, separados por uma barra ('|'): <br>")
			.append("user: ").append(usuarioApi.getAuthUsuario())
			.append("<br>key: ").append(usuarioApi.getAuthKey())
			.append("<br><br>Exemplo de formato para envio é: <br> Authorization: user|key<br> Considerando o usuário 'teste' e a chave de acesso 'teste22', o cabeçalho http deve conter: <br> Authorization: dGVzdGUK|Y2hhdmUyMgo=");
		
		emailUtil.enviaEmailHtml(remetente, destinatarios, null, titulo, msg.toString());
	}
	
	public void gerarIdentificacaoAcesso(UsuarioApi usuarioApi) {
		usuarioApi.setAuthKey(tokenApiOn.gerarIdentificador(32));
		usuarioApi.setAuthUsuario(tokenApiOn.gerarIdentificador(20));
	}
	
	public void reiniciarIdentificacaoAcesso(UsuarioApi usuarioApi) throws ApplicationBusinessException {
		if (usuarioApi == null) {
			throw new ApplicationBusinessException(
					UsuarioApiONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
		}
		
		this.gerarIdentificacaoAcesso(usuarioApi);
		this.enviarEmailChaveIdentificadora(usuarioApi);
		
		this.salvarUsuario(usuarioApi);
	}
	
	public void salvarUsuario(UsuarioApi usuarioApi) throws ApplicationBusinessException {
		if (usuarioApi == null) {
			throw new ApplicationBusinessException(
					UsuarioApiONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
		}
				
		if (usuarioApi.getId() == null) {
			verificarUsuarioNomeExistente(usuarioApi);			
			if (StringUtils.isBlank(usuarioApi.getAuthKey()) || StringUtils.isBlank(usuarioApi.getAuthUsuario())) {
				this.gerarIdentificacaoAcesso(usuarioApi);
				this.enviarEmailChaveIdentificadora(usuarioApi);
			}
			
			usuarioApi.setDataCriacao(new Date());
			usuarioApiDAO.persistir(usuarioApi);
			
			UsuarioApiJn usuarioApiJn = this.criarJournal(usuarioApi, DominioOperacoesJournal.INS);
			usuarioApiJnDAO.persistir(usuarioApiJn);

		} else {
			UsuarioApi usuarioOriginal = usuarioApiDAO.obterOriginal(usuarioApi);
			UsuarioApi uApi = usuarioApiDAO.obterPorChavePrimaria(usuarioApi.getId());
			if (uApi != null) {
				uApi.setAuthKey(usuarioApi.getAuthKey());
				uApi.setAuthUsuario(usuarioApi.getAuthUsuario());
				uApi.setAtivo(usuarioApi.isAtivo());
				uApi.setEmail(usuarioApi.getEmail());
				uApi.setLimiteTokensAtivos(usuarioApi.getLimiteTokensAtivos());
				uApi.setLoginHcpa(usuarioApi.getLoginHcpa());
				uApi.setNome(usuarioApi.getNome());
				uApi.setTempoTokenMinutos(usuarioApi.getTempoTokenMinutos());
				
				UsuarioApiJn usuarioApiJn = null;
				boolean alterado = this.alterado(uApi, usuarioOriginal);
							
				usuarioApiDAO.persistir(uApi);
				
				if(alterado){
					usuarioApiJn = this.criarJournal(usuarioOriginal, DominioOperacoesJournal.UPD);
					usuarioApiJnDAO.persistir(usuarioApiJn);
				}
			}
		}
	}
	
	private void verificarUsuarioNomeExistente(UsuarioApi usuarioApi) throws ApplicationBusinessException {
		Long qtd = usuarioApiDAO.pesquisarUsuariosApiCount(usuarioApi.getNome(), null, null, null);
		if (qtd > 0L){
			throw new ApplicationBusinessException(
					UsuarioApiONExceptionCode.CASCA_USUARIO_NOME_EXISTENTE);
		}		
	}

	public void associarPerfilUsuarioApi(Integer idUsuario, List<PerfisUsuariosApi> listaPerfisUsuarios) throws ApplicationBusinessException {

		if (idUsuario == null) {
			throw new ApplicationBusinessException(UsuarioApiONExceptionCode.CASCA_MENSAGEM_USUARIO_NAO_INFORMADO);
		}

		UsuarioApi usuarioApi = usuarioApiDAO.obterPorChavePrimaria(idUsuario);
		
		if (idUsuario != null) {
			List<PerfisUsuariosApi> perfisUsuariosAtual = perfisUsuariosApiDAO.pequisarPerfisUsuariosApi(usuarioApi);
			
			for (PerfisUsuariosApi perfilUsuario : perfisUsuariosAtual){
				if (!listaPerfisUsuarios.contains(perfilUsuario)){
					this.removerPerfisUsuarioApi(perfilUsuario);
				}
			}
			perfisUsuariosAtual = perfisUsuariosApiDAO.pequisarPerfisUsuariosApi(usuarioApi);
			
			for (PerfisUsuariosApi perfilUsuario : listaPerfisUsuarios){
				if (!perfisUsuariosAtual.contains(perfilUsuario)){
					this.associaPerfisUsuarios(usuarioApi, perfilUsuario);
				}
			}
		}
	}

	public void associaPerfisUsuarios(UsuarioApi usuarioApi, PerfisUsuariosApi perfilUsuarioApi) throws ApplicationBusinessException {
		PerfisUsuariosApi p = new PerfisUsuariosApi();
		p.setDataCriacao(new Date());
		p.setUsuarioApi(usuarioApi);
		p.setPerfilApi(perfilUsuarioApi.getPerfilApi());
		
		perfisUsuariosApiDAO.persistir(p);
		PerfisUsuariosApiJn perfisUsuariosApiJn = this.criarJournal(p, DominioOperacoesJournal.INS);
		perfisUsuariosApiJnDAO.persistir(perfisUsuariosApiJn);
	}

	public void removerPerfisUsuarioApi(PerfisUsuariosApi perfilUsuario) throws ApplicationBusinessException {
		PerfisUsuariosApiJn perfisUsuariosApiJn = this.criarJournal(perfilUsuario, DominioOperacoesJournal.DEL);
		perfisUsuariosApiDAO.removerPorId(perfilUsuario.getId());
		
		perfisUsuariosApiJnDAO.persistir(perfisUsuariosApiJn);
	}
	
	public List<UsuarioApi> pesquisarUsuariosApi(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String nome, String email, String loginHcpa, DominioSituacao situacao) {
		return usuarioApiDAO.pesquisarUsuariosApi(firstResult, maxResult, orderProperty, asc, nome, email, loginHcpa, situacao);
	}

	public Long pesquisarUsuariosApiCount(String nome, String email, String loginHcpa, DominioSituacao situacao) {
		return usuarioApiDAO.pesquisarUsuariosApiCount(nome, email, loginHcpa, situacao);
	}
	
	private boolean alterado(UsuarioApi usuario, UsuarioApi usuarioOriginal){
		if(usuario != null && usuarioOriginal != null){
			if(CoreUtil.modificados(usuario.getNome(), usuarioOriginal.getNome()) ||
				CoreUtil.modificados(usuario.getEmail(), usuarioOriginal.getEmail()) ||
				CoreUtil.modificados(usuario.getLoginHcpa(), usuarioOriginal.getLoginHcpa()) ||
				CoreUtil.modificados(usuario.isAtivo(), usuarioOriginal.isAtivo()) || 
				CoreUtil.modificados(usuario.getTempoTokenMinutos(), usuarioOriginal.getTempoTokenMinutos()) ||
				CoreUtil.modificados(usuario.getLimiteTokensAtivos(), usuarioOriginal.getLimiteTokensAtivos()) ||
				CoreUtil.modificados(usuario.getAuthUsuario(), usuarioOriginal.getAuthUsuario()) ||
				CoreUtil.modificados(usuario.getAuthKey(), usuarioOriginal.getAuthKey())){
				return true;
			}
			return false;
		}
		return false;
	}
	
	private UsuarioApiJn criarJournal(UsuarioApi usuarioApi, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		 
		UsuarioApiJn usuarioJn = BaseJournalFactory.getBaseJournal(operacao, UsuarioApiJn.class, servidorLogado.getUsuario());
		usuarioJn.setId(usuarioApi.getId());
		usuarioJn.setNome(usuarioApi.getNome());
		usuarioJn.setLoginHcpa(usuarioApi.getLoginHcpa());
		usuarioJn.setDataCriacao(usuarioApi.getDataCriacao());
		usuarioJn.setEmail(usuarioApi.getEmail());
		usuarioJn.setDataUltimoAcesso(usuarioApi.getDataUltimoAcesso());
		usuarioJn.setAtivo(usuarioApi.isAtivo());
		usuarioJn.setTempoTokenMinutos(usuarioApi.getTempoTokenMinutos());
		usuarioJn.setLimiteTokensAtivos(usuarioApi.getLimiteTokensAtivos());
		usuarioJn.setAuthUsuario(usuarioApi.getAuthUsuario());
		usuarioJn.setAuthKey(usuarioApi.getAuthKey());
		return usuarioJn;
	}
	
	private PerfisUsuariosApiJn criarJournal(PerfisUsuariosApi perfilUsuarioApi, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		 
		PerfisUsuariosApiJn perfilusuarioApiJn = BaseJournalFactory.getBaseJournal(operacao, PerfisUsuariosApiJn.class, servidorLogado.getUsuario());
		perfilusuarioApiJn.setId(perfilUsuarioApi.getId());
		perfilusuarioApiJn.setIdPerfil(perfilUsuarioApi.getPerfilApi().getId());
		perfilusuarioApiJn.setDataCriacao(perfilUsuarioApi.getDataCriacao());
		perfilusuarioApiJn.setIdUsuario(perfilUsuarioApi.getUsuarioApi().getId());
		
		return perfilusuarioApiJn;
	}
}