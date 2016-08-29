package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.ProcedRiscoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TipoGrupoRiscoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciCriterioPortal;
import br.gov.mec.aghu.model.MciGrupoProcedRisco;
import br.gov.mec.aghu.model.MciMapProcedPrescricao;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.MciParamProcedRisco;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class MciProcedimentoRiscoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciProcedimentoRisco> {

	private static final long serialVersionUID = -2309909471166319673L;

	//# 37965 - C2
	public List<ProcedRiscoVO> pesquisarMciProcedRiscoPorSeqTipoGrupo(Short seqTipoGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciGrupoProcedRisco.class, "GRS");
		criteria.createAlias("GRS." + MciGrupoProcedRisco.Fields.MCI_PROCEDIMENTO_RISCOS.toString() , "POR");
		if(seqTipoGrupo != null) {
			criteria.add(Restrictions.eq("GRS." + MciGrupoProcedRisco.Fields.MCI_TIPO_GRUPO_PROCED_RISCOS_SEQ.toString(), seqTipoGrupo));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("POR." + MciProcedimentoRisco.Fields.DESCRICAO.toString()), ProcedRiscoVO.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ProcedRiscoVO.class));
		return executeCriteria(criteria);
	}
	
	//# 37968 - C1
	public List<MciProcedimentoRisco> pesquisarMciProcedRiscoPorSeqDescricaoSituacao(Short seq, String descricao, DominioSituacao indSituacao, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciProcedimentoRisco.class);
		if(seq != null) {
			criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.SEQ.toString(), seq));
		}
		if(descricao != null && !"".equals(descricao)) {
			criteria.add(Restrictions.ilike(MciProcedimentoRisco.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indSituacao != null) {
			criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciProcedimentoRisco.class));
		return executeCriteria(criteria,firstResult, maxResult, orderProperty, asc);
	}
	
	//# 37968 - C1 Count
	public Long pesquisarMciProcedRiscoPorSeqDescricaoSituacaoCount(Short seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciProcedimentoRisco.class);
		if(seq != null) {
			criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.SEQ.toString(), seq));
		}
		if(descricao != null && !"".equals(descricao)) {
			criteria.add(Restrictions.ilike(MciProcedimentoRisco.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(indSituacao != null) {
			criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.IND_SITUACAO.toString(), indSituacao));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciProcedimentoRisco.class));
		return executeCriteriaCount(criteria);
	}
	
	//# 37968 - C2
	public List<TipoGrupoRiscoVO> pesquisarMciGrupoProcedRiscoPorSeqeSeqTipoGrupo(Short seq, Short seqTipoGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciGrupoProcedRisco.class, "GPR");
		criteria.createAlias("GPR." + MciGrupoProcedRisco.Fields.MCI_TIPO_GRUPO_PROCED_RISCOS.toString() , "TGP", JoinType.INNER_JOIN);
		if(seq != null) {
			criteria.add(Restrictions.eq("GPR." + MciGrupoProcedRisco.Fields.POR_SEQ.toString(), seq));
		}
		if(seqTipoGrupo != null) {
			criteria.add(Restrictions.eq("GPR." + MciGrupoProcedRisco.Fields.TGP_SEQ.toString(), seqTipoGrupo));
		}
		criteria.setProjection(Projections.projectionList()
				//.add(Projections.property("GPR." + MciGrupoProcedRisco.Fields.POR_SEQ.toString()), TipoGrupoRiscoVO.Fields.POR_SEQ.toString())
				//.add(Projections.property("GPR." + MciGrupoProcedRisco.Fields.TGP_SEQ.toString()), TipoGrupoRiscoVO.Fields.TGP_SEQ.toString())
				//.add(Projections.property("GPR." + MciGrupoProcedRisco.Fields.IND_SITUACAO.toString()), TipoGrupoRiscoVO.Fields.IND_SITUACAO.toString())
				//.add(Projections.property("GPR." + MciGrupoProcedRisco.Fields.CRIADO_EM.toString()), TipoGrupoRiscoVO.Fields.CRIADO_EM.toString())
				//.add(Projections.property("GPR." + RapServidores.Fields.MATRICULA.toString()), TipoGrupoRiscoVO.Fields.MATRICULA.toString())
				//.add(Projections.property("GPR." + RapServidores.Fields.CODIGO_VINCULO.toString()), TipoGrupoRiscoVO.Fields.CODIGO_VINCULO.toString())
				.add(Projections.property("TGP." + MciTipoGrupoProcedRisco.Fields.SEQ.toString()), TipoGrupoRiscoVO.Fields.SEQ.toString())
				.add(Projections.property("TGP." + MciTipoGrupoProcedRisco.Fields.DESCRICAO.toString()), TipoGrupoRiscoVO.Fields.DESCRICAO.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(TipoGrupoRiscoVO.class));
		return executeCriteria(criteria);
	}
	
	//# 37968 - C3
	public List<MciTipoGrupoProcedRisco> pesquisarMciTipoGrupo(String strPesquisa, Short seqProcedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciTipoGrupoProcedRisco.class);
		
		DetachedCriteria subquery = DetachedCriteria.forClass(MciGrupoProcedRisco.class);
		subquery.add(Restrictions.eq(MciGrupoProcedRisco.Fields.POR_SEQ.toString(), seqProcedimento));
		subquery.setProjection(Projections.projectionList()
				.add(Projections.property(MciGrupoProcedRisco.Fields.TGP_SEQ.toString()))
		);
		
		criteria.add(Subqueries.propertyNotIn(MciTipoGrupoProcedRisco.Fields.SEQ.toString(), subquery));	
		criteria.add(Restrictions.eq(MciTipoGrupoProcedRisco.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if(strPesquisa != null && !strPesquisa.isEmpty()){
			if(CoreUtil.isNumeroShort(strPesquisa)){
				criteria.add(Restrictions.eq(MciTipoGrupoProcedRisco.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			}else {
				criteria.add(Restrictions.ilike(MciTipoGrupoProcedRisco.Fields.DESCRICAO.toString(), strPesquisa , MatchMode.ANYWHERE));
			}
		}
		
		//criteria.addOrder(Order.asc("TGP." + MciTipoGrupoProcedRisco.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria,0,100,MciTipoGrupoProcedRisco.Fields.DESCRICAO.toString(),true);
	}
	
	//# 37968 - C4
	//Select “IMPRESSAO”, PRU_Seq
	public List<MciParamProcedRisco> pesquisarImpressao(Short porSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciParamProcedRisco.class);
		if(porSeq != null) {
			criteria.add(Restrictions.eq(MciParamProcedRisco.Fields.POR_SEQ.toString(), porSeq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciParamProcedRisco.class));
		return executeCriteria(criteria);
	}
	
	//# 37968 - C4
	//Select “PRESCRICAO”, Seq
	public List<MciMapProcedPrescricao> pesquisarPrescricao(Short porSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMapProcedPrescricao.class);
		if(porSeq != null) {
			criteria.add(Restrictions.eq(MciMapProcedPrescricao.Fields.POR_SEQ.toString(), porSeq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciMapProcedPrescricao.class));
		return executeCriteria(criteria);
	}
	
	//# 37968 - C4
	//Select “NOTIFICACAO”, Seq
	public List<MciMvtoProcedimentoRiscos> pesquisarNotificacao(Short porSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class);
		if(porSeq != null) {
			criteria.add(Restrictions.eq(MciMvtoProcedimentoRiscos.Fields.POR_SEQ.toString(), porSeq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciMvtoProcedimentoRiscos.class));
		return executeCriteria(criteria);
	}
	
	//# 37968 - C4
	//Select “CRITERIOPORTAL”, Seq
	public List<MciCriterioPortal> pesquisarCriterio(Short porSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciCriterioPortal.class);
		if(porSeq != null) {
			criteria.add(Restrictions.eq(MciCriterioPortal.Fields.POR_SEQ.toString(), porSeq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(MciCriterioPortal.class));
		return executeCriteria(criteria);
	}
	
	public MciProcedimentoRisco obterProcedimento(Short seq){
		return super.obterPorChavePrimaria(seq);
	}
	
	public void inserir(MciProcedimentoRisco entity){
		super.persistir(entity);
	}
	
	// #36265 
	public ProcedRiscoVO obterProcedRiscoComRelacionamento(Short seq) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MciProcedimentoRisco.class,"MPR");
			criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.SEQ.toString(), seq));
			
			criteria.createAlias("MPR." + MciProcedimentoRisco.Fields.RAP_SERVIDORES_BY_MCI_POR_SER_FK1.toString(), "SER", JoinType.INNER_JOIN);
			criteria.createAlias("MPR." + MciProcedimentoRisco.Fields.RAP_SERVIDORES_BY_MCI_POR_SER_FK2.toString(), "SER_MOVI", JoinType.LEFT_OUTER_JOIN);
			
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), ProcedRiscoVO.Fields.MATRICULA.toString())
					.add(Projections.property("SER." + RapServidores.Fields.CODIGO_VINCULO.toString()), ProcedRiscoVO.Fields.CODIGO_VINCULO.toString())
					.add(Projections.property("SER_MOVI." + RapServidores.Fields.MATRICULA.toString()), ProcedRiscoVO.Fields.MATRICULA_MOVI.toString())
					.add(Projections.property("SER_MOVI." + RapServidores.Fields.CODIGO_VINCULO.toString()), ProcedRiscoVO.Fields.CODIGO_VINCULO_MOVI.toString())
			);
				
			criteria.setResultTransformer(Transformers.aliasToBean(ProcedRiscoVO.class));
			
			
			return (ProcedRiscoVO) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<MciProcedimentoRisco> obterProcedimentoRiscoPorCodigoDescricao(String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaProcedimentoRiscoPorCodigoDescricao(strPesquisa);
		return this.executeCriteria(criteria,0,100,MciProcedimentoRisco.Fields.DESCRICAO.toString(),true);
	}
	
	public Long obterProcedimentoRiscoPorCodigoDescricaoCount(String strPesquisa) {
		DetachedCriteria criteria = montarCriteriaProcedimentoRiscoPorCodigoDescricao(strPesquisa);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaProcedimentoRiscoPorCodigoDescricao(String strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciProcedimentoRisco.class);

		if(strPesquisa != null && !strPesquisa.isEmpty()){
			if(CoreUtil.isNumeroShort(strPesquisa)){
				criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.SEQ.toString(), Short.valueOf(strPesquisa)));
			}else {
				criteria.add(Restrictions.ilike(MciProcedimentoRisco.Fields.DESCRICAO.toString(), strPesquisa , MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MciProcedimentoRisco.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
}
