package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.MpmItemPrescParecerMdtoId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.DetalhesParecerMedicamentosVO;
import br.gov.mec.aghu.prescricaomedica.vo.HistoricoParecerMedicamentosJnVO;
import br.gov.mec.aghu.core.action.ActionController;

public class DetalhaParecerMedicamentosController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3876605078065270863L;
	
	private static final String PAGE_DETALHA_PARECER_MEDICAMENTOS = "detalhaParecerMedicamentos";

	private static final String PAGE_DETALHA_PARECER = "consultaMedicamentosAvaliados";
	
	private static final String PAGE_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	
	private String cameFrom;
	
	@Inject
	private ModalCentralMensagensController modalCentralMensagensController;
	
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private DetalhesParecerMedicamentosVO detalhesParecerMedicamentosVO;
	
	private List<HistoricoParecerMedicamentosJnVO> historicoParecerMedicamentosJnVO;
	
	private HistoricoParecerMedicamentosJnVO itemSelecionado;
	
	private BigDecimal parecerSeq;
	
	private MpmItemPrescParecerMdtoId mpmItemPrescParecerMdtoId;
	
	
	public void inicio(){
		itemSelecionado = null;
	}
	
	public String obterParecerMedicamento(){
		this.setDetalhesParecerMedicamentosVO(this.prescricaoMedicaFacade.obterDetalhesParecerMedicamentos(getParecerSeq(),mpmItemPrescParecerMdtoId));
		return PAGE_DETALHA_PARECER_MEDICAMENTOS;
	}

	public void visualizarHistorico(){
		setHistoricoParecerMedicamentosJnVO(this.prescricaoMedicaFacade.obterHistoricoParecerMedicamentos(getParecerSeq()));
	}
	
	public String voltar(){
		if(cameFrom != null && cameFrom.equals("PAGE_MANTER_PRESCRICAO_MEDICA")){
			modalCentralMensagensController.iniciarModal();
			return PAGE_MANTER_PRESCRICAO_MEDICA;
		}else{
			return PAGE_DETALHA_PARECER;
		}
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
			
		return item;
	}	
	
	public DetalhesParecerMedicamentosVO getDetalhesParecerMedicamentosVO() {
		return detalhesParecerMedicamentosVO;
	}

	public void setDetalhesParecerMedicamentosVO(
			DetalhesParecerMedicamentosVO detalhesParecerMedicamentosVO) {
		this.detalhesParecerMedicamentosVO = detalhesParecerMedicamentosVO;
	}

	public BigDecimal getParecerSeq() {
		return parecerSeq;
	}

	public void setParecerSeq(BigDecimal parecerSeq) {
		this.parecerSeq = parecerSeq;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public List<HistoricoParecerMedicamentosJnVO> getHistoricoParecerMedicamentosJnVO() {
		return historicoParecerMedicamentosJnVO;
	}

	public void setHistoricoParecerMedicamentosJnVO(
			List<HistoricoParecerMedicamentosJnVO> historicoParecerMedicamentosJnVO) {
		this.historicoParecerMedicamentosJnVO = historicoParecerMedicamentosJnVO;
	}

	public HistoricoParecerMedicamentosJnVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(HistoricoParecerMedicamentosJnVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public MpmItemPrescParecerMdtoId getMpmItemPrescParecerMdtoId() {
		return mpmItemPrescParecerMdtoId;
	}

	public void setMpmItemPrescParecerMdtoId(MpmItemPrescParecerMdtoId mpmItemPrescParecerMdtoId) {
		this.mpmItemPrescParecerMdtoId = mpmItemPrescParecerMdtoId;
	}
}
