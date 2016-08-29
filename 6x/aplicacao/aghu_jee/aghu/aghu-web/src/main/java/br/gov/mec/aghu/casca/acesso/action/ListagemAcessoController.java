package br.gov.mec.aghu.casca.acesso.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Acesso;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioSimNaoTodos;
import br.gov.mec.aghu.dominio.DominioTipoAcesso;
import br.gov.mec.aghu.dominio.DominioTipoAcessoTodos;
import br.gov.mec.aghu.parametrosistema.vo.AghParametroVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller responsavel pelas telas de pesquisarAcessos e meusAcessos.<br>
 * 
 * 
 * @author rcorvalao
 *
 */
public class ListagemAcessoController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -5370726908624909532L;
	
	
	
	private Usuario usuario;
	private DominioSimNaoTodos autorizado = DominioSimNaoTodos.TODOS;
	private String enderecoOrigem;
	private String observacao;
	private Date dataInicial;	
	private Date dataFinal;	
	private DominioTipoAcessoTodos tipoAcesso = DominioTipoAcessoTodos.TODOS;
	
	@Inject @Paginator
	private DynamicDataModel<AghParametroVO> dataModel;
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private ICascaFacade cascaFacade;
	
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	/**
	 * Metodo de inicio para a tela de Meus Acessos.<br>
	 * Necessaria para carregar as informações na abertura da tela.
	 */
	public void initMeusAcessos() {
		try {
			usuario = cascaFacade.recuperarUsuario(this.obterLoginUsuarioLogado());
			reiniciarPaginator();		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	public void reiniciarPaginator() {
		this.getDataModel().reiniciarPaginator();
	}
		
	public DynamicDataModel<AghParametroVO> getDataModel() {
		return dataModel;
	}

	public List<Usuario> pesquisarUsuarios(String parametro) throws ApplicationBusinessException {
		String nomeOuLogin = parametro;
		return this.returnSGWithCount(cascaFacade.pesquisarUsuarios(0, 100, null, true, nomeOuLogin),pesquisarUsuariosCount(parametro));

	}

	public Long pesquisarUsuariosCount(String parametro)
			throws ApplicationBusinessException {
		String nomeOuLogin = parametro;
		return cascaFacade.pesquisarUsuariosCount(nomeOuLogin);

	}

	public void limparPesquisa() {
		usuario = null;
		autorizado = DominioSimNaoTodos.TODOS;
		tipoAcesso = DominioTipoAcessoTodos.TODOS;
		enderecoOrigem = null;
		observacao = null;
		dataFinal = dataInicial = null;
		this.getDataModel().limparPesquisa();
	}

	public void pesquisar() {
		this.reiniciarPaginator();
	}

	@Override
	public List<Acesso> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
        Boolean boAutorizado = getBoAutorizado();
        DominioTipoAcesso tipo = getTipoDominioAcesso();
        
		return cascaFacade.pesquisarAcessos(firstResult, maxResult,
				Acesso.Fields.DATA.toString(), false, usuario, enderecoOrigem,
				observacao, boAutorizado,tipo, dataInicial, dataFinal);
	}

	@Override
	public Long recuperarCount() {
		Boolean boAutorizado = getBoAutorizado();
		DominioTipoAcesso tipo = getTipoDominioAcesso();
		return cascaFacade.pesquisarAcessosCount(usuario, enderecoOrigem,
				observacao, boAutorizado, tipo, dataInicial, dataFinal);
	}
	
	
	private Boolean getBoAutorizado(){
	    Boolean boAutorizado = null;	    
	    switch (this.autorizado) {
        case SIM:
            boAutorizado = Boolean.TRUE;
            break;
        case NAO:
            boAutorizado = Boolean.FALSE;
            break;
        case TODOS:
            boAutorizado = null;
            break;
        }	    
	    return boAutorizado;
	}
	
	
	private DominioTipoAcesso getTipoDominioAcesso(){
       DominioTipoAcesso tipo = null;
        if (tipoAcesso != null) {
            switch (tipoAcesso) {
            case ENTRADA:
                tipo = DominioTipoAcesso.ENTRADA;
                break;
            case SAIDA:
                tipo = DominioTipoAcesso.SAIDA;
                break;
            case TODOS:
                tipo = null;
                break;
            default:
                break;
            }
        }
        return tipo;
	}
	

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public DominioSimNaoTodos getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(DominioSimNaoTodos autorizado) {
		this.autorizado = autorizado;
	}

	public String getEnderecoOrigem() {
		return enderecoOrigem;
	}

	public void setEnderecoOrigem(String enderecoOrigem) {
		this.enderecoOrigem = enderecoOrigem;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public DominioTipoAcessoTodos getTipoAcesso() {
		return tipoAcesso;
	}

	public void setTipoAcesso(DominioTipoAcessoTodos tipoAcesso) {
		this.tipoAcesso = tipoAcesso;
	}
	

}
