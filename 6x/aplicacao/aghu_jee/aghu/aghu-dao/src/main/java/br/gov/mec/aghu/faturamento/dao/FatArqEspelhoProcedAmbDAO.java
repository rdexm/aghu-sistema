package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.persistence.Table;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.transform.Transformers;
import org.hibernate.type.ByteType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TypeResolver;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoFormularioDataSus;
import br.gov.mec.aghu.faturamento.vo.FatArqEspelhoProcedAmbVO;
import br.gov.mec.aghu.model.AelDoadorRedome;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatArqEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;

public class FatArqEspelhoProcedAmbDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatArqEspelhoProcedAmb> {

	private static final long serialVersionUID = -7084847293143713064L;
	private static final String AGH = " AGH.";
	private static final String AEP = ", AEP.";
	private static final String AS = " AS ";
	private static final String SELECT = " select ";
	private static final String WHERE_1_1 = " where 1=1 ";
	private static final String FROM = " from ";
	private static final String AND = " and ";
	private static final String AEP_PONTO = " AEP.";
	private static final String EPA_PONTO = " EPA.";
	private static final String PMR_PONTO = " PMR.";
	private static final String FCP_PONTO = " FCP.";
	private static final String APE_PONTO = " APE.";
	private static final String ATD_PONTO = " ATD.";
	private static final String ISE_PONTO = " ISE.";
	private static final String SOE_PONTO = " SOE.";
	
	private static final String LEFT_JOIN_AGH = "LEFT JOIN AGH.";

