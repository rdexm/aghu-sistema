package br.gov.mec.aghu.exames.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristicaId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;

/**
 * Classe responsável pelos métodos de acesso a banco referentes ao pojo AelExameGrupoCaracteristica
 *  
 * @author aghu
 *
 */
public class AelExameGrupoCaracteristicaDAO extends
br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameGrupoCaracteristica> {



	private static final long serialVersionUID = -5906381250957780655L;
	
	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Override
	protected void obterValorSequencialId(AelExameGrupoCaracteristica elemento) {

		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}

		AelExameGrupoCaracteristicaId id = new AelExameGrupoCaracteristicaId();
		id.setEmaExaSigla(elemento.getExameMaterialAnalise().getId().getExaSigla());
		id.setEmaManSeq(elemento.getExameMaterialAnalise().getId().getManSeq());
		id.setCacSeq(elemento.getResultadoCaracteristica().getSeq());
		id.setGcaSeq(elemento.getGrupoResultadoCaracteristica().getSeq());

		elemento.setId(id);
	}

	/**
	 * Lista as opções de seleção para o campo codificado parametroCampoLaudo
	 * 
	 * @param parametroCampoLaudo
	 * @return
	 */
	public List<AelExameGrupoCaracteristica> pesquisarExameGrupoCarateristicaPorCampo(AelParametroCamposLaudo parametroCampoLaudo){

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class);
		aelParametroCamposLaudoDAO.refresh(parametroCampoLaudo);

		criteria.createAlias(
				AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
				.toString(),
				AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
				.toString());


		criteria.add(Restrictions.eq(
				AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE
				.toString(), parametroCampoLaudo.getAelVersaoLaudo()
				.getExameMaterialAnalise()));

		criteria
		.add(Restrictions
				.eq(AelExameGrupoCaracteristica.Fields.GRUPO_RESULTADO_CARACTERISTICA
						.toString(), parametroCampoLaudo
						.getCampoLaudo()
						.getGrupoResultadoCaracteristica()));


		criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
				.toString()+"." + AelResultadoCaracteristica.Fields.SITUACAO.toString(), DominioSituacao.A));



		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class);
		subCriteria.add(Restrictions.eq(
				AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE
				.toString(), parametroCampoLaudo.getAelVersaoLaudo()
				.getExameMaterialAnalise()));
		subCriteria
		.add(Restrictions
				.eq(AelExameGrupoCaracteristica.Fields.GRUPO_RESULTADO_CARACTERISTICA
						.toString(), parametroCampoLaudo
						.getCampoLaudo()
						.getGrupoResultadoCaracteristica()));

		subCriteria.setProjection(Projections.property(AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA.toString()));


		criteria.add(Subqueries.propertyIn(AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
				.toString(), subCriteria));

		return this.executeCriteria(criteria);
	}


	public List<AelExameGrupoCaracteristica> pesquisarExameGrupoCarateristica(AelExameGrupoCaracteristica exameGrupoCaracteristica,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc){

		DetachedCriteria criteria = this.montarCriteriaPesquisarExameGrupoCarateristica(exameGrupoCaracteristica);

		criteria.addOrder(Order.asc(AelExameGrupoCaracteristica.Fields.GCA_SEQ.toString()));
		criteria.addOrder(Order.asc(AelExameGrupoCaracteristica.Fields.CODIGO_FALANTE.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarExameGrupoCarateristicaCount(AelExameGrupoCaracteristica exameGrupoCaracteristica) {
		DetachedCriteria criteria = this.montarCriteriaPesquisarExameGrupoCarateristica(exameGrupoCaracteristica);

		return this.executeCriteriaCount(criteria);
	}

	public AelExameGrupoCaracteristica obterAelExameGrupoCaracteristica(AelExameGrupoCaracteristica exameGrupoCaracteristica) {

		DetachedCriteria criteria = this.montarCriteriaPesquisarExameGrupoCarateristica(exameGrupoCaracteristica);

		List<AelExameGrupoCaracteristica> result = this.executeCriteria(criteria);

		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}

	}

	private DetachedCriteria montarCriteriaPesquisarExameGrupoCarateristica(AelExameGrupoCaracteristica exameGrupoCaracteristica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class);

		criteria.createAlias(
				AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE
				.toString(),
				AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE
				.toString());

		criteria.createAlias(
				AelExameGrupoCaracteristica.Fields.GRUPO_RESULTADO_CARACTERISTICA
				.toString(),
				AelExameGrupoCaracteristica.Fields.GRUPO_RESULTADO_CARACTERISTICA
				.toString());

		criteria.createAlias(
				AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
				.toString(),
				AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
				.toString());

		//primeira suggestion - exame material analise
		criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE
				.toString()+"." + AelExamesMaterialAnalise.Fields.MAN_SEQ.toString(), 
				exameGrupoCaracteristica.getExameMaterialAnalise().getId().getManSeq()));
		criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE
				.toString()+"." + AelExamesMaterialAnalise.Fields.EXA_SIGLA.toString(), 
				exameGrupoCaracteristica.getExameMaterialAnalise().getId().getExaSigla()));

		if(exameGrupoCaracteristica.getResultadoCaracteristica() != null) {
			criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA
					.toString()+"." + AelResultadoCaracteristica.Fields.SEQ.toString(), 
					exameGrupoCaracteristica.getResultadoCaracteristica().getSeq()));
		}

		if(exameGrupoCaracteristica.getGrupoResultadoCaracteristica() != null) {
			criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.
					GRUPO_RESULTADO_CARACTERISTICA.toString()+"." 
					+ AelGrupoResultadoCaracteristica.Fields.SEQ.toString(), 
					exameGrupoCaracteristica.getGrupoResultadoCaracteristica().getSeq()));

		}

		if(exameGrupoCaracteristica.getCodigoFalante() != null) {
			criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.
					Fields.CODIGO_FALANTE.toString(), 
					exameGrupoCaracteristica.getCodigoFalante()));
		}

		if(exameGrupoCaracteristica.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					AelExameGrupoCaracteristica.Fields.SITUACAO.toString(), 
					exameGrupoCaracteristica.getIndSituacao()));
		}

		return criteria;
	}
	
	/**
	 * Verifica se existe dependencia
	 * de registros atraves do seq
	 * da tabela AEL_GRUPO_RESULTADO_CARACTERIS 
	 * 
	 * @param seq
	 * @return
	 */
	public List<AelExameGrupoCaracteristica> listarExameGrupoCarateristicaPorSeq(Integer seq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class);
		
		criteria.add(Restrictions.eq(AelExameGrupoCaracteristica.Fields.GCA_SEQ.toString(), seq));
				
		return this.executeCriteria(criteria);
	}

	public List<Object[]>  pesquisarRelatorioCaracteristicasResultados(String siglaExame, Integer manSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameGrupoCaracteristica.class,"EGC");

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName("EGC."+ AelExameGrupoCaracteristica.Fields.GCA_SEQ.toString()));
		projectionList.add(Property.forName("EGC."+ AelExameGrupoCaracteristica.Fields.CODIGO_FALANTE.toString()));
		projectionList.add(Property.forName("GCA."+ AelGrupoResultadoCaracteristica.Fields.SEQ.toString()));
		projectionList.add(Property.forName("GCA."+ AelGrupoResultadoCaracteristica.Fields.DESCRICAO.toString()));
		projectionList.add(Property.forName("GCA."+ AelGrupoResultadoCaracteristica.Fields.ORDEM_IMPRESSAO.toString()));
		projectionList.add(Property.forName("CAC."+ AelResultadoCaracteristica.Fields.SEQ.toString()));
		projectionList.add(Property.forName("CAC."+ AelResultadoCaracteristica.Fields.DESCRICAO.toString()));



		criteria.setProjection(projectionList);

		criteria.createAlias(AelExameGrupoCaracteristica.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA");
		criteria.createAlias(AelExameGrupoCaracteristica.Fields.GRUPO_RESULTADO_CARACTERISTICA.toString(),"GCA");
		criteria.createAlias(AelExameGrupoCaracteristica.Fields.RESULTADO_CARACTERISTICA.toString(),"CAC");

		criteria.add(Restrictions.eq("EMA."+AelExamesMaterialAnalise.Fields.EXA_SIGLA,	siglaExame));//siglaExame
		criteria.add(Restrictions.eq("EMA."+AelExamesMaterialAnalise.Fields.MAN_SEQ,	manSeq));//manSeq

		criteria.add(Restrictions.eq("EGC."+AelExameGrupoCaracteristica.Fields.SITUACAO.toString(),	DominioSituacao.A));
		criteria.addOrder(Order.asc("EGC."+AelExameGrupoCaracteristica.Fields.GCA_SEQ.toString()));
		criteria.addOrder(Order.asc("EGC."+AelExameGrupoCaracteristica.Fields.CODIGO_FALANTE.toString()));


		return this.executeCriteria(criteria);


	}

}
