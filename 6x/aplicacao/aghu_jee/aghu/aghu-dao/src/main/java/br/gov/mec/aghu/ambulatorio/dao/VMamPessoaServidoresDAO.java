package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMamDiferCuidServidores;
import br.gov.mec.aghu.model.VMamPessoaServidores;


public class VMamPessoaServidoresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMamPessoaServidores> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6582496605317392349L;
	
	public List<VMamDiferCuidServidores> pesquisarVMamPessoaServidores(RapServidores servido) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(VMamPessoaServidores.class);
		
			criteria.add(Restrictions.eq(VMamPessoaServidores.Fields.MATRICULA.toString(), servido.getId().getMatricula()));
			criteria.add(Restrictions.eq(VMamPessoaServidores.Fields.VIN_CODIGO.toString(), servido.getId().getVinCodigo()));
			
			return  executeCriteria(criteria);
	}
	
	public Long countVMamPessoaServidores(RapServidores servido) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(VMamPessoaServidores.class);
		
			criteria.add(Restrictions.eq(VMamPessoaServidores.Fields.MATRICULA.toString(), servido.getId().getMatricula()));
			criteria.add(Restrictions.eq(VMamPessoaServidores.Fields.VIN_CODIGO.toString(), servido.getId().getVinCodigo()));
			
			return  executeCriteriaCount(criteria);
	}
	
	
	public VMamPessoaServidores obterVMamPessoaServidores(RapServidores servidores) {
		
		   DetachedCriteria criteria = DetachedCriteria.forClass(VMamPessoaServidores.class);
		   criteria.add(Restrictions.eq(
				   VMamPessoaServidores.Fields.VIN_CODIGO.toString(),
					servidores.getId().getVinCodigo()));
		   criteria.add(Restrictions.eq(
				   VMamPessoaServidores.Fields.MATRICULA.toString(),
					servidores.getId().getMatricula()));
		   
		   criteria.setProjection(Projections.projectionList()
					.add(Projections.property(VMamPessoaServidores.Fields.VINCULO.toString()),(VMamPessoaServidores.Fields.VINCULO.toString()))
					.add(Projections.property(VMamPessoaServidores.Fields.COLUNA_MAT.toString()),(VMamPessoaServidores.Fields.COLUNA_MAT.toString()))
					.add(Projections.property(VMamPessoaServidores.Fields.COLUNA_PESNOME.toString()),(VMamPessoaServidores.Fields.COLUNA_PESNOME.toString())));
		   
		   criteria.setResultTransformer(Transformers.aliasToBean(VMamPessoaServidores.class));
		   List<VMamPessoaServidores> lista =executeCriteria(criteria);
		   
		   if(lista != null && !lista.isEmpty()){
			   return lista.get(0);
		   }
			
		return  null;
	}
	
		
}


