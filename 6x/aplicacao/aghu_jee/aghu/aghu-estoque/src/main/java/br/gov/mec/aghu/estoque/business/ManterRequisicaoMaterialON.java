/**
 * 
 */
package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaCentroCusto;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceConsumoTotalMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.vo.ConsumoMaterialVO;
import br.gov.mec.aghu.estoque.vo.ConsumoMaterialVO.TipoConsumo;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * @author bruno.mourao
 * 
 */
@Stateless
public class ManterRequisicaoMaterialON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterRequisicaoMaterialON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private SceConsumoTotalMateriaisDAO sceConsumoTotalMateriaisDAO;

	@EJB
	private IPermissionService permissionService;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private static final long serialVersionUID = -2173338955570777802L;

	protected SceConsumoTotalMateriaisDAO getSceConsumoTotalMateriaisDAO() {
		return sceConsumoTotalMateriaisDAO;
	}

	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	private IPermissionService getPermissionService() {
		return this.permissionService;
	}

	/**
	 * Obtem consumos de materiais. Transcrição da procedure
	 * BUSCA_CONSUMO_MEDIO_SEMESTRE (forms refere à história 595).
	 * 
	 * @ORADB: procedure BUSCA_CONSUMO_MEDIO_SEMESTRE
	 * @return
	 * @author bruno.mourao
	 * 
	 * @since 29/06/2011
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public Map<TipoConsumo, ConsumoMaterialVO> obterConsumos(Date dataCompetencia, Integer codigoMaterial, Integer codigoCCAplicacao,
			Integer codigoGrupoMaterial) throws ApplicationBusinessException {

		Map<TipoConsumo, ConsumoMaterialVO> mapResult = new HashMap<ConsumoMaterialVO.TipoConsumo, ConsumoMaterialVO>();

		Double consUlt30Dias = 0.0;
		Double consumoMesAnterior = 0.0;
		Integer consumoMesAtual = 0;
		Integer consumoSemestre = 0;
		Double consumoMedioSemestre = 0.0;

		Date dataInicio;
		Date dataFim;
		Calendar cal = Calendar.getInstance();
		Integer diaHoje = cal.get(Calendar.DAY_OF_MONTH);

		// Obtem o material
		ScoMaterial material = getComprasFacade().obterScoMaterialPorChavePrimaria(codigoMaterial);

		// obtém o parametro P_GRUPOS_ANALISE
		AghParametros pGruposAnalise = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPOS_ANALISE);

		// separador de substring, com token ","
		// vlrTexto = 01,02,03,04,06,09,17,20,34,36,39,41
		StringTokenizer codigosGruposMateriaisParametro = new StringTokenizer(pGruposAnalise.getVlrTexto(), ",");
		String codigoGrupoParametro = null;
		Integer codigoGrupo = null;

		// armazenará os códigos convertidos para Integer
		List<Integer> codigosGruposMateriais = new ArrayList<Integer>();

		while (codigosGruposMateriaisParametro.hasMoreTokens()) {
			codigoGrupoParametro = codigosGruposMateriaisParametro.nextToken();
			if (CoreUtil.isNumeroInteger(codigoGrupoParametro)) {
				codigoGrupo = Integer.parseInt(codigoGrupoParametro);
				codigosGruposMateriais.add(codigoGrupo);
			}
		}

		// verifica se o código do grupo do material está presente
		// em [01,02,03,04,06,09,17,20,34,36,39,41]
		if (codigosGruposMateriais.contains(codigoGrupoMaterial)) {
			// obtem consumo mes atual
			consumoMesAtual = getSceConsumoTotalMateriaisDAO().obterConsumoNoMes(codigoMaterial, codigoCCAplicacao, dataCompetencia);

			if (consumoMesAtual == null) {
				consumoMesAtual = 0;
			}

			// Obter o dia atual
			/*
			 * cal = Calendar.getInstance(); diaHoje =
			 * cal.get(Calendar.DAY_OF_MONTH);
			 */

			Integer diasMesAnt = 30 - diaHoje; // Considera sempre 30 dias de
												// consumo
			if (diasMesAnt > 0) {
				cal.setTime(dataCompetencia);

				// Obtem data fim (ultimo dia do mes passado)
				cal.set(Calendar.DAY_OF_MONTH, 1); // vai pro primeiro dia deste
													// mes
				cal.add(Calendar.DAY_OF_MONTH, -1); // tira um dia, indo para o
													// ultimo dia do mes passado
				zeraHora(cal);

				dataFim = cal.getTime();

				// Obtem datas de incio do consumo do mes passado (soh pega os
				// dias
				// do mes passado cuja soma com os dias do mes atual darao 30)
				cal.add(Calendar.DAY_OF_MONTH, (diasMesAnt) * -1);
				dataInicio = cal.getTime();
				// obtem consumo do mes anterior
				consumoMesAnterior = getSceMovimentoMaterialDAO().obterConsumoPassadoPorTmvEstorno(dataInicio, dataFim, codigoMaterial,
						codigoCCAplicacao);

				if (consumoMesAnterior == null) {
					consumoMesAnterior = 0.0;
				}
			}
		}
		consUlt30Dias = consumoMesAtual + consumoMesAnterior;

		// Adiciona no mapa o consumo dos ultimos 30 dias
		mapResult.put(ConsumoMaterialVO.TipoConsumo.ULT_30_DIAS, new ConsumoMaterialVO(consUlt30Dias, material,
				ConsumoMaterialVO.TipoConsumo.ULT_30_DIAS));

		// prepara datas para obter do ultimo semestre
		cal.setTime(dataCompetencia); // volta a data para a data de competencia
		cal.add(Calendar.MONTH, -1);// mes final do periodo
		dataFim = cal.getTime();

		// volta para a data de inicio de 6 meses antes do mes anterior a
		// competencia
		cal.add(Calendar.MONTH, -6);

		zeraHora(cal);

		dataInicio = cal.getTime();

		// Obtem consumo dos ultimos 6 meses
		consumoSemestre = getSceConsumoTotalMateriaisDAO().obterConsumoNoPeriodo(codigoMaterial, codigoCCAplicacao, dataInicio, dataFim);

		if (consumoSemestre != null && consumoSemestre > 0) {

			// Obtem o consumo medio do semestre
			consumoMedioSemestre = consumoSemestre.doubleValue() / 6;
		}

		// Adiciona no mapa o consumo dos ultimos 30 dias
		mapResult.put(ConsumoMaterialVO.TipoConsumo.MEDIO_SEMESTRE, new ConsumoMaterialVO(consumoMedioSemestre, material,
				ConsumoMaterialVO.TipoConsumo.MEDIO_SEMESTRE));

		return mapResult;
	}

	/**
	 * @param cal
	 * @author bruno.mourao
	 * @since 28/09/2011
	 */
	private void zeraHora(Calendar cal) {
		// zera horas
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
	}

	private List<Integer> pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		List<FccCentroCustos> retorno = getCentroCustoFacade().pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(null,
				DominioCaracteristicaCentroCusto.GERAR_RM);

		List<Integer> result = new ArrayList<Integer>();
		for (FccCentroCustos cc : retorno) {
			result.add(cc.getCodigo());
		}
		return result;
	}

	/**
	 * Pesquisa almoxarifados que possuem o mesmo centro de custo do usuário
	 * 
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 * 
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadoPorCentroCustoUsuarioCodigoDescricao(String parametro,
			final Date dataFimVinculoServidor) throws ApplicationBusinessException {

		Boolean indCentral = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "consultarAlmoxarifadoCentral",
				"consultar");

		List<Short> codigosAlmoxarifados = new ArrayList<Short>();

		if (getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "consultarAlmoxarifadosNaoDistribuidores", "consultar")) {
			AghParametros paramAlmoxarifadosDistribuidores = getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_ALMOXARIFADOS_DISTRIBUIDORES);
			if (paramAlmoxarifadosDistribuidores != null) {
				String codigosAlmoxarifadosDistribuidores = paramAlmoxarifadosDistribuidores.getVlrTexto();
				String codigoAlmoxarifadoDistribuidor = null;
				Short codigoAlmoxarifado = null;
				if (codigosAlmoxarifadosDistribuidores != null && !codigosAlmoxarifadosDistribuidores.isEmpty()) {
					StringTokenizer codigos = new StringTokenizer(codigosAlmoxarifadosDistribuidores, ",");
					while (codigos.hasMoreTokens()) {
						codigoAlmoxarifadoDistribuidor = codigos.nextToken();
						if (CoreUtil.isNumeroShort(codigoAlmoxarifadoDistribuidor)) {
							codigoAlmoxarifado = Short.parseShort(codigoAlmoxarifadoDistribuidor);
							codigosAlmoxarifados.add(codigoAlmoxarifado);
						}
					}
				}
			}
		}
		return getSceAlmoxarifadoDAO().pesquisarAlmoxarifadoPorCentroCustoDisponivelUsuarioCodigoDescricao(parametro,
				pesquisarCentrosCustosUsuarioHierarquiaAtuacaoLotacao(dataFimVinculoServidor), indCentral, codigosAlmoxarifados);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
