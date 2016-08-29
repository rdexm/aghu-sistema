package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioProcedimentoTerapeuticoOperatorio;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtInstrDesc;
import br.gov.mec.aghu.model.PdtInstrDescId;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.model.PdtMedicDesc;
import br.gov.mec.aghu.model.PdtMedicDescId;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtProcId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 *
 * Controller para a aba Procedimento (Procedimento Diagnóstico Terapêutico).
 * 
 * @author eschweigert
 *
 */

@SuppressWarnings("PMD.AghuTooManyMethods")
public class DescricaoProcDiagTerapProcedController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapProcedController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 928717147110522092L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	
	private DescricaoProcDiagTerapVO descricaoProcDiagTerapVO;
	private MbcCirurgias cirurgia;

	private RapServidores servidorLogado;
	
	// Parametros relacionados a cirurgia selecionada previamente
	private AghUnidadesFuncionais unidadeFuncionalCrg;

	private PdtDadoDesc dadoDesc;
	
	// *************************************************************************************************
	// Procedimento Diagnostico Terapêutico
	private PdtProcDiagTerap procDiagTerap;
	private DominioIndContaminacao contaminacao;
	private String complemento;
	private Integer ddtSeqExc;
	private Short seqpExc;
	private List<PdtProc> procs;
	
	
	// *************************************************************************************************
	// Equipamentos e Técnica
	private PdtInstrumental instrumental;
	private Integer pinSeqExc;
	private List<PdtInstrDesc> instrumentos;
	private Integer nroFilme;
	
	// *************************************************************************************************
	// Técnica Anestésica
	private boolean sedacao;
	private AfaMedicamento medicamentoUsual;
	private AfaMedicamento medicamento;
	private PdtMedicDesc descricaoMedicacao;
	
	private List<PdtMedicDesc> descricoesMedicamentos;
	
	// *************************************************************************************************
	// Outras Informações
	private boolean encaminhaMatExame;
	private boolean encaminhamentoSolicitacao;

	private String material;
	private Integer atdSeq;

	private boolean alterouFirstPdtProc;
	private boolean errorSaveProcDiagTerap;

	private final String PAGE_EXAMES_SOLIC_EXAMES ="exames-solicitacaoExameCRUD";
	private final String PAGE_DESCRICAO_PDT = "blococirurgico-descricaoProcDiagTerap";
	
	@Inject
	private DescricaoProcDiagTerapController descricaoProcDiagTerapController;
	
	private PdtProc procedimentoSelecionado;
	
	private final Integer TAB_3=3; 
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
		
		
		try {
			servidorLogado = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
		dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(descricaoProcDiagTerapVO.getDdtSeq());

		populaProcs();
		pesquisarInstrumentos();
		pesquisarDescricoesMedicamentos();
		descricaoMedicacao = new PdtMedicDesc();
		
		cirurgia = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(descricaoProcDiagTerapVO.getDdtCrgSeq(),
				new Enum[] {MbcCirurgias.Fields.CONVENIO_SAUDE_PLANO},new Enum[] {MbcCirurgias.Fields.ATENDIMENTO});
		
		final Integer atdSeq = cirurgia.getAtendimento() != null ? cirurgia.getAtendimento().getSeq() : null;
		encaminhamentoSolicitacao = blocoCirurgicoFacade.habilitaEncaminhamentoExame(cirurgia.getOrigemPacienteCirurgia(), cirurgia.getPaciente().getCodigo(), atdSeq);
		
		if(encaminhamentoSolicitacao){
			material = blocoCirurgicoFacade.buscaDescricaoMaterialExame(cirurgia.getOrigemPacienteCirurgia(), cirurgia.getPaciente().getCodigo(), atdSeq);
			encaminhaMatExame = material != null;
			encaminhamentoSolicitacao = (material == null);
		}
	}
	
	
	// *************************************************************************************************
	// Procedimento Diagnostico Terapêutico

	public List<PdtProcDiagTerap> pesquisarProcedimentoDiagnosticoTerapeutico(String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapAtivaPorDescricao(strPesquisa),listarProcDiagTerapAtivaPorDescricaoCount(strPesquisa));
	}
	
	public Long listarProcDiagTerapAtivaPorDescricaoCount(String strPesquisa) {
		return blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapAtivaPorDescricaoCount(strPesquisa);
	}
	
	public void posSelectionProcDiagTerap(){
		contaminacao = null;
		complemento = null;
		
		if(procDiagTerap != null){
			contaminacao = procDiagTerap.getContaminacao();
		}
	}

	public void populaProcs(){
		procs = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(descricaoProcDiagTerapVO.getDdtSeq());
	}

	public void salvarProcDiagTerap(){
		try {
			if(procDiagTerap != null){
				contaminacao = procDiagTerap.getContaminacao();
				alterouFirstPdtProc = (procs == null || procs.isEmpty());
				
				MbcProcedimentoCirurgicos procedimentoCirurgico = procDiagTerap.getProcedimentoCirurgico();
				
				if (procedimentoCirurgico != null) {
					blocoCirurgicoProcDiagTerapFacade.validarContaminacaoProcedimentoCirurgicoPdt(procedimentoCirurgico.getSeq(), contaminacao);	
				}
				
				final PdtProc proc = new PdtProc();
				
				proc.setId(new PdtProcId(descricaoProcDiagTerapVO.getDdtSeq(), null));
				proc.setComplemento(complemento);
				proc.setIndContaminacao(contaminacao);
				proc.setPdtProcDiagTerap(procDiagTerap);
				
				blocoCirurgicoProcDiagTerapFacade.persistirPdtProc(proc);
	
				procDiagTerap = null;
				contaminacao = null;
				complemento = null;
				errorSaveProcDiagTerap = false;
				populaProcs();
				
			} else {
				errorSaveProcDiagTerap = true;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		descricaoProcDiagTerapController.posSalvarProcDiagTerap();
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void atualizarProcedimento(PdtProc procedimento){
		try {
				contaminacao = procedimento.getIndContaminacao();
				alterouFirstPdtProc = (procs == null || procs.isEmpty());
				
				MbcProcedimentoCirurgicos procedimentoCirurgico = procedimento.getPdtProcDiagTerap().getProcedimentoCirurgico();
				
				if (procedimentoCirurgico != null) {
					blocoCirurgicoProcDiagTerapFacade.validarContaminacaoProcedimentoCirurgicoPdt(procedimentoCirurgico.getSeq(), contaminacao);	
				}
				blocoCirurgicoProcDiagTerapFacade.persistirPdtProc(procedimento);
				procDiagTerap = null;
				errorSaveProcDiagTerap = false;
				populaProcs();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		descricaoProcDiagTerapController.posSalvarProcDiagTerap();
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void atualizarComplemento(PdtProc procedimento){
		try {
			this.procedimentoSelecionado = procedimento;
			blocoCirurgicoProcDiagTerapFacade.persistirPdtProc(procedimento);
			procDiagTerap = null;
			errorSaveProcDiagTerap = false;
			populaProcs();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void atualizarComplementoPelaModal(){
		try {
			blocoCirurgicoProcDiagTerapFacade.persistirPdtProc(procedimentoSelecionado);			
			procDiagTerap = null;
			errorSaveProcDiagTerap = false;
			populaProcs();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
			descricaoProcDiagTerapController.setAbaSelecionada(TAB_3);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	public void excluirProc(){
		final PdtProc proc = blocoCirurgicoProcDiagTerapFacade.obterPdtProcPorChavePrimaria(new PdtProcId(ddtSeqExc, seqpExc));
		blocoCirurgicoProcDiagTerapFacade.excluirPdtProc(proc);

		//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_PROCEDIMENTO_TERAPEUTICO_EXCLUSAO_SUCESSO",proc.getPdtProcDiagTerap().getDescricao());
		
		// Caso tenha sido excluido todos os itens, 
		// deve limpar campos que são dependentes do primeiro registro
		if(!procs.isEmpty()){
			if(procs.get(0).getId().getDdtSeq().equals(ddtSeqExc)){
				dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(descricaoProcDiagTerapVO.getDdtSeq());
				
				dadoDesc.setPdtTecnica(null);
				dadoDesc.setDteSeq(null);
				
				dadoDesc.setPdtEquipamento(null);
				dadoDesc.setDeqSeq(null);
				
				
				dadoDesc.setNroFilme(null);
				atualizarPdtDadoDesc();
				alterouFirstPdtProc = true;
			}
		}
		
		populaProcs();
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}

	
	// *************************************************************************************************
	// Equipamentos e Técnica

	public void atualizarPdtDadoDesc(PdtDadoDesc dadoDesc){
		this.dadoDesc = dadoDesc;
		atualizarPdtDadoDesc();
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void atualizarPdtDadoDesc(){
		try {
			
			blocoCirurgicoProcDiagTerapFacade.atualizarDadoDesc(dadoDesc);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void pesquisarInstrumentos(){
		instrumentos = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtInstrDescPorDdtSeq(descricaoProcDiagTerapVO.getDdtSeq());
	}
	
	public List<PdtInstrumental> pesquisarInstrumentos(String strPesquisa){
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.pesquisarPdtInstrumental(strPesquisa, descricaoProcDiagTerapVO.getDdtSeq().shortValue()) , 
				blocoCirurgicoCadastroApoioFacade.pesquisarPdtInstrumentalCount(strPesquisa, descricaoProcDiagTerapVO.getDdtSeq().shortValue()));
	}

	public void salvarInstrumento(){
		try {
			final PdtInstrDesc instrDesc = new PdtInstrDesc();
			instrDesc.setId(new PdtInstrDescId(descricaoProcDiagTerapVO.getDdtSeq(), instrumental.getSeq()));
			instrDesc.setPdtInstrumental(instrumental);
			
			blocoCirurgicoProcDiagTerapFacade.inserirPdtInstrDesc(instrDesc);
			
			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSTRUMENTAL_INSERCAO_SUCESSO",instrDesc.getPdtInstrumental().getDescricao());
			instrumental = null;
			pesquisarInstrumentos();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirInstrumento(){
		try {
			final PdtInstrDesc instrDesc = blocoCirurgicoProcDiagTerapFacade.obterPdtInstrDescPorChavePrimaria(new PdtInstrDescId(ddtSeqExc, pinSeqExc));
			blocoCirurgicoProcDiagTerapFacade.excluirPdtInstrDesc(instrDesc);
			
			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INSTRUMENTAL_EXCLUSAO_SUCESSO",instrDesc.getPdtInstrumental().getDescricao());
			pesquisarInstrumentos();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	
	// *************************************************************************************************
	// Técnica Anestésica
	
	
	public void posSelectionTipoAnestesia(MbcTipoAnestesias tipoAnestesia){
		dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(descricaoProcDiagTerapVO.getDdtSeq());
		dadoDesc.setTempoJejum(null);
		
		if(tipoAnestesia != null){
			dadoDesc.setMbcTipoAnestesias(tipoAnestesia);
			dadoDesc.setTanSeq(tipoAnestesia.getSeq());
		} else {
			dadoDesc.setMbcTipoAnestesias(null);
			dadoDesc.setTanSeq(null);
		}
		
		atualizarPdtDadoDesc();
		descricaoProcDiagTerapController.setAbaSelecionada(TAB_3);
	}

	public void posSelectionSedacao(){
		dadoDesc = blocoCirurgicoProcDiagTerapFacade.obterDadoDescPorChavePrimaria(descricaoProcDiagTerapVO.getDdtSeq());
		dadoDesc.setTempoJejum(null);
		atualizarPdtDadoDesc();
	}

	public List<AfaMedicamento> pesquisarMedicamentosUsuais(String filtro){
		return this.returnSGWithCount(farmaciaFacade.pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(descricaoProcDiagTerapVO.getUnidadeFuncional().getSeq(), filtro, true),
				farmaciaFacade.pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuaisCount(descricaoProcDiagTerapVO.getUnidadeFuncional().getSeq(), filtro, true));
	}
	
	public List<AfaMedicamento> pesquisarMedicamentos(String filtro){
		return this.returnSGWithCount(farmaciaFacade.pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuais(descricaoProcDiagTerapVO.getUnidadeFuncional().getSeq(), filtro, false),
				farmaciaFacade.pesquisarAfaMedicamentosAtivosExistentesEmMedicUsuaisCount(descricaoProcDiagTerapVO.getUnidadeFuncional().getSeq(), filtro, false));
	}
	
	public void posSelectionMedicamentoUsual(){
		geraDescricaoMedicamento(medicamentoUsual);
		medicamento = medicamentoUsual;
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}
	
	public void posSelectionMedicamentos(){
		geraDescricaoMedicamento(medicamento);
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
	}

	private void geraDescricaoMedicamento(AfaMedicamento med) {
		if(med != null){
			descricaoMedicacao = new PdtMedicDesc();
			descricaoMedicacao.setAfaMedicamento(med);
			descricaoMedicacao.setId(new PdtMedicDescId(descricaoProcDiagTerapVO.getDdtSeq(), null));
			descricaoMedicacao.setPreTrans(DominioProcedimentoTerapeuticoOperatorio.PRE);
			
		} else {
			descricaoMedicacao = new PdtMedicDesc();
		}
	}
	
	public void pesquisarDescricoesMedicamentos(){
		descricoesMedicamentos = blocoCirurgicoProcDiagTerapFacade.pesquisarMedicDescPorDdtSeqOrdenadoPorDdtSeqESeqp(descricaoProcDiagTerapVO.getDdtSeq());
	}

	public void salvarDescricaoMedicamento(){
		try {
			if(medicamento != null){
				blocoCirurgicoProcDiagTerapFacade.persistirPdtMedicDesc(descricaoMedicacao);
				//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_MEDICAMENTO_INSERT_SUCESSO",descricaoMedicacao.getAfaMedicamento().getDescricao());
	
				pesquisarDescricoesMedicamentos();
				geraDescricaoMedicamento(null);
				medicamento = null;
				medicamentoUsual = null;
				
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_MEDICAMENTO_DESCRICAO_INVALIDO");				
			}
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluirDescricaoMedicamento(){
		try {
			PdtMedicDesc descMedicacao = blocoCirurgicoProcDiagTerapFacade.obterPdtMedicDescPorChavePrimaria(new PdtMedicDescId(ddtSeqExc, pinSeqExc));
			blocoCirurgicoProcDiagTerapFacade.excluirPdtMedicDesc(descMedicacao);

			//this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_DESCRICAO_MEDICAMENTO_DELETE_SUCESSO",descMedicacao.getAfaMedicamento().getDescricao());

			pesquisarDescricoesMedicamentos();
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	


	// *************************************************************************************************
	// Outras Informações
	
	public String atualizarEncaminhamentoExame(){
		
		if(cirurgia.getAtendimento() != null){
			atdSeq =  cirurgia.getAtendimento().getSeq();
			
		} else {
			atdSeq = blocoCirurgicoFacade.obterSeqAghAtendimentoPorPaciente(cirurgia.getPaciente().getCodigo());
		}
		
		if (atdSeq == null) {
			encaminhaMatExame = false;
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CIRURGIA_REDIRECIONAR_PACIENTE_NAO_ESTA_EM_ATENDIMENTO");
			
		} else {
			solicitacaoExameController.setAtendimentoSeq(atdSeq);
			solicitacaoExameController.setPaginaChamadora(PAGE_DESCRICAO_PDT);
			
			FacesContext.getCurrentInstance().getApplication(). getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null , PAGE_EXAMES_SOLIC_EXAMES);
			
		}
		relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		return null;
	}
	
	public DescricaoProcDiagTerapVO getDescricaoProcDiagTerapVO() {
		return descricaoProcDiagTerapVO;
	}


	public void setDescricaoProcDiagTerapVO(
			DescricaoProcDiagTerapVO descricaoProcDiagTerapVO) {
		this.descricaoProcDiagTerapVO = descricaoProcDiagTerapVO;
	}


	public RapServidores getServidorLogado() {
		return servidorLogado;
	}


	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}


	public AghUnidadesFuncionais getUnidadeFuncionalCrg() {
		return unidadeFuncionalCrg;
	}


	public void setUnidadeFuncionalCrg(AghUnidadesFuncionais unidadeFuncionalCrg) {
		this.unidadeFuncionalCrg = unidadeFuncionalCrg;
	}


	public PdtProcDiagTerap getProcDiagTerap() {
		return procDiagTerap;
	}


	public void setProcDiagTerap(PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}


	public DominioIndContaminacao getContaminacao() {
		return contaminacao;
	}


	public void setContaminacao(DominioIndContaminacao contaminacao) {
		this.contaminacao = contaminacao;
	}


	public String getComplemento() {
		return complemento;
	}


	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public List<PdtProc> getProcs() {
		return procs;
	}

	public void setProcs(List<PdtProc> procs) {
		this.procs = procs;
	}

	public Integer getDdtSeqExc() {
		return ddtSeqExc;
	}

	public void setDdtSeqExc(Integer ddtSeqExc) {
		this.ddtSeqExc = ddtSeqExc;
	}

	public Short getSeqpExc() {
		return seqpExc;
	}

	public void setSeqpExc(Short seqpExc) {
		this.seqpExc = seqpExc;
	}

	public PdtInstrumental getInstrumental() {
		return instrumental;
	}

	public void setInstrumental(PdtInstrumental instrumental) {
		this.instrumental = instrumental;
	}

	public List<PdtInstrDesc> getInstrumentos() {
		return instrumentos;
	}

	public void setInstrumentos(List<PdtInstrDesc> instrumentos) {
		this.instrumentos = instrumentos;
	}

	public Integer getPinSeqExc() {
		return pinSeqExc;
	}

	public void setPinSeqExc(Integer pinSeqExc) {
		this.pinSeqExc = pinSeqExc;
	}

	public Integer getNroFilme() {
		return nroFilme;
	}

	public void setNroFilme(Integer nroFilme) {
		this.nroFilme = nroFilme;
	}

	public boolean isSedacao() {
		return sedacao;
	}

	public void setSedacao(boolean sedacao) {
		this.sedacao = sedacao;
	}

	public AfaMedicamento getMedicamentoUsual() {
		return medicamentoUsual;
	}

	public void setMedicamentoUsual(AfaMedicamento medicamentoUsual) {
		this.medicamentoUsual = medicamentoUsual;
	}

	public PdtMedicDesc getDescricaoMedicacao() {
		return descricaoMedicacao;
	}

	public void setDescricaoMedicacao(PdtMedicDesc descricaoMedicacao) {
		this.descricaoMedicacao = descricaoMedicacao;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public List<PdtMedicDesc> getDescricoesMedicamentos() {
		return descricoesMedicamentos;
	}

	public void setDescricoesMedicamentos(List<PdtMedicDesc> descricoesMedicamentos) {
		this.descricoesMedicamentos = descricoesMedicamentos;
	}


	public boolean isEncaminhaMatExame() {
		return encaminhaMatExame;
	}

	public void setEncaminhaMatExame(boolean encaminhaMatExame) {
		this.encaminhaMatExame = encaminhaMatExame;
	}


	public String getMaterial() {
		return material;
	}


	public void setMaterial(String material) {
		this.material = material;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public boolean isEncaminhamentoSolicitacao() {
		return encaminhamentoSolicitacao;
	}

	public void setEncaminhamentoSolicitacao(boolean encaminhamentoSolicitacao) {
		this.encaminhamentoSolicitacao = encaminhamentoSolicitacao;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}


	public boolean isAlterouFirstPdtProc() {
		return alterouFirstPdtProc;
	}


	public void setAlterouFirstPdtProc(boolean alterouFirstPdtProc) {
		this.alterouFirstPdtProc = alterouFirstPdtProc;
	}


	public PdtDadoDesc getDadoDesc() {
		return dadoDesc;
	}


	public void setDadoDesc(PdtDadoDesc dadoDesc) {
		this.dadoDesc = dadoDesc;
	}


	public boolean isErrorSaveProcDiagTerap() {
		return errorSaveProcDiagTerap;
	}


	public void setErrorSaveProcDiagTerap(boolean errorSaveProcDiagTerap) {
		this.errorSaveProcDiagTerap = errorSaveProcDiagTerap;
	}


	public PdtProc getProcedimentoSelecionado() {
		return procedimentoSelecionado;
	}


	public void setProcedimentoSelecionado(PdtProc procedimentoSelecionado) {
		this.procedimentoSelecionado = procedimentoSelecionado;
	} 
	
	
}