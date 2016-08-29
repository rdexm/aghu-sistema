package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsExameComponenteVisualPrescricao;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarExamesDaHemoterapiaController extends ActionController implements ActionPaginator {


	@Inject @Paginator
	private DynamicDataModel<AbsProcedHemoterapico> dataModel;


	private static final long serialVersionUID = 5980768760902420585L;

	private static final String PAGE_CRUD = "manterExamesDaHemoterapia";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;

	private AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao = new AbsExameComponenteVisualPrescricao();
	private AbsExameComponenteVisualPrescricao itemSelecionado;
	
	private String voltarPara;
	
	private Integer seq;

	private AelCampoLaudo campoLaudo;
	
	//paramentro que vai vir de #6384
	private String pesquisaComponenteSaguineo;
	
	private AbsComponenteSanguineo componenteSanguineoSB;
	private String manterComponentesSanguineos = "manterComponentesSanguineos";
	private AbsProcedHemoterapico procedimento;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}	
	
	public void inicio(){
	 

		if(absExameComponenteVisualPrescricao.getComponenteSanguineo() == null && StringUtils.isNotBlank(pesquisaComponenteSaguineo)){
			this.absExameComponenteVisualPrescricao.setComponenteSanguineo(pesquisarComponenteSanguineoUnico(this.pesquisaComponenteSaguineo));		
			dataModel.reiniciarPaginator();
		}
	
	}
	
	public void pesquisar(){
		if(this.seq != null){
			this.absExameComponenteVisualPrescricao.setSeq(seq);
		}
		if(this.campoLaudo != null){
			this.absExameComponenteVisualPrescricao.setCampoLaudo(campoLaudo);
		}
		
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){			
		this.seq = null;
		this.campoLaudo = null;
		
		this.absExameComponenteVisualPrescricao = new AbsExameComponenteVisualPrescricao();
		dataModel.limparPesquisa();
	}
	
	public void excluir() {
		seq = itemSelecionado.getSeq();		
		this.bancoDeSangueFacade.excluirAbsExameComponenteVisualPrescricao(seq);
		apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_EXAME_DA_HEMOTERAPIA", itemSelecionado.getCampoLaudo().getNome());
		dataModel.reiniciarPaginator();
		itemSelecionado = null;	
		seq = null;
	}
	
	public String editar(){
		return PAGE_CRUD;
	}
		
	
	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.bancoDeSangueFacade.listaExameComponenteVisualPrescricao(firstResult, maxResult, orderProperty, asc, this.absExameComponenteVisualPrescricao);
	}

	@Override
	public Long recuperarCount() {		
		return this.bancoDeSangueFacade.listaExameComponenteVisualPrescricaoCount(this.absExameComponenteVisualPrescricao);
	}	

	public String novoExamesHemoterapia(){
		return PAGE_CRUD;
	}
	
	public String voltar(){
		limpar();
		pesquisaComponenteSaguineo = null;
		return voltarPara;
	}

	
	//suggestion - componente saguineo - passado por parametro
	public AbsComponenteSanguineo pesquisarComponenteSanguineoUnico(String param) {
		return this.bancoDeSangueFacade.obterComponenteSanguineoUnico(param);
	}

	//suggestion - componente saguineo
	public List<AbsComponenteSanguineo> pesquisarComponenteSanguineo(String param) {
		return  this.bancoDeSangueFacade.obterComponenteSanguineos(param);
	}

	// suggestion - laudo
	public List<AelCampoLaudo> pesquisarLaudo(String param) {
		return  this.returnSGWithCount(this.examesFacade.obterLaudo(param),pesquisarLaudoCount(param));
	}
	
	public Long pesquisarLaudoCount(String param) {
		return this.examesFacade.pesquisarLaudoCount(param);
	} 	
	public String mantercomponentesSanguineos(){
		return this.manterComponentesSanguineos;
	}
	
	
	public AbsExameComponenteVisualPrescricao getAbsExameComponenteVisualPrescricao() {
		return absExameComponenteVisualPrescricao;
	}

	public void setAbsExameComponenteVisualPrescricao(
			AbsExameComponenteVisualPrescricao absExameComponenteVisualPrescricao) {
		this.absExameComponenteVisualPrescricao = absExameComponenteVisualPrescricao;
	}
	
	public String getManterExames() {
		return PAGE_CRUD;
	}

	public String getManterComponentesSanguineos() {
		return manterComponentesSanguineos;
	}

	public void setManterComponentesSanguineos(String manterComponentesSanguineos) {
		this.manterComponentesSanguineos = manterComponentesSanguineos;
	}

	public AbsComponenteSanguineo getComponenteSanguineoSB() {
		return componenteSanguineoSB;
	}

	public void setComponenteSanguineoSB( AbsComponenteSanguineo componenteSanguineoSB) {
		this.componenteSanguineoSB = componenteSanguineoSB;
	}
	
	public String getPesquisaComponenteSaguineo() {
		return pesquisaComponenteSaguineo;
	}

	public void setPesquisaComponenteSaguineo(String pesquisaComponenteSaguineo) {
		this.pesquisaComponenteSaguineo = pesquisaComponenteSaguineo;
	} 

	public List<AbsProcedHemoterapico> pesquisarProcedimentos(Object param){
		return bancoDeSangueFacade.pesquisarProcedimentoHemoterapicoPorCodigoDescricao(param);
	} 

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}

	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}

	public AbsProcedHemoterapico getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(AbsProcedHemoterapico procedimento) {
		this.procedimento = procedimento;
	}
	
	
	 


	public DynamicDataModel<AbsProcedHemoterapico> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AbsProcedHemoterapico> dataModel) {
	 this.dataModel = dataModel;
	}

	public AbsExameComponenteVisualPrescricao getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(
			AbsExameComponenteVisualPrescricao itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	} 

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
