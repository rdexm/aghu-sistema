package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmModoUsoPrescProcedId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimentoId;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.prescricaomedica.vo.ModoUsoPrescProcedVO;

/**
 * 
 * @author rcorvalao
 */
public class MpmModoUsoPrescProcedDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmModoUsoPrescProced>{

	private static final long serialVersionUID = -711543376688302273L;

	@Override
	protected void obterValorSequencialId(MpmModoUsoPrescProced elemento) {
		if (elemento == null || elemento.getPrescricaoProcedimento() == null || elemento.getPrescricaoProcedimento().getId() == null) {
			throw new IllegalArgumentException("PrescricaoProcedimento nao foi informado corretamente.");
		}
		MpmPrescricaoProcedimentoId idPai = elemento.getPrescricaoProcedimento().getId();
		Integer modoUsoSeqP = this.buscaMaxSeqModoUsoPrescProcedimento(idPai);
		int seq = modoUsoSeqP + 1;
		if (elemento.getId() == null) {
			elemento.setId(new MpmModoUsoPrescProcedId());
		}
		elemento.getId().setSeqp(seq);
		elemento.getId().setPprAtdSeq(idPai.getAtdSeq());
		elemento.getId().setPprSeq(idPai.getSeq());
	}
	
	/**
	 * Busca o maior sequencial associado a PrescricaoProcedimento
	 * MpmModoUsoPrescProced.SEQP
	 * 
	 * @param idPai
	 * @return <code>Integer</code>
	 */
	private Integer buscaMaxSeqModoUsoPrescProcedimento(MpmPrescricaoProcedimentoId id) {
		if (id == null || id.getAtdSeq() == null || id.getSeq() == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}
		Integer returnValue = null;
		
		StringBuilder sql = new StringBuilder(100);
		sql.append("select max(mup.id.seqp) as maxSeq ");
		sql.append("from ").append(MpmModoUsoPrescProced.class.getName()).append(" mup");
		sql.append(" where mup.").append(MpmModoUsoPrescProced.Fields.PPR_ATD_SEQ.toString()).append(" = :atdSeq ");
		sql.append(" and mup.").append(MpmModoUsoPrescProced.Fields.PPR_SEQ.toString()).append(" = :seq ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("atdSeq", id.getAtdSeq());
		query.setParameter("seq", Long.valueOf(id.getSeq()));			
			
		Object maxSeq = query.getSingleResult();
		if (maxSeq == null) {
			returnValue = Integer.valueOf(0);
		} else {
			returnValue = (Integer) maxSeq; 
		}
		
		return returnValue;
	}

	/**
	 * Busca um lista de MpmModoUsoPrescProced associados a MpmPrescricaoProcedimento<br>
	 * 
	 * @author rcorvalao
	 * 08/10/2010
	 * @param id <code>MpmPrescricaoProcedimentoId</code>
	 * @return List of <code>MpmModoUsoPrescProced</code>
	 */
	@SuppressWarnings("unchecked")
	public List<MpmModoUsoPrescProced> buscaModoUsoPrescProcedimentos(MpmPrescricaoProcedimentoId id) {
		if (id == null || id.getAtdSeq() == null || id.getSeq() == null) {
			throw new IllegalArgumentException("Parametro invalido!!!");
		}
		
		StringBuilder sql = new StringBuilder(300);
		sql.append("select mup from  " + MpmModoUsoPrescProced.class.getName()  + " mup ");
		sql.append("  join mup.tipoModUsoProcedimento as tup join mup.prescricaoProcedimento as proc ");
		sql.append(" left join mup.tipoModUsoProcedimento.unidadeMedidaMedica as umm ");
		sql.append(" where mup.").append(MpmModoUsoPrescProced.Fields.PPR_ATD_SEQ.toString()).append(" = :atdSeq ");
		sql.append(" and mup.").append(MpmModoUsoPrescProced.Fields.PPR_SEQ.toString()).append(" = :seq ");
		
		Query query = createQuery(sql.toString());
		query.setParameter("atdSeq", id.getAtdSeq());
		query.setParameter("seq", Long.valueOf(id.getSeq()));			
			
		List<MpmModoUsoPrescProced> list = query.getResultList();
		
		return list;	
	}
	
	/**
	 * Retorna uma lista de MpmModoUsoPrescProced obtidos pelo adtSeq e seq
	 * @param atdSeq
	 * @param seq
	 * @return
	 */
	public List<MpmModoUsoPrescProced> pesquisarModoUsoPrescProcedimentosPorAtdSeq(Integer atdSeq, Long seq) {
		MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId(atdSeq, seq);
		return this.buscaModoUsoPrescProcedimentos(id);
	}
	
	public List<MpmModoUsoPrescProced> pesquisarModoUsoPrescProcedimentosPorID(MpmPrescricaoProcedimento prescricaoProcedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModoUsoPrescProced.class);		
		criteria.add(Restrictions.eq(MpmModoUsoPrescProced.Fields.PRESCRICAO_PROCEDIMENTO.toString(), prescricaoProcedimento));
		return executeCriteria(criteria);
	}
	
