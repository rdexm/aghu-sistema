package br.gov.mec.aghu.paciente.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipProntuariosImpressos;
import br.gov.mec.aghu.model.AipProntuariosImpressosId;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipProntuariosImpressosDAO;
import br.gov.mec.aghu.paciente.vo.PacienteZplVo;
import br.gov.mec.aghu.paciente.vo.ZplVo;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio para geração do Relatório
 * Etiquetas de Movimentacao de Prontuários.
 * 
 * @author Ricardo Costa
 * 
 */

@Stateless
@SuppressWarnings("PMD.ExcessiveClassLength")
public class EtiquetasMovimentacaoProntuarioON extends BaseBusiness {


private static final String TURNO_ZONA_SALA = "Turno/Zona/Sala: ";

@EJB
private EtiquetasON etiquetasON;

private static final Log LOG = LogFactory.getLog(EtiquetasMovimentacaoProntuarioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipProntuariosImpressosDAO aipProntuariosImpressosDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IPacienteFacade pacienteFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private ICadastroPacienteFacade cadastroPacienteFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1783186052492325554L;

	private enum MovimentacaoONExceptionCode implements BusinessExceptionCode {
		NAO_HA_DADOS_DATA, ERRO_AO_GERAR_ETIQUETAS, IMPRESSAO_CANCELADA;
	}

	/**
	 * String separator.
	 */
	private static final String stringSeparator = ".";

	/**
	 * Cursor: c_pac_consultas
	 * 
	 * @param dtReferencia
	 * @param isReprint
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Map<Integer, PacConsultaVo> obterPacConsultas(Date dtReferencia,
			Boolean isReprint) throws ApplicationBusinessException {
		IParametroFacade parametroFacade = this.getParametroFacade();
		
		Map<Integer, PacConsultaVo> hashProntPacConsultaVo = new HashMap<Integer, PacConsultaVo>();

		// Map<Integer, PacConsultaVo> hashProntPacConsultaVo = new
		// LinkedHashMap<Integer, PacConsultaVo>(
		// 0);

		// Data from
		Calendar cal = new GregorianCalendar();
		cal.setTime(dtReferencia);
		Date dtFrom = new GregorianCalendar(cal.get(Calendar.YEAR), cal
				.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
				.getTime();

		// Data to
		cal = new GregorianCalendar();
		cal.setTime(dtReferencia);
		Date dtTo = new GregorianCalendar(cal.get(Calendar.YEAR), cal
				.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59,
				59).getTime();
		
		List<Object[]> res = this.getAmbulatorioFacade().obterPacConsultas(dtFrom, dtTo, isReprint, stringSeparator);

		Iterator<Object[]> it = res.iterator();

		Short vUnfCaps = (Short) parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_UNIDADE_CAPS).getVlrNumerico()
				.shortValue();

		Short vUnf17 = (Short) parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_UNIDADE_ZONA17).getVlrNumerico()
				.shortValue();

		Short vUnf19 = (Short) parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_UNIDADE_ZONA19).getVlrNumerico()
				.shortValue();

		while (it.hasNext()) {
			Object[] obj = it.next();

			PacConsultaVo vo = new PacConsultaVo();

			Integer pacCod = null;
			Short unfSeq = null;
			Short espSeq = null;
			Short seq = null;

			if (obj[2] != null) {
				unfSeq = (Short) obj[2];
			}

			if (obj[3] != null) {
				espSeq = (Short) obj[3];
			}

			if (obj[4] != null) {
				seq = ((Integer) obj[4]).shortValue();
			}

			if (obj[5] != null) {
				pacCod = (Integer) obj[5];
			}

			// Caso não valide, não adiciona no hash. Isso está substituindo uma
			// claúsula AND.
			if (!this.verEnvioPront(unfSeq, pacCod, espSeq, seq, vUnf17,
					vUnf19, vUnfCaps)) {
				continue;
			}

			if (obj[0] != null) {
				vo.setProntuario((Integer) obj[0]);
			}

			if (obj[1] != null) {
				vo.setDtConsulta((Date) obj[1]);
			}

			vo.setIndImpresso(false);

			if (!hashProntPacConsultaVo.containsKey(vo.getProntuario())) {
				hashProntPacConsultaVo.put(vo.getProntuario(), vo);
			}
		}

		return hashProntPacConsultaVo;
	}

	/**
	 * Método utilizado para obter dados que irão gerar as etiquetas na
	 * funcionalidade de Etiquetas por Movimentação de Prontuário. A procedure é
	 * AIPP_IMPR_POR_ZONA e FMB é AIPF_ETIQ_MOV_PRNT.
	 * 
	 * @param dtReferencia
	 * @param isReprint
	 * @param turnoDe
	 * @param turnoAte
	 * @param zonaDe
	 * @param zonaAte
	 * @param salaDe
	 * @param salaAte
	 * @return String
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	public ZplVo obterDadosEtiquetas(Date dtReferencia, Boolean isReprint,
			Integer turnoDe, Integer turnoAte, String zonaDe, String zonaAte,
			Integer salaDe, Integer salaAte,
			Map<Integer, PacConsultaVo> hashProntPacConsultaVo,
			Map<Integer, AipProntuariosImpressos> hashProntImpressos)
			throws ApplicationBusinessException {
		EtiquetasON etiquetasON = getEtiquetasON();
		
		// Obtem os pacientes
		List<PacienteZplVo> pacientes = this.obterPacientes(dtReferencia,
				isReprint, turnoDe, turnoAte, zonaDe, zonaAte, salaDe, salaAte);

		String vtznsl = null;

		// impressão.
		String vtznslInicial = null;
		String vtznslFinal = null;

		// Quantidade de etiquetas impressas.
		Integer countEtiquetas = 0;

		StringBuffer zpl = new StringBuffer(32);

		// Variáveis auxiliares.
		Boolean imp = false;
		Boolean ok = false;

		// Variével auxiliar que indica que não há etiquetas a serem impressas
		// ainda.
		boolean inicio = true;

		List<PacienteZplVo> pacZplVos = new ArrayList<PacienteZplVo>(0);

		Iterator<PacienteZplVo> it = pacientes.iterator();

		while (it.hasNext()) {
			PacienteZplVo pacVo = (PacienteZplVo) it.next();

			if (isReprint) {
				imp = this.verProntuarioImpresso(pacVo.getCodigo(),
						hashProntImpressos);
			}

			if ((isReprint && imp) || (!isReprint && !imp)) {

				ok = this.verConsulta(hashProntPacConsultaVo.get(pacVo
						.getProntuario()), pacVo.getDtConsulta());

				if (vtznsl == null) {
					vtznsl = pacVo.getDesc();
					if (ok) {
						zpl.append(etiquetasON.gerarZpl(99999999, (short) 0,
								TURNO_ZONA_SALA + pacVo.getDesc()));

						inicio = false;
					}
				}

				if (!vtznsl.equals(pacVo.getDesc())) {
					if (ok) {
						zpl.append(etiquetasON.gerarZpl(99999999, (short) 0,
								TURNO_ZONA_SALA + pacVo.getDesc()));

						vtznsl = pacVo.getDesc();

						// Gera Etiqueta
						countEtiquetas++;

						if (countEtiquetas == 1) {
							vtznslInicial = vtznsl;
						}

						vtznslFinal = vtznsl;

						zpl.append(etiquetasON.gerarZpl(pacVo.getProntuario(),
								(short) 0, pacVo.getNome()));

						pacZplVos.add(pacVo);

						// this.atualizaEtiquetasImpressas(pacVo, isReprint,
						// dtReferencia, hashProntPacConsultaVo);
					}
				} else {
					if (ok) {
						if (countEtiquetas == 0 && inicio) {
							zpl.append(etiquetasON.gerarZpl(99999999,
									(short) 0, TURNO_ZONA_SALA
											+ pacVo.getDesc()));

							// Gera Etiqueta
							countEtiquetas++;

							if (countEtiquetas == 1) {
								vtznslInicial = vtznsl;
							}

							vtznslFinal = vtznsl;

							zpl.append(etiquetasON.gerarZpl(pacVo
									.getProntuario(), (short) 0, pacVo
									.getNome()));

							pacZplVos.add(pacVo);

							// this.atualizaEtiquetasImpressas(pacVo, isReprint,
							// dtReferencia, hashProntPacConsultaVo);

						} else {
							countEtiquetas++;

							if (countEtiquetas == 1) {
								vtznslInicial = vtznsl;
							}

							vtznslFinal = vtznsl;

							zpl.append(etiquetasON.gerarZpl(pacVo
									.getProntuario(), (short) 0, pacVo
									.getNome()));

							pacZplVos.add(pacVo);

							// this.atualizaEtiquetasImpressas(pacVo, isReprint,
							// dtReferencia, hashProntPacConsultaVo);
						}
					}
				}
			}
		}

		if (countEtiquetas > 0) {
			zpl.append(etiquetasON.gerarZpl(99999999, (short) 0,
					TURNO_ZONA_SALA + vtznsl));
		} else {
			throw new ApplicationBusinessException(
					MovimentacaoONExceptionCode.NAO_HA_DADOS_DATA);
		}

		// Retorno.
		ZplVo res = new ZplVo(zpl.toString(), vtznslInicial, vtznslFinal,
				pacZplVos);

		return res;
	}

	/**
	 * Método que atualiza tabelas auxiliares indicando a impressão de etiquetas
	 * para determinado paciente.
	 * 
	 * @param vtznsl
	 * @param pacVo
	 * @param isReprint
	 * @param dtReferencia
	 */
	public void atualizaEtiquetasImpressas(List<PacienteZplVo> pacZplVos,
			Boolean isReprint, Date dtReferencia,
			Map<Integer, PacConsultaVo> hashProntPacConsultaVo, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		ICadastroPacienteFacade cadastroPacienteFacade = this.getCadastroPacienteFacade();
		AipProntuariosImpressosDAO aipProntuariosImpressosDAO = this.getAipProntuariosImpressosDAO();
		
		for (PacienteZplVo pacVo : pacZplVos) {
			if (!isReprint) {
				// Atuliza volume
				AipPacientes paciente = pacienteFacade.obterPacientePorProntuario(pacVo.getProntuario());

				if (paciente != null) {
					paciente.setVolumes((short) 0);
					cadastroPacienteFacade.atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
				}

				// tab_consulta(r_pacientes.prontuario).ind_impresso := 'S';
				hashProntPacConsultaVo.get(pacVo.getProntuario())
						.setIndImpresso(true);

				AipProntuariosImpressosId aipProntImpId = new AipProntuariosImpressosId();
				aipProntImpId.setDtReferencia(dtReferencia);
				aipProntImpId.setPacCodigo(pacVo.getCodigo());

				AipProntuariosImpressos aipProntImp = new AipProntuariosImpressos();
				aipProntImp.setId(aipProntImpId);
				aipProntImp.setIndImpresso(true);
				
				aipProntuariosImpressosDAO.persistir(aipProntImp);
				aipProntuariosImpressosDAO.flush();
			}
		}
	}

	/**
	 * ORADB FUNCTION ver_consulta.
	 * 
	 * @param prontuario
	 * @param dtConsulta
	 * @return Boolean
	 */
	private Boolean verConsulta(PacConsultaVo vo, Date dtConsulta) {

		if ((vo != null) && vo.getDtConsulta().equals(dtConsulta)
				&& !vo.getIndImpresso()) {
			return true;
		}

		return false;
	}

	/**
	 * Método que obter um HASH com os prontuários ja impressos por data de
	 * referência.
	 * 
	 * @param dtReferencia
	 * @return
	 */
	public Map<Integer, AipProntuariosImpressos> obterProntImpressos(
			Date dtReferencia) {
		Map<Integer, AipProntuariosImpressos> hashProntImpressos = new HashMap<Integer, AipProntuariosImpressos>(
				0);

		Iterator<AipProntuariosImpressos> it = this.getAipProntuariosImpressosDAO().obterProntImpressos(dtReferencia).iterator();
		
		while (it.hasNext()) {
			AipProntuariosImpressos obj = it.next();
			hashProntImpressos.put(obj.getId().getPacCodigo(), obj);
		}

		return hashProntImpressos;
	}

	/**
	 * Método que consulta no HASH com os prontuários já impressos por data de
	 * referência.
	 * 
	 * @param codigo
	 * @param hashProntImpressos
	 * @return
	 */
	private Boolean verProntuarioImpresso(Integer codigo,
			Map<Integer, AipProntuariosImpressos> hashProntImpressos) {

		if (hashProntImpressos.get(codigo) != null) {
			return true;
		}

		return false;
	}

	/**
	 * Cursor: c_pacientes
	 * 
	 * @param dtReferencia
	 * @param isReprint
	 * @return
	 * @throws ParseException
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private List<PacienteZplVo> obterPacientes(Date dtReferencia,
			Boolean isReprint, Integer turnoDe, Integer turnoAte,
			String zonaDe, String zonaAte, Integer salaDe, Integer salaAte)
			throws ApplicationBusinessException {
		IParametroFacade parametroFacade = this.getParametroFacade();
		
		// Data from
		Calendar cal = new GregorianCalendar();
		cal.setTime(dtReferencia);
		Date dtFrom = new GregorianCalendar(cal.get(Calendar.YEAR), cal
				.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
				.getTime();

		// Data to
		cal = new GregorianCalendar();
		cal.setTime(dtReferencia);
		Date dtTo = new GregorianCalendar(cal.get(Calendar.YEAR), cal
				.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59,
				59).getTime();

		List<Object[]> res = this.getAmbulatorioFacade().obterPacientes(dtFrom, dtTo, isReprint, stringSeparator);

		// Criando lista de VO.
		List<PacienteZplVo> lista = new ArrayList<PacienteZplVo>(0);

		Iterator<Object[]> it = res.iterator();

		String iniTurno1Aux = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_INI_TURNO1).getVlrTexto();

		String fimTurno1Aux = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_FIM_TURNO1).getVlrTexto();

		String iniTurno2Aux = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_INI_TURNO2).getVlrTexto();

		String fimTurno2Aux = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_FIM_TURNO2).getVlrTexto();

		String iniTurno3Aux = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_INI_TURNO3).getVlrTexto();

		String fimTurno3Aux = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_FIM_TURNO3).getVlrTexto();

		while (it.hasNext()) {
			Object[] obj = it.next();

			PacienteZplVo vo = new PacienteZplVo();

			Integer turno = 0;

			if (obj[0] != null) {
				vo.setDtConsulta((Date) obj[0]);

				Calendar calAux = GregorianCalendar.getInstance();
				calAux.setTime(vo.getDtConsulta());

				cal = GregorianCalendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, calAux.get(Calendar.HOUR_OF_DAY));
				cal.set(Calendar.MINUTE, calAux.get(Calendar.MINUTE));
				cal.set(Calendar.SECOND, calAux.get(Calendar.SECOND));
				cal.set(Calendar.MILLISECOND, calAux.get(Calendar.MILLISECOND));

				turno = this.defineTurno(cal.getTime(), iniTurno1Aux,
						fimTurno1Aux, iniTurno2Aux, fimTurno2Aux, iniTurno3Aux,
						fimTurno3Aux);

				vo.setTurno(turno.toString());
			}

			if (obj[3] != null) {
				vo.setProntuario((Integer) obj[3]);
			}

			if (obj[4] != null) {
				vo.setNome((String) obj[4]);
			}

			if (obj[5] != null) {
				vo.setCodigo((Integer) obj[5]);
			}

			String sigla = null;
			String sala = null;

			if (obj[1] != null) {
				sigla = (String) obj[1];
				vo.setSigla(sigla);
				vo.setDesc(vo.getTurno() + "/" + sigla + "/");
			}

			if (obj[2] != null) {
				sala = StringUtils.leftPad(((Byte) obj[2]).toString(), 2, '0');
				vo.setSala(sala);
				vo.setDesc(vo.getDesc() + sala);
			}

			StringBuffer in = new StringBuffer(turno.toString())
				.append(StringUtils.rightPad(sigla, 10, ' '))
				.append(StringUtils.leftPad(sala, 2, '0'));

			String from = parseFrom(turnoDe, zonaDe, salaDe);

			String to = parseTo(turnoAte, zonaAte, salaAte);

			if (!isBetween(in.toString(), from, to)) {
				continue;
			}

			if (!existPac(vo.getProntuario(), lista)) {
				lista.add(vo);
			}
		}

		Collections.sort(lista, new PacienteComparator());

		return lista;
	}

	/**
	 * Verifica se já existe paciente para a data.
	 * 
	 * @param prontuario
	 * @param lista
	 * @return
	 */
	private Boolean existPac(Integer prontuario, List<PacienteZplVo> lista) {
		Iterator<PacienteZplVo> it = lista.iterator();
		while (it.hasNext()) {
			PacienteZplVo pac = (PacienteZplVo) it.next();
			if (pac.getProntuario().equals(prontuario)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Método que gera a String FROM.
	 * 
	 * @param turnoDe
	 * @param zonaDe
	 * @param salaDe
	 * @return
	 */
	private String parseFrom(Integer turnoDe, String zonaDe, Integer salaDe) {
		StringBuffer from = new StringBuffer();

		// NVL(:qms$ctrl.num_field1,'0')||RPAD(NVL(:qms$ctrl.char_field15,'
		// '),10,' ')||LPAD(NVL(:qms$ctrl.num_field2,0),2,'0')

		if (turnoDe != null) {
			from.append(turnoDe);
		} else {
			from.append('0');
		}

		if (zonaDe != null) {
			from.append(StringUtils.rightPad(zonaDe, 10, ' '));
		} else {
			from.append(StringUtils.rightPad("  ", 10, ' '));
		}

		if (salaDe != null) {
			from.append(StringUtils.leftPad(salaDe.toString(), 2, '0'));
		} else {
			from.append(StringUtils.leftPad("0", 2, '0'));
		}

		return from.toString();
	}

	/**
	 * Método que gera a String TO.
	 * 
	 * @param turnoAte
	 * @param zonaAte
	 * @param salaAte
	 * @return
	 */
	private String parseTo(Integer turnoAte, String zonaAte, Integer salaAte) {
		StringBuffer to = new StringBuffer();

		// NVL(:qms$ctrl.num_field3,9)||RPAD(NVL(:qms$ctrl.char_field20,'99'),10,'
		// ')||LPAD(NVL(:qms$ctrl.num_field4,99),2,'0')

		if (turnoAte != null) {
			to.append(turnoAte);
		} else {
			to.append('9');
		}

		if (zonaAte != null) {
			to.append(StringUtils.rightPad(zonaAte, 10, ' '));
		} else {
			to.append(StringUtils.rightPad("99", 10, ' '));
		}

		if (salaAte != null) {
			to.append(StringUtils.leftPad(salaAte.toString(), 2, '0'));
		} else {
			to.append(StringUtils.leftPad("99", 2, '0'));
		}

		return to.toString();
	}

	/**
	 * ORADB PACKAGE AACK_UTIL FUNCTION AACC_DEFINE_TURNO
	 * 
	 * @param date
	 * @return turno
	 * @throws ParseException
	 * @throws ApplicationBusinessException
	 */
	private Integer defineTurno(Date date, String iniTurno1Aux,
			String fimTurno1Aux, String iniTurno2Aux, String fimTurno2Aux,
			String iniTurno3Aux, String fimTurno3Aux)
			throws ApplicationBusinessException {

		Calendar today = GregorianCalendar.getInstance();

		DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		try {
			// Checa 1º Turno.
			Calendar iniTurno1 = GregorianCalendar.getInstance();
			iniTurno1.setTime((Date) formatter.parse(iniTurno1Aux));
			iniTurno1
					.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR));
			iniTurno1.set(Calendar.YEAR, today.get(Calendar.YEAR));

			Calendar fimTurno1 = GregorianCalendar.getInstance();
			fimTurno1.setTime((Date) formatter.parse(fimTurno1Aux));
			fimTurno1
					.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR));
			fimTurno1.set(Calendar.YEAR, today.get(Calendar.YEAR));

			if (this.isBetween(date, iniTurno1.getTime(), fimTurno1.getTime())) {
				return 1;
			}

			// Checa 2º Turno.
			Calendar iniTurno2 = GregorianCalendar.getInstance();
			iniTurno2.setTime((Date) formatter.parse(iniTurno2Aux));
			iniTurno2
					.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR));
			iniTurno2.set(Calendar.YEAR, today.get(Calendar.YEAR));

