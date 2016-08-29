package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class FatConvenioSaudeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvenioSaude> {

	private static final long serialVersionUID = -4640172257887842356L;

	public enum FaturamentoRNExceptionCode implements BusinessExceptionCode {
		FAT_00795;
	}
	
	/**
	 * Retorna todos os convênios
	 * @return
	 */
	public List<FatConvenioSaude> obterConveniosSaudeAtivos() {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
		
	}
	
	/**Consultar FatConvenioSaude ativo por codigo. 
	 * @param codConvSaude
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long obterCountConvenioSaudeAtivoPorPgdSeq(Short pgdSeq) {
		return executeCriteriaCount(this.montarCriteriaBuscarAtivosPorPgdSeq(pgdSeq));
	}
	
	
	public List<FatConvenioSaude> listarConveniosSaudeAtivosPorPgdSeq(Short pgdSeq) {
		return this.executeCriteria(this.montarCriteriaBuscarAtivosPorPgdSeq(pgdSeq));
	}
	
	private DetachedCriteria montarCriteriaBuscarAtivosPorPgdSeq(Short pgdSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);
		criteria.createAlias(FatConvenioSaude.Fields.PAGADOR.toString(), "pgd");
		criteria.add(Restrictions.eq("pgd." + AacPagador.Fields.SEQ.toString(), pgdSeq));
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	public Long pesquisaCount(Short codigo, String descricao, DominioSituacao situacao) {
		return executeCriteriaCount(criarPesquisaCriteria(codigo, descricao, situacao));
	}

	/**
	 * Cria o Criteria que é usado na pesquisa.
	 * 
	 * @dbtables FatConvenioSaude select
	 * 
	 * @param codigo
	 * @param descricao
	 * @param grupoConvenio
	 * @param csAtivo
	 * @return
	 */
	private DetachedCriteria criarPesquisaCriteria(Short codigo,
			String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatConvenioSaude.class);

		criteria.createAlias(FatConvenioSaude.Fields.PAGADOR.toString(), "PGD", JoinType.LEFT_OUTER_JOIN);
		
		if (codigo != null) {
			criteria.add(Restrictions.eq(FatConvenioSaude.Fields.CODIGO
					.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(FatConvenioSaude.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(FatConvenioSaude.Fields.SITUACAO
					.toString(), situacao));
		}

		return criteria;
	}
	
	public List<FatConvenioSaude> pesquisar(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Short codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = criarPesquisaCriteria(codigo, descricao,
				situacao);

		criteria.addOrder(Order.asc(FatConvenioSaude.Fields.CODIGO.toString()));

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	/**
	 * @param codDescConvSaude
	 * @return
	 */
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricao(String codDescConvSaude, FatConvenioSaude.Fields orderBy) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);

		if (StringUtils.isNotBlank(codDescConvSaude)) {
			if (CoreUtil.isNumeroShort(codDescConvSaude)) {
				criteria.add(Restrictions.eq(FatConvenioSaude.Fields.CODIGO
						.toString(), Short.parseShort(codDescConvSaude)));
			} else {
				criteria.add(Restrictions.ilike(
						FatConvenioSaude.Fields.DESCRICAO.toString(),
						codDescConvSaude, MatchMode.ANYWHERE));
			}
		}

		if(orderBy != null) {
			criteria.addOrder(Order.asc(orderBy.toString()));
		}
		
		return this.executeCriteria(criteria);
	}
	
	
	public Long pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(
			String codDescConvSaude) {
		return this
				.executeCriteriaCount(montaCriteriaConveniosSaudePorCodigoOuDescricaoAtivos(codDescConvSaude));
	}

	private DetachedCriteria montaCriteriaConveniosSaudePorCodigoOuDescricaoAtivos(
			String codDescConvSaude) {
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatConvenioSaude.class);

		criteria.add(Restrictions.eq(
				FatConvenioSaude.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (StringUtils.isNotBlank(codDescConvSaude)) {
			if (CoreUtil.isNumeroShort(codDescConvSaude)) {
				criteria.add(Restrictions.eq(
						FatConvenioSaude.Fields.CODIGO.toString(),
						Short.parseShort(codDescConvSaude)));
			} else {
				criteria.add(Restrictions.ilike(
						FatConvenioSaude.Fields.DESCRICAO.toString(),
						codDescConvSaude, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	/**
	 * @param codDescConvSaude
	 * @return
	 */
	public List<FatConvenioSaude> pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(
			String codDescConvSaude) {
		DetachedCriteria criteria = montaCriteriaConveniosSaudePorCodigoOuDescricaoAtivos(codDescConvSaude);
		criteria.addOrder(Order.asc(FatConvenioSaude.Fields.DESCRICAO
				.toString()));
		return this
				.executeCriteria(criteria);
	}
	
	/**
	 * Método para buscar o convenio de saúde pelo seu codigo.
	 * 
	 * @param codigo
	 * @return
	 */
	public FatConvenioSaude obterConvenioSaude(Short codigo) {
		if (codigo == null) {
			return null;
		} else {
			return findByPK(FatConvenioSaude.class, codigo);
		}
	}
	/**
	 * Método para verificar se <code>FatConvenioSaude</code> está ativo.
	 * 
	 * ORADB Procedure FATK_CSP_RN.RN_CSPP_VER_CPS_ATIV
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void validarConvenioSaudeAtivo(final Short codConvSaude) throws ApplicationBusinessException {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);

		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.CODIGO.toString(), codConvSaude));

		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.SITUACAO.toString(), DominioSituacao.A));

		if (executeCriteria(criteria).isEmpty()) {
			throw new ApplicationBusinessException(FaturamentoRNExceptionCode.FAT_00795);
		}
	}
	
	public List<FatConvenioSaude> pesquisarConveniosSaudeGrupoSUS(Short codigoConvenio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.CODIGO.toString(), codigoConvenio));
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.GRUPO_CONVENIO.toString(), DominioGrupoConvenio.S));
		return executeCriteria(criteria);
	}
	
	public List<Byte> obterListaConvenioSaudeAtivoComPlanoAmbulatorialAtivo(Short codigoConvenio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);
		criteria.createAlias(FatConvenioSaude.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP");
		
		criteria.setProjection(Projections.property("CSP."+FatConvenioSaudePlano.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.CODIGO.toString(), codigoConvenio));
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CSP."+FatConvenioSaudePlano.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("CSP."+FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioTipoPlano.A));
		criteria.addOrder(Order.asc("CSP."+FatConvenioSaudePlano.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	public FatConvenioSaude obterConvenioSaudeComPagador(Short codigoConvenio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class, "FCS");
		
		criteria.createAlias(FatConvenioSaude.Fields.PAGADOR.toString(), "PGD");
		criteria.add(Restrictions.eq("FCS." + FatConvenioSaude.Fields.CODIGO.toString(), codigoConvenio));

		
		return (FatConvenioSaude)executeCriteria(criteria).get(0);		
	}

    public FatConvenioSaude obterConvenioSaudeComPagadorEUF(Short codigoConvenio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class, "FCS");

		criteria.createAlias(FatConvenioSaude.Fields.PAGADOR.toString(), "PGD");
		criteria.createAlias(FatConvenioSaude.Fields.UF.toString(), "uf", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("FCS." + FatConvenioSaude.Fields.CODIGO.toString(), codigoConvenio));


		return (FatConvenioSaude)executeCriteria(criteria).get(0);
	}
	
	public Boolean existeFaturamentoComPagador(AacPagador aacPagador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvenioSaude.class);
		criteria.add(Restrictions.eq(FatConvenioSaude.Fields.PAGADOR.toString(), aacPagador));
		return executeCriteriaCount(criteria) > 0;
	}

}
