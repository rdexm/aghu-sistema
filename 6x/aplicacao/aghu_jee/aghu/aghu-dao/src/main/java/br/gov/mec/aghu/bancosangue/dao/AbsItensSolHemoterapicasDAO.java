package br.gov.mec.aghu.bancosangue.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.bancosangue.vo.ItemSolicitacaoHemoterapicaVO;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.AbsItensSolHemoterapicas;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;

public class AbsItensSolHemoterapicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsItensSolHemoterapicas>{


	private static final long serialVersionUID = -4871179344476437281L;

	public List<ItemSolicitacaoHemoterapicaVO> obterListaItemSolicitacaoHemoterapicaVO(Integer atdSeq, Integer seq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItensSolHemoterapicas.class);
		
		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString()),ItemSolicitacaoHemoterapicaVO.Fields.SHE_ATD_SEQ.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString()),ItemSolicitacaoHemoterapicaVO.Fields.SHE_SEQ.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SEQUENCIA.toString()),ItemSolicitacaoHemoterapicaVO.Fields.SEQUENCIA.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.ABS_PROCED_HEMOTERAPICOS_CODIGO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.PHE_CODIGO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS_CODIGO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.CSA_CODIGO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.TIPO_FREQ_APRAZAMENTO_SEQ.toString()),ItemSolicitacaoHemoterapicaVO.Fields.TFQ_SEQ.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_IRRADIADO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_IRRADIADO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_AFERESE.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_AFERESE.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_FILTRADO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_FILTRADO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_IMPR_LAUDO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_IMPR_LAUDO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_LAVADO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_LAVADO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.FREQUENCIA.toString()),ItemSolicitacaoHemoterapicaVO.Fields.FREQUENCIA.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.QTDE_APLICACOES.toString()),ItemSolicitacaoHemoterapicaVO.Fields.QTDE_APLICACOES.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.QTDE_UNIDADES.toString()),ItemSolicitacaoHemoterapicaVO.Fields.QTDE_UNIDADES.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.QTDE_ML.toString()),ItemSolicitacaoHemoterapicaVO.Fields.QTDE_ML.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.DTHR_EXEC_PROCEDIMENTO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.DTHR_EXEC_PROCEDIMENTO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.DTHR_DIGT_EXECUCAO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.DTHR_DIGT_EXECUCAO.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SERVIDOR_MAT.toString()),ItemSolicitacaoHemoterapicaVO.Fields.MAT_SERVIDOR.toString())
