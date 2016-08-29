package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.questionario.vo.VFatProcedSusPhiVO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvGrupoItemProcedId;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;

public class FatConvGrupoItensProcedDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatConvGrupoItemProced> {
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = 3997902033978420135L;

	/**
	 * Obtém um procedimento por parte de seu ID. Retorna o primeiro da lista.
	 * 
	 * @param {Short} cpgCphCspCnvCodigo
	 * @param {Byte} cpgCphCspSeq
	 * @param {Integer} phiSeq
	 * 
	 * @return {FatConvGrupoItensProced}
	 */
	public FatConvGrupoItemProced obterFatConvGrupoItensProcedPeloId(Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Integer phiSeq) {
		List<FatConvGrupoItemProced> list = this.obterListaFatConvGrupoItensProced(cpgCphCspSeq, cpgCphCspCnvCodigo, phiSeq, 1);
		FatConvGrupoItemProced retorno = null;
		
		if (list != null && list.size() > 0) {
			retorno = list.get(0);
		}

		return retorno;
	}
	
	public FatConvGrupoItemProced obterFatConvGrupoItensProcedId(FatConvGrupoItemProcedId id) {	
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);
		if (id.getCpgCphCspCnvCodigo() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), id.getCpgCphCspCnvCodigo()));
		}
		if (id.getCpgCphCspSeq() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), id.getCpgCphCspSeq()));
		}
		if (id.getPhiSeq() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), id.getPhiSeq()));
		}
		if (id.getCpgCphPhoSeq() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), id.getCpgCphPhoSeq()));
		}
		
		if (id.getCpgGrcSeq() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), id.getCpgGrcSeq()));
		}
		if (id.getIphSeq() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), id.getIphSeq()));
		}
		if (id.getIphPhoSeq() != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), id.getIphPhoSeq()));
		}

		return (FatConvGrupoItemProced) executeCriteriaUniqueResult(criteria);
	}	
	
	
	
	/**
	 * ORADB procedure MPMP_VER_DURACAO_TRAT
	 * 
	 * @param cpgCphCspSeq
	 * @param cpgCphCspCnvCodigo
	 * @param phiSeq
	 * 
	 * @return list of <code>FatConvGrupoItensProced</code>
	 */
	private List<FatConvGrupoItemProced> obterListaFatConvGrupoItensProced(Byte cpgCphCspSeq, Short cpgCphCspCnvCodigo, Integer phiSeq, Integer maxResult) {
		if (cpgCphCspSeq == null && cpgCphCspCnvCodigo == null && phiSeq == null) {
			throw new IllegalArgumentException("Parametros Invalidos!!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);
		
		if (cpgCphCspCnvCodigo != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		}
		if (cpgCphCspSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		}
		if (phiSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), phiSeq));
		}

		if(maxResult != null){
			return this.executeCriteria(criteria, 0, maxResult, null, true);
		} else {
			return this.executeCriteria(criteria);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> buscaProcedimentoHospitalarInternoAgrupa(
			Integer phiSeq, Short cnvCodigo, Byte cspSeq, Short phoSeq,
			Short tipoGrupoContaSUS) {
		StringBuffer hql = new StringBuffer(230);
		hql.append(" select iph.");
		hql.append(FatItensProcedHospitalar.Fields.DESCRICAO.toString());
		hql.append(" from FatConvGrupoItemProced cgi ");
		hql.append(" 	join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iph ");
		hql.append(" where cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString());
		hql.append(" = :phiSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSUS ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" >= 0 ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" >= 0 ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString());
		hql.append(" >= 0");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" >= 0 ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<Long> buscaDescricaoProcedimentoHospitalarInterno(
			Integer phiSeq, Short cnvCodigo, Byte cspSeq, Short phoSeq,
			Short tipoGrupoContaSUS) {
		StringBuffer hql = new StringBuffer(230);
		hql.append(" select iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" from FatConvGrupoItemProced cgi ");
		hql.append(" 	join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iph ");
		hql.append(" where cgi.");
		hql.append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString());
		hql.append(" = :phiSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" = :cnvCodigo");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" = :cspSeq ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" = :tipoGrupoContaSUS ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString());
		hql.append(" >= 0 ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString());
		hql.append(" >= 0 ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString());
		hql.append(" >= 0 ");
		hql.append(" 	and cgi.");
		hql.append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString());
		hql.append(" >= 0 ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("phiSeq", phiSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("tipoGrupoContaSUS", tipoGrupoContaSUS);
		
		return query.list();
	}
	
	public Boolean verificarFatConvGrupoItensProcedExigeJustificativa(
			FatProcedHospInternos fatProcedHospInternos,
			FatConvenioSaudePlano convenioSaudePlano) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatConvGrupoItemProced.class);

		criteria.add(Restrictions.eq(
				FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(),
				fatProcedHospInternos));
		
		criteria.add(Restrictions.eq(
				FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(),
				convenioSaudePlano.getId().getSeq()));
		
		criteria
				.add(Restrictions.eq(
						FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
								.toString(), convenioSaudePlano.getId()
								.getCnvCodigo()));
		
		criteria.add(Restrictions.eq(
				FatConvGrupoItemProced.Fields.IMPRIME_LAUDO.toString(),
				Boolean.TRUE));
		
		criteria.setProjection(Projections
				.property(FatConvGrupoItemProced.Fields.IMPRIME_LAUDO
						.toString()));
		
		Boolean value = (Boolean) this.executeCriteriaUniqueResult(criteria);
		return value != null ? value : false;
	}

	@SuppressWarnings("unchecked")
	public FatConvGrupoItemProced buscaItemProcedimentoHospitalarEquivalenteProcedimentoHospitalarInternoTabelaExcecao(
			Integer phiSeq, Long quantidadeRealizada, Short grcSus, DominioSituacao situacao, Byte cspSeq, Short cnvCodigo) {
		StringBuffer hql = new StringBuffer(350);

		hql.append(" select new br.gov.mec.aghu.model.FatConvGrupoItemProced(")
						.append(" cgi.").append(FatConvGrupoItemProced.Fields.IPH_SEQ.toString())
						.append(",cgi.").append(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString()).append(')');
		hql.append(" from ");
		hql.append(FatConvGrupoItemProced.class.getSimpleName());
		hql.append(" as cgi ");
		hql.append(" join cgi.");
		hql.append(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString());
		hql.append(" as iph ");
		hql.append(" where iph.");
		hql.append(FatItensProcedHospitalar.Fields.IND_SITUACAO.toString());
		hql.append(" = :situacao ");
		hql.append(" and coalesce(iph.");
		hql.append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.toString());
		hql.append(", 1) <= :quantidadeRealizada ");
		hql.append(" and cgi.").append(FatConvGrupoItemProced.Fields.PHI_SEQ.toString()).append(" = :phiSeq ");
		hql.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString()).append(" = :grcSus ");
		hql.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString()).append(" = :cspSeq ");
		hql.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo ");
		hql.append(" and cgi.").append(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString()).append(" = cgi.").append(
				FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString());
		hql.append(" order by substring(cast(iph.");
		hql.append(FatItensProcedHospitalar.Fields.COD_TABELA.toString());
		hql.append(" as string),1,5), iph.");
		hql.append(FatItensProcedHospitalar.Fields.QTD_PROCEDIMENTOS_ITEM.toString());
		hql.append(" desc ");
		
		Query query = createHibernateQuery(hql.toString());

		query.setParameter("phiSeq", phiSeq);
		query.setParameter("quantidadeRealizada", quantidadeRealizada!=null?quantidadeRealizada.shortValue():null);
		query.setParameter("grcSus", grcSus);
		query.setParameter("situacao", situacao);
		query.setParameter("cspSeq", cspSeq);
		query.setParameter("cnvCodigo", cnvCodigo);
		
		query.setMaxResults(1);
		
		List<FatConvGrupoItemProced> list = query.list(); 
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}
	
	public FatConvGrupoItemProced buscarPrimeiraFatConvGrupoItensProced(Integer pPhiSeq, Short pIphPhoSeq, Integer pIphSeq,
			Short pCnvCodigo, Byte pCnvCspSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), pPhiSeq));

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), pIphSeq));

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), pIphPhoSeq));

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), pCnvCspSeq));

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), pCnvCodigo));

		criteria.add(Restrictions.eqProperty(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(),
				FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString()));

		List<FatConvGrupoItemProced> list = executeCriteria(criteria, 0, 1, null, true);
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	
	public List<FatConvGrupoItemProced> listarFatConvGrupoItensProced(Short pIphPhoSeq, Integer pIphSeq, Short pCnvCodigo, Byte pCnvCspSeq,
			Short pCpgCphPhoSeq, Short pCpgGrcSeq, Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);

		criteria.createAlias(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "IPH");
		criteria.createAlias(FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "ITEM", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(FatConvGrupoItemProced.Fields.CONVENIO_SAUDE_PLANO.toString(), "CONVENIO", JoinType.LEFT_OUTER_JOIN);

		if (pIphSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), pIphSeq));
		}
		if (pIphPhoSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), pIphPhoSeq));
		}

		if (pCnvCspSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), pCnvCspSeq));
		}

		if (pCnvCodigo != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), pCnvCodigo));
		}

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), pCpgCphPhoSeq));

		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), pCpgGrcSeq));

		if (phiSeq != null) {
			criteria.add(Restrictions.eq("IPH." + FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));
		}

		criteria.addOrder(Order.asc("IPH." + FatProcedHospInternos.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}
	
	public List<FatConvGrupoItemProced> listarFatConvGrupoItensProcedPorPhi(Short pIphPhoSeq, Integer pIphSeq, Short pCnvCodigo, Byte pCnvCspSeq,
			Short pCpgCphPhoSeq, Short pCpgGrcSeq, Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);
		criteria.createAlias(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "IPH");

		if (pIphSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), pIphSeq));
		}
		if (pIphPhoSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), pIphPhoSeq));
		}

		if (pCnvCspSeq != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), pCnvCspSeq));
		}

		if (pCnvCodigo != null) {
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), pCnvCodigo));
		}

		if(pCpgCphPhoSeq != null){
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), pCpgCphPhoSeq));
		}
		
		if(pCpgGrcSeq != null){
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), pCpgGrcSeq));
		}
		
		if (phiSeq != null) {
			criteria.add(Restrictions.eq("IPH." + FatProcedHospInternos.Fields.SEQ.toString(), phiSeq));
		}

		criteria.addOrder(Order.asc("IPH." + FatProcedHospInternos.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * Método para obter um objeto FatConvGrupoItensProced através do parametro
	 * iphSeq recebido, com seq e codigo do convenio igual a 1.
	 * 
	 * @param iphSeq
	 * @param iphPhoSeq
	 * @param phoSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<FatConvGrupoItemProced> pesquisarConvenioGrupoItemProcedimento(
			Integer iphSeq, Short iphPhoSeq) throws ApplicationBusinessException {
		
		if (iphSeq == null) {
			return null;
		} else {
			DetachedCriteria criteria = DetachedCriteria
					.forClass(FatConvGrupoItemProced.class);

			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_SEQ
					.toString(), iphSeq));
			criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ
					.toString(), iphPhoSeq));
			

			AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);

			criteria.add(Restrictions.eq(
					FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO
							.toString(), param.getVlrNumerico().shortValue()));
			criteria.add(Restrictions.eq(
					FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ
							.toString(), param.getVlrNumerico().byteValue()));

			return executeCriteria(criteria);
		}
	}
	
	protected IParametroFacade getParametroFacade() {
		//return (ParametroFacade)Component.getInstance(ParametroFacade.class, true);
		return aIParametroFacade;
	}
	
	public List<FatConvGrupoItemProced> obterListaFatConvGrupoItensProcedPorExame(Byte cpgCphCspSeq, Short cpgCphCspCnvCodigo, Short phiPhoSeq, String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		if (cpgCphCspSeq == null || cpgCphCspCnvCodigo == null || phiPhoSeq == null || emaExaSigla == null || emaManSeq == null || unfSeq == null) {
			throw new IllegalArgumentException("Parametros Invalidos!!!");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);
				
		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));		
		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), phiPhoSeq));		

		DetachedCriteria criteriaProcedHospInt = criteria.createCriteria(FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString());
		DetachedCriteria criteriaExameMatAnalise = criteriaProcedHospInt.createCriteria(FatProcedHospInternosPai.Fields.EXAME_MATERIAL_ANALISE.toString());
		DetachedCriteria criteriaUnfExecutaExame = criteriaExameMatAnalise.createCriteria(AelExamesMaterialAnalise.Fields.UNF_EXECUTA_EXAME.toString());
		criteriaUnfExecutaExame.add(Restrictions.eq(AelUnfExecutaExames.Fields.UNF_SEQ.toString(), unfSeq));
		criteriaUnfExecutaExame.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteriaUnfExecutaExame.add(Restrictions.eq(AelUnfExecutaExames.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		
		return this.executeCriteria(criteria);
		
	}
	
	
	public List<VFatProcedSusPhiVO> pesquisarViewFatProcedSusPhiVO(VFatProcedSusPhiVO filtro, Short pSusPadrao,
			Byte pSusAmbulatorio, Short pTipoGrupoContaSus) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "CGI");
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "PHI");
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.PHI_SEQ.toString()), "phiSeq")
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString()), "iphSeq")
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString()), "iphPhoSeq")
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString()), "cpgCphCspCnvCodigo")
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString()), "cpgCphCspSeq")
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString()), "cpgCphPhoSeq")
				.add(Projections.property("CGI." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString()), "cpgGrcSeq")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()), "codTabela")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IDADE_MIN.toString()), "idadeMin")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IDADE_MAX.toString()), "idadeMax")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString()), "situacao")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.PROC_PRINCIPAL_APAC.toString()), "procPrincipalApac")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.SEQ.toString()), "seq")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString()), "internacao")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.TIPO_AIH5.toString()), "tipoAih5")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_CONSULTA.toString()), "consulta")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_CONSULTA.toString()), "exigeConsulta")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_COBRANCA_APAC.toString()), "cobrancaApac")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_COBRANCA_CONTA.toString()), "cobrancaConta")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_COBRANCA_AMBULATORIO.toString()), "cobrancaAmbulatorio")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.IND_MODO_LANCAMENTO_FAT.toString()), "modoLancamentoFat")
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_PROCEDIMENTO.toString()), "codProcedimento")
				.add(Projections.property("PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "descricaoProcedHospInterno")
				.add(Projections.property("PHI." + FatProcedHospInternos.Fields.SITUACAO.toString()), "situacaoProcedHospInterno"));

		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), pSusPadrao);
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), pSusAmbulatorio);
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), pTipoGrupoContaSus);
		
		adicionarFiltros(criteria, filtro);
		
		criteria.setResultTransformer(Transformers.aliasToBean(VFatProcedSusPhiVO.class));
		
		return executeCriteria(criteria);
	}
	
	private void adicionarFiltros(DetachedCriteria criteria, VFatProcedSusPhiVO filtro) {
		if (filtro == null) {
			return;
		}
		
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), filtro.getPhiSeq());
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), filtro.getIphSeq());
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), filtro.getIphPhoSeq());
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), filtro.getCpgCphCspCnvCodigo());
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), filtro.getCpgCphCspSeq());
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), filtro.getCpgCphPhoSeq());
		adicionarFiltroIgual(criteria, "CGI." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), filtro.getCpgGrcSeq());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), filtro.getCodTabela());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IDADE_MIN.toString(), filtro.getIdadeMin());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IDADE_MAX.toString(), filtro.getIdadeMax());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), filtro.getSituacao());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.PROC_PRINCIPAL_APAC.toString(), filtro.getProcPrincipalApac());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.SEQ.toString(), filtro.getSeq());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_INTERNACAO.toString(), filtro.getInternacao());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.TIPO_AIH5.toString(), filtro.getTipoAih5());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_CONSULTA.toString(), filtro.getConsulta());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_EXIGE_CONSULTA.toString(), filtro.getExigeConsulta());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_COBRANCA_APAC.toString(), filtro.getCobrancaApac());	
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_COBRANCA_CONTA.toString(), filtro.getCobrancaConta());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_COBRANCA_AMBULATORIO.toString(), filtro.getCobrancaAmbulatorio());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.IND_MODO_LANCAMENTO_FAT.toString(), filtro.getModoLancamentoFat());
		adicionarFiltroIgual(criteria, "IPH." + FatItensProcedHospitalar.Fields.COD_PROCEDIMENTO.toString(), filtro.getCodProcedimento());
		adicionarFiltroSemelhante(criteria, "IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString(), filtro.getDescricao());
		adicionarFiltroSemelhante(criteria, "PHI." + FatProcedHospInternos.Fields.DESCRICAO.toString(), filtro.getDescricaoProcedHospInterno());
		adicionarFiltroSemelhante(criteria, "PHI." + FatProcedHospInternos.Fields.SITUACAO.toString(), filtro.getSituacaoProcedHospInterno());		
	}

	private void adicionarFiltroIgual(DetachedCriteria criteria, final String restricao, final Dominio filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.eq(restricao, filtro));
		}
	}
	
	private void adicionarFiltroIgual(DetachedCriteria criteria, final String restricao, final Long filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.eq(restricao, filtro));
		}
	}	

	private void adicionarFiltroIgual(DetachedCriteria criteria, final String restricao, final Integer filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.eq(restricao, filtro));
		}
	}
	
	private void adicionarFiltroIgual(DetachedCriteria criteria, final String restricao, final Byte filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.eq(restricao, filtro));
		}
	}	
	
	private void adicionarFiltroIgual(DetachedCriteria criteria, final String restricao, final Short filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.eq(restricao, filtro));
		}
	}
	
	private void adicionarFiltroIgual(DetachedCriteria criteria, final String restricao, final Boolean filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.eq(restricao, filtro));
		}
	}
	
	private void adicionarFiltroSemelhante(DetachedCriteria criteria, final String restricao, final Object filtro) {
		if (filtro != null) {
			criteria.add(Restrictions.ilike(restricao, filtro));
		}
	}	
	
	public List<FatConvGrupoItemProced> pesquisarCirurgiaSusProcedimentoHospitalarInterno(final Short cpgGrcSeq, final Integer crgSeq, final DominioOrigemPacienteCirurgia origem, final Short cnvCodigo, final Byte cspSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "cgi");

		criteria.createAlias("cgi." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "iph");
		criteria.createAlias("cgi." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "phi");
		criteria.createAlias("phi." + FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci");
		
		criteria.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), cpgGrcSeq));

		criteria.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		criteria.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		
		criteria.add(Restrictions.eq("iph." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subQueryPpc = DetachedCriteria.forClass(MbcProcEspPorCirurgias.class, "ppc");
		
		
		subQueryPpc.setProjection(Projections.property("ppc." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString()));
		
		subQueryPpc.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.ID_CRG_SEQ.toString(), crgSeq));
		subQueryPpc.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.SITUACAO.toString(), DominioSituacao.A));
		subQueryPpc.add(Restrictions.eq("ppc." + MbcProcEspPorCirurgias.Fields.IND_RESP_PROC.toString(), DominioIndRespProc.NOTA));
		subQueryPpc.add(Restrictions.isNull("ppc." + MbcProcEspPorCirurgias.Fields.PROCEDIMENTO_HOSP_INTERNO.toString()));
		subQueryPpc.add(Property.forName("ppc." + MbcProcEspPorCirurgias.Fields.EPR_PCI_SEQ.toString()).eqProperty("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString()));

		
		criteria.add(Subqueries.exists(subQueryPpc));


		return executeCriteria(criteria);
	}
	
	
	public Integer obterProcedimentoCirurgicoPopularProcedimentoHospitalarInterno(final Short iphPhoSeq, final Integer iphSeq, final Integer pciSeq, final Date cpeComp, final Short cnvCodigo, final Byte cspSeq, final Short tipoGrupoContaSus) {

		// FAT_CONV_GRUPO_ITENS_PROCED CGI
		DetachedCriteria subQueryCgi = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "cgi");
		subQueryCgi.setProjection(Projections.property("phi." + FatProcedHospInternos.Fields.SEQ.toString()));

		// FAT_PROCED_HOSP_INTERNOS PHI
		subQueryCgi.createAlias("cgi." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "phi");
		
		// Necessário para WHERE phi.pci_seq = c_pci_seq
		subQueryCgi.createAlias("phi." + FatProcedHospInternos.Fields.PROCEDIMENTO_CIRURGICO.toString(), "pci");
		subQueryCgi.add(Restrictions.eq("pci." + MbcProcedimentoCirurgicos.Fields.SEQ.toString(), pciSeq)); // WHERE phi.pci_seq = c_pci_seq
		
		subQueryCgi.add(Restrictions.eq("phi." + FatProcedHospInternos.Fields.SITUACAO.toString(),  DominioSituacao.A));
		
		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), tipoGrupoContaSus));

		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cnvCodigo));
		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cspSeq));
		
		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.IPH_SEQ.toString(), iphSeq));
		subQueryCgi.add(Restrictions.eq("cgi." + FatConvGrupoItemProced.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));

		return (Integer)executeCriteriaUniqueResult(subQueryCgi);
	}

	/**
	 * Realiza a busca por Itens de Procedimento Ativo através do código do Procedimento Hospitalar Interno.
	 * 
	 * @param seqProcedHospInterno - Código do Procedimento Hospitalar Interno
	 * @return Lista de Itens de Procedimento Ativo
	 */
	public List<FatConvGrupoItemProced> pesquisarItensProcedimentoAtivosPorCodigoProcedimento(Integer seqProcedHospInterno) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "CGI");
		
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.PROCED_HOSP_INTERNO.toString(), "PHI");
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH");
		
		criteria.add(Restrictions.ge("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), 0));
		criteria.add(Restrictions.ge("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), 0));
		criteria.add(Restrictions.ge("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), 0));
		criteria.add(Restrictions.ge("CGI." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), 0));
		criteria.add(Restrictions.ge("PHI." + FatProcedHospInternos.Fields.SEQ.toString(), seqProcedHospInterno));
		criteria.add(Restrictions.ge("IPH." + FatItensProcedHospitalar.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * c19 #47668
	 * @param phiSeq
	 * @return
	 */
	public FatConvGrupoItemProced obterCodigoTabelaDescricaoPorPhiSeq(Integer phiSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class, "CGI");
		
		criteria.createAlias("CGI." + FatConvGrupoItemProced.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH");
		
	
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), Short.valueOf("1")));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), Byte.valueOf("2")));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_CPH_PHO_SEQ.toString(), Short.valueOf("12")));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.CPG_GRC_SEQ.toString(), Short.valueOf("6")));
		criteria.add(Restrictions.eq("CGI." + FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), phiSeq));
		
		List<FatConvGrupoItemProced> list = executeCriteria(criteria); 
		if(list != null && !list.isEmpty()){
			return list.get(0);
		}
		return null;
	}

	public List<FatConvGrupoItemProced> pesquisarConvenioVerificarDuracaoTratamento(Short cpgCphCspCnvCodigo, Byte cpgCphCspSeq, Integer phiSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatConvGrupoItemProced.class);
		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_CNV_CODIGO.toString(), cpgCphCspCnvCodigo));
		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.CPG_CPH_CSP_SEQ.toString(), cpgCphCspSeq));
		criteria.add(Restrictions.eq(FatConvGrupoItemProced.Fields.PHI_SEQ.toString(), phiSeq));
		return executeCriteria(criteria);
	}

}
