package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.faturamento.vo.FatDadosContaSemIntVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.RapServidores;

public class FatDadosContaSemIntDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatDadosContaSemInt> {

	private static final long serialVersionUID = -5975881159307557392L;

	public FatDadosContaSemInt buscarDadosContaSemInt(Integer cthSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDadosContaSemInt.class);

		criteria.createAlias(FatDadosContaSemInt.Fields.CONTAS_INTERNACAO.toString(), FatDadosContaSemInt.Fields.CONTAS_INTERNACAO
				.toString());

		criteria.add(Restrictions.eq(FatDadosContaSemInt.Fields.CONTAS_INTERNACAO.toString() + "."
				+ FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));

		List<FatDadosContaSemInt> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * pesquisarFatDadosContaSemInt
	 * @param pacProntuario
	 * @param pacCodigo
	 * @param cthSeq
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return
	 */
	public List<FatDadosContaSemIntVO> pesquisarFatDadosContaSemInt(final Integer pacProntuario, final Integer pacCodigo, final Integer cthSeq, 
			final  Integer firstResult,final  Integer maxResult,final  String orderProperty, final  boolean asc) {
		
		final DetachedCriteria criteria = this.createCriteria(pacProntuario, pacCodigo, cthSeq);
		
		
		final List<FatDadosContaSemInt> fatDadosContaSemIntList = executeCriteria(criteria);
		
		final List<FatDadosContaSemIntVO> retornoList = new ArrayList<FatDadosContaSemIntVO>();
		int count=0;

		// itera sobre lista de contas sem internacao
		// uma conta sem internacao pode ter mais de um conta internacao referente
		for (final FatDadosContaSemInt fatDadosContaSemInt : fatDadosContaSemIntList) {
			
			final List<FatContasInternacao> contasInternacaoList = fatDadosContaSemInt.getContasInternacao();
			 
			for (FatContasInternacao fatContasInternacao : contasInternacaoList) {
				
				// se pesquisa é por conta então somente busca a conta de internacao referente
				if (cthSeq != null && !cthSeq.equals(fatContasInternacao.getContaHospitalar().getSeq())) {
					continue;
				}
				
				final FatDadosContaSemIntVO retornoVO = new FatDadosContaSemIntVO();
				count++;
				if(count > maxResult) {
					return retornoList;
					
				} else if(count>=firstResult){
					retornoVO.setCthSeq(fatContasInternacao.getContaHospitalar().getSeq());
					retornoVO.setSeq(fatDadosContaSemInt.getSeq());
					retornoVO.setTipoCaraterInternacao(fatDadosContaSemInt.getTipoCaraterInternacao());
					retornoVO.setServidor(fatDadosContaSemInt.getServidor());
					retornoVO.setDataInicial(fatDadosContaSemInt.getDataInicial());
					retornoVO.setDataFinal(fatDadosContaSemInt.getDataFinal());
					retornoVO.setUnf(fatDadosContaSemInt.getUnf());
					retornoVO.setIndSituacao(fatDadosContaSemInt.getIndSituacao());
					
					retornoList.add(retornoVO);
				}
				
			}
		}

		return retornoList;
	}	
	
	/**
	 * pesquisarFatDadosContaSemIntCount
	 * @param pacProntuario
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	public Long pesquisarFatDadosContaSemIntCount(final Integer pacProntuario, final Integer pacCodigo, final Integer cthSeq) {
		
		final DetachedCriteria criteria = this.createCriteria(pacProntuario, pacCodigo, cthSeq);
		
		final List<FatDadosContaSemInt> fatDadosContaSemIntList = executeCriteria(criteria);
		Long count = 0L;
		
		// itera sobre lista de contas sem internacao
		// uma conta sem internacao (FatDadosContaSemInt) pode ter mais de um conta internacao (FatContasInternacao) referente		
		for (FatDadosContaSemInt fatDadosContaSemInt : fatDadosContaSemIntList) {
			
			final List<FatContasInternacao> contasInternacaoList = fatDadosContaSemInt.getContasInternacao();
			// se pesquisa é por conta e encontrou
			if (cthSeq != null && contasInternacaoList.size() > 1) {
				count++;
			} else {
				count += contasInternacaoList.size();				
			}
		}

		return count;
//		return executeCriteriaCount(criteria);
	}	
	
	/**
	 * createCriteria
	 * @param pacProntuario
	 * @param pacCodigo
	 * @param cthSeq
	 * @return
	 */
	private DetachedCriteria createCriteria(Integer pacProntuario, Integer pacCodigo, Integer cthSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatDadosContaSemInt.class);
		criteria.createAlias(FatDadosContaSemInt.Fields.CONTAS_INTERNACAO.toString(), FatDadosContaSemInt.Fields.CONTAS_INTERNACAO.toString(),JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatDadosContaSemInt.Fields.PACIENTE.toString(), FatDadosContaSemInt.Fields.PACIENTE.toString(),JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatDadosContaSemInt.Fields.TIPO_CARATER_INTERNACAO.toString(), FatDadosContaSemInt.Fields.TIPO_CARATER_INTERNACAO.toString(),JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatDadosContaSemInt.Fields.UNF.toString(), FatDadosContaSemInt.Fields.UNF.toString(),JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatDadosContaSemInt.Fields.SERVIDOR.toString(), FatDadosContaSemInt.Fields.SERVIDOR.toString(),JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(FatDadosContaSemInt.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.PESSOA_FISICA.toString(), FatDadosContaSemInt.Fields.SERVIDOR.toString()+"."+RapServidores.Fields.PESSOA_FISICA.toString(),JoinType.LEFT_OUTER_JOIN);
		
		// pesquisa por prontuario
		if (pacProntuario != null) {
			criteria.add(Restrictions.eq(FatDadosContaSemInt.Fields.PACIENTE.toString() + "."+ AipPacientes.Fields.PRONTUARIO.toString(), pacProntuario));				
		}
		
		// pesquisa por codigo do paciente
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(FatDadosContaSemInt.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		
		// pesquisa por conta
		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatDadosContaSemInt.Fields.CONTAS_INTERNACAO.toString() + "."+ FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));		
		}
		
		return criteria;
	}

	public List<FatDadosContaSemInt> listarDadosContaSemIntPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDadosContaSemInt.class);

		criteria.add(Restrictions.eq(FatDadosContaSemInt.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public FatDadosContaSemInt obterFatDadosContaSemIntPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatDadosContaSemInt.class);
		criteria.createAlias(FatDadosContaSemInt.Fields.PACIENTE.toString(), FatDadosContaSemInt.Fields.PACIENTE.toString());
		criteria.createAlias(FatDadosContaSemInt.Fields.TIPO_CARATER_INTERNACAO.toString(), FatDadosContaSemInt.Fields.TIPO_CARATER_INTERNACAO.toString());
		criteria.createAlias(FatDadosContaSemInt.Fields.UNF.toString(), FatDadosContaSemInt.Fields.UNF.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(FatDadosContaSemInt.Fields.SEQ.toString(), seq));

		return (FatDadosContaSemInt) executeCriteriaUniqueResult(criteria);
	}
}
