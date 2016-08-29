package br.gov.mec.aghu.prescricaomedica.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.VisualizaDadosSolicitacaoConsultoriaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #1000 Prescrição: Visualizar Dados da Solicitação de Consultoria
 * 
 * @author aghu
 *
 */
public class VisualizaDadosSolicitacaoConsultoriaController extends ActionController {

	private static final long serialVersionUID = 2047183264021757148L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	/**
	 * Parâmetros de conversação
	 */
	private Integer atdSeq;
	private Integer scnSeq;
	private boolean habilitarBotaoResponder;
	private String voltarPara;
	
	private static final String PAGE_VISUALIZAR_HISTORICO_DIAGNOSTICO = "prescricaomedica-manterDiagnosticosPaciente";
	
	private static final String PAGE_CADASTRO_RESPOSTAS_CONSULTORIA = "cadastroRespostasConsultoria";
	
	private VisualizaDadosSolicitacaoConsultoriaVO vo;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
		
		if(getRequestParameter("atdSeq") != null){
			atdSeq = Integer.valueOf(getRequestParameter("atdSeq"));
		}
		
		if(getRequestParameter("scnSeq") != null){
			scnSeq = Integer.valueOf(getRequestParameter("scnSeq"));
		}

		if (atdSeq == null || scnSeq == null) {
			throw new IllegalArgumentException();
		}
		try {
			this.vo = this.prescricaoMedicaFacade.obterDadosSolicitacaoConsultoria(this.atdSeq, this.scnSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Cancelar
	 * 
	 * @return
	 */
	public String cancelar() {
		String retorno = this.voltarPara;
		this.atdSeq = null;
		this.scnSeq = null;
		this.habilitarBotaoResponder = false;
		this.vo = null;
		this.voltarPara= null;
		return retorno;
	}
	
	public String redirecionarCadastroRespostasConsultorias(){
		return PAGE_CADASTRO_RESPOSTAS_CONSULTORIA;
	}

	public String redirecionarDiagnosticosPacientes() {
		return PAGE_VISUALIZAR_HISTORICO_DIAGNOSTICO;
	}
	
	/*
	 * Getters and Setters
	 */

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getScnSeq() {
		return scnSeq;
	}

	public void setScnSeq(Integer scnSeq) {
		this.scnSeq = scnSeq;
	}

	public boolean isHabilitarBotaoResponder() {
		return habilitarBotaoResponder;
	}

	public void setHabilitarBotaoResponder(boolean habilitarBotaoResponder) {
		this.habilitarBotaoResponder = habilitarBotaoResponder;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public VisualizaDadosSolicitacaoConsultoriaVO getVo() {
		return vo;
	}

	public void setVo(VisualizaDadosSolicitacaoConsultoriaVO vo) {
		this.vo = vo;
	}

}
