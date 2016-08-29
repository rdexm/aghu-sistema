package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FcpRetencaoAliquota;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class FcpTributoDAO extends BaseDao<FcpRetencaoAliquota> {

	private static final long serialVersionUID = 7857418208613109173L;
	
	/**
	 * Critério de pesquisa
	 * @param asc 
	 * @param orderProperty 
	 * @param codigo
	 * @param tipoTributo
	 * @return criteria
	 */
	private DetachedCriteria createPesquisarCriteria(Integer friCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpRetencaoAliquota.class, "FRA");
		criteria.createAlias("FRA." + FcpRetencaoAliquota.Fields.RAP_SERVIDORES.toString(), "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		
		criteria.add(Restrictions.eq("FRA." + FcpRetencaoAliquota.Fields.FRI_CODIGO.toString(), friCodigo));
		
		return criteria;
	}
	
	/**
	 * Método responsável por pesquisar count tributo
	 * @param codigos
	 * @param tipoTributo
	 * @return long
	 */
	public Long obterCountCodigoTributo(Integer codigo) {
		
		DetachedCriteria criteria = createPesquisarCriteria(codigo);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método responsável por pesquisar lista tributo
	 * @param codigo
	 * @param tipoTributo
	 * @return list
	 */
	public List<FcpRetencaoAliquota> obterListaCodigoTributo(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, Integer codigo) {
		
		DetachedCriteria criteria = createPesquisarCriteria(codigo);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Método responsável por excluir tributo
	 * @param codigoRecolhimento
	 * @param dataInicio
	 * @param numero
	 * @param imposto
	 */
	public void excluirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws ApplicationBusinessException {
		
		this.removerPorId(fcpRetencaoAliquota.getId());
	}
	
	/**
	 * Método responsável por persistir tributo
	 * @param codigoRecolhimento
	 * @param dataInicio
	 * @param numero
	 * @param imposto
	 */
	public void persistirRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException, ApplicationBusinessException {
		
		this.persistir(fcpRetencaoAliquota);
	}
	
	/**
	 * Método responsável por alterar tributo
	 * @param codigoRecolhimento
	 * @param dataInicio
	 * @param numero
	 * @param imposto
	 */
	public void atualizarRetencaoAliquota(FcpRetencaoAliquota fcpRetencaoAliquota) throws BaseException, ApplicationBusinessException {
		
		this.atualizar(fcpRetencaoAliquota);
	}
	
	public boolean obterRetencaoAliquotaPorFriCodigo(int friCodigo) throws ApplicationBusinessException {
		
		StringBuffer hql = new StringBuffer(250);
		hql.append("SELECT 1 FROM FcpRetencaoAliquota fra ");
		hql.append("WHERE fra.id.friCodigo = :codigoRecolhimento ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("codigoRecolhimento", friCodigo);
		
		// query.setResultTransformer(Transformers.aliasToBean(CadastroVO.class));
		if (query.list().size() > 0) {
			return false;
		
		} else {
			
			return true;
		}
		
	}
	
	public List<FcpRetencaoAliquota> pesquisarRetencaoAliquotaPorFriCodigoImpDtInicial(FcpRetencaoAliquota fcpRetencaoAliquota) {
		
		StringBuffer hql = new StringBuffer(250);
		hql.append("select FRA ");
		hql.append("from  FcpRetencaoAliquota FRA ");
		hql.append("where  FRA.id.friCodigo = :codigoRecolhimento ");
		hql.append("AND ");
		hql.append("FRA.id.imposto = :imposto ");
		hql.append("AND ");
		hql.append("FRA.id.dtInicioValidade = :dtInicial ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("codigoRecolhimento", fcpRetencaoAliquota.getId().getFriCodigo());
		query.setParameter("imposto", fcpRetencaoAliquota.getId().getImposto());
		query.setParameter("dtInicial", fcpRetencaoAliquota.getId().getDtInicioValidade());
		
		@SuppressWarnings("unchecked")
		List<FcpRetencaoAliquota> listaRetencaoAliquota = query.list();
		
		return listaRetencaoAliquota;
	}
}
