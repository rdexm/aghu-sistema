package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class BairroPaginatorController extends ActionController implements ActionPaginator {

	private static final String REDIRECIONA_MANTER_BAIRRO = "bairroCRUD";

	private static final long serialVersionUID = 5674923740104235280L;
	
	@Inject @Paginator
	private DynamicDataModel<AipBairros> dataModel;

	private AipBairros aipBairro = new AipBairros();

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		dataModel.limparPesquisa();
		this.aipBairro = new AipBairros();
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.obterBairroCount(aipBairro.getCodigo(), aipBairro.getDescricao());
	}

	@Override
	public List<AipBairros> recuperarListaPaginada(Integer firstResult,Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarBairro(firstResult,maxResult, aipBairro.getCodigo(), aipBairro.getDescricao());
	}

	public void excluir() {
		try {
			cadastrosBasicosPacienteFacade.excluirBairro(aipBairro.getCodigo());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_BAIRRO");
			aipBairro = new  AipBairros();
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		aipBairro = new AipBairros();
		return REDIRECIONA_MANTER_BAIRRO;
	}
	
	public String redirecionarInclusao(){
		return REDIRECIONA_MANTER_BAIRRO;
	}

	public AipBairros getAipBairro() {
		return aipBairro;
	}

	public void setAipBairro(AipBairros aipBairro) {
		this.aipBairro = aipBairro;
	}

	public DynamicDataModel<AipBairros> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipBairros> dataModel) {
		this.dataModel = dataModel;
	}
}