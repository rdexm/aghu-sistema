package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.AutFornEntregaProgramadaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class AutFornecimentoEntregasProgramadasRN extends BaseBusiness {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8819518903346077958L;
	
	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	public enum AcessoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO_CADASTRADO, MENSAGEM_NENHUM_CONTATO_ENCONTRADO;
	}
	
	private static final Log LOG = LogFactory.getLog(AutFornecimentoEntregasProgramadasRN.class);

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public List<AutFornEntregaProgramadaVO> obtemAutFornecimentosEntregasProgramadas(
			Integer gmtCodigo, Integer frnNumero, Date dataInicial, Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso) throws ApplicationBusinessException {
		
		AghParametros parametroDataInicial = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL);
		AghParametros parametroDataFinal = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL);
		Set<AutFornEntregaProgramadaVO> listagem = new HashSet<AutFornEntregaProgramadaVO>(scoItemAutorizacaoFornDAO.obtemAutFornecimentosEntregasProgramadas(gmtCodigo, frnNumero, dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
		
		List<AutFornEntregaProgramadaVO> listagemNova = new ArrayList<AutFornEntregaProgramadaVO>();
				
		for (AutFornEntregaProgramadaVO autFornEntregaProgramadaVO : listagem) {
			montarVo(dataInicial, dataFinal, entregasGlobaisAcesso,	parametroDataInicial, parametroDataFinal, listagemNova,	autFornEntregaProgramadaVO);
			
			if ((autFornEntregaProgramadaVO.getSaldoProgramado() != null && autFornEntregaProgramadaVO.getSaldoProgramado().doubleValue() > 0) ||
					(autFornEntregaProgramadaVO.getValorALiberar() != null && autFornEntregaProgramadaVO.getValorALiberar().doubleValue() > 0) ||
					(autFornEntregaProgramadaVO.getValorLiberado() != null && autFornEntregaProgramadaVO.getValorLiberado().doubleValue() > 0) ||
					(autFornEntregaProgramadaVO.getValorEmAtraso() != null && autFornEntregaProgramadaVO.getValorEmAtraso().doubleValue() > 0)) {
				listagemNova.add(autFornEntregaProgramadaVO);
			}
		
		}
		
		Collections.sort(listagemNova, new Comparator<AutFornEntregaProgramadaVO>() {
			@Override
			public int compare(AutFornEntregaProgramadaVO o1,
					AutFornEntregaProgramadaVO o2) {
				return (o2.getValorLiberado() == null ? BigDecimal.ZERO : o2.getValorLiberado()).compareTo(o1.getValorLiberado() == null ? BigDecimal.ZERO : o1.getValorLiberado());
			}
		});
		
		return listagemNova;
	}
	
	
	private void montarVo(Date dataInicial, Date dataFinal,	EntregasGlobaisAcesso entregasGlobaisAcesso,AghParametros parametroDataInicial,
			AghParametros parametroDataFinal, List<AutFornEntregaProgramadaVO> listagemNova, AutFornEntregaProgramadaVO autFornEntregaProgramadaVO) {
		
		if (EntregasGlobaisAcesso.SALDO_PROGRAMADO.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setSaldoProgramado(
					scoItemAutorizacaoFornDAO.obtemValorSaldoProgramado(autFornEntregaProgramadaVO.getAfnNumero(),null, 
							dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
			if(entregasGlobaisAcesso!=null){
				autFornEntregaProgramadaVO.setValorALiberar(null);
				autFornEntregaProgramadaVO.setValorLiberado(null);
				autFornEntregaProgramadaVO.setValorEmAtraso(null);
			}
		}
		
		if (EntregasGlobaisAcesso.VALOR_LIBERAR.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setValorALiberar(
					scoItemAutorizacaoFornDAO.obtemValorLiberar(autFornEntregaProgramadaVO.getAfnNumero(), null,
							dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
			if(entregasGlobaisAcesso!=null){
				autFornEntregaProgramadaVO.setSaldoProgramado(null);
				autFornEntregaProgramadaVO.setValorLiberado(null);
				autFornEntregaProgramadaVO.setValorEmAtraso(null);
			}
		}

		if (EntregasGlobaisAcesso.VALOR_LIBERADO.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setValorLiberado(
					scoItemAutorizacaoFornDAO.obtemValorLiberado(autFornEntregaProgramadaVO.getAfnNumero(), null,
							dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
			if(entregasGlobaisAcesso!=null){
				autFornEntregaProgramadaVO.setValorALiberar(null);
				autFornEntregaProgramadaVO.setSaldoProgramado(null);
				autFornEntregaProgramadaVO.setValorEmAtraso(null);
			}
		}
		
		if (EntregasGlobaisAcesso.VALOR_ATRASO.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setValorEmAtraso(
					scoItemAutorizacaoFornDAO.obtemValorAtraso(autFornEntregaProgramadaVO.getAfnNumero(), null,
							dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
			if(entregasGlobaisAcesso!=null){
				autFornEntregaProgramadaVO.setValorALiberar(null);
				autFornEntregaProgramadaVO.setValorLiberado(null);
				autFornEntregaProgramadaVO.setSaldoProgramado(null);
			}
		}		
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

}
