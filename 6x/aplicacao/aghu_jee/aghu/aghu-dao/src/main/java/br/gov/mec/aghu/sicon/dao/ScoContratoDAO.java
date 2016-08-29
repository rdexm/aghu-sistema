package br.gov.mec.aghu.sicon.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioPossuiSIASG;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoCriterioReajusteContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialSicon;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.LicitacaoFiltroVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Classe responsável pelo acesso a camada de dados do model ScoContrato
 * 
 * @modulo sicon
 * @author agerling
 * 
 */
public class ScoContratoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoContrato> {

	private static final long serialVersionUID = 1485926922442719456L;

	/**
	 * Retorna lista de Contratos associados com algum Tipo de Contrato,
	 * indicado no parâmetro do método.
	 * 
	 * @param _tipoContrato
	 * @return Listagem de Contratos associados.
	 */
	public List<ScoContrato> obterListaContratoAssociado(ScoTipoContratoSicon _tipoContrato) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);

		criteria.add(Restrictions.eq(ScoContrato.Fields.TIPO_CONTRATO_SICON.toString(), _tipoContrato));

		return this.executeCriteria(criteria);
	}

	public List<ScoContrato> obterListaContratoPorCriterioReajuste(ScoCriterioReajusteContrato scoCriterioReajusteContrato) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);

		criteria.add(Restrictions.eq(ScoContrato.Fields.CRITERIO_REAJUSTE_CONTRATO.toString(), scoCriterioReajusteContrato));

		return this.executeCriteria(criteria);
	}

	public boolean numeroContratoJaExiste(Long nrContrato) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);

		criteria.add(Restrictions.eq(ScoContrato.Fields.NR_CONTRATO.toString(), nrContrato));

		List<ScoContrato> list = executeCriteria(criteria);

		if (list.size() > 0) {
			return true;
		}

		return false;
	}

	public ScoContrato obterContratoPorNumeroContrato(Long nrContrato) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);
		
		criteria.createAlias(ScoContrato.Fields.ADITIVOS.toString(), "adtvs", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoContrato.Fields.TIPO_CONTRATO_SICON.toString(), "tcs", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoContrato.Fields.MODALIDADE_LICITACAO.toString(), "mlc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoContrato.Fields.FORNECEDOR.toString(), "forn", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoContrato.Fields.CRITERIO_REAJUSTE_CONTRATO.toString(), "crc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoContrato.Fields.SERVIDOR_GESTOR.toString(), "serv_gest", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv_gest." + RapServidores.Fields.PESSOA_FISICA.toString(), "gest_pf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoContrato.Fields.SERVIDOR_FISCAL.toString(), "serv_fisc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv_fisc." + RapServidores.Fields.PESSOA_FISICA.toString(), "fisc_pf", JoinType.LEFT_OUTER_JOIN);

		if (nrContrato != null) {
			criteria.add(Restrictions.eq(ScoContrato.Fields.NR_CONTRATO.toString(), nrContrato));
		}

		List<ScoContrato> list = executeCriteria(criteria);

		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}

		return null;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public List<ScoContrato> obterContratoByFiltro(ContratoFiltroVO filtro) {
		List<ScoContrato> result = new ArrayList<ScoContrato>();

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);
		
