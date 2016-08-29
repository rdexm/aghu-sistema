package br.gov.mec.aghu.administracao.action;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.dominio.DominioLogAplicacao;
import br.gov.mec.aghu.model.AghLogAplicacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class LogAplicacaoPaginatorController extends ActionController implements ActionPaginator {

   	@Inject @Paginator
	private DynamicDataModel<AghLogAplicacao> dataModel;

	private static final Log LOG = LogFactory.getLog(LogAplicacaoPaginatorController.class);
	
	private static final long serialVersionUID = -4840447813441087888L;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private Usuario usuario;
	private Date dthrCriacaoIni;
	private Date dthrCriacaoFim;
	private String classe;
	private DominioLogAplicacao nivel;
	private String mensagem;

	
	@PostConstruct
	protected void inicializar() {
		LOG.info("inicializar");
		
		this.begin(conversation);
	}

	
	public void limparPesquisa() {
		this.usuario = null;
		this.dthrCriacaoIni = null;
		this.dthrCriacaoFim = null;
		this.classe = null;
		this.nivel = null;
		this.mensagem = null;
		//this.setAtivo(Boolean.FALSE);
		this.dataModel.limparPesquisa();
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Pesquisa usada pela SB que deve retornar todos os usuários passíveis de
	 * serem associados com o servidor em edição
	 * 
	 * @param objParam
	 *            Nome ou login do usuário
	 * @return Lista de usuários
	 */
	public List<Usuario> pesquisarUsuario(String objParam) {
		String strPesquisa = (String) objParam;
		try {
			return  cascaFacade.pesquisarUsuarios(0, 100,
					Usuario.Fields.LOGIN.toString(), true, strPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<AghLogAplicacao> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.aghuFacade.pesquisarAghLogAplicacao(this.getLogin(),
				dthrCriacaoIni, dthrCriacaoFim, classe, this.getStrNivel(),
				mensagem, firstResult, maxResult);
	}

	@Override
	public Long recuperarCount() {
		return this.aghuFacade.pesquisarAghLogAplicacaoCount(this.getLogin(),
				dthrCriacaoIni, dthrCriacaoFim, classe, this.getStrNivel(),
				mensagem);
	}

	/* *** GET/SET *** */

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getLogin() {
		return usuario != null ? usuario.getLogin() : null;
	}

	public Date getDthrCriacaoIni() {
		return dthrCriacaoIni;
	}

	public void setDthrCriacaoIni(Date dthrCriacaoIni) {
		this.dthrCriacaoIni = dthrCriacaoIni;
	}

	public Date getDthrCriacaoFim() {
		return dthrCriacaoFim;
	}

	public void setDthrCriacaoFim(Date dthrCriacaoFim) {
		this.dthrCriacaoFim = dthrCriacaoFim;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public DominioLogAplicacao getNivel() {
		return nivel;
	}

	public void setNivel(DominioLogAplicacao nivel) {
		this.nivel = nivel;
	}

	public String getStrNivel() {
		return nivel != null ? nivel.toString() : null;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	} 


	public DynamicDataModel<AghLogAplicacao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghLogAplicacao> dataModel) {
	 this.dataModel = dataModel;
	}
}
