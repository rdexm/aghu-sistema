package br.gov.mec.aghu.paciente.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.paciente.vo.AipOcupacoesVO;
import br.gov.mec.aghu.paciente.vo.ProfissaoVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.search.Lucene;

public class AipOcupacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AipOcupacoes> {
    @Inject
    private Lucene lucene;
	
	
	private static final long serialVersionUID = 8693231756247598039L;
	private static final Comparator<ProfissaoVO> COMPARATOR_PROFISSOES = new Comparator<ProfissaoVO>() {

		@Override
		public int compare(ProfissaoVO o1, ProfissaoVO o2) {
			return o1.getDescricao().toUpperCase().compareTo(
					o2.getDescricao().toUpperCase());
		}

	};
	

	private	static final String SQL_PROJECTION_CONTA_CARACTERISTICAS = montaSQLProjectionContaCaracteristicas();
	
	private static String montaSQLProjectionContaCaracteristicas() {
		return "(select count(*) from "+AipSinonimosOcupacao.class.getAnnotation(Table.class).schema() + '.'+AipSinonimosOcupacao.class.getAnnotation(Table.class).name() + " ASO "+
				" where ASO."+AipSinonimosOcupacao.Fields.OCP_CODIGO.name() + " = {alias}."+AipOcupacoes.Fields.CODIGO.name()+ ") as "+AipOcupacoesVO.Fields.QT_SINONIMOS.toString();
	}
	
	public List<AipOcupacoesVO> pesquisarOcupacoes(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Integer codigo, String descricao){
		
		DetachedCriteria criteria = createPesquisaCriteria(codigo, descricao);
		

		criteria.setProjection(Projections.projectionList()
									.add(Projections.property(AipOcupacoes.Fields.CODIGO.toString()),AipOcupacoesVO.Fields.CODIGO.toString())
									.add(Projections.property(AipOcupacoes.Fields.DESCRICAO.toString()),AipOcupacoesVO.Fields.DESCRICAO.toString())
									.add(Projections.sqlProjection(SQL_PROJECTION_CONTA_CARACTERISTICAS , new String[]{AipOcupacoesVO.Fields.QT_SINONIMOS.toString()}, new Type[]{IntegerType.INSTANCE}))
								);
		
		criteria.addOrder(Order.asc(AipOcupacoes.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AipOcupacoesVO.class));
		
		return executeCriteria(criteria, firstResult, maxResults,orderProperty, asc);
	}
	
	/**
	 * @dbtables AipOcupacoes select
	 * 
	 * @param codigo
	 * @param descricao
	 * @return
	 */
	public Long pesquisaCount(Integer codigo, String descricao) {
		return executeCriteriaCount(createPesquisaCriteria(codigo, descricao));
	}

	private DetachedCriteria createPesquisaCriteria(Integer codigo, String descricao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AipOcupacoes.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AipOcupacoes.Fields.CODIGO.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AipOcupacoes.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		return criteria;
	}
	
	public List<AipOcupacoes> getOcupacoesComMesmaDescricao(
			AipOcupacoes ocupacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AipOcupacoes.class);

		criteria.add(Restrictions.ilike(
				AipOcupacoes.Fields.DESCRICAO.toString(),
				ocupacao.getDescricao(), MatchMode.EXACT));

