package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagem;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.model.MpmItemMdtoSumario;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AfaItemGrupoMedicamentoDAO extends AbstractMedicamentoDAO<AfaItemGrupoMedicamento> {

	
	private static final long serialVersionUID = -4867718946385164334L;

	@Override
	protected DetachedCriteria pesquisarCriteria(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaItemGrupoMedicamento.class);
		criteria.createAlias(AfaItemGrupoMedicamento.Fields.MEDICAMENTO.toString(), "med", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));					
		return criteria;
	}

	public AfaItemGrupoMedicamento obterPorAfaItemGrupoMedicamento(
			Integer codigoMedicamento, Short seqGrupoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaItemGrupoMedicamento.class);

		criteria.add(Restrictions.eq(
				AfaItemGrupoMedicamento.Fields.MED_MAT_CODIGO.toString(),
				codigoMedicamento));
		criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.GMD_SEQ
				.toString(), seqGrupoMedicamento));

		return (AfaItemGrupoMedicamento) executeCriteriaUniqueResult(criteria);
	}

	public String obterInformacoesFarmacologicas(AfaMedicamento medicamento) {
		DetachedCriteria criteria = DetachedCriteria
		.forClass(AfaItemGrupoMedicamento.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AfaGrupoMedicamentoMensagem.Fields.MENSAGEM_MEDICAMENTO.toString()+"."+AfaMensagemMedicamento.Fields.DESCRICAO.toString())));
		
		criteria.createAlias(AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO.toString(), AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO.toString());
		criteria.createAlias(AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO.toString()+"."+AfaGrupoMedicamento.Fields.GRUPOS_MEDICAMENTOS_MENSAGEM.toString(), AfaGrupoMedicamento.Fields.GRUPOS_MEDICAMENTOS_MENSAGEM.toString());
		criteria.createAlias(AfaGrupoMedicamento.Fields.GRUPOS_MEDICAMENTOS_MENSAGEM.toString()+"."+AfaGrupoMedicamentoMensagem.Fields.MENSAGEM_MEDICAMENTO.toString(), AfaGrupoMedicamentoMensagem.Fields.MENSAGEM_MEDICAMENTO.toString());

		
		criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO.toString()+"."+AfaGrupoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.GRUPOS_MEDICAMENTOS_MENSAGEM.toString()+"."+AfaGrupoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(AfaGrupoMedicamentoMensagem.Fields.MENSAGEM_MEDICAMENTO.toString()+"."+AfaGrupoMedicamentoMensagem.Fields.SITUACAO.toString(), DominioSituacao.A));

		criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.MEDICAMENTO.toString(),medicamento));
		
		List<String> msgs = executeCriteria(criteria);
		
		if(!msgs.isEmpty()) {
			StringBuffer msgResult = new StringBuffer();
			for(String msg : msgs) {
				msgResult.append(msg);
			}
			return msgResult.toString();
		} else {
			return null;
		}
	}

	public List<AfaItemGrupoMedicamento> pesquisarPorSeqGrupoMedicamento(
			Short seqGrupoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaItemGrupoMedicamento.class);
		criteria.createAlias(AfaItemGrupoMedicamento.Fields.MEDICAMENTO.toString(), "med", JoinType.LEFT_OUTER_JOIN);

		if (seqGrupoMedicamento != null) {
			criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.GMD_SEQ
					.toString(), seqGrupoMedicamento));
		}

		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param ituSeq
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Short> listarOrdemSumarioPrescricao(Integer ituSeq) {
		StringBuffer hql = new StringBuffer(212);
		hql.append("SELECT grp." + AfaGrupoMedicamento.Fields.ORDEM_SUMARIO_PRESCRICAO.toString() + " ");
		hql.append("FROM AfaGrupoMedicamento as grp, ");
		hql.append("AfaItemGrupoMedicamento as igr, ");
		hql.append("MpmItemMdtoSumario ims ");
		hql.append("WHERE ims." + MpmItemMdtoSumario.Fields.ID_ITUSEQ.toString() + " = :ituSeq ");
		hql.append("AND igr." + AfaItemGrupoMedicamento.Fields.MEDICAMENTO.toString() + " = ims." + MpmItemMdtoSumario.Fields.MEDICAMENTO.toString() + " ");
		hql.append("AND igr." + AfaItemGrupoMedicamento.Fields.SITUACAO.toString() + " = 'A' ");
		hql.append("AND grp." + AfaGrupoMedicamento.Fields.SEQ.toString() + " = igr." + AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO_SEQ.toString() + " ");
		hql.append("AND grp." + AfaGrupoMedicamento.Fields.USO_SUMARIO_PRESCRICAO.toString() + " = 'S' ");
		hql.append("AND grp." + AfaGrupoMedicamento.Fields.SITUACAO.toString() + " = 'A' ");
		hql.append("ORDER BY grp." + AfaGrupoMedicamento.Fields.ORDEM_SUMARIO_PRESCRICAO.toString() + " ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("ituSeq", ituSeq);

		return query.list();
	}
	
	
	public List<String> obterInformacaoFormacologicaMedicamentosAux(Integer madMatCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaItemGrupoMedicamento.class); 
		
		criteria.createAlias(AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO.toString(), "GMD", JoinType.INNER_JOIN);
		criteria.createAlias("GMD." + AfaGrupoMedicamento.Fields.GRUPOS_MEDICAMENTOS_MENSAGEM.toString(), "GMM", JoinType.INNER_JOIN);
		criteria.createAlias("GMM." + AfaGrupoMedicamentoMensagem.Fields.MENSAGEM_MEDICAMENTO.toString(), "MEM", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(AfaItemGrupoMedicamento.Fields.MED_MAT_CODIGO.toString(), madMatCodigo));
		criteria.add(Restrictions.eq("MEM." + AfaMensagemMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.setProjection((Projections.property("MEM." + AfaMensagemMedicamento.Fields.DESCRICAO.toString())));
	
		return executeCriteria(criteria);
	}
	

}
