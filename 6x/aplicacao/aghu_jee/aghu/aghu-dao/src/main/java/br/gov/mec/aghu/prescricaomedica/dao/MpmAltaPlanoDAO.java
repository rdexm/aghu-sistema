package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmAltaPlano;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;

public class MpmAltaPlanoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmAltaPlano> {

	private static final long serialVersionUID = 1672956826324296833L;

	@Override
	protected void obterValorSequencialId(MpmAltaPlano elemento) {
		
		if (elemento.getAltaSumario() == null) {
			
			throw new IllegalArgumentException("MpmAltaPlano nao esta associado corretamente a MpmAltaSumario.");
		
		}
		
		elemento.setId(elemento.getAltaSumario().getId());
	}
	
	/**
	 * Retorna uma nova referencia a Entidade informado por parâmetro<br>
	 * - com os valores atuais do banco NECESSARIOS para validacoes,<br>
	 * - NAO utiliza evict,<br>
	 * - o objeto de retorno estarah desatachado.
	 * 
	 * @param {MpmAltaPlano} altaPlano
	 * @return
	 */
	public MpmAltaPlano obterAltaPlanoOriginal(MpmAltaPlano altaPlano) {
		if (altaPlano == null || altaPlano.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!");
		}
		
		StringBuilder hql = new StringBuilder(200);
		hql.append("select o.id, o.mpmPlanoPosAltas ");
		hql.append(", o.altaSumario, o.complPlanoPosAlta ");
		hql.append(", o.descPlanoPosAlta ");
		hql.append("from MpmAltaPlano o ");
		hql.append("where o.id.apaAtdSeq = :apaAtdSeq ");
		hql.append("and o.id.apaSeq = :apaSeq ");
		hql.append("and o.id.seqp = :seqp ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("apaAtdSeq", altaPlano.getId().getApaAtdSeq());
		query.setParameter("apaSeq", altaPlano.getId().getApaSeq());
		query.setParameter("seqp", altaPlano.getId().getSeqp());
		
		List<Object[]> lista = query.getResultList();
		MpmAltaPlano obj = null;
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			obj = new MpmAltaPlano();
			for (Object[] listFields : lista) {
				obj.setId((MpmAltaSumarioId) listFields[0]);
				obj.setMpmPlanoPosAltas((MpmPlanoPosAlta) listFields[1]);
				obj.setAltaSumario((MpmAltaSumario) listFields[2]);
				obj.setComplPlanoPosAlta((String) listFields[3]);
				obj.setDescPlanoPosAlta((String) listFields[4]);
			}
		}
		
		return obj;
	}
	
	/**
	 * Retorna alta plano do sumário ativo.
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaPlano obterMpmAltaPlano(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPlano.class);
		criteria.createAlias(MpmAltaPlano.Fields.PLANO_POS_ALTA.toString(), MpmAltaPlano.Fields.PLANO_POS_ALTA.toString());
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.SEQP.toString(), altanAsuSeqp));
		/* bug #10769
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.PLANO_POS_ALTA.toString() + "." + MpmPlanoPosAlta.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		*/	
		return (MpmAltaPlano) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Retorna alta plano do sumário ativo/inativo.
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @return
	 */
	public MpmAltaPlano obterMpmAltaPlanoAtivoInativo(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPlano.class);
		criteria.createAlias(MpmAltaPlano.Fields.PLANO_POS_ALTA.toString(), MpmAltaPlano.Fields.PLANO_POS_ALTA.toString());
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.APA_ATD_SEQ.toString(), altanAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.APA_SEQ.toString(), altanApaSeq));
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.SEQP.toString(), altanAsuSeqp));
		return (MpmAltaPlano) executeCriteriaUniqueResult(criteria);
	}

	public List<MpmAltaPlano> buscaAltaPlanoPorAltaSumario(MpmAltaSumario altaSumario) {
		if (altaSumario == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPlano.class);
		
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.ALTA_SUMARIO_ID.toString(), altaSumario.getId()));
		
		return super.executeCriteria(criteria);
	}
	
	public String obterRelatorioSumario(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPlano.class);
		
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.APA_ATD_SEQ.toString(), asuApaAtdSeq));
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.APA_SEQ.toString(), asuApaSeq));
		criteria.add(Restrictions.eq(MpmAltaPlano.Fields.SEQP.toString(), asuSeqp));

		MpmAltaPlano ap = (MpmAltaPlano) executeCriteriaUniqueResult(criteria);
		if(ap==null){
			return null;
		}		
		if (ap.getMpmPlanoPosAltas().getIndOutros().equals(DominioSimNao.S)){
			return ap.getComplPlanoPosAlta();
		}else{
			return ap.getDescPlanoPosAlta() + " " + (ap.getComplPlanoPosAlta()!=null?ap.getComplPlanoPosAlta():"");
		}
	}
	
	/**
	 * Método que verifica a validação 
	 * do plano da alta do paciente. Deve 
	 * pelo menos ter um registro associado.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public List<Long> listAltaPlano(MpmAltaSumarioId altaSumarioId){
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmAltaPlano.class);
		
		criteria.setProjection(Projections.rowCount())
		.add(Restrictions.idEq(altaSumarioId));
				
		return executeCriteria(criteria);
	}

}
