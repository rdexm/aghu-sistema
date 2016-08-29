package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;

public class MpmItemPrescricaoNptDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmItemPrescricaoNpt> {

	
	
	private static final long serialVersionUID = 9130311886987857837L;

	/**
	 * ORADB cursor c_item_prcr_npt, arquivo mpmf_vis_itens_prcr.pll, procedure MPMP_POPULA_NPT.
	 * 
	 * @param mpmComposicaoPrescricaoNpts
	 * @return
	 */
	public List<MpmItemPrescricaoNpt> pesquisarMpmItemPrescricaoNpt(MpmComposicaoPrescricaoNpt mpmComposicaoPrescricaoNpts) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class, "IPN");
		criteria.add(Restrictions.eq("IPN." + MpmItemPrescricaoNpt.Fields.COMPOSICAO_PRESCRICAO_NPTS.toString(), mpmComposicaoPrescricaoNpts));
		
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString(), "CNP", Criteria.INNER_JOIN);
		criteria.createAlias("CNP." + AfaComponenteNpt.Fields.MEDICAMENTOS.toString(), "MED", Criteria.INNER_JOIN);
		
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.FORMA_DOSAGENS.toString(), "FDS", Criteria.INNER_JOIN);
		criteria.createAlias("FDS." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString() , "UMM", Criteria.LEFT_JOIN);
		
		// Código equivalente ao "order by mpmc_get_ord_compon(ipn.cnp_med_mat_codigo);"
		criteria.addOrder(Order.asc("CNP." + AfaComponenteNpt.Fields.ORDEM.toString()));
		
