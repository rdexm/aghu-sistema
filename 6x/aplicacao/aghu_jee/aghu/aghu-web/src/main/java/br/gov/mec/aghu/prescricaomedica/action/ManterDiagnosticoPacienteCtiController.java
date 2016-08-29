package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.action.PesquisaCidController;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterDiagnosticoPacienteCtiController extends ActionController {
	
	private static final long serialVersionUID = 1900459178863262464L;
	private static final Log LOG = LogFactory.getLog(ManterDiagnosticoPacienteCtiController.class);
	private static final String PAGE_VERIFICA_PRESCRICAO_MEDICA = "prescricaomedica-verificaPrescricaoMedica";
	private static final String REDIRECIONA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";
	private static final String ERRO_CONSULTAR_NOME_MICROCOMPUTADOR = "Erro ao Consultar Nome do MicroComputador";

	private AghCid aghCid;
	
	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private MpmMotivoIngressoCti mpmMotivoIngressoCti;
	
	private String complemento;
	
	private Integer pmeSeqAtendimento;

	private Integer pmeSeq;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private PesquisaCidController pesquisaCidController;
	
	@Inject
	private VerificarPrescricaoMedicaController verificarPrescricaoMedicaController;

	@Inject 
	private ManterPrescricaoMedicaController manterPrescricaoMedicaController;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
		try {
			if (this.pmeSeqAtendimento != null && this.pmeSeq != null) {
				MpmPrescricaoMedicaId filter = new MpmPrescricaoMedicaId(
						this.pmeSeqAtendimento, this.pmeSeq);
				
				this.prescricaoMedicaVO = this.prescricaoMedicaFacade.buscarDadosCabecalhoPrescricaoMedicaVO(filter);
			}
		}catch (ApplicationBusinessException ae){
			apresentarExcecaoNegocio(ae);
		}
	}
	
	/* METODOS PARA SUGGESTION */
	public List<AghCid> pesquisarCids(String pesquisa){		
		return returnSGWithCount(aghuFacade.pesquisarCidPorCodigoDescricaoSGB(pesquisa), aghuFacade.pesquisarCidPorCodigoDescricaoCount(pesquisa));
	}
	
	public String pageCidPorCapitulo() {
		pesquisaCidController.setFromPageDiagnosticoPacienteCti(true);
		return PAGE_PESQUISA_CID;
	}
	
	
	public String cancelarPrescricao() {
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error(ERRO_CONSULTAR_NOME_MICROCOMPUTADOR, e);
			}
			
			this.prescricaoMedicaFacade.cancelarPrescricaoMedica(this.pmeSeqAtendimento, this.pmeSeq, nomeMicrocomputador);
			this.limpar();
			return PAGE_VERIFICA_PRESCRICAO_MEDICA;
		} catch (BaseException e) {
			LOG.error("Erro", e);
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
			
	private void limpar() {
			Iterator<UIComponent> componentes = FacesContext.getCurrentInstance()
					.getViewRoot().getFacetsAndChildren();

			while (componentes.hasNext()) {
				limparValoresSubmetidos(componentes.next());
			}
			
			this.prescricaoMedicaVO = null;
			this.aghCid = null;
			this.complemento = null;
		}

	private void limparValoresSubmetidos(Object object) {

		if (object == null || object instanceof UIComponent == false) {
			return;
		}

		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();

		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}

		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}

	public String salvar(){
		try {
			String msg = this.prescricaoMedicaFacade.salvarDiagPacCti(prescricaoMedicaVO, complemento, aghCid, pmeSeqAtendimento);
				if(msg != null && msg.equals("MENSAGEM_DIAGNOSTICO_GRAVADO_COM_SUCESSO")){
					this.apresentarMsgNegocio(Severity.INFO, msg);
					this.manterPrescricaoMedicaController.setPmeSeq(prescricaoMedicaVO.getId().getSeq());
					this.manterPrescricaoMedicaController.setPmeSeqAtendimento(prescricaoMedicaVO.getId().getAtdSeq());
					this.limpar();
					return REDIRECIONA_MANTER_PRESCRICAO_MEDICA;
				}else if(msg != null && msg.equals("MS01_INFORMAR_CID")){
					this.apresentarMsgNegocio(Severity.WARN, msg);
					return null;
				}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return this.verificarPrescricaoMedicaController.redirecionarParaMantanterPrescricaoMedica();
	}
	
	// GETTERS E SETTERS
	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Integer getPmeSeqAtendimento() {
		return pmeSeqAtendimento;
	}

	public void setPmeSeqAtendimento(Integer pmeSeqAtendimento) {
		this.pmeSeqAtendimento = pmeSeqAtendimento;
	}

	public Integer getPmeSeq() {
		return pmeSeq;
	}

	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}

	public MpmMotivoIngressoCti getMpmMotivoIngressoCti() {
		return mpmMotivoIngressoCti;
	}

	public void setMpmMotivoIngressoCti(MpmMotivoIngressoCti mpmMotivoIngressoCti) {
		this.mpmMotivoIngressoCti = mpmMotivoIngressoCti;
	}
	
}