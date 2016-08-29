package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @author gandriotti
 *
 */
public class AfaLocalDispensacaoMdtosDAO extends AbstractMedicamentoDAO<AfaLocalDispensacaoMdtos> {

	private static final long serialVersionUID = 8058282031817196300L;

	/**
	 * 
	 * @param medicamento
	 * @return
	 */
	public List<AfaLocalDispensacaoMdtos> obterListaLocalDispensacaoMdtos(AfaMedicamento medicamento) {

		List<AfaLocalDispensacaoMdtos> result = null;
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaLocalDispensacaoMdtos.class);
		
		criteria.add(Restrictions.eq(AfaLocalDispensacaoMdtos.Fields.ID_MED_MAT_CODIGO.toString(), 
			medicamento.getMatCodigo()));		
		
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	public AfaLocalDispensacaoMdtos obterlocaldisAfaLocalDispensacaoMdtos(Integer id) {

		AfaLocalDispensacaoMdtos result = null;
		DetachedCriteria criteria = null;
		
		if (id == null) {
			throw new IllegalArgumentException();
		}
		criteria = DetachedCriteria.forClass(AfaLocalDispensacaoMdtos.class);
		criteria.add(Restrictions.eq(AfaLocalDispensacaoMdtos.Fields.ID.toString(), id));
		result = (AfaLocalDispensacaoMdtos) this.executeCriteriaUniqueResult(criteria);
		
		return result;
	}
	*/
	
	/**
	 * Seta na entidade os valores da chave composta id 
	 * 
	 */
	@Override
	protected void obterValorSequencialId(AfaLocalDispensacaoMdtos elemento) {
		AfaLocalDispensacaoMdtosId localDispensacaoid = new AfaLocalDispensacaoMdtosId();
		
		localDispensacaoid.setMedMatCodigo(elemento.getId().getMedMatCodigo());
		localDispensacaoid.setUnfSeq(elemento.getId().getUnfSeq());
		elemento.setId(localDispensacaoid);
		
	}
	
	/**
	 * Obtém o número de registros retornados
	 * @param medicamento
	 * @return 
	 */
	public Long pesquisarLocalDispensacaoMedicamentoCount(AfaMedicamento medicamento){
		
		DetachedCriteria criteria = listaLocalDispensacaoMdtos(medicamento);
		
		return this.executeCriteriaCount(criteria);
	}

	/**
	 * Retorna criteria para busca de local de dispensacao de medicamentos
	 * 
	 * @param medicamento
	 * @return criteria
	 */
	public DetachedCriteria listaLocalDispensacaoMdtos(AfaMedicamento medicamento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaLocalDispensacaoMdtos.class);
		
		criteria.add(Restrictions.eq(AfaLocalDispensacaoMdtos.Fields.ID_MED_MAT_CODIGO.toString(), 
				medicamento.getMatCodigo()));		
		
