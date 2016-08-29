package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class EquipamentosDiagnosticoTerapeuticoCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 3220507732535804619L;

	private static final String EQUIPAMENTOS_DIAG_LIST = "equipamentosDiagnosticoTerapeuticoList";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private Short seqEquipamento;
	private Boolean equipamentoAtivo;
	private PdtEquipamento equipamento;
	private PdtProcDiagTerap procDiagTerap;
	private PdtInstrumental instrumental;
	private List<PdtEquipPorProc> listPdtEquipPorProc;
	private List<PdtInstrPorEquip> listInstrPorEquip;
	private Boolean botaoGravarProcedimentoDiagnosticoTerapeutico;
	private Boolean botaoGravarInstrumentos;
	private Boolean exibirCamposInclusao;
	
	public void inicio() {
		if(seqEquipamento != null){
			equipamento = blocoCirurgicoProcDiagTerapFacade.obterEquipamentoPorChavePrimaria(seqEquipamento);
			if(equipamento.getIndSituacao().equals(DominioSituacao.A)){
				setEquipamentoAtivo(Boolean.TRUE);
			}else{
				setEquipamentoAtivo(Boolean.FALSE);
			}
			if(procDiagTerap == null){
				botaoGravarProcedimentoDiagnosticoTerapeutico = Boolean.TRUE;
			}
			if(instrumental == null){
				botaoGravarInstrumentos = Boolean.TRUE;
			}
			listPdtEquipPorProc = blocoCirurgicoProcDiagTerapFacade.listarPdtEquipPorProcAtivoPorEquipe(equipamento.getSeq());
			listInstrPorEquip = blocoCirurgicoCadastroApoioFacade.listarPdtInstrPorEquipAtivoPorEquip(equipamento.getSeq());
			exibirCamposInclusao = Boolean.TRUE;
		}else{
			equipamento = new PdtEquipamento();
			listPdtEquipPorProc = new ArrayList<PdtEquipPorProc>();
			listInstrPorEquip = new ArrayList<PdtInstrPorEquip>();
			equipamentoAtivo = Boolean.TRUE;
			exibirCamposInclusao = Boolean.FALSE;
		}
	
	}
	
	
	public String voltar() {
		this.setInstrumental(null);
		this.setProcDiagTerap(null);
		return EQUIPAMENTOS_DIAG_LIST;
	}

	public void gravarEquipamento() {
		if(equipamento != null){
			if(equipamentoAtivo){
				equipamento.setIndSituacao(DominioSituacao.A);
			}else{
				equipamento.setIndSituacao(DominioSituacao.I);
			}
		}
		try {
			String mensagemSucesso = getBlocoCirurgicoCadastroApoioFacade().persistirPdtEquipamento(equipamento);
			exibirMensagemSucessoOperacao(mensagemSucesso);
			exibirCamposInclusao = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarPdtEquipPorProc() {
		try {
			String mensagemSucesso = getBlocoCirurgicoCadastroApoioFacade().persistirPdtEquipPorProc(equipamento, procDiagTerap);
			exibirMensagemSucessoOperacao(mensagemSucesso);
			listPdtEquipPorProc = blocoCirurgicoProcDiagTerapFacade.listarPdtEquipPorProcAtivoPorEquipe(equipamento.getSeq());
			getBlocoCirurgicoCadastroApoioFacade().refreshPdtEquipPorProc(listPdtEquipPorProc);
			procDiagTerap = null;
			botaoGravarProcedimentoDiagnosticoTerapeutico = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarPdtInstrPorEquip() {
		try {
			String mensagemSucesso = getBlocoCirurgicoCadastroApoioFacade().persistirPdtInstrPorEquip(equipamento, instrumental);
			exibirMensagemSucessoOperacao(mensagemSucesso);
			listInstrPorEquip = blocoCirurgicoCadastroApoioFacade.listarPdtInstrPorEquipAtivoPorEquip(equipamento.getSeq());
			getBlocoCirurgicoCadastroApoioFacade().refreshPdtInstrPorEquip(listInstrPorEquip);
			instrumental = null;
			botaoGravarInstrumentos = Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluirProcedimentoDiagnosticoTerapeutico(PdtEquipPorProc procedimentoExcluir) {
		String mensagemSucesso = getBlocoCirurgicoCadastroApoioFacade().removerPdtEquipPorProc(procedimentoExcluir);
		exibirMensagemSucessoOperacao(mensagemSucesso);
		listPdtEquipPorProc = blocoCirurgicoProcDiagTerapFacade.listarPdtEquipPorProcAtivoPorEquipe(equipamento.getSeq());
	}
	
	public void excluirInstrumento(PdtInstrPorEquip instrumentoExcluir) {
		String mensagemSucesso = getBlocoCirurgicoCadastroApoioFacade().removerPdtInstrPorEquip(instrumentoExcluir);
		exibirMensagemSucessoOperacao(mensagemSucesso);
		listInstrPorEquip = blocoCirurgicoCadastroApoioFacade.listarPdtInstrPorEquipAtivoPorEquip(equipamento.getSeq());
	}
	
	public void limparMotivoInativacao(){
		getEquipamento().setMotivoInat(null);
	}

	public List<PdtInstrumental> listarInstrumentos(String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.listarPdtInstrumentalAtivaPorDescricao(strPesquisa),listarInstrumentosCount(strPesquisa));
	}
	public Long listarInstrumentosCount(String strPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.listarPdtInstrumentalAtivaPorDescricaoCount(strPesquisa);
	}
		
	public Long listarProcedimentoDiagnosticoTerapeuticoCount(String strPesquisa) {
		return blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapAtivaPorDescricaoCount(strPesquisa);
	}
	public List<PdtProcDiagTerap> listarProcedimentoDiagnosticoTerapeutico(String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoProcDiagTerapFacade.listarProcDiagTerapAtivaPorDescricao(strPesquisa),listarProcedimentoDiagnosticoTerapeuticoCount(strPesquisa));
	}
	
	private void exibirMensagemSucessoOperacao(String mensagem) {
		this.apresentarMsgNegocio(Severity.INFO, mensagem, equipamento.getDescricao());
	}
	
	public void ativaBotaoGravarProcedimentoDiagnosticoTerapeutico() {
		botaoGravarProcedimentoDiagnosticoTerapeutico = Boolean.FALSE;
	}
	
	public void desativaBotaoGravarProcedimentoDiagnosticoTerapeutico() {
		botaoGravarProcedimentoDiagnosticoTerapeutico = Boolean.TRUE;
	}
	
	public void ativaBotaoGravarInstrumentos() {
		botaoGravarInstrumentos = Boolean.FALSE;
	}
	
	public void desativaBotaoGravarInstrumentos() {
		botaoGravarInstrumentos = Boolean.TRUE;
	}
	
	//Getters and Setters

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setEquipamento(PdtEquipamento equipamento) {
		this.equipamento = equipamento;
	}

	public PdtEquipamento getEquipamento() {
		return equipamento;
	}

	public void setSeqEquipamento(Short seqEquipamento) {
		this.seqEquipamento = seqEquipamento;
	}

	public Short getSeqEquipamento() {
		return seqEquipamento;
	}

	public void setBotaoGravarProcedimentoDiagnosticoTerapeutico(
			Boolean botaoGravarProcedimentoDiagnosticoTerapeutico) {
		this.botaoGravarProcedimentoDiagnosticoTerapeutico = botaoGravarProcedimentoDiagnosticoTerapeutico;
	}

	public Boolean getBotaoGravarProcedimentoDiagnosticoTerapeutico() {
		return botaoGravarProcedimentoDiagnosticoTerapeutico;
	}

	public void setBotaoGravarInstrumentos(Boolean botaoGravarInstrumentos) {
		this.botaoGravarInstrumentos = botaoGravarInstrumentos;
	}

	public Boolean getBotaoGravarInstrumentos() {
		return botaoGravarInstrumentos;
	}

	public void setListPdtEquipPorProc(List<PdtEquipPorProc> listPdtEquipPorProc) {
		this.listPdtEquipPorProc = listPdtEquipPorProc;
	}

	public List<PdtEquipPorProc> getListPdtEquipPorProc() {
		return listPdtEquipPorProc;
	}

	public void setListInstrPorEquip(List<PdtInstrPorEquip> listInstrPorEquip) {
		this.listInstrPorEquip = listInstrPorEquip;
	}

	public List<PdtInstrPorEquip> getListInstrPorEquip() {
		return listInstrPorEquip;
	}

	public void setEquipamentoAtivo(Boolean equipamentoAtivo) {
		this.equipamentoAtivo = equipamentoAtivo;
	}

	public Boolean getEquipamentoAtivo() {
		return equipamentoAtivo;
	}

	public void setProcDiagTerap(PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}

	public PdtProcDiagTerap getProcDiagTerap() {
		return procDiagTerap;
	}

	public void setInstrumental(PdtInstrumental instrumental) {
		this.instrumental = instrumental;
	}

	public PdtInstrumental getInstrumental() {
		return instrumental;
	}

	public void setExibirCamposInclusao(Boolean exibirCamposInclusao) {
		this.exibirCamposInclusao = exibirCamposInclusao;
	}

	public Boolean getExibirCamposInclusao() {
		return exibirCamposInclusao;
	}
}
