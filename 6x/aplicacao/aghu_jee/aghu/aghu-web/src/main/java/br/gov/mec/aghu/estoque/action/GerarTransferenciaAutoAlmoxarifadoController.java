package br.gov.mec.aghu.estoque.action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.TransferenciaAutomaticaVO;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.VScoClasMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class GerarTransferenciaAutoAlmoxarifadoController extends ActionController {


	private static final long serialVersionUID = 6349419491609107481L;

	private static final String GERAR_TRANSFERENCIA_AUTOMATICA = "estoque-gerarTransferenciaAutoAlmoxarifado";

	private static final String EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO = "estoque-efetivarTransferenciaAutoAlmoxarifado";

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@Inject
	RelatorioTransferenciaMaterialController relatorioTransferenciaMaterialController;

	@EJB
	private IComprasFacade comprasFacade;

	// Parâmetros da conversação
	private Integer seq;
	private String voltarPara;
	
	// Variáveis que controlam as ações de gravação de transferência e geração de lista de transferência
	private boolean emEdicao;
	private boolean listaTransferenciaJaGerada; // Determina se a lista de itens de transferência foi gerada
	private boolean existeTransferenciaNaoEfetivadaDestino;  // Determina a existência de transferências automaticas não efetivadas no almoxarifado de destino
	private boolean transferenciaGeradaBotaoGravar; // Determina a existência de transferências foi gerada através do BOTÃO GRAVAR
	private boolean naoGerarNovaTransferencia; // Determina que na interação entre o botão gravar e o gerar lista, não será gerada uma nova transferência
	
	// Variáveis que representam os campos do XHTML
	private TransferenciaAutomaticaVO transferenciaAutomaticaVO;
	private VScoClasMaterial classificacaoMaterial;
	
	// Instancias para interações na tabela de itens de nota
	private List<SceItemTransferencia> listaItemTransferencia;
	private SceItemTransferencia itemSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 

		
		this.transferenciaAutomaticaVO = new TransferenciaAutomaticaVO();

		if (this.emEdicao) {

			// Resgata a instância da transferência
			final SceTransferencia transferencia = this.estoqueFacade.obterTransferenciaAutomatica(this.seq);
			this.transferenciaAutomaticaVO.setSeq(transferencia.getSeq());
			this.transferenciaAutomaticaVO.setDtGeracao(transferencia.getDtGeracao());
			this.transferenciaAutomaticaVO.setAlmoxarifadoOrigem(transferencia.getAlmoxarifado());
			this.transferenciaAutomaticaVO.setAlmoxarifadoDestino(transferencia.getAlmoxarifadoRecebimento());
			if(transferencia.getClassifMatNiv5() != null){
				this.classificacaoMaterial = this.comprasFacade.obterVScoClasMaterialPorNumero(transferencia.getClassifMatNiv5().getNumero());	
			}
			this.transferenciaAutomaticaVO.setServidor(transferencia.getServidor());

			// Popula lista de itens de transferência
			this.pesquisarListaItensTransferenciaPorTransferencia();

			// Garante que o botão transferência estará no modo somente leitura durante a edição 
			this.transferenciaGeradaBotaoGravar = true;
			this.naoGerarNovaTransferencia = true;

		} else{

			this.listaItemTransferencia = new LinkedList<SceItemTransferencia>();
			this.classificacaoMaterial = null;
			this.itemSelecionado = null;
			
			// Garante que o botão transferência estará ativado no modo inclusão
			this.listaTransferenciaJaGerada= false;
			this.existeTransferenciaNaoEfetivadaDestino = false;
			this.transferenciaGeradaBotaoGravar = false;
			this.naoGerarNovaTransferencia = false;
		}
	
	}
	
	
	/**
	 * Pesquisa/Popula lista de itens de transferência e realiza as interações necessárias na tela
	 */
	public void pesquisarListaItensTransferenciaPorTransferencia(){
	
		// Resgata a lista de itens de transferência
		this.listaItemTransferencia = this.estoqueFacade.pesquisarListaItensTransferenciaPorTransferencia(transferenciaAutomaticaVO.getSeq());	
	
		if(this.listaItemTransferencia != null && !this.listaItemTransferencia.isEmpty()){
			this.itemSelecionado = this.listaItemTransferencia.get(0);
			this.listaTransferenciaJaGerada = true;
		} else{
			this.listaTransferenciaJaGerada = false;
		}
	}
	
	/**
	 * Valida campos obrigatórios em branco
	 */
	private boolean existeCamposObrigatoriosEmBranco(){

		boolean retorno = false;
		
		if(this.transferenciaAutomaticaVO.getAlmoxarifadoOrigem() == null){
			this.apresentarMsgNegocio("sbAlmoxarifadoOrigem", Severity.ERROR, "CAMPO_OBRIGATORIO", "Almoxarifado Origem");
			retorno = true;
		}
		
		if(this.transferenciaAutomaticaVO.getAlmoxarifadoDestino() == null){
			this.apresentarMsgNegocio("sbAlmoxarifadoDestino", Severity.ERROR, "CAMPO_OBRIGATORIO", "Almoxarifado Destino");
			retorno = true;
		}
		
		// Atenção quanto a validação da classificação, a variável correta é "this.classificacaoMaterial"
		if(this.getClassificacaoMaterial() == null){
			this.apresentarMsgNegocio("sbClassificacaoMaterial", Severity.ERROR, "CAMPO_OBRIGATORIO", "Classificação Material");
			retorno = true;
		}

		return retorno;
	}
	
	
	/**
	 * Verifica a existência de transferências automaticas não efetivadas 
	 * no almoxarifado de destino durante a geração da lista de itens de material
	 */
	public void pesquisarTranferenciaAutomaticaNaoEfetivadaDestino(boolean transferenciaGeradaBotaoGravar){
		
		// Informa que a a geracao da transferência ocorreu através do botão gravar
		this.transferenciaGeradaBotaoGravar = transferenciaGeradaBotaoGravar;
		
		// Verifica se os campos obrigatórios foram preenchidos para pesquisa de transferências não efetivadas...
		if(!this.naoGerarNovaTransferencia && this.transferenciaAutomaticaVO.getAlmoxarifadoOrigem() != null 
				&& this.transferenciaAutomaticaVO.getAlmoxarifadoDestino() != null 
				&& this.getClassificacaoMaterial() != null){

			final Short seqAlmoxarifado = this.transferenciaAutomaticaVO.getAlmoxarifadoOrigem().getSeq();
			final Short seqAlmoxarifadoRecebimento = this.transferenciaAutomaticaVO.getAlmoxarifadoDestino().getSeq();
			final ScoClassifMatNiv5 classifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(this.classificacaoMaterial.getId().getNumero());
			final Long numeroClassifMatNiv5 = classifMatNiv5.getNumero();
			
			// Verifica a existência de transferências automáticas não efetivada no almoxarifado de destino durante a geração da lista de itens de material
			this.existeTransferenciaNaoEfetivadaDestino = this.estoqueFacade.existeTransferenciaAutomaticaNaoEfetivadaDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);

		} else{
			// O valor padrão é falso
			this.existeTransferenciaNaoEfetivadaDestino = false;
		}
	}

	
	/**
	 * Confirma a operacao de gravar/alterar um material
	 */
	public String gerarTransferencia() {
		
		// Valida campos obrigatórios em branco
		if(this.existeCamposObrigatoriosEmBranco()){
			this.transferenciaGeradaBotaoGravar = false;
			return null;
		}
		
		try {
			
			SceTransferencia transferencia = null;
			
			if(this.emEdicao){ 
				transferencia = this.estoqueFacade.obterTransferenciaAutomatica(this.seq);
			} else{
				transferencia = new SceTransferencia();
			}
			
			transferencia.setAlmoxarifado(this.transferenciaAutomaticaVO.getAlmoxarifadoOrigem());
			transferencia.setAlmoxarifadoRecebimento(this.transferenciaAutomaticaVO.getAlmoxarifadoDestino());
			if(this.classificacaoMaterial != null){
				final ScoClassifMatNiv5 classifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(this.classificacaoMaterial.getId().getNumero());
				transferencia.setClassifMatNiv5(classifMatNiv5);
			}
			
			// Persiste transferência automática
			this.estoqueBeanFacade.persistirTransferenciaAutoAlmoxarifado(transferencia);
			
			// Entra AUTOMATICAMENTE em modo de edição após um inclusão!
			if(!this.emEdicao){
				this.seq = transferencia.getSeq();
				this.transferenciaAutomaticaVO.setSeq(transferencia.getSeq());
				this.emEdicao = true;
				this.naoGerarNovaTransferencia = true;
			}
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_TRANSFERENCIA", transferenciaAutomaticaVO.getSeq());
	
			this.inicio();
		
		 } catch (BaseException e) {
			 this.transferenciaGeradaBotaoGravar = false;
			 this.naoGerarNovaTransferencia = false;
			 apresentarExcecaoNegocio(e);
		 }
		return "confirmado";
	}
	
	/**
	 * Gera lista de itens de transferência automática
	 */
	public void gerarListaTransferencia() {
		
		// Valida campos obrigatórios em branco
		if(this.existeCamposObrigatoriosEmBranco()){
			return;
		}
		
		// Controla localmente se uma nova transferência foi gerada
		boolean novaTransferencia = false;
		
		try {
			/*
			 * Chamado quando o usuário clicar no BOTÃO GERAR LISTA será gerada uma transferência e sua respectiva lisa de transferência
			 * A inexistência do seq/id da transferência define que estamos FORA do modo edição
			 */
			if(this.seq == null){
				
				// Gera uma nova transferência
				SceTransferencia transferencia = new SceTransferencia();

				//transferencia.setSeq(this.transferenciaAutomaticaVO.getSeq());
				transferencia.setAlmoxarifado(this.transferenciaAutomaticaVO.getAlmoxarifadoOrigem());
				transferencia.setAlmoxarifadoRecebimento(this.transferenciaAutomaticaVO.getAlmoxarifadoDestino());
				
				if(this.classificacaoMaterial != null){
					final ScoClassifMatNiv5 classifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(this.classificacaoMaterial.getId().getNumero());
					transferencia.setClassifMatNiv5(classifMatNiv5);
				}
				
				// Persiste uma transferência automática
				this.estoqueBeanFacade.persistirTransferenciaAutoAlmoxarifado(transferencia);
				
				// Atualiza o seq da transferência gerada
				this.seq = transferencia.getSeq();
				
				// Informa que uma NOVA transferência foi gerada
				novaTransferencia = true;
				
				// Atualiza o seq do VO
				this.transferenciaAutomaticaVO.setSeq(transferencia.getSeq());
				
				// Entra AUTOMATICAMENTE em modo de edição após a inclusão de uma transferência
				this.emEdicao = true;
				
			}

			// Com o seq/id devidamente populado: A instância da transferência atual será obtida 
			final SceTransferencia transferencia = this.estoqueFacade.obterTransferenciaAutomatica(this.seq);
			
			// Gera uma lista de transferência para transferência obtida
			this.estoqueBeanFacade.gerarListaTransferenciaAutoAlmoxarifado(transferencia);
			
			// Informa que a lista foi gerada corretamente
			this.listaTransferenciaJaGerada = true;
			
			// Popula a lista de itens de transferência da controller para correta exibição da mesma
			this.pesquisarListaItensTransferenciaPorTransferencia(); 

			// Imprime lista de transferência gerada
			this.relatorioTransferenciaMaterialController.setNumTransferenciaMaterial(transferencia.getSeq());
			this.relatorioTransferenciaMaterialController.setOrigem(GERAR_TRANSFERENCIA_AUTOMATICA);
			this.relatorioTransferenciaMaterialController.impressaoDireta();
			
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GERAR_LISTA_TRANSFERENCIA");

			// Reinicializa a tela...
			this.inicio();
			
		} catch (BaseException e) {
			
			// As variáveis de instância para geração de lista serão "resetadas" quando a transferência for gerada pelo botão "gerar lista"
			if(novaTransferencia){
				this.listaTransferenciaJaGerada = false;
				this.emEdicao = false;
				this.seq = null;
			}
			
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public String efetivar(){
		return EFETIVAR_TRANSFERENCIA_AUTO_ALMOXARIFADO;
	}
	
	
	
	/**
	 * Seleciona um item nota de transferência selecionado na lista
	 */
	public void selecionarItemTransferencia(SceItemTransferencia itemTransferencia){
		this.itemSelecionado = itemTransferencia;
	}

	/**
	 * Estorna/Remove a transferência quando há transferências automaticas não efetivadas no almoxarifado de destino
	 */
	public void estornarTransferencia()  {
		
		try {
			
			final Short seqAlmoxarifado = this.transferenciaAutomaticaVO.getAlmoxarifadoOrigem().getSeq();
			final Short seqAlmoxarifadoRecebimento = this.transferenciaAutomaticaVO.getAlmoxarifadoDestino().getSeq();
			final ScoClassifMatNiv5 classifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(this.classificacaoMaterial.getId().getNumero());
			final Long numeroClassifMatNiv5 = classifMatNiv5.getNumero();
			
			// Remove a transferência e seus respectivos itens
			this.estoqueBeanFacade.removerTransferenciaAutomaticaNaoEfetivadaAlmoxarifadoDestino(seqAlmoxarifado, seqAlmoxarifadoRecebimento, numeroClassifMatNiv5);
			
			// Desativa o modo edição
			this.emEdicao = false;
			
			// Desativa a lista
			this.listaTransferenciaJaGerada = false;
			
			// Gera uma nova transferência automática com os mesmos dados
			this.transferenciaAutomaticaVO.setSeq(null); // Importante resetar o seq do VO para evitar a exception de entidade "desatachada" 

			// Reseta seq para geração de uma nova transferência
			this.seq = null;
			
			
			// Gera uma lista de itens da nova transferência automática quando o estorno vem da ação do botão gerar lista
			
			if(this.transferenciaGeradaBotaoGravar){
				this.gerarTransferencia();
			} else{
				this.gerarListaTransferencia();
			}
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Remove um item nota de transferência selecionado na lista
	 */
	public void removerItemTransferencia(SceItemTransferencia itemTransferencia){
		try {
			if (itemTransferencia != null) {
				String nomeMaterial = estoqueFacade.obterNomeMaterialItemTransferencia(itemTransferencia.getId().getEalSeq(), itemTransferencia.getId().getTrfSeq());
				this.estoqueBeanFacade.removerItemTransferenciaAutoAlmoxarifado(itemTransferencia.getId().getEalSeq(), itemTransferencia.getId().getTrfSeq());
				this.pesquisarListaItensTransferenciaPorTransferencia(); // "Reseta" a lista de itens de transferência da controller para correta exibição da mesma
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_ITEM_TRANSFERENCIA", nomeMaterial);
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOVER_ITEM_TRANSFERENCIA");
			}
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Obtem lista para sugestion box de classificação de material de NÍVEL 5
	 */
	public List<VScoClasMaterial> obterClassificacaoMaterial(String param){
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}
	
	/**
	 * Obtem lista para sugestion box de almoxarifado de "ORIGEM E DESTINO"
	 */
	public List<SceAlmoxarifado> obterAlmoxarifado(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		listaTransferenciaJaGerada = false;
		seq = null;
		return this.voltarPara;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public TransferenciaAutomaticaVO getTransferenciaAutomaticaVO() {
		return transferenciaAutomaticaVO;
	}

	public void setTransferenciaAutomaticaVO(
			TransferenciaAutomaticaVO transferenciaAutomaticaVO) {
		this.transferenciaAutomaticaVO = transferenciaAutomaticaVO;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}

	public List<SceItemTransferencia> getListaItemTransferencia() {
		return listaItemTransferencia;
	}

	public void setListaItemTransferencia(
			List<SceItemTransferencia> listaItemTransferencia) {
		this.listaItemTransferencia = listaItemTransferencia;
	}

	public boolean isListaTransferenciaJaGerada() {
		return listaTransferenciaJaGerada;
	}

	public void setListaTransferenciaJaGerada(boolean listaTransferenciaJaGerada) {
		this.listaTransferenciaJaGerada = listaTransferenciaJaGerada;
	}

	public SceItemTransferencia getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(SceItemTransferencia itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public boolean isExisteTransferenciaNaoEfetivadaDestino() {
		return existeTransferenciaNaoEfetivadaDestino;
	}

	public void setExisteTransferenciaNaoEfetivadaDestino(
			boolean existeTransferenciaNaoEfetivadaDestino) {
		this.existeTransferenciaNaoEfetivadaDestino = existeTransferenciaNaoEfetivadaDestino;
	}

	public boolean isTransferenciaGeradaBotaoGravar() {
		return transferenciaGeradaBotaoGravar;
	}

	public void setTransferenciaGeradaBotaoGravar(
			boolean transferenciaGeradaBotaoGravar) {
		this.transferenciaGeradaBotaoGravar = transferenciaGeradaBotaoGravar;
	}

	public boolean isNaoGerarNovaTransferencia() {
		return naoGerarNovaTransferencia;
	}

	public void setNaoGerarNovaTransferencia(boolean naoGerarNovaTransferencia) {
		this.naoGerarNovaTransferencia = naoGerarNovaTransferencia;
	}

}