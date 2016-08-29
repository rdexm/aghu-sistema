package br.gov.mec.aghu.sicon.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoTipoContratoSicon;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;

/**
 * @modulo sicon
 * @author cvagheti
 *
 */
public class ScoResContratoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoResContrato> {

	private static final long serialVersionUID = 6575274303833272298L;

	/**
	 * Gets the rescicao by contrato.
	 * 
	 * @param input
	 *            the input
	 * @return the rescicao by contrato
	 */
	public ScoResContrato getRescicaoByContrato(ScoContrato input) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoResContrato.class);
		criteria.add(Restrictions.eq(ScoResContrato.Fields.CONT_SEQ.toString(),
				input));
		@SuppressWarnings("rawtypes")
		List res = executeCriteria(criteria);
		if (res != null && res.size() > 0) {
			return (ScoResContrato) res.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Pesquisa as rescisões conforme a lista de contrato informada.
	 */
	public List<ScoResContrato> listarRescisoesContrato(
			List<ScoContrato> listContratos) {

		List<ScoResContrato> result = new ArrayList<ScoResContrato>();

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoResContrato.class);
		
		criteria.createAlias(ScoResContrato.Fields.CONT_SEQ.toString(), "contrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("contrato." + ScoContrato.Fields.FORNECEDOR.toString(), "fornecedor", JoinType.LEFT_OUTER_JOIN);

		if (listContratos != null && listContratos.size() > 0) {
			criteria.add(Restrictions.in(
					ScoResContrato.Fields.CONT_SEQ.toString(), listContratos));
		}

		criteria.addOrder(Order.asc(ScoResContrato.Fields.CONT_SEQ.toString()));
		criteria.addOrder(Order.asc(ScoResContrato.Fields.SEQ.toString()));

		result = this.executeCriteria(criteria);

		return result;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public List<ScoResContrato> pesquisarRescisoes(ContratoFiltroVO filtro) {

		List<ScoResContrato> result = new ArrayList<ScoResContrato>();
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoResContrato.class);
		
		DetachedCriteria criteriaContrato = criteria.createCriteria(ScoResContrato.Fields.CONT_SEQ.toString(), JoinType.LEFT_OUTER_JOIN);
		DetachedCriteria criteriaFornecedorContrato = criteriaContrato.createCriteria(ScoContrato.Fields.FORNECEDOR.toString(), JoinType.LEFT_OUTER_JOIN);

		if (filtro != null
				&& filtro.getSitEnvResc() != null) {
			criteria.add(Restrictions.eq(ScoResContrato.Fields.IND_SITUACAO.toString(), filtro.getSitEnvResc()));
		}
		
		if(filtro.getContrato() != null){
			
			if (filtro.getContrato().getNrContrato() != null
					|| filtro.getContrato().getDtInicioVigencia() != null
					|| filtro.getContrato().getDtFimVigencia() != null
					|| filtro.getContrato().getLicitacao()!=null
					|| filtro.getContrato().getTipoContratoSicon() != null
					|| filtro.getContrato().getFornecedor()!=null
					|| filtro.getContrato().getServidorGestor() != null
					|| filtro.getContrato().getServidorFiscal() != null
					|| filtro.getAf().getNumero() != null){
				
				

				if(filtro.getContrato().getNrContrato() != null){					
					if (filtro.getContrato().getSeq() != null){
					    criteriaContrato.add(Restrictions.eq(ScoContrato.Fields.SEQ.toString(), filtro.getContrato().getSeq()));
					}else{
						return null;
					}
				}
				
				if(filtro.getContrato().getDtInicioVigencia() != null){
					criteriaContrato.add(Restrictions.ge(ScoContrato.Fields.DT_INICIO_VIGENCIA.toString(), filtro.getContrato().getDtInicioVigencia()));
				}
				
				if(filtro.getContrato().getDtFimVigencia() != null){					
					criteriaContrato.add(Restrictions.le(ScoContrato.Fields.DT_FIM_VIGENCIA.toString(), filtro.getContrato().getDtFimVigencia()));
				}
				
				if(filtro.getContrato().getLicitacao()!=null){					
					DetachedCriteria criteriaLicitacaoContrato = criteriaContrato.createCriteria(ScoContrato.Fields.LICITACAO.toString()); 
					criteriaLicitacaoContrato.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), filtro.getContrato().getLicitacao().getNumero()));
				}
				
				if(filtro.getContrato().getTipoContratoSicon() != null){					
					DetachedCriteria criteriaTipoContrato = criteriaContrato.createCriteria(ScoContrato.Fields.TIPO_CONTRATO_SICON.toString());
					criteriaTipoContrato.add(Restrictions.eq(ScoTipoContratoSicon.Fields.CODIGO_SICON.toString(), filtro.getContrato().getTipoContratoSicon().getCodigoSicon()));
				}
				
				if(filtro.getContrato().getFornecedor()!=null){
					criteriaFornecedorContrato.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), filtro.getContrato().getFornecedor().getNumero()));
				}  
				
				if(filtro.getAf().getNumero()!=null && filtro.getAf().getNroComplemento()!=null){
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

		}

		criteria.addOrder(Order.asc(ScoResContrato.Fields.CONT_SEQ.toString()));
		criteria.addOrder(Order.asc(ScoResContrato.Fields.SEQ.toString()));

		result = executeCriteria(criteria);
		
		return result;
	}

	/**
	 * Pesquisa as rescisões dos contratos e conforme filtro de rescisão.
	 */
	public List<ScoResContrato> listarRescisoesContratoFiltro(
			List<ScoContrato> listContratos,
			ContratoFiltroVO filtroContratoIntegracao) {

		List<ScoResContrato> result = new ArrayList<ScoResContrato>();

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoResContrato.class);
		
		criteria.createAlias(ScoResContrato.Fields.CONT_SEQ.toString(), "contrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("contrato." + ScoContrato.Fields.FORNECEDOR.toString(), "fornecedor", JoinType.LEFT_OUTER_JOIN);

		if (listContratos != null && listContratos.size() > 0) {
			criteria.add(Restrictions.in(
					ScoResContrato.Fields.CONT_SEQ.toString(), listContratos));
		}

		if (filtroContratoIntegracao != null
				&& filtroContratoIntegracao.getSitEnvResc() != null) {
			criteria.add(Restrictions.eq(
					ScoResContrato.Fields.IND_SITUACAO.toString(),
					filtroContratoIntegracao.getSitEnvResc()));
		}

		criteria.addOrder(Order.asc(ScoResContrato.Fields.CONT_SEQ.toString()));
		criteria.addOrder(Order.asc(ScoResContrato.Fields.SEQ.toString()));

		result = this.executeCriteria(criteria);

		return result;
	}

	public List<ScoResContrato> listarRescisoesSituacaoEnvio(
			DominioSituacaoEnvioContrato situacao) {

		List<ScoResContrato> result = new ArrayList<ScoResContrato>();
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoResContrato.class);
		
		criteria.createAlias(ScoResContrato.Fields.CONT_SEQ.toString(), "contrato", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("contrato." + ScoContrato.Fields.FORNECEDOR.toString(), "fornecedor", JoinType.LEFT_OUTER_JOIN);

		if (situacao != null) {
			criteria.add(Restrictions.eq(
					ScoResContrato.Fields.IND_SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc(ScoResContrato.Fields.CONT_SEQ.toString()));
		criteria.addOrder(Order.asc(ScoResContrato.Fields.SEQ.toString()));

		result = this.executeCriteria(criteria);

		return result;
	}
}