		// Garante que não será comparado a descrição da ocupação sendo editada
		if (ocupacao.getCodigo() != null) {
			criteria.add(Restrictions.ne(AipOcupacoes.Fields.CODIGO.toString(),
					ocupacao.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}
	
	public List<AipOcupacoes> pesquisarPorCodigoDescricao(Object paramPesquisa){
		DetachedCriteria cri = DetachedCriteria.forClass(AipOcupacoes.class);
		if (StringUtils.isNotBlank((String) paramPesquisa)) {
			if (CoreUtil.isNumeroInteger((String) paramPesquisa)) {
				Integer integerPesquisa = Integer
						.valueOf((String) paramPesquisa);
				cri.add(Restrictions.eq(AipOcupacoes.Fields.CODIGO.toString(),
						(integerPesquisa)));
			} else {
				String strPesquisa = (String) paramPesquisa;
				cri.add(Restrictions.ilike(
						AipOcupacoes.Fields.DESCRICAO.toString(),
						((String) strPesquisa).toUpperCase(),
						MatchMode.ANYWHERE));
			}
		}

		cri.addOrder(Order.asc(AipOcupacoes.Fields.DESCRICAO.toString()));
		return executeCriteria(cri);
		
	}
	
	public List<ProfissaoVO> pesquisarProfissioesPorCodigoDescricao(String strPesquisa) {
		int maximoResultados = 100;
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipOcupacoes.class);

			_criteria.add(Restrictions.eq(
					AipOcupacoes.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			
			List<AipOcupacoes> list = executeCriteria(_criteria, 0, maximoResultados, null, false);

			if (list.size() > 0) {
				AipOcupacoes ocupacao = list.get(0);
				
				ProfissaoVO profissaoVO = new ProfissaoVO(ocupacao.getCodigo(), ocupacao.getDescricao());
				List<ProfissaoVO> profissoes = new ArrayList<ProfissaoVO>();
				profissoes.add(profissaoVO);
				return profissoes;
			}
		}

		List<ProfissaoVO> listaProfissoes = new ArrayList<ProfissaoVO>();
		String queryLucene=strPesquisa;
		if (StringUtils.isBlank(strPesquisa)) {
			queryLucene = "*";
		}
		
		
		List<AipOcupacoes> ocupacoes= lucene.executeLuceneQueryParaSuggestionBox(AipOcupacoes.Fields.DESCRICAO.toString(), AipOcupacoes.Fields.DESCRICAOFONETICA.toString(), queryLucene, AipOcupacoes.class);
		if (ocupacoes != null && !ocupacoes.isEmpty()) {
			for (AipOcupacoes ocupacao : ocupacoes) {
				listaProfissoes.add(new ProfissaoVO(ocupacao.getCodigo(), ocupacao.getDescricao()));
			}
		}
		List<AipSinonimosOcupacao> sinonimos= lucene.executeLuceneQueryParaSuggestionBox(AipSinonimosOcupacao.Fields.DESCRICAO.toString(), AipSinonimosOcupacao.Fields.DESCRICAOFONETICA.toString(), queryLucene, AipSinonimosOcupacao.class);
		if (sinonimos != null && !sinonimos.isEmpty()) {
			for (AipSinonimosOcupacao sinonimo : sinonimos) {
				listaProfissoes.add(new ProfissaoVO(sinonimo.getAipOcupacoes().getCodigo(), sinonimo.getDescricao()));
			}
		}
		
		Collections.sort(listaProfissoes, COMPARATOR_PROFISSOES);
		if (listaProfissoes.size()>maximoResultados) {
			listaProfissoes= listaProfissoes.subList(0, maximoResultados);
		}
		
		return listaProfissoes;
	}
	
	public Long pesquisarProfissioesPorCodigoDescricaoCount(
			String strPesquisa) {
		
		Long count;
		if (StringUtils.isNotBlank(strPesquisa)
				&& CoreUtil.isNumeroInteger(strPesquisa)) {
			DetachedCriteria _criteria = DetachedCriteria
					.forClass(AipOcupacoes.class);

			_criteria.add(Restrictions.eq(
					AipOcupacoes.Fields.CODIGO.toString(),
					Integer.valueOf(strPesquisa)));
			
			count= executeCriteriaCount(_criteria);
			
			return count;
			
		}
		String queryLucene=strPesquisa;
		if (StringUtils.isBlank(strPesquisa)) {
			queryLucene = "*";
		} 
		int countInit = lucene.executeLuceneCount(AipOcupacoes.Fields.DESCRICAO.toString(), AipOcupacoes.Fields.DESCRICAOFONETICA.toString(), queryLucene, AipOcupacoes.class);
		count = Long.valueOf(countInit);
		count = count + lucene.executeLuceneCount(AipSinonimosOcupacao.Fields.DESCRICAO.toString(), AipSinonimosOcupacao.Fields.DESCRICAOFONETICA.toString(), queryLucene, AipSinonimosOcupacao.class);
			
		return count;
	}

	public AipOcupacoes obterOcupacaoComSinonimos(Integer codigo) {
		AipOcupacoes retorno = this.obterPorChavePrimaria(codigo);
		if(retorno != null){
			retorno.getSinonimos().size();
		}
		return retorno;
	}

}
