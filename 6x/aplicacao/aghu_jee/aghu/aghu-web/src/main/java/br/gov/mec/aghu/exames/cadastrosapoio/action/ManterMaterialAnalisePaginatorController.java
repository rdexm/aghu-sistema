package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterMaterialAnalisePaginatorController extends ActionController implements ActionPaginator {

	private static final Log LOG = LogFactory.getLog(ManterMaterialAnalisePaginatorController.class);

	private static final long serialVersionUID = 7080793011031392915L;

	private static final String MANTER_MATERIAL_ANALISE_CRUD = "manterMaterialAnaliseCRUD";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AelMateriaisAnalises> dataModel;
	
	private AelMateriaisAnalises selecionado;

	//Campos filtro
	private Integer codigo;
	private String descricao;
	private DominioSimNao indColetavel;
	private DominioSimNao indExigeDescricao;
	private DominioSimNao indUrina;
	private DominioSituacao indSituacao;

	@PostConstruct
	protected void inicializar() {
		LOG.debug("begin conversation");
		this.begin(conversation);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		codigo = null;
		descricao = null;
		indColetavel = null;
		indExigeDescricao = null;
		indUrina = null;
		indSituacao = null;
	}

	@Override
	public List<AelMateriaisAnalises> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return examesFacade.pesquisarMateriasAnalise(firstResult, maxResult, AelMateriaisAnalises.Fields.DESCRICAO.toString(), true, 
													  new AelMateriaisAnalises(codigo, descricao, indSituacao, 
															  				 	indColetavel != null ? indColetavel.isSim() : null, null, 
															  				 	indExigeDescricao != null ? indExigeDescricao.isSim() : null, 
															  				 	indUrina != null ? indUrina.isSim(): null));
	}

	@Override
	public Long recuperarCount() {
		return examesFacade.pesquisarMateriasAnaliseCount(new AelMateriaisAnalises(codigo, descricao, indSituacao, 
																					indColetavel != null ? indColetavel.isSim() : null, null,
																					indExigeDescricao != null ? indExigeDescricao.isSim() : null, 
																					indUrina != null ? indUrina.isSim(): null));
	}

	public void excluir()  {
		try {
			cadastrosApoioExamesFacade.removerMaterialAnalise(selecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MATERIAL_ANALISE", selecionado.getDescricao());

		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String inserir(){
		return MANTER_MATERIAL_ANALISE_CRUD;
	}
	
	public String editar(){
		return MANTER_MATERIAL_ANALISE_CRUD;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getIndColetavel() {
		return indColetavel;
	}

	public void setIndColetavel(DominioSimNao indColetavel) {
		this.indColetavel = indColetavel;
	}

	public DominioSimNao getIndExigeDescricao() {
		return indExigeDescricao;
	}

	public void setIndExigeDescricao(DominioSimNao indExigeDescricao) {
		this.indExigeDescricao = indExigeDescricao;
	}

	public DominioSimNao getIndUrina() {
		return indUrina;
	}

	public void setIndUrina(DominioSimNao indUrina) {
		this.indUrina = indUrina;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DynamicDataModel<AelMateriaisAnalises> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelMateriaisAnalises> dataModel) {
		this.dataModel = dataModel;
	}

	public AelMateriaisAnalises getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelMateriaisAnalises selecionado) {
		this.selecionado = selecionado;
	}
}