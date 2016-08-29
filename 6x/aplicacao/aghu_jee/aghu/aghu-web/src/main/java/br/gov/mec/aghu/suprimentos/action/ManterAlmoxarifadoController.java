package br.gov.mec.aghu.suprimentos.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ComposicaoGruposVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterAlmoxarifadoController extends ActionController {

	private static final long serialVersionUID = -3212573330064288889L;

	private static final Log LOG = LogFactory.getLog(ManterAlmoxarifadoController.class);
	
	private static final String PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO = "estoque-pesquisaAlmoxarifado";

	private enum ManterAlmoxarifadoBusinessExceptionCode implements BusinessExceptionCode {
		ALMOXARIFADO_GRAVADO_COM_SUCESSO, ALMOXARIFADO_ALTERADO_COM_SUCESSO;
	}
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	private List<ComposicaoGruposVO> listaComposicao;
	private Short seq;
	private SceAlmoxarifado almoxarifado;
	private FccCentroCustos centroCustos;
	private DominioSimNao central;
	private DominioSimNao calculaMediaPonderada;
	private DominioSimNao permiteMatDireto;
	private DominioSimNao permiteMultGrupos;
	private DominioSimNao bloqEntTransf;
	private ScoGrupoMaterial grupoMaterial;
	private Boolean exibeModal;
	private Boolean exibeModalGrupos;
	private String nomeComposicao;
	private ComposicaoGruposVO composicaoSelecionada;



	private void incluirMessagemInfo(String retorno) {
		apresentarMsgNegocio(Severity.INFO, retorno, getAlmoxarifado().getSeqDescricao());
	}

	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		if (getSeq() != null) {
			setAlmoxarifado(estoqueFacade.obterAlmoxarifadoPorId(getSeq()));
			if (getAlmoxarifado() != null) {
				carregaDadosAlmoxarifadoParaAtributosDeControle();
				setExibeModal(Boolean.FALSE);
				setExibeModalGrupos(Boolean.FALSE);
				this.listaComposicao = this.estoqueFacade.pesquisarComposicaoAlmox(getAlmoxarifado());
			}
		} else {
			limpar();
		}
	
	}

	private void carregaDadosAlmoxarifadoParaAtributosDeControle() {
		setCentroCustos(getAlmoxarifado().getCentroCusto());
		setCentral(getValorSimNao(getAlmoxarifado().getIndCentral()));
		setCalculaMediaPonderada(getValorSimNao(getAlmoxarifado().getIndCalculaMediaPonderada()));
		setBloqEntTransf(getValorSimNao(getAlmoxarifado().getIndBloqEntrTransf()));
		setPermiteMatDireto(getValorSimNao(getAlmoxarifado().getIndMaterialDireto()));
		setPermiteMultGrupos(getValorSimNao(getAlmoxarifado().getIndMultiplosGrupos()));
	}
	
	private DominioSimNao getValorSimNao(Boolean valor) {
		return (valor ? DominioSimNao.S : DominioSimNao.N);
	}

	public void limpar() {
		setSeq(null);
		setAlmoxarifado(new SceAlmoxarifado());
		setExibeModal(Boolean.FALSE);
		setExibeModalGrupos(Boolean.FALSE);
		limparDadosCentroCusto();
		reiniciaValoresDefault();
	}

	public void cancelarGrupos() {
		this.composicaoSelecionada = null;
	}

	public void confirmarGrupos() {
		this.composicaoSelecionada = null;
	}
	
	private void reiniciaValoresDefault() {
		setCentral(DominioSimNao.N);
		getAlmoxarifado().setIndSituacao(DominioSituacao.A);
		setCalculaMediaPonderada(DominioSimNao.S);
		setBloqEntTransf(DominioSimNao.N);
		setPermiteMatDireto(DominioSimNao.N);
		setPermiteMultGrupos(DominioSimNao.N);
		this.listaComposicao = new ArrayList<ComposicaoGruposVO>();
	}
	
	public void incluirComposicao() {
		if (StringUtils.isBlank(this.nomeComposicao)) {
			this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_NOME_COMPOSICAO_NAO_INFORMADO");
		} else {
			Boolean existe = false;
			for (ComposicaoGruposVO comp : listaComposicao) {
				if (comp.equals(this.nomeComposicao)) {
					existe = true;
				}
			}
			
			if (existe) {
				this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_GRUPO_JA_CADADASTRADO");
			} else {
				this.listaComposicao.add(this.montarComposicao());
				this.nomeComposicao = null;
			}
		}
	}
	
	public void incluirGrupo() {
		if (this.grupoMaterial == null) {
			this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_GRUPO_NAO_INFORMADO");
		} else {
			this.grupoMaterial = this.comprasFacade.obterGrupoMaterialPorId(this.grupoMaterial.getCodigo());
			
			if (estoqueFacade.verificarGrupoOutraComposicao(getGrupoMaterial(), composicaoSelecionada.getSeq(), this.almoxarifado, this.listaComposicao)) {
				this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_GRUPO_JA_UTILIZADO");
			} else {
				Boolean existe = false;
				for (ScoGrupoMaterial grupo : composicaoSelecionada.getListaGrupos()) {
					grupo = this.comprasFacade.obterGrupoMaterialPorId(grupo.getCodigo());
					if (grupo.equals(this.grupoMaterial)) {
						existe = true;
					}
				}
				
				if (existe) {
					this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_GRUPO_JA_CADADASTRADO");
				} else {
					this.composicaoSelecionada.getListaGrupos().add(this.grupoMaterial);
					this.grupoMaterial = null;
				}
			}
		}
	}
	
	public void excluirComposicao() {
		ComposicaoGruposVO item = this.composicaoSelecionada;
		if (item != null) {
			int index = this.listaComposicao.indexOf(item);
			
			if (index >= 0) {
				this.listaComposicao.remove(index);
			}
		}
	}
	
	public void excluirGrupo(ScoGrupoMaterial item) {
		if (item != null) {
			int index = this.composicaoSelecionada.getListaGrupos().indexOf(item);
			
			if (index >= 0) {
				this.composicaoSelecionada.getListaGrupos().remove(index);
			}
		}
	}
	
	private ComposicaoGruposVO montarComposicao() {
		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			LOG.error("Nao foi possivel obter o servidorLogado");
		}
		Random rand = new Random();
		ComposicaoGruposVO comp = new ComposicaoGruposVO();
		comp.setIdRow(Short.valueOf(String.valueOf(rand.nextInt(100))) );
		comp.setComposicao(null);
		comp.setDescricaoComposicao(this.nomeComposicao);
		comp.setServidorInclusao(servidorLogado);
		comp.setListaGrupos(new ArrayList<ScoGrupoMaterial>());
		return comp;
	}
	
	public void carregarGruposComposicao(ComposicaoGruposVO item) {
		this.composicaoSelecionada = item;
		this.composicaoSelecionada.setListaGrupos(item.getListaGrupos());
	}
	
	public List<ScoGrupoMaterial> obterScoGrupoMaterial(String objPesquisa) {
		return this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(objPesquisa);
	}
	

	public void limparDadosCentroCusto() {
		setCentroCustos(null);
	}

	public DominioSimNao[] getDominioSimNao() {
		DominioSimNao[] dom = { DominioSimNao.S, DominioSimNao.N };
		return dom;
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String parametro) throws BaseException {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(parametro),pesquisarCentroCustoCount(parametro));
	}

	public Integer pesquisarCentroCustoCount(String parametro) throws BaseException {
		return centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricaoCount(parametro);
	}

	private void carregaDadosAtributosDeControleParaAlmoxarifado() {
		if (getCentral() != null) {
			getAlmoxarifado().setIndCentral(getCentral().isSim());
		} else {
			getAlmoxarifado().setIndCentral(false);
		}
		if (getCalculaMediaPonderada() != null) {
			getAlmoxarifado().setIndCalculaMediaPonderada(getCalculaMediaPonderada().isSim());
		} else {
			getAlmoxarifado().setIndCalculaMediaPonderada(false);
		}
		if (getBloqEntTransf() != null) {
			getAlmoxarifado().setIndBloqEntrTransf(getBloqEntTransf().isSim());
		} else {
			getAlmoxarifado().setIndBloqEntrTransf(false);
		}
		if (getAlmoxarifado().getIndSituacao() == null) {
			getAlmoxarifado().setIndSituacao(DominioSituacao.I);
		}
		if (getCentroCustos() != null) {
			getAlmoxarifado().setCentroCusto(getCentroCustos());
		}
		if (getPermiteMatDireto() != null) {
			getAlmoxarifado().setIndMaterialDireto(getPermiteMatDireto().isSim());
		}
		
		if (getPermiteMultGrupos() != null) {
			getAlmoxarifado().setIndMultiplosGrupos(getPermiteMultGrupos().isSim());
		}		
	}

	public String gravar() {
		String retorno = "";
		try {
			Boolean composicaoVazia = false;
			if (!this.permiteMultGrupos.isSim() && !this.listaComposicao.isEmpty()) {
				for (ComposicaoGruposVO comp : this.listaComposicao) {
					if (comp.getListaGrupos().isEmpty()) {
						composicaoVazia = true;
					}
				}
			}
			
			if (composicaoVazia) {
				this.apresentarMsgNegocio(Severity.FATAL,"MENSAGEM_COMPOSICAO_SEM_ITENS");					
			} else {
				carregaDadosAtributosDeControleParaAlmoxarifado();
				estoqueFacade.gravarAlmoxarifado(getAlmoxarifado());
				
				if (this.permiteMultGrupos.isSim() || this.listaComposicao.isEmpty()) {
					estoqueFacade.removerComposicaoAlmox(getAlmoxarifado());
					
				} else if (!this.listaComposicao.isEmpty()) {
					estoqueFacade.persistirComposicaoAlmox(getAlmoxarifado(), listaComposicao);
				}
				limpar();
				incluirMessagemInfo(ManterAlmoxarifadoBusinessExceptionCode.ALMOXARIFADO_GRAVADO_COM_SUCESSO.toString());
				retorno = PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO;
				resetarTela();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	public String atualizar() {
		String retorno = "";
		try {
			carregaDadosAtributosDeControleParaAlmoxarifado();
			estoqueFacade.gravarAlmoxarifado(getAlmoxarifado());
			incluirMessagemInfo(ManterAlmoxarifadoBusinessExceptionCode.ALMOXARIFADO_ALTERADO_COM_SUCESSO.toString());
			retorno = PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO;
			resetarTela();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public String verificaAlteracoes() {
		Boolean almoxarifadoAlterado = estoqueFacade.isAlmoxarifadoAlterado(getAlmoxarifado());
		if (almoxarifadoAlterado) {
			setExibeModal(Boolean.TRUE);
			return "";
		}
		return voltar();
	}

	public String voltar() {
		resetarTela();
		setExibeModalGrupos(Boolean.FALSE);
		return PAGE_ESTOQUE_PESQUISA_ALMOXARIFADO;
	}

	public void limparExibeModal() {
		setExibeModal(Boolean.FALSE);
		setExibeModalGrupos(Boolean.FALSE);
	}

	private void resetarTela() {
		this.limpar();
		this.almoxarifado = null;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	public DominioSimNao getCentral() {
		return central;
	}

	public void setCentral(DominioSimNao central) {
		this.central = central;
	}

	public DominioSimNao getCalculaMediaPonderada() {
		return calculaMediaPonderada;
	}

	public void setCalculaMediaPonderada(DominioSimNao calculaMediaPonderada) {
		this.calculaMediaPonderada = calculaMediaPonderada;
	}

	public DominioSimNao getBloqEntTransf() {
		return bloqEntTransf;
	}

	public void setBloqEntTransf(DominioSimNao bloqEntTransf) {
		this.bloqEntTransf = bloqEntTransf;
	}

	public Boolean getExibeModal() {
		return exibeModal;
	}

	public void setExibeModal(Boolean exibeModal) {
		this.exibeModal = exibeModal;
	}
	
	public DominioSimNao getPermiteMatDireto() {
		return permiteMatDireto;
	}

	public void setPermiteMatDireto(DominioSimNao permiteMatDireto) {
		this.permiteMatDireto = permiteMatDireto;
	}

	public DominioSimNao getPermiteMultGrupos() {
		return permiteMultGrupos;
	}

	public void setPermiteMultGrupos(DominioSimNao permiteMultGrupos) {
		this.permiteMultGrupos = permiteMultGrupos;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public Boolean getExibeModalGrupos() {
		return exibeModalGrupos;
	}

	public void setExibeModalGrupos(Boolean exibeModalGrupos) {
		this.exibeModalGrupos = exibeModalGrupos;
	}

	public String getNomeComposicao() {
		return nomeComposicao;
	}

	public void setNomeComposicao(String nomeComposicao) {
		this.nomeComposicao = nomeComposicao;
	}

	public List<ComposicaoGruposVO> getListaComposicao() {
		return listaComposicao;
	}

	public void setListaComposicao(List<ComposicaoGruposVO> listaComposicao) {
		this.listaComposicao = listaComposicao;
	}

	public ComposicaoGruposVO getComposicaoSelecionada() {
		return composicaoSelecionada;
	}

	public void setComposicaoSelecionada(ComposicaoGruposVO composicaoSelecionada) {
		this.composicaoSelecionada = composicaoSelecionada;
	}
}
