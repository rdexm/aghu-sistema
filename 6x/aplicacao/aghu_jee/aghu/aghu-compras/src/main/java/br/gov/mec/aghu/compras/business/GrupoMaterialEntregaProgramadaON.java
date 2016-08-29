/**
 * 
 */
package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.vo.EntregaProgramadaGrupoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroGrupoMaterialEntregaProgramadaVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 *
 */
@Stateless
public class GrupoMaterialEntregaProgramadaON extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2261955322391131845L;
	
	private static final Log LOG = LogFactory.getLog(GrupoMaterialEntregaProgramadaON.class);
	
	@EJB
	private IParametroFacade parametroFacade;
	
	public Date calculaDataLiberacao(FiltroGrupoMaterialEntregaProgramadaVO filtro) throws ApplicationBusinessException {
		Date dataLiberacao = filtro.getDataFimEntrega() ;
		
		if (filtro.getDataFimEntrega() != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(filtro.getDataFimEntrega());
			c.add(Calendar.DATE, 1);
			dataLiberacao = c.getTime();
		} else {
			Calendar c = getCalendar();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			
			int diaHoje = c.get(Calendar.DAY_OF_MONTH);
			int mesHoje = c.get(Calendar.MONTH) + 1;
			
			String mesDia = new StringBuffer().append(mesHoje).append(diaHoje).toString();
			AghParametros parametroInicio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIA_INIC_PROG_ENTG_FIM_ANO);
			AghParametros parametroFim = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIA_FINAL_PROG_ENTG_FIM_ANO);
			
			AghParametros parametroDiaMesCorte = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIA_MES_CORTE_PROGRAMACAO_ENTREGAS);
			
			
			if (Integer.valueOf(mesDia) >= parametroInicio.getVlrNumerico().intValue() &&
				Integer.valueOf(mesDia) <= parametroFim.getVlrNumerico().intValue()) {
				
				dataLiberacao = calculaDataUltimaDataMes(c);
			} else if (diaHoje > parametroDiaMesCorte.getVlrNumerico().intValue()) {
				dataLiberacao = calculaDataCorte();
			} else {
				//Esta sendo utilizado o mesmo metodo pois no documento esta dizendo que é a mesma regra a ser utilizada
				dataLiberacao = calculaDataCorte();
			}
		}
		return dataLiberacao;
	}

	private Date calculaDataCorte() throws ApplicationBusinessException {
		Date dataLiberacao;
		AghParametros parametroNMesesMaior = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_N_MESES_MAIOR_PROGRAMACAO_ENTREGAS);
		AghParametros parametroDiaMes = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_DIA_MES_PROGRAMACAO_ENTREGAS);
		
		Calendar cDataAtual = Calendar.getInstance();
		cDataAtual.add(Calendar.MONTH, parametroNMesesMaior.getVlrNumerico().intValue());
		cDataAtual.set(Calendar.DAY_OF_MONTH, parametroDiaMes.getVlrNumerico().intValue());

		dataLiberacao = cDataAtual.getTime();
		return dataLiberacao;
	}

	private Date calculaDataUltimaDataMes(Calendar c)
			throws ApplicationBusinessException {
		Date dataLiberacao;
		Date ultimaDataDoMes = DateUtil.obterUltimoDiaDoMes(c.getTime());
		AghParametros parametroDiasFinalAno = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PROG_ENTG_FINAL_ANO);
		
		Calendar cUltimaDataDoMes = Calendar.getInstance();
		cUltimaDataDoMes.setTime(ultimaDataDoMes);
		cUltimaDataDoMes.add(Calendar.DAY_OF_MONTH, parametroDiasFinalAno.getVlrNumerico().intValue() - 1);
		
		dataLiberacao = cUltimaDataDoMes.getTime();
		return dataLiberacao;
	}

	public String validaDatas(FiltroGrupoMaterialEntregaProgramadaVO filtro) throws ApplicationBusinessException {
		if (filtro.getDataInicioEntrega() != null && filtro.getDataFimEntrega() != null) {
			if (DateUtil.validaDataMaior(filtro.getDataInicioEntrega(), filtro.getDataFimEntrega())) {
				return "MESSAGE_DATA_FINAL_DEVE_SER_MAIOR_DATA_INICIAL";
			}
		} else if (filtro.getDataInicioEntrega() != null && filtro.getDataFimEntrega() == null) {
//			Em caso de falha da validação quando data inicial (informada) for maior que 
//			final (vlr_data do parâmetro P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL): não executar a pesquisa e exibir a mensagem M2.
			
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL);
			
			if (DateUtil.validaDataMaior(filtro.getDataInicioEntrega(), parametro.getVlrData())) {
				return "MESSAGEM_DATA_INICIAL_MAIOR_QUE_PARAMETRO";
			}
		} else if (filtro.getDataFimEntrega() != null && filtro.getDataInicioEntrega() == null) {
//			Em caso de falha da validação quando data final (informada) for menor que inicial 
//			(vlr_data do parâmetro P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL): não executar a pesquisa e exibir a mensagem M3.
			AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL);
			
			if (DateUtil.validaDataMaior(parametro.getVlrData(), filtro.getDataFimEntrega())) {
				return "MESSAGEM_DATA_FINAL_MAIOR_QUE_PARAMETRO";
			}
		}
		return "";
	}

	public ProgramacaoEntregaGlobalTotalizadorVO totalizaValores(List<EntregaProgramadaGrupoMaterialVO> listagem) {
		ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO = new ProgramacaoEntregaGlobalTotalizadorVO();
		for (EntregaProgramadaGrupoMaterialVO vo : listagem) {
			programacaoEntregaGlobalTotalizadorVO.setTotalSaldoProgramado(programacaoEntregaGlobalTotalizadorVO.getTotalSaldoProgramado().add(vo.getSaldoProgramado() == null ? BigDecimal.ZERO : vo.getSaldoProgramado()));
			programacaoEntregaGlobalTotalizadorVO.setTotalValorALiberar(programacaoEntregaGlobalTotalizadorVO.getTotalValorALiberar().add(vo.getValorALiberar() == null ? BigDecimal.ZERO : vo.getValorALiberar()));
			programacaoEntregaGlobalTotalizadorVO.setTotalValorLiberado(programacaoEntregaGlobalTotalizadorVO.getTotalValorLiberado().add(vo.getValorLiberado() == null ? BigDecimal.ZERO : vo.getValorLiberado()));
			programacaoEntregaGlobalTotalizadorVO.setTotalValorEmAtraso(programacaoEntregaGlobalTotalizadorVO.getTotalValorEmAtraso().add(vo.getValorEmAtraso() == null ? BigDecimal.ZERO : vo.getValorEmAtraso()));
		}
		
		return programacaoEntregaGlobalTotalizadorVO;
	}
	
	protected Calendar getCalendar() {
		return DateUtil.getCalendarBy(new Date());		
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
}	
