package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSSM;
import br.gov.mec.aghu.faturamento.vo.ParCthSeqSsmVO;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;

public class FatCaractFinanciamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCaractFinanciamento> {

	private static final long serialVersionUID = -6928566061615420941L;
	
	@SuppressWarnings("unchecked")
	public List<ParCthSeqSsmVO> listarSsmComplexParaListaCthSeq(
			List<Integer> listaCthSeq, Short cnvCodigo, Byte cspSeq,
			DominioSituacaoSSM sitSsm) {

		List<ParCthSeqSsmVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String eai = null;
		String phi = null;

		switch (sitSsm) {
		case S:
			eai = FatItensProcedHospitalar.Fields.ESPELHOS_AIH_SOLICITADO
					.toString();
			phi = FatContasHospitalares.Fields.PHI_SEQ.toString();
			break;
		case R:
			eai = FatItensProcedHospitalar.Fields.ESPELHOS_AIH_REALIZADO
					.toString();
			phi = FatContasHospitalares.Fields.PHI_SEQ_REALIZADO.toString();
			break;
		default:
			throw new IllegalArgumentException("Situacao SSM desconhecida: "
					+ sitSsm);
		}
		hql = new StringBuffer();
		hql.append(" select eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" as cthSeq");
		// solicitado
		hql.append(" , fcfS.");
		hql.append(FatCaractFinanciamento.Fields.DESCRICAO.toString());
		hql.append(" as ssmStr ");
		// solicitado
		hql.append(" from ");
		hql.append(FatContasHospitalares.class.getName());
		hql.append(" as cthS ");
		hql.append(" , ");
		hql.append(FatCaractFinanciamento.class.getName());
		hql.append(" as fcfS ");
		hql.append(" left join fcfS.");
		hql.append(FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
				.toString());
		hql.append(" as iphS ");
		hql.append(" left join iphS.");
		hql.append(eai);
		hql.append(" as eaiS ");
		hql.append(" left join iphS.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
				.toString());
		hql.append(" as cgiS with ( ");
		hql.append(" 	cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
				.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		// solicitado
		hql.append(" where eaiS.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" in (:cthSeq)");
		hql.append(" and cthS.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = eaiS.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" and cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString());
		hql.append(" = cthS.");
		hql.append(phi);
		// query
		query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("seqp", Integer.valueOf(1));
		query.setParameterList("cthSeq", listaCthSeq);
		// vo
		query.setResultTransformer(Transformers
				.aliasToBean(ParCthSeqSsmVO.class));
		// result
		result = query.list();

		return result;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoSolicitadoComplexidade(Integer cthSeq,
			Short cnvCodigo, Byte cspSeq, Integer phiSeq) {
		StringBuffer hql = new StringBuffer(210);

		hql.append(" select fcf.");
		hql.append(FatCaractFinanciamento.Fields.DESCRICAO.toString());
		hql.append(" from ");
		hql.append(FatCaractFinanciamento.class.getName());
		hql.append(" as fcf ");
		hql.append(" left join fcf.");
		hql.append(FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
				.toString());
		hql.append(" as iph ");
		hql.append(" left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.ESPELHOS_AIH_SOLICITADO
				.toString());
		hql.append(" as eai ");
		hql.append(" left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
				.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
				.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" where eai.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eai.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString());
		hql.append(" = :phiSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("seqp", 1);
		query.setMaxResults(1);

		List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoRealizadoComplexidade(Integer cthSeq,
			Short cnvCodigo, Byte cspSeq, Integer phiSeq) {
		StringBuffer hql = new StringBuffer(210);

		hql.append(" select fcf.");
		hql.append(FatCaractFinanciamento.Fields.DESCRICAO.toString());
		hql.append(" from ");
		hql.append(FatCaractFinanciamento.class.getName());
		hql.append(" as fcf ");
		hql.append(" left join fcf.");
		hql.append(FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
				.toString());
		hql.append(" as iph ");
		hql.append(" left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.ESPELHOS_AIH_REALIZADO
				.toString());
		hql.append(" as eai ");
		hql.append(" left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
				.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
				.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" where eai.");
		hql.append(FatEspelhoAih.Fields.SEQP.toString());
		hql.append(" = :seqp ");
		hql.append(" and eai.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString());
		hql.append(" = :phiSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("seqp", 1);
		query.setMaxResults(1);

		List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public List<ParCthSeqSsmVO> listarSsmAbertaFechadaComplexidadeParaListaCthSeq(
			List<Integer> listaCthSeq, Short cnvCodigo, Byte cspSeq,
			Short tipoGrupoContaSUS, DominioSituacaoSSM sitSsm) {

		List<ParCthSeqSsmVO> result = null;
		StringBuffer hql = null;
		Query query = null;
		String cth = null;

		switch (sitSsm) {
		case S:
			cth = FatProcedHospInternos.Fields.CONTAS_HOSPITALARES.toString();
			break;
		case R:
			cth = FatProcedHospInternos.Fields.CONTAS_HOSPITALARES_REALIZADAS
					.toString();
			break;
		default:
			throw new IllegalArgumentException("Situacao SSM desconhecida: "
					+ sitSsm);
		}
		hql = new StringBuffer();
		hql.append(" select cthS.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" as cthSeq");
		// solicitado
		hql.append(" , fcfS.");
		hql.append(FatCaractFinanciamento.Fields.DESCRICAO.toString());
		hql.append(" as ssmStr ");
		hql.append(" from ");
		// solicitado
		hql.append(FatCaractFinanciamento.class.getName());
		hql.append(" as fcfS ");
		hql.append(" join fcfS.");
		hql.append(FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
				.toString());
		hql.append(" as iphS ");
		hql.append(" left join iphS.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
				.toString());
		hql.append(" as cgiS with ( ");
		hql.append(" 	cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
				.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" left join cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString());
		hql.append(" as phiS ");
		hql.append(" join phiS.");
		hql.append(cth);
		hql.append(" as cthS ");
		// solicitado
		hql.append(" where cthS.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" in (:cthSeq) ");
		hql.append(" and cgiS.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSUS ");
		// query
		query = createHibernateQuery(hql.toString());
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		query.setParameterList("cthSeq", listaCthSeq);
		// vo
		query.setResultTransformer(Transformers
				.aliasToBean(ParCthSeqSsmVO.class));
		// result
		result = query.list();

		return result;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoSolicitadoAbertaFechadaComplexidade(
			Integer cthSeq, Short cnvCodigo, Byte cspSeq, Integer phiSeq,
			Short tipoGrupoContaSUS) {
		StringBuffer hql = new StringBuffer(240);

		hql.append(" select fcf.");
		hql.append(FatCaractFinanciamento.Fields.DESCRICAO.toString());
		hql.append(" from ");
		hql.append(FatCaractFinanciamento.class.getName());
		hql.append(" as fcf ");
		hql.append(" join fcf.");
		hql.append(FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
				.toString());
		hql.append(" as iph ");
		hql.append(" left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
				.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
				.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" left join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString());
		hql.append(" as phi ");
		hql.append(" join phi.");
		hql.append(FatProcedHospInternos.Fields.CONTAS_HOSPITALARES.toString());
		hql.append(" as cth ");
		hql.append(" where cth.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSUS ");
		hql.append(" and cth.");
		hql.append(FatContasHospitalares.Fields.PHI_SEQ.toString());
		hql.append(" = :phiSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		query.setMaxResults(1);

		List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	@SuppressWarnings("unchecked")
	public String buscaProcedimentoRealizadoAbertaFechadaComplexidade(
			Integer cthSeq, Short cnvCodigo, Byte cspSeq, Integer phiSeq,
			Short tipoGrupoContaSUS) {
		StringBuffer hql = new StringBuffer(240);

		hql.append(" select fcf.");
		hql.append(FatCaractFinanciamento.Fields.DESCRICAO.toString());
		hql.append(" from ");
		hql.append(FatCaractFinanciamento.class.getName());
		hql.append(" as fcf ");
		hql.append(" join fcf.");
		hql.append(FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
				.toString());
		hql.append(" as iph ");
		hql.append(" left join iph.");
		hql.append(FatItensProcedHospitalar.Fields.GRUPOS_ITENS_PROCED
				.toString());
		hql.append(" as cgi with ( ");
		hql.append(" 	cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
				.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" ) ");
		hql.append(" left join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString());
		hql.append(" as phi ");
		hql.append(" join phi.");
		hql.append(FatProcedHospInternos.Fields.CONTAS_HOSPITALARES_REALIZADAS
				.toString());
		hql.append(" as cth ");
		hql.append(" where cth.");
		hql.append(FatContasHospitalares.Fields.SEQ.toString());
		hql.append(" = :cthSeq ");
		hql.append(" and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSUS ");
		hql.append(" and cth.");
		hql.append(FatContasHospitalares.Fields.PHI_SEQ_REALIZADO.toString());
		hql.append(" = :phiSeq ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		query.setMaxResults(1);

		List<String> lista = query.list();
		return lista != null && lista.size() > 0 ? lista.get(0) : null;
	}

	public FatCaractFinanciamento obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(
			Short iphPhoSeq, Integer iphSeq, Long codTabela) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCaractFinanciamento.class);
		criteria.createAlias(
				FatCaractFinanciamento.Fields.FAT_ITENS_PROCEDIMENTOS_HOSPITALARES
						.toString(), "IPH");

		criteria.add(Restrictions.eq("IPH."
				+ FatItensProcedHospitalar.Fields.SEQ.toString(), iphSeq));
		criteria.add(Restrictions.eq("IPH."
				+ FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), iphPhoSeq));
		criteria.add(Restrictions.eq("IPH."
				+ FatItensProcedHospitalar.Fields.COD_TABELA.toString(),
				codTabela));

		return (FatCaractFinanciamento) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Lista financiamentos ativos.
	 * 
	 * @param pesquisa
	 * @return
	 */
	public List<FatCaractFinanciamento> listarFinanciamentosAtivosPorCodigoOuDescricao(
			Object pesquisa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCaractFinanciamento.class);

		String strParametro = (String) pesquisa;

		if (StringUtils.isNotBlank(strParametro)) {

			criteria.add(Restrictions.eq(
					FatCaractFinanciamento.Fields.CODIGO.toString(),
					strParametro));
		}

		criteria.add(Restrictions.eq(
				FatCaractFinanciamento.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));


		criteria.addOrder(Order.asc(FatCaractFinanciamento.Fields.DESCRICAO
				.toString()));

		List<FatCaractFinanciamento> result = this.executeCriteria(criteria);

		if (result == null || result.size() == 0) {
			criteria = DetachedCriteria.forClass(FatCaractFinanciamento.class);

			criteria.add(Restrictions.ilike(
					FatCaractFinanciamento.Fields.DESCRICAO.toString(),
					strParametro, MatchMode.ANYWHERE));

			criteria.add(Restrictions.eq(
					FatCaractFinanciamento.Fields.IND_SITUACAO.toString(),
					DominioSituacao.A));

			criteria.addOrder(Order.asc(FatCaractFinanciamento.Fields.DESCRICAO
					.toString()));

			result = this.executeCriteria(criteria);
		}
		return result;
	}
	
	/**
	 * #2112 - consulta padrão
	 */
	public List<FatCaractFinanciamento> pesquisarCaracteristicasFinanciamento(Integer firstResult, Integer maxResult, String orderProperty, 
			boolean asc, final FatCaractFinanciamento filtro) {
		DetachedCriteria criteria = montarQueryCaracteristicaFinanciamento(filtro);
		if (StringUtils.isEmpty(orderProperty)) {
			orderProperty = FatCaractFinanciamento.Fields.SEQ.toString();
			asc = true;
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);		
	}

	public Long pesquisarCaractFinanciamentosCount(final FatCaractFinanciamento filtro) {
		DetachedCriteria criteria = montarQueryCaracteristicaFinanciamento(filtro);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarQueryCaracteristicaFinanciamento(final FatCaractFinanciamento filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractFinanciamento.class);
		
		// Evita NPath complexity
		inserirFiltros(criteria, filtro);		
		
		return criteria;
	}
	
	private void inserirFiltros(DetachedCriteria criteria, final FatCaractFinanciamento filtro) {
		if (StringUtils.isNotBlank(filtro.getCodigo())) {
			criteria.add(Restrictions.eq(FatCaractFinanciamento.Fields.CODIGO.toString(), filtro.getCodigo()));
		}
		
		if (filtro.getSeqSus() != null) {
			criteria.add(Restrictions.eq(FatCaractFinanciamento.Fields.SEQ_SUS.toString(), filtro.getSeqSus()));
		}
		
		if (StringUtils.isNotBlank(filtro.getDescricao())) {
			criteria.add(Restrictions.ilike(FatCaractFinanciamento.Fields.DESCRICAO.toString(), replacePercente(filtro.getDescricao()), MatchMode.ANYWHERE));
		}

		if (filtro.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(FatCaractFinanciamento.Fields.IND_SITUACAO.toString(), filtro.getIndSituacao()));
		}
	}
	
	private String replacePercente(String descricao) {        
	       return descricao.replace("_", "\\_").replace("%", "\\%");
	}
	
	/**
	 * #2112 - Consulta Características Financiamento vinculados a servidores
	 * 
	 */
	public FatCaractFinanciamento pesquisarCaractFinanciamentoComVinculos(final FatCaractFinanciamento entidade){
		FatCaractFinanciamento entidadeRecuperada = new FatCaractFinanciamento();
		
		entidadeRecuperada = this.obterPorChavePrimaria(entidade.getSeq());
		
		return entidadeRecuperada;
	}

	/**
	 * #2112 - Validação da RN3 - pesquisa de código já cadastrado
	 */
	public FatCaractFinanciamento pesquisarCodigoCaracteristicaFinanciamentoDuplicado(String codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractFinanciamento.class);
		
		if (StringUtils.isNotBlank(codigo)) {
			criteria.add(Restrictions.eq(FatCaractFinanciamento.Fields.CODIGO.toString(), codigo));
		}
		return (FatCaractFinanciamento) this.executeCriteriaUniqueResult(criteria);
		
	}
	
	
	public FatCaractFinanciamento obterPorCodigoSus(Integer seqSus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractFinanciamento.class);
		
		if (seqSus != null) {
			criteria.add(Restrictions.eq(FatCaractFinanciamento.Fields.SEQ_SUS.toString(), seqSus));
		}
		List<FatCaractFinanciamento> lista = this.executeCriteria(criteria,0,1,null,false);
		if(lista != null){
			if(lista.size() > 0){
				return lista.get(0);
			}
		}
		return null;
		
	}
	public List<FatCaractFinanciamento> buscaListaFatCaractFinanciamentoAtivos() {
	     DetachedCriteria criteria;
	     criteria = this.createCriteriaListaFatCaractFinanciamentoAtivos();
	     criteria = this.criarFiltroBuscaListaFatCaractFinanciamentoAtivos(criteria);
	     return executeCriteria(criteria);
	}
	public DetachedCriteria createCriteriaListaFatCaractFinanciamentoAtivos() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatCaractFinanciamento.class);
	    return criteria;
	}
	private DetachedCriteria criarFiltroBuscaListaFatCaractFinanciamentoAtivos(DetachedCriteria criteria){
		criteria.add(Restrictions.eq(FatCaractFinanciamento.Fields.IND_SITUACAO.toString(),DominioSituacao.A));
		criteria.add(Restrictions.isNotNull(FatCaractFinanciamento.Fields.SEQ_SUS.toString()));
		
		return criteria;
	}

}