//		ProjectionList pList = Projections.projectionList();
//		pList.add(Property.forName("IPN." + MpmItemPrescricaoNpt.Fields.ID_SEQP.toString()));
//		pList.add(Property.forName("IPN." + MpmItemPrescricaoNpt.Fields.QTDE_PRESCRITA.toString()));
//		pList.add(Property.forName("CNP." + AfaComponenteNpts.Fields.DESCRICAO.toString()));
//		pList.add(Property.forName("MED." + AfaMedicamento.Fields.TPR_SIGLA.toString()));
//		pList.add(Property.forName("UMM." + MpmUnidadeMedidaMedicaConverter.Fields.DESCRICAO.toString()));
//		
//		criteria.setProjection(pList);
		
	
		return executeCriteria(criteria);
		
	}

	/**
	 * Metodo para listar itens prescricao npt, 
	 * filtrando por pnpAtdSeq, pnpSeq, pnpCptSeqp (parte do id) e ordenando ascendentemente pela descricao do componente npt.
	 * @param mpmComposicaoPrescricaoNpts
	 * @return
	 */
	public List<MpmItemPrescricaoNpt> listarItensPrescricaoNpt(Integer pnpAtdSeq, Integer pnpSeq, Short pnpCptSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class, "IPN");
  
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString(), "CNP", Criteria.INNER_JOIN);
		criteria.createAlias("CNP." + AfaComponenteNpt.Fields.MEDICAMENTOS.toString(), "MED", Criteria.INNER_JOIN);
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.FORMA_DOSAGENS.toString(), "FDS", Criteria.INNER_JOIN);
		criteria.createAlias("FDS." + AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "UMM", Criteria.LEFT_JOIN);
		
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_ATD_SEQ.toString(), pnpAtdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_SEQP.toString(), pnpCptSeqp));
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_SEQ.toString(), pnpSeq));

		criteria.addOrder(Order.asc("CNP." + AfaComponenteNpt.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	public List<MpmItemPrescricaoNpt> listarItensPorMatCodigo(Integer cod) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class, "IPN");
  
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString(), "CNP");
		
		criteria.add(Restrictions.eq("CNP."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString(), cod));
		
		
		return executeCriteria(criteria,0,1,null,false);
	}	

	public List<MpmItemPrescricaoNpt> listarItensPorMatCodigoSeqp(Integer matCodigo, Short seqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class);
  
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.PCN_CNP_MED_MAT_CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.PCN_SEQP.toString(), seqp));
		
		return executeCriteria(criteria,0,1,null,false);
	}

	public List<MpmItemPrescricaoNpt> listarItensPrescricaoNpt(Integer pnpAtdSeq, Integer pnpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_ATD_SEQ.toString(), pnpAtdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_SEQ.toString(), pnpSeq));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.ID.toString()).as(MpmItemPrescricaoNpt.Fields.ID.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString()).as(MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.FORMA_DOSAGENS.toString()).as(MpmItemPrescricaoNpt.Fields.FORMA_DOSAGENS.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.QTDE_PRESCRITA.toString()).as(MpmItemPrescricaoNpt.Fields.QTDE_PRESCRITA.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.QTDE_BASE_CALCULO.toString()).as(MpmItemPrescricaoNpt.Fields.QTDE_BASE_CALCULO.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.QTDE_CALCULADA.toString()).as(MpmItemPrescricaoNpt.Fields.QTDE_CALCULADA.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.TIPO_PARAM_CALCULO.toString()).as(MpmItemPrescricaoNpt.Fields.TIPO_PARAM_CALCULO.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.TOT_PARAM_CALCULO.toString()).as(MpmItemPrescricaoNpt.Fields.TOT_PARAM_CALCULO.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.UNIDADE_MEDIDA_MEDICAS.toString()).as(MpmItemPrescricaoNpt.Fields.UNIDADE_MEDIDA_MEDICAS.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.PCN_CNP_MED_MAT_CODIGO.toString()).as(MpmItemPrescricaoNpt.Fields.PCN_CNP_MED_MAT_CODIGO.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.PCN_SEQP.toString()).as(MpmItemPrescricaoNpt.Fields.PCN_SEQP.toString()))
				.add(Projections.property(MpmItemPrescricaoNpt.Fields.PERC_PARAM_CALCULO.toString()).as(MpmItemPrescricaoNpt.Fields.PERC_PARAM_CALCULO.toString()))
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(MpmItemPrescricaoNpt.class));
		
		return executeCriteria(criteria);
	}
	
	// #990 C5
	public List<MpmItemPrescricaoNptVO> buscarComponentesComposicaoNptDescritaPorId(Integer cptPnpAtdSeq, Integer cntPnpSeq, Short cptSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class, "IPN");
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.COMPONENTE_NPTS,"CNP",JoinType.INNER_JOIN);
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.FORMA_DOSAGENS,"FDS",JoinType.INNER_JOIN);
		criteria.createAlias("IPN." + MpmItemPrescricaoNpt.Fields.UNIDADE_MEDIDA_MEDICAS,"UMM",JoinType.INNER_JOIN);
		
		
		criteria.add(Restrictions.eq("IPN." + MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_ATD_SEQ , cptPnpAtdSeq));
		criteria.add(Restrictions.eq("IPN." + MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_SEQ , cntPnpSeq));
		criteria.add(Restrictions.eq("IPN." + MpmItemPrescricaoNpt.Fields.ID_CPT_SEQP , cptSeqp));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_ATD_SEQ), MpmItemPrescricaoNptVO.Fields.CPT_PNP_ATD_SEQ .toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_SEQ), MpmItemPrescricaoNptVO.Fields.CPT_PNP_SEQ.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.ID_CPT_SEQP), MpmItemPrescricaoNptVO.Fields.CPT_SEQP.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.ID_SEQP), MpmItemPrescricaoNptVO.Fields.SEQP.toString())
				.add(Projections.property("CNP." + AfaComponenteNpt.Fields.MED_MAT_CODIGO), MpmItemPrescricaoNptVO.Fields.CNP_MED_MAT_CODIGO.toString())
				.add(Projections.property("CNP." + AfaComponenteNpt.Fields.DESCRICAO ), MpmItemPrescricaoNptVO.Fields.DESCRICAO_COMPONENTE.toString())
				.add(Projections.property("CNP." + AfaComponenteNpt.Fields.IDENTIF_COMPONENTE ), MpmItemPrescricaoNptVO.Fields.IDENTIF_COMPONENTE.toString())
				.add(Projections.property("FDS." + AfaFormaDosagem.Fields.SEQ), MpmItemPrescricaoNptVO.Fields.FDS_SEQ .toString())
				.add(Projections.property("UMM." + MpmUnidadeMedidaMedica.Fields.DESCRICAO), MpmItemPrescricaoNptVO.Fields.UNIDADE_COMPONENTE.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.QTDE_PRESCRITA), MpmItemPrescricaoNptVO.Fields.QTDE_PRESCRITA.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.QTDE_BASE_CALCULO), MpmItemPrescricaoNptVO.Fields.QTDE_BASE_CALCULO.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.QTDE_CALCULADA), MpmItemPrescricaoNptVO.Fields.QTDE_CALCULADA.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.TOT_PARAM_CALCULO), MpmItemPrescricaoNptVO.Fields.TOT_PARAM_CALCULO.toString())
				.add(Projections.property("UMM." + MpmUnidadeMedidaMedica.Fields.SEQ), MpmItemPrescricaoNptVO.Fields.UMM_SEQ.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.PCN_CNP_MED_MAT_CODIGO), MpmItemPrescricaoNptVO.Fields.PCN_CNP_MED_MAT_CODIGO.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.PCN_SEQP), MpmItemPrescricaoNptVO.Fields.PCN_SEQP.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.PERC_PARAM_CALCULO), MpmItemPrescricaoNptVO.Fields.PERC_PARAM_CALCULO.toString())
				.add(Projections.property("IPN." + MpmItemPrescricaoNpt.Fields.TIPO_PARAM_CALCULO), MpmItemPrescricaoNptVO.Fields.TIPO_PARAM_CALCULO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(MpmItemPrescricaoNptVO.class));
		criteria.addOrder(Order.asc("IPN."+MpmItemPrescricaoNpt.Fields.ID_SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Short obterUltimoSeqpComponente(Integer cptPnpAtdSeq, Integer cptPnpSeq, Short cptSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class);
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_ATD_SEQ .toString() , cptPnpAtdSeq));
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_PNP_SEQ.toString() , cptPnpSeq));
		criteria.add(Restrictions.eq(MpmItemPrescricaoNpt.Fields.ID_CPT_SEQP.toString() , cptSeqp));
		criteria.setProjection(Projections.max(MpmItemPrescricaoNpt.Fields.ID_SEQP.toString()));
		Object seqp = executeCriteriaUniqueResult(criteria);
		return seqp == null ? Short.valueOf("0") : (Short) seqp;
	}

	public List<MpmItemPrescricaoNpt> listarItensPrescricaoNptPorComposicao(MpmComposicaoPrescricaoNpt mpmComposicaoPrescricaoNpts) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmItemPrescricaoNpt.class, "IPN");
		criteria.add(Restrictions.eq("IPN." + MpmItemPrescricaoNpt.Fields.COMPOSICAO_PRESCRICAO_NPTS.toString(), mpmComposicaoPrescricaoNpts));
		return executeCriteria(criteria);
		
	}
}