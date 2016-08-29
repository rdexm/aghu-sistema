package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class EquipamentoAssocProcedDiagTerapController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -4633745570402162093L;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	private static final String PDT_LIST = "procedimentoDiagnosticoTerapeuticoList";
	
	private PdtProcDiagTerap procDiagTerap; 
	
	private List<PdtEquipPorProc> equipsPorProc; 
	
	private PdtEquipamento equipamento; 
	
	private List<PdtEquipamento> equipamentos; 	
	
	private Integer dptSeq;
	
	public void inicio() {
	 
		if (dptSeq == null){//Inclusão
			procDiagTerap = new PdtProcDiagTerap();
			this.equipamentos = new ArrayList<PdtEquipamento>();
		}else{ //Edição                                    
			procDiagTerap = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerap(dptSeq);
			
			processarListaPdtEquipamentos();
		}
	
	}
	
	private void processarListaPdtEquipamentos() {
		equipsPorProc = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtEquipPorProcPorDptSeq(dptSeq);
		blocoCirurgicoCadastroApoioFacade.refreshPdtEquipPorProc(equipsPorProc);
		this.equipamentos = new ArrayList<PdtEquipamento>();
		for(PdtEquipPorProc equipePorProc: equipsPorProc){
			this.equipamentos.add(equipePorProc.getPdtEquipamento());	
		}
		CoreUtil.ordenarLista(equipsPorProc, PdtEquipPorProc.Fields.PDT_EQUIPAMENTOS.toString() + "." +PdtEquipamento.Fields.DESCRICAO.toString(), Boolean.TRUE);
	}
	
	public void salvar(Short deqSeq) throws ApplicationBusinessException {

			blocoCirurgicoCadastroApoioFacade.persistirPdtEquipPorProc(equipamento, procDiagTerap);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_CRIACAO_EQUIPAMENTO_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO", this.equipamento.getDescricao());
			
			this.setEquipamento(null);
			processarListaPdtEquipamentos();
	}

	public String voltar(){
		procDiagTerap = new PdtProcDiagTerap();
		this.setEquipamento(null);
		return PDT_LIST;
	}
	
	public void removerEquipamento(PdtEquipPorProc equipPorProcSendoExcluido){
		String mensagemSucesso = blocoCirurgicoCadastroApoioFacade.removerPdtEquipPorProc(equipPorProcSendoExcluido);
		this.apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		processarListaPdtEquipamentos();
	}
	
	public void limparEquipamento() {
		this.setDptSeq(null);
		this.setEquipamento(null);
		this.equipamento = null;				
	}
	
	public void adicionarEquipamento() throws ApplicationBusinessException {
		if(validarInclusaoEquipamento()){
			this.salvar(this.equipamento.getSeq());
		}
	}
	
	private boolean validarInclusaoEquipamento() {
		if (this.equipamento == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_EQUIPAMENTO_NAO_ASSOCIADO");
		}else{
			if(equipamentos.contains(equipamento)){
				this.apresentarMsgNegocio(Severity.ERROR, "EQUIPAMENTO_JA_ASSOCIADO");
			}else{
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public List<PdtEquipamento> pesquisarEquipamento(String nome) {
		return this.returnSGWithCount(blocoCirurgicoProcDiagTerapFacade.pesquisarEquipamentoPorNome(nome),pesquisarEquipamentoCount(nome));
	}
	
	public Long pesquisarEquipamentoCount(String nome) {
		return blocoCirurgicoProcDiagTerapFacade.pesquisarEquipamentoPorNomeCount(nome);
	}
	
	public boolean isMostrarLinkExcluirEquipamento(){
		return this.equipamento != null && this.equipamento.getSeq() != null;
	}
	
	//GETTERS e SETTERS

	public PdtProcDiagTerap getProcDiagTerap() {
		return this.procDiagTerap;
	}
	
	public void setProcDiagTerap(final PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}	
	
	public void setEquipamento(final PdtEquipamento equipamento) {
		this.equipamento = equipamento;
	}

	public PdtEquipamento getEquipamento() {
		return equipamento;
	}

	public void setEquipamentos(final List<PdtEquipamento> equipamentos) {
		this.equipamentos = equipamentos;
	}

	public List<PdtEquipamento> getEquipamentos() {
		return equipamentos;
	}	
	
	public Integer getDptSeq() {
		return dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	public List<PdtEquipPorProc> getEquipsPorProc() {
		return equipsPorProc;
	}

	public void setEquipsPorProc(List<PdtEquipPorProc> equipsPorProc) {
		this.equipsPorProc = equipsPorProc;
	}

}
