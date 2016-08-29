package br.gov.mec.aghu.casca.usuario.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfilApi;
import br.gov.mec.aghu.casca.model.PerfisUsuariosApi;
import br.gov.mec.aghu.casca.model.UsuarioApi;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PerfilUsuarioApiController extends ActionController {

	private static final long serialVersionUID = 5453179102933194327L;

	private static final String REDIRECIONA_PESQUISAR_USUARIO_API = "pesquisarUsuariosApi";

	@EJB
	private ICascaFacade cascaFacade;
	
	private Integer seq = 600000;
	
	private Integer seqUsuario;
	
	private UsuarioApi usuarioApi;

	private List<PerfisUsuariosApi> perfisSelecionados;	
	private PerfilApi perfilApi;
	
	public void iniciar(){
		if (seqUsuario != null) {
			setUsuarioApi(cascaFacade.obterUsuarioApi(seqUsuario));
			carregarPerfisSelecionados();
		} else {
			cancelar();
		}
	}

	public String cancelar() {
		return REDIRECIONA_PESQUISAR_USUARIO_API;
	}
	
	public String salvarPerfis() {
		try {
			for(PerfisUsuariosApi pu : perfisSelecionados) {
				if (pu.getId() > 600000) {
					pu.setId(null); 
				}
			}
 			cascaFacade.associarPerfilUsuarioApi(usuarioApi.getId(), perfisSelecionados);			
			this.apresentarMsgNegocio(Severity.INFO, "CASCA_MENSAGEM_SUCESSO_ATUALIZACAO_PERFIL_USUARIO");
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public List<Perfil> getListaPerfis() {
		return cascaFacade.pesquisarPerfis((String) null);		
	}

	public List<PerfilApi> pesquisarPerfisSuggestionBox(String stringPesquisa) {
		return this.returnSGWithCount(cascaFacade.pesquisarPerfisApiSuggestionBox((String) stringPesquisa),pesquisarPerfilApiCountSuggestionBox(stringPesquisa));
	}

	public Long pesquisarPerfilApiCountSuggestionBox(String stringPesquisa) {
		return cascaFacade.pesquisarPerfilApiCountSuggestionBox((String) stringPesquisa);
	}

	public void adicionarPerfilUsuarioApi() {
		if (perfilApi == null) {			
			this.apresentarMsgNegocio(Severity.ERROR,"CASCA_MENSAGEM_PERFIL_NAO_INFORMADO");
		}else{
			boolean repetido = false;
			for (PerfisUsuariosApi perfilUsuario : getPerfisSelecionados()){
				if (perfilUsuario.getPerfilApi().equals(perfilApi)){
					repetido = true;
					break;
				}
			}			
			if (repetido) {
				this.apresentarMsgNegocio(Severity.ERROR,"CASCA_MENSAGEM_PERFIL_EXISTENTE");
			}else{
 				PerfisUsuariosApi perfilUsuario = new PerfisUsuariosApi();
 				perfilUsuario.setId(seq++);
 				perfilUsuario.setDataCriacao(new Date());
				perfilUsuario.setUsuarioApi(usuarioApi);
				perfilUsuario.setPerfilApi(perfilApi);				
				getPerfisSelecionados().add(perfilUsuario);
				setPerfilApi(null);
			}
		}
	}

	public List<PerfisUsuariosApi> getPerfisSelecionados() {
		if (this.perfisSelecionados == null) {
			this.perfisSelecionados = new ArrayList<PerfisUsuariosApi>();
		}
		return this.perfisSelecionados;
	}
	
	public void setPerfisSelecionados(List<PerfisUsuariosApi> perfisSelecionados) {
		this.perfisSelecionados = perfisSelecionados;
	}

	public void remover(PerfisUsuariosApi perfilUsuarioApi) {
		getPerfisSelecionados().remove(perfilUsuarioApi);		
	}

	public void carregarPerfisSelecionados() {
		this.perfisSelecionados = new ArrayList<PerfisUsuariosApi>();
		List<PerfisUsuariosApi> perfisUsuario2 = cascaFacade.pequisarPerfisUsuariosApi(usuarioApi);		
		
		for (PerfisUsuariosApi perfisUsuarios : perfisUsuario2) {
			if (!getPerfisSelecionados().contains(perfisUsuarios)) {
				getPerfisSelecionados().add(perfisUsuarios);
			}
		}
	}

	public Integer getSeqUsuario() {
		return seqUsuario;
	}

	public void setSeqUsuario(Integer seqUsuario) {
		this.seqUsuario = seqUsuario;
	}

	public UsuarioApi getUsuarioApi() {
		return usuarioApi;
	}

	public void setUsuarioApi(UsuarioApi usuarioApi) {
		this.usuarioApi = usuarioApi;
	}

	public PerfilApi getPerfilApi() {
		return perfilApi;
	}

	public void setPerfilApi(PerfilApi perfilApi) {
		this.perfilApi = perfilApi;
	}
}