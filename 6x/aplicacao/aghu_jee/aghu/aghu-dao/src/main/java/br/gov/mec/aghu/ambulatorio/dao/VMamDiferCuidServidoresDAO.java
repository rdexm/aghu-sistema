package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMamDiferCuidServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class VMamDiferCuidServidoresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMamDiferCuidServidores> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6582496605317392349L;
	
	public List<VMamDiferCuidServidores> pesquisarVMamDiferCuidServidores(RapServidores servidorLogado,String nomeMatriculaVincodigo) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(VMamDiferCuidServidores.class);
		   incluirRestricoesSuggestion(servidorLogado,nomeMatriculaVincodigo, criteria);

			criteria.addOrder(Order.asc(VMamDiferCuidServidores.Fields.PESNOME.toString()));
			return  executeCriteria(criteria, 0, 100, null, false);
	}

	
	public Long countVMamDiferCuidServidores(RapServidores servidorLogado,String nomeMatriculaVincodigo) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(VMamDiferCuidServidores.class);
		
		   incluirRestricoesSuggestion(servidorLogado,nomeMatriculaVincodigo, criteria);
			
			return  executeCriteriaCount(criteria);
	}
	
	private void incluirRestricoesSuggestion(RapServidores servidorLogado,String nomeMatriculaVincodigo,
			DetachedCriteria criteria) {
		Integer matricula = null;
		Short vinCodigo = null;
		
		criteria.add(Restrictions.or(Restrictions.ne(
				VMamDiferCuidServidores.Fields.MATRICULA.toString(),
				servidorLogado.getId().getMatricula()), Restrictions.ne(
				VMamDiferCuidServidores.Fields.VIN_CODIGO.toString(),
				servidorLogado.getId().getVinCodigo())));
		
		if (CoreUtil.isNumeroShort(nomeMatriculaVincodigo)) {
			vinCodigo = Short.parseShort(nomeMatriculaVincodigo);
		}

		if (CoreUtil.isNumeroInteger(nomeMatriculaVincodigo)) {
			matricula = Integer.parseInt(nomeMatriculaVincodigo);
		}
		if (matricula != null && vinCodigo != null) {
			criteria.add(Restrictions.or(Restrictions.eq(
					VMamDiferCuidServidores.Fields.MATRICULA.toString(),
					matricula), Restrictions.eq(
					VMamDiferCuidServidores.Fields.VIN_CODIGO.toString(),
					vinCodigo)));

		} else if (matricula != null) {
			criteria.add(Restrictions.eq(
					VMamDiferCuidServidores.Fields.MATRICULA.toString(),
					matricula));
		} else if (vinCodigo != null) {
			criteria.add(Restrictions.eq(
					VMamDiferCuidServidores.Fields.VIN_CODIGO.toString(),
					vinCodigo));
		} else if(!StringUtils.isBlank(nomeMatriculaVincodigo)){
			criteria.add(Restrictions.ilike(
					VMamDiferCuidServidores.Fields.PESNOME.toString(),
					nomeMatriculaVincodigo,MatchMode.ANYWHERE));
		}
	}
	
	public VMamDiferCuidServidores obterVMamDiferCuidServidores(RapServidores servidores) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(VMamDiferCuidServidores.class);
		   criteria.add(Restrictions.eq(
					VMamDiferCuidServidores.Fields.VIN_CODIGO.toString(),
					servidores.getId().getVinCodigo()));
		   criteria.add(Restrictions.eq(
					VMamDiferCuidServidores.Fields.MATRICULA.toString(),
					servidores.getId().getMatricula()));
		   
		   List<VMamDiferCuidServidores> lista =executeCriteria(criteria);
		   
		   if(lista != null && !lista.isEmpty()){
			   return lista.get(0);
		   }
			
		return  null;
	}
		
}


