package br.gov.mec.aghu.exames.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.exames.vo.NormalidadeHistoricoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.AelValorNormalidCampo;
import br.gov.mec.aghu.model.AelValorNormalidCampoId;
import br.gov.mec.aghu.model.RapServidores;

public class AelValorNormalidCampoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelValorNormalidCampo> {
	
	
	private static final long serialVersionUID = 2754614732855972748L;

	@Override
	protected void obterValorSequencialId(AelValorNormalidCampo elemento) {
		if (elemento == null || elemento.getId() == null || elemento.getId().getCalSeq() == null) {
			throw new IllegalArgumentException("Campo Laudo não foi informado corretamente.");
		}
		
		AelValorNormalidCampoId id = new AelValorNormalidCampoId();
		id.setCalSeq(elemento.getId().getCalSeq());
		this.buscaMaxSeq(id);
	
		elemento.setId(id);
		
	}
	
	public List<AelValorNormalidCampo> buscaNormalidadeCampoLaudoPorData(Integer campoLaudo, Date dthrEvento){

		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorNormalidCampo.class);
		
		/*Restrictions*/
		criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.CAL_SEQ.toString(), campoLaudo));

		Object[] values = {dthrEvento, dthrEvento};
		
		Type[] types = {TimestampType.INSTANCE, TimestampType.INSTANCE};

		StringBuffer sqlRestriction = new StringBuffer(155);
		sqlRestriction.append(" ((? >= dthr_inicial and dthr_final    is null)  or (? between dthr_inicial and dthr_final))");

		criteria.add(Restrictions.sqlRestriction(sqlRestriction.toString(), values, types));

		return executeCriteria(criteria);
	}
	
	/**
	 * Busca o maior sequencial associado a AelValorNormalidCampo
	 * @param id
	 * @return
	 */
	private void buscaMaxSeq(AelValorNormalidCampoId id) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorNormalidCampo.class);
		criteria.setProjection(Projections.max(AelValorNormalidCampo.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.CAL_SEQ.toString(), id.getCalSeq()));
		Short seqp = (Short) this.executeCriteriaUniqueResult(criteria);
		seqp = seqp == null ? 0 : seqp;
		id.setSeqp(++seqp);
		
	}
	
	
	/**
	 * Retorna AelValorNormalidCampo original
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AelValorNormalidCampo obterOriginal(AelValorNormalidCampo elementoModificado) {
		
		final AelValorNormalidCampoId id = elementoModificado.getId();
		
		StringBuilder hql = new StringBuilder(200);

		hql.append("select o.").append(AelValorNormalidCampo.Fields.ID.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.SEQP.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.DTHR_INICIAL.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.VALOR_MAXIMO.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.VALOR_MINIMO.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.QTDE_CASAS_DEC.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.SITUACAO.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.DTHR_FINAL.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.VALOR_MIN_ACEITAVEL.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.VALOR_MAX_ACEITAVEL.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.VALOR_MIN_ABSURDO.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.VALOR_MAX_ABSURDO.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.SEXO.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.IDADE_MINIMA.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.IDADE_MAXIMA.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.SERVIDOR.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.UNID_MEDIDA_IDADE.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.UNID_MEDIDA.toString());
		hql.append(", o.").append(AelValorNormalidCampo.Fields.UNID_MEDIDA_MIN.toString());
		
		
		hql.append(" from ").append(AelValorNormalidCampo.class.getSimpleName()).append(" o ");
		hql.append(" left outer join o.").append(AelValorNormalidCampo.Fields.UNID_MEDIDA.toString());
		hql.append(" left outer join o.").append(AelValorNormalidCampo.Fields.SERVIDOR.toString());
		hql.append(" where o.").append(AelValorNormalidCampo.Fields.ID.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", id);
		
		AelValorNormalidCampo original = null;
		
		List<Object[]> camposList = (List<Object[]>) query.getResultList();
		
		if(camposList != null && camposList.size()>0) {
			
			Object[] campos = camposList.get(0);
			original = new AelValorNormalidCampo();
			
			original.setId(id);
			original.setDthrInicial((Date)campos[2]);
			original.setValorMaximo((String)campos[3]);
			original.setValorMinimo((String)campos[4]);
			original.setQtdeCasasDecimais((Short)campos[5]);
			original.setSituacao((DominioSituacao)campos[6]);
			original.setDthrFinal((Date)campos[7]);
			original.setValorMinimoAceitavel((String)campos[8]);
			original.setValorMaximoAceitavel((String)campos[9]);
			original.setValorMinimoAbsurdo((String)campos[10]);
			original.setValorMaximoAbsurdo((String)campos[11]);
			original.setSexo((DominioSexo)campos[12]);
			original.setIdadeMinima((Short)campos[13]);
			original.setIdadeMaxima((Short)campos[14]);
			original.setServidor((RapServidores)campos[15]);
			original.setUnidMedidaIdade((DominioUnidadeMedidaIdade)campos[16]);
			original.setUnidadeMedida((AelUnidMedValorNormal)campos[17]);
			original.setUnidMedidaIdadeMin((DominioUnidadeMedidaIdade)campos[18]);
				
		}
		
		return original;
		
	}
	
	/**
	 * Lista as normalidades cadastradas para o campo laudo 
	 * @param seqCampoLaudo
	 * @return
	 */
	public List<AelValorNormalidCampo> pesquisarNormalidadesCampoLaudo(Integer seqCampoLaudo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorNormalidCampo.class);
		
		if(seqCampoLaudo != null ){
			criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.CAL_SEQ.toString(), seqCampoLaudo));
		}
		
		criteria.addOrder(Order.asc(AelValorNormalidCampo.Fields.SITUACAO.toString()));
		criteria.addOrder(Order.desc(AelValorNormalidCampo.Fields.SEXO.toString()));
		criteria.addOrder(Order.asc(AelValorNormalidCampo.Fields.IDADE_MINIMA.toString()));

		return this.executeCriteria(criteria);
	}
	
	@SuppressWarnings({"PMD.NPathComplexity"})
	public List<AelValorNormalidCampo> pesquisarSobreposicaoNormalidadesCampoLaudo(AelValorNormalidCampo valorNormalidCampo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorNormalidCampo.class);
		
		if(valorNormalidCampo.getId().getCalSeq()!=null){
			criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.CAL_SEQ.toString(), valorNormalidCampo.getId().getCalSeq()));
		}
		if(valorNormalidCampo.getId().getSeqp()!=null){
			criteria.add(Restrictions.ne(AelValorNormalidCampo.Fields.SEQP.toString(), valorNormalidCampo.getId().getSeqp()));
		}
		criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.SITUACAO.toString(), valorNormalidCampo.getSituacao()));

		if(valorNormalidCampo.getSexo() != null){
			Object[] values = { valorNormalidCampo.getSexo().toString() };
			Type[] types = { StringType.INSTANCE };
			criteria.add(Restrictions.sqlRestriction("(this_.sexo is null or this_.sexo = ?)", values, types));
		}
		
		
		StringBuilder sb = createSQLSobreposicaoNormalidadesCampoLaudo();
		
		
		Object[] values = { (valorNormalidCampo.getIdadeMinima()!=null? valorNormalidCampo.getIdadeMinima() : Short.valueOf("0")),
				valorNormalidCampo.getIdadeMinima()!=null? valorNormalidCampo.getIdadeMinima() : Short.valueOf("0"),
				(valorNormalidCampo.getIdadeMaxima()!=null? valorNormalidCampo.getIdadeMaxima() : Short.valueOf("999")),
			    (valorNormalidCampo.getIdadeMaxima()!=null? valorNormalidCampo.getIdadeMaxima() : Short.valueOf("999"))};
		Type[] types = { ShortType.INSTANCE, ShortType.INSTANCE,ShortType.INSTANCE,ShortType.INSTANCE };
		criteria.add(Restrictions.sqlRestriction(sb.toString(), values, types));

		return this.executeCriteria(criteria);
	}
	
	private StringBuilder createSQLSobreposicaoNormalidadesCampoLaudo() {
		StringBuilder sb = new StringBuilder(100);
		StringBuilder fieldUnidMedidaIdadeMin = new StringBuilder("this_.unid_medida_idade_min");
		StringBuilder fieldUnidMedidaIdade = new StringBuilder("this_.unid_medida_idade");
		StringBuilder idadeMinima = new StringBuilder("this_.idade_minima");
		StringBuilder idadeMaxima = new StringBuilder("this_.idade_maxima");
		
		StringBuilder sqlDiasUnidMedidaIdadeMin = new StringBuilder(100);
		sqlDiasUnidMedidaIdadeMin.append("(CASE WHEN ")
		.append(fieldUnidMedidaIdadeMin)
		.append(" = 'A' THEN 365 WHEN ")
		.append(fieldUnidMedidaIdadeMin)
		.append(" = 'M' THEN 30 ELSE 0 END))");
		
		StringBuilder sqlDiasUnidMedidaIdade = new StringBuilder(100);
		sqlDiasUnidMedidaIdade.append("(CASE WHEN ")
		.append(fieldUnidMedidaIdade)
		.append(" = 'A' THEN 365 WHEN ")
		.append(fieldUnidMedidaIdade)
		.append(" = 'M' THEN 30 ELSE 0 END))"); 
		
		sb.append("  ((( ? * ")
		.append(sqlDiasUnidMedidaIdadeMin)
		.append(" > (")
		.append(idadeMinima)
		.append(" * ")
		.append(sqlDiasUnidMedidaIdadeMin)
		.append(" AND (? * ")
		.append(sqlDiasUnidMedidaIdade)
		.append(" < (")
		.append(idadeMaxima)
		.append(" * ")
		.append(sqlDiasUnidMedidaIdade)
		.append(" OR ((? * ")
		.append(sqlDiasUnidMedidaIdadeMin)
		.append(" > (")
		.append(idadeMinima)
		.append(" * ")
		.append(sqlDiasUnidMedidaIdadeMin)
		.append(" AND (? * ")
		.append(sqlDiasUnidMedidaIdade)
		.append(" < (")
		.append(idadeMaxima)
		.append(" * ")
		.append(sqlDiasUnidMedidaIdade)
		.append(" )))");		
		return sb;
	}
	
	public AelValorNormalidCampo buscaNormalidadeCampoLaudo(Integer campoLaudo, Date dthrEvento, Integer idade, DominioSexo sexo) throws ParseException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorNormalidCampo.class);
		/*Restrictions*/
		criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.CAL_SEQ.toString(), campoLaudo));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Object[] values = {sdf.parse(sdf.format(dthrEvento)), sdf.parse(sdf.format(dthrEvento)), sexo.toString(), idade, DominioSituacao.A.toString()};
		
		Type[] types = {TimestampType.INSTANCE, TimestampType.INSTANCE, StringType.INSTANCE, IntegerType.INSTANCE, StringType.INSTANCE};

		StringBuffer sqlRestriction = new StringBuffer(231);
		sqlRestriction.append(" ((? >= dthr_inicial and dthr_final    is null)  or (? between dthr_inicial and dthr_final))");
		sqlRestriction.append(" and   ((sexo   =  ?) or (sexo  is  null))");
		sqlRestriction.append(" and   ((idade_minima is null) or (? between idade_minima and idade_maxima)) and ind_situacao = ?");

		criteria.add(Restrictions.sqlRestriction(sqlRestriction.toString(), values, types));
		
		List<AelValorNormalidCampo> result = this.executeCriteria(criteria);
		
		if(result.isEmpty()) {
			return null;
		} else {
			return result.get(0);
		}
	}
	
	public List<AelValorNormalidCampo> buscaNormalidadeCampoLaudoSemIdade(Integer campoLaudo, Date dthrEvento, DominioSexo sexo){

		DetachedCriteria criteria = DetachedCriteria.forClass(AelValorNormalidCampo.class);
		
		String sexoPaciente = null;
		
		if (sexo != null) {
			sexoPaciente = sexo.toString();
		}
		 
		/*Restrictions*/
		criteria.add(Restrictions.eq(AelValorNormalidCampo.Fields.CAL_SEQ.toString(), campoLaudo));

		Object[] values = {dthrEvento, dthrEvento, sexoPaciente};
		
		Type[] types = {TimestampType.INSTANCE, TimestampType.INSTANCE, StringType.INSTANCE};

		StringBuffer sqlRestriction = new StringBuffer(155);
		sqlRestriction.append(" ((? >= dthr_inicial and dthr_final    is null)  or (? between dthr_inicial and dthr_final))");
		sqlRestriction.append(" and   ((sexo   =  ?) or (sexo  is  null)) ");

		criteria.add(Restrictions.sqlRestriction(sqlRestriction.toString(), values, types));

		return executeCriteria(criteria);
	}
	
	public List<NormalidadeHistoricoVO> obterValoresNormalidadeHistorico(Integer c_pac_codigo, Integer c_ise_soe_seq, String c_sit_codigo_lib, String c_sit_area_executora, String c_ufe_ema_exa_sigla, Integer c_ufe_ema_man_seq, Integer pclCalSeq, Date c_dthr_liberada, Integer qtdeResults){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelSolicitacaoExames.class, "soe");
		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.ITENS_SOLICITACAO_EXAME.toString(), "ise", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd", DetachedCriteria.LEFT_JOIN);
		criteria.createCriteria("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO_DIVERSO.toString(), "atv", DetachedCriteria.LEFT_JOIN);

		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.EXTRATO_ITEM_SOLIC.toString(), "eis", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("ise."+AelItemSolicitacaoExames.Fields.RESULTADO_EXAME.toString(), "ree", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("ree."+AelResultadoExame.Fields.PARAMETRO_CAMPO_LAUDO.toString(), "pcl", DetachedCriteria.INNER_JOIN);

		/*dados dos resultados*/
		criteria.createCriteria("ree."+AelResultadoExame.Fields.RESULTADO_CODIFICADO.toString(), "reec", DetachedCriteria.LEFT_JOIN);
		criteria.createCriteria("ree."+AelResultadoExame.Fields.RESULTADO_CARACTERISTICAS.toString(), "rcar", DetachedCriteria.LEFT_JOIN);
		/**/
		
		criteria.createCriteria("pcl."+AelParametroCamposLaudo.Fields.CAMPO_LAUDO.toString(), "cal", DetachedCriteria.INNER_JOIN);
		criteria.createCriteria("cal."+AelCampoLaudo.Fields.VALORES_NORMALIDADE.toString(), "vnc", DetachedCriteria.LEFT_JOIN);
		criteria.createCriteria("vnc."+AelValorNormalidCampo.Fields.UNID_MEDIDA.toString(), "uvm", DetachedCriteria.LEFT_JOIN);

		/*
		 * Restrictions
		 */
		criteria.add(Restrictions.sqlRestriction("coalesce(atd2_.pac_codigo,atv3_.pac_codigo) = ? ",c_pac_codigo, IntegerType.INSTANCE));
		criteria.add(Restrictions.eq("ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), c_sit_codigo_lib));
		criteria.add(Restrictions.ne("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), c_ise_soe_seq));
		criteria.add(Restrictions.eq("ise."+AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString(), c_ufe_ema_exa_sigla));
		criteria.add(Restrictions.eq("ise."+AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString(), c_ufe_ema_man_seq));
		
		criteria.add(Restrictions.sqlRestriction(" eis4_.criado_em = (select 	max(eis1.criado_em)" +
												"					from 	agh.ael_extrato_item_solics eis1" +
												"					where 	eis1.ise_soe_seq = ise1_.soe_seq" +
												"					and 	eis1.ise_seqp = ise1_.seqp)")
          										);

		criteria.add(Restrictions.eqProperty("eis."+AelExtratoItemSolicitacao.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString(), "ise."+AelItemSolicitacaoExames.Fields.SITUACAO_ITEM_SOLICITACAO_CODIGO.toString()));
		criteria.add(Restrictions.lt("eis."+AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString(), c_dthr_liberada));
		criteria.add(Restrictions.eq("ree."+AelResultadoExame.Fields.ANULACAO_LAUDO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("cal."+AelCampoLaudo.Fields.SEQ.toString(), pclCalSeq));

		Criterion restFinal1 = Restrictions.isNull("vnc."+AelValorNormalidCampo.Fields.CAL_SEQ.toString());		
		Criterion rest2 = Restrictions.leProperty("vnc."+AelValorNormalidCampo.Fields.DTHR_INICIAL.toString(), "eis."+AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString());
		Criterion rest4 = Restrictions.isNull("vnc."+AelValorNormalidCampo.Fields.DTHR_FINAL.toString());
		Criterion rest5 = Restrictions.gtProperty("vnc."+AelValorNormalidCampo.Fields.DTHR_FINAL.toString(), "eis."+AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString());
		Criterion rest6 =  Restrictions.or(rest4, rest5);

		Criterion restFinal2 = Restrictions.and(rest2, rest6);
		Criterion restFinal = Restrictions.or(restFinal1, restFinal2);

		criteria.add(restFinal);

		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		projection.add(Projections.property("ise."+AelItemSolicitacaoExames.Fields.SEQP.toString()));
		projection.add(Projections.sqlProjection("trim(substr(cal9_.nome,1,25)) as campo_laudo", new String[] {"campo_laudo"},new Type[] {StringType.INSTANCE}));
		
		String sqlProj;
		if (isOracle()) {
			sqlProj = " case " +
					  "	    when ree5_.valor is not null" +
					  "         then to_char ( ree5_.valor / power(10, pcl6_.qtde_casas_decimais) )" +
					  "	        else to_char ( coalesce(to_clob(reec7_.descricao), coalesce(to_clob(reec7_.descricao), to_clob(rcar8_.descricao) ) ) )" +
					  " end as resultado";
		}
		else {
			sqlProj =   "	case " +
						"		when 	ree5_.valor is not null then" +
						"				cast (ree5_.valor / power(10,pcl6_.qtde_casas_decimais) as text) " +
						"     	else" +
						"				coalesce(reec7_.descricao, coalesce(reec7_.descricao, rcar8_.descricao))" +
						"	end as resultado";
		}
		projection.add(Projections.sqlProjection(sqlProj, new String[] {"resultado"},new Type[] {StringType.INSTANCE}));
		projection.add(Projections.sqlProjection("coalesce(uvm11_.descricao, '') as descricao", new String[] {"descricao"},new Type[] {StringType.INSTANCE}));
		projection.add(Projections.property("eis."+AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()));

		criteria.setProjection(projection);

		criteria.addOrder(Order.desc("eis."+AelExtratoItemSolicitacao.Fields.DTHR_EVENTO.toString()));
		
		final List<Object[]> lista = this.executeCriteria(criteria, 0, qtdeResults, null, false);
		final List<NormalidadeHistoricoVO> listNormalidade = new ArrayList<NormalidadeHistoricoVO>();
		
		for (final Object[] obj : lista) {
			final NormalidadeHistoricoVO normalidadeHisto = new NormalidadeHistoricoVO();

			normalidadeHisto.setSoeSeq((Integer) obj[0]);
			normalidadeHisto.setSeqp((Short) obj[1]);
			normalidadeHisto.setResultado((String)obj[3]);
			normalidadeHisto.setDescricao((String)obj[4]);
			normalidadeHisto.setDataEvento((Date)obj[5]);

			listNormalidade.add(normalidadeHisto);
		}
		return listNormalidade;
	}
}



