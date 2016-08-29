package br.gov.mec.aghu.emergencia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamTrgEncExternos;
import br.gov.mec.aghu.model.MamTrgGravidade;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamGravidadeDAO extends BaseDao<MamGravidade> {

	private static final long serialVersionUID = -8146853759339876322L;

	public List<MamGravidade> pesquisarGravadadePorTriagem(MamTriagens mamTriagens) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamGravidade.class, "MamGravidade");
		criteria.createAlias("MamGravidade." + MamGravidade.Fields.TRIAGEM_GRAVIDADE.toString(), "MamTrgGravidade", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("MamTrgGravidade." + MamTrgGravidade.Fields.MAM_TRIAGENS.toString(), mamTriagens));
		criteria.addOrder(Order.desc("MamTrgGravidade." + MamTrgGravidade.Fields.SEQP.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Busca MamGravidades ativas por código ou descricao
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<MamGravidade> pesquisarGravidadesAtivasPorCodigoDescricao(Object objPesquisa, Integer pcrSeq) {
		final DetachedCriteria criteria = montarCriteriaPesquisarGravidadesAtivasPorCodigoDescricao(objPesquisa, pcrSeq);
		criteria.addOrder(Order.asc(MamGravidade.Fields.ORDEM.toString()));
		return this.executeCriteria(criteria);
	}

	/**
	 * Busca MamGravidades ativas por código ou descricao
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public Long pesquisarGravidadesAtivasPorCodigoDescricaoCount(Object objPesquisa, Integer pcrSeq) {
		final DetachedCriteria criteria = montarCriteriaPesquisarGravidadesAtivasPorCodigoDescricao(objPesquisa, pcrSeq);
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Monta criteria MamGravidades ativas por código ou descricao
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public DetachedCriteria montarCriteriaPesquisarGravidadesAtivasPorCodigoDescricao(Object objPesquisa, Integer pcrSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamGravidade.class);

		if (objPesquisa != null) {
			if (CoreUtil.isNumeroInteger(objPesquisa.toString())) {
				criteria.add(Restrictions.eq(MamGravidade.Fields.SEQ.toString(), Integer.valueOf(objPesquisa.toString())));
			} else {
				criteria.add(Restrictions.ilike(MamGravidade.Fields.DESCRICAO.toString(), objPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		if(pcrSeq != null) {
			criteria.add(Restrictions.eq(MamGravidade.Fields.PCR_SEQ.toString(), pcrSeq));
		}
		criteria.add(Restrictions.eq(MamGravidade.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	/**
	 * Monta a criteria de MamGravidade por mamProtClassifRisco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	private DetachedCriteria montarCriteriaGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamGravidade.class);
		criteria.add(Restrictions.eq(MamGravidade.Fields.PROT_CLASSIF_RISCO.toString(), mamProtClassifRisco));
		return criteria;
	}

	/**
	 * Pesquisa MamGravidade por mamProtClassifRisco
	 * 
	 * C2 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public List<MamGravidade> pesquisarGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = this.montarCriteriaGravidadePorProtocolo(mamProtClassifRisco);
		criteria.addOrder(Order.asc(MamGravidade.Fields.IND_SITUACAO.toString()));
		criteria.addOrder(Order.asc(MamGravidade.Fields.ORDEM.toString()));
		return super.executeCriteria(criteria);
	}

	/**
	 * Pesquisa se existe MamGravidade por mamProtClassifRisco e descricao
	 * 
	 * C3 de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @param descricao
	 * @return
	 */
	public Boolean existsGravidadePorProtocoloDescricao(MamProtClassifRisco mamProtClassifRisco, String descricao) {
		DetachedCriteria criteria = this.montarCriteriaGravidadePorProtocolo(mamProtClassifRisco);
		criteria.add(Restrictions.eq(MamGravidade.Fields.DESCRICAO.toString(), descricao));
		return super.executeCriteriaExists(criteria);
	}

	/**
	 * Pesquisa se existe MamGravidade por mamProtClassifRisco e descricao
	 * 
	 * "C3" de #32287 - Manter cadastro de graus de gravidade utilizados nos protocolos de classificação de risco
	 * 
	 * @param mamProtClassifRisco
	 * @param descricao
	 * @return
	 */
	public Boolean existsGravidadePorSeqProtocoloDescricao(Short seq, MamProtClassifRisco mamProtClassifRisco, String descricao) {
		DetachedCriteria criteria = this.montarCriteriaGravidadePorProtocolo(mamProtClassifRisco);
		criteria.add(Restrictions.ne(MamGravidade.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(MamGravidade.Fields.DESCRICAO.toString(), descricao));
		return super.executeCriteriaExists(criteria);
	}
	
	public List<MamGravidade> pesquisarGravadadePorTriagemEncExterno(Long seqTriagem, Short seqpTriagem) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(MamGravidade.class, "GRV");
		criteria.createAlias("GRV." + MamGravidade.Fields.TRIAGEM_GRAVIDADE.toString(), "TGG", JoinType.INNER_JOIN);
		criteria.createAlias("TGG." + MamTrgGravidade.Fields.MAM_TRIAGENS.toString(), "TRG", JoinType.INNER_JOIN);
		criteria.createAlias("TRG." + MamTriagens.Fields.MAM_TRG_ENC_EXTERNO.toString(), "TEE", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("TEE." + MamTrgEncExternos.Fields.TRG_SEQ.toString(), seqTriagem));
		criteria.add(Restrictions.eq("TEE." + MamTrgEncExternos.Fields.SEQP.toString(), seqpTriagem));
		criteria.add(Restrictions.isNull("TEE." + MamTrgEncExternos.Fields.DTHR_ESTORNO.toString()));
		criteria.addOrder(Order.desc("TGG." + MamTrgGravidade.Fields.SEQP.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Pesquisa se existe MamGravidade por mamProtClassifRisco
	 * 
	 * C5 de #32283
	 * 
	 * @param mamProtClassifRisco
	 * @return
	 */
	public Boolean existsGravidadePorProtocolo(MamProtClassifRisco mamProtClassifRisco) {
		DetachedCriteria criteria = this.montarCriteriaGravidadePorProtocolo(mamProtClassifRisco);
		return super.executeCriteriaExists(criteria);
	}
	
	
	/**
	 * Verifica se existe gravidade cadastrada com determinada ordem
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public boolean verificarGravidadeComMesmaOrdem(MamProtClassifRisco mamProtClassifRisco, Short ordem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamGravidade.class);
		criteria.add(Restrictions.eq(MamGravidade.Fields.PROT_CLASSIF_RISCO.toString(), mamProtClassifRisco));
		criteria.add(Restrictions.eq(MamGravidade.Fields.ORDEM.toString(), ordem));
		return super.executeCriteriaExists(criteria);
	}
	
//	DetachedCriteria criteria = DetachedCriteria.forClass(MamObrigatoriedade.class);
//	criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.MAM_DESCRITOR.toString(), mamDescritor));
//	criteria.add(Restrictions.eq(MamObrigatoriedade.Fields.ICE_SEQ.toString(), iceSeq));
//	return super.executeCriteriaExists(criteria);
	
}