		return criteria;
	}

	@Override
	protected DetachedCriteria pesquisarCriteria(AfaMedicamento medicamento) {
		DetachedCriteria criteria = listaLocalDispensacaoMdtos(medicamento);
		
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNIDADE_FUNCIONAL.toString(), "unidade", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("unidade."+AghUnidadesFuncionais.Fields.ALA.toString(), "ala", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_DOSE_INT.toString(), "unidadeFuncionalDispDoseInt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_DOSE_FRAC.toString(), "unidadeFuncionalDispDoseFrac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_ALTERNATIVA.toString(), "unidadeFuncionalDispAlternativa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_USO_DOMICILIAR.toString(), "unidadeFuncionalDispUsoDomiciliar", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoa", JoinType.LEFT_OUTER_JOIN);
		
		criteria.addOrder(Order.asc(AfaLocalDispensacaoMdtos.Fields.ID_MED_MAT_CODIGO.toString()));
		criteria.addOrder(Order.asc(AfaLocalDispensacaoMdtos.Fields.ID_UNF_SEQ.toString()));
		
		return criteria;
	}
	
	//#3559 - Selecionar todos os locais de dispensacao
	
	/**
	 * Retorna lista contendo os ids das unidades solicitantes que o medicamento possui
	 * @param medicamento
	 * @return unfsSolicitantesIds
	 */
	public Short[] listaIdsLocaisDispensacaoMdto(AfaMedicamento medicamento) {

		List<Object> result = null;
		Short[] unfsSolicitantesIds = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaLocalDispensacaoMdtos.class);
		
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(AfaLocalDispensacaoMdtos.Fields.ID_UNF_SEQ.toString())));
				
		criteria.add(Restrictions.eq(AfaLocalDispensacaoMdtos.Fields.ID_MED_MAT_CODIGO.toString(), 
			medicamento.getMatCodigo()));		
	
		result = this.executeCriteria(criteria);
		
		if (result != null && !result.isEmpty()) {
			unfsSolicitantesIds = new Short[result.size()];
			for (int i = 0; i < result.size(); i++) {
				unfsSolicitantesIds[i] = (Short) result.get(i);
			}
		}
		
		return unfsSolicitantesIds;
	}
	
	/**
	 * #3559
	 * Persiste lista contendo os locais de dispensacao disponiveis para o medicamento
	 * @param locaisDispensacaoPersistir
	 */
	public void persisteTodosLocaisDispensacaoDisponiveis(
			List<AfaLocalDispensacaoMdtos> locaisDispensacaoPersistir) {
		
		for (AfaLocalDispensacaoMdtos localDispensacao : locaisDispensacaoPersistir) {
			localDispensacao.setUnidadeFuncional(null);
			persistir(localDispensacao);
		}
		
	}
	
	public List<AfaLocalDispensacaoMdtos> pesquisarPorUnidade(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional) {
		List<AfaLocalDispensacaoMdtos> result = null;
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaLocalDispensacaoMdtos.class);
		
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNIDADE_FUNCIONAL.toString(), "unidade", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_DOSE_INT.toString(), "unidadeFuncionalDispDoseInt", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_DOSE_FRAC.toString(), "unidadeFuncionalDispDoseFrac", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_ALTERNATIVA.toString(), "unidadeFuncionalDispAlternativa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AfaLocalDispensacaoMdtos.Fields.UNF_SEQ_DISP_USO_DOMICILIAR.toString(), "unidadeFuncionalDispUsoDomiciliar", JoinType.LEFT_OUTER_JOIN);
		
		if (unidadeFuncional != null ){
			criteria.add(Restrictions.eq(
					AfaLocalDispensacaoMdtos.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncional));
		}

		result = this.executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);

		return result;
	}
	
	/**
	 * Obtém o número de registros retornados por unidades
	 * @param unidadeFuncional
	 * @return 
	 */
	public Long pesquisarPorUnidadeCount(
			AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = listaLocalDispensacaoMdtos(unidadeFuncional);

		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna criteria para busca de local de dispensacao de medicamentos pro unidades
	 * 
	 * @param medicamento
	 * @return criteria
	 */
	private DetachedCriteria listaLocalDispensacaoMdtos(
			AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaLocalDispensacaoMdtos.class);
		
		if (unidadeFuncional != null ){
			criteria.add(Restrictions.eq(
					AfaLocalDispensacaoMdtos.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncional));
		}

		return criteria;
	}
	
	public List<Integer> obterLocalDispensMdtosPorUnfSeq(Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaLocalDispensacaoMdtos.class);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AfaLocalDispensacaoMdtos.Fields.ID_MED_MAT_CODIGO.toString())));
		
		if (unfSeq != null) {
			criteria.add(Restrictions.eq(AfaLocalDispensacaoMdtos.Fields.ID_UNF_SEQ.toString(), unfSeq));
		}
		return  this.executeCriteria(criteria);
	}

}
