package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class PontoParadaSolicitacaoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(PontoParadaSolicitacaoController.class);

	private static final long serialVersionUID = 4731533190708355034L;

	private static final String PONTO_PARADA_SOLICITACAO_LIST = "pontoParadaSolicitacaoList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoPontoParadaSolicitacao pontoParada;
	private ScoPontoParadaSolicitacao pontoParadaAnterior;
	private Short codigo;
	private Boolean indAtivo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setPontoParada(new ScoPontoParadaSolicitacao());
			this.getPontoParada().setSituacao(DominioSituacao.A);
			this.setIndAtivo(true);
		} else {
			this.setPontoParada(this.comprasCadastrosBasicosFacade.obterPontoParada(this.codigo));

			if(pontoParada == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			if (this.getPontoParada().getSituacao() == DominioSituacao.A) {
				this.setIndAtivo(true);
			}
			else {
				this.setIndAtivo(false);
			}
			try {
				this.setPontoParadaAnterior((ScoPontoParadaSolicitacao) this.getPontoParada().clone());
			} catch (CloneNotSupportedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	
	}

	public String gravar() {
		try {
			
			if (this.getPontoParada() != null) {
				
				if (this.indAtivo) {
					this.getPontoParada().setSituacao(DominioSituacao.A);
				} else {
					this.getPontoParada().setSituacao(DominioSituacao.I);
				}
				
				final boolean novo = this.getPontoParada().getCodigo() == null;

				if (novo) {
					this.comprasCadastrosBasicosFacade.inserirPontoParadaSolicitacao(this.getPontoParada());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PONTO_PARADA_SOLIC_INSERT_SUCESSO");
				} else {
					this.comprasCadastrosBasicosFacade.alterarPontoParadaSolicitacao(this.getPontoParada(), this.getPontoParadaAnterior());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PONTO_PARADA_SOLIC_UPDATE_SUCESSO");
				}

				return cancelar();
			}
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		codigo = null;
		pontoParada = null;
		return PONTO_PARADA_SOLICITACAO_LIST;
	}

	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}

	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public ScoPontoParadaSolicitacao getPontoParadaAnterior() {
		return pontoParadaAnterior;
	}

	public void setPontoParadaAnterior(
			ScoPontoParadaSolicitacao pontoParadaAnterior) {
		this.pontoParadaAnterior = pontoParadaAnterior;
	}

	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}
}
