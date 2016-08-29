package br.gov.mec.aghu.configuracao.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalAplicacao;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.AghParametroAplicacao;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.RapServidores;


public class AghCaixaPostalServidorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghCaixaPostalServidor> {
	private static final long serialVersionUID = -5790510028245666467L;


	/**
	 * Lista as pendâncias na tela inicial do AGHU
	 * @param servidor
	 * @return
	 */
	public List<AghCaixaPostal> pesquisarMensagemPendenciasCaixaPostal(RapServidores servidor) {
		 DominioSituacaoCxtPostalServidor[] situacao = { DominioSituacaoCxtPostalServidor.L, DominioSituacaoCxtPostalServidor.N};

		// Obtem a criteria principal
		DetachedCriteria criteria = DetachedCriteria.forClass(AghCaixaPostal.class, "CXA");

		criteria.add(Restrictions.le("CXA." + AghCaixaPostal.Fields.DTHR_INICIO.toString(), new Date()));
		criteria.add(Restrictions.or(Restrictions.isNull(AghCaixaPostal.Fields.DTHR_FIM.toString()),
				Restrictions.ge("CXA." + AghCaixaPostal.Fields.DTHR_FIM.toString(), new Date())));

		// SubCriteria da criteria principal 
		DetachedCriteria subQuery = DetachedCriteria.forClass(AghCaixaPostalServidor.class,"CXT");
		subQuery.setProjection(Projections.property("CXT."+ AghCaixaPostalServidor.Fields.CXT_SEQ.toString()));
		subQuery.add(Restrictions.eq("CXT." + AghCaixaPostalServidor.Fields.SERVIDOR.toString(),servidor));
		subQuery.add(Restrictions.in("CXT." + AghCaixaPostalServidor.Fields.SITUACAO.toString(), situacao));
		subQuery.add(Restrictions.isNull(AghCaixaPostalServidor.Fields.DTHR_EXCLUIR_DEFINITIVA.toString()));

		criteria.add(Property.forName("CXA." + AghCaixaPostal.Fields.SEQ.toString()).in(subQuery));
		// Distinct na criteria principal
		criteria.setResultTransformer(DetachedCriteria.DISTINCT_ROOT_ENTITY);
		
		criteria.addOrder(Order.desc(AghCaixaPostal.Fields.DTHR_INICIO.toString()));
		criteria.addOrder(Order.asc(AghCaixaPostal.Fields.MENSAGEM.toString()));

		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * @param cxt_seq
	 * Estoria:40229, consulta c14
	 */
	public void removerAghCaixaPostalServidorPorCaixaPostalServidoresSeq(Long cxt_seq){
		DetachedCriteria criteria= DetachedCriteria.forClass(AghCaixaPostalServidor.class);
		
		criteria.add(Restrictions.eq(AghCaixaPostalServidor.Fields.CXT_SEQ.toString(),cxt_seq));
	
		List<AghCaixaPostalServidor> aghCaixaPostalServidores=executeCriteria(criteria);
		
		for(AghCaixaPostalServidor aghCaixaPostalServidor:aghCaixaPostalServidores){
			this.remover(aghCaixaPostalServidor);			
		}
		
	}

	/**
	 * Estoria:40229, consulta c11
	 * @return List<AghCaixaPostal>
	 */
	public  List<AghCaixaPostalServidor> pesquisarAghCaixaPostalServidor(MamInterconsultas interconsulta){
		
		DetachedCriteria criteria= DetachedCriteria.forClass(AghCaixaPostalServidor.class, "XPS");
		
		criteria.add(Restrictions.eq("CXA." + AghCaixaPostalAplicacao.Fields.PARAMETRO_APLICACAO.toString(), "PAA"));
		
		criteria.add(Restrictions.eq("XPS." + AghCaixaPostalServidor.Fields.CXT_SEQ.toString(), interconsulta.getServidorValida()));
		criteria.add(Restrictions.eq("CXA." + AghCaixaPostalAplicacao.Fields.APLICACAO_CODIGO.toString(), "MAMF_LISTA_CONS_AMB"));		
		criteria.add(Restrictions.eq("PAA." + AghParametroAplicacao.Fields.NOME.toString(), "'GLOBAL.CG$IEO_SEQ'"));
//		criteria.add(Restrictions.eq("PAA." + AghParametroAplicacao.Fields.PARAMETROS.toString(), parametro)); TODO REVER
		
		return executeCriteria(criteria);
	}	
	
	
	/**Estoria #43449 - Melhoria #47255
	 * @param aghCaixaPostal
	 * @return List<AghCaixaPostalServidor>
	 */
	public List<AghCaixaPostalServidor> pesquisarAghCaixaPostalServidorVinculadasCaixaPostal(AghCaixaPostal aghCaixaPostal){
		DetachedCriteria criteria= DetachedCriteria.forClass(AghCaixaPostalServidor.class, "XPS");
		criteria.add(Restrictions.eq("XPS." + AghCaixaPostalServidor.Fields.CAIXA_POSTAL.toString(), aghCaixaPostal));
		
		return executeCriteria(criteria);
	}
}