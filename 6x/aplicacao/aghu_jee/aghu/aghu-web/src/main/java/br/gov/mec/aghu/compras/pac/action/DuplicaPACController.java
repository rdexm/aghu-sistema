package br.gov.mec.aghu.compras.pac.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.DupItensPACVO;
import br.gov.mec.aghu.dominio.DominioTipoDuplicacaoPAC;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class DuplicaPACController extends ActionController {

	private static final long serialVersionUID = 6311943654533755944L;

	private static final String PAGE_PROCESSO_ADM_COMPRA_CRUD = "processoAdmCompraCRUD";

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

    @Inject
    private ProcessoAdmComprasPaginatorController processoAdmComprasPaginatorController;

    @Inject
    private ProcessoAdmComprasController processoAdmComprasController;

	private Integer numeroPAC;
	private DominioTipoDuplicacaoPAC tipoDuplicacaoPAC;
	private List<DupItensPACVO> listaDupItensPAC = new ArrayList<DupItensPACVO>();

	public enum DuplicaPACControllerExceptionCode implements BusinessExceptionCode {
		MSG_PAC_DUPLICADO_COM_SUCESSO_M02, MSG_PAC_DUPLICADO_COM_SUCESSO_M04, MSG_PAC_DUPLICADO_ERRO_SEM_ITENS_M05;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		this.setTipoDuplicacaoPAC(DominioTipoDuplicacaoPAC.ABERTURA_PAC);
		this.pesquisar();
	
	}
	

	public String voltar() {
		return PAGE_PROCESSO_ADM_COMPRA_CRUD;
	}

	public void pesquisar() {
		try {
			this.setListaDupItensPAC(this.pacFacade.pesquisarItensPAC(this.getNumeroPAC(), this.getTipoDuplicacaoPAC()));
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.INFO,
					DuplicaPACControllerExceptionCode.MSG_PAC_DUPLICADO_ERRO_SEM_ITENS_M05.toString(), this.getNumeroPAC());
		}
	}

	public String duplicar() {
		ScoLicitacao scoLicitacao = null;
		try {
			scoLicitacao = this.pacFacade.duplicarPAC(numeroPAC, tipoDuplicacaoPAC, servidorLogadoFacade.obterServidorLogado(), listaDupItensPAC);
			this.setNumeroPAC(scoLicitacao.getNumero());

			if (DominioTipoDuplicacaoPAC.ABERTURA_PAC.equals(this.getTipoDuplicacaoPAC())) {
				this.apresentarMsgNegocio(Severity.INFO,
						DuplicaPACControllerExceptionCode.MSG_PAC_DUPLICADO_COM_SUCESSO_M02.toString(), scoLicitacao.getNumero());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						DuplicaPACControllerExceptionCode.MSG_PAC_DUPLICADO_COM_SUCESSO_M04.toString(), scoLicitacao.getNumero());
			}
			if (this.getTipoDuplicacaoPAC().equals(DominioTipoDuplicacaoPAC.AUTORIZACAO_FORN)) {
				this.autFornecimentoFacade.gerarAf(numeroPAC, scoLicitacao.getModalidadeEmpenho());
			}

            processoAdmComprasController.setNumeroPac(scoLicitacao.getNumero());
            processoAdmComprasController.setVoltarParaUrl("processoAdmCompraList");
            processoAdmComprasPaginatorController.setLicitacaoSelecionado(scoLicitacao);
            return  processoAdmComprasPaginatorController.editar();

        } catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;

	}

	public boolean isReadOnlyValorUnitPrevisto() {
		return DominioTipoDuplicacaoPAC.AUTORIZACAO_FORN.equals(this.getTipoDuplicacaoPAC());
	}

	public Integer getNumeroPAC() {
		return numeroPAC;
	}

	public void setNumeroPAC(Integer numeroPAC) {
		this.numeroPAC = numeroPAC;
	}

	public DominioTipoDuplicacaoPAC getTipoDuplicacaoPAC() {
		return tipoDuplicacaoPAC;
	}

	public void setTipoDuplicacaoPAC(DominioTipoDuplicacaoPAC tipoDuplicacaoPAC) {
		this.tipoDuplicacaoPAC = tipoDuplicacaoPAC;
	}

	public List<DupItensPACVO> getListaDupItensPAC() {
		return listaDupItensPAC;
	}

	public void setListaDupItensPAC(List<DupItensPACVO> listaDupItensPAC) {
		this.listaDupItensPAC = listaDupItensPAC;
	}
}
