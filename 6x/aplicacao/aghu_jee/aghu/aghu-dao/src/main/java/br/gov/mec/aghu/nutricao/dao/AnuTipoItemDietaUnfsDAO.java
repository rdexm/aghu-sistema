package br.gov.mec.aghu.nutricao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.RapServidores;

/**
 * 
 * @author gmneto
 * 
 */
public class AnuTipoItemDietaUnfsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AnuTipoItemDietaUnfs> {

	private static final long serialVersionUID = 195171577554027590L;

	
	public enum anuTipoItemDietaUnfsDAO implements BusinessExceptionCode {
		MENSAGEM_TIPOS_DIETAS_UNIDADE_EXISTE,MENSAGEM_TODOS_TIPOS_DIETAS_UNIDADE_EXISTE;
	}

	/**
	 * Lista AnuTipoItemDietaUnfs de acordo com a unidade funcional e o tipo de
	 * dieta.
	 * 
	 * @param tipoItemDieta
	 * @param unidadeFuncional
	 * @return
	 */
	public List<AnuTipoItemDietaUnfs> listarAnuTipoItemDietaUnfsPorTipoItemEUnidadeFuncional(
			AnuTipoItemDieta tipoItemDieta,
			AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AnuTipoItemDietaUnfs.class);

		criteria.add(Restrictions.eq(
				AnuTipoItemDietaUnfs.Fields.TIPO_ITEM_DIETA.toString(),
				tipoItemDieta));

		criteria.add(Restrictions.eq(
				AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(),
				unidadeFuncional));

