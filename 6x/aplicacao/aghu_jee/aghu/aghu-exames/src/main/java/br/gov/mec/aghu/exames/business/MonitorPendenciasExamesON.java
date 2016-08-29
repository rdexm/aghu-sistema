package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaTempo;
import br.gov.mec.aghu.exames.dao.AelAmostraItemExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesFiltrosPesquisaVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesVO;
import br.gov.mec.aghu.exames.vo.ResultadoMonitorPendenciasExamesVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;

/**
 * #5507 Monitor de Pendências de Exames
 * 
 * @author aghu
 * 
 */
@Stateless
public class MonitorPendenciasExamesON extends BaseBusiness {


@EJB
private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;

private static final Log LOG = LogFactory.getLog(MonitorPendenciasExamesON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAmostraItemExamesDAO aelAmostraItemExamesDAO;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IExamesLaudosFacade examesLaudosFacade;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
protected IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7049232099702643507L;
	private static final String SIGLA_U_DIV = "U:DIV";

	public enum MonitorPendenciasExamesONExceptionCode implements BusinessExceptionCode {
		AEL_01903, AEL_01904, AEL_01905, MENSAGEM_MONITOR_PENDENCIAS_EXAMES_ERRO_AREA_EXECUTORA_ABA;
	}

	/**
	 * ORADB PROCEDURE P_VERIFICA_FILTRO Verifica filtros da pesquisa
	 * 
	 * @param filtrosPesquisa
	 * @throws BaseException
	 */
	public void verificarFiltrosPesquisa(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) throws BaseException {

		// Tratamento do AGHU para as validação das variáveis globais da unidade executora e view/aba selecionada
		if (filtrosPesquisa.getUnidadeFuncionalExames() == null || filtrosPesquisa.getViewMonitorPendenciasExames() == null) {
			throw new ApplicationBusinessException(MonitorPendenciasExamesONExceptionCode.MENSAGEM_MONITOR_PENDENCIAS_EXAMES_ERRO_AREA_EXECUTORA_ABA);
		}

		// Ambas as datas devem ser informadas
		if ((filtrosPesquisa.getDataReferenciaIni() != null && filtrosPesquisa.getDataReferenciaFim() == null)
				|| (filtrosPesquisa.getDataReferenciaIni() == null && filtrosPesquisa.getDataReferenciaFim() != null)) {
			throw new ApplicationBusinessException(MonitorPendenciasExamesONExceptionCode.AEL_01903);
		}

		// Se for informado algum filtro de número único todos os demais relacionados devem ser informados
		if ((filtrosPesquisa.getNumeroUnicoIni() != null || filtrosPesquisa.getNumeroUnicoFim() != null || filtrosPesquisa.getDataDia() != null)
				&& (filtrosPesquisa.getNumeroUnicoIni() == null || filtrosPesquisa.getNumeroUnicoFim() == null || filtrosPesquisa.getDataDia() == null)) {
			throw new ApplicationBusinessException(MonitorPendenciasExamesONExceptionCode.AEL_01905);
		}

		// Verifica se a data final é menor que a data inicial
		if (DateValidator.validaDataMenor(filtrosPesquisa.getDataReferenciaFim(), filtrosPesquisa.getDataReferenciaIni())) {
			throw new ApplicationBusinessException(MonitorPendenciasExamesONExceptionCode.AEL_01904);
		}

	}

	/**
	 * Pesquisa pendências de exames para o Monitor de Pendências de Exames
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param filtrosPesquisa
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws BaseException
	 */
	public List<MonitorPendenciasExamesVO> pesquisarMonitorPendenciasExames(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) throws ApplicationBusinessException {

		/*
		 * A verificação dos filtros foi antecipada na controler e ao método P_SETA_DEFAULT_WHERE por questões de performance
		 */

		// Pesquisa pendências de exames para o Monitor de Pendências de Exames
		AghParametros maxResultParam =  parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_MAX_RESULTADOS_MONITOR_PENDENCIAS_EXAMES);
		Integer maxResult = 400; // Valor Default
		if(maxResultParam != null && maxResultParam.getVlrNumerico() != null) {
			maxResult = maxResultParam.getVlrNumerico().intValue();
		}
		
		String situacaoItemSolicitacaoCodigo = obtemSituacaoItemSolicitacao(filtrosPesquisa);
		List<ResultadoMonitorPendenciasExamesVO> listaPesquisaMonitorPendencias = 
				this.getAelAmostraItemExamesDAO().pesquisarMonitorPendenciasExames(filtrosPesquisa,
						situacaoItemSolicitacaoCodigo, maxResult);

		// Instancia a lista com o resultado do método
		List<MonitorPendenciasExamesVO> resultado = new ArrayList<MonitorPendenciasExamesVO>();

		for (ResultadoMonitorPendenciasExamesVO amostraItemExames : listaPesquisaMonitorPendencias) {
			// Instancia uma nova pendência de exames
			final MonitorPendenciasExamesVO pendenciasExamesVO = this.criarPendenciaExameVO(amostraItemExames, filtrosPesquisa);
			resultado.add(pendenciasExamesVO);
		}

		return resultado;
	}
	
