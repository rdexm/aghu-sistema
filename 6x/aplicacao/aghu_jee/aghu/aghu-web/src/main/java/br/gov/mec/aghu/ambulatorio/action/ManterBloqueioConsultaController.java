package br.gov.mec.aghu.ambulatorio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterBloqueioConsultaController extends ActionController {

	private static final long serialVersionUID = -2493364795992419928L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	/**
	 * Condicao de Atendimento a ser criada/editada
	 */
	private AacSituacaoConsultas situacaoConsulta;
	private String id;
	private DominioSituacao situacao;
	private DominioSimNao dominioSimNao;
	private DominioOperacoesJournal operacao;

	private static final Log LOG = LogFactory.getLog(ManterBloqueioConsultaController.class);

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		if (this.id != null && !this.id.equals("null") ) {
			this.situacaoConsulta = ambulatorioFacade.obterSituacaoConsultaPeloId(id);
			this.setDominioSimNao(DominioSimNao.getInstance(this.situacaoConsulta.getAusenciaProfissional()));
			this.setOperacao(DominioOperacoesJournal.UPD);
		} else {
			situacaoConsulta = new AacSituacaoConsultas();			
			this.setOperacao(DominioOperacoesJournal.INS);
		}
	
	}
	

	public String confirmar() {
		try {
			this.situacaoConsulta.setAusenciaProfissional(dominioSimNao.isSim());
			if (this.operacao.equals(DominioOperacoesJournal.INS)) {
				this.ambulatorioFacade.persistirRegistroSituacaoConsulta(situacaoConsulta);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_BLOQUEIO_CONSULTA_INCLUIDO");
			} else {
				this.ambulatorioFacade.atualizarRegistroSituacaoConsulta(situacaoConsulta);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_BLOQUEIO_CONSULTA_ALTERADO");
			}
			//reiniciarPaginator(ManterBloqueioConsultaPaginatorController.class);
		} catch (EJBTransactionRolledbackException e) {
			LOG.error("Exceção capturada: ", e);
			situacaoConsulta.setSituacao(null);
			if (e.getCause() instanceof ConstraintViolationException || e.getCause() instanceof NonUniqueObjectException) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_BLOQUEIO_CONSULTA_EXISTENTE");
			} else {
				if (this.operacao.equals(DominioOperacoesJournal.INS)) {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_BLOQUEIO_CONSULTA_ERRO_INCLUSAO");
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_BLOQUEIO_CONSULTA_ERRO_ALTERACAO");
				}
			}
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} 

		return "manterBloqueioConsultaList";
	}

	public String cancelar() {
		return "manterBloqueioConsultaList";
	}

	public AacSituacaoConsultas getSituacaoConsulta() {
		return situacaoConsulta;
	}

	public void setSituacaoConsulta(AacSituacaoConsultas situacaoConsulta) {
		this.situacaoConsulta = situacaoConsulta;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = String.valueOf(id);
	}

}
