package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioGrupoProcedimento;
import br.gov.mec.aghu.dominio.DominioItemPendencia;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.vo.FatLogErrorVO;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciasInternacaoVO;
import br.gov.mec.aghu.faturamento.vo.PendenciaEncerramentoVO;
import br.gov.mec.aghu.faturamento.vo.PendenciasEncerramentoVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatLogErrorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatLogError> {
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = -6758837684590917838L;

	/**
	 * Remove os FatLogError de um módulo relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCthModulo(final Integer cthSeq, final DominioModuloCompetencia modulo){
		javax.persistence.Query query = createQuery("delete " + FatLogError.class.getName() + 
																	   " where " + FatLogError.Fields.CTH_SEQ.toString() + " = :cthSeq " +
																	   "   and " + FatLogError.Fields.MODULO.toString() +  " = :modulo ");
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("modulo", modulo.toString());
		return query.executeUpdate();
	}

	public Integer removerPorCthModuloDescricaoErro(Integer cthSeq, String modulo, String erro) {
	
		StringBuilder sql = new StringBuilder(500);
		
		sql.append("DELETE ").append(FatLogError.class.getName())
		   .append(" WHERE ").append(FatLogError.Fields.CTH_SEQ.toString()).append( " = :cthSeq  AND (").append(FatLogError.Fields.MODULO.toString())
		   .append(" = :modulo OR ").append(FatLogError.Fields.ERRO.toString())
		   	.append(" LIKE '").append(erro).append("' )");
		
		javax.persistence.Query query = createQuery(sql.toString());
		query.setParameter("cthSeq", cthSeq);
		query.setParameter("modulo", modulo);
		return query.executeUpdate();
	}
	
	/**
	 * Remove os FatLogError de um módulo relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param modulo
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorModulo(final DominioModuloCompetencia modulo){
		javax.persistence.Query query = createQuery("delete " + FatLogError.class.getName() + 
																	   " where " + FatLogError.Fields.MODULO.toString() +  " = :modulo ");
		query.setParameter("modulo", modulo.toString());
		return query.executeUpdate();
	}	
	
	/**
	 * Remove os FatLogError relacionados a uma conta hospitalar.
	 * 
	 * Feito com HQL por motivo de performance, já que a entidade não possui trigger de deleção
	 *  
	 * @param cthSeq conta hospitalar
	 * @return número de registros deletados
	 * @return
	 */
	public Integer removerPorCth(final Integer cthSeq){
		javax.persistence.Query query = createQuery("delete " + FatLogError.class.getName() + 
																	   " where " + FatLogError.Fields.CTH_SEQ.toString() + " = :cthSeq ");
		query.setParameter("cthSeq", cthSeq);
		return query.executeUpdate();
	}
	
	/**
	 * Método que permite a pesquisa de Logs de Erro do Faturamento<br />
	 * 
	 * @param cthSeq - número da conta
	 * @param pacCodigo - código do paciente
	 * @param ichSeqp - código de sequencia de item de erro
	 * @param erro - descrição do erro
	 * @param phiSeqItem1 - código PHI
	 * @param codItemSus1 - código SMM
	 * @param modulo - módulo a ser pesquisado
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 */
	public List<FatLogError> pesquisarFatLogError(final Integer cthSeq,  final Integer pacCodigo,
												  final Short ichSeqp,  final String  erro,
												  final Integer phiSeqItem1, final Long codItemSus1,
												  final DominioModuloCompetencia modulo,
												  final Integer firstResult,  final Integer maxResult,
												  final String orderProperty, final boolean asc) {
		return executeCriteria(getCriteriaPesquisa(cthSeq, pacCodigo, ichSeqp, erro, phiSeqItem1, codItemSus1, modulo), 
				firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método que realiza um count sob a pesquisa de Logs de Erro do Faturamento<br />
	 * 
	 * @param cthSeq - número da conta
	 * @param pacCodigo - código do paciente
	 * @param ichSeqp - código de sequencia de item de erro
	 * @param erro - descrição do erro
	 * @param phiSeqItem1 - código PHI
	 * @param codItemSus1 - código SMM
	 * @param modulo - módulo a ser pesquisado
	 */
	public Long pesquisarFatLogErrorCount( final Integer cthSeq,      final Integer pacCodigo,
											  final Short ichSeqp,       final String  erro,
											  final Integer phiSeqItem1, final Long codItemSus1,
											  final DominioModuloCompetencia modulo ){
		
		return executeCriteriaCount(getCriteriaPesquisa(cthSeq, pacCodigo, ichSeqp, erro, phiSeqItem1, codItemSus1, modulo));
	}
	
	/**
	 * Método que obtém um registro da tabela FatLogError<br />
	 * 
	 * @param seq - chave primária da tabela FatLogError
	 */
	public FatLogError obterFatLogError(final Integer seq){
		return (FatLogError) obterPorChavePrimaria(seq);
	}
	
	
	public FatLogErrorVO obterFatLogErrorVo(final Short iphPhoSeqItem1,     final Integer iphSeqItem1, 	   
											final Short iphPhoSeqItem2, 	final Integer iphSeqItem2, 
											final Short iphPhoSeqRealizado, final Integer iphSeqRealizado, 
											final Short iphPhoSeq,		    final Integer iphSeq ){
		
		final FatLogErrorVO fatLogErrorVO = getFatLogErrorVO();
		
		
		if(iphPhoSeqItem1 != null && iphSeqItem1 != null){
			fatLogErrorVO.setDsProcedimento4((String) executeCriteriaUniqueResult(getBasicCriteria(iphPhoSeqItem1, iphSeqItem1)));
		}

		if(iphPhoSeqItem2 != null && iphSeqItem2 != null){
			fatLogErrorVO.setDsProcedimento2((String) executeCriteriaUniqueResult(getBasicCriteria(iphPhoSeqItem2, iphSeqItem2)));
		}
			
		if(iphPhoSeqRealizado != null && iphSeqRealizado != null){
			fatLogErrorVO.setDsProcedimento3((String) executeCriteriaUniqueResult(getBasicCriteria(iphPhoSeqRealizado, iphSeqRealizado)));
		}
			
		if(iphPhoSeq != null && iphSeq != null){
			fatLogErrorVO.setDsProcedimentoSolicitado((String) executeCriteriaUniqueResult(getBasicCriteria(iphPhoSeq, iphSeq)));
		}
		
		return fatLogErrorVO;
	}
	
	protected DetachedCriteria obterCriteriaPorCthConteudoErro(String conteudoErro, Integer... cthSeq) {
		
		DetachedCriteria result = null;
		
		result = DetachedCriteria.forClass(FatLogError.class);
		result.add(Restrictions.in(FatLogError.Fields.CTH_SEQ.toString(), cthSeq));
		result.add(Restrictions.like(FatLogError.Fields.ERRO.toString(), conteudoErro, MatchMode.ANYWHERE));
		result.setProjection(Projections.property(FatLogError.Fields.CTH_SEQ.toString()));
		
		return result;
	}
	
	public List<Integer> listaCthSeqPorCthConteudoErro(String conteudoErro, Integer... listaCthSeq) {
		
		List<Integer> result = null;
		DetachedCriteria criteria = null;
		
		criteria = this.obterCriteriaPorCthConteudoErro(conteudoErro, listaCthSeq);
		result = this.executeCriteria(criteria);
		
		return result;
	}

	private DetachedCriteria getBasicCriteria(final Short phoSeq, final Integer seq) {
		return DetachedCriteria.forClass(FatItensProcedHospitalar.class)
			   .add(Restrictions.eq(FatItensProcedHospitalar.Fields.SEQ.toString(), seq))
			   .add(Restrictions.eq(FatItensProcedHospitalar.Fields.PHO_SEQ.toString(), phoSeq))
			   .setProjection( Projections.projectionList().add( Projections.property(FatItensProcedHospitalar.Fields.DESCRICAO.toString()) ) );
	}		
	
	/**
	 * Método para criar a criteria de pesquisa de Logs de Erro do Faturamento<br />
	 * 
	 * @param cthSeq - número da conta
	 * @param pacCodigo - código do paciente
	 * @param ichSeqp - código de sequencia de item de erro
	 * @param erro - descrição do erro
	 * @param phiSeqItem1 - código PHI
	 * @param codItemSus1 - código Item Sus 1
	 * @param modulo - módulo a ser pesquisado
	 */
	private DetachedCriteria getCriteriaPesquisa( final Integer cthSeq, final Integer pacCodigo, 
												  final Short ichSeqp,  final String  erro,
												  final Integer phiSeqItem1, final Long codItemSus1,
												  final DominioModuloCompetencia modulo){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatLogError.class);

		if (cthSeq != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.CTH_SEQ.toString(), cthSeq));
		}
		
		if (pacCodigo != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		
		if (ichSeqp != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.ICH_SEQP.toString(), ichSeqp));
		}
		
		if (erro != null && !("").equals(erro.trim())) {
			criteria.add(Restrictions.like(FatLogError.Fields.ERRO.toString(), erro, MatchMode.ANYWHERE));
		}

		if (phiSeqItem1 != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.PHI_SEQ_ITEM1.toString(), phiSeqItem1));
		}
		
		if (codItemSus1 != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.COD_ITEM_SUS1.toString(), codItemSus1));
		}
		
		if (modulo != null) {
			criteria.add(Restrictions.ilike(FatLogError.Fields.MODULO.toString(), modulo.toString(), MatchMode.EXACT));
		}

		return criteria;
	}

	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<LogInconsistenciasInternacaoVO> getLogsInconsistenciasInternacaoVO(
						final Date dtCriacaoIni, final Date dtCriacaoFim, final Date dtPrevia, 
						final Integer prontuario, final Integer cthSeq,
						final String inconsistencia, final String iniciaisPaciente, 
						final Boolean reapresentada, final DominioGrupoProcedimento grupoProcedimento) throws ApplicationBusinessException{
		
		
		final Long codTabela = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MEDICACAO_SEM_RELAC_TABELA_SUS).getVlrNumerico().longValue();
		 
		final StringBuffer sql = new StringBuffer(2300);
		
		sql.append("select distinct")
		   .append(" pac.nome as pacnome")
		   .append(", pac.prontuario as prontuario")
		   .append(", ler.cth_seq as cthseq")
		   .append(", ler.erro as erro")

		   .append(", iph1.cod_tabela as iphseqitem1")
		   .append(", iph1.descricao as descsus1")
		   
		   .append(", iph2.cod_tabela as iphseqitem2")
		   .append(", iph2.descricao as descsus2")
		   
		   .append(", iph3.cod_tabela as iphseqrealizado")
		   .append(", iph3.descricao as descitemreal")
		   
		   .append(", iph4.cod_tabela as iphseq")
		   .append(", iph4.descricao as descitemsol")
		   
		   .append(", ler.phi_seq_item1 as phiseqitem1")
		   .append(", phi1.descricao as descricaophi1")
		   
		   .append(", ler.phi_seq_item2 as phiseqitem2")
		   .append(", phi2.descricao as descricaophi2")
		   
		   .append(", cth.dt_int_administrativa as datainternacaoadministrativa")
		   .append(", cth.dt_alta_administrativa as dtaltaadministrativa")
		   .append(", cth.nro_aih  as nroaih")
		   .append(", msp.codigo_sus as codigosusmsp")
		   .append(", sia.codigo_sus as codigosussia")
		   .append(", ich.unf_seq as unfseq")
		   .append(", ich.dthr_realizado as dthrrealizado")
		   
		   .append(" from ")
		   
		   .append("     agh.fat_log_errors ler ")
		   .append("	 left  join agh.fat_contas_hospitalares      cth  on ler.cth_seq    = cth.seq")
		   .append("	 left  join agh.fat_itens_proced_hospitalar  iph1 on iph1.seq       = ler.iph_seq_item1     and iph1.pho_seq = ler.iph_pho_seq_item1")
		   .append("	 left  join agh.fat_itens_proced_hospitalar  iph2 on iph2.seq       = ler.iph_seq_item2     and iph2.pho_seq = ler.iph_pho_seq_item2")
		   .append("	 left  join agh.fat_itens_proced_hospitalar  iph3 on iph3.seq       = ler.iph_seq_realizado and iph3.pho_seq = ler.iph_pho_seq_realizado")
		   .append(" 	 left  join agh.fat_itens_proced_hospitalar  iph4 on iph4.seq       = ler.iph_seq           and iph4.pho_seq = ler.iph_pho_seq")
		   .append("	 left  join agh.fat_proced_hosp_internos     phi1 on phi1.seq       = ler.phi_seq_item1")
		   .append("	 left  join agh.fat_proced_hosp_internos     phi2 on phi2.seq       = ler.phi_seq_item2")
		   .append("	 inner join agh.fat_itens_conta_hospitalar   ich  on cth.seq 	    = ich.cth_seq  	 	 	and cth.phi_seq_realizado = ich.phi_seq")
		   .append("	 left  join agh.fat_situacoes_saida_paciente sia  on sia.msp_seq    = cth.sia_msp_seq       and cth.sia_seq  =  sia.seq")
		   .append("	 inner join agh.fat_motivos_saida_paciente   msp  on msp.seq        = sia.msp_seq")
		   .append("	 left  join agh.aip_pacientes 	             pac  on ler.pac_codigo = pac.codigo")
		   
		   .append(" where 1=1 ");
		   
		if(cthSeq != null){
			sql.append("	 and ler.cth_seq = :prm_cth_seq");
		}
		
		if(prontuario != null){
			sql.append("	 and pac.prontuario = :prm_prontuario");
		}
		
		sql.append("	 and ler.modulo = :prm_modulo")
		   .append("	 and cth.ind_situacao in  (:prm_ind_situacao)" )
		   .append("	 and (iph1.cod_tabela is null or  iph1.cod_tabela <> :prm_cod_tabela)");
		
		final String condicoes = getCondicoes( dtCriacaoIni, dtCriacaoFim, dtPrevia, prontuario, cthSeq, codTabela, 
										 	   inconsistencia, iniciaisPaciente, reapresentada, grupoProcedimento);
		
		if(condicoes != null){
			sql.append(condicoes);
		}

		if(StringUtils.isNotBlank(iniciaisPaciente)) {			
			sql.append(" and upper(substr(pac.nome,1,1)) in (:prm_iniciais_paciente)");
		}
		
		sql.append(" order by pac.nome, pac.prontuario ");
		
		final SQLQuery q = createSQLQuery(sql.toString());

		q.setParameterList("prm_ind_situacao", new String[] {DominioSituacaoConta.E.toString(), DominioSituacaoConta.F.toString()});
		q.setLong("prm_cod_tabela", codTabela);
		q.setString("prm_modulo", DominioModuloCompetencia.INT.toString());
		
		if(cthSeq!= null){ 		    
			q.setInteger("prm_cth_seq", cthSeq); 
		}
		if(prontuario != null){    
			q.setInteger("prm_prontuario", prontuario); 
		}
		if(dtCriacaoIni != null){
			q.setDate("p_criado_em_inicio", DateUtil.obterDataComHoraFinal(dtCriacaoIni));
		}
		if(dtCriacaoFim != null){ 	
			q.setDate("p_criado_em_fim", DateUtil.obterDataComHoraFinal(dtCriacaoFim)); 
		}
		if(dtPrevia != null){     	
			q.setDate("p_data_previa_ini", DateUtil.obterDataComHoraFinal(dtPrevia)); 
			q.setDate("p_data_previa_fim", DateUtil.adicionaDias(dtPrevia,1)); 
		}
