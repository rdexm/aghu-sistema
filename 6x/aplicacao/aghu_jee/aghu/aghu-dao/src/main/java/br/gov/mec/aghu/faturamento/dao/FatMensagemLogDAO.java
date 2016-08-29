package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioModuloMensagem;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMensagemLog;
import br.gov.mec.aghu.faturamento.vo.LogInconsistenciaBPAVO;
import br.gov.mec.aghu.faturamento.vo.MsgErroCthSeqVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoProcedAmb;
import br.gov.mec.aghu.model.FatEspelhoProcedSiscolo;
import br.gov.mec.aghu.model.FatEspelhoSismama;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatLogError;
import br.gov.mec.aghu.model.FatMensagemLog;
import br.gov.mec.aghu.model.FatMensagemLogId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.VAghUnidFuncional;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class FatMensagemLogDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMensagemLog>{
    @Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = 1728043747494745977L;
	
	protected IParametroFacade getParametroFacade() {
		//return (ParametroFacade)Component.getInstance(ParametroFacade.class, true);
		return aIParametroFacade;
	}
	
	protected DetachedCriteria obterCriteria() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatMensagemLog.class);
		
		criteria.add(Restrictions.isNotNull(FatMensagemLog.Fields.SITUACAO.toString()));
		criteria.add(Restrictions.eq(FatMensagemLog.Fields.MODULO.toString(), DominioModuloMensagem.INT));

		return criteria;
	}

	public List<FatMensagemLog> listarMensagensErro(Object objPesquisa){
		DetachedCriteria criteria = obterCriteria();

		if(StringUtils.isNotEmpty((String)objPesquisa)) {
			criteria.add(Restrictions.ilike(FatMensagemLog.Fields.ERRO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
		}
		
		return executeCriteria(criteria);
	}
	
	public Long listarMensagensErroCount(Object objPesquisa){
		DetachedCriteria criteria = obterCriteria();

		if(StringUtils.isNotEmpty((String)objPesquisa)) {
			criteria.add(Restrictions.ilike(FatMensagemLog.Fields.ERRO.toString(), (String)objPesquisa, MatchMode.ANYWHERE));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public List<FatMensagemLogId> listarMensagensErro(final Object objPesquisa, final DominioModuloMensagem modulo){
		final DetachedCriteria criteria = obterCriteria(objPesquisa, modulo);
		final List<FatMensagemLogId> resultFinal = new ArrayList<FatMensagemLogId>();
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property(FatMensagemLog.Fields.ERRO.toString())))
		);
		
		criteria.addOrder(Order.asc(FatMensagemLog.Fields.ERRO.toString()));
		
		final List<String> result = executeCriteria(criteria);
		if(result != null && !result.isEmpty()){
			for (String string : result) {
				resultFinal.add(new FatMensagemLogId(string, null, null, null));
			}
		}
		
		return resultFinal; 
	}
	
	public Long listarMensagensErroCount(final Object objPesquisa, final DominioModuloMensagem modulo){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatMensagemLog.class);
		
		criteria.add(Restrictions.eq(FatMensagemLog.Fields.MODULO.toString(), modulo));
		
		if(objPesquisa != null && !StringUtils.isEmpty(objPesquisa.toString())){
			criteria.add(Restrictions.ilike(FatMensagemLog.Fields.ERRO.toString(),objPesquisa.toString(), MatchMode.ANYWHERE));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.countDistinct(FatMensagemLog.Fields.ERRO.toString()))
		);
		
		return executeCriteriaCount(criteria);
	}	
	
	protected DetachedCriteria obterCriteria(final Object objPesquisa, final DominioModuloMensagem modulo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatMensagemLog.class);
		
		criteria.add(Restrictions.eq(FatMensagemLog.Fields.MODULO.toString(), modulo));
		
		if(objPesquisa != null && !StringUtils.isEmpty(objPesquisa.toString())){
			criteria.add(Restrictions.ilike(FatMensagemLog.Fields.ERRO.toString(),objPesquisa.toString(), MatchMode.ANYWHERE));
		}
		return criteria;
	}

	private String obterCamposConsulta(int opc){
		return "         LOG."+ FatLogError.Fields.ERRO.name()+"||';' || " +
		       "		 LOG."+ FatLogError.Fields.SER_VIN_CODIGO_PROF.name()+"||';' || " +
		       "		 LOG."+ FatLogError.Fields.SER_MATRICULA_PROF.name()+"||';' || " +
		       "		 VPS."+ VRapPessoaServidor.Fields.NOME.name()+"||';' || " +
		       ((opc == 1) ? "		 EPA."+ FatEspelhoProcedAmb.Fields.COD_ATV_PROF.name()+"||';' || "  : "';' ||")+
		       
		       "		 CON."+ AacConsultas.Fields.GRD_SEQ.name()+"||';' || " +
		       
		       ( (opc == 1) ? "		 EPA."+ FatEspelhoProcedAmb.Fields.PMR_SEQ.name() :
		    	 (opc == 2) ? "		 SMA."+ FatEspelhoSismama.Fields.PMR_SEQ.name()   : 
		    		 		  "		 EPS."+ FatEspelhoProcedSiscolo.Fields.PMR_SEQ.name() 
		        )+"||';' || "+
		    	 
		       "		 PMR."+ FatProcedAmbRealizado.Fields.ISE_SOE_SEQ.name()+"||';' || " +
		       "		 PMR."+ FatProcedAmbRealizado.Fields.ISE_SEQP.name()+"||';' || " +
		       "		 LOG."+ FatLogError.Fields.PHI_SEQ.name()+"||';' || " +
		       "		 LOG."+ FatLogError.Fields.IPH_SEQ.name()+"||';' || " +
		       "		 VAS."+ VFatAssociacaoProcedimento.Fields.COD_TABELA.name()+"||';' || " +
		       "		 IPH."+ FatItensProcedHospitalar.Fields.COD_PROCEDIMENTO.name()+"||';' || " +
		       "		 IPH."+ FatItensProcedHospitalar.Fields.DESCRICAO.name()+"||';' || " +
		       "		 VUNF."+ VAghUnidFuncional.Fields.UNF_DESCRICAO.name()+"||';' || " +
		       "		 PAC."+ AipPacientes.Fields.CODIGO.name()+"||';' || " +
		       "		 PAC."+ AipPacientes.Fields.PRONTUARIO.name()+"||';' || " +
		       "		 PAC."+ AipPacientes.Fields.NOME.name()+"||';' || " +
		       "		 TO_CHAR(PMR."+ FatProcedAmbRealizado.Fields.DTHR_REALIZADO.name()+", 'dd/mm/yyyy') AS LINHA ";
	}
	
	private String obterFromConsulta(int opc){
		final StringBuffer sql = new StringBuffer(260);
		
		sql.append(" FROM ")
		   .append("   AGH.").append(AipPacientes.class.getAnnotation(Table.class).name()).append(" PAC ")

		   .append("   INNER JOIN AGH.").append(FatLogError.class.getAnnotation(Table.class).name()).append(" LOG ON ")
						.append(" LOG.").append(FatLogError.Fields.PAC_CODIGO.name()).append(" = PAC.").append(AipPacientes.Fields.CODIGO.name())
		   
		   .append("   LEFT OUTER JOIN AGH.").append(VRapPessoaServidor.class.getAnnotation(Table.class).name()).append(" VPS ON ")
						.append(" VPS.").append(VRapPessoaServidor.Fields.SER_MATRICULA.name()).append(" = LOG.").append(FatLogError.Fields.SER_MATRICULA_PROF.name())
						.append(" AND VPS.").append(VRapPessoaServidor.Fields.SER_VIN_CODIGO.name()).append(" = LOG.").append(FatLogError.Fields.SER_VIN_CODIGO_PROF.name())

		   .append("   INNER JOIN AGH.").append(FatProcedAmbRealizado.class.getAnnotation(Table.class).name()).append(" PMR ON ")
						.append(" PMR.").append(FatProcedAmbRealizado.Fields.SEQ.name()).append(" = LOG.").append(FatLogError.Fields.PMR_SEQ.name())

								   
		   .append("   LEFT OUTER JOIN AGH.").append(AacConsultas.class.getAnnotation(Table.class).name()).append(" CON ON ")
						.append(" PMR.").append(FatProcedAmbRealizado.Fields.PRH_CON_NUMERO.name()).append(" = CON.").append(AacConsultas.Fields.NUMERO.name())

		   .append(",   AGH.").append(VAghUnidFuncional.class.getAnnotation(Table.class).name()).append(" VUNF ")
		   .append(",   AGH.").append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH ")
		   .append(",   AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VAS ");
		
		switch (opc) {
			case 1:
				sql.append(",   AGH.").append(FatEspelhoProcedAmb.class.getAnnotation(Table.class).name()).append(" EPA ");
			break;

			case 2:
				sql.append(",   AGH.").append(FatEspelhoSismama.class.getAnnotation(Table.class).name()).append(" SMA ");
			break;

			case 3:
				sql.append(",   AGH.").append(FatEspelhoProcedSiscolo.class.getAnnotation(Table.class).name()).append(" EPS ");
			break;
		}
		   
		sql.append(",   AGH.").append(FatCompetencia.class.getAnnotation(Table.class).name()).append(" CPE ");
		
		return sql.toString();
	}
	
	private String obterPrimeiraParteSQL(){
		final StringBuffer sql = new StringBuffer(5000);
		
		sql.append(" SELECT DISTINCT ")
		   .append(obterCamposConsulta(1))
		   .append(obterFromConsulta(1))
		   
		   .append(" WHERE 1=1 ")
		   .append("  AND CPE.").append(FatCompetencia.Fields.MODULO.name()).append(" = :PRM_MODULO1 ")
		   .append("  AND CPE.").append(FatCompetencia.Fields.IND_SITUACAO.name()).append(" = :PRM_IND_SITUACAO ")

		   .append("  AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_MODULO.name()).append(" = CPE.").append(FatCompetencia.Fields.MODULO.name())
		   .append("  AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_MES.name()).append(" = CPE.").append(FatCompetencia.Fields.MES.name())
		   .append("  AND EPA.").append(FatEspelhoProcedAmb.Fields.CPE_ANO.name()).append(" = CPE.").append(FatCompetencia.Fields.ANO.name())

		   .append("  AND LOG.").append(FatLogError.Fields.PMR_SEQ.name()).append(" = EPA.").append(FatEspelhoProcedAmb.Fields.PMR_SEQ.name())

  		   .append("  AND LOG.").append(FatLogError.Fields.MODULO.name()).append(" = CPE.").append(FatCompetencia.Fields.MODULO.name())
  		   
  		   .append("  AND (LOG.").append(FatLogError.Fields.ERRO.name()).append(" IN (:PRM_ERROS) ")
  		   		.append("   OR LOG.").append(FatLogError.Fields.ERRO.name()).append(" LIKE :PRM_ERRO) ")
  		   
  		   .append("  AND LOG.").append(FatLogError.Fields.CRIADO_EM.name()).append(" = ( ")
  		   		.append(" SELECT MAX(LOG1.").append(FatLogError.Fields.CRIADO_EM.name()).append(") ")
  		   		.append("   FROM AGH.").append(FatLogError.class.getAnnotation(Table.class).name()).append(" LOG1 ")
  		   		.append(" WHERE 1=1 ")
  		   		.append("  AND LOG1.").append(FatLogError.Fields.PMR_SEQ.name()).append(" = EPA.").append(FatEspelhoProcedAmb.Fields.PMR_SEQ.name())
  		   		.append("  AND LOG1.").append(FatLogError.Fields.MODULO.name()).append(" = LOG.").append(FatLogError.Fields.MODULO.name())
  		   		.append("  AND LOG1.").append(FatLogError.Fields.ERRO.name()).append(" = LOG.").append(FatLogError.Fields.ERRO.name())
  		   																		.append(" ) ")
  		   		
  		   .append("  AND VAS.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = LOG.").append(FatLogError.Fields.PHI_SEQ.name())
  		   .append("  AND VAS.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :PRM_CPG_CPH_CSP_CNV_CODIGO")
  		   .append("  AND VAS.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :PRM_CPG_CPH_CSP_SEQ")
  		   .append("  AND VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = :PRM_IPH_PHO_SEQ")

  		   .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = :PRM_PHO_SEQ")
  		   .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name())

		   .append("  AND EPA.").append(FatEspelhoProcedAmb.Fields.UNIDADE_FUNCIONAL.name()).append(" = VUNF.").append(VAghUnidFuncional.Fields.SEQ.name())
		   .append("  AND PAC.").append(AipPacientes.Fields.CODIGO.name()).append(" = PMR.").append(FatProcedAmbRealizado.Fields.PAC_CODIGO.name())
		   .append("  ");
		
		return sql.toString();
	}
    
	private String obterSegundaParteSQL(){
		final StringBuffer sql = new StringBuffer(5000);
		
		sql.append("( SELECT DISTINCT ")
		   .append(obterCamposConsulta(2))
		   .append(obterFromConsulta(2))

		   .append(" WHERE 1=1 ")
		   .append("  AND CPE.").append(FatCompetencia.Fields.MODULO.name()).append(" = :PRM_MODULO2 ")
		   .append("  AND CPE.").append(FatCompetencia.Fields.IND_SITUACAO.name()).append(" = :PRM_IND_SITUACAO ")
		   
		   .append("  AND SMA.").append(FatEspelhoSismama.Fields.CPE_MODULO.name()).append(" = CPE.").append(FatCompetencia.Fields.MODULO.name())
		   .append("  AND SMA.").append(FatEspelhoSismama.Fields.CPE_MES.name()).append(" = CPE.").append(FatCompetencia.Fields.MES.name())
		   .append("  AND SMA.").append(FatEspelhoSismama.Fields.CPE_ANO.name()).append(" = CPE.").append(FatCompetencia.Fields.ANO.name())

		   .append("  AND LOG.").append(FatLogError.Fields.PMR_SEQ.name()).append(" = SMA.").append(FatEspelhoSismama.Fields.PMR_SEQ.name())
		   .append("  AND LOG.").append(FatLogError.Fields.MODULO.name()).append(" = CPE.").append(FatCompetencia.Fields.MODULO.name())
		   
		   .append("  AND (LOG.").append(FatLogError.Fields.ERRO.name()).append(" IN (:PRM_ERROS) ")
		   .append("   OR LOG.").append(FatLogError.Fields.ERRO.name()).append(" LIKE :PRM_ERRO) ")

  		   .append("  AND LOG.").append(FatLogError.Fields.CRIADO_EM.name()).append(" = ( ")
  		   		.append(" SELECT MAX(LOG1.").append(FatLogError.Fields.CRIADO_EM.name()).append(") ")
  		   		.append("   FROM AGH.").append(FatLogError.class.getAnnotation(Table.class).name()).append(" LOG1 ")
  		   		.append(" WHERE 1=1 ")
  		   		.append("  AND LOG1.").append(FatLogError.Fields.PMR_SEQ.name()).append(" = SMA.").append(FatEspelhoSismama.Fields.PMR_SEQ.name())
  		   		.append("  AND LOG1.").append(FatLogError.Fields.MODULO.name()).append(" = LOG.").append(FatLogError.Fields.MODULO.name())
  		   		.append("  AND LOG1.").append(FatLogError.Fields.ERRO.name()).append(" = LOG.").append(FatLogError.Fields.ERRO.name())
  		   																		.append(" ) ")
  		   																		
  		   .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = :PRM_PHO_SEQ")
  		   .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name())

		   .append("  AND SMA.").append(FatEspelhoSismama.Fields.UNF_SEQ.name()).append(" = VUNF.").append(VAghUnidFuncional.Fields.SEQ.name())
		   .append("  AND PAC.").append(AipPacientes.Fields.CODIGO.name()).append(" = PMR.").append(FatProcedAmbRealizado.Fields.PAC_CODIGO.name())
		   .append(" ) ");
		
		return sql.toString();
	}

	private String obterTerceiraParteSQL(){
		final StringBuffer sql = new StringBuffer(5000);
		
		sql.append("( SELECT DISTINCT ")
		   .append(obterCamposConsulta(3))
		   .append(obterFromConsulta(3))

		   .append(" WHERE 1=1 ")
		   .append("  AND CPE.").append(FatCompetencia.Fields.MODULO.name()).append(" = :PRM_MODULO3 ")
		   .append("  AND CPE.").append(FatCompetencia.Fields.IND_SITUACAO.name()).append(" = :PRM_IND_SITUACAO ")
		   
		   .append("  AND EPS.").append(FatEspelhoProcedSiscolo.Fields.CPE_MODULO.name()).append(" = CPE.").append(FatCompetencia.Fields.MODULO.name())
		   .append("  AND EPS.").append(FatEspelhoProcedSiscolo.Fields.CPE_MES.name()).append(" = CPE.").append(FatCompetencia.Fields.MES.name())
		   .append("  AND EPS.").append(FatEspelhoProcedSiscolo.Fields.CPE_ANO.name()).append(" = CPE.").append(FatCompetencia.Fields.ANO.name())

		   .append("  AND LOG.").append(FatLogError.Fields.MODULO.name()).append(" = CPE.").append(FatCompetencia.Fields.MODULO.name())

		   .append("  AND (LOG.").append(FatLogError.Fields.ERRO.name()).append(" IN (:PRM_ERROS) ")
		   .append("   OR LOG.").append(FatLogError.Fields.ERRO.name()).append(" LIKE :PRM_ERRO) ")

  		   .append("  AND LOG.").append(FatLogError.Fields.CRIADO_EM.name()).append(" = ( ")
  		   		.append(" SELECT MAX(LOG1.").append(FatLogError.Fields.CRIADO_EM.name()).append(") ")
  		   		.append("   FROM AGH.").append(FatLogError.class.getAnnotation(Table.class).name()).append(" LOG1 ")
  		   		.append(" WHERE 1=1 ")
  		   		.append("  AND LOG1.").append(FatLogError.Fields.PMR_SEQ.name()).append(" = EPS.").append(FatEspelhoProcedSiscolo.Fields.PMR_SEQ.name())
  		   		.append("  AND LOG1.").append(FatLogError.Fields.MODULO.name()).append(" = LOG.").append(FatLogError.Fields.MODULO.name())
  		   		.append("  AND LOG1.").append(FatLogError.Fields.ERRO.name()).append(" = LOG.").append(FatLogError.Fields.ERRO.name())
  		   																		.append(" ) ")
  		   		
  		   .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append(" = :PRM_PHO_SEQ")
  		   .append("  AND IPH.").append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = VAS.").append(VFatAssociacaoProcedimento.Fields.IPH_SEQ.name())

		   .append("  AND EPS.").append(FatEspelhoProcedSiscolo.Fields.UNF_SEQ.name()).append(" = VUNF.").append(VAghUnidFuncional.Fields.SEQ.name())
		   .append("  AND PAC.").append(AipPacientes.Fields.CODIGO.name()).append(" = PMR.").append(FatProcedAmbRealizado.Fields.PAC_CODIGO.name())
		   .append(" ) ");

		return sql.toString();
	}
	
	public List<String> obterLogInconsistenciaBPACSV( final DominioModuloCompetencia[] modulo, 
												      final String[] erros, 
													  final String erro, 
													  final DominioSituacao situacao,
													  final Short pCpgCphCspSeq,
													  final Short pCpgCphCspCnvCodigo,
													  final Short pIphPhoSeq) throws ApplicationBusinessException {
		
		final String sql =  obterPrimeiraParteSQL() + " UNION " +
		   					obterSegundaParteSQL()  + " UNION " +
		   					obterTerceiraParteSQL();
		
		final SQLQuery query = createSQLQuery(sql);

		query.setString("PRM_MODULO1", modulo[0].toString());
		query.setString("PRM_MODULO2", modulo[1].toString());
		query.setString("PRM_MODULO3", modulo[2].toString());
		
		
		query.setString("PRM_IND_SITUACAO", situacao.toString());
		query.setString("PRM_ERRO", erro+"%");
		query.setParameterList("PRM_ERROS", erros);
		
		query.setShort("PRM_CPG_CPH_CSP_CNV_CODIGO", pCpgCphCspCnvCodigo);
		query.setShort("PRM_CPG_CPH_CSP_SEQ", pCpgCphCspSeq);
		query.setShort("PRM_IPH_PHO_SEQ", pIphPhoSeq);
		query.setShort("PRM_PHO_SEQ", pIphPhoSeq); 
		
		return query.addScalar("LINHA", StringType.INSTANCE).list();
	}
	

	/**
	 * ORADB: PROCEDURE LOG_AMB_CBO
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<LogInconsistenciaBPAVO> obterLogInconsistenciaBPAVO( final DominioModuloMensagem modulo, final String erro, 
																	 final DominioSituacaoMensagemLog situacao, 
																	 final Short pIphPhoSeq,
																	 final Short pTipoGrupoContaSUS, 
																	 final Short pCpgCphCspSeq, 
																	 final Short pCpgCphCspCnvCodigo 
																	 ) throws ApplicationBusinessException {
		
		final StringBuffer sql = new StringBuffer(5000);
		
		// Atenção com o order by ao se adicionar campos na consulta 
		sql.append(" SELECT ")
		   .append("        LOGE.").append(FatLogError.Fields.ERRO.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.ERRO.toString())
		   .append("	  , LOGE.").append(FatLogError.Fields.PAC_CODIGO.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.PAC_CODIGO.toString())
		   .append("      , PAC.").append(AipPacientes.Fields.PRONTUARIO.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.PRONTUARIO.toString())
		   .append("      , PAC.").append(AipPacientes.Fields.NOME.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.PAC_NOME.toString())
		   .append("      , LOGE.").append(FatLogError.Fields.PMR_SEQ.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.PRM_SEQ.toString())
		   .append("	  , LOGE.").append(FatLogError.Fields.PHI_SEQ.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.PHI.toString())
		   .append("	  , LOGE.").append(FatLogError.Fields.IPH_SEQ.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.IPH.toString())
		   .append("      , LOGE.").append(FatLogError.Fields.IPH_PHO_SEQ.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.IPH_PHO_SEQ.toString())
		   .append("      , PAR.").append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.DT_REALIZADO.toString())

		   .append("	  , (SELECT substr(rtrim(IPH.").append(FatItensProcedHospitalar.Fields.DESCRICAO.name()).append("),1,100) FROM AGH.")
		   					.append(FatItensProcedHospitalar.class.getAnnotation(Table.class).name()).append(" IPH WHERE IPH.")
		   							.append(FatItensProcedHospitalar.Fields.SEQ.name()).append(" = LOGE.").append(FatLogError.Fields.IPH_SEQ.name())
		   							.append(" AND IPH.").append(FatItensProcedHospitalar.Fields.PHO_SEQ.name()).append("= :PRM_IPH_PHO_SEQ ")
		   							.append(" ) as ").append(LogInconsistenciaBPAVO.Fields.DESCRICAO_IPH.toString())
		   							
		   .append("	  , (SELECT substr(rtrim(PHI_IN.").append(FatProcedHospInternos.Fields.DESCRICAO.name()).append("),1,100) FROM AGH.")
		   					.append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI_IN WHERE PHI_IN.")
		   							.append(FatProcedHospInternos.Fields.SEQ.name()).append(" = LOGE.").append(FatLogError.Fields.PHI_SEQ.name())
		   							.append(") as ").append(LogInconsistenciaBPAVO.Fields.DESCRICAO_PHI.toString())
		   							
		   .append("      , PAR.").append(FatProcedAmbRealizado.Fields.IND_ORIGEM.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.IND_ORIGEM.toString())
		   .append("	  , VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.COD_TABELA.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.COD_SUS.toString())
		   
		   .append(" FROM ")
		   .append("        AGH.").append(FatMensagemLog.class.getAnnotation(Table.class).name()).append(" MSL ")
		   .append("      , AGH.").append(FatLogError.class.getAnnotation(Table.class).name()).append(" LOGE ")
		   
		   .append("   LEFT OUTER JOIN AGH.").append(AipPacientes.class.getAnnotation(Table.class).name()).append(" PAC ON ")
						.append(" PAC.").append(AipPacientes.Fields.CODIGO.name()).append(" = LOGE.").append(FatLogError.Fields.PAC_CODIGO.name())
		   .append("   LEFT JOIN AGH.").append(FatProcedAmbRealizado.class.getAnnotation(Table.class).name())
		   			    .append(" PAR ON PAR.").append(FatProcedAmbRealizado.Fields.SEQ.name()).append(" = LOGE.").append(FatLogError.Fields.PMR_SEQ.name())
					
		   			    
		   			    
		   .append("   LEFT JOIN AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VFASSOCPROC ON ( ")

		   // convenio que tem na row da pmr (09/2011)
		   .append("	  VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :PRM_CPG_CPH_CSP_CNV_CODIGO") 

		   // plano que tem na row da pmr (09/2011)
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :PRM_CPG_CPH_CSP_SEQ ")         	   
		   
		   // grupo válido atualmente (09/2011)
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.name()).append(" = :PRM_TIPO_GRUPO_CONTA_SUS ")
		   
		   // tabela unificada.
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = :PRM_IPH_PHO_SEQ ")
		   
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = LOGE.").append(FatLogError.Fields.PHI_SEQ.name())
		   .append(" ) ")
		    
		   .append(" WHERE 1=1 ")
		   .append("  AND LOGE.").append(FatCompetencia.Fields.MODULO.name()).append(" = :PRM_MODULO ");
		   
		
		
		// And Loge.Erro     = Nvl(:P_Erro,Msl.Erro)
		if(erro != null){
			sql.append(" AND LOGE.").append(FatLogError.Fields.ERRO.name()).append(" = :PRM_ERRO ");
		}
			
		sql.append(" AND MSL.").append(FatMensagemLog.Fields.SITUACAO.name()).append(" = :PRM_SITUACAO ");
		
		sql.append(" AND LOGE.").append(FatLogError.Fields.ERRO.name()).append(" = MSL.").append(FatMensagemLog.Fields.ERRO.name())
		   .append(" AND MSL.").append(FatMensagemLog.Fields.MODULO.name()).append(" = LOGE.").append(FatLogError.Fields.MODULO.name())
		   
		   .append(" UNION ")
		   
		   .append(" SELECT ")
	   	   .append("        'Procedimento EXAME incluido na triagem emergencia' ")
	   	   .append("	  , 0 ")
	   	   .append("	  , 0 ")
		   .append("      , EMS.").append(MamItemExame.Fields.DESCRICAO.name())
		   .append("      , 0 ")
		   .append("	  , EMS.").append(MamItemExame.Fields.SEQ.name())
		   .append("	  , 0 ")
		   .append("	  , 0 ")
		   .append("	  , null ")
		   .append("	  , null ")
		   
		   .append("	  , (SELECT substr(rtrim(PHI_IN.").append(FatProcedHospInternos.Fields.DESCRICAO.name()).append("),1,100) FROM AGH.")
		   					.append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI_IN WHERE PHI_IN.")
		   							.append(FatProcedHospInternos.Fields.SEQ.name()).append(" = EMS.").append(MamItemExame.Fields.SEQ.name())
		   							.append(") as ").append(LogInconsistenciaBPAVO.Fields.DESCRICAO_PHI.toString())
           .append("	  , null ")
           .append("	  , VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.COD_TABELA.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.COD_SUS.toString())

		   .append(" FROM ")
		   .append("     AGH.").append(MamItemExame.class.getAnnotation(Table.class).name()).append(" EMS ")
		   
		   .append("     LEFT JOIN AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VFASSOCPROC ON ( ")

		   // convenio que tem na row da pmr (09/2011)
		   .append("	  VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :PRM_CPG_CPH_CSP_CNV_CODIGO") 

		   // plano que tem na row da pmr (09/2011)
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :PRM_CPG_CPH_CSP_SEQ ")         	   
		   
		   // grupo válido atualmente (09/2011)
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.name()).append(" = :PRM_TIPO_GRUPO_CONTA_SUS ")
		   
		   // tabela unificada.
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = :PRM_IPH_PHO_SEQ ")
		   
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = EMS.").append(MamItemExame.Fields.SEQ.name())
		   .append(" ) ")
		   
		   
		   .append(" WHERE ")
		   .append(" 	    EMS.").append(MamItemExame.Fields.CRIADO_EM.name()).append(" > :PRM_CRIADO_EM ")
		   .append("	AND NOT EXISTS (SELECT 1 FROM AGH.").append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI ")
		   								.append(" where PHI.").append(FatProcedHospInternos.Fields.EMS_SEQ.name())
		   									.append(" = EMS.").append(MamItemExame.Fields.SEQ.name()).append(") ")		
		   
		   .append(" UNION ")
		   
		   .append(" SELECT ")
	   	   .append("        'Procedimento MEDICAÇÃO  incluido na triagem emergencia' ")
	   	   .append("	  , 0 ")
	   	   .append("	  , 0 ")
	   	   .append("      , MDM.").append(MamItemMedicacao.Fields.DESCRICAO.name())
	   	   .append("	  , 0 ")
	   	   .append("      , MDM.").append(MamItemMedicacao.Fields.SEQ.name())
	   	   .append("      , 0 ")
	   	   .append("	  , 0 ")
		   .append("	  , null ")
		   .append("	  , null ")
		   .append("	  , (SELECT substr(rtrim(PHI_IN.").append(FatProcedHospInternos.Fields.DESCRICAO.name()).append("),1,100) FROM AGH.")
		   					.append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI_IN WHERE PHI_IN.")
		   							.append(FatProcedHospInternos.Fields.SEQ.name()).append(" = MDM.").append(MamItemExame.Fields.SEQ.name())
		   							.append(" ) as ").append(LogInconsistenciaBPAVO.Fields.DESCRICAO_PHI.toString())
           .append("	  , null ")
           .append("	  , VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.COD_TABELA.name()).append(" as ").append(LogInconsistenciaBPAVO.Fields.COD_SUS.toString())

		   							
	   	   .append(" FROM ")
		   .append("    AGH.").append(MamItemMedicacao.class.getAnnotation(Table.class).name()).append(" MDM ")
		   .append("     LEFT JOIN AGH.").append(VFatAssociacaoProcedimento.class.getAnnotation(Table.class).name()).append(" VFASSOCPROC ON ( ")

		   // convenio que tem na row da pmr (09/2011)
		   .append("	  VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.name()).append(" = :PRM_CPG_CPH_CSP_CNV_CODIGO") 

		   // plano que tem na row da pmr (09/2011)
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.name()).append(" = :PRM_CPG_CPH_CSP_SEQ ")         	   
		   
		   // grupo válido atualmente (09/2011)
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.CPG_GRC_SEQ.name()).append(" = :PRM_TIPO_GRUPO_CONTA_SUS ")
		   
		   // tabela unificada.
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.name()).append(" = :PRM_IPH_PHO_SEQ ")
		   
		   .append("  AND VFASSOCPROC.").append(VFatAssociacaoProcedimento.Fields.PHI_SEQ.name()).append(" = MDM.").append(MamItemMedicacao.Fields.SEQ.name())
		   .append(" ) ")
		   
		   .append(" WHERE ")
		   .append(" 	    MDM.").append(MamItemMedicacao.Fields.CRIADO_EM.name()).append(" > :PRM_CRIADO_EM ")
		   .append("	AND NOT EXISTS (SELECT 1 FROM AGH.").append(FatProcedHospInternos.class.getAnnotation(Table.class).name()).append(" PHI ")
		   								.append(" where PHI.").append(FatProcedHospInternos.Fields.MDM_SEQ.name())
		   									.append(" = MDM.").append(MamItemMedicacao.Fields.SEQ.name()).append(") ")	

		    // ATENÇÃO AO ODER BY, DEVE SER PELOS CAMPOS: ERRO, phi_seq E iph_seq 
		   .append(" ORDER BY 1, 5, 6 ");
		
		final SQLQuery query = createSQLQuery(sql.toString());
		
		query.setString("PRM_MODULO", modulo.toString());
		
		if(erro != null){
			query.setString("PRM_ERRO", erro);
		}
		
		query.setString("PRM_SITUACAO", situacao.toString());
		query.setDate("PRM_CRIADO_EM", DateUtil.obterDataComUltimoDiaMesAnterior(new Date()));
		query.setShort("PRM_TIPO_GRUPO_CONTA_SUS", pTipoGrupoContaSUS);
		query.setShort("PRM_CPG_CPH_CSP_SEQ", pCpgCphCspSeq);
		query.setShort("PRM_CPG_CPH_CSP_CNV_CODIGO", pCpgCphCspCnvCodigo);
		query.setShort("PRM_IPH_PHO_SEQ", pIphPhoSeq);
		
		final List<LogInconsistenciaBPAVO> result =  query.addScalar(LogInconsistenciaBPAVO.Fields.ERRO.toString(), StringType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.PAC_CODIGO.toString(),IntegerType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.PRONTUARIO.toString(),IntegerType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.PAC_NOME.toString(),StringType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.PRM_SEQ.toString(),IntegerType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.PHI.toString(),IntegerType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.IPH.toString(),IntegerType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.IPH_PHO_SEQ.toString(),ShortType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.DT_REALIZADO.toString(),TimestampType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.DESCRICAO_IPH.toString(), StringType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.DESCRICAO_PHI.toString(), StringType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.IND_ORIGEM.toString(), StringType.INSTANCE)
														  .addScalar(LogInconsistenciaBPAVO.Fields.COD_SUS.toString(), LongType.INSTANCE)
													      .setResultTransformer(Transformers.aliasToBean(LogInconsistenciaBPAVO.class)).list();

		return result;
	}
	
	public List<MsgErroCthSeqVO> listarMensagensLogErrosChtSeq(List<Integer> listaCthSeq){
		List<MsgErroCthSeqVO> result = null;
		
		StringBuffer hql = null;
		Query query = null;
		
		hql = new StringBuffer();
		hql.append(" select distinct ");
		hql.append("y.");
		hql.append(FatLogError.Fields.CTH_SEQ.toString());
		hql.append(" as cthSeq, ");
		hql.append("x.");
		hql.append(FatMensagemLog.Fields.ERRO.toString());
		hql.append(" as msgErro ");
		hql.append("from ");
		hql.append(FatMensagemLog.class.getName());
		hql.append(" x, ");
		hql.append(FatLogError.class.getName());
		hql.append(" y ");
		hql.append("where ");
		hql.append("y.");
		hql.append(FatLogError.Fields.ERRO.toString());
		hql.append(" = ");
		hql.append("x.");
		hql.append(FatMensagemLog.Fields.ERRO.toString());
		hql.append(" and ");
		hql.append("y.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(" in (:cthSeq)");
		hql.append(" and ");
		hql.append("x.");
		hql.append(FatMensagemLog.Fields.MODULO.toString());
		hql.append(" = :modulo ");
		hql.append(" and ");
		hql.append("x.");
		hql.append(FatMensagemLog.Fields.IND_SECRETARIO.toString());
		hql.append(" = :indSecretario ");
		hql.append(" order by ");
		hql.append("y.");
		hql.append(FatEspelhoAih.Fields.CTH_SEQ.toString());
		hql.append(", x.");
		hql.append(FatMensagemLog.Fields.ERRO.toString());
		hql.append(" desc");

		query = createHibernateQuery(hql.toString());

		query.setParameterList("cthSeq", listaCthSeq);
		query.setParameter("modulo", DominioModuloMensagem.INT);
		query.setParameter("indSecretario", "A");

		query.setResultTransformer(Transformers.aliasToBean(MsgErroCthSeqVO.class));

		result = query.list();
		return result;
	}
	
	/**
	 * #2152
	 * 
	 * @author thiago.cortes
	 * @param fatMensagemLogFiltro
	 * Listar todas as mensagens de acordo as restrições aplicadas
	 */

	public List<FatMensagemLog> listarMensagemLog(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			final FatMensagemLog fatMensagemLog) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatMensagemLog.class);

		inserirFiltro(criteria, fatMensagemLog);

		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.asc(FatMensagemLog.Fields.CODIGO.toString()));
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	/**
	 * #2152
	 * 
	 * @author thiago.cortes
	 * @param fatMensagemLog
	 * metodo para editar os filtros utilizados na pesquisa
	 */

	private void inserirFiltro(final DetachedCriteria criteria,
			FatMensagemLog fatMensagemLogFiltro) {

		if (fatMensagemLogFiltro.getCodigo() != null) {
			criteria.add(Restrictions.eq(
					FatMensagemLog.Fields.CODIGO.toString(),
					fatMensagemLogFiltro.getCodigo()));
		}
		if (StringUtils.isNotBlank(fatMensagemLogFiltro.getErro())) {
			criteria.add(Restrictions.ilike(FatMensagemLog.Fields.ERRO
					.toString(), fatMensagemLogFiltro.getErro(),
					MatchMode.ANYWHERE));
		}
		if (fatMensagemLogFiltro.getIndSecretario() != null) {
			criteria.add(Restrictions.eq(
					FatMensagemLog.Fields.IND_SECRETARIO.toString(),
					fatMensagemLogFiltro.getIndSecretario()));
		}
		if (fatMensagemLogFiltro.getSituacao() != null) {
			criteria.add(Restrictions.eq(
					FatMensagemLog.Fields.SITUACAO.toString(),
					fatMensagemLogFiltro.getSituacao()));
		}
		criteria.add(Restrictions.eq(FatMensagemLog.Fields.MODULO.toString(),
				DominioModuloMensagem.INT));
	}

	public Long pesquisarMensagemLogCount(FatMensagemLog fatMensagemLog) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatMensagemLog.class);
		inserirFiltro(criteria, fatMensagemLog);
		return this.executeCriteriaCount(criteria);
	}
	
	public FatMensagemLog obterFatMensagemLogPorCodigo(Integer codigo){
		return obterPorChavePrimaria(codigo);
		
	}

}