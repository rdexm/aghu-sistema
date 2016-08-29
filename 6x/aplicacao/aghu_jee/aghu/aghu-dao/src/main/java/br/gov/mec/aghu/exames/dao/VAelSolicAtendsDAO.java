package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioResponsavelColetaExames;
import br.gov.mec.aghu.dominio.DominioSimNaoRotina;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesFiltroVO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemHorarioAgendado;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelSolicAtends;

public class VAelSolicAtendsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VAelSolicAtends>{

	private static final long serialVersionUID = 7385159385574503573L;
	private final static String VSA = "vsa.";
	private final static String ATD = "ATD.";
	private final static String PAC = "PAC.";
	private final static String ISE = "ise.";
	private final static String SOE = "soe.";
	private final static String PUS = "pus.";
	private final static String TAE = "tae.";
	private final static String IHA = "iha.";

	/***
	 * Cria uma criteria para pesquisa para VO de solicitacoes de exame para coletas de emergencia
	 * @param unidadeExecutora
	 * @param dataCalculadaAparecimentoSolicitacao
	 * @param sitCodigo
	 * @return
	 * @throws BaseException
	 */
	public DetachedCriteria createPesquisaCriteriaVPL(AghUnidadesFuncionais unidadeExecutora, Date dataCalculadaAparecimentoSolicitacao, String sitCodigo) throws BaseException {

		// Criteria PRINCIPAL da View de Solicitacao Atendimentos - Alias VSA
		DetachedCriteria criteriaVsa= DetachedCriteria.forClass(VAelSolicAtends.class,VSA.substring(0,3));

		// Left outer join para obter os dados do atendimento/paciente
		criteriaVsa.createAlias(VSA + VAelSolicAtends.Fields.ATENDIMENTO.toString(), ATD.substring(0,3), JoinType.LEFT_OUTER_JOIN);
		criteriaVsa.createAlias(ATD + AghAtendimentos.Fields.PACIENTE.toString(), PAC.substring(0,3), JoinType.LEFT_OUTER_JOIN);
	
		// Projecoes da Criteria PRINCIPAL
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(VSA + VAelSolicAtends.Fields.SEQ.toString()),"numero");
		projectionList.add(Projections.property(VSA + VAelSolicAtends.Fields.ATD_SEQ.toString()),"atdSeq");
		projectionList.add(Projections.property(VSA + VAelSolicAtends.Fields.CRIADO_EM.toString()),"dataSolicitacao");
		projectionList.add(Projections.property(VSA + VAelSolicAtends.Fields.QRT_DESCRICAO.toString()),"quarto");
		projectionList.add(Projections.property(VSA + VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()),"leito");
		// Projecoes do Left outer join para obter os dados do atendimento/paciente
		projectionList.add(Projections.property(VSA + VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()),"prontuario");
		projectionList.add(Projections.property(PAC + AipPacientes.Fields.NOME.toString()),"pacienteDiversos");
		criteriaVsa.setProjection(projectionList);
		

