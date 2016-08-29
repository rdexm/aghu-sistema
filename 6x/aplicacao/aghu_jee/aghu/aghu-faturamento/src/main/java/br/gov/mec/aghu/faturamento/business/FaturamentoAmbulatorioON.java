package br.gov.mec.aghu.faturamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaDAO;
import br.gov.mec.aghu.faturamento.dao.FatMetaDAO;
import br.gov.mec.aghu.faturamento.vo.FaturaAmbulatorioVO;
import br.gov.mec.aghu.model.FatMeta;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FaturamentoAmbulatorioON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(FaturamentoAmbulatorioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCompetenciaDAO fatCompetenciaDAO;

@Inject
private FatMetaDAO fatMetaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5594215618641803125L;

	public List<FaturaAmbulatorioVO> listarFaturamentoAmbulatorioPorCompetencia(final Integer mes, Integer ano, final Date dtHoraInicio) {
		List<FaturaAmbulatorioVO> lista = getFatCompetenciaDAO()
				.listarFaturamentoAmbulatorioPorCompetencia(
						DominioModuloCompetencia.AMB, mes, ano, dtHoraInicio);
		
		FatMetaDAO fatMetaDAO = getFatMetaDAO();
				
		for(FaturaAmbulatorioVO vo : lista) {
			if (vo.getCaracteristicaFinanciamentoSeq() != null) {
				List<FatMeta> listaMeta = fatMetaDAO
						.listarMetaPeloGrupoPeloSubGrupoPelaFormOrgPeloFinancPeloProced(
								vo.getGrupoSeq(), vo.getSubGrupoSeq(),
								vo.getFormaOrganizacaoCodigo(),
								vo.getCaracteristicaFinanciamentoSeq(),
								vo.getIphPhoSeq(), vo.getIphSeq());
				if (!listaMeta.isEmpty()) {
					FatMeta meta = obterMetaVigenteCompetencia(listaMeta, mes, ano);
					if (meta != null) {
						vo.setQuantidadeTeto(meta.getQuantidade());
						vo.setValorTeto(meta.getValor());
						
						if (meta.getQuantidade() != null) {
							vo.setDiferencaQuantidadeTeto(meta.getQuantidade() - vo.getQuantidade().longValue());
						} else {
							vo.setDiferencaQuantidadeTeto(null);
						}
						
						if (meta.getValor() != null) {
							vo.setDiferencaValorTeto(meta.getValor().subtract(vo.getValorProcedimento()));
						} else {
							vo.setDiferencaValorTeto(null);	
						}
					}
				}
				listaMeta = fatMetaDAO
						.listarMetaPeloGrupoPeloSubGrupoPelaFormOrgPeloFinancPeloProced(
								vo.getGrupoSeq(), vo.getSubGrupoSeq(),
								vo.getFormaOrganizacaoCodigo(),
								vo.getCaracteristicaFinanciamentoSeq(), null,
								null);
				if (!listaMeta.isEmpty()) {
					FatMeta meta = obterMetaVigenteCompetencia(listaMeta, mes, ano);
					if (meta != null) {
						vo.setQuantidadeTeto(meta.getQuantidade());
						vo.setValorTeto(meta.getValor());	
						vo.setQuantidadeTetoGeral(meta.getQuantidade());
						vo.setValorTetoGeral(meta.getValor());
						
						if (meta.getQuantidade() != null) {
							vo.setDiferencaQuantidadeTeto(meta.getQuantidade() - vo.getQuantidade().longValue());
						} else {
							vo.setDiferencaQuantidadeTeto(null);
						}
						
						if (meta.getValor() != null) {
							vo.setDiferencaValorTeto(meta.getValor().subtract(vo.getValorProcedimento()));
						} else {
							vo.setDiferencaValorTeto(null);	
						}
					}
				}
			}
		}
		return lista;
	}
	
	/**
	 * Retorna a primeira meta cuja dthrInicio for menor que a competencia (mes/ano) informada.
	 * 
	 * @param listaMeta
	 * @param cpeMes
	 * @param cpeAno
	 * @return
	 */
	public FatMeta obterMetaVigenteCompetencia(List<FatMeta> listaMeta, Integer cpeMes, Integer cpeAno) {
		// Data do mês/ano da competência (com hora, minutos, segundos e milisegundos zerados)
		Date dataCompetencia = obterDataPorDiaMesAno(1, cpeMes, cpeAno);
		
		for (FatMeta meta : listaMeta) {
			Date dthrInicioVig = meta.getDthrInicioVig();
			Date dthrFimVig = meta.getDthrFimVig();
			if ((dthrInicioVig.before(dataCompetencia) || dthrInicioVig.equals(dataCompetencia))
					&& (dthrFimVig == null || (dthrFimVig
							.after(dataCompetencia) || dthrFimVig.equals(dataCompetencia)))) {
				return meta;
			}
		}
		
		return null;
	}

	private Date obterDataPorDiaMesAno(int dia, int mes, int ano) {
		Calendar c = Calendar.getInstance();
		c.clear();
		
		c.set(Calendar.YEAR, ano);
		c.set(Calendar.MONTH, mes - 1);
		c.set(Calendar.DAY_OF_MONTH, dia);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);		
		
		return c.getTime();
	}	
	
	protected FatCompetenciaDAO getFatCompetenciaDAO() {
		return fatCompetenciaDAO;
	}
	
	protected FatMetaDAO getFatMetaDAO() {
		return fatMetaDAO;
	}

}
