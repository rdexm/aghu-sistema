package br.gov.mec.aghu.sicon.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAditContratoId;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;

// TODO: Auto-generated Javadoc
/**
 * Classe para acesso a dados do model {@code ScoAditContrato}.
 *
 * @modulo sicon
 * @author ptneto
 */
public class ScoAditContratoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoAditContrato> {

	private static final long serialVersionUID = 4399857255986297780L;

	/**
	 * obtem lista de aditivos de contrato associados a um determinado tipo de
	 * contrato informado em parâmetro de entrada.
	 *
	 * @param _tipoContrato the _tipo contrato
	 * @return Listagem de aditivos associados.
	 */
	public List<ScoAditContrato> obterListaAditContratoAssociado(
			ScoTipoContratoSicon _tipoContrato) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoAditContrato.class);

		criteria.add(Restrictions.eq(
				ScoAditContrato.Fields.TIPO_CONTRATO_SICON.toString(),
				_tipoContrato));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Obter aditivos do contrato.
	 *
	 * @param input the input
	 * @return the list
	 */
	public List<ScoAditContrato> obterAditivosByContrato(ScoContrato input){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAditContrato.class);
		criteria.add(Restrictions.eq(ScoAditContrato.Fields.CONT_SEQ.toString(),input.getSeq()));
		return this.executeCriteria(criteria);

	}
	
	public List<ScoAditContrato> obterAditivosPorContratoSituacao(ScoContrato input, DominioSituacaoEnvioContrato situacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAditContrato.class);
		
		criteria.add(Restrictions.eq(ScoAditContrato.Fields.CONT_SEQ.toString(),input.getSeq()));
		criteria.add(Restrictions.eq(ScoAditContrato.Fields.SITUACAO.toString(), situacao));
		
		return this.executeCriteria(criteria);

	}
	
	/**
	 *  Obter aditivos dos contratos que contém na listagem e 
	 * através do filtro geral de pesquisa. 
	 *
	 */
	public List<ScoAditContrato> listarAditivosContrato(List<ScoContrato> listContratos){
		
		List<ScoAditContrato> result = new ArrayList<ScoAditContrato>();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAditContrato.class);
		
		if(listContratos != null &&
		   listContratos.size() > 0){
			criteria.add(Restrictions.in(ScoAditContrato.Fields.CONTRATO.toString(), listContratos));
		}
		
		criteria.addOrder(Order.asc(ScoAditContrato.Fields.CONTRATO.toString()));
		criteria.addOrder(Order.asc(ScoAditContrato.Fields.NUMERO.toString()));
		
		result = this.executeCriteria(criteria);
		
		return result;
	}
	
	/**
	 * Pesquisa os aditivos com base no filtro geral de integração dos contratos.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<ScoAditContrato> pesquisarAditivos(ContratoFiltroVO filtro){
		
		List<ScoAditContrato> result = new ArrayList<ScoAditContrato>();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAditContrato.class);
		
		DetachedCriteria criteriaContrato = criteria.createCriteria(ScoAditContrato.Fields.CONTRATO.toString(), JoinType.LEFT_OUTER_JOIN);
		DetachedCriteria criteriaFornecedorContrato = criteriaContrato.createCriteria(ScoContrato.Fields.FORNECEDOR.toString(), JoinType.LEFT_OUTER_JOIN);
		
		
		if(filtro.getSitEnvAditivo() != null){
			criteria.add(Restrictions.eq(ScoAditContrato.Fields.SITUACAO.toString(), 
					                     filtro.getSitEnvAditivo()));
		}
		
		if(filtro.getContrato() != null){
			
			if (filtro.getContrato().getServidorGestor() != null
					|| filtro.getContrato().getServidorFiscal() != null
					|| filtro.getContrato().getLicitacao() != null
					|| filtro.getContrato().getFornecedor() != null
					|| filtro.getContrato().getNrContrato() != null
					|| filtro.getAf().getNumero() != null) {

				
				
				if(filtro.getContrato().getNrContrato() != null){					
					criteriaContrato.add(Restrictions.eq(ScoContrato.Fields.NR_CONTRATO.toString(), filtro.getContrato().getNrContrato()));
				}
				
				if (filtro.getContrato().getLicitacao() != null){
					DetachedCriteria criteriaLicitacaoContrato = criteriaContrato.createCriteria(ScoContrato.Fields.LICITACAO.toString()); 
					
					criteriaLicitacaoContrato.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), 
							filtro.getContrato().getLicitacao().getNumero()));
				}
				
				if(filtro.getContrato().getFornecedor()!=null){
										
					criteriaFornecedorContrato.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), 
							filtro.getContrato().getFornecedor().getNumero()));
				}
				
				if(filtro.getAf().getNumero() != null && filtro.getAf().getNroComplemento() != null){
					DetachedCriteria afCrit = criteriaContrato.createCriteria(ScoContrato.Fields.AFS.toString());
					
					DetachedCriteria afContratoCrit = afCrit.createCriteria(ScoAfContrato.Fields.AUT_FORN.toString()); 
					
					afContratoCrit.add(Restrictions.eq(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), filtro.getAf().getNroComplemento()));
					
					DetachedCriteria propCrit = afContratoCrit.createCriteria(ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString());

					DetachedCriteria licCrit = propCrit.createCriteria(ScoPropostaFornecedor.Fields.LICITACAO.toString());
					
					licCrit.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), filtro.getAf().getNumero()));
				}			

				if(filtro.getContrato().getServidorGestor() != null){					
					DetachedCriteria criteriaGestorContrato = criteriaContrato.createCriteria(ScoContrato.Fields.SERVIDOR_GESTOR.toString()); 
					criteriaGestorContrato.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), filtro.getContrato().getServidorGestor().getId().getMatricula()));
				}

				if(filtro.getContrato().getServidorFiscal() != null){					
					DetachedCriteria criteriaFiscalContrato = criteriaContrato.createCriteria(ScoContrato.Fields.SERVIDOR_FISCAL.toString()); 
					criteriaFiscalContrato.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), filtro.getContrato().getServidorFiscal().getId().getMatricula()));
				}
				
			}
			
			if(filtro.getContrato().getDtInicioVigencia() != null){
				criteria.add(Restrictions.ge(ScoAditContrato.Fields.DT_INICIO_VIGENCIA.toString(),
						                     filtro.getContrato().getDtInicioVigencia()));
			}
			
			if(filtro.getContrato().getDtFimVigencia() != null){
				criteria.add(Restrictions.le(ScoAditContrato.Fields.DT_FIM_VIGENCIA.toString(),
						                     filtro.getContrato().getDtFimVigencia()));
			}
			
			if(filtro.getContrato().getTipoContratoSicon() != null){
				DetachedCriteria criteriaTipoContrato = criteria.createCriteria(ScoAditContrato.Fields.TIPO_CONTRATO_SICON.toString());
				criteriaTipoContrato.add(Restrictions.eq(ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(), filtro.getContrato().getTipoContratoSicon().getCodigoSicon()));
			}
												
 		}
		
		criteria.addOrder(Order.asc(ScoAditContrato.Fields.CONTRATO.toString()));
		criteria.addOrder(Order.asc(ScoAditContrato.Fields.NUMERO.toString()));

		result = executeCriteria(criteria);
		
		return result;
	}

	public ScoAditContrato obterOld(ScoAditContrato input) {
		if (input == null || input.getId() == null) {
			throw new IllegalArgumentException("Argumento obrigatório.");
		}

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoAditContrato.class);

		// projection
		ProjectionList projectionList = Projections.projectionList().add(
				Projections.property(ScoAditContrato.Fields.DT_RECISAO
						.toString()),
				ScoAditContrato.Fields.DT_RECISAO.toString());
		criteria.setProjection(projectionList);

		// restrictions
		criteria.add(Restrictions.idEq(input.getId()));

		criteria.setResultTransformer(Transformers
				.aliasToBean(ScoAditContrato.class));

		ScoAditContrato result = (ScoAditContrato) executeCriteriaUniqueResult(criteria);

		return result;
	}

	/**
	 * Query criada para evitar LIE no momento de recuperar o tipo do contrato
	 * @param aditId
	 * @return
	 */
	public ScoAditContrato obterContratoPorIdComContrato(ScoAditContratoId aditId){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAditContrato.class);
		
		criteria.createAlias(ScoAditContrato.Fields.TIPO_CONTRATO_SICON.toString(), "TIPO_CONT", JoinType.INNER_JOIN);
		criteria.createAlias(ScoAditContrato.Fields.CONTRATO.toString(), "CONT", JoinType.INNER_JOIN);
		criteria.createAlias("CONT." + ScoContrato.Fields.TIPO_CONTRATO_SICON.toString(),"TIPO_CONT_ORIGINAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CONT." + ScoContrato.Fields.FORNECEDOR.toString(),"FORNEC_ORIGINAL", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.idEq(aditId));
		
		ScoAditContrato result = (ScoAditContrato) executeCriteriaUniqueResult(criteria);
		
		return result;
	}
}