package br.gov.mec.aghu.ambulatorio.business;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPeriodoReferenciaDAO;
import br.gov.mec.aghu.ambulatorio.vo.GerarDiariasProntuariosSamisVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTipoEnvioProntuario;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPeriodoReferencia;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class GerarDiariasProntuariosSamisON extends BaseBusiness {

    private static final String END_TAG = "> \n";

    @EJB
    private AmbulatorioConsultaRN ambulatorioConsultaRN;

    private static final Log LOG = LogFactory.getLog(GerarDiariasProntuariosSamisON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private AacPeriodoReferenciaDAO aacPeriodoReferenciaDAO;

    @Inject
    private AacConsultasDAO aacConsultasDAO;

    @EJB
    private IInternacaoFacade internacaoFacade;

    @EJB
    private IPacienteFacade pacienteFacade;

    @EJB
    private IParametroFacade parametroFacade;

    @EJB
    private IAghuFacade aghuFacade;
    
    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @EJB
	private IServidorLogadoFacade servidorLogadoFacade;

    /**
	 * 
	 */
    private static final long serialVersionUID = 2973531025480320879L;

    private enum GerarDiariasProntuariosSamisONExceptionCode implements BusinessExceptionCode {
	DATA_DIARIA_FERIADO, DATA_DIARIA_INVALIDA_DT_REFERENCIA, DATA_DIARIA_SABADO, DATA_DIARIA_DOMINGO
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public List<GerarDiariasProntuariosSamisVO> pesquisarMapaDesarquivamento(Date dataDiaria) throws ApplicationBusinessException {
	// #32738
	AghParametros parametroDtRef = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DT_REFERENCIA);
	dataDiaria = parametroDtRef.getVlrData();

	/*
	 * AghParametros parametroDtRefAnt =
	 * getParametroFacade().buscarAghParametro
	 * (AghuParametrosEnum.P_DT_REFER_ANTERIOR); Date dtReferenciaAN =
	 * parametroDtRefAnt.getVlrData();
	 * 
	 * Calendar dataVer = Calendar.getInstance();
	 * dataVer.setTime(dataDiaria);
	 * 
	 * // Seta a data para + 2 dias. dataVer.add(Calendar.DATE, +2);
	 * dtReferenciaAN = dataVer.getTime();
	 */

	List<AacConsultas> consultas = getAacConsultasDAO().pesquisarMapaDesarquivamento(dataDiaria);
	List<GerarDiariasProntuariosSamisVO> listaVO = new ArrayList<GerarDiariasProntuariosSamisVO>();

	Short vUnfCaps = (Short) getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_CAPS).getVlrNumerico().shortValue();

	Short vUnf17 = (Short) getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_ZONA17).getVlrNumerico().shortValue();

	Short vUnf19 = (Short) getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_ZONA19).getVlrNumerico().shortValue();

	AghParametros pIniTurno1 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_INI_TURNO1);
	AghParametros pFimTurno1 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FIM_TURNO1);
	AghParametros pIniTurno2 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_INI_TURNO2);
	AghParametros pFimTurno2 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FIM_TURNO2);
	AghParametros pIniTurno3 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_INI_TURNO3);
	AghParametros pFimTurno3 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FIM_TURNO3);

	String hospitalLocal = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU).getVlrTexto();

	for (AacConsultas con : consultas) {
	    GerarDiariasProntuariosSamisVO vo = new GerarDiariasProntuariosSamisVO();

	    // Solução temporária para as regras do HCPA em relação as diárias
	    // do SAMIS
	    // Permite outros HU's a exibição do relatório.
	    if (hospitalLocal.equalsIgnoreCase("HCPA")) {
		if (!getPacienteFacade().verEnvioPront(
			con.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional().getSeq(),
			con.getPaciente().getCodigo(), con.getGradeAgendamenConsulta().getEspecialidade().getSeq(),
			con.getGradeAgendamenConsulta().getSeq().shortValue(), vUnf17, vUnf19, vUnfCaps)) {
		    continue;
		}
	    }

	    // aacc_busca_int_etiq(SUBSTR('00000'||PAC.PRONTUARIO,
	    // LENGTH(PAC.PRONTUARIO)+3,2)) ARQuivista,
	    if (con.getPaciente().getProntuario() != null) {
		// Solução para quando o prontuário tiver 2 dígitos não ocorrer
		// String Index Out Of Range
		String prontuarioAux = "00000" + con.getPaciente().getProntuario().toString();
		String parte = prontuarioAux.substring(prontuarioAux.length() - 3, prontuarioAux.length() - 1);
		Integer arq = buscaIntEtiq(Integer.valueOf(parte));
		if (arq != null) {
		    vo.setArquivista(arq.toString());
		}
	    }

	    /**
	     * SUBSTR(TO_CHAR(PAC.PRONTUARIO,'FM00000009'),1,1) || ' ' ||
	     * SUBSTR(TO_CHAR(PAC.PRONTUARIO,'FM00000009'),2,2) || ' ' ||
	     * SUBSTR(TO_CHAR(PAC.PRONTUARIO,'FM00000009'),4,2) || ' ' ||
	     * SUBSTR(TO_CHAR(PAC.PRONTUARIO,'FM00000009'),6,2) || ' ' ||
	     * SUBSTR(TO_CHAR(PAC.PRONTUARIO,'FM00000009'),8,1)
	     */
	    if (con.getPaciente().getProntuario() != null) {
		StringBuffer prontuarioAux = new StringBuffer(con.getPaciente().getProntuario().toString());
		Integer dif = 8 - prontuarioAux.length();
		if (dif != 0) {
		    for (int a = 0; a < dif; a++) {
			prontuarioAux.insert(0, "0");
		    }
		}
		String prontuario = prontuarioAux.substring(0, 1) + " " + prontuarioAux.substring(1, 3) + " "
			+ prontuarioAux.substring(3, 5) + " " + prontuarioAux.substring(5, 7) + " " + prontuarioAux.substring(7, 8);
		vo.setProntuario(prontuario);
		vo.setProntuario1(prontuarioAux.substring(5, 7));
	    }
	    vo.setNome(con.getPaciente().getNome());

	    // aacc_busca_turno(con.dt_consulta) DT_NASCIMENTO
	    DominioTurno turno = getAmbulatorioConsultaRN().obterTurno(con.getDtConsulta(), pIniTurno1, pFimTurno1, pIniTurno2, pFimTurno2,
		    pIniTurno3, pFimTurno3);
	    vo.setDtNascimento(Integer.toString(turno.getCodigo() + 1));

	    // decode(VUSL.SALA,null,vuSL.SIGLA,VUSL.sigla||lpad(to_char(vusl.sala),2,'0'))
	    // SIGLA
	    if (con.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala() == null) {
		vo.setSigla(con.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional().getSigla());
	    } else {
		String sala = con.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala().toString();
		vo.setSigla(con.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional().getSigla()
			+ (sala.length() == 1 ? "0" + sala : sala));
	    }

	    vo.setSala(con.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala().toString());
	    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
	    vo.setDtConsulta(sdf2.format(con.getDtConsulta()));

	    // ,decode(int.seq,null,decode(aipc_busca_ult_mov(con.pac_Codigo),
	    // null,null,'*'),'I') obs
	    // query ** and int.pac_codigo(+) = con.pac_Codigo --
	    // and int.ind_paciente_internado(+) = 'S'
	    AinInternacao internacao = getInternacaoFacade().obrterInternacaoPorPacienteInternado(con.getPaciente().getCodigo());
	    if (internacao == null) {
		String local = buscaUltMov(con.getPaciente().getCodigo());
		if (local != null && !"".equals("local")) {
		    vo.setObs("*");
		}
	    } else {
		vo.setObs("I");
	    }

	    listaVO.add(vo);
	}
	Collections.sort(listaVO);
	// converteParaXml(listaVO);
	return listaVO;
    }

    public void getConsultasDiariaParaMovimentar(Date dataDiaria, Boolean exibeMsgProntuarioJaMovimentado) throws ApplicationBusinessException {

		AghParametros parametroDtRef = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DT_REFERENCIA);
	    dataDiaria = parametroDtRef.getVlrData();
		List<AacConsultas> consultas = getAacConsultasDAO().pesquisarMapaDesarquivamento(dataDiaria);

		for(AacConsultas consulta : consultas){
			movimentarProntuariosParaDesarquivamento(consulta, exibeMsgProntuarioJaMovimentado);
		}
	}

	public void movimentarProntuariosParaDesarquivamento(AacConsultas conNumero, Boolean exibeMsgProntuarioJaMovimentado) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.getAmbulatorioFacade().gerarMovimentacaoProntuario(conNumero, servidorLogado,exibeMsgProntuarioJaMovimentado);
	}

    
    public void converteParaXml(List<? extends Object> source) {

	try {
	    StringBuffer xml = new StringBuffer("\n <?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <aghu> \n");
	    xml.append(montarXml(source))
	    .append("</aghu>");
	    logInfo(xml.toString());
	} catch (IllegalArgumentException e) {
	    logError(e);
	} catch (IllegalAccessException e) {
	    logError(e);
	}

    }

    private String montarXml(List<? extends Object> source) throws IllegalAccessException {
	StringBuffer xml = new StringBuffer();
	if (source != null && !source.isEmpty()) {
	    Field[] fields = source.get(0).getClass().getDeclaredFields();
	    for (Object vo : source) {
		xml.append("    <").append(vo.getClass().getSimpleName()).append(END_TAG);
		for (Field field : fields) {
		    field.setAccessible(true);
		    String nomeField = field.getName();

		    Class<?> typeField = field.getType();
		    if (typeField.getName().contains("List")) {
			List<Object> list = (List<Object>) field.get(vo);

			xml.append("        <").append(nomeField).append(END_TAG);
			xml.append(montarXml(list));
			xml.append("\n        </").append(nomeField).append(END_TAG);

		    } else {
			Object valor = field.get(vo);
			xml.append("        <").append(nomeField).append('>');
			xml.append(valor);
			xml.append("</").append(nomeField).append(END_TAG);
		    }

		}
		xml.append("    </").append(vo.getClass().getSimpleName()).append(END_TAG);
	    }
	}
	return xml.toString();
    }

    /**
     * ORADB AIPC_BUSCA_ULT_MOV
     * 
     * @param pacCodigo
     * @return
     */
    public String buscaUltMov(Integer pacCodigo) {
	AipMovimentacaoProntuarios movimento = getPacienteFacade().obterMovimentacaoPorPacienteSituacaoTipoEnvioDtMovimento(pacCodigo,
		DominioSituacaoMovimentoProntuario.R, DominioTipoEnvioProntuario.P, new Date());

	return movimento == null ? null : movimento.getLocal();
    }

    /**
     * ORADB AACC_BUSCA_INT_ETIQ
     * 
     * @return
     */
    public Integer buscaIntEtiq(Integer sessao) {
	// IF p_sessao BETWEEN 00 AND 24 THEN---alterado em 14/06/2011
	// v_intervalo := 1;
	// ELSIF p_sessao BETWEEN 25 AND 49 THEN
	// v_intervalo := 2;
	// ELSIF p_sessao BETWEEN 50 AND 74 THEN
	// v_intervalo := 3;
	// ELSIF p_sessao BETWEEN 75 AND 99 THEN
	// v_intervalo := 4;
	// END IF;

	if (sessao >= 0 && sessao <= 24) {
	    return 1;
	} else if (sessao >= 25 && sessao <= 49) {
	    return 2;
	} else if (sessao >= 50 && sessao <= 74) {
	    return 3;
	} else if (sessao >= 75 && sessao <= 99) {
	    return 4;
	}
	return 0;
    }

    public void inicioDiaria(Date dataDiaria) throws ApplicationBusinessException {
	// #32738
	AghParametros parametroDiariaSabado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_SABADO);
	AghParametros parametroDiariaDomingo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_DOMINGO);
	AghParametros parametroDiariaFeriado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_FERIADO);

	AghFeriados feriado = getAghuFacade().obterFeriado(dataDiaria);
	if (feriado != null && parametroDiariaFeriado.getVlrTexto().equalsIgnoreCase("N")) {
	    throw new ApplicationBusinessException(GerarDiariasProntuariosSamisONExceptionCode.DATA_DIARIA_FERIADO);
	}

	if (dataDiaria.getDay() == 6 && parametroDiariaSabado.getVlrTexto().equalsIgnoreCase("N")) {
	    throw new ApplicationBusinessException(GerarDiariasProntuariosSamisONExceptionCode.DATA_DIARIA_SABADO);
	}

	if (dataDiaria.getDay() == 0 && parametroDiariaDomingo.getVlrTexto().equalsIgnoreCase("N")) {
	    throw new ApplicationBusinessException(GerarDiariasProntuariosSamisONExceptionCode.DATA_DIARIA_DOMINGO);
	}

	aacpInicioDiaria(dataDiaria);
    }

    /**
     * ORADB AACP_INICIO_DIARIA
     * 
     * @param dataDiaria
     * 
     */
    private void aacpInicioDiaria(Date dataDiaria) throws ApplicationBusinessException, ApplicationBusinessException {
	AghParametros parametroDtRefAnt = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DT_REFER_ANTERIOR);
	List<AacPeriodoReferencia> aacPeriodoRef = getAacPeriodoReferenciaDAO().pesquisarPeriodoReferencia();
	Date dtPerRef = aacPeriodoRef.get(0).getDtReferencia();

	// #32738
	/**
	 * Nova regra: a dt informada deve ser no maximo, dois dias uteis após a
	 * dt de referencia anterior. A data de referência é {1}. Informar uma
	 * data até dois dias úteis após a data de referência;
	 */
	/*
	 * SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy"); Date
	 * dataLimite = calcDtRefer(dtPerRef,2); dataLimite =
	 * DateUtil.adicionaDias(dataLimite, 1); if(
	 * !(dataDiaria.after(dtPerRef) && dataDiaria.before(dataLimite)) ){
	 * throw new AGHUNegocioExceptionSemRollback(
	 * GerarDiariasProntuariosSamisONExceptionCode
	 * .DATA_DIARIA_INVALIDA_DT_REFERENCIA,sdf1.format(dtPerRef)); }
	 */

	parametroDtRefAnt.setVlrData(dtPerRef);
	getParametroFacade().setAghpParametro(parametroDtRefAnt);

	AghParametros parametroNroDiasRef = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_REFERENCIA);
	Date dtRef = calcDtRefer(dataDiaria, parametroNroDiasRef.getVlrNumerico().intValue());

	dtRef = calculaProximoDiaValido(dtRef);

	AghParametros parametroDtReferencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DT_REFERENCIA);
	parametroDtReferencia.setVlrData(dtRef);
	getParametroFacade().setAghpParametro(parametroDtReferencia);
    }

    /**
     * ORADB AACC_CALC_DT_REFER (modificado #32738)
     * 
     * @return
     * @throws AGHUNegocioException
     */
    private Date calcDtRefer(Date dataSolic, Integer nroDiasRef) throws ApplicationBusinessException {
	AghParametros parametroDiariaSabado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_SABADO);
	AghParametros parametroDiariaDomingo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_DOMINGO);
	AghParametros parametroDiariaFeriado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_FERIADO);

	Date dataCalculada = dataSolic;
	Date dtReferencia = DateUtil.adicionaDias(dataSolic, nroDiasRef);
	Integer qntFinalSemana = 0;
	Integer qntFeriado = 0;
	while (dataCalculada.compareTo(dtReferencia) <= 0) {
	    if (dataCalculada.getDay() == 6 && parametroDiariaSabado.getVlrTexto().equalsIgnoreCase("N")) {
		qntFinalSemana++;
	    }
	    if (dataCalculada.getDay() == 0 && parametroDiariaDomingo.getVlrTexto().equalsIgnoreCase("N")) {
		qntFinalSemana++;
	    }
	    dataCalculada = DateUtil.adicionaDias(dataCalculada, 1);
	}
	if (parametroDiariaFeriado.getVlrTexto().equalsIgnoreCase("N")) {
	    if (parametroDiariaFeriado.getVlrTexto().equalsIgnoreCase("N")) {
		qntFeriado = getAghuFacade().obterQtdFeriadosEntreDatasSemFindeSemTurno(dataSolic, dtReferencia).intValue();
	    }
	}

	dtReferencia = DateUtil.adicionaDias(dtReferencia, qntFinalSemana + qntFeriado);

	return dtReferencia;
    }

    public Date calculaProximoDiaValido(Date dtReferencia) throws ApplicationBusinessException {
	AghParametros parametroDiariaSabado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_SABADO);
	AghParametros parametroDiariaDomingo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_DOMINGO);
	AghParametros parametroDiariaFeriado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.AMB_DIARIA_FERIADO);

	Boolean controle = true;

	while (controle) {
	    AghFeriados feriado = getAghuFacade().obterFeriado(dtReferencia);

	    if (feriado != null && parametroDiariaFeriado.getVlrTexto().equalsIgnoreCase("N")) {
		dtReferencia = DateUtil.adicionaDias(dtReferencia, 1);
		continue;
	    }
	    if (dtReferencia.getDay() == 6 && parametroDiariaSabado.getVlrTexto().equalsIgnoreCase("N")) {
		dtReferencia = DateUtil.adicionaDias(dtReferencia, 1);
		continue;
	    }
	    if (dtReferencia.getDay() == 0 && parametroDiariaDomingo.getVlrTexto().equalsIgnoreCase("N")) {
		dtReferencia = DateUtil.adicionaDias(dtReferencia, 1);
		continue;
	    }
	    controle = false;
	}

	return dtReferencia;
    }

    public void fimDiaria(Date dataDiaria) throws ApplicationBusinessException {
	aacpFinalDiaria(dataDiaria);
    }

    /**
     * ORADB AACP_FINAL_DIARIA
     * 
     * @param dataDiaria
     * 
     */
    private void aacpFinalDiaria(Date dataDiaria) throws ApplicationBusinessException {
	AghParametros parametroDtRef = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DT_REFERENCIA);
	Date dtReferencia = parametroDtRef.getVlrData();

	AghParametros parametroDtAtualiza = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DT_ATUALIZACAO);
	Date dtAtualiza = parametroDtAtualiza.getVlrData();

	AacPeriodoReferencia ref = (getAacPeriodoReferenciaDAO().pesquisarPeriodoReferencia()).get(0);
	getAacPeriodoReferenciaDAO().remover(ref);
	getAacPeriodoReferenciaDAO().flush();
	ref.setDtReferencia(dtReferencia);
	ref.setDtAtualizacao(dtAtualiza);
	ref.setCriadoEm(new Date());

	getAacPeriodoReferenciaDAO().persistir(ref);
	getAacPeriodoReferenciaDAO().flush();
    }

    protected IParametroFacade getParametroFacade() {
    	return (IParametroFacade) parametroFacade;
    }

    protected IAghuFacade getAghuFacade() {
    	return (IAghuFacade) aghuFacade;
    }
    
    
    protected IInternacaoFacade getInternacaoFacade() {
    	return (IInternacaoFacade) internacaoFacade;
    }

    protected AacConsultasDAO getAacConsultasDAO() {
    	return aacConsultasDAO;
    }

    protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
    	return ambulatorioConsultaRN;
    }
    
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return (IAmbulatorioFacade) ambulatorioFacade;
	}

    protected IPacienteFacade getPacienteFacade() {
    	return (IPacienteFacade) pacienteFacade;
    }

    protected AacPeriodoReferenciaDAO getAacPeriodoReferenciaDAO() {
	return aacPeriodoReferenciaDAO;
    }

}