		return this.executeCriteria(criteria);
	}

	public void inserirAtualizarListaUnidadesTipoDieta(AnuTipoItemDieta tipoDieta, List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas,
			List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) {
		
		if(listaExcluiUnidadeFunc != null && !listaExcluiUnidadeFunc.isEmpty()){
			/*Itero para excluir*/
			for (AnuTipoItemDietaUnfs itemToRemove : listaExcluiUnidadeFunc) {
				remover(merge(itemToRemove));
			}
			flush();
		}

		/*salvo a nova lista*/
		if(listaUnidadeFuncAdicionadas != null && listaUnidadeFuncAdicionadas.size()>0){
			/*Itero para excluir*/
			for (AnuTipoItemDietaUnfs itemToInsert : listaUnidadeFuncAdicionadas) {
				itemToInsert.setTipoItemDieta(tipoDieta);
				merge(itemToInsert);
			}
			flush();
		}
	}
	
	public void inserirAtualizarListaUnidadesTipoDieta(AnuTipoItemDieta tipoDieta, List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) {
		/*Busco a lista para excluir*/
		List<AnuTipoItemDietaUnfs> lstToRemove = obterUnfsPorTipoDietaSeq(tipoDieta);

		if(lstToRemove != null && lstToRemove.size()>0){
			/*Itero para excluir*/
			for (AnuTipoItemDietaUnfs itemToRemove : lstToRemove) {
				remover(itemToRemove);
			}
			flush();
		}

		/*salvo a nova lista*/
		if(listaAnuTipoItemDietaUnfs != null && listaAnuTipoItemDietaUnfs.size()>0){
			/*Itero para excluir*/
			for (AnuTipoItemDietaUnfs itemToInsert : listaAnuTipoItemDietaUnfs) {
				itemToInsert.setTipoItemDieta(tipoDieta);
				persistir(itemToInsert);
			}
			flush();
		}
	}

	public List<AnuTipoItemDietaUnfs> obterUnfsPorTipoDietaSeq(AnuTipoItemDieta tipoItemDieta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AnuTipoItemDietaUnfs.class);
		criteria.createAlias(AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("UNF." + AghUnidadesFuncionais.Fields.ALA.toString(), "ALA", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AnuTipoItemDietaUnfs.Fields.TIPO_ITEM_DIETA.toString(), tipoItemDieta));
		return this.executeCriteria(criteria);
	}
	/**
	 * Lista AnuTipoItemDietaUnfs de acordo com a unidade funcional 
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param unidadeFuncional
	 * @return
	 */

	public List<AnuTipoItemDietaUnfs> listarAnuTipoItemDietaUnfsPorUnidadeFuncional(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AnuTipoItemDietaUnfs.class);

		if (unidadeFuncional != null) {
			criteria.add(Restrictions.eq(AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(),unidadeFuncional));
		}
		return this.executeCriteria(criteria, firstResult, maxResults,orderProperty, asc);

	}

	/**
	/**
	 * contador AnuTipoItemDietaUnfs de acordo com a unidade funcional 
	 * 
	 * @param unidadeFuncional
	 * @return
	 */
	public Integer listarAnuTipoItemDietaUnfsCount(AghUnidadesFuncionais unidadeFuncional) {
		return montarAnuTipoItemDietaUnfsUnf(unidadeFuncional);
	}
	
	private Integer montarAnuTipoItemDietaUnfsUnf(AghUnidadesFuncionais unidadeFuncional) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AnuTipoItemDietaUnfs.class);

		if (unidadeFuncional != null) {
			criteria.add(Restrictions.eq(AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(),unidadeFuncional));
		}

		List<AnuTipoItemDietaUnfs> listaMpmCuidadoUsualUnif = this.executeCriteria(criteria);
		return listaMpmCuidadoUsualUnif.size();
	}
	
	/**
	 * Metodo que deletar o vinculo.
	 * 
	 * @param anuTipoItemDietaUnfsTmp
	 */	

	public void apagar(AnuTipoItemDietaUnfs anuTipoItemDietaUnfsTmp){
		this.remover(anuTipoItemDietaUnfsTmp);
		this.flush();
	}

	/**
	 * Metodo que vincular todas unidade funcionais aos tipos de Dietas.
	 * 
	 * @param rapServidores
	 * @throws AGHUNegocioException
	 */
	public void inserirTodosTiposDietasUnf(RapServidores rapServidores)	throws ApplicationBusinessException {

		DetachedCriteria criteria0 = DetachedCriteria.forClass(AnuTipoItemDieta.class);
		List<AnuTipoItemDieta> listaAnuTipoItemDieta = this.executeCriteria(criteria0);

		DetachedCriteria criteria = DetachedCriteria.forClass(AghUnidadesFuncionais.class);
		List<AghUnidadesFuncionais> listaAghUnidadesFuncionais = this.executeCriteria(criteria);

		DetachedCriteria criteria2 = DetachedCriteria.forClass(AnuTipoItemDietaUnfs.class);
		List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs = this.executeCriteria(criteria2);

		List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfTmp = new ArrayList<AnuTipoItemDietaUnfs>();

		for (AnuTipoItemDieta anuTipoItemDieta : listaAnuTipoItemDieta) {
			for (AghUnidadesFuncionais aghUnidadesFuncionais : listaAghUnidadesFuncionais) {
				AnuTipoItemDietaUnfs anuTipoItemDietaUnfs = new AnuTipoItemDietaUnfs();
				if (excluirLinha(anuTipoItemDieta, aghUnidadesFuncionais,listaAnuTipoItemDietaUnfs)) {
					anuTipoItemDietaUnfs.setTipoItemDieta(anuTipoItemDieta);
					anuTipoItemDietaUnfs.setUnidadeFuncional(aghUnidadesFuncionais);
					anuTipoItemDietaUnfs.setServidor(rapServidores);
					anuTipoItemDietaUnfs.setCriadoEm(new Date());
					listaAnuTipoItemDietaUnfTmp.add(anuTipoItemDietaUnfs);
				}
			}
		}

		if (listaAnuTipoItemDietaUnfTmp.size() != 0) {
			for (AnuTipoItemDietaUnfs anuTipoItemDietaUnfsTmp : listaAnuTipoItemDietaUnfTmp) {
				persistir(anuTipoItemDietaUnfsTmp);
				flush();
			}
		} else {
			throw new ApplicationBusinessException(anuTipoItemDietaUnfsDAO.MENSAGEM_TODOS_TIPOS_DIETAS_UNIDADE_EXISTE);
		}
	}

	private boolean excluirLinha(AnuTipoItemDieta anuTipoItemDieta,	AghUnidadesFuncionais aghUnidadesFuncionais,
			List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) {

		for (AnuTipoItemDietaUnfs anuTipoItemDietaUnfsTmp : listaAnuTipoItemDietaUnfs) {
			if (anuTipoItemDietaUnfsTmp.getTipoItemDieta().equals(anuTipoItemDieta)	&& anuTipoItemDietaUnfsTmp.getUnidadeFuncional().equals(
							aghUnidadesFuncionais)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Metodo que vincular uma unidade funcionais aos tipos de Dietas.
	 * 
	 * @param rapServidores
	 * @throws AGHUNegocioException
	 */
	public void inserirTiposDietasUnf(AghUnidadesFuncionais unidadeFuncional,RapServidores rapServidores) throws ApplicationBusinessException {

		DetachedCriteria criteria0 = DetachedCriteria.forClass(AnuTipoItemDieta.class);
		List<AnuTipoItemDieta> listaAnuTipoItemDieta = this.executeCriteria(criteria0);

		DetachedCriteria criteria2 = DetachedCriteria.forClass(AnuTipoItemDietaUnfs.class);

		if (unidadeFuncional != null) {
			criteria2.add(Restrictions.eq(AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(),unidadeFuncional));
		}

		List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs = this.executeCriteria(criteria2);

		if (listaAnuTipoItemDietaUnfs.size() != listaAnuTipoItemDieta.size()) {
			for (AnuTipoItemDieta anuTipoItemDieta : listaAnuTipoItemDieta) {
				AnuTipoItemDietaUnfs anuTipoItemDietaUnfs = new AnuTipoItemDietaUnfs();
				if (incluirUnf(anuTipoItemDieta, unidadeFuncional,listaAnuTipoItemDietaUnfs)) {
					anuTipoItemDietaUnfs.setTipoItemDieta(anuTipoItemDieta);
					anuTipoItemDietaUnfs.setUnidadeFuncional(unidadeFuncional);
					anuTipoItemDietaUnfs.setServidor(rapServidores);
					anuTipoItemDietaUnfs.setCriadoEm(new Date());
					persistir(anuTipoItemDietaUnfs);
					flush();
				}
			}
		} else {
			throw new ApplicationBusinessException(anuTipoItemDietaUnfsDAO.MENSAGEM_TIPOS_DIETAS_UNIDADE_EXISTE);
		}
	}
	private boolean incluirUnf(AnuTipoItemDieta anuTipoItemDieta,AghUnidadesFuncionais aghUnidadesFuncionais,List<AnuTipoItemDietaUnfs> listaAnuTipoItemDietaUnfs) {

		for (AnuTipoItemDietaUnfs anuTipoItemDietaUnfs : listaAnuTipoItemDietaUnfs) {
			if (anuTipoItemDietaUnfs.getTipoItemDieta().equals(anuTipoItemDieta)&& anuTipoItemDietaUnfs.getUnidadeFuncional().equals(
							aghUnidadesFuncionais)) {
				return false;
			}
		}
		return true;
	}
}
