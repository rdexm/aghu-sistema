package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class LaboratorioHemocentroController extends ActionController {

	private static final long serialVersionUID = 6368393482185931473L;

	private static final String LABORATORIO_HEMOCENTRO_PESQUISA = "laboratorioHemocentroPesquisa";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private AelLaboratorioExternos laboratorioExterno;
	private Short convenioId;
	private Byte planoId;

	//param
	private String voltarPara;

	private Enum[] fetchArgsInnerJoin = {AelLaboratorioExternos.Fields.CONVENIO_SAUDE, AelLaboratorioExternos.Fields.CONVENIO_SAUDE_PLANO};
		

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(laboratorioExterno != null && laboratorioExterno.getSeq() != null) {
			laboratorioExterno = cadastrosApoioExamesFacade.obterLaboratorioExternoPorId(laboratorioExterno.getSeq(), fetchArgsInnerJoin, null);
			
			if(laboratorioExterno == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			atribuirPlano();
		} else {
			this.laboratorioExterno = new AelLaboratorioExternos();
		}
		
		return null;
	
	}
	
	public String gravarlaboratorioExterno() {
		Boolean isSave = Boolean.FALSE;
		
		try {
			isSave = this.laboratorioExterno.getSeq() == null;
			

			this.cadastrosApoioExamesFacade.saveOrUpdateLaboratorioHemocentro(this.laboratorioExterno);
			//fazer o flush
			
			if(isSave) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_LABORATORIO_HEMOCENTRO", this.laboratorioExterno.getNome());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_LABORATORIO_HEMOCENTRO", this.laboratorioExterno.getNome());
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch(PersistenceException e) {
			if(isSave) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_INCLUSAO_LABORATORIO_HEMOCENTRO", this.laboratorioExterno.getNome());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_LABORATORIO_HEMOCENTRO", this.laboratorioExterno.getNome());
			}
			return null;
		}
	}
	
	
	public String cancelar() {
		this.laboratorioExterno = null;
		convenioId = null;
		planoId = null;
		
		if(voltarPara != null){
			return voltarPara;
		}
		
		return LABORATORIO_HEMOCENTRO_PESQUISA;
	}
	
	public void limpar() {
		this.laboratorioExterno = new AelLaboratorioExternos();
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}
	
	public void atribuirPlano() {
		if (laboratorioExterno != null && laboratorioExterno.getConvenio() != null) {
			this.convenioId = laboratorioExterno.getConvenio().getId().getCnvCodigo();
			this.planoId = laboratorioExterno.getConvenio().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			laboratorioExterno.setConvenio(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));		
		}
	}

	// getters and setters
	public AelLaboratorioExternos getLaboratorioExterno() {
		return laboratorioExterno;
	}

	public void setLaboratorioExterno(AelLaboratorioExternos laboratorioExterno) {
		this.laboratorioExterno = laboratorioExterno;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}
}