package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioAcaoParametrosOrcamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoParametrosOrcamentoCriteriaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * ON de Grupos de Natureza de Despesa Parâmetros para SS.
 * 
 * @author mlcruz
 */
@Stateless
public class FsoGrupoNaturezaDespesaSsParamON extends BaseBusiness {

	// TODO migracao
	@EJB
	private FsoParametrosOrcamentoFilterON fsoParametrosOrcamentoFilterON;

	@EJB
	private FsoParametrosOrcamentoSsFinderON fsoParametrosOrcamentoSsFinderON;

	@EJB
	private FsoParametrosOrcamentoON fsoParametrosOrcamentoON;


	@EJB
	private FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON fsoGrupoNaturezaDespesaUniqueRequiredParamFilterON;

	@EJB
	private FsoGrupoNaturezaDespesaParamListFilterON fsoGrupoNaturezaDespesaParamListFilterON;

	private static final long serialVersionUID = 4611739261861006532L;

	private static final Log LOG = LogFactory
			.getLog(FsoGrupoNaturezaDespesaSsParamON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Obtem grupo de natureza parâmetro conforme SS.
	 * 
	 * @param servico
	 *            Serviço
	 * @return Grupo de Natureza
	 */
	public FsoGrupoNaturezaDespesa getGrupoNaturezaSsParam(ScoServico servico) {
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);
		criteria.setMaxResults(2);

		FsoParametrosOrcamento param = getFsoParametrosOrcamentoSsFinderON()
				.getResult(criteria, getFsoParametrosOrcamentoFilterON());
		
		if (param != null) {
			if (Boolean.TRUE.equals(param.getIsCadastradaGrupo())) {
				FsoNaturezaDespesa natureza = servico.getNaturezaDespesa();
				if (natureza != null && natureza.getGrupoNaturezaDespesa() != null) {
					return natureza.getGrupoNaturezaDespesa();
				} else {
					return null;
				}
			} else {
				return param.getGrupoNaturezaDespesa();
			}
		} else {
			return null;
		}
	}

	/**
	 * Indica se grupo de natureza possui parâmetro obrigatório único.
	 * 
	 * @param servico
	 *            Serviço
	 * @return Flag
	 */
	public boolean hasUniqueRequiredGrupoNaturezaSsParam(ScoServico servico) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);

		Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
		acoes.add(DominioAcaoParametrosOrcamento.O);
		acoes.add(DominioAcaoParametrosOrcamento.S);
		criteria.setAcoes(acoes);

		criteria.setMaxResults(2);

		Boolean flag = getFsoParametrosOrcamentoSsFinderON().getResult(
				criteria,
				getFsoGrupoNaturezaDespesaUniqueRequiredParamFilterON());

		return Boolean.TRUE.equals(flag);
	}

	/**
	 * Obtem grupos de natureza parametrizados.
	 * 
	 * @param servico
	 *            Serviço
	 * @param filter
	 *            Filtro
	 * @return Parâmetros
	 */
	public List<FsoGrupoNaturezaDespesa> listarGruposNaturezaSsParams(
			ScoServico servico, Object filter) {
		// Critério
		FsoParametrosOrcamentoCriteriaVO criteria = new FsoParametrosOrcamentoCriteriaVO();

		criteria.setAplicacao(DominioTipoSolicitacao.SS);
		criteria.setParametro(FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);
		criteria.setServico(servico);
		criteria.setSituacao(DominioSituacao.A);
		criteria.setFiltro(filter);
		criteria.setMaxResults(100);

		FsoParametrosOrcamento acaoParam = getFsoParametrosOrcamentoON()
				.getAcaoSsParam(
						servico,
						FsoParametrosOrcamentoCriteriaVO.Parametro.GRUPO_NATUREZA);

		if (acaoParam != null) {
			Set<DominioAcaoParametrosOrcamento> acoes = new HashSet<DominioAcaoParametrosOrcamento>();
			acoes.add(acaoParam.getAcaoGnd());
			criteria.setAcoes(acoes);
		}

		criteria.setOrder(DominioAcaoParametrosOrcamento.O.equals(criteria
				.getAcao()) ? FsoParametrosOrcamento.Fields.GRUPO_NATUREZA_DESPESA
				.toString() : FsoGrupoNaturezaDespesa.Fields.CODIGO.toString());

		return getFsoParametrosOrcamentoSsFinderON().getResult(criteria,
				getFsoGrupoNaturezaDespesaParamListFilterON());
	}

	protected FsoParametrosOrcamentoON getFsoParametrosOrcamentoON() {
		return fsoParametrosOrcamentoON;
	}

	protected FsoParametrosOrcamentoSsFinderON getFsoParametrosOrcamentoSsFinderON() {
		return fsoParametrosOrcamentoSsFinderON;
	}

	protected FsoParametrosOrcamentoFilterON getFsoParametrosOrcamentoFilterON() {
		return fsoParametrosOrcamentoFilterON;
	}

	protected FsoGrupoNaturezaDespesaUniqueRequiredParamFilterON getFsoGrupoNaturezaDespesaUniqueRequiredParamFilterON() {
		return fsoGrupoNaturezaDespesaUniqueRequiredParamFilterON;
	}

	protected FsoGrupoNaturezaDespesaParamListFilterON getFsoGrupoNaturezaDespesaParamListFilterON() {
		return fsoGrupoNaturezaDespesaParamListFilterON;
	}

}
