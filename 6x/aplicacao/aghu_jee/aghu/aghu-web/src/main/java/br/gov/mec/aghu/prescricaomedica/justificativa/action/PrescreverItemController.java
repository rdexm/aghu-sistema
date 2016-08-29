package br.gov.mec.aghu.prescricaomedica.justificativa.action;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.dominio.DominioTelaPrescreverItemMdto;
import br.gov.mec.aghu.prescricaomedica.action.ManterPrescricaoMedicaController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificarDadosItensJustificativaPrescricaoVO;

public class PrescreverItemController extends ActionController {

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 2369093725254275814L;

	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private ManterPrescricaoMedicaController manterPrescricaoMedicaController;

	@Inject
	private CadastroSinamController cadastroSinamController;

	@Inject
	private AtualizaJustificativaUsoMedicamentoController atualizaJustificativaUsoMedicamentoController;

	@Inject
	private CadastroJustificativaMedicamentoNaoSelecionadoController cadastroJustificativaMedicamentoNaoSelecionadoController;

	@Inject
	private CadastroJustificativaMedicamentoUsoNsAntimicrobianoController cadastroJustificativaMedicamentoUsoNsAntimicrobianoController;

	@Inject
	private CadastroJustificativaMedicamentoUsoQuimioterapicoController cadastroJustificativaMedicamentoUsoQuimioterapicoController;

	@Inject
	private CadastroJustificativaMedicamentoUsoRestritoController cadastroJustificativaMedicamentoUsoRestritoController;

	@Inject
	private CadastroJustificativaMedicamentoUsoRestritoAntimicrobianoController cadastroJustificativaMedicamentoUsoRestritoAntimicrobianoController;

	private PrescricaoMedicaVO prescricaoMedicaVO;

	private DominioTelaPrescreverItemMdto telaAnterior;
	
	private boolean exibirBotaoRetroceder = false;

	private Map<Integer, DominioTelaPrescreverItemMdto> telasProcessadas = new LinkedHashMap<Integer, DominioTelaPrescreverItemMdto>();

	public String iniciar() {
		return avancar();
	}

	public String cancelar() {
		limparParametros();
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}

	/**
	 * 
	 * @return
	 */
	public String avancar() {
		// REGRA COMENTADA DEVIDO AS ISSUES #70677, #70674, #70673, #70640, #70639, #70638, #70636
//		VerificarDadosItensJustificativaPrescricaoVO retorno = null;
//		try {
//			retorno = this.prescricaoMedicaFacade.mpmpVerDadosItens(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getSeq());
//			if (retorno == null) {
				return confirmar();
//			}
//			this.telasProcessadas.put(0, retorno.getTela());
//		} catch (ApplicationBusinessException e) {
//			apresentarExcecaoNegocio(e);
//			return null;
//		}
//		atribuirAtendimentoController(retorno, this.prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getSeq());
//		return retorno != null && retorno.getTela() != null ? retorno.getTela().getXhtml() : null;
	}

	/**
	 * Atribuí o atendimento na respectiva controller
	 * 
	 * @param selecionado
	 * @param atdSeq
	 */
	public void atribuirAtendimentoController(VerificarDadosItensJustificativaPrescricaoVO selecionado, final Integer atdSeq) {
		if (selecionado != null) {
			switch (selecionado.getTela()) {
			case MPMF_NOTIFICACAO_TB:
				this.cadastroSinamController.setNtbSeq(selecionado.getSeqNotificacao());
				break;
			case MPMF_JUST_QT:
				this.cadastroJustificativaMedicamentoUsoQuimioterapicoController.setAtdSeq(atdSeq);
				break;
			case MPMF_JUST_UR:
				this.cadastroJustificativaMedicamentoUsoRestritoController.setAtdSeq(atdSeq);
				break;
			case MPMF_JUST_NS:
				this.cadastroJustificativaMedicamentoNaoSelecionadoController.setAtdSeq(atdSeq);
				break;
			case MPMF_JUST_UR_MICROB:
				this.cadastroJustificativaMedicamentoUsoRestritoAntimicrobianoController.setAtdSeq(atdSeq);
				break;
			case MPMF_JUST_NS_MICROB:
				this.cadastroJustificativaMedicamentoUsoNsAntimicrobianoController.setAtdSeq(atdSeq);
				break;
			case MPMF_ATU_JUST_MDTO:
				this.atualizaJustificativaUsoMedicamentoController.setAtdSeq(atdSeq);
				break;
			default:
				break;
			}
		}
	}

	public String confirmar() {
		this.manterPrescricaoMedicaController.setCameFromPrescreverItem(Boolean.TRUE);
		this.limparParametros();
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}

	private void limparParametros() {

		/*
		 * Limpa parâmetros locais
		 */
		this.prescricaoMedicaVO = null;
		this.telaAnterior = null;
		this.telasProcessadas = new LinkedHashMap<Integer, DominioTelaPrescreverItemMdto>();

		/*
		 * Limpa parâmetros das telas filhas
		 */
		this.cadastroSinamController.limparParametros();
		this.atualizaJustificativaUsoMedicamentoController.limparParametros();
		this.cadastroJustificativaMedicamentoNaoSelecionadoController.limparParametros();
		this.cadastroJustificativaMedicamentoUsoNsAntimicrobianoController.limparParametros();
		this.cadastroJustificativaMedicamentoUsoQuimioterapicoController.limparParametros();
		this.cadastroJustificativaMedicamentoUsoRestritoController.limparParametros();
		this.cadastroJustificativaMedicamentoUsoRestritoAntimicrobianoController.limparParametros();
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public DominioTelaPrescreverItemMdto getTelaAnterior() {
		return telaAnterior;
	}

	public void setTelaAnterior(DominioTelaPrescreverItemMdto telaAnterior) {
		this.telaAnterior = telaAnterior;
	}

	public Map<Integer, DominioTelaPrescreverItemMdto> getTelasProcessadas() {
		return telasProcessadas;
	}

	public void setTelasProcessadas(Map<Integer, DominioTelaPrescreverItemMdto> telasProcessadas) {
		this.telasProcessadas = telasProcessadas;
	}

	public boolean isExibirBotaoRetroceder() {
		return exibirBotaoRetroceder;
	}

	public void setExibirBotaoRetroceder(boolean exibirBotaoRetroceder) {
		this.exibirBotaoRetroceder = exibirBotaoRetroceder;
	}

}