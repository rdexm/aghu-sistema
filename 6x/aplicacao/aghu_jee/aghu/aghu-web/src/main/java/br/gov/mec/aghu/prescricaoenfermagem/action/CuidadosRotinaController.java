package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagemId;
import br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action.PrescricaoEnfermagemTemplateController;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.SelecaoCuidadoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller para a funcionalidade Selecionar Cuidados de Rotina 
 * 
 * @author sgralha
 *
 */

public class CuidadosRotinaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CuidadosRotinaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5653077630439302847L;
	
	private Integer atdSeq;
	
	private Integer seq;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private PrescricaoEnfermagemTemplateController prescricaoEnfermagemTemplateController;
	
	//TODO @Out(required = false, scope = ScopeType.SESSION)
	private SelecaoCuidadoVO selecaoCuidadoVO = new SelecaoCuidadoVO();
	
	private Boolean cuidadosSelecionado;
	
	private String cameFrom;
	
	private int idConversacaoAnterior;
	
	public void iniciar() {
	 

		try {
			List<CuidadoVO> listaCuidadoVO = new ArrayList<CuidadoVO>();
			
			if (this.atdSeq != null && this.seq != null) {
				EpePrescricaoEnfermagemId prescricaoId = new EpePrescricaoEnfermagemId(atdSeq, seq);

				this.prescricaoEnfermagemTemplateController.setPrescricaoEnfermagemVO(this.prescricaoEnfermagemFacade
					.buscarDadosCabecalhoPrescricaoEnfermagemVO(prescricaoId));

				AghAtendimentos atd = aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);
				
				List<EpeCuidados> listaCuidadosAux = this.prescricaoEnfermagemFacade.pesquisarCuidadosdeRotinasAtivos(atd.getUnidadeFuncional().getSeq());
					for (EpeCuidados cuidado : listaCuidadosAux) {
						CuidadoVO cuidadoVO = new CuidadoVO();
						cuidadoVO.setCuidado(cuidado);
				//		EpeFatRelacionado etiologia = this.prescricaoEnfermagemFacade.obterDescricaoEtiologiaPorSeq(fdgFreSeq);
				//		cuidadoVO.setDescricaoEtiologia(etiologia.getDescricao());
				//		cuidadoVO.setDescricao(cuidado.getDescricao());
				//		cuidadoVO.setFdgFreSeq(etiologia.getSeq());
						listaCuidadoVO.add(cuidadoVO);
				}
				
				
					this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().setListaCuidadoVO(listaCuidadoVO);
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	
	}
	
	public void verificarSelecaoCuidados(){
		for(CuidadoVO cuidado : this.prescricaoEnfermagemTemplateController.getPrescricaoEnfermagemVO().getListaCuidadoVO()){
			if(cuidado.getMarcado()!=null && cuidado.getMarcado()) {
				this.setCuidadosSelecionado(true);
				this.selecaoCuidadoVO.setAtdSeq(this.atdSeq);
				this.selecaoCuidadoVO.setPenSeq(this.seq);
				return;
			}
		}
		this.setCuidadosSelecionado(false);
	}
	
	public String  voltarLista(){
		
		return "prescricaoenfermagem-manterPrescricaoEnfermagem";
	}
	
	
	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}

	public void setPrescricaoEnfermagemFacade(
			IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade) {
		this.prescricaoEnfermagemFacade = prescricaoEnfermagemFacade;
	}

	
	
	public String confirmarCuidados() {
		return "prescricaoenfermagem-manterPrescricaoCuidado"; 
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

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public SelecaoCuidadoVO getSelecaoCuidadoVO() {
		return selecaoCuidadoVO;
	}

	public void setSelecaoCuidadoVO(SelecaoCuidadoVO selecaoCuidadoVO) {
		this.selecaoCuidadoVO = selecaoCuidadoVO;
	}

}