//		criteria.createAlias(ScoContrato.Fields.ADITIVOS.toString(), "adtvs",
//				JoinType.LEFT_OUTER_JOIN);

		DetachedCriteria tipoContCrit = criteria.createCriteria(ScoContrato.Fields.TIPO_CONTRATO_SICON.toString(),
				JoinType.LEFT_OUTER_JOIN);
		DetachedCriteria fornCrit = criteria.createCriteria(ScoContrato.Fields.FORNECEDOR.toString(), JoinType.LEFT_OUTER_JOIN);

		if (filtro.getContrato().getNrContrato() != null) {
			criteria.add(Restrictions.eq(ScoContrato.Fields.NR_CONTRATO.toString(), filtro.getContrato().getNrContrato()));
		}

		if (filtro.getContrato().getDataIniVigComAditivos() != null) {
			criteria.add(Restrictions.ge(ScoContrato.Fields.DT_INICIO_VIGENCIA.toString(), filtro.getContrato()
					.getDataIniVigComAditivos()));
		}

		if (filtro.getContrato().getDataFimVigComAditivos() != null) {
			criteria.add(Restrictions.le(ScoContrato.Fields.DT_FIM_VIGENCIA.toString(), filtro.getContrato()
					.getDataFimVigComAditivos()));
		}

		if (filtro.getContrato().getLicitacao() != null) {
			DetachedCriteria licCrit = criteria.createCriteria(ScoContrato.Fields.LICITACAO.toString());

			licCrit.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), filtro.getContrato().getLicitacao().getNumero()));

		}

		if (filtro.getContrato().getModalidadeLicitacao() != null) {
			DetachedCriteria modLicCrit = criteria.createCriteria(ScoContrato.Fields.MODALIDADE_LICITACAO.toString());

			modLicCrit.add(Restrictions.eq(ScoModalidadeLicitacao.Fields.CODIGO.toString(), filtro.getContrato()
					.getModalidadeLicitacao().getCodigo()));
		}

		if (filtro.getContrato().getIndAditivar() != null) {
			criteria.add(Restrictions.eq(ScoContrato.Fields.IND_ADITIVAR.toString(), filtro.getContrato().getIndAditivar()));
		}

		if (filtro.getContrato().getTipoContratoSicon() != null) {

			tipoContCrit.add(Restrictions.eq(ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(), filtro.getContrato()
					.getTipoContratoSicon().getCodigoSicon()));
		}

		if (filtro.getContrato().getFornecedor() != null) {

			fornCrit.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), filtro.getContrato().getFornecedor().getNumero()));
		}

		if (filtro.getAf().getNumero() != null && filtro.getAf().getNroComplemento() != null) {

			DetachedCriteria afCrit = criteria.createCriteria(ScoContrato.Fields.AFS.toString(), JoinType.LEFT_OUTER_JOIN);
			DetachedCriteria afContratoCrit = afCrit.createCriteria(ScoAfContrato.Fields.AUT_FORN.toString(),
					JoinType.LEFT_OUTER_JOIN);

			afContratoCrit.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getAf()
					.getNroComplemento()));

			DetachedCriteria propCrit = afContratoCrit.createCriteria(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString());

			DetachedCriteria licCrit = propCrit.createCriteria(ScoPropostaFornecedor.Fields.LICITACAO.toString());

			licCrit.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), filtro.getAf().getNumero()));

		}

		Disjunction or = Restrictions.disjunction();

		if (filtro.getContrato().getSituacao() != null) {
			or.add(Restrictions.eq(ScoContrato.Fields.SITUACAO.toString(), filtro.getContrato().getSituacao()));
		}

