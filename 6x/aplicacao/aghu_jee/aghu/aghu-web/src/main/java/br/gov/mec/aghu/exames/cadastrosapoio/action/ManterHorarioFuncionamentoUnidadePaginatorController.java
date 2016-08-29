package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoDia;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AghHorariosUnidFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterHorarioFuncionamentoUnidadePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -2264756887273527495L;

	private static final String MANTER_HORARIO_FUNCIONAMENTO_UNIDADE_CRUD = "manterHorarioFuncionamentoUnidadeCRUD";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AghUnidadesFuncionais unidadeFuncional;
	private DominioTipoDia tipoDia;
	private Date hrInicial;
	private Date hrFinal;
	private DominioSimNao plantao;
	
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais;
	
	//parametros
	private String horaInicial;
	private DominioTipoDia tipoDiaP;
	private Short unfSeqP;
	
	private AghHorariosUnidFuncional horariosUnidFuncional;
	
	@Inject @Paginator
	private DynamicDataModel<AghHorariosUnidFuncional> dataModel;
	
	private AghHorariosUnidFuncional selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
//		TODO o que fazer aqui???????
//		if(getFirstResult() == 0 && this.ativo){
//			pesquisar();
//		}
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		this.unidadeFuncional = null;
		this.hrFinal = null;
		this.hrInicial = null;
		this.tipoDia = null;
		this.plantao = null;
	}
	
	public String inserir(){
		return MANTER_HORARIO_FUNCIONAMENTO_UNIDADE_CRUD;
	}
	
	public String editar(){
		return MANTER_HORARIO_FUNCIONAMENTO_UNIDADE_CRUD;
	}
	
	public void excluir() {
		try{
			this.cadastrosApoioExamesFacade.removerHorarioFuncionamentoUnidade(selecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_HORARIO_FUNCIONAMENTO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	public Long recuperarCount() {
		Short unfSeq = null;
		Boolean indPlantao = null;
		
		if (this.getUnidadeFuncional() != null) {
			unfSeq = this.getUnidadeFuncional().getSeq();
		}
		
		if (this.getPlantao() != null && this.getPlantao().equals(DominioSimNao.S)) {
			indPlantao = true;
			
		} else if (this.getPlantao() != null && this.getPlantao().equals(DominioSimNao.N)) {
			indPlantao = false;
		}
		
		return this.aghuFacade.pesquisarAghHorariosUnidFuncionalCount(unfSeq, tipoDia, hrInicial, hrFinal, indPlantao);
	}

	@Override
	public List<AghHorariosUnidFuncional> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		Short unfSeq = null;
		Boolean indPlantao = null;
		List<AghHorariosUnidFuncional> retorno;
		
		if (this.getUnidadeFuncional() != null) {
			unfSeq = this.getUnidadeFuncional().getSeq();
		}
		
		if (this.getPlantao() != null && this.getPlantao().equals(DominioSimNao.S)) {
			indPlantao = true;
			
		} else if (this.getPlantao() != null && this.getPlantao().equals(DominioSimNao.N)) {
			indPlantao = false;
		}	
		
		retorno = this.aghuFacade.pesquisarAghHorariosUnidFuncional(unfSeq, tipoDia, hrInicial, hrFinal, indPlantao, firstResult, maxResult, orderProperty, asc);
		sortList(retorno);
		
		return retorno; 
	}
	
	/**
	 * Método de pesquisa suggestionBox
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.aghuFacade.pesquisarUnidadesPorCodigoDescricao(param, true);
	}
	
	private void sortList(List<AghHorariosUnidFuncional> list) {
		
		Collections.sort(list, new Comparator<AghHorariosUnidFuncional>() {
			
			@Override
			public int compare(AghHorariosUnidFuncional c1, AghHorariosUnidFuncional c2) {
				int order1 = c1.getUnidadeFuncional().getDescricao().compareTo(c2.getUnidadeFuncional().getDescricao()); 
				int order2 = c1.getId().getTipoDia().getCodigoNumerico().compareTo(c2.getId().getTipoDia().getCodigoNumerico());
				return order1 !=0 ? order1 : order2;
			}	
			
		});
		
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public DominioTipoDia getTipoDia() {
		return tipoDia;
	}

	public void setTipoDia(DominioTipoDia tipoDia) {
		this.tipoDia = tipoDia;
	}

	public Date getHrInicial() {
		return hrInicial;
	}

	public void setHrInicial(Date hrInicial) {
		this.hrInicial = hrInicial;
	}

	public Date getHrFinal() {
		return hrFinal;
	}

	public void setHrFinal(Date hrFinal) {
		this.hrFinal = hrFinal;
	}

	public DominioSimNao getPlantao() {
		return plantao;
	}

	public void setPlantao(DominioSimNao plantao) {
		this.plantao = plantao;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(
			List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public AghHorariosUnidFuncional getHorariosUnidFuncional() {
		return horariosUnidFuncional;
	}

	public void setHorariosUnidFuncional(
			AghHorariosUnidFuncional horariosUnidFuncional) {
		this.horariosUnidFuncional = horariosUnidFuncional;
	}

	public DominioTipoDia getTipoDiaP() {
		return tipoDiaP;
	}

	public void setTipoDiaP(DominioTipoDia tipoDiaP) {
		this.tipoDiaP = tipoDiaP;
	}

	public Short getUnfSeqP() {
		return unfSeqP;
	}

	public void setUnfSeqP(Short unfSeqP) {
		this.unfSeqP = unfSeqP;
	}

	public String getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(String horaInicial) {
		this.horaInicial = horaInicial;
	}

	public DynamicDataModel<AghHorariosUnidFuncional> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AghHorariosUnidFuncional> dataModel) {
		this.dataModel = dataModel;
	}

	public AghHorariosUnidFuncional getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AghHorariosUnidFuncional selecionado) {
		this.selecionado = selecionado;
	}
}
