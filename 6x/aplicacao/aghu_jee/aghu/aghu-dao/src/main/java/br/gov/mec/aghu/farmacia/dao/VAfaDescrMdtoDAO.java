package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaAfaDescMedicamentoTipoUsoMedicamentoVO;
import br.gov.mec.aghu.view.VAfaDescrMdto;

public class VAfaDescrMdtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAfaDescrMdto> {

	
	
	private static final long serialVersionUID = 5992513393010982576L;

	public List<VAfaDescrMdto> obtemListaDiluentes() {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAfaDescrMdto.class);		

		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_DILUENTE.toString(), true));
		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		criteria.add(Restrictions.isNull(VAfaDescrMdto.Fields.SINONIMO.toString()));		
		criteria.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()));
		
		List<VAfaDescrMdto> lista = executeCriteria(criteria);
		
		return lista;

	}

	public List<VAfaDescrMdto> obtemListaDiluentes(Integer matCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAfaDescrMdto.class);		

		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_DILUENTE.toString(), true));
		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		criteria.add(Restrictions.isNull(VAfaDescrMdto.Fields.SINONIMO.toString()));
		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(), matCodigo));
		
		criteria.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()));
		
		List<VAfaDescrMdto> lista = executeCriteria(criteria);
		
		return lista;

	}
	
	public DetachedCriteria criarCriteriaObtemListaDescrMdtoAtivos(Boolean isCount){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAfaDescrMdto.class);		
		criteria.createAlias(VAfaDescrMdto.Fields.MEDICAMENTO.toString(), "MED", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(!isCount) {
			criteria.addOrder(Order.desc(VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()));
			criteria.addOrder(Order.asc(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()));
		}
		
		return criteria;
	}
	
	public List<VAfaDescrMdto> obtemListaDescrMdtoAtivos(Object parametro) {
		DetachedCriteria criteria = criarCriteriaObtemListaDescrMdtoAtivos(Boolean.FALSE);
		String srtPesquisa = (String) parametro;
		if (CoreUtil.isNumeroInteger(parametro)) {
	    	criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(), Integer.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}
		
		List<VAfaDescrMdto> lista = this.executeCriteria(criteria, 0, 100, null, true);
		return lista;
	}
	
	public Long obtemListaDescrMdtoAtivosCount(Object parametro) {
		DetachedCriteria criteria = criarCriteriaObtemListaDescrMdtoAtivos(Boolean.TRUE);
		String srtPesquisa = (String) parametro;
		if (CoreUtil.isNumeroInteger(parametro)) {
	    	criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.MAT_CODIGO.toString(), Integer.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}
		
		return this.executeCriteriaCount(criteria);
	}
	
	
	public List<VAfaDescrMdto> obterListaDiluentes(String parametro) {
		DetachedCriteria criteria = criarCriteriaObtemListaDescrMdtoAtivos(Boolean.FALSE);

		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_DILUENTE.toString(), true));
		criteria.add(Restrictions.eq(VAfaDescrMdto.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		criteria.add(Restrictions.isNull(VAfaDescrMdto.Fields.SINONIMO.toString()));
	    
		if (StringUtils.isNotBlank(parametro)) {
			criteria.add(Restrictions.ilike(VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(), parametro, MatchMode.ANYWHERE));
		}
	
		return this.executeCriteria(criteria);
	}


	/**
	 * @author marcelo.deus
	 * #44281 - C1 - SB Medicamentos
	 */
	public List<ListaAfaDescMedicamentoTipoUsoMedicamentoVO> listarSbMedicamentos(String param, Boolean indPadronizado){
		DetachedCriteria criteria = montarConsultaSBMedicamentos(param,	indPadronizado);
		criteria.addOrder(Order.desc("VDM." + VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()))
				.addOrder(Order.asc("VDM." + VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}

	public Long listarSbMedicamentosCount(String param, Boolean indPadronizado){
		DetachedCriteria criteria = montarConsultaSBMedicamentos(param,	indPadronizado);
		List<ListaAfaDescMedicamentoTipoUsoMedicamentoVO> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return Long.valueOf(lista.size());
		} else {
			return 0l;
		}
	}
	
	private DetachedCriteria montarConsultaSBMedicamentos(String param,	Boolean indPadronizado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAfaDescrMdto.class, "VDM");
		String srtPesquisa = (String) param;
		criteria.createAlias("VDM." + VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UMM", JoinType.LEFT_OUTER_JOIN)
				.createAlias("VDM." + VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(), "TUM");
		criteria.add(Restrictions.eq("VDM." + VAfaDescrMdto.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		if(indPadronizado != null){
//			indPadronizado = Boolean.FALSE;
			criteria.add(Restrictions.eq("VDM." + VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString(), indPadronizado));
		}
		if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike("VDM." + VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString(), srtPesquisa, MatchMode.ANYWHERE));
		}
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.MAT_CODIGO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.MAT_CODIGO.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.DESCRICAO_EDITADA.toString());
//		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.DESCRICAO_MATERIAL.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.CONCENTRACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.CONCENTRACAO.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.UMM_SEQ.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.UNIDADE_MEDIDA_MEDICAMENTO.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.TPR_SIGLA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.TRP_SIGLA.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_PADRONIZACAO.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.FREQUENCIA_USUAL.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.FREQUENCIA_USUAL.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.TUM_SIGLA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.TUM_SIGLA.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.IND_SITUACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_SITUACAO.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.TFQ_SEQ.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.TFQ_SEQ.toString());
		projections.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_EXIGE_JUSTIFICATIVA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_EXIGE_JUSTIFICATIVA.toString());
		projections.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString());
		projections.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_ANTIMICROBIANO.toString());
		projections.add(Projections.property("VDM." + VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_EXIGE_OBSERVACAO.toString());
		projections.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_QUIMIOTERAPICO.toString());
		criteria.setProjection(Projections.distinct(projections));
		criteria.setResultTransformer(Transformers.aliasToBean(ListaAfaDescMedicamentoTipoUsoMedicamentoVO.class));
		return criteria;
	}
	
	
	public ListaAfaDescMedicamentoTipoUsoMedicamentoVO obterSbMedicamentos(Integer medMatCodigo, String descricaoMat){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAfaDescrMdto.class, "VDM");
		
		criteria.createAlias("VDM." + VAfaDescrMdto.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UMM", JoinType.LEFT_OUTER_JOIN)
		.createAlias("VDM." + VAfaDescrMdto.Fields.TIPO_USO_MDTO.toString(), "TUM");
		criteria.add(Restrictions.eq("VDM." + VAfaDescrMdto.Fields.IND_SITUACAO.toString(), DominioSituacaoMedicamento.A));
		criteria.add(Restrictions.eq("VDM." + VAfaDescrMdto.Fields.MAT_CODIGO.toString(), medMatCodigo));
		criteria.add(Restrictions.eq("VDM." + VAfaDescrMdto.Fields.DESCRICAO_MAT.toString(), descricaoMat));
	
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.MAT_CODIGO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.DESCRICAO_EDITADA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.DESCRICAO_EDITADA.toString())
//				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.DESCRICAO_MAT.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.DESCRICAO_MATERIAL.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.CONCENTRACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.CONCENTRACAO.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.UMM_SEQ.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.UNIDADE_MEDIDA_MEDICAMENTO.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.TPR_SIGLA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.TRP_SIGLA.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.IND_PADRONIZACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_PADRONIZACAO.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.FREQUENCIA_USUAL.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.FREQUENCIA_USUAL.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.TUM_SIGLA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.TUM_SIGLA.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.IND_SITUACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.TFQ_SEQ.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.TFQ_SEQ.toString())
				.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_EXIGE_JUSTIFICATIVA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_EXIGE_JUSTIFICATIVA.toString())
				.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_EXIGE_DURACAO_SOLICITADA.toString())
				.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_ANTIMICROBIANO.toString())
				.add(Projections.property("VDM." + VAfaDescrMdto.Fields.IND_EXIGE_OBSERVACAO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_EXIGE_OBSERVACAO.toString())
				.add(Projections.property("TUM." + AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString()), ListaAfaDescMedicamentoTipoUsoMedicamentoVO.Fields.IND_QUIMIOTERAPICO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(ListaAfaDescMedicamentoTipoUsoMedicamentoVO.class));
		
		return (ListaAfaDescMedicamentoTipoUsoMedicamentoVO) executeCriteriaUniqueResult(criteria);
	}
}
