package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class EditarItemSolicitacaoExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(EditarItemSolicitacaoExameController.class);

	private static final long serialVersionUID = -6621845996726916256L;

	private static final String PAGE_EXAMES_LISTAR_EXAMES_CRITERIOS_SELECIONADOS = "exames-listarExamesCriteriosSelecionados";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesBeanFacade examesBeanFacade;

	// Variáveis para controle de edição
	private DominioTipoTransporteUnidade transporte;
	private Boolean indUsoO2Un;

	private Integer soeSeq;
	private Short seqp;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 

		AelItemSolicitacaoExames itemSolicitacao = this.examesFacade.buscaItemSolicitacaoExamePorId(this.soeSeq, this.seqp);
		if (itemSolicitacao != null) {
			this.transporte = itemSolicitacao.getTipoTransporteUn();
			this.indUsoO2Un = itemSolicitacao.getIndUsoO2Un();
		}

	
	}

	// @Restrict("#{s:hasPermission('recepcionarPaciente','executar')}")
	public String confirmar() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			AelItemSolicitacaoExames itemSolicitacaoAtualizar = this.examesFacade.buscaItemSolicitacaoExamePorId(this.soeSeq, this.seqp);

			this.examesBeanFacade.recepcionarPaciente(itemSolicitacaoAtualizar, this.transporte, this.indUsoO2Un, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_ITEM_SOLICITACAO");

			return this.cancelar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de sinônimo
	 */
	public String cancelar() {
		this.soeSeq = null;
		this.seqp = null;
		this.transporte = null;
		this.indUsoO2Un = null;
		return PAGE_EXAMES_LISTAR_EXAMES_CRITERIOS_SELECIONADOS;
	}

	public Boolean getIndUsoO2Un() {
		return indUsoO2Un;
	}

	public void setIndUsoO2Un(Boolean indUsoO2Un) {
		this.indUsoO2Un = indUsoO2Un;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public DominioTipoTransporteUnidade getTransporte() {
		return transporte;
	}

	public void setTransporte(DominioTipoTransporteUnidade transporte) {
		this.transporte = transporte;
	}
}
