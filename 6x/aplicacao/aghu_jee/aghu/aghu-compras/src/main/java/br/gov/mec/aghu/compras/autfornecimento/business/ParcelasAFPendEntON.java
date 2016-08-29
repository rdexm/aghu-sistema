package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.compras.vo.FiltroParcelasAFPendEntVO;
import br.gov.mec.aghu.compras.vo.ParcelasAFPendEntVO;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ParcelasAFPendEntON extends BaseBusiness {

	private static final long serialVersionUID = 153237391533209871L;

	@EJB
	private ParcelasAFPendEntRN parcelasAFPendEntRN;
	
	public enum ParcelasAFPendEntONExceptionCode implements BusinessExceptionCode {
		VALIDACAO_DT_AF_PEND_ENTREGA;
	}

	public Boolean desativarAtivarPeriodoEntrega(
			FiltroParcelasAFPendEntVO filtro) {
		if (filtro.getQtdDiasEntrega() == null
				|| filtro.getQtdDiasEntrega() == 0) {
			return Boolean.FALSE;
		} else {
			filtro.setDataEntregaInicial(null);
			filtro.setDataEntregaFinal(null);
			return Boolean.TRUE;
		}
	}

	public List<ParcelasAFPendEntVO> listarParcelasAFsPendentes(
			FiltroParcelasAFPendEntVO filtro, Integer firstResult, Integer maxResult,
			String order, boolean asc) throws ApplicationBusinessException {
		return getParcelasAFPendEntRN().listarParcelasAFsPendentes(filtro, firstResult, maxResult, order, asc);
	}

	public void validarDatas(Date dtInicial, Date dtFinal)
			throws ApplicationBusinessException {

		if (dtInicial != null && dtFinal != null) {
			if (dtInicial.after(dtFinal)) {
				throw new ApplicationBusinessException(
						ParcelasAFPendEntONExceptionCode.VALIDACAO_DT_AF_PEND_ENTREGA);
			}
		}
	}

	public void desfazerSelecaoTodasAFs(List<ParcelasAFPendEntVO> listaAFs,
			List<ParcelasAFPendEntVO> listaAFsSelecionadas) {
		listaAFsSelecionadas.clear();
		if (listaAFs != null && !listaAFs.isEmpty()) {
			for (ParcelasAFPendEntVO vo : listaAFs) {
				vo.setAtivo(Boolean.FALSE);
			}
		}
	}

	public void selecionarTodasAFs(boolean todasAFsSelecionadas,
			List<ParcelasAFPendEntVO> listaAFs,
			List<ParcelasAFPendEntVO> listaAFsSelecionadas) {
		if (todasAFsSelecionadas) {
			for (ParcelasAFPendEntVO vo : listaAFs) {
				vo.setAtivo(Boolean.TRUE);
			}
			listaAFsSelecionadas.clear();
			listaAFsSelecionadas.addAll(listaAFs);
		} else {
			desfazerSelecaoTodasAFs(listaAFs, listaAFsSelecionadas);
		}
	}

	public void selecionarAFs(ParcelasAFPendEntVO parcela,
			List<ParcelasAFPendEntVO> afsPendentes,
			List<ParcelasAFPendEntVO> afsSelecionados) {

		for (ParcelasAFPendEntVO vo : afsPendentes) {
			if (vo.equals(parcela)) {
				vo.setAtivo(parcela.getAtivo());
				if (parcela.getAtivo()) {
					afsSelecionados.add(parcela);
				} else {
					afsSelecionados.remove(parcela);
				}
				break;
			}
		}
	}

	public ScoContatoFornecedor obterPrimeiroContatoPorFornecedor(ScoFornecedor fornecedor) {
		return getParcelasAFPendEntRN().obterPrimeiroContatoPorFornecedor(fornecedor);
	}
	
	private ParcelasAFPendEntRN getParcelasAFPendEntRN() {
		return parcelasAFPendEntRN;
	}


	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}
}
