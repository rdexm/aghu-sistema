package br.gov.mec.aghu.sig.custos.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigProcessamentoCustoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ManterProcessamentoMensalON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterProcessamentoMensalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigProcessamentoCustoDAO sigProcessamentoCustoDAO;

	private static final long serialVersionUID = -23112384315315L;

	public enum ProcessamentoMensalONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DATA_COMPETENCIA_OBRIGATORIA, 
		MENSAGEM_DATA_COMPETENCIA_INVALIDA, 
		MENSAGEM_PROCESSAMENTO_EXISTENTE_COMPETENCIA, 
		MENSAGEM_EXISTE_PROCESSAMENTO_AGENDADO;
	}

	public SigProcessamentoCusto incluirProcessamentoCusto(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		processamentoCusto.setCriadoEm(new Date());
		processamentoCusto.setRapServidores(servidorLogado);
		processamentoCusto.setIndSituacao(DominioSituacaoProcessamentoCusto.S);

		//  Validar com processamento anteriores, mesma competência, e agendamento Schedulado para outra competência. 
		if (validarInclusaoProcessamentoCusto(processamentoCusto)) {
			getSigProcessamentoCustoDAO().persistir(processamentoCusto);
		}
		return processamentoCusto;
	}

	public SigProcessamentoCusto reprocessarProcessamentoCusto(SigProcessamentoCusto processamentoCusto)
			throws ApplicationBusinessException {
		processamentoCusto.setIndSituacao(DominioSituacaoProcessamentoCusto.S);
		if (validarReprocessamentoProcessamentoCusto(processamentoCusto)) {
			this.getSigProcessamentoCustoDAO().atualizar(processamentoCusto);
		}
		return processamentoCusto;
	}

	private boolean validarInclusaoProcessamentoCusto(SigProcessamentoCusto processamentoCusto) throws ApplicationBusinessException {

		this.validarDataCompetencia(processamentoCusto.getCompetencia());
		this.validarProcessamentoCompetencia(processamentoCusto.getCompetencia());
		this.validarProcessamentoSchedulado();

		return true;
	}

	private boolean validarReprocessamentoProcessamentoCusto(SigProcessamentoCusto processamentoCusto) throws ApplicationBusinessException {

		this.validarDataCompetencia(processamentoCusto.getCompetencia());
		this.validarProcessamentoSchedulado();

		return true;
	}

	private void validarDataCompetencia(Date dataCompetencia) throws ApplicationBusinessException {

		if (dataCompetencia != null) {
			Calendar dtCompetencia = Calendar.getInstance();
			dtCompetencia.setTime(dataCompetencia);
			dtCompetencia.set(Calendar.DAY_OF_MONTH, 1);
			dtCompetencia.set(Calendar.HOUR_OF_DAY, 0);
			dtCompetencia.set(Calendar.MINUTE, 0);
			dtCompetencia.set(Calendar.SECOND, 0);
			dtCompetencia.set(Calendar.MILLISECOND, 0);

			Calendar dtAtual = Calendar.getInstance();
			dtAtual.setTime(new Date());
			dtAtual.set(Calendar.DAY_OF_MONTH, 1);
			dtAtual.set(Calendar.HOUR_OF_DAY, 0);
			dtAtual.set(Calendar.MINUTE, 0);
			dtAtual.set(Calendar.SECOND, 0);
			dtAtual.set(Calendar.MILLISECOND, 0);

			if (!dtCompetencia.getTime().before(dtAtual.getTime())) {
				throw new ApplicationBusinessException(ProcessamentoMensalONExceptionCode.MENSAGEM_DATA_COMPETENCIA_INVALIDA);
			}

		} else {
			throw new ApplicationBusinessException(ProcessamentoMensalONExceptionCode.MENSAGEM_DATA_COMPETENCIA_OBRIGATORIA);
		}

	}

	private void validarProcessamentoCompetencia(Date dataCompetencia) throws ApplicationBusinessException {

		List<SigProcessamentoCusto> listProcessamentoCusto = getSigProcessamentoCustoDAO().obterSigProcessamentoCustoCompetencia(dataCompetencia);

		if (listProcessamentoCusto.size() > 0) {
			throw new ApplicationBusinessException(ProcessamentoMensalONExceptionCode.MENSAGEM_PROCESSAMENTO_EXISTENTE_COMPETENCIA,
					DateUtil.obterDataFormatada(dataCompetencia, "MM/yyyy"));
		}
	}

	private void validarProcessamentoSchedulado() throws ApplicationBusinessException {

		List<SigProcessamentoCusto> listProcessamentoCusto = getSigProcessamentoCustoDAO().pesquisarCompetencia(DominioSituacaoProcessamentoCusto.S);

		if (listProcessamentoCusto.size() > 0) {
			throw new ApplicationBusinessException(ProcessamentoMensalONExceptionCode.MENSAGEM_EXISTE_PROCESSAMENTO_AGENDADO);
		}
	}

	public SigProcessamentoCusto obterProcessamentoCusto(Date dataCompetenciaDefault) {
		List<SigProcessamentoCusto> lista = this.getSigProcessamentoCustoDAO().obterSigProcessamentoCustoCompetencia(dataCompetenciaDefault);

		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}

	private SigProcessamentoCustoDAO getSigProcessamentoCustoDAO() {
		return sigProcessamentoCustoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
