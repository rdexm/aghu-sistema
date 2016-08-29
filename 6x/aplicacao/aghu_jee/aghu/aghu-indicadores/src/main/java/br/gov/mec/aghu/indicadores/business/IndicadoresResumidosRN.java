package br.gov.mec.aghu.indicadores.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.indicadores.dao.AghDatasIgDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghDatasIg;
import br.gov.mec.aghu.model.AghDatasIgId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * RN para Indicadores Resumidos.
 * 
 * @author riccosta
 * 
 */
@Stateless
public class IndicadoresResumidosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(IndicadoresResumidosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

@Inject
private AghDatasIgDAO aghDatasIgDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	public enum IndicadoresResumidosRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_EXECUCAO_GERACAO_INDICADORES
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 898402900709603451L;

	protected IInternacaoFacade getInternacaoFacade() {
		return this
				.internacaoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	/**
	 * Obter Indicadores por tipo de indicador.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AinIndicadorHospitalarResumido> obterTotaisIndicadoresUnidade(
			Date mesCompetencia, Date mesCompetenciaFim,
			DominioTipoIndicador tipoIndicador,
			AghUnidadesFuncionais unidadeFuncional) {

		return this.getInternacaoFacade().obterTotaisIndicadoresUnidade(
				mesCompetencia, mesCompetenciaFim, tipoIndicador,
				unidadeFuncional);
	}

	/**
	 * Método responsável por gravar os Indicadores por Tipo de Indicador.
	 * 
	 * @param anoMesCompetencia
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void gravarIndicadoresPorTipo(Date anoMesCompetencia,
			DominioTipoIndicador tipoIndicador) throws ApplicationBusinessException {

		// Se existir registros para os parametros acima, remove-os.
		if (internacaoFacade.existeIndicadorParaCompetencia(anoMesCompetencia,tipoIndicador)) {
			internacaoFacade.removerIndicadorPorCompetenciaTipoIndicador(anoMesCompetencia, tipoIndicador);
		}

		// Obter qtde de dias do mês.
		Integer quantidadeDiasMes = DateUtil.obterQtdeDiasMes(anoMesCompetencia);

		// Obter Capacidade instalada
		Map<Integer, AinIndicadorHospitalarResumido> map = obterCapacidadeInstaladaPorTipo(quantidadeDiasMes,tipoIndicador);

		// Obter Pacientes dia
		this.obterPacientesDiaPorTipoIndicador(anoMesCompetencia, map,tipoIndicador);

		// Obter qtde de obitos
		this.obterObitosPorTipoIndicador(anoMesCompetencia, map, tipoIndicador);

		// Obter qtde de saidas
		this.obterSaidasPorTipoIndicador(anoMesCompetencia, map, tipoIndicador);

		// Obter qtde de transferencias por tipo de indicador.
		switch (tipoIndicador) {
			case U: getInternacaoFacade().obterTransferenciasPorAreaFuncional(anoMesCompetencia, map); break;
			case C: getInternacaoFacade().obterTransferenciasPorClinica(anoMesCompetencia, map);break;
			case E: getInternacaoFacade().obterTransferenciasPorEspecialidade(anoMesCompetencia, map);break;
			default:break;
		}

		// Calcular indicadores
		Iterator<Integer> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Integer key = iterator.next();
			AinIndicadorHospitalarResumido indRes = map.get(key);

			// Calcular taxa de ocupação.
			Integer numeroPacienteDia = indRes.getQuantidadePaciente();
			Integer leitoDia = indRes.getLeitoDia();

			if (numeroPacienteDia != null && numeroPacienteDia > 0
					&& leitoDia != null && leitoDia > 0) {
				indRes.setTaxaOcupacao(BigDecimal
						.valueOf(numeroPacienteDia)
						.divide(BigDecimal.valueOf(leitoDia), 4,
								BigDecimal.ROUND_HALF_UP)
						.multiply(BigDecimal.valueOf(100)));
			}

			// Obter qtde de tranferência e entidade de acordo com tipo.
			Integer qtdeTransfPorTipo = 0;

			switch (tipoIndicador) {
			case U:
				qtdeTransfPorTipo = indRes
						.getQuantidadeTransferenciaAreaFuncional();
				indRes.setUnidadeFuncional(getAghuFacade()
						.obterAghUnidadesFuncionaisPorChavePrimaria(
								key.shortValue()));
				break;
			case C:
				qtdeTransfPorTipo = indRes.getQuantidadeTransferenciaClinica();
				indRes.setClinica(getAghuFacade().obterClinicaPorChavePrimaria(key.intValue()));
				break;
			case E:
				qtdeTransfPorTipo = indRes
						.getQuantidadeTransferenciaEspecialidade();
				indRes.setEspecialidade(getAghuFacade()
						.obterAghEspecialidadesPorChavePrimaria(
								key.shortValue()));
				break;
			default:
				break;
			}

			Integer numeroObitos = indRes.getQuantidadeObito();
			Integer totalSaidas = indRes.getTotalSaidas();

			// Calcular media de permanencia.
			// Calcular taxa de mortalidade.
			if (((totalSaidas != null && totalSaidas > 0) || (qtdeTransfPorTipo != null && qtdeTransfPorTipo > 0))) {

				Integer denominador = (totalSaidas != null ? totalSaidas : 0)
						+ (qtdeTransfPorTipo != null ? qtdeTransfPorTipo : 0);

				if (numeroPacienteDia != null && numeroPacienteDia > 0) {

					indRes.setMediaPermanencia(BigDecimal.valueOf(
							numeroPacienteDia).divide(
							BigDecimal.valueOf(denominador), 4,
							BigDecimal.ROUND_HALF_UP));
				}

				if (numeroObitos != null && numeroObitos > 0) {

					indRes.setTaxaMortalidade(BigDecimal
							.valueOf(numeroObitos)
							.divide(BigDecimal.valueOf(denominador), 4,
									BigDecimal.ROUND_HALF_UP)
							.multiply(BigDecimal.valueOf(100)));
				}

			}

			// Definindo demais atributos.
			indRes.setDataCriacao(new Date());
			indRes.setCompetenciaInternacao(anoMesCompetencia);
			indRes.setSeq(internacaoFacade.obterIndicadoresResumidosSeq());
			indRes.setTipoIndicador(tipoIndicador);

			this.getInternacaoFacade().inserirAinIndicadorHospitalarResumido(
					indRes);
		}

	}

	/**
	 * Método para atualizar a data inicial e final da tabela AghDatasIg.
	 * 
	 * @param cal
	 */
	public void atualizarDataInicialIg(Calendar cal)
			throws ApplicationBusinessException {
		List<AghDatasIg> datasIg = this.getAghDatasIgDAO().obterAghDataIg();

		Date dataInicio = DateUtil.obterDataInicioCompetencia(cal.getTime());
		Date dataFinal = DateUtil.obterDataFimCompetencia(cal.getTime());

		if (datasIg == null) {
			throw new ApplicationBusinessException(
					IndicadoresResumidosRNExceptionCode.MENSAGEM_ERRO_EXECUCAO_GERACAO_INDICADORES,
					new IllegalArgumentException(
							"Datas competência não localizadas na tabela AGH_DATAS_IG."));
		}

		if (datasIg.isEmpty()) {
			this.getAghDatasIgDAO().persistir(
					new AghDatasIg(new AghDatasIgId(dataInicio, dataFinal)));
		} else {
			datasIg.get(0).getId().setDataInicial(dataInicio);
			datasIg.get(0).getId().setDataFinal(dataFinal);
			this.getAghDatasIgDAO().atualizarDatasIg(dataInicio, dataFinal);
		}

		this.getAghDatasIgDAO().flush();
	}

	protected AghDatasIgDAO getAghDatasIgDAO() {
		return aghDatasIgDAO;
	}

	/**
	 * Metodo utilizado para obter Capacidade Instalada.
	 * 
	 * @param quantidadeDiasMes
	 * @param tipoIndicador
	 * @return
	 */
	private Map<Integer, AinIndicadorHospitalarResumido> obterCapacidadeInstaladaPorTipo(
			Integer quantidadeDiasMes, DominioTipoIndicador tipoIndicador) {

		switch (tipoIndicador) {
		case G:
			return this.getInternacaoFacade().obterCapacidadeInstaladaGeral(
					quantidadeDiasMes);
		case U:
			return this.getInternacaoFacade()
					.obterCapacidadeInstaladaPorUnidadeFuncional(
							quantidadeDiasMes);
		default:
			// Não há capacidade instalada.
			return new HashMap<Integer, AinIndicadorHospitalarResumido>();
		}
	}

	/**
	 * Obter a Numero de saídas do mês por Tipo de Indicador.
	 * 
	 * @param mesCompetencia
	 * @return
	 */
	private void obterSaidasPorTipoIndicador(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			DominioTipoIndicador tipoIndicador) {

		switch (tipoIndicador) {
		case G:
			this.getInternacaoFacade().obterSaidasGeral(mesCompetencia, map);
			break;
		case U:
			this.getInternacaoFacade().obterSaidasPorAreaFuncional(
					mesCompetencia, map);
			break;
		case C:
			this.getInternacaoFacade().obterSaidasPorClinica(mesCompetencia,
					map);
			break;
		case E:
			this.getInternacaoFacade().obterSaidasPorEspecialidade(
					mesCompetencia, map);
			break;
		default:
			break;
		}
	}

	/**
	 * Obter o número de pacientes dia por tipo de indicador.
	 * 
	 * @param mesCompetencia
	 * @param map
	 * @param tipoIndicador
	 * @throws ApplicationBusinessException
	 */
	private void obterPacientesDiaPorTipoIndicador(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			DominioTipoIndicador tipoIndicador) throws ApplicationBusinessException {

		AghParametros parametroAltaC = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_C);

		AghParametros parametroAltaD = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_D);

		switch (tipoIndicador) {
		case G:
			this.getInternacaoFacade().obterPacientesDiaGeral(mesCompetencia,
					map, parametroAltaC, parametroAltaD);
			break;
		case U:
			this.getInternacaoFacade().obterPacientesDiaPorAreaFuncional(
					mesCompetencia, map, parametroAltaC, parametroAltaD);
			break;
		case C:
			this.getInternacaoFacade().obterPacientesDiaPorClinica(
					mesCompetencia, map, parametroAltaC, parametroAltaD);
			break;
		case E:
			this.getInternacaoFacade().obterPacientesDiaPorEspecialidade(
					mesCompetencia, map, parametroAltaC, parametroAltaD);
			break;
		default:
			break;
		}
	}

	/**
	 * Obter a Numero de Obitos por tipo de indicador e mês.
	 * 
	 * @param mesCompetencia
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void obterObitosPorTipoIndicador(Date mesCompetencia,
			Map<Integer, AinIndicadorHospitalarResumido> map,
			DominioTipoIndicador tipoIndicador) throws ApplicationBusinessException {

		AghParametros parametroAltaC = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_C);
		AghParametros parametroAltaD = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CODIGO_TIPO_ALTA_MEDICA_D);

		switch (tipoIndicador) {
		case G:
			this.getInternacaoFacade().obterObitosGeral(mesCompetencia, map,
					parametroAltaC, parametroAltaD);
			break;
		case U:
			this.getInternacaoFacade().obterObitosPorAreaFuncional(
					mesCompetencia, map, parametroAltaC, parametroAltaD);
			break;
		case C:
			this.getInternacaoFacade().obterObitosPorClinica(mesCompetencia,
					map, parametroAltaC, parametroAltaD);
			break;
		case E:
			this.getInternacaoFacade().obterObitosPorEspecialidade(
					mesCompetencia, map, parametroAltaC, parametroAltaD);
			break;
		default:
			break;
		}

	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}
