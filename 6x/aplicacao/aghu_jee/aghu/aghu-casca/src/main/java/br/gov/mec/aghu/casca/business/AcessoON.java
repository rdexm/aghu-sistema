package br.gov.mec.aghu.casca.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.dao.AcessoDAO;
import br.gov.mec.aghu.casca.model.Acesso;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.ParametrosSistema;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioTipoAcesso;

/**
 * 
 * @author aghu
 * 
 */
@Stateless
public class AcessoON extends BaseBusiness {

	@EJB
	private UsuarioON usuarioON;

	private static final Log LOG = LogFactory.getLog(AcessoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	
	@Inject
	private AcessoDAO acessoDAO;
	
	@Inject
	private ParametrosSistema parametrosSistema;
	

	private static final long serialVersionUID = -3242312547018786359L;

	public void registrarAcesso(String login, String enderecoOrigem,
			String mensagem, Boolean autorizado, DominioTipoAcesso tipoAcesso)
			throws ApplicationBusinessException {
		
		Usuario usuario = getUsuarioON().obterUsuario(login);
		if (usuario != null) {
			if (tipoAcesso == DominioTipoAcesso.ENTRADA && autorizado) {
				//atribuirContextoSessao("ultimoAcesso", usuario.getDataUltimoAcesso());
				usuario.setDataUltimoAcesso(new Date());
			}
			
			this.criarAcesso(usuario, enderecoOrigem, mensagem, autorizado,
					tipoAcesso);
		} else {
			LOG.warn("Login não informado para registro de acesso");
		}
	}
	
	public Boolean verificarMaximoTentativasAcessoUltimoMinuto( String enderecoOrigem) throws ApplicationBusinessException{
		Boolean retorno = false;
		Long numTentativasUltimoMinuto = getAcessoDAO().pesquisarCountTentativasAcessoUltimoMinuto(enderecoOrigem);
		Integer numMaxTentativasUltimoMinuto =  Integer.valueOf(parametrosSistema.getParametro("nro_tentativas_login_por_minuto"));
		if (numTentativasUltimoMinuto > numMaxTentativasUltimoMinuto){
			retorno = true;
		}
		return retorno;
		
	}

	private void criarAcesso(Usuario usuario, String enderecoOrigem,
			String mensagem, boolean autorizado, DominioTipoAcesso tipoAcesso) {
		Acesso acesso = new Acesso(usuario, enderecoOrigem, autorizado,
				new Date(), mensagem, tipoAcesso);
		getAcessoDAO().persistir(acesso);
	}

	private UsuarioON getUsuarioON() {
		return usuarioON;
	}
	

	private AcessoDAO getAcessoDAO() {
		return acessoDAO;

	}

	public String getBaseUrlDocumentacao() {
		return parametrosSistema.getParametro("base_url_documentacao");
	}
}