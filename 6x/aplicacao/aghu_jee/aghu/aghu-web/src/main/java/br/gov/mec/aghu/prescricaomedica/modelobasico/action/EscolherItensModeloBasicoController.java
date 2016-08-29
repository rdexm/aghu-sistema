package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EscolherItensModeloBasicoController extends ActionController {

	private static final String PRESCRICAOMEDICA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final long serialVersionUID = 4230746628574273647L;
	private static final Log LOG = LogFactory.getLog(EscolherItensModeloBasicoController.class);
	
	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	
	private PrescricaoMedicaVO prescricaoMedicaVO;

	private Integer seqModelo;
	
	private List<ItensModeloBasicoVO> itensModeloBasicoVO;

	private MpmModeloBasicoPrescricao modeloBasico;

	
	private ItensModeloBasicoVO itemSelecionado;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Recebe a seq do modelo basico e delega a pesquisa de itens para a ON.
	 * 
	 * @throws AGHUNegocioException
	 */
	public void iniciar() throws ApplicationBusinessException {
		// obter itens do Modelo Basico
		if (seqModelo != null) {
			modeloBasico = this.modeloBasicoFacade.obterModeloBasico(seqModelo);

			if (modeloBasico != null) {
				itensModeloBasicoVO = this.modeloBasicoFacade
						.obterListaItensModelo(seqModelo);
			}
		} else {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SEQ_MODELO_NAO_INFORMADA");
		}
	}

	/**
	 * incluirItensSelecionados - Percorrer os itens selecionados no VO e
	 * proceder com a inclusão
	 * 
	 * @param prescricaoMedica
	 * @param itens
	 * @throws AGHUNegocioException
	 */
	public void incluirItensSelecionados() {
		try {
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			Boolean retorno = this.modeloBasicoFacade.incluirItensSelecionados(
					prescricaoMedicaVO.getPrescricaoMedica(),
					itensModeloBasicoVO,
					nomeMicrocomputador);
			if (retorno) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ITENS_SELECIONADOS");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ITENS_NAO_SELECIONADOS");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		return PRESCRICAOMEDICA_MANTER_PRESCRICAO_MEDICA;
	}

	public List<ItensModeloBasicoVO> getItensModeloBasicoVO() {
		return itensModeloBasicoVO;
	}

	public void setItensModeloBasicoVO(
			List<ItensModeloBasicoVO> itensModeloBasicoVO) {
		this.itensModeloBasicoVO = itensModeloBasicoVO;
	}

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public Integer getSeqModelo() {
		return seqModelo;
	}

	public void setSeqModelo(Integer seqModelo) {
		this.seqModelo = seqModelo;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public ItensModeloBasicoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ItensModeloBasicoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}
