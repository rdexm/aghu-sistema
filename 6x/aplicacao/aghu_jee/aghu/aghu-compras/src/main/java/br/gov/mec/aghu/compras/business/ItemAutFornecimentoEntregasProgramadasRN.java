package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
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
import br.gov.mec.aghu.compras.vo.ItemAutFornEntregaProgramadaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class ItemAutFornecimentoEntregasProgramadasRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8819518903346077958L;
	
	private static final Log LOG = LogFactory.getLog(ItemAutFornecimentoEntregasProgramadasRN.class);

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	public enum AcessoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO_CADASTRADO, MENSAGEM_NENHUM_CONTATO_ENCONTRADO;
	}
	
	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return scoItemAutorizacaoFornDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	
	public List<ItemAutFornEntregaProgramadaVO> obtemItemAutFornecimentosEntregasProgramadas(Integer afNumero,
			Date dataInicial, Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso) throws ApplicationBusinessException {
		
		AghParametros parametroDataInicial = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL);
		AghParametros parametroDataFinal = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL);
		
		List<ItemAutFornEntregaProgramadaVO> itens = getScoItemAutorizacaoFornDAO().obtemItemAutFornecimentosEntregasProgramadas(afNumero, dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData());
		if (itens == null || itens.isEmpty()) {
			return new ArrayList<ItemAutFornEntregaProgramadaVO>();
		}
		
		Set<ItemAutFornEntregaProgramadaVO> listagem = new HashSet<ItemAutFornEntregaProgramadaVO>(itens);
		
		List<ItemAutFornEntregaProgramadaVO> listagemNova = new ArrayList<ItemAutFornEntregaProgramadaVO>();
		
		for (ItemAutFornEntregaProgramadaVO autFornEntregaProgramadaVO : listagem) {
			setValorSaldoProgramado(afNumero, dataInicial, dataFinal,
					entregasGlobaisAcesso, parametroDataInicial,
					parametroDataFinal, autFornEntregaProgramadaVO);
			setValorLiberar(afNumero, dataInicial, dataFinal,
					entregasGlobaisAcesso, parametroDataInicial,
					parametroDataFinal, autFornEntregaProgramadaVO);
			setValorLiberado(afNumero, dataInicial, dataFinal,
					entregasGlobaisAcesso, parametroDataInicial,
					parametroDataFinal, autFornEntregaProgramadaVO);
			setValorAtraso(afNumero, dataInicial, dataFinal,
					entregasGlobaisAcesso, parametroDataInicial,
					parametroDataFinal, autFornEntregaProgramadaVO);
			if ((autFornEntregaProgramadaVO.getSaldoProgramado() != null && autFornEntregaProgramadaVO.getSaldoProgramado().doubleValue() > 0) ||
					(autFornEntregaProgramadaVO.getValorALiberar() != null && autFornEntregaProgramadaVO.getValorALiberar().doubleValue() > 0) ||
					(autFornEntregaProgramadaVO.getValorLiberado() != null && autFornEntregaProgramadaVO.getValorLiberado().doubleValue() > 0) ||
					(autFornEntregaProgramadaVO.getValorEmAtraso() != null && autFornEntregaProgramadaVO.getValorEmAtraso().doubleValue() > 0)) {
				listagemNova.add(autFornEntregaProgramadaVO);
			}
			
			
		}
		
		
		return listagemNova;
	}

	private void setValorAtraso(Integer afNumero, Date dataInicial,
			Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso,
			AghParametros parametroDataInicial,
			AghParametros parametroDataFinal,
			ItemAutFornEntregaProgramadaVO autFornEntregaProgramadaVO) {
		if (EntregasGlobaisAcesso.VALOR_ATRASO.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setValorEmAtraso(
				this.getScoItemAutorizacaoFornDAO().obtemValorAtraso(afNumero, autFornEntregaProgramadaVO.getIafNumero(),
						dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
		}
	}

	private void setValorLiberado(Integer afNumero, Date dataInicial,
			Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso,
			AghParametros parametroDataInicial,
			AghParametros parametroDataFinal,
			ItemAutFornEntregaProgramadaVO autFornEntregaProgramadaVO) {
		if (EntregasGlobaisAcesso.VALOR_LIBERADO.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setValorLiberado(
				this.getScoItemAutorizacaoFornDAO().obtemValorLiberado(afNumero, autFornEntregaProgramadaVO.getIafNumero(),
						dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
		}
	}

	private void setValorLiberar(Integer afNumero, Date dataInicial,
			Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso,
			AghParametros parametroDataInicial,
			AghParametros parametroDataFinal,
			ItemAutFornEntregaProgramadaVO autFornEntregaProgramadaVO) {
		if (EntregasGlobaisAcesso.VALOR_LIBERAR.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setValorALiberar(
				this.getScoItemAutorizacaoFornDAO().obtemValorLiberar(afNumero, autFornEntregaProgramadaVO.getIafNumero(),
						dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
		}
	}

	private void setValorSaldoProgramado(Integer afNumero, Date dataInicial,
			Date dataFinal, EntregasGlobaisAcesso entregasGlobaisAcesso,
			AghParametros parametroDataInicial,
			AghParametros parametroDataFinal,
			ItemAutFornEntregaProgramadaVO autFornEntregaProgramadaVO) {
		if (EntregasGlobaisAcesso.SALDO_PROGRAMADO.equals(entregasGlobaisAcesso) || entregasGlobaisAcesso == null) {
			autFornEntregaProgramadaVO.setSaldoProgramado(
					this.getScoItemAutorizacaoFornDAO().obtemValorSaldoProgramado(afNumero, autFornEntregaProgramadaVO.getIafNumero(),
							dataInicial, parametroDataInicial.getVlrData(), dataFinal, parametroDataFinal.getVlrData()));
		}
	}
	
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

}
