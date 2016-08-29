package br.gov.mec.aghu.parametrosistema.dao;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;

import br.gov.mec.aghu.dominio.DominioFiltroParametrosPreenchidos;
import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghModuloParametroAghu;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.parametrosistema.vo.ParametroAgendaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
public class AghParametrosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghParametros> {
	
	
	private static final long serialVersionUID = 5733493007874439888L;
	
	/**
	 * Retorna o número de parâmetros que não possuem qualquer valor associado,
	 * seja em vlrData, vlrNumerico e vlrTexto (C4)
	 * @author clayton.bras
	 * @return
	 */
	public Long obterNumeroParametrosSemQualquerValorAssociado() {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AghParametros.class, "PAR");
		
		Criterion regra1 = Restrictions.isNull("PAR." + AghParametros.Fields.VLR_DATA.toString());
		Criterion regra2 = Restrictions.isNull("PAR." +	AghParametros.Fields.VLR_NUMERICO.toString());
		Criterion regra3 = Restrictions.isNull("PAR." +	AghParametros.Fields.VLR_TEXTO.toString());
		Criterion regra4 = Restrictions.eq("PAR." + AghParametros.Fields.VLR_TEXTO.toString(), StringUtils.EMPTY);
			
		LogicalExpression expressao3 = Restrictions.or(regra3, regra4);
        
		criteria.add(regra1);
		criteria.add(regra2);
		criteria.add(expressao3);
		
