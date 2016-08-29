package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action.PrescricaoEnfermagemTemplateController;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author diego.pacheco
 *
 */

public class EncerramentoDiagnosticoController extends ActionController {

	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-manterPrescricaoEnfermagem";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(EncerramentoDiagnosticoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3208570735560285276L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	private Integer penAtdSeq;
	
	private Integer penSeq;
	
	private List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO;
	
	private Boolean existeDiagnosticoSelecionado;
	
	private String cameFrom;
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	
	private int idConversacaoAnterior;
	
	public void iniciar() {
	 

		try{
			EpePrescricaoEnfermagemId id = new EpePrescricaoEnfermagemId();
			if (this.penSeq != null && this.penAtdSeq != null) {
				id.setAtdSeq(penAtdSeq);
				id.setSeq(penSeq);
				prescricaoEnfermagem = this.prescricaoEnfermagemFacade.obterPrescricaoEnfermagemPorId(id);
				this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO( 
						this.prescricaoEnfermagemFacade.popularDadosCabecalhoPrescricaoEnfermagemVO(prescricaoEnfermagem));
				
				// Lista de diagnosticos/etiologias listados na tela
				carregarListaDiagnosticoEtiologiaVO();
				
				existeDiagnosticoSelecionado = false;
			}
		} catch(ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	
	}
	
	public void carregarListaDiagnosticoEtiologiaVO() {
		this.listaDiagnosticoEtiologiaVO = 
				prescricaoEnfermagemFacade.listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(
						prescricaoEnfermagem.getAtendimento().getSeq(),
						prescricaoEnfermagem.getDthrInicio(),
						prescricaoEnfermagem.getDthrFim(),
						prescricaoEnfermagem.getDthrMovimento());
	}

	public void verificarSelecaoDiagnostico() {
		for (DiagnosticoEtiologiaVO diagnosticoEtiologiaVO : listaDiagnosticoEtiologiaVO) {
			if (diagnosticoEtiologiaVO.getSelecionado() != null && diagnosticoEtiologiaVO.getSelecionado()) {
				existeDiagnosticoSelecionado = true;
				return;
			}
		}
		existeDiagnosticoSelecionado = false;
	}
	
	public String redirecionarManterPrescricaoEnfermagem() {
		return MANTER_PRESCRICAO_ENFERMAGEM;
	}
	
	public void removerPrescCuidadosDiagnosticosSelecionados() {
		try {
			prescricaoEnfermagemFacade.removerPrescCuidadosDiagnosticosSelecionados(
					listaDiagnosticoEtiologiaVO, 
					prescricaoEnfermagem.getAtendimento().getSeq(), 
					prescricaoEnfermagem.getId().getSeq());
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_DIAGNOSTICO_ETIOLOGIA_ENCERRADO_SUCESSO");
			this.carregarListaDiagnosticoEtiologiaVO();
			existeDiagnosticoSelecionado = false;
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	}

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(
			IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	public PrescricaoEnfermagemVO getPrescricaoEnfermagemVO() {
		return prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO();
	}

	public void setPrescricaoEnfermagemVO(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO) {
		this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(prescricaoEnfermagemVO);
	}

	public Integer getPenAtdSeq() {
		return penAtdSeq;
	}

	public void setPenAtdSeq(Integer penAtdSeq) {
		this.penAtdSeq = penAtdSeq;
	}

	public Integer getPenSeq() {
		return penSeq;
	}

	public void setPenSeq(Integer penSeq) {
		this.penSeq = penSeq;
	}

	public Boolean getExisteDiagnosticoSelecionado() {
		return existeDiagnosticoSelecionado;
	}

	public void setExisteDiagnosticoSelecionado(Boolean existeDiagnosticoSelecionado) {
		this.existeDiagnosticoSelecionado = existeDiagnosticoSelecionado;
	}

	public List<DiagnosticoEtiologiaVO> getListaDiagnosticoEtiologiaVO() {
		return listaDiagnosticoEtiologiaVO;
	}

	public void setListaDiagnosticoEtiologiaVO(
			List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO) {
		this.listaDiagnosticoEtiologiaVO = listaDiagnosticoEtiologiaVO;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}	
}