package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcPorEquipeId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;

public class MbcProcPorEquipeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcProcPorEquipe> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8880155184346793952L;

	@Override
	protected void obterValorSequencialId(MbcProcPorEquipe elemento) {

		if (elemento == null || elemento.getAghUnidadesFuncionais() == null || elemento.getRapServidoresByMbcPxqSerFk1() == null || elemento.getMbcProcedimentoCirurgicos() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!");
		}

		MbcProcPorEquipeId id = new MbcProcPorEquipeId();

		id.setUnfSeq(elemento.getAghUnidadesFuncionais().getSeq());
		id.setSerMatriculaPrf(elemento.getRapServidoresByMbcPxqSerFk1().getId().getMatricula());
		id.setSerVinCodigoPrf(elemento.getRapServidoresByMbcPxqSerFk1().getId().getVinCodigo());
		id.setPciSeq(elemento.getMbcProcedimentoCirurgicos().getSeq());

		elemento.setId(id);

	}

	/**
	 * Monta criteria da pesquisa paginada dos procedimentos utilizados por equipe
	 * @param elemento
	 * @return
	 */
	private DetachedCriteria montarCriteriaPesquisarProcedimentosUsadosEquipe(MbcProcPorEquipe elemento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcPorEquipe.class);

		criteria.createAlias(MbcProcPorEquipe.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), "UND", JoinType.INNER_JOIN);
		criteria.createAlias(MbcProcPorEquipe.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PROC", JoinType.INNER_JOIN);
		criteria.createAlias(MbcProcPorEquipe.Fields.RAP_SERVIDORES_BY_MBC_PXQ_SER_FK1.toString(), "EQP", JoinType.INNER_JOIN);
		criteria.createAlias("EQP." + RapServidores.Fields.PESSOA_FISICA.toString(), "PFS", JoinType.INNER_JOIN);

		if (elemento.getAghUnidadesFuncionais() != null) {
			criteria.add(Restrictions.eq(MbcProcPorEquipe.Fields.AGH_UNIDADES_FUNCIONAIS.toString(), elemento.getAghUnidadesFuncionais()));
		}

		if (elemento.getMbcProcedimentoCirurgicos() != null) {
			criteria.add(Restrictions.eq(MbcProcPorEquipe.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), elemento.getMbcProcedimentoCirurgicos()));
		}

		if (elemento.getRapServidoresByMbcPxqSerFk1() != null) {
			criteria.add(Restrictions.eq(MbcProcPorEquipe.Fields.RAP_SERVIDORES_BY_MBC_PXQ_SER_FK1.toString(), elemento.getRapServidoresByMbcPxqSerFk1()));
		}

		return criteria;
	}

	/**
	 * Pesquisa paginada dos procedimentos utilizados por equipe
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return
	 */
	public List<MbcProcPorEquipe> pesquisarProcedimentosUsadosEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MbcProcPorEquipe elemento) {
		DetachedCriteria criteria = montarCriteriaPesquisarProcedimentosUsadosEquipe(elemento);

		// Ordena pela unidade, servidor/equipe e procedimento
		criteria.addOrder(Order.asc("UND." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()));
		criteria.addOrder(Order.asc("PFS." + RapPessoasFisicas.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("PROC." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Contabiliza resultados da pesquisa paginada dos procedimentos utilizados por equipe
	 * 
	 * @param elemento
	 * @return
	 */
	public Long pesquisarProcedimentosUsadosEquipeCount(MbcProcPorEquipe elemento) {
		DetachedCriteria criteria = montarCriteriaPesquisarProcedimentosUsadosEquipe(elemento);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Verifica a existência de registro duplicado
	 * 
	 * @param procPorEquipe
	 * @return
	 */
	public Boolean existeProcedimentoUsadoEquipe(MbcProcPorEquipe procPorEquipe) {
		DetachedCriteria criteria = montarCriteriaPesquisarProcedimentosUsadosEquipe(procPorEquipe);
		return executeCriteriaCount(criteria) > 0;
	}

	public List<MbcProcPorEquipe> pesquisarProcedimentosEquipe(
			Integer matriculaEquipe, Short vinCodigoEquipe, Short unfSeqEquipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcProcPorEquipe.class,"PPE");
		criteria.createCriteria(MbcProcPorEquipe.Fields.MBC_PROCEDIMENTO_CIRURGICOS.toString(), "PROC", Criteria.INNER_JOIN);
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(matriculaEquipe);
		id.setVinCodigo(vinCodigoEquipe);
		criteria.add(Restrictions.eq(MbcProcPorEquipe.Fields.RAP_SERVIDORES_BY_MBC_PXQ_SER_FK1_ID.toString(), id));
		criteria.add(Restrictions.in("PROC."+MbcProcedimentoCirurgicos.Fields.TIPO.toString(), new DominioTipoProcedimentoCirurgico[]{DominioTipoProcedimentoCirurgico.CIRURGIA,DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO}));
		criteria.add(Restrictions.eq("PPE."+MbcProcPorEquipe.Fields.AGH_UNIDADES_FUNCIONAIS_ID.toString(), unfSeqEquipe));
		
		criteria.addOrder(Order.asc("PROC." + MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

}
