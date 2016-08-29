package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmRespostaConsultoria;
import br.gov.mec.aghu.model.MpmRespostaConsultoriaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.RespostasConsultoriaVO;

public class MpmRespostaConsultoriaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmRespostaConsultoria> {

	private static final long serialVersionUID = 2096890911972162074L;

	private DetachedCriteria obterCriteriaRespostaSolicitacaoConsultoria(Integer idAtendimento, Integer seqConsultoria, Integer ordem) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoria.class);

		criteria.createAlias(MpmRespostaConsultoria.Fields.TIPO_RESPOSTA_CONSULTORIA.toString(), "TRC");

		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.SCN_ATD_SEQ.toString(), idAtendimento));
		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.SCN_SEQ.toString(), seqConsultoria));

		if (ordem != null && ordem.equals(1)) {
			criteria.addOrder(Order.desc(MpmRespostaConsultoria.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.asc("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		} else if (ordem != null && ordem.equals(1)) {
			criteria.addOrder(Order.asc(MpmRespostaConsultoria.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.asc("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		}

		return criteria;
	}

	public List<MpmRespostaConsultoria> obterRespostasConsultoria(Integer idAtendimento, Integer seqConsultoria, Integer ordem) {
		DetachedCriteria criteria = obterCriteriaRespostaSolicitacaoConsultoria(idAtendimento, seqConsultoria, ordem);
		return executeCriteria(criteria);
	}
	
	public List<MpmRespostaConsultoria> obterMpmRespostaConsultoriaPorIdAtivo(MpmRespostaConsultoriaId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoria.class);
		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.SCN_ATD_SEQ.toString(), id.getScnAtdSeq()));
		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.SCN_SEQ.toString(), id.getScnSeq()));
		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.TRC_SEQ.toString(), id.getTrcSeq()));
		criteria.add(Restrictions.eq(MpmRespostaConsultoria.Fields.CRIADO_EM.toString(), id.getCriadoEm()));
		return executeCriteria(criteria);
	}
	
	public List<RespostasConsultoriaVO> listarRespostaConsultoriaPorAtdSeqConsultoria(Integer atdSeq, Integer scnSeq, Integer ordem){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoria.class, "REC");
		criteria.createAlias("REC." + MpmRespostaConsultoria.Fields.TIPO_RESPOSTA_CONSULTORIA.toString(), "TRC", JoinType.INNER_JOIN);
		criteria.createAlias("REC." + MpmRespostaConsultoria.Fields.SERVIDOR.toString(), "RAP", JoinType.INNER_JOIN);
		criteria.createAlias("RAP." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("REC." + MpmRespostaConsultoria.Fields.SCN_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("REC." + MpmRespostaConsultoria.Fields.SCN_SEQ.toString(), scnSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("REC." + MpmRespostaConsultoria.Fields.CRIADO_EM.toString()), RespostasConsultoriaVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()), RespostasConsultoriaVO.Fields.ORDEM_VISUALIZACAO.toString())
				.add(Projections.property("REC." + MpmRespostaConsultoria.Fields.TRC_SEQ.toString()), RespostasConsultoriaVO.Fields.TRC_SEQ.toString())
				.add(Projections.property("REC." + MpmRespostaConsultoria.Fields.DESCRICAO.toString()), RespostasConsultoriaVO.Fields.DESCRICAO.toString())
				.add(Projections.property("REC." + MpmRespostaConsultoria.Fields.FINALIZACAO.toString()), RespostasConsultoriaVO.Fields.IND_FINALIZACAO.toString())
				.add(Projections.property("REC." + MpmRespostaConsultoria.Fields.SER_MATRICULA.toString()), RespostasConsultoriaVO.Fields.SER_MATRICULA.toString())
				.add(Projections.property("REC." + MpmRespostaConsultoria.Fields.SER_VIN_CODIGO.toString()), RespostasConsultoriaVO.Fields.SER_VIN_CODIGO.toString())
				.add(Projections.property("TRC." + MpmTipoRespostaConsultoria.Fields.DESCRICAO.toString()), RespostasConsultoriaVO.Fields.TIPO.toString())
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), RespostasConsultoriaVO.Fields.NOME.toString()));
		
		if (ordem != null && ordem.equals(1)) {
			criteria.addOrder(Order.desc("REC." + MpmRespostaConsultoria.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.asc("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		} else if (ordem != null && !ordem.equals(1)) {
			criteria.addOrder(Order.asc("REC." + MpmRespostaConsultoria.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.asc("TRC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(RespostasConsultoriaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MpmRespostaConsultoria> listarRespostaConsultoriaPorId(Integer atdSeq, Integer scnSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoria.class, "REC");
		criteria.add(Restrictions.eq("REC." + MpmRespostaConsultoria.Fields.SCN_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq("REC." + MpmRespostaConsultoria.Fields.SCN_SEQ.toString(), scnSeq));
		criteria.addOrder(Order.desc("REC." + MpmRespostaConsultoria.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**#45341 C3 - Retorna uma lista com espSeq e Descricao **/
	public MpmRespostaConsultoria obterDescricaoEspSeqPorAtdSeq(Integer pAtdSeqSol){
		DetachedCriteria criteria  = criteriaObterDescricaoEspSeqPorAtdSeq(pAtdSeqSol);
		return (MpmRespostaConsultoria)executeCriteriaUniqueResult(criteria);
	}
	
	//#45341 - montagem da consulta C3
	private DetachedCriteria criteriaObterDescricaoEspSeqPorAtdSeq(Integer pAtdSeqSolicitacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoria.class, "RESP");	
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MpmSolicitacaoConsultoria.class, "SOLIC");
		subCriteria.setProjection(Projections.distinct(
				Projections.projectionList().add(Projections.property("SOLIC."+MpmSolicitacaoConsultoria.Fields.SOLICITACAO_CONSULTORIA_ORIGINAL_SEQ.toString()), MpmSolicitacaoConsultoria.Fields.SOLICITACAO_CONSULTORIA_ORIGINAL_SEQ.toString())));
		if (pAtdSeqSolicitacao != null) {
			subCriteria.add(Restrictions.eq("SOLIC."+MpmSolicitacaoConsultoria.Fields.ATD_SEQ.toString(), pAtdSeqSolicitacao));
		}
		
		criteria.add(Subqueries.propertyIn("RESP."+MpmRespostaConsultoria.Fields.SCN_SEQ.toString(), subCriteria));
		
		return criteria;
	}

	public boolean existeRespostaAssociada(MpmTipoRespostaConsultoria tpResposta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmRespostaConsultoria.class, "RES");
		criteria.add(Restrictions.eq("RES."+MpmRespostaConsultoria.Fields.TIPO_RESPOSTA_CONSULTORIA.toString(), tpResposta));
		
		return executeCriteriaExists(criteria);
	}
}
