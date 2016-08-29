package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class GrupoPesquisaExamePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 3185865646460952846L;

	private static final String GRUPO_PESQUISA_EXAME = "grupoPesquisaExame";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	private AelAgrpPesquisas filtros = new AelAgrpPesquisas();

	@Inject @Paginator
	private DynamicDataModel<AelAgrpPesquisas> dataModel;
	
	private AelAgrpPesquisas itemSelecionado;
	
	private Short seq;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.obterAelAgrpPesquisasListCount(filtros);
	}
	
	@Override
	public List<AelAgrpPesquisas> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.obterAelAgrpPesquisasList(filtros, firstResult, maxResult, orderProperty, asc);
	}

	public void limpar() {
		this.filtros = new AelAgrpPesquisas();
		dataModel.limparPesquisa();
	}
	
	public String inserir(){
		return GRUPO_PESQUISA_EXAME;
	}
	
	public String detalhar(){
		return GRUPO_PESQUISA_EXAME;
	}
	
	public void ativarInativar(final AelAgrpPesquisas aelAgrpPesquisas) {
		try {
			aelAgrpPesquisas.setIndSituacao( (DominioSituacao.A.equals(aelAgrpPesquisas.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			cadastrosApoioExamesFacade.persistirAelAgrpPesquisas(aelAgrpPesquisas);
			this.apresentarMsgNegocio( Severity.INFO, 
												    ( DominioSituacao.A.equals(aelAgrpPesquisas.getIndSituacao()) 
												    	? "MENSAGEM_GRUPOS_EXAME_INATIVADO_SUCESSO" 
														: "MENSAGEM_GRUPOS_EXAME_ATIVADO_SUCESSO" 
													), aelAgrpPesquisas.getDescricao());
			
			pesquisar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public AelAgrpPesquisas getFiltros() {
		return filtros;
	}

	public void setFiltros(AelAgrpPesquisas filtros) {
		this.filtros = filtros;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public DynamicDataModel<AelAgrpPesquisas> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelAgrpPesquisas> dataModel) {
		this.dataModel = dataModel;
	}

	public AelAgrpPesquisas getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(AelAgrpPesquisas itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}