	/** Remove registro do FatArqEspelhoProcedAmb
	 * 
	 * Usado HQL para melhorar desempenho pq pode ter grande nro de registro,
	 * é uma tabela temporária para encerramento do faturamento do ambulatório,
	 * não possui triggers
	 * 
	 * @param cpeDtHrInicio
	 * @param ano
	 * @param mes
	 * @param modulo
	 * @param tipoFormulario
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void removeArqEspelhoProcedAmb(Date cpeDtHrInicio, Integer ano, Integer  mes, 
			DominioModuloCompetencia modulo, DominioTipoFormularioDataSus tipoFormulario) {
		StringBuffer hql = null;
		Query query = null;
		
		hql = new StringBuffer();
		
		hql.append("delete ").append(FROM);
		hql.append(FatArqEspelhoProcedAmb.class.getName());
		hql.append(WHERE_1_1);
		
		if (cpeDtHrInicio != null) {
			hql.append(AND).append(FatArqEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString()).append(" = :cpeDtHrInicio");
		}
		if (ano != null) {
			hql.append(AND).append(FatArqEspelhoProcedAmb.Fields.CPE_ANO.toString()).append(" = :ano");
		}
		if (mes != null) {
			hql.append(AND).append(FatArqEspelhoProcedAmb.Fields.CPE_MES.toString()).append(" = :mes");
		}
		if (modulo != null) {
			hql.append(AND).append(FatArqEspelhoProcedAmb.Fields.CPE_MODULO.toString()).append(" = :modulo");
		}
		if (tipoFormulario != null) {
			hql.append(AND).append(FatArqEspelhoProcedAmb.Fields.TIPO_FORMULARIO.toString()).append(" = :tipoFormulario");
		}
		
		query = createHibernateQuery(hql.toString());
		if (cpeDtHrInicio != null){
			query.setParameter("cpeDtHrInicio", cpeDtHrInicio);
		}
		if (ano != null){
			query.setParameter("ano", ano);
		}
		if (mes != null){
			query.setParameter("mes", mes);
		}
		if (modulo != null){
			query.setParameter("modulo", modulo);
		}
		if (tipoFormulario != null){
			query.setParameter("tipoFormulario", tipoFormulario);
		}
		query.executeUpdate();
		
	}

/*	
	public List<FatArqEspelhoProcedAmbVO> obterRegistrosGeracaoArquivoBPA(final FatCompetencia competencia, final Long procedimento, final Integer tctSeq){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatArqEspelhoProcedAmb.class,"arqEsp");
		
		criteria.add(Restrictions.eq(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_MES.toString(), competencia.getId().getMes()));
		criteria.add(Restrictions.eq(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_ANO.toString(), competencia.getId().getAno()));
		criteria.add(Restrictions.eq(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString(), competencia.getId().getDtHrInicio()));
		criteria.add(Restrictions.eq(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_MODULO.toString(), competencia.getId().getModulo()));
		criteria.add(Restrictions.ne(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString(), procedimento));
		
		// AND fatc_ver_caract_iph (NULL, NULL,IPH_PHO_SEQ, IPH_SEQ, 'Desconsiderar Item no Arquivo') <> 'S'
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(FatCaractItemProcHosp.class,"caractItem");
		subCriteria.setProjection(Projections.property(CARACT_ITEM+FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(CARACT_ITEM+FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.toString(),ARQ_ESP+FatArqEspelhoProcedAmb.Fields.IPH_PHO_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(CARACT_ITEM+FatCaractItemProcHosp.Fields.IPH_SEQ.toString(),ARQ_ESP+FatArqEspelhoProcedAmb.Fields.IPH_SEQ.toString()));
		subCriteria.add(Restrictions.eq(CARACT_ITEM+FatCaractItemProcHosp.Fields.TCT_SEQ.toString(), tctSeq));
		criteria.add(Subqueries.notExists(subCriteria));
		
		criteria.setProjection(Projections.projectionList()
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_MES.toString()), FatArqEspelhoProcedAmbVO.Fields.CPE_MES.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_ANO.toString()),FatArqEspelhoProcedAmbVO.Fields.CPE_ANO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString()),FatArqEspelhoProcedAmbVO.Fields.CPE_DT_HR_INICIO.toString())
							   
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CODIGO_UPS.toString()),FatArqEspelhoProcedAmbVO.Fields.CODIGO_UPS.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.COD_ATV_PROF.toString()),FatArqEspelhoProcedAmbVO.Fields.COD_ATV_PROF.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.COMPETENCIA.toString()),FatArqEspelhoProcedAmbVO.Fields.COMPETENCIA.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.FOLHA.toString()),FatArqEspelhoProcedAmbVO.Fields.FOLHA.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.LINHA.toString()),FatArqEspelhoProcedAmbVO.Fields.LINHA.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.toString()),FatArqEspelhoProcedAmbVO.Fields.PROCEDIMENTO_HOSP.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CNS_PACIENTE.toString()),FatArqEspelhoProcedAmbVO.Fields.CNS_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CNS_MEDICO.toString()),FatArqEspelhoProcedAmbVO.Fields.CNS_MEDICO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.NOME_PACIENTE.toString()),FatArqEspelhoProcedAmbVO.Fields.NOME_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.DT_NASCIMENTO.toString()),FatArqEspelhoProcedAmbVO.Fields.DT_NASCIMENTO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.SEXO.toString()),FatArqEspelhoProcedAmbVO.Fields.SEXO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.COD_IBGE.toString()),FatArqEspelhoProcedAmbVO.Fields.COD_IBGE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.DATA_ATENDIMENTO.toString()),FatArqEspelhoProcedAmbVO.Fields.DATA_ATENDIMENTO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CID_10.toString()),FatArqEspelhoProcedAmbVO.Fields.CID_10.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.IDADE.toString()),FatArqEspelhoProcedAmbVO.Fields.IDADE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.QUANTIDADE.toString()),FatArqEspelhoProcedAmbVO.Fields.QUANTIDADE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CARATER_ATENDIMENTO.toString()),FatArqEspelhoProcedAmbVO.Fields.CARATER_ATENDIMENTO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.NRO_AUTORIZACAO.toString()),FatArqEspelhoProcedAmbVO.Fields.NRO_AUTORIZACAO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.ORIGEM_INF.toString()),FatArqEspelhoProcedAmbVO.Fields.ORIGEM_INF.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.RACA.toString()),FatArqEspelhoProcedAmbVO.Fields.RACA.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CODIGO_NACIONALIDADE.toString()),FatArqEspelhoProcedAmbVO.Fields.CODIGO_NACIONALIDADE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.TIPO_ATENDIMENTO.toString()), FatArqEspelhoProcedAmbVO.Fields.TIPO_ATENDIMENTO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.GRUPO_ATENDIMENTO.toString()), FatArqEspelhoProcedAmbVO.Fields.GRUPO_ATENDIMENTO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.FAIXA_ETARIA.toString()), FatArqEspelhoProcedAmbVO.Fields.FAIXA_ETARIA.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.SERVICO.toString()), FatArqEspelhoProcedAmbVO.Fields.SERVICO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.CLASSIFICACAO.toString()), FatArqEspelhoProcedAmbVO.Fields.CLASSIFICACAO.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.ATV_PROFISSIONAL.toString()), FatArqEspelhoProcedAmbVO.Fields.ATV_PROFISSIONAL.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.END_CEP_PACIENTE.toString()), FatArqEspelhoProcedAmbVO.Fields.END_CEP_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.END_COD_LOGRADOURO_PACIENTE.toString()), FatArqEspelhoProcedAmbVO.Fields.END_COD_LOGRADOURO_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.END_LOGRADOURO_PACIENTE.toString()), FatArqEspelhoProcedAmbVO.Fields.END_LOGRADOURO_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.END_COMPLEMENTO_PACIENTE.toString()), FatArqEspelhoProcedAmbVO.Fields.END_COMPLEMENTO_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.END_NUMERO_PACIENTE.toString()), FatArqEspelhoProcedAmbVO.Fields.END_NUMERO_PACIENTE.toString())
							   .add(Projections.property(ARQ_ESP+FatArqEspelhoProcedAmb.Fields.END_BAIRRO_PACIENTE.toString()), FatArqEspelhoProcedAmbVO.Fields.END_BAIRRO_PACIENTE.toString())
							   
		 					   );
		
		criteria.setResultTransformer(Transformers.aliasToBean(FatArqEspelhoProcedAmbVO.class));
		
		final List<FatArqEspelhoProcedAmbVO> resultAmbs = executeCriteria(criteria);
		
		return resultAmbs;
	}
	*/
	
