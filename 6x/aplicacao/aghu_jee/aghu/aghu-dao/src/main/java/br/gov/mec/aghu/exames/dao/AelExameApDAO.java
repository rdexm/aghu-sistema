package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.vo.RelatorioExamesPendentesVO;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelExameApDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameAp> {
	
	
	private static final long serialVersionUID = -2916713743773585686L;

	public AelExameAp obterPorAelAnatomoPatologicoAelConfigExLaudoUnico(final Long lumSeq, final Integer lu2Seq) {
		/*
CURSOR c_lux (c_lum_seq     ael_exame_aps.lum_seq%type,
              c_lu2_seq     ael_exame_aps.lu2_seq%type)
  IS
   SELECT seq
     FROM ael_exame_aps
    WHERE lum_seq = c_lum_seq
      AND lu2_seq = c_lu2_seq;
		 */
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class);
		criteria.add(Restrictions.eq(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_SEQ.toString(), lumSeq));
		criteria.add(Restrictions.eq(AelExameAp.Fields.CONFIG_EX_LAUDO_UNICO_SEQ.toString(), lu2Seq));
		return (AelExameAp) executeCriteriaUniqueResult(criteria);
	}
	
	public AelExameAp obterAelExameApPorSeq(final Long luxSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class);
		
		criteria.createAlias(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString() + "." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(),
				AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_ORIGEM.toString(), AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_ORIGEM.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_EXTRATO_EXAME_APS.toString(), AelExameAp.Fields.AEL_EXTRATO_EXAME_APS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.CONFIG_EX_LAUDO_UNICO.toString(), AelExameAp.Fields.CONFIG_EX_LAUDO_UNICO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.SERVIDOR.toString(), AelExameAp.Fields.SERVIDOR.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelExameAp.Fields.SERVIDOR_RESP_LAUDO.toString(), AelExameAp.Fields.SERVIDOR_RESP_LAUDO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.SERVIDOR_RESP_IMPRESSO.toString(), AelExameAp.Fields.SERVIDOR_RESP_IMPRESSO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelExameAp.Fields.SEQ.toString(), luxSeq));
		return (AelExameAp) executeCriteriaUniqueResult(criteria);
	}
	

	public List<AelExameAp> listarExameApPorSeqESituacao(final Long luxSeq, final DominioSituacaoExamePatologia... situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class);
		
		if (luxSeq != null) {
			criteria.add(Restrictions.eq(AelExameAp.Fields.SEQ.toString(), luxSeq));
		}
		
		if (situacao != null) {
			criteria.add(Restrictions.in(AelExameAp.Fields.ETAPAS_LAUDO.toString(), situacao));
		}
		
		return executeCriteria(criteria);
	}

	public AelExameAp obterAelExameApPorAelAnatomoPatologicos(final AelAnatomoPatologico aelAP){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class);
		criteria.createAlias(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_ORIGEM.toString(), AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_ORIGEM.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_ORIGEM.toString() + "." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "confExame", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS + "." + AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(),
				AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS + "." + AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(),JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.AEL_EXTRATO_EXAME_APS.toString(), AelExameAp.Fields.AEL_EXTRATO_EXAME_APS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.CONFIG_EX_LAUDO_UNICO.toString(), AelExameAp.Fields.CONFIG_EX_LAUDO_UNICO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.SERVIDOR.toString(), AelExameAp.Fields.SERVIDOR.toString(), JoinType.INNER_JOIN);
		criteria.createAlias(AelExameAp.Fields.SERVIDOR_RESP_LAUDO.toString(), AelExameAp.Fields.SERVIDOR_RESP_LAUDO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExameAp.Fields.SERVIDOR_RESP_IMPRESSO.toString(), AelExameAp.Fields.SERVIDOR_RESP_IMPRESSO.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), aelAP));
		return (AelExameAp) executeCriteriaUniqueResult(criteria);
	}
	
	
	public List<AelExameAp> pesquisarAelExameApPorMateriais(final String filtro){
		final DetachedCriteria criteria = obterCriteria(filtro);
		criteria.addOrder(Order.asc(AelExameAp.Fields.MATERIAIS.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long pesquisarAelExameApPorMateriaisCount(final String filtro){
		return executeCriteriaCount(obterCriteria(filtro));
	}
	
	private DetachedCriteria obterCriteria(final String filtro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class);
		
		if(!StringUtils.isEmpty(filtro)){
			if(CoreUtil.isNumeroLong(filtro)){
				criteria.add(Restrictions.eq(AelExameAp.Fields.SEQ.toString(), Long.valueOf(filtro)));
			} else {
				criteria.add(Restrictions.ilike(AelExameAp.Fields.MATERIAIS.toString(), filtro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}

	/**
	 * Lista os exames pendentes para o relatório.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param situacao
	 * @param patologistaSeq
	 * @param residenteSeq
	 * @param unidadePatologicaSeq
	 * @param situacaoExmAnd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<RelatorioExamesPendentesVO> obterListaExamesPendentes(Date dataInicial, Date dataFinal,
			String[] situacao, Integer[] patologistaSeq, Short unidadePatologicaSeq, DominioSituacaoExamePatologia situacaoExmAnd) {
		
		final StringBuilder hql = new StringBuilder(900);
		
		hql.append(" SELECT DISTINCT ")
		.append("EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.DTHR_EVENTO.toString()).append(',')
		
		.append(" ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.SOLICITACAO.toString()).append(',')
		
		.append(" LUM.").append(AelAnatomoPatologico.Fields.NUMERO_AP.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.NUMERO_AP.toString()).append(',')
		
		.append(" LUM.").append(AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.LU2_SEQ.toString()).append(',')
		
		.append(" LU2.").append(AelConfigExLaudoUnico.Fields.SIGLA.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.TIPO.toString()).append(',')
		
		.append(" PAC.").append(AipPacientes.Fields.PRONTUARIO.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.PRONTUARIO.toString()).append(',')
		
		.append(" PAC.").append(AipPacientes.Fields.NOME.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.NOME_PACIENTE.toString()).append(',')
		
		.append(" EXA.").append(AelExames.Fields.DESCRICAO_USUAL.toString()).append(" || ' / ' || ")
		.append("MAN.").append(AelMateriaisAnalises.Fields.DESCRICAO.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.EXAME_MATERIAL.toString()).append(',')
		
		.append(" LUI.").append(AelPatologista.Fields.NOME.toString())
		.append(" as ").append(RelatorioExamesPendentesVO.Fields.PATOLOGISTA_RESPONSAVEL.toString())
		
		.append(" FROM ")
		
		.append(AelItemSolicitacaoExames.class.getName()).append(" ISE, ")
		.append(AelExameApItemSolic.class.getName()).append(" LUL, ")
		.append(AelExameAp.class.getName()).append(" LUX, ")
		.append(AelAnatomoPatologico.class.getName()).append(" LUM, ")
		.append(AelItemConfigExame.class.getName()).append(" ICE, ")
		.append(AelConfigExLaudoUnico.class.getName()).append(" LU2, ")
		.append(AelExtratoItemSolicitacao.class.getName()).append(" EIS, ")
		.append(AelExames.class.getName()).append(" EXA, ")
		.append(AelMateriaisAnalises.class.getName()).append(" MAN, ")
		.append(AelApXPatologista.class.getName()).append(" LO5, ")
		.append(AelPatologista.class.getName()).append(" LUI, ")
		.append(AelSolicitacaoExames.class.getName()).append(" SOE, ")
		.append(AghAtendimentos.class.getName()).append(" ATD, ")
		.append(AipPacientes.class.getName()).append(" PAC ")
		
		.append(" WHERE 1=1 " )
		
		.append("   AND LUL.").append(AelExameApItemSolic.Fields.ISE_SOE_SEQ.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		
		.append("   AND LUL.").append(AelExameApItemSolic.Fields.ISE_SEQP.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.SEQP.toString())
		
		.append("   AND LUX.").append(AelExameAp.Fields.SEQ.toString())
		.append(" = LUL.").append(AelExameApItemSolic.Fields.LUX_SEQ.toString())
		
		.append("   AND LUM.").append(AelAnatomoPatologico.Fields.SEQ.toString())
		.append(" = LUX.").append(AelExameAp.Fields.LUM_SEQ.toString())
		
		.append("   AND ICE.").append(AelItemConfigExame.Fields.UFE_EMA_EXA_SILGA.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())
		
		.append("   AND ICE.").append(AelItemConfigExame.Fields.UFE_EMA_MAN_SEQ.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString())
		
		.append("   AND LU2.").append(AelConfigExLaudoUnico.Fields.SEQ.toString())
		.append(" = ICE.").append(AelItemConfigExame.Fields.LU2SEQ.toString())
		
		.append("   AND EIS.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		
		.append("   AND EIS.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.SEQP.toString())
		
		.append("   AND EIS.").append(AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString())
		
		.append("   AND EXA.").append(AelExames.Fields.SIGLA.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString())
		
		.append("   AND MAN.").append(AelMateriaisAnalises.Fields.SEQ.toString())
		.append(" = ISE.").append(AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString())
		
		.append("   AND LO5.").append(AelApXPatologista.Fields.LUM_SEQ.toString())
		.append(" = LUM.").append(AelAnatomoPatologico.Fields.SEQ.toString())
		
		.append("   AND LUI.").append(AelPatologista.Fields.SEQ.toString())
		.append(" = LO5.").append(AelApXPatologista.Fields.LUI_SEQ.toString())
		
		.append("   AND ISE.").append(AelItemSolicitacaoExames.Fields.SOE_SEQ.toString())
		.append(" = SOE.").append(AelSolicitacaoExames.Fields.SEQ.toString())
		
		.append("   AND SOE.").append(AelSolicitacaoExames.Fields.ATD_SEQ.toString())
		.append(" = ATD.").append(AghAtendimentos.Fields.SEQ.toString())
		
		.append("   AND ATD.").append(AghAtendimentos.Fields.PAC_CODIGO.toString())
		.append(" = PAC.").append(AipPacientes.Fields.CODIGO.toString())
		
		//.append("   AND LUI.").append(AelPatologista.Fields.FUNCAO.toString())
		//.append("   IN ('C', 'P') ")
		
		.append("   AND EIS.").append(AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString())
		.append("   IN (SELECT MAX(EIS1.").append(AelExtratoItemSolicitacao.Fields.CRIADO_EM.toString()).append(')')
		.append("   FROM ").append(AelExtratoItemSolicitacao.class.getName()).append(" EIS1 ")
		.append(" WHERE 1=1 " )
		.append("   AND EIS1.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString())
		.append(" = EIS.").append(AelExtratoItemSolicitacao.Fields.ISE_SOE_SEQ.toString())
		
		.append("   AND EIS1.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString())
		.append(" = EIS.").append(AelExtratoItemSolicitacao.Fields.ISE_SEQP.toString()).append(')')
		
		.append("   AND ISE.").append(AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()).append(" = :UNIDADE_PATOLOGIA ");
		
		hql.append("   AND ISE.").append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString());
		if (situacao.length > 1){
			hql.append(" in ( :SITUACAO ) ");
		} else {
			hql.append(" = :SITUACAO ");
		}
		hql.append("   AND EIS.").append(AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()).append(" BETWEEN :DT_INICIO AND :DT_FIM ");

		if (patologistaSeq != null) {
			hql.append("   AND LUI.").append(AelPatologista.Fields.SEQ.toString()).append(" = :PATOLOGISTA ");
		}
		
		if (situacaoExmAnd != null) {
			hql.append("   AND LUX.").append(AelExameAp.Fields.ETAPAS_LAUDO.toString()).append(" = :ETAPA_LAUDO ");
		}

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("UNIDADE_PATOLOGIA", unidadePatologicaSeq);
		query.setParameterList("SITUACAO", situacao);
		query.setParameter("DT_INICIO", dataInicial);
		query.setParameter("DT_FIM", dataFinal);
		if (patologistaSeq != null) {
			query.setParameterList("PATOLOGISTA", patologistaSeq);
		}
		if (situacaoExmAnd != null) {
			query.setParameter("ETAPA_LAUDO", situacaoExmAnd);
		}
		query.setResultTransformer(Transformers.aliasToBean(RelatorioExamesPendentesVO.class));
		
		List<RelatorioExamesPendentesVO> result = query.list();
		
		return result;
	}
	
	/**
	 * Consulta o AelExameAP para fazer update no exame apos ser gerado
	 * C7 #22049
	 * 
	 * @param numeroAp numero do ap do exame gerado
	 * @param numeroTipoExame tipo de exame
	 */
	public AelExameAp obterExameParaAgrupamento(Long numeroAp, Integer numeroTipoExame) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameAp.class, "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS.toString(), "lum");
		criteria.createAlias("lux." + AelExameAp.Fields.CONFIG_EX_LAUDO_UNICO.toString(), "lu2");
		
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), numeroTipoExame));
		
		Object result = executeCriteriaUniqueResult(criteria);
		
		return result != null ? (AelExameAp) result : null;
	}
	
}