/*		if (filtro.getSitEnvAditivo() != null) {
			criteria.createAlias(ScoContrato.Fields.ADITIVOS.toString(), "adt", Criteria.LEFT_JOIN);

			or.add(Restrictions.eq("adt." + ScoAditContrato.Fields.SITUACAO.toString(), filtro.getSitEnvAditivo()));
		}*/

		if (filtro.getSitEnvResc() != null) {
			criteria.createAlias(ScoContrato.Fields.RESCICAO.toString(), "res", JoinType.LEFT_OUTER_JOIN);

			or.add(Restrictions.eq("res." + ScoResContrato.Fields.IND_SITUACAO.toString(), filtro.getSitEnvResc()));

		}

		criteria.add(or);

		criteria.addOrder(Order.asc(ScoContrato.Fields.IND_ORIGEM.toString()));
		criteria.addOrder(Order.asc(ScoContrato.Fields.CRIADO_EM.toString()));

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		result = executeCriteria(criteria);

		return result;
	}


	/*private void proxyScoContratoInitialize(List<ScoContrato> contratos) {
		for (ScoContrato contrato : contratos) {
			Hibernate.initialize(contrato.getScoAfContratos());
			Hibernate.initialize(contrato.getItensContrato());
			for (ScoAfContrato afContrato : contrato.getScoAfContratos()) {
				Hibernate.initialize(afContrato.getScoAutorizacoesForn());
				if (afContrato.getScoAutorizacoesForn() != null) {
					Hibernate.initialize(afContrato.getScoAutorizacoesForn().getItensAutorizacaoForn());
					for (ScoItemAutorizacaoForn item : afContrato.getScoAutorizacoesForn().getItensAutorizacaoForn()) {
						Hibernate.initialize(item.getScoFaseSolicitacao());
						
						for (ScoFaseSolicitacao faseSol : item.getScoFaseSolicitacao()) {
							Hibernate.initialize(faseSol.getSolicitacaoDeCompra());
							if (faseSol.getSolicitacaoDeCompra() != null) {
								Hibernate.initialize(faseSol.getSolicitacaoDeCompra().getMaterial());
								if (faseSol.getSolicitacaoDeCompra().getMaterial() != null) {
									Hibernate.initialize(faseSol.getSolicitacaoDeCompra().getMaterial().getMateriaisSicon());
								}
							}
							if (faseSol.getSolicitacaoServico() != null) {
								Hibernate.initialize(faseSol.getSolicitacaoServico());
								Hibernate.initialize(faseSol.getSolicitacaoServico().getServico());
								if (faseSol.getSolicitacaoServico().getServico() != null) {
									Hibernate.initialize(faseSol.getSolicitacaoServico().getServico().getServicoSicon());
								}
							}
						}
					}
				}
			}
			for (ScoItensContrato itemCont : contrato.getItensContrato()) {
				Hibernate.initialize(itemCont.getServico());
				if (itemCont.getServico() != null) {
					Hibernate.initialize(itemCont.getServico().getServicoSicon());
				}

				Hibernate.initialize(itemCont.getMaterial());

				if (itemCont.getMaterial() != null) {
					Hibernate.initialize(itemCont.getMaterial().getMateriaisSicon());
				}
			}
		}
	}*/

	// Consulta de Gerenciamento de Integração
	@SuppressWarnings("PMD.NPathComplexity")
	public List<ScoContrato> listarContratosFiltro(ContratoFiltroVO filtro) {

		List<ScoContrato> result = new ArrayList<ScoContrato>();

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);

		DetachedCriteria fornCrit = criteria.createCriteria(ScoContrato.Fields.FORNECEDOR.toString(), JoinType.LEFT_OUTER_JOIN);

		DetachedCriteria afCrit = criteria.createCriteria(ScoContrato.Fields.AFS.toString(), JoinType.LEFT_OUTER_JOIN);
		DetachedCriteria afContratoCrit = afCrit.createCriteria(ScoAfContrato.Fields.AUT_FORN.toString(),
				JoinType.LEFT_OUTER_JOIN);

		if (filtro.getContrato().getSituacao() != null) {
			criteria.add(Restrictions.eq(ScoContrato.Fields.SITUACAO.toString(), filtro.getContrato().getSituacao()));
		}

		if (filtro.getContrato().getNrContrato() != null) {
			criteria.add(Restrictions.eq(ScoContrato.Fields.NR_CONTRATO.toString(), filtro.getContrato().getNrContrato()));
		}

		if (filtro.getContrato().getDataIniVigComAditivos() != null) {
			criteria.add(Restrictions.ge(ScoContrato.Fields.DT_INICIO_VIGENCIA.toString(), filtro.getContrato()
					.getDataIniVigComAditivos()));
		}

		if (filtro.getContrato().getDataFimVigComAditivos() != null) {
			criteria.add(Restrictions.le(ScoContrato.Fields.DT_FIM_VIGENCIA.toString(), filtro.getContrato()
					.getDataFimVigComAditivos()));
		}

		if (filtro.getContrato().getLicitacao() != null) {
			DetachedCriteria licCrit = criteria.createCriteria(ScoContrato.Fields.LICITACAO.toString());

			licCrit.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), filtro.getContrato().getLicitacao().getNumero()));
		}

		if (filtro.getContrato().getTipoContratoSicon() != null) {
			DetachedCriteria tipoContCrit = criteria.createCriteria(ScoContrato.Fields.TIPO_CONTRATO_SICON.toString());

			tipoContCrit.add(Restrictions.eq(ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(), filtro.getContrato()
					.getTipoContratoSicon().getCodigoSicon()));
		}

		if (filtro.getContrato().getFornecedor() != null) {

			fornCrit.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), filtro.getContrato().getFornecedor().getNumero()));
		}

		if (filtro.getAf().getNumero() != null && filtro.getAf().getNroComplemento() != null) {

			afContratoCrit.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getAf()
					.getNroComplemento()));

			DetachedCriteria propCrit = afContratoCrit.createCriteria(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString());

			DetachedCriteria licCrit = propCrit.createCriteria(ScoPropostaFornecedor.Fields.LICITACAO.toString());

			licCrit.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), filtro.getAf().getNumero()));

		}

		if (filtro.getContrato().getServidorGestor() != null) {
			DetachedCriteria criteriaGestorContrato = criteria.createCriteria(ScoContrato.Fields.SERVIDOR_GESTOR.toString());

			criteriaGestorContrato.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), filtro.getContrato()
					.getServidorGestor().getId().getMatricula()));
		}

		if (filtro.getContrato().getServidorFiscal() != null) {
			DetachedCriteria criteriaFiscalContrato = criteria.createCriteria(ScoContrato.Fields.SERVIDOR_FISCAL.toString());

			criteriaFiscalContrato.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), filtro.getContrato()
					.getServidorFiscal().getId().getMatricula()));
		}

		criteria.addOrder(Order.asc(ScoContrato.Fields.IND_ORIGEM.toString()));
		criteria.addOrder(Order.asc(ScoContrato.Fields.CRIADO_EM.toString()));

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		result = executeCriteria(criteria);

		return result;
	}

	public List<ScoContrato> listarContratoByNroOuDescricao(Object paramPesquisa) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class, "c");

		if (paramPesquisa != null) {
			String srtPesquisa = (String) paramPesquisa;
			if (StringUtils.isNotBlank(srtPesquisa)) {
				if (CoreUtil.isNumeroInteger(paramPesquisa)) {
					criteria.add(Restrictions.eq("c." + ScoContrato.Fields.NR_CONTRATO.toString(), Long.valueOf(srtPesquisa)));
				} else {
					criteria.add(Restrictions.ilike("c." + ScoContrato.Fields.OBJETO_CONTRATO.toString(), srtPesquisa,
							MatchMode.ANYWHERE));
				}
			}
		}

		criteria.add(Subqueries.notExists(DetachedCriteria
				.forClass(ScoResContrato.class, "rc")
				.setProjection(Projections.property("rc." + ScoResContrato.Fields.SEQ.toString()))
				.add(Restrictions.eqProperty(
						"rc." + ScoResContrato.Fields.CONT_SEQ.toString() + "." + ScoContrato.Fields.SEQ.toString(), "c."
								+ ScoContrato.Fields.SEQ.toString()))));

		// Restrição para exibir apenas contratos automáticos.
		criteria.add(Restrictions.eq("c." + ScoContrato.Fields.IND_ORIGEM.toString(), DominioOrigemContrato.A));

		return this.executeCriteria(criteria);
	}

	public List<Integer> obterConsultaLicitacoesVO(LicitacaoFiltroVO filtro, DominioModalidadeEmpenho param) {
		StringBuilder hql = new StringBuilder(300);
		hql.append("SELECT lct." ).append( ScoLicitacao.Fields.NUMERO.toString())
		.append(" FROM " ).append( ScoLicitacao.class.getSimpleName() ).append( " lct, ")
		.append(ScoItemLicitacao.class.getSimpleName() ).append( " itl, ")
		.append(ScoFaseSolicitacao.class.getSimpleName() ).append( " fsc ")
		.append(" WHERE lct." ).append( ScoLicitacao.Fields.IND_EXCLUSAO.toString() ).append( " =:EXCLUSAO ")
		.append(" AND lct." ).append( ScoLicitacao.Fields.MODALIDADE_EMPENHO.toString() ).append( " =:AFN_MOD_EMPENHO_PARAM ")
		.append(" AND lct." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( " = itl."
				).append( ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString() ).append(' ')
		.append(" AND itl." ).append( ScoItemLicitacao.Fields.IND_EXCLUSAO.toString() ).append( " =:EXCLUSAO ")
		.append(" AND ((itl." ).append( ScoItemLicitacao.Fields.IND_EM_AF.toString() ).append( " is null ) or (itl."
				).append( ScoItemLicitacao.Fields.IND_EM_AF.toString() ).append( " is not null and itl."
				).append( ScoItemLicitacao.Fields.IND_EM_AF.toString() ).append( " = :ITL_EM_AF ))")
		.append(" AND fsc." ).append( ScoFaseSolicitacao.Fields.LCT_NUMERO.toString() ).append( " = lct."
				).append( ScoLicitacao.Fields.NUMERO.toString() ).append(' ')
		.append(" AND fsc." ).append( ScoFaseSolicitacao.Fields.ITL_NUMERO.toString() ).append( " = itl."
				).append( ScoItemLicitacao.Fields.NUMERO.toString() ).append(' ')
		.append(" AND fsc." ).append( ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString() ).append( " =:EXCLUSAO ");

		montarFiltros1(filtro, hql);
		montarFiltros2(filtro, hql);

		hql.append(" GROUP BY lct." ).append( ScoLicitacao.Fields.NUMERO.toString());

		Query q = createHibernateQuery(hql.toString());
		q.setParameter("AFN_MOD_EMPENHO_PARAM", param);
		q.setParameter("EXCLUSAO", Boolean.FALSE);
		q.setParameter("ITL_EM_AF", Boolean.FALSE);

		return q.list();
	}

	private void montarFiltros1(LicitacaoFiltroVO filtro, StringBuilder hql) {
		// Licitação
		if ((filtro.getLicitacao() != null) && (filtro.getLicitacao().getNumero() != null)) {
			hql.append(" AND lct." ).append( ScoLicitacao.Fields.NUMERO.toString() ).append( " = " ).append( filtro.getLicitacao().getNumero().toString()
				).append(' ');
		}

		// Filtro de Fornecedor e AF não se aplica nesta consulta

		// Filtro de Tipo de Serviço: serviço
		if (DominioTipoItemContrato.S.equals(filtro.getTipoItens())) {
			hql.append(" AND EXISTS (");
			hql.append(" SELECT 1 FROM " ).append( ScoSolicitacaoServico.class.getSimpleName() ).append( " sls");
			hql.append(" WHERE sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString() ).append(' ');
			hql.append(')');
		}
		// Filtro de Tipo de Serviço: material
		if (DominioTipoItemContrato.M.equals(filtro.getTipoItens())) {
			hql.append(" AND EXISTS (");
			hql.append(" SELECT 1 FROM " ).append( ScoSolicitacaoDeCompra.class.getSimpleName() ).append( " slc");
			hql.append(" WHERE slc." ).append( ScoSolicitacaoDeCompra.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append(' ');
			hql.append(')');
		}
		// Filtro de De Para Siasg: pendente/existente
		if (DominioPossuiSIASG.P.equals(filtro.getCodSiasg()) || DominioPossuiSIASG.E.equals(filtro.getCodSiasg())) {
			StringBuilder hqlAuxSrv = new StringBuilder();
			hqlAuxSrv.append(" SELECT 1 FROM ");
			hqlAuxSrv.append(ScoServicoSicon.class.getSimpleName() ).append( " srvs, ");
			hqlAuxSrv.append(ScoServico.class.getSimpleName() ).append( " srv, ");
			hqlAuxSrv.append(ScoSolicitacaoServico.class.getSimpleName() ).append( " sls ");
			hqlAuxSrv.append(" WHERE srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = " ).append( " sls."
					).append( ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString() ).append(' ');
			hqlAuxSrv.append(" AND sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString() ).append(' ');
			hqlAuxSrv.append(" AND srvs." ).append( ScoServicoSicon.Fields.SERVICO.toString() ).append( " = " ).append( " srv."
					).append( ScoServico.Fields.CODIGO.toString() ).append(' ');
			hqlAuxSrv.append(" AND srvs." ).append( ScoServicoSicon.Fields.SITUACAO.toString() ).append( " = 'A' ");

			StringBuilder hqlAuxMat = new StringBuilder();
			hqlAuxMat.append(" SELECT 1 FROM ");
			hqlAuxMat.append(ScoMaterialSicon.class.getSimpleName() ).append( " mats, ");
			hqlAuxMat.append(ScoMaterial.class.getSimpleName() ).append( " mat, ");
			hqlAuxMat.append(ScoSolicitacaoDeCompra.class.getSimpleName() ).append( " slc ");
			hqlAuxMat.append(" WHERE mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = " ).append( " slc."
					).append( ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString() ).append(' ');
			hqlAuxMat.append(" AND slc." ).append( ScoSolicitacaoDeCompra.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append(' ');
			hqlAuxMat.append(" AND mats." ).append( ScoMaterialSicon.Fields.MATERIAL.toString() ).append( " = " ).append( " mat."
					).append( ScoMaterial.Fields.CODIGO.toString() ).append(' ');
			hqlAuxMat.append(" AND mats." ).append( ScoMaterialSicon.Fields.SITUACAO.toString() ).append( " = 'A' ");

			// Filtro de De Para Siasg: pendente
			if (DominioPossuiSIASG.P.equals(filtro.getCodSiasg())) {
				hql.append(" AND  (NOT EXISTS (" + hqlAuxSrv + ") " + " OR NOT EXISTS (" + hqlAuxMat + "))");
			}
			// Filtro de De Para Siasg: existente
			if (DominioPossuiSIASG.E.equals(filtro.getCodSiasg())) {
				hql.append(" AND (EXISTS (" + hqlAuxSrv + ") " + " OR EXISTS (" + hqlAuxMat + "))");
			}
		}
	}

	private void montarFiltros2(LicitacaoFiltroVO filtro, StringBuilder hql) {
		// Filtro de Grupo de Serviço
		if (filtro.getGrupoServico() != null) {
			hql.append(" AND (EXISTS (")
			.append(" SELECT 1 FROM ")
			.append(ScoServico.class.getSimpleName() ).append( " srv, ")
			.append(ScoSolicitacaoServico.class.getSimpleName() ).append( " sls ")
			.append(" WHERE srv." ).append( ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString() ).append( " = "
					).append( filtro.getGrupoServico().getCodigo() ).append(' ')
			.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = " ).append( " sls."
					).append( ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString() ).append(' ')
			.append(" AND sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString() ).append(' ')
			.append("))");
		}
		// Filtro de Serviço
		if (filtro.getServico() != null) {
			hql.append(" AND (EXISTS (")
			.append(" SELECT 1 FROM ")
			.append(ScoServico.class.getSimpleName() ).append( " srv, ")
			.append(ScoSolicitacaoServico.class.getSimpleName() ).append( " sls ")
			.append(" WHERE srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = " ).append( filtro.getServico().getCodigo() ).append(' ')
			.append(" AND srv." ).append( ScoServico.Fields.CODIGO.toString() ).append( " = " ).append( " sls."
					).append( ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString() ).append(' ')
			.append(" AND sls." ).append( ScoSolicitacaoServico.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLS_NUMERO.toString() ).append(' ')
			.append("))");
		}
		// Filtro de Grupo de Material
		if (filtro.getGrupoMaterial() != null) {
			hql.append(" AND (EXISTS (")
			.append(" SELECT 1 FROM ")
			.append(ScoMaterial.class.getSimpleName() ).append( " mat, ")
			.append(ScoSolicitacaoDeCompra.class.getSimpleName() ).append( " slc ")
			.append(" WHERE mat." ).append( ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString() ).append( " = "
					).append( filtro.getGrupoMaterial().getCodigo() ).append(' ')
			.append(" AND mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = " ).append( " slc."
					).append( ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString() ).append(' ')
			.append(" AND slc." ).append( ScoSolicitacaoDeCompra.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append(' ')
			.append("))");
		}
		// Filtro de Material
		if (filtro.getMaterial() != null) {
			hql.append(" AND (EXISTS (")
			.append(" SELECT 1 FROM ")
			.append(ScoMaterial.class.getSimpleName() ).append( " mat, ")
			.append(ScoSolicitacaoDeCompra.class.getSimpleName() ).append( " slc ")
			.append(" WHERE mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = " ).append( filtro.getMaterial().getCodigo() ).append(' ')
			.append(" AND mat." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( " = " ).append( " slc."
					).append( ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString() ).append( ' ')
			.append(" AND slc." ).append( ScoSolicitacaoDeCompra.Fields.NUMERO.toString() ).append( " = " ).append( " fsc."
					).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append(' ')
			.append("))");
		}
	}
	
	public Long materiaisSiconPendentesAutomaticoCount(ScoContrato contrato){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);
		criteria.add(Restrictions.eq(ScoContrato.Fields.SEQ.toString(), contrato.getSeq()));

		DetachedCriteria materialSiconCriteria = criteria.createCriteria(ScoContrato.Fields.AFS.toString())
				.createCriteria(ScoAfContrato.Fields.AUT_FORN.toString())
				.createCriteria(ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString())
				.createCriteria(ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString())
				.createCriteria(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString())
				.createCriteria(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString())
				.createCriteria(ScoMaterial.Fields.MATERIAL_SICON.toString());
		materialSiconCriteria.add(//
				Restrictions.or(
						Restrictions.isNull(
								ScoMaterialSicon.Fields.CODIGO_SICON.toString()), //
						Restrictions.eq(
								ScoMaterialSicon.Fields.SITUACAO.toString(),
								DominioSituacao.I)));

		return executeCriteriaCount(materialSiconCriteria);
	}
	
	public Long materiaisSiconPendentesManualCount(ScoContrato contrato){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);
		criteria.add(Restrictions.eq(ScoContrato.Fields.SEQ.toString(), contrato.getSeq()));

		DetachedCriteria materialSiconCriteria = criteria.createCriteria(ScoContrato.Fields.ITENS_CONT.toString())
				.createCriteria(ScoItensContrato.Fields.MATERIAL.toString())
				.createCriteria(ScoMaterial.Fields.MATERIAL_SICON.toString());
		materialSiconCriteria.add(//
				Restrictions.or(
						Restrictions.isNull(
								ScoMaterialSicon.Fields.CODIGO_SICON.toString()), //
						Restrictions.eq(
								ScoMaterialSicon.Fields.SITUACAO.toString(),
								DominioSituacao.I)));

		return executeCriteriaCount(materialSiconCriteria);
	}
	
	public Long servicosSiconPendentesAutomaticoCount(ScoContrato contrato){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);
		criteria.add(Restrictions.eq(ScoContrato.Fields.SEQ.toString(), contrato.getSeq()));

		DetachedCriteria servicoSiconCriteria = criteria.createCriteria(ScoContrato.Fields.AFS.toString())
				.createCriteria(ScoAfContrato.Fields.AUT_FORN.toString())
				.createCriteria(ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString())
				.createCriteria(ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString())
				.createCriteria(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString())
				.createCriteria(ScoSolicitacaoServico.Fields.SERVICO.toString())
				.createCriteria(ScoServico.Fields.SERVICO_SICON.toString());
		servicoSiconCriteria.add(//
				Restrictions.or(
						Restrictions.isNull(
								ScoServicoSicon.Fields.CODIGO_SICON.toString()), //
						Restrictions.eq(
								ScoServicoSicon.Fields.SITUACAO.toString(),
								DominioSituacao.I)));

		return executeCriteriaCount(servicoSiconCriteria);
	}
	
	public Long servicosSiconPendentesManualCount(ScoContrato contrato){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoContrato.class);
		criteria.add(Restrictions.eq(ScoContrato.Fields.SEQ.toString(), contrato.getSeq()));

		DetachedCriteria servicoSiconCriteria = criteria.createCriteria(ScoContrato.Fields.ITENS_CONT.toString())
				.createCriteria(ScoItensContrato.Fields.SERVICO.toString())
				.createCriteria(ScoServico.Fields.SERVICO_SICON.toString());
		servicoSiconCriteria.add(//
				Restrictions.or(
						Restrictions.isNull(
								ScoServicoSicon.Fields.CODIGO_SICON.toString()), //
						Restrictions.eq(
								ScoServicoSicon.Fields.SITUACAO.toString(),
								DominioSituacao.I)));

		return executeCriteriaCount(servicoSiconCriteria);
	}
}
