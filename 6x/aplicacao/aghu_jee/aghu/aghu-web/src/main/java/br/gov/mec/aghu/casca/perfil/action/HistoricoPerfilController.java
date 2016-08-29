package br.gov.mec.aghu.casca.perfil.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfilJn;
import br.gov.mec.aghu.casca.vo.FiltroPerfilJnVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class HistoricoPerfilController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = 484829647024871557L;
	private final String PAGE_CADASTRAR_PERFIL = "cadastrarPerfil";

	@EJB @SuppressWarnings("cdi-ambiguous-dependency")	
	private ICascaFacade cascaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<PerfilJn> dataModel;	
	private Perfil perfil;
	
	//Filtros
	private String nome;
	private DominioOperacoesJournal operacao;
	private Date dataInicio;
	private Date dataFim;	
	private String alteradoPor;

	@PostConstruct
	public void init() {
		begin(conversation);
		nome=null;
		operacao=null;
		dataInicio=null;
		dataFim=null;
		alteradoPor=null;
		if (perfil!=null){
			nome=perfil.getNome();
		}		
	}
	
	
	@Override
	public Long recuperarCount() {
		return cascaFacade.pesquisarHistoricoPorPerfilCount(new FiltroPerfilJnVO(perfil.getId(), nome, operacao, dataInicio, dataFim,alteradoPor));
	}

	@Override
	public List<PerfilJn> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return cascaFacade.pesquisarHistoricoPorPerfil(firstResult, maxResult,orderProperty, asc, 
				new FiltroPerfilJnVO(perfil.getId(), nome, operacao, dataInicio, dataFim, alteradoPor));
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	
	
	public void limparPesquisa() {
		init();
		pesquisar();
	}
	
	public String cancelar() {
		init();
		dataModel.limparPesquisa();
		return PAGE_CADASTRAR_PERFIL;
	}	
	

	public ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	public void setCascaFacade(ICascaFacade cascaFacade) {
		this.cascaFacade = cascaFacade;
	}

	public DynamicDataModel<PerfilJn> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PerfilJn> dataModel) {
		this.dataModel = dataModel;
	}

	public Perfil getPerfil() {
		return perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getAlteradoPor() {
		return alteradoPor;
	}

	public void setAlteradoPor(String alteradoPor) {
		this.alteradoPor = alteradoPor;
	}	
	

}
