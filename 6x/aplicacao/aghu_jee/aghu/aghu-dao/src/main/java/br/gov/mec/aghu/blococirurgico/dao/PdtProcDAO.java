package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.ComplementoCidVO;
import br.gov.mec.aghu.blococirurgico.vo.ProcEspPorCirurgiaVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PdtProcDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtProc> {

	private static final long serialVersionUID = 464554797425278643L;

	public List<PdtProc> pesquisarPdtProcPorDdtSeqOrdenadoPorSeqP(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class,"PDT");
				
		criteria.add(Restrictions.eq("PDT."+PdtProc.Fields.DDT_SEQ.toString(), seq));	
		criteria.createAlias("PDT."+PdtProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "DPT2");
		criteria.createAlias("DPT2."+PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), "PCI",JoinType.LEFT_OUTER_JOIN);

		DetachedCriteria subCriteria = DetachedCriteria.forClass(PdtProcDiagTerap.class, "DPT");
		subCriteria.setProjection(Projections.property("DPT."+PdtProcDiagTerap.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("PDT."+PdtProc.Fields.DPT_SEQ.toString(), "DPT."+PdtProcDiagTerap.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq("DPT."+PdtProcDiagTerap.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Subqueries.exists(subCriteria));
		/*
		 Select * 
from PDT_PROCS
where  exists (select 1
from   PDT_PROC_DIAG_TERAPS DPT
where  (PDT_PROCS.DPT_SEQ = DPT.SEQ )
and    DPT.IND_SITUACAO = 'A'
)
		 */
		
		criteria.addOrder(Order.asc(PdtProc.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
	}

	public List<PdtProc> pesquisarPdtProcPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class);		
				
		criteria.add(Restrictions.eq(PdtProc.Fields.DDT_SEQ.toString(), seq));		
		
		return executeCriteria(criteria);
	}
	
	public Long obterCountProcPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class);
		
		criteria.add(Restrictions.eq(PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarCriteriaPesquisaProcEspPorDdtSeqEEspSeq(Integer ddtSeq, Short espSeq) {
		String aliasDpt = "dpt";
		String aliasDpc = "dpc";
		String aliasEpr = "epr";
		String aliasPci = "pci";
		String aliasEsp = "esp";
		String ponto = ".";
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class, aliasDpc);

		criteria.createAlias(aliasDpc + ponto + PdtProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), aliasDpt);
		criteria.createAlias(aliasDpt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), aliasPci);
		criteria.createAlias(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.ESPECIALIDADES_PROCS_CIRGS.toString(), aliasEpr);
		criteria.createAlias(aliasEpr + ponto + MbcEspecialidadeProcCirgs.Fields.ESPECIALIDADE.toString(), aliasEsp);
		
		criteria.add(Restrictions.eq(aliasDpc + ponto + PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq(aliasEpr + ponto + MbcEspecialidadeProcCirgs.Fields.SITUACAO, DominioSituacao.A));	
		
		Projection proj = Projections.projectionList()
				.add(Projections.property(aliasDpt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO_SEQ.toString()), 
						ProcEspPorCirurgiaVO.Fields.PCI_SEQ.toString())
				.add(Projections.property(aliasDpt + ponto + PdtProcDiagTerap.Fields.SEQ.toString()), 
						ProcEspPorCirurgiaVO.Fields.DPT_SEQ.toString())
				.add(Projections.property(aliasDpc + ponto + PdtProc.Fields.IND_CONTAMINACAO.toString()), 
						ProcEspPorCirurgiaVO.Fields.IND_CONTAMINACAO.toString());
		
		criteria.setProjection(Projections.distinct(proj));
		
		return criteria;
	}
	
	public List<ProcEspPorCirurgiaVO> pesquisarProcEspNaoPrincipalPorDdtSeqEEspSeq(Integer ddtSeq, Short espSeq) {
		String aliasEsp = "esp";
		String ponto = ".";
		DetachedCriteria criteria = montarCriteriaPesquisaProcEspPorDdtSeqEEspSeq(ddtSeq, espSeq);
		
		criteria.add(Restrictions.eq(aliasEsp + ponto + AghEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcEspPorCirurgiaVO.class));
		
		return executeCriteria(criteria);		
	}
	
	public List<ProcEspPorCirurgiaVO> pesquisarProcEspPrincipalPorDdtSeqEEspSeq(Integer ddtSeq, Short espSeq) {
		String aliasEsp = "esp";
		String ponto = ".";
		DetachedCriteria criteria = montarCriteriaPesquisaProcEspPorDdtSeqEEspSeq(ddtSeq, espSeq);
		
		criteria.add(Restrictions.isNull(aliasEsp + ponto + AghEspecialidades.Fields.ESP_SEQ.toString()));
		criteria.add(Restrictions.eq(aliasEsp + ponto + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ProcEspPorCirurgiaVO.class));
		
		return executeCriteria(criteria);		
	}
	
	public List<PdtProc> pesquisarProcComProcedimentoCirurgicoAtivoPorDdtSeq(Integer ddtSeq) {
		String aliasDpc = "dpc";
		String aliasDpt = "dpt";
		String aliasPci = "pci";
		String ponto = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class, aliasDpc);

		criteria.createAlias(aliasDpc + ponto + PdtProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), aliasDpt);
		criteria.createAlias(aliasDpt + ponto + PdtProcDiagTerap.Fields.PROCEDIMENTO_CIRURGICO.toString(), aliasPci);
		
		criteria.add(Restrictions.eq(aliasDpc + ponto + PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(aliasPci + ponto + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Short obterMaiorSeqpProcPorDdtSeq(Integer ddtSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class);		
		
		criteria.add(Restrictions.eq(PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));		
		
		criteria.setProjection(Projections.max(PdtProc.Fields.SEQP.toString()));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}

	public List<ComplementoCidVO> obterListaComplementoCid(Integer ddtSeq, Object strPesquisa) {
		return executeCriteria(obterCriteriaListaComplementoCid(ddtSeq, strPesquisa, false));
	}

	public Long obterListaComplementoCidCount(Integer ddtSeq, Object strPesquisa) {
		return executeCriteriaCount(obterCriteriaListaComplementoCid(ddtSeq, strPesquisa, true));
	}

	public DetachedCriteria obterCriteriaListaComplementoCid(Integer ddtSeq, Object strPesquisa, Boolean isCount) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class);
		
		criteria.createAlias(PdtProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "DIAGT", Criteria.INNER_JOIN);
		criteria.createAlias("DIAGT." + PdtProcDiagTerap.Fields.PDT_CID_POR_PROCES.toString(), "CPROCS", Criteria.INNER_JOIN);
		criteria.createAlias("CPROCS." + PdtCidPorProc.Fields.AGH_CID.toString(), "CID", Criteria.INNER_JOIN);

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq( "CID." + AghCid.Fields.SEQ.toString(), Integer.valueOf(strPesquisa.toString())));
		}else if(!StringUtils.isEmpty((String)strPesquisa)){
			criteria.add(Restrictions.or(Restrictions.ilike("CID." + AghCid.Fields.CODIGO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE),
					Restrictions.ilike("CID." + AghCid.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE)));
		}
		
		criteria.add(Restrictions.eq(PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq( "DIAGT." + PdtProcDiagTerap.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq( "CPROCS." + PdtCidPorProc.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if(Boolean.FALSE.equals(isCount)) {
			Projection proj = Projections.projectionList()
					.add(Projections.property("CID." + AghCid.Fields.SEQ.toString()), 
							ComplementoCidVO.Fields.SEQ.toString())
					.add(Projections.property("CID." + AghCid.Fields.CODIGO.toString()), 
							ComplementoCidVO.Fields.CODIGO.toString())
					.add(Projections.property("CID." + AghCid.Fields.DESCRICAO.toString()), 
							ComplementoCidVO.Fields.DESCRICAO.toString())
					.add(Projections.property("CID." + AghCid.Fields.DESCRICAO_EDITADA.toString()), 
							ComplementoCidVO.Fields.DESCRICAO_EDITADA.toString());
			criteria.setProjection(Projections.distinct(proj));
			criteria.addOrder(Order.asc("CID." + AghCid.Fields.DESCRICAO.toString()));
			criteria.setResultTransformer(Transformers.aliasToBean(ComplementoCidVO.class));
		}
		
		return criteria;
	}

	public Long countListaComplementoCid(Integer ddtSeq, Object strPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtProc.class);

		criteria.createAlias(PdtProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "DIAGT", Criteria.INNER_JOIN);
		criteria.createAlias("DIAGT." + PdtProcDiagTerap.Fields.PDT_CID_POR_PROCES.toString(), "CPROCS", Criteria.INNER_JOIN);
		criteria.createAlias("CPROCS." + PdtCidPorProc.Fields.AGH_CID.toString(), "CID", Criteria.INNER_JOIN);

		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq( "CID." + AghCid.Fields.SEQ.toString(), Integer.valueOf(strPesquisa.toString())));
		}else if(!StringUtils.isEmpty((String)strPesquisa)){
			criteria.add(Restrictions.or(Restrictions.ilike("CID." + AghCid.Fields.CODIGO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE),
					Restrictions.ilike("CID." + AghCid.Fields.DESCRICAO.toString(), strPesquisa.toString(), MatchMode.ANYWHERE)));
		}
		
		criteria.add(Restrictions.eq(PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq( "DIAGT." + PdtProcDiagTerap.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq( "CPROCS." + PdtCidPorProc.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteriaCount(criteria);
	}
}
