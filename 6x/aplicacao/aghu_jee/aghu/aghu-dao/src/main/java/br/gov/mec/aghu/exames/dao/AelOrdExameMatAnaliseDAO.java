package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.model.AelOrdExameMatAnaliseId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;

/**
 * 
 * @author lalegre
 *
 */
public class AelOrdExameMatAnaliseDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelOrdExameMatAnalise> {
	
	private static final long serialVersionUID = 5287535669670723710L;

	@Override
	protected void obterValorSequencialId(AelOrdExameMatAnalise elemento) {
				
		if (elemento.getExamesMaterialAnalise() == null) {
			throw new IllegalArgumentException("Exames nao esta associado corretamente.");
		}
		
		AelOrdExameMatAnaliseId id = new AelOrdExameMatAnaliseId();
		id.setEmaExaSigla(elemento.getExamesMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExamesMaterialAnalise().getId().getManSeq());
		elemento.setId(id);

	}
	
	public void removerAelOrdExameMatAnalisePorMaterial(AelExamesMaterialAnaliseId exaManId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelOrdExameMatAnalise.class);
		criteria.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.EMA_EXA_SIGLA.toString(), exaManId.getExaSigla()));
		criteria.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.EMA_MAN_SEQ.toString(), exaManId.getManSeq()));
		AelOrdExameMatAnalise aelOrdExameMatAnalise = (AelOrdExameMatAnalise) this.executeCriteriaUniqueResult(criteria);
		
		if(aelOrdExameMatAnalise != null){
			this.remover(aelOrdExameMatAnalise);
		}
	}
	
	/**
	 * Obtém dados de examos em ordem cronológica
	 * @param codPaciente
	 * @param paramSitCodLiberado
	 * @param paramSitCodAreaExec
	 * @return
	 * @author bruno.mourao
	 * @since 14/02/2012
	 */
	public List<ExameOrdemCronologicaVO> obterDadosOrdemCronologicaArvorePol(Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec){
		List<ExameOrdemCronologicaVO> result = new ArrayList<ExameOrdemCronologicaVO>();
		
		ProjectionList p = getProjectionDadosOrdemCronologicaArvorePol();
		
		DetachedCriteria criteria = obterCriteriaDadosOrdemCronologicaArvorePol(codPaciente);
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ExameOrdemCronologicaVO.class));
		result = executeCriteria(criteria);
		
		//Union
		DetachedCriteria criteria1 = obterCriteriaDadosOrdemCronologicaArvorePol(codPaciente);
		criteria1.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV");
		criteria1.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria1.setProjection(p);
		criteria1.setResultTransformer(Transformers.aliasToBean(ExameOrdemCronologicaVO.class));
		
		List<ExameOrdemCronologicaVO> list = this.executeCriteria(criteria1);
		
		result.addAll(list);
		
		// apenas para forçar a entrada pelo menos a 1a vez no if do loop 
		// abaixo foi atribuído um valor que nunca existirá que é o -1
		Integer soeSeqAnterior = -1;
		Short seqpAnterior = -1;
		
		Date dataExame = null;

		for(ExameOrdemCronologicaVO vo : result){
			if (!vo.getSoeSeq().equals(soeSeqAnterior) || !vo.getSeqp().equals(seqpAnterior)) {
				soeSeqAnterior = vo.getSoeSeq();
				seqpAnterior = vo.getSeqp();
				
				//percorre todos os registros buscando a data de exame
				dataExame = obterDataExame(soeSeqAnterior, seqpAnterior, paramSitCodLiberado, paramSitCodAreaExec);
			}

			vo.setDtExame(dataExame);
		}
		
		return result;
	}

	private ProjectionList getProjectionDadosOrdemCronologicaArvorePol() {
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("EXA." + AelExames.Fields.DESCRICAO_USUAL.toString()), ExameOrdemCronologicaVO.Fields.DESCRICAO_USUAL.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()), ExameOrdemCronologicaVO.Fields.MAT_DESCRICAO.toString());
		p.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.SEQ.toString()),ExameOrdemCronologicaVO.Fields.SOLICITACAO_SEQ.toString());
		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()),ExameOrdemCronologicaVO.Fields.ITEM_SOLICITACAO_SEQ.toString());
		p.add(Projections.property("EXA." + AelExames.Fields.SIGLA.toString()),ExameOrdemCronologicaVO.Fields.SIGLA.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.SEQ.toString()),ExameOrdemCronologicaVO.Fields.MAN_SEQ.toString());
		p.add(Projections.property("OEM." + AelOrdExameMatAnalise.Fields.ORDEM_NIVEL1.toString()), ExameOrdemCronologicaVO.Fields.ORDEM_NIVEL1.toString());
		p.add(Projections.property("OEM." + AelOrdExameMatAnalise.Fields.ORDEM_NIVEL2.toString()), ExameOrdemCronologicaVO.Fields.ORDEM_NIVEL2.toString());
		return p;
	}
	
	private DetachedCriteria obterCriteriaDadosOrdemCronologicaArvorePol(Integer codPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class,"SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_ORD_EXAME_MAT_ANALISE.toString(), "OEM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "CA"));
		return criteria;
	}
	

	/**
	 * ORADB AELC_BUSCA_DT_EXAME
	 * @param soeSeq
	 * @param seqp
	 * @param sitCodigoLiberado
	 * @param sitCodigoAreaExec
	 * @return
	 */
	public Date obterDataExame(Integer soeSeq,Short seqp, String sitCodigoLiberado, String sitCodigoAreaExec) {
		
		Date dataExame = null;
		
		DetachedCriteria criteria1 = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ISE");
		criteria1.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria1.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString(), soeSeq));
		criteria1.add(Restrictions.eq(AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));
		
		AelItemSolicitacaoExames result = (AelItemSolicitacaoExames) this.executeCriteriaUniqueResult(criteria1,true);
		
		
		//Se não estiver liberado
		if(result!=null && !result.getSituacaoItemSolicitacao().getCodigo().equals(sitCodigoLiberado)){
			//A data é a de cricação da Solicitação
			dataExame = result.getSolicitacaoExame().getCriadoEm();
		}
		else{
			//Caso contrário, buscar a maior data de evento
			DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicitacao.class);
			ProjectionList p = Projections.projectionList();
			p.add(Projections.max(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()));
			criteria.setProjection(p);
			criteria.createAlias(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "EIS");
			
			criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString(), soeSeq));
			criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString(), seqp));
			criteria.add(Restrictions.eq(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigoAreaExec));
			
			return (Date) this.executeCriteriaUniqueResult(criteria);
			
		}
		
		return dataExame;

	}
	
	public List<ExameAmostraColetadaVO> obterDadosPorAmostrasColetadas(Integer codPaciente, String paramSitCodLiberado, String paramSitCodAreaExec){
		List<ExameAmostraColetadaVO> result = new ArrayList<ExameAmostraColetadaVO>();
		
		ProjectionList p = getProjectionDadosPorAmostrasColetadas();
		
		//recupera os campos sem a data de exame
		DetachedCriteria criteria = obterCriteriaDadosPorAmostrasColetadas(codPaciente);
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ExameAmostraColetadaVO.class));
		result = executeCriteria(criteria);
		
		//Union
		DetachedCriteria criteria1 = obterCriteriaDadosPorAmostrasColetadas(codPaciente);
		criteria1.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV");
		criteria1.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria1.setProjection(p);
		criteria1.setResultTransformer(Transformers.aliasToBean(ExameAmostraColetadaVO.class));
		List<ExameAmostraColetadaVO> list = this.executeCriteria(criteria1);
		
		result.addAll(list);
		
		// apenas para forçar a entrada pelo menos a 1a vez no if do loop 
		// abaixo foi atribuído um valor que nunca existirá que é o -1
		Integer soeSeqAnterior = -1; 
		Short seqpAnterior = -1;
		
		Date dataExame = null;

		//Busca data de exames e grupos de material
		for(ExameAmostraColetadaVO vo : result) {
			if (!vo.getSoeSeq().equals(soeSeqAnterior) || !vo.getSeqp().equals(seqpAnterior)) {
				soeSeqAnterior = vo.getSoeSeq();
				seqpAnterior = vo.getSeqp();
				
				//percorre todos os registros buscando a data de exame
				dataExame = obterDataExame(soeSeqAnterior, seqpAnterior, paramSitCodLiberado, paramSitCodAreaExec);
			}

			vo.setDtExame(dataExame);
		}
		
		return result;
	}

	private ProjectionList getProjectionDadosPorAmostrasColetadas() {
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("EXA." + AelExames.Fields.DESCRICAO_USUAL.toString()), ExameAmostraColetadaVO.Fields.DESCRICAO_USUAL.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()), ExameAmostraColetadaVO.Fields.MAT_DESCRICAO.toString());
		p.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.SEQ.toString()),ExameAmostraColetadaVO.Fields.SOLICITACAO_SEQ.toString());
		p.add(Projections.property("ISE." + AelItemSolicitacaoExames.Fields.SEQP.toString()),ExameAmostraColetadaVO.Fields.ITEM_SOLICITACAO_SEQ.toString());
		p.add(Projections.property("EXA." + AelExames.Fields.SIGLA.toString()),ExameAmostraColetadaVO.Fields.SIGLA.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.SEQ.toString()),ExameAmostraColetadaVO.Fields.MAN_SEQ.toString());
		p.add(Projections.property("OEM." + AelOrdExameMatAnalise.Fields.ORDEM_NIVEL1.toString()), ExameAmostraColetadaVO.Fields.ORDEM_NIVEL1.toString());
		p.add(Projections.property("OEM." + AelOrdExameMatAnalise.Fields.ORDEM_NIVEL2.toString()), ExameAmostraColetadaVO.Fields.ORDEM_NIVEL2.toString());
		p.add(Projections.property("GMA." + AelGrupoMaterialAnalise.Fields.DESCRICAO.toString()), ExameAmostraColetadaVO.Fields.DESCRICAO_GRUPO_MAT.toString());
		p.add(Projections.property("GMA." + AelGrupoMaterialAnalise.Fields.SEQ.toString()), ExameAmostraColetadaVO.Fields.SEQ_GRUPO_MAT_ANALISE.toString());
		p.add(Projections.property("GMA." + AelGrupoMaterialAnalise.Fields.ORD_PRONT_ONLINE.toString()), ExameAmostraColetadaVO.Fields.ORD_PRONT_ONLINE.toString());
		
		return p;
	}

	/**
	 * Monta critéria com joins entre AelSolicitacaoExames, AelItemSolicitacaoExames, AelExames, AelExamesMaterialAnalise e AghAtendimentos, e 
	 * cláusula where AelItemSolicitacaoExames.SIT_CODIGO != CA e AghAtendimentos.COD_PACIENTE = codPaciente
	 * @param codPaciente
	 * @return
	 */
	private DetachedCriteria obterCriteriaDadosPorAmostrasColetadas(Integer codPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class,"SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_ORD_EXAME_MAT_ANALISE.toString(), "OEM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MAN." + AelGrupoMaterialAnalise.Fields.GRUPO_MATERIAL.toString(), "GXM");
		criteria.createAlias("GXM." + AelGrupoXMaterialAnalise.Fields.GRUPO_MATERIAL.toString(), "GMA");

		criteria.add(Restrictions.ne("ISE." + AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(), "CA"));
		criteria.add(Restrictions.eq("GMA." + AelGrupoMaterialAnalise.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteria.add(Restrictions.eq("GXM." + AelGrupoXMaterialAnalise.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		
		return criteria;
	}
	
	public List<AelOrdExameMatAnalise> pesquisarAelOrdExameMatParaPOL(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, AelExames exame, AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2) {
		DetachedCriteria dc = criarCriteriaPesquisaAelOrdExameMatParaPOL(exame, materialAnalise, ordemNivel1, ordemNivel2);
		
		dc.createAlias(AelOrdExameMatAnalise.Fields.EXAME.toString(), "EX", JoinType.LEFT_OUTER_JOIN);

		dc.createAlias(AelOrdExameMatAnalise.Fields.EXAME_MAT_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		dc.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "EMA_MA", JoinType.LEFT_OUTER_JOIN);

		dc.addOrder(Order.asc("EX."+AelExames.Fields.DESCRICAO.toString()));
		dc.addOrder(Order.asc("EMA_MA."+AelMateriaisAnalises.Fields.DESCRICAO.toString()));
		
		dc.addOrder(Order.asc(AelOrdExameMatAnalise.Fields.ORDEM_NIVEL1.toString()));
		dc.addOrder(Order.asc(AelOrdExameMatAnalise.Fields.ORDEM_NIVEL2.toString()));
		
		return executeCriteria(dc, firstResult, maxResults, null);
	}

	private DetachedCriteria criarCriteriaPesquisaAelOrdExameMatParaPOL(AelExames exame, AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2) {
		DetachedCriteria dc = DetachedCriteria.forClass(AelOrdExameMatAnalise.class);
		
		if (exame != null) {
			dc.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.EMA_EXA_SIGLA.toString(), exame.getSigla()));
		}
		
		if (materialAnalise != null) {
			dc.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.EMA_MAN_SEQ.toString(), materialAnalise.getSeq()));
		}
		
		if (ordemNivel1 != null) {
			dc.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.ORDEM_NIVEL1.toString(), ordemNivel1));
		}
		
		if (ordemNivel2 != null) {
			dc.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.ORDEM_NIVEL2.toString(), ordemNivel2));
		}
		return dc;
	}

	public Long pesquisarAelOrdExameMatParaPOLCount(AelExames exame, AelMateriaisAnalises materialAnalise, Short ordemNivel1, Short ordemNivel2) {
		DetachedCriteria dc = criarCriteriaPesquisaAelOrdExameMatParaPOL(exame, materialAnalise, ordemNivel1, ordemNivel2);
		
		return executeCriteriaCount(dc);
		
		
		/*boolean temFiltroPesquisa = Boolean.FALSE;
		
		StringBuilder hql = new StringBuilder(200);
		hql.append("select count(oema)");
		hql.append(" from AelOrdExameMatAnalise oema");
		hql.append(" inner join oema.examesMaterialAnalise ema");
		hql.append(" inner join ema.aelMateriaisAnalises ma");
		
		if(exame != null && StringUtils.isNotBlank(exame.getSigla())) {
			hql.append(" where");
			hql.append(" ema.id.exaSigla = '"+exame.getSigla()+"'");
			temFiltroPesquisa = Boolean.TRUE;
		}
		
		if(materialAnalise != null && materialAnalise.getSeq() != null) {
			if(temFiltroPesquisa) {
				hql.append(" and");
				hql.append(" ma.seq  = "+materialAnalise.getSeq());
			} else {
				hql.append(" where");
				hql.append(" ma.seq = "+materialAnalise.getSeq());
				temFiltroPesquisa = Boolean.TRUE;
			}
		}
		
		if(ordemNivel1 != null) {
			if(temFiltroPesquisa) {
				hql.append(" and");
				hql.append(" oema.ordemNivel1  = "+ordemNivel1);
			} else {
				hql.append(" where");
				hql.append(" oema.ordemNivel1 = "+ordemNivel1);
				temFiltroPesquisa = Boolean.TRUE;
			}
		}
		
		if(ordemNivel2 != null) {
			if(temFiltroPesquisa) {
				hql.append(" and");
				hql.append(" oema.ordemNivel2  = "+ordemNivel2);
			} else {
				hql.append(" where");
				hql.append(" oema.ordemNivel2 = "+ordemNivel2);
				temFiltroPesquisa = Boolean.TRUE;
			}
		}

		Query query = createHibernateQuery(hql.toString());
		
		Long countResultadoPesquisa = (Long) query.uniqueResult();
		
		return countResultadoPesquisa.intValue();*/
		
	}
	
	public AelOrdExameMatAnalise recuperaAelOrdExameMatAnalisePorMaterial(AelExamesMaterialAnaliseId aelExamesMaterialAnaliseId) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelOrdExameMatAnalise.class);
		
		criteria.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.EMA_EXA_SIGLA.toString(), aelExamesMaterialAnaliseId.getExaSigla()));
		criteria.add(Restrictions.eq(AelOrdExameMatAnalise.Fields.EMA_MAN_SEQ.toString(), aelExamesMaterialAnaliseId.getManSeq()));

		criteria.createAlias(AelOrdExameMatAnalise.Fields.EXAME.toString(), "EX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelOrdExameMatAnalise.Fields.EXAME_MAT_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "EMA_MA", JoinType.LEFT_OUTER_JOIN);
		
		AelOrdExameMatAnalise aelOrdExameMatAnalise = (AelOrdExameMatAnalise) this.executeCriteriaUniqueResult(criteria);
		
		return aelOrdExameMatAnalise;
		
	}
}