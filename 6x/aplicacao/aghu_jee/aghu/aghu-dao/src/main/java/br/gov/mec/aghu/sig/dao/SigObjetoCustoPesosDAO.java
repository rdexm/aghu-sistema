package br.gov.mec.aghu.sig.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatVlrItemProcedHospComps;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;

public class SigObjetoCustoPesosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoPesos> {

	private static final long serialVersionUID = 6158873238788403923L;

	@SuppressWarnings("unchecked")
	public Map<Integer, Double> pesquisarPesoTabelaUnificadaSUS(Integer codigoCentroCusto, Integer pConvenioSus, Integer pTabelaFaturPadrao,
			Integer pSusPlanoInternacao, Integer pSusPlanoAmbulatorio) {

		StringBuilder sql = new StringBuilder(600);

		sql.append("SELECT ");

		sql.append(" ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString() ).append( ", ");
		sql.append("round(avg( case when coalesce(ipc." ).append( FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.toString() ).append( ",0) > 0 then ");
		sql.append(" coalesce(ipc." ).append( FatVlrItemProcedHospComps.Fields.VLR_PROCEDIMENTO.toString() ).append( ",0) ");
		sql.append(" else (coalesce(ipc." ).append( FatVlrItemProcedHospComps.Fields.VLR_SERV_HOSPITALAR.toString() ).append( ",0) + ");
		sql.append(" coalesce(ipc." ).append( FatVlrItemProcedHospComps.Fields.VLR_SERV_PROFISSIONAL.toString() ).append( ",0) + ");
		sql.append("  coalesce(ipc." ).append( FatVlrItemProcedHospComps.Fields.VLR_SADT.toString() ).append( ",0)) ");
		sql.append("   end)) as valor ");

		sql.append("FROM ");
		sql.append(FatProcedHospInternos.class.getSimpleName() ).append( " phi, ");
		sql.append(FatVlrItemProcedHospComps.class.getSimpleName() ).append( " ipc, ");
		sql.append(VFatAssociacaoProcedimento.class.getSimpleName() ).append( " vw, ");
		sql.append(SigObjetoCustoVersoes.class.getSimpleName() ).append( " ocv, ");
		sql.append(SigObjetoCustoPhis.class.getSimpleName() ).append( " ocp, ");
		sql.append(SigObjetoCustoCcts.class.getSimpleName() ).append( " occ ");

		sql.append("WHERE ");
		sql.append(" ipc." ).append( FatVlrItemProcedHospComps.Fields.DT_INICIO_COMPETENCIA.toString() ).append( " <= :dataAtual ");
		sql.append(" and coalesce(ipc." ).append( FatVlrItemProcedHospComps.Fields.DT_FIM_COMPETENCIA.toString() ).append( ", :dataAtual ) >= :dataAtual ");
		sql.append(" and ipc." ).append( FatVlrItemProcedHospComps.Fields.IPH_SEQ.toString() ).append( " = vw." ).append( VFatAssociacaoProcedimento.Fields.IPH_SEQ.toString());
		sql.append(" and ipc." ).append( FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString() ).append( " = vw." ).append( VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString());
		sql.append(" and vw." ).append( VFatAssociacaoProcedimento.Fields.PHI_SEQ.toString() ).append( " = phi." ).append( FatProcedHospInternos.Fields.SEQ.toString());
		sql.append(" and vw." ).append( VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_CNV_CODIGO.toString() ).append( " = :pConvenioSus");
		sql.append(" and ipc." ).append( FatVlrItemProcedHospComps.Fields.IPH_PHO_SEQ.toString() ).append( " = :pTabelaFaturPadrao");
		sql.append(" and vw." ).append( VFatAssociacaoProcedimento.Fields.CPG_CPH_CSP_SEQ.toString() ).append( " in ( :pSusPlanoInternacao, :pSusPlanoAmbulatorio )");
		sql.append(" and ocv." ).append( SigObjetoCustoVersoes.Fields.SEQ.toString() ).append( " = ocp." ).append( SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString());
		sql.append(" and ocv." ).append( SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString() ).append( " = 'A' ");
		sql.append(" and ocp." ).append( SigObjetoCustoPhis.Fields.FAT_PHI_SEQ.toString() ).append( " = phi." ).append( FatProcedHospInternos.Fields.SEQ.toString());
		sql.append(" and ocp." ).append( SigObjetoCustoPhis.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString() ).append( " = occ."
				).append( SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString() ).append( '.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString());
		sql.append(" and occ." ).append( SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString() ).append( " = :codigoCentroCusto ");

		sql.append(" GROUP BY ");
		sql.append(" ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString());

		sql.append(" ORDER BY ");
		sql.append(" ocv." ).append( SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString() ).append( '.' ).append( SigObjetoCustos.Fields.SEQ.toString());

		Query query = this.createHibernateQuery(sql.toString());

		query.setDate("dataAtual", new Date());//:dataAtual
		query.setInteger("codigoCentroCusto", codigoCentroCusto);//:codigoCentroCusto
		query.setShort("pConvenioSus", pConvenioSus.shortValue()); //:pConvenioSus
		query.setShort("pTabelaFaturPadrao", pTabelaFaturPadrao.shortValue()); //:pTabelaFaturPadrao
		query.setByte("pSusPlanoInternacao", pSusPlanoInternacao.byteValue()); //:pSusPlanoInternacao
		query.setByte("pSusPlanoAmbulatorio", pSusPlanoAmbulatorio.byteValue());//:pSusPlanoAmbulatorio

		Map<Integer, Double> lstRetorno = new HashMap<Integer, Double>();

		List<Object[]> valores = query.list();
		if (valores != null && valores.size() > 0) {
			for (Object[] objects : valores) {
				lstRetorno.put((Integer) objects[0], (Double) objects[1]);
			}
		}

		return lstRetorno;
	}

}
