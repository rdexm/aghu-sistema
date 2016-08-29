package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class TecnicaAssocProcedDiagTerapController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2920222675936662602L;

	private static final String PDT_LIST = "procedimentoDiagnosticoTerapeuticoList";
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	private PdtProcDiagTerap procDiagTerap; 
	
	private List<PdtTecnicaPorProc> tecnicasPorProc; 
	
	private PdtTecnica tecnica; 
	
	private List<PdtTecnica> tecnicas; 	
	
	private Integer dptSeq;
	
	
	public void inicio() {
		if (dptSeq == null){//Inclusão
			procDiagTerap = new PdtProcDiagTerap();
			this.tecnicas = new ArrayList<PdtTecnica>();
		}else{ //Edição                                    
			procDiagTerap = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerap(dptSeq);
			
			processarListaPdtTecnicas();
		}
	
	}
	

	private void processarListaPdtTecnicas() {
		                                                           
		tecnicasPorProc = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtTecnicaPorProcPorDptSeq(dptSeq);
		blocoCirurgicoProcDiagTerapFacade.refreshPdtTecnicaPorProc(tecnicasPorProc);
		this.tecnicas = new ArrayList<PdtTecnica>();
		for(PdtTecnicaPorProc tecnicaPorProc: tecnicasPorProc){//
			this.tecnicas.add(tecnicaPorProc.getPdtTecnica());//	
		}
	}
	
	public void salvar(Integer dteSeq) throws ApplicationBusinessException {

		String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.persistirPdtTecnicaPorProc(tecnica, procDiagTerap);
		this.apresentarMsgNegocio(Severity.INFO, mensagemSucesso, this.tecnica.getDescricao());
		this.setTecnica(null);
		processarListaPdtTecnicas();
	}

	public String voltar(){
		procDiagTerap = new PdtProcDiagTerap();
		this.setTecnica(null);
		return PDT_LIST;
	}
	
	public void removerTecnica(PdtTecnicaPorProc tecnicaPorProcSendoExcluido){
		String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.removerPdtTecnicaPorProc(tecnicaPorProcSendoExcluido);
		this.apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		processarListaPdtTecnicas();
	}
	
	public void limparTecnica() {
		this.setDptSeq(null);
		this.setTecnica(null);
		this.tecnica = null;				
	}
	
	public void adicionarTecnica() throws ApplicationBusinessException {
		if(validarInclusaoTecnica()){
			this.salvar(this.tecnica.getSeq());
		}
	}
	
	private boolean validarInclusaoTecnica() {
		if (this.tecnica == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_TECNICA_NAO_ASSOCIADA");
		}else{
			if(tecnicas.contains(tecnica)){
				this.apresentarMsgNegocio(Severity.ERROR, "TECNICA_JA_ASSOCIADA");
			}else{
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public List<PdtTecnica> pesquisarTecnica(String strPesquisa) {
		return blocoCirurgicoProcDiagTerapFacade.pesquisarTecnicaPorDescricaoOuSeq(strPesquisa);
	}
	
	public boolean isMostrarLinkExcluirTecnica(){
		return this.tecnica != null && this.tecnica.getSeq() != null;
	}
	
	//GETTERS e SETTERS

	public PdtProcDiagTerap getProcDiagTerap() {
		return this.procDiagTerap;
	}
	
	public void setProcDiagTerap(final PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}	
	
	public void setTecnica(final PdtTecnica tecnica) {
		this.tecnica = tecnica;
	}

	public PdtTecnica getTecnica() {
		return tecnica;
	}

	public void setTecnicas(final List<PdtTecnica> tecnicas) {
		this.tecnicas = tecnicas;
	}

	public List<PdtTecnica> getTecnicas() {
		return tecnicas;
	}	
	
	public Integer getDptSeq() {
		return dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	public List<PdtTecnicaPorProc> getTecnicasPorProc() {
		return tecnicasPorProc;
	}

	public void setTecnicasPorProc(List<PdtTecnicaPorProc> tecnicasPorProc) {
		this.tecnicasPorProc = tecnicasPorProc;
	}

}