			Calendar fimTurno2 = GregorianCalendar.getInstance();
			fimTurno2.setTime((Date) formatter.parse(fimTurno2Aux));
			fimTurno2
					.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR));
			fimTurno2.set(Calendar.YEAR, today.get(Calendar.YEAR));

			if (this.isBetween(date, iniTurno2.getTime(), fimTurno2.getTime())) {
				return 2;
			}

			// Checa 3º Turno.
			Calendar iniTurno3 = GregorianCalendar.getInstance();
			iniTurno3.setTime((Date) formatter.parse(iniTurno3Aux));
			iniTurno3
					.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR));
			iniTurno3.set(Calendar.YEAR, today.get(Calendar.YEAR));

			Calendar fimTurno3 = GregorianCalendar.getInstance();
			fimTurno3.setTime((Date) formatter.parse(fimTurno3Aux));
			fimTurno3
					.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR));
			fimTurno3.set(Calendar.YEAR, today.get(Calendar.YEAR));

			if (this.isBetween(date, iniTurno3.getTime(), fimTurno3.getTime())) {
				return 3;
			}

		} catch (ParseException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					MovimentacaoONExceptionCode.ERRO_AO_GERAR_ETIQUETAS);
		}

		return 0;
	}

	/**
	 * Verifica se data está em intervalo;
	 * 
	 * @param in
	 * @param date1
	 * @param date2
	 * @return
	 */
	private boolean isBetween(Date in, Date date1, Date date2) {

		if (in.equals(date1)) {
			return true;
		}

		if (in.equals(date2)) {
			return true;
		}

		if (in.after(date1) && in.before(date2)) {
			return true;
		}

		return false;
	}

	/**
	 * Verifica se String está em intervalo;
	 * 
	 * @param in
	 * @param str1
	 * @param str2
	 * @return
	 */
	private boolean isBetween(String in, String from, String to) {

		Collator myCollator = Collator.getInstance();

		// FROM < IN < TO
		if ((myCollator.compare(from, in) < 0 && myCollator.compare(in, to) < 0)
				|| (myCollator.compare(in, from) == 0)
				|| (myCollator.compare(in, to) == 0)) {
			return true;
		}

		return false;
	}

	/**
	 * ORADB FUNCTION AIPC_VER_ENVIO_PRNT (P_UNF_SEQ NUMBER ,P_PAC_CODIGO IN
	 * NUMBER ,P_ESP_SEQ IN NUMBER ,P_GRD_SEQ IN NUMBER)
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public Boolean verEnvioPront(Short unfSeq, Integer pacCod, Short espSeq,
			Short grdSeq, Short vUnf17, Short vUnf19, Short vUnfCaps)
			throws ApplicationBusinessException {

		// TODO: Operação está custosa. Ponderar a troca para HASH?
		AipPacientes pac = this.verPaciente(pacCod);

		if (verificarProntuarioNulo(pac) && pac.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {
			return false;
		}

		if (grdSeq != null && grdSeq.intValue() == 2130) {
			return true;
		}

		// 139 -- Nefro
		// 41 -- Banco de sangue
		if ((unfSeq != null) && (unfSeq.intValue() == 139 || unfSeq.intValue() == 41)) {
			return false;
		}

		if ((checarIgualdade(vUnfCaps, unfSeq)) && espSeqIn(espSeq)) {
			return false;
		}

		Short vUnfQuimio = 36;
		Short vUnfRadio = 247;
		Short vUnf11 = 213;

		Short vCctCodigo = this.buscarEsp(espSeq);

		if ((checarIgualdade(vUnf17, unfSeq))
				|| (checarIgualdade(vUnf19, unfSeq))
				|| (checarIgualdade(vUnfCaps, unfSeq))
				|| vUnfQuimio.equals(unfSeq)
				|| vUnfRadio.equals(unfSeq)
				|| vUnf11.equals(unfSeq)
				|| vCctCodigo.equals((short) 21105)
				|| ((espSeq.equals((short) 96)) || (espSeq.equals((short) 465)))) {
			return true;
		}

		if (pac.getProntuario() == null) {
			return true;
		}

		Date dtAux1 = new GregorianCalendar(2006, 0, 1).getTime();

		Date dtIdent = DateUtils.truncate(pac.getDtIdentificacao(),
				Calendar.DAY_OF_MONTH);

		if (dtIdent.before(dtAux1) && pac.getProntuario() < VALOR_MAXIMO_PRONTUARIO) {
			Date dtAux2 = new GregorianCalendar(2007, 1, 27, 8, 30).getTime();

			Date sysdate = new GregorianCalendar().getTime();
			if (sysdate.before(dtAux2)) {
				return true;
			} else {
				boolean achouCons = this.consulta(pac);

				if (verMovimento(pac.getDtUltAlta())
						&& verMovimento(pac.getDtUltAltaHospDia())
						&& verMovimento(pac.getDtUltAtendHospDia())
						&& !achouCons && verMovimento(pac.getDtUltInternacao())
						&& verMovimento(pac.getDtUltProcedimento())) {
					return false;
				} else {
					return true;
				}

			}
		}

		return false;
	}

	private boolean verificarProntuarioNulo(AipPacientes pac) {
		return pac != null && pac.getProntuario() != null;
	}
	
	private boolean checarIgualdade(Short valor, Short valorComparar) {
		return (valor != null && valor.equals(valorComparar));
		
	}

	/**
	 * c_consulta(p_pac_codigo);
	 * 
	 * @param pacCodigo
	 * @return Short
	 */
	private Boolean verMovimento(Date date) {
		Calendar aux = new GregorianCalendar();
		Date dataCinco = new GregorianCalendar(aux.get(Calendar.YEAR) - 5, aux
				.get(Calendar.MONTH), aux.get(Calendar.DAY_OF_MONTH)).getTime();
		dataCinco = DateUtils.truncate(dataCinco, Calendar.DAY_OF_MONTH);

		if (date == null
				|| (date.before(dataCinco) || (date.equals(dataCinco)))) {
			return true;
		}

		return false;
	}

	/**
	 * Cursor: c_consulta(p_pac_codigo);
	 * 
	 * @param pacCodigo
	 * @return Short
	 */
	private Boolean consulta(AipPacientes pac) {

		// De
		Calendar today = new GregorianCalendar();
		Date dtFrom = new GregorianCalendar(today.get(Calendar.YEAR) - 5, today
				.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
				.getTime();

		// Até
		today = new GregorianCalendar();
		today.set(Calendar.DAY_OF_YEAR, today.get(Calendar.DAY_OF_YEAR) - 1);
		Date dtTo = today.getTime();

		for (AacConsultas consulta : pac.getAacConsultas()) {
			Date dtConsulta = consulta.getDtConsulta();
			if (dtFrom.before(dtConsulta) && dtTo.after(dtConsulta)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Método auxiliar para teste de elemento na lista.
	 * 
	 * @param espSeq
	 * @return Boolean
	 */
	private Boolean espSeqIn(Short espSeq) {
		Short lista[] = { 80, 85, 99, 156, 182, 267, 550, 554, 637, 641, 643,
				655, 674, 675, 676, 677, 678, 688, 692, 714, 715, 730, 731,
				732, 812, 894, 895, 896 };

		// Convert to list
		ArrayList<Short> list = new ArrayList<Short>(Arrays.asList(lista));

		Integer index = null;

		index = Collections.binarySearch(list, espSeq);

		if (index != null) {
			return true;
		}

		return false;
	}

	/**
	 * ORADB CURSOR c_ver_paciente
	 * 
	 * @param pacCodigo
	 * @return PacienteZplVo
	 */
	private AipPacientes verPaciente(Integer pacCodigo) {

		AipPacientes pac = null;

		if (pacCodigo != null) {
			pac = this.getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
		} else {
			return null;
		}

		if (pac != null) {

			if (pac.getDtIdentificacao() != null) {
				pac.setDtIdentificacao(DateUtils.truncate((Date) pac
						.getDtIdentificacao(), Calendar.DAY_OF_MONTH));
			}

			if (pac.getDtUltAlta() != null) {
				pac.setDtUltAlta(DateUtils.truncate((Date) pac.getDtUltAlta(),
						Calendar.DAY_OF_MONTH));
			}

			if (pac.getDtUltAltaHospDia() != null) {
				pac.setDtUltAltaHospDia(DateUtils.truncate((Date) pac
						.getDtUltAltaHospDia(), Calendar.DAY_OF_MONTH));
			}

			if (pac.getDtUltAtendHospDia() != null) {
				pac.setDtUltAtendHospDia(DateUtils.truncate((Date) pac
						.getDtUltAtendHospDia(), Calendar.DAY_OF_MONTH));
			}

			if (pac.getDtUltInternacao() != null) {
				pac.setDtUltInternacao(DateUtils.truncate((Date) pac
						.getDtUltInternacao(), Calendar.DAY_OF_MONTH));
			}

			if (pac.getDtUltProcedimento() != null) {
				pac.setDtUltProcedimento(DateUtils.truncate((Date) pac
						.getDtUltProcedimento(), Calendar.DAY_OF_MONTH));
			}

		}

		return pac;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected AipProntuariosImpressosDAO getAipProntuariosImpressosDAO() {
		return aipProntuariosImpressosDAO;
	}

	protected EtiquetasON getEtiquetasON() {
		return etiquetasON;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade; 
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade; 
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade; 
	}
	
	/**
	 * Cursor: c_busca_esp
	 * 
	 * @param espSeq
	 * @return Short
	 */
	private Short buscarEsp(Short espSeq) {
		if (espSeq == null) {
			return 0;
		}
		
		AghEspecialidades esp = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeq);

		FccCentroCustos fcc = esp.getCentroCusto();

		if (fcc != null) {
			return fcc.getCodigo().shortValue();
		}

		return 0;
	}
	
}

/**
 * Classe comparadora utilizada para ordenar a lista de
 * <code>PacienteZplVo</code>.
 * 
 * @author Ricardo Costa
 * 
 */
class PacienteComparator implements Comparator<PacienteZplVo> {

	// ORDER BY:
	// TO_CHAR(aack_util.aacc_define_turno(con.dt_consulta))||'/'||
	// vusl.sigla||'/'||
	// LPAD(vusl.sala,2,'0') t_zn_sl
	// ,con.dt_consulta
	// ,PAC.PRONTUARIO
	// ,PAC.NOME NOME
	// criteria.addOrder(Order.asc("pac.prontuario"));
	// criteria.addOrder(Order.asc("con.dtConsulta"));

	@Override
	public int compare(PacienteZplVo o1, PacienteZplVo o2) {

		Integer t1 = Integer.parseInt(((PacienteZplVo) o1).getTurno());
		Integer t2 = Integer.parseInt(((PacienteZplVo) o2).getTurno());

		if (t1 > t2) {
			return 1;
		} else if (t1 < t2) {
			return -1;
		} else {

			String sig1 = ((PacienteZplVo) o1).getSigla();
			String sig2 = ((PacienteZplVo) o2).getSigla();

			Integer sigla1 = 0;
			Integer sigla2 = 0;

			if (CoreUtil.isNumeroInteger(sig1)) {
				sigla1 = Integer.parseInt(sig1);
			}

			if (CoreUtil.isNumeroInteger(sig2)) {
				sigla2 = Integer.parseInt(sig2);
			}

			if (sigla1 > sigla2) {
				return 1;
			} else if (sigla1 < sigla2) {
				return -1;
			} else {
				Integer s1 = Integer.parseInt(((PacienteZplVo) o1).getSala());
				Integer s2 = Integer.parseInt(((PacienteZplVo) o2).getSala());

				if (s1 > s2) {
					return 1;
				} else if (s1 < s2) {
					return -1;
				} else {

					Date date1 = ((PacienteZplVo) o1).getDtConsulta();
					Date date2 = ((PacienteZplVo) o2).getDtConsulta();

					if (date1.after(date2)) {
						return 1;
					} else if (date1.before(date2)) {
						return -1;
					} else {

						Integer p1 = ((PacienteZplVo) o1).getProntuario();
						Integer p2 = ((PacienteZplVo) o2).getProntuario();

						if (p1 > p2) {
							return 1;
						} else if (p1 < p2) {
							return -1;
						} else {
							String n1 = ((PacienteZplVo) o1).getNome();
							String n2 = ((PacienteZplVo) o2).getNome();

							return n1.compareToIgnoreCase(n2);
						}
					}
				}
			}
		}
	}
	
}
