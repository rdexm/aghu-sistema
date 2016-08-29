package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.vo.ExamesGrupoExameVO;
import br.gov.mec.aghu.exames.agendamento.vo.ItemHorarioAgendadoVO;
import br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoExameUnidExame;
import br.gov.mec.aghu.model.AelGrupoExameUnidExameId;
import br.gov.mec.aghu.model.AelGrupoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class AelGrupoExameUnidExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrupoExameUnidExame> {
	
	private static final long serialVersionUID = 6827636084362120323L;
	
	private static final String ALIASGEUE = "geue";
	private static final String ALIASEXE = "exe";
	private static final String ALIASEMA = "ema";
	private static final String ALIASEXA = "exa";
	private static final String ALIASMAN = "man";
	private static final String ALIASUNF = "unf";
	private static final String SEPARADOR = ".";

	
	public List<AelGrupoExameUnidExame> pesquisarGrupoExameUnidExamePorGexSeq(Integer gexSeq) {
		
		String aliasGrupoExameUnidade = "geu";
		String separador = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExameUnidExame.class, aliasGrupoExameUnidade);
		criteria.add(Restrictions.eq(aliasGrupoExameUnidade + separador	+ AelGrupoExameUnidExame.Fields.GEX_SEQ.toString(), gexSeq));
		
		return executeCriteria(criteria);
	}
	
	public List<ExamesGrupoExameVO> buscarListaExamesGrupoExameVOPorCodigoGrupoExame(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Integer codigoGrupoExame) {
		
		final DetachedCriteria criteria = montarCriteriaListaExamesGrupoExameVOPorCodigoGrupoExame(codigoGrupoExame);
		criteria.setResultTransformer(Transformers.aliasToBean(ExamesGrupoExameVO.class));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long countBuscarListaExamesGrupoExameVOPorCodigoGrupoExame(Integer codigo) {
		return executeCriteriaCount(montarCriteriaListaExamesGrupoExameVOPorCodigoGrupoExame(codigo));
	}
	
	private DetachedCriteria montarCriteriaListaExamesGrupoExameVOPorCodigoGrupoExame(Integer codigoGrupoExame) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExameUnidExame.class, ALIASGEUE);
		criteria.createAlias(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString(), ALIASEXE);
		criteria.createAlias(ALIASEXE + SEPARADOR + AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), ALIASEMA);
		criteria.createAlias(ALIASEMA + SEPARADOR + AelExamesMaterialAnalise.Fields.EXAME.toString(), ALIASEXA);
		criteria.createAlias(ALIASEMA + SEPARADOR +	AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), ALIASMAN);
		criteria.createAlias(ALIASEXE + SEPARADOR +	AelUnfExecutaExames.Fields.UNIDADE_FUNCIONAL_OBJ.toString(), ALIASUNF);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString()),ExamesGrupoExameVO.Fields.UFE_EMA_EXA_SIGLA.toString())
				.add(Projections.property(ALIASEXA + SEPARADOR + AelExames.Fields.DESCRICAO_USUAL.toString()),ExamesGrupoExameVO.Fields.DESCRICAO_USUAL_EXAME.toString())
				.add(Projections.property(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString()),ExamesGrupoExameVO.Fields.UFE_EMA_MAN_SEQ.toString())
				.add(Projections.property(ALIASMAN + SEPARADOR + AelMateriaisAnalises.Fields.DESCRICAO.toString()),ExamesGrupoExameVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property(ALIASEXE + SEPARADOR + AelUnfExecutaExames.Fields.UNF_SEQ.toString()),ExamesGrupoExameVO.Fields.UFE_UNF_SEQ.toString())
				.add(Projections.property(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.SITUACAO.toString()),ExamesGrupoExameVO.Fields.SITUACAO.toString())
				.add(Projections.property(ALIASUNF + SEPARADOR + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),ExamesGrupoExameVO.Fields.UNF_DESCRICAO.toString())
				.add(Projections.property(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.VERSION.toString()),ExamesGrupoExameVO.Fields.VERSION.toString()));
						
		criteria.add(Restrictions.eq(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.GEX_SEQ.toString(), codigoGrupoExame));
		return criteria;
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoExameVO> pesquisarGrupoExameTransferenciaAgendamento(List<ItemHorarioAgendadoVO> listaItens){
			StringBuffer hql = new StringBuffer(400);
			hql.append(" select new br.gov.mec.aghu.exames.coleta.vo.GrupoExameVO(");
			hql.append(" gex.").append(AelGrupoExames.Fields.SEQ.toString());
			hql.append(", ");
			hql.append(" gex.").append(AelGrupoExames.Fields.UNF_SEQ.toString());
			hql.append(", ");
			hql.append(" max(ufe."+AelUnfExecutaExames.Fields.DTHR_REATIVA_TEMP.toString()+")");
			hql.append(" ) ");
			hql.append(" from AelGrupoExameUnidExame geu");
			hql.append(" join geu.").append(AelGrupoExameUnidExame.Fields.GRUPO_EXAME.toString());
			hql.append(" as gex ");
			hql.append(" join geu.").append(AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString());
			hql.append(" as ufe ");
			hql.append(" where ");
			for(int i=0; i<listaItens.size();i++){
				hql.append(" gex.").append(AelGrupoExames.Fields.IND_AGENDA_EX_MESMO_HOR.toString()).append(" = :agendaExMesmoHor");
				hql.append(" and gex.").append(AelGrupoExames.Fields.SITUACAO.toString()).append(" = :situacaoGrupo");
				hql.append(" and geu.").append(AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString()).append(" = :sigla").append(i);
				hql.append(" and geu.").append(AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString()).append(" = :manSeq").append(i);
				hql.append(" and geu.").append(AelGrupoExameUnidExame.Fields.UFE_UNF_SEQ.toString()).append(" = :unfSeq").append(i);
				hql.append(" and geu.").append(AelGrupoExameUnidExame.Fields.SITUACAO.toString()).append(" = :situacaoGrupoExame");
				if(i!=listaItens.size()-1){
					hql.append(" or ");		
				}
			}
			hql.append(" group by ");
			hql.append(" gex.").append(AelGrupoExames.Fields.SEQ.toString());
			hql.append(" , gex.").append(AelGrupoExames.Fields.UNF_SEQ.toString());
			hql.append(" having count(gex.").append(AelGrupoExames.Fields.SEQ.toString()).append(") = ").append(listaItens.size());
			
				        
			Query query = createHibernateQuery(hql.toString());
			query.setParameter("agendaExMesmoHor", true);
			query.setParameter("situacaoGrupo", DominioSituacao.A);
			query.setParameter("situacaoGrupoExame", DominioSituacao.A);
			
			for(int i=0; i<listaItens.size();i++){
				ItemHorarioAgendadoVO itemHorarioAgendadoVO = listaItens.get(i);
				String sigla = itemHorarioAgendadoVO.getSigla();
				Integer manSeq = itemHorarioAgendadoVO.getSeqMaterialAnalise();
				Short unfSeq = itemHorarioAgendadoVO.getSeqUnidade();
				query.setParameter("sigla"+i, sigla);
				query.setParameter("manSeq"+i, manSeq);
				query.setParameter("unfSeq"+i, unfSeq);
			}
			return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<AelGrupoExameUnidExame> pesquisarGrupoExamePorListaExamesAgendamentoEGexSeq(List<ItemHorarioAgendadoVO> listaItens, 
			Integer gexSeq) {
		StringBuffer hql = new StringBuffer(150);
		hql.append("select geu");
		hql.append(" from");
		hql.append(' ').append(AelGrupoExameUnidExame.class.getSimpleName()).append(" geu");
		hql.append(" join geu.").append(AelGrupoExameUnidExame.Fields.GRUPO_EXAME.toString());
		hql.append(" where");
		for (int i = 0; i < listaItens.size(); i++) {
			String indexStr = String.valueOf(i);
			hql.append(" geu." + AelGrupoExameUnidExame.Fields.SITUACAO.toString() + " = :situacaoGrupoExame"+indexStr);
			hql.append(" and geu." + AelGrupoExameUnidExame.Fields.GEX_SEQ.toString() + " = :gexSeq"+indexStr);
			hql.append(" and geu." + AelGrupoExameUnidExame.Fields.UFE_EMA_EXA_SIGLA.toString() + " = :sigla"+indexStr);
			hql.append(" and geu." + AelGrupoExameUnidExame.Fields.UFE_EMA_MAN_SEQ.toString() + " = :manSeq"+indexStr);
			hql.append(" and geu." + AelGrupoExameUnidExame.Fields.UFE_UNF_SEQ.toString() + " = :unfSeq"+indexStr);
			if (i < listaItens.size() - 1) {
				hql.append(" or");		
			}
		}
		
		Query query = createHibernateQuery(hql.toString());
		
		for (int i = 0; i < listaItens.size(); i++) {
			ItemHorarioAgendadoVO itemHorarioAgendadoVO = listaItens.get(i);
			String sigla = itemHorarioAgendadoVO.getSigla();
			Integer manSeq = itemHorarioAgendadoVO.getSeqMaterialAnalise();
			Short unfSeq = itemHorarioAgendadoVO.getSeqUnidade();
			query.setParameter("situacaoGrupoExame"+i, DominioSituacao.A);
			query.setParameter("gexSeq"+i, gexSeq);
			query.setParameter("sigla"+i, sigla);
			query.setParameter("manSeq"+i, manSeq);
			query.setParameter("unfSeq"+i, unfSeq);
		}
		
		return query.list();
	}

	public AelGrupoExameUnidExame obterPorChavePrimariaFull(AelGrupoExameUnidExameId id) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrupoExameUnidExame.class, ALIASGEUE);
		criteria.createAlias(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.UNF_EXECUTA_EXAME.toString(), ALIASEXE);
		criteria.createAlias(ALIASEXE + SEPARADOR + AelUnfExecutaExames.Fields.EXAME_MATERIAL_ANALISE.toString(), ALIASEMA);
		criteria.createAlias(ALIASEMA + SEPARADOR + AelExamesMaterialAnalise.Fields.EXAME.toString(), ALIASEXA);
		criteria.createAlias(ALIASEMA + SEPARADOR +	AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), ALIASMAN);

		criteria.add(Restrictions.eq(ALIASGEUE + SEPARADOR + AelGrupoExameUnidExame.Fields.ID.toString(), id));
		return (AelGrupoExameUnidExame)executeCriteriaUniqueResult(criteria);
	}
	
}
