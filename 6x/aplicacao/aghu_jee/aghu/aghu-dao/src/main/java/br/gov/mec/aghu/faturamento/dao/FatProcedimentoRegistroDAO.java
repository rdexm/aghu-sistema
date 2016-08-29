package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.CacheMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatRegistro;

public class FatProcedimentoRegistroDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatProcedimentoRegistro> {

	private static final long serialVersionUID = 6499622939639449329L;
	
	public enum FatProcedimentoRegistroDAOExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_PROCEDIMENTO_REGISTRO;
	}
	/**
	 * Busca primeiro prodecimento por uma lista de códigos de registro e um iph
	 * 
	 * @param codigosRegistro
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return
	 */
	public FatProcedimentoRegistro buscarPrimeiroPorCodigosRegistroEPorIph(
			String[] codigosRegistro, Short iphPhoSeq, Integer iphSeq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatProcedimentoRegistro.class);

		criteria.add(Restrictions.in(
				FatProcedimentoRegistro.Fields.COD_REGISTRO.toString(),
				codigosRegistro));
		criteria.add(Restrictions.eq(
				FatProcedimentoRegistro.Fields.IPH_PHO_SEQ
						.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq(
				FatProcedimentoRegistro.Fields.IPH_SEQ
						.toString(), iphSeq));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		
		List<FatProcedimentoRegistro> list = executeCriteria(criteria, 0, 1, null, true, CacheMode.NORMAL);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * Buscar todos os registro da tabela FAT_PROCEDIMENTOS_REGISTRO
	 * 
	 * @return
	 */
	public List<FatProcedimentoRegistro> buscarFatProcedimentoRegistro() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatProcedimentoRegistro.class);
		return executeCriteria(criteria);
	}
	public void limparFatProcedimentoRegistro(){

		StringBuilder sql = new StringBuilder(90);
		sql.append("DELETE FROM " ).append("AGH.FAT_PROCEDIMENTOS_REGISTRO");
		Query query = createNativeQuery(sql.toString());
		
		query.executeUpdate();
	}
	
	private DetachedCriteria createCriteriaFatRegistroPorItensProcedimentoHospitalar(){
		DetachedCriteria criteria = DetachedCriteria.forClass(FatRegistro.class);
		criteria.createAlias(FatRegistro.Fields.FAT_PROCEDIMENTO_REGISTROS.toString(),FatRegistro.Fields.FAT_PROCEDIMENTO_REGISTROS.toString());

		return criteria;
	}
	private DetachedCriteria addRestrictionsCriteriaFatRegistroPorItensProcedimentoHospitalar(DetachedCriteria criteria,final Integer iphSeq, final Short iphPhoSeq){
		criteria.add(Restrictions.eq(FatRegistro.Fields.FAT_PROCEDIMENTO_REGISTROS.toString()+"."+FatProcedimentoRegistro.Fields.IPH_SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq(FatRegistro.Fields.FAT_PROCEDIMENTO_REGISTROS.toString()+"."+FatProcedimentoRegistro.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		return criteria;
	}
	public List<FatRegistro> listarFatRegistroPorItensProcedimentoHospitalar(final Integer iphSeq, final Short iphPhoSeq){
		DetachedCriteria criteria = createCriteriaFatRegistroPorItensProcedimentoHospitalar();
		criteria = addRestrictionsCriteriaFatRegistroPorItensProcedimentoHospitalar(criteria, iphSeq, iphPhoSeq);
		return executeCriteria(criteria);
	}
}
