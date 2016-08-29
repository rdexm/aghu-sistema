package br.gov.mec.aghu.exames.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoTransporte;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.vo.RelatorioEstatisticaTipoTransporteExtratoVO;
import br.gov.mec.aghu.exames.vo.RelatorioEstatisticaTipoTransporteVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateValidator;

@Stateless
public class RelatorioEstatisticaTipoTransporteON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(RelatorioEstatisticaTipoTransporteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1161148045628088746L;

	// Formata hora em 24Hmm
	private static final SimpleDateFormat SDF_24HMM = new SimpleDateFormat("kmm");

	public enum RelatorioEstatisticaTipoTransporteONExceptionCode implements BusinessExceptionCode {
		AEL_01904;
	}

	/**
	 * Pesquisa do relatório de estatística do tipo de transporte para uma unidade executora
	 */
	public List<RelatorioEstatisticaTipoTransporteVO> pesquisarRelatorioEstatisticaTipoTransporte(AghUnidadesFuncionais unidadeExecutora, DominioOrigemAtendimento origem,
			Date dataInicial, Date dataFinal) throws BaseException {

		// Verifica se a data final é menor que a data inicial
		if (DateValidator.validaDataMenor(dataFinal, dataInicial)) {
			throw new ApplicationBusinessException(RelatorioEstatisticaTipoTransporteONExceptionCode.AEL_01904);
		}

		// Pesquisa principal do relatório
		List<RelatorioEstatisticaTipoTransporteExtratoVO> listaItemExtrato = this.getAelExtratoItemSolicitacaoDAO().pesquisarRelatorioEstatisticaTipoTransporte(unidadeExecutora,
				origem, dataInicial, dataFinal);

		// Instancia conjunto de VALORES ÚNICOS para armazenar os DIFERENTES TIPOS DE TRANSPORTE
		Set<RelatorioEstatisticaTipoTransporteVO> retorno = new HashSet<RelatorioEstatisticaTipoTransporteVO>();

		// Instancia para o Tipo de transporte: DEAMBULANDO
		final RelatorioEstatisticaTipoTransporteVO deambulando = new RelatorioEstatisticaTipoTransporteVO();
		deambulando.setTipoTransporte(DominioTipoTransporte.D.getDescricao());

		// Instancia para o Tipo de transporte: CADEIRA
		final RelatorioEstatisticaTipoTransporteVO cadeira = new RelatorioEstatisticaTipoTransporteVO();
		cadeira.setTipoTransporte(DominioTipoTransporte.C.getDescricao());

		// Instancia para o Tipo de transporte: LEITO
		final RelatorioEstatisticaTipoTransporteVO leito = new RelatorioEstatisticaTipoTransporteVO();
		leito.setTipoTransporte(DominioTipoTransporte.L.getDescricao());

		// Instancia para o Tipo de transporte: MACA
		final RelatorioEstatisticaTipoTransporteVO maca = new RelatorioEstatisticaTipoTransporteVO();
		maca.setTipoTransporte(DominioTipoTransporte.M.getDescricao());

		// Instancia para o Tipo de transporte: CAMA
		final RelatorioEstatisticaTipoTransporteVO cama = new RelatorioEstatisticaTipoTransporteVO();
		cama.setTipoTransporte(DominioTipoTransporte.A.getDescricao());

		// Instancia para o Tipo de transporte: COLO
		final RelatorioEstatisticaTipoTransporteVO colo = new RelatorioEstatisticaTipoTransporteVO();
		colo.setTipoTransporte(DominioTipoTransporte.O.getDescricao());

		// Instancia para o Tipo de transporte: SEM TRANSPORTE
		final RelatorioEstatisticaTipoTransporteVO semTransporte = new RelatorioEstatisticaTipoTransporteVO();
		semTransporte.setTipoTransporte("Sem Transporte");

		// Hora inicial e final do PRIMEIRO TURNO
		final Integer pAghuHoraIniTurno1 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_INI_TURNO1);
		final Integer pAghuHoraFimTurno1 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_FIM_TURNO1);

		// Hora inicial e final do SEGUNDO TURNO
		final Integer pAghuHoraIniTurno2 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_INI_TURNO2);
		final Integer pAghuHoraFimTurno2 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_FIM_TURNO2);

		// Hora inicial e final do TERCEIRO TURNO
		final Integer pAghuHoraIniTurno3 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_INI_TURNO3);
		final Integer pAghuHoraFimTurno3 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_FIM_TURNO3);

		// Hora inicial e final do QUARTO TURNO
		final Integer pAghuHoraIniTurno4 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_INI_TURNO4);
		final Integer pAghuHoraFimTurno4 = this.obterValorTurnoAghParametro(AghuParametrosEnum.P_AGHU_HORA_FIM_TURNO4);

		// Percorre itens de extrato da consulta principal
		for (RelatorioEstatisticaTipoTransporteExtratoVO itemExtrato : listaItemExtrato) {

			// Instancia que receberá o tipo de transporte selecionado atual
			RelatorioEstatisticaTipoTransporteVO voTipoTransporteAtual = null;

			DominioTipoTransporte tipoTransporteItemExtrato = itemExtrato.getTipoTransporte();

			// Testa o tipo de transporte
			if (tipoTransporteItemExtrato == null) {
				// Sem transporte
				voTipoTransporteAtual = semTransporte;
				retorno.add(semTransporte);
			} else {
				switch (tipoTransporteItemExtrato) {
				case D:
					voTipoTransporteAtual = deambulando;
					retorno.add(deambulando); // ATENÇÃO: Acrescenta (sem sobrescrever) no conjunto
					break;
				case C:
					voTipoTransporteAtual = cadeira;
					retorno.add(cadeira);
					break;
				case L:
					voTipoTransporteAtual = leito;
					retorno.add(leito);
					break;
				case M:
					voTipoTransporteAtual = maca;
					retorno.add(maca);
					break;
				case A:
					voTipoTransporteAtual = cama;
					retorno.add(cama);
					break;
				case O:
					voTipoTransporteAtual = colo;
					retorno.add(colo);
					break;
				}
			}

			// Formata a data e hora do evento
			Integer horaFormatada = Integer.valueOf(SDF_24HMM.format(itemExtrato.getDataHoraEvento()));

			// Calcula a quantidade por turno
			if (horaFormatada > pAghuHoraIniTurno1 && horaFormatada <= pAghuHoraFimTurno1) {
				// Primeiro turno
				Integer somaQuantidadeTurno1 = voTipoTransporteAtual.getQuantidadeTurno1() + 1;
				voTipoTransporteAtual.setQuantidadeTurno1(somaQuantidadeTurno1);
			} else if (horaFormatada > pAghuHoraIniTurno2 && horaFormatada <= pAghuHoraFimTurno2) {
				// Segundo turno
				Integer somaQuantidadeTurno2 = voTipoTransporteAtual.getQuantidadeTurno2() + 1;
				voTipoTransporteAtual.setQuantidadeTurno2(somaQuantidadeTurno2);
			} else if (horaFormatada > pAghuHoraIniTurno3 && horaFormatada <= pAghuHoraFimTurno3) {
				// Terceiro turno
				Integer somaQuantidadeTurno3 = voTipoTransporteAtual.getQuantidadeTurno3() + 1;
				voTipoTransporteAtual.setQuantidadeTurno3(somaQuantidadeTurno3);
			} else if (horaFormatada > pAghuHoraIniTurno4 || horaFormatada <= pAghuHoraFimTurno4) {
				// Quarto turno
				Integer somaQuantidadeTurno4 = voTipoTransporteAtual.getQuantidadeTurno4() + 1;
				voTipoTransporteAtual.setQuantidadeTurno4(somaQuantidadeTurno4);
			}

		}

		// Processa TOTAL por tipo de transporte
		Integer totalGeral = 0;
		for (RelatorioEstatisticaTipoTransporteVO vo : retorno) {
			// Calcula o total de todos os turnos de um tipo de transporte
			vo.setTotal(this.calcularTotalTurnosTipoTransporte(vo));
			// Soma no total geral de todos os turnos em todos os tipos de transporte
			totalGeral += vo.getTotal();
		}

		// Processa PERCENTUAL por tipo de transporte
		for (RelatorioEstatisticaTipoTransporteVO vo : retorno) {
			// Calcula o total de todos os turnos de um tipo de transporte
			Integer totalTodosTurnosTipoTransporte = this.calcularTotalTurnosTipoTransporte(vo);
			// Calcula o percentual do total de todos os turnos com o total geral dos turnos
			vo.setPercentual(this.calcularPercentualTotal(totalTodosTurnosTipoTransporte, totalGeral));
		}

		// Converte SET/CONJUNTO de resultados em um LIST
		List<RelatorioEstatisticaTipoTransporteVO> resultados = new LinkedList<RelatorioEstatisticaTipoTransporteVO>(retorno);

		/*
		 * Ordena a lista através do tipo de transporte. A ORDENAÇÃO PADRÃO é a DESCRIÇÃO do tipo de transporte
		 */
		Collections.sort(resultados);

		return resultados;

	}

	/**
	 * Calcula o total de todos os turnos de um tipo de transporte
	 * 
	 * @param voAtual
	 * @return
	 */
	private Integer calcularTotalTurnosTipoTransporte(RelatorioEstatisticaTipoTransporteVO vo) {

		Integer quantidadeTurno1 = vo.getQuantidadeTurno1() != null ? vo.getQuantidadeTurno1() : 0;
		Integer quantidadeTurno2 = vo.getQuantidadeTurno2() != null ? vo.getQuantidadeTurno2() : 0;
		Integer quantidadeTurno3 = vo.getQuantidadeTurno3() != null ? vo.getQuantidadeTurno3() : 0;
		Integer quantidadeTurno4 = vo.getQuantidadeTurno4() != null ? vo.getQuantidadeTurno4() : 0;

		return quantidadeTurno1 + quantidadeTurno2 + quantidadeTurno3 + quantidadeTurno4;
	}

	/**
	 * Obtem um mapa com os totais por turno
	 * 
	 * @param lista
	 * @return
	 */
	public Map<Integer, Integer> obterTotaisTurno(List<RelatorioEstatisticaTipoTransporteVO> lista) {

		HashMap<Integer, Integer> turnos = new HashMap<Integer, Integer>();

		Integer totalTurno1 = 0;
		Integer totalTurno2 = 0;
		Integer totalTurno3 = 0;
		Integer totalTurno4 = 0;

		for (RelatorioEstatisticaTipoTransporteVO vo : lista) {
			totalTurno1 += vo.getQuantidadeTurno1() != null ? vo.getQuantidadeTurno1() : 0;
			totalTurno2 += vo.getQuantidadeTurno2() != null ? vo.getQuantidadeTurno2() : 0;
			totalTurno3 += vo.getQuantidadeTurno3() != null ? vo.getQuantidadeTurno3() : 0;
			totalTurno4 += vo.getQuantidadeTurno4() != null ? vo.getQuantidadeTurno4() : 0;
		}

		turnos.put(1, totalTurno1);
		turnos.put(2, totalTurno2);
		turnos.put(3, totalTurno3);
		turnos.put(4, totalTurno4);

		return turnos;

	}

	/**
	 * Obtem um mapa com os percentuais por turno
	 */
	public Map<Integer, BigDecimal> obterPercentuaisTurno(List<RelatorioEstatisticaTipoTransporteVO> lista) {

		HashMap<Integer, BigDecimal> turnos = new HashMap<Integer, BigDecimal>();

		Integer totalTurno1 = 0;
		Integer totalTurno2 = 0;
		Integer totalTurno3 = 0;
		Integer totalTurno4 = 0;

		for (RelatorioEstatisticaTipoTransporteVO vo : lista) {
			totalTurno1 += vo.getQuantidadeTurno1() != null ? vo.getQuantidadeTurno1() : 0;
			totalTurno2 += vo.getQuantidadeTurno2() != null ? vo.getQuantidadeTurno2() : 0;
			totalTurno3 += vo.getQuantidadeTurno3() != null ? vo.getQuantidadeTurno3() : 0;
			totalTurno4 += vo.getQuantidadeTurno4() != null ? vo.getQuantidadeTurno4() : 0;
		}

		Integer totalGeral = totalTurno1 + totalTurno2 + totalTurno3 + totalTurno4;

		BigDecimal percentualTurno1 = BigDecimal.ZERO;
		BigDecimal percentualTurno2 = BigDecimal.ZERO;
		BigDecimal percentualTurno3 = BigDecimal.ZERO;
		BigDecimal percentualTurno4 = BigDecimal.ZERO;

		percentualTurno1 = this.calcularPercentualTotal(totalTurno1, totalGeral);
		percentualTurno2 = this.calcularPercentualTotal(totalTurno2, totalGeral);
		percentualTurno3 = this.calcularPercentualTotal(totalTurno3, totalGeral);
		percentualTurno4 = this.calcularPercentualTotal(totalTurno4, totalGeral);

		turnos.put(1, percentualTurno1);
		turnos.put(2, percentualTurno2);
		turnos.put(3, percentualTurno3);
		turnos.put(4, percentualTurno4);

		return turnos;

	}

	/**
	 * Obtem o valor do turno através de um parâmetro do sistema
	 * 
	 * @param parametrosEnum
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Integer obterValorTurnoAghParametro(AghuParametrosEnum parametrosEnum) throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(parametrosEnum);
		return parametro.getVlrNumerico().intValue();
	}

	/**
	 * Calcula o percentual de um total
	 * 
	 * @param parcial
	 * @param total
	 * @return
	 */
	private BigDecimal calcularPercentualTotal(Integer parcial, Integer total) {
		// Equivale a RN1: ( turno ‘N’ / total_turnos * 100 )
		if (total != null && total > 0) {
			return new BigDecimal(new Double(parcial) * 100 / total).setScale(2, RoundingMode.HALF_EVEN);
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Obtem o total geral de todos os turnos EM TODOS TIPOS DE TRANSPORTE
	 * 
	 * @param lista
	 * @return
	 */
	public Integer obterTotalGeralTurnos(List<RelatorioEstatisticaTipoTransporteVO> lista) {

		Integer totalTurno1 = 0;
		Integer totalTurno2 = 0;
		Integer totalTurno3 = 0;
		Integer totalTurno4 = 0;

		for (RelatorioEstatisticaTipoTransporteVO vo : lista) {
			totalTurno1 += vo.getQuantidadeTurno1() != null ? vo.getQuantidadeTurno1() : 0;
			totalTurno2 += vo.getQuantidadeTurno2() != null ? vo.getQuantidadeTurno2() : 0;
			totalTurno3 += vo.getQuantidadeTurno3() != null ? vo.getQuantidadeTurno3() : 0;
			totalTurno4 += vo.getQuantidadeTurno4() != null ? vo.getQuantidadeTurno4() : 0;
		}

		return totalTurno1 + totalTurno2 + totalTurno3 + totalTurno4;
	}

	/**
	 * Getters para RNs e DAOs
	 */

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

}