		// Criteria SECUNDARIA de Item de Solicitacao de Exames - Alias ISE
		DetachedCriteria criteriaIse = DetachedCriteria.forClass(AelItemSolicitacaoExames.class,ISE.substring(0,3));
		// Alias para Solicitacao de Exame - Alias SOE1
		criteriaIse.createAlias(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), SOE.substring(0,3), JoinType.LEFT_OUTER_JOIN); 
		criteriaIse.setProjection(Projections.property(ISE + AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME_SEQ.toString())); 
		// Restricoes de ISE
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), sitCodigo));
		criteriaIse.add(Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora.getSeq()));
		criteriaIse.add(Restrictions.le(ISE + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString(), new Date(dataCalculadaAparecimentoSolicitacao.getTime())));

		// Restricoes aninhadas de ISE e SOE1
		Criterion criterion1 = Restrictions.ltProperty(SOE + AelSolicitacaoExames.Fields.CRIADO_EM.toString(), ISE + AelItemSolicitacaoExames.Fields.DTHR_PROGRAMADA.toString());
		//Criterion criterion2 = Restrictions.isNotNull(SOE + AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString()); // TODO
		//Criterion criterion3 = Restrictions.and(criterion1, criterion2);
		Criterion criterion4 = Restrictions.or(criterion1, Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(), DominioTipoColeta.U));
		criteriaIse.add(criterion4);

		// SubCriteria/SubConsulta (EXISTS) de Permissao de Unid. Solicao - Alias PUS
		DetachedCriteria subQueryPus = DetachedCriteria.forClass(AelPermissaoUnidSolic.class,PUS.substring(0,3));	
		subQueryPus.setProjection(Projections.property(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()));
		// Restricoes de PUS
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UFE_UNF.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ.toString()));
		subQueryPus.add(Property.forName(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_SOLICITANTE.toString()).eqProperty(SOE + AelSolicitacaoExames.Fields.UNF_SEQ.toString()));
		subQueryPus.add(Restrictions.eq(PUS + AelPermissaoUnidSolic.Fields.UNF_SEQ_AVISA.toString(), unidadeExecutora));
		// Restricoes aninhadas de PUS, ISE e SOE1
		Criterion criterion5 = Restrictions.eq(PUS + AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), DominioSimNaoRotina.S);
		Criterion criterion6 = Restrictions.in(PUS + AelPermissaoUnidSolic.Fields.IND_PERMITE_PROGRAMAR_EXAMES.toString(), new DominioSimNaoRotina[]{DominioSimNaoRotina.R,DominioSimNaoRotina.N});
		Criterion criterion7 = Restrictions.eq(ISE + AelItemSolicitacaoExames.Fields.TIPO_COLETA.toString(),DominioTipoColeta.U);
		Criterion criterion8 = Restrictions.and(criterion6, criterion7);
		subQueryPus.add(Restrictions.or(criterion5, criterion8));
		// Adiciona SubCriteria de Permissao de Unid. Solicao - PUS na criteria SECUNDARIA
		criteriaIse.add(Subqueries.exists(subQueryPus));

		// SubCriteria/SubConsulta (EXISTS) de Tipo de Amostras de Exame - Alias TAE
		DetachedCriteria subQueryTae = DetachedCriteria.forClass(AelTipoAmostraExame.class,TAE.substring(0,3));	
		subQueryTae.setProjection(Projections.property(AelTipoAmostraExame.Fields.NRO_AMOSTRAS.toString()));
		// Restricoes aninhadas de TAE - feitas fora na RN
		// Restricoes de TAE
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_EXA_SIGLA.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_EXAMES_SIGLA.toString()));
		subQueryTae.add(Property.forName(TAE + AelTipoAmostraExame.Fields.EMA_MAN_SEQ.toString()).eqProperty(ISE + AelItemSolicitacaoExames.Fields.AEL_MATERIAIS_ANALISES_SEQ.toString()));
		subQueryTae.add(Restrictions.in(TAE + AelTipoAmostraExame.Fields.RESPONSAVEL_COLETA.toString(), new DominioResponsavelColetaExames[]{DominioResponsavelColetaExames.C,DominioResponsavelColetaExames.S}));

		// Adiciona SubCriteria de  Tipo de Amostras de Exame - TAE na criteria SECUNDARIA
		criteriaIse.add(Subqueries.exists(subQueryTae));

		// In da criteria PRINCIPAL (alias VSA) na criteria SECUNDARIA (alias ISE) 
		criteriaVsa.add(Property.forName(VSA + VAelSolicAtends.Fields.SEQ.toString()).in(criteriaIse));

		// Ordena a lista
		criteriaVsa.addOrder(Order.asc(VSA + VAelSolicAtends.Fields.SEQ.toString()));

		return criteriaVsa;
	}


	/**
	 * Pesquisa para VO de solicitacoes de exame para coletas de emergencia
	 * @param unidadeExecutora
	 * @param dataCalculadaAparecimentoSolicitacao
	 * @param sitCodigo
	 * @return
	 * @throws BaseException
	 */
	public List<VAelSolicAtendsVO> pesquisarVPL(AghUnidadesFuncionais unidadeExecutora, Date dataCalculadaAparecimentoSolicitacao, String sitCodigo) throws BaseException {
		DetachedCriteria criteria = createPesquisaCriteriaVPL(unidadeExecutora, dataCalculadaAparecimentoSolicitacao, sitCodigo);
		criteria.setResultTransformer(Transformers.aliasToBean(VAelSolicAtendsVO.class));
		return executeCriteria(criteria);
	}

	
	
	/**
	 * Pesquisa para VO de solicitacoes de exame para coletas de emergencia em uma query unica evitando o estouro da transação.
	 * Chamado #79045.
	 * Construído com SQL nativo pois o incidente é imediato. Deverá ser feito Refactoring.
	 * @param unidadeExecutora
	 * @param dataCalculadaAparecimentoSolicitacao
	 * @param sitCodigo
	 * @return
	 * @throws BaseException
	 */
	public List<VAelSolicAtendsVO> pesquisarVPLAgregado(AghUnidadesFuncionais unidadeExecutora, Date dataCalculadaAparecimentoSolicitacao, String sitCodigo) throws BaseException {
		return pesquisaVPLAgregado(unidadeExecutora, dataCalculadaAparecimentoSolicitacao, sitCodigo);
	}

	private List<VAelSolicAtendsVO> pesquisaVPLAgregado(AghUnidadesFuncionais unidadeExecutora,
			Date dataCalculadaAparecimentoSolicitacao, String sitCodigo) {
		StringBuilder hql = new StringBuilder(7000);
		
		hql	
		.append(" SELECT ")
		.append(" distinct this_.seq            AS numero,")
		.append(" this_.atd_seq        AS atdSeq,")
		.append(" this_.criado_em      AS dataSolicitacao,")
		.append("		this_.qrt_descricao  AS quarto,")
		.append("		this_.atd_lto_lto_id AS leito,")
		.append("		this_.atd_prontuario AS prontuario,")
		.append("		pac2_.nome           AS pacienteDiversos,")
		.append(" CASE ")
		.append("	(select ")
		.append("		count(*)")
		.append("	from  ")
		.append("		agh.ael_solicitacao_exames  soe")
		.append("		inner join agh.agh_atendimentos atd on (soe.atd_seq = atd.seq)")
		.append("		inner join agh.ael_item_solicitacao_exames ise on (soe.seq = ise.soe_seq)")
		.append("		inner join AGH.AEL_TIPOS_AMOSTRA_EXAMES amo on (ise.ufe_ema_exa_sigla = AMO.EMA_EXA_SIGLA and ise.ufe_ema_man_seq= AMO.EMA_MAN_SEQ)")
		.append("	where ")
		.append("		soe_seq = this_.seq")
		.append("		and ISE.TIPO_COLETA = :tipoColeta")
		.append("		and AMO.RESPONSAVEL_COLETA = :coletador")
		.append("		and (amo.origem_atendimento = :todasOrigens")
		.append("			or amo.origem_atendimento = atd.origem)) ")
		.append("			WHEN  0")
		.append("			THEN ''")
		.append("			Else 'Urgente' ")
		.append(" END AS prioridade,")
		.append(" CASE ")
		.append("(SELECT count(*)")
		.append("         FROM   agh.ael_solicitacao_exames soe")
		.append("                INNER JOIN agh.agh_atendimentos atd ON ( soe.atd_seq = atd.seq )")
		.append("                INNER JOIN agh.ael_item_solicitacao_exames ise ON ( soe.seq = ise.soe_seq )")
		.append("               INNER JOIN agh.ael_tipos_amostra_exames amo ON ( ise.ufe_ema_exa_sigla = AMO.ema_exa_sigla")
		.append("                            AND ise.ufe_ema_man_seq = AMO.ema_man_seq )")
		.append("        WHERE  soe_seq = this_.seq")
		.append("               AND ISE.dthr_programada > soe.criado_em")
		.append("     AND ISE.dthr_programada <= :dataHora")
		.append("    and ise.UNF_SEQ_AVISA = :unidadeExecutora")
		.append("    and SIT_CODIGO = :sitCodigo")
		.append("               AND AMO.responsavel_coleta = :coletador")
		.append("                AND ( amo.origem_atendimento = :todasOrigens")
		.append("                       OR amo.origem_atendimento = atd.origem )")
		.append("                and exists (")
		.append("	     select")
		.append("		 pus_.UNF_SEQ ") 
		.append("	     from")
		.append("		 AGH.AEL_PERMISSAO_UNID_SOLICS pus_ ")
		.append("	     where")
		.append("		 pus_.UFE_EMA_EXA_SIGLA=ISE.UFE_EMA_EXA_SIGLA ")
		.append("		 and pus_.UFE_EMA_MAN_SEQ=ISE.UFE_EMA_MAN_SEQ ")
		.append("		 and pus_.UFE_UNF_SEQ=ISE.UFE_UNF_SEQ ")
		.append("		 and pus_.UNF_SEQ_AVISA=:unidadeExecutora")
		.append("	 )")
		.append("                	and exists (")
		.append("	    select")
		.append("		tae_.NRO_AMOSTRAS")
		.append("	    from")
		.append("		AGH.AEL_TIPOS_AMOSTRA_EXAMES tae_ ")
		.append("	    where")
		.append("		tae_.EMA_EXA_SIGLA=ISE.UFE_EMA_EXA_SIGLA ")
		.append("		and tae_.EMA_MAN_SEQ=ISE.UFE_EMA_MAN_SEQ ")
		.append("		and tae_.RESPONSAVEL_COLETA=:coletador")
		.append("	)")
		.append("	and ")
		.append("	((SELECT Count(*)")
		.append("	FROM   agh.ael_horario_rotina_coletas hor")
		.append("	WHERE  ")
		.append("		hor.unf_seq = :unidadeExecutora")
		.append("		AND hor.unf_seq_solicitante = :unidadeExecutora")
		.append("		AND hor.dia = (SELECT CASE")
		.append("                                     WHEN")
		.append("                           To_char(ise.dthr_programada,'D') = '1'")
		.append("                                   THEN 'DOM'")
		.append("                                     WHEN To_char(ise.dthr_programada,'D')='2'")
		.append("                                   THEN 'SEG'")
		.append("                                   WHEN To_char(ise.dthr_programada,'D')='3'")
		.append("                                   THEN 'TER'")
		.append("                                   WHEN To_char(ise.dthr_programada,'D')='4'")
		.append("                                   THEN 'QUA'")
		.append("                                    WHEN To_char(ise.dthr_programada,'D')='5'")
		.append("                                    THEN 'QUI' ")
		.append("                                   WHEN To_char(ise.dthr_programada,'D')='6'")
		.append("                                   THEN 'SEX'")
		.append("                                   WHEN To_char(ise.dthr_programada,'D')='7'")
		.append("                                   THEN 'SAB'")
		.append("                                   END AS DIA_DA_SEMANA)")
		.append("		AND To_char(hor.horario, 'hh24:mi') = To_char(ise.dthr_programada, 'hh24:mi')) = 0))")
		.append("			WHEN  0")
		.append("			THEN ''")
		.append("			Else 'Programada' ")
		.append(" END AS programada ")
		.append(" FROM   	agh.v_ael_solic_atends_aghu_refat this_")
		.append(" LEFT OUTER JOIN agh.agh_atendimentos atd1_ ON this_.atd_seq = atd1_.seq")
		.append(" LEFT OUTER JOIN agh.aip_pacientes pac2_ ON atd1_.pac_codigo = pac2_.codigo")
		.append(" INNER JOIN agh.ael_item_solicitacao_exames ise_ on (this_.seq = ise_.soe_seq)")
		.append(" INNER JOIN AGH.AEL_TIPOS_AMOSTRA_EXAMES AMO ON (ise_.ufe_ema_exa_sigla = AMO.EMA_EXA_SIGLA and ise_.ufe_ema_man_seq= AMO.EMA_MAN_SEQ)")
		.append(" INNER JOIN AGH.AGH_ATENDIMENTOS ATD ON (THIS_.ATD_SEQ = ATD.SEQ)")
	                    
		.append(" WHERE  ")
		.append(" ise_.sit_codigo = :sitCodigo ")
		.append(" AND ise_.unf_seq_avisa = :unidadeExecutora ")
		.append(" AND ise_.dthr_programada <= :dataHora ")
//		-- this.restricaoHorarioColeta
		.append(" AND (   ")
		.append(" 	(this_.criado_em < ise_.dthr_programada  ")
		.append(" 		AND    ")
		.append(" 		(select   ")
		.append(" 			count(*)   ")
		.append(" 		from   ")
		.append(" 			agh.AEL_HORARIO_ROTINA_COLETAS hor    ")
		.append(" 		where    ")
		.append(" 			hor.unf_seq = ise_.ufe_unf_seq   ")
		.append(" 			and hor.unf_seq_solicitante = this_.unf_seq   ")
		.append(" 			and hor.dia = (SELECT CASE WHEN TO_CHAR(ise_.dthr_programada, 'D') = '1' THEN 'DOM'  ")
		.append(" 					    WHEN TO_CHAR(ise_.dthr_programada, 'D') = '2' THEN 'SEG'  ")
		.append(" 					    WHEN TO_CHAR(ise_.dthr_programada, 'D') = '3' THEN 'TER'  ")
		.append(" 					    WHEN TO_CHAR(ise_.dthr_programada, 'D') = '4' THEN 'QUA'  ")
		.append(" 					    WHEN TO_CHAR(ise_.dthr_programada, 'D') = '5' THEN 'QUI'  ")
		.append(" 					    WHEN TO_CHAR(ise_.dthr_programada, 'D') = '6' THEN 'SEX'  ")
		.append(" 					    WHEN TO_CHAR(ise_.dthr_programada, 'D') = '7' THEN 'SAB'  ")
		.append(" 				       END AS DIA_DA_SEMANA)  ")
		.append(" 		and To_char(hor.HORARIO, 'hh24:mi') = To_char(ise_.dthr_programada, 'hh24:mi')  ")	       
		.append(" 		) = 0  ")
		.append(" 	   )  ")
		.append(" 	OR ise_.tipo_coleta = :tipoColeta )  ")
		.append(" AND EXISTS (  ")
		.append(" 	SELECT ")
		.append(" 		pus_.unf_seq AS y0_")
		.append(" 	FROM   ")
		.append(" 		agh.ael_permissao_unid_solics pus_")
		.append(" 	WHERE")
		.append(" 		pus_.ufe_ema_exa_sigla = ise_.ufe_ema_exa_sigla ")
		.append("                     AND pus_.ufe_ema_man_seq = ise_.ufe_ema_man_seq ")
		.append("                     AND pus_.ufe_unf_seq = ise_.ufe_unf_seq ")
		.append("                     AND pus_.unf_seq = this_.unf_seq ")
		.append("                     AND pus_.unf_seq_avisa = :unidadeExecutora")
		.append("                     AND ( pus_.ind_permite_programar_exames = :programarExamesS ")
		.append("                        OR ( pus_.ind_permite_programar_exames IN")
		.append("                               ( :programarExamesR,")
		.append("                             	:programarExamesN )")
		.append("                        AND ise_.tipo_coleta = :tipoColeta ) ))")
		.append("                        AND EXISTS (SELECT tae_.nro_amostras AS y0_")
		.append("                                     FROM   agh.ael_tipos_amostra_exames tae_")
		.append("                                     WHERE")
		.append("                        tae_.ema_exa_sigla = ise_.ufe_ema_exa_sigla")
		.append("                        AND tae_.ema_man_seq = ise_.ufe_ema_man_seq")
		.append("                        AND tae_.responsavel_coleta IN ( :coletador, :solicitante )")
		.append("                        )")
//				-- restricaoLaudoOrigemPaciente")
		.append(" 		AND AMO.RESPONSAVEL_COLETA IN ( :coletador, :solicitante )")
		.append(" 		and ( AMO.ORIGEM_ATENDIMENTO = :todasOrigens")
		.append(" 			OR AMO.ORIGEM_ATENDIMENTO = ATD.ORIGEM)")
		.append(" 		ORDER  BY this_.seq ASC");
		
		SQLQuery query = createSQLQuery(hql.toString());
		query.setString("tipoColeta", DominioTipoColeta.U.toString());
		query.setString("coletador", DominioResponsavelColetaExames.C.toString());
		query.setString("solicitante", DominioResponsavelColetaExames.S.toString());
		query.setString("todasOrigens", DominioOrigemAtendimento.T.toString());
		query.setString("sitCodigo", sitCodigo);
		query.setInteger("unidadeExecutora", unidadeExecutora.getSeq().intValue());	
		query.setTimestamp("dataHora", dataCalculadaAparecimentoSolicitacao);
		query.setString("programarExamesS", DominioSimNaoRotina.S.toString());
		query.setString("programarExamesR", DominioSimNaoRotina.R.toString());
		query.setString("programarExamesN", DominioSimNaoRotina.N.toString());
		
		List<VAelSolicAtendsVO> list = query.addScalar("numero", IntegerType.INSTANCE)
			 .addScalar("atdSeq", IntegerType.INSTANCE)
			 .addScalar("dataSolicitacao", TimestampType.INSTANCE)
			 .addScalar("quarto", StringType.INSTANCE)
			 .addScalar("leito", StringType.INSTANCE)
			 .addScalar("prontuario", IntegerType.INSTANCE)
			 .addScalar("pacienteDiversos", StringType.INSTANCE)
			 .addScalar("prioridade", StringType.INSTANCE)
			 .addScalar("programada", StringType.INSTANCE)
			 .setResultTransformer(Transformers.aliasToBean(VAelSolicAtendsVO.class)).list();
		
		if(!list.isEmpty()){
			return list;
		} else {
			return null;
		}
	}
	
	/**
	 * Pesquisa para VO de solicitacoes de exame
	 * @param unidadeExecutora
	 * @param dataCalculadaAparecimentoSolicitacao
	 * @param sitCodigo
	 * @return
	 * @throws BaseException
	 */
	public List<Object[]> pesquisarSolicAtendsView(AghUnidadesFuncionais unidadeExecutora, String sitCodigo, Date dtSolicitacao) throws BaseException {	
		StringBuilder sql = new StringBuilder(6407);
		
		sql.append(" SELECT tabela.soeSeq, ")
			.append("       tabela.atendimentoSeq,")
			.append("       tabela.pacCodigo,")
			.append("       tabela.criadoEm,")
			.append("       tabela.convenio,")
			.append("       tabela.atdOrigem,")
			.append("       tabela.unfSigla,")
			.append("       tabela.ltoId,")
			.append("       tabela.qrtDesc,")
			.append("       tabela.andar,")
			.append("       tabela.ala,")
			.append("       tabela.caracteristica,")
			.append("       tabela.urgente,")
			.append("       tabela.prioridade,")
			.append("       tabela.imprimiu,")
			.append("       count(notGrm.seq),")
			.append("       paciente.prontuario,")
			.append("       paciente.nome")
			.append(" FROM ")
			.append("  (SELECT soe.SEQ AS soeSeq,")
			.append("          coalesce(soe.ATD_SEQ, soe.ATV_SEQ) atendimentoSeq,")
			.append("          coalesce(atd.pac_codigo, atv.pac_codigo) pacCodigo,")
			.append("          soe.CRIADO_EM criadoEm,")
			.append("          cnv.DESCRICAO convenio,")
			.append("          soe.UNF_SEQ unfSeq,")
			.append("          atd.origem atdOrigem,")
			.append("          unf.sigla unfSigla,")
			.append("          atd.LTO_LTO_ID ltoId,")
			.append("          qrt.DESCRICAO qrtDesc,")
			.append("          unf.andar andar,")
			.append("          ala.descricao ala,")
			.append("          car.caracteristica,")
			.append("          CASE")
			.append("              WHEN")
			.append("                     (SELECT DISTINCT count(ise.seqp) AS urgente")
			.append("                      FROM AGH.AEL_ITEM_SOLICITACAO_EXAMES ise")
			.append("                      WHERE ISE.SIT_CODIGO = :sitCodigo ")
			.append("                        AND ise.SOE_SEQ = soe.seq")
			.append("                        AND ise.TIPO_COLETA='U'")
			.append("                        AND ise.UFE_UNF_SEQ= :unfSeq ")
			.append("                        AND EXISTS")
			.append("                          (SELECT pus_.unf_seq AS y0_")
			.append("                           FROM agh.AEL_PERMISSAO_UNID_SOLICS pus_")
			.append("                           WHERE pus_.ufe_ema_exa_sigla=ise.UFE_EMA_EXA_SIGLA")
			.append("                             AND pus_.ufe_ema_man_seq=ise.UFE_EMA_MAN_SEQ")
			.append("                             AND pus_.ufe_unf_seq=ise.UFE_UNF_SEQ")
			.append("                             AND (pus_.unf_seq_avisa= :unfSeq")
			.append("                                  OR pus_.ufe_unf_seq= :unfSeq ) ) )>0 THEN 'Ur'")
			.append("              ELSE ''")
			.append("          END AS urgente,")
			.append("          CASE")
			.append("              WHEN")
			.append("                     (SELECT count(*) AS counter")
			.append("                      FROM AGH.AEL_ITEM_SOLICITACAO_EXAMES ise")
			.append("                      INNER JOIN AGH.AEL_UNF_EXECUTA_EXAMES ufe1_ ON ise.UFE_EMA_EXA_SIGLA=ufe1_.EMA_EXA_SIGLA")
			.append("                      AND ise.UFE_EMA_MAN_SEQ=ufe1_.EMA_MAN_SEQ")
			.append("                      AND ise.UFE_UNF_SEQ=ufe1_.UNF_SEQ")
			.append("                      INNER JOIN agh.AEL_PERMISSAO_UNID_SOLICS pus2_ ON ufe1_.EMA_EXA_SIGLA=pus2_.UFE_EMA_EXA_SIGLA")
			.append("                      AND ufe1_.EMA_MAN_SEQ=pus2_.UFE_EMA_MAN_SEQ")
			.append("                      AND ufe1_.UNF_SEQ=pus2_.UFE_UNF_SEQ")
			.append("                      WHERE ise.SOE_SEQ = soe.seq")
			.append("                        AND ise.DTHR_PROGRAMADA >= soe.CRIADO_EM")
			.append("                        AND ise.SIT_CODIGO = :sitCodigo ")
			.append("                        AND ise.UNF_SEQ_AVISA = :unfSeq ")
			.append("                        AND pus2_.unf_seq = soe.UNF_SEQ")
			.append("                        AND EXISTS")
			.append("                          (SELECT unf_.SEQ AS y0_")
			.append("                           FROM AGH.AGH_UNIDADES_FUNCIONAIS unf_")
			.append("                           WHERE unf_.SEQ=pus2_.unf_seq_avisa")
			.append("                             AND (unf_.UNF_SEQ = soe.UNF_SEQ")
			.append("                                  OR unf_.SEQ= :unfSeq ) ) )>0 THEN 'Prg'")
			.append("              ELSE ''")
			.append("          END AS prioridade,")
			.append("          CASE")
			.append("              WHEN")
			.append("                     (SELECT count(*) AS counter")
			.append("                      FROM AGH.AEL_ITEM_SOLICITACAO_EXAMES ise")
			.append("                      INNER JOIN AGH.AEL_UNF_EXECUTA_EXAMES ufe1_ ON ise.UFE_EMA_EXA_SIGLA=ufe1_.EMA_EXA_SIGLA")
			.append("                      AND ise.UFE_EMA_MAN_SEQ=ufe1_.EMA_MAN_SEQ")
			.append("                      AND ise.UFE_UNF_SEQ=ufe1_.UNF_SEQ")
			.append("                      INNER JOIN agh.AEL_PERMISSAO_UNID_SOLICS pus2_ ON ufe1_.EMA_EXA_SIGLA=pus2_.UFE_EMA_EXA_SIGLA")
			.append("                      AND ufe1_.EMA_MAN_SEQ=pus2_.UFE_EMA_MAN_SEQ")
			.append("                      AND ufe1_.UNF_SEQ=pus2_.UFE_UNF_SEQ")
			.append("                      WHERE ise.SOE_SEQ= soe.seq")
			.append("                        AND ise.SIT_CODIGO= :sitCodigo ")
			.append("                        AND pus2_.unf_seq = soe.UNF_SEQ")
			.append("                        AND ise.IND_IMPRIMIU_TICKET = 'S'")
			.append("                        AND EXISTS")
			.append("                          (SELECT unf_.SEQ AS y0_")
			.append("                           FROM AGH.AGH_UNIDADES_FUNCIONAIS unf_")
			.append("                           WHERE unf_.SEQ=pus2_.unf_seq_avisa")
			.append("                             AND (unf_.UNF_SEQ = soe.UNF_SEQ")
			.append("                                  OR unf_.SEQ= :unfSeq ) ) )>0 THEN 'S'")
			.append("              ELSE 'N'")
			.append("          END AS imprimiu")
			.append("   FROM AGH.AEL_SOLICITACAO_EXAMES soe")
			.append("   LEFT OUTER JOIN AGH.AGH_ATENDIMENTOS atd ON soe.atd_seq = atd.seq")
			.append("   LEFT OUTER JOIN agh.AIN_QUARTOS qrt ON qrt.NUMERO = atd.QRT_NUMERO")
			.append("   LEFT OUTER JOIN AGH.AGH_UNIDADES_FUNCIONAIS unf ON unf.seq = atd.unf_seq")
			.append("   LEFT OUTER JOIN AGH.AGH_CARACT_UNID_FUNCIONAIS car ON unf.seq = car.unf_seq")
			.append("   AND caracteristica = 'Unid Emergencia'")
			.append("   LEFT OUTER JOIN AGH.AGH_ALAS ala ON unf.IND_ALA = ala.CODIGO")
			.append("   LEFT OUTER JOIN AGH.AEL_ATENDIMENTO_DIVERSOS atv ON soe.atv_seq = atv.seq")
			.append("   LEFT OUTER JOIN AGH.FAT_CONVENIOS_SAUDE cnv ON soe.CSP_CNV_CODIGO=cnv.CODIGO")
			.append("   WHERE 1=1 ");
			if(dtSolicitacao != null) {
				sql.append("     AND soe.CRIADO_EM>= :dtSolicitacao ");
			}
			sql.append("  AND soe.seq IN")
			.append("       (SELECT ise.soe_seq")
			.append("        FROM agh.ael_permissao_unid_solics pus,")
			.append("             agh.ael_solicitacao_exames soe1,")
			.append("             agh.ael_item_solicitacao_exames ise")
			.append("        WHERE ise.sit_codigo = :sitCodigo ")
			.append("          AND ise.unf_seq_avisa IN")
			.append("            (SELECT unf.seq")
			.append("             FROM agh.agh_unidades_funcionais unf")
			.append("             WHERE (unf.unf_seq = :unfSeq ")
			.append("                    OR seq =:unfSeq) )")
			.append("          AND soe1.seq = ise.soe_seq")
			.append("          AND pus.ufe_ema_exa_sigla = ise.ufe_ema_exa_sigla")
			.append("          AND pus.ufe_ema_man_seq = ise.ufe_ema_man_seq")
			.append("          AND pus.ufe_unf_seq = ise.ufe_unf_seq")
			.append("          AND pus.unf_seq = soe1.unf_seq")
			.append("          AND pus.unf_seq_avisa IN")
			.append("            (SELECT unf1.seq")
			.append("             FROM agh.agh_unidades_funcionais unf1")
			.append("             WHERE (unf1.unf_seq =:unfSeq ")
			.append("                    OR unf1.seq =:unfSeq) ) )) AS tabela")
			.append(" LEFT OUTER JOIN agh.MCI_NOTIFICACAO_GMR notGrm ON tabela.pacCodigo = notGrm.pac_codigo AND notGrm.IND_NOTIFICACAO_ATIVA = 'S' ")
			.append(" LEFT OUTER JOIN agh.aip_pacientes paciente ON paciente.codigo = tabela.pacCodigo")
			.append(" GROUP BY tabela.soeSeq,")
			.append("       tabela.atendimentoSeq,")
			.append("       tabela.pacCodigo,")
			.append("       tabela.criadoEm,")
			.append("       tabela.convenio,")
			.append("       tabela.atdOrigem,")
			.append("       tabela.unfSigla,")
			.append("       tabela.ltoId,")
			.append("       tabela.qrtDesc,")
			.append("       tabela.andar,")
			.append("       tabela.ala,")
			.append("       tabela.caracteristica,")
			.append("       tabela.urgente,")
			.append("       tabela.prioridade,")
			.append("       tabela.imprimiu,")
			.append("       paciente.prontuario,")
			.append("       paciente.nome")
			.append(" ORDER BY criadoEm DESC");
		
		SQLQuery query = createSQLQuery(sql.toString());
		query.setInteger("unfSeq", unidadeExecutora.getSeq());
		query.setString("sitCodigo", sitCodigo);
		query.setDate("dtSolicitacao", dtSolicitacao);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.list();
		
		if(!list.isEmpty()){
			return list;
		} else {
			return null;
		}
	}
	
	public String buscarVAelSolicAtendsPorSoeSeq(Integer soeSeq) {
		DetachedCriteria criteriaVsa= DetachedCriteria.forClass(VAelSolicAtends.class,"vsa");

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VAelSolicAtends.Fields.CNV_DESCRICAO.toString()));
		criteriaVsa.setProjection(projectionList);			

		criteriaVsa.add(Restrictions.eq("vsa."+VAelSolicAtends.Fields.SEQ.toString(), soeSeq));
		return (String) executeCriteriaUniqueResult(criteriaVsa);
	}


	public List<VAelSolicAtendsVO> listarExamesCancelamentoSolicitante(PesquisaExamesFiltroVO filtro) throws BaseException {

		List<VAelSolicAtendsVO> exames = new ArrayList<VAelSolicAtendsVO>();

		DetachedCriteria criteria = obtemCriteriaListaCancelExames(filtro);

		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.distinct(Projections.property("soe."+AelSolicitacaoExames.Fields.SEQ.toString())));
		projectionList.add(Property.forName("con.numero"),"numConsulta");
		projectionList.add(Property.forName("aip."+AipPacientes.Fields.PRONTUARIO.toString()));
		projectionList.add(Property.forName("atd."+AghAtendimentos.Fields.ORIGEM.toString()));
		projectionList.add(Property.forName("aip."+AipPacientes.Fields.NOME.toString()));
		projectionList.add(Property.forName("aip2."+AipPacientes.Fields.NOME.toString()));

		criteria.setProjection(projectionList);

		List<Object[]> resultadoPesquisa = executeCriteria(criteria);

		for (Object[] record : resultadoPesquisa) {

			VAelSolicAtendsVO exame = new VAelSolicAtendsVO();
			exame.setNumero((Integer)record[0]); //soeSeq
			exame.setNumConsulta((Integer)record[1]);
			exame.setProntuario((Integer)record[2]);

			//-- if (origem == N) { origem = 'I' }			
			if (record[3] != null) {
				if(((DominioOrigemAtendimento)record[3]).equals(DominioOrigemAtendimento.N)) {
					exame.setOrigem(DominioOrigemAtendimento.I);
				}else{
					exame.setOrigem((DominioOrigemAtendimento)record[3]);
				}
			}
			
			if((String)record[4] != null){
				exame.setPacienteDiversos((String)record[4]);
			}else{
				exame.setPacienteDiversos((String)record[5]);

			}

			exames.add(exame);
		}

		return exames;
	}


	public Integer listarExamesCancSolicTotalRegistros(PesquisaExamesFiltroVO filtro) {

		DetachedCriteria criteria = obtemCriteriaListaCancelExames(filtro);


		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("soe."+AelSolicitacaoExames.Fields.SEQ.toString()))));

		List<Object[]> results = executeCriteria(criteria);


		return (results!=null?results.size():0);

	}


	private DetachedCriteria obtemCriteriaListaCancelExames(PesquisaExamesFiltroVO filtro) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");

		criteria.createCriteria(AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe", JoinType.INNER_JOIN);
		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atd."+AelSolicitacaoExames.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.CONVENIO_SAUDE_PLANO.toString(), "cnv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "aip", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atv."+AelAtendimentoDiversos.Fields.PACIENTE.toString(), "aip2", JoinType.LEFT_OUTER_JOIN);
		criteria.createCriteria("atd."+AghAtendimentos.Fields.CONSULTA.toString(), "con", JoinType.LEFT_OUTER_JOIN);
		Object[] objSituacao = {"AC","AG","AX","CA","CS","Y"};
		criteria.add(Restrictions.in("ise."+AelItemSolicitacaoExames.Fields.SIT_CODIGO, objSituacao));
		criteria.add(Restrictions.isNull("ise."+AelItemSolicitacaoExames.Fields.AEL_ITEM_SOLICITACAO_EXAMES_PAI));

		if(filtro.getNumeroSolicitacaoInfo()!=null ){
			criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SEQ.toString(), filtro.getNumeroSolicitacaoInfo()));
		}

		//Fazer com atv ou atd
		if(filtro.getProntuarioPac()!=null){
			criteria.add(Restrictions.eq("aip."+AipPacientes.Fields.PRONTUARIO, filtro.getProntuarioPac()));
		}
		if(filtro.getConsultaPac()!=null){
			criteria.add(Restrictions.eq("con."+AacConsultas.Fields.NUMERO, filtro.getConsultaPac()));
		}
		if(filtro.getOrigemAtendimentoInfo()!=null){
			criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.ORIGEM, filtro.getOrigemAtendimentoInfo()));
		}

		/**
		 * Filtra com os campos Matrícula e Vínculo do responsável logado
		 */
		criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SERVIDOR_MATRICULA.toString(), filtro.getServidorLogado().getId().getMatricula()));
		criteria.add(Restrictions.eq("soe."+AelSolicitacaoExames.Fields.SERVIDOR_VIN_CODIGO.toString(), filtro.getServidorLogado().getId().getVinCodigo()));

		return criteria;

	}

	public Object[] obterVAelSolicAtendsPorSoeSeq(Integer soeSeq) {
		DetachedCriteria criteria= DetachedCriteria.forClass(VAelSolicAtends.class,VSA.substring(0,3));

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CRIADO_EM.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_UNF_DESCRICAO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_CON_NUMERO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.UNF_SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ORIGEM.toString()));
		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq(VAelSolicAtends.Fields.SEQ.toString(), soeSeq));

		return (Object[]) executeCriteriaUniqueResult(criteria);
	}	

	public List<Object[]> obterSolicitacoesExame(VAelSolicAtendsVO filtro) {
		DetachedCriteria criteria= DetachedCriteria.forClass(VAelSolicAtends.class,VSA.substring(0,3));

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CRIADO_EM.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ORIGEM.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_CON_NUMERO.toString()));
		criteria.setProjection(projectionList);

		if(filtro.getNumero()!=null) {
			criteria.add(Restrictions.eq(VAelSolicAtends.Fields.SEQ.toString(), filtro.getNumero()));
		}
		else {
			if(filtro.getDataSolicitacao()!=null) {
				criteria.add(Restrictions.eq(VAelSolicAtends.Fields.CRIADO_EM.toString(), filtro.getDataSolicitacao()));
			}
			if(filtro.getCodPaciente()!=null) {
				criteria.add(Restrictions.eq(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString(), filtro.getCodPaciente()));
			}
			if(filtro.getNumConsulta()!=null) {
				criteria.add(Restrictions.eq(VAelSolicAtends.Fields.ATD_CON_NUMERO.toString(), filtro.getNumConsulta()));
			}
			if(filtro.getOrigem()!=null) {
				criteria.add(Restrictions.eq(VAelSolicAtends.Fields.ORIGEM.toString(), filtro.getOrigem().toString()));
			}
		}

		List<Object[]> results = executeCriteria(criteria);

		return results;
	}

	public RelatorioFichaTrabalhoPatologiaVO obterSolicitacaoAtendimentoFichaTrabAmostra(Integer soeSeq, Short seqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelSolicAtends.class, "SOLS");
		criteria.createAlias(VAelSolicAtends.Fields.SOLICITACAO_EXAME.toString(), "SOLE");
		criteria.createAlias(VAelSolicAtends.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("SOLE." + AelSolicitacaoExames.Fields.AMOSTRAS.toString(), "AMOS");

		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.distinct(Projections.property("SOLS." + VAelSolicAtends.Fields.SEQ.toString())),"soeSeq")
		.add(Projections.property("AMOS." + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), "unfSeq")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),"unfSeqSolicitante")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),"unfDescricao")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.ANDAR.toString()),"andar")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.ALA.toString()),"ala")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString()),"cspCnvCodigo")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.CSP_SEQ.toString()),"cspSeq")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.CRIADO_EM.toString()),"dtSolic")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.SERVIDOR_RESP.toString() + "." + RapServidores.Fields.MATRICULA.toString()),"matriculaResponsavel")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.SERVIDOR_RESP.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString()),"vinCodigoResponsavel")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()),"leitoID")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.INFORMACOES_CLINICAS.toString()),"informacoesClinicas")
		.add(Projections.property("AMOS." + AelAmostras.Fields.TEMPO_INTERVALO_COLETA.toString()),"tempoIntervaloColeta")
		.add(Projections.property("AMOS." + AelAmostras.Fields.UNID_TEMPO_INTERVALO_COLETA.toString()),"unidTempoIntervaloColeta")
		.add(Projections.property("AMOS." + AelAmostras.Fields.SEQP.toString()), "amostraSeqP")
		.add(Projections.property("AMOS." + AelAmostras.Fields.NRO_UNICO.toString()),"nroUnico")
		.add(Projections.property("AMOS." + AelAmostras.Fields.DT_NUMERO_UNICO.toString()),"dtNumeroUnico")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.RECEM_NASCIDO.toString()),"recemNascido")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.ATD_CON_NUMERO.toString()),"atdConNumero");
		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq("SOLS." + VAelSolicAtends.Fields.SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("AMOS." + AelAmostras.Fields.SEQP.toString() , seqP));

		criteria.setResultTransformer(Transformers
				.aliasToBean(RelatorioFichaTrabalhoPatologiaVO.class));

		return (RelatorioFichaTrabalhoPatologiaVO)executeCriteriaUniqueResult(criteria);
	}

	public RelatorioFichaTrabalhoPatologiaVO obterSolicitacaoAtendimento(Integer soeSeq, Short unfSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VAelSolicAtends.class, "SOLS");
		criteria.createAlias(VAelSolicAtends.Fields.SOLICITACAO_EXAME.toString(), "SOLE");
		criteria.createAlias(VAelSolicAtends.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		criteria.createAlias("SOLE." + AelSolicitacaoExames.Fields.AMOSTRAS.toString(), "AMOS");

		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.distinct(Projections.property("SOLS." + VAelSolicAtends.Fields.SEQ.toString())),"soeSeq")
		.add(Projections.property("AMOS." + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), "unfSeq")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()),"unfSeqSolicitante")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.DESCRICAO.toString()),"unfDescricao")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.ANDAR.toString()),"andar")
		.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.ALA.toString()),"ala")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString()),"cspCnvCodigo")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.CSP_SEQ.toString()),"cspSeq")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.CRIADO_EM.toString()),"dtSolic")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.SERVIDOR_RESP.toString() + "." + RapServidores.Fields.MATRICULA.toString()),"matriculaResponsavel")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.SERVIDOR_RESP.toString() + "." + RapServidores.Fields.CODIGO_VINCULO.toString()),"vinCodigoResponsavel")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()),"leitoID")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.INFORMACOES_CLINICAS.toString()),"informacoesClinicas")
		.add(Projections.property("AMOS." + AelAmostras.Fields.TEMPO_INTERVALO_COLETA.toString()),"tempoIntervaloColeta")
		.add(Projections.property("AMOS." + AelAmostras.Fields.UNID_TEMPO_INTERVALO_COLETA.toString()),"unidTempoIntervaloColeta")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.RECEM_NASCIDO.toString()),"recemNascido")
		.add(Projections.property("SOLS." + VAelSolicAtends.Fields.ATD_CON_NUMERO.toString()),"atdConNumero");
		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq("SOLS." + VAelSolicAtends.Fields.SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("AMOS." + AelAmostras.Fields.UNIDADE_FUNCIONAL.toString() + "." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));

		criteria.setResultTransformer(Transformers
				.aliasToBean(RelatorioFichaTrabalhoPatologiaVO.class));

		return (RelatorioFichaTrabalhoPatologiaVO)executeCriteriaUniqueResult(criteria);
	}

	public List<Object[]> obterSolicAtendsPorItemHorarioAgendado(Short hedGaeUnfSeq, Integer hedGaeSeqp, Date hedDthrAgenda) {
		DetachedCriteria criteria= DetachedCriteria.forClass(VAelSolicAtends.class, VSA.substring(0,3));

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.distinct(Property.forName(VSA + VAelSolicAtends.Fields.SEQ.toString())));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CNV_DESCRICAO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CSP_SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ORIGEM.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_UNF_DESCRICAO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.QRT_DESCRICAO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.INFORMACOES_CLINICAS.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString()));
		criteria.setProjection(projectionList);     

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelItemHorarioAgendado.class, IHA.substring(0,3));
		subCriteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(IHA + AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString()))));

		subCriteria.add(Restrictions.eq(IHA + AelItemHorarioAgendado.Fields.HED_GAE_UNF_SEQ.toString(), hedGaeUnfSeq));
		subCriteria.add(Restrictions.eq(IHA + AelItemHorarioAgendado.Fields.HED_GAE_SEQP.toString(), hedGaeSeqp));
		subCriteria.add(Restrictions.eq(IHA + AelItemHorarioAgendado.Fields.HED_DTHR_AGENDA.toString(), hedDthrAgenda));
		subCriteria.add(Restrictions.eqProperty(IHA + AelItemHorarioAgendado.Fields.ISE_SOE_SEQ.toString(), VSA + VAelSolicAtends.Fields.SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));
		criteria.addOrder(Order.desc(VSA + VAelSolicAtends.Fields.SEQ.toString()));


		List<Object[]> results = executeCriteria(criteria);

		return results;
	}

	public List<Object[]> pesquisarSolicitacaoPorPaciente(Integer soeSeq, Integer pacCodigo) {
		DetachedCriteria criteria= DetachedCriteria.forClass(VAelSolicAtends.class,VSA.substring(0,3));

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CSP_SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ORIGEM.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.TIPO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_UNF_DESCRICAO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_QRT_NUMERO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.INFORMACOES_CLINICAS.toString()));
		
		criteria.setProjection(projectionList);
		if(soeSeq!=null){
			criteria.add(Restrictions.eq(VAelSolicAtends.Fields.SEQ.toString(), soeSeq));	
		}
		if(pacCodigo!=null){
			criteria.add(Restrictions.eq(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString(), pacCodigo));	
		}

		List<Object[]> results = executeCriteria(criteria);
		return results;
	}	

	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorPacienteEAmostra(Integer soeSeq, Integer pacCodigo, Short amostraSeq) {

		StringBuffer hql = new StringBuffer(240);
		hql.append("select new br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO("); 	
		hql.append("	vsa.").append(VAelSolicAtends.Fields.SEQ.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.CSP_SEQ.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.ORIGEM.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.TIPO.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.ATD_UNF_DESCRICAO.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.QRT_DESCRICAO.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()).append(',');
		hql.append("	vsa.").append(VAelSolicAtends.Fields.INFORMACOES_CLINICAS.toString());
		hql.append(") from VAelSolicAtends vsa");

		hql.append(" , AelAmostras amo ");

		hql.append(" where ");

		if(soeSeq!=null){
			hql.append("vsa.");
			hql.append(VAelSolicAtends.Fields.SEQ.toString());
			hql.append(" = :soeSeq and ");
		}
		if(pacCodigo!=null){
			hql.append("vsa.");
			hql.append(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString());
			hql.append(" = :atdPacCodigo and ");
		}
		if(amostraSeq!=null){
			hql.append("amo.");
			hql.append(AelAmostras.Fields.SEQP.toString());
			hql.append(" = :amoSeq and ");
		}

		hql.append("amo.");
		hql.append(AelAmostras.Fields.SOE_SEQ.toString());
		hql.append(" = ");
		hql.append("vsa.");
		hql.append(VAelSolicAtends.Fields.SEQ.toString());

		Query query = createHibernateQuery(hql.toString());
		if(soeSeq!=null){
			query.setParameter("soeSeq", soeSeq);	
		}
		if(pacCodigo!=null){
			query.setParameter("atdPacCodigo", pacCodigo);	
		}
		if(amostraSeq!=null){
			query.setParameter("amoSeq", amostraSeq);	
		}
		return query.list();
	}
	
	public List<Object[]> pesquisarSolicitacaoPorSoePacienteUnf(Integer soeSeq, Integer pacCodigo, Short unfSeq) {
		DetachedCriteria criteria= DetachedCriteria.forClass(VAelSolicAtends.class,VSA.substring(0,3));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CSP_CNV_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CSP_SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ORIGEM.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_PRONTUARIO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.TIPO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_UNF_DESCRICAO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_QRT_NUMERO.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_LTO_LTO_ID.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.INFORMACOES_CLINICAS.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.ATD_SEQ.toString()));
		projectionList.add(Property.forName(VSA + VAelSolicAtends.Fields.CRIADO_EM.toString()));
		criteria.setProjection(projectionList);
		if(soeSeq!=null){
			criteria.add(Restrictions.eq(VAelSolicAtends.Fields.SEQ.toString(), soeSeq));	
		}
		if(pacCodigo!=null){
			criteria.add(Restrictions.eq(VAelSolicAtends.Fields.ATD_PAC_CODIGO.toString(), pacCodigo));	
		}
		if(unfSeq!=null){
			criteria.add(Restrictions.eq(VAelSolicAtends.Fields.ATD_UNF_SEQ.toString(), unfSeq));	
		}
		
		List<Object[]> results = executeCriteria(criteria);
		return results;
	}	
}
