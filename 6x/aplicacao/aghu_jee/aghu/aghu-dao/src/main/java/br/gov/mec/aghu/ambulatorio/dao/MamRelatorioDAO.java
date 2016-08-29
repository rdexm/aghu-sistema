package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.MamRelatorioVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamRelatorio;

public class MamRelatorioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRelatorio> {

	private static final long serialVersionUID = -1409029150822016792L;
	

	/**
	 * #43029 C1
	 * CONSULTA PARA OBTER SEQ, IND_PENDENTE DE MAM_RELATORIOS
	 * 
	 * @param conNumero
	 * @return
	 */
	public List<MamRelatorioVO> obterListaMamRelatorioVOPorConNumero(Integer conNumero){
		
		final String ALIAS_REL = "REL";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRelatorio.class, ALIAS_REL);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.SEQ.toString()),MamRelatorioVO.Fields.SEQ.toString())
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.IND_PENDENTE.toString()), MamRelatorioVO.Fields.IND_PENDENTE.toString()));
		
		criteria.add(Restrictions.eq(ALIAS_REL+"."+MamRelatorio.Fields.CON_NUMERO.toString(), conNumero));		
		criteria.add(Restrictions.isNull(ALIAS_REL+"."+MamRelatorio.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(ALIAS_REL+"."+MamRelatorio.Fields.IND_PENDENTE.toString(), new String[]{"V","P"}));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MamRelatorioVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * #43029 C2
	 * CONSULTA PARA OBTER REL.DESCRICAO, REL.SER_MATRICULA_VALIDA, REL.SER_VIN_CODIGO_VALIDA, PAC.NOME
	 * DE MAM_RELATORIOS, AIP_PACIENTES
	 * 
	 * @param conNumero
	 * @return
	 */
	public MamRelatorioVO obterMamRelatorioVOPorSeq(Long seq){
		
		final String ALIAS_REL = "REL";
		final String ALIAS_PAC = "PAC";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRelatorio.class, ALIAS_REL);
		
		criteria.createAlias(ALIAS_REL+"."+MamRelatorio.Fields.AIP_PACIENTES.toString(), ALIAS_PAC);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.DESCRICAO.toString()),MamRelatorioVO.Fields.DESCRICAO.toString())
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.SER_MATRICULA_VALIDA.toString()), MamRelatorioVO.Fields.SER_MATRICULA_VALIDA.toString())
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.SER_VIN_CODIGO_VALIDA.toString()), MamRelatorioVO.Fields.SER_VIN_CODIGO_VALIDA.toString())
				.add(Projections.property(ALIAS_PAC+"."+AipPacientes.Fields.NOME.toString()), MamRelatorioVO.Fields.NOME_PAC.toString())
				);
		
		criteria.add(Restrictions.eq(ALIAS_REL+"."+MamRelatorio.Fields.SEQ.toString(), seq));		
		criteria.setResultTransformer(Transformers.aliasToBean(MamRelatorioVO.class));
		
		return (MamRelatorioVO) executeCriteriaUniqueResult(criteria);
	} 
	
	/**
	 * #43029 P1 CUR_REL
	 * CONSULTA PARA OBTER SEQ, IND_PENDENTE DE MAM_RELATORIOS
	 * 
	 * @param conNumero
	 * @return
	 */
	public List<MamRelatorioVO> obterCurRelPorConNumero(Integer conNumero){
		
		final String ALIAS_REL = "REL";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRelatorio.class, ALIAS_REL);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.SEQ.toString()),MamRelatorioVO.Fields.SEQ.toString())
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.IND_PENDENTE.toString()), MamRelatorioVO.Fields.IND_PENDENTE.toString())
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.IND_IMPRESSO.toString()), MamRelatorioVO.Fields.IND_IMPRESSO.toString())
				.add(Projections.property(ALIAS_REL+"."+MamRelatorio.Fields.NRO_VIAS.toString()), MamRelatorioVO.Fields.NRO_VIAS.toString())
				);
		
		criteria.add(Restrictions.eq(ALIAS_REL+"."+MamRelatorio.Fields.CON_NUMERO.toString(), conNumero));		
		criteria.add(Restrictions.isNull(ALIAS_REL+"."+MamRelatorio.Fields.DTHR_VALIDA_MVTO.toString()));
		criteria.add(Restrictions.in(ALIAS_REL+"."+MamRelatorio.Fields.IND_PENDENTE.toString(), new String[]{"V","P"}));
		criteria.setResultTransformer(Transformers.aliasToBean(MamRelatorioVO.class));
		
		return executeCriteria(criteria);
	} 
	

	@Override
	public MamRelatorio atualizar(MamRelatorio elemento) {
		MamRelatorio mamRelatorio = super.atualizar(elemento);
		mamRelatorio.setIndImpresso("S");
		return mamRelatorio;
	}

	@Override
	public MamRelatorio obterPorChavePrimaria(Object pk) {
		MamRelatorio mamRelatorio = super.obterPorChavePrimaria(pk);
		return mamRelatorio;
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamRelatorio buscarRelatorioPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRelatorio.class);

		criteria.add(Restrictions.eq(MamRelatorio.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamRelatorio> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}


}