	private String obtemSituacaoItemSolicitacao(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) {
		String situacaoItemSolicitacaoCodigo = null;

		switch (filtrosPesquisa.getViewMonitorPendenciasExames()) {
		case AREA_EXECUTORA:
			// PROCEDURE AELC_GET_DTHR_RECEBE
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.AE.toString();
			break;
		case EXECUTANDO:
			// PROCEDURE AELC_GET_DTHR_EXECUT
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.EX.toString();
			break;
		case COLETADO:
			// PROCEDURE AELC_GET_DTHR_COLETA
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.CO.toString();
			break;
		case EM_COLETA:
			// PROCEDURE AELC_GET_DTHR_EM_COL
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.EC.toString();
			break;
		}
		return situacaoItemSolicitacaoCodigo;
	}

	/*
	 * Métodos auxiliares
	 */

	/**
	 * Instancia uma nova pendência para a lista contendo o resultado de pendências de exames
	 * 
	 * @param amostraItemExames
	 * @param filtrosPesquisa
	 * @return
	 */
	protected MonitorPendenciasExamesVO criarPendenciaExameVO(ResultadoMonitorPendenciasExamesVO resultado, MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) {

		// Instancia um novo VO do monitor de pendências de exames
		final MonitorPendenciasExamesVO pendenciasExamesVO = new MonitorPendenciasExamesVO();
		
		//final AelSolicitacaoExames solicitacaoExame = this.getAelSolicitacaoExameDAO().obterPeloId(resultado.getSoeSeq());
		final AelSolicitacaoExames solicitacaoExame = this.getAelSolicitacaoExameDAO().obterPorChavePrimaria(resultado.getSoeSeq());

		// Obtem a data e hora do evento através da view ou aba selecionada
		pendenciasExamesVO.setDthrEvento(resultado.getDtHrEvento());
		pendenciasExamesVO.setDataHora(resultado.getDtHrEvento());
		pendenciasExamesVO.setDataHoraFormatada(DateUtil.obterDataFormatada(resultado.getDtHrEvento(), "dd/MM/yyyy HH:mm"));

		pendenciasExamesVO.setNroUnico(resultado.getNroUnico());
		pendenciasExamesVO.setSoeSeq(solicitacaoExame.getSeq());
		pendenciasExamesVO.setSeqp(resultado.getIseSeqp());
		pendenciasExamesVO.setAmoSeqp(resultado.getAmoSeqp());

		// Chamada para FUNCTION AELC_LAUDO_PRNT_PAC
		String prontuarioFormatado = this.getRelatorioTicketExamesPacienteON().buscarLaudoProntuarioPaciente(solicitacaoExame);
		pendenciasExamesVO.setProntuarioFormatado(prontuarioFormatado);
		Integer prontuario = Integer.parseInt(prontuarioFormatado.replace("/", ""));
		pendenciasExamesVO.setProntuario(prontuario);

		// Chamada para FUNCTION AELC_LAUDO_NOME_PAC
		String paciente = this.getRelatorioTicketExamesPacienteON().buscarLaudoNomePaciente(solicitacaoExame);
		pendenciasExamesVO.setPaciente(paciente);

		// Chamada para FUNCTION AELC_LAUDO_COD_PAC
		Integer codigoPacienteLis = this.getRelatorioTicketExamesPacienteON().buscarLaudoCodigoPaciente(solicitacaoExame);
		pendenciasExamesVO.setPacCodigo(codigoPacienteLis);

		// Resultado do DECODE: decode(aelc_local_pac(soe.atd_seq),' ','U:DIV',aelc_local_pac(soe.atd_seq)) local
		String resultadoAelcLocalPac = null;
		if (solicitacaoExame.getAtendimento() != null) {
			resultadoAelcLocalPac = getSolicitacaoExameFacade().recuperarLocalPaciente(solicitacaoExame.getAtendimento());
		}
		String local = StringUtils.isEmpty(resultadoAelcLocalPac) ? SIGLA_U_DIV : resultadoAelcLocalPac;
		pendenciasExamesVO.setLocal(local);

		pendenciasExamesVO.setExame(resultado.getExame());
		pendenciasExamesVO.setMaterial(resultado.getMaterial());

		// Formata exame material
		String exameMaterial = this.obterExameMaterial(pendenciasExamesVO.getExame(), pendenciasExamesVO.getMaterial());
		pendenciasExamesVO.setExameMaterial(exameMaterial);

		// Resultado DECODE: decode(amo.unid_tempo_intervalo_coleta,null,null,amo.tempo_intervalo_coleta||amo.unid_tempo_intervalo_coleta) tempo
		final DominioUnidadeMedidaTempo unidTempoIntervaloColeta = resultado.getUnidTempoIntervaloColeta();
		final BigDecimal tempoIntervaloColeta = resultado.getTempoIntervaloColeta();
		String tempo = unidTempoIntervaloColeta == null ? null : unidTempoIntervaloColeta.toString() + tempoIntervaloColeta;
		pendenciasExamesVO.setTempo(tempo);

		pendenciasExamesVO.setIseUfeUnfSeq(resultado.getIseUfeUnfSeq());
		pendenciasExamesVO.setAmoUnfSeq(resultado.getAmoUnfSeq());
		pendenciasExamesVO.setDtNumeroUnico(resultado.getDtNumeroUnico());
		pendenciasExamesVO.setEnviado(resultado.getIndEnviado());
		pendenciasExamesVO.setOrigemMapa(resultado.getOrigemMapa());
		pendenciasExamesVO.setUfeEmaExaSigla(resultado.getUfeEmaExaSigla());
		pendenciasExamesVO.setUfeEmaManSeq(resultado.getUfeEmaManSeq());

		return pendenciasExamesVO;
	}

