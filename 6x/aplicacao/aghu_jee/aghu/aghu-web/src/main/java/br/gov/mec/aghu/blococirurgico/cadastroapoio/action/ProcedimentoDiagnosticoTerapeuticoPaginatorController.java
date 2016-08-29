package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class ProcedimentoDiagnosticoTerapeuticoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<LinhaReportVO> dataModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5878396916573469841L;
	
	private static final String CIDS_PROCEDIMENTO_LIST = "cidsProcedimentoList";
	private static final String EQUIPAMENTO_PDT_LIST = "equipamentoAssocProcedDiagTerap";
	private static final String TECNICAS_PDT_LIST = "tecnicaAssocProcedDiagTerap";
	
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;		

	private Integer seq;
	
	private String descricao;
		
	private DominioIndContaminacao contaminacao;		
	
	private MbcEspecialidadeProcCirgs especialidadeProcCirgs;
	
	private PdtProcDiagTerap procDiagTerap;
	
	private List<LinhaReportVO> procedimentosDiagnosticoTerapeutico;
	
	private LinhaReportVO especialidade;	
	
	@Inject
	private CidsProcedimentoListController cidsProcedimentoListController;
	
	@Inject
	private EquipamentoAssocProcedDiagTerapController equipamentoAssocProcedDiagTerapController;
	
	@Inject
	private TecnicaAssocProcedDiagTerapController tecnicaAssocProcedDiagTerapController;
	
	public List<LinhaReportVO> pesquisarEspecialidadePorTipoProcCirgs(final String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.pesquisarEspecialidadePorTipoProcCirgs((String) strPesquisa),pesquisarEspecialidadePorTipoProcCirgsCount(strPesquisa));
	}

	public Long pesquisarEspecialidadePorTipoProcCirgsCount(final String strPesquisa) {
		return this.blocoCirurgicoCadastroApoioFacade.pesquisarEspecialidadePorTipoProcCirgsCount((String) strPesquisa);
	}
	
	public void pesquisar(){
		this.dataModel.reiniciarPaginator();		
	}
	
	@Override
	public Long recuperarCount() {	
		return blocoCirurgicoCadastroApoioFacade.
		 pesquisarProcDiagTerapCount(seq, descricao, especialidade == null ? null : especialidade.getNumero4(), contaminacao);
	}

	@Override
	public List<LinhaReportVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return procedimentosDiagnosticoTerapeutico = blocoCirurgicoCadastroApoioFacade.
	 		pesquisarProcDiagTerap(firstResult, maxResult, orderProperty, asc, seq, descricao, 
	 			especialidade == null ? null : especialidade.getNumero4(), contaminacao);
	}	
	
	public String redirecionarCids(Integer seq) {
		cidsProcedimentoListController.setDptSeq(seq);
		return CIDS_PROCEDIMENTO_LIST;
	}
	
	public String redirecionarEquips(Integer seq) {
		equipamentoAssocProcedDiagTerapController.setDptSeq(seq);
		return EQUIPAMENTO_PDT_LIST;
	}
	
	public String redirecionarTecnicas(Integer seq) {
		tecnicaAssocProcedDiagTerapController.setDptSeq(seq);
		return TECNICAS_PDT_LIST;
	}
	
	public void limparPesquisa() {
		setEspecialidade(null);
		setSeq(null);
		setDescricao(null);
		setContaminacao(null);	
		this.dataModel.setPesquisaAtiva(false);
	}	
	
	
	// Getters e Setters

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

//	public List<PdtProcDiagTerap> getProcedimentosDiagnosticoTerapeutico() {
//		return procedimentosDiagnosticoTerapeutico;
//	}
//
//	public void setProcedimentosDiagnosticoTerapeutico(
//			List<PdtProcDiagTerap> procedimentosDiagnosticoTerapeutico) {
//		this.procedimentosDiagnosticoTerapeutico = procedimentosDiagnosticoTerapeutico;
//	}

	public DominioIndContaminacao getContaminacao() {
		return contaminacao;
	}

	public void setContaminacao(DominioIndContaminacao contaminacao) {
		this.contaminacao = contaminacao;
	}

	public MbcEspecialidadeProcCirgs getEspecialidadeProcCirgs() {
		return especialidadeProcCirgs;
	}

	public void setEspecialidadeProcCirgs(
			MbcEspecialidadeProcCirgs especialidadeProcCirgs) {
		this.especialidadeProcCirgs = especialidadeProcCirgs;
	}

	public LinhaReportVO getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(LinhaReportVO especialidade) {
		this.especialidade = especialidade;
	}

	public DominioSituacao[] getSituacaoItens() {
		return DominioSituacao.values();
	}

	public PdtProcDiagTerap getProcDiagTerap() {
		return procDiagTerap;
	}

	public void setProcDiagTerap(PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public List<LinhaReportVO> getProcedimentosDiagnosticoTerapeutico() {
		return procedimentosDiagnosticoTerapeutico;
	}

	public void setProcedimentosDiagnosticoTerapeutico(
			List<LinhaReportVO> procedimentosDiagnosticoTerapeutico) {
		this.procedimentosDiagnosticoTerapeutico = procedimentosDiagnosticoTerapeutico;
	}

	public DynamicDataModel<LinhaReportVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<LinhaReportVO> dataModel) {
	 this.dataModel = dataModel;
	}
}
