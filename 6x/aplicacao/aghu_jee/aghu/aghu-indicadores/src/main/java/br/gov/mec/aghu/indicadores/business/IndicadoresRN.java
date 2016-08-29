package br.gov.mec.aghu.indicadores.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.indicadores.vo.CapacidadeEspecialidadeIndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.ClinicaIndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.IndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.LeitoIndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.LeitosBloqueadosIndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.LeitosDesativadosIndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.MovimentoInternacaoIndicadoresVO;
import br.gov.mec.aghu.indicadores.vo.UnidadeFuncionalIndicadoresVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinIndicadoresHospitalares;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * RN Geral para Indicadores.
 * 
 * @author riccosta / evschneider
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.SuspiciousConstantFieldName"})
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class IndicadoresRN extends BaseBMTBusiness {

	
	private static final String PRIVATIVO = "PRIVATIVO";

	@EJB
	private IndicadoresHospitalaresUtilRN indicadoresHospitalaresUtilRN;
	
	private static final Log LOG = LogFactory.getLog(IndicadoresRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	private static final long serialVersionUID = 3928961667873228663L;

	protected IndicadoresHospitalaresUtilRN getIndicadoresHospitalaresUtilRN() {
		return indicadoresHospitalaresUtilRN;
	}

	static Date MES_COMPETENCIA;
	static Date DTHR_FIM_MES;
	static Integer X_DIAS_MES;
	static IndicadoresVO indicadoresVO;

	static {
		MES_COMPETENCIA = null;
		DTHR_FIM_MES = null;
		X_DIAS_MES = null;
		indicadoresVO = new IndicadoresVO();
	}

	/*
	 * static { indicadoresVO = new IndicadoresVO();
	 * 
	 * Calendar mesComp = Calendar.getInstance(); //FIXME retirar essa data
	 * setada manualmente daqui!!!! mesComp.set(2010,6,1);
	 * 
	 * mesComp.add(Calendar.MONTH, -1); mesComp.set(Calendar.DAY_OF_MONTH, 1);
	 * mesComp.set(Calendar.HOUR_OF_DAY, 00); mesComp.set(Calendar.MINUTE, 00);
	 * mesComp.set(Calendar.SECOND, 00);
	 * 
	 * MES_COMPETENCIA = mesComp.getTime();
	 * 
	 * mesComp.set(Calendar.DAY_OF_MONTH, mesComp
	 * .getActualMaximum(Calendar.DAY_OF_MONTH));
	 * mesComp.set(Calendar.HOUR_OF_DAY, 23); mesComp.set(Calendar.MINUTE, 59);
	 * mesComp.set(Calendar.SECOND, 59); DTHR_FIM_MES = mesComp.getTime();
	 * 
	 * X_DIAS_MES = mesComp.getActualMaximum(Calendar.DAY_OF_MONTH);
	 * 
	 * // delete from ain_indicadores_hospitalares where competencia_internacao
	 * = to_date(v_mes_comp,'mmyyyy'); }
	 */

	public enum IndicadoresRNCode implements BusinessExceptionCode {
		ERRO_PARAMETRO
	}

	/**
	 * ORADB AINP_GERA_IND_HOSP
	 */
	public void gerarIndicadoresHospitalares(Date anoMesCompetencia) throws ApplicationBusinessException {
		try {
			this.beginTransaction(3*60*60);	// 3 horas

			Calendar cal1 = Calendar.getInstance();
			indicadoresVO.setCountLog(0);
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			
			LOG.info("Inicio da geração de indicadores - " + sdf.format(new Date()));

			// INICIO -----------------------------
			// TODO LimparEntityManager está no GenericDAO.
			// TODOmigracao
			//this.getAghuFacade().limparEntityManagerUnidadesFuncionais();
			
			LOG.info("Chamada para método inicializarCompetencia(anoMesCompetencia)");
			inicializarCompetencia(anoMesCompetencia);
			
			LOG.info("Chamada para método carregarCapacidadeUnidades()");
			carregarCapacidadeUnidades();
			
			LOG.info("Chamada para método carregarCapacidadeUnidadesPriv()");
			carregarCapacidadeUnidadesPriv();
			
			LOG.info("Chamada para método carregarCapacidadeClinica()");
			carregarCapacidadeClinica();
			
			LOG.info("Chamada para método carregarCapacidadeEspecialidade()");
			carregarCapacidadeEspecialidade();
			
			LOG.info("Chamada para método carregarBloqueios()");
			carregarBloqueios();
			
			LOG.info("Chamada para método carregarDesativados()");
			carregarDesativados();
			
			LOG.info("Chamada para método carregarControleMovimentos()");
			carregarControleMovimentos();
			
			LOG.info("Chamada para método corrigirCapacidades()");
			corrigirCapacidades();
			
			LOG.info("Chamada para método gravarIndicadores()");
			gravarIndicadores();
			// FIM --------------------------------

			for (String valor : indicadoresVO.getValoresLog()) {
				LOG.info(valor);
			}

			Calendar cal2 = Calendar.getInstance();
			long tempo = cal2.getTimeInMillis() - cal1.getTimeInMillis();
			LOG.info("Tempo de execução em minutos = " + tempo / 1000 / 60);

			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			LOG.info("Mes competencia da geração = " + sdf.format(MES_COMPETENCIA.getTime()));
			LOG.info("Dias do mes da competencia= " + X_DIAS_MES);

			// commit
			super.commitTransaction();
		} catch (ApplicationBusinessException e) {
			super.rollbackTransaction();
			throw e;
		}
	}

	/**
	 * ORADB inicializa_comp
	 * 
	 * @param anoMesCompetencia
	 */
	private void inicializarCompetencia(Date anoMesCompetencia) {
		indicadoresVO = new IndicadoresVO();

		Calendar mesComp = Calendar.getInstance();

		// Se a geração dos indicadores for feita via tela (com competencia
		// informada), gera do mês informado.
		// Se a geração dos indicadores for feita via tarefa agendada, diminui
		// um mês para gerar os indicadores do mês anterior.
		if (anoMesCompetencia != null) {
			mesComp.setTime(anoMesCompetencia);
		} else {
			mesComp.add(Calendar.MONTH, -1);
		}

		mesComp.set(Calendar.DAY_OF_MONTH, 1);
		mesComp.set(Calendar.HOUR_OF_DAY, 00);
		mesComp.set(Calendar.MINUTE, 00);
		mesComp.set(Calendar.SECOND, 00);

		MES_COMPETENCIA = mesComp.getTime();

		mesComp.set(Calendar.DAY_OF_MONTH, mesComp.getActualMaximum(Calendar.DAY_OF_MONTH));
		mesComp.set(Calendar.HOUR_OF_DAY, 23);
		mesComp.set(Calendar.MINUTE, 59);
		mesComp.set(Calendar.SECOND, 59);
		DTHR_FIM_MES = mesComp.getTime();

		X_DIAS_MES = mesComp.getActualMaximum(Calendar.DAY_OF_MONTH);

		Date anoMesComp = null;
		if (anoMesCompetencia != null) {
			anoMesComp = anoMesCompetencia;
		} else {
			anoMesComp = MES_COMPETENCIA;
		}

		getInternacaoFacade().removerPorCompetencia(anoMesComp);

	}

	/**
	 * ORADB carga_capacidade_unid
	 */
	private void carregarCapacidadeUnidades() {

		List<Object> lista = getAghuFacade().obterCapacUnid();

		UnidadeFuncionalIndicadoresVO unidadeFuncionalIndicadoresVO = null;

		for (Object unidadeFuncional : lista) {
			indicadoresVO.incrIndUnid();

			unidadeFuncionalIndicadoresVO = new UnidadeFuncionalIndicadoresVO();
			Object[] valores = (Object[]) unidadeFuncional;

			unidadeFuncionalIndicadoresVO
					.setUnidade(valores[0] == null ? null : (Short) valores[0]);
			unidadeFuncionalIndicadoresVO.setCapacidade(valores[1] == null ? null
					: (Short) valores[1]);
			unidadeFuncionalIndicadoresVO.setClinica(valores[2] == null ? null
					: (Integer) valores[2]);
			unidadeFuncionalIndicadoresVO.setBloqueios(BigDecimal.valueOf(0));
			unidadeFuncionalIndicadoresVO.setBlqDesat(0);

			indicadoresVO.getTabUnid().put(unidadeFuncionalIndicadoresVO.getUnidade(),
					unidadeFuncionalIndicadoresVO);
		}

		indicadoresVO.setLstUnid(indicadoresVO.getIndUnid());
	}

	
	/**
	 * ORADB carga_capac_unid_priv
	 */
	private void carregarCapacidadeUnidadesPriv() {

		// Lista dos leitos contendo somente unfSeq, capacidade
		List<AinLeitos> lista = getInternacaoFacade().obterCapacUnidPriv();

		List<Short> leitosUnidadesPrivadas = new ArrayList<Short>();

		// Aplica restrição na lista para depois fazer o agrupamento
		for (AinLeitos ainLeitos : lista) {
			if ("S".equalsIgnoreCase(getInternacaoFacade().obterCaracteristicaLeito(
					ainLeitos.getLeitoID(), PRIVATIVO))) {
				leitosUnidadesPrivadas.add(ainLeitos.getUnidadeFuncional().getSeq());
			}
		}

		// Agrupa lista e incrementa indUnidPriv para cada registro
		this.agruparLeitosPrivados(leitosUnidadesPrivadas);

		indicadoresVO.setLstUnidPriv(indicadoresVO.getIndUnidPriv());
	}

	
	/**
	 * Método para agrupar os leitos com unidade funcional privada.
	 * 
	 * @param leitosunidadesPrivadas
	 * @return VO com unfSeq, capacidade e bloqueados
	 */
	private Set<LeitoIndicadoresVO> agruparLeitosPrivados(List<Short> leitosUnidadesPrivadas) {
		Set<LeitoIndicadoresVO> leitosAgrupados = new HashSet<LeitoIndicadoresVO>();

		if (leitosUnidadesPrivadas != null && !leitosUnidadesPrivadas.isEmpty()) {
			Predicate predicadoIgualdade = null;
			int contador = 0;
			LeitoIndicadoresVO elemento = null;

			for (Short unfSeq : leitosUnidadesPrivadas) {
				indicadoresVO.incrIndUnidPriv();

				predicadoIgualdade = new EqualPredicate(unfSeq);
				contador = CollectionUtils.countMatches(leitosUnidadesPrivadas, predicadoIgualdade);

				elemento = new LeitoIndicadoresVO(unfSeq, contador, BigDecimal.valueOf(0));

				if (!leitosAgrupados.contains(elemento)) {
					leitosAgrupados.add(elemento);
				}
			}
		}

		return leitosAgrupados;
	}

	
	/**
	 * @ORADB Cursor carga_capacidade_clinica
	 */
	private void carregarCapacidadeClinica() {

		List<AghClinicas> lista = getAghuFacade().obterTodasClinicas();

		ClinicaIndicadoresVO clinicaIndicadoresVO = null;
		for (AghClinicas aghClinicas : lista) {
			clinicaIndicadoresVO = new ClinicaIndicadoresVO();

			clinicaIndicadoresVO.setClinica(aghClinicas.getCodigo());
			clinicaIndicadoresVO.setCapacidade(aghClinicas.getCapacReferencial());
			clinicaIndicadoresVO.setBloqueios(BigDecimal.valueOf(0));
			clinicaIndicadoresVO.setBlqDesat(0);
			clinicaIndicadoresVO.setCapcMais(0);
			clinicaIndicadoresVO.setCapcMenos(0);
			indicadoresVO.getTabClinicas().put(aghClinicas.getCodigo(), clinicaIndicadoresVO);
		}
	}

	/**
	 * @ORADB carga_capacidade_esp
	 */
	private void carregarCapacidadeEspecialidade() {

		List<AghEspecialidades> lista = getAghuFacade().carregarCapacidadeEspecialidade();

		CapacidadeEspecialidadeIndicadoresVO capacidadeEspecialidadeIndicadoresVO = null;
		for (AghEspecialidades especialidade : lista) {
			indicadoresVO.incrIndEsp();
			capacidadeEspecialidadeIndicadoresVO = new CapacidadeEspecialidadeIndicadoresVO();
			capacidadeEspecialidadeIndicadoresVO.setEsp(especialidade.getSeq());
			capacidadeEspecialidadeIndicadoresVO.setClinica(especialidade.getClinica().getCodigo());
			capacidadeEspecialidadeIndicadoresVO.setCapacidade(especialidade.getCapacReferencial());
			capacidadeEspecialidadeIndicadoresVO.setBloqueios(0);
			indicadoresVO.getTabEsp().put(especialidade.getSeq(),
					capacidadeEspecialidadeIndicadoresVO);
		}
		indicadoresVO.setLstEsp(indicadoresVO.getIndEsp());
	}

	/**
	 * Método para pegar o ultimo dia do mes atual (MES_COMPETENCIA) e somar um
	 * dia no mesmo
	 * 
	 * @return
	 */
	private Date obterDiaMaximoMesMaisUm() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(MES_COMPETENCIA);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		cal.add(Calendar.DAY_OF_MONTH, 1);

		return DateUtils.truncate(cal.getTime(), Calendar.DAY_OF_MONTH);
	}

	/**
	 * Método para formatar os campos retornados na primeira query do UNION do
	 * cursor "l_desativados"
	 * 
	 * @return
	 */
	private List<LeitosDesativadosIndicadoresVO> pesquisarQuery1LeitosDesativados() {
		List<LeitosDesativadosIndicadoresVO> leitoDesativadoList = new ArrayList<LeitosDesativadosIndicadoresVO>();

		// Codigo para representar primeira query do UNION
		List<AinLeitos> leitoList = this.getInternacaoFacade().pesquisarLeitosAtivos();
		LeitosDesativadosIndicadoresVO leitoDesativado = null;

		for (AinLeitos leito : leitoList) {
			leitoDesativado = new LeitosDesativadosIndicadoresVO();

			leitoDesativado.setUnfSeq(leito.getUnidadeFuncional().getSeq());
			leitoDesativado.setLtoId(leito.getLeitoID());
			leitoDesativado.setIndBlq(getInternacaoFacade().obterSituacaoPacienteLD(leito.getLeitoID(),
					MES_COMPETENCIA));
			leitoDesativado.setDataI(getInternacaoFacade().obterDataInicialLD(leito.getLeitoID(),
					MES_COMPETENCIA));

			// Código para setar a variável "data_f" da primeira query do UNION
			Date dataInicialDesativacao = null;
			if (leitoDesativado.getDataI() != null) {
				dataInicialDesativacao = getInternacaoFacade().obterDataFinalLancamentoLD(
						leito.getLeitoID(), MES_COMPETENCIA);
				if (dataInicialDesativacao == null) {
					dataInicialDesativacao = this.obterDiaMaximoMesMaisUm();
				}
			}
			leitoDesativado.setDataF(dataInicialDesativacao);

			// Código para setar a variável "dias" da primeira query do UNION
			Date dataInicial = getInternacaoFacade().obterDataInicialLD(leito.getLeitoID(),
					MES_COMPETENCIA);
			Date dataFinal = null;

			if (dataInicial != null) {
				dataFinal = (Date) dataInicial.clone();
				dataInicial = getInternacaoFacade().obterDataFinalLancamentoLD(leito.getLeitoID(),
						MES_COMPETENCIA);
				if (dataInicial == null) {
					dataInicial = this.obterDiaMaximoMesMaisUm();
				}
			}

			int dias = br.gov.mec.aghu.core.utils.DateUtil.calcularDiasEntreDatas(dataInicial, dataFinal);
			leitoDesativado.setDias(dias);

			leitoDesativadoList.add(leitoDesativado);
		}

		return leitoDesativadoList;
	}

	/**
	 * Método para formatar os campos retornados na segunda query do UNION do
	 * cursor "l_desativados"
	 * 
	 * @return
	 */
	private List<LeitosDesativadosIndicadoresVO> pesquisarQuery2LeitosDesativados() {
		List<LeitosDesativadosIndicadoresVO> leitoDesativadoList = new ArrayList<LeitosDesativadosIndicadoresVO>();

		// Codigo para representar segunda query do UNION
		List<AinExtratoLeitos> extratoLeitoList = this.getInternacaoFacade()
				.pesquisarExtratosLeito(MES_COMPETENCIA);
		LeitosDesativadosIndicadoresVO leitoDesativadoVO = null;

		for (AinExtratoLeitos extratoLeito : extratoLeitoList) {
			leitoDesativadoVO = new LeitosDesativadosIndicadoresVO();
			leitoDesativadoVO.setUnfSeq(extratoLeito.getLeito().getUnidadeFuncional().getSeq());
			leitoDesativadoVO.setLtoId(extratoLeito.getLeito().getLeitoID());
			leitoDesativadoVO.setIndBlq(extratoLeito.getTipoMovimentoLeito()
					.getIndBloqueioPaciente());
			leitoDesativadoVO.setDataI(extratoLeito.getDthrLancamento());

			// Código para setar a variável "data_f" da segunda query do UNION
			Date dataFinal = getInternacaoFacade().obterDataFinalLancamentoLD(extratoLeito.getLeito()
					.getLeitoID(), extratoLeito.getDthrLancamento());
			if (dataFinal == null) {
				leitoDesativadoVO.setDataF(this.obterDiaMaximoMesMaisUm());
			} else {
				leitoDesativadoVO.setDataF(dataFinal);
			}

			// Código para setar a variável "dias" da segunda query do UNION
			if (dataFinal == null) {
				leitoDesativadoVO.setDias(DateUtil.calcularDiasEntreDatas(
						this.obterDiaMaximoMesMaisUm(), extratoLeito.getDthrLancamento()));
			} else {
				leitoDesativadoVO.setDias(DateUtil.calcularDiasEntreDatas(dataFinal,
						extratoLeito.getDthrLancamento()));
			}
			leitoDesativadoList.add(leitoDesativadoVO);
		}

		return leitoDesativadoList;
	}

	/**
	 * Método que implementa o cursor "l_desativados"
	 * 
	 * @return
	 */
	private List<LeitosDesativadosIndicadoresVO> pesquisarLeitosDesativados() {
		List<LeitosDesativadosIndicadoresVO> leitoDesativadoList = new ArrayList<LeitosDesativadosIndicadoresVO>();
		leitoDesativadoList.addAll(this.pesquisarQuery1LeitosDesativados());
		leitoDesativadoList.addAll(this.pesquisarQuery2LeitosDesativados());

		class OrdenacaoLeitosDesativados implements Comparator<LeitosDesativadosIndicadoresVO> {

			@Override
			public int compare(LeitosDesativadosIndicadoresVO o1, LeitosDesativadosIndicadoresVO o2) {

				if (o1.getUnfSeq() == null) {
					return -1;
				} else if (o2.getUnfSeq() == null) {
					return 1;
				} else {
					if (o1.getUnfSeq() < o2.getUnfSeq()) {
						return -1;
					} else if (o1.getUnfSeq() > o2.getUnfSeq()) {
						return 1;
					} else {
						// Se unidades forem iguais, compara leito
						return o1.getLtoId().compareTo(o2.getLtoId());
					}
				}
			}
		}

		// CoreUtil.ordenarLista(leitoDesativadoList, "unfSeq", true);
		Collections.sort(leitoDesativadoList, new OrdenacaoLeitosDesativados());

		return leitoDesativadoList;
	}

	/**
	 * Método que implementa o método "carga_desativados"
	 * 
	 * @ORADB Procedure AINP_GERA_IND_HOSP.CARGA_DESATIVADOS
	 * 
	 */
	private void carregarDesativados() {
		List<LeitosDesativadosIndicadoresVO> leitoDesativadoList = this
				.pesquisarLeitosDesativados();

		// Variável usada dentro do loop "for"
		UnidadeFuncionalIndicadoresVO unfIndicadoresVO = null;
		Integer codigoClinica = null;

		// Código equivalente ao loop "for rec in l_desativados"
		for (LeitosDesativadosIndicadoresVO leitoDesativadoVO : leitoDesativadoList) {
			if (leitoDesativadoVO.getDias() != null) {
				indicadoresVO.setIndDesat(indicadoresVO.getIndDesat() + 1);
				indicadoresVO.getTabDesat().put(leitoDesativadoVO.getUnfSeq(), leitoDesativadoVO);

				unfIndicadoresVO = indicadoresVO.getTabUnid().get(leitoDesativadoVO.getUnfSeq());

				// Código equivalente a "tab_unid.exists(rec.unf_seq)"
				if (unfIndicadoresVO != null) {
					BigDecimal numeroBloqueio = indicadoresVO.getTabUnid()
							.get(leitoDesativadoVO.getUnfSeq()).getBloqueios();

					if (numeroBloqueio == null) {
						numeroBloqueio = BigDecimal.valueOf(0);
					}
					Integer numeroDias = leitoDesativadoVO.getDias() == null ? 0
							: leitoDesativadoVO.getDias();
					unfIndicadoresVO
							.setBloqueios(numeroBloqueio.add(BigDecimal.valueOf(numeroDias)));

					// Código equivalente a
					// "tab_clinicas.exists(tab_unid(rec.unf_seq).clinica)"
					codigoClinica = unfIndicadoresVO.getClinica();
					if (indicadoresVO.getTabClinicas().get(codigoClinica) != null) {
						Integer numeroBloqueioDesativado = indicadoresVO.getTabClinicas()
								.get(codigoClinica).getBlqDesat();

						if (numeroBloqueioDesativado == null) {
							numeroBloqueioDesativado = 0;
						}

						indicadoresVO.getTabClinicas().get(codigoClinica)
								.setBlqDesat(numeroBloqueioDesativado + numeroDias);
					}
				}
			}
		}

		indicadoresVO.setLstDesat(indicadoresVO.getIndDesat());

		// Código equivalente ao loop "For i in tab_unid.first..tab_unid.last"
		Integer numeroBlqDesat = 0;
		Integer numeroXDiasMes = X_DIAS_MES;

		Set<Short> mapKeysTabUnid = indicadoresVO.getTabUnid().keySet();
		for (Short indice : mapKeysTabUnid) {
			indicadoresVO.setInd(indice.intValue());
			if (indicadoresVO.getTabUnid().get(indice) != null) {
				numeroBlqDesat = indicadoresVO.getTabUnid().get(indice).getBlqDesat();
				indicadoresVO.getTabUnid().get(indice).setBlqDesat(numeroBlqDesat / numeroXDiasMes);
			}
		}

		// Código equivalente a "if lst_unid_priv > 0 then"
		if (indicadoresVO.getLstUnidPriv() > 0) {
			Set<Short> mapKeysTabUnidPriv = indicadoresVO.getTabUnidPriv().keySet();
			for (Short indice : mapKeysTabUnidPriv) {
				indicadoresVO.setInd(indice.intValue());
			}
		}

		// Código equivalente a "for i in tab_clinicas.first..tab_clinicas.last"
		Set<Integer> mapKeysTabClinicas = indicadoresVO.getTabClinicas().keySet();
		for (Integer indice : mapKeysTabClinicas) {
			if (indicadoresVO.getTabClinicas().get(indice) != null) {
				numeroBlqDesat = indicadoresVO.getTabClinicas().get(indice).getBlqDesat();
				indicadoresVO.getTabClinicas().get(indice)
						.setBlqDesat(numeroBlqDesat / numeroXDiasMes);
			}
		}
	}

	private void obterParametrosMovimentosInternacao(List<Integer> tipoMovimentoInternacaoList)
			throws ApplicationBusinessException {
		// MVTO INTERNACAO
		Integer codigoMovimentoInternacao = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
		// MVTO INTERNACAO TRANSFERENCIA
		Integer codigoMovimentoInternacaoTransferencia = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INTERNACAO_TRANSFERENCIA);
		// MVTO INTERNACAO EQUIPE
		Integer codigoMovimentoInternacaoEquipe = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INTERNACAO_EQUIPE);
		// MVTO INTERNACAO ESPECIALIDADE
		Integer codigoMovimentoInternacaoEspecialidade = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INTERNACAO_ESPECIALIDADE);
		// MVTO INTERNACAO LEITO
		Integer codigoMovimentoInternacaoLeito = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INTERNACAO_LEITO);
		// MVTO INTERNACAO UNIDADE FUNCIONAL
		Integer codigoMovimentoInternacaoUnidade = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INTERNACAO_UNIDADE);
		// MVTO INTERNACAO P_COD_MVTO_INTERNACAO_CLINICA
		Integer codigoMovimentoInternacaoClinica = this
				.obterParametroMovimentoInternacao(AghuParametrosEnum.P_COD_MVTO_INTERNACAO_CLINICA);

		tipoMovimentoInternacaoList.add(codigoMovimentoInternacao);
		tipoMovimentoInternacaoList.add(codigoMovimentoInternacaoTransferencia);
		tipoMovimentoInternacaoList.add(codigoMovimentoInternacaoEquipe);
		tipoMovimentoInternacaoList.add(codigoMovimentoInternacaoEspecialidade);
		tipoMovimentoInternacaoList.add(codigoMovimentoInternacaoLeito);
		tipoMovimentoInternacaoList.add(codigoMovimentoInternacaoUnidade);
		tipoMovimentoInternacaoList.add(codigoMovimentoInternacaoClinica);
	}

	/**
	 * Método para implementar query do cursor "movimentos"
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<Object> pesquisarMovimentosInternacao() throws ApplicationBusinessException {
		List<Integer> tipoMovimentoInternacaoList = new ArrayList<Integer>();

		// Busca parametros necessários para a consulta do cursor "movimentos"
		this.obterParametrosMovimentosInternacao(tipoMovimentoInternacaoList);

		Calendar cal = Calendar.getInstance();
		cal.setTime(this.obterDiaMaximoMesMaisUm());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date ultimoDiaMes = cal.getTime();

		return this.getInternacaoFacade().pesquisarMovtsInternacao(
				tipoMovimentoInternacaoList, MES_COMPETENCIA, ultimoDiaMes,
				obterDiaMaximoMesMaisUm());
	}

	/**
	 * Método para iterar todos os movimentos de internação e popular/formatar
	 * campos que não foram buscados na query original.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private List<MovimentoInternacaoIndicadoresVO> formatarMovimentosInternacao()
			throws ApplicationBusinessException {

		List<Object> objetosMovimentoInternacao = this.pesquisarMovimentosInternacao();

		List<MovimentoInternacaoIndicadoresVO> movimentosInternacao = new ArrayList<MovimentoInternacaoIndicadoresVO>();
		AinMovimentosInternacao movimentoInternacao = null;
		MovimentoInternacaoIndicadoresVO vo = null;

		AghOrigemEventos origemEvento = null;

		IndicadoresHospitalaresUtilRN indHospUtilRN = getIndicadoresHospitalaresUtilRN();

		IInternacaoFacade internacaoFacade = getInternacaoFacade();

		for (Object objetoMovimentoInternacao : objetosMovimentoInternacao) {
			origemEvento = null;
			Object[] objetos = (Object[]) objetoMovimentoInternacao;

			// Popula VO
			vo = new MovimentoInternacaoIndicadoresVO();
			vo.setProntuario((Integer) objetos[0]);
			vo.setSigla((String) objetos[1]);
			vo.setIntSeq((Integer) objetos[2]);
			vo.setAtuSeq((Integer) objetos[3]);
			vo.setTamCodigo((String) objetos[4]);
			vo.setDthrAltaMedica((Date) objetos[5]);
			// vo.setLtoLtoId((String) objetos[6]);
			// vo.setEspSeq((Short) objetos[7]);
			vo.setDthrLancamento((Date) objetos[8]);
			vo.setMovimentoInternacaoSeq((Integer) objetos[9]);

			movimentoInternacao = internacaoFacade.obterMovimentoInternacao(vo
					.getMovimentoInternacaoSeq());

			origemEvento = movimentoInternacao.getInternacao().getOrigemEvento();
			vo.setOevSeq(origemEvento.getSeq());

			vo.setTmiSeq(movimentoInternacao.getTipoMovimentoInternacao().getCodigo());

			if (movimentoInternacao.getLeito() != null) {
				vo.setLtoLtoId(movimentoInternacao.getLeito().getLeitoID());
			}

			if (movimentoInternacao.getEspecialidade() != null) {
				vo.setEspSeq(movimentoInternacao.getEspecialidade().getSeq());
			}

			AghUnidadesFuncionais unidadeFuncional = movimentoInternacao.getUnidadeFuncional();
			if (unidadeFuncional != null) {
				vo.setUnfSeq(unidadeFuncional.getSeq());
			}

			RapServidores servidor = movimentoInternacao.getServidor();
			if (servidor != null) {
				vo.setServidor(servidor);
				vo.setSerMatricula(servidor.getId().getMatricula());
				vo.setSerVinCodigo(servidor.getId().getVinCodigo());
			} else {
				vo.setSerMatricula(999999);
				vo.setSerVinCodigo(Short.valueOf("999"));
			}

			Date dataFinalMovimento = indHospUtilRN.obterDataFinalMovimento(vo.getIntSeq(),
					vo.getDthrLancamento(), vo.getDthrAltaMedica());
			vo.setDthrFinalM(dataFinalMovimento);

			Integer seqTipoMovimentoInternacao = indHospUtilRN.obterTipoMovimentoId(vo.getIntSeq(),
					vo.getDthrLancamento());
			vo.setTmiSeqT(seqTipoMovimentoInternacao);

			Short seqEspecialidade = indHospUtilRN.obterEspecialidadeId(vo.getIntSeq(),
					vo.getDthrLancamento());
			vo.setEspSeqUtil(seqEspecialidade);

			Short seqUnidade = indHospUtilRN.obterUnidadeId(vo.getIntSeq(), vo.getDthrLancamento());
			vo.setUnidadeUtil(seqUnidade);

			if (movimentoInternacao.getEspecialidade() != null) {
				vo.setCctCodigo(movimentoInternacao.getEspecialidade().getCentroCusto().getCodigo());
				vo.setClcCodigo(movimentoInternacao.getEspecialidade().getClinica().getCodigo());
			}

			vo.setProcedimento(0);
			vo.setGphSeq(0);
			vo.setCddCodigo(0);
			vo.setBclBaiCodigo(0);
			vo.setDstCodigo(0);

			AinQuartos quarto = movimentoInternacao.getQuarto();
			Integer codigoClinicaQuartoUnidade = null;
			if (quarto != null && quarto.getClinica() != null) {
				codigoClinicaQuartoUnidade = quarto.getClinica().getCodigo();
			} else if (movimentoInternacao.getUnidadeFuncional() != null
					&& movimentoInternacao.getUnidadeFuncional().getClinica() != null) {
				codigoClinicaQuartoUnidade = movimentoInternacao.getUnidadeFuncional().getClinica()
						.getCodigo();
			}
			vo.setCodigoClinicaNvl(codigoClinicaQuartoUnidade);

			Boolean indConsClinLto = Boolean.TRUE;
			if (movimentoInternacao.getLeito() != null
					&& movimentoInternacao.getLeito().getIndConsClinUnidade() != null) {
				indConsClinLto = movimentoInternacao.getLeito().getIndConsClinUnidade();
			}
			vo.setIndConsClinLto(indConsClinLto);

			DominioSimNao indConsClinQrt = DominioSimNao.S;
			if (quarto != null) {
				if (quarto.getIndConsClin() != null) {
					indConsClinQrt = quarto.getIndConsClin();
				}
				vo.setIndExclusivInfeccao(quarto.getIndExclusivInfeccao());
			}
			vo.setIndConsClinQrt(indConsClinQrt);

			vo.setIndConsClinUnf(getAghuFacade().validarCaracteristicaDaUnidadeFuncional(vo.getUnfSeq(), ConstanteAghCaractUnidFuncionais.CONS_CLIN));

			// TODO ajustar valores hard coded "A", "B", "U", 8 para parametros
			String tipo = null;
			if (getAghuFacade().validarCaracteristicaDaUnidadeFuncional(vo.getUnfSeq(), ConstanteAghCaractUnidFuncionais.CO)) {
				tipo = "A";
			} else if (getAghuFacade().validarCaracteristicaDaUnidadeFuncional(vo.getUnfSeq(), ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)) {
				tipo = "A";
			} else if (quarto != null && quarto.getClinica() != null
					&& quarto.getClinica().getCodigo().equals(8)) {
				tipo = "B";
			} else if (unidadeFuncional != null && unidadeFuncional.getClinica() != null
					&& unidadeFuncional.getClinica().getCodigo().equals(8)) {
				tipo = "B";
			} else {
				tipo = "U";
			}

			vo.setTipo(tipo);

			// TODO ajustar valores hard coded "PRIVATIVO"
			vo.setLeitoPrivativo(internacaoFacade.obterCaracteristicaLeito(vo.getLtoLtoId(),
					PRIVATIVO));
			vo.setConvenioSaudePlano(movimentoInternacao.getInternacao().getConvenioSaudePlano());

			movimentosInternacao.add(vo);
		}

		return movimentosInternacao;
	}	

	private Integer obterParametroMovimentoInternacao(AghuParametrosEnum nomeParametro)
			throws ApplicationBusinessException {
		Integer seq = null;
		try {
			// MVTO INTERNACAO UNIDADE FUNCIONAL
			AghParametros parametro = getParametroFacade().buscarAghParametro(nomeParametro);
			seq = parametro.getVlrNumerico() == null ? null : parametro.getVlrNumerico()
					.intValue();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(IndicadoresRNCode.ERRO_PARAMETRO,
					AghuParametrosEnum.P_COD_MVTO_INTERNACAO_UNIDADE.toString());
		}

		return seq;
	}

	/**
	 * Query pertencente ao ORADB cursor bloqueios.
	 * 
	 * @return Lista de Vo.
	 */
	private List<LeitosBloqueadosIndicadoresVO> obterQuery1LeitosBloqueados() {

		List<Object[]> tmp = getInternacaoFacade().obterLeitosTipoMovsLeitos();

		Iterator<Object[]> it = tmp.iterator();

		List<LeitosBloqueadosIndicadoresVO> res = new ArrayList<LeitosBloqueadosIndicadoresVO>(0);

		IInternacaoFacade internacaoFacade = getInternacaoFacade();

		while (it.hasNext()) {
			Object[] obj = it.next();

			String leitoId = (String) obj[1];
			// Short codigo = (Short) obj[2];

			Short codBloq = getInternacaoFacade().obterCodigoBloqueioLB(leitoId, MES_COMPETENCIA);

			// If substitui clausula WHERE com left join ().
			// if (codBloq == null || codBloq.equals(codigo)) {

			LeitosBloqueadosIndicadoresVO vo = new LeitosBloqueadosIndicadoresVO();

			Short unfSeq = (Short) obj[0];
			// String tipoMovLeitoDesc = (String) obj[3];

			vo.setUnfSeq(unfSeq);
			vo.setLtoId(leitoId);
			vo.setCodBloqueio(codBloq);

			// Somente os registros com codigo de bloqueio tem descrição
			if (codBloq != null) {
				String descricao = null;
				try {
					descricao = internacaoFacade.obterDescTipoMvtoPorCodigo(codBloq);
				} catch (Exception e) {
					LOG.error("Exceção capturada: ", e);
					// Se ocorrer erro ao buscar descricao do movimento, seta
					// null
					descricao = null;
				}
				// vo.setDescricao(tipoMovLeitoDesc);
				vo.setDescricao(descricao);
			}

			vo.setIndBlq(getInternacaoFacade().obterSituacaoPacienteLB(leitoId, MES_COMPETENCIA));

			Date dataI = getInternacaoFacade().obterDataInicialLB(leitoId, MES_COMPETENCIA);

			vo.setDataI(dataI);

			Date dataF = getInternacaoFacade().obterDataFinalLancamentoLB(leitoId, MES_COMPETENCIA);

			if (dataI == null) {
				vo.setDataF(null);
			} else {
				if (dataF == null) {
					vo.setDataF(obterDiaMaximoMesMaisUm());
				} else {
					vo.setDataF(dataF);
				}
			}

			BigDecimal dias = br.gov.mec.aghu.core.utils.DateUtil.calcularDiasEntreDatasComPrecisao(
					vo.getDataI(), vo.getDataF());

			vo.setDias(dias);

			vo.setPrivativo(internacaoFacade.obterCaracteristicaLeito(leitoId, PRIVATIVO));

			res.add(vo);
		}

		return res;
	}

	/**
	 * Query pertencente ao ORADB Cursor bloqueios.
	 * 
	 * @return Lista de Vo.
	 */
	private List<LeitosBloqueadosIndicadoresVO> obterQuery2LeitosBloqueados() {

		List<Object[]> tmp = getInternacaoFacade()
				.pesquisarExtratosLeitoPorTipoMovs(MES_COMPETENCIA,
						new Object[] { DominioMovimentoLeito.B, DominioMovimentoLeito.BI });

		Iterator<Object[]> it = tmp.iterator();

		List<LeitosBloqueadosIndicadoresVO> res = new ArrayList<LeitosBloqueadosIndicadoresVO>(0);

		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();

		while (it.hasNext()) {
			Object[] obj = it.next();

			LeitosBloqueadosIndicadoresVO vo = new LeitosBloqueadosIndicadoresVO();

			Short unfSeq = (Short) obj[0];
			String leitoId = (String) obj[1];
			Short codigo = (Short) obj[2];
			String tipoMovLeitoDesc = (String) obj[3];
			DominioSimNao indBloqueioPaciente = (DominioSimNao) obj[4];
			Date dtHrLancamento = (Date) obj[5];

			vo.setUnfSeq(unfSeq);
			vo.setLtoId(leitoId);
			vo.setIndBlq(indBloqueioPaciente);
			vo.setCodBloqueio(codigo);
			vo.setDescricao(tipoMovLeitoDesc);
			vo.setDataI(dtHrLancamento);

			Date dataF = getInternacaoFacade().obterDataFinalLancamentoLB(leitoId, dtHrLancamento);

			if (dataF != null) {
				vo.setDataF(dataF);
			} else {
				vo.setDataF(obterDiaMaximoMesMaisUm());
			}

			BigDecimal dias = br.gov.mec.aghu.core.utils.DateUtil.calcularDiasEntreDatasComPrecisao(
					vo.getDataI(), vo.getDataF());

			vo.setDias(dias);

			vo.setPrivativo(internacaoFacade.obterCaracteristicaLeito(leitoId, PRIVATIVO));

			res.add(vo);
		}

		return res;
	}

	/**
	 * ORADB carga_bloqueios
	 * 
	 */
	private void carregarBloqueios() {
		List<LeitosBloqueadosIndicadoresVO> leitosBloqueadosList = pesquisarLeitosBloqueados();

		// this.inserirMensagemListaLog("carga_bloqueios",
		// leitosBloqueadosList.size());

		for (LeitosBloqueadosIndicadoresVO leitoBloqueadoVo : leitosBloqueadosList) {
			if (leitoBloqueadoVo.getDias().compareTo(BigDecimal.valueOf(0)) > 0) {
				indicadoresVO.setInd((indicadoresVO.getInd() + 1));
				indicadoresVO.getTabBlq().put(indicadoresVO.getInd(), leitoBloqueadoVo);

				if (indicadoresVO.getTabUnid().containsKey(leitoBloqueadoVo.getUnfSeq())) {
					BigDecimal bloqueiosTotal = indicadoresVO.getTabUnid()
							.get(leitoBloqueadoVo.getUnfSeq()).getBloqueios();
					bloqueiosTotal = bloqueiosTotal.add(leitoBloqueadoVo.getDias());
					indicadoresVO.getTabUnid().get(leitoBloqueadoVo.getUnfSeq())
							.setBloqueios(bloqueiosTotal);
					Integer codClinica = indicadoresVO.getTabUnid()
							.get(leitoBloqueadoVo.getUnfSeq()).getClinica();
					if (indicadoresVO.getTabClinicas().containsKey(codClinica)) {
						BigDecimal bloqueiosTotalClinicas = indicadoresVO.getTabClinicas()
								.get(codClinica).getBloqueios().add(leitoBloqueadoVo.getDias());
						indicadoresVO.getTabClinicas().get(codClinica)
								.setBloqueios(bloqueiosTotalClinicas);
					}
				}
				if ("S".equals(leitoBloqueadoVo.getPrivativo())) {
					if (indicadoresVO.getTabUnidPriv().containsKey(leitoBloqueadoVo.getUnfSeq())) {
						BigDecimal bloqueiosPrivTotal = indicadoresVO.getTabUnidPriv()
								.get(leitoBloqueadoVo.getUnfSeq()).getBloqueio()
								.add(leitoBloqueadoVo.getDias());
						indicadoresVO.getTabUnidPriv().get(leitoBloqueadoVo.getUnfSeq())
								.setBloqueio(bloqueiosPrivTotal);
					}
				}
			}
		}
		indicadoresVO.setLst(indicadoresVO.getInd());
	}

	/**
	 * ORADB cursor bloqueios
	 * 
	 * @return List de VO
	 */
	private List<LeitosBloqueadosIndicadoresVO> pesquisarLeitosBloqueados() {

		// TODO FIXME está dando erro de timeout na transação!!!
		List<LeitosBloqueadosIndicadoresVO> leitosBloqueadosList = new ArrayList<LeitosBloqueadosIndicadoresVO>();
		leitosBloqueadosList.addAll(this.obterQuery1LeitosBloqueados());
		leitosBloqueadosList.addAll(this.obterQuery2LeitosBloqueados());

		class OrdenacaoLeitosBloqueados implements Comparator<LeitosBloqueadosIndicadoresVO> {

			@Override
			public int compare(LeitosBloqueadosIndicadoresVO o1, LeitosBloqueadosIndicadoresVO o2) {

				if (o1.getUnfSeq() == null) {
					return -1;
				} else if (o2.getUnfSeq() == null) {
					return 1;
				} else {
					if (o1.getUnfSeq() < o2.getUnfSeq()) {
						return -1;
					} else if (o1.getUnfSeq() > o2.getUnfSeq()) {
						return 1;
					} else {
						// Se unidades forem iguais, compara leito
						return o1.getLtoId().compareTo(o2.getLtoId());
					}
				}
			}
		}

		// CoreUtil.ordenarLista(leitosBloqueadosList, "unfSeq", true);
		Collections.sort(leitosBloqueadosList, new OrdenacaoLeitosBloqueados());

		return leitosBloqueadosList;
	}

	/**
	 * ORADB gera_indicador
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void gerarIndicador() {

		// this.inserirMensagemListaLog("gera_indicador", "");
		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //usado
		// somente para mensagens

		// TODO verificar todos valores hard coded

		indicadoresVO.setXadm(0);
		indicadoresVO.setXasat(0);
		indicadoresVO.setXoutClinE(Short.valueOf("0"));
		indicadoresVO.setXoutClinS(Short.valueOf("0"));
		indicadoresVO.setXoutUnidE(Short.valueOf("0"));
		indicadoresVO.setXoutUnidS(Short.valueOf("0"));
		indicadoresVO.setXoutEspE(Short.valueOf("0"));
		indicadoresVO.setXoutEspS(Short.valueOf("0"));
		indicadoresVO.setXasat(0);
		indicadoresVO.setXoutProfE(Short.valueOf("0"));
		indicadoresVO.setXasat(0);
		indicadoresVO.setXoutProfS(Short.valueOf("0"));
		indicadoresVO.setXalt(0);
		indicadoresVO.setXobtC(Short.valueOf("0"));
		indicadoresVO.setXobtD(0);
		indicadoresVO.setXsdo(Short.valueOf("0"));
		indicadoresVO.setXpacMesAnt(0);
		indicadoresVO.setXblq(0);

		// this.inserirMensagemListaLog("gera_indicador - xdthr_lancamento",
		// indicadoresVO.getXdthrLancamento());

		if (indicadoresVO.getXdthrLancamento() != null
				&& indicadoresVO.getXdthrLancamento().before(MES_COMPETENCIA)) {
			// this.inserirMensagemListaLog("gera_indicador - IF xDatai 1",
			// indicadoresVO.getXdatai());
			indicadoresVO.setXdatai(MES_COMPETENCIA);
			indicadoresVO.setXpacMesAnt(1);
		} else {
			if ("A".equals(indicadoresVO.getXtipo()) || "B".equals(indicadoresVO.getXtipo())) {
				// this.inserirMensagemListaLog("gera_indicador - IF xDatai 2",
				// indicadoresVO.getXdatai());
				indicadoresVO.setXdatai(indicadoresVO.getXdthrLancamento());
			} else {
				// this.inserirMensagemListaLog("gera_indicador - IF xDatai 3",
				// indicadoresVO.getXdatai());
				indicadoresVO.setXdatai(DateUtils.truncate(indicadoresVO.getXdthrLancamento(),
						Calendar.DAY_OF_MONTH));
			}

			// TODO verificar valores hard coded
			if (Byte.valueOf("1").equals(indicadoresVO.getXtmiSeq())
					|| Byte.valueOf("2").equals(indicadoresVO.getXtmiSeq())) {
				if ("U".equals(indicadoresVO.getXtipo())) {
					indicadoresVO.setvOrgmAsat("N");
					indicadoresVO.setvOrgmBlc("N");
				}
				if (Short.valueOf("2").equals(indicadoresVO.getXoevSeq())
						|| indicadoresVO.getXatuSeq() != null) {
					indicadoresVO.setXasat(1);
				} else {
					indicadoresVO.setXadm(1);
				}
			} else if (Byte.valueOf("15").equals(indicadoresVO.getXtmiSeq())
					|| Byte.valueOf("14").equals(indicadoresVO.getXtmiSeq())
					|| Byte.valueOf("13").equals(indicadoresVO.getXtmiSeq())) {
				if ("S".equals(indicadoresVO.getvOrgmAsat())) {
					indicadoresVO.setXasat(1);
					indicadoresVO.setvOrgmAsat("N");
				} else if ("S".equals(indicadoresVO.getvOrgmBlc())) {
					indicadoresVO.setXadm(1);
					indicadoresVO.setvOrgmBlc("N");
				} else {
					if (Byte.valueOf("15").equals(indicadoresVO.getXtmiSeq())) {
						indicadoresVO.setXoutClinE(Short.valueOf("1"));
					} else if (Byte.valueOf("14").equals(indicadoresVO.getXtmiSeq())) {
						indicadoresVO.setXoutUnidE(Short.valueOf("1"));
					}
				}
			} else if (Byte.valueOf("12").equals(indicadoresVO.getXtmiSeq())) {
				indicadoresVO.setXoutEspE(Short.valueOf("1"));
			} else if (Byte.valueOf("11").equals(indicadoresVO.getXtmiSeq())) {
				indicadoresVO.setXoutProfE(Short.valueOf("1"));
			}
		}

		if (indicadoresVO.getXdthrFinal() == null
				|| indicadoresVO.getXdthrFinal().after(DTHR_FIM_MES)) {
			// this.inserirMensagemListaLog("gera_indicador - IF xData 1",
			// "-->>"+indicadoresVO.getXdataf());
			indicadoresVO.setXdataf(DateUtils.truncate(this.obterDiaMaximoMesMaisUm(),
					Calendar.DAY_OF_MONTH));
			indicadoresVO.setXsdo(Short.valueOf("1"));
		} else {
			if ("A".equals(indicadoresVO.getXtipo()) || "B".equals(indicadoresVO.getXtipo())) {
				// this.inserirMensagemListaLog("gera_indicador - IF xData 2",
				// "-->>"+indicadoresVO.getXdataf());
				indicadoresVO.setXdataf(indicadoresVO.getXdthrFinal());
			} else {
				// this.inserirMensagemListaLog("gera_indicador - IF xData 3",
				// "-->>"+indicadoresVO.getXdataf());
				indicadoresVO.setXdataf(DateUtils.truncate(indicadoresVO.getXdthrFinal(),
						Calendar.DAY_OF_MONTH));
			}
			if (Byte.valueOf("21").equals(indicadoresVO.getXtmiSeqProx())) {
				if ("A".equals(indicadoresVO.getXtipo()) || "B".equals(indicadoresVO.getXtipo())) {
					// this.inserirMensagemListaLog("gera_indicador - IF xData 4",
					// "-->>"+indicadoresVO.getXdataf());
					indicadoresVO.setXdataf(indicadoresVO.getXdtAlta());
				} else {
					// this.inserirMensagemListaLog("gera_indicador - IF xData 5",
					// "-->>"+indicadoresVO.getXdataf());
					indicadoresVO.setXdataf(DateUtils.truncate(indicadoresVO.getXdtAlta(),
							Calendar.DAY_OF_MONTH));
				}
				if ("C".equals(indicadoresVO.getXtamCodigo())) {
					indicadoresVO.setXobtC(Short.valueOf("1"));
				} else if ("D".equals(indicadoresVO.getXtamCodigo())) {
					indicadoresVO.setXobtD(1);
				} else {
					indicadoresVO.setXalt(1);
				}
			} else if (Byte.valueOf("15").equals(indicadoresVO.getXtmiSeqProx())) {
				indicadoresVO.setXoutClinS(Short.valueOf("1"));
				indicadoresVO.setZespSeq(indicadoresVO.getXespSeq().intValue());

			} else if (Byte.valueOf("2").equals(indicadoresVO.getXtmiSeqProx())
					|| Byte.valueOf("14").equals(indicadoresVO.getXtmiSeqProx())) {
				if ("S".equals(indicadoresVO.getvOrgmAsat())) {
					indicadoresVO.setXoutClinS(Short.valueOf("1"));
				} else if ("S".equals(indicadoresVO.getvOrgmBlc())) {
					indicadoresVO.setXoutClinS(Short.valueOf("1"));
				} else {
					indicadoresVO.setXoutUnidS(Short.valueOf("1"));
				}
				indicadoresVO.setZespSeq(indicadoresVO.getXespSeq().intValue());

			} else if (Byte.valueOf("13").equals(indicadoresVO.getXtmiSeqProx())) {
				if ("S".equals(indicadoresVO.getvOrgmAsat())) {
					indicadoresVO.setXoutClinS(Short.valueOf("1"));
				} else if ("S".equals(indicadoresVO.getvOrgmBlc())) {
					indicadoresVO.setXoutClinS(Short.valueOf("1"));
				}
			} else if (Byte.valueOf("12").equals(indicadoresVO.getXtmiSeqProx())) {
				indicadoresVO.setZespSeq(indicadoresVO.getXespSeq().intValue());
				indicadoresVO.setXoutEspS(Short.valueOf("1"));
			} else if (Byte.valueOf("11").equals(indicadoresVO.getXtmiSeqProx())) {
				indicadoresVO.setXoutProfS(Short.valueOf("1"));
			}
		}

		// this.inserirMensagemListaLog("gera_indicador - VALOR DATAS",
		// "-->>"+indicadoresVO.getXdatai(), indicadoresVO.getXdataf());

		if (indicadoresVO.getXdataf() != null && indicadoresVO.getXdatai() != null) {
			// && indicadoresVO.getXdataf().before(indicadoresVO.getXdatai())) {
			Date dataInicial = DateUtils.truncate(indicadoresVO.getXdatai(), Calendar.SECOND);
			Date dataFinal = DateUtils.truncate(indicadoresVO.getXdataf(), Calendar.SECOND);

			if (dataFinal.before(dataInicial)) {
				// this.inserirMensagemListaLog("gera_indicador - IF xData 6",
				// "-->>"+indicadoresVO.getXdataf());
				indicadoresVO.setXdataf(DateUtils.truncate(this.obterDiaMaximoMesMaisUm(),
						Calendar.DAY_OF_MONTH));
			}
		}

		if ("A".equals(indicadoresVO.getXtipo()) || "B".equals(indicadoresVO.getXtipo())) {
			// this.inserirMensagemListaLog("gera_indicador - IF xPacDia 1",
			// "-->>"+indicadoresVO.getXdatai(),
			// "-->>"+indicadoresVO.getXdataf());
			indicadoresVO.setXpacDia(DateUtil.calcularDiasEntreDatasComPrecisao(
					indicadoresVO.getXdatai(), indicadoresVO.getXdataf()));
		} else {
			if (indicadoresVO.getXdatai() != null
					&& indicadoresVO.getXdataf() != null
					&& DateUtils.truncate(indicadoresVO.getXdatai(), Calendar.SECOND).compareTo(
							DateUtils.truncate(indicadoresVO.getXdataf(), Calendar.SECOND)) == 0) {
				// this.inserirMensagemListaLog("gera_indicador - IF xPacDia 2",
				// "1");
				indicadoresVO.setXpacDia(BigDecimal.valueOf(1));
			} else {
				// this.inserirMensagemListaLog("gera_indicador - IF xPacDia 3",
				// "-->>"+indicadoresVO.getXdatai(),
				// "-->>"+indicadoresVO.getXdataf());
				indicadoresVO.setXpacDia(DateUtil.calcularDiasEntreDatasComPrecisao(
						indicadoresVO.getXdatai(), indicadoresVO.getXdataf()));
			}
		}

		if (indicadoresVO.getXpacDia() == null
				|| indicadoresVO.getXpacDia().compareTo(BigDecimal.valueOf(0)) < 0) {
			// this.inserirMensagemListaLog("gera_indicador - IF xPacDia 4",
			// "0");
			indicadoresVO.setXpacDia(BigDecimal.valueOf(0));
		}

		// this.inserirMensagemListaLog("gera_indicador - valor xpac_dia",
		// indicadoresVO.getXpacDia());
		indicadoresVO.setXpacDiaRefl(indicadoresVO.getXpacDia());

		// TODO verificar valores hard coded
		if ("S".equals(indicadoresVO.getxOutClin())
				&& (Integer.valueOf(1).equals(indicadoresVO.getXclcCodigoPac())
						|| Integer.valueOf(2).equals(indicadoresVO.getXclcCodigoPac())
						|| Integer.valueOf(3).equals(indicadoresVO.getXclcCodigoPac())
						|| Integer.valueOf(5).equals(indicadoresVO.getXclcCodigoPac()) || Integer
						.valueOf(7).equals(indicadoresVO.getXclcCodigoPac()))
				&& (Integer.valueOf(1).equals(indicadoresVO.getXclcCodigoLto())
						|| Integer.valueOf(2).equals(indicadoresVO.getXclcCodigoLto())
						|| Integer.valueOf(3).equals(indicadoresVO.getXclcCodigoLto())
						|| Integer.valueOf(5).equals(indicadoresVO.getXclcCodigoLto()) || Integer
						.valueOf(7).equals(indicadoresVO.getXclcCodigoLto()))) {

			if (indicadoresVO.getXdatai() != null
					&& indicadoresVO.getXdataf() != null
					&& DateUtils.truncate(indicadoresVO.getXdatai(), Calendar.SECOND).compareTo(
							DateUtils.truncate(indicadoresVO.getXdataf(), Calendar.SECOND)) == 0) {
				// this.inserirMensagemListaLog("gera_indicador - IF 1",
				// sdf.format(indicadoresVO.getXdatai()),
				// sdf.format(indicadoresVO.getXdataf()));
				indicadoresVO.setXltoDia(1);
			} else {
				// this.inserirMensagemListaLog("gera_indicador - IF 2",
				// sdf.format(indicadoresVO.getXdatai()),
				// sdf.format(indicadoresVO.getXdataf()));
				indicadoresVO.setXltoDia(DateUtil.calcularDiasEntreDatas(indicadoresVO.getXdatai(),
						indicadoresVO.getXdataf()) + 1);
			}

			if (indicadoresVO.getXltoDia() > X_DIAS_MES) {
				// this.inserirMensagemListaLog("gera_indicador - IF 3",
				// sdf.format(indicadoresVO.getXdatai()),
				// sdf.format(indicadoresVO.getXdataf()));
				indicadoresVO.setXltoDia(X_DIAS_MES);
			}

			this.capcMais(indicadoresVO.getXclcCodigoPac(), indicadoresVO.getXclcCodigoLto(),
					indicadoresVO.getXltoDia());
		} else {
			indicadoresVO.setxOutClin("N");
		}

		Integer xUnfSeq = indicadoresVO.getXunfSeq() == null ? 0 : indicadoresVO.getXunfSeq()
				.intValue();
		
		Integer xLtoId = 0;		// 0,
		if(indicadoresVO.getXltoId() != null && indicadoresVO.getXltoId().length() != 0){
			Short quartoId = getInternacaoFacade().obterNumeroQuartoPorLeito(indicadoresVO.getXltoId());  // leitosBlq.getLtoId().length()-1
			if(quartoId != null){
				xLtoId = quartoId.intValue();
			}
		}																			

		int diasBloqueio = this.diasBloqueio(xUnfSeq, xLtoId);
		indicadoresVO.setXblq(diasBloqueio);

		// this.inserirMensagemListaLog("gera_indicador - diasBloqueio",
		// diasBloqueio);

		/*
		 * v_valores := 'UNF_SEQ='||xunf_seq||'; ESP='||xesp_seq||';
		 * CLC='||xclc_codigo||' ; '
		 * 'xalt='||xalt||';xobt_c='||xobt_c||';xobt_d='||xobt_d||';xout_clin_s='||xout_clin_s||';xout_esp_s='||xout_esp_s||';xout_prof_s='||xout_prof_s||';'||
		 * 'xadm='||xadm||';xasat='||xasat||';xout_clin_e='||xout_clin_e||';xout_esp_e='||xout_esp_e||';xout_prof_e='||xout_prof_e;
		 */

		// -----------------------------
		/*
		 * StringBuilder valores = new StringBuilder();
		 * valores.append("UNF_SEQ=").append(indicadoresVO.getXunfSeq())
		 * .append("; ESP=").append(indicadoresVO.getXespSeq())
		 * .append("; CLC=").append(indicadoresVO.getXclcCodigo())
		 * .append(" ; xalt=").append(indicadoresVO.getXalt())
		 * .append(";xobt_c=").append(indicadoresVO.getXobtC())
		 * .append(";xobt_d=").append(indicadoresVO.getXobtD())
		 * .append(";xout_clin_s=").append(indicadoresVO.getXoutClinS())
		 * .append(";xout_esp_s=").append(indicadoresVO.getXoutEspS())
		 * .append(";xout_prof_s=").append(indicadoresVO.getXoutProfS());
		 * valores.append(";xadm=").append(indicadoresVO.getXadm())
		 * .append(";xasat=").append(indicadoresVO.getXasat())
		 * .append(";xout_clin_e=").append(indicadoresVO.getXoutClinE())
		 * .append(";xout_esp_e=").append(indicadoresVO.getXoutEspE())
		 * .append(";xout_prof_e=").append(indicadoresVO.getXoutProfE());
		 * indicadoresVO.addElementoValoresLog(valores.toString());
		 */
		// -----------------------------

		this.chamadaAcumulaTab();

		// -----------------------------
		/*
		 * StringBuilder valores2 = new StringBuilder();
		 * valores2.append("UNF_SEQ=").append(indicadoresVO.getXunfSeq())
		 * .append("; ESP=").append(indicadoresVO.getXespSeq())
		 * .append("; CLC=").append(indicadoresVO.getXclcCodigo())
		 * .append(" ; xalt=").append(indicadoresVO.getXalt())
		 * .append(";xobt_c=").append(indicadoresVO.getXobtC())
		 * .append(";xobt_d=").append(indicadoresVO.getXobtD())
		 * .append(";xout_clin_s=").append(indicadoresVO.getXoutClinS())
		 * .append(";xout_esp_s=").append(indicadoresVO.getXoutEspS())
		 * .append(";xout_prof_s=").append(indicadoresVO.getXoutProfS());
		 * valores2.append(";xadm=").append(indicadoresVO.getXadm())
		 * .append(";xasat=").append(indicadoresVO.getXasat())
		 * .append(";xout_clin_e=").append(indicadoresVO.getXoutClinE())
		 * .append(";xout_esp_e=").append(indicadoresVO.getXoutEspE())
		 * .append(";xout_prof_e=").append(indicadoresVO.getXoutProfE());
		 * indicadoresVO.addElementoValoresLog2(valores2.toString());
		 */
		// -----------------------------

		if (Byte.valueOf("1").equals(indicadoresVO.getXtmiSeq())
				&& "U".equals(indicadoresVO.getXtipo()) && indicadoresVO.getXatuSeq() != null) {

			if (CoreUtil.isBetweenDatas(indicadoresVO.getXdthrLancamento(), MES_COMPETENCIA,
					DTHR_FIM_MES)) {
				indicadoresVO.setXasat(1);
			}
			indicadoresVO.setXadm(0);
			indicadoresVO.setXoutClinE(Short.valueOf("0"));

			if (indicadoresVO.getXdthrFinal() == null
					|| indicadoresVO.getXdthrFinal().after(DTHR_FIM_MES)) {
				indicadoresVO.setXsdo(Short.valueOf("1"));
			} else {
				indicadoresVO.setXsdo(Short.valueOf("0"));
				indicadoresVO.setXoutClinS(Short.valueOf("1"));
			}

			indicadoresVO.setXoutUnidE(Short.valueOf("0"));
			indicadoresVO.setXoutUnidS(Short.valueOf("0"));
			indicadoresVO.setXoutEspE(Short.valueOf("0"));
			indicadoresVO.setXoutEspS(Short.valueOf("0"));
			indicadoresVO.setXoutProfE(Short.valueOf("0"));
			indicadoresVO.setXoutProfS(Short.valueOf("0"));
			indicadoresVO.setXalt(0);
			indicadoresVO.setXobtC(Short.valueOf("0"));
			indicadoresVO.setXobtD(0);
			indicadoresVO.setXsdo(Short.valueOf("0"));
			indicadoresVO.setXpacMesAnt(0);
			indicadoresVO.setXblq(0);
			indicadoresVO.setXpacDia(BigDecimal.valueOf(0));
			indicadoresVO.setXpacDiaRefl(indicadoresVO.getXpacDia() == null ? BigDecimal.valueOf(0)
					: indicadoresVO.getXpacDia());
			indicadoresVO.setXtipo("A");

			// TODO verificar valores hard coded
			if (Integer.valueOf(7).equals(indicadoresVO.getXclcCodigo())) {
				indicadoresVO.setXunfSeq(Short.valueOf("103"));
			} else if (Integer.valueOf(2).equals(indicadoresVO.getXclcCodigo())) {
				indicadoresVO.setXunfSeq(Short.valueOf("123"));
			} else {
				indicadoresVO.setXunfSeq(Short.valueOf("102"));
			}

			this.chamadaAcumulaTab();
		}
	}

	/**
	 * ORADB procedure chamadadas_acumula_tab
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void chamadaAcumulaTab() {

		// this.inserirMensagemListaLog("chamadadas_acumula_tab", "");

		// gera indicadores do profissional/especialidade
		indicadoresVO.setXsaidas(indicadoresVO.getXalt() + indicadoresVO.getXobtC()
				+ indicadoresVO.getXobtD() + indicadoresVO.getXoutClinS()
				+ indicadoresVO.getXoutEspS() + indicadoresVO.getXoutProfS());
		indicadoresVO.setXentradas(indicadoresVO.getXadm() + indicadoresVO.getXasat()
				+ indicadoresVO.getXoutClinE() + indicadoresVO.getXoutEspE()
				+ indicadoresVO.getXoutProfE());

		this.acumulaTab(
				posicionaTab(indicadoresVO.getXclcCodigoPac(), null, indicadoresVO.getXespSeq(),
						indicadoresVO.getXserVinCodigo(), indicadoresVO.getXserMatricula(),
						indicadoresVO.getXtipo(), null), indicadoresVO.getXadm(), indicadoresVO
						.getXasat(), indicadoresVO.getXoutClinE(), indicadoresVO.getXoutEspE(),
				indicadoresVO.getXoutProfE(), Short.valueOf("0"), indicadoresVO.getXentradas(),
				indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
				indicadoresVO.getXoutClinS(), indicadoresVO.getXoutEspS(), indicadoresVO
						.getXoutProfS(), Short.valueOf("0"), indicadoresVO.getXsaidas(),
				indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(), indicadoresVO.getXblq(),
				indicadoresVO.getXpacDiaRefl());

		if ("U".equals(indicadoresVO.getXtipo())) {
			this.acumulaTab(
					posicionaTab(indicadoresVO.getXclcCodigoPac(), null,
							indicadoresVO.getXespSeq(), indicadoresVO.getXserVinCodigo(),
							indicadoresVO.getXserMatricula(), "C", indicadoresVO.getXcnvCodigo()),
					indicadoresVO.getXadm(), indicadoresVO.getXasat(),
					indicadoresVO.getXoutClinE(), indicadoresVO.getXoutEspE(), indicadoresVO
							.getXoutProfE(), Short.valueOf("0"), indicadoresVO.getXentradas(),
					indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
					indicadoresVO.getXoutClinS(), indicadoresVO.getXoutEspS(), indicadoresVO
							.getXoutProfS(), Short.valueOf("0"), indicadoresVO.getXsaidas(),
					indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(),
					indicadoresVO.getXblq(), indicadoresVO.getXpacDiaRefl());
		}

		// acumula indicadores da especialidade/clinica
		indicadoresVO.setXsaidas(indicadoresVO.getXalt() + indicadoresVO.getXobtC()
				+ indicadoresVO.getXobtD() + indicadoresVO.getXoutClinS()
				+ indicadoresVO.getXoutEspS());
		indicadoresVO.setXentradas(indicadoresVO.getXadm() + indicadoresVO.getXasat()
				+ indicadoresVO.getXoutClinE() + indicadoresVO.getXoutEspE());

		this.acumulaTab(
				posicionaTab(indicadoresVO.getXclcCodigoPac(), null, indicadoresVO.getXespSeq(),
						null, null, indicadoresVO.getXtipo(), null), indicadoresVO.getXadm(),
				indicadoresVO.getXasat(), indicadoresVO.getXoutClinE(),
				indicadoresVO.getXoutEspE(), Short.valueOf("0"), Short.valueOf("0"), indicadoresVO
						.getXentradas(), indicadoresVO.getXalt(), indicadoresVO.getXobtC(),
				indicadoresVO.getXobtD(), indicadoresVO.getXoutClinS(),
				indicadoresVO.getXoutEspS(), Short.valueOf("0"), Short.valueOf("0"), indicadoresVO
						.getXsaidas(), indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(),
				indicadoresVO.getXblq(), indicadoresVO.getXpacDiaRefl());

		this.acumulaTab(
				posicionaTab(indicadoresVO.getXclcCodigoPac(), null, indicadoresVO.getXespSeq(),
						null, null, "C", indicadoresVO.getXcnvCodigo()), indicadoresVO.getXadm(),
				indicadoresVO.getXasat(), indicadoresVO.getXoutClinE(),
				indicadoresVO.getXoutEspE(), Short.valueOf("0"), Short.valueOf("0"), indicadoresVO
						.getXentradas(), indicadoresVO.getXalt(), indicadoresVO.getXobtC(),
				indicadoresVO.getXobtD(), indicadoresVO.getXoutClinS(),
				indicadoresVO.getXoutEspS(), Short.valueOf("0"), Short.valueOf("0"), indicadoresVO
						.getXsaidas(), indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(),
				indicadoresVO.getXblq(), indicadoresVO.getXpacDiaRefl());

		// acumula indicadores da unidade/especialidade
		indicadoresVO.setXsaidas(indicadoresVO.getXalt() + indicadoresVO.getXobtC()
				+ indicadoresVO.getXobtD() + indicadoresVO.getXoutClinS()
				+ indicadoresVO.getXoutUnidS() + indicadoresVO.getXoutEspS());
		indicadoresVO.setXentradas(indicadoresVO.getXadm() + indicadoresVO.getXasat()
				+ indicadoresVO.getXoutClinE() + indicadoresVO.getXoutEspE()
				+ indicadoresVO.getXoutUnidE());

		this.acumulaTab(
				posicionaTab(null, indicadoresVO.getXunfSeq(), indicadoresVO.getXespSeq(), null,
						null, indicadoresVO.getXtipo(), null), indicadoresVO.getXadm(),
				indicadoresVO.getXasat(), indicadoresVO.getXoutClinE(),
				indicadoresVO.getXoutEspE(), Short.valueOf("0"), indicadoresVO.getXoutUnidE(),
				indicadoresVO.getXentradas(), indicadoresVO.getXalt(), indicadoresVO.getXobtC(),
				indicadoresVO.getXobtD(), indicadoresVO.getXoutClinS(),
				indicadoresVO.getXoutEspS(), Short.valueOf("0"), indicadoresVO.getXoutUnidS(),
				indicadoresVO.getXsaidas(), indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(),
				indicadoresVO.getXblq(), indicadoresVO.getXpacDiaRefl());

		// os bloqueios já estão inicializados em registros consolidados
		indicadoresVO.setXblq(0);
		if ("S".equals(indicadoresVO.getXltoPrivativo())
				&& ((short) indicadoresVO.getXunfSeq() != (short) 102
						&& (short) indicadoresVO.getXunfSeq() != (short) 103 && (short) indicadoresVO
						.getXunfSeq() != (short) 123)) {

			// acumula indicadores da unidade/especialidade privativo
			this.acumulaTab(
					posicionaTab(null, indicadoresVO.getXunfSeq(), indicadoresVO.getXespSeq(),
							null, null, "P", null), indicadoresVO.getXadm(), indicadoresVO
							.getXasat(), indicadoresVO.getXoutClinE(), indicadoresVO.getXoutEspE(),
					Short.valueOf("0"), indicadoresVO.getXoutUnidE(), indicadoresVO.getXentradas(),
					indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
					indicadoresVO.getXoutClinS(), indicadoresVO.getXoutEspS(), Short.valueOf("0"),
					indicadoresVO.getXoutUnidS(), indicadoresVO.getXsaidas(), indicadoresVO
							.getXpacMesAnt(), indicadoresVO.getXsdo(), indicadoresVO.getXblq(),
					indicadoresVO.getXpacDiaRefl());
		}

		// acumula indicadores da unidade
		indicadoresVO.setXsaidas(indicadoresVO.getXalt() + indicadoresVO.getXobtC()
				+ indicadoresVO.getXobtD() + indicadoresVO.getXoutClinS()
				+ indicadoresVO.getXoutUnidS());
		indicadoresVO.setXentradas(indicadoresVO.getXadm() + indicadoresVO.getXasat()
				+ indicadoresVO.getXoutClinE() + indicadoresVO.getXoutUnidE());

		this.acumulaTab(
				posicionaTab(null, indicadoresVO.getXunfSeq(), null, null, null,
						indicadoresVO.getXtipo(), null), indicadoresVO.getXadm(),
				indicadoresVO.getXasat(), indicadoresVO.getXoutClinE(), Short.valueOf("0"),
				Short.valueOf("0"), indicadoresVO.getXoutUnidE(), indicadoresVO.getXentradas(),
				indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
				indicadoresVO.getXoutClinS(), Short.valueOf("0"), Short.valueOf("0"),
				indicadoresVO.getXoutUnidS(), indicadoresVO.getXsaidas(),
				indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(), indicadoresVO.getXblq(),
				indicadoresVO.getXpacDiaRefl());

		if ("S".equals(indicadoresVO.getXltoPrivativo())
				&& ((short) indicadoresVO.getXunfSeq() != (short) 102
						&& (short) indicadoresVO.getXunfSeq() != (short) 103 && (short) indicadoresVO
						.getXunfSeq() != (short) 123)) {

			// acumula indicadores da unidade privativa
			this.acumulaTab(
					posicionaTab(null, indicadoresVO.getXunfSeq(), null, null, null, "P", null),
					indicadoresVO.getXadm(), indicadoresVO.getXasat(),
					indicadoresVO.getXoutClinE(), Short.valueOf("0"), Short.valueOf("0"),
					indicadoresVO.getXoutUnidE(), indicadoresVO.getXentradas(),
					indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
					indicadoresVO.getXoutClinS(), Short.valueOf("0"), Short.valueOf("0"),
					indicadoresVO.getXoutUnidS(), indicadoresVO.getXsaidas(),
					indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(),
					indicadoresVO.getXblq(), indicadoresVO.getXpacDiaRefl());
		}

		// acumula indicadores da clinica
		indicadoresVO.setXsaidas(indicadoresVO.getXalt() + indicadoresVO.getXobtC()
				+ indicadoresVO.getXobtD() + indicadoresVO.getXoutClinS());
		indicadoresVO.setXentradas(indicadoresVO.getXadm() + indicadoresVO.getXasat()
				+ indicadoresVO.getXoutClinE());

		this.acumulaTab(
				posicionaTab(indicadoresVO.getXclcCodigo(), null, null, null, null,
						indicadoresVO.getXtipo(), null), indicadoresVO.getXadm(),
				indicadoresVO.getXasat(), indicadoresVO.getXoutClinE(), Short.valueOf("0"),
				Short.valueOf("0"), indicadoresVO.getXoutUnidE(), indicadoresVO.getXentradas(),
				indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
				indicadoresVO.getXoutClinS(), Short.valueOf("0"), Short.valueOf("0"),
				indicadoresVO.getXoutUnidS(), indicadoresVO.getXsaidas(),
				indicadoresVO.getXpacMesAnt(), indicadoresVO.getXsdo(), indicadoresVO.getXblq(),
				indicadoresVO.getXpacDiaRefl());

		// acumula indicadores da clinica/convenio
		if ("U".equals(indicadoresVO.getXtipo())) {
			this.acumulaTab(
					posicionaTab(indicadoresVO.getXclcCodigo(), null, null, null, null, "C",
							indicadoresVO.getXcnvCodigo()), indicadoresVO.getXadm(), indicadoresVO
							.getXasat(), indicadoresVO.getXoutClinE(), Short.valueOf("0"), Short
							.valueOf("0"), Short.valueOf("0"), indicadoresVO.getXentradas(),
					indicadoresVO.getXalt(), indicadoresVO.getXobtC(), indicadoresVO.getXobtD(),
					indicadoresVO.getXoutClinS(), Short.valueOf("0"), Short.valueOf("0"), Short
							.valueOf("0"), indicadoresVO.getXsaidas(), indicadoresVO
							.getXpacMesAnt(), indicadoresVO.getXsdo(), indicadoresVO.getXblq(),
					indicadoresVO.getXpacDiaRefl());
		}

	}

	/**
	 * ORADB procedure acumula_tab
	 */
	private void acumulaTab(Integer ind, Integer adm, Integer asat, Short outClinE, Short outEspE,
			Short outProfE, Short outUnidE, Integer entradas, Integer alt, Short obtC,
			Integer obtD, Short outClinS, Short outEspS, Short outProfS, Short outUnidS,
			Integer saidas, Integer pacMesAnt, Short sdo, Integer blq, BigDecimal pacDia) {

		// this.inserirMensagemListaLog("acumula_tab", ind, adm, asat, outClinE,
		// outEspE, outProfE, outUnidE, entradas, alt, obtC, obtD,
		// outClinS, outEspS, outProfS, outUnidS, saidas, pacMesAnt, sdo,
		// blq, pacDia);

		AinIndicadoresHospitalares ainIndicadoresHospitalares = indicadoresVO.getTabIndicadores()
				.get(ind);

		ainIndicadoresHospitalares.setTotalInternacoesMes(ainIndicadoresHospitalares
				.getTotalInternacoesMes() + adm);

		ainIndicadoresHospitalares.setTotalIntAreaSatelite(ainIndicadoresHospitalares
				.getTotalIntAreaSatelite() + asat);

		ainIndicadoresHospitalares.setTotalEntreOutrasClinicas((short) (ainIndicadoresHospitalares
				.getTotalEntreOutrasClinicas() + outClinE));

		ainIndicadoresHospitalares
				.setTotalEntreOutrasEspecialidades((short) (ainIndicadoresHospitalares
						.getTotalEntreOutrasEspecialidades() + outEspE));

		ainIndicadoresHospitalares
				.setTotalEntreOutrosProfissionais((short) (ainIndicadoresHospitalares
						.getTotalEntreOutrosProfissionais() + outProfE));

		ainIndicadoresHospitalares.setTotalEntreOutrasUnidades((short) (ainIndicadoresHospitalares
				.getTotalEntreOutrasUnidades() + outUnidE));

		ainIndicadoresHospitalares.setTotalEntradas(ainIndicadoresHospitalares.getTotalEntradas()
				+ entradas);

		ainIndicadoresHospitalares.setTotalAltas(ainIndicadoresHospitalares.getTotalAltas() + alt);

		ainIndicadoresHospitalares.setTotalObitosMenos48h((short) (ainIndicadoresHospitalares
				.getTotalObitosMenos48h() + obtC));

		ainIndicadoresHospitalares.setTotalObitosMais48h(ainIndicadoresHospitalares
				.getTotalObitosMais48h() + obtD);

		ainIndicadoresHospitalares.setTotalSaidaOutrasClinicas((short) (ainIndicadoresHospitalares
				.getTotalSaidaOutrasClinicas() + outClinS));

		ainIndicadoresHospitalares
				.setTotalSaidaOutrasEspecialidades((short) (ainIndicadoresHospitalares
						.getTotalSaidaOutrasEspecialidades() + outEspS));

		ainIndicadoresHospitalares
				.setTotalSaidaOutrosProfissionais((short) (ainIndicadoresHospitalares
						.getTotalSaidaOutrosProfissionais() + outProfS));

		ainIndicadoresHospitalares.setTotalSaidaOutrasUnidades((short) (ainIndicadoresHospitalares
				.getTotalSaidaOutrasUnidades() + outUnidS));

		ainIndicadoresHospitalares.setTotalSaidas(ainIndicadoresHospitalares.getTotalSaidas()
				+ saidas);

		ainIndicadoresHospitalares.setPacientesMesAnterior((short) (ainIndicadoresHospitalares
				.getPacientesMesAnterior() + pacMesAnt));

		ainIndicadoresHospitalares.setTotalSaldo((short) (ainIndicadoresHospitalares
				.getTotalSaldo() + sdo));

		ainIndicadoresHospitalares.setTotalBloqueios(ainIndicadoresHospitalares.getTotalBloqueios()
				.add(BigDecimal.valueOf(blq)));

		// this.inserirMensagemListaLog("acumula_tab - total bloqueios",
		// ainIndicadoresHospitalares.getTotalBloqueios());

		BigDecimal pacienteDia = ainIndicadoresHospitalares.getPacienteDia().add(pacDia);
		ainIndicadoresHospitalares.setPacienteDia(pacienteDia);
	}

	/**
	 * ORADB procedure salva_x
	 * 
	 * Salva em variaveis 'x' o conteudo do movimento lido.
	 */
	private void salvaX() {

		indicadoresVO.setXprontuario(indicadoresVO.getYprontuario());
		indicadoresVO.setXsigla(indicadoresVO.getYsigla());
		indicadoresVO.setXunfSeq(indicadoresVO.getYunfSeq());
		indicadoresVO.setXltoId(indicadoresVO.getYltoId());
		indicadoresVO.setXatuSeq(indicadoresVO.getYatuSeq());
		indicadoresVO.setXtamCodigo(indicadoresVO.getYtamCodigo());
		indicadoresVO.setXoevSeq(indicadoresVO.getYoevSeq());
		indicadoresVO.setXdtAlta(indicadoresVO.getYdtAlta());
		indicadoresVO.setXespSeq(indicadoresVO.getYespSeq());
		indicadoresVO.setXserVinCodigo(indicadoresVO.getYserVinCodigo());
		indicadoresVO.setXserMatricula(indicadoresVO.getYserMatricula());
		indicadoresVO.setXdthrLancamento(indicadoresVO.getYdthrLancamento());
		indicadoresVO.setXdthrFinal(indicadoresVO.getYdthrFinal());
		indicadoresVO.setXtmiSeqProx(indicadoresVO.getYtmiSeqProx());
		indicadoresVO.setXespSeqProx(indicadoresVO.getYespSeqProx());
		indicadoresVO.setXunfSeqProx(indicadoresVO.getYunfSeqProx());
		indicadoresVO.setXcctCodigo(indicadoresVO.getYcctCodigo());
		indicadoresVO.setXclcCodigo(indicadoresVO.getYclcCodigo());
		indicadoresVO.setXclcCodigoLto(indicadoresVO.getYclcCodigoLto());
		indicadoresVO.setXclcCodigoPac(indicadoresVO.getYclcCodigoPac());
		indicadoresVO.setXphiSeq(indicadoresVO.getYphiSeq());
		indicadoresVO.setXgphSeq(indicadoresVO.getYgphSeq());
		indicadoresVO.setXcddCodigo(indicadoresVO.getYcddCodigo());
		indicadoresVO.setXbclBaiCodigo(indicadoresVO.getYbclBaiCodigo());
		indicadoresVO.setXdstCodigo(indicadoresVO.getYdstCodigo());
		indicadoresVO.setXindConsClinLto(indicadoresVO.getYindConsClinLto());
		indicadoresVO.setXindConsClinQrt(indicadoresVO.getYindConsClinQrt());
		indicadoresVO.setXindConsClinUnf(indicadoresVO.getYindConsClinUnf());
		indicadoresVO.setXindExclusivInfeccao(indicadoresVO.getYindExclusivInfeccao());
		indicadoresVO.setxOutClin(indicadoresVO.getvOutClin());
		indicadoresVO.setXtipo(indicadoresVO.getYtipo());
		indicadoresVO.setXltoPrivativo(indicadoresVO.getYltoPrivativo());
		indicadoresVO.setXcnvCodigo(indicadoresVO.getYcnvCodigo());

	}

	/**
	 * ORADB procedure ver_clinica_pac
	 */
	private void verClinicaPac() {

		indicadoresVO.setvOutClin("N");
		indicadoresVO.setYclcCodigoPac(indicadoresVO.getYclcCodigo());

		if ("U".equals(indicadoresVO.getYtipo())) {
			if (DominioSimNao.S.equals(indicadoresVO.getYindExclusivInfeccao())) {
				indicadoresVO.setvOutClin("S");
			} else if (DominioSimNao.N.equals(indicadoresVO.getYindConsClinLto())) {
				indicadoresVO.setvOutClin("S");
			} else if (DominioSimNao.N.equals(indicadoresVO.getYindConsClinQrt())) {
				indicadoresVO.setvOutClin("S");
			} else if (DominioSimNao.N.equals(indicadoresVO.getYindConsClinUnf())) {
				indicadoresVO.setvOutClin("S");
			}
		}

		if ("S".equals(indicadoresVO.getvOutClin())) {
			if (indicadoresVO.getYclcCodigo().equals(indicadoresVO.getYclcCodigoLto())) {
				indicadoresVO.setvOutClin("N");
			}
		}
	}

	/**
	 * ORADB procedure ultima_data
	 */
	private void ultimaData(Iterator<MovimentoInternacaoIndicadoresVO> it)
			throws ApplicationBusinessException {

		// this.inserirMensagemListaLog("ultima_data", "");

		while (indicadoresVO.getXdthrLancamento() != null
				&& indicadoresVO.getXdthrLancamento().before(MES_COMPETENCIA)
				&& indicadoresVO.getXdthrFinal() != null
				&& indicadoresVO.getXdthrFinal().before(MES_COMPETENCIA)
				&& isNumerosIguais(indicadoresVO.getXintSeq(), indicadoresVO.getYintSeq())
				&& indicadoresVO.getNaoFim()) {

			// salva em variaveis 'x' o conteudo do movimento lido,
			indicadoresVO.setXtmiSeq(indicadoresVO.getYtmiSeq());
			salvaX();

			// le proximo movimento com data menor
			executarFetch(it);
		}
	}

	private boolean isNumerosIguais(Integer n1, Integer n2) {
		boolean retorno = false;

		if (n1 == null && n2 == null) {
			retorno = true;
		} else {
			if (n1 != null && n2 != null) {
				if (n1.equals(n2)) {
					retorno = true;
				} else {
					retorno = false;
				}
			}
		}
		return retorno;
	}

	/**
	 * ORADB procedure ultimo_mvto
	 */
	private void ultimoMvto(Iterator<MovimentoInternacaoIndicadoresVO> it)
			throws ApplicationBusinessException {

		// this.inserirMensagemListaLog("ultimo_mvto", "");

		// Manter equals() para campos XData e YData, pois são String
		while (indicadoresVO.getXdata() != null
				&& indicadoresVO.getXdata().equals(indicadoresVO.getYdata())
				&& indicadoresVO.getXintSeq() != null
				&& indicadoresVO.getXintSeq().equals(indicadoresVO.getYintSeq())
				&& indicadoresVO.getXtipo() != null
				&& indicadoresVO.getXtipo().equals(indicadoresVO.getYtipo())
				&& indicadoresVO.getNaoFim()) {

			// String naoFim = indicadoresVO.getNaoFim() ? "TRUE" : "FALSE";
			// this.inserirMensagemListaLog("ultimo_mvto - WHILE",
			// indicadoresVO.getXdata(), indicadoresVO.getYdata(),
			// indicadoresVO.getXintSeq(), indicadoresVO.getYintSeq(),
			// indicadoresVO.getXtipo(), indicadoresVO.getYtipo(), naoFim);

			// salva em variaveis 'x' o conteudo do movimento lido,
			// sem alterar o tipo de movimento do primeiro (tmi_seq)
			salvaX();

			// le movimento com mesma data
			executarFetch(it);
		}
	}

	/**
	 * ORADB procedure ultimo_tmi
	 */
	private void ultimoTmi(Iterator<MovimentoInternacaoIndicadoresVO> it)
			throws ApplicationBusinessException {

		// this.inserirMensagemListaLog("ultimo_tmi", "");

		// Manter equals() para a comparacao de Xdata e Ydata, pois são campos
		// string
		while (indicadoresVO.getXdata() != null
				&& indicadoresVO.getXdata().equals(indicadoresVO.getYdata())
				&& indicadoresVO.getXintSeq() != null
				&& indicadoresVO.getXintSeq().equals(indicadoresVO.getYintSeq())
				&& indicadoresVO.getXtipo() != null
				&& indicadoresVO.getXtipo().equals(indicadoresVO.getYtipo())
				&& indicadoresVO.getNaoFim()) {

			// salva em variaveis 'x' o conteudo do movimento lido,
			indicadoresVO.setXtmiSeq(indicadoresVO.getYtmiSeq());
			salvaX();

			// le movimento com mesma data
			executarFetch(it);
		}
	}

	/**
	 * ORADB procedure executa_fetch.
	 */
	private void executarFetch(Iterator<MovimentoInternacaoIndicadoresVO> it)
			throws ApplicationBusinessException {

		// indicadoresVO.addElementoValoresLog(s)
		// this.inserirMensagemListaLog("executa_fetch");

		if (it.hasNext()) {
			MovimentoInternacaoIndicadoresVO vo = (MovimentoInternacaoIndicadoresVO) it.next();

			indicadoresVO.setYprontuario(vo.getProntuario());
			indicadoresVO.setYsigla(vo.getSigla());
			indicadoresVO.setYintSeq(vo.getIntSeq());
			indicadoresVO.setYatuSeq(vo.getAtuSeq());
			indicadoresVO.setYtamCodigo(vo.getTamCodigo());
			indicadoresVO.setYoevSeq(vo.getOevSeq());
			indicadoresVO.setYdtAlta(vo.getDthrAltaMedica());
			indicadoresVO.setYtmiSeq(vo.getTmiSeq());
			indicadoresVO.setYunfSeq(vo.getUnfSeq());
			indicadoresVO.setYltoId(vo.getLtoLtoId());
			indicadoresVO.setYespSeq(vo.getEspSeq());
			indicadoresVO.setYserVinCodigo(vo.getSerVinCodigo());
			indicadoresVO.setYserMatricula(vo.getSerMatricula());
			indicadoresVO.setYdthrLancamento(vo.getDthrLancamento());
			indicadoresVO.setYdthrFinal(vo.getDthrFinalM());
			indicadoresVO.setYtmiSeqProx(vo.getTmiSeqT());
			indicadoresVO.setYespSeqProx(vo.getEspSeqUtil());
			indicadoresVO.setYunfSeqProx(vo.getUnidadeUtil());
			indicadoresVO.setYcctCodigo(vo.getCctCodigo());
			indicadoresVO.setYclcCodigo(vo.getClcCodigo());
			indicadoresVO.setYphiSeq(vo.getProcedimento());
			indicadoresVO.setYgphSeq(vo.getGphSeq());
			indicadoresVO.setYcddCodigo(vo.getCddCodigo());
			indicadoresVO.setYbclBaiCodigo(vo.getBclBaiCodigo());
			indicadoresVO.setYdstCodigo(vo.getDstCodigo());
			indicadoresVO.setYclcCodigoLto(vo.getCodigoClinicaNvl());

			indicadoresVO.setYindConsClinLto(vo.getIndConsClinLto());
			indicadoresVO.setYindConsClinQrt(vo.getIndConsClinQrt());

			DominioSimNao indConsClinUnf = null;
			if (vo.getIndConsClinUnf()) {
				indConsClinUnf = DominioSimNao.S;
			} else if (!vo.getIndConsClinUnf()) {
				indConsClinUnf = DominioSimNao.N;
			}

			indicadoresVO.setYindConsClinUnf(indConsClinUnf);
			indicadoresVO.setYindExclusivInfeccao(vo.getIndExclusivInfeccao());
			indicadoresVO.setYtipo(vo.getTipo());
			indicadoresVO.setYltoPrivativo(vo.getLeitoPrivativo());
			indicadoresVO.setYcnvCodigo(vo.getConvenioSaudePlano() == null ? null : vo
					.getConvenioSaudePlano().getId().getCnvCodigo());

			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

			this.verClinicaPac();
			indicadoresVO.setYdata(sdf.format(vo.getDthrLancamento()));

			// considerar especialidade das unidades (121,141) como neo (307):
			if (Short.valueOf("121").equals(indicadoresVO.getYunfSeq())
					|| Short.valueOf("141").equals(indicadoresVO.getYunfSeq())) {
				indicadoresVO.setYespSeq(Short.valueOf("307"));
			}

			if (Short.valueOf("121").equals(indicadoresVO.getYunfSeqProx())
					|| Short.valueOf("141").equals(indicadoresVO.getYunfSeqProx())) {
				indicadoresVO.setYespSeqProx(Short.valueOf("307"));
			}

			// considerar movimento de alojamento conjunto como alta:
			if (Short.valueOf("152").equals(indicadoresVO.getYunfSeqProx())) {
				indicadoresVO.setYtmiSeqProx(Integer.valueOf("21"));
			}

			if (Short.valueOf("21").equals(indicadoresVO.getYtmiSeq())) {
				indicadoresVO.setNaoAlta(false);
			} else {
				indicadoresVO.setNaoAlta(true);
			}

		} else {
			indicadoresVO.setNaoFim(false);
		}
	}

	/**
	 * ORADB procedure controle_data
	 */
	private void controleData(Iterator<MovimentoInternacaoIndicadoresVO> it)
			throws ApplicationBusinessException {

		// SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		// String dataI = indicadoresVO.getXdthrLancamento() == null ? "" :
		// sdf.format(indicadoresVO.getXdthrLancamento());
		// String dataF = indicadoresVO.getXdthrFinal() == null ? "" :
		// sdf.format(indicadoresVO.getXdthrFinal());

		// this.inserirMensagemListaLog("controle_data", dataI, dataF,
		// MES_COMPETENCIA);

		if (indicadoresVO.getXdthrLancamento() != null
				&& indicadoresVO.getXdthrLancamento().before(MES_COMPETENCIA)
				&& indicadoresVO.getXdthrFinal() != null
				&& indicadoresVO.getXdthrFinal().before(MES_COMPETENCIA)) {

			// this.inserirMensagemListaLog("controle_data - IF 1", "");
			indicadoresVO.setvOrgmAsat("N");
			ultimaData(it);
		} else if (indicadoresVO.getXdthrLancamento() != null
				&& (indicadoresVO.getXdthrLancamento().after(MES_COMPETENCIA) || indicadoresVO
						.getXdthrLancamento().compareTo(MES_COMPETENCIA) == 0)) {

			// this.inserirMensagemListaLog("controle_data - IF 2", "");
			ultimoMvto(it);
		} else {

			// this.inserirMensagemListaLog("controle_data - IF 3", "");
			indicadoresVO.setvOrgmAsat("N");
			ultimoTmi(it);
		}

		if (!indicadoresVO.getXunfSeq().equals(indicadoresVO.getXunfSeqProx())
				|| Byte.valueOf("13").equals(indicadoresVO.getXtmiSeqProx())) {
			indicadoresVO.setNaoQuebra(false);
		}
	}

	/**
	 * ORADB procedure controle_mvtos
	 * 
	 * @throws ApplicationBusinessException
	 */
	private void carregarControleMovimentos() throws ApplicationBusinessException {

		// this.inserirMensagemListaLog("controle_mvtos", "");

		List<MovimentoInternacaoIndicadoresVO> list = this.formatarMovimentosInternacao();

		Iterator<MovimentoInternacaoIndicadoresVO> it = list.iterator();
		executarFetch(it);

		// TODO verificar valores hard coded
		while (indicadoresVO.getNaoFim()) {
			// controla quebra para internacao, unidade, especialidade, servidor
			// e clinica
			if (indicadoresVO.getXintSeq().intValue() != indicadoresVO.getYintSeq().intValue()) {
				if (indicadoresVO.getYatuSeq() != null) {
					indicadoresVO.setvOrgmAsat("S");
					indicadoresVO.setvOrgmBlc("N");
				} else {
					indicadoresVO.setvOrgmAsat("N");
					if ("B".equals(indicadoresVO.getYtipo())) {
						indicadoresVO.setvOrgmBlc("S");
					} else {
						indicadoresVO.setvOrgmBlc("N");
					}
				}
			}

			indicadoresVO.setNaoQuebra(true);
			indicadoresVO.setXintSeq(indicadoresVO.getYintSeq());
			indicadoresVO.setXtmiSeq(indicadoresVO.getYtmiSeq());
			salvaX();
			indicadoresVO.setZespSeq(0);

			while (indicadoresVO.getXintSeq().equals(indicadoresVO.getYintSeq())
					&& indicadoresVO.getXunfSeq().equals(indicadoresVO.getYunfSeq())
					&& indicadoresVO.getXespSeq().equals(indicadoresVO.getYespSeq())
					&& indicadoresVO.getXserVinCodigo().equals(indicadoresVO.getYserVinCodigo())
					&& indicadoresVO.getXserMatricula().equals(indicadoresVO.getYserMatricula())
					&& indicadoresVO.getXclcCodigo().equals(indicadoresVO.getYclcCodigo())
					&& indicadoresVO.getXtipo().equals(indicadoresVO.getYtipo())
					&& indicadoresVO.getNaoFim() && indicadoresVO.getNaoQuebra()) {
				indicadoresVO.setXdata(indicadoresVO.getYdata());
				controleData(it);
			}

			if (!Short.valueOf("205").equals(indicadoresVO.getXunfSeq())
					&& !Short.valueOf("207").equals(indicadoresVO.getXunfSeq())
					&& !Short.valueOf("152").equals(indicadoresVO.getXunfSeq())) {
				this.gerarIndicador();
			}
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private Integer posicionaTab(Integer pClin, Short pUnid, Short pEsp, Short pVin, Integer pMatr,
			String pTipo, Short pConv) {

		// this.inserirMensagemListaLog("posiciona_tab", pClin, pUnid, pEsp,
		// pVin,
		// pMatr, pTipo, pConv);

		indicadoresVO.setIndTab(0);

		AinIndicadoresHospitalares ainIndicadoresHospitalares = null;
		Boolean tipoUnidadeTeste = null;
		Boolean cnvCodigoTeste = null;
		Boolean clcCodigoTeste = null;
		Boolean unfSeqTeste = null;
		Boolean espSeqTeste = null;
		Boolean serVinCodigoTeste = null;
		Boolean serMatriculaTeste = null;

		while (indicadoresVO.getIndTab() <= indicadoresVO.getLstTab()) {

			indicadoresVO.incrIndTab();

			if (indicadoresVO.getTabIndicadores().containsKey(indicadoresVO.getIndTab())) {

				ainIndicadoresHospitalares = indicadoresVO.getTabIndicadores().get(
						indicadoresVO.getIndTab());

				tipoUnidadeTeste = StringUtils.equalsIgnoreCase(pTipo, ainIndicadoresHospitalares
						.getTipoUnidade() == null ? null : ainIndicadoresHospitalares
						.getTipoUnidade().toString());

				// Para os valores abaixo foi necessário fazer um cast antes das
				// comparações "==", pois caso duas classes Wrapper (Short,
				// Integer etc) tivessem o mesmo valor, não garantia a igualdade
				// dos dados usando "==".
				cnvCodigoTeste = (short) (ainIndicadoresHospitalares.getCnvCodigo() == null ? 0
						: ainIndicadoresHospitalares.getCnvCodigo()) == (short) (pConv == null ? 0
						: pConv);

				clcCodigoTeste = (int) (ainIndicadoresHospitalares.getClinica() == null ? 0
						: ainIndicadoresHospitalares.getClinica().getCodigo()) == (int) (pClin == null ? 0
						: pClin);

				unfSeqTeste = (short) (ainIndicadoresHospitalares.getUnidadeFuncional() == null ? 0
						: ainIndicadoresHospitalares.getUnidadeFuncional().getSeq()) == (short) (pUnid == null ? 0
						: pUnid);

				espSeqTeste = (short) (ainIndicadoresHospitalares.getEspecialidade() == null ? 0
						: ainIndicadoresHospitalares.getEspecialidade().getSeq()) == (short) (pEsp == null ? 0
						: pEsp);

				serVinCodigoTeste = (short) (ainIndicadoresHospitalares.getSerVinCodigo() == null ? 0
						: ainIndicadoresHospitalares.getSerVinCodigo()) == (short) (pVin == null ? 0
						: pVin);

				serMatriculaTeste = (int) (ainIndicadoresHospitalares.getSerMatricula() == null ? 0
						: ainIndicadoresHospitalares.getSerMatricula()) == (int) (pMatr == null ? 0
						: pMatr);

				if (tipoUnidadeTeste && cnvCodigoTeste && clcCodigoTeste && unfSeqTeste
						&& espSeqTeste && serVinCodigoTeste && serMatriculaTeste) {
					return indicadoresVO.getIndTab();
				}
			}
		}

		indicadoresVO.setLstTab(indicadoresVO.getIndTab());
		this.inicializaTab(indicadoresVO.getIndTab(), pClin, pUnid, pEsp, pVin, pMatr, pTipo, pConv);

		return indicadoresVO.getIndTab();

	}

	/*
	 * private void inserirMensagemListaLog(String nomeMetodo, Object...
	 * valores) { indicadoresVO.incrementarCountLog();
	 * 
	 * StringBuilder sb = new StringBuilder();
	 * sb.append(indicadoresVO.getCountLog()).append(';');
	 * sb.append(nomeMetodo).append(';');
	 * 
	 * for (Object valor : valores) { sb.append(valor).append(','); }
	 * 
	 * sb.append(';').append("xunf_seq=").append(indicadoresVO.getXunfSeq());
	 * sb.
	 * append(",").append("xclc_codigo=").append(indicadoresVO.getXclcCodigo());
	 * sb.append(',').append("xesp_seq=").append(indicadoresVO.getXespSeq());
	 * sb.append(',').append("yunf_seq=").append(indicadoresVO.getYunfSeq());
	 * sb.
	 * append(",").append("yclc_codigo=").append(indicadoresVO.getYclcCodigo());
	 * sb.append(',').append("yesp_seq=").append(indicadoresVO.getYespSeq());
	 * 
	 * indicadoresVO.addElementoValoresLog(sb.toString()); }
	 */

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void inicializaTab(Integer pInd, Integer pClin, Short pUnid, Short pEsp, Short pVin,
			Integer pMatr, String pTipo, Short pConv) {

		// this.inserirMensagemListaLog("inicializa_tab", pInd, pClin, pUnid,
		// pEsp, pVin, pMatr, pTipo, pConv);

		AinIndicadoresHospitalares indicadoresHospitalares = new AinIndicadoresHospitalares();

		AghEspecialidades aghEspecialidades = pEsp == null ? null : getAghuFacade()
				.obterAghEspecialidadesPorChavePrimaria(pEsp);
		
		AghClinicas aghClinicas = pClin == null ? null : getAghuFacade().obterClinica(pClin);
		
		AghUnidadesFuncionais aghUnidade = pUnid == null ? null : getAghuFacade()
				.obterAghUnidadesFuncionaisPorChavePrimaria(pUnid);

		indicadoresHospitalares.setCompetenciaInternacao(DateUtils.truncate(MES_COMPETENCIA,
				Calendar.DAY_OF_MONTH));
		indicadoresHospitalares.setClinica(aghClinicas);
		indicadoresHospitalares.setUnidadeFuncional(aghUnidade);
		indicadoresHospitalares.setEspecialidade(aghEspecialidades);
		indicadoresHospitalares.setSerMatricula(pMatr);
		indicadoresHospitalares.setTipoUnidade(DominioTipoUnidade.valueOf(pTipo));
		indicadoresHospitalares.setSerVinCodigo(pVin);
		indicadoresHospitalares.setTotalInternacoesMes(0);
		indicadoresHospitalares.setTotalIntAreaSatelite(0);
		indicadoresHospitalares.setTotalEntreOutrasClinicas(Short.valueOf("0"));
		indicadoresHospitalares.setTotalEntreOutrasEspecialidades(Short.valueOf("0"));
		indicadoresHospitalares.setTotalEntreOutrosProfissionais(Short.valueOf("0"));
		indicadoresHospitalares.setTotalEntreOutrasUnidades(Short.valueOf("0"));
		indicadoresHospitalares.setTotalEntradas(0);
		indicadoresHospitalares.setTotalAltas(0);
		indicadoresHospitalares.setTotalObitosMenos48h(Short.valueOf("0"));
		indicadoresHospitalares.setTotalObitosMais48h(0);
		indicadoresHospitalares.setTotalSaidaOutrasClinicas(Short.valueOf("0"));
		indicadoresHospitalares.setTotalSaidaOutrasEspecialidades(Short.valueOf("0"));
		indicadoresHospitalares.setTotalSaidaOutrosProfissionais(Short.valueOf("0"));
		indicadoresHospitalares.setTotalSaidaOutrasUnidades(Short.valueOf("0"));
		indicadoresHospitalares.setTotalSaidas(0);
		indicadoresHospitalares.setPacientesMesAnterior(Short.valueOf("0"));
		indicadoresHospitalares.setTotalSaldo(Short.valueOf("0"));
		indicadoresHospitalares.setTotalBloqueios(BigDecimal.valueOf(0));
		indicadoresHospitalares.setPacienteDia(BigDecimal.valueOf(0));
		indicadoresHospitalares.setCapacidadeReferencial(BigDecimal.valueOf(0));
		indicadoresHospitalares.setLeitoDia(0);

		if (pVin != null) {
			indicadoresHospitalares.setCapacidadeReferencial(getAghuFacade()
					.capacidadeProf(pEsp, pVin, pMatr));
			indicadoresHospitalares.setTotalBloqueios(BigDecimal.valueOf(0));

		} else if (pEsp != null) {
			if (indicadoresVO.getTabEsp().containsKey(pEsp)) {
				CapacidadeEspecialidadeIndicadoresVO aghEspecialidade = indicadoresVO.getTabEsp()
						.get(pEsp);
				if (aghEspecialidade.getCapacidade() == null) {
					indicadoresHospitalares.setCapacidadeReferencial(BigDecimal.valueOf(0));
				} else {
					indicadoresHospitalares.setCapacidadeReferencial(BigDecimal
							.valueOf(aghEspecialidade.getCapacidade()));
				}
				indicadoresHospitalares.setTotalBloqueios(BigDecimal.valueOf(0));
			}

		} else if (pUnid != null) {
			if ("A".equalsIgnoreCase(pTipo) || "U".equalsIgnoreCase(pTipo)) {
				if (indicadoresVO.getTabUnid().containsKey(pUnid)) {
					UnidadeFuncionalIndicadoresVO unidadeFuncionalIndicadoresVO = indicadoresVO
							.getTabUnid().get(pUnid);

					if (unidadeFuncionalIndicadoresVO != null) {
						Short capacidade = unidadeFuncionalIndicadoresVO.getCapacidade() == null ? 0
								: unidadeFuncionalIndicadoresVO.getCapacidade();
						Integer blqDesat = unidadeFuncionalIndicadoresVO.getBlqDesat() == null ? 0
								: unidadeFuncionalIndicadoresVO.getBlqDesat();

						indicadoresHospitalares.setCapacidadeReferencial(BigDecimal
								.valueOf(capacidade - blqDesat));
						indicadoresHospitalares.setTotalBloqueios(unidadeFuncionalIndicadoresVO
								.getBloqueios());

						// this.inserirMensagemListaLog("inicializa_tab A,U - capacidade",
						// capacidade);
						// this.inserirMensagemListaLog("inicializa_tab A,U - blq_desat",
						// blqDesat);
						// this.inserirMensagemListaLog("inicializa_tab A,U - total bloqueios",
						// unidadeFuncionalIndicadoresVO.getBloqueios());
					}
				}

			} else if ("P".equalsIgnoreCase(pTipo)) {
				if (indicadoresVO.getTabUnidPriv().containsKey(pUnid)) {
					LeitoIndicadoresVO leitoIndicadoresVO = indicadoresVO.getTabUnidPriv().get(
							pUnid);
					if (leitoIndicadoresVO != null) {
						if (leitoIndicadoresVO.getCapacidade() == null) {
							indicadoresHospitalares.setCapacidadeReferencial(BigDecimal.valueOf(0));
						} else {
							indicadoresHospitalares.setCapacidadeReferencial(BigDecimal
									.valueOf(leitoIndicadoresVO.getCapacidade()));
						}
						indicadoresHospitalares.setTotalBloqueios(leitoIndicadoresVO.getBloqueio());

						// this.inserirMensagemListaLog("inicializa_tab P - capacidade",
						// indicadoresHospitalares.getCapacidadeReferencial());
						// this.inserirMensagemListaLog("inicializa_tab P - total bloqueios",
						// indicadoresHospitalares.getTotalBloqueios());
					}
				}
			}

		} else if (pClin != null) {
			if ("U".equalsIgnoreCase(pTipo)) {
				if (indicadoresVO.getTabClinicas().containsKey(pClin)) {
					ClinicaIndicadoresVO clinicaIndicadoresVO = indicadoresVO.getTabClinicas().get(
							pClin);

					if (clinicaIndicadoresVO != null) {
						Integer capacidade = clinicaIndicadoresVO.getCapacidade() == null ? 0
								: clinicaIndicadoresVO.getCapacidade();
						Integer blqDesat = clinicaIndicadoresVO.getBlqDesat() == null ? 0
								: clinicaIndicadoresVO.getBlqDesat();
						indicadoresHospitalares.setCapacidadeReferencial(BigDecimal
								.valueOf(capacidade - blqDesat));
						indicadoresHospitalares.setTotalBloqueios(clinicaIndicadoresVO
								.getBloqueios());

						// this.inserirMensagemListaLog("inicializa_tab U - capacidade",
						// capacidade);
						// this.inserirMensagemListaLog("inicializa_tab U - blq_desat",
						// blqDesat);
						// this.inserirMensagemListaLog("inicializa_tab U - total bloqueios",
						// indicadoresHospitalares.getTotalBloqueios());
					}
				}
			} else {
				UnidadeFuncionalIndicadoresVO unidadesFuncionaisVO = indicadoresVO.getTabUnid()
						.get(indicadoresVO.getXunfSeq());
				if (unidadesFuncionaisVO != null) {
					Short capacidade = unidadesFuncionaisVO.getCapacidade() == null ? 0
							: unidadesFuncionaisVO.getCapacidade();
					indicadoresHospitalares
							.setCapacidadeReferencial(BigDecimal.valueOf(capacidade));

					// this.inserirMensagemListaLog("inicializa_tab ELSE - capacidade ref",
					// indicadoresHospitalares.getCapacidadeReferencial());
				}
			}
		}

		// @Alterado
		// if (indicadoresHospitalares.getCapacidadeReferencial() == null) {
		// indicadoresHospitalares.setLeitoDia(0);
		// } else {
		// indicadoresHospitalares.setLeitoDia(indicadoresHospitalares.getCapacidadeReferencial().multiply(BigDecimal.valueOf(X_DIAS_MES)).intValue());
		// }
		indicadoresHospitalares.setLeitoDia(indicadoresHospitalares.getCapacidadeReferencial()
				.multiply(BigDecimal.valueOf(X_DIAS_MES)).intValue());
		indicadoresHospitalares.setPercentualOcupacao(BigDecimal.valueOf(0));
		indicadoresHospitalares.setMediaPacienteDia(BigDecimal.valueOf(0));
		indicadoresHospitalares.setMediaPermanencia(BigDecimal.valueOf(0));
		indicadoresHospitalares.setIndiceMorteGeral(BigDecimal.valueOf(0));
		indicadoresHospitalares.setIndiceMorteEspecifico(BigDecimal.valueOf(0));
		indicadoresHospitalares.setIndiceIntervaloSubstituicao(BigDecimal.valueOf(0));
		indicadoresHospitalares.setIndiceRenovacao(BigDecimal.valueOf(0));
		indicadoresHospitalares.setIhSeq(null);
		indicadoresHospitalares.setCnvCodigo(pConv);
		indicadoresHospitalares.setCctCodigo(null);
		// unfSeq já foi setada anteriormente, não precisa ser setado novamente
		indicadoresHospitalares.setCddCodigo(null);
		indicadoresHospitalares.setDstCodigo(null);
		indicadoresHospitalares.setBaiCodigo(null);
		indicadoresHospitalares.setPhiSeq(null);
		indicadoresHospitalares.setGphSeq(null);

		indicadoresVO.getTabIndicadores().put(pInd, indicadoresHospitalares);
	}

	private void corrigirCapacidades() {
		Set<Integer> indicesTabClinicas = indicadoresVO.getTabClinicas().keySet();
		ClinicaIndicadoresVO clinicaIndicadoresVO;

		for (Integer indice : indicesTabClinicas) {
			clinicaIndicadoresVO = indicadoresVO.getTabClinicas().get(indice);

			if (indicadoresVO.getTabClinicas().containsKey(indice)
					&& (Integer.valueOf(1).equals(clinicaIndicadoresVO.getClinica())
							|| Integer.valueOf(2).equals(clinicaIndicadoresVO.getClinica())
							|| Integer.valueOf(3).equals(clinicaIndicadoresVO.getClinica())
							|| Integer.valueOf(5).equals(clinicaIndicadoresVO.getClinica()) || Integer
							.valueOf(7).equals(clinicaIndicadoresVO.getClinica()))) {
				if (clinicaIndicadoresVO.getCapcMais() > 0
						|| clinicaIndicadoresVO.getCapcMenos() > 0) {
					Integer ind = posicionaTab(clinicaIndicadoresVO.getClinica(), null, null, null,
							null, "U", null);
					AinIndicadoresHospitalares indicadoresHospitalares = indicadoresVO
							.getTabIndicadores().get(ind);

					BigDecimal capacidade = indicadoresHospitalares.getCapacidadeReferencial() == null ? BigDecimal
							.valueOf(0) : indicadoresHospitalares.getCapacidadeReferencial();
					Integer capcMais = clinicaIndicadoresVO.getCapcMais() == null ? 0
							: clinicaIndicadoresVO.getCapcMais();
					Integer capcMenos = clinicaIndicadoresVO.getCapcMenos() == null ? 0
							: clinicaIndicadoresVO.getCapcMenos();
					BigDecimal capacidadeReferencial = capacidade.add(BigDecimal.valueOf(
							capcMais - capcMenos).divide(BigDecimal.valueOf(X_DIAS_MES), 2,
							BigDecimal.ROUND_HALF_UP));

					indicadoresHospitalares.setCapacidadeReferencial(capacidadeReferencial);

					Integer leitoDia = indicadoresHospitalares.getLeitoDia() == null ? 0
							: indicadoresHospitalares.getLeitoDia();
					indicadoresHospitalares.setLeitoDia(leitoDia + capcMais - capcMenos);
				}
			}
		}
	}

	/**
	 * ORADB grava_indicadores
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void gravarIndicadores() {

		Collection<AinIndicadoresHospitalares> indices = indicadoresVO.getTabIndicadores().values();

		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		
		for (AinIndicadoresHospitalares indicador : indices) {
			Integer leitoDia = indicador.getLeitoDia() == null ? 0 : indicador.getLeitoDia();
			// Integer totalBloqueio = indicador.getTotalBloqueios() == null ? 0
			// : indicador.getTotalBloqueios().intValue();
			BigDecimal totalBloqueio = indicador.getTotalBloqueios() == null ? BigDecimal
					.valueOf(0) : indicador.getTotalBloqueios();

			indicadoresVO.setvLtoDia(leitoDia); // - totalBloqueio.setScale(0,
												// BigDecimal.ROUND_HALF_UP).intValue());
												// //@alterado

			// @alterado (retirado total bloqueados do calculo para ficar igual
			// ao IG)
			BigDecimal capacidadeReferencial = indicador.getCapacidadeReferencial() == null ? BigDecimal
					.valueOf(0) : indicador.getCapacidadeReferencial();
			// indicadoresVO.setvCapacidade(capacidadeReferencial); // -
			// (totalBloqueio / X_DIAS_MES));
			indicadoresVO.setvCapacidade(capacidadeReferencial.subtract(totalBloqueio.divide(
					BigDecimal.valueOf(X_DIAS_MES), 2, BigDecimal.ROUND_HALF_UP)));

			BigDecimal pacienteDia = indicador.getPacienteDia() == null ? BigDecimal.valueOf(0)
					: indicador.getPacienteDia();

			if (indicadoresVO.getvLtoDia() > 0) {
				indicador.setPercentualOcupacao(pacienteDia.multiply(BigDecimal.valueOf(100))
						.divide(BigDecimal.valueOf(indicadoresVO.getvLtoDia()), 2,
								BigDecimal.ROUND_HALF_UP));
				indicadoresVO.setvOcupacao((pacienteDia.multiply(BigDecimal.valueOf(100)))
						.divide(BigDecimal.valueOf(indicadoresVO.getvLtoDia()), 2,
								BigDecimal.ROUND_HALF_UP).intValue());
			}

			indicador.setMediaPacienteDia(pacienteDia.divide(BigDecimal.valueOf(X_DIAS_MES), 2,
					BigDecimal.ROUND_HALF_UP));

			if (indicador.getTotalSaidas() > 0) {
				indicador
						.setMediaPermanencia(pacienteDia.divide(
								BigDecimal.valueOf(indicador.getTotalSaidas()), 2,
								BigDecimal.ROUND_HALF_UP));
				indicador.setIndiceMorteGeral(BigDecimal.valueOf(((indicador
						.getTotalObitosMenos48h() + indicador.getTotalObitosMais48h()) * 100)
						/ indicador.getTotalSaidas()));
				indicador.setIndiceMorteEspecifico(BigDecimal.valueOf((indicador
						.getTotalObitosMais48h() * 100) / indicador.getTotalSaidas()));
			}

			if (indicador.getPercentualOcupacao().compareTo(BigDecimal.valueOf(0)) > 0) {
				indicador.setIndiceIntervaloSubstituicao(BigDecimal.valueOf(100)
						.subtract(indicador.getPercentualOcupacao())
						.multiply(indicador.getMediaPermanencia())
						.divide(indicador.getPercentualOcupacao(), 2, BigDecimal.ROUND_HALF_UP));
			}

			if (indicadoresVO.getvCapacidade() != null
					&& indicadoresVO.getvCapacidade().compareTo(BigDecimal.valueOf(0)) > 0) {
				indicador.setIndiceRenovacao(BigDecimal.valueOf(indicador.getTotalSaidas()).divide(
						indicadoresVO.getvCapacidade(), 2, BigDecimal.ROUND_HALF_UP));
			}

			indicador.setCapacidadeReferencial(indicadoresVO.getvCapacidade());
			indicador.setLeitoDia(indicadoresVO.getvLtoDia());

			// TODO: verificar se será necessário chamar um "commit" explícito
			// v_commit := v_commit + 1;
			// if v_commit > 1000 then
			// v_commit := 0;
			// commit;
			// end if;

			internacaoFacade.inserirAinIndicadoresHospitalares(indicador);

		}
	}

	/**
	 * ORADB dias_bloqueio
	 */
	private Integer diasBloqueio(Integer pUnid, Integer pQuarto) {
		// this.inserirMensagemListaLog("dias_bloqueio", pUnid, pQuarto);
		// this.inserirMensagemListaLog("dias_bloqueio - valor de lst",
		// indicadoresVO.getLst());
		// this.inserirMensagemListaLog("dias_bloqueio - tamanho lista",
		// indicadoresVO.getTabBlq().size());

		indicadoresVO.setDiasAcum(0);
		// Collection<LeitosBloqueadosIndicadoresVO> indices = indicadoresVO
		// .getTabBlq().values();

		// for (LeitosBloqueadosIndicadoresVO leitosBlq : indices) {
		LeitosBloqueadosIndicadoresVO leitosBlq = null;
		BigDecimal dias = null;
		for (int i = 1; i <= indicadoresVO.getLst(); i++) {
			leitosBlq = indicadoresVO.getTabBlq().get(i);

			if (leitosBlq == null) {
				continue;
			}

			// this.inserirMensagemListaLog("dias_bloqueio",
			// leitosBlq.getUnfSeq() + ", " + leitosBlq.getLtoId(), "");
			if ((int) leitosBlq.getUnfSeq() > (int) pUnid) {
				// this.inserirMensagemListaLog("dias_bloqueio - IF01", "");
				return indicadoresVO.getDiasAcum();
			} else {
				// this.inserirMensagemListaLog("dias_bloqueio - IF02", "");
				if ((int) leitosBlq.getUnfSeq() == (int) pUnid) {
					Short quartoId = getInternacaoFacade().obterNumeroQuartoPorLeito(leitosBlq.getLtoId());
					if(quartoId == null){
						quartoId = 0;
					}
					// this.inserirMensagemListaLog("dias_bloqueio - IF03", "");
					if (quartoId.intValue() > pQuarto.intValue()) {
						// this.inserirMensagemListaLog("dias_bloqueio - IF04",
						// "");
						return indicadoresVO.getDiasAcum();
					} else {
						// this.inserirMensagemListaLog("dias_bloqueio - IF05",
						// "");
						
						if (quartoId.intValue() == pQuarto.intValue() && DominioSimNao.S.equals(leitosBlq.getIndBlq())) {
							// this.inserirMensagemListaLog("dias_bloqueio - IF06",
							// "");

							dias = leitosBlq.getDias().setScale(0, BigDecimal.ROUND_HALF_UP);
							indicadoresVO
									.setDiasAcum(indicadoresVO.getDiasAcum() + dias.intValue());
						}
					}
				}
			}
		}
		return 0;
	}

	/**
	 * ORADB capc_mais
	 * 
	 * @param pClinMais
	 * @param pClinMenos
	 * @param pDias
	 */
	private void capcMais(Integer pClinMais, Integer pClinMenos, Integer pDias) {
		// this.inserirMensagemListaLog("capc_mais", pClinMais, pClinMenos,
		// pDias);

		if (indicadoresVO.getTabClinicas().containsKey(pClinMais)) {
			ClinicaIndicadoresVO clinicaIndicadoresVO = indicadoresVO.getTabClinicas().get(
					pClinMais);
			clinicaIndicadoresVO.setCapcMais(clinicaIndicadoresVO.getCapcMais() + pDias);
		}
		if (indicadoresVO.getTabClinicas().containsKey(pClinMenos)) {
			ClinicaIndicadoresVO clinicaIndicadoresVO = indicadoresVO.getTabClinicas().get(
					pClinMenos);
			clinicaIndicadoresVO.setCapcMenos(clinicaIndicadoresVO.getCapcMenos() + pDias);
		}
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
}