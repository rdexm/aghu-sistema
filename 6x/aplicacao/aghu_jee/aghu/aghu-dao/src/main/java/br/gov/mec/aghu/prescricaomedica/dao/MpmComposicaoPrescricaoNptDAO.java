package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;

public class MpmComposicaoPrescricaoNptDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmComposicaoPrescricaoNpt> {
	
	
	private static final long serialVersionUID = 6185293370852021459L;

	/**
	 * ORADB cursor c_compos_prcr_npt, arquivo mpmf_vis_itens_prcr.pll, procedure MPMP_POPULA_NPT.
	 *
	 * @author mtocchetto
	 * @param mpmPrescricaoNpts
	 * @return
	 */
	public List<MpmComposicaoPrescricaoNpt> pesquisarMpmComposicaoPrescricaoNpts(MpmPrescricaoNpt mpmPrescricaoNpts) {	
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmComposicaoPrescricaoNpt.class, "CPT");
		criteria.add(Restrictions.eq("CPT."+MpmComposicaoPrescricaoNpt.Fields.MPM_PRESCRICAO_NPTS.toString(), mpmPrescricaoNpts));

		criteria.createAlias("CPT."+MpmComposicaoPrescricaoNpt.Fields.TIPO_VELOCIDADE_ADMINISTRACOES.toString(), "TVA", Criteria.INNER_JOIN);
		criteria.createAlias("CPT."+MpmComposicaoPrescricaoNpt.Fields.TIPO_COMPOSICOES.toString(), "TIC", Criteria.INNER_JOIN);

		criteria.addOrder(Order.asc("TIC." + AfaTipoComposicoes.Fields.ORDEM.toString()));
		
//		ProjectionList pList = Projections.projectionList();
//		pList.add(Property.forName("CPT." + MpmComposicaoPrescricaoNpt.Fields.ID_SEQP.toString()));
//		pList.add(Property.forName("CPT." + MpmComposicaoPrescricaoNpt.Fields.VELOCIDADE_ADMINISTRACAO.toString()));
//		pList.add(Property.forName("CPT." + MpmComposicaoPrescricaoNpt.Fields.QTDE_HORAS_CORRER.toString()));
//		pList.add(Property.forName("TVA." + AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString()));
//		pList.add(Property.forName("TIC." + AfaTipoComposicoes.Fields.DESCRICAO.toString()));
//
//		criteria.setProjection(pList);		
		
