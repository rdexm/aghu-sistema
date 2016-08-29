package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
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
import br.gov.mec.aghu.model.AelExtratoItemSolicHist;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelOrdExameMatAnalise;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;
import br.gov.mec.aghu.model.AghAtendimentos;


public class AelSolicitacaoExamesHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSolicitacaoExamesHist> {

	private static final long serialVersionUID = 6703514061410527219L;

	public List<ExameAmostraColetadaVO> listarExamesSolicManAtvHist(Integer codPaciente){
		ProjectionList p = getProjectionDadosPorAmostrasColetadas();
		
		DetachedCriteria criteria1 = obterCriteriaDadosPorAmostrasColetadas(codPaciente);
		criteria1.createAlias("SOE." + AelSolicitacaoExamesHist.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV");
		criteria1.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria1.setProjection(p);
		criteria1.setResultTransformer(Transformers.aliasToBean(ExameAmostraColetadaVO.class));
		return this.executeCriteria(criteria1);
		
	}
	
	private DetachedCriteria obterCriteriaDadosPorAmostrasColetadas(Integer codPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExamesHist.class,"SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExamesHist.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicExameHist.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicExameHist.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		criteria.createAlias("ISE." + AelItemSolicExameHist.Fields.AEL_ORD_EXAME_MAT_ANALISE.toString(), "OEM");
		criteria.createAlias("MAN." + AelGrupoMaterialAnalise.Fields.GRUPO_MATERIAL.toString(), "GXM");
		criteria.createAlias("GXM." + AelGrupoXMaterialAnalise.Fields.GRUPO_MATERIAL.toString(), "GMA");

		criteria.add(Restrictions.ne("ISE." + AelItemSolicExameHist.Fields.SIT_CODIGO.toString(), "CA"));
		criteria.add(Restrictions.eq("GMA." + AelGrupoMaterialAnalise.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteria.add(Restrictions.eq("GXM." + AelGrupoXMaterialAnalise.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		
		return criteria;
	}
	
	public List<ExameAmostraColetadaVO> listarExamesSolicManAtdHist(Integer codPaciente){
		
		ProjectionList p = getProjectionDadosPorAmostrasColetadas();
		
		//recupera os campos sem a data de exame
		DetachedCriteria criteria = obterCriteriaDadosPorAmostrasColetadas(codPaciente);
		criteria.createAlias("SOE." + AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ExameAmostraColetadaVO.class));
		return executeCriteria(criteria);
	}
	
	private ProjectionList getProjectionDadosPorAmostrasColetadas() {
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("EXA." + AelExames.Fields.DESCRICAO_USUAL.toString()), ExameAmostraColetadaVO.Fields.DESCRICAO_USUAL.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()), ExameAmostraColetadaVO.Fields.MAT_DESCRICAO.toString());
		p.add(Projections.property("SOE." + AelSolicitacaoExamesHist.Fields.SEQ.toString()),ExameAmostraColetadaVO.Fields.SOLICITACAO_SEQ.toString());
		p.add(Projections.property("ISE." + AelItemSolicExameHist.Fields.SEQP.toString()),ExameAmostraColetadaVO.Fields.ITEM_SOLICITACAO_SEQ.toString());
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
	 * Monta critéria com joins entre AelSolicitacaoExamesHist, AelSolicitacaoExamesHist, AelExames, AelExamesMaterialAnalise e AghAtendimentos, e 
	 * cláusula where AelItemSolicitacaoExames.SIT_CODIGO != CA e AghAtendimentos.COD_PACIENTE = codPaciente
	 * @param codPaciente
	 * @return
	 */
	private DetachedCriteria obterCriteriaDadosOrdemCronologicaArvorePol(Integer codPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExamesHist.class,"SOE");
		criteria.createAlias("SOE." + AelSolicitacaoExamesHist.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ISE");
		criteria.createAlias("ISE." + AelItemSolicExameHist.Fields.AEL_EXAMES.toString(), "EXA");
		criteria.createAlias("ISE." + AelItemSolicExameHist.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN");
		criteria.createAlias("ISE." + AelItemSolicExameHist.Fields.AEL_ORD_EXAME_MAT_ANALISE.toString(), "OEM", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.ne("ISE." + AelItemSolicExameHist.Fields.SIT_CODIGO.toString(), "CA"));
		return criteria;
	}
	
	public AelItemSolicExameHist obterAelItemSolicExamesHistPorSeqSeqp(Integer soeSeq, Short seqp) {
		DetachedCriteria criteria1 = DetachedCriteria.forClass(AelItemSolicExameHist.class, "ISE");
		criteria1.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE");
		criteria1.add(Restrictions.eq(AelItemSolicExameHist.Fields.SOLICITACAO_EXAME_SEQ.toString(), soeSeq));
		criteria1.add(Restrictions.eq(AelItemSolicExameHist.Fields.SEQP.toString(), seqp));
		
		return (AelItemSolicExameHist) executeCriteriaUniqueResult(criteria1);
	}
	
	public Date obterMaxDataHoraEvento(Integer soeSeq, Short seqp, String sitCodigoAreaExec) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExtratoItemSolicHist.class);
		ProjectionList p = Projections.projectionList();
		p.add(Projections.max(AelExtratoItemSolicHist.Fields.DATA_HORA_EVENTO.toString()));
		criteria.setProjection(p);
		criteria.createAlias(AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO.toString(), "EIS");
		
		criteria.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelExtratoItemSolicHist.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigoAreaExec));
		
		return (Date) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<ExameOrdemCronologicaVO> listarCriteriaExameOrdemCronologicaSolManAtdHist(Integer codPaciente){
		ProjectionList p = getProjectionDadosOrdemCronologicaArvorePol();
		
		DetachedCriteria criteria = obterCriteriaDadosOrdemCronologicaArvorePol(codPaciente);
		criteria.createAlias("SOE." + AelSolicitacaoExamesHist.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(ExameOrdemCronologicaVO.class));
		return executeCriteria(criteria);
	}
	
	private ProjectionList getProjectionDadosOrdemCronologicaArvorePol() {
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("EXA." + AelExames.Fields.DESCRICAO_USUAL.toString()), ExameOrdemCronologicaVO.Fields.DESCRICAO_USUAL.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()), ExameOrdemCronologicaVO.Fields.MAT_DESCRICAO.toString());
		p.add(Projections.property("SOE." + AelSolicitacaoExamesHist.Fields.SEQ.toString()),ExameOrdemCronologicaVO.Fields.SOLICITACAO_SEQ.toString());
		p.add(Projections.property("ISE." + AelItemSolicExameHist.Fields.SEQP.toString()),ExameOrdemCronologicaVO.Fields.ITEM_SOLICITACAO_SEQ.toString());
		p.add(Projections.property("EXA." + AelExames.Fields.SIGLA.toString()),ExameOrdemCronologicaVO.Fields.SIGLA.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.SEQ.toString()),ExameOrdemCronologicaVO.Fields.MAN_SEQ.toString());
		p.add(Projections.property("OEM." + AelOrdExameMatAnalise.Fields.ORDEM_NIVEL1.toString()), ExameOrdemCronologicaVO.Fields.ORDEM_NIVEL1.toString());
		p.add(Projections.property("OEM." + AelOrdExameMatAnalise.Fields.ORDEM_NIVEL2.toString()), ExameOrdemCronologicaVO.Fields.ORDEM_NIVEL2.toString());
		return p;
	}

	public List<ExameOrdemCronologicaVO> listarCriteriaExameOrdemCronologicaSolManAtvHist(Integer codPaciente) {
		ProjectionList p = getProjectionDadosOrdemCronologicaArvorePol();
		
		DetachedCriteria criteria1 = obterCriteriaDadosOrdemCronologicaArvorePol(codPaciente);
		criteria1.createAlias("SOE." + AelSolicitacaoExamesHist.Fields.ATENDIMENTO_DIVERSO.toString(), "ATV");
		criteria1.add(Restrictions.eq("ATV." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), codPaciente));
		
		criteria1.setProjection(p);
		criteria1.setResultTransformer(Transformers.aliasToBean(ExameOrdemCronologicaVO.class));
		
		return this.executeCriteria(criteria1);
	}
}