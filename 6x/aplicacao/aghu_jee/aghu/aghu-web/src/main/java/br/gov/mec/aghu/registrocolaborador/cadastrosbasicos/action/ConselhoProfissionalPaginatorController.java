package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConselhoProfissionalPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -2056260244394895229L;

	private static final String CADASTRAR_CONSELHO_PROFISSIONAL = "cadastrarConselhoProfissional";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapConselhosProfissionais conselhoProfissional  = new RapConselhosProfissionais();
	

	@Inject @Paginator
	private DynamicDataModel<RapConselhosProfissionais> dataModel;
	
	private RapConselhosProfissionais selecionado;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosFacade.pesquisarConselhosProfissionaisCount(conselhoProfissional.getCodigo(),
																		   conselhoProfissional.getNome(), 
																		   conselhoProfissional.getSigla(), 
																		   conselhoProfissional.getIndSituacao(),
																		   conselhoProfissional.getTituloMasculino(),
																		   conselhoProfissional.getTituloFeminino(), null);
	}
	
	@Override
	public List<RapConselhosProfissionais> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cadastrosBasicosFacade.pesquisarConselhosProfissionais(conselhoProfissional.getCodigo(), 
																	  conselhoProfissional.getNome(), 
																	  conselhoProfissional.getSigla(), 
																	  conselhoProfissional.getIndSituacao(),
																	  conselhoProfissional.getTituloMasculino(), 
																	  conselhoProfissional.getTituloFeminino(),
																	  firstResult, maxResult,	orderProperty, asc, null);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String editar(){
		return CADASTRAR_CONSELHO_PROFISSIONAL;
	}

	public String inserir(){
		return CADASTRAR_CONSELHO_PROFISSIONAL;
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.conselhoProfissional = new RapConselhosProfissionais();
	}

	public void excluir() {
		try {
			
			cadastrosBasicosFacade.excluirConselhoProfissional(selecionado.getCodigo());
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_CONSELHO_PROFISSIONAL");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public RapConselhosProfissionais getConselhoProfissional() {
		return conselhoProfissional;
	}

	public void setConselhoProfissional(
			RapConselhosProfissionais conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}

	public DynamicDataModel<RapConselhosProfissionais> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapConselhosProfissionais> dataModel) {
		this.dataModel = dataModel;
	}

	public RapConselhosProfissionais getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(RapConselhosProfissionais selecionado) {
		this.selecionado = selecionado;
	}

}