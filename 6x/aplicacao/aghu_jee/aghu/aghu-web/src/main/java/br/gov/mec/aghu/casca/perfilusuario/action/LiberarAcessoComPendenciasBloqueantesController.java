package br.gov.mec.aghu.casca.perfilusuario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class LiberarAcessoComPendenciasBloqueantesController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2584938043614537964L;

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<Usuario> dataModel;	

	private String nomeOuLogin;
	private Usuario usuario;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}	
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.nomeOuLogin = null;
		dataModel.limparPesquisa();
	}
	
	public boolean usuarioLiberado(Usuario usuario) {
		return cascaFacade.usuarioLiberadoDePendenciasBloqueantes(usuario.getNome());
	}

	public void ativarAcessoComPendenciasBloqueadas() {
		try {
			if (usuario != null) {
				this.cascaFacade.liberarAcessoParaAcessarSistemaComPendenciasBloqueantes(usuario);

				apresentarMsgNegocio(Severity.INFO,
						"CASCA_MENSAGEM_USUARIO_LIBERADO_PARA_ACESSAR_SISTEMA_COM_PENDENCIAS_BLOQUEANTES", usuario.getNome());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "CASCA_MENSAGEM_ERRO_CARREGAR_INFORMACOES_USUARIO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	
	public void desativarAcessoComPendenciasBloqueadas() {
		try {
			if (usuario != null) {
				this.cascaFacade.removerLiberacaoAcessoSistemaComPendenciasBloqueantes(usuario);
				apresentarMsgNegocio(Severity.INFO,
						"CASCA_MENSAGEM_USUARIO_BLOQUADO_PARA_ACESSAR_SISTEMA_COM_PENDENCIAS_BLOQUEANTES", usuario.getNome());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "CASCA_MENSAGEM_ERRO_CARREGAR_INFORMACOES_USUARIO");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarUsuariosCount(nomeOuLogin);
	}

	@Override
	public List<Usuario> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			return cascaFacade.pesquisarUsuarios(firstResult, maxResult, orderProperty, asc, nomeOuLogin);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String getNomeOuLogin() {
		return nomeOuLogin;
	}

	public void setNomeOuLogin(String nomeOuLogin) {
		this.nomeOuLogin = nomeOuLogin;
	}

	public DynamicDataModel<Usuario> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Usuario> dataModel) {
		this.dataModel = dataModel;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}