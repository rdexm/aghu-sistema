package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.ConsultaDispensacaoMedicamentosVO;
import br.gov.mec.aghu.farmacia.vo.DispensacaoMedicamentosVO;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class MedicamentosDispensacaoPaginatorController extends ActionController implements ActionPaginator{
	
	private static final long serialVersionUID = 648325970734440554L;
 	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
 	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
 	@Inject
 	private PesquisaDispensacaoMdtosPaginatorController pesquisaDispensacaoMdtosPaginatorController;
 	
 	@Inject
 	private MovTriagemDispMdtosPaginatorController movTriagemDispMdtosPaginatorController;
	
	private MpmPrescricaoMedica prescricaoMedica;
	private Integer atdSeqPrescricao;
	private Integer seqPrescricao;
	private Long seqPrescricaoNaoEletronica;
	private ConsultaDispensacaoMedicamentosVO consultaDispensacaoMedicamentos;
	private String urlBtVoltar;
	private Short unfSeq;
	private DispensacaoMedicamentosVO dispensacaoMedicamentosVOSelecionado;

	@Inject @Paginator
	private DynamicDataModel<DispensacaoMedicamentosVO> dataModel;
	
	public void iniciar(){
	 

		//Obtém a prescrição médica pelo ID
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
		this.prescricaoMedica = this.prescricaoMedicaFacade.obterPrescricaoPorId(atdSeqPrescricao, seqPrescricao);
		consultaDispensacaoMedicamentos = farmaciaDispensacaoFacade.preencherVoDispensacaoMedicamentos(prescricaoMedica, seqPrescricaoNaoEletronica);
		if(urlBtVoltar == null || "".equals(urlBtVoltar.trim())){
			urlBtVoltar = "prescricaoSituacaoDispensacaoList";
		}
	
	}
	
	public String encaminharHistorico(){
		pesquisaDispensacaoMdtosPaginatorController.setAtdSeq(consultaDispensacaoMedicamentos.getAtdSeq());
		pesquisaDispensacaoMdtosPaginatorController.setProntuario(consultaDispensacaoMedicamentos.getProntuario());
		pesquisaDispensacaoMdtosPaginatorController.setNomePaciente(consultaDispensacaoMedicamentos.getNome());
		pesquisaDispensacaoMdtosPaginatorController.setUrlBtVoltar("medicamentosSituacaoDispensacaoList");
		pesquisaDispensacaoMdtosPaginatorController.setMatCodigo(dispensacaoMedicamentosVOSelecionado.getMatCodigo());
		pesquisaDispensacaoMdtosPaginatorController.setUnfSeq(dispensacaoMedicamentosVOSelecionado.getSeqUnidadeFuncional());
		pesquisaDispensacaoMdtosPaginatorController.setSeqPresc(seqPrescricao);
		pesquisaDispensacaoMdtosPaginatorController.setExibirBotaoLimpar(false);
		pesquisaDispensacaoMdtosPaginatorController.inicializaPaginaRedirecionada();
		pesquisaDispensacaoMdtosPaginatorController.pesquisar();
		return "pesquisarDispensacaoMdtosList";
	}
	
	public String encaminharMovimento(){
		movTriagemDispMdtosPaginatorController.setAtdSeq(prescricaoMedica.getId().getAtdSeq());
		movTriagemDispMdtosPaginatorController.setSeq(prescricaoMedica.getId().getSeq());
		movTriagemDispMdtosPaginatorController.setUrlBtVoltar("medicamentosSituacaoDispensacaoList");
		movTriagemDispMdtosPaginatorController.setMatCodigo(dispensacaoMedicamentosVOSelecionado.getMatCodigo());
		movTriagemDispMdtosPaginatorController.setExibirBotaoLimpar(false);
		
		return "movimentacaoTriagemDispensacaoMdtosList";
	}
	
	public void limparPesquisa(){
		prescricaoMedica = null;
		atdSeqPrescricao = null;
		seqPrescricao = null;
		seqPrescricaoNaoEletronica = null;
	}
	
	public String voltar(){
		return urlBtVoltar;
	}
		
	public String historico(){
		return "historico";
	}
	
	public String movimento(){
		return "movimento";
	}
	
	@Override
	public Long recuperarCount() {
		return 	farmaciaDispensacaoFacade.pesquisarDispensacaoMdtosCount(prescricaoMedica, unfSeq, seqPrescricaoNaoEletronica);
	}

	@Override
	public List<DispensacaoMedicamentosVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<DispensacaoMedicamentosVO> result = farmaciaDispensacaoFacade.pesquisarDispensacaoMdtos(firstResult, maxResult, orderProperty,
				asc, prescricaoMedica, unfSeq, seqPrescricaoNaoEletronica);
		
		return result;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public Integer getAtdSeqPrescricao() {
		return atdSeqPrescricao;
	}

	public void setAtdSeqPrescricao(Integer atdSeqPrescricao) {
		this.atdSeqPrescricao = atdSeqPrescricao;
	}

	public Integer getSeqPrescricao() {
		return seqPrescricao;
	}

	public void setSeqPrescricao(Integer seqPrescricao) {
		this.seqPrescricao = seqPrescricao;
	}

	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}
	
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public DynamicDataModel<DispensacaoMedicamentosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DispensacaoMedicamentosVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Long getSeqPrescricaoNaoEletronica() {
		return seqPrescricaoNaoEletronica;
	}

	public void setSeqPrescricaoNaoEletronica(Long seqPrescricaoNaoEletronica) {
		this.seqPrescricaoNaoEletronica = seqPrescricaoNaoEletronica;
	}

	public ConsultaDispensacaoMedicamentosVO getConsultaDispensacaoMedicamentos() {
		return consultaDispensacaoMedicamentos;
	}

	public void setConsultaDispensacaoMedicamentos(
			ConsultaDispensacaoMedicamentosVO consultaDispensacaoMedicamentos) {
		this.consultaDispensacaoMedicamentos = consultaDispensacaoMedicamentos;
	}

	public DispensacaoMedicamentosVO getDispensacaoMedicamentosVOSelecionado() {
		return dispensacaoMedicamentosVOSelecionado;
	}

	public void setDispensacaoMedicamentosVOSelecionado(
			DispensacaoMedicamentosVO dispensacaoMedicamentosVOSelecionado) {
		this.dispensacaoMedicamentosVOSelecionado = dispensacaoMedicamentosVOSelecionado;
	}
}