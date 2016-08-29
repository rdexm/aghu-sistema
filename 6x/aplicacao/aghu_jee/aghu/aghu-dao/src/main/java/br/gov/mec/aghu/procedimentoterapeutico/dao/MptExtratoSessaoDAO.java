package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoJustificativa;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ExtratoSessaoVO;

public class MptExtratoSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptExtratoSessao> {

	private static final long serialVersionUID = 6082584819984406090L;

	

	/* #47717 - VISUALIZAR EXTRATO DA SESSÃO*/
	
	/**
	 * SELECT 
  EXTRATO.IND_SITUACAO,
  EXTRATO.CRIADO_EM,
  EXTRATO.JUSTIFICATIVA,
  PESSOA.NOME,
  JUS.DESCRICAO AS DESC_JUS,
  TPJ.DESCRICAO AS DESC_TPJ
	
	
	
NNER JOIN
MPT_PRESCRICAO_PACIENTES PTE
ON SESSAO.PTE_SEQ = PTE.SEQ
INNER JOIN AGH_ATENDIMENTOS ATD
ON PTE.ATD_SEQ = ATD.SEQ
INNER JOIN MPT_JUSTIFICATIVA JUS
ON EXTRATO.JUS_SEQ = JUS.SEQ
INNER JOIN MPT_TIPO_JUSTIFICATIVA TPJ
ON JUS.TPJ_SEQ = TPJ.SEQ
WHERE ATD.PAC_CODIGO = <Código_Paciente>
ORDER BY EXTRATO.CRIADO_EM DESC;
* */
	
	private DetachedCriteria montarCriteriaTodosExtratoDaSessao(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptExtratoSessao.class, "EXTRATO");
		criteria.createAlias("EXTRATO." + MptExtratoSessao.Fields.SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		criteria.createAlias("SERVIDOR." + RapServidores.Fields.PESSOA_FISICA.toString(), "PESSOA", JoinType.INNER_JOIN);
		criteria.createAlias("EXTRATO." + MptExtratoSessao.Fields.MPTSESSAO.toString(), "SESSAO", JoinType.INNER_JOIN);
		criteria.createAlias("SESSAO." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("EXTRATO." + MptExtratoSessao.Fields.MOTIVO.toString(), "JUS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("JUS." + MptJustificativa.Fields.MPT_TIPO_JUSTIFICATIVA.toString(), "TPJ", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.CODIGO_PACIENTE.toString(), codigoPaciente));
		criteria.addOrder(Order.desc("EXTRATO." + MptExtratoSessao.Fields.CRIADO_EM.toString()));
		criteria.setProjection(Projections.projectionList()
			.add(Projections.property("EXTRATO." + MptExtratoSessao.Fields.IND_SITUACAO.toString()), ExtratoSessaoVO.Fields.INDSITUACAO.toString())
			.add(Projections.property("EXTRATO." + MptExtratoSessao.Fields.CRIADO_EM.toString()), ExtratoSessaoVO.Fields.CRIADO_EM.toString())
			.add(Projections.property("EXTRATO." + MptExtratoSessao.Fields.JUSTIFICATIVA.toString()), ExtratoSessaoVO.Fields.JUSTIFICATIVA.toString())
			.add(Projections.property("PESSOA." +  RapPessoasFisicas.Fields.NOME.toString()), ExtratoSessaoVO.Fields.USUARIO_SERVIDOR.toString())
			.add(Projections.property("JUS." + MptJustificativa.Fields.DESCRICAO.toString()), ExtratoSessaoVO.Fields.DESCRICAO_JUSTIFICATIVA.toString())
			.add(Projections.property("TPJ." + MptTipoJustificativa.Fields.DESCRICAO.toString()), ExtratoSessaoVO.Fields.TIPO_JUSTIFICATIVA.toString()));		
		
		criteria.setResultTransformer(Transformers.aliasToBean(ExtratoSessaoVO.class));
		return criteria;
	}
	
	public List<ExtratoSessaoVO> pesquisarListaExtratoSessao(Integer codigoPaciente) {
		DetachedCriteria criteria = montarCriteriaTodosExtratoDaSessao(codigoPaciente);	
		return executeCriteria(criteria);
	}
	
	public boolean existeVinculoExtratoSessao(MptJustificativa mptJustificativa){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptExtratoSessao.class, "EXT");
		criteria.createAlias("EXT."+MptExtratoSessao.Fields.MOTIVO.toString(), "JUS", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("JUS."+MptJustificativa.Fields.SEQ.toString(), mptJustificativa.getSeq()));
		
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * Consulta Hora de Inicio
	 * @param seqSessao
	 * @return List<Date>
	 */
	public List<MptExtratoSessao> buscarHoraInicio(Integer seqSessao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptExtratoSessao.class, "EXT");
		
		criteria.add(Restrictions.eq("EXT."+MptExtratoSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoSessao.SAT));
		criteria.add(Restrictions.eq("EXT."+MptExtratoSessao.Fields.MPTSESSAO_SEQ.toString(), seqSessao));		
		criteria.addOrder(Order.desc("EXT."+MptExtratoSessao.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria, 0, 1, null, true);
	}
}