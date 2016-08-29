package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action.PrescricaoEnfermagemTemplateController;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SelecaoCuidadoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller para a funcionalidade Selecionar Cuidados do Diagnóstico 
 * 
 * @author diego.pacheco
 *
 */

public class SelecaoCuidadosDiagnosticoController extends ActionController {

	

	private static final String MANTER_PRESCRICAO_CUIDADO = "prescricaoenfermagem-manterPrescricaoCuidado";

	private static final Log LOG = LogFactory.getLog(SelecaoCuidadosDiagnosticoController.class);

	private static final long serialVersionUID = -5653077630439302847L;
	
	private Integer penSeqAtendimento;
	
	private Integer penSeq;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	private SelecaoCuidadoVO selecaoCuidadoVO;
	
	private Boolean cuidadosSelecionado;
	
	private String cameFrom;
	
	private int idConversacaoAnterior;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		try {
			this.penSeqAtendimento = selecaoCuidadoVO.getAtdSeq();
			this.penSeq = selecaoCuidadoVO.getPenSeq();
			if (this.penSeqAtendimento != null && this.penSeq != null) {
				EpePrescricaoEnfermagemId prescricaoId = new EpePrescricaoEnfermagemId(this.penSeqAtendimento, this.penSeq);

				this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(this.prescricaoEnfermagemFacade
					.buscarDadosCabecalhoPrescricaoEnfermagemVO(prescricaoId));

				Short fdgDgnSnbGnbSeq = selecaoCuidadoVO.getDgnSnbGnbSeq();  // -- chave composta do diagnostico+etiologia:   
				Short fdgDgnSnbSequencia = selecaoCuidadoVO.getDgnSnbSequencia(); // --  DIAGNOSTICO = FADIGA
				Short fdgDgnSequencia = selecaoCuidadoVO.getDgnSequencia(); // --  ETIOLOGIA   = ANEMIA
				List<Short> listaFdgFreSeq = selecaoCuidadoVO.getEtiologias();
				
				List<CuidadoVO> listaCuidadoVO = new ArrayList<CuidadoVO>();
				
				for(Short fdgFreSeq : listaFdgFreSeq) {
					List<EpeCuidados> listaCuidadosAux = this.prescricaoEnfermagemFacade.pesquisarCuidadosAtivosPorGrupoDiagnosticoEtiologiaUnica(fdgDgnSnbGnbSeq, fdgDgnSnbSequencia, fdgDgnSequencia, fdgFreSeq);
					for (EpeCuidados cuidado : listaCuidadosAux) {
						CuidadoVO cuidadoVO = new CuidadoVO();
						cuidadoVO.setCuidado(cuidado);
						EpeFatRelacionado etiologia = this.prescricaoEnfermagemFacade.obterDescricaoEtiologiaPorSeq(fdgFreSeq);
						cuidadoVO.setDescricaoEtiologia(etiologia.getDescricao());
						cuidadoVO.setDescricao(cuidado.getDescricao());
						cuidadoVO.setFdgDgnSequencia(fdgDgnSequencia);
						cuidadoVO.setFdgDgnSnbGnbSeq(fdgDgnSnbGnbSeq);
						cuidadoVO.setFdgDgnSnbSequencia(fdgDgnSnbSequencia);
						cuidadoVO.setFdgFreSeq(etiologia.getSeq());
						listaCuidadoVO.add(cuidadoVO);
					}
				}
				
				this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().setListaCuidadoVO(listaCuidadoVO);
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	
	}
	
	public void verificarSelecaoCuidados(){
		for(CuidadoVO cuidado : prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO()){
			if(cuidado.getMarcado()!=null && cuidado.getMarcado()) {
				this.setCuidadosSelecionado(true);
				return;
			}
		}
		this.setCuidadosSelecionado(false);
	}
	
	public Integer getPenSeqAtendimento() {
		return penSeqAtendimento;
	}

	public void setPenSeqAtendimento(Integer penSeqAtendimento) {
		this.penSeqAtendimento = penSeqAtendimento;
	}

	public Integer getPenSeq() {
		return penSeq;
	}

	public void setPenSeq(Integer penSeq) {
		this.penSeq = penSeq;
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
	
	public String confirmarCuidados() {
		return MANTER_PRESCRICAO_CUIDADO; 
	}
	
	public String voltar() {
		this.setCuidadosSelecionado(false);
		return this.cameFrom;
	}

	public void setCuidadosSelecionado(Boolean cuidadosSelecionado) {
		this.cuidadosSelecionado = cuidadosSelecionado;
	}

	public Boolean getCuidadosSelecionado() {
		return cuidadosSelecionado;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public SelecaoCuidadoVO getSelecaoCuidadoVO() {
		return selecaoCuidadoVO;
	}

	public void setSelecaoCuidadoVO(SelecaoCuidadoVO selecaoCuidadoVO) {
		this.selecaoCuidadoVO = selecaoCuidadoVO;
	}	
}