//		if(inconsistencia != null && !StringUtils.isEmpty(inconsistencia)){ 
//			q.setString("p_erro",inconsistencia.toLowerCase());
//		}
		if(StringUtils.isNotBlank(iniciaisPaciente)) {
			
			final List<String> lst = new ArrayList<String>();
			for(char a : iniciaisPaciente.toCharArray()){
				lst.add(Character.toString(a));
			}
			
			q.setParameterList("prm_iniciais_paciente", lst);
		}
		
		final List<LogInconsistenciasInternacaoVO> result = q.addScalar("pacnome", StringType.INSTANCE)
													   .addScalar("prontuario",IntegerType.INSTANCE)
													   .addScalar("cthseq",IntegerType.INSTANCE)
													   .addScalar("erro",StringType.INSTANCE)
													   
													   .addScalar("iphseqitem1",IntegerType.INSTANCE)
													   .addScalar("descsus1",StringType.INSTANCE)
													   
													   .addScalar("iphseqitem2",IntegerType.INSTANCE)
													   .addScalar("descsus2",StringType.INSTANCE)
													   
													   .addScalar("iphseqrealizado",IntegerType.INSTANCE)
													   .addScalar("descitemreal",StringType.INSTANCE)
													   
													   .addScalar("iphseq",IntegerType.INSTANCE)
													   .addScalar("descitemsol",StringType.INSTANCE)
													   
													   .addScalar("phiseqitem1",IntegerType.INSTANCE)
													   .addScalar("descricaophi1",StringType.INSTANCE)
													   
													   .addScalar("phiseqitem2",IntegerType.INSTANCE)
													   .addScalar("descricaophi2",StringType.INSTANCE)
													   
													   .addScalar("datainternacaoadministrativa",TimestampType.INSTANCE)
													   .addScalar("dtaltaadministrativa",TimestampType.INSTANCE)
													   .addScalar("nroaih",LongType.INSTANCE)
													   .addScalar("codigosusmsp",ByteType.INSTANCE)
													   .addScalar("codigosussia",ByteType.INSTANCE)
													   .addScalar("unfseq",ShortType.INSTANCE)
													   
													   .addScalar("dthrrealizado",DateType.INSTANCE)
													   .setResultTransformer(Transformers.aliasToBean(LogInconsistenciasInternacaoVO.class)).list();
		   
		return result;
	}
	

	private String getCondicoes( final Date dtCriacaoIni, final Date dtCriacaoFim, final Date dtPrevia, 
								 final Integer prontuario, final Integer cthSeq, final Long codTabela, 
								 final String inconsistencia, final String iniciaisPaciente, final Boolean reapresentada, 
								 final DominioGrupoProcedimento grupoProcedimento) throws ApplicationBusinessException{
		

		final StringBuffer condicoes = new StringBuffer(200);
		
		if(dtCriacaoIni != null){
			condicoes.append(" and ler.criado_em >= :p_criado_em_inicio ");
		}
		
		if(dtCriacaoFim != null){
			condicoes.append(" and ler.criado_em <= :p_criado_em_fim ");
		}
		
		if(dtPrevia != null){
			condicoes.append(" and ler.data_previa >= :p_data_previa_ini ");
			condicoes.append(" and ler.data_previa <= :p_data_previa_fim ");
		}
		
		if(inconsistencia != null && !StringUtils.isEmpty(inconsistencia)){
			condicoes.append(" and lower(ler.erro) like '%").append(inconsistencia.toLowerCase()).append("%' ");
		}
		
		if(reapresentada != null && reapresentada){
			condicoes.append(" and cth.cth_seq_reapresentada is not null "); 
		} else {
			condicoes.append(" and cth.cth_seq_reapresentada is null ");
		}
		
		if(grupoProcedimento!= null){
			switch (grupoProcedimento) {
				case C: condicoes.append(" and phi1.pci_seq is not null "); 	  break;
				case D: condicoes.append(" and phi1.ped_seq is not null "); 	  break;
				case E: condicoes.append(" and phi1.ema_exa_sigla is not null "); break;
				case H: condicoes.append(" and phi1.phe_codigo is not null "); 	  break;
				case M: condicoes.append(" and phi1.mat_codigo is not null "); 	  break;
				case S: condicoes.append(" and phi1.csa_codigo is not null "); 	  break;
			}
		}
		
		return condicoes.toString();
	}
	
	private FatLogErrorVO getFatLogErrorVO(){
		return new FatLogErrorVO();
	}
	
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	
	public List<FatLogError> pesquisaFatLogErrorFatMensagensLog(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, final Short ichSeqp, final String erro,
			final Integer phiSeqItem1, final Long codItemSus1){
		
		List<FatLogError> lista = new ArrayList<FatLogError>();
		
		// cria criteria e define alias
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLogError.class, "FLE");
		criteria.createAlias(FatLogError.Fields.FAT_MENSGAEM_LOG.toString(), "FML");
		criteria.createAlias("FLE."+FatLogError.Fields.CONTAS_HOSPITALARES.toString(), "CTH", JoinType.LEFT_OUTER_JOIN);
		
		//Adiciona critérios
		criteria.add(Restrictions.eqProperty("FLE."+FatLogError.Fields.COD_MSG.toString(),"FML."+FatMensagemLog.Fields.CODIGO.toString()));
		criteria.add(Restrictions.eq("FLE."+FatLogError.Fields.MODULO.toString(), DominioModuloMensagem.INT.toString()));
		criteria.add(Restrictions.eq("CTH."+FatContasHospitalares.Fields.SEQ.toString(), contaHospitalar));
		
		if(administrarUnidadeFuncionalInternacao){
			criteria.add(Restrictions.eq("FML."+FatMensagemLog.Fields.IND_SECRETARIO.toString(), "A"));
		}
		
		if(situacao!=null){//filtro
			criteria.add(Restrictions.eq("FML."+FatMensagemLog.Fields.SITUACAO.toString(), situacao));
		}	
		
		if (ichSeqp != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.ICH_SEQP.toString(), ichSeqp));
		}

		if (erro != null && !("").equals(erro.trim())) {
			criteria.add(Restrictions.ilike(FatLogError.Fields.ERRO.toString(), erro, MatchMode.ANYWHERE));
		}

		if (phiSeqItem1 != null) {
			criteria.add(Restrictions.eq(
					FatLogError.Fields.PHI_SEQ_ITEM1.toString(), phiSeqItem1));
		}

		if (codItemSus1 != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.COD_ITEM_SUS1.toString(), codItemSus1));
		}
		
		criteria.addOrder(Order.asc("FML."+FatMensagemLog.Fields.SITUACAO.toString()));
		criteria.addOrder(Order.asc("FLE."+FatLogError.Fields.ERRO.toString()));
		criteria.addOrder(Order.asc("FLE."+FatLogError.Fields.PHI_SEQ_ITEM1.toString()));
		
		lista = executeCriteria(criteria);
		
		return lista;
	}
	
	public Long pesquisaFatLogErrorFatMensagensLogCount(Integer contaHospitalar,DominioSituacaoMensagemLog situacao,boolean administrarUnidadeFuncionalInternacao, final Short ichSeqp, final String erro,
			final Integer phiSeqItem1, final Long codItemSus1){
		
		// cria criteria e define alias
		DetachedCriteria criteria = DetachedCriteria.forClass(FatLogError.class, "FLE");
		criteria.createAlias(FatLogError.Fields.FAT_MENSGAEM_LOG.toString(), "FML");
		criteria.createAlias("FLE."+FatLogError.Fields.CONTAS_HOSPITALARES.toString(), "CTH", JoinType.LEFT_OUTER_JOIN);
		
		//Adiciona critérios
		criteria.add(Restrictions.eqProperty("FLE."+FatLogError.Fields.COD_MSG.toString(),"FML."+FatMensagemLog.Fields.CODIGO.toString()));
		criteria.add(Restrictions.eq("FLE."+FatLogError.Fields.MODULO.toString(), DominioModuloMensagem.INT.toString()));
		criteria.add(Restrictions.eq("CTH."+FatContasHospitalares.Fields.SEQ.toString(), contaHospitalar));
		
		if(administrarUnidadeFuncionalInternacao){
			criteria.add(Restrictions.eq("FML."+FatMensagemLog.Fields.IND_SECRETARIO.toString(), "A"));
		}
		
		if(situacao!=null){//filtro
			criteria.add(Restrictions.eq("FML."+FatMensagemLog.Fields.SITUACAO.toString(), situacao));
		}	
		
		if (ichSeqp != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.ICH_SEQP.toString(), ichSeqp));
		}

		if (erro != null && !("").equals(erro.trim())) {
			criteria.add(Restrictions.ilike(FatLogError.Fields.ERRO.toString(), erro, MatchMode.ANYWHERE));
		}

		if (phiSeqItem1 != null) {
			criteria.add(Restrictions.eq(
					FatLogError.Fields.PHI_SEQ_ITEM1.toString(), phiSeqItem1));
		}

		if (codItemSus1 != null) {
			criteria.add(Restrictions.eq(FatLogError.Fields.COD_ITEM_SUS1.toString(), codItemSus1));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public StringBuffer getPendenciasQuery(StringBuffer SQL) throws ApplicationBusinessException{
		final String PS1 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LOG_ERRO_NUM_NAO_INFORMADO).getVlrTexto();
		final String PS2 = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LOG_ERRO_SEM_NOTA_FISCAL).getVlrTexto();
		SQL.append("SELECT f.cth_seq                 AS conta, \n");
		SQL.append("  f.iph_pho_seq_item1 || '-' || f.iph_seq_item1   AS labelItem1, \n");
		SQL.append("  f.iph_pho_seq_item2 || '-' || f.iph_seq_item2   AS labelItem2, \n");
		SQL.append("  f.iph_pho_seq_realizado || '-' || f.iph_seq_realizado   AS labelRealizado, \n");
		SQL.append("  f.iph_pho_seq  || '-' || f.iph_seq   AS labelSolicitado, \n");
		SQL.append("  f.ich_seqp                     AS itemConta, \n");
		SQL.append("  f.erro                         AS erro, \n");
		SQL.append("  f.prontuario || '-' || p.nome  AS paciente, \n");
		SQL.append("  f.prontuario  				 AS prontuario, \n");
		SQL.append("  f.iph_pho_seq_item1            AS tabItem1, \n");
		SQL.append("  f.iph_seq_item1                AS seqItem1, \n");
		SQL.append("  i1.descricao                   AS descricaoItem1, \n");
		SQL.append("  f.cod_item_sus_1               AS susItem1, \n");
		SQL.append("  f.phi_seq_item1                AS hcpaItem1, \n");
		SQL.append("  f.iph_pho_seq_item2            AS tabItem2, \n");
		SQL.append("  f.iph_seq_item2                AS seqItem2, \n");
		SQL.append("  i2.descricao              	 AS descricaoItem2, \n");
		SQL.append("  f.cod_item_sus_2          	 AS susItem2, \n");
		SQL.append("  f.phi_seq_item2           	 AS hcpaItem2, \n");
		SQL.append("  f.iph_pho_seq_realizado   	 AS tabRealizado, \n");
		SQL.append("  f.iph_seq_realizado       	 AS seqRealizado, \n");
		SQL.append("  i3.descricao              	 AS descricaoRealizado, \n");
		SQL.append("  f.cod_item_sus_realizado  	 AS susRealizado, \n");
		SQL.append("  f.phi_seq_realizado       	 AS hcpaRealizado, \n");
		SQL.append("  f.iph_pho_seq            	 	 AS tabSolicitado, \n");
		SQL.append("  f.iph_seq                 	 AS seqSolicitado, \n");
		SQL.append("  i4.descricao              	 AS descricaoSolicitado, \n");
		SQL.append("  f.cod_item_sus_solicitado 	 AS susSolicitado, \n");
		SQL.append("  f.phi_seq                 	 AS hcpaSolicitado, \n");
		SQL.append("  f.criado_em               	 AS dtOperacao, \n");
		SQL.append("  f.programa                	 AS programa \n");
		SQL.append("FROM agh.fat_log_errors f \n");
		SQL.append("INNER JOIN agh.aip_pacientes p \n");
		SQL.append("ON p.codigo = f.pac_codigo \n");
		SQL.append("LEFT JOIN agh.fat_itens_proced_hospitalar i1 \n");
		SQL.append("ON i1.pho_seq = f.iph_pho_seq_item1 \n");
		SQL.append("AND i1.seq    = f.iph_seq_item1 \n");
		SQL.append("LEFT JOIN agh.fat_itens_proced_hospitalar i2 \n");
		SQL.append("ON i2.pho_seq = f.iph_pho_seq_item2 \n");
		SQL.append("AND i2.seq    = f.iph_seq_item2 \n");
		SQL.append("LEFT JOIN agh.fat_itens_proced_hospitalar i3 \n");
		SQL.append("ON i3.pho_seq = f.iph_pho_seq_realizado \n");
		SQL.append("AND i3.seq    = f.iph_seq_realizado \n");
		SQL.append("LEFT JOIN agh.fat_itens_proced_hospitalar i4 \n");
		SQL.append("ON i4.pho_seq = f.iph_pho_seq \n");
		SQL.append("AND i4.seq    = f.iph_seq \n");
		SQL.append("WHERE f.erro in ('"+PS1+"', '"+PS2+"') -- Observar o uso dos parâmetros \n");
		SQL.append("  and f.modulo = 'INT'\n");
		SQL.append("AND f.data_previa IS NULL \n");
		SQL.append("AND EXISTS \n");
		SQL.append("  (SELECT 1 \n");
		SQL.append("  FROM agh.fat_contas_hospitalares c \n");
		SQL.append("  WHERE c.ind_situacao IN ('A', 'F') \n");
		SQL.append("  AND c.seq             = f.cth_seq \n");
		SQL.append("  )");
		return SQL;
	}
	
	public StringBuffer getPendenciasClausulas(StringBuffer SQL,Integer conta,Short itemConta,String erro,Integer prontuario,Date dtOperacao,String programa,DominioItemPendencia item,Short tabItem,Integer seqItem,Long sus,Integer hcpa){
		if(conta != null){
			SQL.append(" and f.cth_seq = "+conta+" \n");
		}
		if(itemConta != null){
			SQL.append(" and f.ich_seqp = "+itemConta.intValue()+" \n");
		}
		if(StringUtils.isNotBlank(erro)){
			SQL.append(" and f.erro like '%"+erro+"%' \n");
		}
		if(prontuario != null){
			SQL.append(" and f.prontuario = "+prontuario+" \n");
		}
		if(dtOperacao != null){
			if(isOracle()){
				String data = DateUtil.obterDataFormatada(dtOperacao, "dd/MM/yyyy");
				Date date2 = DateUtil.adicionaDias(dtOperacao, 1);
				String data2 = DateUtil.obterDataFormatada(date2, "dd/MM/yyyy");
				SQL.append(" and f.criado_em BETWEEN '"+data+"' AND '"+data2+"' \n");
			}else{
				String data = DateUtil.obterDataFormatada(dtOperacao, "yyyy-MM-dd");
				SQL.append(" and f.criado_em BETWEEN '"+data+" 00:00:00' AND '"+data+" 23:59:59' \n");
			}
		}
		if(StringUtils.isNotBlank(programa)){
			SQL.append(" and f.programa like '%"+programa+"%' \n");
		}
		return SQL;
	}
	
	public StringBuffer getPendenciasItens(StringBuffer SQL,Integer conta,Short itemConta,String erro,Integer prontuario,Date dtOperacao,String programa,DominioItemPendencia item,Short tabItem,Integer seqItem,Long sus,Integer hcpa){
		if(DominioItemPendencia.I1.equals(item)){
			if(tabItem != null){
				SQL.append(" and f.iph_pho_seq_item1 = "+tabItem.intValue()+" \n");
			}
			if(seqItem != null){
				SQL.append(" and f.iph_seq_item1 = "+seqItem+" \n");
			}
			if(sus != null){
				SQL.append(" and f.cod_item_sus_1 = "+sus+" \n");
			}
			if(hcpa != null){
				SQL.append(" and f.phi_seq_item1 = "+hcpa+" \n");
			}
		}else if(DominioItemPendencia.I2.equals(item)){
			if(tabItem != null){
				SQL.append(" and f.iph_pho_seq_item2 = "+tabItem.intValue()+" \n");
			}
			if(seqItem != null){
				SQL.append(" and f.iph_seq_item2 = "+seqItem+" \n");
			}
			if(sus != null){
				SQL.append(" and f.cod_item_sus_2 = "+sus+" \n");
			}
			if(hcpa != null){
				SQL.append(" and f.phi_seq_item2 = "+hcpa+" \n");
			}
		}else if(DominioItemPendencia.RE.equals(item)){
			if(tabItem != null){
				SQL.append(" and f.iph_pho_seq_realizado = "+tabItem.intValue()+" \n");
			}
			if(seqItem != null){
				SQL.append(" and f.iph_seq_realizado = "+seqItem+" \n");
			}
			if(sus != null){
				SQL.append(" and f.cod_item_sus_realizado = "+sus+" \n");
			}
			if(hcpa != null){
				SQL.append(" and f.phi_seq_realizado = "+hcpa+" \n");
			}
		}else if(DominioItemPendencia.SO.equals(item)){
			if(tabItem != null){
				SQL.append(" and f.iph_pho_seq = "+tabItem.intValue()+" \n");
			}
			if(seqItem != null){
				SQL.append(" and f.iph_seq = "+seqItem+" \n");
			}
			if(sus != null){
				SQL.append(" and f.cod_item_sus_solicitado = "+sus+" \n");
			}
			if(hcpa != null){
				SQL.append(" and f.phi_seq = "+hcpa+" \n");
			}
		}
		return SQL;
	}

	public List<PendenciasEncerramentoVO> getPendenciasEncerramento(Integer conta,Short itemConta,String erro,Integer prontuario,Date dtOperacao,String programa,DominioItemPendencia item,Short tabItem,Integer seqItem,Long sus,Integer hcpa) throws ApplicationBusinessException{
		StringBuffer SQL = new StringBuffer(3800);
		SQL = getPendenciasQuery(SQL);
		SQL = getPendenciasClausulas(SQL,conta,itemConta,erro,prontuario,dtOperacao,programa,item,tabItem,seqItem,sus,hcpa);
		SQL = getPendenciasItens(SQL,conta,itemConta,erro,prontuario,dtOperacao,programa,item,tabItem,seqItem,sus,hcpa);
		
		final SQLQuery q = createSQLQuery(SQL.toString());
		final List<PendenciasEncerramentoVO> result = q.addScalar("conta",IntegerType.INSTANCE)
													   .addScalar("labelItem1",StringType.INSTANCE)
													   .addScalar("labelItem2",StringType.INSTANCE)
													   .addScalar("labelRealizado",StringType.INSTANCE)
													   .addScalar("labelSolicitado",StringType.INSTANCE)
													   .addScalar("itemConta",ShortType.INSTANCE)
													   .addScalar("erro",StringType.INSTANCE)
													   .addScalar("paciente", StringType.INSTANCE)
													   .addScalar("prontuario", IntegerType.INSTANCE)
													   .addScalar("tabItem1",ShortType.INSTANCE)
													   .addScalar("seqItem1",IntegerType.INSTANCE)
													   .addScalar("descricaoItem1", StringType.INSTANCE)
													   .addScalar("susItem1",LongType.INSTANCE)
													   .addScalar("hcpaItem1",IntegerType.INSTANCE)
													   .addScalar("tabItem2",ShortType.INSTANCE)
													   .addScalar("seqItem2",IntegerType.INSTANCE)
													   .addScalar("descricaoItem2", StringType.INSTANCE)
													   .addScalar("susItem2",LongType.INSTANCE)
													   .addScalar("hcpaItem2",IntegerType.INSTANCE)
													   .addScalar("tabRealizado",ShortType.INSTANCE)
													   .addScalar("seqRealizado",IntegerType.INSTANCE)
													   .addScalar("descricaoRealizado", StringType.INSTANCE)
													   .addScalar("susRealizado",LongType.INSTANCE)
													   .addScalar("hcpaRealizado",IntegerType.INSTANCE)
													   .addScalar("tabSolicitado",ShortType.INSTANCE)
													   .addScalar("seqSolicitado",IntegerType.INSTANCE)
													   .addScalar("descricaoSolicitado", StringType.INSTANCE)
													   .addScalar("susSolicitado",LongType.INSTANCE)
													   .addScalar("hcpaSolicitado",IntegerType.INSTANCE)
													   .addScalar("dtOperacao",TimestampType.INSTANCE)
													   .addScalar("programa",StringType.INSTANCE)
													   .setResultTransformer(Transformers.aliasToBean(PendenciasEncerramentoVO.class)).list();
		return result;
	}



	/**
	 * #2381 - C4
	 * @return
	 */
	public Date obterUltimaDataCriacaoFatLogError() {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatLogError.class);
		criteria.add(Restrictions.eq(FatLogError.Fields.MODULO.toString(), "AIH"));
		criteria.setProjection(Projections.max(FatLogError.Fields.CRIADO_EM.toString()));
		
		return (Date)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #2381 - C1
	 * @param criadoEm
	 * @param erro
	 * @return
	 */
	public List<PendenciaEncerramentoVO> pesquisarMensagensErroPorData(Date criadoEm) {
		StringBuilder sql = new StringBuilder(4000);
		queryMensagensErrorPorData(sql);
	
		SQLQuery query = createSQLQuery(sql.toString());

		if (criadoEm != null) {
			query.setTimestamp("p_criado_em", criadoEm);	
		}

		
		query.addScalar("cthseq",IntegerType.INSTANCE);
		query.addScalar("prontuario",IntegerType.INSTANCE);
		query.addScalar("nome",StringType.INSTANCE);
		query.addScalar("leito",StringType.INSTANCE);
		query.addScalar("dtIntAdm",DateType.INSTANCE);
		query.addScalar("dtAltAdm",DateType.INSTANCE);
		query.addScalar("nroAih",LongType.INSTANCE);
		query.addScalar("mspSia",StringType.INSTANCE);
		query.addScalar("intseq",IntegerType.INSTANCE);
		query.addScalar("erro",StringType.INSTANCE);
		query.addScalar("desdobr",StringType.INSTANCE);
		query.addScalar("phirealizado",IntegerType.INSTANCE);
		query.addScalar("cspcnvcodigo",ShortType.INSTANCE);
		query.addScalar("cspseq",ByteType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(PendenciaEncerramentoVO.class));		
		return query.list();
	}

	private void queryMensagensErrorPorData(StringBuilder sql) {
		sql.append("SELECT");
		sql.append(" DISTINCT");
		sql.append(" LER.CTH_SEQ as cthseq ");
		sql.append(" ,PAC.PRONTUARIO as prontuario ");
		sql.append(" ,PAC.NOME as nome ");
		sql.append(" ,  CASE WHEN INTER.LTO_LTO_ID IS NOT NULL THEN INTER.LTO_LTO_ID WHEN  INTER.LTO_LTO_ID IS NULL THEN ");
		sql.append(" CASE WHEN INTER.LTO_LTO_ID IS NULL ");
		sql.append("  THEN CAST(INTER.qrt_numero AS VARCHAR(20))");
		sql.append("  WHEN INTER.QRT_NUMERO IS NULL ");
		sql.append(" THEN UNF.ANDAR||UNF.IND_ALA ");
		sql.append(" END");
		sql.append(" END leito ");
		sql.append(" ,CTH.DT_INT_ADMINISTRATIVA as dtIntAdm");
		sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA as dtAltAdm");
		sql.append(" ,CTH.NRO_AIH as nroAih");
		sql.append(" ,LER.COD_ITEM_SUS_REALIZADO as mspSia");
		sql.append(" ,MAX(INTER.SEQ) as intseq");
		sql.append(" ,LER.ERRO as erro");
		sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END as desdobr");
		sql.append(" ,CTH.PHI_SEQ_REALIZADO as phirealizado");
		sql.append(" ,CTH.CSP_CNV_CODIGO as cspcnvcodigo");
		sql.append(" ,CTH.CSP_SEQ as cspseq");
		sql.append(" FROM agh.FAT_CONTAS_HOSPITALARES CTH");		
		sql.append(" INNER JOIN agh.FAT_CONTAS_INTERNACAO COI 	ON COI.CTH_SEQ = CTH.SEQ ");
		sql.append(" INNER JOIN agh.AIN_INTERNACOES  INTER ON INTER.SEQ = COI.INT_SEQ ");
		sql.append(" INNER JOIN agh.AIP_PACIENTES  PAC ON PAC.CODIGO = INTER.PAC_CODIGO ");
		sql.append(" LEFT JOIN agh.AGH_UNIDADES_FUNCIONAIS  UNF ON UNF.SEQ = INTER.UNF_SEQ");
		sql.append(" INNER JOIN agh.FAT_LOG_ERRORS  LER ON  CTH.SEQ = LER.CTH_SEQ");
		sql.append(" LEFT JOIN agh.FAT_SITUACOES_SAIDA_PACIENTE SIA");
		sql.append(" ON SIA.SEQ = CTH.SIA_SEQ");
		sql.append(" AND SIA.MSP_SEQ = CTH.SIA_MSP_SEQ");
		sql.append(" LEFT JOIN agh.FAT_MOTIVOS_SAIDA_PACIENTE MSP ON MSP.SEQ = SIA.MSP_SEQ");
		sql.append(" LEFT JOIN agh.V_FAT_ASSOCIACAO_PROCEDIMENTOS ASP");
		sql.append(" ON ASP.PHI_SEQ  = COALESCE(CTH.PHI_SEQ_REALIZADO, COALESCE(CTH.PHI_SEQ,1))");
		sql.append(" AND ASP.CPG_CPH_CSP_CNV_CODIGO = CTH.CSP_CNV_CODIGO");
		sql.append(" AND ASP.CPG_CPH_CSP_SEQ = CTH.CSP_SEQ");
		sql.append(" AND ASP.IPH_IND_SITUACAO = 'A' ");
		sql.append(" AND ASP.IPH_IND_TIPO_AIH5 <> 'S' ");		
		sql.append(" WHERE CTH.IND_SITUACAO IN('F', 'E') ");
		sql.append(" AND LER.MODULO = 'AIH' ");
		
		if(isOracle()) {
			sql.append(" AND TRUNC(LER.CRIADO_EM) = TRUNC(:p_criado_em)");
			
			sql.append(" GROUP BY");
			sql.append(" LER.CTH_SEQ");
			sql.append(" ,PAC.PRONTUARIO");
			sql.append(" ,PAC.NOME ");
			sql.append(" ,  CASE WHEN INTER.LTO_LTO_ID IS NOT NULL THEN INTER.LTO_LTO_ID WHEN  INTER.LTO_LTO_ID IS NULL THEN ");
			sql.append(" CASE WHEN INTER.LTO_LTO_ID IS NULL ");
			sql.append("  THEN CAST(INTER.qrt_numero AS VARCHAR(20))");
			sql.append("  WHEN INTER.QRT_NUMERO IS NULL ");
			sql.append(" THEN UNF.ANDAR||UNF.IND_ALA ");
			sql.append(" END");
			sql.append(" END");
			sql.append(" ,CTH.DT_INT_ADMINISTRATIVA" );
			sql.append(" ,CTH.DT_ALTA_ADMINISTRATIVA ");
			sql.append(" ,CTH.NRO_AIH ");
			sql.append(" ,LER.COD_ITEM_SUS_REALIZADO ");
			sql.append(" ,LER.ERRO ");
			sql.append(" ,CASE WHEN CTH.CTH_SEQ IS NULL THEN ' ' ELSE '*' END ");
			sql.append(" ,CTH.PHI_SEQ_REALIZADO ");
			sql.append(" ,CTH.CSP_CNV_CODIGO ");
			sql.append(" ,CTH.CSP_SEQ ");
		}else{
			sql.append(" AND DATE_TRUNC('DAY', LER.CRIADO_EM) = DATE_TRUNC('DAY', :p_criado_em)");
			
			sql.append(" GROUP BY");
			sql.append(" LER.CTH_SEQ");
			sql.append(" ,PAC.PRONTUARIO");
			sql.append(" ,nome");
			sql.append(" ,leito");
			sql.append(" ,dtIntAdm");
			sql.append(" ,dtAltAdm");
			sql.append(" ,nroAih");
			sql.append(" ,mspSia");
			sql.append(" ,erro");
			sql.append(" ,desdobr");
			sql.append(" ,phirealizado");
			sql.append(" ,cspcnvcodigo");
			sql.append(" ,cspseq");
		}

		sql.append(" ORDER BY cth.dt_alta_administrativa,  pac.nome");
	}



}