package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ShortType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.dominio.DominoOrigemMapaAmostraItemExame;
import br.gov.mec.aghu.exames.vo.RelatorioMapaBioquimicaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaEpfVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHematologiaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaHemoculturaVO;
import br.gov.mec.aghu.exames.vo.RelatorioMapaUroculturaVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelConfigMapa;
import br.gov.mec.aghu.model.AelConfigMapaExames;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.utils.DateUtil;

public class AelConfigMapaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelConfigMapa> {

	private static final long serialVersionUID = 6579896419522381965L;

	public List<AelConfigMapa> pesquisarAelConfigMapaPorPrioridadeUnidadeFederativa( final AghUnidadesFuncionais unidadeFuncional, final String mapa, 
																					 final DominoOrigemMapaAmostraItemExame origem, final List<Short> unfs, 
																					 final Integer firstResult, final Integer maxResult, 
																					 final String orderProperty, final boolean asc){
		
		final DetachedCriteria criteria = obterCriteriaAelConfigMapaPorPrioridadeUnidadeFederativa(unidadeFuncional, mapa, origem, unfs);
		criteria.addOrder(Order.asc(AelConfigMapa.Fields.NOME_MAPA.toString()));
	
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
		
	public Long pesquisarAelConfigMapaPorPrioridadeUnidadeFederativaCount( final AghUnidadesFuncionais unidadeFuncional, final String mapa, 
			  final DominoOrigemMapaAmostraItemExame origem, final List<Short> unfs){
		return executeCriteriaCount(obterCriteriaAelConfigMapaPorPrioridadeUnidadeFederativa(unidadeFuncional, mapa, origem, unfs));
	}
		
	private DetachedCriteria obterCriteriaAelConfigMapaPorPrioridadeUnidadeFederativa(
		final AghUnidadesFuncionais unidadeFuncional, final String mapa, final DominoOrigemMapaAmostraItemExame origem, final List<Short> unfs) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigMapa.class);
		