	/**
	 * Metodo para listar Prescricoes Procedimento, filtrando Por AtendimentoSeq e Seq.
	 * @param seq
	 * @param atdSeq
	 * @return
	 */
	public List<MpmModoUsoPrescProced> obterPrescricaoProcedimentoPorAtendimentoESeq(Long seq, Integer atdSeq){
		if(seq == null || atdSeq == null){
			throw new IllegalArgumentException("Os parâmetros para o metodo obterPrescricaoProcedimentoPorAtendimentoESeq não foram informados.");
		}
		List<MpmModoUsoPrescProced> listaUsoPrescProced = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmModoUsoPrescProced.class, "UPP");
				
		criteria.add(Restrictions.eq(MpmModoUsoPrescProced.Fields.PPR_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MpmModoUsoPrescProced.Fields.PPR_ATD_SEQ.toString(), atdSeq));
		
		listaUsoPrescProced = executeCriteria(criteria);
		
		return listaUsoPrescProced;
	}
	
	/**
	 * Busca conjunto de VOs com as informacoes que estao atualmente salva
	 * na base de dados para a Entidade <code>MpmModoUsoPrescProced</code>.
	 * Associados a Entidade informada no parametro <code>MpmPrescricaoProcedimento</code>.<br>
	 * 
	 * @param prescProc
	 * @return list of <code>MpmModoUsoPrescProced</code>
	 */
	public List<ModoUsoPrescProcedVO> buscaModoUsoPrescProcedimentoVos(MpmPrescricaoProcedimento prescProc) {
		if (prescProc == null || prescProc.getId() == null 
				|| prescProc.getId().getAtdSeq() == null 
				|| prescProc.getId().getSeq() == null) {
			throw new IllegalArgumentException("Parametros Invalidos!!!");
		}
		
		StringBuilder hql = new StringBuilder(100);
		hql.append("select o.").append(MpmModoUsoPrescProced.Fields.ID.toString());
		hql.append(", o.").append(MpmModoUsoPrescProced.Fields.TIPO_MOD_USO_PROCEDIMENTO.toString());
		hql.append(", o.").append(MpmModoUsoPrescProced.Fields.QUANTIDADE.toString());
		hql.append(" from ").append(MpmModoUsoPrescProced.class.getSimpleName()).append(" o ");
		hql.append("where o.").append(MpmModoUsoPrescProced.Fields.PRESCRICAO_PROCEDIMENTO.toString()).append(" = :pProcedimento ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("pProcedimento", prescProc);
		
		List<Object[]> lista = query.getResultList();
		ModoUsoPrescProcedVO vo = null;
		
		List<ModoUsoPrescProcedVO> listaModoUso = new LinkedList<ModoUsoPrescProcedVO>();
		if (lista != null && !lista.isEmpty()) {
			for (Object[] listFileds : lista) {
				vo = new ModoUsoPrescProcedVO();
				
				vo.setId((MpmModoUsoPrescProcedId)listFileds[0]);
				vo.setTipoModUsoProcedimento((MpmTipoModoUsoProcedimento)listFileds[1]);
				vo.setQuantidade((Short)listFileds[2]);
				
				listaModoUso.add(vo);
			}
		}
		
		return listaModoUso;
	}
	
	/**
	 * Retorna true quando existir prescrição com o modo de uso, e false caso contrário.
	 * 
	 * @param tipoModoUso
	 * @return
	 */
	public boolean existePrescricaoComModoUso(Short pedSeq, Short seqp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MpmModoUsoPrescProced.class);
		criteria.add(Restrictions.eq(MpmModoUsoPrescProced.Fields.TIPO_MOD_USO_PROCEDIMENTO_PED_SEQ.toString(), pedSeq));
		criteria.add(Restrictions.eq(MpmModoUsoPrescProced.Fields.TIPO_MOD_USO_PROCEDIMENTO_SEQP.toString(), seqp));

		return executeCriteriaCount(criteria) > 0;
	}
	
}
