package br.gov.mec.aghu.estoque.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.estoque.vo.PacoteMateriaisVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemPacoteMateriais;
import br.gov.mec.aghu.model.SceItemPacoteMateriaisId;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterPacoteMateriaisController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -903490455325564456L;

	private static final String PAGE_ESTOQUE_MANTER_PACOTE_MATERIAIS = "estoque-manterPacoteMateriais";

	public enum EnumManterPacoteMateriaisMessageCode {
		SUCESSO_EDICAO_PACOTE_MATERIAIS, SUCESSO_GRAVACAO_PACOTE_MATERIAIS, 
		MENSAGEM_INFORMACAO_ADICIONAR_APOS_CLIQUE_GRAVAR, MENSAGEM_INFORMACAO_ALTERAR_APOS_CLIQUE_GRAVAR, 
		MENSAGEM_INFORMACAO_EXCLUIR_APOS_CLIQUE_GRAVAR;
		
	}

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ICascaFacade cascaFacade;

	// Numero do pacote para receber por parametro
	private Integer numeroPacoteMaterial;

	// pacote de materiais que será armazanado ou editado
	private ScePacoteMateriais pacoteMaterial;

	// atributos a serem armazenados pelo pacote de materiais
	private FccCentroCustos centroCustoProprietario;
	private FccCentroCustos centroCustoAplicacao;
	private SceAlmoxarifado almoxarifado;
	private DominioSituacao situacao;
	private boolean isAlmoxarife;

	// armazena os ítens a serem incluídos no pacote de materiais
	private List<SceItemPacoteMateriais> listaItens = new ArrayList<SceItemPacoteMateriais>();

	// armazenam os ítens(com apenas os campos necessários) a serem apresentados
	// no grid de ítens do pacote
	private List<ItemPacoteMateriaisVO> itensPacoteMateriaisVO = new ArrayList<ItemPacoteMateriaisVO>();

	// novo ítem a ser inserido na lista que será armazenada no banco
	private SceItemPacoteMateriais novoItem;

	// armazena a quantidade do ítem a ser inserida no pacote de materiais
	private Integer quantidadeItem;

	// armazena valores para serem apresentados na tela,
	// de código e razão social do fornecedor, e código e
	// descrição da unidade de medida do ítem
	private String codigoRazaoSocialFornecedor;
	private String codigoDescricaoUnidadeMedida;

	// armazena valores primitivos relativos a pacote de material, a serem
	// utilizados em pesquisa,
	// de forma que evite a busca de objetos desnecessários
	private PacoteMateriaisVO pacoteMateriaisVO = new PacoteMateriaisVO();

	// atributos utilizados para a remoção e atualização de ítens do grid e da
	// lista
	private Integer codigoMaterialItem;
	private Integer codigoCentroCustoAplicacaoItem;
	private Integer codigoCentroCustoProprietarioItem;
	private Integer numeroPacoteItem;
	private Integer codigoEstoqueItem;

	// armazena o estoque almoxarifado (com material) e atributos para
	// ser inseridos no grid e lista para o banco
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;

	// controla a exclusão dos ítens, caso o almoxarifado do pacote
	// seja trocado
	private SceAlmoxarifado almoxarifadoTemp;

	// atributos que controlam componentes da tela
	private boolean modoEdicao = Boolean.FALSE;
	private Boolean modoEdicaoItem = Boolean.FALSE;
	private Boolean modoVisualizacao = Boolean.FALSE;
	private Boolean quantidadeObrigatoria = Boolean.TRUE;
	private Boolean materialItemPacoteObrigatorio = Boolean.TRUE;
	private boolean exibePainelConfirmacao = Boolean.FALSE;
	// origem da entrada na tela de manter
	private String origemPesquisa;
	private String acaoCadastro;
	private final String manterPacoteMateriais = "estoque-manterPacoteMateriais";

	private Integer numPacote;
	private Integer codCctProprietario;
	private Integer codCctAplicacao;
	private Short seqAlmox;
	private DominioSituacao situacaoPacote;

	// armazena o estado original do pacote + itens
	private DominioSituacao situacaoPacoteOriginal;
	private String descricaoPacoteMaterialOriginal;
	private List<SceItemPacoteMateriais> listaItensOriginal = new ArrayList<SceItemPacoteMateriais>();
	private boolean exibeModalAlteracao = Boolean.FALSE;

	private Map<String, SceItemPacoteMateriais> mapItens;

	private List<SceItemPacoteMateriais> listaRemover = new ArrayList<SceItemPacoteMateriais>();

	private enum AcaoEnum {
		NOVO, EDITAR;
	}

	/**
	 * Executado quando a tela é acionada
	 * 
	 */
	public void iniciar() throws ApplicationBusinessException {
	 

	 

		setAlmoxarife(this.cascaFacade.usuarioTemPerfil(this.obterLoginUsuarioLogado(), "ADM29"));
		itensPacoteMateriaisVO = new ArrayList<ItemPacoteMateriaisVO>();

		if (acaoCadastro != null) {
			if (AcaoEnum.NOVO.toString().equalsIgnoreCase(acaoCadastro)) {
				setPacoteMaterial(null);
			} else if (AcaoEnum.EDITAR.toString().equals(acaoCadastro)) {
				ScePacoteMateriaisId idPacoteMateriais = new ScePacoteMateriaisId();
				idPacoteMateriais.setNumero(numeroPacoteMaterial);
				idPacoteMateriais.setCodigoCentroCustoProprietario(codCctProprietario);
				idPacoteMateriais.setCodigoCentroCustoAplicacao(codCctAplicacao);
				setPacoteMaterial(estoqueFacade.obterPacoteMateriais(idPacoteMateriais));
				setModoEdicao(Boolean.TRUE);
			}
			else {
				ScePacoteMateriaisId idPacoteMateriais = new ScePacoteMateriaisId();
				idPacoteMateriais.setNumero(numeroPacoteMaterial);
				idPacoteMateriais.setCodigoCentroCustoProprietario(codCctProprietario);
				idPacoteMateriais.setCodigoCentroCustoAplicacao(codCctAplicacao);
				setPacoteMaterial(estoqueFacade.obterPacoteMateriais(idPacoteMateriais));
				setModoVisualizacao(Boolean.TRUE);
			}
		}

		// Mapea os itens da lista
		mapItens = new HashMap<String, SceItemPacoteMateriais>();

		// Novo pacote de materiais
		if (getPacoteMaterial() == null) {
			setPacoteMaterial(new ScePacoteMateriais());
			getPacoteMaterial().setId(new ScePacoteMateriaisId());
			getPacoteMaterial().setIndSituacao(DominioSituacao.A);
			setModoEdicao(Boolean.FALSE);
			setModoEdicaoItem(Boolean.FALSE);
			setModoVisualizacao(Boolean.FALSE);
			setListaItens(new ArrayList<SceItemPacoteMateriais>());

			// edição de algum pacote de materiais
		} else {
			setListaItens(estoqueFacade.pesquisarItensPacoteMateriais(getPacoteMaterial().getId().getCodigoCentroCustoProprietario(), pacoteMaterial.getId().getCodigoCentroCustoAplicacao(),
					pacoteMaterial.getId().getNumero()));
			for (SceItemPacoteMateriais itemPacote : getListaItens()) {

				StringBuilder key = new StringBuilder();

				key.append(itemPacote.getId().getSeqEstoque().toString())
				.append(itemPacote.getId().getCodigoCentroCustoAplicacaoPacoteMateriais().toString())
				.append(itemPacote.getId().getCodigoCentroCustoProprietarioPacoteMateriais().toString())
				.append(itemPacote.getId().getNumeroPacoteMateriais().toString());
				mapItens.put(key.toString(), itemPacote);
			}
			setPacoteMateriaisVO(estoqueFacade.obterPacoteMaterialVO(getPacoteMaterial().getId()));
			setCentroCustoProprietario(centroCustoFacade.obterFccCentroCustos(getPacoteMateriaisVO().getCodigoCentroCustoProprietario()));
			setCentroCustoAplicacao(centroCustoFacade.obterFccCentroCustos(getPacoteMateriaisVO().getCodigoCentroCustoAplicacao()));
			setAlmoxarifado(estoqueFacade.obterAlmoxarifadoPorId(getPacoteMateriaisVO().getSeqAlmoxarifado()));
			setAlmoxarifadoTemp(getAlmoxarifado());
			setItensPacoteMateriaisVO(estoqueFacade.pesquisarItensPacoteMateriaisVO(getPacoteMateriaisVO().getCodigoCentroCustoProprietario(),
					getPacoteMateriaisVO().getCodigoCentroCustoAplicacao(), getPacoteMateriaisVO().getNumero()));

			// armazena o estado original do pacote + itens para verificar
			// alterações
			setSituacaoPacoteOriginal(getPacoteMaterial().getIndSituacao());
			setDescricaoPacoteMaterialOriginal(getPacoteMaterial().getDescricao());
			setListaItensOriginal(getListaItens());
		}
	
	}
	

	public String novo() {
		setPacoteMaterial(null);
		setAcaoCadastro(AcaoEnum.NOVO.toString());
		return PAGE_ESTOQUE_MANTER_PACOTE_MATERIAIS;
	}

	/**
	 * Verifica se os dados do pacote foram alterados
	 * 
	 * @return
	 */
	private boolean verificarAlteracaoDados() {

		boolean retorno = Boolean.FALSE;

		if (getPacoteMaterial() != null && getSituacaoPacoteOriginal() != null) {
			if (!getSituacaoPacoteOriginal().equals(getPacoteMaterial().getIndSituacao())) {
				retorno = Boolean.TRUE;
			}
		}

		if (getDescricaoPacoteMaterialOriginal() != null && getPacoteMaterial() != null) {
			if (!getDescricaoPacoteMaterialOriginal().equals(getPacoteMaterial().getDescricao())) {
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}

	public String gravar() {
		setQuantidadeObrigatoria(Boolean.FALSE);
		setMaterialItemPacoteObrigatorio(Boolean.FALSE);
		String bundleMsgSucesso = "";
		if (getCentroCustoAplicacao() != null) {
			getPacoteMaterial().getId().setCodigoCentroCustoAplicacao(getCentroCustoAplicacao().getCodigo());
		}
		if (getCentroCustoProprietario() != null) {
			getPacoteMaterial().getId().setCodigoCentroCustoProprietario(getCentroCustoProprietario().getCodigo());
		}
		if (getAlmoxarifado() != null) {
			getPacoteMaterial().setAlmoxarifado(getAlmoxarifado());
		}

		if (getPacoteMaterial().getId().getNumero() != null) {
			bundleMsgSucesso = EnumManterPacoteMateriaisMessageCode.SUCESSO_EDICAO_PACOTE_MATERIAIS.toString();
			getPacoteMaterial().setItens(new HashSet<SceItemPacoteMateriais>());
		} else {
			bundleMsgSucesso = EnumManterPacoteMateriaisMessageCode.SUCESSO_GRAVACAO_PACOTE_MATERIAIS.toString();
		}
		getPacoteMaterial().setItens(new HashSet<SceItemPacoteMateriais>(getListaItens()));

		try {
			for (SceItemPacoteMateriais item : listaRemover) {

				estoqueFacade.removerItemPacoteMaterial(item);
			}
            //Limpa a lista apos remover os itens
            listaRemover = new ArrayList<SceItemPacoteMateriais>();

			estoqueFacade.persistirPacoteMaterais(getPacoteMaterial());
			apresentarMsgNegocio(Severity.INFO, bundleMsgSucesso);
			setDescricaoPacoteMaterialOriginal(getPacoteMaterial().getDescricao());
			setSituacaoPacoteOriginal(getPacoteMaterial().getIndSituacao());
			return this.voltar();
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public List<SceEstoqueAlmoxarifado> pesquisarMateriaisEstoqueAlmoxarifadoPorCodigoDescricaoAlmoxarifado(String parametro)  {
		List<SceEstoqueAlmoxarifado> estoquesAlmoxarifados = null;
		Short seqAlmoxarifado = null;
		if (getAlmoxarifado() != null) {
			seqAlmoxarifado = getAlmoxarifado().getSeq();
		}

		List<Integer> listaGrupos = new ArrayList<Integer>();
		this.montarListaGrupos(listaGrupos);
		
		Boolean somenteDiretos = null;
		Boolean somenteEstocaveis = null;

		if (!isAlmoxarife) {			
			if (this.getAlmoxarifado()!=null && !this.getAlmoxarifado().getIndMaterialDireto()) {
				if (this.itensPacoteMateriaisVO == null  || this.itensPacoteMateriaisVO.isEmpty()) {
					somenteEstocaveis = null;
					somenteDiretos = null;
				} else if (this.comprasFacade.obterMaterialPorId(this.itensPacoteMateriaisVO.get(0).getCodigoMaterial()).getEstocavel()) {
					somenteEstocaveis = true;
				} else {
					somenteDiretos = true;
				}			
			}
		}
		try{
			estoquesAlmoxarifados = estoqueFacade.pesquisarMateriaisEstoquePorCodigoDescricaoAlmoxarifado(parametro, seqAlmoxarifado, listaGrupos, somenteEstocaveis, somenteDiretos);	
		}
		catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		
		return estoquesAlmoxarifados;
	}

	private void montarListaGrupos(List<Integer> listaGrupos) {
		// se o almoxarifado permite multiplos grupos passa reto
		// se o almoxarifado nao permite multiplos grupos
		if (this.getAlmoxarifado() != null && !this.getAlmoxarifado().getIndMultiplosGrupos()) {
			List<SceAlmoxarifadoGrupos> listaAlmoxGrupos = estoqueFacade.pesquisarGruposPorAlmoxarifado(this.getAlmoxarifado());
			
			// se nao tem lista de excecoes, cai na regra anterior
			if (listaAlmoxGrupos == null || listaAlmoxGrupos.isEmpty()) {
				if(this.itensPacoteMateriaisVO != null  && !this.itensPacoteMateriaisVO.isEmpty()){
					listaGrupos.add(this.itensPacoteMateriaisVO.get(0).getCodigoGrupoMaterial());
				}
			} else {
				// se tem lista de excecoes, tem que mandar a lista para a query				
				// se a RM ja possui um item...
				if(this.itensPacoteMateriaisVO != null  && !this.itensPacoteMateriaisVO.isEmpty()){
					ScoGrupoMaterial grupoReq = this.comprasFacade.obterGrupoMaterialPorId(this.itensPacoteMateriaisVO.get(0).getCodigoGrupoMaterial());

					//	verifica se esta na lista de grupos que pode misturar
					Integer alcSeq = null;
					for (SceAlmoxarifadoGrupos grp : listaAlmoxGrupos) {
						if (grp.getGrupoMaterial().equals(grupoReq)) {
							alcSeq = grp.getComposicao().getSeq();
						}
					}
					if (alcSeq != null) {
						// se estiver na lista, pega o alcSeq e insere na listaGrupos somente
						// os grupos do mesmo alcSeq
						for (SceAlmoxarifadoGrupos grp : listaAlmoxGrupos) {
							if (grp.getComposicao().getSeq().equals(alcSeq)) {
								listaGrupos.add(grp.getGrupoMaterial().getCodigo());
							}
						}	
					} else {
						// se nao estiver na lista, insere na listaGrupos somente o grupo do item ja existente
						listaGrupos.add(grupoReq.getCodigo());
					}
				}
			}
		}
	}

	/**
	 * Adiciona itens a lista provisória e a lista definitiva (a ser armazenada
	 * em banco).
	 */
	public void adicionarItem() {

		try {

			if (getEstoqueAlmoxarifado() == null) {
				apresentarMsgNegocio(Severity.ERROR, " * O campo código material é obrigatório");
				return;
			}

			if (getQuantidadeItem() == null) {
				setQuantidadeItem(0);
			}

			SceItemPacoteMateriaisId id = new SceItemPacoteMateriaisId();

			id.setCodigoCentroCustoAplicacaoPacoteMateriais(getCentroCustoAplicacao().getCodigo());
			id.setCodigoCentroCustoProprietarioPacoteMateriais(getCentroCustoProprietario().getCodigo());
			id.setNumeroPacoteMateriais(getPacoteMaterial().getId().getNumero());
			id.setSeqEstoque(getEstoqueAlmoxarifado() != null ? getEstoqueAlmoxarifado().getSeq() : null);
			setNovoItem(new SceItemPacoteMateriais());
			getNovoItem().setId(id);
			getNovoItem().setQuantidade(getQuantidadeItem());

			ItemPacoteMateriaisVO itemPacoteMateriaisVO = new ItemPacoteMateriaisVO();
			if (getEstoqueAlmoxarifado() != null) {
				itemPacoteMateriaisVO = estoqueFacade.obterItemPacoteMateriaisVO(id.getSeqEstoque(), getEstoqueAlmoxarifado().getCodigoMaterial());
				getNovoItem().setCodigoMaterialEstoque(itemPacoteMateriaisVO.getCodigoMaterial());
			}
			// armazena valores a serem utilizados pela atualização ou remoção
			// de ítens do grid e lista definitiva
			itemPacoteMateriaisVO.setNumeroPacote(id.getNumeroPacoteMateriais());
			itemPacoteMateriaisVO.setCodigoCentroCustoProprietarioPacote(id.getCodigoCentroCustoProprietarioPacoteMateriais());
			itemPacoteMateriaisVO.setCodigoCentroCustoAplicacaoPacote(id.getCodigoCentroCustoAplicacaoPacoteMateriais());
			itemPacoteMateriaisVO.setSeqEstoque(id.getSeqEstoque());
			itemPacoteMateriaisVO.setQuantidade(getQuantidadeItem());
			this.estoqueFacade.popularConsumosItemPacoteVO(itemPacoteMateriaisVO);
			try {
				estoqueFacade.verificarInclusaoItensPacoteMateriais(getItensPacoteMateriaisVO(), itemPacoteMateriaisVO.getCodigoCentroCustoProprietarioPacote(),
						itemPacoteMateriaisVO.getCodigoCentroCustoAplicacaoPacote(), itemPacoteMateriaisVO.getNumeroPacote(), itemPacoteMateriaisVO.getSeqEstoque(),
						itemPacoteMateriaisVO.getQuantidade(), itemPacoteMateriaisVO.getCodigoMaterial(), getAlmoxarifado().getSeq(), estoqueAlmoxarifado.getSeqAlmoxarifado(),
						getAlmoxarifado().getIndSituacao());
				getListaItens().add(getNovoItem());

				StringBuilder key = new StringBuilder();

				key.append(getNovoItem().getId().getSeqEstoque().toString())
				.append(getNovoItem().getId().getCodigoCentroCustoAplicacaoPacoteMateriais().toString())
				.append(getNovoItem().getId().getCodigoCentroCustoProprietarioPacoteMateriais().toString());
				mapItens.put(key.toString(), getNovoItem());

				setNovoItem(null);
				getItensPacoteMateriaisVO().add(itemPacoteMateriaisVO);
				setEstoqueAlmoxarifado(null);
				setQuantidadeItem(null);
				setCodigoRazaoSocialFornecedor(null);
				setCodigoDescricaoUnidadeMedida(null);

				String bundleMsgSucesso = "";
				bundleMsgSucesso = EnumManterPacoteMateriaisMessageCode.MENSAGEM_INFORMACAO_ADICIONAR_APOS_CLIQUE_GRAVAR.toString();
				apresentarMsgNegocio(Severity.INFO, bundleMsgSucesso);
			} catch (BaseListException e) {
				apresentarExcecaoNegocio(e);
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Remove item da lista provisória e da lista definitiva
	 */
	public void removerItem(ItemPacoteMateriaisVO itemPacoteMateriaisVO) {
		try {

			if (itemPacoteMateriaisVO != null) {
				setCodigoMaterialItem(itemPacoteMateriaisVO.getCodigoMaterial());
				setCodigoCentroCustoAplicacaoItem(itemPacoteMateriaisVO.getCodigoCentroCustoAplicacaoPacote());
				setCodigoCentroCustoProprietarioItem(itemPacoteMateriaisVO.getCodigoCentroCustoProprietarioPacote());
				setCodigoEstoqueItem(itemPacoteMateriaisVO.getSeqEstoque());
				setNumeroPacoteItem(itemPacoteMateriaisVO.getNumeroPacote());

				StringBuilder key = new StringBuilder();

				if (getCodigoEstoqueItem() != null) {
					key.append(getCodigoEstoqueItem().toString());
				}
				if (getCodigoCentroCustoAplicacaoItem() != null) {
					key.append(getCodigoCentroCustoAplicacaoItem().toString());
				}
				if (getCodigoCentroCustoProprietarioItem() != null) {
					key.append(getCodigoCentroCustoProprietarioItem().toString());
				}
				if (getNumeroPacoteItem() != null) {
					key.append(getNumeroPacoteItem().toString());
				}

				if (key != null && !key.toString().isEmpty()) {
					SceItemPacoteMateriais aRemover = mapItens.get(key.toString());
					getListaItens().remove(aRemover);
					listaRemover.add(aRemover);
					mapItens.remove(aRemover);
				}

				estoqueFacade.verificarUltimoItemPacoteMateriais(getListaItens().size());
				getItensPacoteMateriaisVO().remove(itemPacoteMateriaisVO);
			}
			String bundleMsgSucesso = "";
			bundleMsgSucesso = EnumManterPacoteMateriaisMessageCode.MENSAGEM_INFORMACAO_EXCLUIR_APOS_CLIQUE_GRAVAR.toString();
			apresentarMsgNegocio(Severity.INFO, bundleMsgSucesso);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Aplica os dados do ítem aos campos de edição
	 * 
	 * @param itemPacoteMateriaisVO
	 * @throws ApplicationBusinessException
	 */
	public void alterarItem(ItemPacoteMateriaisVO itemPacoteMateriaisVO) throws ApplicationBusinessException {
		setModoEdicaoItem(Boolean.TRUE);
		setCodigoMaterialItem(itemPacoteMateriaisVO.getCodigoMaterial());
		setCodigoCentroCustoAplicacaoItem(itemPacoteMateriaisVO.getCodigoCentroCustoAplicacaoPacote());
		setCodigoCentroCustoProprietarioItem(itemPacoteMateriaisVO.getCodigoCentroCustoProprietarioPacote());
		setCodigoEstoqueItem(itemPacoteMateriaisVO.getSeqEstoque());
		setNumeroPacoteItem(itemPacoteMateriaisVO.getNumeroPacote());
		setEstoqueAlmoxarifado(pesquisarMateriaisEstoqueAlmoxarifadoPorCodigoDescricaoAlmoxarifado(itemPacoteMateriaisVO.getCodigoMaterial().toString()).get(0));
		setQuantidadeItem(itemPacoteMateriaisVO.getQuantidade());
		if (getQuantidadeItem() == null) {
			setQuantidadeItem(0);
		}
	}

	public void atualizarItem() throws ApplicationBusinessException {

		if (getEstoqueAlmoxarifado() == null) {
			apresentarMsgNegocio(Severity.ERROR, "O campo almoxarifado é obrigatório");
			return;
		}

		if (getQuantidadeItem() == null) {
			setQuantidadeItem(0);
		}

		setModoEdicaoItem(Boolean.FALSE);

		for (ItemPacoteMateriaisVO itemPacoteVO : getItensPacoteMateriaisVO()) {
			if (itemPacoteVO.getCodigoMaterial().equals(getCodigoMaterialItem())) {
				Integer codigoCentroCustoAplicacao = Integer.valueOf(itemPacoteVO.getCodigoCentroCustoAplicacaoPacote());
				getItensPacoteMateriaisVO().remove(itemPacoteVO);
				itemPacoteVO = estoqueFacade.obterItemPacoteMateriaisVO(getEstoqueAlmoxarifado().getSeq(), getEstoqueAlmoxarifado().getCodigoMaterial());
				itemPacoteVO.setQuantidade(getQuantidadeItem());
				itemPacoteVO.setCodigoCentroCustoAplicacaoPacote(codigoCentroCustoAplicacao);
				this.estoqueFacade.popularConsumosItemPacoteVO(itemPacoteVO);
				getItensPacoteMateriaisVO().add(itemPacoteVO);
				break;
			}
		}
		for (SceItemPacoteMateriais itemPacote : getListaItens()) {

			if (itemPacote.getCodigoMaterialEstoque() == null) {

				if (itemPacote.getId().getSeqEstoque().equals(getCodigoEstoqueItem()) && itemPacote.getId().getCodigoCentroCustoAplicacaoPacoteMateriais().equals(getCodigoCentroCustoAplicacaoItem())
						&& itemPacote.getId().getCodigoCentroCustoProprietarioPacoteMateriais().equals(getCodigoCentroCustoProprietarioItem())
						&& itemPacote.getId().getNumeroPacoteMateriais().equals(getNumeroPacoteItem())) {
					getListaItens().remove(itemPacote);
					itemPacote.getId().setSeqEstoque(getEstoqueAlmoxarifado().getSeq());
					itemPacote.setQuantidade(getQuantidadeItem());
					getListaItens().add(itemPacote);
					break;
				} else {

					if (itemPacote.getCodigoMaterialEstoque() != null && itemPacote.getCodigoMaterialEstoque().equals(getCodigoMaterialItem())) {
						getListaItens().remove(itemPacote);
						itemPacote.getId().setSeqEstoque(getEstoqueAlmoxarifado().getSeq());
						itemPacote.setQuantidade(getQuantidadeItem());
						getListaItens().add(itemPacote);
						break;
					}
				}
			}
		}
		String bundleMsgSucesso = "";
		bundleMsgSucesso = EnumManterPacoteMateriaisMessageCode.MENSAGEM_INFORMACAO_ALTERAR_APOS_CLIQUE_GRAVAR.toString();
		apresentarMsgNegocio(Severity.INFO, bundleMsgSucesso);
		setEstoqueAlmoxarifado(null);
		limparFornecedorUnidadeMedida();
		setQuantidadeItem(null);
	}

	public List<FccCentroCustos> pesquisarCentrosCustoAtivos(String parametro) {
		return  centroCustoFacade.pesquisarCentroCustosAtivos(parametro);
	}

	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricao(String parametro) {
		return estoqueFacade.pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(parametro);
	}

	public String abreviar(String str, int maxWidth) {
		String abreviado = null;
		if (str != null) {
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}

	public String editar(ScePacoteMateriais pacote) {
		setPacoteMaterial(pacote);
		setModoEdicao(Boolean.TRUE);
		return PAGE_ESTOQUE_MANTER_PACOTE_MATERIAIS;
	}

	public String visualizar(ScePacoteMateriais pacote) {
		setPacoteMaterial(pacote);
		setModoVisualizacao(Boolean.TRUE);		
		return "/estoque/manterPacoteMateriais.xhtml";
	}

	public void limparItens() {
		setListaItens(new ArrayList<SceItemPacoteMateriais>());
		novoItem = null;
		setExibePainelConfirmacao(Boolean.FALSE);
	}

	public void defineAlmoxarifadoTemp() {
		setAlmoxarifadoTemp(getAlmoxarifado());
	}

	public void cancelarTrocaAlmoxarifado() {
		setAlmoxarifado(getAlmoxarifadoTemp());
		setExibePainelConfirmacao(Boolean.FALSE);
	}

	public void confirmaExclusaoItens() {
		setExibePainelConfirmacao(Boolean.TRUE);
	}

	public boolean isDesabilitaNovoItem() {
		if (almoxarifado == null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	public void limparFornecedorUnidadeMedida() {
		setCodigoRazaoSocialFornecedor(null);
		setCodigoDescricaoUnidadeMedida(null);
	}

	public void setCodigoRazaoSocialFornecedor(String codigoRazaoSocialFornecedor) {
		this.codigoRazaoSocialFornecedor = codigoRazaoSocialFornecedor;
	}

	public String getCodigoRazaoSocialFornecedor() {

		if (getEstoqueAlmoxarifado() != null && getEstoqueAlmoxarifado().getNumeroFornecedor() != null && getEstoqueAlmoxarifado().getRazaoSocialFornecedor() != null) {
			codigoRazaoSocialFornecedor = getEstoqueAlmoxarifado().getNumeroFornecedor() + " - " + getEstoqueAlmoxarifado().getRazaoSocialFornecedor();
		}

		return codigoRazaoSocialFornecedor;
	}

	public void setCodigoDescricaoUnidadeMedida(String codigoDescricaoUnidadeMedida) {
		this.codigoDescricaoUnidadeMedida = codigoDescricaoUnidadeMedida;
	}

	public String getCodigoDescricaoUnidadeMedida() {

		if (getEstoqueAlmoxarifado() != null && getEstoqueAlmoxarifado().getCodigoUnidadeMedida() != null && getEstoqueAlmoxarifado().getDescricaoUnidadeMedida() != null) {
			codigoDescricaoUnidadeMedida = getEstoqueAlmoxarifado().getCodigoUnidadeMedida() + " - " + getEstoqueAlmoxarifado().getDescricaoUnidadeMedida();
		}

		return codigoDescricaoUnidadeMedida;
	}

	public ScePacoteMateriais getPacoteMaterial() {
		return pacoteMaterial;
	}

	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
	}

	public Integer getQuantidadeItem() {
		return quantidadeItem;
	}

	public void setQuantidadeItem(Integer quantidadeItem) {
		this.quantidadeItem = quantidadeItem;
	}

	public FccCentroCustos getCentroCustoProprietario() {
		return centroCustoProprietario;
	}

	public void setCentroCustoProprietario(FccCentroCustos centroCustoProprietario) {
		this.centroCustoProprietario = centroCustoProprietario;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public List<SceItemPacoteMateriais> getListaItens() {
		return listaItens;
	}

	public void setListaItens(List<SceItemPacoteMateriais> listaItens) {
		this.listaItens = listaItens;
	}

	public SceItemPacoteMateriais getNovoItem() {
		return novoItem;
	}

	public void setNovoItem(SceItemPacoteMateriais novoItem) {
		this.novoItem = novoItem;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public boolean isExibePainelConfirmacao() {
		return exibePainelConfirmacao;
	}

	public void setExibePainelConfirmacao(boolean exibePainelConfirmacao) {
		this.exibePainelConfirmacao = exibePainelConfirmacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}

	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return estoqueAlmoxarifado;
	}

	public void setItensPacoteMateriaisVO(List<ItemPacoteMateriaisVO> itensPacoteMateriaisVO) {
		this.itensPacoteMateriaisVO = itensPacoteMateriaisVO;
	}

	public List<ItemPacoteMateriaisVO> getItensPacoteMateriaisVO() {
		return itensPacoteMateriaisVO;
	}

	public Integer getCodigoMaterialItem() {
		return codigoMaterialItem;
	}

	public void setCodigoMaterialItem(Integer codigoMaterialItem) {
		this.codigoMaterialItem = codigoMaterialItem;
	}

	public Boolean getModoEdicaoItem() {
		return modoEdicaoItem;
	}

	public void setModoEdicaoItem(Boolean modoEdicaoItem) {
		this.modoEdicaoItem = modoEdicaoItem;
	}

	public Integer getCodigoCentroCustoAplicacaoItem() {
		return codigoCentroCustoAplicacaoItem;
	}

	public void setCodigoCentroCustoAplicacaoItem(Integer codigoCentroCustoAplicacaoItem) {
		this.codigoCentroCustoAplicacaoItem = codigoCentroCustoAplicacaoItem;
	}

	public Integer getCodigoCentroCustoProprietarioItem() {
		return codigoCentroCustoProprietarioItem;
	}

	public void setCodigoCentroCustoProprietarioItem(Integer codigoCentroCustoProprietarioItem) {
		this.codigoCentroCustoProprietarioItem = codigoCentroCustoProprietarioItem;
	}

	public Integer getNumeroPacoteItem() {
		return numeroPacoteItem;
	}

	public void setNumeroPacoteItem(Integer numeroPacoteItem) {
		this.numeroPacoteItem = numeroPacoteItem;
	}

	public Integer getCodigoEstoqueItem() {
		return codigoEstoqueItem;
	}

	public void setCodigoEstoqueItem(Integer codigoEstoqueItem) {
		this.codigoEstoqueItem = codigoEstoqueItem;
	}

	public Boolean getQuantidadeObrigatoria() {
		return quantidadeObrigatoria;
	}

	public void setQuantidadeObrigatoria(Boolean quantidadeObrigatoria) {
		this.quantidadeObrigatoria = quantidadeObrigatoria;
	}

	public Boolean getMaterialItemPacoteObrigatorio() {
		return materialItemPacoteObrigatorio;
	}

	public void setMaterialItemPacoteObrigatorio(Boolean materialItemPacoteObrigatorio) {
		this.materialItemPacoteObrigatorio = materialItemPacoteObrigatorio;
	}

	public PacoteMateriaisVO getPacoteMateriaisVO() {
		return pacoteMateriaisVO;
	}

	public void setPacoteMateriaisVO(PacoteMateriaisVO pacoteMateriaisVO) {
		this.pacoteMateriaisVO = pacoteMateriaisVO;
	}

	public SceAlmoxarifado getAlmoxarifadoTemp() {
		return almoxarifadoTemp;
	}

	public void setAlmoxarifadoTemp(SceAlmoxarifado almoxarifadoTemp) {
		this.almoxarifadoTemp = almoxarifadoTemp;
	}

	/**
	 * Valida se houve alguma alteração e retorna
	 * 
	 * @return
	 */
	public String getOrigemPesquisa() {
		if (verificarAlteracaoDados()) {
			setExibeModalAlteracao(Boolean.TRUE);
			return null;
		}
		return voltar();
	}

	public String voltar() {
		setPacoteMaterial(null);
		setAlmoxarifado(null);
		setEstoqueAlmoxarifado(null);
		setQuantidadeItem(null);
		setCodigoRazaoSocialFornecedor(null);
		setCodigoDescricaoUnidadeMedida(null);
		setCentroCustoAplicacao(null);
		setCentroCustoProprietario(null);
		setModoEdicao(Boolean.FALSE);
		setModoVisualizacao(Boolean.FALSE);
		setModoEdicaoItem(Boolean.FALSE);
		setExibeModalAlteracao(Boolean.FALSE);
		setExibePainelConfirmacao(Boolean.FALSE);
		setAcaoCadastro(null);
		setSituacaoPacoteOriginal(null);
		setDescricaoPacoteMaterialOriginal(null);
		setListaItensOriginal(null);
		return this.origemPesquisa;
	}

	public void cancelarEdicao() {
		setEstoqueAlmoxarifado(null);
		setQuantidadeItem(null);
		setCodigoRazaoSocialFornecedor(null);
		setCodigoDescricaoUnidadeMedida(null);
		setModoEdicaoItem(Boolean.FALSE);
	}

	public String getTotalItensPacoteMaterial() {
		return "Total de Itens do Pacote: " + this.itensPacoteMateriaisVO.size();
	}

	public void setOrigemPesquisa(String origemPesquisa) {
		this.origemPesquisa = origemPesquisa;
	}

	public String getManterPacoteMateriais() {
		return manterPacoteMateriais;
	}

	public Integer getCodCctProprietario() {
		return codCctProprietario;
	}

	public void setCodCctProprietario(Integer codCctProprietario) {
		this.codCctProprietario = codCctProprietario;
	}

	public Integer getCodCctAplicacao() {
		return codCctAplicacao;
	}

	public void setCodCctAplicacao(Integer codCctAplicacao) {
		this.codCctAplicacao = codCctAplicacao;
	}

	public Short getSeqAlmox() {
		return seqAlmox;
	}

	public void setSeqAlmox(Short seqAlmox) {
		this.seqAlmox = seqAlmox;
	}

	public DominioSituacao getSituacaoPacote() {
		return situacaoPacote;
	}

	public void setSituacaoPacote(DominioSituacao situacaoPacote) {
		this.situacaoPacote = situacaoPacote;
	}

	public Integer getNumPacote() {
		return numPacote;
	}

	public void setNumPacote(Integer numPacote) {
		this.numPacote = numPacote;
	}

	public DominioSituacao getSituacaoPacoteOriginal() {
		return situacaoPacoteOriginal;
	}

	public void setSituacaoPacoteOriginal(DominioSituacao situacaoPacoteOriginal) {
		this.situacaoPacoteOriginal = situacaoPacoteOriginal;
	}

	public String getDescricaoPacoteMaterialOriginal() {
		return descricaoPacoteMaterialOriginal;
	}

	public void setDescricaoPacoteMaterialOriginal(String descricaoPacoteMaterialOriginal) {
		this.descricaoPacoteMaterialOriginal = descricaoPacoteMaterialOriginal;
	}

	public List<SceItemPacoteMateriais> getListaItensOriginal() {
		return listaItensOriginal;
	}

	public void setListaItensOriginal(List<SceItemPacoteMateriais> listaItensOriginal) {
		this.listaItensOriginal = listaItensOriginal;
	}

	public boolean isExibeModalAlteracao() {
		return exibeModalAlteracao;
	}

	public void setExibeModalAlteracao(boolean exibeModalAlteracao) {
		this.exibeModalAlteracao = exibeModalAlteracao;
	}

	public String getAcaoCadastro() {
		return acaoCadastro;
	}

	public void setAcaoCadastro(String acaoCadastro) {
		this.acaoCadastro = acaoCadastro;
	}

	public Integer getNumeroPacoteMaterial() {
		return numeroPacoteMaterial;
	}

	public void setNumeroPacoteMaterial(Integer numeroPacoteMaterial) {
		this.numeroPacoteMaterial = numeroPacoteMaterial;
	}

	public Boolean getModoVisualizacao() {
		return modoVisualizacao;
	}

	public void setModoVisualizacao(Boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}

	public boolean isAlmoxarife() {
		return isAlmoxarife;
	}

	public void setAlmoxarife(boolean isAlmoxarife) {
		this.isAlmoxarife = isAlmoxarife;
	}
}