		criteria.createAlias(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelConfigMapa.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if(mapa != null){
			criteria.add(Restrictions.ilike(AelConfigMapa.Fields.NOME_MAPA.toString(), mapa, MatchMode.ANYWHERE));
		}
		
		if(origem != null){
			criteria.add(Restrictions.eq(AelConfigMapa.Fields.ORIGEM.toString(), origem));
		}
		
		if(unfs.isEmpty()){
			criteria.add(Restrictions.eq(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), unidadeFuncional));
		
		} else {
			criteria.add(Restrictions.or(
										  Restrictions.eq(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), unidadeFuncional), 
										  Restrictions.in(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS_SEQ.toString(), unfs)
										)
						);
		}
		 
		return criteria;
	}

	public List<AelConfigMapa> pesquisarAelConfigMapa(final AelConfigMapa filtros){
		final DetachedCriteria criteria = obterCriteria(filtros);
		criteria.addOrder(Order.asc(AelConfigMapa.Fields.NOME_MAPA.toString()));
		criteria.createAlias(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias(AelConfigMapa.Fields.RAP_SERVIDORES.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		return executeCriteria(criteria); 
	}
	
	private DetachedCriteria obterCriteria(final AelConfigMapa filtros) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigMapa.class);
		
		if(filtros != null){
			
			if(filtros.getSeq() != null){
				criteria.add(Restrictions.eq(AelConfigMapa.Fields.SEQ.toString(), filtros.getSeq()));
			}
			
			if(filtros.getNomeMapa() != null){
				criteria.add(Restrictions.ilike(AelConfigMapa.Fields.NOME_MAPA.toString(), filtros.getNomeMapa(), MatchMode.ANYWHERE));
			}
			
			if(filtros.getIndSituacao() != null){
				criteria.add(Restrictions.eq(AelConfigMapa.Fields.IND_SITUACAO.toString(), filtros.getIndSituacao()));
			}
			
			if(filtros.getIndEmite() != null){
				criteria.add(Restrictions.eq(AelConfigMapa.Fields.IND_EMITE.toString(), filtros.getIndEmite()));
			}
			
			if(filtros.getOrigem() != null){
				criteria.add(Restrictions.eq(AelConfigMapa.Fields.ORIGEM.toString(), filtros.getOrigem()));
			}
			
			if(filtros.getAghUnidadesFuncionais() != null){
				criteria.add(Restrictions.eq(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), filtros.getAghUnidadesFuncionais()));
			}
		}
		
		return criteria;
	}
	
	public List<RelatorioMapaBioquimicaVO> obterMapasBioquimicaVo( final Integer nroMapa, final DominoOrigemMapaAmostraItemExame origem, 
																   final Date dtImplantacao, final Short cgmSeq, final Short unfSeq){

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class, "AMO");
		criteria.createAlias("AMO."+AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias("AIE."+AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		criteria.createAlias("AMO."+AelAmostras.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		criteria.createAlias("AMO."+AelAmostras.Fields.ANTICOAGULANTE.toString(), "ATC", Criteria.LEFT_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("AMO."+AelAmostras.Fields.NRO_UNICO.toString()),RelatorioMapaBioquimicaVO.Fields.NRO_INTERNO.toString())
				.add(Projections.property("AMO."+AelAmostras.Fields.SOE_SEQ.toString()),RelatorioMapaBioquimicaVO.Fields.SOE_SEQ.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString()),RelatorioMapaBioquimicaVO.Fields.NRO_MAPA.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()),RelatorioMapaBioquimicaVO.Fields.AMO_SOE_SEQ.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.AMO_SEQP.toString()),RelatorioMapaBioquimicaVO.Fields.AMO_SEQP.toString())
				.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
													"	from   	agh.ael_extrato_item_solics eis" +
													"	where	eis.ise_soe_seq = ise2_.soe_seq" +
													"	and     eis.ise_seqp = ise2_.seqp" +
													"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
													new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), RelatorioMapaBioquimicaVO.Fields.DTHR_EVENTO.toString())
				
				.add(Projections.property("MAN."+AelMateriaisAnalises.Fields.DESCRICAO.toString()),RelatorioMapaBioquimicaVO.Fields.MATERIAL.toString())
				.add(Projections.property("ATC."+AelAnticoagulante.Fields.DESCRICAO.toString()),RelatorioMapaBioquimicaVO.Fields.ANTICOAGULANTE.toString())
													)
							   );
		
		// Parâmetro do numero do mapa (ou gerada ou informada)
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString(), nroMapa));
		
		// Parâmetro da origem do mapa
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.ORIGEM_MAPA.toString(), origem));
		
		criteria.add(Restrictions.ge("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), dtImplantacao));
		criteria.add(Restrictions.lt("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), DateUtil.adicionaDias(dtImplantacao, 1)));
		
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.CGM_SEQ.toString(), cgmSeq));
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.addOrder(Order.asc(RelatorioMapaBioquimicaVO.Fields.DTHR_EVENTO.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaBioquimicaVO.Fields.NRO_INTERNO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMapaBioquimicaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioMapaHemoculturaVO> obterMapasHemoculturaVO( final Integer nroMapa, final DominoOrigemMapaAmostraItemExame origem, 
			   final Date dtImplantacao, final Short cgmSeq, final Short unfSeq){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class, "AMO");
		criteria.createAlias("AMO." + AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		
		//projection
		criteria.setProjection(Projections.distinct(Projections.projectionList()
			.add(Projections.property("AMO." + AelAmostras.Fields.NRO_UNICO.toString()), RelatorioMapaHemoculturaVO.Fields.NRO_INTERNO.toString())			
			.add(Projections.property("AMO." + AelAmostras.Fields.NRO_FRASCO_FABRICANTE.toString()), RelatorioMapaHemoculturaVO.Fields.NRO_FRASCO_FABRICANTE.toString())
			
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()), RelatorioMapaHemoculturaVO.Fields.AMO_SOE_SEQ.toString())
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString()), RelatorioMapaHemoculturaVO.Fields.AMO_SEQP.toString())
			
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()), RelatorioMapaHemoculturaVO.Fields.EXAME_SIGLA.toString())
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), RelatorioMapaHemoculturaVO.Fields.ISE_SOE_SEQ.toString())
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), RelatorioMapaHemoculturaVO.Fields.ISE_SEQP.toString())
			
			.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
					"	from   	agh.ael_extrato_item_solics eis" +
					"	where	eis.ise_soe_seq = ise2_.soe_seq" +
					"	and     eis.ise_seqp = ise2_.seqp" +
					"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
					new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), RelatorioMapaHemoculturaVO.Fields.DTHR_EVENTO.toString())
			
			)
		);
		
		//where
		// Parâmetro do numero do mapa (ou gerada ou informada)
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString(), nroMapa));
		
		// Parâmetro da origem do mapa
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.ORIGEM_MAPA.toString(), origem));
		
		criteria.add(Restrictions.ge("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), dtImplantacao));
		criteria.add(Restrictions.lt("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), DateUtil.adicionaDias(dtImplantacao, 1)));
		
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.CGM_SEQ.toString(), cgmSeq));
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.addOrder(Order.asc(RelatorioMapaHemoculturaVO.Fields.DTHR_EVENTO.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaHemoculturaVO.Fields.NRO_INTERNO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMapaHemoculturaVO.class));
		
		return executeCriteria(criteria);
	}

	public List<RelatorioMapaEpfVO> obterMapasEpfVO( final Integer nroMapa, final DominoOrigemMapaAmostraItemExame origem, 
			   final Date dtImplantacao, final Short cgmSeq, final Short unfSeq){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class, "AMO");
		criteria.createAlias("AMO." + AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		
		//projection
		criteria.setProjection(Projections.distinct(Projections.projectionList()
			.add(Projections.property("AMO." + AelAmostras.Fields.NRO_UNICO.toString()), RelatorioMapaEpfVO.Fields.NRO_INTERNO.toString())			
			.add(Projections.property("AMO." + AelAmostras.Fields.NRO_FRASCO_FABRICANTE.toString()), RelatorioMapaEpfVO.Fields.NRO_FRASCO_FABRICANTE.toString())
			
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()), RelatorioMapaEpfVO.Fields.AMO_SOE_SEQ.toString())
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString()), RelatorioMapaEpfVO.Fields.AMO_SEQP.toString())
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.NRO_MAPA.toString()), RelatorioMapaEpfVO.Fields.NRO_MAPA.toString())
			
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()), RelatorioMapaEpfVO.Fields.EXAME_SIGLA.toString())
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), RelatorioMapaEpfVO.Fields.ISE_SOE_SEQ.toString())
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), RelatorioMapaEpfVO.Fields.ISE_SEQP.toString())
			
			.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
					"	from   	agh.ael_extrato_item_solics eis" +
					"	where	eis.ise_soe_seq = ise2_.soe_seq" +
					"	and     eis.ise_seqp = ise2_.seqp" +
					"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
					new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), RelatorioMapaEpfVO.Fields.DTHR_EVENTO.toString())
			
			)
		);
		
		//where
		// Parâmetro do numero do mapa (ou gerada ou informada)
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString(), nroMapa));
		
		// Parâmetro da origem do mapa
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.ORIGEM_MAPA.toString(), origem));
		
		criteria.add(Restrictions.ge("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), dtImplantacao));
		criteria.add(Restrictions.lt("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), DateUtil.adicionaDias(dtImplantacao, 1)));
		
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.CGM_SEQ.toString(), cgmSeq));
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.addOrder(Order.asc(RelatorioMapaEpfVO.Fields.DTHR_EVENTO.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaEpfVO.Fields.NRO_INTERNO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMapaEpfVO.class));
		
		return executeCriteria(criteria);
	}

	public List<RelatorioMapaUroculturaVO> obterMapasUroculturaVO( final Integer nroMapa, final DominoOrigemMapaAmostraItemExame origem, 
			   final Date dtImplantacao, final Short cgmSeq, final Short unfSeq){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostras.class, "AMO");
		criteria.createAlias("AMO." + AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		//projection
		criteria.setProjection(Projections.distinct(Projections.projectionList()
			.add(Projections.property("AMO." + AelAmostras.Fields.NRO_UNICO.toString()), RelatorioMapaUroculturaVO.Fields.NRO_INTERNO.toString())			
			
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()), RelatorioMapaUroculturaVO.Fields.AMO_SOE_SEQ.toString())
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.AMO_SEQP.toString()), RelatorioMapaUroculturaVO.Fields.AMO_SEQP.toString())
			.add(Projections.property("AIE." + AelAmostraItemExames.Fields.NRO_MAPA.toString()), RelatorioMapaUroculturaVO.Fields.NRO_MAPA.toString())
			
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()), RelatorioMapaUroculturaVO.Fields.ISE_SOE_SEQ.toString())
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()), RelatorioMapaUroculturaVO.Fields.ISE_SEQP.toString())
			
			.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()), RelatorioMapaUroculturaVO.Fields.UFE_EMA_MAN_SEQ.toString())
			
			.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString()), RelatorioMapaUroculturaVO.Fields.INFORMACOES_CLINICAS.toString())
			
			.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
					"	from   	agh.ael_extrato_item_solics eis" +
					"	where	eis.ise_soe_seq = ise2_.soe_seq" +
					"	and     eis.ise_seqp = ise2_.seqp" +
					"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
					new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), RelatorioMapaUroculturaVO.Fields.DTHR_EVENTO.toString())
			
			)
		);
		
		//where
		// Parâmetro do numero do mapa (ou gerada ou informada)
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString(), nroMapa));
		
		// Parâmetro da origem do mapa
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.ORIGEM_MAPA.toString(), origem));
		
		criteria.add(Restrictions.ge("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), dtImplantacao));
		criteria.add(Restrictions.lt("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), DateUtil.adicionaDias(dtImplantacao, 1)));
		
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.CGM_SEQ.toString(), cgmSeq));
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.addOrder(Order.asc(RelatorioMapaUroculturaVO.Fields.DTHR_EVENTO.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaUroculturaVO.Fields.NRO_INTERNO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMapaUroculturaVO.class));
		
		return executeCriteria(criteria);
	}	
	
	public List<RelatorioMapaBioquimicaVO> cAtuMapa( final AelConfigMapa mapa, final String cSitCodigo, final Short cguSeq ){

//		final DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigMapaExames.class, "CGE");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("AMO."+AelAmostras.Fields.SOE_SEQ.toString()),RelatorioMapaBioquimicaVO.Fields.AMO_SOE_SEQ.toString())
				.add(Projections.property("AMO."+AelAmostras.Fields.SEQP.toString()),RelatorioMapaBioquimicaVO.Fields.AMO_SEQP.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.SOE_SEQ.toString()),RelatorioMapaBioquimicaVO.Fields.ISE_SOE_SEQ.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.SEQP.toString()),RelatorioMapaBioquimicaVO.Fields.ISE_SEQP.toString())
				.add(Projections.property("SOE."+AelSolicitacaoExames.Fields.UNF_SEQ.toString()),RelatorioMapaBioquimicaVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("ISE."+AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()),RelatorioMapaBioquimicaVO.Fields.TIPO_COLETA.toString())
							  );
		
		// AND cge.ufe_ema_exa_sigla = ise.ufe_ema_exa_sigla
		// AND cge.ufe_ema_man_seq   = ise.ufe_ema_man_seq
		// AND cge.ufe_unf_seq       = ise.ufe_unf_seq
		criteria.createAlias("CGE."+AelConfigMapaExames.Fields.AEL_UNF_EXECUTA_EXAMES.toString(), "UEE");
		criteria.createAlias("UEE."+AelUnfExecutaExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		
		// AND aie.ise_soe_seq  = ise.soe_seq
		// AND aie.ise_seqp     = ise.seqp
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		
		// AND soe.seq = ise.soe_seq
		criteria.createAlias("ISE."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		
		// AND amo.soe_seq  = aie.amo_soe_seq
		// AND amo.seqp     = aie.amo_seqp
		criteria.createAlias("AIE."+AelAmostraItemExames.Fields.AEL_AMOSTRAS.toString(), "AMO");
		
		//AND cge.cgm_seq +0 = c_cgm_seq
		criteria.add(Restrictions.eq("CGE."+AelConfigMapaExames.Fields.CONFIG_MAPA.toString(), mapa));
		
		// AND cge.ind_situacao = 'A'
		criteria.add(Restrictions.eq("CGE."+AelConfigMapaExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		// AND ise.sit_codigo = c_sit_codigo
		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), cSitCodigo));
		
		// AND ise.ufe_unf_seq+0                                       = c_ufe_unf_seq
		criteria.add(Restrictions.eq("ISE."+AelItemSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), mapa.getAghUnidadesFuncionais()));
		
		// AND aie.nro_mapa IS NULL
		criteria.add(Restrictions.isNull("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString()));
		
		// AND aie.situacao = 'R'
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.SITUACAO.toString(),DominioSituacaoAmostra.R));
		
		// AND NVL(amo.cgu_seq,0)+0 = DECODE(c_cgu_seq,NULL,NVL(amo.cgu_seq,0)+0,c_cgu_seq)
		if(cguSeq != null){
			criteria.add(
						Restrictions.sqlRestriction(" CASE WHEN AMO5_.CGU_SEQ is null THEN 0 " + //AelAmostras.Fields.CGU_SEQ.toString() + 
												    " ELSE AMO5_.CGU_SEQ END = ? "  /* + AelAmostras.Fields.CGU_SEQ.toString()*/ 
												    ,cguSeq,ShortType.INSTANCE
												   )
					    );
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMapaBioquimicaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<RelatorioMapaHematologiaVO> obterMapasHematologiaVO(final Integer nroMapa, final DominoOrigemMapaAmostraItemExame origem, 
			   final Date dtImplantacao, final Short cgmSeq, final Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.AMOSTRAS.toString(), "AMO");
		criteria.createAlias("AMO." + AelAmostras.Fields.AMOSTRA_ITEM_EXAMES.toString(), "AIE");
		criteria.createAlias("AIE." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");

		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("AMO."+AelAmostras.Fields.NRO_UNICO.toString()),RelatorioMapaHematologiaVO.Fields.NRO_INTERNO.toString())
				.add(Projections.property("AMO."+AelAmostras.Fields.SOE_SEQ.toString()),RelatorioMapaHematologiaVO.Fields.SOE_SEQ.toString())
				.add(Projections.property("AMO."+AelAmostras.Fields.SEQP.toString()),RelatorioMapaHematologiaVO.Fields.AMO_SEQP.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString()),RelatorioMapaHematologiaVO.Fields.NRO_MAPA.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString()),RelatorioMapaHematologiaVO.Fields.AMO_SOE_SEQ.toString())
				.add(Projections.property("AIE."+AelAmostraItemExames.Fields.AMO_SEQP.toString()),RelatorioMapaHematologiaVO.Fields.AMO_SEQP.toString())
				.add(Projections.property("EXA."+AelExames.Fields.DESCRICAO_USUAL.toString()),RelatorioMapaHematologiaVO.Fields.EXAME.toString())
				.add(Projections.property("SOE."+AelSolicitacaoExames.Fields.INFORMACOES_CLINICAS.toString()),RelatorioMapaHematologiaVO.Fields.INFORMACOES_CLINICAS.toString())
				.add(Projections.property("ISE."+AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString()),RelatorioMapaHematologiaVO.Fields.TIPO_COLETA.toString())
				.add(Projections.sqlProjection("(	select 	max(eis.dthr_evento)" +
													"	from   	agh.ael_extrato_item_solics eis" +
													"	where	eis.ise_soe_seq = ise4_.soe_seq" +
													"	and     eis.ise_seqp = ise4_.seqp" +
													"	and     substr(eis.sit_codigo,1,2) = 'AE') as dthr_evento", 
													new String[] { "dthr_evento" },new Type[] { TimestampType.INSTANCE}), RelatorioMapaHematologiaVO.Fields.DTHR_EVENTO.toString())
				
				.add(Projections.property("MAN."+AelMateriaisAnalises.Fields.DESCRICAO.toString()),RelatorioMapaHematologiaVO.Fields.MATERIAL.toString())
													)
							   );

		// Parâmetro do numero do mapa (ou gerada ou informada)
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.NRO_MAPA.toString(), nroMapa));
		
		// Parâmetro da origem do mapa
		criteria.add(Restrictions.eq("AIE."+AelAmostraItemExames.Fields.ORIGEM_MAPA.toString(), origem));
		
		criteria.add(Restrictions.between("AIE."+AelAmostraItemExames.Fields.DT_IMP_MAPA.toString(), dtImplantacao, DateUtil.adicionaDias(dtImplantacao, 1)));
		
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.CGM_SEQ.toString(), cgmSeq));
		criteria.add(Restrictions.eq("AMO."+AelAmostras.Fields.UNF_SEQ.toString(), unfSeq));
		
		criteria.addOrder(Order.asc(RelatorioMapaHematologiaVO.Fields.DTHR_EVENTO.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaHematologiaVO.Fields.SOE_SEQ.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaHematologiaVO.Fields.NRO_INTERNO.toString()));
		criteria.addOrder(Order.asc(RelatorioMapaHematologiaVO.Fields.MATERIAL.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioMapaHematologiaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public AelConfigMapa obterAelConfigMapaPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigMapa.class);
		criteria.add(Restrictions.eq(AelConfigMapa.Fields.SEQ.toString(), seq));
		criteria.setFetchMode(AelConfigMapa.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), FetchMode.JOIN);
		return (AelConfigMapa) executeCriteriaUniqueResult(criteria);
	}
}