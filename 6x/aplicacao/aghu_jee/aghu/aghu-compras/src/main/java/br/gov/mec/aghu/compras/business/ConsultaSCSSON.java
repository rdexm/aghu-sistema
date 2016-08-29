package br.gov.mec.aghu.compras.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacaoServicoDAO;
import br.gov.mec.aghu.compras.dao.ScoSolicitacoesDeComprasDAO;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultaSCSSON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultaSCSSON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoSolicitacoesDeComprasDAO scoSolicitacoesDeComprasDAO;

	@Inject
	private ScoSolicitacaoServicoDAO scoSolicitacaoServicoDAO;

	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = -4338416372797483187L;

	public enum ConsultaSCSSONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CONSULTA_SC_SS_M01, MENSAGEM_ERRO_CONSULTA_SC_SS_M02, MENSAGEM_ERRO_CONSULTA_SC_SS_M03, MENSAGEM_ERRO_CONSULTA_SC_SS_M04, MENSAGEM_ERRO_CONSULTA_SC_SS_M05, MENSAGEM_ERRO_CONSULTA_SC_SS_M06, MENSAGEM_ERRO_CONSULTA_SC_SS_M07, MENSAGEM_ERRO_CONSULTA_SC_SS_M08, MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_ARG, MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_NAO_ENCONTRADO, MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_ACESSO, MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_CHAMADA, MENSAGEM_ERRO_CONSULTA_SC_ERRO_CONVERSAO_DATA;
	}
	
	public Boolean verificaCamposFiltro(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2) throws ApplicationBusinessException {
		return this.verificaCamposFiltro(filtroConsulta, ignorarMetodo1, ignorarMetodo2, null);
	}

	public Boolean verificaCamposFiltro(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2, String ignorarMetodo3)
			throws ApplicationBusinessException {
		Object arglist[] = new Object[0];	
		boolean flagResult = false;
		List<Method> Metodos = Arrays.asList(filtroConsulta.getClass().getDeclaredMethods());
		for (Method metodo : Metodos) {
			metodo.setAccessible(true);
			Class parametros[] = metodo.getParameterTypes();
			/*** Se nao recebe parametros é get ****/
			if (parametros.length == 0) {
				try {					
					
					if (verificarMetodo(metodo,ignorarMetodo1) && verificarMetodo(metodo,ignorarMetodo2) && verificarMetodo(metodo,ignorarMetodo3) ) {
						if (!(metodo.getReturnType().toString().equals(Boolean.class.toString()))) {

							if (metodo.getReturnType().cast(metodo.invoke(filtroConsulta, arglist)) != null) {
								if (metodo.getReturnType().toString().equals(String.class.toString())
										|| metodo.getReturnType().toString().equals(Date.class.toString())) {
									if (StringUtils.isNotBlank(metodo.getReturnType().cast(metodo.invoke(filtroConsulta, arglist))
											.toString())) {
										flagResult = true;
									}

								} else if (!flagResult) {
									flagResult = true;
								}
							}

						}
					}
				} catch (IllegalArgumentException e) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_ARG);
				} catch (IllegalAccessException e) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_ACESSO);

				} catch (InvocationTargetException e) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_CHAMADA);
				}

			}
		}
		return flagResult;
	}

	public boolean verificarMetodo(Method metodo, String ignorarMetodo){
		return (StringUtils.isNotBlank(ignorarMetodo)) ? !metodo.getName().equals(ignorarMetodo) : true;
	}
	
	public Boolean verificaCamposFiltroData(FiltroConsSCSSVO filtroConsulta, String ignorarMetodo1, String ignorarMetodo2)
			throws ApplicationBusinessException {
		Object arglist[] = new Object[0];
		boolean flagResult = false;
		boolean flagTestarMetodo1 = true;
		boolean flagTestarMetodo2 = true;
		List<Method> Metodos = Arrays.asList(filtroConsulta.getClass().getDeclaredMethods());
		for (Method metodo : Metodos) {
			metodo.setAccessible(true);
			Class parametros[] = metodo.getParameterTypes();
			/*** Se nao recebe parametros é get ***/
			if (parametros.length == 0) {
				try {

					if ((metodo.getReturnType().toString().equals(Date.class.toString()))) {

						flagTestarMetodo1 = (StringUtils.isNotBlank(ignorarMetodo1)) ? !metodo.getName().equals(ignorarMetodo1) : true;

						flagTestarMetodo2 = (StringUtils.isNotBlank(ignorarMetodo2)) ? !metodo.getName().equals(ignorarMetodo2) : true;

						if (flagTestarMetodo1 && flagTestarMetodo2) {
							if (metodo.getReturnType().cast(metodo.invoke(filtroConsulta, arglist)) != null) {
								if (StringUtils.isNotBlank(metodo.getReturnType().cast(metodo.invoke(filtroConsulta, arglist)).toString())) {
									return true;
								}
							}
						}
					}
				} catch (IllegalArgumentException e) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_ARG);
				} catch (IllegalAccessException e) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_ACESSO);

				} catch (InvocationTargetException e) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_METODO_CHAMADA);
				}

			}
		}
		return flagResult;
	}

	public boolean verificaPesquisarPAC(FiltroConsSCSSVO filtro) {

		String dtInicioAberPAC = (filtro.getDataInicioAberturaPropostaPAC() != null ? filtro.getDataInicioAberturaPropostaPAC().toString()
				: null);
		String dtFimAberPAC = (filtro.getDataFimAberturaPropostaPAC() != null ? filtro.getDataFimAberturaPropostaPAC().toString() : null);

		return (filtro.getNumeroPAC() != null || StringUtils.isNotBlank(filtro.getDescricaoPAC())
				|| StringUtils.isNotBlank(dtInicioAberPAC) || StringUtils.isNotBlank(dtFimAberPAC) || filtro.getExclusaoPAC() != null
				|| filtro.getNumDocPAC() != null || filtro.getNumEditalPAC() != null || filtro.getAnoComplementoPAC() != null
				|| filtro.getModalidadeLicitacaoPAC() != null || filtro.getTipoPregaoPAC() != null || filtro.getArtigoPAC() != null
				|| StringUtils.isNotBlank(filtro.getIncisoArtigoPAC()) || filtro.getTipoPAC() != null || filtro.getServidorGestorPAC() != null);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public boolean verificaPesquisarAF(FiltroConsSCSSVO filtro) {

		String dtInicioGerAF = (filtro.getDataInicioGeracaoAF() != null ? filtro.getDataInicioGeracaoAF().toString() : null);
		String dtFimGerAF = (filtro.getDataFimGeracaoAF() != null ? filtro.getDataFimGeracaoAF().toString() : null);
		String dtInicioPrevEntregaAF = (filtro.getDataInicioPrevEntregaAF() != null ? filtro.getDataInicioPrevEntregaAF().toString() : null);
		String dtFimPrevEntregaAF = (filtro.getDataFimPrevEntregaAF() != null ? filtro.getDataFimPrevEntregaAF().toString() : null);

		return (filtro.getNumeroAF() != null || filtro.getNroComplementoAF() != null || StringUtils.isNotBlank(dtInicioGerAF)
				|| StringUtils.isNotBlank(dtFimGerAF) || StringUtils.isNotBlank(dtInicioPrevEntregaAF)
				|| StringUtils.isNotBlank(dtFimPrevEntregaAF) || filtro.getExclusaoAF() != null || filtro.getFornecedorAF() != null
				|| StringUtils.isNotBlank(filtro.getNroEmpenhoAF()) || filtro.getNroContratoAF() != null || filtro.getServidorGestorAF() != null);

	}

	public void validaFiltroPesquisaDatas(Date dataInicio, Date dataFim, ConsultaSCSSONExceptionCode mensagemErro)
			throws ApplicationBusinessException {

		if (dataInicio != null && dataFim != null) {
			if (dataFim.before(dataInicio)) {
				throw new ApplicationBusinessException(mensagemErro);
			}
		}
	}

	public void validaFiltroPesquisa(FiltroConsSCSSVO filtroConsulta) throws ApplicationBusinessException {

		validaFiltroPesquisaDatas(filtroConsulta.getDataInicioSolicitacao(), filtroConsulta.getDataFimSolicitacao(),
				ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M01);

		validaFiltroPesquisaDatas(filtroConsulta.getDataInicioAutorizacao(), filtroConsulta.getDataFimAutorizacao(),
				ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M03);

		validaFiltroPesquisaDatas(filtroConsulta.getDataInicioAnalise(), filtroConsulta.getDataFimAnalise(),
				ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M04);

		validaFiltroPesquisaDatas(filtroConsulta.getDataInicioAberturaPropostaPAC(), filtroConsulta.getDataFimAberturaPropostaPAC(),
				ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M05);

		validaFiltroPesquisaDatas(filtroConsulta.getDataInicioGeracaoAF(), filtroConsulta.getDataFimGeracaoAF(),
				ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M06);

		validaFiltroPesquisaDatas(filtroConsulta.getDataInicioPrevEntregaAF(), filtroConsulta.getDataFimPrevEntregaAF(),
				ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M07);

		if (filtroConsulta.getNumero() == null && filtroConsulta.getNumeroPAC() == null && filtroConsulta.getNumeroAF() == null) {

			Integer diasPesquisa = this.buscaParametroDiasPesquisa();

			this.validaData(filtroConsulta.getDataInicioSolicitacao(), filtroConsulta.getDataFimSolicitacao(), diasPesquisa);
			this.validaData(filtroConsulta.getDataInicioAutorizacao(), filtroConsulta.getDataFimAutorizacao(), diasPesquisa);
			this.validaData(filtroConsulta.getDataInicioAnalise(), filtroConsulta.getDataFimAnalise(), diasPesquisa);
			this.validaData(filtroConsulta.getDataInicioAberturaPropostaPAC(), filtroConsulta.getDataFimAberturaPropostaPAC(), diasPesquisa);
			this.validaData(filtroConsulta.getDataInicioGeracaoAF(), filtroConsulta.getDataFimGeracaoAF(), diasPesquisa);
			this.validaData(filtroConsulta.getDataInicioPrevEntregaAF(), filtroConsulta.getDataFimPrevEntregaAF(), diasPesquisa);

		}
		if (!this.verificaCamposFiltro(filtroConsulta, null, null)) {
			throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M02);
		}

	}

	public Date getDataInicio(Date dataInicio, Date dataFim, FiltroConsSCSSVO filtroConsulta, Integer quantidadeDiasConsultaSCSS)
			throws ParseException {

		Calendar calPeriodoInicio = Calendar.getInstance();
		
		SimpleDateFormat formatadorTime = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS);
		SimpleDateFormat formatadorDia = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY);

		if (dataInicio == null && dataFim != null && filtroConsulta.getNumero() == null && filtroConsulta.getNumeroPAC() == null
				&& filtroConsulta.getNumeroAF() == null) {

			calPeriodoInicio.setTime(dataFim);
			calPeriodoInicio.add(Calendar.DAY_OF_MONTH, (quantidadeDiasConsultaSCSS * -1));
			dataInicio = calPeriodoInicio.getTime();
		}

		return (dataInicio != null) ? formatadorTime.parse(formatadorDia.format(dataInicio) + " 00:00:00") : dataInicio;

	}

	public Date getDataFim(Date dataInicio, Date dataFim, FiltroConsSCSSVO filtroConsulta, Integer quantidadeDiasConsultaSCSS)
			throws ParseException {

		Calendar calPeriodoInicio = Calendar.getInstance();
		SimpleDateFormat formatadorTime = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDOS);
		SimpleDateFormat formatadorDia = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY);

		if (dataInicio != null && dataFim == null && filtroConsulta.getNumero() == null && filtroConsulta.getNumeroPAC() == null
				&& filtroConsulta.getNumeroAF() == null) {

			calPeriodoInicio.setTime(dataInicio);
			calPeriodoInicio.add(Calendar.DAY_OF_MONTH, quantidadeDiasConsultaSCSS);
			dataFim = calPeriodoInicio.getTime();

		}

		return (dataFim != null) ? formatadorTime.parse(formatadorDia.format(dataFim) + " 23:59:59") : dataFim;

	}

	public void validaData(Date dataInicio, Date dataFim, Integer quantidadeDiasConsultaSCSS) throws ApplicationBusinessException {

		if (dataFim != null && dataInicio != null) {
			Calendar calPeriodoInicio = Calendar.getInstance();
			calPeriodoInicio.setTime(dataFim);
			calPeriodoInicio.add(Calendar.DAY_OF_MONTH, (quantidadeDiasConsultaSCSS * -1));
			Date dataInicioCalculada = calPeriodoInicio.getTime();

			if (dataInicioCalculada != null) {
				if (dataInicio.before(dataInicioCalculada)) {
					throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_SS_M08,
							quantidadeDiasConsultaSCSS);
				}
			}
		}
	}

	public Integer buscaParametroDiasPesquisa() throws ApplicationBusinessException {
		AghParametros diasPermitidosSCSS = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PESQUISA_COMPRAS);

		return diasPermitidosSCSS.getVlrNumerico().intValue();

	}

	public void setaValoresDefaults(FiltroConsSCSSVO filtro) throws ApplicationBusinessException {

		if (filtro.getNumero() == null && filtro.getNumeroPAC() == null && filtro.getNumeroAF() == null) {
			Integer diasPesquisa = this.buscaParametroDiasPesquisa();
			try {
				if (filtro.getDataInicioSolicitacao() == null && filtro.getDataFimSolicitacao() == null
						&& filtro.getDataInicioAutorizacao() == null && filtro.getDataFimAutorizacao() == null
						&& filtro.getDataInicioAnalise() == null && filtro.getDataFimAnalise() == null
						&& filtro.getDataInicioAberturaPropostaPAC() == null && filtro.getDataFimAberturaPropostaPAC() == null
						&& filtro.getDataInicioGeracaoAF() == null && filtro.getDataFimGeracaoAF() == null
						&& filtro.getDataInicioPrevEntregaAF() == null && filtro.getDataFimPrevEntregaAF() == null) {

					filtro.setDataInicioSolicitacao(this.getDataInicio(null, new Date(), filtro, diasPesquisa));
					filtro.setDataFimSolicitacao(this.getDataFim(null, new Date(), filtro, 0));

				} else {

					filtro.setDataInicioSolicitacao(this.getDataInicio(filtro.getDataInicioSolicitacao(), filtro.getDataFimSolicitacao(),
							filtro, diasPesquisa));
					filtro.setDataFimSolicitacao(this.getDataFim(filtro.getDataInicioSolicitacao(), filtro.getDataFimSolicitacao(), filtro,
							diasPesquisa));
					filtro.setDataInicioAutorizacao(this.getDataInicio(filtro.getDataInicioAutorizacao(), filtro.getDataFimAutorizacao(),
							filtro, diasPesquisa));
					filtro.setDataFimAutorizacao(this.getDataFim(filtro.getDataInicioAutorizacao(), filtro.getDataFimAutorizacao(), filtro,
							diasPesquisa));
					filtro.setDataInicioAnalise(this.getDataInicio(filtro.getDataInicioAnalise(), filtro.getDataFimAnalise(), filtro,
							diasPesquisa));
					filtro.setDataFimAnalise(this.getDataFim(filtro.getDataInicioAnalise(), filtro.getDataFimAnalise(), filtro,
							diasPesquisa));
					filtro.setDataInicioAberturaPropostaPAC(this.getDataInicio(filtro.getDataInicioAberturaPropostaPAC(),
							filtro.getDataFimAberturaPropostaPAC(), filtro, diasPesquisa));
					filtro.setDataFimAberturaPropostaPAC(this.getDataFim(filtro.getDataInicioAberturaPropostaPAC(),
							filtro.getDataFimAberturaPropostaPAC(), filtro, diasPesquisa));
					filtro.setDataInicioGeracaoAF(this.getDataInicio(filtro.getDataInicioGeracaoAF(), filtro.getDataFimGeracaoAF(), filtro,
							diasPesquisa));
					filtro.setDataFimGeracaoAF(this.getDataFim(filtro.getDataInicioGeracaoAF(), filtro.getDataFimGeracaoAF(), filtro,
							diasPesquisa));
					filtro.setDataInicioPrevEntregaAF(this.getDataInicio(filtro.getDataInicioPrevEntregaAF(),
							filtro.getDataFimPrevEntregaAF(), filtro, diasPesquisa));
					filtro.setDataFimPrevEntregaAF(this.getDataFim(filtro.getDataInicioPrevEntregaAF(), filtro.getDataFimPrevEntregaAF(),
							filtro, diasPesquisa));

				}
			} catch (ParseException ex) {
				throw new ApplicationBusinessException(ConsultaSCSSONExceptionCode.MENSAGEM_ERRO_CONSULTA_SC_ERRO_CONVERSAO_DATA);
			}
		}
		filtro.setPesquisarPorPAC(this.verificaPesquisarPAC(filtro));
		filtro.setPesquisarPorAF(this.verificaPesquisarAF(filtro));

	}

	public Long listarSCItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSC) {
		return this.getScoSolicitacoesDeComprasDAO().listarSCItensSCSSVOCount(filtroConsultaSC);
	}

	public Long listarSSItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSC) {
		return this.getScoSolicitacaoServicoDAO().listarSSItensSCSSVOCount(filtroConsultaSC);
	}

	public void desatacharSolicitacaoPorListaItensVO(List<ItensSCSSVO> listaItensSCSSVO) {
		for (ItensSCSSVO itemSCSSVO : listaItensSCSSVO) {
			if (itemSCSSVO.getNumero() != null) {
				ScoSolicitacaoDeCompra sC = getScoSolicitacoesDeComprasDAO().obterPorChavePrimaria(itemSCSSVO.getNumero());
				if (sC != null) {
					getScoSolicitacoesDeComprasDAO().desatachar(sC);
				}

				ScoSolicitacaoServico sS = getScoSolicitacaoServicoDAO().obterPorChavePrimaria(itemSCSSVO.getNumero());
				if (sS != null) {
					getScoSolicitacaoServicoDAO().desatachar(sS);
				}
			}
		}
	}

	public List<ItensSCSSVO> montaSCItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FiltroConsSCSSVO filtroConsultaSC) {

		List<ItensSCSSVO> listaItensSCSSVO = this.getScoSolicitacoesDeComprasDAO().listarSCItensSCSSVO(firstResult, maxResults,
				orderProperty, asc, filtroConsultaSC);

		for (ItensSCSSVO item : listaItensSCSSVO) {
			item.setTipoSolicitacao(DominioTipoSolicitacao.SC);
			item.setHintDtSolicitacao(obterHintDtSolicitacao(item));
			if (item.getExclusao()) {
				item.setHintDtExclusao(obterHintExclusao(item));
			}
			if (item.getNumeroAf() != null) {
				item.setNumeroEComplementoAf(obterNumeroAf(item));
			}
		}

		return listaItensSCSSVO;
	}

	private String obterNumeroAf(final ItensSCSSVO item) {
		StringBuilder str = new StringBuilder();
		return str.append(item.getNumeroAf()).append("/").append(item.getComplementoAf()).toString();
	}

	private String obterHintDtSolicitacao(final ItensSCSSVO item) {
		StringBuilder str = new StringBuilder(18);
		str.append("Solicitante: ").append(item.getNomeServidorGeracao()).append("<br/>");

		if (item.getDataAutorizacao() != null) {
			str.append("Autorizador: ").append(item.getNomeServidorAutorizador()).append(" em ")
					.append(DateUtil.dataToString(item.getDataAutorizacao(), DateConstants.DATE_PATTERN_DDMMYYYY));
		}

		return str.toString();
	}

	private String obterHintExclusao(final ItensSCSSVO item) {
		StringBuilder str = new StringBuilder();
		str.append(item.getNomeServidorExclusao()).append(" em ").append(DateUtil.dataToString(item.getDataExclusao(), DateConstants.DATE_PATTERN_DDMMYYYY));
		return str.toString();
	}

	public List<ItensSCSSVO> montaSSItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			FiltroConsSCSSVO filtroConsultaSS) {
		List<ItensSCSSVO> listaItensSCSSVO = this.getScoSolicitacaoServicoDAO().listarSSItensSCSSVO(firstResult, maxResults, orderProperty,
				asc, filtroConsultaSS);

		for (ItensSCSSVO item : listaItensSCSSVO) {
			item.setTipoSolicitacao(DominioTipoSolicitacao.SS);
			item.setHintDtSolicitacao(obterHintDtSolicitacao(item));
			if (item.getExclusao()) {
				item.setHintDtExclusao(obterHintExclusao(item));
			}
			if (item.getNumeroAf() != null) {
				item.setNumeroEComplementoAf(obterNumeroAf(item));
			}
		}

		return listaItensSCSSVO;
	}

	public List<ScoAutorizacaoForn> pesquisarAutorizacaoForn(Integer numero, DominioTipoSolicitacao tipoSolicitacao) {
		List<ScoFaseSolicitacao> listaFaseSolicitacao = this.getScoFaseSolicitacaoDAO().obterFaseSolicitacao(numero, true, tipoSolicitacao);

		List<ScoAutorizacaoForn> listaAutorizacaoForn = new ArrayList<ScoAutorizacaoForn>();

		for (ScoFaseSolicitacao faseSolicitacao : listaFaseSolicitacao) {
			listaAutorizacaoForn.add(faseSolicitacao.getItemAutorizacaoForn().getAutorizacoesForn());
		}
		return listaAutorizacaoForn;
	}

	private ScoSolicitacoesDeComprasDAO getScoSolicitacoesDeComprasDAO() {
		return scoSolicitacoesDeComprasDAO;
	}

	private ScoSolicitacaoServicoDAO getScoSolicitacaoServicoDAO() {
		return scoSolicitacaoServicoDAO;
	}

	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}