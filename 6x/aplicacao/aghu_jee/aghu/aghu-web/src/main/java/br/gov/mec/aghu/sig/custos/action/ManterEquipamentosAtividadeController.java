package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.patrimonio.IPatrimonioService;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.EquipamentoSistemaPatrimonioVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterEquipamentosAtividadeController extends ActionController {

	private static final String MENSAGEM_SERVICO_PATRIMONIO_NAO_ENCONTRADO = "MENSAGEM_SERVICO_PATRIMONIO_NAO_ENCONTRADO";

	private static final String ADICIONAR_EQUIPAMENTOS_LOTE = "adicionarEquipamentosLote";

	private static final long serialVersionUID = 6649612877148582990L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private ManterAtividadesController manterAtividadesController;

	@Inject
	private AdicionarEquipamentoEmLoteController adicionarEquipamentoEmLoteController;

	private List<SigAtividadeEquipamentos> listEquipamentoAtividadeExclusao;

	private List<SigAtividadeEquipamentos> listEquipamentoAtividade;
	private List<SigDirecionadores> listaDirecionadores;
	private List<EquipamentoSistemaPatrimonioVO> listaEquipamentoVO;

	private SigAtividadeEquipamentos equipamentoAtividade;
	private SigAtividades atividade;
	private boolean edicao;
	private Integer indexOfAtividade;
	private String codigoEquipamentoExclusao;
	private boolean possuiAlteracao;
	private static final Integer ABA_3 = 2;
	private boolean integracaoPatrimonioOnline;

	private String descricaoBem;
	private BigDecimal valorBem;
	private BigDecimal valorDepreciado;

	private EquipamentoSistemaPatrimonioVO equipamentoSelecionado;

	private SigDirecionadores direcionadorOld;

	private DominioSituacao situacaoOld;
	private boolean atividadeJaAdicionada;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	protected void iniciarAbaEquipamentos(Integer seqAtividade) {
		this.setPossuiAlteracao(false);
		this.setaValoresIniciaisAtividade();
		this.setListEquipamentoAtividade(new ArrayList<SigAtividadeEquipamentos>());
		this.setListEquipamentoAtividadeExclusao(new ArrayList<SigAtividadeEquipamentos>());
		this.setListaEquipamentoVO(new ArrayList<EquipamentoSistemaPatrimonioVO>());
		this.verificaServicoPatrimonioEstaOnline();
		this.setAtividadeJaAdicionada(false);
		if (seqAtividade != null) {
			this.atividade = this.custosSigFacade.obterAtividade(seqAtividade);
			List<SigAtividadeEquipamentos> lista = this.custosSigFacade.pesquisarListaEquipamentosAtividade(this.atividade);
			for (SigAtividadeEquipamentos sigAtividadeEquipamentos : lista) {
				sigAtividadeEquipamentos.setCodigoCC(this.getCodigoCentroCusto());
				sigAtividadeEquipamentos.setEmEdicao(Boolean.FALSE);
			}
			this.setListEquipamentoAtividade(lista);
			this.carregaListaEquipamentosVO(lista);
			this.setAtividadeJaAdicionada(true);
		}
	}

	private void carregaListaEquipamentosVO(List<SigAtividadeEquipamentos> lista) {
		try {
			List<String> listCodigos = new ArrayList<String>();
			for (SigAtividadeEquipamentos sigAtividadeEquipamentos : lista) {
				listCodigos.add(sigAtividadeEquipamentos.getCodPatrimonio());
			}

			this.setListaEquipamentoVO(custosSigFacade.pesquisarEquipamentosSistemaPatrimonioById(listCodigos));
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_FORA");
			setIntegracaoPatrimonioOnline(Boolean.FALSE);
		}
	}

	private void verificaServicoPatrimonioEstaOnline() {
		try {
			String codigo = "0000";
			Integer cc = 0;
			this.custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(codigo, cc);
			setIntegracaoPatrimonioOnline(Boolean.TRUE);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_FORA");
			setIntegracaoPatrimonioOnline(Boolean.FALSE);
		}
	}

	private void setaValoresIniciaisAtividade() {
		setEquipamentoAtividade(new SigAtividadeEquipamentos());
		getEquipamentoAtividade().setIndSituacao(DominioSituacao.A);
		setEquipamentoSelecionado(null);
		this.setEdicao(false);
	}

	public void adicionar() {
		try {
			if (!this.integracaoPatrimonioOnline) {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
				return;
			}
			if (!this.validaEquipamentoAoAdicionar()) {
				return;
			}
			this.setPossuiAlteracao(true);
			if (this.getEquipamentoAtividade().getSeq() == null) {
				this.getEquipamentoAtividade().setCriadoEm(new Date());
			}
			this.getEquipamentoAtividade().setCodigoCC(this.getCodigoCentroCusto());
			this.getEquipamentoAtividade().setServidorResp(
					registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			this.custosSigFacade.validarInclusaoEquipamentoAtividade(this.getEquipamentoAtividade(), this.getListEquipamentoAtividade());
			this.getListEquipamentoAtividade().add(this.getEquipamentoAtividade());
			this.setaValoresIniciaisAtividade();
			this.getManterAtividadesController().setTabSelecionada(ABA_3);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String adicionarEquipamentoLote() {
		adicionarEquipamentoEmLoteController.setAtividade(atividade);
		adicionarEquipamentoEmLoteController.setCentroCusto(getFccCentroCusto());
		return ADICIONAR_EQUIPAMENTOS_LOTE;
	}

	public void editar(SigAtividadeEquipamentos equipamento, Integer indEquipamento) {
		setPossuiAlteracao(true);
		this.setEdicao(true);
		situacaoOld = equipamento.getIndSituacao();
		direcionadorOld = equipamento.getSigDirecionadores();
		getEquipamentoAtividade().setCodigoCC(this.getCodigoCentroCusto());
		this.setIndexOfAtividade(indEquipamento);
		this.setEquipamentoAtividade(equipamento);
		if (integracaoPatrimonioOnline) {
			buscaEquipamentoModuloPatrimonio(true);
		}
		this.getEquipamentoAtividade().setEmEdicao(Boolean.TRUE);
		getManterAtividadesController().setTabSelecionada(ABA_3);
	}

	public void visualizarEquipamento(SigAtividadeEquipamentos equipamento) {
		this.setEquipamentoAtividade(equipamento);
	}

	public void excluir() {
		setPossuiAlteracao(true);
		for (int i = 0; i < this.listEquipamentoAtividade.size(); i++) {
			SigAtividadeEquipamentos equipamento = this.listEquipamentoAtividade.get(i);
			if (equipamento.getCodPatrimonio().equalsIgnoreCase(codigoEquipamentoExclusao)) {
				if (equipamento.getSeq() != null) {
					if (getAtividade() != null && custosSigFacade.verificaAtividadeEstaVinculadaAoObjetoCusto(getAtividade())) {
						codigoEquipamentoExclusao = null;
						this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EXCLUSAO_EQUIPAMENTO_ATIVIDADE_OBJETO_CUSTO");
						getManterAtividadesController().setTabSelecionada(ABA_3);
						return;
					}
					listEquipamentoAtividadeExclusao.add(equipamento);
				}
				listEquipamentoAtividade.remove(i);
				break;
			}
		}
		codigoEquipamentoExclusao = null;
		getManterAtividadesController().setTabSelecionada(ABA_3);
	}

	public void gravar() {
		setPossuiAlteracao(true);
		this.setEdicao(false);
		this.getEquipamentoAtividade().setEmEdicao(Boolean.FALSE);
		try {
			this.getEquipamentoAtividade().setServidorResp(
					registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			this.getEquipamentoAtividade().setServidorResp(null);
		}
		this.getListEquipamentoAtividade().set(this.getIndexOfAtividade(), this.getEquipamentoAtividade());
		this.setEquipamentoAtividade(new SigAtividadeEquipamentos());
		this.setaValoresIniciaisAtividade();
		getManterAtividadesController().setTabSelecionada(ABA_3);
		direcionadorOld = null;
		situacaoOld = null;
	}

	public void cancelar() {
		getEquipamentoAtividade().setIndSituacao(situacaoOld);
		getEquipamentoAtividade().setSigDirecionadores(direcionadorOld);
		this.setEdicao(false);
		this.getListEquipamentoAtividade().get(indexOfAtividade).setEmEdicao(Boolean.FALSE);
		this.setaValoresIniciaisAtividade();
		codigoEquipamentoExclusao = null;
		getManterAtividadesController().setTabSelecionada(ABA_3);
	}

	public List<SigDirecionadores> getListaDirecionadores() {
		listaDirecionadores = new ArrayList<SigDirecionadores>();
		listaDirecionadores = this.custosSigCadastrosBasicosFacade.pesquisarDirecionadoresAtivosInativo(Boolean.TRUE, Boolean.TRUE);
		return listaDirecionadores;
	}

	public void setListEquipamentoAtividade(List<SigAtividadeEquipamentos> listEquipamentoAtividade) {
		this.listEquipamentoAtividade = listEquipamentoAtividade;
	}

	public List<SigAtividadeEquipamentos> getListEquipamentoAtividade() {
		return listEquipamentoAtividade;
	}

	public void setEquipamentoAtividade(SigAtividadeEquipamentos equipamentoAtividade) {
		this.equipamentoAtividade = equipamentoAtividade;
	}

	public SigAtividadeEquipamentos getEquipamentoAtividade() {
		return equipamentoAtividade;
	}

	public void setCustosSigFacade(ICustosSigFacade custosSigFacade) {
		this.custosSigFacade = custosSigFacade;
	}

	public ICustosSigFacade getCustosSigFacade() {
		return custosSigFacade;
	}

	public void setIndexOfAtividade(Integer indexOfAtividade) {
		this.indexOfAtividade = indexOfAtividade;
	}

	public Integer getIndexOfAtividade() {
		return indexOfAtividade;
	}

	public void setListEquipamentoAtividadeExclusao(List<SigAtividadeEquipamentos> listEquipamentoAtividadeExclusao) {
		this.listEquipamentoAtividadeExclusao = listEquipamentoAtividadeExclusao;
	}

	public List<SigAtividadeEquipamentos> getListEquipamentoAtividadeExclusao() {
		return listEquipamentoAtividadeExclusao;
	}

	public void setCodigoEquipamentoExclusao(String codigoEquipamentoExclusao) {
		this.codigoEquipamentoExclusao = codigoEquipamentoExclusao;
	}

	public String getCodigoEquipamentoExclusao() {
		return codigoEquipamentoExclusao;
	}

	public void setManterAtividadesController(ManterAtividadesController manterAtividadesController) {
		this.manterAtividadesController = manterAtividadesController;
	}

	public ManterAtividadesController getManterAtividadesController() {
		return manterAtividadesController;
	}

	public void setPossuiAlteracao(Boolean possuiAlteracao) {
		this.possuiAlteracao = possuiAlteracao;
	}

	public void setPossuiAlteracao(boolean possuiAlteracao) {
		this.possuiAlteracao = possuiAlteracao;
	}

	public boolean isPossuiAlteracao() {
		return possuiAlteracao;
	}

	public SigAtividades getAtividade() {
		return atividade;
	}

	public void setAtividade(SigAtividades atividade) {
		this.atividade = atividade;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public FccCentroCustos getFccCentroCusto() {
		return manterAtividadesController.getFccCentroCustos();
	}

	public Integer getCodigoCentroCusto() {
		if (this.getFccCentroCusto() == null) {
			return null;
		} else {
			return this.getFccCentroCusto().getCodigo();
		}
	}

	public Integer getCodigoCentroCustoNormalizado() {
		if (this.getCodigoCentroCusto() == null) {
			return 0;
		} else {
			return this.getCodigoCentroCusto();
		}
	}

	public void adicionaEquipamentosEmLote(List<SigAtividadeEquipamentos> listEquipamentos) {
		boolean isItemRepetido = false;
		for (SigAtividadeEquipamentos sigAtividadeEquipamentos : listEquipamentos) {
			try {
				custosSigFacade.validarInclusaoEquipamentoAtividade(sigAtividadeEquipamentos, getListEquipamentoAtividade());
				getListEquipamentoAtividade().add(sigAtividadeEquipamentos);
			} catch (ApplicationBusinessException e) {
				isItemRepetido = true;
			}
		}
		if (isItemRepetido) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EQUIPAMENTO_JA_ADICIONADO_LOTE_FAZ_PARTE_CADASTRO");
		}
		this.carregaListaEquipamentosVO(getListEquipamentoAtividade());
	}

	// _---------------------------------IMPLEMNTACAO SERVICO
	// ------------------------------------------//

	public List<EquipamentoSistemaPatrimonioVO> pesquisarEquipamento(String paramPesquisa) throws BaseException {
		List<EquipamentoSistemaPatrimonioVO> lista = custosSigFacade.pesquisarEquipamentoSistemaPatrimonio((String)paramPesquisa,
				this.getCodigoCentroCusto());
		if (CoreUtil.isNumeroInteger(paramPesquisa)) {
			EquipamentoSistemaPatrimonioVO equipamento = lista.get(0);
			if (equipamento.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_NAO_ENCONTRADO) {
				lista = new ArrayList<EquipamentoSistemaPatrimonioVO>();
			}
			if (equipamento.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_PERTENCE_OUTRO_CC) {
				lista = new ArrayList<EquipamentoSistemaPatrimonioVO>();
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_EQUIPAMENTO_OUTRO_CC");
			}
		}
		return lista;
	}

	public void posSelectionEquipamento() {
		equipamentoAtividade.setCodigoCC(this.getCodigoCentroCusto());
		equipamentoAtividade.setCodPatrimonio(this.getEquipamentoSelecionado().getCodigo());
	}

	public void posDeleteEquipamento() {
		equipamentoAtividade.setCodigoCC(null);
		equipamentoAtividade.setCodPatrimonio(null);
	}

	private boolean validaEquipamentoAoAdicionar() {
		boolean retorno = true;

		try {
			boolean parametrosCorretos = true;
			for (int i = 0; i < getEquipamentoAtividade().getCodPatrimonio().length(); i++) {
				if (!Character.isDigit(getEquipamentoAtividade().getCodPatrimonio().charAt(i))) {
					parametrosCorretos = false;
					break;
				}
			}
			if (parametrosCorretos) {
				equipamentoSelecionado = custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(getEquipamentoAtividade()
						.getCodPatrimonio(), this.getCodigoCentroCustoNormalizado());
				if (equipamentoSelecionado == null) {
					this.getEquipamentoAtividade().setCodPatrimonio("");
					this.apresentarMsgNegocio(Severity.ERROR, MENSAGEM_SERVICO_PATRIMONIO_NAO_ENCONTRADO);
					retorno = false;
				} else if (equipamentoSelecionado.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_NAO_ENCONTRADO) {
					this.getEquipamentoAtividade().setCodPatrimonio("");
					this.apresentarMsgNegocio(Severity.ERROR, MENSAGEM_SERVICO_PATRIMONIO_NAO_ENCONTRADO);
					retorno = false;
				} else if (this.manterAtividadesController.getFccCentroCustos() != null
						&& equipamentoSelecionado.getRetorno().intValue() == IPatrimonioService.RETORNO_INFO_PATRIMONIO_EQUIPAMENTO_PERTENCE_OUTRO_CC) {
					this.getEquipamentoAtividade().setCodPatrimonio("");
					this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_EQUIPAMENTO_OUTRO_CC");
					retorno = false;
				} else if (listaEquipamentoVO != null && !listaEquipamentoVO.isEmpty()) {
					this.getListaEquipamentoVO().add(equipamentoSelecionado);
				}
			} else {
				this.getEquipamentoAtividade().setCodPatrimonio("");
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_PARAMETROS_INVALIDOS");
				retorno = false;
			}
		} catch (Exception e) {
			retorno = false;
		}

		return retorno;
	}

	public void buscaEquipamentoModuloPatrimonio(boolean isEdicao) {
		try {
			equipamentoSelecionado = null;
			Integer codigoCC = this.getCodigoCentroCustoNormalizado();
			if (getEquipamentoAtividade().getCodigoCC() != null && codigoCC != null
					&& getEquipamentoAtividade().getCodigoCC().intValue() != codigoCC.intValue()) {
				codigoCC = getEquipamentoAtividade().getCodigoCC();
			}
			boolean parametrosCorretos = true;
			for (int i = 0; i < getEquipamentoAtividade().getCodPatrimonio().length(); i++) {
				if (!Character.isDigit(getEquipamentoAtividade().getCodPatrimonio().charAt(i))) {
					parametrosCorretos = false;
					break;
				}
			}
			if (parametrosCorretos) {
				equipamentoSelecionado = custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(getEquipamentoAtividade()
						.getCodPatrimonio(), codigoCC);
				if (equipamentoSelecionado == null) {
					this.getEquipamentoAtividade().setCodPatrimonio("");
					this.apresentarMsgNegocio(Severity.ERROR, MENSAGEM_SERVICO_PATRIMONIO_NAO_ENCONTRADO);
				} else if (equipamentoSelecionado.getRetorno().intValue() == 2) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_SEM_CONTROLE_DEPRECIACAO");
				} else if (equipamentoSelecionado.getRetorno().intValue() == 3) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SERVICO_PATRIMONIO_COM_VALOR_DEPRECIADO");
				} else if (equipamentoSelecionado.getRetorno().intValue() == 1) {
					this.getEquipamentoAtividade().setCodPatrimonio("");
					this.apresentarMsgNegocio(Severity.ERROR, MENSAGEM_SERVICO_PATRIMONIO_NAO_ENCONTRADO);
				} else if (equipamentoSelecionado.getRetorno().intValue() == 4) {
					if (!isEdicao) {
						this.getEquipamentoAtividade().setCodPatrimonio("");
						this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_EQUIPAMENTO_OUTRO_CC");
					}
				}
			} else {
				this.getEquipamentoAtividade().setCodPatrimonio("");
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_PARAMETROS_INVALIDOS");
			}
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
			if (integracaoPatrimonioOnline) {
				verificaServicoPatrimonioEstaOnline();
				getEquipamentoAtividade().setCodPatrimonio("");
			} else {
				getEquipamentoAtividade().setCodPatrimonio("");
			}
		}
	}

	private EquipamentoSistemaPatrimonioVO buscaEquipamentoModuloPatrimonio(SigAtividadeEquipamentos equipamento) {
		try {
			Integer centroCustoUniversal = 0;
			return custosSigFacade.pesquisarEquipamentoSistemaPatrimonioById(equipamento.getCodPatrimonio(), centroCustoUniversal);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SERVICO_PATRIMONIO_FALHA");
			if (integracaoPatrimonioOnline) {
				verificaServicoPatrimonioEstaOnline();
			}
			return null;
		}
	}

	public String getDescricaoBem(SigAtividadeEquipamentos equipamento) {
		if (listaEquipamentoVO != null && !listaEquipamentoVO.isEmpty()) {
			for (EquipamentoSistemaPatrimonioVO equipamentoVo : listaEquipamentoVO) {
				if (equipamento.getCodPatrimonio().equalsIgnoreCase(equipamentoVo.getCodigo())) {
					this.setDescricaoBem(equipamentoVo.getDescricao());
					this.setValorBem(equipamentoVo.getValor());
					this.setValorDepreciado(equipamentoVo.getValorDepreciado());
					return descricaoBem;
				}
			}
			this.setDescricaoBem(null);
			this.setValorBem(null);
			this.setValorDepreciado(null);
			return descricaoBem;
		} else {
			EquipamentoSistemaPatrimonioVO equipamentoVo = buscaEquipamentoModuloPatrimonio(equipamento);

			if (equipamentoVo == null) {
				this.setDescricaoBem(null);
				this.setValorBem(null);
				this.setValorDepreciado(null);
				return descricaoBem;
			} else {
				this.setDescricaoBem(equipamentoVo.getDescricao());
				this.setValorBem(equipamentoVo.getValor());
				this.setValorDepreciado(equipamentoVo.getValorDepreciado());
				return descricaoBem;
			}
		}
	}

	public boolean isIntegracaoPatrimonioOnline() {
		return integracaoPatrimonioOnline;
	}

	public void setIntegracaoPatrimonioOnline(boolean integracaoPatrimonioOnline) {
		this.integracaoPatrimonioOnline = integracaoPatrimonioOnline;
	}

	public BigDecimal getValorDepreciado() {
		return valorDepreciado;
	}

	public void setValorDepreciado(BigDecimal valorDepreciado) {
		this.valorDepreciado = valorDepreciado;
	}

	public BigDecimal getValorBem() {
		return valorBem;
	}

	public void setValorBem(BigDecimal valorBem) {
		this.valorBem = valorBem;
	}

	public String getDescricaoBem() {
		return descricaoBem;
	}

	public void setDescricaoBem(String descricaoBem) {
		this.descricaoBem = descricaoBem;
	}

	public EquipamentoSistemaPatrimonioVO getEquipamentoSelecionado() {
		return equipamentoSelecionado;
	}

	public void setEquipamentoSelecionado(EquipamentoSistemaPatrimonioVO equipamentoSelecionado) {
		this.equipamentoSelecionado = equipamentoSelecionado;
	}

	public boolean isAtividadeJaAdicionada() {
		return atividadeJaAdicionada;
	}

	public void setAtividadeJaAdicionada(boolean atividadeJaAdicionada) {
		this.atividadeJaAdicionada = atividadeJaAdicionada;
	}

	public List<EquipamentoSistemaPatrimonioVO> getListaEquipamentoVO() {
		return listaEquipamentoVO;
	}

	public void setListaEquipamentoVO(List<EquipamentoSistemaPatrimonioVO> listaEquipamentoVO) {
		this.listaEquipamentoVO = listaEquipamentoVO;
	}
}
