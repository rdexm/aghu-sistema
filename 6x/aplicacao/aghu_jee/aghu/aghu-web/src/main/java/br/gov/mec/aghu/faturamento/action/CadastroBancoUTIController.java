package br.gov.mec.aghu.faturamento.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatSaldoUTIVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroBancoUTIController extends ActionController {

	private static final long serialVersionUID = 2879105568647544309L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatSaldoUTIVO vo;
	private boolean emEdicao;
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

		if(vo == null){
			vo =  new  FatSaldoUTIVO();
		}
	
	}

	public String gravar() {
		try {
			faturamentoFacade.persistirBancoUTI(vo, Boolean.valueOf(emEdicao));
			if (emEdicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_BANCO_UTI");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_BANCO_UTI");
			}
			setEmEdicao(true);			
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		limpar();
		return getVoltarPara();
	}
	
	private void limpar(){
		vo = null;
	}

	public String getVoltarPara() {
		return "faturamento-pesquisaBancoUTI";
	}

	public IFaturamentoFacade getControleInfeccaoFacade() {
		return faturamentoFacade;
	}

	public void setControleInfeccaoFacade(
			IFaturamentoFacade controleInfeccaoFacade) {
		this.faturamentoFacade = controleInfeccaoFacade;
	}

	public FatSaldoUTIVO getVo() {
		return vo;
	}

	public void setVo(FatSaldoUTIVO vo) {
		this.vo = vo;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
}
