package br.gov.mec.aghu.transplante.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaFilaTransplante;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaSobrevidaTransplante;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioSituacaoTransplante;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;
import br.gov.mec.aghu.dominio.DominioTipoRetorno;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.MtxDoencaBases;
import br.gov.mec.aghu.model.MtxExtratoTransplantes;
import br.gov.mec.aghu.model.MtxOrigens;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.transplante.dao.MtxTipoRetornoDAO.AddCriteria;
import br.gov.mec.aghu.transplante.vo.AgendaTransplanteRetornoVO;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteOrgaosSituacaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioTransplanteTmoSituacaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteMedulaOsseaVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.RelatorioExtratoTransplantesPacienteVO;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class MtxTransplantesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxTransplantes> {
	private static final long serialVersionUID = 2574178840222530720L;
	
	private static final String ALIAS_DATA_SITUACAO_ATUAL = "dataSituacaoAtual";
	private static final String ALIAS_DATA_SITUACAO = "dataSituacao";
	private static final String ALIAS_COEFICIENTE = "coeficiente";
	
	

	/**
	 * #41772 - C5r
	 * @author marcelo.deus
	 */
	public MtxTransplantes obterTransplantePorSeq(Integer transplanteSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "MT");
		criteria.add(Restrictions.eq("MT." + MtxTransplantes.Fields.SEQ.toString(), transplanteSeq));
		return (MtxTransplantes) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #46359
	 * @param mtxOrigens 
	 * @return
	 */
	public Long pesquisarMtxOrigensEmMtxTransplantesCount(MtxOrigens mtxOrigens){
    	DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class);
    	criteria.add(Restrictions.eq(MtxTransplantes.Fields.ORIGEM.toString(), mtxOrigens));
    	return executeCriteriaCount(criteria);
    }
		
	/**
	 * #41790 C4
	 * @param prontuario
	 * @param data inicio
	 * @param data fim
	 * @param tipo orgaos
	 * @return Paciente transplante de orgao
	 */
	public List<PacienteTransplanteOrgaoVO> consultarPacientesTransplanteOrgaos(String prontuario, Date dataInicio, Date dataFim, DominioTipoOrgao tipoOrgaos, DominioOrdenacaoPesquisaFilaTransplante ordenacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class,"trp");
		criteria.createAlias("trp."+MtxTransplantes.Fields.RECEPTOR.toString(),"pac",JoinType.INNER_JOIN);
		Projection proj = Projections.projectionList()
				.add(Projections.property("pac."+AipPacientes.Fields.NOME.toString()),PacienteTransplanteOrgaoVO.Fields.NOME.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.PRONTUARIO.toString()),PacienteTransplanteOrgaoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.SEQ.toString()),PacienteTransplanteOrgaoVO.Fields.TRANSPLANTE_SEQ.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.DATA_INGRESSO.toString()), PacienteTransplanteOrgaoVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.TIPO_ORGAO.toString()), PacienteTransplanteOrgaoVO.Fields.ORGAO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.CRITERIO_PRIORIZACAO_SEQ.toString()), PacienteTransplanteOrgaoVO.Fields.CPT_SEQ.toString());
		criteria.setProjection(proj);
		criteria.add(Restrictions.isNull("trp."+MtxTransplantes.Fields.TIPO_TMO.toString()));

		if(prontuario != null && !"".equals(prontuario)){
			criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.PRONTUARIO.toString(), Integer.valueOf(prontuario)));
		}
		if(dataInicio != null && dataFim != null){
			criteria.add(Restrictions.between("trp."+MtxTransplantes.Fields.DATA_INGRESSO.toString(), dataInicio, dataFim));
		}
		if(tipoOrgaos != null && !"".equals(tipoOrgaos)){
			criteria.add(Restrictions.eq("trp."+MtxTransplantes.Fields.TIPO_ORGAO.toString(), tipoOrgaos));
		}
		if(ordenacao != null && !"".equals(ordenacao) && ordenacao.equals(DominioOrdenacaoPesquisaFilaTransplante.nome)){
			criteria.addOrder(Order.asc("pac."+ordenacao));
		}else if(ordenacao != null && !"".equals(ordenacao) && ordenacao.equals(DominioOrdenacaoPesquisaFilaTransplante.dataIngresso)) {
			criteria.addOrder(Order.asc("trp."+ordenacao));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteTransplanteOrgaoVO.class));
		return executeCriteria(criteria);
	}
	/**
	 * #41790 C5
	 * @param prontuario
	 * @param data inicio
	 * @param data fim
	 * @param tipo orgaos
	 * @return Paciente transplante de Medula Ossea
	 */
	public List<PacienteTransplanteMedulaOsseaVO> consultarPacientesTransplanteMedulaOssea(String prontuario, Date dataInicio, Date dataFim, DominioSituacaoTmo tipoTmo,DominioTipoAlogenico tipoTmoAlogenico, DominioOrdenacaoPesquisaFilaTransplante ordenacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class,"trp");
		criteria.createAlias("trp."+MtxTransplantes.Fields.RECEPTOR.toString(),"pac",JoinType.INNER_JOIN);
		Projection proj = Projections.projectionList()
				.add(Projections.property("pac."+AipPacientes.Fields.NOME.toString()),PacienteTransplanteMedulaOsseaVO.Fields.NOME.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.PRONTUARIO.toString()),PacienteTransplanteMedulaOsseaVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.DATA_NASCIMENTO.toString()),PacienteTransplanteMedulaOsseaVO.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.SEQ.toString()),PacienteTransplanteMedulaOsseaVO.Fields.TRANSPLANTE_SEQ.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.DATA_INGRESSO.toString()), PacienteTransplanteMedulaOsseaVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.CRITERIO_PRIORIZACAO_SEQ.toString()), PacienteTransplanteMedulaOsseaVO.Fields.CPT_SEQ.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.TIPO_TMO.toString()), PacienteTransplanteMedulaOsseaVO.Fields.TIPO_TMO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.TIPO_ALOGENICO.toString()), PacienteTransplanteMedulaOsseaVO.Fields.TIPO_ALOGENICO.toString());
		criteria.setProjection(proj);
		criteria.add(Restrictions.isNotNull("trp."+MtxTransplantes.Fields.TIPO_TMO.toString()));
				
		if(prontuario != null && !"".equals(prontuario)){
			criteria.add(Restrictions.eq("pac."+AipPacientes.Fields.PRONTUARIO.toString(),Integer.valueOf(prontuario)));
		}
		if(dataInicio != null && dataFim != null){
			criteria.add(Restrictions.between("trp."+MtxTransplantes.Fields.DATA_INGRESSO.toString(), dataInicio, dataFim));
		}
		if(tipoTmo != null && !"".equals(tipoTmo)){
			criteria.add(Restrictions.eq("trp."+MtxTransplantes.Fields.TIPO_TMO.toString(), tipoTmo));
		}
		if(tipoTmoAlogenico != null && !"".equals(tipoTmoAlogenico)){
			criteria.add(Restrictions.eq("trp." +MtxTransplantes.Fields.TIPO_ALOGENICO.toString(), tipoTmoAlogenico));
		}
		if(ordenacao != null && !"".equals(ordenacao) && ordenacao.equals(DominioOrdenacaoPesquisaFilaTransplante.nome)){
			criteria.addOrder(Order.asc("pac."+ordenacao));
		}else if(ordenacao != null && !"".equals(ordenacao) && ordenacao.equals(DominioOrdenacaoPesquisaFilaTransplante.dataIngresso)) {
			criteria.addOrder(Order.asc("trp."+ordenacao));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteTransplanteMedulaOsseaVO.class));
		return executeCriteria(criteria);
	}
		
	/**
	 * #41792 C4
	 * @param prontuario
	 * @param data inicio
	 * @param data fim
	 * @param tipo orgaos
	 * @return Paciente transplante de Medula Ossea
	 * 
	 * 
	 */
	public List<PacienteTransplanteMedulaOsseaVO> consultarPacientesTransplanteMedulaOsseaSobrevida(String prontuario, Date dataInicio, Date dataFim, DominioSituacaoTmo tipoTmo,DominioTipoAlogenico tipoTmoAlogenico, DominioOrdenacaoPesquisaSobrevidaTransplante ordenacao){
		StringBuilder sql = new StringBuilder(6500);
    	sql.append("SELECT PAC.PRONTUARIO as prontuario, ");
    	sql.append("PAC.NOME as nome, "); 
    	sql.append("TRP.SEQ as transplanteSeq, ");
    	sql.append("TRP.TIPO_TMO as tipoIndTmo,  ");
	    sql.append("TRP.IND_TIPO_ALOGENICO as tipoIndAlogenico, ");
    	sql.append("EXT.DATA_OCORRENCIA as dataTransplante ");
    	sql.append("FROM "); 
    	sql.append("MTX_TRANSPLANTES TRP, ");
    	sql.append("MTX_EXTRATO_TRANSPLANTES EXT, "); 
    	sql.append("AIP_PACIENTES PAC ");
    	sql.append("WHERE ");
    	sql.append("EXT.SITUACAO_TRANSPLANTE = 'T' AND ");
    	sql.append("EXT.TRP_SEQ = TRP.SEQ AND ");
    	sql.append("TRP.TIPO_TMO IS NOT NULL AND ");
    	sql.append("PAC.CODIGO = TRP.PAC_CODIGO_RECEPTOR ");
    	if(prontuario != null && !"".equals(prontuario)){
    		sql.append("AND PAC.PRONTUARIO = :prontuario  ");
    	}
    	if(dataInicio != null && dataFim != null){
    		sql.append("AND EXT.DATA_OCORRENCIA BETWEEN :dtInicio AND :dtFim ");
    	}
    	if(tipoTmo != null && !"".equals(tipoTmo)){
    		sql.append("AND TRP.TIPO_TMO = :tipoTmo ");
    	}
    	if(tipoTmoAlogenico != null && !"".equals(tipoTmoAlogenico)){
    		sql.append("AND TRP.IND_TIPO_ALOGENICO = :tipoTmoAlogenico ");
    	}
    	if(ordenacao.equals(DominioOrdenacaoPesquisaSobrevidaTransplante.NOME)){
    		sql.append("ORDER BY nome ");
    	}else if(ordenacao.equals(DominioOrdenacaoPesquisaSobrevidaTransplante.DATA_TRANSPLANTE)){
    		sql.append("ORDER BY dataTransplante");
    	}
		SQLQuery q = createSQLQuery(sql.toString());
		setarParametrosConsultarPacientesTransplanteMedulaOsseaSobrevida(prontuario, dataInicio, dataFim, tipoTmo, tipoTmoAlogenico, q);
    	q.setResultTransformer(Transformers.aliasToBean(PacienteTransplanteMedulaOsseaVO.class));
    	q.addScalar("prontuario",IntegerType.INSTANCE);
    	q.addScalar("nome",StringType.INSTANCE);
    	q.addScalar("transplanteSeq",IntegerType.INSTANCE);
    	q.addScalar("tipoIndTmo",StringType.INSTANCE);
    	q.addScalar("tipoIndAlogenico",StringType.INSTANCE);
    	q.addScalar("dataTransplante",TimestampType.INSTANCE);
    	return q.list();
	}
	private void setarParametrosConsultarPacientesTransplanteMedulaOsseaSobrevida(
		String prontuario, Date dataInicio, Date dataFim,DominioSituacaoTmo tipoTmo, DominioTipoAlogenico tipoTmoAlogenico,	SQLQuery q) {
		if(prontuario != null && !"".equals(prontuario)){
			q.setInteger("prontuario",Integer.valueOf(prontuario));
    	}
    	if(dataInicio != null && dataFim != null){
    		q.setDate("dtInicio", dataInicio);
    		q.setDate("dtFim", dataFim);
    	}
    	if(tipoTmo != null && !"".equals(tipoTmo)){
    		q.setString("tipoTmo",tipoTmo.toString());
    	}
    	if(tipoTmoAlogenico != null && !"".equals(tipoTmoAlogenico)){
    		q.setString("tipoTmoAlogenico",tipoTmoAlogenico.toString());
    	}
	}
		
	/**
	 * #41792 C3
	 * @param prontuario
	 * @param data inicio
	 * @param data fim
	 * @param tipo orgaos
	 * @return Paciente transplante de Medula Ossea
	 * 
	 * 
	 */
	public List<PacienteTransplanteOrgaoVO> consultarPacientesTransplanteOrgaosSobrevida(String prontuario, Date dataInicio, Date dataFim, DominioTipoOrgao dominioTipoOrgao, DominioOrdenacaoPesquisaSobrevidaTransplante ordenacao){
		StringBuilder sql = new StringBuilder(6500);
    	sql.append("SELECT PAC.PRONTUARIO as prontuario, ");
    	sql.append("PAC.NOME as nomePaciente, "); 
    	sql.append("TRP.SEQ as transplanteSeq, ");
    	sql.append("TRP.TIPO_ORGAO as tipoOrgao,  ");
    	sql.append("EXT.DATA_OCORRENCIA as dataTransplante ");
    	sql.append("FROM "); 
    	sql.append("MTX_TRANSPLANTES TRP ");
    	sql.append("JOIN MTX_EXTRATO_TRANSPLANTES EXT ON EXT.TRP_SEQ = TRP.SEQ "); 
    	sql.append("INNER JOIN AIP_PACIENTES PAC ON PAC.CODIGO = TRP.PAC_CODIGO_RECEPTOR ");
    	sql.append("WHERE ");
    	sql.append("EXT.SITUACAO_TRANSPLANTE = 'T' AND ");
    	sql.append("TRP.TIPO_TMO IS NULL ");
    	if(prontuario != null && !"".equals(prontuario)){
    		sql.append("AND PAC.PRONTUARIO = :prontuario  ");
    	}
    	if(dataInicio != null && dataFim != null){
    		sql.append("AND EXT.DATA_OCORRENCIA BETWEEN :dtInicio AND :dtFim ");
    	}
    	if(dominioTipoOrgao != null && !"".equals(dominioTipoOrgao)){
    		sql.append("AND TRP.TIPO_ORGAO = :tipoOrgao  ");
    	}
    	if(ordenacao.equals(DominioOrdenacaoPesquisaSobrevidaTransplante.NOME)){
    		sql.append("ORDER BY nomePaciente ");
    	}else if(ordenacao.equals(DominioOrdenacaoPesquisaSobrevidaTransplante.DATA_TRANSPLANTE)){
    		sql.append("ORDER BY dataTransplante");
    	}
    	SQLQuery q = createSQLQuery(sql.toString());
		setarParametrosConsultarPacientesTransplanteOrgaosSobrevida(prontuario,dataInicio, dataFim, dominioTipoOrgao, q);
    	q.setResultTransformer(Transformers.aliasToBean(PacienteTransplanteOrgaoVO.class));
    	q.addScalar("prontuario",IntegerType.INSTANCE);
    	q.addScalar("nomePaciente",StringType.INSTANCE);
    	q.addScalar("transplanteSeq",IntegerType.INSTANCE);
    	q.addScalar("tipoOrgao",StringType.INSTANCE);
    	q.addScalar("dataTransplante",TimestampType.INSTANCE);
    	return q.list();
 	}
	
	private void setarParametrosConsultarPacientesTransplanteOrgaosSobrevida(
		String prontuario, Date dataInicio, Date dataFim,DominioTipoOrgao dominioTipoOrgao, SQLQuery q) {
		if(prontuario != null && !"".equals(prontuario)){
			q.setInteger("prontuario",Integer.valueOf(prontuario));
    	}
    	if(dataInicio != null && dataFim != null){
    		q.setDate("dtInicio", dataInicio);
    		q.setDate("dtFim", dataFim);
    	}
       	if(dominioTipoOrgao != null && !"".equals(dominioTipoOrgao)){
    		q.setString("tipoOrgao",dominioTipoOrgao.toString());
    	}
	}
	
	public List<RelatorioExtratoTransplantesPacienteVO> pesquisarPacienteComObtitoListaTranplante(FiltroTempoPermanenciaListVO  filtro,Integer masSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class,"trp");
		criteria.createAlias("trp."+MtxTransplantes.Fields.RECEPTOR.toString(),"pac");
		Projection proj = Projections.projectionList()
				.add(Projections.property("pac."+AipPacientes.Fields.NOME.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.NOME.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.PRONTUARIO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.CODIGO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("pac."+AipPacientes.Fields.DT_OBITO.toString()),RelatorioExtratoTransplantesPacienteVO.Fields.DT_OBITO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.DATA_INGRESSO.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.DT_INGRESSO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.TIPO_ORGAO.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.TIPO_ORGAO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.TIPO_TMO.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.TIPO_TMO.toString())
				.add(Projections.property("trp."+MtxTransplantes.Fields.TIPO_ALOGENICO.toString()), RelatorioExtratoTransplantesPacienteVO.Fields.TIPO_ALOGENICO.toString());
		criteria.setProjection(proj);
		criteria.add(Restrictions.isNotNull("pac."+AipPacientes.Fields.DT_OBITO.toString()));
		criteria.add(Restrictions.ne("trp."+MtxTransplantes.Fields.SITUACAO.toString(),DominioSituacaoTransplante.S));
		if(filtro.getDataInicio() != null && filtro.getDataFim() != null){
			criteria.add(Restrictions.between("pac."+AipPacientes.Fields.DT_OBITO.toString(), filtro.getDataInicio(), DateUtil.obterDataComHoraFinal(filtro.getDataFim())));
		}
		partFiltroConsultaObito(filtro, criteria);
		if(filtro != null && filtro.getTipoAlogenico() != null){
			criteria.add(Restrictions.eq("trp."+MtxTransplantes.Fields.TIPO_ALOGENICO.toString(),filtro.getTipoAlogenico()));
		}
			
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MtxExtratoTransplantes.class,"ext");
				
		DominioSituacaoTransplante[] dominioSituacaosTransplante = new DominioSituacaoTransplante[2];
		dominioSituacaosTransplante[0] = DominioSituacaoTransplante.R;
		dominioSituacaosTransplante[1] = DominioSituacaoTransplante.I;
		
		ProjectionList proj2 = Projections.projectionList();
		proj2.add(Projections.property("ext."+MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()));
		proj2.add(Projections.property("ext."+MtxExtratoTransplantes.Fields.MTX_MOTIVO_ALTERA_SITUACAO_SEQ.toString()));
		
		subCriteria.setProjection(proj2);
		subCriteria.add(Restrictions.in("ext."+MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString(),dominioSituacaosTransplante));
		
		subCriteria.add(Restrictions.eq("ext."+MtxExtratoTransplantes.Fields.MTX_MOTIVO_ALTERA_SITUACAO_SEQ.toString(),masSeq));
		subCriteria.add(Restrictions.eqProperty("ext."+MtxExtratoTransplantes.Fields.MTX_TRANSPLANTE_SEQ.toString(), "trp."+MtxTransplantes.Fields.SEQ.toString()));
		
		DominioSituacaoTransplante[] situacoesTranplante = new DominioSituacaoTransplante[2];
		situacoesTranplante[0] = DominioSituacaoTransplante.E;
		situacoesTranplante[1] = DominioSituacaoTransplante.A;
		criteria.add(Restrictions.or(Subqueries.exists(subCriteria),
				(Restrictions.in("trp."+MtxTransplantes.Fields.SITUACAO.toString(),situacoesTranplante))));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioExtratoTransplantesPacienteVO.class));
		criteria.addOrder(Order.asc("pac."+AipPacientes.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("pac."+AipPacientes.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("trp."+MtxTransplantes.Fields.DATA_INGRESSO.toString()));
		return executeCriteria(criteria);
	}

	private void partFiltroConsultaObito(FiltroTempoPermanenciaListVO filtro,
			DetachedCriteria criteria) {
		if(filtro != null && filtro.getTipoTransplante() != null && filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)){
			criteria.add(Restrictions.isNotNull("trp."+MtxTransplantes.Fields.TIPO_TMO.toString()));
			if(filtro.getTipoTMO()!=null){
				criteria.add(Restrictions.eq("trp."+MtxTransplantes.Fields.TIPO_TMO.toString(),filtro.getTipoTMO()));
			}
		}
		
		if(filtro != null && filtro.getTipoTransplante() != null && filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.O)){
			criteria.add(Restrictions.isNotNull("trp."+MtxTransplantes.Fields.TIPO_ORGAO.toString()));
			if(filtro.getTipoOrgao()!=null){
				criteria.add(Restrictions.eq("trp."+MtxTransplantes.Fields.TIPO_ORGAO.toString(),filtro.getTipoOrgao()));
			}
		}
	}	
		
	/**
	 * #41787 - C1
	 * @author romario.caldeira
	 */
	public ListarTransplantesVO obterPacientePorCodTransplante(Integer codTransplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), ListarTransplantesVO.Fields.PRONTUARIO_PACIENTE.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), ListarTransplantesVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.CODIGO), ListarTransplantesVO.Fields.CODIGO_PACIENTE.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_TMO), ListarTransplantesVO.Fields.TIPO_TRANSPLANTE_TMO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ALOGENICO), ListarTransplantesVO.Fields.TIPO_ALOGENICO.toString())
				);
		
		criteria.createAlias("PAC." + AipPacientes.Fields.TRANSPLANTE_RECEPTOR.toString(), "TRP");
		criteria.setResultTransformer(Transformers.aliasToBean(ListarTransplantesVO.class));
		if (codTransplante != null) {
			criteria.add(Restrictions.eq("TRP." + MtxTransplantes.Fields.SEQ.toString(), codTransplante));
		}
		return (ListarTransplantesVO) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria montarConsultaPacientesAguardandoTransplantePorFiltro(ListarTransplantesVO filtro, Boolean verificaObito){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "MTX");
		//JOIN
		criteria.createAlias("MTX." + MtxTransplantes.Fields.RECEPTOR.toString(), "PAC");
		criteria.createAlias("MTX." + MtxTransplantes.Fields.CRITERIO_PRIORIZACAO.toString(), "CPT");
		//SELECT
		criteria.setProjection(gerarProjecoesConsultaPacientesAguardandoTransplantePorFiltro(verificaObito, filtro));
		//WHERE
		if(filtro.getSelecioneAba() == 3 && filtro.getSelecioneAba() != null){
			criteria.createAlias("MTX." + MtxTransplantes.Fields.EXTRATO_TRANSPLANTE.toString(), "MTE");
			criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.T));
			criteria.add(Restrictions.eqProperty("MTX." + MtxTransplantes.Fields.SITUACAO.toString(), 
					"MTE."+MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()));
			if(filtro.getDataTransplante() != null){
				StringBuilder sql= new StringBuilder(50);
				sql.append(" trunc(mte3_.data_ocorrencia) = '"+DateUtil.obterDataFormatada(filtro.getDataTransplante(), "dd/MM/yyyy")+"' ");
				criteria.add(Restrictions.sqlRestriction(sql.toString()));
			}
		}else if(filtro.getSelecioneAba() == 2 && filtro.getSelecioneAba() != null){
			criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.E));
		}else if(filtro.getSelecioneAba() == 4 && filtro.getSelecioneAba() != null){
			criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.I));
		}else if(filtro.getSelecioneAba() == 5 && filtro.getSelecioneAba() != null){
			criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.S));
		}else{
			criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.A));
		}
		if(filtro != null){
			if(filtro.getCodigoPaciente() != null){
				criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), filtro.getCodigoPaciente()));
			}
			if(filtro.getNomePaciente() != null){
				criteria.add(Restrictions.like("PAC." + AipPacientes.Fields.NOME.toString(), StringUtils.upperCase(filtro.getNomePaciente()), MatchMode.ANYWHERE));
			}
			if(filtro.getProntuarioPaciente() != null){
				criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), filtro.getProntuarioPaciente()));
			}
			if(filtro.getDataInclusao()!= null){
				criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.DATA_INGRESSO.toString(), filtro.getDataInclusao()));
			}
			if(filtro.getTransplanteTipoTmo() != null){
				criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.TIPO_TMO.toString(), filtro.getTransplanteTipoTmo()));
			}
			if(filtro.getTransplanteTipoAlogenico() != null){
				criteria.add(Restrictions.eq("MTX." + MtxTransplantes.Fields.TIPO_ALOGENICO.toString(), filtro.getTransplanteTipoAlogenico()));
			}
			
		}
		return criteria;
	}
	
	private ProjectionList gerarProjecoesConsultaPacientesAguardandoTransplantePorFiltro(Boolean verificaObito, ListarTransplantesVO filtro){
		ProjectionList projList = Projections.projectionList();
		if(verificaObito){
			projList.add(Projections.property("MTX." + MtxTransplantes.Fields.SEQ.toString()),
					MtxTransplantes.Fields.SEQ.toString());
		} else {
			StringBuilder consultaCoeficiente = new StringBuilder(500);
			StringBuilder consultaPermanencia = new StringBuilder(500);
			StringBuilder consultaEscore = new StringBuilder(500);
			StringBuilder consultaTemDiabetes = new StringBuilder(500);
			StringBuilder consultaTemHIV = new StringBuilder(500);
			StringBuilder consultaTemHepatiteB = new StringBuilder(500);
			StringBuilder consultaTemHepatiteC = new StringBuilder(500);
			StringBuilder verificarMaterialBiologicoInformado = new StringBuilder(500);
			StringBuilder dataSituacao = new StringBuilder(500);
			StringBuilder dataSituacaoAtual = new StringBuilder(500);
			StringBuilder permanencia = new StringBuilder(500);
			StringBuilder escore = new StringBuilder(500);
			
			String consultaTemGerme = "(select count(*) from mci_notificacao_gmr mci where mci.pac_codigo = pac1_.CODIGO and ind_notificacao_ativa = 'S') temGermeMultiresistente ";
			consultaPermanencia.append("(SELECT CURRENT_DATE - CAST(MTXP.DATA_INGRESSO AS DATE) FROM MTX_TRANSPLANTES MTXP WHERE MTXP.SEQ = this_.seq) permanencia ");
			consultaEscore.append("(SELECT FLOOR(CURRENT_DATE - CAST(MTXE.DATA_INGRESSO AS DATE)) * 0.33 ")
					.append("+(select  cpte.gravidade + cpte.criticidade from AGH.mtx_criterio_priorizacoes_tmo cpte where cpte.seq = MTXE.cpt_seq) ")
					.append("+(CASE WHEN ((CURRENT_DATE - CAST(PACE.DT_NASCIMENTO AS DATE))/365) < 13 THEN 20 ELSE 0 END) ")
					.append("FROM MTX_TRANSPLANTES MTXE INNER JOIN AIP_PACIENTES PACE ")
					.append("ON MTXE.PAC_CODIGO_RECEPTOR = PACE.CODIGO ")
					.append("WHERE MTXE.PAC_CODIGO_RECEPTOR = pac1_.codigo and MTXE.SEQ = {alias}.seq) escore");
			consultaTemDiabetes.append("(select count(*) from agh_cids cid, mam_diagnosticos dia ")
					.append("where DIA.PAC_CODIGO = pac1_.codigo and dia.ind_pendente = 'V' ")
					.append("and dia.dthr_valida_mvto is null ")
					.append("and cid.seq = dia.cid_seq ")
					.append("and (cid.codigo like 'E10%' or cid.codigo like 'E11%')) temDiabetes");
			consultaTemHIV.append("(select count(*) from agh_cids cid, mam_diagnosticos dia ")
				 	.append("where DIA.PAC_CODIGO = pac1_.codigo and dia.ind_pendente = 'V' ")
				 	.append("and dia.dthr_valida_mvto is null ")
				 	.append("and cid.seq = dia.cid_seq ")
				 	.append("and (cid.codigo between 'B20' and 'B24.9' or  cid.codigo = 'F02.4' ")
				    .append("or  cid.codigo = 'R75' or  cid.codigo = 'Z21')) temHIV");
			consultaTemHepatiteB.append("(select count(*) from agh_cids cid, mam_diagnosticos dia ")
				 	.append("where DIA.PAC_CODIGO = pac1_.codigo and dia.ind_pendente = 'V' ")
				 	.append("and dia.dthr_valida_mvto is null ")
				 	.append("and cid.seq = dia.cid_seq ")
				 	.append("and (cid.codigo like 'B16%' or  cid.codigo = 'B18.0' or  cid.codigo = 'B18.1')) temHepatiteB ");
			consultaTemHepatiteC.append("(select count(*) from agh_cids cid, mam_diagnosticos dia ")
		 			.append("where DIA.PAC_CODIGO = pac1_.codigo and dia.ind_pendente = 'V' ")
		 			.append("and dia.dthr_valida_mvto is null ")
		 			.append("and cid.seq = dia.cid_seq ")
		 			.append("and (cid.codigo = 'B17.1' or cid.codigo = 'B18.2')) temHepatiteC ");
			consultaCoeficiente.append("(select  cpt.gravidade + cpt.criticidade from mtx_criterio_priorizacoes_tmo cpt where cpt.seq = cpt2_.SEQ) ");
			
			//C10
			verificarMaterialBiologicoInformado.append("(select count(*) from mtx_coleta_materiais_tmo cmt ")
					.append(" where cmt.PAC_CODIGO = CASE ")
					.append(" when THIS_.tipo_TMO = 'G' then THIS_.PAC_CODIGO_DOADOR else THIS_.PAC_CODIGO_RECEPTOR END) verificarMaterialBiologico ");
			//C8
			dataSituacao.append("(select EXT.DATA_OCORRENCIA DATA_SITUACAO from MTX_EXTRATO_TRANSPLANTES EXT")
				.append(" where EXT.TRP_SEQ = this_.seq and EXT.SITUACAO_TRANSPLANTE = 'E' and ext.criado_em = ")
				.append("(select min(ext2.criado_em) from MTX_EXTRATO_TRANSPLANTES EXT2 ")
				.append(" where EXT2.TRP_SEQ = this_.seq and EXT2.SITUACAO_TRANSPLANTE = 'E') )");
			
			//C9
			dataSituacaoAtual.append("(select EXT.DATA_OCORRENCIA DATA_SITUACAO from MTX_EXTRATO_TRANSPLANTES EXT ")
					.append(" where EXT.TRP_SEQ = this_.seq and EXT.SITUACAO_TRANSPLANTE = this_.situacao and ext.seq= ")
					.append(" (select max(ext2.seq) from MTX_EXTRATO_TRANSPLANTES EXT2 where EXT2.TRP_SEQ = this_.seq ")
					.append(" and EXT2.SITUACAO_TRANSPLANTE = EXT.SITUACAO_TRANSPLANTE))");
			
			projList.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), 
						ListarTransplantesVO.Fields.NOME_PACIENTE.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()), 
						ListarTransplantesVO.Fields.CODIGO_PACIENTE.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), 
						ListarTransplantesVO.Fields.PRONTUARIO_PACIENTE.toString())
				.add(Projections.property("CPT." + MtxCriterioPriorizacaoTmo.Fields.SEQ.toString()), 
						ListarTransplantesVO.Fields.CRITERIO_SEQ.toString())
				.add(Projections.property("CPT." + MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO.toString()), 
						ListarTransplantesVO.Fields.CRITERIO_STATUS.toString())
				.add(Projections.property("MTX."+MtxTransplantes.Fields.SEQ.toString()),
						ListarTransplantesVO.Fields.TRP_SEQ.toString())
				.add(Projections.property("MTX." + MtxTransplantes.Fields.SEQ.toString()), 
						ListarTransplantesVO.Fields.CODIGO_TRANSPLANTE.toString())
				.add(Projections.property("MTX." + MtxTransplantes.Fields.CRITERIO_PRIORIZACAO_SEQ.toString()), 
						ListarTransplantesVO.Fields.TRANSPLANTE_CRITERIO_SEQ.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.SEXO.toString()), 
						ListarTransplantesVO.Fields.SEXO_PACIENTE.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), 
						ListarTransplantesVO.Fields.DT_NASCIMENTO_PACIENTE.toString())
				.add(Projections.property("MTX." + MtxTransplantes.Fields.TIPO_TMO.toString()), 
						ListarTransplantesVO.Fields.TIPO_TRANSPLANTE_TMO.toString())
				.add(Projections.property("MTX." + MtxTransplantes.Fields.TIPO_ALOGENICO.toString()), 
						ListarTransplantesVO.Fields.TIPO_ALOGENICO.toString())
				.add(Projections.property("MTX." + MtxTransplantes.Fields.DATA_INGRESSO.toString()), 
						ListarTransplantesVO.Fields.DATA_INCLUSAO.toString())
				.add(Projections.property("PAC." + AipPacientes.Fields.DT_OBITO.toString()), 
						ListarTransplantesVO.Fields.DT_OBITO_PACIENTE.toString())
				.add(Projections.property("MTX."+MtxTransplantes.Fields.SITUACAO.toString()),
						ListarTransplantesVO.Fields.SITUACAO_TRANSPLANTE.toString())
				.add(Projections.property("MTX."+MtxTransplantes.Fields.DOADOR_CODIGO.toString()),
						ListarTransplantesVO.Fields.CODIGO_PACIENTE_DOADOR.toString())	
				.add(Projections.property("MTX."+MtxTransplantes.Fields.RECEPTOR_CODIGO.toString()),
						ListarTransplantesVO.Fields.CODIGO_PACIENTE_RECEPTOR.toString())
				.add(Projections.sqlProjection(consultaCoeficiente.toString()+ALIAS_COEFICIENTE
						, new String [] {"coeficiente"}, new Type[] {new IntegerType()}))
				.add(Projections.sqlProjection(consultaTemGerme
						, new String [] {"temGermeMultiresistente"}, new Type[] {new IntegerType()}))
				.add(Projections.sqlProjection(consultaTemDiabetes.toString()
						, new String [] {"temDiabetes"}, new Type[] {new IntegerType()}))
				.add(Projections.sqlProjection(consultaTemHIV.toString()
						, new String [] {"temHIV"}, new Type[] {new IntegerType()}))
				.add(Projections.sqlProjection(consultaTemHepatiteB.toString()
						, new String [] {"temHepatiteB"}, new Type[] {new IntegerType()}))
				.add(Projections.sqlProjection(consultaTemHepatiteC.toString()
						, new String [] {"temHepatiteC"}, new Type[] {new IntegerType()}))
				.add(Projections.sqlProjection(dataSituacao.toString()+ALIAS_DATA_SITUACAO
						, new String [] {"dataSituacao"}, new Type[] {new DateType()}))
				.add(Projections.sqlProjection(dataSituacaoAtual.toString()+ALIAS_DATA_SITUACAO_ATUAL
						, new String [] {"dataSituacaoAtual"}, new Type[] {new DateType()}))

				.add(Projections.sqlProjection(verificarMaterialBiologicoInformado.toString()
						, new String [] {"verificarMaterialBiologico"}, new Type[] {new IntegerType()}));
			
			if(filtro.getSelecioneAba() == 3){
				permanencia.append(" FLOOR ("+dataSituacaoAtual.toString()+ " - "+ dataSituacao.toString()+") ");
			}else{
				permanencia.append(" FLOOR (CURRENT_DATE - "+ dataSituacao.toString()+") ");
			}
			
			escore.append(permanencia + " * (0.33) + "+consultaCoeficiente+"+(CASE WHEN ((CURRENT_DATE - CAST(PAC1_.DT_NASCIMENTO AS DATE))/365) < 13 THEN 20 ELSE 0 END) escore ");
			
			if(filtro.getSelecioneAba() == 1 && filtro.getSelecioneAba() != null){
				projList.add(Projections.sqlProjection(consultaEscore.toString()
						, new String [] {"escore"}, new Type[] {new DoubleType()}));
				projList.add(Projections.sqlProjection(consultaPermanencia.toString()
						, new String [] {"permanencia"}, new Type[] {new IntegerType()}));
			}else{
				projList.add(Projections.sqlProjection(escore.toString()
						, new String [] {"escore"}, new Type[] {new DoubleType()}));
				projList.add(Projections.sqlProjection(permanencia.toString()+" permanencia "
						, new String [] {"permanencia"}, new Type[] {new IntegerType()}));
			}
		}
		return projList;

	}
	
	public List<ListarTransplantesVO> obterPacientesAguardandoTransplantePorFiltro(ListarTransplantesVO filtro, Integer firstResult, Integer maxResults, 
			String orderProperty, boolean asc){
		DetachedCriteria criteria = montarConsultaPacientesAguardandoTransplantePorFiltro(filtro, false);
		criteria.setResultTransformer(Transformers.aliasToBean(ListarTransplantesVO.class));
		if(orderProperty == null || StringUtils.isEmpty(orderProperty)){ 
			orderProperty = MtxTransplantes.Fields.DATA_INGRESSO.toString();
		} else if(orderProperty.equals("dataNascimentoPaciente")){
			asc = !asc;
		} else if(orderProperty.equals("escore")){
			orderProperty = null;
			if(asc){
                criteria.addOrder(OrderBySql.sql("escore asc"));
	         } else {
	            criteria.addOrder(OrderBySql.sql("escore desc"));
	         }
		} else if(orderProperty.equals("permanencia")){
			orderProperty = null;
			if(asc){
				criteria.addOrder(OrderBySql.sql("permanencia asc"));
			} else {
				criteria.addOrder(OrderBySql.sql("permanencia desc"));
			}
		}
		else if(orderProperty.equals("dataSituacao")){
			orderProperty = null;
			if(asc){
				criteria.addOrder(OrderBySql.sql("dataSituacao asc"));
			} else {
				criteria.addOrder(OrderBySql.sql("dataSituacao desc"));
			}
		}else if(orderProperty.equals("dataSituacaoAtual")){
			orderProperty = null;
			if(asc){
				criteria.addOrder(OrderBySql.sql("dataSituacaoAtual asc"));
			} else {
				criteria.addOrder(OrderBySql.sql("dataSituacaoAtual desc"));
			}
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long obterPacientesAguardandoTransplantePorFiltroCount(ListarTransplantesVO filtro){
		DetachedCriteria criteria = montarConsultaPacientesAguardandoTransplantePorFiltro(filtro, false);
		criteria.setResultTransformer(Transformers.aliasToBean(ListarTransplantesVO.class));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MtxTransplantes> obterTransplantesParaAtualizar(ListarTransplantesVO filtro){
		DetachedCriteria criteria = montarConsultaPacientesAguardandoTransplantePorFiltro(filtro, true);
		criteria.add(Restrictions.isNotNull("PAC." + AipPacientes.Fields.DT_OBITO.toString()));
		
		DetachedCriteria criteriaPrincipal = DetachedCriteria.forClass(MtxTransplantes.class, "MTXP");
		criteriaPrincipal.add(Subqueries.propertyIn("MTXP." + MtxTransplantes.Fields.SEQ.toString(), criteria));
		
		return executeCriteria(criteriaPrincipal);
	}
	
	public MtxTransplantes obterTransplanteEdicao(Integer transplanteSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias("TRP." + MtxTransplantes.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TRP." + MtxTransplantes.Fields.ORIGEM.toString(), "ORI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TRP." + MtxTransplantes.Fields.CRITERIO_PRIORIZACAO.toString(), "CRI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TRP." + MtxTransplantes.Fields.DOADOR.toString(), "DOA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TRP." + MtxTransplantes.Fields.RECEPTOR.toString(), "REC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("TRP." + MtxTransplantes.Fields.SEQ.toString(), transplanteSeq));
		
		return (MtxTransplantes)executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * #41798
	 * Consulta - C1
	 * @param pacCodigo
	 * @param nome
	 * @param prontuario
	 * @param dataInclusao
	 * @param rgct
	 * @param tipoOrgao
	 * @return
	 */
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesAguardandoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc, boolean paginacao){
		
		DetachedCriteria criteria = obterCriteriaListaTransplanteOrgao(filtro, orderProperty);
		
		if(paginacao){
			
			if(orderProperty == null || orderProperty.trim().isEmpty()){
				criteria.addOrder(Order.asc("TRP."+MtxTransplantes.Fields.DATA_INGRESSO.toString()));
			}else if(orderProperty.equals("permanencia")){
				orderProperty = null;
				if(asc){
					criteria.addOrder(OrderBySql.sql("permanencia asc"));
				} else {
					criteria.addOrder(OrderBySql.sql("permanencia desc"));
				}
			}else if(orderProperty.equals("dataRetirada")){
				orderProperty = null;
				if(asc){
					criteria.addOrder(OrderBySql.sql("dataRetirada asc"));
				} else {
					criteria.addOrder(OrderBySql.sql("dataRetirada desc"));
				}
			}
			
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}else{
			return executeCriteria(criteria);
		}
	}

	/**
	 * Método para obter criteria utilizadas em método
	 * Long obterListaPacientesAguardandoTransplanteOrgaoCount e 
	 * List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesAguardandoTransplanteOrgao
	 * @param filtro
	 * @return
	 */
	private DetachedCriteria obterCriteriaListaTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, String orderProperty){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias("TRP."+MtxTransplantes.Fields.RECEPTOR.toString(), "PAC");
		criteria.createAlias("TRP."+MtxTransplantes.Fields.DOENCA_BASE.toString(),"DOB", JoinType.LEFT_OUTER_JOIN);
		
		filtroObterCriteria(filtro, criteria);
		
		StringBuilder sqlDataRetirada = new StringBuilder(300);
		StringBuilder sqlPermanencia = new StringBuilder(350);
		//C8 #41801 thiago.cortes
		sqlDataRetirada.append(" (SELECT max(etx.DATA_OCORRENCIA) FROM  MTX_EXTRATO_TRANSPLANTES etx  "
				+ "where etx.TRP_SEQ = this_.seq and etx.SITUACAO_TRANSPLANTE = 'R' ) ");
		
		sqlPermanencia.append(" ( "+sqlDataRetirada+" - this_.DATA_INGRESSO) permanencia");
		
		projectionObterCriteria(criteria, sqlDataRetirada, sqlPermanencia);
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteAguardandoTransplanteOrgaoVO.class));
		
		if(filtro.getSelectTab() == 4 && filtro.getSelectTab() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.R));	
		}
		else{
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.E));
		}
		
		return criteria;
	}

	private void filtroObterCriteria(FiltroTransplanteOrgaoVO filtro,
			DetachedCriteria criteria) {
		if(filtro.getPacCodigo() != null){
			criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.CODIGO.toString(), filtro.getPacCodigo()));
		}
		
		if(filtro.getNomePaciente() != null && !filtro.getNomePaciente().trim().isEmpty()){
			criteria.add(Restrictions.ilike("PAC."+AipPacientes.Fields.NOME.toString(), filtro.getNomePaciente(), MatchMode.ANYWHERE));
		}

		if(filtro.getProntuario() != null){
			criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.PRONTUARIO.toString(), filtro.getProntuario()));
		}
		
		if(filtro.getDataInclusao() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.DATA_INGRESSO.toString(), filtro.getDataInclusao()));
		}
		
		if(filtro.getRgct() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.RGCT.toString(), filtro.getRgct()));
		}
		
		if(filtro.getDominioTipoOrgao() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.TIPO_ORGAO.toString(), filtro.getDominioTipoOrgao()));
		}
		criteria.add(Restrictions.isNull("TRP."+MtxTransplantes.Fields.TIPO_TMO.toString()));
	}

	private void projectionObterCriteria(DetachedCriteria criteria,
			StringBuilder sqlDataRetirada, StringBuilder sqlPermanencia) {
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlDataRetirada.toString()+PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_RETIRADA.toString()
						, new String [] {PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_RETIRADA.toString()}, new Type[] {new DateType()}))
				.add(Projections.sqlProjection(sqlPermanencia.toString()
						, new String [] {PacienteAguardandoTransplanteOrgaoVO.Fields.PERMANENCIA.toString()}, new Type[] {new IntegerType()}))
						
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), PacienteAguardandoTransplanteOrgaoVO.Fields.NOME.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SEQ), PacienteAguardandoTransplanteOrgaoVO.Fields.SEQ.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), PacienteAguardandoTransplanteOrgaoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("DOB."+MtxDoencaBases.Fields.DESCRICAO), PacienteAguardandoTransplanteOrgaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.SEXO), PacienteAguardandoTransplanteOrgaoVO.Fields.SEXO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_NASCIMENTO), PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ORGAO), PacienteAguardandoTransplanteOrgaoVO.Fields.TIPO_ORGAO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.DATA_INGRESSO), PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_OBITO), PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_OBITO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.CODIGO_RECEPTOR), PacienteAguardandoTransplanteOrgaoVO.Fields.CODIGO_RECEPTOR.toString())
				);
	}
	
	/**
	 * #41720
	 * Consulta - C1
	 * @param filtro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param paginacao
	 * @return
	 */
	public List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesInativoAguardandoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc, boolean paginacao){
		
		DetachedCriteria criteria = obterCriteriaListaPacienteInativoTransplanteOrgao(filtro, orderProperty);
		if(orderProperty != null && orderProperty.equals("dataRegistroInativado")){
			orderProperty = null; //A entidade não possui dataRegistroInativado para ordenar, esta ordenação será feita pelo java.
		}
		if(orderProperty != null && orderProperty.equals("pacDtNascimento")){
			asc = !asc;
		}
		if(paginacao){
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
		}else{
			return executeCriteria(criteria);
		}
	}

	/**
	 * Método para obter criteria utilizadas em método
	 * Long obterListaPacientesAguardandoTransplanteOrgaoCount e 
	 * List<PacienteAguardandoTransplanteOrgaoVO> obterListaPacientesInativoAguardandoTransplanteOrgao
	 * @param filtro
	 * @param orderProperty
	 * @return
	 */
	private DetachedCriteria obterCriteriaListaPacienteInativoTransplanteOrgao(FiltroTransplanteOrgaoVO filtro, String orderProperty){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias("TRP."+MtxTransplantes.Fields.RECEPTOR.toString(), "PAC");
		criteria.createAlias("TRP."+MtxTransplantes.Fields.CID.toString(), "CID", JoinType.LEFT_OUTER_JOIN);
		
		if(filtro.getPacCodigo() != null){
			criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.CODIGO.toString(), filtro.getPacCodigo()));
		}
		
		if(filtro.getNomePaciente() != null && !filtro.getNomePaciente().trim().isEmpty()){
			criteria.add(Restrictions.ilike("PAC."+AipPacientes.Fields.NOME.toString(), filtro.getNomePaciente(), MatchMode.ANYWHERE));
		}

		if(filtro.getProntuario() != null){
			criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.PRONTUARIO.toString(), filtro.getProntuario()));
		}
		
		if(filtro.getDataInclusao() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.DATA_INGRESSO.toString(), filtro.getDataInclusao()));
		}
		
		if(filtro.getRgct() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.RGCT.toString(), filtro.getRgct()));
		}
		
		if(filtro.getDominioTipoOrgao() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.TIPO_ORGAO.toString(), filtro.getDominioTipoOrgao()));
		}
		
		criteria.add(Restrictions.isNull("TRP."+MtxTransplantes.Fields.TIPO_TMO.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), PacienteAguardandoTransplanteOrgaoVO.Fields.NOME.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SEQ), PacienteAguardandoTransplanteOrgaoVO.Fields.SEQ.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), PacienteAguardandoTransplanteOrgaoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("CID."+AghCid.Fields.DESCRICAO), PacienteAguardandoTransplanteOrgaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.SEXO), PacienteAguardandoTransplanteOrgaoVO.Fields.SEXO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_NASCIMENTO), PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ORGAO), PacienteAguardandoTransplanteOrgaoVO.Fields.TIPO_ORGAO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.DATA_INGRESSO), PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_OBITO), PacienteAguardandoTransplanteOrgaoVO.Fields.DATA_OBITO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.CODIGO_RECEPTOR), PacienteAguardandoTransplanteOrgaoVO.Fields.CODIGO_RECEPTOR.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteAguardandoTransplanteOrgaoVO.class));
		
		criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.I));
		
		if(orderProperty == null || orderProperty.trim().isEmpty()){
			criteria.addOrder(Order.asc("TRP."+MtxTransplantes.Fields.DATA_INGRESSO.toString()));
		}
		
		return criteria;
	}
	
	
	/**
	 * #41807
	 * @author thiago.cortes
	 * @param filtro, firstResult, maxResults, orderProperty, asc
	 * @return List<PacienteAguardandoTransplanteOrgaoVO>
	 */
	public List<PacienteTransplantadosOrgaoVO> obterListaPacientesTransplantadosOrgao(FiltroTransplanteOrgaoVO filtro, 
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc){
		
		DetachedCriteria criteria = criteriaListaPacientesTransplantadosOrgao(filtro);
		
			if(orderProperty == null || orderProperty.trim().isEmpty()){
				criteria.addOrder(Order.asc("TRP."+MtxTransplantes.Fields.DATA_INGRESSO.toString()));
			}else if(orderProperty.equals("permanencia")){
				orderProperty = null;
				if(asc){
					criteria.addOrder(OrderBySql.sql("permanencia asc"));
				} else {
					criteria.addOrder(OrderBySql.sql("permanencia desc"));
				}
			}else if(orderProperty.equals("pacDtNascimento")){
				orderProperty = null;
				if(asc){
					criteria.addOrder(OrderBySql.sql("DT_NASCIMENTO desc"));
				} else {
					criteria.addOrder(OrderBySql.sql("DT_NASCIMENTO asc"));
				}
			}
			
			return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	public Long obterListaPacientesTransplantadosOrgaoCount(FiltroTransplanteOrgaoVO filtro){
		DetachedCriteria criteria = criteriaListaPacientesTransplantadosOrgao(filtro);
		return executeCriteriaCount(criteria);
	}

	/**
	 * #41807
	 * @author thiago.cortes
	 * @return DetachedCriteria
	 */
	private DetachedCriteria criteriaListaPacientesTransplantadosOrgao(FiltroTransplanteOrgaoVO filtro){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias("TRP."+MtxTransplantes.Fields.RECEPTOR.toString(), "PAC");
		criteria.createAlias("TRP."+MtxTransplantes.Fields.DOENCA_BASE.toString(), "DOB",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("TRP." + MtxTransplantes.Fields.EXTRATO_TRANSPLANTE.toString(), "MET");
		
		filtroListaPacientesTransplantadosOrgao(filtro, criteria);
		
		StringBuilder sqlPermanencia = new StringBuilder(350);
		StringBuilder sqlDataTransplante =  new StringBuilder(380);
		
		//C8 #41807 thiago.cortes
		sqlDataTransplante.append(" (SELECT max(etx.DATA_OCORRENCIA) FROM  MTX_EXTRATO_TRANSPLANTES etx  ")
				.append("where etx.TRP_SEQ = this_.seq and etx.SITUACAO_TRANSPLANTE = 'T' ) ");
				
		sqlPermanencia.append(" ( "+sqlDataTransplante+" - this_.DATA_INGRESSO) permanencia");
		
		projectionListaPacientesTransplantadosOrgao(criteria, sqlPermanencia, sqlDataTransplante);
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteTransplantadosOrgaoVO.class));
		
		return criteria;
	}
	
	/**
	 * #41807
	 * @author thiago.cortes
	 */
	private void filtroListaPacientesTransplantadosOrgao(FiltroTransplanteOrgaoVO filtro,
			DetachedCriteria criteria) {
		if(filtro.getPacCodigo() != null){
			criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.CODIGO.toString(), filtro.getPacCodigo()));
		}
		
		if(filtro.getNomePaciente() != null && !filtro.getNomePaciente().trim().isEmpty()){
			criteria.add(Restrictions.ilike("PAC."+AipPacientes.Fields.NOME.toString(), filtro.getNomePaciente(), MatchMode.ANYWHERE));
		}

		if(filtro.getProntuario() != null){
			criteria.add(Restrictions.eq("PAC."+AipPacientes.Fields.PRONTUARIO.toString(), filtro.getProntuario()));
		}
		
		if(filtro.getDataInclusao() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.DATA_INGRESSO.toString(), filtro.getDataInclusao()));
		}
		
		if(filtro.getRgct() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.RGCT.toString(), filtro.getRgct()));
		}
		
		if(filtro.getDominioTipoOrgao() != null){
			criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.TIPO_ORGAO.toString(), filtro.getDominioTipoOrgao()));
		}
		if(filtro.getDataTransplante() != null){
			StringBuilder sql = new StringBuilder(60);
			sql.append(" to_char(met3_.data_ocorrencia,'dd/MM/yyyy') = '"+DateUtil.obterDataFormatada(filtro.getDataTransplante(), "dd/MM/yyyy")+"' ");
			criteria.add(Restrictions.sqlRestriction(sql.toString()));
		}
		criteria.add(Restrictions.eqProperty("TRP." + MtxTransplantes.Fields.SITUACAO.toString(), 
				"MET." + MtxExtratoTransplantes.Fields.SITUACAO_TRANSPLANTE.toString()));
		
		criteria.add(Restrictions.eq("TRP."+MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.T));
		criteria.add(Restrictions.isNull("TRP."+MtxTransplantes.Fields.TIPO_TMO.toString()));
	}

	/**
	 * #41807
	 * @author thiago.cortes
	 */
	private void projectionListaPacientesTransplantadosOrgao(DetachedCriteria criteria, StringBuilder sqlPermanencia, StringBuilder sqlDataTransplante) {
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlDataTransplante.toString()+PacienteTransplantadosOrgaoVO.Fields.DATA_TRANSPLANTE.toString()
						, new String [] {PacienteTransplantadosOrgaoVO.Fields.DATA_TRANSPLANTE.toString()}, new Type[] {new DateType()}))
				.add(Projections.sqlProjection(sqlPermanencia.toString()
						, new String [] {PacienteTransplantadosOrgaoVO.Fields.PERMANENCIA.toString()}, new Type[] {new IntegerType()}))
						
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), PacienteTransplantadosOrgaoVO.Fields.NOME.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SEQ), PacienteTransplantadosOrgaoVO.Fields.SEQ.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), PacienteTransplantadosOrgaoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("DOB."+MtxDoencaBases.Fields.DESCRICAO), PacienteTransplantadosOrgaoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.SEXO), PacienteTransplantadosOrgaoVO.Fields.SEXO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_NASCIMENTO), PacienteTransplantadosOrgaoVO.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ORGAO), PacienteTransplantadosOrgaoVO.Fields.TIPO_ORGAO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.DATA_INGRESSO), PacienteTransplantadosOrgaoVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DT_OBITO), PacienteTransplantadosOrgaoVO.Fields.DATA_OBITO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.CODIGO_RECEPTOR), PacienteTransplantadosOrgaoVO.Fields.CODIGO_RECEPTOR.toString())
				.add(Projections.property("MET."+MtxExtratoTransplantes.Fields.DATA_OCORRENCIA), PacienteTransplantadosOrgaoVO.Fields.DATA_TRANSPLANTE.toString())
				);
	}

	public MtxTransplantes pesquisarTransplantePaciente(Integer pacCodigo2, Integer seqTransplante) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class);
		criteria.setFetchMode(MtxTransplantes.Fields.DOENCA_BASE.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(MtxTransplantes.Fields.RECEPTOR_CODIGO.toString(), pacCodigo2));	
		criteria.add(Restrictions.eq(MtxTransplantes.Fields.SEQ.toString(), seqTransplante));
		return (MtxTransplantes) executeCriteriaUniqueResult(criteria);
	}	
	
	/**
	 * #41799 - C1
	 * @author romario.caldeira
	 */
	public PacienteAguardandoTransplanteOrgaoVO obterPacientePorCodTransplanteRins(Integer codTransplante){
		DetachedCriteria criteria = DetachedCriteria.forClass(AipPacientes.class, "PAC");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), PacienteAguardandoTransplanteOrgaoVO.Fields.PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), PacienteAguardandoTransplanteOrgaoVO.Fields.NOME.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ORGAO), PacienteAguardandoTransplanteOrgaoVO.Fields.TIPO_ORGAO.toString())
				);
		
		criteria.createAlias("PAC." + AipPacientes.Fields.TRANSPLANTE_RECEPTOR.toString(), "TRP");
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteAguardandoTransplanteOrgaoVO.class));
		if (codTransplante != null) {
			criteria.add(Restrictions.eq("TRP." + MtxTransplantes.Fields.SEQ.toString(), codTransplante));
		}
		return (PacienteAguardandoTransplanteOrgaoVO) executeCriteriaUniqueResult(criteria);
	}
	
	public MtxTransplantes obterTransplantePorPacienteOrgao(Integer codPaciente, DominioTipoOrgao tipoOrgao){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "MTR");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("MTR." + MtxTransplantes.Fields.SEQ.toString()).as(MtxTransplantes.Fields.SEQ.toString()))
				);
		
		criteria.add(Restrictions.eq("MTR." + MtxTransplantes.Fields.TIPO_ORGAO.toString(), tipoOrgao));
		criteria.add(Restrictions.eq("MTR." + MtxTransplantes.Fields.SITUACAO.toString(), DominioSituacaoTransplante.E));
		criteria.add(Restrictions.eq("MTR." + MtxTransplantes.Fields.RECEPTOR_CODIGO.toString(), codPaciente));
		criteria.setResultTransformer(Transformers.aliasToBean(MtxTransplantes.class));
		return (MtxTransplantes) executeCriteriaUniqueResult(criteria);
		}
		
	
	// #49925
	public List<AgendaTransplanteRetornoVO> obterAgendaPacienteTransplante(Integer codPaciente, List<AghEspecialidades> listaEspecialidade, DominioTipoRetorno tipoRetorno){
		AgendaPacienteTransplanteQueryBuilder builder = new AgendaPacienteTransplanteQueryBuilder();
		return executeCriteria(builder.build(codPaciente, listaEspecialidade, tipoRetorno));
	}
	
	//#48373
	public List<RelatorioTransplanteOrgaosSituacaoVO> pesquisarTransplante(DominioTipoOrgao dominioTipoOrgao, Integer prontuario, Date dataInicial,
			Date dataFinal,	List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados, DominioSexo sexo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias(MtxTransplantes.Fields.RECEPTOR.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(MtxTransplantes.Fields.DOENCA_BASE.toString(), "DOB", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.isNotNull(MtxTransplantes.Fields.TIPO_ORGAO.toString()));
		criteria.add(Restrictions.in(MtxTransplantes.Fields.SITUACAO.toString(), listaDominioSituacaoTransplanteSelecionados));
		AddCriteria.EQ.filtro(criteria, "PAC."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario);
		AddCriteria.EQ.filtro(criteria, "PAC."+AipPacientes.Fields.SEXO.toString(), sexo);
		AddCriteria.EQ.filtro(criteria, MtxTransplantes.Fields.TIPO_ORGAO.toString(), dominioTipoOrgao);
		AddCriteria.GE.filtro(criteria, MtxTransplantes.Fields.DATA_INGRESSO.toString(), dataInicial);
		AddCriteria.LE.filtro(criteria, MtxTransplantes.Fields.DATA_INGRESSO.toString(), dataFinal);
		
		StringBuilder dataOcorrencia = new StringBuilder(500);
		dataOcorrencia.append(" (SELECT  max(DATA_OCORRENCIA) FROM MTX_EXTRATO_TRANSPLANTES extt ");
		dataOcorrencia.append(" WHERE extt.TRP_SEQ = this_.SEQ AND extt.SITUACAO_TRANSPLANTE = this_.SITUACAO) as dataOcorrencia ");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), RelatorioTransplanteOrgaosSituacaoVO.Fields.PAC_PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), RelatorioTransplanteOrgaosSituacaoVO.Fields.PAC_NOME.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.SEXO), RelatorioTransplanteOrgaosSituacaoVO.Fields.PAC_SEXO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DATA_NASCIMENTO), RelatorioTransplanteOrgaosSituacaoVO.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ORGAO), RelatorioTransplanteOrgaosSituacaoVO.Fields.TIPO_ORGAO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SEQ), RelatorioTransplanteOrgaosSituacaoVO.Fields.SEQ_TRANSPLANTES.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SITUACAO), RelatorioTransplanteOrgaosSituacaoVO.Fields.SITUACAO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.DATA_INGRESSO), RelatorioTransplanteOrgaosSituacaoVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.OBSERVACOES), RelatorioTransplanteOrgaosSituacaoVO.Fields.OBSERVACOES.toString())
				.add(Projections.property("DOB."+MtxDoencaBases.Fields.DESCRICAO), RelatorioTransplanteOrgaosSituacaoVO.Fields.DOENCA_BASE.toString())
				.add(Projections.sqlProjection(dataOcorrencia.toString(), new String [] {"dataOcorrencia"}, new Type[] {new DateType()})));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioTransplanteOrgaosSituacaoVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * Verifica se ao incluir um receptor para um certo tipo de transplante este já não esta cadastrado para o mesmo transplante
	 * @return
	 */
	public List<MtxTransplantes> verificaTransplanteJaExistente(DominioSituacaoTmo tipoTmo, Integer codPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "MTR");
		criteria.add(Restrictions.and(Restrictions.eq("MTR."+MtxTransplantes.Fields.RECEPTOR_CODIGO.toString(),codPaciente),Restrictions.eq("MTR."+MtxTransplantes.Fields.TIPO_TMO.toString(),tipoTmo)));
		criteria.addOrder(Order.asc("MTR."+MtxTransplantes.Fields.DATA_INGRESSO.toString()));
		
		return executeCriteria(criteria);
			
	
	}
	
	//#41795
	public List<RelatorioTransplanteTmoSituacaoVO> pesquisarTransplante(DominioSituacaoTmo situacaoTmo, DominioTipoAlogenico tipoAlogenico, Integer prontuario, Date dataInicial,
			Date dataFinal,	List<DominioSituacaoTransplante> listaDominioSituacaoTransplanteSelecionados){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxTransplantes.class, "TRP");
		criteria.createAlias(MtxTransplantes.Fields.RECEPTOR.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias(MtxTransplantes.Fields.CRITERIO_PRIORIZACAO.toString(), "CPT", JoinType.INNER_JOIN);
		criteria.add(Restrictions.isNotNull(MtxTransplantes.Fields.TIPO_TMO.toString()));
		criteria.add(Restrictions.in(MtxTransplantes.Fields.SITUACAO.toString(), listaDominioSituacaoTransplanteSelecionados));
		AddCriteria.EQ.filtro(criteria, "PAC."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario);
		AddCriteria.EQ.filtro(criteria, MtxTransplantes.Fields.TIPO_TMO.toString(), situacaoTmo);
		AddCriteria.EQ.filtro(criteria, MtxTransplantes.Fields.TIPO_ALOGENICO.toString(), tipoAlogenico);
		AddCriteria.GE.filtro(criteria, MtxTransplantes.Fields.DATA_INGRESSO.toString(), dataInicial);
		AddCriteria.LE.filtro(criteria, MtxTransplantes.Fields.DATA_INGRESSO.toString(), dataFinal);
		
		StringBuilder dataOcorrencia = new StringBuilder(500);
		dataOcorrencia.append(" (SELECT  max(DATA_OCORRENCIA) FROM MTX_EXTRATO_TRANSPLANTES extt ");
		dataOcorrencia.append(" WHERE extt.TRP_SEQ = this_.SEQ AND extt.SITUACAO_TRANSPLANTE = this_.SITUACAO) as dataOcorrencia ");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO), RelatorioTransplanteTmoSituacaoVO.Fields.PAC_PRONTUARIO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.NOME), RelatorioTransplanteTmoSituacaoVO.Fields.PAC_NOME.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.SEXO), RelatorioTransplanteTmoSituacaoVO.Fields.PAC_SEXO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.CODIGO), RelatorioTransplanteTmoSituacaoVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("PAC."+AipPacientes.Fields.DATA_NASCIMENTO), RelatorioTransplanteTmoSituacaoVO.Fields.DATA_NASCIMENTO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SEQ), RelatorioTransplanteTmoSituacaoVO.Fields.SEQ_TRANSPLANTES.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.SITUACAO), RelatorioTransplanteTmoSituacaoVO.Fields.SITUACAO_TRANSPLANTE.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_TMO), RelatorioTransplanteTmoSituacaoVO.Fields.SITUACAO_TMO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.TIPO_ALOGENICO), RelatorioTransplanteTmoSituacaoVO.Fields.TIPO_ALOGENICO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.DATA_INGRESSO), RelatorioTransplanteTmoSituacaoVO.Fields.DATA_INGRESSO.toString())
				.add(Projections.property("TRP."+MtxTransplantes.Fields.OBSERVACOES), RelatorioTransplanteTmoSituacaoVO.Fields.OBSERVACOES.toString())
				.add(Projections.property("CPT."+MtxCriterioPriorizacaoTmo.Fields.SEQ), RelatorioTransplanteTmoSituacaoVO.Fields.CRITERIO_PRIORIZACAO_SEQ.toString())
				.add(Projections.property("CPT."+MtxCriterioPriorizacaoTmo.Fields.GRAVIDADE), RelatorioTransplanteTmoSituacaoVO.Fields.GRAVIDADE.toString())
				.add(Projections.property("CPT."+MtxCriterioPriorizacaoTmo.Fields.CRITICIDADE), RelatorioTransplanteTmoSituacaoVO.Fields.CRITICIDADE.toString())
				.add(Projections.property("CPT."+MtxCriterioPriorizacaoTmo.Fields.STATUS_TMO), RelatorioTransplanteTmoSituacaoVO.Fields.STATUS.toString())
				.add(Projections.sqlProjection(dataOcorrencia.toString(), new String [] {"dataOcorrencia"}, new Type[] {new DateType()})));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioTransplanteTmoSituacaoVO.class));
		return executeCriteria(criteria);
	}
}