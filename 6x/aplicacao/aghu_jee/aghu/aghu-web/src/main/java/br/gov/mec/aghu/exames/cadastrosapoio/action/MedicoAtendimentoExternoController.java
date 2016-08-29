package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.FatCbos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MedicoAtendimentoExternoController extends ActionController {

	private static final long serialVersionUID = -8096710363004137996L;

	private static final String MEDICO_ATENDIMENTO_EXTERNO_PESQUISA = "medicoAtendimentoExternoPesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;	
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	private AghMedicoExterno medicoExterno;
	
	//param
	private String voltarPara;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		if(medicoExterno != null && medicoExterno.getSeq() != null) {
			this.medicoExterno = this.examesFacade.obterMedicoExternoPorPK(this.medicoExterno.getSeq());
			
			if(medicoExterno == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		} else {
			this.medicoExterno = new AghMedicoExterno();
		}
		
		return null;
	
	}
	
	public String gravarMedicoExterno() {
		Boolean isSave = Boolean.FALSE;
		
		try {
			isSave = this.medicoExterno.getSeq() == null;
		
			this.cadastrosApoioExamesFacade.saveOrUpdateMedicoExterno(this.medicoExterno);
			
			if(isSave) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MEDICO_EXTERNO", this.medicoExterno.getNome());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MEDICO_EXTERNO", this.medicoExterno.getNome());
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
			
		} catch(PersistenceException e) {
			if(isSave) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_INCLUSAO_MEDICO_EXTERNO", this.medicoExterno.getNome());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_MEDICO_EXTERNO", this.medicoExterno.getNome());
			}
			return null;
		}
	}
	
	public String cancelar() {		
		if(voltarPara != null){
			return voltarPara;
		}
		this.medicoExterno = null;
		return MEDICO_ATENDIMENTO_EXTERNO_PESQUISA;
	}
	
	public void limpar() {
		this.medicoExterno = new AghMedicoExterno();
	}
	
	public List<FatCbos> listarCbos(String objPesquisa){
		try {
			return this.returnSGWithCount(this.faturamentoFacade.listarCbosAtivos(objPesquisa),listarCbosAtivosCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return new ArrayList<FatCbos>();
	}

	public Long listarCbosAtivosCount(String objPesquisa){
		return this.faturamentoFacade.listarCbosAtivosCount(objPesquisa);
	}
	

	// getters and setters
	public AghMedicoExterno getMedicoExterno() {
		return medicoExterno;
	}

	public void setMedicoExterno(AghMedicoExterno medicoExterno) {
		this.medicoExterno = medicoExterno;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}