package br.gov.mec.aghu.casca.usuario.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisUsuarios;
import br.gov.mec.aghu.casca.model.Permissao;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

public class MeusPerfisController extends ActionController {
	private static final long serialVersionUID = 3208712850704098761L;

	private final String PAGE_DELEGAR_PERFIL = "delegarPerfil";	
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	private Usuario usuario;
	private List<PerfisUsuarios> meusPerfis;
	private Perfil perfil;
	
	private List<Permissao> minhasPermissoes;	

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void inicio() {
		usuario = cascaFacade.obterUsuario(obterLoginUsuarioLogado());
		if (!usuario.isAtivo()){
			usuario=null;
		}else{
			meusPerfis = cascaFacade.pequisarPerfisUsuariosSemCache(usuario);
		}
	}

	public String tituloPerfil(PerfisUsuarios perfil){
		StringBuffer titulo = new StringBuffer(perfil.getPerfil().getNome());
		if(perfil.getPerfil().isDelegavel() && usuario.isDelegarPerfil() &&  perfil.getDataExpiracao() == null){
			titulo.append(" - Delegável");
		} else if(perfil.getDataExpiracao() != null){
			titulo.append(" - Data Expiração:").append(DateFormatUtil.formataTimeStamp(perfil.getDataExpiracao()));
		}
		return titulo.toString();
	}
	
	
	public List<Permissao> permissoesPorPerfil(Perfil perfil){
		if (!perfil.equals(this.perfil)){
			this.perfil=perfil;
			minhasPermissoes = cascaFacade.obterPermissoesPorPerfil(perfil);
		}
		return minhasPermissoes;
	}
	

	public String delegarPerfil(){
		return PAGE_DELEGAR_PERFIL;
	}
	
	
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public List<PerfisUsuarios> getMeusPerfis() {
		return meusPerfis;
	}


	public void setMeusPerfis(List<PerfisUsuarios> meusPerfis) {
		this.meusPerfis = meusPerfis;
	}
}