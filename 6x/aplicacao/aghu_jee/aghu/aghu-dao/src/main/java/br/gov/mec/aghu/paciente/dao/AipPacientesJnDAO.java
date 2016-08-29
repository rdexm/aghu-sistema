package br.gov.mec.aghu.paciente.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.model.BaseJournal;
import br.gov.mec.aghu.model.AipPacientesHistJn;
import br.gov.mec.aghu.model.AipPacientesJn;

public class AipPacientesJnDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipPacientesJn> {

	private static final long serialVersionUID = -4903617871846533694L;

	public List<Object[]> pesquisaReimpressaoEtiquetas(Date dataInicial, Date dataFinal) {
		DetachedCriteria criteria = createPesquisaCriteria(dataInicial,
				dataFinal);

		return executeCriteria(criteria);
	}

	/**
	 * Prepara o criteria para a re-impressão de etiquetas.
	 * 
	 * @param prontuarios
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	private DetachedCriteria createPesquisaCriteria(Date dataInicial,
			Date dataFinal) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipPacientesJn.class);
		criteria.createAlias(AipPacientesJn.Fields.PACIENTE.toString(),
				AipPacientesJn.Fields.PACIENTE.toString());

		criteria.setProjection(Projections.projectionList().add(
				Projections.distinct(Projections.property(AipPacientesJn.Fields.PRONTUARIO
						.toString()))).add(
						Projections.property(AipPacientesJn.Fields.PAC_NOME
								.toString())).add(
						Projections.property(AipPacientesJn.Fields.PAC_LEITO
								.toString())).add(
						Projections.property(AipPacientesJn.Fields.CODIGO_CENTRO_CUSTO_CADASTRO.toString())));

		if (dataInicial != null) {
			criteria.add(Restrictions.ge(AipPacientesJn.Fields.DATA_ALTERACAO
					.toString(), dataInicial));
		}
		if (dataFinal != null) {
			criteria.add(Restrictions.le(AipPacientesJn.Fields.DATA_ALTERACAO
					.toString(), dataFinal));
		}

		criteria.add(Restrictions.eq(AipPacientesJn.Fields.JN_OPERATION
				.toString(), DominioOperacoesJournal.UPD));
		criteria.add(Restrictions.isNotNull(AipPacientesJn.Fields.NOME
				.toString()));
		criteria.add(Restrictions.like(AipPacientesJn.Fields.NOME.toString(),
				"RN", MatchMode.START));
		criteria.add(Restrictions.neProperty(AipPacientesJn.Fields.PAC_NOME
				.toString(), AipPacientesJn.Fields.NOME.toString()));
		criteria.add(Restrictions.isNotNull(AipPacientesJn.Fields.PRONTUARIO
				.toString()));

		criteria.addOrder(Order.asc(AipPacientesJn.Fields.CODIGO_CENTRO_CUSTO_CADASTRO.toString()));
		criteria.addOrder(Order.asc(AipPacientesJn.Fields.PRONTUARIO.toString()));

		return criteria;
	}
	
	public List<AipPacientesJn> obterSituacoesAnteriores(Integer pacCodigo) {

		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesJn.class);
		cri.add(Restrictions.eq(AipPacientesJn.Fields.CODIGO.toString(), pacCodigo));
		cri.addOrder(Order.desc(AipPacientesJn.Fields.DATA_ALTERACAO.toString()));

		return executeCriteria(cri);
	}
	
	public List<AipPacientesJn> listaPacientesJn(Integer codigoPaciente) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesJn.class);
		cri.add(Restrictions.eq(AipPacientesJn.Fields.CODIGO.toString(),
				codigoPaciente));
		cri.add(Restrictions.isNotNull(AipPacientesJn.Fields.NOME.toString()));
		cri.addOrder(Order.desc(AipPacientesJn.Fields.DATA_ALTERACAO.toString()));
		
		return executeCriteria(cri);
	}
	
	public List<AipPacientesJn> listaPacientesJnComProntuarioAlterado(Integer codigoPaciente) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesJn.class);
		cri.add(Restrictions.eq(AipPacientesJn.Fields.CODIGO.toString(),
				codigoPaciente));
		cri.add(Restrictions.isNotNull(AipPacientesJn.Fields.PRONTUARIO_JN.toString()));
		cri.add(Restrictions.eq(AipPacientesJn.Fields.JN_OPERATION.toString(),DominioOperacoesJournal.UPD));
		cri.addOrder(Order.desc(AipPacientesJn.Fields.DATA_ALTERACAO.toString()));
		
		return executeCriteria(cri);
	}
	
	public Date buscaMaiorDataPacienteHistJn(Integer codigo, DominioOperacoesJournal operacao) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesHistJn.class);
		cri.setProjection(Projections.projectionList().add(Projections.max(BaseJournal.Fields.DATA_ALTERACAO.toString())));

		if (codigo != null) {
			cri.add(Restrictions.eq(AipPacientesHistJn.Fields.CODIGO.toString(), codigo));
		}
		if (operacao != null) {
			cri.add(Restrictions.eq(BaseJournal.Fields.OPERACAO.toString(), operacao));
		}

		return (Date) executeCriteriaUniqueResult(cri);
	}	
	
	public Date buscaMaiorDataPacienteJn(Integer codigo, DominioOperacoesJournal operacao) {
		DetachedCriteria cri = DetachedCriteria.forClass(AipPacientesJn.class);
		cri.setProjection(Projections.projectionList().add(Projections.max(BaseJournal.Fields.DATA_ALTERACAO.toString())));

		if (codigo != null) {
			cri.add(Restrictions.eq(AipPacientesJn.Fields.CODIGO.toString(), codigo));
		}
		if (operacao != null) {
			cri.add(Restrictions.eq(BaseJournal.Fields.OPERACAO.toString(), operacao));
		}

		return (Date) executeCriteriaUniqueResult(cri);
	}
	
	public AipPacientesHistJn buscaUltimoRegistroPorDataAlteracaoHist(Integer codigo, DominioOperacoesJournal operacao, Date dataAlteracao) {
		// Busca ultimo registro inserido através da data de alteração
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientesHistJn.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AipPacientesHistJn.Fields.CODIGO.toString(), codigo));
		}
		if (operacao != null) {
			criteria.add(Restrictions.eq(BaseJournal.Fields.OPERACAO.toString(), operacao));
		}
		criteria.add(Restrictions.eq(BaseJournal.Fields.DATA_ALTERACAO.toString(), dataAlteracao));

		return (AipPacientesHistJn) executeCriteriaUniqueResult(criteria);
	}
	
	public AipPacientesJn buscaUltimoRegistroPorDataAlteracao(Integer codigo, DominioOperacoesJournal operacao, Date dataAlteracao) {
		// Busca ultimo registro inserido através da data de alteração
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientesJn.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AipPacientesJn.Fields.CODIGO.toString(), codigo));
		}
		if (operacao != null) {
			criteria.add(Restrictions.eq(BaseJournal.Fields.OPERACAO.toString(), operacao));
		}
		criteria.add(Restrictions.eq(BaseJournal.Fields.DATA_ALTERACAO.toString(), dataAlteracao));

		return (AipPacientesJn) executeCriteriaUniqueResult(criteria);
	}

}