//						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SERVIDOR_VIN.toString()),ItemSolicitacaoHemoterapicaVO.Fields.VIN_SERVIDOR.toString())
		);

		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString(), seq));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemSolicitacaoHemoterapicaVO.class));
		
		return executeCriteria(criteria);
	}

	
	public ItemSolicitacaoHemoterapicaVO obterItemSolicitacaoHemoterapicaVO(Integer atdSeq, Integer seq, Short sequencia) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItensSolHemoterapicas.class);
		
		criteria
		.setProjection(Projections
						.projectionList()
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString()),ItemSolicitacaoHemoterapicaVO.Fields.SHE_ATD_SEQ.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString()),ItemSolicitacaoHemoterapicaVO.Fields.SHE_SEQ.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SEQUENCIA.toString()),ItemSolicitacaoHemoterapicaVO.Fields.SEQUENCIA.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.ABS_PROCED_HEMOTERAPICOS_CODIGO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.PHE_CODIGO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS_CODIGO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.CSA_CODIGO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.TIPO_FREQ_APRAZAMENTO_SEQ.toString()),ItemSolicitacaoHemoterapicaVO.Fields.TFQ_SEQ.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_IRRADIADO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_IRRADIADO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_AFERESE.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_AFERESE.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_FILTRADO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_FILTRADO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_IMPR_LAUDO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_IMPR_LAUDO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.IND_LAVADO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.IND_LAVADO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.FREQUENCIA.toString()),ItemSolicitacaoHemoterapicaVO.Fields.FREQUENCIA.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.QTDE_APLICACOES.toString()),ItemSolicitacaoHemoterapicaVO.Fields.QTDE_APLICACOES.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.QTDE_UNIDADES.toString()),ItemSolicitacaoHemoterapicaVO.Fields.QTDE_UNIDADES.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.QTDE_ML.toString()),ItemSolicitacaoHemoterapicaVO.Fields.QTDE_ML.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.DTHR_EXEC_PROCEDIMENTO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.DTHR_EXEC_PROCEDIMENTO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.DTHR_DIGT_EXECUCAO.toString()),ItemSolicitacaoHemoterapicaVO.Fields.DTHR_DIGT_EXECUCAO.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SERVIDOR_MAT.toString()),ItemSolicitacaoHemoterapicaVO.Fields.MAT_SERVIDOR.toString())
						.add(Projections.property(AbsItensSolHemoterapicas.Fields.SERVIDOR_VIN.toString()),ItemSolicitacaoHemoterapicaVO.Fields.VIN_SERVIDOR.toString())
		);

		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SEQUENCIA.toString(), sequencia));

		criteria.setResultTransformer(Transformers.aliasToBean(ItemSolicitacaoHemoterapicaVO.class));
		
		return (ItemSolicitacaoHemoterapicaVO)executeCriteriaUniqueResult(criteria);
	}
	
	/**
		select 
	  	ish.ind_irradiado, ish.ind_filtrado, ish.ind_lavado, ish.ind_aferese,
    	ish.frequencia,	 ish.qtde_aplicacoes,ish.qtde_unidades, ish.qtde_ml,
		phe.descricao desc_phe, csa.descricao desc_csa, tfq.sintaxe sint_tfq,
		tfq.descricao desc_tfq
      from   
      	mpm_tipo_freq_aprazamentos tfq, abs_componentes_sanguineos csa,
		abs_proced_hemoterapicos phe, abs_itens_sol_hemoterapicas ish
      where  
        csa.codigo(+)     =   ish.csa_codigo 
        and  phe.codigo(+)     =   ish.phe_codigo
        and  tfq.seq(+)        =   ish.tfq_seq	
        and  ish.she_atd_seq   =   p_atd_seq 
        and  ish.she_seq       =   cur_pdt_seq;
	 */
	public List<AbsItensSolHemoterapicas> buscaItensSolHemoterapicas(Integer pAtdSeq, Integer curPdtSeq) {
		if (pAtdSeq == null || curPdtSeq == null) {
			throw new IllegalArgumentException("buscaItensSolHemoterapicas: um dos parametro obrigatorios nao foi informado.");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItensSolHemoterapicas.class, "ish");
		
		criteria.createAlias("ish."+AbsItensSolHemoterapicas.Fields.TIPO_FREQ_APRAZAMENTO.toString(), "tfq",Criteria.LEFT_JOIN);
		criteria.createAlias("ish."+AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS.toString(), "codigo",Criteria.LEFT_JOIN);
		criteria.createAlias("ish."+AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS.toString(), "phe",Criteria.LEFT_JOIN);
	    
		// RESTRICTIONS: ish.she_atd_seq   =   p_atd_seq  and  ish.she_seq       =   cur_pdt_seq
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString(), pAtdSeq ));							
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString(), curPdtSeq ));			
		
		return super.executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisa itens de uma hemoterapia
	 * @param sheAtdSeq
	 * @param sheSeq
	 * @return
	 */
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapia(Integer sheAtdSeq, Integer sheSeq) {
		DetachedCriteria criteria = obterCriteriaItensHemoterapiaPorHemoterapia(sheAtdSeq, sheSeq);
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaItensHemoterapiaPorHemoterapia(Integer sheAtdSeq, Integer sheSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItensSolHemoterapicas.class);
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString(), sheAtdSeq));
		criteria.add(Restrictions.eq(AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString(), sheSeq));
		return criteria;
	}
	
	/**
	 * Pesquisa os ítens que são grupos sanguíneos
	 * @param sheAtdSeq
	 * @param sheSeq
	 * @return
	 */
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapiaComponentesSanguineos(Integer sheAtdSeq, Integer sheSeq){
		DetachedCriteria criteria = obterCriteriaItensHemoterapiaPorHemoterapia(sheAtdSeq, sheSeq);
		criteria.add(Restrictions.isNotNull(AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AbsItensSolHemoterapicas> pesquisarItensHemoterapiaProcedimentos(Integer sheAtdSeq, Integer sheSeq){
		DetachedCriteria criteria = obterCriteriaItensHemoterapiaPorHemoterapia(sheAtdSeq, sheSeq);
		criteria.add(Restrictions.isNotNull(AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarItensSolicitacaoHemoterapicaCount(
			Integer sheAtdSeq, Integer sheSeq, Short sequencia) {
		DetachedCriteria criteria = createItensSolicitacaoHemoterapicaCriteria(
				sheAtdSeq, sheSeq, sequencia);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createItensSolicitacaoHemoterapicaCriteria(
			Integer sheAtdSeq, Integer sheSeq, Short sequencia) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AbsItensSolHemoterapicas.class);

		if (sheAtdSeq != null) {
			criteria.add(Restrictions.eq(
					AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString(),
					sheAtdSeq));
		}

		if (sheSeq != null) {
			criteria
					.add(Restrictions.eq(
							AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString(),
							sheSeq));
		}

		if (sequencia != null) {
			criteria.add(Restrictions.eq(
					AbsItensSolHemoterapicas.Fields.SEQUENCIA.toString(),
					sequencia));
		}

		return criteria;
	}
	
	/**
	 * Pesquisa a lista de ítens de solicitação hemoterápica para a exibição no POL
	 * @param paciente
	 * @return
	 */
	public List<AbsItensSolHemoterapicas> pesquisarItensComponentesSanguineosPrescricaoPOL(Integer codigoPaciente, Date dataBancoSangue){
		
		DetachedCriteria criteria = obterCriteriaItensComponentesSanguineosPrescricao(
				codigoPaciente, dataBancoSangue);
		return executeCriteria(criteria);
	}


	private DetachedCriteria obterCriteriaItensComponentesSanguineosPrescricao(
			Integer codigoPaciente, Date dataBancoSangue) {
		DetachedCriteria criteria = createCriteriaItensSolHemoterapicasPrescricaoPOL(codigoPaciente);
		criteria.add(Restrictions.isNotNull(AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS.toString()));
		
		if (dataBancoSangue != null){
			Criterion criterionDthrValidaNotNull = Restrictions
			.isNotNull("hemoterapia."+ AbsSolicitacoesHemoterapicas.Fields.DTHR_VALIDA.toString());
			Criterion criterionAnd1 = Restrictions.and(criterionDthrValidaNotNull,
					Restrictions.lt("hemoterapia."
							+ AbsSolicitacoesHemoterapicas.Fields.DTHR_VALIDA
							.toString(), dataBancoSangue));
			
			Criterion criterionDthrValidaNull = Restrictions
					.isNull("hemoterapia."
							+ AbsSolicitacoesHemoterapicas.Fields.DTHR_VALIDA
									.toString());
			Criterion criterionAnd2 = Restrictions.and(criterionDthrValidaNull,
					Restrictions.lt("hemoterapia."
							+ AbsSolicitacoesHemoterapicas.Fields.DT_HR_SOLICITACAO
							.toString(), dataBancoSangue));
			
			Criterion criterionOr = Restrictions.or(criterionAnd1, criterionAnd2);
			criteria.add(criterionOr);			
		}
		return criteria;
	}
	
	
	public boolean verificarExisteItensComponentesSanguineosPescricao(Integer codigoPaciente, Date dataBancoSangue){		
	
		DetachedCriteria criteria = obterCriteriaItensComponentesSanguineosPrescricao(codigoPaciente, dataBancoSangue);
		
		return executeCriteriaExists(criteria);
	}
	
	public List<AbsItensSolHemoterapicas> pesquisarItensProcedimentosPrescricaoPOL(Integer codigoPaciente){
		
		DetachedCriteria criteria = createCriteriaItensSolHemoterapicasPrescricaoPOL(codigoPaciente);
		criteria.add(Restrictions.isNotNull(AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS.toString()));
		return executeCriteria(criteria);
	}
	
	
	public boolean verificarExisteItensProcedimentosPrescricao(Integer codigoPaciente){
		
		DetachedCriteria criteria = createCriteriaItensSolHemoterapicasPrescricaoPOL(codigoPaciente);
		criteria.add(Restrictions.isNotNull(AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS.toString()));
		
		return executeCriteriaExists(criteria);
	}
	
	private DetachedCriteria createCriteriaItensSolHemoterapicasPrescricaoPOL(Integer codigoPaciente){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItensSolHemoterapicas.class);
		//Alias
		criteria.createAlias(AbsItensSolHemoterapicas.Fields.SOLICITACOES_HEMOTERAPICAS.toString(), "hemoterapia");
		criteria.createAlias("hemoterapia." + AbsSolicitacoesHemoterapicas.Fields.PRESCRICAOMEDICA.toString(), "prescricaoMedica");
		criteria.createAlias("prescricaoMedica." + MpmPrescricaoMedica.Fields.ATENDIMENTO.toString(), "atendimento");
		
		criteria.add(Restrictions.eq("hemoterapia."
				+ AbsSolicitacoesHemoterapicas.Fields.IND_PENDENTE.toString(),
				DominioIndPendenteItemPrescricao.N));
		criteria.add(Restrictions.isNull("hemoterapia." + AbsSolicitacoesHemoterapicas.Fields.DTHR_FIM.toString()));
		criteria.add(Restrictions.eq("atendimento." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codigoPaciente));

		
		return criteria;
	}
	
	public List<AbsItensSolHemoterapicas> pesquisarAbsItensSolHemoterapicasPorPheCodigo(String pheCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"ISH");
		dc.createAlias("ISH.".concat(AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS.toString()), "APH");
		dc.add(Restrictions.ilike("APH.".concat(AbsProcedHemoterapico.Fields.CODIGO.toString()), pheCodigo, MatchMode.EXACT));
		return executeCriteria(dc);		
	}
	
	public List<AbsItensSolHemoterapicas> pesquisarAbsItensSolHemoterapicasPorAtdSeqSheSeq(Integer sheAtdSeq, Integer sheSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsItensSolHemoterapicas.class, "ISH");		
		criteria.createAlias("ISH." + AbsItensSolHemoterapicas.Fields.COMPONENTES_SANGUINEOS.toString(), "CSA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ISH." + AbsItensSolHemoterapicas.Fields.PROCED_HEMOTERAPICOS.toString(), "PHE", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("ISH." + AbsItensSolHemoterapicas.Fields.SHE_ATD_SEQ.toString(), sheAtdSeq));
		criteria.add(Restrictions.eq("ISH." + AbsItensSolHemoterapicas.Fields.SHE_SEQ.toString(), sheSeq));
		return this.executeCriteria(criteria);		
	}
}
