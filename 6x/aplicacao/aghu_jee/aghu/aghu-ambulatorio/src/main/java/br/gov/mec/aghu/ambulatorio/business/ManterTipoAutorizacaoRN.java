package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacNivelBuscaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacTipoAgendamentoDAO;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterTipoAutorizacaoRN extends BaseBusiness {

	public enum ManterTipoAutorizacaoRNExceptionCode implements BusinessExceptionCode {
		MSG_DEPENDENCIAS_TIPO_AUTORIZACAO;
	}

	private static final long serialVersionUID = -1431334637452812962L;
	private static final Log LOG = LogFactory.getLog(ManterTipoAutorizacaoRN.class);

	@Inject
	private AacTipoAgendamentoDAO aacTipoAgendamentoDAO;
	
	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;
	
	@Inject
	private AacNivelBuscaDAO aacNivelBuscaDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public void remover(Short codigoCondicaoAtendimento) throws ApplicationBusinessException {
		AacTipoAgendamento tipoAgendamento = this.getAacTipoAgendamentoDAO().obterPorChavePrimaria(codigoCondicaoAtendimento);
		if (this.getAacFormaAgendamentoDAO().existeFormaAgendamentoComTipoAgendamentoCount(tipoAgendamento)
				|| this.getAacNivelBuscaDAO().existeNivelBuscaComTipoAgendamentoCount(tipoAgendamento)) {
			throw new ApplicationBusinessException(ManterTipoAutorizacaoRNExceptionCode.MSG_DEPENDENCIAS_TIPO_AUTORIZACAO);
		} else {
			this.getAacTipoAgendamentoDAO().remover(tipoAgendamento);
			this.getAacTipoAgendamentoDAO().flush();
		}
	}

	/**
	 * 
	 * @throws MECBaseException
	 */
	public void persistirTipoAgendamento(AacTipoAgendamento tipoAgendamento) throws ApplicationBusinessException {		
		tipoAgendamento.setServidor(this.getServidorLogadoFacade().obterServidorLogado());
		tipoAgendamento.setCriadoEm(new Date());
		this.getAacTipoAgendamentoDAO().persistir(tipoAgendamento);
		this.getAacTipoAgendamentoDAO().flush();
	}

	public void atualizarCondicaoAtendimento(AacTipoAgendamento tipoAgendamento) throws ApplicationBusinessException {
		tipoAgendamento.setServidorAlterado(this.getServidorLogadoFacade().obterServidorLogado());
		tipoAgendamento.setAlteradoEm(new Date());
		this.getAacTipoAgendamentoDAO().atualizar(tipoAgendamento);
		this.getAacTipoAgendamentoDAO().flush();
	}

	/**
	 * GET/SET *
	 */
	protected AacTipoAgendamentoDAO getAacTipoAgendamentoDAO() {
		return aacTipoAgendamentoDAO;
	}

	protected AacFormaAgendamentoDAO getAacFormaAgendamentoDAO() {
		return aacFormaAgendamentoDAO;
	}

	protected AacNivelBuscaDAO getAacNivelBuscaDAO() {
		return aacNivelBuscaDAO;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

}
