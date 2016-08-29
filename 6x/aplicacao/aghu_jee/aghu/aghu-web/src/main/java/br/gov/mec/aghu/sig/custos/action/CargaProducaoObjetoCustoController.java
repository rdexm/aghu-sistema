package br.gov.mec.aghu.sig.custos.action;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.dominio.DominioColunaExcel;
import br.gov.mec.aghu.dominio.DominioGrupoDetalheProducao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCalculoObjeto;
import br.gov.mec.aghu.dominio.DominioTipoDirecionadorCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDetalheProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;
import br.gov.mec.aghu.sig.custos.vo.EntradaProducaoObjetoCustoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CargaProducaoObjetoCustoController extends ActionController {

	private static final String MENSAGEM_15_ERROS_LEITURA_DOCUMENTO = "MENSAGEM_15_ERROS_LEITURA_DOCUMENTO";

	private static final String PRODUCAO_OBJETO_CUSTO_CRUD = "producaoObjetoCustoCRUD";

	private static final String PRODUCAO_OBJETO_CUSTO_LIST = "producaoObjetoCustoList";

	private static final Log LOG = LogFactory.getLog(CargaProducaoObjetoCustoController.class);

	private static final long serialVersionUID = -3743429245675800924L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ICustosSigProcessamentoFacade custosSigProcessamentoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Integer codigoCentroCusto;
	private Integer seqDetalheProducao;

	private SigObjetoCustoVersoes objetoCustoVersao;
	private UploadedFile UploadedFile;
	private DominioColunaExcel colCentroCusto;
	private DominioColunaExcel colValor;
	private Integer linInicial;
	private SigProcessamentoCusto competencia;
	private boolean arquivoValidado = false;
	private List<String> listaMensagensErroValidacao;
	private List<SigProcessamentoCusto> listaCompetencias;
	private List<SigDirecionadores> listaDirecionadores;
	private SigDirecionadores direcionador;
	private FccCentroCustos centroCusto;
	private InputStream input;

	private List<EntradaProducaoObjetoCustoVO> listaEntrada;

	@PostConstruct
	protected void inicializar() {
		LOG.debug("begin conversation");
		this.begin(conversation, true);
	}

	public void iniciar() {
		this.setObjetoCustoVersao(null);
		this.setDirecionador(null);
		this.setCompetencia(null);
		this.setColCentroCusto(null);
		this.setColValor(null);
		this.setLinInicial(null);
		this.setUploadedFile(null);
		this.setArquivoValidado(false);
		this.input = null;
		this.setListaDirecionadores(new ArrayList<SigDirecionadores>());
		this.setCentroCusto(centroCustoFacade.pesquisarCentroCustoAtivoPorCodigo(this.getCodigoCentroCusto()));
		this.carregarListaCompetencias();
	}

	private void carregarListaCompetencias() {
		this.setListaCompetencias(this.custosSigProcessamentoFacade.pesquisarCompetenciaSemProducao(this.getObjetoCustoVersao(),
				this.getDirecionador()));
	}

	public List<SigObjetoCustoVersoes> pesquisarObjetoCusto(String paramPesquisa) {
		if(this.getCentroCusto() != null) {
			return custosSigFacade.buscarObjetoCustoVersoesCentroCusto(this.getCentroCusto().getCodigo(), paramPesquisa);
		}
		
		return null;
	}

	public void pesquisaDirecionadores() {
		listaDirecionadores = this.custosSigCadastrosBasicosFacade.pesquisaDirecionadoresDoObjetoCusto(objetoCustoVersao,
				DominioSituacao.A, DominioTipoDirecionadorCustos.RT, DominioTipoCalculoObjeto.PM);
	}

	public void limpaDirecionadores() {
		listaDirecionadores = null;
	}

	/**
	 * Exibe mensagem de erro para tipo de arquivo rejeitado durante o upload
	 */
	public void exibirMensagemUploadTipoArquivoRejeitado() {
		this.apresentarMsgNegocio(Severity.ERROR, "ERRO_LEITURA_ARQUIVO_ARQUIVO");
	}

	public void selecionarDirecionador() {
		this.setCompetencia(null);
		this.carregarListaCompetencias();
	}

	public String importar() throws ApplicationBusinessException {
		for (EntradaProducaoObjetoCustoVO entrada : listaEntrada) {
			SigDetalheProducao producao = new SigDetalheProducao();
			producao.setCriadoEm(new Date());
			producao.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));

			producao.setGrupo(DominioGrupoDetalheProducao.PAM);
			producao.setSigProcessamentoCustos(competencia);
			producao.setSigObjetoCustoVersoes(objetoCustoVersao);

			producao.setFccCentroCustos(this.centroCustoFacade.pesquisarCentroCustoAtivoPorCodigo(entrada.getCentroCusto()));
			producao.setSigDirecionadores(direcionador);
			producao.setQtde(new BigDecimal(entrada.getValor()));

			this.custosSigCadastrosBasicosFacade.persistirSigDetalheProducao(producao);

			this.setSeqDetalheProducao(producao.getSeq());
		}
		return PRODUCAO_OBJETO_CUSTO_CRUD;
	}

	public void validarDocumento() {
		arquivoValidado = false;
		setListaEntrada(null);
		setListaMensagensErroValidacao(null);
		if (colCentroCusto.equals(colValor)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_COLUNA_VALOR_CC_E_A_MESMA");
			return;
		}

		if (this.input != null) {
			try {
				setListaEntrada(custosSigFacade.efetuaLeituraExcel(input, colCentroCusto, colValor, linInicial));
			} catch (ApplicationBusinessException e) {
				this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				return;
			} catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				return;
			}
			
			this.efetuaValidacaoInformacoesDocumento(getListaEntrada());
		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ARQUIVO_NAO_SELECIONADO");
		}
		
		if(getListaMensagensErroValidacao() != null && getListaMensagensErroValidacao().size() > 0) {
			this.openDialog("modalVisualizarErroLeituraArquivoWG");
		}
	}
	
	private SigObjetoCustoVersoes buscarObjetoCustoVersoesAtualizado() {
		return this.custosSigFacade.buscarObjetoCustoVersoesAtualizado(objetoCustoVersao);
	}

	private void efetuaValidacaoInformacoesDocumento(List<EntradaProducaoObjetoCustoVO> listaEntrada) {

		if (listaEntrada != null && listaEntrada.size() > 0) {
			arquivoValidado = true;

			// Valida se foram encontrados problemas na leitura do arquivo
			setListaMensagensErroValidacao(new ArrayList<String>());
			Set<Integer> listCodigoUnicoCentroCusto = new HashSet<Integer>();

			for (EntradaProducaoObjetoCustoVO entradaProducaoObjetoCustoVO : listaEntrada) {
				if (!entradaProducaoObjetoCustoVO.isLeituraCorreta()) {
					arquivoValidado = false;
					if (getListaMensagensErroValidacao().size() <= 14) {
						getListaMensagensErroValidacao().add(entradaProducaoObjetoCustoVO.getErroLeitura());
					} else if (getListaMensagensErroValidacao().size() == 15) {
						getListaMensagensErroValidacao().add(this.buscarMensagem(MENSAGEM_15_ERROS_LEITURA_DOCUMENTO));
					}
				} else {
					listCodigoUnicoCentroCusto.add(entradaProducaoObjetoCustoVO.getCentroCusto());
				}
			}

			// Valida se nao há centro de custos repetidos
			if (arquivoValidado) {
				if (listCodigoUnicoCentroCusto.size() != listaEntrada.size()) {
					getListaMensagensErroValidacao().add(this.buscarMensagem("MENSAGEM_CENTRO_CUSTOS_REPETIDOS_LEITURA_DOCUMENTO"));
					// Deixar o add("") porque serve para definir o tamanho da
					// modal de apresentação do erro quando o mesmo acontece
					getListaMensagensErroValidacao().add("");
					arquivoValidado = false;
				}
			}

			// valida se o centro de custo existe na base de dados
			if (arquivoValidado) {
				for (EntradaProducaoObjetoCustoVO entradaProducaoObjetoCustoVO : listaEntrada) {
					FccCentroCustos centroCusto = centroCustoFacade.obterCentroCustoPorChavePrimaria(entradaProducaoObjetoCustoVO
							.getCentroCusto());
					if (centroCusto == null) {
						if (getListaMensagensErroValidacao().size() <= 14) {
							getListaMensagensErroValidacao().add(
									this.buscarMensagem("MENSAGEM_CENTRO_CUSTO_NAO_ENCONTRADO_DOCUMENTO", entradaProducaoObjetoCustoVO
											.getCentroCusto().toString()));
							arquivoValidado = false;
						} else if (getListaMensagensErroValidacao().size() == 15) {
							getListaMensagensErroValidacao().add(this.buscarMensagem(MENSAGEM_15_ERROS_LEITURA_DOCUMENTO));
						}
					} else {
						boolean clienteFazParteOC = false;

						SigObjetoCustoVersoes objCustoVersoes = this.buscarObjetoCustoVersoesAtualizado();

						for (SigObjetoCustoClientes cliente : objCustoVersoes.getListObjetoCustoClientes()) {
							if (cliente.getCentroCusto() != null) {
								if (cliente.getCentroCusto().getCodigo().equals(centroCusto.getCodigo())
										&& cliente.getSituacao().equals(DominioSituacao.A)
										&& cliente.getDirecionadores().equals(direcionador)) {
									clienteFazParteOC = true;
									continue;
								} else if (cliente.getCentroCusto().getCodigo().equals(centroCusto.getCodigo())
										&& cliente.getSituacao().equals(DominioSituacao.I)) {
									if (getListaMensagensErroValidacao().size() <= 14) {
										getListaMensagensErroValidacao().add(
												this.buscarMensagem("MENSAGEM_CENTRO_CUSTO_CLIENTE_INATIVO", cliente.getCentroCusto()
														.getCodigo().toString(), entradaProducaoObjetoCustoVO.getNroLinha()));
										arquivoValidado = false;
									} else if (getListaMensagensErroValidacao().size() == 15) {
										getListaMensagensErroValidacao().add(this.buscarMensagem(MENSAGEM_15_ERROS_LEITURA_DOCUMENTO));
									}

									clienteFazParteOC = true;
									continue;
								}
							}
						}
						if (!clienteFazParteOC) {

							if (getListaMensagensErroValidacao().size() <= 14) {
								getListaMensagensErroValidacao().add(
										this.buscarMensagem("MENSAGEM_DIRECIONADOR_NAO_CLIENTE", direcionador.getNome(), centroCusto
												.getCodigo().toString(), entradaProducaoObjetoCustoVO.getNroLinha()));
								arquivoValidado = false;
							} else if (getListaMensagensErroValidacao().size() == 15) {
								getListaMensagensErroValidacao().add(this.buscarMensagem(MENSAGEM_15_ERROS_LEITURA_DOCUMENTO));
							}

						}
					}
				}
			}
		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ARQUIVO_VAZIO");
		}
	}

	public List<SigDirecionadores> listarDirecionadores() {
		return this.getListaDirecionadores();
	}

	public List<SigProcessamentoCusto> listarCompetencias() {
		return this.getListaCompetencias();
	}

	public String cancelar() {
		return PRODUCAO_OBJETO_CUSTO_LIST;
	}

	protected String buscarMensagem(String chave, Object... parametros) {
		return WebUtil.initLocalizedMessage(chave, null, parametros);
	}

	public void listener(FileUploadEvent event) throws IOException {
		this.UploadedFile = event.getFile();
		this.input = UploadedFile.getInputstream();
	}

	public void clearUploadData() {
		this.arquivoValidado = false;
		this.UploadedFile = null;
	}

	public DominioColunaExcel getColCentroCusto() {
		return colCentroCusto;
	}

	public void setColCentroCusto(DominioColunaExcel colCentroCusto) {
		this.colCentroCusto = colCentroCusto;
	}

	public DominioColunaExcel getColValor() {
		return colValor;
	}

	public void setColValor(DominioColunaExcel colValor) {
		this.colValor = colValor;
	}

	public Integer getLinInicial() {
		return linInicial;
	}

	public void setLinInicial(Integer linInicial) {
		this.linInicial = linInicial;
	}

	public SigObjetoCustoVersoes getObjetoCustoVersao() {
		return objetoCustoVersao;
	}

	public void setObjetoCustoVersao(SigObjetoCustoVersoes objetoCustoVersao) {
		this.objetoCustoVersao = objetoCustoVersao;
	}

	public UploadedFile getUploadedFile() {
		return UploadedFile;
	}

	public void setUploadedFile(UploadedFile UploadedFile) {
		this.UploadedFile = UploadedFile;
	}

	public boolean isArquivoValidado() {
		return arquivoValidado;
	}

	public void setArquivoValidado(boolean arquivoValidado) {
		this.arquivoValidado = arquivoValidado;
	}

	public List<String> getListaMensagensErroValidacao() {
		return listaMensagensErroValidacao;
	}

	public void setListaMensagensErroValidacao(List<String> listaMensagensErroValidacao) {
		this.listaMensagensErroValidacao = listaMensagensErroValidacao;
	}

	public List<SigProcessamentoCusto> getListaCompetencias() {
		return listaCompetencias;
	}

	public void setListaCompetencias(List<SigProcessamentoCusto> listaCompetencias) {
		this.listaCompetencias = listaCompetencias;
	}

	public SigProcessamentoCusto getCompetencia() {
		return competencia;
	}

	public void setCompetencia(SigProcessamentoCusto competencia) {
		this.competencia = competencia;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public List<SigDirecionadores> getListaDirecionadores() {
		return listaDirecionadores;
	}

	public void setListaDirecionadores(List<SigDirecionadores> listaDirecionadores) {
		this.listaDirecionadores = listaDirecionadores;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public List<EntradaProducaoObjetoCustoVO> getListaEntrada() {
		return listaEntrada;
	}

	public void setListaEntrada(List<EntradaProducaoObjetoCustoVO> listaEntrada) {
		this.listaEntrada = listaEntrada;
	}

	public Integer getSeqDetalheProducao() {
		return seqDetalheProducao;
	}

	public void setSeqDetalheProducao(Integer seqDetalheProducao) {
		this.seqDetalheProducao = seqDetalheProducao;
	}
}
