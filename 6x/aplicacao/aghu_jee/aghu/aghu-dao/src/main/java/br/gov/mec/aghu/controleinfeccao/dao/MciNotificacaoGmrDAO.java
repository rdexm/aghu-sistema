package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.GMRPacienteVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioNotificGermeMultirresistenteVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciAntimicrobianos;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.model.MciNotificacaoGmr;


public class MciNotificacaoGmrDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciNotificacaoGmr>{

	private static final long serialVersionUID = -1081110401245709754L;

	public Boolean verificarNotificacaoGmrPorCodigo(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString(), Boolean.TRUE));
		
		return (executeCriteriaCount(criteria) > 0);
	}
	
	public List<MciNotificacaoGmr> pesquisarNotificacaoGrmPorAmbSeq(Integer ambSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.AMB_SEQ.toString(), ambSeq));
		
		return executeCriteria(criteria);	
	}
	
	public List<MciNotificacaoGmr> pesquisarNotificacaoGrmPorBmrSeq(Integer bmrSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.BMR_SEQ.toString(), bmrSeq));
		
		return executeCriteria(criteria);	
	}
	
	public Boolean existeNotificacaoListarPacientesCCIH(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		criteria.createAlias(MciNotificacaoGmr.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		return executeCriteriaExists(criteria);
	}
	
	public List<GMRPacienteVO> pesquisarGermesMultirresistentesPaciente(final Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		
		ProjectionList p = Projections.projectionList();
		
		p.add(Projections.property(MciNotificacaoGmr.Fields.SEQ.toString()), GMRPacienteVO.Fields.SEQ.toString());
		p.add(Projections.property(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString()), GMRPacienteVO.Fields.ATIVO.toString());
		p.add(Projections.property("MAN." + AelMateriaisAnalises.Fields.DESCRICAO.toString()), GMRPacienteVO.Fields.MATERIAL_ANALISE.toString());
		p.add(Projections.property(MciNotificacaoGmr.Fields.CRIADO_EM.toString()), GMRPacienteVO.Fields.DATA_IDENTIFICACAO.toString());
		p.add(Projections.property("SOE." + AelSolicitacaoExames.Fields.SEQ.toString()), GMRPacienteVO.Fields.SOLICITACAO.toString());
		p.add(Projections.property("BMR." + MciBacteriaMultir.Fields.DESCRICAO.toString()), GMRPacienteVO.Fields.BACTERIA.toString());
		p.add(Projections.property("AMB." + MciAntimicrobianos.Fields.DESCRICAO.toString()), GMRPacienteVO.Fields.MEDICAMENTO.toString());
		
		criteria.setProjection(p);
		
		criteria.createAlias(MciNotificacaoGmr.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias(MciNotificacaoGmr.Fields.MCI_BACTERIA_MULTIR.toString(), "BMR");
		criteria.createAlias(MciNotificacaoGmr.Fields.MCI_ANTIMICROBIANOS.toString(), "AMB", JoinType.LEFT_OUTER_JOIN);
		
		// Faz a ligação para REE_ISE_SOE_SEQ, REE_ISE_SOE_SEQ, 
		criteria.createAlias(MciNotificacaoGmr.Fields.RESULTADO_EXAME.toString(), "RES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("RES." + AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ISE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "SOE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_EXAMES.toString(), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISE." + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString(), "MAN", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		
		criteria.addOrder(Order.desc(MciNotificacaoGmr.Fields.CRIADO_EM.toString())); // DATANOTIFICACAO
		
		criteria.setResultTransformer(Transformers.aliasToBean(GMRPacienteVO.class));
		
		return executeCriteria(criteria);
	}
	
	public boolean obterAtendimentoFibrose(String paramFibrose, Integer pacCodigo){
		StringBuilder sql = new StringBuilder(2000);
		sql.append("SELECT DISTINCT 1 ");
		sql.append(" FROM  " ).append(AghCid.class.getSimpleName()).append(" CID ");
		sql.append(" INNER JOIN CID." ).append(AghCid.Fields.DIAGNOSTICOS.toString()).append(" DIAG ");
		sql.append(" WHERE SUBSTR(CID.").append(AghCid.Fields.CODIGO.toString()).append(", 1, 3) in (:paramFibrose)");
		sql.append(" AND DIAG.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" IN (SELECT ");
		sql.append(" ATD.").append(AghAtendimentos.Fields.PAC_CODIGO.toString());
		sql.append(" FROM  " ).append(AghAtendimentos.class.getSimpleName()).append(" ATD ");
		sql.append(" WHERE ATD.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo  )");
		Query query = createHibernateQuery(sql.toString());
		query.setParameter("paramFibrose", paramFibrose); 
		query.setParameter("pacCodigo", pacCodigo); 
		Integer retornoConsulta =(Integer) query.uniqueResult();
		if (retornoConsulta != null && retornoConsulta > 0) {
			return true;
		} else {
			return false;
		}
		
		
	}	
	public List<RelatorioNotificGermeMultirresistenteVO> listarRelatorioPacientesPortadoresGermeMultiResistente(String paramFibrose, Integer bacteriaSeq, Short unidadeSeq, Boolean indNotificao){
		StringBuilder sql = new StringBuilder(2000);
		sql.append("SELECT NGM.").append(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.IND_NOTIFICACAO_ATIVA.toString()).append(" , ");
		sql.append("PAC.").append(AipPacientes.Fields.CODIGO.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.PAC_CODIGO.toString()).append(" , ");
		sql.append("PAC.").append(AipPacientes.Fields.PRONTUARIO.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.PRONTUARIO_PACIENTE.toString()).append(" , ");
		sql.append("PAC.").append(AipPacientes.Fields.NOME.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.NOME_PACIENTE.toString()).append(" , ");
		sql.append("AMB.").append(MciAntimicrobianos.Fields.DESCRICAO.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.DESCRICAO_MICROBIANA.toString()).append(" , ");
		sql.append("BMR.").append(MciBacteriaMultir.Fields.DESCRICAO.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.DESCRICAO_BACTERIA.toString()).append(" , ");
		sql.append("NGM.").append(MciNotificacaoGmr.Fields.CRIADO_EM.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.DATA_IDENTIFICACAO.toString()).append(" , ");
		sql.append("MAN.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString()).append(" as ").append(RelatorioNotificGermeMultirresistenteVO.Fields.DESCRICAO_MATERIAL.toString()).append(" , ");
		sql.append("ATD.").append(AghAtendimentos.Fields.LTO_LTO_ID.toString()).append(" as  ").append(RelatorioNotificGermeMultirresistenteVO.Fields.LEITO_ATENDIMENTO.toString());
		sql.append(" FROM ").append(MciNotificacaoGmr.class.getSimpleName()).append(" NGM ");
		sql.append(" inner join NGM.").append(MciNotificacaoGmr.Fields.PACIENTE.toString()).append(" PAC ");
		sql.append(" left outer join  NGM.").append(MciNotificacaoGmr.Fields.MCI_ANTIMICROBIANOS.toString()).append(" AMB");
		sql.append(" inner join NGM.").append(MciNotificacaoGmr.Fields.MCI_BACTERIA_MULTIR.toString()).append(" BMR ");
		sql.append(" left outer join  NGM.").append(MciNotificacaoGmr.Fields.RESULTADO_EXAME.toString()).append(" RE ");
		sql.append(" left outer join  RE.").append(AelResultadoExame.Fields.ITEM_SOLICITACAO_EXAME.toString()).append(" ISE ");
		sql.append(" left outer join  ISE.").append(AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES.toString()).append(" MAN ");
		sql.append(" inner join  PAC.").append(AipPacientes.Fields.ATENDIMENTOS.toString()).append(" ATD ");
		sql.append(" left outer join  ATD.").append(AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString()).append(" UNF ");
		sql.append(" WHERE ATD.").append(AghAtendimentos.Fields.SEQ.toString()).append("= COALESCE((SELECT MAX(ATD2.").append(AghAtendimentos.Fields.SEQ.toString()).append(") FROM ").append(AghAtendimentos.class.getSimpleName()).append(" ATD2 ").append(" WHERE ATD2.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" = ATD.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" AND ATD2.").append(AghAtendimentos.Fields.ORIGEM).append(" IN ('I', 'U', 'N'))");
        sql.append("  , (SELECT MAX(ATD2.").append(AghAtendimentos.Fields.SEQ.toString()).append(") FROM ").append(AghAtendimentos.class.getSimpleName()).append(" ATD2 ").append(" WHERE ATD2.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" = ATD.").append(AghAtendimentos.Fields.PAC_CODIGO.toString()).append(" AND ATD2.").append(AghAtendimentos.Fields.ORIGEM).append(" IN ('A', 'C')))");
        if (bacteriaSeq != null) {
			sql.append(" AND BMR.").append(MciBacteriaMultir.Fields.SEQ.toString()).append("= :paramBacteriaSeq");
		}
		if (unidadeSeq != null) {
			sql.append(" AND UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).append("=:paramUnidadeSeq");
		}
		if (indNotificao != null) {
			sql.append(" AND NGM.").append(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString()).append("=:paramNotificacaoAtiva");
		}
		sql.append("  ORDER BY UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).append(" , ATD.").append(AghAtendimentos.Fields.QRT_NUMERO.toString()).append(", ATD.").append(AghAtendimentos.Fields.LTO_LTO_ID.toString()).append(" , PAC.").append(AipPacientes.Fields.PRONTUARIO.toString());
		Query query = createHibernateQuery(sql.toString());
		if (bacteriaSeq != null) {
			query.setParameter("paramBacteriaSeq", bacteriaSeq);
		}
		if (unidadeSeq != null) {
			query.setParameter("paramUnidadeSeq", unidadeSeq);
		}
		if (indNotificao != null) {
			query.setParameter("paramNotificacaoAtiva", indNotificao);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(RelatorioNotificGermeMultirresistenteVO.class));

		return query.list();
		
	}
	
	public List<MciBacteriaMultir> pesquisarDescricaoBacteria(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		
		criteria.createAlias(MciNotificacaoGmr.Fields.MCI_BACTERIA_MULTIR.toString(), "BMR");
		criteria.createAlias(MciNotificacaoGmr.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString(), true));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("BMR." + MciBacteriaMultir.Fields.DESCRICAO.toString()).as(MciBacteriaMultir.Fields.DESCRICAO.toString()))));

		criteria.setResultTransformer(Transformers.aliasToBean(MciBacteriaMultir.class));
		
		return executeCriteria(criteria);	
	}
	
	public List<MciNotificacaoGmr> pesquisarNotificacaoAtiva(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciNotificacaoGmr.class);
		
		criteria.createAlias(MciNotificacaoGmr.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString(), true));
		
		return executeCriteria(criteria);
	}

}