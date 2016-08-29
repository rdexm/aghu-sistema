package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO;
import br.gov.mec.aghu.compras.vo.ItensScoDireitoAutorizacaoTempVO.Operacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoPontoServidorId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class AutorizacoesTemporariasParaGeracaoScController extends ActionController {

	private static final long serialVersionUID = 3210794267909892541L;

	private static final String AUTORIZACOES_TEMPORARIAS_PARA_GERACAO_SC_LIST = "autorizacoesTemporariasParaGeracaoScList";

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	protected IAghuFacade aghuFacade;

	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	
	
	private ScoDireitoAutorizacaoTemp direitoAutorizacaoTemp = new ScoDireitoAutorizacaoTemp();

	private List<ItensScoDireitoAutorizacaoTempVO> listaDireitosAutorizacoesTemporarias;

	private ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoVo;

	private ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoVoAnterior = null;

	private boolean consulta = false;

	private String voltarPara = null;
	
	private Integer idLista = 0;
	
	private Boolean pendenteItens = false;

	private FccCentroCustos centroCusto = null;
	private ScoPontoServidor pontoParadaServidor = null;
	
	private ScoPontoServidorId id;
	private boolean iniciouTela = false;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

	 

		try {
	
			if(iniciouTela){
				return null;
			}
			iniciouTela = true;
			
			pontoParadaServidor = comprasCadastrosBasicosFacade.obterPontoParadaServidorCodigoMatriculaVinculo(id.getCodigoPontoParadaSolicitacao(), 
																											   id.getVinCodigo(), id.getMatricula());
			if(pontoParadaServidor == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
			this.setCentroCusto(servidor.getCentroCustoLotacao());
			
			this.setListaDireitosAutorizacoesTemporarias(this.carregarDadosBanco());
			this.pendenteItens = false;
			this.limpar();
			return null;
			
		} catch (ApplicationBusinessException esr) {
			apresentarExcecaoNegocio(esr);
			return cancelar();
		}
	
	}

	public List<ItensScoDireitoAutorizacaoTempVO> carregarDadosBanco() {
		List<ItensScoDireitoAutorizacaoTempVO> listaRetorno = new ArrayList<ItensScoDireitoAutorizacaoTempVO>();

		for (ScoDireitoAutorizacaoTemp itemScoDireitoAutorizacaoTemp : this.solicitacaoComprasFacade.listarScoDireitoAutorizacaoTemp(pontoParadaServidor,	null)) {

			ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoVoTemp = new ItensScoDireitoAutorizacaoTempVO();
			itemScoDireitoAutorizacaoVoTemp.setDireitoAutorizacaoTemp(itemScoDireitoAutorizacaoTemp);
			itemScoDireitoAutorizacaoVoTemp.setOperacao(Operacao.SAVE);
			listaRetorno.add(itemScoDireitoAutorizacaoVoTemp);

		}
		return listaRetorno;

	}

	public void adicionarAutorizacaoTemp() {

		int indexErroEdicao = -1;
		try {

			this.solicitacaoComprasFacade.validarConflitoPeriodoDatas(this.listaDireitosAutorizacoesTemporarias, this.direitoAutorizacaoTemp, this.idLista);
			if (this.getItemScoDireitoAutorizacaoVoAnterior() == null) {
				this.setItemScoDireitoAutorizacaoVo(new ItensScoDireitoAutorizacaoTempVO());
			}

			this.getItemScoDireitoAutorizacaoVo().setDireitoAutorizacaoTemp(
					this.getDireitoAutorizacaoTemp());

			if ((!this.getListaDireitosAutorizacoesTemporarias().contains(
					this.getItemScoDireitoAutorizacaoVo()))
					&& (this.getItemScoDireitoAutorizacaoVoAnterior() == null)) {

				this.solicitacaoComprasFacade.validaDataInicioFimScoDireitoAutorizacaoTemp(this
						.getItemScoDireitoAutorizacaoVo()
						.getDireitoAutorizacaoTemp().getDtInicio(), this
						.getItemScoDireitoAutorizacaoVo()
						.getDireitoAutorizacaoTemp().getDtFim());
				
				
				this.getItemScoDireitoAutorizacaoVo().setOperacao(Operacao.ADD);
				
				this.getListaDireitosAutorizacoesTemporarias().add(
						this.getItemScoDireitoAutorizacaoVo());

			} else {

				if (this.getItemScoDireitoAutorizacaoVoAnterior() != null) {

					indexErroEdicao = this
							.getListaDireitosAutorizacoesTemporarias().indexOf(
									this.getItemScoDireitoAutorizacaoVo());
					
					this.solicitacaoComprasFacade.validaDataInicioFimScoDireitoAutorizacaoTemp(this
							.getItemScoDireitoAutorizacaoVo()
							.getDireitoAutorizacaoTemp().getDtInicio(), this
							.getItemScoDireitoAutorizacaoVo()
							.getDireitoAutorizacaoTemp().getDtFim());

					if ( indexErroEdicao != this.getIdLista() &&
						 indexErroEdicao > -1	) {
						this.solicitacaoComprasFacade.mensagemErroDuplicadoScoDireitoAutorizacaoTemp();
					}

					this.getItemScoDireitoAutorizacaoVo().setOperacao(
							Operacao.EDIT);
					
					if (this.getIdLista() != null) {
						this.getListaDireitosAutorizacoesTemporarias().set(
								this.getIdLista(),
								this.getItemScoDireitoAutorizacaoVo());
					}
				} else {

					int index = this.getListaDireitosAutorizacoesTemporarias()
							.indexOf(this.getItemScoDireitoAutorizacaoVo());

					if (index > -1) {

						if (this.getListaDireitosAutorizacoesTemporarias()
								.get(index).getOperacao()
								.equals(Operacao.DELETE)) {
							this.getListaDireitosAutorizacoesTemporarias()
									.get(index).setOperacao(Operacao.ADD);
						} else {
							this.solicitacaoComprasFacade
									.mensagemErroDuplicadoScoDireitoAutorizacaoTemp();
						}

					}
				}

			}

			this.validaItensPendentes();
			this.limpar();

		} catch (final ApplicationBusinessException e) {

			if (this.getIdLista() != null){
			this.getListaDireitosAutorizacoesTemporarias().set(
					this.getIdLista(),
					this.getItemScoDireitoAutorizacaoVoAnterior());
			}
			apresentarExcecaoNegocio(e);
		}
	}

	public void gravar() {

		try {

			ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoTempVO = null;

			int index = 0;
			boolean isPersistiu = false;
			while (index < this.getListaDireitosAutorizacoesTemporarias().size()) {

				itemScoDireitoAutorizacaoTempVO = getListaDireitosAutorizacoesTemporarias().get(index);

				ScoDireitoAutorizacaoTemp scoDireitoAutTemp = itemScoDireitoAutorizacaoTempVO.getDireitoAutorizacaoTemp();

				final boolean novo = scoDireitoAutTemp.getNumero() == null;
				final Operacao operacao = itemScoDireitoAutorizacaoTempVO.getOperacao();

				if (!operacao.equals(Operacao.SAVE)) {
					if (novo) {
						if (operacao.equals(Operacao.ADD) || operacao.equals(Operacao.EDIT)) {
							this.solicitacaoComprasFacade.inserirScoDireitoAutorizacaoTemp(scoDireitoAutTemp);
							itemScoDireitoAutorizacaoTempVO.setOperacao(Operacao.SAVE);
							isPersistiu = true;

						}
						
					} else {
						if (operacao.equals(Operacao.ADD) || operacao.equals(Operacao.EDIT)) {
							this.solicitacaoComprasFacade.alterarScoDireitoAutorizacaoTemp(scoDireitoAutTemp);
							itemScoDireitoAutorizacaoTempVO.setOperacao(Operacao.SAVE);
							isPersistiu = true;
							
						} else {
							this.solicitacaoComprasFacade.excluirScoDireitoAutorizacaoTemp(scoDireitoAutTemp);
							isPersistiu = true;
						}
					}

					if (operacao.equals(Operacao.DELETE)) {
						this.getListaDireitosAutorizacoesTemporarias().remove(index);
					}
				}

				index = index + 1;
			}

			if (isPersistiu) {
				this.pendenteItens = false;
				this.apresentarMsgNegocio(Severity.INFO, "MESSAGEM_SUCESSO_DIREITOS_TEMPORARIOS_PERSISTENTE");
			}

		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void validaItensPendentes() {

		int index = 0;
		boolean isValidaPendente = false;
		while (index < this.getListaDireitosAutorizacoesTemporarias().size()
				&& isValidaPendente == false) {

			ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoTempVO = this
					.getListaDireitosAutorizacoesTemporarias().get(index);

			if (itemScoDireitoAutorizacaoTempVO.getOperacao().equals(Operacao.ADD) || 
				itemScoDireitoAutorizacaoTempVO.getOperacao().equals(Operacao.EDIT)) {
				isValidaPendente = true;
			}
			index = index + 1;
		}
		this.setPendenteItens(isValidaPendente);
	}

	public String voltar() {
		if (pendenteItens) {
			super.openDialog("pendenteWG");
			return null;
		} else {
			return this.cancelar();
		}
	}
	
	public void excluir(ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutTemp) {
		this.pendenteItens = true;
		this.setItemScoDireitoAutorizacaoVoAnterior(null);
		itemScoDireitoAutTemp.setOperacao(Operacao.DELETE);
	}

	public void editar(ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutTemp) {
		this.setIdLista(this.getListaDireitosAutorizacoesTemporarias().indexOf(itemScoDireitoAutTemp));
		
		try {
			this.setItemScoDireitoAutorizacaoVoAnterior(itemScoDireitoAutTemp.clone());
			this.setItemScoDireitoAutorizacaoVo(itemScoDireitoAutTemp.clone());
			if (!itemScoDireitoAutTemp.getOperacao().equals(Operacao.SAVE)) {
				this.setDireitoAutorizacaoTemp(itemScoDireitoAutTemp.getDireitoAutorizacaoTemp());
			} else {
				this.setDireitoAutorizacaoTemp(comprasCadastrosBasicosFacade.obterDireitoAutorizacaoTemporarioPorId(itemScoDireitoAutTemp.getDireitoAutorizacaoTemp()));
			}
		} catch (CloneNotSupportedException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_CLONAR_AUT_DIRETO_TEMPORARIO");
		}
	}

	public void limpar() {
		this.setItemScoDireitoAutorizacaoVoAnterior(null);
		this.setItemScoDireitoAutorizacaoVo(null);
		this.setDireitoAutorizacaoTemp(new ScoDireitoAutorizacaoTemp());
		this.getDireitoAutorizacaoTemp().setCentroCusto(this.getCentroCusto());
		this.getDireitoAutorizacaoTemp().setScoPontoServidor(getPontoParadaServidor());
		this.setIdLista(null);

	}

	public void preparaCancelamentoEdicao() {
		this.limpar();
		this.validaItensPendentes();
	}

	public String cancelar() {
		id = null;
		pontoParadaServidor = null;
		iniciouTela = false;
		pendenteItens = false;
		limpar();
		return AUTORIZACOES_TEMPORARIAS_PARA_GERACAO_SC_LIST;
	}

		
	// Metódo para Suggestion Box de Servidor
	public List<RapServidores> obterServidor(String objPesquisa) {
		try {
			return this.registroColaboradorFacade.pesquisarServidoresVinculados(objPesquisa);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public Long listarCentroCustosSolicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}


	public void setConsulta(final boolean consulta) {
		this.consulta = consulta;
	}

	public boolean isConsulta() {
		return consulta;
	}

	public void setVoltarPara(final String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public ScoDireitoAutorizacaoTemp getDireitoAutorizacaoTemp() {
		return direitoAutorizacaoTemp;
	}

	public void setDireitoAutorizacaoTemp(
			ScoDireitoAutorizacaoTemp direitoAutorizacaoTemp) {
		this.direitoAutorizacaoTemp = direitoAutorizacaoTemp;
	}


	public List<ItensScoDireitoAutorizacaoTempVO> getListaDireitosAutorizacoesTemporarias() {
		return listaDireitosAutorizacoesTemporarias;
	}

	public List<ItensScoDireitoAutorizacaoTempVO> getListaDireitosAutTemp() {

		List<ItensScoDireitoAutorizacaoTempVO> listaRetorno = new ArrayList<ItensScoDireitoAutorizacaoTempVO>();
		if(listaDireitosAutorizacoesTemporarias != null){
			for (ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoTempVO : listaDireitosAutorizacoesTemporarias) {
	
				if (itemScoDireitoAutorizacaoTempVO.getOperacao().equals(
						Operacao.ADD) || itemScoDireitoAutorizacaoTempVO.getOperacao().equals(
								Operacao.EDIT) || itemScoDireitoAutorizacaoTempVO.getOperacao().equals(
								Operacao.SAVE)) {
	
					listaRetorno.add(itemScoDireitoAutorizacaoTempVO);
				}
			}
		}
		return listaRetorno;
	}

	public void setListaDireitosAutTemp(
			List<ItensScoDireitoAutorizacaoTempVO> listaDireitosAutorizacoesTemporarias) {
		this.listaDireitosAutorizacoesTemporarias = listaDireitosAutorizacoesTemporarias;
	}

	public void setListaDireitosAutorizacoesTemporarias(
			List<ItensScoDireitoAutorizacaoTempVO> listaDireitosAutorizacoesTemporarias) {
		this.listaDireitosAutorizacoesTemporarias = listaDireitosAutorizacoesTemporarias;
	}

	public ItensScoDireitoAutorizacaoTempVO getItemScoDireitoAutorizacaoVo() {
		return itemScoDireitoAutorizacaoVo;
	}

	public void setItemScoDireitoAutorizacaoVo(
			ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoVo) {
		this.itemScoDireitoAutorizacaoVo = itemScoDireitoAutorizacaoVo;
	}

	public ItensScoDireitoAutorizacaoTempVO getItemScoDireitoAutorizacaoVoAnterior() {
		return itemScoDireitoAutorizacaoVoAnterior;
	}

	public void setItemScoDireitoAutorizacaoVoAnterior(
			ItensScoDireitoAutorizacaoTempVO itemScoDireitoAutorizacaoVoAnterior) {
		this.itemScoDireitoAutorizacaoVoAnterior = itemScoDireitoAutorizacaoVoAnterior;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public Integer getIdLista() {
		return idLista;
	}

	public void setIdLista(Integer idLista) {
		this.idLista = idLista;
	}

	public Boolean getPendenteItens() {
		return pendenteItens;
	}

	public void setPendenteItens(Boolean pendenteItens) {
		this.pendenteItens = pendenteItens;
	}

	public ScoPontoServidorId getId() {
		return id;
	}

	public void setId(ScoPontoServidorId id) {
		this.id = id;
	}

	public ScoPontoServidor getPontoParadaServidor() {
		return pontoParadaServidor;
	}

	public void setPontoParadaServidor(ScoPontoServidor pontoParadaServidor) {
		this.pontoParadaServidor = pontoParadaServidor;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}
}