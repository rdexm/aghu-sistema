/**
 * 
 */
package br.gov.mec.aghu.estoque.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.vo.EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceConsumoTotalMaterial;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @author bruno.mourao
 *
 */
public class SceConsumoTotalMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceConsumoTotalMaterial> {
    @Inject
    private IParametroFacade aIParametroFacade;


	private static final long serialVersionUID = 8661252587250093727L;

	public Integer obterConsumoNoMes(Integer codigoMat, Integer codigoCC, Date dataAtual){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceConsumoTotalMaterial.class);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.sum(SceConsumoTotalMaterial.Fields.QUANTIDADE.toString()));

		criteria.setProjection(p);

		if(codigoMat != null && codigoCC!= null && dataAtual != null){
			criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMat));
			criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(), codigoCC));
			criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString(), dataAtual));

		}

		List<Object> list = this.executeCriteria(criteria);


		if(list != null && !list.isEmpty()){
			final Long soma = (Long) list.get(0);
			return soma != null ? soma.intValue() : null;

		}else{
			return null;
		}
	}


	public SceConsumoTotalMaterial obterSceConsumoTotalMaterial(Integer codigoMat, Integer codigoCC, Date dataAtual, Short almSeq){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceConsumoTotalMaterial.class);

		//ProjectionList p = Projections.projectionList();

		//p.add(Projections.sum(SceConsumoTotalMaterial.Fields.QUANTIDADE.toString()));

		//criteria.setProjection(p);

		criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMat));
		criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(), codigoCC));
		criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString(), dataAtual));
		criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
		List<Object> list = this.executeCriteria(criteria);


		if(list != null && !list.isEmpty()){
			return (SceConsumoTotalMaterial) list.get(0);

		}else{
			return null;
		}

	}

	public Integer obterConsumoNoPeriodo(Integer codigoMat, Integer codigoCC, Date dataInicio, Date dataFim){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceConsumoTotalMaterial.class);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.sum(SceConsumoTotalMaterial.Fields.QUANTIDADE.toString()));

		criteria.setProjection(p);

		if(codigoMat != null && codigoCC!= null && dataInicio != null && dataFim != null){
			criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMat));
			criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(), codigoCC));
			criteria.add(Restrictions.between(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString(), dataInicio,dataFim));

			List<Object> list = this.executeCriteria(criteria);

			Integer result = null;
			if(list != null && !list.isEmpty()){
				Long soma = (Long) list.get(0);
				result = soma != null ? soma.intValue() : null;
			}

			return result;
		}
		else{
			return 0;
		}
	}

	public Integer obterConsumoTotalNosUltimosSeisMeses(Integer codigoMat){

		DetachedCriteria criteria = DetachedCriteria.forClass(SceConsumoTotalMaterial.class);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.sum(SceConsumoTotalMaterial.Fields.QUANTIDADE.toString()));

		criteria.setProjection(p);

		Calendar data = GregorianCalendar.getInstance();
		data.setTime(new Date());
		data.set(Calendar.DAY_OF_MONTH, 1);
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);

		Date dataInicial = DateUtils.addMonths(data.getTime(), -6);

		data.set(Calendar.HOUR_OF_DAY, 23);
		data.set(Calendar.MINUTE, 59);
		data.set(Calendar.SECOND, 59);
		data.set(Calendar.MILLISECOND, 59);	
		Date dataFinal = DateUtils.addMonths(data.getTime(), -1);

		criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codigoMat));
		criteria.add(Restrictions.between(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString(), dataInicial, dataFinal));

		List<Object> list = this.executeCriteria(criteria);

		Integer result = null;
		if(list != null && !list.isEmpty()){
			final Long soma = (Long) list.get(0);
			result = (soma != null ? soma.intValue() : null);
		}
		
		if (result == null) {
			result = 0;
		}

		return result;
	}

	public Integer obterConsumoTotalNosUltimosSeisMesesPeloMedicamento(Integer codigoMat) throws BaseException{

		final StringBuffer sql = new StringBuffer(400);
		sql.append("SELECT sum(egr.QTDE) AS QTDE, sum(ctm.QUANTIDADE) AS QUANTIDADE");
		sql.append(" FROM agh.sce_estq_gerais egr, agh.sce_consumo_total_materiais ctm");
		sql.append(" WHERE ctm.DT_COMPETENCIA BETWEEN :dataInicial AND :dataFinal");
		sql.append(" AND ctm.MAT_CODIGO = :matCodigo");
		sql.append(" AND egr.MAT_CODIGO = ctm.MAT_CODIGO");
		sql.append(" AND egr.DT_COMPETENCIA BETWEEN :dtCompetenciaInicial AND :dtCompetenciaFinal");
		sql.append(" AND egr.FRN_NUMERO = :fornecedor");
		sql.append(" GROUP BY egr.QTDE");

		Calendar data = GregorianCalendar.getInstance();
		data.setTime(new Date());
		data.set(Calendar.DAY_OF_MONTH, 1);
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);

		Date dataInicial = DateUtils.addMonths(data.getTime(), -6);

		data.set(Calendar.HOUR_OF_DAY, 23);
		data.set(Calendar.MINUTE, 59);
		data.set(Calendar.SECOND, 59);
		data.set(Calendar.MILLISECOND, 0);	
		Date dataFinal = DateUtils.addMonths(data.getTime(), -1);

		data.setTime(new Date());
		data.set(Calendar.DAY_OF_MONTH, 1);
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);
		Date dtCompetenciaInicial = new Date(data.getTime().getTime());

		data.setTime(new Date());
		data.set(Calendar.DAY_OF_MONTH, 1);
		data.set(Calendar.HOUR_OF_DAY, 23);
		data.set(Calendar.MINUTE, 59);
		data.set(Calendar.SECOND, 59);
		data.set(Calendar.MILLISECOND, 0);
		Date dtCompetenciaFinal = new Date(data.getTime().getTime());

		SQLQuery query = createSQLQuery(sql.toString());

		query.setDate("dataInicial", dataInicial);
		query.setDate("dataFinal", dataFinal);
		query.setInteger("matCodigo", codigoMat);
		query.setDate("dtCompetenciaInicial", dtCompetenciaInicial);
		query.setDate("dtCompetenciaFinal", dtCompetenciaFinal);
		query.setInteger("fornecedor", getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO).getVlrNumerico().intValue());

		List<Object[]> lista = query.addScalar("QTDE",IntegerType.INSTANCE)
		.addScalar("QUANTIDADE",IntegerType.INSTANCE)
		.list();

		Integer consumo = 0;
		if(!lista.isEmpty()) {
			Integer qtde = (Integer)lista.get(0)[0];
			Integer quantidade = (Integer)lista.get(0)[1];
//			Integer divisor = 1;
//
//			if((quantidade/60/30) != 0) {
//				divisor = quantidade/60/30 ;
//			}
			consumo = Math.round(qtde / quantidade);
		}

		return consumo;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO populaEstatisticasDeConsumoEmUmAnoPorMaterialAlmoxDtCompetencia(Integer codMaterial, Short almoxSeq, Date dtCompentencia,Date dtComptenciaSistema){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceConsumoTotalMaterial.class);

		ProjectionList p = Projections.projectionList();
		p.add(Projections.groupProperty(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString()));
		p.add(Projections.sum(SceConsumoTotalMaterial.Fields.QUANTIDADE.toString()));

		criteria.setProjection(p);

		criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.MATERIAL.toString() + "." + ScoMaterial.Fields.CODIGO.toString(), codMaterial));

		if (almoxSeq != null) {

			criteria.add(Restrictions.eq(SceConsumoTotalMaterial.Fields.ALMOXARIFADO.toString() + "." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(), almoxSeq));

		}



		Calendar data = GregorianCalendar.getInstance();
		data.setTime(dtCompentencia);
		data.set(Calendar.DAY_OF_MONTH, 1);
		data.set(Calendar.HOUR_OF_DAY, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		data.set(Calendar.MILLISECOND, 0);		

		Date dataInicial = DateUtils.addMonths(data.getTime(), -11);
		criteria.add(Restrictions.ge(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString(), dataInicial));
		criteria.addOrder(Order.asc(SceConsumoTotalMaterial.Fields.DATA_COMPETENCIA.toString()));


		List<Object[]> list = this.executeCriteria(criteria);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM/yyyy", new Locale("pt", "BR"));

		EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO estatistica = new EstatisticaEstoqueAlmoxarifadoHistoricoConsumoVO();

		Calendar dataCompare = GregorianCalendar.getInstance();

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -11));			
		estatistica.setNomeMesAnterio11(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -10));
		estatistica.setNomeMesAnterio10(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -9));
		estatistica.setNomeMesAnterio9(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -8));
		estatistica.setNomeMesAnterio8(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -7));
		estatistica.setNomeMesAnterio7(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -6));
		estatistica.setNomeMesAnterio6(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -5));
		estatistica.setNomeMesAnterio5(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -4));
		estatistica.setNomeMesAnterio4(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -3));
		estatistica.setNomeMesAnterio3(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -2));
		estatistica.setNomeMesAnterio2(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), -1));
		estatistica.setNomeMesAnterio1(sdf.format(dataCompare.getTime()));

		dataCompare.setTime(DateUtils.addMonths(data.getTime(), 0));


		Calendar dtComp = Calendar.getInstance();
		dtComp.setTime(dtCompentencia);
		Calendar dtParametro = Calendar.getInstance();
		dtParametro.setTime(dtComptenciaSistema);
		if(dtComp.get(Calendar.MONTH) == dtParametro.get(Calendar.MONTH)){
			estatistica.setNomeMesComp("Atual");
		}else{
			estatistica.setNomeMesComp(sdf.format(dataCompare.getTime()));
		}



		if(list != null && !list.isEmpty()){

			for (int i = 0; i < list.size(); i++) {
				Object[] valores = list.get(i);
				Date dataValor = (Date)valores[0];
				Long somaQuantidades = (Long)valores[1];
				Integer valor = somaQuantidades.intValue();

				dataCompare.setTime(dataValor);

				if(estatistica.getNomeMesAnterio11().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior11(valor);

				}else if(estatistica.getNomeMesAnterio10().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior10(valor);

				}else if(estatistica.getNomeMesAnterio9().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior9(valor);

				}else if(estatistica.getNomeMesAnterio8().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior8(valor);

				}else if(estatistica.getNomeMesAnterio7().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior7(valor);

				}else if(estatistica.getNomeMesAnterio6().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior6(valor);

				}else if(estatistica.getNomeMesAnterio5().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior5(valor);

				}else if(estatistica.getNomeMesAnterio4().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior4(valor);

				}else if(estatistica.getNomeMesAnterio3().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior3(valor);

				}else if(estatistica.getNomeMesAnterio2().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior2(valor);

				}else if(estatistica.getNomeMesAnterio1().equals(sdf.format(dataCompare.getTime()))){
					estatistica.setQtdeMesAnterior1(valor);

				}else if(estatistica.getNomeMesComp().equals(sdf.format(dataCompare.getTime()))
						|| dtComp.get(Calendar.MONTH) == dtParametro.get(Calendar.MONTH)) {
					estatistica.setQtdeMesComp(valor);
				} 
			}
		}

		return estatistica;
	}

	protected IParametroFacade getParametroFacade() {		
		return aIParametroFacade;
	}

}
