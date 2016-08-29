package br.gov.mec.aghu.sig.custos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesosClientesObjetoCustoController extends ActionController {

	private static final String PESQUISAR_OBJETO_CUSTO_PESO_CLIENTES = "pesquisarObjetoCustoPesoClientes";

	private static final Log LOG = LogFactory.getLog(PesosClientesObjetoCustoController.class);

	private static final long serialVersionUID = 5754151981822242609L;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ICustosSigFacade custosSigFacade;

	private Integer dirSeq;
	private Integer ocvSeq;

	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private SigDirecionadores sigDirecionadores;

	private List<SigObjetoCustoClientes> listaClientes;

	private Boolean clientesSemValor;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.sigDirecionadores = this.custosSigCadastrosBasicosFacade.obterDirecionador(this.dirSeq);
		this.sigObjetoCustoVersoes = this.custosSigFacade.obterObjetoCustoVersoes(this.ocvSeq);
		this.listaClientes = this.custosSigCadastrosBasicosFacade.buscaObjetoClienteVersaoAtivo(this.sigObjetoCustoVersoes, this.sigDirecionadores, null);
		this.setClientesSemValor(Boolean.FALSE);
	
	}

	public void validaPreenchimentoPesos() {
		if (!this.clientesSemValor) {
			if (listaClientes != null && !listaClientes.isEmpty()) {
				for (SigObjetoCustoClientes cliente : listaClientes) {
					if (cliente.getValor() == null) {
						this.openDialog("modalValidaPreenchimentoPesosWG");
						return;
					} 
				}
			}
		}
		
		this.gravar();
	}

	public void gravar() {
		try {
			this.custosSigCadastrosBasicosFacade.atualizarValorCliente(this.listaClientes);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRAVADO_PESOS_SUCESSO",
					this.sigObjetoCustoVersoes.getSigObjetoCustoCctsPrincipal().getFccCentroCustos().getDescricao());

			if (this.getClientesSemValor()) {
				this.listaClientes = this.custosSigCadastrosBasicosFacade.buscaObjetoClienteVersaoAtivo(this.sigObjetoCustoVersoes, this.sigDirecionadores,
						Boolean.TRUE);
			} else {
				this.listaClientes = this.custosSigCadastrosBasicosFacade.buscaObjetoClienteVersaoAtivo(this.sigObjetoCustoVersoes, this.sigDirecionadores,
						null);
			}

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public String voltar() {
		return PESQUISAR_OBJETO_CUSTO_PESO_CLIENTES;
	}

	// GETS AND SETS

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public Integer getDirSeq() {
		return dirSeq;
	}

	public void setDirSeq(Integer dirSeq) {
		this.dirSeq = dirSeq;
	}

	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
	}

	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	public List<SigObjetoCustoClientes> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(List<SigObjetoCustoClientes> listaClientes) {
		this.listaClientes = listaClientes;
	}

	public Boolean getClientesSemValor() {
		return clientesSemValor;
	}

	public void setClientesSemValor(Boolean clientesSemValor) {
		this.clientesSemValor = clientesSemValor;
	}
}