	public List<FatArqEspelhoProcedAmbVO> obterRegistrosGeracaoArquivoBPAParte2(final FatCompetencia competencia, final Long procedHosp, final Integer tctSeq){
		
		final StringBuilder sql = new StringBuilder(4000);
		final StringBuilder cabecalho = new StringBuilder(2000);
		
		// TODO eSchweigert 06/09/2013 TEMPORÁRIO até Data Sus atualizar a base de endereços, deve permanecer código antigo
		 final String join_aipp = " LEFT JOIN  AGH.AIPP_INS_TIPO_TITULO_LOG B " +// -- Codigos de tipo de logradouros antigos" 
								  "	        ON (" +
								  "	                B.TABELA = 'AIP_TIPO_LOGRADOUROS'" +
								  "	              AND B.CODIGO_NOVO = AEP." + FatArqEspelhoProcedAmb.Fields.END_COD_LOGRADOURO_PACIENTE.name()+
								  "	              AND B.MENSAGEM = 'Criando registro'" +
								  "	            )";
		
		cabecalho.append(SELECT)
	    
	    .append(" AEP.").append(FatArqEspelhoProcedAmb.Fields.ATV_PROFISSIONAL.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.ATV_PROFISSIONAL.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.CODIGO_UPS.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.CODIGO_UPS.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.COD_ATV_PROF.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.COD_ATV_PROF.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.COMPETENCIA.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.COMPETENCIA.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.FOLHA.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.FOLHA.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.LINHA.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.LINHA.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.PROCEDIMENTO_HOSP.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.CNS_PACIENTE.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.CNS_PACIENTE.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.CNS_MEDICO.name()).append(AS).append(FatArqEspelhoProcedAmbVO.Fields.CNS_MEDICO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.NOME_PACIENTE.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.NOME_PACIENTE.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.DT_NASCIMENTO.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.DT_NASCIMENTO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.SEXO.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.SEXO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.COD_IBGE.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.COD_IBGE.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.DATA_ATENDIMENTO.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.DATA_ATENDIMENTO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.CID10.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.CID_10.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.IDADE.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.IDADE.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.QUANTIDADE.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.QUANTIDADE.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.CARATER_ATENDIMENTO.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.CARATER_ATENDIMENTO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.NRO_AUTORIZACAO.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.NRO_AUTORIZACAO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.ORIGEM_INF.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.ORIGEM_INF.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.RACA.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.RACA.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.NACIONALIDADE.name()).append(AS).append( FatArqEspelhoProcedAmbVO.Fields.CODIGO_NACIONALIDADE.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.TIPO_ATENDIMENTO.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.TIPO_ATENDIMENTO.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.GRUPO_ATENDIMENTO.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.GRUPO_ATENDIMENTO.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.FAIXA_ETARIA.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.FAIXA_ETARIA.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.SERVICO.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.SERVICO.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.CLASSIFICACAO.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.CLASSIFICACAO.toString())
	    .append(AEP).append(FatArqEspelhoProcedAmb.Fields.END_CEP_PACIENTE.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_CEP_PACIENTE.toString())

	    .append(", B.CODIGO_ANTIGO ").append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_COD_LOGRADOURO_PACIENTE_BACKUP.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.END_COD_LOGRADOURO_PACIENTE.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_COD_LOGRADOURO_PACIENTE.toString())
		
		
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.END_LOGRADOURO_PACIENTE.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_LOGRADOURO_PACIENTE.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.END_COMPLEMENTO_PACIENTE.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_COMPLEMENTO_PACIENTE.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.END_NUMERO_PACIENTE.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_NUMERO_PACIENTE.toString())
		.append(AEP).append(FatArqEspelhoProcedAmb.Fields.END_BAIRRO_PACIENTE.name()).append(AS).append(  FatArqEspelhoProcedAmbVO.Fields.END_BAIRRO_PACIENTE.toString());
		
		
		sql.append(cabecalho);
		sql.append(FROM)
		   .append(AGH).append(FatArqEspelhoProcedAmb.class.getAnnotation(Table.class).name()).append(" AEP ")
		   .append(join_aipp);
		
		// where
		sql.append(WHERE_1_1)
		   .append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.name()).append(" = :P_CPE_DT_HR_INICIO ")
		   .append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_ANO.name()).append("          = :P_CPE_ANO ")
		   .append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_MES.name()).append("          = :P_CPE_MES ")
		   .append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_MODULO.name()).append("       = :P_CPE_MODULO ")
		   .append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.name()).append("  <> :P_PROCEDIMENTO_HOSP ")
		   .append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.ORIGEM_INF.name()).append(" <> :P_ORIGEM_INF ");
		   

		sql.append(AND).append(" not exists (")
			
			.append(SELECT).append(FCP_PONTO).append(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.name())
					.append(FROM)
					.append(AGH).append(FatCaractItemProcHosp.class.getAnnotation(Table.class).name()).append(" FCP ")
					.append(WHERE_1_1)
					.append(AND).append(FCP_PONTO ).append(FatCaractItemProcHosp.Fields.IPH_PHO_SEQ.name()).append('=').append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.IPH_PHO_SEQ.name())
					.append(AND).append(FCP_PONTO ).append(FatCaractItemProcHosp.Fields.IPH_SEQ.name()).append('=').append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.IPH_SEQ.name())
					.append(AND).append(FCP_PONTO ).append(FatCaractItemProcHosp.Fields.TCT_SEQ.name()).append(" = :P_TCT_SEQ ")
			
		                           .append(") ");
		sql.append(" UNION ");
		
		sql.append(cabecalho);
		sql.append(FROM)
		
		.append(AGH).append(AghAtendimentosPacExtern.class.getAnnotation(Table.class).name()).append(" APE ")

		.append(LEFT_JOIN_AGH).append(AelDoadorRedome.class.getAnnotation(Table.class).name())
						.append(" DOR ON DOR.").append(AelDoadorRedome.Fields.PAC_CODIGO.name()).append(" = APE.").append(AghAtendimentosPacExtern.Fields.PAC_CODIGO.name()).append(", ")
		
		.append(AGH).append(AghAtendimentos.class.getAnnotation(Table.class).name()).append(" ATD, ")
		.append(AGH).append(AelSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" SOE, ")
		.append(AGH).append(AelItemSolicitacaoExames.class.getAnnotation(Table.class).name()).append(" ISE, ")
		.append(AGH).append(AipPacientes.class.getAnnotation(Table.class).name()).append(" PAC, ")
		.append(AGH).append(FatProcedAmbRealizado.class.getAnnotation(Table.class).name()).append(" PMR, ")
		.append(AGH).append(FatEspelhoProcedAmb.class.getAnnotation(Table.class).name()).append(" EPA, ")
		.append(AGH).append(FatArqEspelhoProcedAmb.class.getAnnotation(Table.class).name()).append(" AEP ")
		.append(join_aipp)

		// where
		.append(WHERE_1_1)
		.append(AND).append(APE_PONTO).append(AghAtendimentosPacExtern.Fields.PAC_CODIGO.name()).append(" = PAC.").append(AipPacientes.Fields.CODIGO.name())
		.append(AND).append(ATD_PONTO).append(AghAtendimentos.Fields.APE_SEQ.name()).append('=').append(APE_PONTO).append(AghAtendimentosPacExtern.Fields.SEQ.name())
		.append(AND).append(ATD_PONTO).append(AghAtendimentos.Fields.SEQ.name()).append('=').append(SOE_PONTO).append(AelSolicitacaoExames.Fields.ATD_SEQ.name())
		.append(AND).append(SOE_PONTO).append(AelSolicitacaoExames.Fields.SEQ.name()).append('=').append(ISE_PONTO).append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name())

		.append(AND).append(ISE_PONTO).append(AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.name()).append(" = :UFE_EMA_EXA_SIGLA ")
		.append(AND).append("DOR.").append(AelDoadorRedome.Fields.CODIGO_REDOME.name()).append(" IS NULL ")//  -- não estão no sistema novo 
		
		.append(AND).append(ISE_PONTO).append(AelItemSolicitacaoExames.Fields.SIT_CODIGO.name()).append(" = :SIT_CODIGO ")

		.append(AND).append(PMR_PONTO).append(FatProcedAmbRealizado.Fields.ISE_SOE_SEQ.name()).append('=').append(ISE_PONTO).append(AelItemSolicitacaoExames.Fields.SOE_SEQ.name())
		.append(AND).append(PMR_PONTO).append(FatProcedAmbRealizado.Fields.ISE_SEQP.name()).append('=').append(ISE_PONTO).append(AelItemSolicitacaoExames.Fields.SEQP.name())

		.append(AND).append(PMR_PONTO).append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.name()).append(" = :P_CPE_DT_HR_INICIO ")
		.append(AND).append(PMR_PONTO).append(FatProcedAmbRealizado.Fields.CPE_ANO.name()).append("          = :P_CPE_ANO ")//  -- 2012
		.append(AND).append(PMR_PONTO).append(FatProcedAmbRealizado.Fields.CPE_MES.name()).append("          = :P_CPE_MES ")//  -- 01
		.append(AND).append(PMR_PONTO).append(FatProcedAmbRealizado.Fields.CPE_MODULO.name()).append("       = :P_CPE_MODULO ")

		//--    LIGANDO PMR COM EPA 
		.append(AND).append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.PMR_SEQ.name()).append(" = PMR.").append(FatProcedAmbRealizado.Fields.SEQ.name()) 

		.append(AND).append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.name()).append(" = :P_IND_CONSISTENTE ")// -- SOMENTE COM INDICADOR DE CONSISTENTE = 'S'

		//--    LIGANDO AEP COM EPA 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.name())  .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.PROCEDIMENTO_HOSP.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_ANO.name())			   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.CPE_ANO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_MES.name())	  		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.CPE_MES.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.name())   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CPE_MODULO.name())		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.CPE_MODULO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CNS_MEDICO.name())		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.CNS_MEDICO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.COD_ATV_PROF.name())	   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.COD_ATV_PROF.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.NOME_PACIENTE.name())	   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.NOME_PACIENTE.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.DT_NASCIMENTO.name())	   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.DT_NASCIMENTO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.SEXO.name())			   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.SEXO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.COD_IBGE.name())		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.COD_IBGE.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.DATA_ATENDIMENTO.name())   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.DATA_ATENDIMENTO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.IDADE.name())			   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.IDADE.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.QUANTIDADE.name())		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.QUANTIDADE.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.CARATER_ATENDIMENTO.name()).append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.CARATER_ATENDIMENTO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.NRO_AUTORIZACAO.name())    .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.NRO_AUTORIZACAO.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.ORIGEM_INF.name())		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.ORIGEM_INF.name()) 
		.append(AND).append(AEP_PONTO).append(FatArqEspelhoProcedAmb.Fields.COMPETENCIA.name())		   .append('=').append(EPA_PONTO).append(FatEspelhoProcedAmb.Fields.COMPETENCIA.name());
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setString("P_CPE_MODULO", 	  	 competencia.getId().getModulo().toString());
		query.setInteger("P_CPE_MES", 	  	     competencia.getId().getMes());
		query.setInteger("P_CPE_ANO", 	  	     competencia.getId().getAno());
		query.setTimestamp("P_CPE_DT_HR_INICIO", competencia.getId().getDtHrInicio());
		query.setString("P_IND_CONSISTENTE",     String.valueOf('S'));
		query.setString("SIT_CODIGO",            DominioSituacaoItemSolicitacaoExame.LI.toString());
		query.setString("UFE_EMA_EXA_SIGLA",	 "HLE");	
		query.setLong("P_PROCEDIMENTO_HOSP", procedHosp);
		query.setString("P_ORIGEM_INF", "BP2");	// AND ORIGEM_INF <> 'BP2' --TEMPORARIO
		query.setInteger("P_TCT_SEQ", 	  	     tctSeq);
		
		
		Properties paramDominioSexo = new Properties();
		paramDominioSexo.put("enumClass", "br.gov.mec.aghu.dominio.DominioSexo");
		paramDominioSexo.put("type", "12");
		   
		Properties paramDominioBoletimAmbulatorio = new Properties();
		paramDominioBoletimAmbulatorio.put("enumClass", "br.gov.mec.aghu.dominio.DominioBoletimAmbulatorio");
		paramDominioBoletimAmbulatorio.put("type", "12");
		   
		@SuppressWarnings("unchecked")
		final List<FatArqEspelhoProcedAmbVO> result =  query.addScalar(FatArqEspelhoProcedAmbVO.Fields.ATV_PROFISSIONAL.toString(), ByteType.INSTANCE)
															 .addScalar(FatArqEspelhoProcedAmbVO.Fields.CODIGO_UPS.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.COD_ATV_PROF.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.COMPETENCIA.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.FOLHA.toString(), ShortType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.LINHA.toString(), ByteType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.PROCEDIMENTO_HOSP.toString(), LongType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.CNS_PACIENTE.toString(), LongType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.CNS_MEDICO.toString(), LongType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.NOME_PACIENTE.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.DT_NASCIMENTO.toString(), DateType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.SEXO.toString(), new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, paramDominioSexo))
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.COD_IBGE.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.DATA_ATENDIMENTO.toString(), DateType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.CID_10.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.QUANTIDADE.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.CARATER_ATENDIMENTO.toString(), ByteType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.NRO_AUTORIZACAO.toString(), LongType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.ORIGEM_INF.toString(), new TypeLocatorImpl(new TypeResolver()).custom(org.hibernate.type.EnumType.class, paramDominioBoletimAmbulatorio))
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.RACA.toString(), ByteType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.CODIGO_NACIONALIDADE.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.TIPO_ATENDIMENTO.toString(), ByteType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.GRUPO_ATENDIMENTO.toString(), ByteType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.FAIXA_ETARIA.toString(), ByteType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.SERVICO.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.CLASSIFICACAO.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.IDADE.toString(), ShortType.INSTANCE)
														     
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_CEP_PACIENTE.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_COD_LOGRADOURO_PACIENTE.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_COD_LOGRADOURO_PACIENTE_BACKUP.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_LOGRADOURO_PACIENTE.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_COMPLEMENTO_PACIENTE.toString(), StringType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_NUMERO_PACIENTE.toString(), IntegerType.INSTANCE)
														     .addScalar(FatArqEspelhoProcedAmbVO.Fields.END_BAIRRO_PACIENTE.toString(), StringType.INSTANCE)

														     .setResultTransformer(Transformers.aliasToBean(FatArqEspelhoProcedAmbVO.class)).list();
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<FatArqEspelhoProcedAmbVO> listarFatArqEspelhoProcedAmbVO(final FatCompetencia competencia, final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc){
		final Query query = getCriteriaListarFatArqEspelhoProcedAmbVo(competencia);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		
		final List<FatArqEspelhoProcedAmbVO> result = query.list();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Integer listarFatArqEspelhoProcedAmbVOCount(final FatCompetencia competencia){
		final Query query = getCriteriaListarFatArqEspelhoProcedAmbVo(competencia);
		final List<FatArqEspelhoProcedAmbVO> result = query.list();
		
		if(result != null && !result.isEmpty()){
			return result.size();
		} else {
			return null; 
		}
	}
	
	private Query getCriteriaListarFatArqEspelhoProcedAmbVo(final FatCompetencia competencia) {
		final StringBuilder hql = new StringBuilder(600);
		
		hql.append(SELECT)
		   .append("  UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).append(AS).append(FatArqEspelhoProcedAmbVO.Fields.UNF_SEQ)
		   .append(" ,UNF.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString()).append(AS).append(FatArqEspelhoProcedAmbVO.Fields.UNF_DESC)
		   .append(" ,IPH.").append(FatItensProcedHospitalar.Fields.COD_TABELA.toString()).append(AS).append(FatArqEspelhoProcedAmbVO.Fields.COD_TABELA)
		   .append(" ,IPH.").append(FatItensProcedHospitalar.Fields.DESCRICAO.toString()).append(AS).append(FatArqEspelhoProcedAmbVO.Fields.DESCRICAO_ITEM)
		   
		   .append(" ,SUM( " ).append(
				   "     CASE WHEN EPA.").append(FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.toString()).append(" = 'N' THEN " ).append(
				   "         CASE WHEN EPA.").append(FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()).append(" IS NOT NULL THEN EPA.").append(FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()).append(" ELSE 1 END " ).append(
				   "       ELSE 0 " ).append(
				   "     END " ).append(
				   "   ) as ").append(FatArqEspelhoProcedAmbVO.Fields.QT_NOK)
		   
		   .append(" ,SUM( " ).append(
				   "     CASE WHEN EPA.").append(FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.toString()).append(" = 'S' THEN " ).append(
				   "         CASE WHEN EPA.").append(FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()).append(" IS NOT NULL THEN EPA.").append(FatEspelhoProcedAmb.Fields.QUANTIDADE.toString()).append(" ELSE 1 END " ).append(
				   "       ELSE 0 " ).append(
				   "     END " ).append(
				   "   ) as ").append(FatArqEspelhoProcedAmbVO.Fields.QT_OK)
		   
		   .append(" ,SUM(EPA.").append(FatEspelhoProcedAmb.Fields.VLR_PROC.toString()).append(") as ").append(FatArqEspelhoProcedAmbVO.Fields.VL_PROC)
		   .append(" ,SUM(EPA.").append(FatEspelhoProcedAmb.Fields.VLR_SERV_PROF.toString()).append(") as ").append(FatArqEspelhoProcedAmbVO.Fields.VL_SERV_PROF)
		   .append(" ,SUM(EPA.").append(FatEspelhoProcedAmb.Fields.VLR_ANESTES.toString()).append(") as ").append(FatArqEspelhoProcedAmbVO.Fields.VL_ANESTESIA)

		   .append(FROM)
		   
		   .append( FatEspelhoProcedAmb.class.getSimpleName()).append(" AS EPA ")
		   .append(" JOIN EPA.").append(FatEspelhoProcedAmb.Fields.ITENS_PROCED_HOSPITALAR.toString()).append(" AS IPH ")
		   .append(" JOIN EPA.").append(FatEspelhoProcedAmb.Fields.UNIDADE_FUNCIONAL.toString()).append(" AS UNF ")
		   
		   .append(WHERE_1_1)
		   .append("       AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_MODULO.toString()).append(" = :PMR_MODULO ")
		   .append("       AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_MES.toString()).append(" = :PMR_MES ")
		   .append("       AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_ANO.toString()).append(" = :PMR_ANO ")
		   .append("       AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_DT_HR_INICIO.toString()).append(" = :PMR_DT_HR_INICIO ")
		   
		   .append(" group by ")
		   .append("           UNF.").append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString())
		   .append("          ,UNF.").append(AghUnidadesFuncionais.Fields.DESCRICAO.toString() )
		   .append("          ,iph.").append(FatItensProcedHospitalar.Fields.COD_TABELA.toString())
		   .append("          ,iph.").append(FatItensProcedHospitalar.Fields.DESCRICAO.toString())
		   .append("          ,EPA.").append(FatEspelhoProcedAmb.Fields.IND_CONSISTENTE.toString())
		   ;
		
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("PMR_MODULO", competencia.getId().getModulo());
		query.setParameter("PMR_MES", competencia.getId().getMes());
		query.setParameter("PMR_ANO", competencia.getId().getAno());
		query.setParameter("PMR_DT_HR_INICIO", competencia.getId().getDtHrInicio());

		query.setResultTransformer(Transformers.aliasToBean(FatArqEspelhoProcedAmbVO.class));
		return query;
	}
	
	public void executaSQLVoltaCompetenciaProcedimentos(final String sql, final Integer mesAnterior, final Integer anoAnterior, final Date dtHoraInicioAnterior, 
							final DominioModuloCompetencia modulo, final Integer mesAtual, final Integer anoAtual){
	

		final SQLQuery query = this.createSQLQuery(sql);
		query.setInteger("PRM_MES", mesAnterior);
		query.setInteger("PRM_ANO", anoAnterior);
		query.setTimestamp("PRM_DT_INICIO", dtHoraInicioAnterior);
		query.setString("PRM_MODULO", modulo.toString());
		query.setInteger("PRM_MES_ATUAL", mesAtual);
		query.setInteger("PRM_ANO_ATUAL", anoAtual);
		query.executeUpdate();
	}
	
	public void executaSQLVoltaCompetenciaProcedimentosDeletes(final String sql, final DominioModuloCompetencia modulo, final Integer mesAtual,
																final Integer anoAtual, final Date dtHoraInicioAtual){
		
		final SQLQuery query = this.createSQLQuery(sql);
		query.setString("PRM_MODULO", modulo.toString());
		query.setInteger("PRM_MES_ATUAL", mesAtual);
		query.setInteger("PRM_ANO_ATUAL", anoAtual);
		query.setTimestamp("PRM_DT_HR_INICIO_ATUAL", dtHoraInicioAtual);
		query.executeUpdate();
	}
	
	public void executaSQLVoltaSituacaoProcedimentos( final String sql, final Integer mes, final Integer ano, final String situacao,
													  final String modulo,  final String ...situacoes){
		
		final SQLQuery query = this.createSQLQuery(sql);
		query.setInteger("PRM_MES", mes);
		query.setInteger("PRM_ANO", ano);
		query.setString("PRM_MODULO", modulo);
		query.setString("PRM_IND_SITUACAO", situacao);
		query.setParameterList("PRM_IND_SITUACAO_LIST", situacoes);
		query.executeUpdate();
	}
}