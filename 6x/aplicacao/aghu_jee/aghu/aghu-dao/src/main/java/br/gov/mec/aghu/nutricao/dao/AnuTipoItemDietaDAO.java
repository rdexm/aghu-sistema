package br.gov.mec.aghu.nutricao.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AnuTipoItemDietaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AnuTipoItemDieta> {

	private static final long serialVersionUID = -4030410513423381455L;
	private static final SimpleDateFormat DATE_FORMAT_COM_HORA = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	private static final String SEPARATOR = ".";
	
	/**
	 * Retorna tipos de itens de dieta ativos com o id ou descrição fornecidas.
	 * 
	 * @param idOuDescricao
	 * 
	 * @return
	 */
	private DetachedCriteria obterCriteriaTiposItemDieta(Object idOuDescricao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AnuTipoItemDieta.class);

		criteria.add(Restrictions.eq(AnuTipoItemDieta.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		String strParametro = (String) idOuDescricao;
		Integer seqTipoItemDieta = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			seqTipoItemDieta = Integer.valueOf(strParametro);
		}

		if (seqTipoItemDieta != null) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.SEQ.toString(), seqTipoItemDieta));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(
						AnuTipoItemDieta.Fields.DESCRICAO.toString(),
						strParametro.toUpperCase(), MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}
	
	public List<AnuTipoItemDieta> obterTiposItemDieta(Object idOuDescricao) {
		DetachedCriteria criteria = obterCriteriaTiposItemDieta(idOuDescricao);
		criteria.addOrder(Order.asc(AnuTipoItemDieta.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long obterTiposItemDietaCount(Object idOuDescricao) {
		DetachedCriteria criteria = obterCriteriaTiposItemDieta(idOuDescricao);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna tipo dieta com o id.
	 * 
	 * @param idTipoDieta
	 * 
	 * @return
	 */
	public AnuTipoItemDieta obterTipoDieta(Integer atdSeq, Enum... alias) {
		if(atdSeq == null){
			return null;
		}
		return super.obterPorChavePrimaria(atdSeq, alias);
	}
	
	public AnuTipoItemDieta obterTipoDietaEdicao(Integer idTipoDieta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AnuTipoItemDieta.class);
		criteria.add(Restrictions.eq(AnuTipoItemDieta.Fields.SEQ.toString(), idTipoDieta));
		criteria.createAlias(AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString(), "TFA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(), "UMM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AnuTipoItemDieta.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		return (AnuTipoItemDieta) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Retorna tipo dieta com as unidades funcionais com o id.
	 * 
	 * @param idTipoDieta
	 * 
	 * @return
	 */
	public AnuTipoItemDieta obterTipoDietaComUnidadesFuncionais(Integer atdSeq){
		if(atdSeq == null){
			return null;
		}
		AnuTipoItemDieta itemDieta = super.obterPorChavePrimaria(atdSeq);

		if(itemDieta != null){
			/*Inicializa lazy*/
			itemDieta.getTipoDietaUnidadeFuncional().size();
		}
		return itemDieta;
	}

	/**
	 * 
	 * 
	 * @param idOuDescricao
	 * @param unidade
	 * @param neonatologia
	 * @param adulto
	 * @param pediatrico
	 * @return
	 */
	public List<AnuTipoItemDieta> obterTiposItemDieta(Object idOuDescricao,
			AghUnidadesFuncionais unidade, boolean neonatologia,
			boolean adulto, boolean pediatrico) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AnuTipoItemDieta.class);

		criteria.createAlias(
				AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				"UNIDADE_MEDIDA_MEDICA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(
				AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString(),
				"TPF", JoinType.LEFT_OUTER_JOIN);
		
		if(idOuDescricao != null){
			String strParametro = (String) idOuDescricao;
			Integer seqTipoItemDieta = null;

			if (CoreUtil.isNumeroInteger(strParametro)) {
				seqTipoItemDieta = Integer.valueOf(strParametro);
			}

			if (seqTipoItemDieta != null) {
				criteria.add(Restrictions.eq(
						AnuTipoItemDieta.Fields.SEQ.toString(), seqTipoItemDieta));
			} else {
				if (StringUtils.isNotBlank(strParametro)) {
					criteria.add(Restrictions.ilike(
							AnuTipoItemDieta.Fields.DESCRICAO.toString(),
							strParametro.toUpperCase(), MatchMode.ANYWHERE));
				}
			}			
		}
		
		criteria.add(Restrictions.eq(
				AnuTipoItemDieta.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		if (neonatologia) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.IND_NEONATOLOGIA.toString(),
					neonatologia));
		} else if (pediatrico) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.IND_PEDIATRIA.toString(),
					pediatrico));
		} else if (adulto) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.IND_ADULTO.toString(), adulto));
		}
		
		if (unidade != null) {				
			DetachedCriteria subCriteria = DetachedCriteria
					.forClass(AnuTipoItemDietaUnfs.class);
			subCriteria.add(Restrictions.eq(
					AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(),
					unidade));
			subCriteria.setProjection(Projections
					.property(AnuTipoItemDietaUnfs.Fields.TIPO_ITEM_DIETA
							.toString()));
						
			criteria.add(Subqueries.propertyIn(
					AnuTipoItemDieta.Fields.SEQ.toString(), subCriteria));
		}

		criteria.addOrder(Order.asc(AnuTipoItemDieta.Fields.DESCRICAO
				.toString()));

		return executeCriteria(criteria);

	}
	
	public Long obterTiposItemDietaCount(Object idOuDescricao,
			AghUnidadesFuncionais unidade, boolean neonatologia,
			boolean adulto, boolean pediatrico) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AnuTipoItemDieta.class);

		criteria.createAlias(
				AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),
				"UNIDADE_MEDIDA_MEDICA", Criteria.LEFT_JOIN);
		
		if(idOuDescricao != null){
			String strParametro = (String) idOuDescricao;
			Integer seqTipoItemDieta = null;

			if (CoreUtil.isNumeroInteger(strParametro)) {
				seqTipoItemDieta = Integer.valueOf(strParametro);
			}

			if (seqTipoItemDieta != null) {
				criteria.add(Restrictions.eq(
						AnuTipoItemDieta.Fields.SEQ.toString(), seqTipoItemDieta));
			} else {
				if (StringUtils.isNotBlank(strParametro)) {
					criteria.add(Restrictions.ilike(
							AnuTipoItemDieta.Fields.DESCRICAO.toString(),
							strParametro.toUpperCase(), MatchMode.ANYWHERE));
				}
			}			
		}
		
		criteria.add(Restrictions.eq(
				AnuTipoItemDieta.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		if (neonatologia) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.IND_NEONATOLOGIA.toString(),
					neonatologia));
		} else if (pediatrico) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.IND_PEDIATRIA.toString(),
					pediatrico));
		} else if (adulto) {
			criteria.add(Restrictions.eq(
					AnuTipoItemDieta.Fields.IND_ADULTO.toString(), adulto));
		}
		
		if (unidade != null) {				
			DetachedCriteria subCriteria = DetachedCriteria
					.forClass(AnuTipoItemDietaUnfs.class);
			subCriteria.add(Restrictions.eq(
					AnuTipoItemDietaUnfs.Fields.UNIDADE_FUNCIONAL.toString(),
					unidade));
			subCriteria.setProjection(Projections
					.property(AnuTipoItemDietaUnfs.Fields.TIPO_ITEM_DIETA
							.toString()));
						
			criteria.add(Subqueries.propertyIn(
					AnuTipoItemDieta.Fields.SEQ.toString(), subCriteria));
		}

		/*criteria.addOrder(Order.asc(AnuTipoItemDieta.Fields.DESCRICAO
				.toString()));*/

		return executeCriteriaCount(criteria);

	}
	
	
	
	/**
	 * 
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	private DetachedCriteria getDetachedCriteriaPesquisarTipoItemDieta(Integer codigo, String descricao, DominioSituacao situacao, DominioSimNao usoNutricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(AnuTipoItemDieta.class);
		criteria.createAlias(AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString(),AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString(),DetachedCriteria.LEFT_JOIN);
		criteria.createAlias(AnuTipoItemDieta.Fields.SERVIDOR.toString(),AnuTipoItemDieta.Fields.SERVIDOR.toString());
		criteria.createAlias(AnuTipoItemDieta.Fields.SERVIDOR.toString()+SEPARATOR+RapServidores.Fields.PESSOA_FISICA.toString(),RapServidores.Fields.PESSOA_FISICA.toString());
		criteria.createAlias(AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString(),DetachedCriteria.LEFT_JOIN);
		
		if(codigo != null){
			criteria.add(Restrictions.eq(AnuTipoItemDieta.Fields.SEQ.toString(), codigo));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(AnuTipoItemDieta.Fields.IND_SITUACAO.toString(), situacao));
		}
		if(descricao != null && StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike(AnuTipoItemDieta.Fields.DESCRICAO.toString(), descricao.toUpperCase(), MatchMode.ANYWHERE));
		}
		if(usoNutricao != null){
			criteria.add(Restrictions.eq(AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString()+SEPARATOR+MpmUnidadeMedidaMedica.Fields.IND_USO_NUTRICAO.toString(), usoNutricao));
		}

		return criteria;
	}
	
	
		/**
		 * where (exists 
		 * 			(select 1 from MPM_UNIDADE_MEDIDA_MEDICAS UMM where 
		 * 				(ANU_TIPO_ITEM_DIETAS.UMM_SEQ = UMM.SEQ (+))
						and (UMM.IND_SITUACAO = 'A' and
						UMM.IND_USO_NUTRICAO = 'S')
					)
				  or (ANU_TIPO_ITEM_DIETAS.UMM_SEQ is null )
				 )
		 */
	
	/**
	 * 
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	public Long pesquisarTipoItemDietaCount(Integer codigo, String descricao, DominioSituacao situacao, DominioSimNao usuNutricao){
		return this.executeCriteriaCount(getDetachedCriteriaPesquisarTipoItemDieta(codigo, descricao, situacao, usuNutricao));
	}

	/**
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<AnuTipoItemDietaVO> pesquisarTipoItemDieta(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,Integer codigo, String descricao, DominioSituacao situacao, DominioSimNao usoNutricao){

				DetachedCriteria criteria = this.createPesquisaCriteriaVPL(firstResult, maxResult, orderProperty, asc, codigo, descricao, situacao, usoNutricao);
				
				List<AnuTipoItemDietaVO> retorno = new ArrayList<AnuTipoItemDietaVO>();
				
				List<Object[]> res = this.executeCriteria(criteria, firstResult,maxResult, orderProperty, asc);
				
				// Gerando lista de VOs
				Iterator<Object[]> it = res.iterator();
				while (it.hasNext()) {
					Object[] obj = it.next();
					AnuTipoItemDietaVO vo = new AnuTipoItemDietaVO();
					if (obj[0] != null) {
						vo.setSeq((Integer) obj[0]);
					}
					if (obj[1] != null) {
						vo.setDescricao((String) obj[1]);
					}
					if (obj[2] != null) {
						vo.setIndDietaPadronizada((Boolean) obj[2]);
					}
					if (obj[3] != null) {
						vo.setIndItemUnico((Boolean) obj[3]);
					}
					if (obj[4] != null) {
						vo.setFrequencia((Short) obj[4]);
					}
					if (obj[5] != null) {
						vo.setCriadoEm(DATE_FORMAT_COM_HORA.format((Date) obj[5]));
					}
					if (obj[6] != null) {
						vo.setIndSituacao((DominioSituacao) obj[6]);
					}
					if (obj[7] != null) {
						vo.setUn((String)obj[7]);
					}
					if (obj[8] != null) {
						vo.setAprazamento((String)obj[8]);
					}
					if (obj[9] != null) {
						vo.setAprazamento(vo.getAprazamento()+" "+(String)obj[9]);
					}
					if (obj[10] != null) {
						vo.setResponsavel((String)obj[10]);
					}
					if (obj[11] != null) {
						vo.setSeqAprazamento((Short)obj[11]);
					}
					retorno.add(vo);
				}
				
				return retorno;
				
			}
	
	/**
	 * 		
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param codigo
	 * @param descricao
	 * @param situacao
	 * @return
	 */
	private DetachedCriteria createPesquisaCriteriaVPL(Integer firstResult,Integer maxResult, String orderProperty, boolean asc, Integer codigo, String descricao, DominioSituacao situacao, DominioSimNao usoNutricao){

		DetachedCriteria criteria = getDetachedCriteriaPesquisarTipoItemDieta(codigo, descricao, situacao, usoNutricao);

		ProjectionList  projectionList = Projections.projectionList();

		projectionList.add(Projections.property(AnuTipoItemDieta.Fields.SEQ.toString()))		
			.add(Projections.property(AnuTipoItemDieta.Fields.DESCRICAO.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.IND_DIETA_PADRONIZADA.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.IND_ITEM_UNICO.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.FREQUENCIA.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.CRIADO_EM.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.IND_SITUACAO.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.UNIDADE_MEDIDA_MEDICAS.toString()+SEPARATOR+MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString()+SEPARATOR+MpmTipoFrequenciaAprazamento.Fields.DESCRICAO.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString()+SEPARATOR+MpmTipoFrequenciaAprazamento.Fields.SIGLA.toString()))
			.add(Projections.property(RapServidores.Fields.PESSOA_FISICA.toString()+SEPARATOR+RapPessoasFisicas.Fields.NOME.toString()))
			.add(Projections.property(AnuTipoItemDieta.Fields.TIPO_FREQ_APRAZAMENTOS.toString()+SEPARATOR+MpmTipoFrequenciaAprazamento.Fields.SEQ.toString()));

		criteria.setProjection(projectionList);

		criteria.addOrder(Order.desc(AnuTipoItemDieta.Fields.SEQ.toString()));

		return criteria;
		
	}	
	
	/**
	 * Verificar a existência de registros de tipo item de dieta em outras entidades
	 * @param tipoDieta
	 * @param class1
	 * @param field
	 * @return
	 */
	public boolean existeItemDieta(AnuTipoItemDieta tipoDieta, Class class1, Enum field) {

		if (tipoDieta == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		DetachedCriteria criteria = DetachedCriteria.forClass(class1);
		criteria.add(Restrictions.eq(field.toString(),tipoDieta));
		
		return (executeCriteriaCount(criteria) > 0);
	}	
}