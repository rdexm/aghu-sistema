package br.gov.mec.aghu.suprimentos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomatica;
import br.gov.mec.aghu.model.SceAlmoxarifadoTransferenciaAutomaticaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterAlmoxarifadoTransferenciaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3212573330064288889L;

	private static final String PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO_TRANSFERENCIA = "estoque-pesquisaAlmoxarifadoTransferencia";

	@EJB
	private IEstoqueFacade estoqueFacade;

	private SceAlmoxarifado almoxOrigem;
	private SceAlmoxarifado almoxDestino;
	private SceAlmoxarifadoTransferenciaAutomatica almoxTransfAuto;
	private DominioSituacao indSituacao;
	private Short almoxOrigemSeq;
	private Short almoxDestinoSeq;

	public void iniciar() {
	 

		this.almoxTransfAuto = new SceAlmoxarifadoTransferenciaAutomatica();
		this.almoxTransfAuto.setSituacao(DominioSituacao.A); // Valor padrão
	
	}

	public void limpar() {
		this.almoxOrigem = null;
		this.almoxDestino = null;
		this.almoxTransfAuto = new SceAlmoxarifadoTransferenciaAutomatica();
		this.almoxTransfAuto.setSituacao(DominioSituacao.A); // Valor padrão
	}

	// Suggestions
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param) {
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}

	public String gravar() {
		try {
			if (this.almoxOrigemSeq == null && this.almoxDestinoSeq == null) {
				Long counter = estoqueFacade.pesquisarSceAlmoxTransfAutomaticasCount((this.almoxOrigem != null) ? this.almoxOrigem.getSeq() : null, (this.almoxDestino != null) ? this.almoxDestino.getSeq() : null, null);
				if (counter > 0) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_ALMOX_TRANS_JA_EXISTENTE");
					return "";
				}
			}
			this.getAlmoxTransfAuto().setId(new SceAlmoxarifadoTransferenciaAutomaticaId(this.almoxOrigem.getSeq(), this.almoxDestino.getSeq()));
			// Insere
			this.estoqueFacade.inserirSceAlmoxTransfAutomaticas(this.getAlmoxTransfAuto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ALMOX_TRANS");

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return "";
		}
		this.resetarTela();
		return PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO_TRANSFERENCIA;
	}

	public String voltar() {
		this.resetarTela();
		return PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO_TRANSFERENCIA;
	}
	
	private void resetarTela(){
		this.iniciar();
		this.limpar();
	}

	public SceAlmoxarifado getAlmoxOrigem() {
		return almoxOrigem;
	}

	public void setAlmoxOrigem(SceAlmoxarifado almoxOrigem) {
		this.almoxOrigem = almoxOrigem;
	}

	public SceAlmoxarifado getAlmoxDestino() {
		return almoxDestino;
	}

	public void setAlmoxDestino(SceAlmoxarifado almoxDestino) {
		this.almoxDestino = almoxDestino;
	}

	public SceAlmoxarifadoTransferenciaAutomatica getAlmoxTransfAuto() {
		return almoxTransfAuto;
	}

	public void setAlmoxTransfAuto(SceAlmoxarifadoTransferenciaAutomatica almoxTransfAuto) {
		this.almoxTransfAuto = almoxTransfAuto;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Short getAlmoxOrigemSeq() {
		return almoxOrigemSeq;
	}

	public void setAlmoxOrigemSeq(Short almoxOrigemSeq) {
		this.almoxOrigemSeq = almoxOrigemSeq;
	}

	public Short getAlmoxDestinoSeq() {
		return almoxDestinoSeq;
	}

	public void setAlmoxDestinoSeq(Short almoxDestinoSeq) {
		this.almoxDestinoSeq = almoxDestinoSeq;
	}

}
