package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AfaFormaDosagemVO;
import br.gov.mec.aghu.view.VMpmDosagem;

public class AfaFormaDosagemDAO extends AbstractMedicamentoDAO<AfaFormaDosagem> {
	
	private static final long serialVersionUID = -151699937251508805L;

	@Override
	protected DetachedCriteria pesquisarCriteria(AfaMedicamento medicamento) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(AfaFormaDosagem.class);
		result.createAlias(AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), 	"unidadeMedidaMedicas", JoinType.LEFT_OUTER_JOIN);
		result.createAlias(AfaFormaDosagem.Fields.MEDICAMENTOS.toString(), 		"medicamento", JoinType.LEFT_OUTER_JOIN);
		result.createAlias("medicamento."+AfaMedicamento.Fields.TPR.toString(), "tipoApresentacaoMedicamento", JoinType.LEFT_OUTER_JOIN);
		result.createAlias(AfaFormaDosagem.Fields.RAP_SERVIDOR.toString(), 		"servidor", JoinType.LEFT_OUTER_JOIN);
		result.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), 		"pessoa", JoinType.LEFT_OUTER_JOIN);
		
		if (medicamento != null) {
			//result.createAlias("itemPrescricaoMedicamentos", 		"itemPrescricaoMedicamentos", JoinType.LEFT_OUTER_JOIN);
			result.add(Restrictions.eq(
					AfaFormaDosagem.Fields.MEDICAMENTOS.toString(), medicamento));
		}

		return result;
	}
	
	public AfaFormaDosagem obterAfaFormaDosagemWithJoin(Integer seqAfaFormaDosagem){
		DetachedCriteria cri = pesquisarCriteria(null);
		
		cri.createAlias("itemPrescricaoMedicamentos", 		"itemPrescricaoMedicamentos", JoinType.LEFT_OUTER_JOIN);
		
		cri.add(Restrictions.eq(AfaFormaDosagem.Fields.SEQ.toString(), seqAfaFormaDosagem));
		
		return (AfaFormaDosagem) executeCriteriaUniqueResult(cri);
	}
	/**
	 * 
	 * @param medMatCodigo
	 * @return
	 */
	public AfaFormaDosagem buscarDosagenPadraoMedicamento(Integer medMatCodigo) {
		
		AfaFormaDosagem result = null;
		DetachedCriteria criteria = null;

		if (medMatCodigo == null) {
			throw new IllegalArgumentException();
		}
		criteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.MEDICAMENTOS.toString()
				+ "." + AfaMedicamento.Fields.MAT_CODIGO.toString(),
				medMatCodigo));
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		criteria.add(Restrictions.eq(
				AfaFormaDosagem.Fields.IND_USUAL_PRESCRICAO.toString(), Boolean.TRUE));
		result = (AfaFormaDosagem) this.executeCriteriaUniqueResult(criteria);

		return result;
	}

	/**
	 * Lista todas as dosagens permitidas ao medicamento
	 * 
	 * @param medMatCodigo
	 * @return
	 */
	public List<AfaFormaDosagem> listaFormaDosagemMedicamento(
			Integer medMatCodigo) {
		
		List<AfaFormaDosagem> result = null;
		DetachedCriteria criteria = null;
		
		if (medMatCodigo == null) {
			throw new IllegalArgumentException();
		}
		criteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.MEDICAMENTOS.toString()
				+ "." + AfaMedicamento.Fields.MAT_CODIGO.toString(),
				medMatCodigo));
		result = this.executeCriteria(criteria);
		
		return result;
	}

	public DominioSituacao obtemSituacaoFormaDosagem(Integer seq,
			Integer medMatCodigo) {

		DominioSituacao result = null;
		DetachedCriteria criteria = null;

		if (seq == null) {
			throw new IllegalArgumentException();
		}
		if (medMatCodigo == null) {
			throw new IllegalArgumentException();
		}
		criteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		criteria.setProjection(Projections.distinct(Projections
				.projectionList().add(
						Projections
								.property(AfaFormaDosagem.Fields.IND_SITUACAO
										.toString()))));
		criteria.add(Restrictions.eq(
				AfaFormaDosagem.Fields.MEDICAMENTOS.toString() + "."
						+ AfaMedicamento.Fields.MAT_CODIGO.toString(),
				medMatCodigo));
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.SEQ.toString(), seq));
		result = (DominioSituacao) this.executeCriteriaUniqueResult(criteria);

		return result;
	}

	/**
	 * @author marcelo.deus
	 * #44281
	 */
	public AfaFormaDosagem buscarDosagemPadraoMedicamentoMedida(Integer medMatCodigo, Integer seqDosagem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.MEDICAMENTOS_MAT_CODIGO.toString(),	medMatCodigo));
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.SEQ.toString(),	seqDosagem));
		return (AfaFormaDosagem) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AfaFormaDosagemVO> pesquisarFormaDosagem(Integer matCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		
		criteria.createAlias(AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "UMM", JoinType.INNER_JOIN);
		criteria.createAlias(AfaFormaDosagem.Fields.MEDICAMENTOS.toString(), "MED", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("MED."+AfaMedicamento.Fields.MAT_CODIGO.toString() , matCodigo));
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property(AfaFormaDosagem.Fields.SEQ.toString()), AfaFormaDosagemVO.Fields.FDS_SEQ.toString())
				.add(Projections.property("UMM." + MpmUnidadeMedidaMedica.Fields.SEQ.toString()), AfaFormaDosagemVO.Fields.FORMA_DOSAGEM_UMM_SEQ.toString())
				.add(Projections.property("UMM." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), AfaFormaDosagemVO.Fields.UMM_DESCRICAO.toString());

		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(AfaFormaDosagemVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<AfaFormaDosagemVO> pesquisarFormaDosagemParamDose(Integer matCodigo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VMpmDosagem.class);
		
		criteria.add(Restrictions.eq(VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString(), matCodigo));
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property(VMpmDosagem.Fields.SEQ_DOSAGEM.toString()), AfaFormaDosagemVO.Fields.FDS_SEQ.toString())
				.add(Projections.property(VMpmDosagem.Fields.FDS_UMM_SEQ.toString()), AfaFormaDosagemVO.Fields.FORMA_DOSAGEM_UMM_SEQ.toString())
				.add(Projections.property(VMpmDosagem.Fields.SEQ_UNIDADE.toString()), AfaFormaDosagemVO.Fields.UMM_DESCRICAO.toString());

		criteria.setProjection(projectionList);
		criteria.addOrder(Order.asc(VMpmDosagem.Fields.SEQ_UNIDADE.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AfaFormaDosagemVO.class));
		
		return executeCriteria(criteria);
	}
	
	public AfaFormaDosagemVO obterFormaDosagem(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaFormaDosagem.class);
		
		criteria.createAlias(AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "UM", JoinType.INNER_JOIN);
		criteria.createAlias(AfaFormaDosagem.Fields.MEDICAMENTOS.toString(), "MED", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AfaFormaDosagem.Fields.SEQ.toString(), seq));
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property(AfaFormaDosagem.Fields.SEQ.toString()), AfaFormaDosagemVO.Fields.FDS_SEQ.toString())
				.add(Projections.property("UM." + MpmUnidadeMedidaMedica.Fields.SEQ.toString()), AfaFormaDosagemVO.Fields.FORMA_DOSAGEM_UMM_SEQ.toString())
				.add(Projections.property("UM." + MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), AfaFormaDosagemVO.Fields.UMM_DESCRICAO.toString());

		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(AfaFormaDosagemVO.class));
		
		return (AfaFormaDosagemVO) executeCriteriaUniqueResult(criteria);
	}
}
