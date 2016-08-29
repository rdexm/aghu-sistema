package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class CadCtrlQualidadeController extends ActionController {


	private static final long serialVersionUID = -1691228141864387692L;

	private static final String CADASTRO_CONTROLE_QUALIDADE_LIST = "cadastroControleQualidadeList";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	private AelCadCtrlQualidades aelCadCtrlQualidade;

	private Short convenioId;
	private Byte planoId;
	
	//param
	private String voltarPara;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if(aelCadCtrlQualidade != null && aelCadCtrlQualidade.getSeq() != null) {
			this.aelCadCtrlQualidade = this.cadastrosApoioExamesFacade.obterAelCadCtrlQualidadesPorId(aelCadCtrlQualidade.getSeq());

			if(aelCadCtrlQualidade == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			atribuirPlano();
			
		} else {
			this.aelCadCtrlQualidade = new AelCadCtrlQualidades();
		}
		
		return null;
	
	}
	
	public String gravar() {
		try {
			
			boolean isUpdate = aelCadCtrlQualidade.getSeq() != null;
			this.cadastrosApoioExamesFacade.persistirAelCadCtrlQualidades(aelCadCtrlQualidade);
			
			if(isUpdate) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_AEL_CAD_CTRL_QA", this.aelCadCtrlQualidade.getMaterial());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_AEL_CAD_CTRL_QA", this.aelCadCtrlQualidade.getMaterial());
			}
			
			return cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}
	
	public void atribuirPlano() {
		if (aelCadCtrlQualidade != null && aelCadCtrlQualidade.getConvenioSaudePlano() != null) {
			this.convenioId = aelCadCtrlQualidade.getConvenioSaudePlano().getId().getCnvCodigo();
			this.planoId = aelCadCtrlQualidade.getConvenioSaudePlano().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			aelCadCtrlQualidade.setConvenioSaudePlano(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));		
		}
	}
	
	public String cancelar() {
		aelCadCtrlQualidade = null;
		convenioId = null;
		planoId = null;
		if(voltarPara != null) {
			return voltarPara;
		}
		
		return CADASTRO_CONTROLE_QUALIDADE_LIST;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelCadCtrlQualidades getAelCadCtrlQualidade() {
		return aelCadCtrlQualidade;
	}

	public void setAelCadCtrlQualidade(AelCadCtrlQualidades aelCadCtrlQualidade) {
		this.aelCadCtrlQualidade = aelCadCtrlQualidade;
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