	/**
	 * Acrescenta pendência na lista de resultados de pendências de exames
	 * 
	 * @param pendenciasExamesVO
	 * @param filtrosPesquisa
	 * @param resultado
	 */
	protected void acrescentarPendenciasExamesListaResultado(MonitorPendenciasExamesVO pendenciasExamesVO, MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa,
			List<MonitorPendenciasExamesVO> resultado) {
		// Parte (FORA DA CONSULTA devido ao impedimento em obter dthrEvento) de p_seta_default_where: Quando informados os filtros da data de referência
		if (filtrosPesquisa.getDataReferenciaIni() != null && filtrosPesquisa.getDataReferenciaFim() != null) {
			// Verifica se a data e hora do evento corresponde ao intervalo de datas informado
			if (DateUtil.entre(pendenciasExamesVO.getDthrEvento(), filtrosPesquisa.getDataReferenciaIni(), filtrosPesquisa.getDataReferenciaFim())) {
				resultado.add(pendenciasExamesVO);
			}
		} else {
			resultado.add(pendenciasExamesVO);
		}
	}

	/**
	 * Obtém a data e hora através da view ou aba selecionada
	 * 
	 * @param amostraItemExames
	 * @param filtrosPesquisa
	 * @return
	 */
	protected Date obterDataHora(ResultadoMonitorPendenciasExamesVO resultado, MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) {

		// Parâmetro que determina a chamada do método correto
		String situacaoItemSolicitacaoCodigo = null;

		switch (filtrosPesquisa.getViewMonitorPendenciasExames()) {
		case AREA_EXECUTORA:
			// PROCEDURE AELC_GET_DTHR_RECEBE
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.AE.toString();
			break;
		case EXECUTANDO:
			// PROCEDURE AELC_GET_DTHR_EXECUT
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.EX.toString();
			break;
		case COLETADO:
			// PROCEDURE AELC_GET_DTHR_COLETA
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.CO.toString();
			break;
		case EM_COLETA:
			// PROCEDURE AELC_GET_DTHR_EM_COL
			situacaoItemSolicitacaoCodigo = DominioSituacaoItemSolicitacaoExame.EC.toString();
			break;
		}

		Integer soeSeq = resultado.getSoeSeq();
		Short seqp = resultado.getIseSeqp();

		return this.getExamesLaudosFacade().buscaMaiorDataRecebimento(soeSeq, seqp, situacaoItemSolicitacaoCodigo);

	}

	/**
	 * Obtém a descrição do exame e material de análise
	 * 
	 * @param descricaoExame
	 * @param descricaoMaterial
	 * @return
	 */
	protected String obterExameMaterial(String descricaoExame, String descricaoMaterial) {
		return obterSubstringExameMaterial(descricaoExame) + "/" + obterSubstringExameMaterial(descricaoMaterial);
	}

	/**
	 * Método auxiliar para obter a descrição do exame e material de análise
	 * 
	 * @param string
	 * @return
	 */
	protected String obterSubstringExameMaterial(String string) {
		if (string != null) {
			return string.length() < 10 ? string : string.substring(0, 10);
		}
		return string;
	}

	/*
	 * Facades, ONs, RNs e DAOs
	 */

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON() {
		return relatorioTicketExamesPacienteON;
	}

	protected AelAmostraItemExamesDAO getAelAmostraItemExamesDAO() {
		return aelAmostraItemExamesDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}

}
