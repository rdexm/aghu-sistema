package br.gov.mec.aghu.exames.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.exames.dao.AelServidorCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelValorNormalidCampoDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosDataValorVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialDadosVO;
import br.gov.mec.aghu.exames.pesquisa.vo.FluxogramaLaborarorialVO;
import br.gov.mec.aghu.model.AelItemSolicConsultado;
import br.gov.mec.aghu.model.AelItemSolicConsultadoId;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.NPathComplexity"})
@Stateless
public class PesquisaFluxogramaON extends BaseBusiness {

	@EJB
	private AelItemSolicConsultadoRN aelItemSolicConsultadoRN;
	
	private static final Log LOG = LogFactory.getLog(PesquisaFluxogramaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelValorNormalidCampoDAO aelValorNormalidCampoDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelServidorCampoLaudoDAO aelServidorCampoLaudoDAO;

	public enum PesquisaFluxogramaONExceptionCode implements BusinessExceptionCode {
		AEL_01242, AEL_00901, AEL_00353;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7378495636321793970L;

	public FluxogramaLaborarorialVO pesquisarFluxograma(Short especialidade,
			Integer prontuario, boolean historicos) throws ApplicationBusinessException {
		return pesquisarFluxograma(especialidade, prontuario, true, historicos);
	}

	public FluxogramaLaborarorialVO pesquisarFluxograma(Short especialidade,
			Integer prontuario, boolean atualizarAelItemSolicConultas, boolean historicos)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			logError("Não encontrou o servidor logado!");
		}
		return pesquisarFluxograma(servidorLogado, especialidade, prontuario, atualizarAelItemSolicConultas,historicos);
	}
	