		return executeCriteriaCount(criteria);
	}

	
	/**
	 * Realiza a pesquisa utilizando o nome do parâmetro,
	 * módulos, valor e filtro(parâmetros com vlrData, vlrNumerico
	 * e vlrTexto preenchidos ou não)
	 * @author clayton.bras
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param nomeParametro
	 * @param modulos
	 * @param valor
	 * @param filtroPreenchido
	 * @return
	 */
	public List<AghParametros> pesquisarParametrosPorNomeModuloValorFiltro(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String nomeParametro, List<AghModuloAghu> modulos, Object valor, 
			DominioFiltroParametrosPreenchidos filtroPreenchido){
		
		DetachedCriteria criteria = montarPesquisaParametrosPorNomeModuloValorFiltro(nomeParametro, modulos, valor, filtroPreenchido);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Retorna a quantidade de parâmetros de sistema encontrados na pesquisa
	 * @author clayton.bras
	 * @param nomeParametro
	 * @param modulos
	 * @param valor
	 * @param filtroPreenchido
	 * @return
	 */
	public Long pesquisarParametrosPorNomeModuloValorFiltroCount(String nomeParametro, List<AghModuloAghu> modulos, Object valor, 
			DominioFiltroParametrosPreenchidos filtroPreenchido) {
		
		DetachedCriteria criteria = montarPesquisaParametrosPorNomeModuloValorFiltro(nomeParametro, modulos, valor, filtroPreenchido);
		return executeCriteriaCount(criteria);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private DetachedCriteria montarPesquisaParametrosPorNomeModuloValorFiltro(String nomeParametro, List<AghModuloAghu> modulos, Object valor, 
			DominioFiltroParametrosPreenchidos filtroPreenchido){
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AghParametros.class, "PAR");
		
		if(nomeParametro!=null && !"".equals(nomeParametro)){
				
				Criterion regra1 = Restrictions.ilike("PAR." +
						AghParametros.Fields.NOME.toString(),
						nomeParametro, MatchMode.EXACT);
				
				Criterion regra2 = Restrictions.ilike("PAR." +
						AghParametros.Fields.DESCRICAO.toString(),
						nomeParametro, MatchMode.ANYWHERE);
				
				Criterion regra3 = Restrictions.ilike("PAR." +
						AghParametros.Fields.EXEMPLO_USO.toString(),
						nomeParametro, MatchMode.ANYWHERE);
				LogicalExpression expressao = Restrictions.or(regra2, regra3);
				LogicalExpression expressaoCompleta = Restrictions.or(regra1, expressao);
				criteria.add(expressaoCompleta);
		}
		
		if(valor!=null && !"".equals(valor)){
			final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy");
			final String vlPesquisa = (String) valor;
			final String[] valoresData = vlPesquisa.split("/");
			boolean dataFalhou = false;	
			Date dataValor = null;			
			sf.setLenient(false);
			try {
				if (valoresData.length == 3 && CoreUtil.isNumeroInteger(valoresData[0]) && valoresData[0].length() == 2 
						&& CoreUtil.isNumeroInteger(valoresData[1]) && valoresData[1].length() == 2
						&& CoreUtil.isNumeroInteger(valoresData[2]) && valoresData[2].length() == 4){
					dataValor = sf.parse(vlPesquisa);
					//coloca como restrição a data presente em vlr_data ou vlr_texto
					Criterion regra1 = Restrictions.eq("PAR." + 
							AghParametros.Fields.VLR_DATA.toString(), DateUtil.truncaData(dataValor));
					Criterion regra2 = Restrictions.ilike("PAR." +
							AghParametros.Fields.VLR_TEXTO.toString(),
							vlPesquisa, MatchMode.ANYWHERE);
					LogicalExpression expressao = Restrictions.or(regra1, regra2);
					criteria.add(expressao);
				}
				else{
					dataFalhou = true;
				}

			} catch (ParseException e) {
				dataFalhou = true;				
			}
			if(dataFalhou){
				//Se não houve parse da data, efetua pesquisa como numério e texto
				if(StringUtils.isNumeric(vlPesquisa)){
					BigDecimal valorNumerico = null;
					valorNumerico = new BigDecimal(vlPesquisa);
					Criterion regra1 = Restrictions.eq("PAR." + 
							AghParametros.Fields.VLR_NUMERICO.toString(), valorNumerico);
					Criterion regra2 = Restrictions.ilike("PAR." +
							AghParametros.Fields.VLR_TEXTO.toString(),
							vlPesquisa, MatchMode.ANYWHERE);
					LogicalExpression expressao = Restrictions.or(regra1, regra2);
					criteria.add(expressao);
				}else{
					criteria.add(Restrictions.ilike("PAR." +
							AghParametros.Fields.VLR_TEXTO.toString(),
							vlPesquisa, MatchMode.ANYWHERE));
				}
			}
		}
			
		if(DominioFiltroParametrosPreenchidos.PREENCHIDOS.equals(filtroPreenchido)){
			
			Criterion regra1 = Restrictions.isNotNull("PAR." + AghParametros.Fields.VLR_DATA.toString());
			Criterion regra2 = Restrictions.isNotNull("PAR." + AghParametros.Fields.VLR_NUMERICO.toString());
			Criterion regra3 = Restrictions.isNotNull("PAR." + AghParametros.Fields.VLR_TEXTO.toString());
			Criterion regra4 = Restrictions.ne("PAR." + AghParametros.Fields.VLR_TEXTO.toString(), "");

			LogicalExpression expressao1 = Restrictions.and(regra1, regra2);
			LogicalExpression expressao2 = Restrictions.and(expressao1, regra3);
			LogicalExpression expressao3 = Restrictions.or(expressao2, regra4);
			criteria.add(expressao3);
			
		}else if(DominioFiltroParametrosPreenchidos.NAO_PREENCHIDOS.equals(filtroPreenchido)){	
				
			Criterion regra1 = Restrictions.isNull("PAR." + AghParametros.Fields.VLR_DATA.toString());
			Criterion regra2 = Restrictions.isNull("PAR." +	AghParametros.Fields.VLR_NUMERICO.toString());
			Criterion regra3 = Restrictions.isNull("PAR." +	AghParametros.Fields.VLR_TEXTO.toString());
			Criterion regra4 = Restrictions.eq("PAR." + AghParametros.Fields.VLR_TEXTO.toString(), "");
				
			LogicalExpression expressao1 = Restrictions.and(regra1, regra2);
			LogicalExpression expressao2 = Restrictions.and(expressao1, regra3);
			LogicalExpression expressao3 = Restrictions.or(expressao2, regra4);
			criteria.add(expressao3);
		}
		
		if(modulos!=null && !modulos.isEmpty()){
			
			//armazerna códigos dos módulos passados como filtro na pesquisa
			List<Integer> seqModulos = new ArrayList<Integer>();
			
			for(AghModuloAghu modulo : modulos){
				seqModulos.add(modulo.getSeq());
			}
			
			//modulos e parâmetros associados
			List<AghModuloParametroAghu> modulosParametros = executeCriteria(subCriteriaRetornaModulosParametros(seqModulos)); 
			
			//armazena códigos de parâmetros associados aos módulos
			//passados como filtro na pesquisa
			List<Integer> seqParametros = new ArrayList<Integer>();
			
			for(AghModuloParametroAghu moduloParametro : modulosParametros){
				//controle para não multriplicar um mesmo código de parâmetro já
				//presente na lista, pois um mesmo código pode esyat associado
				//a vários módulos
				if(!seqParametros.contains(moduloParametro.getId().getSeqParametro())){
					seqParametros.add(moduloParametro.getId().getSeqParametro());
				}
			}
			//verifica se o parâmetro está presente na lista de parâmetros associados aos módulos
			//que foram passados como filtro
			criteria.add(Restrictions.in("PAR." + AghParametros.Fields.CODIGO.toString(), seqParametros));
		}
		return criteria;
	}
	
	/**
	 * Subquery que auxilia na busca pelos parâmetros que são associados aos módulos
	 * passados como filtro na pesquisa
	 * @param seqModulos
	 * @return
	 */
	private DetachedCriteria subCriteriaRetornaModulosParametros(List<Integer> seqModulos){
		
		DetachedCriteria criteriaModulosParametros = DetachedCriteria.forClass(
				AghModuloParametroAghu.class, "MP");
		//verifica todos os modulosParâmetros que possui no id algum código presente na lista de códigos
		//dos módulos passados como filtro na pesquisa
		criteriaModulosParametros.add(Restrictions.in("MP."+AghModuloParametroAghu.Fields.CODIGO_MODULO.toString(), seqModulos));
		
		return criteriaModulosParametros;
	}

	public BigDecimal obterValorNumericoAghParametros(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);

		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), nome));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghParametros.Fields.VLR_NUMERICO.toString())));

		BigDecimal vlrNumerico = (BigDecimal) this.executeCriteriaUniqueResult(criteria, true);

		return vlrNumerico;
	}

	/**
	 * Obtem um aghParametro por nome.
	 * 
	 * @param nome
	 * @return
	 */
	public AghParametros obterAghParametroPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), nome));
		
		return (AghParametros) this.executeCriteriaUniqueResult(criteria, true);
	}

	/**
	 * @deprecated usar obterAghParametroPorNome(String nome)
	 * @param nome
	 * @return
	 */
	public List<AghParametros> pesquisarAghParametroPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), nome));
		return executeCriteria(criteria, true);
	}
	
	@SuppressWarnings("unchecked")
	public List<AghParametros> pesquisaAghParametroListPorAghSistema(String sigla) {

		StringBuilder hql = new StringBuilder(100);
		hql.append(" select p ")
		.append(" from ")
		.append(" AghParametros p ")
		.append(" where ")
		.append(" p.sisSigla = :pSistemaSigla ");

		Query query = this.createQuery(hql.toString());
		query.setParameter("pSistemaSigla", sigla);

		return query.getResultList();
	}
	
	private DetachedCriteria createPesquisarParametroPorNomeCriteria(String nomeParametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);

		if (StringUtils.isNotBlank(nomeParametro)) {
			criteria.add(Restrictions.ilike(AghParametros.Fields.NOME.toString(), nomeParametro,MatchMode.ANYWHERE));
		}

		return criteria;
	}
		
	public List<AghParametros> pesquisarParametroPorNome(String nomeParametro, Integer maxResults) {
		DetachedCriteria criteria = this.createPesquisarParametroPorNomeCriteria(nomeParametro);
		if(maxResults != null){
			return executeCriteria(criteria, 0, maxResults, AghParametros.Fields.NOME.toString(), true);	
		} else {
			criteria.addOrder(Order.asc(AghParametros.Fields.NOME.toString()));
			return executeCriteria(criteria);
		}
	}
	
	/**
	 * Obtem os parâmetros que não possuem nenhum valor associado a nenhum dos campos de valor (Data, numérico nem texto).
	 * @return
	 * @author bruno.mourao
	 * @author clayton.bras
	 * @since 04/11/2011
	 */
	public List<AghParametros> obterParametrosSemQualquerValorAssociado() {
        DetachedCriteria criteria = DetachedCriteria.forClass(
                AghParametros.class, "PAR");
        
        Criterion regra1 = Restrictions.isNull("PAR." + AghParametros.Fields.VLR_DATA.toString());
		Criterion regra2 = Restrictions.isNull("PAR." +	AghParametros.Fields.VLR_NUMERICO.toString());
		Criterion regra3 = Restrictions.isNull("PAR." +	AghParametros.Fields.VLR_TEXTO.toString());
		Criterion regra4 = Restrictions.eq("PAR." + AghParametros.Fields.VLR_TEXTO.toString(), StringUtils.EMPTY);
			
		LogicalExpression expressao3 = Restrictions.or(regra3, regra4);
        
		criteria.add(regra1);
		criteria.add(regra2);
		criteria.add(expressao3);
        return executeCriteria(criteria);
	}
	
	/**
	 * Obtem os parâmetros que possuem pelo menos um campo de valor (data, nuḿerico ou texto) preenchido.
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public List<AghParametros> obterParametrosComValorAssociado() {
        DetachedCriteria criteria = DetachedCriteria.forClass(
                AghParametros.class, "PAR");
        
        Criterion vlrDatNotNull = Restrictions.isNotNull("PAR." + AghParametros.Fields.VLR_DATA.toString());
        Criterion vlrNumNotNull = Restrictions.isNotNull("PAR." + AghParametros.Fields.VLR_NUMERICO.toString());
        Criterion vlrTxtNotNull = Restrictions.isNotNull("PAR." + AghParametros.Fields.VLR_TEXTO.toString());
        
        LogicalExpression datNumNotNull = Restrictions.or(vlrDatNotNull, vlrNumNotNull);
        criteria.add(Restrictions.or(datNumNotNull, vlrTxtNotNull));
        
        return executeCriteria(criteria);
	}

	/**
	 * Obtém uma lista com todos os parâmetros do sistema
	 * @return List<br.gov.mec.aghu.model.AghParametros>
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public List<AghParametros> obterTodosParametros() {
		DetachedCriteria criteria = DetachedCriteria.forClass(
                AghParametros.class, "PAR");
		
		return executeCriteria(criteria);
	}
	
	
	public Long pesquisaParametroListCount(AghParametros parametro) {
		StringBuilder hql = new StringBuilder(100);

		hql.append(" select ")
		.append(" count (*) ")
		.append(" from ")
		.append(" AghParametros p ");

		Query query = this.getQueryParametro(hql, parametro, false);

		return Long.valueOf(query.getSingleResult().toString());
	}

	@SuppressWarnings("unchecked")
	public List<AghParametros> pesquisaParametroList(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AghParametros parametro) {

		StringBuilder hql = new StringBuilder(100);
		hql.append(" select p ")
		.append(" from ")
		.append(" AghParametros p ");

		Query query = this.getQueryParametro(hql, parametro, true);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		return query.getResultList();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private Query getQueryParametro(StringBuilder hql, AghParametros parametro, boolean isOrder) {
		boolean buff = false;

		if ((parametro.getSeq() != null && parametro.getSeq() > 0) || StringUtils.isNotBlank(parametro.getNome())
				|| (parametro.getSistema() != null && StringUtils.isNotBlank(parametro.getSistema().getSigla()))
				|| parametro.getVlrData() != null || parametro.getVlrNumerico() != null
				|| StringUtils.isNotBlank(parametro.getVlrTexto())) {
			hql.append(" where ");

			if (parametro.getSeq() != null && parametro.getSeq() > 0) {
				hql.append(" p.seq = :pSeq ");
				buff = true;
			}
			if (!StringUtils.isBlank(parametro.getNome())) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" p.nome like :pNome ");
				buff = true;
			}
			if ((parametro.getSistema() != null && StringUtils.isNotBlank(parametro.getSistema().getSigla()))) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" p.sistema.sigla = :pSiglaSistema ");
				buff = true;
			}
			if (parametro.getVlrData() != null) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" p.vlrData between :pVlrData1 and :pVlrData2 ");
				buff = true;
			}
			if (parametro.getVlrNumerico() != null) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" p.vlrNumerico = :pVlrNumerico ");
				buff = true;
			}
			if (StringUtils.isNotBlank(parametro.getVlrTexto())) {
				if (buff) {
					hql.append(" and ");
				}
				hql.append(" upper(p.vlrTexto) like :pVlrTexto ");
				buff = true;
			}
		}

		if (isOrder) {
			hql.append(" order by p.nome ");
		}

		Query query = this.createQuery(hql.toString());

		if (parametro.getSeq() != null && parametro.getSeq() > 0) {
			query.setParameter("pSeq", parametro.getSeq());
		}
		if (!StringUtils.isBlank(parametro.getNome())) {
			query.setParameter("pNome", "%".concat(parametro.getNome().toUpperCase()).concat("%"));
		}
		if ((parametro.getSistema() != null && StringUtils.isNotBlank(parametro.getSistema().getSigla()))) {
			query.setParameter("pSiglaSistema", parametro.getSistema().getSigla());
		}

		if (parametro.getVlrData() != null) {
			query.setParameter("pVlrData1", DateUtil.obterDataComHoraInical(parametro.getVlrData()), TemporalType.TIMESTAMP);
			query.setParameter("pVlrData2", DateUtil.obterDataComHoraFinal(parametro.getVlrData()), TemporalType.TIMESTAMP);
		}
		if (parametro.getVlrNumerico() != null) {
			query.setParameter("pVlrNumerico", parametro.getVlrNumerico());
		}
		if (StringUtils.isNotBlank(parametro.getVlrTexto())) {
			query.setParameter("pVlrTexto", "%".concat(parametro.getVlrTexto().toUpperCase()).concat("%"));
		}

		return query;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public ParametroAgendaVO consultarParametros() {
		StringBuffer sql = new StringBuffer(1200);	

		sql.append(" select cnv_sus.COD as codCnvSusCod ")
				.append(" ,csp_int.COD  as codCspIntSeq ")
				.append(" ,crg_prz.COD as codCrgPrzPln")
				.append(" from   ( select vlr_numerico cod ")
				.append(" from   agh.agh_parametros ")
				.append(" where  nome = 'P_CONVENIO_SUS_PADRAO' ")
				.append(" ) cnv_sus ").append(" ,( select vlr_numerico cod ")
				.append(" from   agh.agh_parametros ")
				.append(" where  nome = 'P_SUS_PLANO_INTERNACAO' ")
				.append(" ) csp_int ").append(" ,( select vlr_numerico cod ")
				.append(" from   agh.agh_parametros ")
				.append(" where  nome = 'P_AGHU_PRAZO_PLAN_CIRURGIA' ")
				.append(" ) crg_prz ");
	

		SQLQuery query = createSQLQuery(sql.toString());
		
		query.addScalar("codCnvSusCod", BigDecimalType.INSTANCE);
		query.addScalar("codCspIntSeq", BigDecimalType.INSTANCE);
		query.addScalar("codCrgPrzPln", BigDecimalType.INSTANCE);

		// Transforma e seta aliases do resultado no VO
		query.setResultTransformer(Transformers.aliasToBean(ParametroAgendaVO.class));


		// Retorna Lista
		return (ParametroAgendaVO)query.uniqueResult();


	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public List<AghParametros> obterPorVariosNomes(String[] nomeParametros){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class, "par");
		criteria.add(Restrictions.in("par."+AghParametros.Fields.NOME.toString(), nomeParametros));
		
//		criteria.setResultTransformer(Transformers.aliasToBean(AghParametrosVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Método responsável por adicionar as regras de pesquisa
	 * @param parametro Informações de pesquisa do hospital
	 * @return criteria
	 */
	private DetachedCriteria createCriteriaPesquisarHospital(Object parametro) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class, "par");
		if (parametro instanceof String) {
			String codigo = (String) parametro;
			if (!StringUtils.isBlank(codigo)) {
				criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), parametro));
			}			
		}		
		return criteria;
	}
	
	/**
	 * Restrição da listagem de datas de vencimento VO
	 * @param parametro informações referente hospital
	 * @return objeto aghParametros
	 */
	public AghParametros pesquisaHospital(Object parametro) {
		return (AghParametros) this.executeCriteriaUniqueResult(createCriteriaPesquisarHospital(parametro), true);
	}
	
	/**
	 * 42011
	 */
	public BigDecimal buscarParametro () {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class);
		
		criteria.add(Restrictions.eq(AghParametros.Fields.SIS_SIGLA.toString(), "FAT"));
		criteria.add(Restrictions.eq(AghParametros.Fields.NOME.toString(), "P_PRIN_APAC_CORNEA"));
		
		AghParametros aghParametros = (AghParametros) executeCriteriaUniqueResult(criteria);
		if (aghParametros == null) {
			return null;
		}
		
		return aghParametros.getVlrNumerico();
	}
}