package br.gov.mec.aghu.sig.custos.action;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.dominio.DominioTipoRateio;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoPesos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesoObjetoCustoController extends ActionController {

	private static final String MANTER_OBJETOS_CUSTO = "manterObjetosCusto";

	private static final String PESO_OBJETO_CUSTO_LIST = "pesoObjetoCustoList";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(PesoObjetoCustoController.class);

	private static final long serialVersionUID = -5466545657798789654L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ICentroCustoFacade centroCustosFacade;

	private FccCentroCustos centroCusto;
	private Integer codigoCentroCusto;
	private String nomeCentroCusto;
	private DominioTipoRateio tipoRateio;
	private SigDirecionadores direcionador;

	private List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto;
	private Map<Integer, Double> mapeamentoSus = null;

	private Boolean exibirCamposObjetosCusto;
	private Boolean exibirTabelaObjetosCusto;
	private Boolean exibirColunaValorAtual;
	private Boolean voltarPaginaManterObjetoCusto;

	private Integer seqObjetoCustoVersao;

	private static final String NOME_DIRECIONADOR_TABELA_SUS = "Tabela SUS";

	public void iniciar() {
		// Limpa os campos da página
		this.limpar();

		if(this.getCodigoCentroCusto() != null){
			this.setCentroCusto(centroCustosFacade.obterCentroCustoPorChavePrimaria(this.getCodigoCentroCusto()));
		}

		if (this.getCentroCusto() != null) {

			// Carrega as listas utilizadas pela página
			this.montarListas();

			// Define o nome
			this.setNomeCentroCusto(this.getCentroCusto().getCodigo() + " - " + this.getCentroCusto().getDescricao());

			// Se os parametrs já existirem então já os carrega
			if (this.getCentroCusto().getSigParamCentroCusto() != null) {
				this.setTipoRateio(this.getCentroCusto().getSigParamCentroCusto().getTipoRateio());
				this.setDirecionador(this.getCentroCusto().getSigParamCentroCusto().getSigDirecionadores());
			}

		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CENTRO_CUSTO_NAO_ENCONTRADO");
		}
	}

	protected void limpar() {
		this.setCentroCusto(null);
		this.setNomeCentroCusto(null);
		this.setTipoRateio(null);
		this.setDirecionador(null);
		this.setListaObjetoCustoCentroCusto(null);
		this.setExibirCamposObjetosCusto(false);
		this.setExibirTabelaObjetosCusto(false);
		this.setExibirColunaValorAtual(true);
	}

	protected void montarListas() {

		// Busca a lista de objetos de custo
		this.setListaObjetoCustoCentroCusto(custosSigFacade.pesquisarObjetosCustoCentroCusto(this.getCentroCusto()));

		// Se existem objetos de custos vinculados ao centro de custo
		if (this.getListaObjetoCustoCentroCusto() != null && !this.getListaObjetoCustoCentroCusto().isEmpty()) {

			// Faz a consulta para obter os valores da tabela SUS
			mapeamentoSus = this.custosSigCadastrosBasicosFacade.pesquisarPesoTabelaUnificadaSUS(this.getCentroCusto().getCodigo());

			// Percorre toda a lista
			for (SigObjetoCustoCcts objetoCustoCcts : this.getListaObjetoCustoCentroCusto()) {

				// E verifica se não existe o peso do objeto de custo
				if (objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos() == null
						|| objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos().getSeq() == null) {
					// Para criar uma nova instancia que deverá ser usada como
					// auxiliar da tabela
					objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().setSigObjetoCustoPesos(new SigObjetoCustoPesos());
				}

				// Armazena o seq do centro de custo
				//Integer seq = objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSeq();

				// E verifica se no mapeamento do sus existe
				//if (mapeamentoSus != null && mapeamentoSus.containsKey(seq)) {
				//	// Para armazenar o valor utilizado
				//	objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos()
				//			.setValor(new BigDecimal(mapeamentoSus.get(seq)));
				//} else {
				//	objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos().setValor(null);
				//}
			}
		}
	}
	
	public BigInteger buscarValorInteiroMapeamentoTabelaSUS(Integer seqObjetoCusto){
		if (mapeamentoSus != null && mapeamentoSus.containsKey(seqObjetoCusto) && mapeamentoSus.get(seqObjetoCusto) != null) {
			return BigInteger.valueOf(mapeamentoSus.get(seqObjetoCusto).longValue());
		} else {
			return null;
		}
	}

	public String gravar() {

		try {
			this.custosSigCadastrosBasicosFacade.persistirPesosObjetoCusto(this.getListaObjetoCustoCentroCusto(), this.getCentroCusto(),
					this.getTipoRateio(), this.getDirecionador(), this.estaUtilizandoTabelaSUS(), this.mapeamentoSus);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAO_PESO_OBJETO_CUSTO", this.getCentroCusto().getDescricao());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		if (this.getVoltarPaginaManterObjetoCusto() != null && this.getVoltarPaginaManterObjetoCusto()) {
			return MANTER_OBJETOS_CUSTO;
		} else {
			return PESO_OBJETO_CUSTO_LIST;
		}
	}

	public String cancelar() {

		if (this.getVoltarPaginaManterObjetoCusto() != null && this.getVoltarPaginaManterObjetoCusto()) {
			return MANTER_OBJETOS_CUSTO;
		} else {

			// Descarta as alterações feitas nos objetos que já estão salvos no
			// banco de dados
			if (this.getListaObjetoCustoCentroCusto() != null) {
				for (SigObjetoCustoCcts objetoCustoCcts : this.getListaObjetoCustoCentroCusto()) {
					if (objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos().getSeq() != null) {
						// this.custosSigFacade.refresh(objetoCustoCcts.getSigObjetoCustoVersoes().getSigObjetoCustos().getSigObjetoCustoPesos());
						// TODO MIGRAÇÃO
						LOG.debug("refresh");
					}
				}
			}
			return PESO_OBJETO_CUSTO_LIST;
		}
	}

	public String visualizarObjetoCusto() {
		return MANTER_OBJETOS_CUSTO;
	}

	public Boolean estaUtilizandoTabelaSUS() {
		if (this.getDirecionador() == null) {
			return false;
		} else {
			return this.getDirecionador().getNome().equals(NOME_DIRECIONADOR_TABELA_SUS);
		}
	}

	public List<SigDirecionadores> listarDirecionadores() {
		return custosSigCadastrosBasicosFacade.pesquisarDirecionadores(DominioSituacao.A, DominioTipoDirecionadorCustos.RO);
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}

	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public DominioTipoRateio getTipoRateio() {
		return tipoRateio;
	}

	public void setTipoRateio(DominioTipoRateio tipoRateio) {
		this.tipoRateio = tipoRateio;

		// Limpa o direcionador quando mudar o tipo de rateio
		this.setDirecionador(null);

		// Esconde o painel com os campos do objeto de custo
		this.setExibirCamposObjetosCusto(false);

		// Só mostra os campos dos objetos de custo quanto for selecionado o
		// tipo P (Peso)
		if (tipoRateio == DominioTipoRateio.P) {

			// E existir objetos de custo
			if (this.getListaObjetoCustoCentroCusto() != null && !this.getListaObjetoCustoCentroCusto().isEmpty()) {
				this.setExibirCamposObjetosCusto(true);
			}
			// Caso contrário limpa o tipo de rateio e exibe mensagem de aviso
			else {
				this.tipoRateio = null;
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_SEM_OBJETO_CUSTO");
			}
		}
	}

	public List<SigObjetoCustoCcts> getListaObjetoCustoCentroCusto() {
		return listaObjetoCustoCentroCusto;
	}

	public void setListaObjetoCustoCentroCusto(List<SigObjetoCustoCcts> listaObjetoCustoCentroCusto) {
		this.listaObjetoCustoCentroCusto = listaObjetoCustoCentroCusto;
	}

	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;

		// Só exibe a tabela se o direcionador for selecionado
		if (this.direcionador == null) {
			this.setExibirTabelaObjetosCusto(false);
		} else {

			this.setExibirTabelaObjetosCusto(true);

			// Só pode exibir a coluna do valor atual se for o direcionador do
			// sus
			if (this.estaUtilizandoTabelaSUS()) {
				this.setExibirColunaValorAtual(true);
			} else {
				this.setExibirColunaValorAtual(false);
			}
		}
	}

	public Boolean getExibirCamposObjetosCusto() {
		return exibirCamposObjetosCusto;
	}

	public void setExibirCamposObjetosCusto(Boolean exibirCamposObjetosCusto) {
		this.exibirCamposObjetosCusto = exibirCamposObjetosCusto;
	}

	public Boolean getExibirColunaValorAtual() {
		return exibirColunaValorAtual;
	}

	public void setExibirColunaValorAtual(Boolean exibirColunaValorAtual) {
		this.exibirColunaValorAtual = exibirColunaValorAtual;
	}

	public Boolean getExibirTabelaObjetosCusto() {
		return exibirTabelaObjetosCusto;
	}

	public void setExibirTabelaObjetosCusto(Boolean exibirTabelaObjetosCusto) {
		this.exibirTabelaObjetosCusto = exibirTabelaObjetosCusto;
	}

	public Integer getSeqObjetoCustoVersao() {
		return seqObjetoCustoVersao;
	}

	public void setSeqObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		this.seqObjetoCustoVersao = seqObjetoCustoVersao;
	}

	public Boolean getVoltarPaginaManterObjetoCusto() {
		return voltarPaginaManterObjetoCusto != null ? voltarPaginaManterObjetoCusto : Boolean.FALSE;
	}

	public void setVoltarPaginaManterObjetoCusto(Boolean voltarPaginaManterObjetoCusto) {
		this.voltarPaginaManterObjetoCusto = voltarPaginaManterObjetoCusto;
	}

}