	/** Prepara os resitros para serem exibidos na tela de fluxograma
	 * ORADB AELP_SELECIONA_REGISTROS
	 * @param especialidade
	 * @param prontuario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "ucd" })
	public FluxogramaLaborarorialVO pesquisarFluxograma(
			RapServidores consultadoPor, Short especialidade, Integer prontuario, boolean historicos)
			throws ApplicationBusinessException {
		return pesquisarFluxograma(consultadoPor, especialidade, prontuario, true, historicos);
	}	
	
	/**
	 * Retorna dados para o fluxograma de exames.
	 * 
	 * @param consultadoPor
	 * @param especialidade
	 * @param prontuario
	 * @param registraConsulta
	 *            indica se deve ser registrada a consulta aos exames pelo
	 *            usuario.
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity"})
	public FluxogramaLaborarorialVO pesquisarFluxograma(
			RapServidores consultadoPor, Short especialidade,
			Integer prontuario, boolean registraConsulta, boolean historicos)
			throws ApplicationBusinessException {
		
		List<Date> datas = new ArrayList<Date>();
		List<FluxogramaLaborarorialDadosVO> resultCustom = new ArrayList<FluxogramaLaborarorialDadosVO>();
		SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);

		boolean existeFluxogramaPersonalisado = getAelServidorCampoLaudoDAO().verificaExitenciaFluxogramaPersonalizado(consultadoPor);

		/*Situações*/
		AghParametros pExecutando = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
		AghParametros pLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros pAreaExecutora = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros pDiasRegConsResu = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_REG_CONS_RESU);
		AghParametros pTempoMinutosFluxo = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TEMPO_MINUTOS_FLUXO);
		
		List<String> situacoesIn = new ArrayList<String>();
		situacoesIn.add(pExecutando.getVlrTexto());
		situacoesIn.add(pLiberado.getVlrTexto());
		List<FluxogramaLaborarorialDadosVO> resultGeral = new ArrayList<FluxogramaLaborarorialDadosVO>();
		Map<Integer, Map<Short, Map<String, Date>>> solicitacoes = new HashMap<Integer, Map<Short,Map<String,Date>>>();

		if(existeFluxogramaPersonalisado){
			resultGeral = getPacienteFacade().buscaFluxogramaPorServidorEPaciente(consultadoPor, prontuario, situacoesIn, historicos);
		}else if(especialidade != null){
			situacoesIn.add(pAreaExecutora.getVlrTexto());
			resultGeral = getPacienteFacade().buscaFluxogramaPorEspecialidadeEPaciente(especialidade, prontuario, situacoesIn, historicos);
		}else{
			situacoesIn.add(pAreaExecutora.getVlrTexto());
			resultGeral = getPacienteFacade().buscaFluxogramaPorPaciente(prontuario, situacoesIn, historicos);
		}

		if(resultGeral.size()==0){
			throw new ApplicationBusinessException(PesquisaFluxogramaONExceptionCode.AEL_00901);
		}

		String nomeSumarioAux = "";
		FluxogramaLaborarorialDadosVO fluxLabDadosVOAux = null;
		boolean existeNaLista = false;

		for (int i = 0; i < resultGeral.size(); i++) {
			FluxogramaLaborarorialDadosVO fluxLabDadosVO = resultGeral.get(i);

			if(!fluxLabDadosVO.getNomeSumario().equals(nomeSumarioAux)){

				if(i > 0 && !existeNaLista){
					resultCustom.add(fluxLabDadosVOAux);
				}

				fluxLabDadosVOAux = verificaExistenciaCollection(resultCustom, fluxLabDadosVO);

				if(fluxLabDadosVOAux==null){
					fluxLabDadosVOAux = new FluxogramaLaborarorialDadosVO();
					fluxLabDadosVOAux.setSeqp(fluxLabDadosVO.getSeqp());
					fluxLabDadosVOAux.setSoeSeq(fluxLabDadosVO.getSoeSeq());
					fluxLabDadosVOAux.setNomeSumario(fluxLabDadosVO.getNomeSumario());
					fluxLabDadosVOAux.setCalSeq(fluxLabDadosVO.getCalSeq());
					fluxLabDadosVOAux.setIndDividePorMil(fluxLabDadosVO.getIndDividePorMil());
					fluxLabDadosVOAux.setQuantidadeCasasDecimais(fluxLabDadosVO.getQuantidadeCasasDecimais());
					fluxLabDadosVOAux.setSexo(fluxLabDadosVO.getSexo());
					fluxLabDadosVOAux.setDatasValores(new HashMap<String, FluxogramaLaborarorialDadosDataValorVO>());
					fluxLabDadosVOAux.setIdade(fluxLabDadosVO.getIdade());
					fluxLabDadosVOAux.setOrdem(fluxLabDadosVO.getOrdem());
					fluxLabDadosVOAux.setNtcSeqp(fluxLabDadosVO.getNtcSeqp());
					fluxLabDadosVOAux.setDthrLiberada(fluxLabDadosVO.getDthrLiberada());
					existeNaLista = false;
				}else{
					existeNaLista = true;
				}

				nomeSumarioAux = fluxLabDadosVO.getNomeSumario(); 
			}

			Date dataExibicao = fluxLabDadosVO.getDthrEvento();
			if(fluxLabDadosVO.getDthrEvento() != null && datas.size() < 13){
				dataExibicao = verificaExistenciaData(datas, fluxLabDadosVO.getDthrEvento(), pTempoMinutosFluxo.getVlrNumerico().intValue());
			}

			//fluxLabDadosVOAux.getDatasValores().put(fluxLabDadosVO.getDthrLiberada(), getValorExameData(fluxLabDadosVO));
			//Deve sempre devolver o valor da primeira liberação do resultado
			if(fluxLabDadosVO.getDthrEvento() != null
					&& (fluxLabDadosVOAux.getDatasValores().isEmpty()
							|| (fluxLabDadosVO.getDthrLiberada() != null
							&& fluxLabDadosVOAux.getDthrLiberada() != null
							&& DateUtil.validaDataMenorIgual(fluxLabDadosVO.getDthrLiberada(), fluxLabDadosVOAux.getDthrLiberada())))) {
				fluxLabDadosVOAux.getDatasValores().put(sdf.format(dataExibicao), getValorExameData(fluxLabDadosVO));
			}

			//if(i+1 == resultGeral.size()){//PMD
			if(i+1 == resultGeral.size() && verificaExistenciaCollection(resultCustom, fluxLabDadosVOAux)==null){				
				resultCustom.add(fluxLabDadosVOAux);
			}
			//}//PMD
			populaHashSolicitacoesESituacoes(fluxLabDadosVO, solicitacoes);			
		}

		removeExamesSemRelevancia(resultCustom, datas, solicitacoes);

		Collections.sort(datas);
		Collections.sort(resultCustom, new Comparator<FluxogramaLaborarorialDadosVO>() {
			@Override
			public int compare(FluxogramaLaborarorialDadosVO item1, FluxogramaLaborarorialDadosVO item2) {
				return item1.getOrdem().compareTo(item2.getOrdem());
			}
		});

		if (registraConsulta) {
			efetuaAtuResuCons(solicitacoes, consultadoPor,
					pDiasRegConsResu.getVlrNumerico().intValue(),
					pLiberado.getVlrTexto());
		}

		FluxogramaLaborarorialVO fluxRetorno = new FluxogramaLaborarorialVO();
		fluxRetorno.setDatasExibicao(datas);
		fluxRetorno.setDadosFluxograma(resultCustom);

		return fluxRetorno;
	}

	/**
	 * Remove os exames que não serão exibidos nem impressos.
	 * @param resultCustom
	 * @param datas
	 */
	private void removeExamesSemRelevancia(List<FluxogramaLaborarorialDadosVO> resultCustom, List<Date> datas, Map<Integer, Map<Short, Map<String, Date>>> solicitacoes){
		SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
		List<FluxogramaLaborarorialDadosVO> resultCustomToRemove = new ArrayList<FluxogramaLaborarorialDadosVO>();

		for (int i = 0; i < resultCustom.size(); i++) {

			boolean excluir = true;

			FluxogramaLaborarorialDadosVO flux = resultCustom.get(i);

			for (int j = 0; j < datas.size(); j++) {
				if(flux.getDatasValores().containsKey(sdf.format(datas.get(j)))){
					excluir = false;
				}
			}

			if(excluir){
				resultCustomToRemove.add(resultCustom.get(i));
				solicitacoes.remove(resultCustom.get(i).getSoeSeq());
			}
		}
		resultCustom.removeAll(resultCustomToRemove);
	}

	private FluxogramaLaborarorialDadosDataValorVO getValorExameData(FluxogramaLaborarorialDadosVO fluxLabDadosVO){

		StringBuffer mascara = new StringBuffer("0"); 
		String retorno = null;

		if(fluxLabDadosVO.getQuantidadeCasasDecimais()>0){
			mascara.append('.');
			for (int i = 0; i < fluxLabDadosVO.getQuantidadeCasasDecimais(); i++) {
				mascara.append('#');
			}
			if(fluxLabDadosVO.getValor() != null){
				DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
				dfSymbols.setDecimalSeparator(',');
				retorno = new DecimalFormat(mascara.toString(), dfSymbols).format(fluxLabDadosVO.getValor() / Math.pow(new Double(10), fluxLabDadosVO.getQuantidadeCasasDecimais()));
			}
		}else{
			if(fluxLabDadosVO.getValor() != null){
				retorno = fluxLabDadosVO.getValor().toString();
			}
		}
		return new FluxogramaLaborarorialDadosDataValorVO(retorno, fluxLabDadosVO.getNtcSeqp()!=null, fluxLabDadosVO.getSoeSeq(), fluxLabDadosVO.getSeqp());
	}

	/**
	 * Faz o preparo dos registro que serão inseridos em ael_item_solic_consultados
	 * @param registro
	 * @param solicitacoes
	 */
	private void populaHashSolicitacoesESituacoes(FluxogramaLaborarorialDadosVO registro, Map<Integer, Map<Short, Map<String, Date>>> solicitacoes){
		if(solicitacoes.containsKey(registro.getSoeSeq())){
			
			Map<Short, Map<String, Date>> seqpHash = solicitacoes.get(registro.getSoeSeq());
			if(seqpHash.containsKey(registro.getSeqp())){
				
				Map<String, Date> situacaoHash = seqpHash.get(registro.getSeqp());
				if(!situacaoHash.containsKey(registro.getCodSituacao())){
					seqpHash.get(registro.getSeqp()).put(registro.getCodSituacao(), registro.getDthrEvento());
				}
			}else{
				Map<String, Date> situacaoHash = new HashMap<String, Date>();
				situacaoHash.put(registro.getCodSituacao(), registro.getDthrEvento());

				solicitacoes.get(registro.getSoeSeq()).put(registro.getSeqp(), situacaoHash);
			}
		}else{
			Map<String, Date> situacaoHash = new HashMap<String, Date>();
			situacaoHash.put(registro.getCodSituacao(), registro.getDthrEvento());

			Map<Short, Map<String, Date>> seqpHashAdd = new HashMap<Short, Map<String,Date>>();
			seqpHashAdd.put(registro.getSeqp(), situacaoHash);

			solicitacoes.put(registro.getSoeSeq(), seqpHashAdd);
		}
	}

	private void efetuaAtuResuCons(Map<Integer, Map<Short, Map<String, Date>>> solicitacoes, RapServidores consultadoPor, Integer pDiasRegConsResu, String pSitLiberado) throws ApplicationBusinessException{
		for (Map.Entry<Integer, Map<Short, Map<String, Date>>> entrySolicitacao : solicitacoes.entrySet()) {
			for (Map.Entry<Short, Map<String, Date>> entrySeqP : entrySolicitacao.getValue().entrySet()) {
				for (Map.Entry<String, Date> entrySituacao : entrySeqP.getValue().entrySet()) {
					atuResuCons(entrySolicitacao.getKey(), 
								entrySeqP.getKey(), 
								entrySituacao.getKey(), 
								consultadoPor, 
								pDiasRegConsResu, 
								pSitLiberado,
								entrySituacao.getValue());
				}
			}
		}
	}

	/** Atualiza registros na ael_item_solic_consultados
	 * ORADB FUNCTION AELC_ATU_RESU_CONS
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param sitCodigo
	 * @param consultadoPor
	 * @param pDiasRegConsResu
	 * @param pSitLiberado
	 * @param dthrLiberada
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean atuResuCons(Integer iseSoeSeq, Short iseSeqp, String sitCodigo, RapServidores consultadoPor, Integer pDiasRegConsResu, String pSitLiberado, Date dthrLiberada) throws ApplicationBusinessException{
		Date v_dthrLiberada = null;

		int tempoDia = 1000 * 60 * 60 * 24;

		if(dthrLiberada == null){
			v_dthrLiberada = new Date(new Date().getTime()-pDiasRegConsResu);
			v_dthrLiberada = new Date(v_dthrLiberada.getTime() - tempoDia);
		}else{
			v_dthrLiberada = dthrLiberada;
		}

		if(DateUtil.validaDataMaiorIgual(new Date(new Date().getTime()-pDiasRegConsResu), v_dthrLiberada)){
			return false;
		}

		if(!sitCodigo.equalsIgnoreCase(pSitLiberado)){
			return false;
		}

		if (consultadoPor == null) {
			throw new ApplicationBusinessException(PesquisaFluxogramaONExceptionCode.AEL_00353);
		}

		AelItemSolicConsultado item = new AelItemSolicConsultado();

		AelItemSolicConsultadoId itemId = new AelItemSolicConsultadoId();
		itemId.setIseSoeSeq(iseSoeSeq);
		itemId.setIseSeqp(iseSeqp);
		itemId.setSerMatricula(consultadoPor.getId().getMatricula());
		itemId.setSerVinCodigo(consultadoPor.getId().getVinCodigo());
		itemId.setCriadoEm(new Date());
		item.setId(itemId);
		getAelItemSolicConsultadoRN().inserir(item, Boolean.TRUE);

		return true;
	} 

	private Date verificaExistenciaData(List<Date> datas, Date dthrEvento, int p_tempo_minutos_solic){
		SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);

		boolean existe = false;
		Date dataExibicao = dthrEvento;
		
		for (int i = 0; i < datas.size(); i++) {
			Date data = datas.get(i);

			if(sdf.format(data).equals(sdf.format(dthrEvento)) || isDataEntreTempoMinutosSolic(dthrEvento, data, p_tempo_minutos_solic)){
				existe = true;
				dataExibicao = data;
			}
		}
		
		if(!existe){
			datas.add(dthrEvento);
		}
		
		return dataExibicao;
	}
	
	private boolean isDataEntreTempoMinutosSolic(Date dataEvento,Date dataEntre, int p_tempo_minutos_solic){
		Calendar dtEventoInicio = DateUtil.getCalendarBy(dataEvento);
		Calendar dtEventoFinal = DateUtil.getCalendarBy(dataEvento);
		Calendar dtEntre = DateUtil.getCalendarBy(dataEntre);

		//dtEventoInicio.set(Calendar.SECOND, 0);
		//dtEventoFinal.set(Calendar.SECOND, 0);
		//dtEntre.set(Calendar.SECOND, 0);
		
		dtEventoInicio.add(Calendar.MINUTE, p_tempo_minutos_solic * (-1));
		dtEventoFinal.add(Calendar.MINUTE, p_tempo_minutos_solic);

		return DateUtil.entre(dtEntre.getTime(), dtEventoInicio.getTime(), dtEventoFinal.getTime());
		
	}

	private FluxogramaLaborarorialDadosVO verificaExistenciaCollection(List<FluxogramaLaborarorialDadosVO> resultCustom, FluxogramaLaborarorialDadosVO fluxLabDadosVO){
		FluxogramaLaborarorialDadosVO voReturn = null;
		for (int i = 0; i < resultCustom.size(); i++) {
			FluxogramaLaborarorialDadosVO fluxVO = resultCustom.get(i); 

			if(fluxVO.getNomeSumario().equals(fluxLabDadosVO.getNomeSumario()) && fluxVO.getCalSeq().intValue()==fluxLabDadosVO.getCalSeq().intValue()){
				voReturn = fluxVO;
			}
		}
		return voReturn;
	}
	
	public String verificaNormalidade(FluxogramaLaborarorialDadosVO dadoVO, Date dataEvento) throws ParseException{
		String pCor = null;
		Double resultado = new Double(((FluxogramaLaborarorialDadosDataValorVO)dadoVO.getDatasValores().get(new SimpleDateFormat(DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO).format(dataEvento))).getDataValor().replace(",", "."));

		AelValorNormalidCampo normalidade = getAelValorNormalidCampoDAO().buscaNormalidadeCampoLaudo(dadoVO.getCalSeq(), dataEvento, dadoVO.getIdade(), DominioSexo.valueOf(dadoVO.getSexo()));
		if(normalidade != null && StringUtils.isNotEmpty(normalidade.getValorMinimoAceitavel()) && StringUtils.isNotEmpty(normalidade.getValorMaximoAceitavel())){
			//Se o resultado estiver entre o mínimo e o máximo, não é necessário pintar.
			if (StringUtils.isNotEmpty(normalidade.getValorMinimo()) && StringUtils.isNotEmpty(normalidade.getValorMaximo())){
				Double v_valor_minimo = (new Double(normalidade.getValorMinimo()) / Math.pow(new Double(10), 
						(normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
				Double v_valor_maximo = (new Double(normalidade.getValorMaximo()) / Math.pow(new Double(10), 
						(normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
				if(resultado >= v_valor_minimo && resultado <= v_valor_maximo){
					return null; 
				} 
			}
			Double v_valor_minimo_aceitavel = (new Double(normalidade.getValorMinimoAceitavel()) / Math.pow(new Double(10), 
					(normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
			Double v_valor_maximo_aceitavel = (new Double(normalidade.getValorMaximoAceitavel()) / Math.pow(new Double(10), 
					(normalidade.getQtdeCasasDecimais()!=null)?normalidade.getQtdeCasasDecimais():0));
			if(resultado >= v_valor_minimo_aceitavel && resultado <= v_valor_maximo_aceitavel){
				return "#FFFFB3"; //"AMARELO";
			}else if(resultado < v_valor_minimo_aceitavel || resultado > v_valor_maximo_aceitavel){
				return "#FF1A1A"; //"VERMELHO";
			}
		}
		return pCor;
	}

	protected AelValorNormalidCampoDAO getAelValorNormalidCampoDAO(){
		return aelValorNormalidCampoDAO;
	}

	protected AelServidorCampoLaudoDAO getAelServidorCampoLaudoDAO(){
		return aelServidorCampoLaudoDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected AelItemSolicConsultadoRN getAelItemSolicConsultadoRN(){
		return aelItemSolicConsultadoRN;
	}

	private IParametroFacade getParametroFacade() {
		//return (ParametroFacade)parametroFacade;
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}