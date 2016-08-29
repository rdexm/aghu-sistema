package br.gov.mec.aghu.estoque.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.vo.SceRmrPacienteVO;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRmps;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceRmrPaciente;
import br.gov.mec.aghu.model.ScoMaterial;

/**
 * 
 * @modulo estoque
 *
 */
public class SceRmrPacienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceRmrPaciente> {
	
	private static final long serialVersionUID = -8206215632032811736L;

	/**
	 * Listar SceRmrPaciente filtrando PorRmpSeq e Numero (id) do SceItemRmps.
	 * 
	 * TODO: Corrigir, consulta alterada pois não existe dados de almoxarifado(sce_estq_almoxs) 
	 * 
	 * @param rmpSeq
	 * @param numero
	 * @return
	 */
	public List<SceRmrPacienteVO> listarRmrPacientePorRmpSeqENumero(Integer rmpSeq, Short numero) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceRmrPaciente.class);
		
		criteria.createAlias(SceRmrPaciente.Fields.SCE_ITEM_RMPS.toString(), "ips");
		
		criteria.add(Restrictions.eq("ips." + SceItemRmps.Fields.RMP_SEQ.toString(), rmpSeq));
		
		criteria.add(Restrictions.eq("ips."+SceItemRmps.Fields.NUMERO.toString(), numero));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(SceRmrPaciente.Fields.CRG_SEQ.toString()), SceRmrPacienteVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(SceRmrPaciente.Fields.DT_UTILIZACAO.toString()), SceRmrPacienteVO.Fields.DT_UTILIZACAO.toString())
				.add(Projections.property(SceRmrPaciente.Fields.INT_SEQ.toString()), SceRmrPacienteVO.Fields.INT_SEQ.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(SceRmrPacienteVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<SceRmrPacienteVO> listarSceRmrPacienteVOPorCirurgiaESituacao(final Integer crgSeq, final DominioSituacao situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(SceRmrPaciente.class, "RMP");
		
		criteria.createAlias("RMP."+SceRmrPaciente.Fields.SCE_ITEM_RMPS.toString(), 	"IPS");
		criteria.createAlias("IPS."+SceItemRmps.Fields.ITEM_RMR.toString(), 			"IRR", JoinType.LEFT_OUTER_JOIN);	
		criteria.createAlias("IPS."+SceItemRmps.Fields.SCE_ESTQ_ALMOX.toString(), 		"EAL");
		criteria.createAlias("EAL."+SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), 	"MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.PROCED_HOSP_INTERNO.toString(), 	"PHI", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("RMP."+SceRmrPaciente.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq("PHI."+FatProcedHospInternosPai.Fields.SITUACAO.toString(), situacao));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RMP."+SceRmrPaciente.Fields.CRG_SEQ.toString()), SceRmrPacienteVO.Fields.CRG_SEQ.toString())
				.add(Projections.property("RMP."+SceRmrPaciente.Fields.DT_UTILIZACAO.toString()), SceRmrPacienteVO.Fields.DT_UTILIZACAO.toString())
				.add(Projections.property("RMP."+SceRmrPaciente.Fields.INT_SEQ.toString()), SceRmrPacienteVO.Fields.INT_SEQ.toString())
				.add(Projections.property("PHI."+FatProcedHospInternosPai.Fields.SEQ.toString()), SceRmrPacienteVO.Fields.PHI_SEQ.toString())
				.add(Projections.property("IPS."+SceItemRmps.Fields.RMP_SEQ.toString()), SceRmrPacienteVO.Fields.RMP_SEQ.toString())
				.add(Projections.property("IPS."+SceItemRmps.Fields.NUMERO.toString()), SceRmrPacienteVO.Fields.NUMERO.toString())
				.add(Projections.property("IPS."+SceItemRmps.Fields.QUANTIDADE.toString()), SceRmrPacienteVO.Fields.QUANTIDADE.toString())				
		);

		criteria.setResultTransformer(Transformers.aliasToBean(SceRmrPacienteVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<SceRmrPaciente> listarRmrPacientesPorSeqNfGeralNaoNula(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceRmrPaciente.class);
		
		criteria.add(Restrictions.eq(SceRmrPaciente.Fields.SEQ.toString(), seq));
		
		criteria.add(Restrictions.isNotNull(SceRmrPaciente.Fields.NF_GERAL.toString()));
		
		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public Double verificarDoacoes(Integer matCodigo, String rmpSeq, Boolean porNotaFiscal){
		
		StringBuilder hql = new StringBuilder()
		.append(" select idf.valor_unitario ")
		.append(" from agh.sce_rmr_pacientes rmp ")
		.append(" inner join  agh.sce_item_rmps ips on (ips.rmp_seq = rmp.seq) ")
		.append(" left join  agh.sce_documento_fiscal_entradas dfe on (dfe.numero = ips.nota_fiscal or dfe.numero = rmp.nf_geral ) ")
		.append(" left join agh.sce_item_dfe idf on (idf.dfe_seq = dfe.seq) ");
		
		if(!porNotaFiscal){
			hql.append(" left join agh.sce_entr_said_sem_licitacoes esl on (esl.dfe_seq = dfe.seq) ");
		}	
		
		hql.append(" where ")
		.append(" rmp.frn_numero = dfe.frn_numero ");
		if(!porNotaFiscal){
			hql.append(" and esl.tmv_seq = 37 ")
			.append(" and esl.tmv_complemento = 2 ")
			.append(" and esl.mat_codigo = ").append(matCodigo);
		} else {
			hql.append(" and idf.mat_codigo = ").append(matCodigo);
		}	
		hql.append(" and rmp.seq = ").append(rmpSeq);
		
		javax.persistence.Query query = this.createNativeQuery(hql.toString());
		List<BigDecimal> lista = query.getResultList();
		
		for(BigDecimal campo : lista){
			return campo.doubleValue();
		}
	
		return null;
	}

	public Double buscarCustoOrteseOuProteseUltimaEntrada(Integer matCodigo) {
		StringBuffer sql = new StringBuffer()
		.append(" select ").append(SceMovimentoMaterial.Fields.VALOR.toString()).append("/").append(SceMovimentoMaterial.Fields.QUANTIDADE).append(" as valorUnitario")
		.append(" from ").append(SceMovimentoMaterial.class.getSimpleName()).append(" sce")
		.append(" where ").append(SceMovimentoMaterial.Fields.MATERIAL_CODIGO.toString()).append(" = :matCodigo ")
		.append(" and ").append(SceMovimentoMaterial.Fields.TIPO_MOVIMENTO_SEQ.toString()).append(" = 11 ")
		.append(" and ").append(SceMovimentoMaterial.Fields.IND_ESTORNO.toString()).append(" = 'N' ")
		.append(" order by ").append(SceMovimentoMaterial.Fields.SEQ.toString()).append(" desc");
		
		javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("matCodigo", matCodigo);
		List<BigDecimal> lista = query.getResultList();
		
		for(BigDecimal campo : lista){
			return campo.doubleValue();
		}
	
		return null;
	}	
}
