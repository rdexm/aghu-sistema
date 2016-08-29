package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;

import br.gov.mec.aghu.dominio.DominioConvenioExameSituacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoNaoAplicavel;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.exames.vo.MedicoSolicitanteVO;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExtratoExameAp;
import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.AelNomenclaturaEspecs;
import br.gov.mec.aghu.model.AelNomenclaturaGenerics;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelTopografiaAparelhos;
import br.gov.mec.aghu.model.AelTopografiaAps;
import br.gov.mec.aghu.model.AelTopografiaSistemas;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.model.VAelApXPatologiaAghu;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class VAelApXPatologiaAghuDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelApXPatologiaAghu> {
	
	
	private static final long serialVersionUID = -3201219774575020556L;

	public List<MedicoSolicitanteVO> pesquisarMedicosSolicitantesVO(final String filtro, final String siglaConselhoProfissional) {
		final String hql = obterHQLBasicaMedicosSolicitantesVO(filtro, false);
		final String orderBy = " order by pes." + RapPessoasFisicas.Fields.NOME.toString() + " asc ";
		
		final Query query = createHibernateQuery(hql + orderBy);
		popularPrmsHQLMedicosSolicitantesVO(filtro, query, siglaConselhoProfissional);
		query.setMaxResults(100);
		
		@SuppressWarnings("unchecked")
		final List<MedicoSolicitanteVO> result = query.list();
		
		return result;
	}
	
	public Integer pesquisarMedicosSolicitantesVOCount(final String filtro, final String siglaConselhoProfissional) {
		final String hql = obterHQLBasicaMedicosSolicitantesVO(filtro, true);
		 
		final Query query = createHibernateQuery(hql);
		popularPrmsHQLMedicosSolicitantesVO(filtro, query, siglaConselhoProfissional);
		
		final Object result = query.uniqueResult();
		
		return Integer.valueOf(result.toString());
	}
	
	private String obterHQLBasicaMedicosSolicitantesVO(final String filtro, final boolean isCount) {
		final StringBuffer hql =  new StringBuffer(275);
		
		if(isCount){
			hql.append(" select count(*) from ");
			
		} else {
			hql.append(" select " )
			   .append("   new br.gov.mec.aghu.exames.vo.MedicoSolicitanteVO(")
			   .append("        pes.").append(RapPessoasFisicas.Fields.NOME.toString())
			   .append("      , vin.").append(RapVinculos.Fields.DESCRICAO.toString())
			   .append("      , ser.").append(RapServidores.Fields.MATRICULA.toString())
			   .append("      , ser.").append(RapServidores.Fields.CODIGO_VINCULO.toString())
			   .append("   ) ")
			   .append(" from ");
		}
		
		hql.append(RapVinculos.class.getName()).append(" as vin ")
		   .append(", ").append(RapServidores.class.getName()).append(" as ser ")
		   .append(", ").append(RapPessoasFisicas.class.getName()).append(" as pes ")
		   .append(", ").append(RapQualificacao.class.getName()).append(" as qlf ")
		   .append(", ").append(RapTipoQualificacao.class.getName()).append(" as tql ")
		   .append(", ").append(RapConselhosProfissionais.class.getName()).append(" as cpr ")
		   
		   .append(" where ")
		   
		   .append("     cpr.").append(RapConselhosProfissionais.Fields.SIGLA.toString()).append(" = :prmSigla ")
		   .append(" and tql.").append(RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL_CODIGO.toString()).append(" = cpr.").append(RapConselhosProfissionais.Fields.CODIGO.toString())
		   .append(" and qlf.").append(RapQualificacao.Fields.TIPO_QUALIFICACAO_CODIGO.toString()).append(" = tql.").append(RapTipoQualificacao.Fields.CODIGO.toString())
		   .append(" and pes.").append(RapPessoasFisicas.Fields.CODIGO.toString()).append(" = qlf.").append(RapQualificacao.Fields.PESSOA_CODIGO.toString())
		   .append(" and ser.").append(RapServidores.Fields.CODIGO_PESSOA_FISICA.toString()).append(" = pes.").append(RapPessoasFisicas.Fields.CODIGO.toString())
		   
		   .append(" and (ser.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString()).append(" is null or ")
		   		.append("  ser.").append(RapServidores.Fields.DATA_FIM_VINCULO.toString()).append(" > :prmDataFimVinculo ")
		   		.append(')')
		   
		   .append(" and ser.").append(RapServidores.Fields.IND_SITUACAO.toString()).append(" = :prmIndSituacao ")
		   .append(" and ser.").append(RapServidores.Fields.CODIGO_VINCULO.toString()).append(" = vin.").append(RapVinculos.Fields.CODIGO.toString())
		   ;

		if (!StringUtils.isEmpty(filtro)) {
			if(CoreUtil.isNumeroInteger(filtro)){
				hql.append(" and ser.").append(RapServidores.Fields.MATRICULA.toString()).append(" = :prmMatricula ");
				
			} else {
				hql.append(" and UPPER(pes.").append(RapPessoasFisicas.Fields.NOME.toString()).append(") like UPPER(:prmNome) ");
			}
		}

		return hql.toString();
	}
	
	private void popularPrmsHQLMedicosSolicitantesVO(final String filtro, final Query query, final String siglaConselhoProfissional) {
		query.setString("prmSigla", siglaConselhoProfissional);	
		query.setDate("prmDataFimVinculo", new Date());
		query.setParameter("prmIndSituacao", DominioSituacaoVinculo.A);
		
		if (!StringUtils.isEmpty(filtro)) {
			if(CoreUtil.isNumeroInteger(filtro)){
				query.setInteger("prmMatricula", Integer.valueOf(filtro));
				
			} else {
				query.setString("prmNome", "%".concat(filtro.toUpperCase()).concat("%"));
			}
		}
	}

	@SuppressWarnings({"unchecked"})
	public List<VAelApXPatologiaAghu> pesquisarVAelApXPatologiaAghu(final Integer firstResult, final Integer maxResults, String orderProperty,
			boolean asc, final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte,
			final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame, final Long numeroAp,
			final MedicoSolicitanteVO medicoSolic, final AelExameAp material, final DominioConvenioExameSituacao convenio, final DominioSimNao laudoImpresso,
			final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica,
			final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia,
			final boolean apenasNroAp) {
		

		final String hql = this.obterHQLBasica( residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp, 
											    medicoSolic, material, convenio, laudoImpresso, nomenclaturaGenerica, nomenclaturaEspecifica,
											    topografiaSistema, topografiaAparelho, neoplasiaMaligna, margemComprometida, biopsia, false);
		
		// Adiciona ordenação
		final String orderBy;
		final Query query;
		if (orderProperty != null && !VAelApXPatologiaAghu.Fields.CONVENIO.toString().equalsIgnoreCase(orderProperty) 
				&& !VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString().equalsIgnoreCase(orderProperty)) {
			orderBy = " order by " + orderProperty + (asc ? " asc " : " desc ");
			query = createHibernateQuery(hql + orderBy);
		} else {
			query = createHibernateQuery(hql);
		}

		popularPrmsHQL( residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp, medicoSolic, material, 
					    laudoImpresso, nomenclaturaGenerica, nomenclaturaEspecifica, topografiaSistema, topografiaAparelho, 
					    neoplasiaMaligna, margemComprometida, biopsia, query);

		if (convenio == null && !VAelApXPatologiaAghu.Fields.CONVENIO.toString().equalsIgnoreCase(orderProperty) 
				&& !VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString().equalsIgnoreCase(orderProperty) && !apenasNroAp) {
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults); 
		}
		
		return query.list();
	}

	public Object pesquisarVAelApXPatologiaAghuCount(final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte,
			final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame, final Long numeroAp,
			final MedicoSolicitanteVO medicoSolic, final AelExameAp material, final DominioConvenioExameSituacao convenio, 
			final DominioSimNao laudoImpresso,
			final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica,
			final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia) {

		final String hql = this.obterHQLBasica( residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp, medicoSolic, 
												material, convenio, laudoImpresso, nomenclaturaGenerica, nomenclaturaEspecifica, 
												topografiaSistema, topografiaAparelho, neoplasiaMaligna, margemComprometida, biopsia, true);
		
		final Query query = createHibernateQuery(hql);
		
		popularPrmsHQL( residenteResp, situacaoExmAnd, dtDe, dtAte, patologistaResp, exame, numeroAp, medicoSolic, material, 
					    laudoImpresso, nomenclaturaGenerica, nomenclaturaEspecifica, topografiaSistema, topografiaAparelho, 
					    neoplasiaMaligna, margemComprometida, biopsia, query);
		
		// Filtra resultados obtendo apenas registros de mesmo convenio
		if(convenio != null){
			@SuppressWarnings("unchecked")
			List<VAelApXPatologiaAghu> list = query.list();
			return list;

		} else {
			final Object result = query.uniqueResult();
			return Integer.valueOf(result.toString());
		}
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private String obterHQLBasica( final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd, final Date dtDe, final Date dtAte,
								   final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame, final Long numeroAp,
								   final MedicoSolicitanteVO medicoSolic, final AelExameAp material, final DominioConvenioExameSituacao convenio, 
								   final DominioSimNao laudoImpresso, 
								   final AelNomenclaturaGenerics nomenclaturaGenerica,
								   final AelNomenclaturaEspecs nomenclaturaEspecifica,
								   final AelTopografiaSistemas topografiaSistema,
								   final AelTopografiaAparelhos topografiaAparelho,
								   final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia,
								   final boolean isCount) {

//		final StringBuffer hql =  new StringBuffer(150);
//		
//		/* Caso convenio seja informado, deve retornar registros
//		 * para posterior filtro de convenio */
//		if(isCount && convenio == null){
//			hql.append(" select count( ").append("vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString()).append(" ) from ")
//			   .append(VAelApXPatologiaAghu.class.getName()).append(" as vael ");
//			
//		} else {
//			hql.append(" select " )
//			   .append("   new br.gov.mec.aghu.model.VAelApXPatologiaAghu(")
//			   .append("        vael.").append(VAelApXPatologiaAghu.Fields.LUM_SEQ.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.ATV_SEQ.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.ATD_SEQ.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_MATERIAIS.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_ETAPAS_LAUDO.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SITUACAO.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LU2_SEQ.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LU2_NOME.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SER_MATRICULA_RESP_LAUDO.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SER_VIN_CODIGO_RESP_LAUDO.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_IND_IMPRESSO.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.DATA_EXTRATO.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_PAC.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_MEDICO_SOLIC.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_MEDICO_SOLIC.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_RESIDENTE.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_RESIDENTE.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_PATOLOGISTA.toString())
//			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_PATOLOGISTA.toString())
//			   .append("   ) ")
//			.append(" from ").append(VAelApXPatologiaAghu.class.getName()).append(" as vael ");
//		}
//		
//		hql.append(" where 1=1 ");
//		
//		if (numeroAp != null) {
//			hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString()).append(" = :prmNumeroAp ");
//					
//		} else {
//			hql.append( " and exists  ( ")
//						 .append("  select lu5.").append(AelExtratoExameAp.Fields.LUX_SEQ.toString())
//						 .append("   from ").append(AelExtratoExameAp.class.getName()).append(" as lu5 ")
//						 .append("   where lu5.").append(AelExtratoExameAp.Fields.LUX_SEQ.toString()).append(" = ")
//						 					.append(" vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//						 .append("      and lu5.").append(AelExtratoExameAp.Fields.CRIADO_EM.toString()).append(" >= :prmCriadoEmLu5De ")
//						 .append("      and lu5.").append(AelExtratoExameAp.Fields.CRIADO_EM.toString()).append(" <= :prmCriadoEmLu5Ate ")
//						 .append(") ");
//			
//			if (exame != null) {
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LU2_SEQ.toString()).append(" = :prmExame ");			
//			}  
//			
//			
//			// defaul where qdo Exames em Andamento
//			if(DominioSituacaoExamePatologia.EA.equals(situacaoExmAnd)){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_ETAPAS_LAUDO.toString()).append(" in (:prmExamesEmAndamento) ");
//			}
//			
//			// Residentes Responsáveis
//			if(residenteResp != null){
//				hql.append( " and vael.").append(VAelApXPatologiaAghu.Fields.LUM_SEQ.toString())
//				   .append("        in ( select lo5.").append(AelApXPatologista.Fields.LUM_SEQ.toString())
//				   			  .append("   from ").append(AelPatologista.class.getName()).append(" as lui, ")
//				   			  .append(       AelApXPatologista.class.getName()).append(" as lo5 ")
//				   			  .append("   where  lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString()).append(" = :prmResidenteResp ")
//				   			  .append("      and lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString())
//				   			  .append("         = lui.").append(AelPatologista.Fields.SEQ.toString())
//				   			  .append(" ) ");
//			}
//			
//			// Patologistas Responsáveis
//			if(patologistaResp != null){
//				hql.append( " and vael.").append(VAelApXPatologiaAghu.Fields.LUM_SEQ.toString())
//				   .append("        in ( select lo5.").append(AelApXPatologista.Fields.LUM_SEQ.toString())
//				   			  .append("   from ").append(AelPatologista.class.getName()).append(" as lui, ")
//				   			  .append(       AelApXPatologista.class.getName()).append(" as lo5 ")
//				   			  .append("   where  lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString()).append(" = :prmPatologistaResp ")
//				   			  .append("      and lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString())
//				   			  .append("         = lui.").append(AelPatologista.Fields.SEQ.toString())
//				   			  .append(" ) ");
//			}
//			
//			// Exames em Andamento 
//			if(situacaoExmAnd != null && !DominioSituacaoExamePatologia.EA.equals(situacaoExmAnd)){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_ETAPAS_LAUDO.toString()).append(" = :prmSituacaoExmAnd ");
//			}
//			
//			if(material != null && material.getMateriais() != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_MATERIAIS.toString()).append(" = :prmMaterial ");
//			}
//			
//			if(medicoSolic != null && medicoSolic.getMatricula() != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_MEDICO_SOLIC.toString()).append(" = :prmMedicoSolic ");
//			}
//			
//			if(laudoImpresso != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_IND_IMPRESSO.toString()).append(" = :prmIndImpresso");
//			}
//			
//			//Nomenclatura Genérica
//			if(nomenclaturaGenerica != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//				   .append("        in ( select lo6.").append(AelNomenclaturaAps.Fields.LUX_SEQ.toString())
//				   			  .append("   from ").append(AelNomenclaturaAps.class.getName()).append(" as lo6, ")
//				   			  .append(       		     AelNomenclaturaEspecs.class.getName()).append(" as lue ")
//				   			  .append("   where  lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString()).append(" = :prmAelNomenclaturaEspecs ")
//				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString())
//				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_LUG_SEQ.toString())
//				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.SEQP.toString())
//				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_SEQP.toString())
//				   			  .append(" ) ");
//								
//								/*
//					v_where := v_where || ' AND lux_seq IN
//							(SELECT lo6.lux_seq
//				 			   FROM ael_nomenclatura_aps    lo6,
//								  ael_nomenclatura_especs lue
//							  WHERE lue.lug_seq = '||:QMS$CTRL.GENERICA ||
//							  ' AND lue.lug_seq = lo6.lue_lug_seq ' ||
//				 			  ' AND lue.seqp	  = lo6.lue_seqp )';
//								 */
//
//			}
//			
//			// Nomenclatura Específica
//			if(nomenclaturaEspecifica != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//				   .append("        in ( select lo6.").append(AelNomenclaturaAps.Fields.LUX_SEQ.toString())
//				   			  .append("   from ").append(AelNomenclaturaAps.class.getName()).append(" as lo6, ")
//				   			  .append(       		     AelNomenclaturaEspecs.class.getName()).append(" as lue ")
//				   			  .append("   where  lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString()).append(" = :prmEspecificaLugSeq ")
//				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.SEQP.toString()).append(" = :prmEspecificaSeqP ")
//				   			  
//				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString())
//				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_LUG_SEQ.toString())
//				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.SEQP.toString())
//				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_SEQP.toString())
//				   			  .append(" ) ");
//								
//						/*
//						v_where := v_where || ' AND lux_seq IN
//								(SELECT lo6.lux_seq
//					 			   FROM ael_nomenclatura_aps    lo6,
//									  ael_nomenclatura_especs lue
//								  WHERE lue.lug_seq = '||SUBSTR(:QMS$CTRL.ESPECIFICA,1,5) ||
//								  ' AND lue.seqp    = '||SUBSTR(:QMS$CTRL.ESPECIFICA,6,4) ||
//								  ' AND lue.lug_seq = lo6.lue_lug_seq ' ||
//					 			  ' AND lue.seqp	  = lo6.lue_seqp )';
//					 */
//
//			}
//			
//			// Topografia Sistema
//			if(topografiaSistema != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//				   .append("        in ( select lo7.").append(AelTopografiaAps.Fields.LUX_SEQ.toString())
//				   			  .append("   from ").append(AelTopografiaAps.class.getName()).append(" as lo7, ")
//				   			  .append(       		     AelTopografiaAparelhos.class.getName()).append(" as lua ")
//				   			  .append("   where  lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString()).append(" = :prmTopoSis ")
//				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString())
//				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_LUT_SEQ.toString())
//				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.SEQP.toString())
//				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_SEQP.toString())
//				   			  .append(" ) ");
//			
//				/*
//				 v_where := v_where || ' AND lux_seq IN
//										(SELECT lo7.lux_seq
//							 			   FROM ael_topografia_aps       lo7,
//											  ael_topografia_aparelhos lua
//										  WHERE lua.lut_seq = '||:QMS$CTRL.TOPO_SIS ||
//										  ' AND lua.lut_seq = lo7.lua_lut_seq ' ||
//							 			  ' AND lua.seqp	  = lo7.lua_seqp )';
//				 */
//			}
//			
//			// Topografia Aparelho
//			if(topografiaAparelho != null){
//				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//				   .append("        in ( select lo7.").append(AelTopografiaAps.Fields.LUX_SEQ.toString())
//				   			  .append("   from ").append(AelTopografiaAps.class.getName()).append(" as lo7, ")
//				   			  .append(       		     AelTopografiaAparelhos.class.getName()).append(" as lua ")
//				   			  .append("   where  lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString()).append(" = :prmAparelhoLutSeq ")
//				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.SEQP.toString()).append(" = :prmAparelhoSeqP ")
//				   			  
//				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString())
//				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_LUT_SEQ.toString())
//				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.SEQP.toString())
//				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_SEQP.toString())
//				   			  .append(" ) ");
//			
//				/*
//				 v_where := v_where || ' AND lux_seq IN
//							(SELECT lo7.lux_seq
//				 			   FROM ael_topografia_aps       lo7,
//								  ael_topografia_aparelhos lua
//							  WHERE lua.lut_seq = '||SUBSTR(:QMS$CTRL.APARELHO,1,5) ||
//							  ' AND lua.seqp    = '||SUBSTR(:QMS$CTRL.APARELHO,6,4) ||
//							  ' AND lua.lut_seq = lo7.lua_lut_seq ' ||
//				 			  ' AND lua.seqp	  = lo7.lua_seqp )';
//				 */
//			}
//
//			// Neoplasia Maligna
//			if(neoplasiaMaligna != null){
//					hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//					   .append("        in ( select lun.").append(AelDiagnosticoAps.Fields.LUX_SEQ.toString())
//					   			  .append("   from ").append(AelDiagnosticoAps.class.getName()).append(" as lun ")
//					   			  .append("   where  lun.").append(AelDiagnosticoAps.Fields.NEOPLASIA_MALIGNA.toString()).append(" = :prmNeoplasiaMaligna ")
//					   			  .append(" ) ");
//			}
//			
//			// Margem Comprometida
//			if(margemComprometida != null){
//					hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//					   .append("        in ( select lun.").append(AelDiagnosticoAps.Fields.LUX_SEQ.toString())
//					   			  .append("   from ").append(AelDiagnosticoAps.class.getName()).append(" as lun ")
//					   			  .append("   where  lun.").append(AelDiagnosticoAps.Fields.MARGEM_COMPROMETIDA.toString()).append(" = :prmMargemComprometida ")
//					   			  .append(" ) ");
//					
//			}
//			
//			// Biopsia
//			if(biopsia != null){
//					hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
//					   .append("        in ( select lun.").append(AelDiagnosticoAps.Fields.LUX_SEQ.toString())
//					   			  .append("   from ").append(AelDiagnosticoAps.class.getName()).append(" as lun ")
//					   			  .append("   where  lun.").append(AelDiagnosticoAps.Fields.BIOPSIA.toString()).append(" = :prmBiopsia ")
//					   			  .append(" ) ");
//					
//			}
//		}			
//		
//		return hql.toString();
		
		final StringBuffer hql =  new StringBuffer(150);
		
		/* Caso convenio seja informado, deve retornar registros
		 * para posterior filtro de convenio */
		if(isCount && convenio == null){
			hql.append(" select count( ").append("vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString()).append(" ) from ")
			   .append(VAelApXPatologiaAghu.class.getName()).append(" as vael ");
			
		} else {
			hql.append(" select " )
			   .append("   new br.gov.mec.aghu.model.VAelApXPatologiaAghu(")
			   .append("        vael.").append(VAelApXPatologiaAghu.Fields.LUM_SEQ.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.ATV_SEQ.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.ATD_SEQ.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_MATERIAIS.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_ETAPAS_LAUDO.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SITUACAO.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LU2_SEQ.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LU2_NOME.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SER_MATRICULA_RESP_LAUDO.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_SER_VIN_CODIGO_RESP_LAUDO.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.LUX_IND_IMPRESSO.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.DATA_EXTRATO.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_PAC.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_MEDICO_SOLIC.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_MEDICO_SOLIC.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_RESIDENTE.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_RESIDENTE.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.NOME_PATOLOGISTA.toString())
			   .append("      , vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_PATOLOGISTA.toString())
			   .append("   ) ")
			.append(" from ").append(VAelApXPatologiaAghu.class.getName()).append(" as vael ");
		}
		
		hql.append(" where 1=1 ");
		
		if (numeroAp != null && exame != null) {
			hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString()).append(" = :prmNumeroAp ");
			hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LU2_SEQ.toString()).append(" = :prmExame ");			
		} else {
			hql.append( " and exists  ( ")
						 .append("  select lu5.").append(AelExtratoExameAp.Fields.LUX_SEQ.toString())
						 .append("   from ").append(AelExtratoExameAp.class.getName()).append(" as lu5 ")
						 .append("   where lu5.").append(AelExtratoExameAp.Fields.LUX_SEQ.toString()).append(" = ")
						 					.append(" vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
						 .append("      and lu5.").append(AelExtratoExameAp.Fields.CRIADO_EM.toString()).append(" >= :prmCriadoEmLu5De ")
						 .append("      and lu5.").append(AelExtratoExameAp.Fields.CRIADO_EM.toString()).append(" <= :prmCriadoEmLu5Ate ")
						 .append(") ");
			
			if (exame != null) {
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LU2_SEQ.toString()).append(" = :prmExame ");			
			} else if (numeroAp != null) {
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUM_NUMERO_AP.toString()).append(" = :prmNumeroAp ");
			} 
			
			
			// defaul where qdo Exames em Andamento
			if(DominioSituacaoExamePatologia.EA.equals(situacaoExmAnd)){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_ETAPAS_LAUDO.toString()).append(" in (:prmExamesEmAndamento) ");
			}
			
			// Residentes Responsáveis
			if(residenteResp != null){
				hql.append( " and vael.").append(VAelApXPatologiaAghu.Fields.LUM_SEQ.toString())
				   .append("        in ( select lo5.").append(AelApXPatologista.Fields.LUM_SEQ.toString())
				   			  .append("   from ").append(AelPatologista.class.getName()).append(" as lui, ")
				   			  .append(       AelApXPatologista.class.getName()).append(" as lo5 ")
				   			  .append("   where  lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString()).append(" = :prmResidenteResp ")
				   			  .append("      and lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString())
				   			  .append("         = lui.").append(AelPatologista.Fields.SEQ.toString())
				   			  .append(" ) ");
			}
			
			// Patologistas Responsáveis
			if(patologistaResp != null){
				hql.append( " and vael.").append(VAelApXPatologiaAghu.Fields.LUM_SEQ.toString())
				   .append("        in ( select lo5.").append(AelApXPatologista.Fields.LUM_SEQ.toString())
				   			  .append("   from ").append(AelPatologista.class.getName()).append(" as lui, ")
				   			  .append(       AelApXPatologista.class.getName()).append(" as lo5 ")
				   			  .append("   where  lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString()).append(" = :prmPatologistaResp ")
				   			  .append("      and lo5.").append(AelApXPatologista.Fields.LUI_SEQ.toString())
				   			  .append("         = lui.").append(AelPatologista.Fields.SEQ.toString())
				   			  .append(" ) ");
			}
			
			// Exames em Andamento 
			if(situacaoExmAnd != null && !DominioSituacaoExamePatologia.EA.equals(situacaoExmAnd)){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_ETAPAS_LAUDO.toString()).append(" = :prmSituacaoExmAnd ");
			}
			
			if(material != null && material.getMateriais() != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_MATERIAIS.toString()).append(" = :prmMaterial ");
			}
			
			if(medicoSolic != null && medicoSolic.getMatricula() != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.MATRICULA_MEDICO_SOLIC.toString()).append(" = :prmMedicoSolic ");
			}
			
			if(laudoImpresso != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_IND_IMPRESSO.toString()).append(" = :prmIndImpresso");
			}
			
			//Nomenclatura Genérica
			if(nomenclaturaGenerica != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
				   .append("        in ( select lo6.").append(AelNomenclaturaAps.Fields.LUX_SEQ.toString())
				   			  .append("   from ").append(AelNomenclaturaAps.class.getName()).append(" as lo6, ")
				   			  .append(       		     AelNomenclaturaEspecs.class.getName()).append(" as lue ")
				   			  .append("   where  lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString()).append(" = :prmAelNomenclaturaEspecs ")
				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString())
				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_LUG_SEQ.toString())
				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.SEQP.toString())
				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_SEQP.toString())
				   			  .append(" ) ");
								
								/*
					v_where := v_where || ' AND lux_seq IN
							(SELECT lo6.lux_seq
				 			   FROM ael_nomenclatura_aps    lo6,
								  ael_nomenclatura_especs lue
							  WHERE lue.lug_seq = '||:QMS$CTRL.GENERICA ||
							  ' AND lue.lug_seq = lo6.lue_lug_seq ' ||
				 			  ' AND lue.seqp	  = lo6.lue_seqp )';
								 */

			}
			
			// Nomenclatura Específica
			if(nomenclaturaEspecifica != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
				   .append("        in ( select lo6.").append(AelNomenclaturaAps.Fields.LUX_SEQ.toString())
				   			  .append("   from ").append(AelNomenclaturaAps.class.getName()).append(" as lo6, ")
				   			  .append(       		     AelNomenclaturaEspecs.class.getName()).append(" as lue ")
				   			  .append("   where  lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString()).append(" = :prmEspecificaLugSeq ")
				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.SEQP.toString()).append(" = :prmEspecificaSeqP ")
				   			  
				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.LUG_SEQ.toString())
				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_LUG_SEQ.toString())
				   			  .append("      and lue.").append(AelNomenclaturaEspecs.Fields.SEQP.toString())
				   			  .append("         = lo6.").append(AelNomenclaturaAps.Fields.LUE_SEQP.toString())
				   			  .append(" ) ");
								
						/*
						v_where := v_where || ' AND lux_seq IN
								(SELECT lo6.lux_seq
					 			   FROM ael_nomenclatura_aps    lo6,
									  ael_nomenclatura_especs lue
								  WHERE lue.lug_seq = '||SUBSTR(:QMS$CTRL.ESPECIFICA,1,5) ||
								  ' AND lue.seqp    = '||SUBSTR(:QMS$CTRL.ESPECIFICA,6,4) ||
								  ' AND lue.lug_seq = lo6.lue_lug_seq ' ||
					 			  ' AND lue.seqp	  = lo6.lue_seqp )';
					 */

			}
			
			// Topografia Sistema
			if(topografiaSistema != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
				   .append("        in ( select lo7.").append(AelTopografiaAps.Fields.LUX_SEQ.toString())
				   			  .append("   from ").append(AelTopografiaAps.class.getName()).append(" as lo7, ")
				   			  .append(       		     AelTopografiaAparelhos.class.getName()).append(" as lua ")
				   			  .append("   where  lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString()).append(" = :prmTopoSis ")
				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString())
				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_LUT_SEQ.toString())
				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.SEQP.toString())
				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_SEQP.toString())
				   			  .append(" ) ");
			
				/*
				 v_where := v_where || ' AND lux_seq IN
										(SELECT lo7.lux_seq
							 			   FROM ael_topografia_aps       lo7,
											  ael_topografia_aparelhos lua
										  WHERE lua.lut_seq = '||:QMS$CTRL.TOPO_SIS ||
										  ' AND lua.lut_seq = lo7.lua_lut_seq ' ||
							 			  ' AND lua.seqp	  = lo7.lua_seqp )';
				 */
			}
			
			// Topografia Aparelho
			if(topografiaAparelho != null){
				hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
				   .append("        in ( select lo7.").append(AelTopografiaAps.Fields.LUX_SEQ.toString())
				   			  .append("   from ").append(AelTopografiaAps.class.getName()).append(" as lo7, ")
				   			  .append(       		     AelTopografiaAparelhos.class.getName()).append(" as lua ")
				   			  .append("   where  lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString()).append(" = :prmAparelhoLutSeq ")
				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.SEQP.toString()).append(" = :prmAparelhoSeqP ")
				   			  
				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.LUT_SEQ.toString())
				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_LUT_SEQ.toString())
				   			  .append("      and lua.").append(AelTopografiaAparelhos.Fields.SEQP.toString())
				   			  .append("         = lo7.").append(AelTopografiaAps.Fields.LUA_SEQP.toString())
				   			  .append(" ) ");
			
				/*
				 v_where := v_where || ' AND lux_seq IN
							(SELECT lo7.lux_seq
				 			   FROM ael_topografia_aps       lo7,
								  ael_topografia_aparelhos lua
							  WHERE lua.lut_seq = '||SUBSTR(:QMS$CTRL.APARELHO,1,5) ||
							  ' AND lua.seqp    = '||SUBSTR(:QMS$CTRL.APARELHO,6,4) ||
							  ' AND lua.lut_seq = lo7.lua_lut_seq ' ||
				 			  ' AND lua.seqp	  = lo7.lua_seqp )';
				 */
			}
			
			// Neoplasia Maligna
			if(neoplasiaMaligna != null){
					hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
					   .append("        in ( select lun.").append(AelDiagnosticoAps.Fields.LUX_SEQ.toString())
					   			  .append("   from ").append(AelDiagnosticoAps.class.getName()).append(" as lun ")
					   			  .append("   where  lun.").append(AelDiagnosticoAps.Fields.NEOPLASIA_MALIGNA.toString()).append(" = :prmNeoplasiaMaligna ")
					   			  .append(" ) ");
			}
			
			// Margem Comprometida
			if(margemComprometida != null){
					hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
					   .append("        in ( select lun.").append(AelDiagnosticoAps.Fields.LUX_SEQ.toString())
					   			  .append("   from ").append(AelDiagnosticoAps.class.getName()).append(" as lun ")
					   			  .append("   where  lun.").append(AelDiagnosticoAps.Fields.MARGEM_COMPROMETIDA.toString()).append(" = :prmMargemComprometida ")
					   			  .append(" ) ");
					
			}
			
			// Biopsia
			if(biopsia != null){
					hql.append(" and vael.").append(VAelApXPatologiaAghu.Fields.LUX_SEQ.toString())
					   .append("        in ( select lun.").append(AelDiagnosticoAps.Fields.LUX_SEQ.toString())
					   			  .append("   from ").append(AelDiagnosticoAps.class.getName()).append(" as lun ")
					   			  .append("   where  lun.").append(AelDiagnosticoAps.Fields.BIOPSIA.toString()).append(" = :prmBiopsia ")
					   			  .append(" ) ");
					
			}
		}
		
		return hql.toString();
		
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	private void popularPrmsHQL(final AelPatologista residenteResp, final DominioSituacaoExamePatologia situacaoExmAnd, 
			final Date dtDe, final Date dtAte, final AelPatologista patologistaResp, final AelConfigExLaudoUnico exame, final Long numeroAp,
			final MedicoSolicitanteVO medicoSolic, final AelExameAp material,
			final DominioSimNao laudoImpresso, 
			final AelNomenclaturaGenerics nomenclaturaGenerica,
			final AelNomenclaturaEspecs nomenclaturaEspecifica,
			final AelTopografiaSistemas topografiaSistema,
			final AelTopografiaAparelhos topografiaAparelho,
			final DominioSimNao neoplasiaMaligna, final DominioSimNaoNaoAplicavel margemComprometida, final DominioSimNao biopsia, 
			Query query) {
		
		if (numeroAp != null && exame != null) {
			query.setLong("prmNumeroAp", numeroAp);
			query.setInteger("prmExame", exame.getSeq());
		} else {
			if (exame != null) {
				query.setInteger("prmExame", exame.getSeq());
			} else if (numeroAp != null) {
				query.setLong("prmNumeroAp", numeroAp);
			}
			
			query.setDate("prmCriadoEmLu5De", dtDe);
			query.setDate("prmCriadoEmLu5Ate", DateUtil.adicionaDias(dtAte, 1));

			// defaul where qdo Exames em Andamento
			if( DominioSituacaoExamePatologia.EA.equals(situacaoExmAnd)){
				final DominioSituacaoExamePatologia[] vls = {DominioSituacaoExamePatologia.RE, DominioSituacaoExamePatologia.MC, DominioSituacaoExamePatologia.DC};
				query.setParameterList("prmExamesEmAndamento", vls);
			}
			
			// Residentes Responsáveis
			if(residenteResp != null){
				query.setInteger("prmResidenteResp", residenteResp.getSeq());
			}

			// Patologistas Responsáveis
			if(patologistaResp != null){
				query.setInteger("prmPatologistaResp", patologistaResp.getSeq());
			}
			
			// Exames em Andamento 
			if(situacaoExmAnd != null && !DominioSituacaoExamePatologia.EA.equals(situacaoExmAnd)){
				query.setParameter("prmSituacaoExmAnd", situacaoExmAnd);
			}
			
			if(material != null && material.getMateriais() != null){
				query.setString("prmMaterial", material.getMateriais());
			}
			
			if(medicoSolic != null && medicoSolic.getMatricula() != null){
				query.setInteger("prmMedicoSolic", medicoSolic.getMatricula());
			}

			if(laudoImpresso != null){
				query.setParameter("prmIndImpresso", laudoImpresso.isSim());
			}
			
			//Nomenclatura Genérica
			if(nomenclaturaGenerica != null){
				query.setInteger("prmAelNomenclaturaEspecs", nomenclaturaGenerica.getSeq());
			}
			
			// Nomenclatura Específica
			if(nomenclaturaEspecifica != null){
				query.setInteger("prmEspecificaLugSeq", nomenclaturaEspecifica.getId().getLugSeq());
				query.setShort("prmEspecificaSeqP", nomenclaturaEspecifica.getId().getSeqp());
			}
			
			// Topografia Sistema
			if(topografiaSistema != null){
				query.setInteger("prmTopoSis", topografiaSistema.getSeq());
			}
			
			// Topografia Aparelho
			if(topografiaAparelho != null){
				query.setInteger("prmAparelhoLutSeq", topografiaAparelho.getId().getLutSeq());
				query.setShort("prmAparelhoSeqP", topografiaAparelho.getId().getSeqp());
			}
			

			// Neoplasia Maligna
			if(neoplasiaMaligna != null){
				query.setParameter("prmNeoplasiaMaligna", neoplasiaMaligna);
			}
			
			// Margem Comprometida
			if(margemComprometida != null){
				query.setParameter("prmMargemComprometida", margemComprometida);
			}
			
			//Biopsia
			if(biopsia != null){
				query.setParameter("prmBiopsia", biopsia);
			}
		}
	}
	
}