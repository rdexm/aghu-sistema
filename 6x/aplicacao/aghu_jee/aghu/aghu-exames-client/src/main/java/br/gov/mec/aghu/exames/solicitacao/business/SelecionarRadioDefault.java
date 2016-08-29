package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.LoteDefaultVO;
import br.gov.mec.aghu.model.AelLoteExameUsual;

public abstract class SelecionarRadioDefault {

	private SelecionarRadioDefault proximo;
	
	private IExamesFacade examesFacade;

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public LoteDefaultVO executar() {
		List<AelLoteExameUsual> listTipoLote = getLote();
		LoteDefaultVO loteDefaultVO = new LoteDefaultVO();
		if (listTipoLote != null && !listTipoLote.isEmpty()) {
			loteDefaultVO.setTipoLoteDefault(getDominioSolicitacaoExameLote());
			for (AelLoteExameUsual aelLoteExameUsual : listTipoLote) {		
				if (aelLoteExameUsual.getIndLoteDefault() == DominioSimNao.S) {
					loteDefaultVO.setLoteDefault(aelLoteExameUsual);
				}
			}
			return loteDefaultVO;
		} else {
			return executarProximo();
		}
	}

	protected abstract DominioSolicitacaoExameLote getDominioSolicitacaoExameLote();

	protected LoteDefaultVO executarProximo() {
		if (getProximo() != null) {
			return getProximo().executar();
		} else {
			LoteDefaultVO loteDefaultVO = new LoteDefaultVO();
			loteDefaultVO.setTipoLoteDefault(DominioSolicitacaoExameLote.G);
			loteDefaultVO.setLoteDefault(null);
			return loteDefaultVO;
		}
	}

	protected SelecionarRadioDefault getProximo() {
		return proximo;
	}

	public void setProximo(SelecionarRadioDefault proximo) {
		this.proximo = proximo;
	}

	protected abstract List<AelLoteExameUsual> getLote();


}
