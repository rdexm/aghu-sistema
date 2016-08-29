package br.gov.mec.aghu.ambulatorio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author vinicius.silva
 */

public class ManterRetornoConsultaController extends ActionController {

	private static final Log LOG = LogFactory
			.getLog(ManterRetornoConsultaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2493364795992419928L;
	
	private static final String RETORNO_CONSULTA_LIST = "ambulatorio-manterRetornoConsultaList";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	/**
	 * Condicao de Atendimento a ser criada/editada
	 */
	private AacRetornos retornoConsulta;
	private Short seq;
	private DominioSituacao situacao;
	private DominioSimNao dominioSimNao;
	private DominioIndAbsenteismo dominioIndAbsenteismo;
	private DominioOperacoesJournal operacao;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
		if (this.retornoConsulta != null && this.retornoConsulta.getSeq() != null) {
			this.retornoConsulta = ambulatorioFacade.obterRetornoConsultaPeloId(this.retornoConsulta.getSeq());
			this.dominioIndAbsenteismo = this.retornoConsulta.getAbsenteismo();
			if (retornoConsulta.getAusenteAmbu()) {
				dominioSimNao = DominioSimNao.S;
			} else {
				dominioSimNao = DominioSimNao.N;
			}
			this.situacao = retornoConsulta.getSituacao();
			this.setOperacao(DominioOperacoesJournal.UPD);
		} else {
			retornoConsulta = new AacRetornos();
			this.dominioIndAbsenteismo = DominioIndAbsenteismo.P;
			this.setOperacao(DominioOperacoesJournal.INS);
		}
	}
	

	public String confirmar() {
		try {
			if (this.operacao.equals(DominioOperacoesJournal.INS)) {
				this.ambulatorioFacade.persistirRetornoConsulta(
						retornoConsulta, dominioSimNao, situacao,
						dominioIndAbsenteismo);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_RETORNO_CONSULTA_INCLUIDO");
			} else {
				this.ambulatorioFacade.atualizarRetornoConsulta(
						retornoConsulta, situacao, dominioIndAbsenteismo);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_RETORNO_CONSULTA_ALTERADO");
			}
			return RETORNO_CONSULTA_LIST;
		} catch (ApplicationBusinessException e) {
			if (e.getCause() instanceof ConstraintViolationException
					|| e.getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_RETORNO_CONSULTA_EXISTENTE");
			} else {
				if (this.operacao.equals(DominioOperacoesJournal.INS)) {
					this.apresentarMsgNegocio(Severity.ERROR,
							"MENSAGEM_RETORNO_CONSULTA_ERRO_INCLUSAO");
				} else {
					this.apresentarMsgNegocio(Severity.ERROR,
							"MENSAGEM_RETORNO_CONSULTA_ERRO_ALTERACAO");
				}
			}
			return null;
		}
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro da
	 * condição de atendimento.
	 */
	public String cancelar() {
		// Força refazer a pesquisa, para não ficar os valores modificados nesta
		// edição (que foi cancelada)
		retornoConsulta = null;
		seq = null;
		situacao = null;
		dominioSimNao = null;
		dominioIndAbsenteismo = null;
		operacao = null;
		LOG.info("Cancelado");
		return RETORNO_CONSULTA_LIST;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}

	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}

	public AacRetornos getRetornoConsulta() {
		return retornoConsulta;
	}

	public void setRetornoConsulta(AacRetornos retornoConsutla) {
		this.retornoConsulta = retornoConsutla;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short id) {
		this.seq = id;
	}

	public DominioIndAbsenteismo getDominioIndAbsenteismo() {
		return dominioIndAbsenteismo;
	}

	public void setDominioIndAbsenteismo(
			DominioIndAbsenteismo dominioIndAbsenteismo) {
		this.dominioIndAbsenteismo = dominioIndAbsenteismo;
	}

}
