package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoSocios;
import br.gov.mec.aghu.model.ScoSociosFornecedores;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ScoSociosFornecedoresDAO extends BaseDao<ScoSociosFornecedores> {
	private static final long serialVersionUID = 15454544555554L;

	public List<ScoSociosFornecedores> listarSociosFornecedores(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer codigo, String nome, String rg, Long cpf, Integer numeroFornecedor){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSociosFornecedores.class);
		
		if(numeroFornecedor != null) {
			criteria.add(Restrictions.eq(ScoSociosFornecedores.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}
		
		if(codigo != null || StringUtils.isNotBlank(nome) || StringUtils.isNotBlank(rg) || cpf != null){
			criteria.createAlias(ScoSociosFornecedores.Fields.SOCIO.toString(), "SOCIO");
		}
		
		if(codigo != null) {
			criteria.add(Restrictions.eq("SOCIO." + ScoSocios.Fields.SEQ.toString(), codigo));
		}
		
		if(StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("SOCIO." + ScoSocios.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(rg)) {
			criteria.add(Restrictions.eq("SOCIO." + ScoSocios.Fields.RG.toString(), rg));
		}
		
		if(cpf != null) {
			criteria.add(Restrictions.eq("SOCIO." + ScoSocios.Fields.CPF.toString(), cpf));
		}
	
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	
	}
	
	public Long listarSociosFornecedoresCount(Integer codigo, String nome, String rg, Long cpf, Integer numeroFornecedor){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSociosFornecedores.class);
		
		if(numeroFornecedor != null) {
			criteria.add(Restrictions.eq(ScoSociosFornecedores.Fields.NUMERO_FORNECEDOR.toString(), numeroFornecedor));
		}
		
		if(codigo != null || StringUtils.isNotBlank(nome) || StringUtils.isNotBlank(rg) || cpf != null){
			criteria.createAlias(ScoSociosFornecedores.Fields.SOCIO.toString(), "SOCIO");
		}
		
		if(codigo != null) {
			criteria.add(Restrictions.eq("SOCIO." + ScoSocios.Fields.SEQ.toString(), codigo));
		}
		
		if(StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike("SOCIO." + ScoSocios.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		if(StringUtils.isNotBlank(rg)) {
			criteria.add(Restrictions.eq("SOCIO." + ScoSocios.Fields.RG.toString(), rg));
		}
		
		if(cpf != null) {
			criteria.add(Restrictions.eq("SOCIO." + ScoSocios.Fields.CPF.toString(), cpf));
		}
	
		return executeCriteriaCount(criteria);
	
	}
	
	public Long quantidadeFornecedorPorSeqSocio(Integer seqSocio){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSociosFornecedores.class);
		
		criteria.add(Restrictions.eq(ScoSociosFornecedores.Fields.SOCIO_SEQ.toString(), seqSocio));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<ScoSociosFornecedores> listarFornecedoresPorSeqSocio(Integer seqSocio){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSociosFornecedores.class);
		
		criteria.createAlias(ScoSociosFornecedores.Fields.FORNECEDOR.toString(), "FORNECEDOR");
		criteria.createAlias("FORNECEDOR."+ScoFornecedor.Fields.CIDADE.toString(), "CIDADE", JoinType.LEFT_OUTER_JOIN);
		
//		criteria.add(Restrictions.eq("FORNECEDOR." + ScoFornecedor.Fields.NUMERO.toString(), ScoSociosFornecedores.Fields.NUMERO_FORNECEDOR.toString()));
//		criteria.createAlias("FORNECEDOR." + ScoFornecedor.Fields.CIDADE.toString(), "CIDADE");
//		criteria.add(Restrictions.eq("CIDADE." + AipCidades.Fields.COD_CIDADE , "FORNECEDOR." + ScoFornecedor.Fields.CIDADE_CODIGO.toString()));
//		criteria.createAlias("CIDADE." + AipCidades.Fields.UF, "UF");
//		criteria.add(Restrictions.eq("UF." + AipUfs.Fields.SIGLA.toString(), "CIDADE." + AipCidades.Fields.UF_SIGLA.toString()));
		
		criteria.add(Restrictions.eq(ScoSociosFornecedores.Fields.SOCIO_SEQ.toString(), seqSocio));
		
		criteria.addOrder(Order.asc("FORNECEDOR." + ScoFornecedor.Fields.RAZAO_SOCIAL.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<ScoSociosFornecedores> listarSociosFornecedoresPorNumeroFornecedor(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSociosFornecedores.class);
	
		criteria.add(Restrictions.eq(ScoSociosFornecedores.Fields.NUMERO_FORNECEDOR.toString(), numero));
		
		return executeCriteria(criteria);
	}
	
}
