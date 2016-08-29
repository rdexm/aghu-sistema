package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;

public class EscolherModeloBasicoController extends ActionController {

	private static final String PAGINA_ESCOLHER_ITENS_MODELO_BASICO = "escolherItensModeloBasico";
	private static final long serialVersionUID = 5067661647990145103L;
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final String PAGINA_MANTER_MODELO_BASICO = "manterModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private int idConversacaoAnterior;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private RapServidores servidor;
	private List<MpmModeloBasicoPrescricao> modelos;
	private MpmModeloBasicoPrescricao modeloSelecionado;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		this.servidor = servidorLogadoFacade.obterServidorLogadoSemCache();
		this.modelos = this.modeloBasicoFacade.listarModelosBasicos();
	}

	public String cadastrarModeloBasico(){
		return PAGINA_MANTER_MODELO_BASICO;
	}

	public String escolherItensModeloBasico(){
		return PAGINA_ESCOLHER_ITENS_MODELO_BASICO;
	}
	
	public String voltar() {
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}

	// getters & setters

	public List<MpmModeloBasicoPrescricao> getModelos() {
		return modelos;
	}

	public void setModelos(List<MpmModeloBasicoPrescricao> modelos) {
		this.modelos = modelos;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public MpmModeloBasicoPrescricao getModeloSelecionado() {
		return modeloSelecionado;
	}

	public void setModeloSelecionado(MpmModeloBasicoPrescricao modeloSelecionado) {
		this.modeloSelecionado = modeloSelecionado;
	}
}
