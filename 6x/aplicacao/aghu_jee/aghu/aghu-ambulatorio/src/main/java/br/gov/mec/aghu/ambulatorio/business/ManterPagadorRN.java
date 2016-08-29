package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacFormaAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacNivelBuscaDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPagadorDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterPagadorRN extends BaseBusiness {

	private static final long serialVersionUID = 4773397216023246737L;

	private static final Log LOG = LogFactory.getLog(ManterPagadorRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private AacPagadorDAO aacPagadorDAO;

	@Inject
	private AacFormaAgendamentoDAO aacFormaAgendamentoDAO;

	@Inject
	private AacNivelBuscaDAO aacNivelBuscaDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public enum ManterAacPagadorDAOExceptionCode implements BusinessExceptionCode {
		MSG_DEPENDENCIAS_PAGADOR;
	}

	public void remover(Short codigoPagador) throws ApplicationBusinessException {
		AacPagador pagador = this.aacPagadorDAO.obterPorChavePrimaria(codigoPagador);
		if (this.aacFormaAgendamentoDAO.existeFormaAgendamentoComPagadorCount(pagador)
				|| this.aacNivelBuscaDAO.existeNivelBuscaComPagadorCount(pagador) || faturamentoFacade.existeFaturamentoComPagador(pagador)) {
			throw new ApplicationBusinessException(ManterAacPagadorDAOExceptionCode.MSG_DEPENDENCIAS_PAGADOR);
		} else {
			this.aacPagadorDAO.remover(pagador);
			this.aacPagadorDAO.flush();
		}
	}

	public void persistirPagador(AacPagador pagador) throws ApplicationBusinessException {
		pagador.setServidor(this.getServidorLogadoFacade().obterServidorLogado());
		pagador.setCriadoEm(new Date());
		this.aacPagadorDAO.persistir(pagador);
		this.aacPagadorDAO.flush();
	}

	public void atualizarPagador(AacPagador pagador) throws ApplicationBusinessException {
		pagador.setServidorAlterado(this.getServidorLogadoFacade().obterServidorLogado());
		pagador.setAlteradoEm(new Date());
		this.aacPagadorDAO.atualizar(pagador);
		this.aacPagadorDAO.flush();
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}