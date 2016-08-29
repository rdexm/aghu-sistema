package br.gov.mec.aghu.certificacaodigital.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.certificacaodigital.dao.AghDocumentoCertificadoDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class DocumentosPacienteON extends BaseBusiness {


private static final Log LOG = LogFactory.getLog(DocumentosPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghDocumentoCertificadoDAO aghDocumentoCertificadoDAO;

@EJB
private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6491759448479758735L;
	
	public enum DocumentosPacienteONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_DATA_FINAL_MENOR_DATA_INICIAL,
		MENSAGEM_PARAMETRO_NRO_MAXIMO_PESQUISA_NAO_DEFINIDO,
		MENSAGEM_PERIODO_MAXIMO_PESQUISA_EXCEDIDO,
		MENSAGEM_PERIODO_MAXIMO_PARAMETRO_EXCEDIDO,
		MENSAGEM_NECESSARIO_INFORMAR_FILTRO,
		MENSAGEM_HU_NAO_POSSUI_CERTIFICACAO_DIGITAL;
	}

	private static final Integer PERIODO_PADRAO_PESQUISA = 30;

	/**
	 * Realizar a validação dos filtros informados para a pesquisa
	 * 
	 * @param paciente
	 * @param dataInicial
	 * @param dataFinal
	 * @param responsavel
	 * @param servico
	 * @param situacao
	 * @param tipoDocumento
	 * @throws BaseException
	 */
	public void validarFiltrosPesquisa(AipPacientes paciente, Date dataInicial,
			Date dataFinal, RapServidores responsavel, FccCentroCustos servico,
			DominioSituacaoVersaoDocumento situacao,
			DominioTipoDocumento tipoDocumento) throws BaseException {

		if (dataInicial == null && dataFinal == null) {
			throw new IllegalArgumentException("Argumentos inválidos.");
		}
		
		this.verificarHUCertificacaoDigital();
		this.verificarFiltrosInformados(paciente, responsavel, servico,
				situacao, tipoDocumento);
		this.verificarDataFinalBeforeDataInicial(dataInicial, dataFinal);
	}

	/**
	 * Verificar se pelo menos um filtro foi informado além das datas inicial e
	 * final
	 * 
	 * @param paciente
	 * @param responsavel
	 * @param servico
	 * @param situacao
	 * @param tipoDocumento
	 * @throws ApplicationBusinessException
	 */
	protected void verificarFiltrosInformados(AipPacientes paciente,
			RapServidores responsavel, FccCentroCustos servico,
			DominioSituacaoVersaoDocumento situacao,
			DominioTipoDocumento tipoDocumento) throws ApplicationBusinessException {
		if (paciente == null && responsavel == null && servico == null
				&& situacao == null && tipoDocumento == null) {
			throw new ApplicationBusinessException(
					DocumentosPacienteONExceptionCode.MENSAGEM_NECESSARIO_INFORMAR_FILTRO);
		}
	}

	/**
	 * Verificar data final anterior a data inicial
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ApplicationBusinessException
	 */
	protected void verificarDataFinalBeforeDataInicial(Date dataInicial,
			Date dataFinal) throws ApplicationBusinessException {

		if (dataFinal.before(dataInicial)) {
			throw new ApplicationBusinessException(
					DocumentosPacienteONExceptionCode.MENSAGEM_DATA_FINAL_MENOR_DATA_INICIAL);
		}

	}

	/**
	 * Verificar se o período informado para a pesquisa não extrapola o
	 * parâmetro P_AGHU_NRO_MAX_DIAS_PESQ_CERT_DIGITAL. Caso o parâmetro não
	 * esteja definido, assume-se 30 dias
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @throws BaseException
	 */
	public void verificarPeriodoPesquisa(Date dataInicial, Date dataFinal)
			throws BaseException {

		Integer periodoMaximoPesquisa = this
				.obterParametroPeriodoMaximoPesquisa();

		boolean periodoPadrao = false;
					
		// Caso o parâmetro não esteja definido, utiliza-se o período padrão de
		// 30 dias
		if (periodoMaximoPesquisa == null) {						
			periodoMaximoPesquisa = this.obterPeriodoPadraoPesquisa();
			periodoPadrao = true;
		}

		long valorPeriodoMaximoEmMillis = periodoMaximoPesquisa 
				* DateUtils.MILLIS_PER_DAY;
		int intervaloDias = DateUtil.diffInDaysInteger(dataFinal,dataInicial);
		long intervaloEmMillis = intervaloDias * DateUtils.MILLIS_PER_DAY;
				
		if ((intervaloEmMillis) > valorPeriodoMaximoEmMillis) {
			if (!periodoPadrao) {
				throw new ApplicationBusinessException(
						DocumentosPacienteONExceptionCode.MENSAGEM_PERIODO_MAXIMO_PESQUISA_EXCEDIDO,
						periodoMaximoPesquisa, DateUtil.diffInDaysInteger(
								dataFinal, dataInicial));
			} else {
				throw new ApplicationBusinessException(
						DocumentosPacienteONExceptionCode.MENSAGEM_PERIODO_MAXIMO_PARAMETRO_EXCEDIDO,
						periodoMaximoPesquisa);
			}
		}
	}

	public Integer obterPeriodoMaximoPesquisa() throws BaseException {
				
		Integer periodoMaximoPesquisa = this.obterParametroPeriodoMaximoPesquisa();
		
		if (periodoMaximoPesquisa == null) {
			periodoMaximoPesquisa = PERIODO_PADRAO_PESQUISA;
		}
		
		return periodoMaximoPesquisa;
	}
	
	public Integer obterPeriodoPadraoPesquisa() throws BaseException {
		return PERIODO_PADRAO_PESQUISA;
	}

	/**
	 * Realiza a busca do parâmetro do período máximo para pesquisa
	 * 
	 * @return
	 * @throws AGHUBaseException
	 */
	public Integer obterParametroPeriodoMaximoPesquisa()
			throws BaseException {
		
		AghParametros aghParametro = this
				.getParametroFacade()
				.buscarAghParametro(
						AghuParametrosEnum.P_AGHU_NRO_MAX_DIAS_PESQ_CERT_DIGITAL);

		if (aghParametro == null) {
			throw new ApplicationBusinessException(
					DocumentosPacienteONExceptionCode.MENSAGEM_PARAMETRO_NRO_MAXIMO_PESQUISA_NAO_DEFINIDO);
		}
		
		return (aghParametro == null || aghParametro.getVlrNumerico() == null) ? null : aghParametro
				.getVlrNumerico().intValue();

	}
	
	protected void verificarHUCertificacaoDigital() throws ApplicationBusinessException {
		if (!"S".equals(getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL).getVlrTexto())) {
			throw new ApplicationBusinessException(
					DocumentosPacienteONExceptionCode.MENSAGEM_HU_NAO_POSSUI_CERTIFICACAO_DIGITAL);
		}
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public List<DominioTipoDocumento[]> listarTipoDocumentosAtivos() {
		return this.getAghDocumentoCertificadoDAO().listarTipoDocumentosAtivos();
	}
	
	protected AghDocumentoCertificadoDAO getAghDocumentoCertificadoDAO() {
		return aghDocumentoCertificadoDAO;
	}
}