		// FIXME Tocchetto ver código equivalente para "order by mpmc_get_ord_compos(cpt.tic_seq);"
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa Composições de Nutrição Parental Total pela prescrição NPTS
	 * @param pmeAtdSeq
	 * @param seq
	 * @return
	 */
	public List<MpmComposicaoPrescricaoNpt> pesquisarComposicoesPrescricaoNpt(Integer pmeAtdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmComposicaoPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmComposicaoPrescricaoNpt.Fields.ID_PNP_ATD_SEQ.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ.toString(), seq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmComposicaoPrescricaoNpt.Fields.ID.toString()).as(MpmComposicaoPrescricaoNpt.Fields.ID.toString()))
				.add(Projections.property(MpmComposicaoPrescricaoNpt.Fields.TIPO_VELOCIDADE_ADMINISTRACOES.toString()).as(MpmComposicaoPrescricaoNpt.Fields.TIPO_VELOCIDADE_ADMINISTRACOES.toString()))
				.add(Projections.property(MpmComposicaoPrescricaoNpt.Fields.TIPO_COMPOSICOES.toString()).as(MpmComposicaoPrescricaoNpt.Fields.TIPO_COMPOSICOES.toString()))
				.add(Projections.property(MpmComposicaoPrescricaoNpt.Fields.VELOCIDADE_ADMINISTRACAO.toString()).as(MpmComposicaoPrescricaoNpt.Fields.VELOCIDADE_ADMINISTRACAO.toString()))
				.add(Projections.property(MpmComposicaoPrescricaoNpt.Fields.QTDE_HORAS_CORRER.toString()).as(MpmComposicaoPrescricaoNpt.Fields.QTDE_HORAS_CORRER.toString()))
		);
		criteria.setResultTransformer(Transformers.aliasToBean(MpmComposicaoPrescricaoNpt.class));
		return executeCriteria(criteria);
	}

	/**
	 * Metodo para listar Composições de Nutrição Parental Total, filtrando por parte do ID (pelo seq do atendimento da prescricao medica e
	 *  pelo seq da composicao da prescricao NPT).
	 * @param pmeAtdSeq
	 * @param seq
	 * @return retorna uma lista de Composições de Nutrição Parental Total ordenadas ascendentemente pela 
	 * descricao do tipo composição.
	 */
	public List<MpmComposicaoPrescricaoNpt> listarComposicoesPrescricaoNpt(Integer pmeAtdSeq, Long seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmComposicaoPrescricaoNpt.class, "NPT");
		
		criteria.createAlias("NPT."+ MpmComposicaoPrescricaoNpt.Fields.TIPO_VELOCIDADE_ADMINISTRACOES.toString(), "TVA", Criteria.INNER_JOIN);
		criteria.createAlias("NPT."+ MpmComposicaoPrescricaoNpt.Fields.TIPO_COMPOSICOES.toString(), "TIC", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq(MpmComposicaoPrescricaoNpt.Fields.ID_PNP_ATD_SEQ.toString(), pmeAtdSeq));
		criteria.add(Restrictions.eq(MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ.toString(), seq));
		
		criteria.addOrder(Order.asc("TIC."+AfaTipoComposicoes.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	// #990 C4
	public List<MpmComposicaoPrescricaoNptVO> buscarComposicoesNptDescritaPorId(Integer seq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmComposicaoPrescricaoNpt.class, "CPT");
		criteria.createAlias("CPT." + MpmComposicaoPrescricaoNpt.Fields.MPM_PRESCRICAO_NPTS , "PNP", JoinType.INNER_JOIN);
		criteria.createAlias("CPT." + MpmComposicaoPrescricaoNpt.Fields.TIPO_COMPOSICOES , "TIC", JoinType.INNER_JOIN);
		criteria.createAlias("CPT." + MpmComposicaoPrescricaoNpt.Fields.TIPO_VELOCIDADE_ADMINISTRACOES , "TVA", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("PNP." + MpmPrescricaoNpt.Fields.ID_SEQ, seq));
		criteria.add(Restrictions.eq("PNP." + MpmPrescricaoNpt.Fields.ID_ATD_SEQ, atdSeq));

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CPT." + MpmComposicaoPrescricaoNpt.Fields.ID_PNP_ATD_SEQ), MpmComposicaoPrescricaoNptVO.Fields.PNP_ATD_SEQ.toString())
				.add(Projections.property("CPT." + MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ), MpmComposicaoPrescricaoNptVO.Fields.PNP_SEQ.toString())
				.add(Projections.property("CPT." + MpmComposicaoPrescricaoNpt.Fields.ID_SEQP), MpmComposicaoPrescricaoNptVO.Fields.SEQP.toString())
				.add(Projections.property("TIC." + AfaTipoComposicoes.Fields.SEQ), MpmComposicaoPrescricaoNptVO.Fields.TIC_SEQ.toString())
				.add(Projections.property("TIC." + AfaTipoComposicoes.Fields.DESCRICAO), MpmComposicaoPrescricaoNptVO.Fields.COMPOSICAO_DESCRICAO.toString())
				.add(Projections.property("TVA." + AfaTipoVelocAdministracoes.Fields.SEQ), MpmComposicaoPrescricaoNptVO.Fields.TVA_SEQ.toString())
				.add(Projections.property("TVA." + AfaTipoVelocAdministracoes.Fields.DESCRICAO), MpmComposicaoPrescricaoNptVO.Fields.UNIDADE.toString())
				.add(Projections.property("CPT." + MpmComposicaoPrescricaoNpt.Fields.VELOCIDADE_ADMINISTRACAO), MpmComposicaoPrescricaoNptVO.Fields.VELOCIDADE_ADMINISTRACAO.toString())
				.add(Projections.property("CPT." + MpmComposicaoPrescricaoNpt.Fields.QTDE_HORAS_CORRER), MpmComposicaoPrescricaoNptVO.Fields.QTDE_HORAS_CORRER.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(MpmComposicaoPrescricaoNptVO.class));
		criteria.addOrder(Order.asc("CPT."+MpmComposicaoPrescricaoNpt.Fields.ID_SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Short obterUltimoSeqpComposicao(Integer pnpSeq, Integer pnpAtdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmComposicaoPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmComposicaoPrescricaoNpt.Fields.ID_PNP_SEQ.toString() , pnpSeq));
		criteria.add(Restrictions.eq(MpmComposicaoPrescricaoNpt.Fields.ID_PNP_ATD_SEQ.toString() , pnpAtdSeq));
		criteria.setProjection(Projections.max(MpmComposicaoPrescricaoNpt.Fields.ID_SEQP.toString()));
		Object seqp = executeCriteriaUniqueResult(criteria);
		return seqp == null ? Short.valueOf("0") : (Short) seqp;
	}
	
	public List<MpmComposicaoPrescricaoNpt> listarComposicoesPorPrescricaoNpt(MpmPrescricaoNpt mpmPrescricaoNpts) {	
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmComposicaoPrescricaoNpt.class, "CPT");
		criteria.add(Restrictions.eq("CPT."+MpmComposicaoPrescricaoNpt.Fields.MPM_PRESCRICAO_NPTS.toString(), mpmPrescricaoNpts));
		return executeCriteria(criteria);
	}
}
