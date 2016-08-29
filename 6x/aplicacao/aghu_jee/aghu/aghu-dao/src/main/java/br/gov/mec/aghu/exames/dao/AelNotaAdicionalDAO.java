package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelNotaAdicionalId;
import br.gov.mec.aghu.model.AelNotasAdicionaisHist;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class AelNotaAdicionalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelNotaAdicional> {
	
	private static final long serialVersionUID = -2007291709003452057L;


	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelNotaAdicional.class);
		return criteria;
    }
	
	@Override
	public void obterValorSequencialId(AelNotaAdicional elemento) {
		if (elemento == null || elemento.getId() == null) {
			throw new IllegalArgumentException("Parametro Invalido!!!");
		}
		AelNotaAdicionalId id = new AelNotaAdicionalId();

		id.setIseSeqp(elemento.getId().getIseSeqp());
		id.setIseSoeSeq(elemento.getId().getIseSoeSeq());

		Integer seqp = 0;
		Integer maxSeqp = this.obterAelNotaAdicionalSeqpMax(elemento);
		
		if (maxSeqp != null) {
			seqp = maxSeqp;
		}

		seqp = seqp + 1;
		id.setSeqp(seqp);

		elemento.setId(id);
	}
	
	public Integer obterAelNotaAdicionalSeqpMax(AelNotaAdicional elemento) {
		DetachedCriteria criteria = obterCriteria();

		criteria.setProjection(Projections.max(AelNotaAdicional.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SOE_SEQ.toString(), elemento.getId().getIseSoeSeq()));
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SEQP.toString(), elemento.getId().getIseSeqp()));
		Object objMax = this.executeCriteriaUniqueResult(criteria);

		return (Integer) objMax;
	}
	
	/**
	 * @HIST AelNotaAdicionalDAO.pequsisarPorSolicitacaoEItemHist
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public List<AelNotaAdicional> pesquisarNotaAdicionalPorSolicitacaoEItem(Integer soeSeq, short seqp){
		DetachedCriteria criteria = obterCriteria();
		addIseSoeSeqAndIseSeqp(soeSeq, seqp, criteria);
		criteria.createAlias(AelNotaAdicional.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria);
	}

	private void addIseSoeSeqAndIseSeqp(Integer soeSeq, short seqp,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SEQP.toString(), seqp));
	}
	
	public List<AelNotasAdicionaisHist> pesquisarNotaAdicionalPorSolicitacaoEItemHist(Integer soeSeq, short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelNotasAdicionaisHist.class);
		addIseSoeSeqAndIseSeqpHist(soeSeq, seqp, criteria);
		return executeCriteria(criteria);
	}

	private void addIseSoeSeqAndIseSeqpHist(Integer soeSeq, short seqp,
			DetachedCriteria criteria) {
		criteria.add(Restrictions.eq(AelNotasAdicionaisHist.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelNotasAdicionaisHist.Fields.ISE_SEQP.toString(), seqp));
	}
	
	public List<LinhaReportVO> pesquisarNotaAdicionalPorSolicitacaoEItemVO(Integer soeSeq, short seqp, Boolean isHist){
		DetachedCriteria criteria;
		if(isHist){
			criteria = DetachedCriteria.forClass(AelNotasAdicionaisHist.class);
		}else{
			criteria = obterCriteria();
		}
		
		addIseSoeSeqAndIseSeqp(soeSeq, seqp, criteria);
		criteria.createAlias(AelNotaAdicional.Fields.SERVIDOR.toString(), "srv");
		criteria.createAlias("srv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AelNotaAdicional.Fields.CRIADO_EM.toString()), LinhaReportVO.Fields.DATA.toString())
				.add(Projections.property(AelNotaAdicional.Fields.NOTAS_ADICIONAIS.toString()), LinhaReportVO.Fields.TEXTO1.toString())
				.add(Projections.property("pes."+RapPessoasFisicas.Fields.NOME.toString()), LinhaReportVO.Fields.TEXTO2.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(LinhaReportVO.class));
		
		return executeCriteria(criteria);
	}
	
	public Date obterMaxCriadoEm(Integer iseSoeSeq, Short iseSeqp){
		DetachedCriteria criteria = obterCriteria();
		
		criteria.setProjection(Projections.max(AelNotaAdicional.Fields.CRIADO_EM.toString()));
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SEQP.toString(), iseSeqp));
		
		Date dataNota = (Date) executeCriteriaUniqueResult(criteria);
	
		return dataNota;
	}
	
	
	public List<AelNotaAdicional> obterListaNotasAdicionaisPeloItemSolicitacaoExame(Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelNotaAdicional.class);

		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelNotaAdicional.Fields.ISE_SEQP.toString(), iseSeqp));
		criteria.addOrder(Order.desc(AelNotaAdicional.Fields.SEQP.toString()));
		
		return executeCriteria(criteria);
    }

}