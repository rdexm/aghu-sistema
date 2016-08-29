package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;

public class CntaConvDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<CntaConv> {

	private static final long serialVersionUID = 2083606364941146461L;

	/**
	 * ORADB Forms AINP_VERIFICA_ENCERRA (CURSOR CONTAS - PARTE 1) Obtém a
	 * CntaConv
	 * 
	 * @param intSeq
	 * @return
	 */
	public CntaConv obterCntaConv(Integer intSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CntaConv.class);
		DetachedCriteria criteriaAtendimento = criteria.createCriteria(CntaConv.Fields.ATENDIMENTO.toString());
		criteriaAtendimento.add(Restrictions.eq(AghAtendimentos.Fields.INT_SEQ.toString(), intSeq));
		Criterion criterionTipoAtdNull = Restrictions.isNull(CntaConv.Fields.TIPO_ATD.toString());

		DominioTipoPlano tipoAtd = DominioTipoPlano.I;
		Criterion criterionTipoAtdI = Restrictions.eq(CntaConv.Fields.TIPO_ATD.toString(), tipoAtd);

		criteria.add(Restrictions.or(criterionTipoAtdNull, criterionTipoAtdI));

		List<CntaConv> listaCntaConv = executeCriteria(criteria, 0, 1, null, true);
		if (listaCntaConv != null && !listaCntaConv.isEmpty()) {
			return listaCntaConv.get(0);
		}
		return null;
	}
	
	public CntaConv obterCntaConv(Integer intdCodPrnt, Date intdDataInt, Short convCod) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CntaConv.class);
		criteria.add(Restrictions.eq(CntaConv.Fields.INTD_COD_PRNT.toString(), intdCodPrnt));
		criteria.add(Restrictions.eq(CntaConv.Fields.INTD_DATA_INT.toString(), intdDataInt));
		criteria.add(Restrictions.eq(CntaConv.Fields.CONV_COD.toString(), convCod));

		return (CntaConv) executeCriteriaUniqueResult(criteria);
	}

	public List<CntaConv> listarCntaConvPorIntdCodPrnt(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CntaConv.class);

		criteria.add(Restrictions.eq(CntaConv.Fields.INTD_COD_PRNT.toString(), prontuario));

		return executeCriteria(criteria);
	}
	
	public List<CntaConv> pesquisarContaNotEcrtPorInternacao(Integer seqInternacao) {
		DetachedCriteria cri = DetachedCriteria.forClass(CntaConv.class);
		cri.createAlias(CntaConv.Fields.ATENDIMENTO.toString(), CntaConv.Fields.ATENDIMENTO.toString());
		cri.add(Restrictions.eq(CntaConv.Fields.ATENDIMENTO.toString() + "." + AghAtendimentos.Fields.INT_SEQ.toString(),
				seqInternacao));
		cri.add(Restrictions.eq(CntaConv.Fields.IND_ECRT.toString(), 'N'));
		List<CntaConv> listaCntaConv = executeCriteria(cri);
		return listaCntaConv;
	}

	public boolean temContaConv(Integer nroConta){
		DetachedCriteria criteria = DetachedCriteria.forClass(CntaConv.class);
		criteria.add(Restrictions.eq(CntaConv.Fields.NRO.toString(), nroConta));
		criteria.setProjection(Projections.projectionList().add(Projections.property(CntaConv.Fields.NRO.toString())));
		Integer nroContaExistente = (Integer) this.executeCriteriaUniqueResult(criteria);
		if (nroContaExistente != null){
			return true;
		}
		return false;
		
	}

	public List<CntaConv> listarCntaConvPorSeqAtendimento(Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(CntaConv.class);
		criteria.add(Restrictions.eq(CntaConv.Fields.ATENDIMENTO_SEQ.toString(), seqAtendimento));

		return executeCriteria(criteria);
	}

	/**
	 * Retorna lista de {@link CntaConv} passando como parametro {@link FatSituacaoSaidaPaciente}
	 * 
	 * @param entity {@link FatSituacaoSaidaPaciente}
	 * @return {@link List} de {@link CntaConv}
	 */
	public List<CntaConv> listarCntaConvPorFatSituacaoSaidaPaciente(FatSituacaoSaidaPaciente entity) {

		DetachedCriteria criteria = DetachedCriteria.forClass(CntaConv.class);

		criteria.add(Restrictions.eq(CntaConv.Fields.SITUACAO_SAIDA_PACIENTE.toString(), entity.getId().getSeq()));
		criteria.add(Restrictions.eq(CntaConv.Fields.MOTIVO_SAIDA_PACIENTE.toString(), entity.getId().getMspSeq()));
		
		return executeCriteria(criteria);
	}
}
